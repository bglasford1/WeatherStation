/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for storing console data.  The long term
            data is stored to flat files that are the same ones used by the
            legacy Davis program.  This data is derived from the HiLow data and
            the DMP/DMPAFT data.  Each file contains one month worth of data.
            The file header contains information about the number of records in
            a day and the offset into the file for that day's worth of data.
            The data can be saved at different intervals depending on the
            settings in the console.  Data can also be missing.

  Mods:		  09/01/21  Initial Release.
            10/15/21  Fixed ET calculation.
            10/18/21  Added Summary 1 & 2 data tables.
*/
package dbif;

import algorithms.Calculations;
import data.consolerecord.DmpData;
import data.consolerecord.DmpDataExtended;
import data.dbrecord.*;
import util.ByteUtil;
import util.ConfigProperties;
import util.Logger;
import util.TimeUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class DatabaseWriter
{
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private static final DatabaseCommon DB_COMMON = DatabaseCommon.getInstance();
  private static final DatabaseReader DB_READER = DatabaseReader.getInstance();
  private final Logger logger = Logger.getInstance();
  private final String databaseLocation;

  public DatabaseWriter()
  {
    databaseLocation = DatabaseCommon.getDirectory();
  }

  /**
   * Internal redirect method to make the remaining code easier to read.
   *
   * @param out The output file stream.
   * @param datum The data to write.
   * @throws IOException Any exception that may be thrownl
   */
  private void writeShortValue(FileOutputStream out, short datum) throws IOException
  {
    DB_COMMON.writeShortValue(out, datum);
  }

  /**
   * Internal redirect method to make the remaining code easier to read.
   *
   * @param out The random access file.
   * @param offset The offset to the first byte.
   * @param datum The data to write.
   * @throws IOException Any exception that may be thrown.
   */
  private void writeShortValue(RandomAccessFile out, int offset, short datum) throws IOException
  {
    DB_COMMON.writeShortValue(out, offset, datum);
  }

  /**
   * Internal redirect method to make the remaining code easier to read.
   *
   * @param updateFile The open random access file.
   * @param offset The byte offset.
   * @return The value read.
   * @throws IOException Any I/O exception is rethrown for the caller to handle.
   */
  private short readTwoByteValues(RandomAccessFile updateFile, int offset) throws IOException
  {
    return DB_COMMON.readTwoByteValues(updateFile, offset);
  }

  /**
   * This method writes a time value with an even index.  In the time value area, two values share three bytes.
   * The even indexes use the first byte and the lower half of the third byte as the high order value byte.
   * The summary records use random file access since the fields already exist.  This routine does not handle
   * any file I/O exception, but simply throws it to the caller.
   *
   * @param value  The two byte value to write.
   * @param updateFile The file pointer.
   * @param offset1 The offset of the first, low order byte to write.
   * @param offset2 The offset of the second, high order byte to write.
   * @throws IOException Any exception that may be encountered.
   */
  private void updateEvenTimeValue(short value, RandomAccessFile updateFile, int offset1, int offset2)
      throws IOException
  {
    byte[] valueBytes = ByteUtil.shortToByteArray(value);
    updateFile.seek(offset1);
    updateFile.write(valueBytes[1] & 0xFF);
    updateFile.seek(offset2);
    byte originalByte = (byte) (updateFile.readByte() & 0xF0);
    byte lowOrderByte = (byte) (valueBytes[0] & 0x0F);
    byte secondByte = (byte)(lowOrderByte | originalByte);
    updateFile.seek(offset2); // Must reset the file pointer because the read is moving the pointer.
    updateFile.write(secondByte & 0xFF);
  }

  /**
   * This method writes a time value with an odd index.  In the time value area, two values share three bytes.
   * The odd indexes use the second byte and the higher half of the third byte as the high order value byte.
   * The summary records use random file access since the fields already exist.  This routine does not handle
   * any file I/O exception, but simply throws it to the caller.
   *
   * @param value  The two byte value to write.
   * @param updateFile The file pointer.
   * @param offset1 The offset of the first, low order byte to write.
   * @param offset2 The offset of the second, high order byte to write.
   * @throws IOException Any exception that may be encountered.
   */
  private void updateOddTimeValue(short value, RandomAccessFile updateFile, int offset1, int offset2)
      throws IOException
  {
    byte[] valueBytes = ByteUtil.shortToByteArray(value);
    updateFile.seek(offset1);
    updateFile.write(valueBytes[1] & 0xFF);
    updateFile.seek(offset2);
    byte originalByte = (byte) (updateFile.readByte() & 0x0F);
    byte highOrderByte = (byte) ((valueBytes[0] << 4) & 0xF0);
    byte secondByte = (byte)(highOrderByte | originalByte);
    updateFile.seek(offset2); // Must reset the file pointer because the read is moving the pointer.
    updateFile.write(secondByte & 0xFF);
  }

  /**
   * This method writes a wind bin value with an even index.  In the wind bin value area, two values share three bytes.
   * The even indexes use the first byte and the lower half of the third byte as the high order value byte.
   * The summary records use random file access since the fields already exist.  This routine does not handle
   * any file I/O exception, but simply throws it to the caller.
   *
   * @param updateFile The file pointer.
   * @param offset1 The offset of the first, low order byte to write.
   * @param offset2 The offset of the second, high order byte to write.
   * @throws IOException Any exception that may be encountered.
   */
  private void updateEvenWindValue(RandomAccessFile updateFile, int offset1, int offset2)
    throws IOException
  {
    // Read existing value and add 5 minutes.
    byte[] valueBytes = new byte[2];
    updateFile.seek(offset1);
    valueBytes[1] = (byte)(updateFile.readByte() & 0xFF);
    updateFile.seek(offset2);
    valueBytes[0] = (byte)(updateFile.readByte() & 0x0F);
    // Add the archive interval worth of data because that is what the data record represents.
    // It is assumed the entire record is represented by the single wind direction value.
    short value = (short)(PROPS.getArchiveInterval() + ByteUtil.byteArrayToShort(valueBytes));

    // Write back out the value.
    byte[] outputBytes = ByteUtil.shortToByteArray(value);
    updateFile.seek(offset1);
    updateFile.write(outputBytes[1] & 0xFF);
    updateFile.seek(offset2);
    byte originalByte = (byte) (updateFile.readByte() & 0xF0);
    byte highOrderByte = (byte) (outputBytes[0] & 0x0F);
    byte byteToWrite = (byte)(originalByte | highOrderByte);
    updateFile.seek(offset2);
    updateFile.write(byteToWrite & 0xFF);
  }

  /**
   * This method writes a wind bin value with an odd index.  In the wind bin value area, two values share three bytes.
   * The odd indexes use the second byte and the higher half of the third byte as the high order value byte.
   * The summary records use random file access since the fields already exist.  This routine does not handle
   * any file I/O exception, but simply throws it to the caller.
   *
   * @param updateFile The file pointer.
   * @param offset1 The offset of the first, low order byte to write.
   * @param offset2 The offset of the second, high order byte to write.
   * @throws IOException Any exception that may be encountered.
   */
  private void updateOddWindValue(RandomAccessFile updateFile, int offset1, int offset2)
    throws IOException
  {
    // Read existing value and add 5 minutes.
    byte[] valueBytes = new byte[2];
    updateFile.seek(offset1);
    valueBytes[1] = (byte)(updateFile.readByte() & 0xFF);
    updateFile.seek(offset2);
    valueBytes[0] = (byte)((updateFile.readByte() & 0xF0) >> 4);
    // Add the archive interval worth of data because that is what the data record represents.
    // It is assumed the entire record is represented by the single wind direction value.
    short value = (short)(PROPS.getArchiveInterval() + ByteUtil.byteArrayToShort(valueBytes));

    // Write back out the value.
    byte[] outputBytes = ByteUtil.shortToByteArray(value);
    updateFile.seek(offset1);
    updateFile.write(outputBytes[1] & 0xFF);
    updateFile.seek(offset2);
    byte originalByte = (byte) (updateFile.readByte() & 0x0F);
    byte highOrderByte = (byte) ((outputBytes[0] << 4) & 0xF0);
    byte byteToWrite = (byte)(highOrderByte | originalByte);
    updateFile.seek(offset2);
    updateFile.write(byteToWrite & 0xFF);
  }

  /**
   * This is a convience method to add a value to the summary record. Only a two byte short value can be written.
   *
   * @param updateFile The file to modify, opened for writing.
   * @param data The data value to write
   * @param offset The file offset
   * @throws IOException Any I/O exceptions are thrown to the caller.
   */
  private void updateSummaryRecord(RandomAccessFile updateFile, short data, int offset) throws IOException
  {
    byte[] byteArray = ByteUtil.shortToByteArray(data);
    updateFile.seek(offset);
    updateFile.write(byteArray[1]);
    updateFile.seek(offset + 1);
    updateFile.write(byteArray[0]);
  }

  /**
   * This is called to create a new data file.  There is one data file per month.  The format of the file name
   * is yyyy-mm.wlk.
   */
  private void createNewDataFile(int year, int month)
  {
    String filename = DatabaseCommon.getFilename(year, month);
    try
    {
      File file = new File(databaseLocation + filename);

      if (!file.createNewFile())
      {
        logger.logData("File " + filename + " already exists.");
        return;
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    // Write header bytes
    byte data[] = DatabaseCommon.FILE_HEADER.getBytes(StandardCharsets.UTF_8);
    try (FileOutputStream out = new FileOutputStream(databaseLocation + filename))
    {
      // Write header line
      out.write(data);
      for (int i = 0; i < 7; i++)
      {
        out.write(0x00);
      }
      out.write(0x05);
      out.write(0x03);

      // Output placeholder for total records in file (4 bytes) and the day index records (32 * 6).
      for (int i = 0; i < 196; i++)
      {
        out.write(0x00);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Insert the two new summary records based on the new DMP record into the database.  At the end of the day
   * these summary records will be updated based on the hi/low data.  In the interim, some of the values are
   * updated based on data within the DMP data.
   *
   * @param filename The filename of the latest record.
   * @param data The DMP data to insert.
   */
  private void insertNewSummaryRecords(String filename, DmpData data)
  {
    try (FileOutputStream out = new FileOutputStream(databaseLocation + filename, true))
    {
      // Create summary 1 record bytes
      out.write(0x02); // the data type = Daily Summary Record 1
      out.write(0x00); // reserved
      out.write(0x00); // data span record, no records written yet.
      out.write(0x00); // second byte of data span record.
      writeShortValue(out, data.getHighOutsideTempNative());
      writeShortValue(out, data.getLowOutsideTempNative());
      writeShortValue(out, data.getInsideTempNative()); // hi inside temp
      writeShortValue(out, data.getInsideTempNative()); // low inside temp
      writeShortValue(out, data.getHighOutsideTempNative()); // Average outside temp, assume same as hi outside temp
      writeShortValue(out, data.getInsideTempNative()); // average inside temp

      writeShortValue(out, (short)0); // Hi wind chill
      writeShortValue(out, (short)999); // Low wind chill
      writeShortValue(out, (short)0);  // Hi dew point
      writeShortValue(out, (short)999);  // Low dew point
      writeShortValue(out, (short)0); // Avg wind chill
      writeShortValue(out, (short)0);  // Avg dew point

      writeShortValue(out, (short)(data.getOutsideHumidity() * 10)); // hi outside humidity
      writeShortValue(out, (short)(data.getOutsideHumidity() * 10)); // low outside humidity
      writeShortValue(out, (short)(data.getInsideHumidity() * 10)); // hi inside humidity
      writeShortValue(out, (short)(data.getInsideHumidity() * 10)); // low inside humidity
      writeShortValue(out, (short)(data.getOutsideHumidity() * 10)); // average outside humidity
      writeShortValue(out, data.getPressureNative()); // Hi pressure
      writeShortValue(out, data.getPressureNative()); // Low pressure
      writeShortValue(out, data.getPressureNative()); // Average pressure
      writeShortValue(out, data.getHighWindSpeedNative());
      writeShortValue(out, data.getAverageWindSpeedNative());
      writeShortValue(out, (short)0x0000); // Wind run total

      out.write(0x00); // hi10minspeed not implemented by Davis
      out.write(0x80);

      out.write(0x00); // Hi wind speed direction.
      out.write(0x00); // hi10mindir not implemented by Davis
      writeShortValue(out, (short)((data.getRainfall() * 100) / 1000));
      writeShortValue(out, data.getHighRainRateNative());
      writeShortValue(out, (short)0x8000); // No sensor
      out.write(0xFF); // No sensor

      // Add time value fields, set them all to 005 except 15 and 17 (High Average Wind Speed and High UV)
      for (int i = 0; i < 7; i++)
      {
        out.write(0x05);
        out.write(0x05);
        out.write(0x00);
      }
      for (int j = 0; j < 2; j++)
      {
        out.write(0x05);
        out.write(0xFF);
        out.write(0x70);
      }

      // Create summary 2 record bytes
      out.write(0x03); // the data type = Daily Summary Record 2
      out.write(0xFF); // reserved
      out.write(0x00); // today's weather not implemented by Davis
      out.write(0x00); // today's weather not implemented by Davis
      writeShortValue(out, (short)0x00); // number of wind samples
      writeShortValue(out, data.getHighSolarRadiation());

      writeShortValue(out, (short)0x00); // Daily solar energy not yet calculated
      writeShortValue(out, (short)0x00); // Min of sunlight assumed to be zero at midnight. Davis sets to 0x8000, unused.
      writeShortValue(out, (short)0x00); // Daily ET Total.

      writeShortValue(out, (short)0);  // Hi heat index
      writeShortValue(out, (short)999);  // Low heat index
      writeShortValue(out, (short)0);  // Avg heat index

      writeShortValue(out, (short)0); // Hi THSW
      writeShortValue(out, (short)999); // Low THSW
      writeShortValue(out, (short)0); // Hi THW
      writeShortValue(out, (short)999); // Low THW

      if (data.getOutsideTempNative() != (short)0x8000)
      {
        short heatDD = (short)((65.0 - data.getOutsideTemp()) / 288.0);
        writeShortValue(out, heatDD);
      }
      else
      {
        writeShortValue(out, (short)0);
      }

      // Wet bulb temps
      writeShortValue(out, (short)0);  // Hi wet bulb temp
      writeShortValue(out, (short)999);  // Low wet bulb temp
      writeShortValue(out, (short)0);  // Avg wet bulb temp

      // Direction bin values
      for (int i = 0; i < 24; i++)
      {
        out.write(0x00);
      }

      // Time values, note that the time of solar radiation has not been written.
      // The time in the dump record is at the start of the interval.
      // The time to write to the DB is at the end of the interval.
      short packedTime = TimeUtil.getPackedTime((short)(data.getTimeStamp() + 5));
      byte[] valueBytes2 = ByteUtil.intToByteArray(packedTime);
      for (int i = 0; i < 3; i++)
      {
        out.write(valueBytes2[0]);
        out.write(valueBytes2[0]);
        byte lowOrderByte = (byte) (valueBytes2[1] & 0x0F);
        byte highOrderByte = (byte) (valueBytes2[1] & 0xF0);
        out.write(lowOrderByte | highOrderByte);
      }
      out.write(valueBytes2[0]);
      out.write(0x00);
      out.write(0x00);

      for (int i = 0; i < 3; i++)
      {
        out.write(0x00);
      }

      // Cooling Degree Days, calculated
      if (data.getOutsideTempNative() != (short)0x8000)
      {
        short coolDD = (short)((data.getOutsideTemp() - 65.0) / 288.0);
        writeShortValue(out, coolDD);
      }
      else
      {
        writeShortValue(out, (short)0);
      }

      // reserved values
      for (int i = 0; i < 11; i++)
      {
        if ((i%2) == 0) // even
          out.write(0x00);
        else // odd
          out.write(0x80);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    // Clear out the wind bin values.
    WindBins.getInstance().clearDayBins();
  }

  /**
   * This method writes a data record which is derived from the DMP data.  The summary records are
   * filled in when the day is up. This method assumes the records are received in time order.
   *
   * @param data  The data to write.
   */
  public void insertWeatherRecord(DmpDataExtended data)
  {
    // The time in the dump record is at the start of the interval.
    // The time to write to the DB is at the end of the interval.
    int year = TimeUtil.getYear(data.getDateStamp());
    int month = TimeUtil.getMonth(data.getDateStamp());
    int day = TimeUtil.getDay(data.getDateStamp());
    int hour = TimeUtil.getHour(data.getTimeStamp());
    int minute = TimeUtil.getMinute(data.getTimeStamp());
    String filename = DatabaseCommon.getFilename(year, month);

    if (DB_READER.recordExists(year, month, day, hour, minute))
    {
      System.out.println("Record exists: Skipping: day = " + day + ", hour = " + hour + ", minute = " + minute);
      return;
    }

    // If this is the first record in the day, then insert 2 new summary records.
    if (hour == 0 && minute == 0)
    {
      // If this is the first record in the month, then create a new data file first.
      if (day == 1)
      {
        createNewDataFile(year, month);
      }
      insertNewSummaryRecords(filename, data);
    }

    try (RandomAccessFile updateFile = new RandomAccessFile(databaseLocation + filename, "rw"))
    {
      // Read the total number of records value (4 bytes, little endian).
      byte[] byteArray = new byte[4];
      updateFile.seek(DatabaseCommon.TOTAL_RECORDS_OFFSET);
      byteArray[3] = updateFile.readByte();
      updateFile.seek(DatabaseCommon.TOTAL_RECORDS_OFFSET + 1);
      byteArray[2] = updateFile.readByte();
      int totalRecords = ByteUtil.byteArrayToInt(byteArray);

      // Index into the header and retrieve the day index record.
      int dayRecordOffset = DatabaseCommon.DAY_INDEX_RECORD_OFFSET + day * 6;
      byte[] recordsByteArray = new byte[2];
      updateFile.seek(dayRecordOffset);
      recordsByteArray[1] = updateFile.readByte();
      updateFile.seek(dayRecordOffset + 1);
      recordsByteArray[0] = updateFile.readByte();
      short recordCount = ByteUtil.byteArrayToShort(recordsByteArray);

      // Increment the records in day and write the bytes back.  If this is the start of a new day then set
      // record count and write out the starting position as well.
      int lastStartPosition;
      if (hour == 0 && minute == 0)
      {
        // Add two to the total records and day's records to account for the summary records.
        totalRecords += 2;
        recordCount += 2;

        // Get the start position of the previous record and the record count.
        updateFile.seek(dayRecordOffset - 4);
        byteArray[3] = updateFile.readByte();
        updateFile.seek(dayRecordOffset - 3);
        byteArray[2] = updateFile.readByte();
        updateFile.seek(dayRecordOffset - 2);
        byteArray[1] = updateFile.readByte();
        updateFile.seek(dayRecordOffset - 1);
        byteArray[0] = updateFile.readByte();
        lastStartPosition = ByteUtil.byteArrayToInt(byteArray);

        updateFile.seek(dayRecordOffset - 6);
        recordsByteArray[1] = updateFile.readByte();
        updateFile.seek(dayRecordOffset - 5);
        recordsByteArray[0] = updateFile.readByte();
        short previousRecordCount = ByteUtil.byteArrayToShort(recordsByteArray);

        // Only if there is a previous set of day records do you write the start position.
        // TODO: this will not work for missing days worth of records.
        if (previousRecordCount != 0)
        {
          // Calculate the position in bytes by adding the start position of the last valid day record plus
          // the # of records.
          int newStartPosition = lastStartPosition + previousRecordCount;
          System.out.println("Last Starting Pos: " + lastStartPosition + ", Prev. Record Count: " + previousRecordCount + ", new Start Pos: " + newStartPosition);
          byte[] newPositionArray = ByteUtil.intToByteArray(newStartPosition);
          updateFile.seek(dayRecordOffset + 2);
          updateFile.write(newPositionArray[3]);
          updateFile.seek(dayRecordOffset + 3);
          updateFile.write(newPositionArray[2]);
          updateFile.seek(dayRecordOffset + 4);
          updateFile.write(newPositionArray[1]);
          updateFile.seek(dayRecordOffset + 5);
          updateFile.write(newPositionArray[0]);
        }
      }

      // Increment the total records in file.
      totalRecords++;
      byte[] newArray = ByteUtil.intToByteArray(totalRecords);
      updateFile.seek(DatabaseCommon.TOTAL_RECORDS_OFFSET);
      updateFile.write(newArray[3]);
      updateFile.seek(DatabaseCommon.TOTAL_RECORDS_OFFSET + 1);
      updateFile.write(newArray[2]);

      // Increment record count and write out.
      recordCount++;
      byte[] newRecordCount = ByteUtil.shortToByteArray(recordCount);
      updateFile.seek(dayRecordOffset);
      updateFile.write(newRecordCount[1]);
      updateFile.seek(dayRecordOffset + 1);
      updateFile.write(newRecordCount[0]);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    try (FileOutputStream out = new FileOutputStream(databaseLocation + filename, true))
    {
      // Write record.
      out.write(0x01); // Data type
      out.write(PROPS.getArchiveInterval()); // # of minutes in this archive record
      out.write(0x00); // icon flags: bits set if record has been edited or a note has been added
      out.write(0x00); // more flags: only used if more than one station
      short packedTime = (short)(TimeUtil.getPackedTime(data.getTimeStamp()) + 5);
      System.out.println("Writing DMP Record: Date: " + data.getDateStamp() +
                           ", Timestamp: " + data.getTimeStamp() +
                           ", PackedTime: " + packedTime);
      DB_COMMON.setLastTimeStamp(data.getTimeStamp());
      DB_COMMON.setLastDateStamp(TimeUtil.getDateStamp(day, month, year));
      writeShortValue(out, packedTime); // minutes past midnight of end of record.
      writeShortValue(out, data.getOutsideTempNative());
      writeShortValue(out, data.getHighOutsideTempNative());
      writeShortValue(out, data.getLowOutsideTempNative());
      writeShortValue(out, data.getInsideTempNative());
      writeShortValue(out, data.getPressureNative());
      writeShortValue(out, (short)(data.getOutsideHumidity() * 10));
      writeShortValue(out, (short)(data.getInsideHumidity() * 10));
      writeShortValue(out, data.getRainfallNative());
      writeShortValue(out, data.getHighRainRateNative());
      writeShortValue(out, data.getAverageWindSpeedNative());
      writeShortValue(out, data.getHighWindSpeedNative());
      out.write(data.getPrevailingWindDir());
      out.write(data.getHighWindDirection());
      writeShortValue(out, data.getNumOfWindSamples());
      writeShortValue(out, data.getSolarRadiation());
      writeShortValue(out, data.getHighSolarRadiation());
      out.write(data.getAverageUV());
      out.write(data.getHighUVIndex());
      out.write((byte)255); // leafTemp1
      out.write((byte)255); // leafTemp2
      out.write((byte)255); // leafTemp3
      out.write((byte)255); // leafTemp4
      out.write((byte)255); // extraRad - not currently calculated
      out.write((byte)255); // extraRad - not currently calculated
      for (int i = 0; i < 6; i++) // 6 future sensors
      {
        out.write((byte)0);
        out.write((byte)128);
      }
      out.write(data.getForecastRule());
      out.write(data.getEvapotranspiration());
      out.write(data.getSoilTemp1Native());
      out.write((byte)255); // soilTemp2
      out.write((byte)255); // soilTemp3
      out.write((byte)255); // soilTemp4
      out.write((byte)255); // soilTemp5
      out.write((byte)255); // soilTemp6
      out.write((byte)255); // soilMoisture1
      out.write((byte)255); // soilMoisture2
      out.write((byte)255); // soilMoisture3
      out.write((byte)255); // soilMoisture4
      out.write((byte)255); // soilMoisture5
      out.write((byte)255); // soilMoisture6
      out.write((byte)255); // leafWetness1
      out.write((byte)255); // leafWetness2
      out.write((byte)255); // LeafWetness3
      out.write((byte)255); // LeafWetness4
      out.write((byte)255); // extraTemp1
      out.write((byte)255); // extraTemp2
      out.write((byte)255); // extraTemp3
      out.write((byte)255); // ExtraTemp4
      out.write((byte)255); // ExtraTemp5
      out.write((byte)255); // ExtraTemp6
      out.write((byte)255); // ExtraTemp7
      out.write((byte)255); // extraHumid1
      out.write((byte)255); // extraHumid2
      out.write((byte)255); // ExtraHumid3
      out.write((byte)255); // ExtraHumid4
      out.write((byte)255); // ExtraHumid5
      out.write((byte)255); // ExtraHumid6
      out.write((byte)255); // ExtraHumid7
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * This method updates the summary records with the current weather record that was just added.
   *
   * @param data The DMP record representing the last archive interval of data.
   */
  public void updateSummaryRecords(DmpDataExtended data)
  {
    try (RandomAccessFile updateFile = new RandomAccessFile(databaseLocation + DatabaseCommon.getLatestFilename(),
                                                            "rw"))
    {
      int summaryRecordOffset = DB_COMMON.getSummaryRecordOffset(updateFile);
      int summaryRecordOffset2 = summaryRecordOffset + DatabaseCommon.RECORD_SIZE;

      // The time in the dump record is at the start of the interval.
      // The time to write to the DB is at the end of the interval.
      short packedTime = TimeUtil.getPackedTime((short)(data.getTimeStamp() + 5));

      // Update the dataSpan value
      int dataSpan = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.DATA_SPAN_OFFSET);
      dataSpan = dataSpan + PROPS.getArchiveInterval();
      updateSummaryRecord(updateFile, (short) dataSpan, summaryRecordOffset + DailySummary1Record.DATA_SPAN_OFFSET);

      // Update high outside temp.
      short hiOutTemp = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.HI_OUT_TEMP_OFFSET);
      short dayHighOutsideTemp = data.getHighOutsideTempNative();
      if (hiOutTemp < dayHighOutsideTemp)
      {
        updateSummaryRecord(updateFile, dayHighOutsideTemp,
                            summaryRecordOffset + DailySummary1Record.HI_OUT_TEMP_OFFSET);

        updateEvenTimeValue(packedTime, updateFile,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_OUT_TEMP_OFFSET_1,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_OUT_TEMP_OFFSET_2);
      }

      // Update low outside temp.
      short lowOutTemp = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.LOW_OUT_TEMP_OFFSET);
      short dayLowOutsideTemp = data.getLowOutsideTempNative();
      if (lowOutTemp > dayLowOutsideTemp)
      {
        updateSummaryRecord(updateFile, dayLowOutsideTemp,
                            summaryRecordOffset + DailySummary1Record.LOW_OUT_TEMP_OFFSET);

        updateOddTimeValue(packedTime, updateFile,
                           summaryRecordOffset + DailySummary1Record.TIME_LOW_OUT_TEMP_OFFSET_1,
                           summaryRecordOffset + DailySummary1Record.TIME_LOW_OUT_TEMP_OFFSET_2);
      }

      // Update high inside temp.
      short hiInTemp = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.HI_IN_TEMP_OFFSET);
      short dayInsideTemp = data.getInsideTempNative();
      if (hiInTemp < dayInsideTemp)
      {
        updateSummaryRecord(updateFile, dayInsideTemp,
                            summaryRecordOffset + DailySummary1Record.HI_IN_TEMP_OFFSET);

        updateEvenTimeValue(packedTime, updateFile,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_IN_TEMP_OFFSET_1,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_IN_TEMP_OFFSET_2);
      }

      // Update low inside temp.
      short lowInTemp = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.LOW_IN_TEMP_OFFSET);
      if (lowInTemp > dayInsideTemp)
      {
        updateSummaryRecord(updateFile, dayInsideTemp,
                            summaryRecordOffset + DailySummary1Record.LOW_IN_TEMP_OFFSET);

        updateOddTimeValue(packedTime, updateFile,
                           summaryRecordOffset + DailySummary1Record.TIME_LOW_IN_TEMP_OFFSET_1,
                           summaryRecordOffset + DailySummary1Record.TIME_LOW_IN_TEMP_OFFSET_2);
      }

      // Update average inside temperature.
      int roundedAvgInTemp = Math.round(data.getAvgInsideTemp() * 10);
      updateSummaryRecord(updateFile, (short) roundedAvgInTemp,
                          summaryRecordOffset + DailySummary1Record.AVG_IN_TEMP_OFFSET);

      // Update average outside temperature.
      int roundedAvgOutTemp = Math.round(data.getAvgOutsideTemp() * 10);
      updateSummaryRecord(updateFile, (short) roundedAvgOutTemp,
                          summaryRecordOffset + DailySummary1Record.AVG_OUT_TEMP_OFFSET);

      // Update high wind chill.
      if (data.getOutsideTempNative() != (short)0x8000)
      {
        float dayWindChill = Calculations.calculateWindChill(data.getOutsideTemp(), data.getAverageWindSpeed());
        float hiWindChill =
          readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.HI_CHILL_OFFSET) / 10.0f;
        if (hiWindChill < dayWindChill)
        {
          updateSummaryRecord(updateFile, (short)Math.round(dayWindChill * 10),
                              summaryRecordOffset + DailySummary1Record.HI_CHILL_OFFSET);

          updateEvenTimeValue(packedTime, updateFile,
                              summaryRecordOffset + DailySummary1Record.TIME_HIGH_CHILL_OFFSET_1,
                              summaryRecordOffset + DailySummary1Record.TIME_HIGH_CHILL_OFFSET_2);
        }

        // Update low wind chill.
        float lowWindChill =
          readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.LOW_CHILL_OFFSET) / 10.0f;
        if (lowWindChill > dayWindChill)
        {
          updateSummaryRecord(updateFile, (short)Math.round(dayWindChill * 10),
                              summaryRecordOffset + DailySummary1Record.LOW_CHILL_OFFSET);

          updateOddTimeValue(packedTime, updateFile,
                             summaryRecordOffset + DailySummary1Record.TIME_LOW_CHILL_OFFSET_1,
                             summaryRecordOffset + DailySummary1Record.TIME_LOW_CHILL_OFFSET_2);
        }

        // Update high dew point.
        float dayDewPoint = Calculations.calculateDewPoint(data.getOutsideTemp(), data.getOutsideHumidity());
        float hiDewPoint = readTwoByteValues(updateFile,
                                             summaryRecordOffset + DailySummary1Record.HI_DEW_OFFSET) / 10.0f;
        if (hiDewPoint < dayDewPoint)
        {
          updateSummaryRecord(updateFile, (short)Math.round(dayDewPoint * 10),
                              summaryRecordOffset + DailySummary1Record.HI_DEW_OFFSET);

          updateEvenTimeValue(packedTime, updateFile,
                              summaryRecordOffset + DailySummary1Record.TIME_HIGH_DEW_OFFSET_1,
                              summaryRecordOffset + DailySummary1Record.TIME_HIGH_DEW_OFFSET_2);
        }

        // Update low dew point.
        float lowDewPoint =
          readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.LOW_DEW_OFFSET) / 10.0f;
        if (lowDewPoint > dayDewPoint)
        {
          updateSummaryRecord(updateFile, (short)Math.round(dayDewPoint * 10),
                              summaryRecordOffset + DailySummary1Record.LOW_DEW_OFFSET);

          updateOddTimeValue(packedTime, updateFile,
                             summaryRecordOffset + DailySummary1Record.TIME_LOW_DEW_OFFSET_1,
                             summaryRecordOffset + DailySummary1Record.TIME_LOW_DEW_OFFSET_2);
        }
      }

      // Update average wind chill.
      int roundedAvgChill = Math.round(data.getAvgWindChill() * 10);
      updateSummaryRecord(updateFile, (short) roundedAvgChill,
                          summaryRecordOffset + DailySummary1Record.AVG_CHILL_OFFSET);

      // Update average dew point.
      int roundedAvgDewPoint = Math.round(data.getAvgDewPoint() * 10);
      updateSummaryRecord(updateFile, (short) roundedAvgDewPoint,
                          summaryRecordOffset + DailySummary1Record.AVG_DEW_OFFSET);

      // Update high outside humidity.
      short hiOutHumid = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.HI_OUT_HUMID_OFFSET);
      short dayOutsideHumid = data.getOutsideHumidity();
      if (hiOutHumid < dayOutsideHumid)
      {
        updateSummaryRecord(updateFile, dayOutsideHumid,
                            summaryRecordOffset + DailySummary1Record.HI_OUT_HUMID_OFFSET);

        updateEvenTimeValue(packedTime, updateFile,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_OUT_HUM_OFFSET_1,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_OUT_HUM_OFFSET_2);
      }

      // Update low outside humidity.
      short lowOutHumid = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.LOW_OUT_HUMID_OFFSET);
      if (lowOutHumid > dayOutsideHumid)
      {
        updateSummaryRecord(updateFile, dayOutsideHumid,
                            summaryRecordOffset + DailySummary1Record.LOW_OUT_HUMID_OFFSET);

        updateOddTimeValue(packedTime, updateFile,
                           summaryRecordOffset + DailySummary1Record.TIME_LOW_OUT_HUM_OFFSET_1,
                           summaryRecordOffset + DailySummary1Record.TIME_LOW_OUT_HUM_OFFSET_2);
      }

      // Update high inside humidity.
      short hiInHumid = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.HI_IN_HUMID_OFFSET);
      short dayInsideHumid = data.getInsideHumidity();
      if (hiInHumid < dayInsideHumid)
      {
        updateSummaryRecord(updateFile, dayInsideHumid,
                            summaryRecordOffset + DailySummary1Record.HI_IN_HUMID_OFFSET);

        updateEvenTimeValue(packedTime, updateFile,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_IN_HUM_OFFSET_1,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_IN_HUM_OFFSET_2);
      }

      // Update low inside humidity.
      short lowInHumid = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.LOW_IN_HUMID_OFFSET);
      if (lowInHumid > dayInsideHumid)
      {
        updateSummaryRecord(updateFile, dayInsideHumid,
                            summaryRecordOffset + DailySummary1Record.LOW_IN_HUMID_OFFSET);

        updateOddTimeValue(packedTime, updateFile,
                           summaryRecordOffset + DailySummary1Record.TIME_LOW_IN_HUM_OFFSET_1,
                           summaryRecordOffset + DailySummary1Record.TIME_LOW_IN_HUM_OFFSET_2);
      }

      // Update avg outside humidity.
      int roundedAvgOutHumid = Math.round(data.getAvgOutsideHumidity() * 10);
      updateSummaryRecord(updateFile, (short) roundedAvgOutHumid,
                          summaryRecordOffset + DailySummary1Record.AVG_OUT_HUMID_OFFSET);

      // Update high pressure.
      short hiPressure = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.HI_BAR_OFFSET);
      short dayPressure = data.getPressureNative();
      if (hiPressure < dayPressure)
      {
        updateSummaryRecord(updateFile, dayPressure, summaryRecordOffset + DailySummary1Record.HI_BAR_OFFSET);

        updateEvenTimeValue(packedTime, updateFile,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_BAR_OFFSET_1,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_BAR_OFFSET_2);
      }

      // Update low pressure.
      short lowPressure = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.LOW_BAR_OFFSET);
      if (lowPressure > dayPressure)
      {
        updateSummaryRecord(updateFile, dayPressure, summaryRecordOffset + DailySummary1Record.LOW_BAR_OFFSET);

        updateOddTimeValue(packedTime, updateFile,
                           summaryRecordOffset + DailySummary1Record.TIME_LOW_BAR_OFFSET_1,
                           summaryRecordOffset + DailySummary1Record.TIME_LOW_BAR_OFFSET_2);
      }

      // Update avg pressure.
      int roundedAvgBar = Math.round(data.getAvgPressure() * 1000);
      updateSummaryRecord(updateFile, (short) roundedAvgBar, summaryRecordOffset + DailySummary1Record.AVG_BAR_OFFSET);

      // Update high wind speed and direction of high wind speed.
      short hiWindSpeed = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.HI_WIND_SPEED_OFFSET);
      short dayHiWindSpeed = data.getHighWindSpeedNative();
      if (dayHiWindSpeed > hiWindSpeed)
      {
        updateSummaryRecord(updateFile, dayHiWindSpeed, summaryRecordOffset + DailySummary1Record.HI_WIND_SPEED_OFFSET);

        updateEvenTimeValue(packedTime, updateFile,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_WIND_OFFSET_1,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_WIND_OFFSET_2);

        updateSummaryRecord(updateFile, data.getHighWindDirection(),
                            summaryRecordOffset + DailySummary1Record.DIR_HI_WIND_SPEED_OFFSET);
      }

      // Update avg wind speed.
      int roundedAvgWindSpeed = Math.round(data.getAvgWindSpeed() * 10);
      updateSummaryRecord(updateFile, (short) roundedAvgWindSpeed,
                          summaryRecordOffset + DailySummary1Record.AVG_WIND_SPEED_OFFSET);

      // Update daily wind run total, in tenths of miles per hour.
      int newWindRunTotal = Math.round(data.getTotalWindRun() * 10);
      updateSummaryRecord(updateFile, (short) newWindRunTotal,
                          summaryRecordOffset + DailySummary1Record.DAILY_WIND_RUN_TOTAL_OFFSET);

      // NOTE: High 10 minute wind speed and direction of high 10 minute wind speed are not implemented in WeatherLink.

      // Update daily rain total. Rainfall from DMP is in hundredths whereas rainfall in summary is thousands.
      short rainTotal =
        readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.DAILY_RAIN_TOTAL_OFFSET);
      float rainAmount = data.getRainfall();
      short newRainTotal = (short)(rainTotal + rainAmount * 1000);
      updateSummaryRecord(updateFile, newRainTotal, summaryRecordOffset + DailySummary1Record.DAILY_RAIN_TOTAL_OFFSET);

      // Update high rain rate.
      short hiRainRate = readTwoByteValues(updateFile, summaryRecordOffset + DailySummary1Record.HI_RAIN_RATE_OFFSET);
      short dayHiRainRate = data.getHighRainRateNative();
      if (hiRainRate < dayHiRainRate)
      {
        updateSummaryRecord(updateFile, dayHiRainRate, summaryRecordOffset + DailySummary1Record.HI_RAIN_RATE_OFFSET);

        updateEvenTimeValue(packedTime, updateFile,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_RAIN_OFFSET_1,
                            summaryRecordOffset + DailySummary1Record.TIME_HIGH_RAIN_OFFSET_2);
      }

      // ****** Second summary record updates starts here ******

      // Update number of wind packets.
      short totalPackets = readTwoByteValues(updateFile,
                                             summaryRecordOffset2 + DailySummary2Record.NUM_OF_WIND_PACKETS_OFFSET);
      short newPackets = data.getNumOfWindSamples();
      short newTotalPackets = (short) (totalPackets + newPackets);
      updateSummaryRecord(updateFile, newTotalPackets,
                          summaryRecordOffset2 + DailySummary2Record.NUM_OF_WIND_PACKETS_OFFSET);

      // Update high solar.
      short hiSolar = readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.HI_SOLAR_OFFSET);
      short daySolar = data.getHighSolarRadiation();
      if (hiSolar < daySolar)
      {
        updateSummaryRecord(updateFile, daySolar, summaryRecordOffset2 + DailySummary2Record.HI_SOLAR_OFFSET);

        updateEvenTimeValue(packedTime, updateFile,
                            summaryRecordOffset2 + DailySummary2Record.TIME_HIGH_SOLAR_OFFSET_1,
                            summaryRecordOffset2 + DailySummary2Record.TIME_HIGH_SOLAR_OFFSET_2);
      }

      // TODO: Update daily solar energy.  NEED A REASONABLE VALUE TO INSERT HERE....
