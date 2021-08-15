/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class replaces the SerialDriver class when you want to test
            the software with using the console.  Data is fed from the Simulator.
            The simulator can send captured DMP data.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

import dbif.DatabaseCommon;
import util.ByteUtil;
import util.CCITT;
import util.Logger;
import util.TimeUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;

public class TestDriver implements ActionListener
{
  private Socket socket;
  private OutputStream out;
  private InputStream in;

  private final ConsoleCmdQueue commandQueue = ConsoleCmdQueue.getInstance();
  private Command nextCommand = null;
  private Thread commandSendThread;
  private Thread receiverThread;
  private CommandControl commandControl;
  private JFrame parent;
  private final CCITT ccitt = new CCITT();
  private final DatabaseCommon dbCommon = DatabaseCommon.getInstance();
  private final Logger logger = Logger.getInstance();

  private final WakeupNotifier wakeupNotifier = new WakeupNotifier();
  private final CommandNotifier commandNotifier = new CommandNotifier();
  private int wakeupTries = 0;
  private final Timer wakeupTimer = new Timer(5000, this);
  private final CurrentDataTimer currentDataTimer = CurrentDataTimer.getInstance();
  private final HistoricDataTimer historicDataTimer = HistoricDataTimer.getInstance();
  private final HiLowDataTimer hiLowDataTimer = HiLowDataTimer.getInstance();
  private final Timer delayTimer = new Timer(2000, this);
  private boolean wakeupTimerIsSet = false;
  private boolean delayTimerIsSet = false;

  private int dmpAftPageOffset = 0;
  private short numOfPages = 0;
  private int nextByte = 0;
  private final byte[] inputBuffer = new byte['ǂ'];

  private static class SingletonHelper
  {
    private static final TestDriver INSTANCE = new TestDriver();
  }

  public static TestDriver getInstance()
  {
    return SingletonHelper.INSTANCE;
  }

  private TestDriver() { }

  public void init(CommandControl commandControl, JFrame parent)
  {
    this.commandControl = commandControl;
    this.parent = parent;
  }

