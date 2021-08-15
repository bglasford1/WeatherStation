/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This simulator mimics the station protocol.  This allows testing
            without being connected to the simulator.  Actual data captured
            from the station is used to play the data back into the software.
            The communications mechanism is replaced by a simple socket mechanism.

  Mods:		  09/01/21 Initial Release.
*/
package consolesimulator;

import data.consolerecord.PressureTrend;
import util.ByteUtil;
import util.CCITT;
import util.TimeUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Simulator
{
  // If simData is set then the data is generated in code, else the data comes from a dump file.
  private static final boolean simData = true;

  private static final byte ACK = 0x06;
  private static final byte NAK = 0x21;
  private static final byte ESC = 0x1B;
  private static final String WAKEUP_CMD = "\n";
  private static final String WAKEUP_RESP = "\n\r";
  private static final String OK_RESP = "\n\rOK\n\r";
  private static final String TEST_COMMAND = "TEST\n";
  private static final String TEST_RESPONSE = "TEST\n\r";
  private static final String VERSION_CMD = "VERS\n";
  private static final String VERSION_RESP = OK_RESP + "Feb 05 1960\n\r";
  private static final String RECEIVERS_CMD = "RECEIVERS\n";
  private static final String RECEIVERS_RESP = OK_RESP; // Plus needs a byte (bitmap).
  private static final String RXCHECK_CMD = "RXCHECK\n";
  private static final String RXCHECK_RESP = OK_RESP + "18167 76 0 1369 63\n\r";
  private static final String HILOWS_CMD = "HILOWS\n";
  private static final String LOOP_CMD = "LOOP";
  private static final String DMP_CMD = "DMP\n";
  private static final String DMPAFT_CMD = "DMPAFT\n";
  private static final String GETTIME_CMD = "GETTIME\n";
  // returns 5:17:42 Jan 28, 2003
  private static final byte[] GETTIME_RESP = {0x2A, 0x11, 0x05, 0x1C, 0x01, 0x67, 0x00, 0x00};
  private static final String LAMP_ON_CMD = "LAMPS 1\n";
  private static final String LAMP_OFF_CMD = "LAMPS 0\n";
  private static final String EEBRD_ALARMS = "EEBRD 52 5E\n";
  private static final String EEBWR = "EEBWR";

  enum StateValue
  {
    WAITING_FOR_NEXT_CMD,
    WAITING_FOR_DMPAFT_TIME,
    SENDING_DMPAFT_DATA
  }

  private static final String DIRECTORY = "/Users/bill/Documents/Bill/WeatherStation/TestData/";
  private static final String DMP_FILENAME = DIRECTORY + "dmpaft.binary";
  private static final String LOOP_FILENAME = DIRECTORY + "loop.binary";
  private static final String HILOW_FILENAME = DIRECTORY + "hilow.binary";
  private FileInputStream dmpInputStream;
  private byte[] previousDmpData;

  private final CCITT ccitt = new CCITT();

  private boolean waitingForEEBWRData = false;
  
  private Simulator()
  {
    while (true)
    {
      try
      {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Waiting for client to connect...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected...");
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();

        StateValue state = StateValue.WAITING_FOR_NEXT_CMD;

        while (true)
        {
          byte[] data = new byte[100];
          int count = inputStream.read(data);
          if (count > 0)
          {
            System.out.print("Data read: ");
            for (int i = 0; i < count; i++)
            {
              System.out.print(Integer.toHexString(data[i] & 0xFF) + " ");
            }
            System.out.println("");
            
            String inputString = new String(data, Charset.forName("US-ASCII"));
            inputString = inputString.substring(0, count);

            switch (state)
            {
              case WAITING_FOR_NEXT_CMD:
                if (inputString.equals(WAKEUP_CMD))
                {
                  System.out.println("Wakeup Request received...");
                  outputStream.write(WAKEUP_RESP.getBytes());
                  System.out.println("Wrote: " + WAKEUP_RESP);
                }
                else if (inputString.equals(TEST_COMMAND))
                {
                  System.out.println("Test Command received...");
                  outputStream.write(TEST_RESPONSE.getBytes());
                  System.out.println("Wrote: " + TEST_RESPONSE);
                }
                else if (inputString.equals(VERSION_CMD))
                {
                  System.out.println("Version command received...");
                  outputStream.write(VERSION_RESP.getBytes());
                  System.out.println("Wrote: " + VERSION_RESP);
                }
                else if (inputString.equals(RXCHECK_CMD))
                {
                  System.out.println("RX Check command received...");
                  try
                  {
                    Thread.sleep(3000);
                  }
                  catch (InterruptedException e)
                  {
                    e.printStackTrace();
                  }
                  outputStream.write(RXCHECK_RESP.getBytes());
                  System.out.println("Wrote: " + RXCHECK_RESP);
                }
                else if (inputString.equals(RECEIVERS_CMD))
                {
                  System.out.println("Receivers command received...");
                  outputStream.write(RECEIVERS_RESP.getBytes());
                  byte[] responseBitmap = {0x03};
                  outputStream.write(responseBitmap);
                  System.out.println("Wrote: " + RECEIVERS_RESP + " 0x03");
                }
                else if (inputString.equals(GETTIME_CMD))
                {
                  System.out.println("Get time request received...");
                  byte[] crcArray = ccitt.calculateCRCByteArray(GETTIME_RESP, 6);
                  GETTIME_RESP[6] = crcArray[0];
                  GETTIME_RESP[7] = crcArray[1];
                  byte[] response = new byte[9];
                  response[0] = ACK;
                  System.arraycopy(GETTIME_RESP, 0, response, 1, 8);
                  outputStream.write(response);
                  System.out.println("Wrote: " + Arrays.toString(response));
                }
                else if (inputString.equals(LAMP_ON_CMD))
                {
                  System.out.println("Lamp On request received...");
                  outputStream.write(OK_RESP.getBytes());
                  System.out.println("Wrote: " + OK_RESP);
                }
                else if (inputString.equals(LAMP_OFF_CMD))
                {
                  System.out.println("Lamp Off request received...");
                  outputStream.write(OK_RESP.getBytes());
                  System.out.println("Wrote: " + OK_RESP);
                }
                else if (inputString.equals(EEBRD_ALARMS))
                {
                  System.out.println("Alarm Read received...");
                  outputStream.write(ACK);
                  System.out.println("Wrote: ACK");

                  byte[] alarmData = getAlarmData();
                  outputStream.write(alarmData);
                }
                else if (inputString.length() >= 5 && inputString.substring(0, 5).equalsIgnoreCase(EEBWR))
                {
                  System.out.println("EEPROM Write received...");
                  System.out.println("Command = " + inputString);
                  outputStream.write(ACK);
                  System.out.println("Wrote: ACK");
                  waitingForEEBWRData = true;
                }
                else if (waitingForEEBWRData)
                {
                  waitingForEEBWRData = false;
                }
                else if (inputString.equals(HILOWS_CMD))
                {
                  System.out.println("HiLow command received...");
                  outputStream.write(ACK);
                  System.out.println("Wrote: ACK");

                  byte[] hilowData;
                  if (simData)
                  {
                    hilowData = getSimulatedHiLowData();
                  }
                  else
                  {
                    hilowData = getBinaryHiLowData();
                  }

                  if (hilowData != null)
                  {
                    outputStream.write(hilowData);
                  }
                }
                else if (inputString.equals(DMP_CMD))
                {
                  System.out.println("DMP command received...");
                  byte[] dmpData = getBinaryDmpData();

                  if (dmpData != null)
                  {
                    outputStream.write(dmpData);
                    System.out.println("Bytes Wrote: " + ByteUtil.bytesToHex(dmpData));
                  }
                  break;
                }
                else if (inputString.equals(DMPAFT_CMD))
                {
                  System.out.println("DMP AFT command received...");
                  state = StateValue.WAITING_FOR_DMPAFT_TIME;
                }
                else if (count >= 4 && inputString.substring(0, 4).equals(LOOP_CMD))
                {
                  System.out.println("Loop command received...");
                  Integer numberToLoop = Integer.valueOf(inputString.substring(5, 6));
                  outputStream.write(ACK);
                  System.out.println("Wrote: ACK");

                  for (int i = 0; i < numberToLoop; i++)
                  {
                    byte[] loopData;
                    if (simData)
                    {
                      loopData = getSimulatedLoopData();
                    }
                    else
                    {
                      loopData = getBinaryLoopData();
                    }

                    if (loopData != null)
                    {
                      outputStream.write(loopData);
                    }

                    // TODO: delay 2 seconds
                  }
                }
                break;

              case WAITING_FOR_DMPAFT_TIME:
                byte[] crc = ccitt.calculateCRC(data, count);
                if (crc[0] != 0 || crc[1] != 0)
                {
                  System.out.println("DMPAFT time value failed CRC check.  Aborting DMPAFT protocol.");
                  state = StateValue.WAITING_FOR_NEXT_CMD;
                  outputStream.write(ESC);
                  System.out.println("Wrote: ESC");
                }
                else
                {
                  byte[] responseMinusAck = new byte[4];
                  responseMinusAck[0] = 0x01; // # of pages
                  responseMinusAck[1] = 0x00;
                  responseMinusAck[2] = 0x00; // page offset is always 0 for dump converter blocks
                  responseMinusAck[3] = 0x00;
                  byte[] crcBytes = ccitt.calculateCRCByteArray(responseMinusAck, 4);

                  byte[] response = new byte[7];
                  response[0] = ACK;
                  System.arraycopy(responseMinusAck, 0, response, 1, 4);
                  response[5] = crcBytes[0];
                  response[6] = crcBytes[1];
                  outputStream.write(response);
                  System.out.println("Wrote: " + ByteUtil.bytesToHex(response));
//                  openBinaryDmpDataFile();
                  state = StateValue.SENDING_DMPAFT_DATA;
                }
                break;

              case SENDING_DMPAFT_DATA:
                if (count == 1)
                {
                  switch (data[0])
                  {
                    case ACK:
                      // Send next data page.
//                      byte[] dmpData = getBinaryDmpData();
                      byte[] dmpData = getSimulatedDmpData();
                      if (dmpData != null)
                      {
                        outputStream.write(dmpData);
                        previousDmpData = dmpData;
                      }
                      else
                      {
                        System.out.println("No more DMP data.");
                        closeBinaryDmpDataFile();
                        state = StateValue.WAITING_FOR_NEXT_CMD;
                      }
                      break;
                    case NAK:
                      // Send previously sent data page.
                      if (previousDmpData != null)
                      {
                        outputStream.write(previousDmpData);
                      }
                      break;
                    case ESC:
                      System.out.println("Received a cancel command.");
                      closeBinaryDmpDataFile();
                      state = StateValue.WAITING_FOR_NEXT_CMD;
                      break;
                  }
                }
                break;
            }
          }
          else if (count == -1)
          {
            System.out.println("Client Disconnected...");
            return;
          }
          else
          {
            System.out.println("Received 0 bytes...");
          }
        }
      }
      catch (IOException e)
      {
        System.out.println(e.getMessage());
        return;
      }
    }
  }

  private byte[] getBinaryLoopData()
  {
    try (FileInputStream fstream = new FileInputStream(LOOP_FILENAME))
    {
      byte[] loopData = new byte[99];
      int byteCount = fstream.read(loopData);
      System.out.println("Read " + byteCount + " bytes of loop data from file.");
      return loopData;
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      return null;
    }
  }

  private byte[] getBinaryHiLowData()
  {
    try (FileInputStream fstream = new FileInputStream(HILOW_FILENAME))
    {
      byte[] hilowData = new byte[438];
      int byteCount = fstream.read(hilowData);
      System.out.println("Read " + byteCount + " bytes of hilow data from file.");
      return hilowData;
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      return null;
    }
  }

  private void openBinaryDmpDataFile()
  {
    try
    {
      dmpInputStream = new FileInputStream(DMP_FILENAME);
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
  }

  private byte[] getBinaryDmpData()
  {
    try
    {
      byte[] dmpData = new byte[267];
      int byteCount = dmpInputStream.read(dmpData);
      System.out.println("Read " + byteCount + " bytes of dmp data from file.");
      if (byteCount == -1)
        return null;

      // Read and discard the 3 "-"s.
      dmpInputStream.read();
      dmpInputStream.read();
      dmpInputStream.read();
      return dmpData;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  private void closeBinaryDmpDataFile()
  {
    try
    {
      dmpInputStream.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private byte[] getAlarmData()
  {
    byte[] alarmData = new byte[96];

    alarmData[0] = 0;
    alarmData[1] = 0;

    // Time alarms
    alarmData[2] = (byte)0xFF;
    alarmData[3] = (byte)0xFF;
    alarmData[4] = (byte)0xFF;
    alarmData[5] = (byte)0xFF;

    alarmData[6] = (byte)0xFF; // Low temp in alarm not set
    alarmData[7] = (byte)0xBE; // High temp in alarm set to 100 degrees
    alarmData[8] = (byte)0xFF; // Low temp out alarm
    alarmData[9] = (byte)0xFF; // High temp out alarm

    // Misc low temp alarms
    alarmData[10] = (byte)0xFF; // Low temp extra 0
    alarmData[11] = (byte)0xFF; // Low temp extra 1
    alarmData[12] = (byte)0xFF; // Low temp extra 2
    alarmData[13] = (byte)0xFF; // Low temp extra 3
    alarmData[14] = (byte)0xFF; // Low temp extra 4
    alarmData[15] = (byte)0xFF; // Low temp extra 5
    alarmData[16] = (byte)0xFF; // Low temp extra 6
    alarmData[17] = (byte)0xD2; // Low Soil temp 1 set to 120 degrees
    alarmData[18] = (byte)0xFF; // Low Soil temp 2
    alarmData[19] = (byte)0xFF; // Low Soil temp 3
    alarmData[20] = (byte)0xFF; // Low Soil temp 4
    alarmData[21] = (byte)0xFF; // Low Leaf temp 1
    alarmData[22] = (byte)0xFF; // Low Leaf temp 2
    alarmData[23] = (byte)0xFF; // Low Leaf temp 3
    alarmData[24] = (byte)0xFF; // Low Leaf temp 4

    // Misc high temp alarms
    alarmData[25] = (byte)0xFF; // High temp extra 0
    alarmData[26] = (byte)0xFF; // High temp extra 1
    alarmData[27] = (byte)0xFF; // High temp extra 2
    alarmData[28] = (byte)0xFF; // High temp extra 3
    alarmData[29] = (byte)0xFF; // High temp extra 4
    alarmData[30] = (byte)0xFF; // High temp extra 5
    alarmData[31] = (byte)0xFF; // High temp extra 6
    alarmData[32] = (byte)0xFF; // High Soil temp 1 not set
    alarmData[33] = (byte)0xFF; // High Soil temp 2
    alarmData[34] = (byte)0xFF; // High Soil temp 3
    alarmData[35] = (byte)0xFF; // High Soil temp 4
    alarmData[36] = (byte)0xFF; // High Leaf temp 1
    alarmData[37] = (byte)0xFF; // High Leaf temp 2
    alarmData[38] = (byte)0xFF; // High Leaf temp 3
    alarmData[39] = (byte)0xFF; // High Leaf temp 4

    alarmData[40] = (byte)0xFF; // Low in humid alarm
    alarmData[41] = (byte)0xFF; // High in humid alarm

    // Misc Low out humid alarms.
    alarmData[42] = (byte)0xFF; // Low out humid alarm (ISS)
    alarmData[43] = (byte)0xFF; //
    alarmData[44] = (byte)0xFF; //
    alarmData[45] = (byte)0xFF; //
    alarmData[46] = (byte)0xFF; //
    alarmData[47] = (byte)0xFF; //
    alarmData[48] = (byte)0xFF; //
    alarmData[49] = (byte)0xFF; //

    // Misc High out humid alarms.
    alarmData[50] = (byte)0xFF; // High out humid alarm (ISS)
    alarmData[51] = (byte)0xFF; //
    alarmData[52] = (byte)0xFF; //
    alarmData[53] = (byte)0xFF; //
    alarmData[54] = (byte)0xFF; //
    alarmData[55] = (byte)0xFF; //
    alarmData[56] = (byte)0xFF; //
    alarmData[57] = (byte)0xFF; //

    alarmData[58] = (byte)0xFF; // Low Dew alarm
    alarmData[59] = (byte)0xFF; // High dew alarm
    alarmData[60] = (byte)0xFF; // Chill alarm
    alarmData[61] = (byte)0xFF; // Heat alarm
    alarmData[62] = (byte)0xFF; // THSW Alarm
    alarmData[63] = (byte)0xFF; // Speed alarm
    alarmData[64] = (byte)0xFF; // Speed 10 min alarm
    alarmData[65] = (byte)0xFF; // UV alarm
    alarmData[66] = (byte)0xFF; // UV Dose alarm

    // Moisture and Leaf Wetness alarms
    alarmData[67] = (byte)0xFF; // Low Soil moisture alarm 1
    alarmData[68] = (byte)0xFF; // Low Soil moisture alarm 2
    alarmData[69] = (byte)0xFF; // Low Soil moisture alarm 3
    alarmData[70] = (byte)0xFF; // Low Soil moisture alarm 4
    alarmData[71] = (byte)0xFF; // High Soil moisture alarm 1
    alarmData[72] = (byte)0xFF; // High Soil moisture alarm 2
    alarmData[73] = (byte)0xFF; // High Soil moisture alarm 3
    alarmData[74] = (byte)0xFF; // High Soil moisture alarm 4
    alarmData[75] = (byte)0xFF; // Low Leaf wetness alarm 1
    alarmData[76] = (byte)0xFF; // Low Leaf wetness alarm 2
    alarmData[77] = (byte)0xFF; // Low Leaf wetness alarm 3
    alarmData[78] = (byte)0xFF; // Low Leaf wetness alarm 4
    alarmData[79] = (byte)0xFF; // High Leaf wetness alarm 1
    alarmData[80] = (byte)0xFF; // High Leaf wetness alarm 2
    alarmData[81] = (byte)0xFF; // High Leaf wetness alarm 3
    alarmData[82] = (byte)0xFF; // High Leaf wetness alarm 4

    // Solar alarm
    alarmData[83] = (byte)0x7F; //
    alarmData[84] = (byte)0xFF; //

    // Rain rate alarm
    alarmData[85] = (byte)0xFF; //
    alarmData[86] = (byte)0xFF; //

    // Rain 15 minute alarm
    alarmData[87] = (byte)0xFF; //
    alarmData[88] = (byte)0xFF; //

    // Rain 24 hour alarm
    alarmData[89] = (byte)0xFF; //
    alarmData[90] = (byte)0xFF; //

    // Rain storm alarm
    alarmData[91] = (byte)0xFF; //
    alarmData[92] = (byte)0xFF; //

    alarmData[93] = (byte)0xFF; // ET day alarm

    byte[] crcArray = ccitt.calculateCRCByteArray(alarmData, 94);
    alarmData[94] = crcArray[0];
    alarmData[95] = crcArray[1];
    return alarmData;
  }

  private byte[] getSimulatedDmpData()
  {
    byte[] dmpData = new byte[267];
    dmpData[0] = 0x00; // sequence number

    LocalDateTime now = LocalDateTime.now();
    short dateStamp = TimeUtil.getDateStamp(now.getDayOfMonth(), now.getMonthValue(), now.getYear());
    byte[] dateStampBytes = ByteUtil.shortToByteArray(dateStamp);
    dmpData[1] = dateStampBytes[1];
    dmpData[2] = dateStampBytes[0];

    int minute = (now.getMinute() + 4) / 5 * 5;
    short timeStamp = TimeUtil.getTimestamp(now.getHour(), minute);
    byte[] timeStampBytes = ByteUtil.shortToByteArray(timeStamp);
    dmpData[3] = timeStampBytes[1];
    dmpData[4] = timeStampBytes[0];
    System.out.println("DMPAFT: DateStamp: " + dateStamp + ", Timestamp: " + timeStamp);

    byte[] outTemp = ByteUtil.shortToByteArray(712);
    dmpData[5] = outTemp[1];
    dmpData[6] = outTemp[0];

    byte[] highOutTemp = ByteUtil.shortToByteArray(777);
    dmpData[7] = highOutTemp[1];
    dmpData[8] = highOutTemp[0];

    byte[] lowOutTemp = ByteUtil.shortToByteArray(696);
    dmpData[9] = lowOutTemp[1];
    dmpData[10] = lowOutTemp[0];

    byte[] dayRain = ByteUtil.shortToByteArray(1);
    dmpData[11] = dayRain[1];
    dmpData[12] = dayRain[0];

    byte[] highRainRate = ByteUtil.shortToByteArray(10);
    dmpData[13] = highRainRate[1];
    dmpData[14] = highRainRate[0];

    byte[] pressure = ByteUtil.shortToByteArray(29907);
    dmpData[15] = pressure[1];
    dmpData[16] = pressure[0];

    byte[] solarRad = ByteUtil.shortToByteArray(100);
    dmpData[17] = solarRad[1];
    dmpData[18] = solarRad[0];

    byte[] numWindSamples = ByteUtil.shortToByteArray(100);
    dmpData[19] = numWindSamples[1];
    dmpData[20] = numWindSamples[0];

    byte[] inTemp = ByteUtil.shortToByteArray(696);
    dmpData[21] = inTemp[1];
    dmpData[22] = inTemp[0];

    dmpData[23] = 0x18; // in Humid
    dmpData[24] = 0x20; // out Humid
    dmpData[25] = 0x05; // Avg wind speed
    dmpData[26] = 0x11; // High wind speed
    dmpData[27] = 0x04; // Dir high wind speed
    dmpData[28] = 0x00; // Prevailing wind dir
    dmpData[29] = 0x00; // Avg UV
    dmpData[30] = 0x00; // ET;
    dmpData[31] = (byte)0xFF; // Unused

    // Extra Temps, soil temp, leaf temp
    for (int j = 32; j < 52; j++)
    {
      dmpData[j] = (byte)0xFF;
    }
    dmpData[36] = (byte)0xA5; // Soil1 Temp

    int offset = 53;
    byte[] dummyRecord = createDummyRecord();
    for (int i = 0; i < 4; i++)
    {
      for (byte nextByte : dummyRecord)
      {
        dmpData[offset] = nextByte;
        offset++;
      }
    }

    dmpData[261] = (byte)0xFF;
    dmpData[262] = (byte)0xFF;
    dmpData[263] = (byte)0xFF;
    dmpData[264] = (byte)0xFF;

    byte[] crcArray = ccitt.calculateCRCByteArray(dmpData, 265);
    dmpData[265] = crcArray[0];
    dmpData[266] = crcArray[1];
    return dmpData;
  }

  /**
   * Create a dummy record.  This is all 0xFF values except for the datestamp which is put 9 days into the past.
   * This signals the program that this is old data and ignore this record.
   *
   * @return A 52 byte array.
   */
  private byte[] createDummyRecord()
  {
    byte[] byteArray = new byte[52];
    int index = 0;

    LocalDate localDate = LocalDate.now().minusDays(9);
    addField(byteArray, index, TimeUtil.getDateStamp(localDate.getDayOfMonth(),
                                                     localDate.getMonthValue(),
                                                     localDate.getYear()));
    index += 2;

    addField(byteArray, index, (short)0x0005);
    index += 2;

    for (int i = 0; i < 48; i++)
    {
      byteArray[index] = (byte)0xFF; // Dummy Data
      index++;
    }

    return byteArray;
  }

  /**
   * This is a convenience method that adds a short field (2 bytes) into a byte array.
   *
   * @param byteArray The array to add the two bytes to.
   * @param index Where within the array to add the bytes.
   * @param field The short field to add.
   */
  private void addField(byte[] byteArray, int index, short field)
  {
    byte[] bytes = ByteUtil.shortToByteArray(field);
    byteArray[index] = bytes[1];
    byteArray[index + 1] = bytes[0];
  }

  private byte[] getSimulatedLoopData()
  {
    byte[] loopData = new byte[99];
    byte[] bytes = "L00".getBytes();
    loopData[0] = bytes[0];
    loopData[1] = bytes[1];
    loopData[2] = bytes[2];
    loopData[3] = (byte)PressureTrend.RISING_SLOWLY.value();
    loopData[4] = 1;
    loopData[5] = 0x1D; // next record number.
    loopData[6] = 0x06; // next record number.

    byte[] pressure = ByteUtil.shortToByteArray(29907);
    loopData[7] = pressure[1];
    loopData[8] = pressure[0];

    byte[] inTemp = ByteUtil.shortToByteArray(696);
    loopData[9] = inTemp[1];
    loopData[10] = inTemp[0];

    loopData[11] = 0x1C; // in Humid

    byte[] outTemp = ByteUtil.shortToByteArray(712);
    loopData[12] = outTemp[1];
    loopData[13] = outTemp[0];

    loopData[14] = 0x11; // wind speed
    loopData[15] = 0x05; // Avg wind speed

    byte[] windDir = ByteUtil.shortToByteArray(45);
    loopData[16] = windDir[1];
    loopData[17] = windDir[0];

    // Extra Temps, soil temp, leaf temp
    for (int j = 18; j < 33; j++)
    {
      loopData[j] = (byte)0xFF;
    }
    loopData[25] = (byte)0xA5; // Soil1 Temp

    loopData[33] = 0x18; // out Humid

    // Extra Humids
    for (int j = 34; j < 41; j++)
    {
      loopData[j] = (byte)0xFF;
    }

    byte[] rainRate = ByteUtil.shortToByteArray(10);
    loopData[41] = rainRate[1];
    loopData[42] = rainRate[0];

    loopData[43] = (byte)0xFF; // UV

    byte[] solarRad = ByteUtil.shortToByteArray(100);
    loopData[44] = solarRad[1];
    loopData[45] = solarRad[0];

    byte[] stormRain = ByteUtil.shortToByteArray(123);
    loopData[46] = stormRain[1];
    loopData[47] = stormRain[0];

//    byte[] stormStartDate = ByteUtil.convertDateToStormDate(2019, 2, 5);
//    loopData[48] = stormStartDate[1];
//    loopData[49] = stormStartDate[0];
    loopData[48] = (byte)0xFF;
    loopData[49] = (byte)0xFF;

    byte[] dayRain = ByteUtil.shortToByteArray(0);
    loopData[50] = dayRain[1];
    loopData[51] = dayRain[0];

    byte[] monthRain = ByteUtil.shortToByteArray(220);
    loopData[52] = monthRain[1];
    loopData[53] = monthRain[0];

    byte[] yearRain = ByteUtil.shortToByteArray(330);
    loopData[54] = yearRain[1];
    loopData[55] = yearRain[0];

    // ETs, soil moisture, leaf wetness
    for (int j = 56; j < 70; j++)
    {
      loopData[j] = (byte)0xFF;
    }

    // Alarm data
    loopData[70] = (byte)0x08; // insideAlarms
    loopData[71] = (byte)0x00; // rainAlarms
    loopData[72] = (byte)0x00; // outsideAlarmsByte1
    loopData[73] = (byte)0x00; // outsideAlarmsByte2
    loopData[74] = (byte)0x00; // outsideHumidAlarms

    // soil moisture & leaf wetness alarms
    for (int j = 75; j < 86; j++)
    {
      loopData[j] = (byte)0xFF;
    }

    loopData[86] = 0x0; // battery status

    byte[] batteryVoltage = ByteUtil.shortToByteArray(563);
    loopData[87] = batteryVoltage[1];
    loopData[88] = batteryVoltage[0];

    loopData[89] = 0x18; // forecast icons
    loopData[90] = 0x0; // forecast rule number

    byte[] sunriseTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(6, 30));
    loopData[91] = sunriseTime[1];
    loopData[92] = sunriseTime[0];

    byte[] sunsetTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(17, 30));
    loopData[93] = sunsetTime[1];
    loopData[94] = sunsetTime[0];

    loopData[95] = 0x0A; // LF
    loopData[96] = 0x0D; // CR

    byte[] crcArray = ccitt.calculateCRCByteArray(loopData, 97);
    loopData[97] = crcArray[0];
    loopData[98] = crcArray[1];
    return loopData;
  }

  private byte[] getSimulatedHiLowData()
  {
    byte[] hiLowData = new byte[438];

    //*** Pressure ***//
    byte[] dayLowPressure = ByteUtil.shortToByteArray(29500);
    hiLowData[0] = dayLowPressure[1];
    hiLowData[1] = dayLowPressure[0];

    byte[] dayHighPressure = ByteUtil.shortToByteArray(30100);
    hiLowData[2] = dayHighPressure[1];
    hiLowData[3] = dayHighPressure[0];

    byte[] monthLowPressure = ByteUtil.shortToByteArray(29300);
    hiLowData[4] = monthLowPressure[1];
    hiLowData[5] = monthLowPressure[0];

    byte[] monthHighPressure = ByteUtil.shortToByteArray(30300);
    hiLowData[6] = monthHighPressure[1];
    hiLowData[7] = monthHighPressure[0];

    byte[] yearLowPressure = ByteUtil.shortToByteArray(29100);
    hiLowData[8] = yearLowPressure[1];
    hiLowData[9] = yearLowPressure[0];

    byte[] yearHighPressure = ByteUtil.shortToByteArray(30500);
    hiLowData[10] = yearHighPressure[1];
    hiLowData[11] = yearHighPressure[0];

    byte[] dayLowPressureTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(6, 30));
    hiLowData[12] = dayLowPressureTime[1];
    hiLowData[13] = dayLowPressureTime[0];

    byte[] dayHighPressureTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(17, 30));
    hiLowData[14] = dayHighPressureTime[1];
    hiLowData[15] = dayHighPressureTime[0];

    //*** Wind Speed ***//
    hiLowData[16] = 0x18; // day high wind speed

    byte[] dayHighWindSpeedTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(12, 0));
    hiLowData[17] = dayHighWindSpeedTime[1];
    hiLowData[18] = dayHighWindSpeedTime[0];

    hiLowData[19] = 0x20; // month high wind speed
    hiLowData[20] = 0x22; // year high wind speed

    //*** Inside Temperature ***//
    byte[] dayHighInTemp = ByteUtil.shortToByteArray(722);
    hiLowData[21] = dayHighInTemp[1];
    hiLowData[22] = dayHighInTemp[0];

    byte[] dayLowInTemp = ByteUtil.shortToByteArray(720);
    hiLowData[23] = dayLowInTemp[1];
    hiLowData[24] = dayLowInTemp[0];

    byte[] dayHighInTempTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(13, 30));
    hiLowData[25] = dayHighInTempTime[1];
    hiLowData[26] = dayHighInTempTime[0];

    byte[] dayLowInTempTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(8, 30));
    hiLowData[27] = dayLowInTempTime[1];
    hiLowData[28] = dayLowInTempTime[0];

    byte[] monthLowInTemp = ByteUtil.shortToByteArray(677);
    hiLowData[29] = monthLowInTemp[1];
    hiLowData[30] = monthLowInTemp[0];

    byte[] monthHighInTemp = ByteUtil.shortToByteArray(733);
    hiLowData[31] = monthHighInTemp[1];
    hiLowData[32] = monthHighInTemp[0];

    byte[] yearLowInTemp = ByteUtil.shortToByteArray(655);
    hiLowData[33] = yearLowInTemp[1];
    hiLowData[34] = yearLowInTemp[0];

    byte[] yearHighInTemp = ByteUtil.shortToByteArray(755);
    hiLowData[35] = yearHighInTemp[1];
    hiLowData[36] = yearHighInTemp[0];

    //*** Inside Humidity ***//
    hiLowData[37] = 0x20; // day high inside humidity
    hiLowData[38] = 0x18; // day low inside humidity

    byte[] dayHighInHumidTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(11, 30));
    hiLowData[39] = dayHighInHumidTime[1];
    hiLowData[40] = dayHighInHumidTime[0];

    byte[] dayLowInHumidTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(9, 30));
    hiLowData[41] = dayLowInHumidTime[1];
    hiLowData[42] = dayLowInHumidTime[0];

    hiLowData[43] = 0x22; // month high inside humidity
    hiLowData[44] = 0x16; // month low inside humidity
    hiLowData[45] = 0x24; // year high inside humidity
    hiLowData[46] = 0x14; // year low inside humidity

    //*** Outside Temperature ***//
    byte[] dayLowOutTemp = ByteUtil.shortToByteArray(600);
    hiLowData[47] = dayLowOutTemp[1];
    hiLowData[48] = dayLowOutTemp[0];

    byte[] dayHighOutTemp = ByteUtil.shortToByteArray(800);
    hiLowData[49] = dayHighOutTemp[1];
    hiLowData[50] = dayHighOutTemp[0];

    byte[] dayLowOutTempTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(10, 30));
    hiLowData[51] = dayLowOutTempTime[1];
    hiLowData[52] = dayLowOutTempTime[0];

    byte[] dayHighOutTempTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(2, 30));
    hiLowData[53] = dayHighOutTempTime[1];
    hiLowData[54] = dayHighOutTempTime[0];

    byte[] monthHighOutTemp = ByteUtil.shortToByteArray(822);
    hiLowData[55] = monthHighOutTemp[1];
    hiLowData[56] = monthHighOutTemp[0];

    byte[] monthLowOutTemp = ByteUtil.shortToByteArray(588);
    hiLowData[57] = monthLowOutTemp[1];
    hiLowData[58] = monthLowOutTemp[0];

    byte[] yearHighOutTemp = ByteUtil.shortToByteArray(844);
    hiLowData[59] = yearHighOutTemp[1];
    hiLowData[60] = yearHighOutTemp[0];

    byte[] yearLowOutTemp = ByteUtil.shortToByteArray(555);
    hiLowData[61] = yearLowOutTemp[1];
    hiLowData[62] = yearLowOutTemp[0];

    //*** Dew Point ***//
    byte[] dayLowDewPoint = ByteUtil.shortToByteArray(800);
    hiLowData[63] = dayLowDewPoint[1];
    hiLowData[64] = dayLowDewPoint[0];

    byte[] dayHighDewPoint = ByteUtil.shortToByteArray(600);
    hiLowData[65] = dayHighDewPoint[1];
    hiLowData[66] = dayHighDewPoint[0];

    byte[] dayLowDewPointTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(10, 30));
    hiLowData[67] = dayLowDewPointTime[1];
    hiLowData[68] = dayLowDewPointTime[0];

    byte[] dayHighDewPointTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(2, 30));
    hiLowData[69] = dayHighDewPointTime[1];
    hiLowData[70] = dayHighDewPointTime[0];

    byte[] monthHighDewPoint = ByteUtil.shortToByteArray(822);
    hiLowData[71] = monthHighDewPoint[1];
    hiLowData[72] = monthHighDewPoint[0];

    byte[] monthLowDewPoint = ByteUtil.shortToByteArray(588);
    hiLowData[73] = monthLowDewPoint[1];
    hiLowData[74] = monthLowDewPoint[0];

    byte[] yearHighDewPoint = ByteUtil.shortToByteArray(844);
    hiLowData[75] = yearHighDewPoint[1];
    hiLowData[76] = yearHighDewPoint[0];

    byte[] yearLowDewPoint = ByteUtil.shortToByteArray(555);
    hiLowData[77] = yearLowDewPoint[1];
    hiLowData[78] = yearLowDewPoint[0];

    //*** Wind Chill ***//
    byte[] dayLowWindChill = ByteUtil.shortToByteArray(800);
    hiLowData[79] = dayLowWindChill[1];
    hiLowData[80] = dayLowWindChill[0];

    byte[] dayLowWindChillTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(10, 30));
    hiLowData[81] = dayLowWindChillTime[1];
    hiLowData[82] = dayLowWindChillTime[0];

    byte[] monthLowWindChill = ByteUtil.shortToByteArray(588);
    hiLowData[83] = monthLowWindChill[1];
    hiLowData[84] = monthLowWindChill[0];

    byte[] yearLowWindChill = ByteUtil.shortToByteArray(555);
    hiLowData[85] = yearLowWindChill[1];
    hiLowData[86] = yearLowWindChill[0];

    //*** Heat Index ***//
    byte[] dayHighHeatIndex = ByteUtil.shortToByteArray(800);
    hiLowData[87] = dayHighHeatIndex[1];
    hiLowData[88] = dayHighHeatIndex[0];

    byte[] dayHighHeatIndexTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(10, 30));
    hiLowData[89] = dayHighHeatIndexTime[1];
    hiLowData[90] = dayHighHeatIndexTime[0];

    byte[] monthHighHeatIndex = ByteUtil.shortToByteArray(588);
    hiLowData[91] = monthHighHeatIndex[1];
    hiLowData[92] = monthHighHeatIndex[0];

    byte[] yearHighHeatIndex = ByteUtil.shortToByteArray(555);
    hiLowData[93] = yearHighHeatIndex[1];
    hiLowData[94] = yearHighHeatIndex[0];

    //*** THSW Index ***//
    byte[] dayHighTHSWIndex = ByteUtil.shortToByteArray(800);
    hiLowData[95] = dayHighTHSWIndex[1];
    hiLowData[96] = dayHighTHSWIndex[0];

    byte[] dayHighTHSWIndexTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(10, 30));
    hiLowData[97] = dayHighTHSWIndexTime[1];
    hiLowData[98] = dayHighTHSWIndexTime[0];

    byte[] monthHighTHSWIndex = ByteUtil.shortToByteArray(588);
    hiLowData[99] = monthHighTHSWIndex[1];
    hiLowData[100] = monthHighTHSWIndex[0];

    byte[] yearHighTHSWIndex = ByteUtil.shortToByteArray(555);
    hiLowData[101] = yearHighTHSWIndex[1];
    hiLowData[102] = yearHighTHSWIndex[0];

    //*** Solar Radiation ***//
    byte[] dayHighSolarRad = ByteUtil.shortToByteArray(800);
    hiLowData[103] = dayHighSolarRad[1];
    hiLowData[104] = dayHighSolarRad[0];

    byte[] dayHighSolarRadTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(10, 30));
    hiLowData[105] = dayHighSolarRadTime[1];
    hiLowData[106] = dayHighSolarRadTime[0];

    byte[] monthHighSolarRad = ByteUtil.shortToByteArray(588);
    hiLowData[107] = monthHighSolarRad[1];
    hiLowData[108] = monthHighSolarRad[0];

    byte[] yearHighSolarRad = ByteUtil.shortToByteArray(555);
    hiLowData[109] = yearHighSolarRad[1];
    hiLowData[110] = yearHighSolarRad[0];

    //*** UV ***//
    hiLowData[111] = 0x22; // day high UV

    byte[] dayHighUVTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(10, 30));
    hiLowData[112] = dayHighUVTime[1];
    hiLowData[113] = dayHighUVTime[0];

    hiLowData[114] = 0x22; // month high UV
    hiLowData[115] = 0x22; // year high UV

    //*** Rain Rate ***//
    byte[] dayHighRainRate = ByteUtil.shortToByteArray(60);
    hiLowData[116] = dayHighRainRate[1];
    hiLowData[117] = dayHighRainRate[0];

    byte[] dayHighRainRateTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(10, 30));
    hiLowData[118] = dayHighRainRateTime[1];
    hiLowData[119] = dayHighRainRateTime[0];

    byte[] hourHighRainRate = ByteUtil.shortToByteArray(10);
    hiLowData[120] = hourHighRainRate[1];
    hiLowData[121] = hourHighRainRate[0];

    byte[] monthHighRainRate = ByteUtil.shortToByteArray(120);
    hiLowData[122] = monthHighRainRate[1];
    hiLowData[123] = monthHighRainRate[0];

    byte[] yearHighRainRate = ByteUtil.shortToByteArray(150);
    hiLowData[124] = yearHighRainRate[1];
    hiLowData[125] = yearHighRainRate[0];

    //*** Extra Leaf/Soil Temps ***//
    for (int i = 126; i < 133; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }
    hiLowData[133] = (byte)0x87;

    for (int i = 134; i < 148; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }
    hiLowData[148] = (byte)0xB2;

    for (int i = 149; i < 172; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }
    byte[] timeOfLowSoil1Temp = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(2, 20));
    hiLowData[172] = timeOfLowSoil1Temp[1];
    hiLowData[173] = timeOfLowSoil1Temp[0];

    for (int i = 174; i < 202; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }
    byte[] timeOfHighSoil1Temp = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(4, 40));
    hiLowData[202] = timeOfHighSoil1Temp[1];
    hiLowData[203] = timeOfHighSoil1Temp[0];

    for (int i = 204; i < 223; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }
    hiLowData[223] = (byte)0xB3;

    for (int i = 224; i < 238; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }
    hiLowData[238] = (byte)0x84;

    for (int i = 239; i < 253; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }
    hiLowData[253] = (byte)0xB4;

    for (int i = 254; i < 268; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }
    hiLowData[268] = (byte)0x82;

    for (int i = 269; i < 276; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }

    //*** Outside Humidity & Extra Outside Humidity ***//
    hiLowData[276] = 0x18; // day low outside humidity

    for (int i = 277; i < 284; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }

    hiLowData[284] = 0x20; // day high outside humidity

    for (int i = 285; i < 292; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }

    byte[] dayLowOutHumidTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(11, 30));
    hiLowData[292] = dayLowOutHumidTime[1];
    hiLowData[293] = dayLowOutHumidTime[0];

    for (int i = 294; i < 308; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }

    byte[] dayHighOutHumidTime = ByteUtil.shortToByteArray(TimeUtil.getPackedTime(9, 30));
    hiLowData[308] = dayHighOutHumidTime[1];
    hiLowData[309] = dayHighOutHumidTime[0];

    for (int i = 310; i < 324; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }

    hiLowData[324] = 0x22; // month high inside humidity

    for (int i = 325; i < 332; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }

    hiLowData[332] = 0x16; // month low inside humidity

    for (int i = 333; i < 340; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }

    hiLowData[340] = 0x24; // year high inside humidity

    for (int i = 341; i < 348; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }

    hiLowData[348] = 0x14; // year low inside humidity

    for (int i = 349; i < 356; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }

    //*** Soil Moisture / Leaf Wetness***//
    for (int i = 356; i < 436; i++)
    {
      hiLowData[i] = (byte)0xFF;
    }

    byte[] crcArray = ccitt.calculateCRCByteArray(hiLowData, 436);
    hiLowData[436] = crcArray[0];
    hiLowData[437] = crcArray[1];

    return hiLowData;
  }
  
  public static void main(String[] args)
  {
    new Simulator();
  }
}
