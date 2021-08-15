/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is the central hub for all commands that go to the console.
            Commands from the operator are sent here by the windowing classes.
            Because commands take a while to execute, commands are queued up
            using the ConsoleCmdQueue.  The SerialDriver is a thread that loops
            forever waiting for a command to process.  Upon receiving a command
            it sends the command to the console and retrieves the results.  The
            results are sent back to this class to parse and disposition.
            Depending the on the command, the resulting data is either sent to
            the windowing classes for display or sent to the DatabaseWriter to
            save.  The save data is the HiLow and DMP/DMPAFT data.  The Loop data
            is sent the windowing classes for display.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

import data.consolerecord.AlarmData;
import data.consolerecord.DmpDataExtended;
import data.consolerecord.HiLoData;
import data.consolerecord.LoopData;
import data.dbrecord.WindDirection;
import dbif.DatabaseCommon;
import dbif.DatabaseReader;
import dbif.DatabaseWriter;
import gui.*;
import gui.currentreadings.CurrentReadings;
import gui.graph.StreamDataThread;
import util.CCITT;
import util.Logger;
import util.ConfigProperties;
import wxserverif.WeatherServerIF;

import javax.swing.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class CommandControl
{
  private final JFrame parent;
  private final CurrentReadings currentReadings;
  private static final DatabaseWriter DB_WRITER = new DatabaseWriter();
  private static final DatabaseReader DB_READER = DatabaseReader.getInstance();
  private static final DatabaseCommon DB_COMMON = DatabaseCommon.getInstance();
  private final CCITT ccitt = new CCITT();
  private final SerialDriver serialDriver = SerialDriver.getInstance();
  private final TestDriver testDriver = TestDriver.getInstance();
  private final WeatherServerIF wxInterface = WeatherServerIF.getInstance();
  private final StreamDataThread streamDataThread = StreamDataThread.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private final Logger logger = Logger.getInstance();

  private LoopData lastLoopData = null;
  private TestDialog testDialog;
  private DiagsDialog diagsDialog;
  private VersionDialog versionDialog;
  private TimeDialog timeDialog;
  private TransceiversDialog transceiversDialog;
  private AlarmConfigDialog alarmConfigDialog;

  /**
   * The constructor that initializes the communications with the console.
   *
   * @param parent The parent JFrame object.
   * @param currentReadings A pointer to the CurrentReadings class.
   */
  public CommandControl(JFrame parent, CurrentReadings currentReadings)
  {
    this.parent = parent;
    this.currentReadings = currentReadings;

    if (PROPS.getTestMode())
    {
      testDriver.init(this, parent);
    }
    else
    {
      serialDriver.init(this);
      serialDriver.start();
    }

    initializeCommunications();
  }

  /**
   * Method to initialize the communications mechanism.
   */
  private void initializeCommunications()
  {
    if (PROPS.getTestMode())
    {
      testDriver.openPort();
    }
    else
    {
      serialDriver.openPort();
    }
  }

  /**
   * Method used to reset the communications mechanism.  This simply closes and then opens the port.
   */
  public void resetCommunications()
  {
    if (PROPS.getTestMode())
    {
      testDriver.resetPort();
    }
    else
    {
      serialDriver.resetPort();
    }
  }

  /**
   * Method used to close the communications mechanism.
   */
  public void terminateCommunications()
  {
    if (PROPS.getTestMode())
    {
      testDriver.closePort();
    }
    else
    {
      serialDriver.closePort();
    }
  }

  /**
   * This method verifies the bytes returned from an OK command sent to the console.
   *
   * @param bytes The bytes to verify.
   * @return Whether or not the bytes are correct.
   */
  private boolean verifyOk(byte[] bytes)
  {
    return (bytes[0] == 10) && (bytes[1] == 13) && (bytes[2] == 79) && (bytes[3] == 75) && (bytes[4] == 10) && (bytes[5] == 13);
  }

  /**
   * Method to parse data returned from the console.
   *
   * @param originalCmd The command sent to the console.
   * @param buffer The bytes returned from the console.
   * @param bufferSize The number of bytes returned from the console.
   * @param dmpAftPageOffset The page offset into the group of 5 DMPAFT pages.
   */
  public void parseReturnValues(Command originalCmd, byte[] buffer, int bufferSize, int dmpAftPageOffset)
  {
    if (originalCmd.getCommand().ok())
    {
      if (verifyOk(buffer))
      {
        byte[] newBuffer = Arrays.copyOfRange(buffer, 6, bufferSize);

        if (originalCmd.getCommand() == ConsoleCommand.RXCHECK)
        {
          String packetsReceived;
          String missedPackets;
          String resynchronizations;
          String maxContiguousPackets;
          String crcErrors;

          String results = new String(newBuffer, 0, bufferSize - 6);
          try
          {
            StringTokenizer tokens = new StringTokenizer(results);
            packetsReceived = tokens.nextToken();
            missedPackets = tokens.nextToken();
            resynchronizations = tokens.nextToken();
            maxContiguousPackets = tokens.nextToken();
            crcErrors = tokens.nextToken();
          }
          catch (NoSuchElementException nsee)
          {
            logger.logData("Rx: RXCHECK results did not parse.");
            return;
          }

          int packets = Integer.parseInt(packetsReceived);
          int missed = Integer.parseInt(missedPackets);
          float percent = ((float)(packets - missed) / packets) * 100;
          String percentGood = String.format("%.2f", percent);
          String batteryVoltage = "";
          String batteryStatus = "";

          if (this.lastLoopData != null)
          {
            batteryVoltage = String.format("%.2f", this.lastLoopData.getConsoleBatteryVolt());
            batteryStatus = Float.toString(this.lastLoopData.getTransBatteryStatus());
          }

          // Display the results in a diags dialog box.
          if (diagsDialog == null)
          {
            diagsDialog = new DiagsDialog(parent, packetsReceived, missedPackets, resynchronizations,
              maxContiguousPackets, crcErrors, percentGood, batteryVoltage, batteryStatus);
          }
          else
          {
            diagsDialog.setNewValues(packetsReceived, missedPackets, resynchronizations,
              maxContiguousPackets, crcErrors, percentGood, batteryVoltage, batteryStatus);

            diagsDialog.setVisible(true);
          }
        }
        else if (originalCmd.getCommand() == ConsoleCommand.VERSION)
        {
          String results = new String(newBuffer);
          results = results.substring(0, 11);
          if (this.versionDialog == null)
          {
            versionDialog = new VersionDialog(this.parent, results);
          }
          else
          {
            versionDialog.setNewVersion(results);
            versionDialog.setVisible(true);
          }
        }
        else if (originalCmd.getCommand() == ConsoleCommand.RECEIVERS)
        {
          byte bitmap = newBuffer[0];
          if (this.transceiversDialog == null)
          {
            transceiversDialog = new TransceiversDialog(this.parent, bitmap);
          }
          else
          {
            transceiversDialog.setBitmap(bitmap);
            transceiversDialog.setVisible(true);
          }
        }
        else if (originalCmd.getCommand() != ConsoleCommand.NVERSION)
        {
          switch (originalCmd.getCommand())
          {
            case LAMPON:
              logger.captureData("Lamp On Response", Logger.Level.COARSE);
              break;
            case LAMPOFF:
              logger.captureData("Lamp Off Response", Logger.Level.COARSE);
              break;
            default:
              logger.captureData("Invalid OK command...", Logger.Level.COARSE);
              logger.logData("Invalid OK command...");
              break;
          }
        }
      }
      else
      {
        logger.logData("  Rx: Bad response, OK not correctly received...");
      }
    }
    else if (originalCmd.getCommand().eol())
    {
      if (originalCmd.getCommand() == ConsoleCommand.TEST)
      {
        String results = new String(buffer, 0, bufferSize);
        if (testDialog == null)
        {
          testDialog = new TestDialog(this.parent, results);
        }
        else
        {
          testDialog.setTestText(results);
          testDialog.setVisible(true);
        }
      }
      else
      {
        logger.logData("  Rx: Invalid EOL command...");
      }
    }
    else if (originalCmd.getCommand().equals(ConsoleCommand.GETTIME) ||
             originalCmd.getCommand().equals(ConsoleCommand.LOOP) ||
             originalCmd.getCommand().equals(ConsoleCommand.HILOWS) ||
             originalCmd.getCommand().equals(ConsoleCommand.DMP) ||
             originalCmd.getCommand().equals(ConsoleCommand.EEBRD_ALARMS))
    {
      byte[] newBuffer = Arrays.copyOfRange(buffer, 1, bufferSize);
      byte[] crc = ccitt.calculateCRC(newBuffer, bufferSize - 1);
      if (crc[0] != 0 || crc[1] != 0)
      {
        logger.captureData("  Rx: CRC Failure: " + originalCmd.toString(), Logger.Level.COARSE);
        logger.logData("Rx: CRC Failure: " + originalCmd.toString());
        return;
      }

      switch (originalCmd.getCommand())
      {
        case GETTIME:
          int seconds = newBuffer[0];
          int minutes = newBuffer[1];
          int hour = newBuffer[2];
          int day = newBuffer[3];
          int month = newBuffer[4];
          int year = newBuffer[5] + 1900;

          String monthString = "";
          String adder = " am";
          switch (month)
          {
            case 1:
              monthString = "Jan";
              break;
            case 2:
              monthString = "Feb";
              break;
            case 3:
              monthString = "Mar";
              break;
            case 4:
              monthString = "Apr";
              break;
            case 5:
              monthString = "May";
              break;
            case 6:
              monthString = "Jun";
              break;
            case 7:
              monthString = "Jul";
              break;
            case 8:
              monthString = "Aug";
              break;
            case 9:
              monthString = "Sep";
              break;
            case 10:
              monthString = "Oct";
              break;
            case 11:
              monthString = "Nov";
              break;
            case 12:
              monthString = "Dec";
          }
          if (hour > 12)
          {
            hour -= 12;
            adder = " pm";
          }
          String datestamp = monthString + " " + day + ", " + year;
          String timestamp;
          if (minutes < 10)
          {
            timestamp = hour + ":0" + minutes + ":" + seconds + adder;
          }
          else
          {
            timestamp = hour + ":" + minutes + ":" + seconds + adder;
          }

          if (timeDialog == null)
          {
            timeDialog = new TimeDialog(this.parent, datestamp, timestamp);
          }
          else
          {
            timeDialog.setNewValues(datestamp, timestamp);
            timeDialog.setVisible(true);
          }
          break;

        case LOOP:
          LoopData loopData = new LoopData();
          loopData.setData(newBuffer);
          lastLoopData = loopData;

          currentReadings.updateReadings(loopData);
          wxInterface.setCurrentData(loopData);
          break;

        case HILOWS:
          HiLoData hiloData = new HiLoData();
          hiloData.setData(newBuffer);

          currentReadings.updateMinMax(hiloData);
          break;

        case EEBRD_ALARMS:
          AlarmData alarmData = new AlarmData();
          alarmData.setData(newBuffer);

          if (alarmConfigDialog == null)
          {
            alarmConfigDialog = new AlarmConfigDialog(this.parent, alarmData);
          }
          else
          {
            alarmConfigDialog.setNewValues(alarmData);
            alarmConfigDialog.setVisible(true);
          }
          break;

        case DMP:  // TODO: this does not work.  this is probably not needed.
          extractDmpData(buffer, 0);
          break;
      }
    }
    else if (originalCmd.getCommand() == ConsoleCommand.EEBWR)
    {
      int dataLength = originalCmd.getData().length;
      byte[] byteArray = new byte[dataLength + 2];
      System.arraycopy(originalCmd.getData(), 0, byteArray, 0, dataLength);
      byte[] crcArray = ccitt.calculateCRCByteArray(originalCmd.getData(), dataLength);
      byteArray[dataLength] = crcArray[0];
      byteArray[dataLength + 1] = crcArray[1];
      if (PROPS.getTestMode())
      {
        testDriver.sendCommand(byteArray);
      }
      else
      {
        serialDriver.sendCommand(byteArray);
      }
    }
    else if (originalCmd.getCommand() == ConsoleCommand.DMPAFTDATA)
    {
      extractDmpData(buffer, dmpAftPageOffset);

      // Send ACK
      byte[] byteArray = new byte[] {(byte)0x06};
      if (PROPS.getTestMode())
      {
        testDriver.sendCommand(byteArray);
      }
      else
      {
        serialDriver.sendCommand(byteArray);
      }
    }
  }

  /**
   * Method that extracts the DMP data from a 5 record packet and sends the data to be written to the database.
   *
   * @param buffer The bytes of DMP data.
   * @param firstRecord The index of the first record within the block of 5 records.  The records before this are ignored.
   */
  private void extractDmpData(byte[] buffer, int firstRecord)
  {
    int seqNumber = buffer[0];
    logger.captureData("  Rx: Extracting data from page # " + seqNumber, Logger.Level.MEDIUM);

    int lastPackedDate = 0;
    byte[] nextRecord = new byte[52];
    for (int recordIndex = 0; recordIndex < 5; recordIndex++)
    {
      if (recordIndex < firstRecord)
      {
        logger.captureData("  Rx: Skipping record # " + recordIndex + " of first page.", Logger.Level.MEDIUM);
        continue;
      }

      // Note: The first byte is a sequence number.
      int offset = 1 + recordIndex * 52;
      for (int j = 0; j < 52; j++)
      {
        nextRecord[j] = buffer[offset];
        offset++;
      }

      DmpDataExtended dmpData = new DmpDataExtended();
      dmpData.setData(nextRecord);
      int dateStamp = dmpData.getDateStamp();
      int timeStamp = dmpData.getTimeStamp();

      if ((dateStamp == DB_COMMON.getLastDateStamp() && timeStamp > DB_COMMON.getLastTimeStamp()) ||
          (dateStamp > DB_COMMON.getLastTimeStamp() && timeStamp == 0))
      {
        if (lastPackedDate == 0)
        {
          lastPackedDate = dateStamp;
        }

        if (lastPackedDate == dateStamp || dateStamp > lastPackedDate)
        {
          DB_WRITER.insertWeatherRecord(dmpData);
          dmpData.calculateData(DB_READER.getHeatDDTotal(), DB_READER.getCoolDDTotal());
          DB_WRITER.updateSummaryRecords(dmpData);
//          wxInterface.setDmpData(dmpData); // TODO: was causing an error.
          streamDataThread.addNewData(dmpData);

          WindDirection windDirection = WindDirection.valueOf(dmpData.getPrevailingWindDir());
          if (windDirection != null)
          {
            currentReadings.addPrevailingDirection(windDirection);
          }
          currentReadings.updateForecastRule(dmpData.getForecastRule());
        }
        else
        {
          logger.captureData("  Rx: Discarding next DMPAFT record, date in the past, date = " + dateStamp,
                             Logger.Level.MEDIUM);
        }
      }
    }
  }
}