  void openPort()
  {
    try
    {
      socket = new Socket("127.0.0.1", 1234);
      out = socket.getOutputStream();
      in  = socket.getInputStream();
      System.out.println("Port Open...");

      receiverThread = new Thread(this::receiver);
      receiverThread.start();

      commandSendThread = new Thread(this::commandSender);
      commandSendThread.start();
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  void closePort()
  {
    try
    {
      if (socket != null)
      {
        receiverThread.interrupt();
        commandSendThread.interrupt();
        socket.close();
      }
      System.out.println("Port Closed...");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  void resetPort()
  {
    closePort();
    openPort();
  }

  private void commandSender()
  {
    while (true)
    {
      if (nextCommand != null)
      {
        //        System.out.println("  TestDriver: waiting for previous command to complete.");
        commandNotifier.waitForNotification();
      }
      commandQueue.resetCommand();
      nextCommand = commandQueue.getConsoleCmd();
      if ((nextCommand.getCommand() != ConsoleCommand.ACK) && (nextCommand.getCommand() != ConsoleCommand.NAK))
      {
        wakeupConsole(1);

//        System.out.println("  TestDriver: waking up console, waiting for console to wake up.");
        wakeupNotifier.waitForNotification();
      }
      System.out.println("  TestDriver: sending command: " + nextCommand.getCommand().command());
      logger.captureData("TestDriver: sending command: " + nextCommand.getCommand().command(), Logger.Level.MEDIUM);

      // Turn off other command generation when the DMPAFT is going.
      if (nextCommand.getCommand() == ConsoleCommand.DMPAFT)
      {
        currentDataTimer.temporariallyStopTimer();
        historicDataTimer.temporariallyStopTimer();
        hiLowDataTimer.temporariallyStopTimer();
      }
      sendCommand(this.nextCommand);
    }
  }

  private void receiver()
  {
    try
    {
      while (true)
      {
        byte[] bytes = new byte[500];
        int count = in.read(bytes);
        if (count > 0)
        {
          System.out.println("Received: " + ByteUtil.bytesToHex(bytes));
          logger.captureData("Received: " + ByteUtil.bytesToHex(bytes), Logger.Level.MEDIUM);
          System.arraycopy(bytes, 0, inputBuffer, nextByte, count);
//          System.out.println("Bytes Received = " + count + " bytes: " + ByteUtil.bytesToHex(inputBuffer));
          nextByte += count;
//          System.out.println("NextByte: " + nextByte);
          if ((wakeupTimerIsSet) && (bytes[0] == 10) && (bytes[1] == 13))
          {
            nextByte = 0;
            wakeupTimerIsSet = false;
            wakeupNotifier.notifyWaiter();
          }

          if (nextCommand == null)
          {
            System.out.println("Bytes Received = " + count + " bytes: " + ByteUtil.bytesToHex(inputBuffer));
            logger.captureData("Bytes Received = " + count + " bytes: " + ByteUtil.bytesToHex(inputBuffer),
                               Logger.Level.FINE);
            continue;
          }

          if (nextCommand.getCommand().equals(ConsoleCommand.DMPAFT))
          {
            // First response, send the date timestamp.
            byte[] timestamp = TimeUtil.getDateTimestamp(dbCommon.getLastDateStamp(),
                                                         dbCommon.getLastTimeStamp());
            byte[] dateTimeStamp = new byte[6];
            System.arraycopy(timestamp, 0, dateTimeStamp, 0, 4);
            byte[] crcArray = ccitt.calculateCRCByteArray(timestamp, 4);
            dateTimeStamp[4] = crcArray[0];
            dateTimeStamp[5] = crcArray[1];
            System.out.println("Sent: " + ByteUtil.bytesToHex(dateTimeStamp));
            logger.captureData("Sent: " + ByteUtil.bytesToHex(dateTimeStamp), Logger.Level.MEDIUM);
            sendCommand(dateTimeStamp);
            nextCommand = new Command(ConsoleCommand.DMPAFTTIME);
            nextByte = 0;
          }
          else if (nextCommand.getCommand().equals(ConsoleCommand.DMPAFTTIME))
          {
            // Second go around, parse the number of pages, set notify dialog and send either send an ACK or a NAK.
            // Verify CRC
            byte[] bytesMinusAck = new byte[6];
            System.arraycopy(bytes, 1, bytesMinusAck, 0, 6);

            System.out.println("Bytes received: " + ByteUtil.bytesToHex(bytesMinusAck));

            byte[] crc = ccitt.calculateCRC(bytesMinusAck, bytesMinusAck.length);
            if (crc[0] != 0 || crc[1] != 0)
            {
              System.out.println("CRC for DMPAFT length response failed");
              logger.captureData("CRC for DMPAFT length response failed", Logger.Level.COARSE);
              byte[] byteArray = new byte[1];
              byteArray[0] = 0x1B;
              sendCommand(byteArray);
              return;
            }

            byte[] tempBuffer = new byte[2];
            tempBuffer[0] = bytesMinusAck[1];
            tempBuffer[1] = bytesMinusAck[0];
            numOfPages = ByteUtil.byteArrayToShort(tempBuffer);
            System.out.println("Number of Pages = " + numOfPages);
            logger.captureData("Number of Pages = " + numOfPages, Logger.Level.MEDIUM);

            tempBuffer[0] = bytesMinusAck[3];
            tempBuffer[1] = bytesMinusAck[2];
            dmpAftPageOffset = ByteUtil.byteArrayToShort(tempBuffer);
            System.out.println("Page offset = " + dmpAftPageOffset);
            logger.captureData("Page offset = " + dmpAftPageOffset, Logger.Level.MEDIUM);

            float percent = (numOfPages / 512.0f) * 100.0f;
            DecimalFormat df = new DecimalFormat("#.##");

            Object[] options = {"Download", "Cancel"};
            int n = JOptionPane.showOptionDialog(parent,
                                                 "Download " + numOfPages + " pages (" + df.format(percent) +"%)?",
                                                 "DMP AFT Download",
                                                 JOptionPane.OK_CANCEL_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE,
                                                 null,
                                                 options,
                                                 options[0]);

            if (n == 0)
            {
              byte[] byteArray = new byte[1];
              byteArray[0] = 0x06;
              sendCommand(byteArray);
              System.out.println("Sent ACK...");
              logger.captureData("Sent ACK...", Logger.Level.FINE);

              nextCommand = new Command(ConsoleCommand.DMPAFTDATA);
              nextByte = 0;
            }
            else
            {
              System.out.println("Cancel chosen.");
              logger.captureData("Cancel chosen.", Logger.Level.FINE);
              byte[] byteArray = new byte[1];
              byteArray[0] = 0x1B;
              sendCommand(byteArray);
              System.out.println("Sent ESC...");
              logger.captureData("Sent ESC...", Logger.Level.FINE);

              nextCommand = null;
              nextByte = 0;
            }
          }
          else if (nextByte >= nextCommand.getCommand().size())
          {
            processReceiveData(inputBuffer, nextByte, dmpAftPageOffset);
            if (dmpAftPageOffset > 0)
              dmpAftPageOffset = 0;
            nextByte = 0;
          }
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Method called to process the received data bytes depending on what type of timer is set.  If there are
   * return bytes to parse, the data is sent to the CommandControl class to perform the heavy lifting.  After the
   * bytes are processed, a wakeup notification is sent to notify the run method to send the next command.
   *
   * @param inputBuffer An array of bytes that were read.
   * @param bytesRead   The number of bytes that were read.
   * @param dmpAftPageOffset The page offset into the group of 5 DMPAFT pages.
   */
  private void processReceiveData(byte[] inputBuffer, int bytesRead, int dmpAftPageOffset)
  {
    if (wakeupTimerIsSet)
    {
//      System.out.println("Got a response from the wakeup command.");

      wakeupTimer.stop();
      wakeupTimerIsSet = false;
      wakeupNotifier.notifyWaiter();
    }
    else
    {
      if (delayTimerIsSet)
      {
        delayTimer.stop();
        delayTimerIsSet = false;
      }

      commandControl.parseReturnValues(nextCommand, inputBuffer, bytesRead, dmpAftPageOffset);

      if (nextCommand.getCommand().equals(ConsoleCommand.DMPAFTDATA))
      {
        numOfPages--;
        if (numOfPages == 0)
        {
          nextCommand = null;
        }
      }
      else
      {
        nextCommand = null;
      }

      commandNotifier.notifyWaiter();

      // Turn other command generation back on after DMPAFT has completed.
      currentDataTimer.resetTimer();
      historicDataTimer.resetTimer();
      hiLowDataTimer.resetTimer();
    }
  }

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
        break;
      default:
        byteArray = command.getCommand().command().getBytes();
        break;
    }
    sendCommand(byteArray);
  }

  public void sendCommand(byte[] bytes)
  {
    try
    {
      System.out.println("Sending: " + ByteUtil.bytesToHex(bytes));
      logger.captureData("Sending: " + ByteUtil.bytesToHex(bytes), Logger.Level.MEDIUM);

      delayTimerIsSet = true;
      delayTimer.start();
      out.write(bytes);
    }
    catch (IOException ioe)
    {
      System.out.println("Failed to send command.");
      logger.captureData("Failed to send command.", Logger.Level.COARSE);
    }
  }

  private void wakeupConsole(int tries)
  {
    sendCommand(new Command(ConsoleCommand.WAKEUP));

    wakeupTries = tries;
    wakeupTimerIsSet = true;
    wakeupTimer.start();
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (wakeupTimerIsSet)
    {
//      System.out.println("Console timer timed out.");
      wakeupTries += 1;
      if (wakeupTries < 4)
      {
        wakeupConsole(wakeupTries);
      }
      else
      {
        System.out.println("COULD NOT WAKE UP CONSOLE!");
        logger.captureData("COULD NOT WAKE UP CONSOLE!", Logger.Level.COARSE);
        wakeupTimerIsSet = false;
        resetPort();
      }
    }
    else if (delayTimerIsSet)
    {
      System.out.println("No response to last command.");
      logger.captureData("No response to last command.", Logger.Level.COARSE);
      delayTimerIsSet = false;
      commandNotifier.notifyWaiter();
    }
  }
}
