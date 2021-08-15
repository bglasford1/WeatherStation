/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is the interface to the console.  It utilizes the "PI4J"
            FOSS software to communicate with the console via a serial interface.
            The console has a simple protocol in that it has to wake up first if
            a command has not been sent within the last 2 minutes.  Most commands
            are simple command/response.  The DMP and DMPAFT are much more complex.
            The commands and responses are textual in nature with the data being
            binary.  The data responses are not decoded here, but are sent to
            CommandControl for decoding.

  DMPAFT Protocol: Send "DMPAFT\n".  The Wx Stn sends back 0x06 (ACK). Send 2 byte DateStamp, 2 byte TimeStamp and a
                   2 byte CRC.  If 6 bytes are not sent, the Wx Stn will send back a 0x21 (NAK).  The Wx Stn sends a
                   0x06 (ACK) followed by the # of pages, the location of the first page within the 5 record page
                   block and a 2 byte CRC.  If the CRC is incorrect, send back a 0x18.  At this point either a 0x1B
                   (ESC) can be sent to cancel the download or 0x06 (ACK) to tell the Wx Stn to send the first page.
                   If the CRC is incorrect, send 0x21 (NAK) to have the Wx Stn send the page again.  Otherwise send
                   0x06 (ACK) to receive the next page.  A 0x1B (ESC) can be sent anytime to cancel the downloads.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