//      short totalSolarEnergy =
//        readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.DAILY_SOLAR_ENERGY_OFFSET);
//
//      totalSolarEnergy += data.getSolarRadiation() * 5;
//
//      updateSummaryRecord(updateFile, totalSolarEnergy,
//                          summaryRecordOffset2 + DailySummary2Record.DAILY_SOLAR_ENERGY_OFFSET);

      // Update minutes of sunlight.
      short minOfSunlight =
        readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.MIN_SUNLIGHT_OFFSET);

      if (data.getSolarRadiation() > 0)
        minOfSunlight += 5;

      updateSummaryRecord(updateFile, minOfSunlight, summaryRecordOffset2 + DailySummary2Record.MIN_SUNLIGHT_OFFSET);

      // Update daily ET total.
      short dailyEtTotal =
        readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.DAILY_ET_TOTAL_OFFSET);

      Integer value = dailyEtTotal + Math.round(data.getEt() * 1000);
      dailyEtTotal = value.shortValue();

      updateSummaryRecord(updateFile, dailyEtTotal, summaryRecordOffset2 + DailySummary2Record.DAILY_ET_TOTAL_OFFSET);

      // Update high heat index.
      if (data.getOutsideTempNative() != (short)0x8000)
      {
        float dayHeatIndex = Calculations.calculateHeatIndex(data.getOutsideTemp(), data.getOutsideHumidity());
        float hiHeatIndex =
          readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.HI_HEAT_OFFSET) / 10.0f;
        if (hiHeatIndex < dayHeatIndex)
        {
          updateSummaryRecord(updateFile, (short)Math.round(dayHeatIndex * 10),
                              summaryRecordOffset2 + DailySummary2Record.HI_HEAT_OFFSET);

          updateOddTimeValue(packedTime, updateFile,
                             summaryRecordOffset2 + DailySummary2Record.TIME_HIGH_HEAT_OFFSET_1,
                             summaryRecordOffset2 + DailySummary2Record.TIME_HIGH_HEAT_OFFSET_2);
        }

        // Update low heat index.
        float lowHeatIndex =
          readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.LOW_HEAT_OFFSET) / 10.0f;
        if (lowHeatIndex > dayHeatIndex)
        {
          updateSummaryRecord(updateFile, (short)Math.round(dayHeatIndex * 10),
                              summaryRecordOffset2 + DailySummary2Record.LOW_HEAT_OFFSET);

          updateEvenTimeValue(packedTime, updateFile,
                              summaryRecordOffset2 + DailySummary2Record.TIME_LOW_HEAT_OFFSET_1,
                              summaryRecordOffset2 + DailySummary2Record.TIME_LOW_HEAT_OFFSET_2);
        }

        // Update average heat index.
        updateSummaryRecord(updateFile, (short)Math.round(data.getAvgHeatIndex() * 10),
                            summaryRecordOffset2 + DailySummary2Record.AVG_HEAT_OFFSET);

        // Update high THSW. // TODO: not working....
        float highTHSW = readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.HI_THSW_OFFSET);
        float dayTHSW = Calculations.calculateTHSW(data.getOutsideTemp(), data.getAverageWindSpeed(),
                                                   data.getOutsideHumidity(), data.getSolarRadiation());
        if (dayTHSW > highTHSW)
        {
          updateSummaryRecord(updateFile, (short)Math.round(dayTHSW * 10),
                              summaryRecordOffset2 + DailySummary2Record.HI_THSW_OFFSET);

          updateOddTimeValue(packedTime, updateFile,
                             summaryRecordOffset2 + DailySummary2Record.TIME_HIGH_THSW_OFFSET_1,
                             summaryRecordOffset2 + DailySummary2Record.TIME_HIGH_THSW_OFFSET_2);
        }

        // Update low THSW.
        float lowTHSW = readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.LOW_THSW_OFFSET);
        if (dayTHSW < lowTHSW)
        {
          updateSummaryRecord(updateFile, (short)Math.round(dayTHSW * 10),
                              summaryRecordOffset2 + DailySummary2Record.LOW_THSW_OFFSET);

          updateEvenTimeValue(packedTime, updateFile,
                              summaryRecordOffset2 + DailySummary2Record.TIME_LOW_THSW_OFFSET_1,
                              summaryRecordOffset2 + DailySummary2Record.TIME_LOW_THSW_OFFSET_2);
        }

        // Update high THW.
        float highTHW = readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.HI_THW_OFFSET);
        float dayTHW = Calculations.calculateTHW(data.getOutsideTemp(), data.getAverageWindSpeed(),
                                                 data.getOutsideHumidity());
        if (dayTHW > highTHW)
        {
          updateSummaryRecord(updateFile, (short)Math.round(dayTHW * 10),
                              summaryRecordOffset2 + DailySummary2Record.HI_THW_OFFSET);

          updateOddTimeValue(packedTime, updateFile,
                             summaryRecordOffset2 + DailySummary2Record.TIME_HIGH_THW_OFFSET_1,
                             summaryRecordOffset2 + DailySummary2Record.TIME_HIGH_THW_OFFSET_2);
        }

        // Update low THW.
        float lowTHW = readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.LOW_THW_OFFSET);
        if (dayTHW < lowTHW)
        {
          updateSummaryRecord(updateFile, (short)Math.round(dayTHW * 10),
                              summaryRecordOffset2 + DailySummary2Record.LOW_THW_OFFSET);

          updateEvenTimeValue(packedTime, updateFile,
                              summaryRecordOffset2 + DailySummary2Record.TIME_LOW_THW_OFFSET_1,
                              summaryRecordOffset2 + DailySummary2Record.TIME_LOW_THW_OFFSET_2);
        }

        // Update heating degree days (65 degrees). If the average 5 minute outside temperature is below 65 degrees,
        // then 65 minus the average temp reading divided by the percentage of the day or 24 * 60 / 5 = 288.
        // These 5 minute values are added for each 5 minute period in the day.
        float heatDD =
          readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.HEAT_DD_OFFSET) / 10.0f;
        float outsideTemp = data.getOutsideTemp();
        if (outsideTemp < 65.0)
        {
          updateSummaryRecord(updateFile, (short)Math.round((heatDD + (65.0 - data.getOutsideTemp()) / 288.0) * 10.0),
                              summaryRecordOffset2 + DailySummary2Record.HEAT_DD_OFFSET);
        }

        // Update cooling degree days (65 degrees). This is the opposite of the heating degree days or the summation of
        // the 5 minute intervals where the outside temperature is above 65 degrees.
        float coolDD =
          readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.COOL_DD_OFFSET) / 10.0f;
        if (outsideTemp > 65.0)
        {
          updateSummaryRecord(updateFile, (short)Math.round((coolDD + (data.getOutsideTemp() - 65.0) / 288.0) * 10.0),
                              summaryRecordOffset2 + DailySummary2Record.COOL_DD_OFFSET);
        }
      }

      // Update high wet bulb temp.
      if (data.getOutsideTempNative() != (short)0x8000)
      {
        float dayWetBulbTemp = Calculations.calculateWetBulbTemperature(data.getOutsideTemp(), data.getOutsideHumidity());
        float hiWetBulbTemp =
          readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.HI_WET_BULB_OFFSET) / 10.0f;
        if (hiWetBulbTemp < dayWetBulbTemp)
        {
          updateSummaryRecord(updateFile, (short) Math.round(dayWetBulbTemp * 10),
                              summaryRecordOffset2 + DailySummary2Record.HI_WET_BULB_OFFSET);

          updateOddTimeValue(packedTime, updateFile,
                             summaryRecordOffset2 + DailySummary2Record.TIME_HIGH_WET_BULB_OFFSET_1,
                             summaryRecordOffset2 + DailySummary2Record.TIME_HIGH_WET_BULB_OFFSET_2);
        }

        // Update low wet bulb temp.
        float lowWetBulbIndex =
          readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.LOW_WET_BULB_OFFSET) / 10.0f;
        if (lowWetBulbIndex > dayWetBulbTemp)
        {
          updateSummaryRecord(updateFile, (short) Math.round(dayWetBulbTemp * 10),
                              summaryRecordOffset2 + DailySummary2Record.LOW_WET_BULB_OFFSET);

          updateEvenTimeValue(packedTime, updateFile,
                              summaryRecordOffset2 + DailySummary2Record.TIME_LOW_WET_BULB_OFFSET_1,
                              summaryRecordOffset2 + DailySummary2Record.TIME_LOW_WET_BULB_OFFSET_2);
        }

        // Update average wet bulb temp.
        updateSummaryRecord(updateFile, (short) Math.round(data.getAvgWetBulbTemp() * 10),
                            summaryRecordOffset2 + DailySummary2Record.AVG_WET_BULB_OFFSET);
      }

      // Update Wind Direction.
      WindDirection windDirection = WindDirection.valueOf(data.getPrevailingWindDir());
      if (windDirection != null)
      {
        switch (windDirection)
        {
          case N:
            updateEvenWindValue(updateFile,
                                summaryRecordOffset2 + DailySummary2Record.N_OFFSET_1,
                                summaryRecordOffset2 + DailySummary2Record.N_OFFSET_2);
            break;
          case NNE:
            updateOddWindValue(updateFile,
                               summaryRecordOffset2 + DailySummary2Record.NNE_OFFSET_1,
                               summaryRecordOffset2 + DailySummary2Record.NNE_OFFSET_2);
            break;
          case NE:
            updateEvenWindValue(updateFile,
                                summaryRecordOffset2 + DailySummary2Record.NE_OFFSET_1,
                                summaryRecordOffset2 + DailySummary2Record.NE_OFFSET_2);
            break;
          case ENE:
            updateOddWindValue(updateFile,
                               summaryRecordOffset2 + DailySummary2Record.ENE_OFFSET_1,
                               summaryRecordOffset2 + DailySummary2Record.ENE_OFFSET_2);
            break;
          case E:
            updateEvenWindValue(updateFile,
                                summaryRecordOffset2 + DailySummary2Record.E_OFFSET_1,
                                summaryRecordOffset2 + DailySummary2Record.E_OFFSET_2);
            break;
          case ESE:
            updateOddWindValue(updateFile,
                               summaryRecordOffset2 + DailySummary2Record.ESE_OFFSET_1,
                               summaryRecordOffset2 + DailySummary2Record.ESE_OFFSET_2);
            break;
          case SE:
            updateEvenWindValue(updateFile,
                                summaryRecordOffset2 + DailySummary2Record.SE_OFFSET_1,
                                summaryRecordOffset2 + DailySummary2Record.SE_OFFSET_2);
            break;
          case SSE:
            updateOddWindValue(updateFile,
                               summaryRecordOffset2 + DailySummary2Record.SSE_OFFSET_1,
                               summaryRecordOffset2 + DailySummary2Record.SSE_OFFSET_2);
            break;
          case S:
            updateEvenWindValue(updateFile,
                                summaryRecordOffset2 + DailySummary2Record.S_OFFSET_1,
                                summaryRecordOffset2 + DailySummary2Record.S_OFFSET_2);
            break;
          case SSW:
            updateOddWindValue(updateFile,
                               summaryRecordOffset2 + DailySummary2Record.SSW_OFFSET_1,
                               summaryRecordOffset2 + DailySummary2Record.SSW_OFFSET_2);
            break;
          case SW:
            updateEvenWindValue(updateFile,
                                summaryRecordOffset2 + DailySummary2Record.SW_OFFSET_1,
                                summaryRecordOffset2 + DailySummary2Record.SW_OFFSET_2);
            break;
          case WSW:
            updateOddWindValue(updateFile,
                               summaryRecordOffset2 + DailySummary2Record.WSW_OFFSET_1,
                               summaryRecordOffset2 + DailySummary2Record.WSW_OFFSET_2);
            break;
          case W:
            updateEvenWindValue(updateFile,
                                summaryRecordOffset2 + DailySummary2Record.W_OFFSET_1,
                                summaryRecordOffset2 + DailySummary2Record.W_OFFSET_2);
            break;
          case WNW:
            updateOddWindValue(updateFile,
                               summaryRecordOffset2 + DailySummary2Record.WNW_OFFSET_1,
                               summaryRecordOffset2 + DailySummary2Record.WNW_OFFSET_2);
            break;
          case NW:
            updateEvenWindValue(updateFile,
                                summaryRecordOffset2 + DailySummary2Record.NW_OFFSET_1,
                                summaryRecordOffset2 + DailySummary2Record.NW_OFFSET_2);
            break;
          case NNW:
            updateOddWindValue(updateFile,
                               summaryRecordOffset2 + DailySummary2Record.NNW_OFFSET_1,
                               summaryRecordOffset2 + DailySummary2Record.NNW_OFFSET_2);
            break;
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * This method updates an existing weather record due to an edit action.
   *
   * @param record The weather record to update.
   */
  public void updateWeatherRecord(WeatherRecord record)
  {
    LocalDateTime timestamp = record.getTimestamp();
    int year = timestamp.getYear();
    int month = timestamp.getMonth().getValue();
    String filename = DatabaseCommon.getFilename(year, month);

    try (RandomAccessFile updateFile = new RandomAccessFile(databaseLocation + filename, "rw"))
    {
      int day = record.getTimestamp().getDayOfMonth();
      byte archiveInterval = record.getArchiveInterval();

      // Index into the header and retrieve the day index record.
      int dayRecordOffset = DatabaseCommon.DAY_INDEX_RECORD_OFFSET + 6 + (day * 6);
      byte[] byteArray = new byte[4];
      updateFile.seek(dayRecordOffset - 4);
      byteArray[3] = updateFile.readByte();
      updateFile.seek(dayRecordOffset - 3);
      byteArray[2] = updateFile.readByte();
      updateFile.seek(dayRecordOffset - 2);
      byteArray[1] = updateFile.readByte();
      updateFile.seek(dayRecordOffset - 1);
      byteArray[0] = updateFile.readByte();
      int startPos = ByteUtil.byteArrayToInt(byteArray);

      // This assumes the archive interval for this record is good for all day.
      short packedTime = TimeUtil.getPackedTime(timestamp.getHour(), timestamp.getMinute());
      int offset = DatabaseCommon.HEADER_BLOCK_SIZE + startPos * DatabaseCommon.RECORD_SIZE +
        ((packedTime / archiveInterval) + 2) * DatabaseCommon.RECORD_SIZE;

      byte iconFlags = 0x10;
      updateFile.seek(offset + 2);
      updateFile.write(iconFlags);

      writeShortValue(updateFile, offset + WeatherRecord.OUTSIDE_TEMP_OFFSET, record.getOutsideTempNative());
      writeShortValue(updateFile, offset + WeatherRecord.HI_OUTSIDE_TEMP_OFFSET, record.getHighOutsideTempNative());
      writeShortValue(updateFile, offset + WeatherRecord.LOW_OUTSIDE_TEMP_OFFSET, record.getLowOutsideTempNative());
      writeShortValue(updateFile, offset + WeatherRecord.INSIDE_TEMP_OFFSET, record.getInsideTempNative());
      writeShortValue(updateFile, offset + WeatherRecord.BAROMETER_OFFSET, record.getPressureNative());
      writeShortValue(updateFile, offset + WeatherRecord.OUTSIDE_HUMID_OFFSET, record.getOutsideHumidityNative());
      writeShortValue(updateFile, offset + WeatherRecord.INSIDE_HUMID_OFFSET, record.getInsideHumidityNative());
      writeShortValue(updateFile, offset + WeatherRecord.RAIN_OFFSET, record.getRainfallNative());
      writeShortValue(updateFile, offset + WeatherRecord.HI_RAIN_RATE_OFFSET, record.getHighRainRateNative());
      writeShortValue(updateFile, offset + WeatherRecord.WIND_SPEED_OFFSET, record.getAverageWindSpeedNative());
      writeShortValue(updateFile, offset + WeatherRecord.HI_WIND_SPEED_OFFSET, record.getHighWindSpeedNative());
      writeShortValue(updateFile, offset + WeatherRecord.WIND_DIR_OFFSET, record.getWindDirectionNative());
      writeShortValue(updateFile, offset + WeatherRecord.HI_WIND_DIR_OFFSET, record.getHighWIndDirectionNative());
      writeShortValue(updateFile, offset + WeatherRecord.NUM_WIND_SAMPLES_OFFSET, record.getNumOfWindSamples());
      writeShortValue(updateFile, offset + WeatherRecord.SOLAR_RAD_OFFSET, record.getSolarRadiation());
      writeShortValue(updateFile, offset + WeatherRecord.HI_SOLAR_OFFSET, record.getHighSolarRadiation());
      writeShortValue(updateFile, offset + WeatherRecord.FORECAST_OFFSET, record.getForecast());

      byte soilTemp1Byte = record.getSoilTemp1Native();
      updateFile.seek(offset + WeatherRecord.SOIL_TEMP_1_OFFSET);
      updateFile.write(soilTemp1Byte);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * This method updates an existing Daily Summary #1 record due to an edit action.
   *
   * @param record The daily summary #1 record to update.
   */
  public void updateSummary1Record(DailySummary1Record record, int year, int month, int day)
  {
    String filename = DatabaseCommon.getFilename(year, month);

    try (RandomAccessFile updateFile = new RandomAccessFile(databaseLocation + filename, "rw"))
    {
      // Index into the header and retrieve the day index record.
      int dayRecordOffset = DatabaseCommon.DAY_INDEX_RECORD_OFFSET + 6 + (day * 6);
      byte[] byteArray = new byte[4];
      updateFile.seek(dayRecordOffset - 4);
      byteArray[3] = updateFile.readByte();
      updateFile.seek(dayRecordOffset - 3);
      byteArray[2] = updateFile.readByte();
      updateFile.seek(dayRecordOffset - 2);
      byteArray[1] = updateFile.readByte();
      updateFile.seek(dayRecordOffset - 1);
      byteArray[0] = updateFile.readByte();
      int startPos = ByteUtil.byteArrayToInt(byteArray);

      // The daily summary #2 record is always the second record of the day.
      int offset = DatabaseCommon.HEADER_BLOCK_SIZE + startPos * DatabaseCommon.RECORD_SIZE + DatabaseCommon.RECORD_SIZE;

      // Skip over the dataType and todaysWeather fields.
      updateFile.seek(offset + 4);

      writeShortValue(updateFile, offset + DailySummary1Record.HI_OUT_TEMP_OFFSET, record.getHiOutTempNative());
      writeShortValue(updateFile, offset + DailySummary1Record.LOW_OUT_TEMP_OFFSET, record.getLowOutTempNative());
      writeShortValue(updateFile, offset + DailySummary1Record.HI_IN_TEMP_OFFSET, record.getHiInTempNative());
      writeShortValue(updateFile, offset + DailySummary1Record.LOW_IN_TEMP_OFFSET, record.getLowInTempNative());
      writeShortValue(updateFile, offset + DailySummary1Record.AVG_OUT_TEMP_OFFSET, record.getAvgOutTempNative());
      writeShortValue(updateFile, offset + DailySummary1Record.AVG_IN_TEMP_OFFSET, record.getAvgInTempNative());
      writeShortValue(updateFile, offset + DailySummary1Record.HI_CHILL_OFFSET, record.getHiChillNative());
      writeShortValue(updateFile, offset + DailySummary1Record.LOW_CHILL_OFFSET, record.getLowChillNative());
      writeShortValue(updateFile, offset + DailySummary1Record.HI_DEW_OFFSET, record.getHiDewNative());
      writeShortValue(updateFile, offset + DailySummary1Record.LOW_DEW_OFFSET, record.getLowDewNative());
      writeShortValue(updateFile, offset + DailySummary1Record.AVG_CHILL_OFFSET, record.getAvgChillNative());
      writeShortValue(updateFile, offset + DailySummary1Record.AVG_DEW_OFFSET, record.getAvgDewNative());
      writeShortValue(updateFile, offset + DailySummary1Record.HI_OUT_HUMID_OFFSET, record.getHiOutHumidNative());
      writeShortValue(updateFile, offset + DailySummary1Record.LOW_OUT_HUMID_OFFSET, record.getLowOutHumidNative());
      writeShortValue(updateFile, offset + DailySummary1Record.HI_IN_HUMID_OFFSET, record.getHiInHumidNative());
      writeShortValue(updateFile, offset + DailySummary1Record.LOW_IN_HUMID_OFFSET, record.getLowInHumidNative());
      writeShortValue(updateFile, offset + DailySummary1Record.AVG_OUT_HUMID_OFFSET, record.getAvgOutTempNative());
      writeShortValue(updateFile, offset + DailySummary1Record.HI_BAR_OFFSET, record.getHiBarNative());
      writeShortValue(updateFile, offset + DailySummary1Record.LOW_BAR_OFFSET, record.getLowBarNative());
      writeShortValue(updateFile, offset + DailySummary1Record.AVG_BAR_OFFSET, record.getAvgBarNative());
      writeShortValue(updateFile, offset + DailySummary1Record.HI_WIND_SPEED_OFFSET, record.getHiSpeedNative());
      writeShortValue(updateFile, offset + DailySummary1Record.AVG_WIND_SPEED_OFFSET, record.getAvgSpeedNative());
      writeShortValue(updateFile, offset + DailySummary1Record.DAILY_WIND_RUN_TOTAL_OFFSET, record.getDailyWindRunTotalNative());
      writeShortValue(updateFile, offset + DailySummary1Record.HI_10_MIN_SPEED_OFFSET, record.getHiTenMinSpeedNative());
      writeShortValue(updateFile, offset + DailySummary1Record.DIR_HI_WIND_SPEED_OFFSET, record.getDirHiSpeedNative());
      writeShortValue(updateFile, offset + DailySummary1Record.DIR_HI_10_MIN_SPEED_OFFSET, record.getDirHiTenMinNative());
      writeShortValue(updateFile, offset + DailySummary1Record.DAILY_RAIN_TOTAL_OFFSET, record.getDailyRainTotalNative());
      writeShortValue(updateFile, offset + DailySummary1Record.HI_RAIN_RATE_OFFSET, record.getHiRainRateNative());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * This method updates an existing Daily Summary #2 record due to an edit action.
   *
   * @param record The daily summary #2 record to update.
   */
  public void updateSummary2Record(DailySummary2Record record, int year, int month, int day)
  {
    String filename = DatabaseCommon.getFilename(year, month);

    try (RandomAccessFile updateFile = new RandomAccessFile(databaseLocation + filename, "rw"))
    {
      // Index into the header and retrieve the day index record.
      int dayRecordOffset = DatabaseCommon.DAY_INDEX_RECORD_OFFSET + 6 + (day * 6);
      byte[] byteArray = new byte[4];
      updateFile.seek(dayRecordOffset - 4);
      byteArray[3] = updateFile.readByte();
      updateFile.seek(dayRecordOffset - 3);
      byteArray[2] = updateFile.readByte();
      updateFile.seek(dayRecordOffset - 2);
      byteArray[1] = updateFile.readByte();
      updateFile.seek(dayRecordOffset - 1);
      byteArray[0] = updateFile.readByte();
      int startPos = ByteUtil.byteArrayToInt(byteArray);

      // The daily summary #2 record is always the second record of the day.
      int offset = DatabaseCommon.HEADER_BLOCK_SIZE + startPos * DatabaseCommon.RECORD_SIZE + DatabaseCommon.RECORD_SIZE;

      // Skip over the dataType and todaysWeather fields.
      updateFile.seek(offset + 4);

      writeShortValue(updateFile, offset + DailySummary2Record.NUM_OF_WIND_PACKETS_OFFSET, (short)record.getNumOfWindPackets());
      writeShortValue(updateFile, offset + DailySummary2Record.HI_SOLAR_OFFSET, (short)record.getHiSolar());
      writeShortValue(updateFile, offset + DailySummary2Record.DAILY_SOLAR_ENERGY_OFFSET, record.getDailySolarEnergyNative());
      writeShortValue(updateFile, offset + DailySummary2Record.MIN_SUNLIGHT_OFFSET, record.getMinSunlight());
      writeShortValue(updateFile, offset + DailySummary2Record.DAILY_ET_TOTAL_OFFSET, record.getDailyETTotalNative());
      writeShortValue(updateFile, offset + DailySummary2Record.HI_HEAT_OFFSET, record.getHiHeatNative());
      writeShortValue(updateFile, offset + DailySummary2Record.LOW_HEAT_OFFSET, record.getLowHeatNative());
      writeShortValue(updateFile, offset + DailySummary2Record.AVG_HEAT_OFFSET, record.getAvgHeatNative());
      writeShortValue(updateFile, offset + DailySummary2Record.HI_THSW_OFFSET, record.getHiTHSWNative());
      writeShortValue(updateFile, offset + DailySummary2Record.LOW_THSW_OFFSET, record.getLowTHSWNative());
      writeShortValue(updateFile, offset + DailySummary2Record.HI_THW_OFFSET, record.getHiTHWNative());
      writeShortValue(updateFile, offset + DailySummary2Record.LOW_THW_OFFSET, record.getLowTHWNative());
      writeShortValue(updateFile, offset + DailySummary2Record.HEAT_DD_OFFSET, record.getIntegratedHeatDD65Native());
      writeShortValue(updateFile, offset + DailySummary2Record.HI_WET_BULB_OFFSET, record.getHiWetBuldTempNative());
      writeShortValue(updateFile, offset + DailySummary2Record.LOW_WET_BULB_OFFSET, record.getLowWetBulbTempNative());
      writeShortValue(updateFile, offset + DailySummary2Record.AVG_WET_BULB_OFFSET, record.getAvgWetBulbTempNative());
      writeShortValue(updateFile, offset + DailySummary2Record.COOL_DD_OFFSET, record.getIntegratedCoolDD65Native());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    DatabaseWriter dbWriter = new DatabaseWriter();
    DatabaseReader dbReader = DatabaseReader.getInstance();

    // Get the last record's timestamp
    try
    {
      dbReader.readData(2019, 5, null);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    DataFileRecord nextRecord = dbReader.getNextRecord();
    DataFileRecord lastRecord = nextRecord;

    while (nextRecord != null)
    {
      lastRecord = nextRecord;
      nextRecord = dbReader.getNextRecord();
    }
    System.out.println("Last record's packed time = " + ((WeatherRecord) lastRecord).getPackedTime());
    short timestamp = TimeUtil.convertPackedTimeToTimestamp(((WeatherRecord) lastRecord).getPackedTime());
    int hour = TimeUtil.getHour(timestamp);
    int minute = TimeUtil.getMinute(timestamp) - 5;
    System.out.println("Last record's hour = " + hour + ", minute = " + minute);
    int day = 2;

    DmpDataExtended data = new DmpDataExtended();
    for (int i = 0; i < 100; i++)
    {
      if (hour == 23 && minute == 55)
      {
        day++;
        hour = 0;
        minute = 0;
      }
      else if (minute == 55)
      {
        hour++;
        minute = 0;
      }
      else
      {
        minute += 5;
      }

      data.setTimeStamp(TimeUtil.getPackedTime(hour, minute));
      data.setDateStamp(TimeUtil.getDateStamp(day, 5, 2019));
      dbWriter.insertWeatherRecord(data);
    }
  }
}