import com.pi4j.io.serial.*;
import dbif.DatabaseCommon;
import util.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SerialDriver extends Thread implements SerialDataEventListener, ActionListener
{
  private static SerialDriver instance = null;
  private Serial serial;
  private CommandControl commandControl;
  private final ConsoleCmdQueue commandQueue = ConsoleCmdQueue.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private Command nextCommand = null;
  private final CCITT ccitt = new CCITT();
  private final DatabaseCommon dbCommon = DatabaseCommon.getInstance();
  private final CurrentDataTimer currentDataTimer = CurrentDataTimer.getInstance();
  private final HistoricDataTimer historicDataTimer = HistoricDataTimer.getInstance();
  private final HiLowDataTimer hiLowDataTimer = HiLowDataTimer.getInstance();
  private final Logger logger = Logger.getInstance();

  private final WakeupNotifier wakeupNotifier = new WakeupNotifier();
  private final CommandNotifier commandNotifier = new CommandNotifier();
  private int wakeupTries = 0;
  private final Timer wakeupTimer = new Timer(3000, this);
  private final Timer delayTimer = new Timer(180000, this);
  private boolean wakeupTimerIsSet = false;
  private boolean delayTimerIsSet = false;

  private int dmpAftPageOffset = 0;
  private short numOfPages = 0;
  private int nextByte = 0;
  private final byte[] inputBuffer = new byte['ǂ'];

  public static SerialDriver getInstance()
  {
    if (instance == null)
    {
      instance = new SerialDriver();
    }
    return instance;
  }

  public void init(CommandControl commandControl)
  {
    this.commandControl = commandControl;
  }

  /**
   * Configure and open the serial port.
   */
  void openPort()
  {
    serial = SerialFactory.createInstance();
    serial.addListener(this);

    SerialConfig config = new SerialConfig();
    try
    {
      // TODO: get baud rate from config properties.  Deal with modifying mid-stream.

      System.out.println("Opening Port: Port Name = " + PROPS.getPortName());

      config.device(PROPS.getPortName()).
        baud(Baud._19200).
              dataBits(DataBits._8).
              parity(Parity.NONE).
              stopBits(StopBits._1).
              flowControl(FlowControl.NONE);

      serial.open(config);

      System.out.println("Port Open...");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Close the serial port.
   */
  void closePort()
  {
    try
    {
      if (serial != null)
      {
        serial.close();
      }
      System.out.println("Port Closed...");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Reset the serial port by closing and then opening the port.
   */
  void resetPort()
  {
    closePort();
    openPort();
  }

  /**
   * Send a command at the command level.  This gets the bytes associated with the command.
   *
   * @param command The command.
   */
  private void sendCommand(Command command)
  {
    byte[] byteArray = new byte[1];
    switch (command.getCommand())
    {
      case ACK:
        byteArray[0] = 6;
        break;
      case NAK:
        byteArray[0] = 33;
        break;
      case EEBWR:
        String commandString = command.getCommand().command();
        commandString = commandString.replace("oo", Integer.toHexString(command.getOffset()));
        commandString = commandString.replace("nn", Integer.toString(command.getData().length));
        byteArray = commandString.getBytes();
        System.out.println("Writing: " + ByteUtil.bytesToHex(byteArray));
        break;
      default:
        byteArray = command.getCommand().command().getBytes();
        break;
    }
    sendCommand(byteArray);
  }

  /**
   * The low level command send which sends the actual bytes to the serial port.
   *
   * @param bytes The bytes to send.
   */
  public void sendCommand(byte[] bytes)
  {
    try
    {
      logger.captureData("Sending: " + ByteUtil.bytesToHex(bytes), Logger.Level.MEDIUM);

      delayTimerIsSet = true;
      delayTimer.start();
      serial.write(bytes);
    }
    catch (IOException ioe)
    {
      logger.logData("Failed to send command: " + ioe.getLocalizedMessage());
    }
  }

  /**
   * Wake up the console by sending the wakeup command. This sets a timer.
   *
   * @param tries The number of tries to send the wakeup command.
   */
  private void wakeupConsole(int tries)
  {
    sendCommand(new Command(ConsoleCommand.WAKEUP));

    wakeupTries = tries;
    wakeupTimerIsSet = true;
    wakeupTimer.start();
  }

  /**
   * Once the serial driver starts it loops forever waiting to be notified to send the next command.  Before the
   * next command can be sent, the console needs to be woken up only if a command has not been sent in the last 2 minutes.
   */
  public void run()
  {
    logger.captureData("Run Loop: started.", Logger.Level.FINE);

    // Loop forever processing commands.
    while (true)
    {
      // Get the next command or waits until a command is available.
      logger.captureData("  Run Loop: waiting for next command.", Logger.Level.FINE);
      nextCommand = commandQueue.getConsoleCmd();

      // If not in the middle of a current command then wakeup the console and wait for it to complete.
      if ((nextCommand.getCommand() != ConsoleCommand.ACK) && (nextCommand.getCommand() != ConsoleCommand.NAK))
      {
        logger.captureData("  Run Loop: Waking up console.", Logger.Level.FINE);
        wakeupConsole(1);

        wakeupNotifier.waitForNotification();
      }

      // If a command is available then send the command.
      if (nextCommand != null)
      {
        // Turn off other command generation when the DMPAFT is going.
        if (nextCommand.getCommand() == ConsoleCommand.DMPAFT)
        {
          currentDataTimer.temporariallyStopTimer();
          historicDataTimer.temporariallyStopTimer();
          hiLowDataTimer.temporariallyStopTimer();
        }

        logger.captureData("  Run Loop: Sending command: " + nextCommand.getCommand().command(),
                           Logger.Level.MEDIUM);
        sendCommand(nextCommand);
      }

      // If a command is currently being processed...
      if (nextCommand != null)
      {
        // Wait for the command to complete.
        logger.captureData("  Run Loop: waiting for command to complete.", Logger.Level.FINE);
        commandNotifier.waitForNotification();
      }

      // Tell Queue the last command has completed.
      commandQueue.resetCommand();
    }
  }

  /**
   * The action performed method required as this class is an ActionListener.  This method is called when one of
   * two timers timeout.  There is the wakeup timer and the delay timer.  The wakeup timer is waiting for the console
   * to wakeup and return an ack.  If an ack is not returned after 3 tries, then the port is reset.  The delay
   * timer is used to timeout a command sent to the console.  If there is no valid response sent then the run
   * method is woken up to send the next command.
   *
   * @param event The required action event.  This is not used.
   */
  public void actionPerformed(ActionEvent event)
  {
    if (wakeupTimerIsSet)
    {
      logger.captureData("  Timeout: Console timer timed out.", Logger.Level.MEDIUM);

      wakeupTries += 1;
      if (wakeupTries < 4)
      {
        wakeupConsole(wakeupTries);
      }
      else
      {
        logger.captureData("  Timeout: COULD NOT WAKE UP CONSOLE!", Logger.Level.COARSE);
        logger.logData("   Timeout: COULD NOT WAKE UP CONSOLE!");

        wakeupTimerIsSet = false;
        resetPort();
      }
    }
    else if (delayTimerIsSet)
    {
      logger.captureData("  Timeout: No response to last command.", Logger.Level.COARSE);
      logger.logData("  Timeout: No response to last command.");

      delayTimerIsSet = false;
      commandNotifier.notifyWaiter();
    }
  }

  /**
   * This method is called when data is available by the serial data driver.  The available data is received,
   * parsed and either processed or the next action of a protocol such as the DMPAFT protocol is sent.
   *
   * @param event The pointer to the serial data.
   */
  public void dataReceived(SerialDataEvent event)
  {
    try
    {
      byte[] bytes = event.getBytes();
      logger.captureData("  Rx: Received: " + ByteUtil.bytesToHex(bytes), Logger.Level.MEDIUM);

      System.arraycopy(bytes, 0, inputBuffer, nextByte, bytes.length);
      nextByte += bytes.length;

      if ((wakeupTimerIsSet) && (bytes[0] == 10) && (bytes[1] == 13))
      {
        logger.captureData("  Rx: Valid wakeup response.", Logger.Level.FINE);
        nextByte = 0;
        wakeupTimerIsSet = false;
        wakeupNotifier.notifyWaiter();
        return;
      }

      if (nextCommand.getCommand().equals(ConsoleCommand.DMPAFT))
      {
        // First response, send the date timestamp.
        byte[] timestamp = TimeUtil.getDateTimestamp(dbCommon.getLastDateStamp(), dbCommon.getLastTimeStamp());

        logger.captureData("  Rx: Last Datestamp: " + dbCommon.getLastDateStamp() +
                             ", Last Timestamp: " + dbCommon.getLastTimeStamp(), Logger.Level.FINE);

        byte[] dateTimeStamp = new byte[6];
        System.arraycopy(timestamp, 0, dateTimeStamp, 0, 4);
        byte[] crcArray = ccitt.calculateCRCByteArray(timestamp, 4);
        dateTimeStamp[4] = crcArray[0];
        dateTimeStamp[5] = crcArray[1];
        logger.captureData("  Rx: Sent: " + ByteUtil.bytesToHex(dateTimeStamp), Logger.Level.MEDIUM);

        sendCommand(dateTimeStamp);
        nextCommand = new Command(ConsoleCommand.DMPAFTTIME);
        nextByte = 0;
      }
      else if (nextCommand.getCommand().equals(ConsoleCommand.DMPAFTTIME))
      {
        // Second go around, parse the number of pages, set notify dialog and send either send an ACK or a NAK
        byte[] timestampBytes = event.getBytes();

        if (timestampBytes.length == 1)
        {
          // If response = 0x18 then CRC is not correct.
          // If response = 0x21 then 6 bytes not sent.
          logger.captureData("  Rx: DMPAFT:  Aborting:  Received: " + ByteUtil.bytesToHex(timestampBytes),
                             Logger.Level.COARSE);
          logger.logData("  Rx: DMPAFT:  Aborting:  Received: " + ByteUtil.bytesToHex(timestampBytes));

          // Reload DMPAFT command to send again.
          commandQueue.dumpArchivedDataAfterDate();

          endCommand();
          return;
        }

        if (timestampBytes.length < 6)
        {
          logger.captureData("  Rx: DMPAFT: Aborting: Less than 6 bytes received: Received: " +
            ByteUtil.bytesToHex(timestampBytes), Logger.Level.COARSE);
          logger.logData("  Rx: DMPAFT: Aborting: Less than 6 bytes received: Received: " +
                               ByteUtil.bytesToHex(timestampBytes));

          endCommand();
          return;
        }

        // Verify CRC
        byte[] bytesMinusAck = new byte[6];
        System.arraycopy(timestampBytes, 1, bytesMinusAck, 0, 6);

        byte[] crc = ccitt.calculateCRC(bytesMinusAck, bytesMinusAck.length);
        if (crc[0] != 0 || crc[1] != 0)
        {
          logger.captureData("  Rx: CRC for DMPAFT length response failed", Logger.Level.COARSE);
          logger.logData("  Rx: CRC for DMPAFT length response failed");

          byte[] byteArray = new byte[1];
          byteArray[0] = 0x1B;
          sendCommand(byteArray);
          return;
        }

        byte[] tempBuffer = new byte[2];
        tempBuffer[0] = bytesMinusAck[1];
        tempBuffer[1] = bytesMinusAck[0];
        numOfPages = ByteUtil.byteArrayToShort(tempBuffer);

        tempBuffer[0] = bytesMinusAck[3];
        tempBuffer[1] = bytesMinusAck[2];
        dmpAftPageOffset = ByteUtil.byteArrayToShort(tempBuffer);
        logger.captureData("  Rx: Number of Pages = " + numOfPages + ", Page offset = " + dmpAftPageOffset,
                           Logger.Level.MEDIUM);

//        float percent = (numOfPages / 512.0f) * 100.0f;
//        DecimalFormat df = new DecimalFormat("#.##");
//
//        Object[] options = {"Download", "Cancel"};
//        int n = JOptionPane.showOptionDialog(parent,
//                                             "Download " + numOfPages + " pages (" + df.format(percent) +"%)?",
//                                             "DMP AFT Download",
//                                             JOptionPane.OK_CANCEL_OPTION,
//                                             JOptionPane.QUESTION_MESSAGE,
//                                             null,
//                                             options,
//                                             options[0]);
//
//        if (n == 0)
//        {
        byte[] byteArray = new byte[1];
        byteArray[0] = 0x06;
        sendCommand(byteArray);
        logger.captureData("  Rx: Sent ACK...", Logger.Level.FINE);

        if (numOfPages == 0)
        {
          endCommand();
        }
        else
        {
          nextCommand = new Command(ConsoleCommand.DMPAFTDATA);
          nextByte = 0;
        }
//        }
//        else
//        {
//          System.out.println("Cancel chosen.");
//          byte[] byteArray = new byte[1];
//          byteArray[0] = 0x1B;
//          sendCommand(byteArray);
//          System.out.println("Sent ESC...");
//
//          nextCommand = null;
//          nextByte = 0;
//        }
      }
      // Else if the next command response has been received then process the command.
      else if (nextByte >= nextCommand.getCommand().size())
      {
        stopDelayTimer();

        // Parese the received bytes and process.
        commandControl.parseReturnValues(nextCommand, inputBuffer, nextByte, dmpAftPageOffset);

        if (nextCommand.getCommand().equals(ConsoleCommand.DMPAFTDATA))
        {
          numOfPages--;
          if (numOfPages == 0)
          {
            endCommand();
          }
        }
        else
        {
          endCommand();
        }

        if (dmpAftPageOffset > 0)
          dmpAftPageOffset = 0;
        nextByte = 0;
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private void endCommand()
  {
    nextCommand = null;
    nextByte = 0;

    // Turn other command generation back on after DMPAFT has completed.
    currentDataTimer.resetTimer();
    historicDataTimer.resetTimer();
    hiLowDataTimer.resetTimer();

    stopDelayTimer();

    // Notify the run thread that the command is complete.
    commandNotifier.notifyWaiter();
  }

  /**
   *  Stop the delay timer which is used to time the command completion.
   */
  private void stopDelayTimer()
  {
    if (delayTimerIsSet)
    {
      delayTimer.stop();
      delayTimerIsSet = false;
    }
  }
}
