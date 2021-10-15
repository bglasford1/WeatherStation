/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class reads database records.  When requested, the code reads
            all the records in a given database file and stores them in an
            internal array.  The records can be retrieved without any more file
            reads.  This class is a singleton because it saves the written
            records and all other classes operate on the one read data file.

  Mods:		  09/01/21  Initial Release.
            10/15/21  Fixed ET calculation.
*/
package dbif;

import algorithms.Calculations;
import data.dbrecord.*;
import util.ByteUtil;
import util.Logger;
import util.TimeUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseReader
{
  private static final DatabaseCommon DB_COMMON = DatabaseCommon.getInstance();

  private int yearRead = 0;
  private int monthRead = 0;
  private int nextRecord = 0;
  private final List<DataFileRecord> records = new ArrayList<>();
  private int nextDaysRecord = 0;
  private int nextSummaryRecord = 0;
  private final List<DataFileRecord> summaryRecords = new ArrayList<>();
  private final List<WeatherRecordExtended> daysRecords = new ArrayList<>();
  private final int[] recordsInDay = new int[32];
  private final Logger logger = Logger.getInstance();

  // Data bins: monthly and yearly.  All time values are in milliseconds since epoch
  private final HashMap<Long, Float> yearlyBins  = new HashMap<>(); // time index is by season with a value of January 1st.
  private final HashMap<Long, Float> monthlyBins = new HashMap<>(); // time index is by month with a value of the 1st.

  private static class SingletonHelper
  {
    private static final DatabaseReader INSTANCE = new DatabaseReader();
  }

  public static DatabaseReader getInstance()
  {
    return SingletonHelper.INSTANCE;
  }

  /**
   * Constructor that reads and pre-populates the last packed time.
   */
  private DatabaseReader() { }

  /**
   * This method reads a wind bin value with an even index.  In the wind bin value area, two values share three bytes.
   * The even indexes use the first byte and the lower half of the third byte as the high order value byte.
   * The summary records use random file access since the fields already exist.  This routine does not handle
   * any file I/O exception, but simply throws it to the caller.
   *
   * @param updateFile The file pointer.
   * @param offset1 The offset of the first, low order byte to write.
   * @param offset2 The offset of the second, high order byte to write.
   * @return The int value of the wind bin.
   * @throws IOException Any exception that may be encountered.
   */
  private int readEvenWindValue(RandomAccessFile updateFile, int offset1, int offset2)
    throws IOException
  {
    byte[] valueBytes = new byte[2];
    updateFile.seek(offset1);
    valueBytes[1] = updateFile.readByte();
    updateFile.seek(offset2);
    valueBytes[0] = (byte)(updateFile.readByte() & 0x0F);
    return ByteUtil.byteArrayToShort(valueBytes);
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
   * @return The int value of the wind bin.
   * @throws IOException Any exception that may be encountered.
   */
  private int readOddWindValue(RandomAccessFile updateFile, int offset1, int offset2)
    throws IOException
  {
    // Read existing value and add 5 minutes.
    byte[] valueBytes = new byte[2];
    updateFile.seek(offset1);
    valueBytes[1] = updateFile.readByte();
    updateFile.seek(offset2);
    valueBytes[0] = (byte)((updateFile.readByte() & 0xF0) >> 4);
    return ByteUtil.byteArrayToShort(valueBytes);
  }

  /**
   * This method gets the current row count pointer into the internal records array.
   *
   * @return The row counter.
   */
  public int getRowCount()
  {
    return records.size();
  }

  /**
   * This method resets the internal records pointer to the start of the array.
   */
  public void reset()
  {
    nextRecord = 0;
  }

  /**
   * Clear the internal records list.  This is used when getting a new data set.
   */
  private void clearDaysRecords()
  {
    records.clear();
    daysRecords.clear();
    nextRecord = 0;
    nextDaysRecord = 0;
  }

  /**
   * Return the next record in the internal record array.  The internal pointer is incremented.
   *
   * @return The data file record; one of either WeatherRecord, DailySummary1Record or DailySummary2Record.
   */
  public DataFileRecord getNextRecord()
  {
    if (nextRecord >= records.size())
    {
      return null;
    }
    DataFileRecord record = records.get(nextRecord);
    nextRecord += 1;
    return record;
  }

  /**
   * Return the next record in the internal record array.  The records are all the 5 minute weather records in
   * the current day.  The internal pointer is incremented.
   *
   * @return The next weather record.
   */
  public WeatherRecordExtended getNextDaysRecord()
  {
    if (nextDaysRecord >= daysRecords.size())
    {
      return null;
    }
    WeatherRecordExtended record = daysRecords.get(nextDaysRecord);
    nextDaysRecord += 1;
    return record;
  }

  /**
   * Clear the internal summary records list.  This is used when getting a new data set.
   */
  private void clearSummaryRecords()
  {
    summaryRecords.clear();
    nextSummaryRecord = 0;
  }

  /**
   * Return the next record in the internal record array.  The internal pointer is incremented.
   *
   * @return The data file record; either DailySummary1Record or DailySummary2Record.
   */
  public DataFileRecord getNextSummaryRecord()
  {
    if (nextSummaryRecord >= summaryRecords.size())
    {
      return null;
    }
    DataFileRecord record = summaryRecords.get(nextSummaryRecord);
    nextSummaryRecord += 1;
    return record;
  }

  /**
   * This method resets the internal summary records pointer to the start of the array.
   */
  public void resetSummary()
  {
    nextSummaryRecord = 0;
  }

  /**
   * Check to see if a record exists.  The hour/minute is of the data from the console, i.e. at the front of the
   * data packet.  This needs to be converted to the end of the packet by adding the archive interval.
   *
   * @param year The year of the record.
   * @param month The month of the record.
   * @param day The day of the record.
   * @param hour The hour of the record.
   * @param minute The minute of the record.
   * @return Whether or not the record exists.
   */
  public boolean recordExists(int year, int month, int day, int hour, int minute)
  {
    // Calculate the packed time to search for.
    int newMinute = minute + 5; // TODO: get archive interval and add that.
    int newHour = hour;
    if (newMinute == 60)
    {
      newMinute = 0;
      newHour++;
    }
    short packedTime = TimeUtil.getPackedTime(newHour, newMinute);

    // If the year/month data is not in the buffers, then read that data.
    try
    {
      if (year != yearRead || month != monthRead)
        readData(year, month, null);
    }
    catch (IOException e)
    {
      return false;
    }

    // Reset and read all data looking for the record.
    reset();
    boolean recordFound = false;
    boolean dayFound = false;
    int thisDay = 0;
    DataFileRecord nextRecord = getNextRecord();
    while (nextRecord != null)
    {
      if (nextRecord instanceof DailySummary1Record)
      {
        thisDay++;
        if (day == thisDay)
        {
          dayFound = true;
        }
      }
      if (dayFound)
      {
        if (nextRecord instanceof WeatherRecord)
        {
          WeatherRecord data = (WeatherRecord)nextRecord;
          if (data.getPackedTime() == packedTime)
          {
            recordFound = true;
            break;
          }
        }
      }
      nextRecord = getNextRecord();
    }
    return recordFound;
  }

  /**
   * Read and pre-populate the rain data into yearly and monthly bins.  The daily data is read from the summary records.
   */
  public void readRainData()
  {
    // Clear out any previous data.
    monthlyBins.clear();
    yearlyBins.clear();

    // Loop through each weather DB file.
    File folder = new File(DatabaseCommon.getDirectory());
    File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(DatabaseCommon.FILE_EXT));
    if (listOfFiles == null || listOfFiles.length == 0)
      return;

    for (File file : listOfFiles)
    {
      float monthlyRainTotal = 0;

      // For each file read each summary record's rain total and add up.
      try (RandomAccessFile updateFile = new RandomAccessFile(DatabaseCommon.getDirectory() + file.getName(), "rw"))
      {
        // For each day summary record, index into summary record 1's rain total.
        for (int day = 1; day <= 31; day++)
        {
          // Extract the records in day.
          int dayRecordOffset = DatabaseCommon.DAY_INDEX_RECORD_OFFSET + 6 + ((day - 1) * 6);
          byte[] recordsInDayByteArray = new byte[2];
          updateFile.seek(dayRecordOffset);
          recordsInDayByteArray[1] = updateFile.readByte();
          updateFile.seek(dayRecordOffset + 1);
          recordsInDayByteArray[0] = updateFile.readByte();
          int recordsInDay = ByteUtil.byteArrayToShort(recordsInDayByteArray);

          if (recordsInDay != 0)
          {
            // Index into the header and retrieve the day index record.
            byte[] recordsByteArray = new byte[4];
            updateFile.seek(dayRecordOffset + 2);
            recordsByteArray[3] = updateFile.readByte();
            updateFile.seek(dayRecordOffset + 3);
            recordsByteArray[2] = updateFile.readByte();
            updateFile.seek(dayRecordOffset + 4);
            recordsByteArray[1] = updateFile.readByte();
            updateFile.seek(dayRecordOffset + 5);
            recordsByteArray[0] = updateFile.readByte();

            int rainOffset = ByteUtil.byteArrayToInt(recordsByteArray) * DatabaseCommon.RECORD_SIZE +
              DatabaseCommon.HEADER_BLOCK_SIZE + DailySummary1Record.DAILY_RAIN_TOTAL_OFFSET;
            byte[] rainTotalByteArray = new byte[2];
            updateFile.seek(rainOffset);
            rainTotalByteArray[1] = updateFile.readByte();
            updateFile.seek(rainOffset + 1);
            rainTotalByteArray[0] = updateFile.readByte();
            float daysRain = ByteUtil.byteArrayToShort(rainTotalByteArray);
            daysRain = daysRain / 100f; // Divide by 100 and then another 10 later on...
            monthlyRainTotal = monthlyRainTotal + daysRain;
          }
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }

      // For some reason dividing by 1000 right off the bat causes the numbers to be low.
      monthlyRainTotal = monthlyRainTotal / 10;

      int year = TimeUtil.getYear(file.getName());
      int month = TimeUtil.getMonth(file.getName());
      LocalDateTime localMonthDate = LocalDateTime.of(year, month, 1, 0, 0);
      long monthDate = localMonthDate.atZone(ZoneId.of("America/Denver")).toInstant().toEpochMilli();
      monthlyBins.put(monthDate, monthlyRainTotal);

      LocalDateTime localYearDate = LocalDateTime.of(year, 1, 1, 0, 0);
      long yearDate = localYearDate.atZone(ZoneId.of("America/Denver")).toInstant().toEpochMilli();
      Float yearValue = yearlyBins.get(yearDate);
      if (yearValue == null)
      {
        yearValue = monthlyRainTotal;
        yearlyBins.put(yearDate, yearValue);
      }
      else
      {
        yearValue = yearValue + monthlyRainTotal;
        yearlyBins.replace(yearDate, yearValue);
      }
    }
  }

  /**
   * Return the pre-populated yearly rain bins.
   *
   * @return The yearly data.
   */
  public HashMap<Long, Float> getYearlyRainData()
  {
    return yearlyBins;
  }

  /**
   * Get the pre-populated monthly data.
   *
   * @return The monthly data.
   */
  public HashMap<Long, Float> getMonthlyRainData()
  {
    return monthlyBins;
  }

  /**
   * Method to get the date of the last day it rained.
   *
   * @return The date string.
   */
  public String getLastRainDate()
  {
    int year = LocalDate.now().getYear();
    int month = LocalDate.now().getMonthValue();
    int day = 1;

    int lastDay = 0;
    boolean dataFound = false;
    while (!dataFound)
    {
      try
      {
        readData(year, month, null);
        reset();
      }
      catch (IOException e)
      {
        return "mm/dd/yyyy";
      }

      DataFileRecord nextRecord = getNextRecord();
      while (nextRecord != null)
      {
        if (nextRecord instanceof DailySummary1Record)
        {
          DailySummary1Record record = (DailySummary1Record)nextRecord;
          if (record.getDailyRainTotal() > 0)
          {
            dataFound = true;
            lastDay = day;
          }
          day++;
        }
        nextRecord = getNextRecord();
      }

      if (!dataFound)
      {
        if (month == 1)
        {
          month = 12;
          year--;
        }
        else
        {
          month--;
        }
      }
    }
    return Integer.toString(month) + "/" + Integer.toString(lastDay) + "/" + Integer.toString(year);
  }

  public String getFilename(int year, int month)
  {
    String monthString = "";
    if ((month > 0) && (month < 10))
    {
      monthString = "0" + month;
    }
    else
    {
      monthString = monthString + month;
    }

    return DatabaseCommon.getDirectory() + year + "-" + monthString + DatabaseCommon.FILE_EXT;
  }

  public boolean fileExists(int year, int month)
  {
    File f = new File(getFilename(year, month));
    return (f.exists() && !f.isDirectory());
  }

  /**
   * Method to get the latest heat degree day total from the last day's summary record.
   *
   * @return The total heat degree day.
   */
  public float getHeatDDTotal()
  {
    try (RandomAccessFile updateFile =
           new RandomAccessFile(DatabaseCommon.getDirectory() + DatabaseCommon.getLatestFilename(), "rw"))
    {
      int summaryRecordOffset = DB_COMMON.getSummaryRecordOffset(updateFile);
      int summaryRecordOffset2 = summaryRecordOffset + DatabaseCommon.RECORD_SIZE;
      return DB_COMMON.readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.HEAT_DD_OFFSET) / 10.0f;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * Method to get the latest cool degree day total from the last day's summary record.
   *
   * @return The total cool degree day.
   */
  public float getCoolDDTotal()
  {
    try (RandomAccessFile updateFile =
           new RandomAccessFile(DatabaseCommon.getDirectory() + DatabaseCommon.getLatestFilename(), "rw"))
    {
      int summaryRecordOffset = DB_COMMON.getSummaryRecordOffset(updateFile);
      int summaryRecordOffset2 = summaryRecordOffset + DatabaseCommon.RECORD_SIZE;
      return DB_COMMON.readTwoByteValues(updateFile, summaryRecordOffset2 + DailySummary2Record.COOL_DD_OFFSET) / 10.0f;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * Method to get the min/max temperature, min/max humidity and average solar radiation for the last 24 hour period.
   *
   * @param endDate The date/time for the end of the period, typically time now.
   * @return The data contained within a record.
   */
  public EvapotransRecord getEvapotransData(LocalDateTime endDate)
  {
    EvapotransRecord etRecord = new EvapotransRecord();

    LocalDateTime startDate = endDate.minusDays(1);

    try
    {
      readData(startDate.getYear(), startDate.getMonthValue(),
               getFilename(startDate.getYear(), startDate.getMonthValue()));
      reset();
    }
    catch (IOException e)
    {
      logger.logData("DatabaseReader: getEvapotransData: Unable to read data: " + e.getLocalizedMessage());
      return null;
    }

    float totalSolar = 0.0f;
    int totalPoints = 0;
    float totalWindSpeed = 0.0f;
    boolean startFound = false;
    DataFileRecord nextRecord = getNextRecord();
    while (nextRecord != null)
    {
      if (nextRecord instanceof WeatherRecord)
      {
        WeatherRecord record = (WeatherRecord) nextRecord;

        // Skip forward to the correct day/hour record.
        if (!startFound)
        {
          LocalDateTime time = record.getTimestamp();
          if (startDate.getDayOfMonth() <= time.getDayOfMonth() &&
              startDate.getHour() <= time.getHour() &&
              startDate.getMinute() <= time.getMinute())
          {
            startFound = true;
          }
          else
          {
            nextRecord = getNextRecord();
            continue;
          }
        }

        if (record.getOutsideTemp() < etRecord.getMinTemp())
          etRecord.setMinTemp(record.getOutsideTemp());
        if (record.getOutsideTemp() > etRecord.getMaxTemp())
          etRecord.setMaxTemp(record.getOutsideTemp());
        if (record.getOutsideHumidity() < etRecord.getMinHumidity())
          etRecord.setMinHumidity(record.getOutsideHumidity());
        if (record.getOutsideHumidity() > etRecord.getMaxHumidity())
          etRecord.setMaxHumidity(record.getOutsideHumidity());

        totalPoints++;
        totalSolar = totalSolar + record.getSolarRadiation();
        totalWindSpeed = totalWindSpeed + record.getAverageWindSpeed();
      }
      nextRecord = getNextRecord();
    }

    // If the data crosses the month boundary then there is another file's worth of data to read.
    if (startDate.getYear() != endDate.getYear() || startDate.getMonthValue() != endDate.getMonthValue())
    {
      try
      {
        readData(endDate.getYear(), endDate.getMonthValue(), getFilename(endDate.getYear(), endDate.getMonthValue()));
        reset();
      }
      catch (IOException e)
      {
        logger.logData("DatabaseReader: getEvapotransData: Unable to read data: " + e.getLocalizedMessage());
        return null;
      }

      nextRecord = getNextRecord();
      while (nextRecord != null)
      {
        if (nextRecord instanceof WeatherRecord)
        {
          WeatherRecord record = (WeatherRecord) nextRecord;

          if (record.getOutsideTemp() < etRecord.getMinTemp())
            etRecord.setMinTemp(record.getOutsideTemp());
          if (record.getOutsideTemp() > etRecord.getMaxTemp())
            etRecord.setMaxTemp(record.getOutsideTemp());
          if (record.getOutsideHumidity() < etRecord.getMinHumidity())
            etRecord.setMinHumidity(record.getOutsideHumidity());
          if (record.getOutsideHumidity() > etRecord.getMaxHumidity())
            etRecord.setMaxHumidity(record.getOutsideHumidity());

          totalPoints++;
          totalSolar = totalSolar + record.getSolarRadiation();
          totalWindSpeed = totalWindSpeed + record.getAverageWindSpeed();
        }
        nextRecord = getNextRecord();
      }
    }
    etRecord.setAvgSolarRad(totalSolar / (float)totalPoints);
    etRecord.setAvgWindSpeed(totalWindSpeed / (float)totalPoints);
    return etRecord;
  }

  /**
   * This method reads a days worth of data for a given data file.  The data files are one file for each month.  The data
   * is placed into an internal record array for later retrieval.
   *
   * @param year  The year to retrieve.
   * @param month The month to retrieve.
   * @param day   The day to retrieve.
   * @return An array of averages; InTemp, OutTemp, WindChill, DewPoint, OutHumid, Pressure, WindSpeed, WindRunTotal,
   *         heatIndex, wetBulbTemp.
   */
  public float[] readDaysAverages(int year, int month, int day)
  {
    int totalItems = 10;
    float[] answers = new float[totalItems];
    for (int i = 0; i < totalItems; i++)
      answers[i] = 0;

    try (RandomAccessFile updateFile = new RandomAccessFile(getFilename(year, month), "rw"))
    {
      // Index into the header and retrieve the day index record and record count.
      int dayRecordOffset = DatabaseCommon.DAY_INDEX_RECORD_OFFSET + 6 + ((day - 1) * 6);
      byte[] recordCountByteArray = new byte[2];
      updateFile.seek(dayRecordOffset);
      recordCountByteArray[1] = updateFile.readByte();
      updateFile.seek(dayRecordOffset + 1);
      recordCountByteArray[0] = updateFile.readByte();
      // Subtract two because the record count contains two summary records.
      int recordCount = ByteUtil.byteArrayToShort(recordCountByteArray) - 2;

      byte[] recordIndexByteArray = new byte[4];
      updateFile.seek(dayRecordOffset + 2);
      recordIndexByteArray[3] = updateFile.readByte();
      updateFile.seek(dayRecordOffset + 3);
      recordIndexByteArray[2] = updateFile.readByte();
      updateFile.seek(dayRecordOffset + 4);
      recordIndexByteArray[1] = updateFile.readByte();
      updateFile.seek(dayRecordOffset + 5);
      recordIndexByteArray[0] = updateFile.readByte();
      int recordIndex =
        ByteUtil.byteArrayToInt(recordIndexByteArray) * DatabaseCommon.RECORD_SIZE + DatabaseCommon.HEADER_BLOCK_SIZE;

      // Add to index the length of the first two summary records.
      recordIndex += DatabaseCommon.RECORD_SIZE * 2;

      for (int index = 0; index < recordCount; index++)
      {
        byte[] inTempByteArray = new byte[2];
        int inTempIndex = recordIndex + index * DatabaseCommon.RECORD_SIZE + WeatherRecord.INSIDE_TEMP_OFFSET;
        updateFile.seek(inTempIndex);
        inTempByteArray[1] = updateFile.readByte();
        updateFile.seek(inTempIndex + 1);
        inTempByteArray[0] = updateFile.readByte();
        answers[0] += ByteUtil.byteArrayToShort(inTempByteArray) / 10.0f;

        byte[] outTempByteArray = new byte[2];
        int outTempIndex = recordIndex + index * DatabaseCommon.RECORD_SIZE + WeatherRecord.OUTSIDE_TEMP_OFFSET;
        updateFile.seek(outTempIndex);
        outTempByteArray[1] = updateFile.readByte();
        updateFile.seek(outTempIndex + 1);
        outTempByteArray[0] = updateFile.readByte();
        float outsideTemp = ByteUtil.byteArrayToShort(outTempByteArray) / 10.0f;
        answers[1] += outsideTemp;

        byte[] outHumidByteArray = new byte[2];
        int outHumidIndex = recordIndex + index * DatabaseCommon.RECORD_SIZE + WeatherRecord.OUTSIDE_HUMID_OFFSET;
        updateFile.seek(outHumidIndex);
        outHumidByteArray[1] = updateFile.readByte();
        updateFile.seek(outHumidIndex + 1);
        outHumidByteArray[0] = updateFile.readByte();
        float outsideHumidity = ByteUtil.byteArrayToShort(outHumidByteArray) / 10.0f;
        answers[4] += outsideHumidity;

        byte[] pressureByteArray = new byte[2];
        int pressureIndex = recordIndex + index * DatabaseCommon.RECORD_SIZE + WeatherRecord.BAROMETER_OFFSET;
        updateFile.seek(pressureIndex);
        pressureByteArray[1] = updateFile.readByte();
        updateFile.seek(pressureIndex + 1);
        pressureByteArray[0] = updateFile.readByte();
        answers[5] += ByteUtil.byteArrayToShort(pressureByteArray) / 1000.0f;

        byte[] windSpeedByteArray = new byte[2];
        int windSpeedIndex = recordIndex + index * DatabaseCommon.RECORD_SIZE + WeatherRecord.WIND_SPEED_OFFSET;
        updateFile.seek(windSpeedIndex);
        windSpeedByteArray[1] = updateFile.readByte();
        updateFile.seek(windSpeedIndex + 1);
        windSpeedByteArray[0] = updateFile.readByte();
        float windSpeed = ByteUtil.byteArrayToShort(windSpeedByteArray) / 10.0f;
        answers[6] += windSpeed;

        float windChill = Calculations.calculateWindChill(outsideTemp, windSpeed);
        answers[2] += windChill;

        float dewPoint = Calculations.calculateDewPoint(outsideTemp, outsideHumidity);
        answers[3] += dewPoint;

        float heatIndex = Calculations.calculateHeatIndex(outsideTemp, outsideHumidity);
        answers[8] += heatIndex;

        float wetBulbTemp = Calculations.calculateWetBulbTemperature(outsideTemp, outsideHumidity);
        answers[9] += wetBulbTemp;

        // Totaling the wind run.
        // TODO: the value 5 minutes should be variablized to be the archive interval.
        answers[7] += windSpeed * (5.0 / 60.0);
      }
      answers[0] = answers[0] / recordCount; // Average In Temp
      answers[1] = answers[1] / recordCount; // Average Out Temp
      answers[2] = answers[2] / recordCount; // Average Wind Chill
      answers[3] = answers[3] / recordCount; // Average Dew Point
      answers[4] = answers[4] / recordCount; // Average Out Humid
      answers[5] = answers[5] / recordCount; // Average Pressure
      answers[6] = answers[6] / recordCount; // Average Wind Speed
      answers[8] = answers[8] / recordCount; // Average Heat Index
      answers[9] = answers[9] / recordCount; // Average Wet Bulb Temp
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
    return answers;
  }

  /**
   * Read the summary data records for the given year and month and save them for later retrieval.
   *
   * @param year The year to retrieve.
   * @param month The month to retrieve.
   */
  public void readSummaryData(int year, int month)
  {
    clearSummaryRecords();

    // Read the summary record's for each day.
    try (RandomAccessFile updateFile = new RandomAccessFile(getFilename(year, month),"r"))
    {
      for (int day = 1; day <= 31; day++)
      {
        // Extract the records in day.
        int dayRecordOffset = DatabaseCommon.DAY_INDEX_RECORD_OFFSET + 6 + ((day - 1) * 6);
        byte[] recordsInDayByteArray = new byte[2];
        updateFile.seek(dayRecordOffset);
        recordsInDayByteArray[1] = updateFile.readByte();
        updateFile.seek(dayRecordOffset + 1);
        recordsInDayByteArray[0] = updateFile.readByte();
        int recordsInDay = ByteUtil.byteArrayToShort(recordsInDayByteArray);

        if (recordsInDay != 0)
        {
          // Index into the header and retrieve the day index record.
          byte[] recordsByteArray = new byte[4];
          updateFile.seek(dayRecordOffset + 2);
          recordsByteArray[3] = updateFile.readByte();
          updateFile.seek(dayRecordOffset + 3);
          recordsByteArray[2] = updateFile.readByte();
          updateFile.seek(dayRecordOffset + 4);
          recordsByteArray[1] = updateFile.readByte();
          updateFile.seek(dayRecordOffset + 5);
          recordsByteArray[0] = updateFile.readByte();
          int dayIndex = ByteUtil.byteArrayToInt(recordsByteArray);

          // Offset to the next summary record. Skip over the data type byte.
          int offset = dayIndex * DatabaseCommon.RECORD_SIZE + DatabaseCommon.HEADER_BLOCK_SIZE + 1;
          updateFile.seek(offset);

          // Read the summary record #1 and place in the records cache.
          DailySummary1Record dailySummary1Record = getSummaryRecord1(updateFile);
          dailySummary1Record.setDay(day);
          summaryRecords.add(dailySummary1Record);

          // Read the summary record #2 and place in the records cache.
          updateFile.readByte();
          DailySummary2Record dailySummary2Record = getSummaryRecord2(updateFile);
          dailySummary2Record.setDay(day);
          summaryRecords.add(dailySummary2Record);
        }
      }
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  /**
   * This method reads all the data for a given data file.  The data files are one file for each month.  The data
   * is placed into an internal record array for later retrieval.
   *
   * @param year  The year to retrieve.
   * @param month The month to retrieve.
   * @param dbFilename The filename and directory location of the file to read.  If null then use standard location.
   * @throws IOException There is something wrong with the file.
   */
  public void readData(int year, int month, String dbFilename) throws IOException
  {
    clearDaysRecords();

    try
    {
      // Open file to read.
      RandomAccessFile fstream;
      if (dbFilename == null)
      {
        fstream = new RandomAccessFile(getFilename(year, month), "r");
      }
      else
      {
        fstream = new RandomAccessFile(dbFilename, "r");
      }

      yearRead = year;
      monthRead = month;

      // ID code = "WDAT5.3 ...
      for (int i = 0; i < 16; i++)
      {
        fstream.read();
      }

      // The four byte total number of records
      int[] intBytes = new int[4];
      for (int i = 0; i < 4; i++)
      {
        intBytes[i] = fstream.read();
      }
      int numOfRecords = ByteUtil.intArrayToInt(intBytes);

      int day = -1;

      // 32 day index records.  These records list the number of records in each day and the starting position of
      // each record block, starting with the first daily summary record.  For reading, only the records in a day
      // are saved to an array.  The array is indexed by day number.
      for (int i = 0; i < 32; i++)
      {
        int byte1 = fstream.read();
        int byte2 = fstream.read();
        recordsInDay[i] = (byte2 << 8 | byte1);

        // Set day to the day before the first day with data.  Each time a summary record is encountered,
        // the day will be incremented.
        if (day == -1 && recordsInDay[i] != 0)
          day = i - 1;

        // start position is not used.
        fstream.read();
        fstream.read();
        fstream.read();
        fstream.read();
      }

      // Determine the number of records excluding the records in the last day.
      int recordsBeforeLastDay = 0; // Number of records before the last day.
      for (int i = 1; i < 32; i++)
      {
        if (recordsInDay[i] == 0)
        {
          recordsBeforeLastDay -= recordsInDay[i - 1];
          break;
        }
        recordsBeforeLastDay += recordsInDay[i];
      }

      // Read each record in the file.
      for (int nextRecord = 0; nextRecord < numOfRecords; nextRecord++)
      {
        // The data type is 2 = Daily Summary Record #1, 3 = Daily Summary Record #2, 1 = Weather Data Record
        int dataType = fstream.read();

        switch (dataType)
        {
          case 2: // Daily Summary Record #1
          {
            // Increment to the next day.
            day = day + 1;

            // Read the summary record and place in the
            DailySummary1Record dailySummary1Record = getSummaryRecord1(fstream);
            records.add(dailySummary1Record);
            break;
          }
          case 3: // Daily Summary Record #2
          {
            // Read the summary record and place in the
            DailySummary2Record dailySummary2Record = getSummaryRecord2(fstream);
            records.add(dailySummary2Record);
            break;
          }
          case 1: // 5-minute Weather Record
          {
            WeatherRecordExtended weatherRecord = getWeatherRecord(fstream, year, month, day);
            records.add(weatherRecord);

            if (nextRecord > recordsBeforeLastDay)
            {
              daysRecords.add(weatherRecord);
            }
            break;
          }
          case -1: // End of file is reached.
            return;
          default:
            System.out.println("Invalid type code: " + dataType);
            break;
        }
      }
      fstream.close();
    }
    catch (IOException ioe)
    {
      logger.logData("DB Reader: Read Data error: " + ioe.getLocalizedMessage());
      throw ioe;
    }
  }

  /**
   * Read the file input stream, getting the next summary record #1 and returning the record.
   *
   * @param fstream The file input stream.
   * @return The daily summary #1 record.
   * @throws IOException Any IO exceptions.
   */
  private DailySummary1Record getSummaryRecord1(RandomAccessFile fstream) throws IOException
  {
    DailySummary1Record dailySummary1Record = new DailySummary1Record();

    // unused byte used to align remaining bytes
    fstream.read();

    int byte1 = fstream.read();
    int byte2 = fstream.read();
    dailySummary1Record.setDataSpan((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setHiOutTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setLowOutTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setHiInTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setLowInTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setAvgOutTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setAvgInTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setHiChillNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setLowChillNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setHiDewNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setLowDewNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setAvgChillNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setAvgDewNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setHiOutHumidNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setLowOutHumidNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setHiInHumidNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setLowInHumidNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setAvgOutHumidNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setHiBarNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setLowBarNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setAvgBarNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setHiSpeedNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setAvgSpeedNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setDailyWindRunTotalNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setHiTenMinSpeedNative((short) (byte2 << 8 | byte1));

    dailySummary1Record.setDirHiSpeedNative((byte) fstream.read());

    dailySummary1Record.setHiTenMinDirNative((byte) (fstream.read()));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setDailyRainTotalNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setHiRainRateNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary1Record.setDailyUVDoseNative((short) (byte2 << 8 | byte1));

    dailySummary1Record.setHiUVNative((byte) (fstream.read()));

    // Read and process 18 time values.  Three bytes hold two values. ex: if bytes are AA BB CD then
    // time value one is DAA and time value two is CBB.
    int byte3;
    for (int index = 0; index < 9; index++)
    {
      byte1 = fstream.read();
      byte2 = fstream.read();
      byte3 = fstream.read();

      dailySummary1Record.setTimeValue(index * 2, (short) ((byte3 & 0x0F) << 8 | byte1));
      dailySummary1Record.setTimeValue(index * 2 + 1, (short) ((byte3 & 0xF0) << 4 | byte2));
    }
    return dailySummary1Record;
  }

  /**
   * Read the file input stream, getting the next summary record #2 and returning the record.
   *
   * @param fstream The file input stream.
   * @return The daily summary #2 record.
   * @throws IOException Any IO exceptions.
   */
  private DailySummary2Record getSummaryRecord2(RandomAccessFile fstream) throws IOException
  {
    DailySummary2Record dailySummary2Record = new DailySummary2Record();

    // unused bytes; alignment byte and todays weather bitmap
    fstream.read();
    fstream.read();
    fstream.read();

    int byte1 = fstream.read();
    int byte2 = fstream.read();
    dailySummary2Record.setNumOfWindPackets(byte2 << 8 | byte1);

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setHiSolar((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setDailySolarEnergyNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setMinSunlight((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setDailyETTotalNative((short) (byte2 << 8 | byte1));

    // Heat Index values.
    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setHiHeatNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setLowHeatNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setAvgHeatNative((short) (byte2 << 8 | byte1));

    // THSW and THW values.
    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setHiTHSWNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setLowTHSWNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setHiTHWNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setLowTHWNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setIntegratedHeatDD65Native((short) (byte2 << 8 | byte1));

    // Wet Bulb Temps
    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setHiWetBulbTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setLowWetBulbTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setAvgWetBulbTempNative((short) (byte2 << 8 | byte1));

    // Read and process the 16 direction bin values.  These values contain the total number of minutes
    // within the day that the wind was blowing in this direction.  Three bytes hold two values.
    // Ex: if bytes are AA BB CD then the first value is DAA and the second value is CBB.
    int byte3;
    for (int i = 0; i < 8; i++)
    {
      byte1 = fstream.read();
      byte2 = fstream.read();
      byte3 = fstream.read();

      dailySummary2Record.setWindDirMinutes(i * 2, (short) ((byte3 & 0x0F) << 8 | byte1));
      dailySummary2Record.setWindDirMinutes(i * 2 + 1, (short) ((byte3 & 0xF0) << 4 | byte2));
    }

    // Read and process 10 time values.  Three bytes hold two values. ex: if bytes are AA BB CD then
    // time value one is DAA and time value two is CBB.
    for (int index = 0; index < 5; index++)
    {
      byte1 = fstream.read();
      byte2 = fstream.read();
      byte3 = fstream.read();

      dailySummary2Record.setTimeValue(index * 2, (short) ((byte3 & 0x0F) << 8 | byte1));
      dailySummary2Record.setTimeValue(index * 2 + 1, (short) ((byte3 & 0xF0) << 4 | byte2));
    }

    byte1 = fstream.read();
    byte2 = fstream.read();
    dailySummary2Record.setIntegratedCoolDD65Native((short) (byte2 << 8 | byte1));

    // Reserved bytes
    for (int i = 0; i < 11; i++)
    {
      fstream.read();
    }
    return dailySummary2Record;
  }

  /**
   * Read the file input stream, getting the next weather record and returning the record.
   *
   * @param fstream The file input stream.
   * @return The weather record.
   * @throws IOException Any IO exceptions.
   */
  private WeatherRecordExtended getWeatherRecord(RandomAccessFile fstream, int year, int month, int day)
    throws IOException
  {
    WeatherRecordExtended weatherRecord = new WeatherRecordExtended();

    int archiveInterval = fstream.read();
    weatherRecord.setArchiveInterval((byte) archiveInterval);

    fstream.read(); // iconFlags
    fstream.read(); // moreFlags

    int byte1 = fstream.read();
    int byte2 = fstream.read();
    // Minutes past midnight at the end of the 15 minute window that this data represents.
    short packedTime = (short) (byte2 << 8 | byte1);
    weatherRecord.setPackedTime(packedTime);

    // Create Java timestamp for convience in the GUI.  Note that the start time might not be midnight,
    // the start time is based on the packed time.  Convert the packed time to hours and minutes.
    // Also note that the packed time is at the end of the archive interval.
    int tempPackedTime = packedTime - archiveInterval;
    int hour = tempPackedTime / 60;
    int minute = tempPackedTime - (hour * 60);
    LocalDateTime timestamp = LocalDateTime.of(year, month, day, hour, minute);
    weatherRecord.setTimestamp(timestamp);

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setOutsideTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setHighOutsideTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setLowOutsideTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setInsideTempNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setPressureNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setOutsideHumidityNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setInsideHumidityNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    // Pull out the rain collector type.  It is not saved, but assumed to be hardcoded to 0.01".
    weatherRecord.setRainfallNative((short) (((byte) byte2 & (byte) 0x0F) << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setHighRainRateNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setAverageWindSpeedNative((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setHighWindSpeedNative((short) (byte2 << 8 | byte1));

    weatherRecord.setWindDirectionNative((byte) fstream.read());

    weatherRecord.setHighWindDirectionNative((byte) fstream.read());

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setNumOfWindSamples((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setSolarRadiation((short) (byte2 << 8 | byte1));

    byte1 = fstream.read();
    byte2 = fstream.read();
    weatherRecord.setHighSolarRadiation((short) (byte2 << 8 | byte1));

    fstream.read(); // UV, don't have sensor
    fstream.read(); // High UV, don't have sensor

    // no leaf temp sensors, extra Rad & new sensors
    for (int i = 0; i < 18; i++)
    {
      fstream.read();
    }

    weatherRecord.setForecast((byte) fstream.read());

    fstream.read(); // ET, Need UV sensor

    weatherRecord.setSoilTemp1Native((byte) fstream.read());

    // remaining soil temps, soil moisture, leaf wetness and extra temps & humidities
    for (int i = 0; i < 29; i++)
    {
      fstream.read();
    }

    // Add the calculated values.  These are saved in the extended record.
    if (weatherRecord.getOutsideTemp() < 65.0)
      weatherRecord.setHeatDD((float)((65.0 - weatherRecord.getOutsideTemp()) / 288.0));
    else
      weatherRecord.setHeatDD(0);

    if (weatherRecord.getOutsideTemp() > 65.0)
      weatherRecord.setCoolDD((float)((weatherRecord.getOutsideTemp() - 65.0) / 288.0));
    else
      weatherRecord.setCoolDD(0);

    weatherRecord.setWindRunTotal((float)(weatherRecord.getAverageWindSpeed() * (5.0 / 60.0)));

    return  weatherRecord;
  }

    /**
     * This method is called upon initialization to load the wind bins for the first time.
     *
     * @param direction Which wind direction to read.
     * @return The number of minutes the wind was dominant for this direction.
     */
  public Integer getDaysWindValue(WindDirection direction)
  {
    try (RandomAccessFile updateFile = new RandomAccessFile(DatabaseCommon.getDirectory() + DatabaseCommon.getLatestFilename(),
                                                            "rw"))
    {
      int baseOffset = DB_COMMON.getSummaryRecordOffset(updateFile);
      baseOffset = baseOffset + DatabaseCommon.RECORD_SIZE;

      switch (direction)
      {
        case N:
          return readEvenWindValue(updateFile,
                                   baseOffset + DailySummary2Record.N_OFFSET_1,
                                   baseOffset + DailySummary2Record.N_OFFSET_2);
        case NNE:
          return readOddWindValue(updateFile,
                                  baseOffset + DailySummary2Record.NNE_OFFSET_1,
                                  baseOffset + DailySummary2Record.NNE_OFFSET_2);
        case NE:
          return readEvenWindValue(updateFile,
                                   baseOffset + DailySummary2Record.NE_OFFSET_1,
                                   baseOffset + DailySummary2Record.NE_OFFSET_2);
        case ENE:
          return readOddWindValue(updateFile,
                                  baseOffset + DailySummary2Record.ENE_OFFSET_1,
                                  baseOffset + DailySummary2Record.ENE_OFFSET_2);
        case E:
          return readEvenWindValue(updateFile,
                                   baseOffset + DailySummary2Record.E_OFFSET_1,
                                   baseOffset + DailySummary2Record.E_OFFSET_2);
        case ESE:
          return readOddWindValue(updateFile,
                                  baseOffset + DailySummary2Record.ESE_OFFSET_1,
                                  baseOffset + DailySummary2Record.ESE_OFFSET_2);
        case SE:
          return readEvenWindValue(updateFile,
                                   baseOffset + DailySummary2Record.SE_OFFSET_1,
                                   baseOffset + DailySummary2Record.SE_OFFSET_2);
        case SSE:
          return readOddWindValue(updateFile,
                                  baseOffset + DailySummary2Record.SSE_OFFSET_1,
                                  baseOffset + DailySummary2Record.SSE_OFFSET_2);
        case S:
          return readEvenWindValue(updateFile,
                                   baseOffset + DailySummary2Record.S_OFFSET_1,
                                   baseOffset + DailySummary2Record.S_OFFSET_2);
        case SSW:
          return readOddWindValue(updateFile,
                                  baseOffset + DailySummary2Record.SSW_OFFSET_1,
                                  baseOffset + DailySummary2Record.SSW_OFFSET_2);
        case SW:
          return readEvenWindValue(updateFile,
                                   baseOffset + DailySummary2Record.SW_OFFSET_1,
                                   baseOffset + DailySummary2Record.SW_OFFSET_2);
        case WSW:
          return readOddWindValue(updateFile,
                                  baseOffset + DailySummary2Record.WSW_OFFSET_1,
                                  baseOffset + DailySummary2Record.WSW_OFFSET_2);
        case W:
          return readEvenWindValue(updateFile,
                                   baseOffset + DailySummary2Record.W_OFFSET_1,
                                   baseOffset + DailySummary2Record.W_OFFSET_2);
        case WNW:
          return readOddWindValue(updateFile,
                                  baseOffset + DailySummary2Record.WNW_OFFSET_1,
                                  baseOffset + DailySummary2Record.WNW_OFFSET_2);
        case NW:
          return readEvenWindValue(updateFile,
                                   baseOffset + DailySummary2Record.NW_OFFSET_1,
                                   baseOffset + DailySummary2Record.NW_OFFSET_2);
        case NNW:
          return readOddWindValue(updateFile,
                                  baseOffset + DailySummary2Record.NNW_OFFSET_1,
                                  baseOffset + DailySummary2Record.NNW_OFFSET_2);
        default:
          return null;
      }
    }
    catch (IOException e)
    {
      System.out.println("Unable to read wind dir bins.");
      e.printStackTrace();
      return null;
    }
  }

  public void decodeHeader(int year, int month, String dbFilename)
  {
    try (PrintStream printStream = new PrintStream(new FileOutputStream(
      "/Users/bill/Documents/Bill/WeatherStation/TestData/" + year + "-" + month + ".txt")))
    {
      // Open file to read.
      FileInputStream fstream;
      if (dbFilename == null)
      {
        fstream = new FileInputStream(getFilename(year, month));
        printStream.println("File: " + getFilename(year, month));
      }
      else
      {
        fstream = new FileInputStream(dbFilename);
        printStream.println("File: " + dbFilename);
      }

      // ID code = "WDAT5.3 ...
      for (int i = 0; i < 16; i++)
      {
        fstream.read();
      }

      // The four byte total number of records
      int[] intBytes = new int[4];
      for (int i = 0; i < 4; i++)
      {
        intBytes[i] = fstream.read();
      }
      int numOfRecords = ByteUtil.intArrayToInt(intBytes);
      printStream.println("Total records = " + numOfRecords);

      int day = -1;

      // 32 day index records.  These records list the number of records in each day and the starting position of
      // each record block, starting with the first daily summary record.  For reading, only the records in a day
      // are saved to an array.  The array is indexed by day number.
      for (int i = 0; i < 32; i++)
      {
        int byte1 = fstream.read();
        int byte2 = fstream.read();
        recordsInDay[i] = (byte2 << 8 | byte1);

        // Set day to the day before the first day with data.  Each time a summary record is encountered,
        // the day will be incremented.
        if (day == -1 && recordsInDay[i] != 0)
          day = i - 1;

        // start position is not used.
        for (int j = 0; j < 4; j++)
        {
          intBytes[j] = fstream.read();
        }
        int startPosition = ByteUtil.intArrayToInt(intBytes);
        startPosition = startPosition * 88 + 212;
        printStream.println("Day: " + i + "  Records in day: " + recordsInDay[i] + "  Start Position: " + startPosition);
      }
      printStream.println("---------------------------");

      fstream.close();
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    DatabaseReader database = DatabaseReader.getInstance();
    int year = 2019;
    int month = 6;
    int day = 9;
    int hour = 10;
    int minute = 15;

//    if (database.recordExists(year, month, day, hour, minute))
//    {
//      System.out.println(">>>>>> Record Exists <<<<<<<<");
//    }
//    else
//    {
//      System.out.println(">>>>>>>>  Record Does Not Exist <<<<<<<<");
//    }

    // This section prints out a summary of the records contained in the DB file specified.
    try (PrintStream printStream = new PrintStream(new FileOutputStream(
      "/Users/bill/Documents/Bill/WeatherStation/TestData/" + year + "-" + month + ".txt")))
    {
      try
      {
        database.readData(year, month, null);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      database.reset();
      printStream.println("Total records = " + database.getRowCount());

      day = 1;
      float totalWindSpeed = 0;
      int numberOfRecords = 0;
      int numberOfDayRecords = 0;
      short previousPackedTime = 0;
      DataFileRecord nextWeatherRecord = database.getNextRecord();
      while (nextWeatherRecord != null)
      {
        if ((nextWeatherRecord instanceof WeatherRecord))
        {
          numberOfDayRecords++;
          short packedTime = ((WeatherRecord) nextWeatherRecord).getPackedTime();
          short archiveInterval = ((WeatherRecord) nextWeatherRecord).getArchiveInterval();
          if (day == 17)
          {
            totalWindSpeed += ((WeatherRecord) nextWeatherRecord).getAverageWindSpeed();
            numberOfRecords++;
            System.out.println("Avg Wind: " + ((WeatherRecord) nextWeatherRecord).getAverageWindSpeed() +
                               ", Hi Wind: " + ((WeatherRecord) nextWeatherRecord).getHighWindSpeed());
          }
//          printStream.println("PackedTime: " + packedTime);
//                              ", Archive Interval: " + archiveInterval +
//                              ", OutTemp: " + ((WeatherRecord) nextWeatherRecord).getOutsideTemp() +
//                              ", InTemp: " + ((WeatherRecord) nextWeatherRecord).getInsideTemp() +
//                              ", Greenhouse: " + ((WeatherRecord) nextWeatherRecord).getSoilTemp1());
          if (packedTime == previousPackedTime)
          {
            System.out.println("Duplicate record found: Packed Time = " + packedTime);
          }
          else if ((previousPackedTime + archiveInterval) != packedTime)
          {
            System.out.println("Missing Record: Packed Time = " + packedTime);
          }
          previousPackedTime = packedTime;
        }
        else if ((nextWeatherRecord instanceof DailySummary1Record))
        {
          printStream.println("Number of day Records: " + numberOfDayRecords);
          printStream.println("---------------------------");
          printStream.println("Day = " + day);
//          System.out.println("Number of day Records: " + numberOfDayRecords);
          System.out.println("---------------------------");
          System.out.println("Day = " + day);
          System.out.println("High Wind: " + ((DailySummary1Record) nextWeatherRecord).getHiSpeed() +
                               ", Time High Wind: " + TimeUtil.toString(((DailySummary1Record) nextWeatherRecord).getTimeOfHighWindSpeed()));
//                               ", Low Out Temp: " + ((DailySummary1Record) nextWeatherRecord).getLowOutTemp());
        }
        else if ((nextWeatherRecord instanceof DailySummary2Record))
        {
//          printStream.println("High Solar: " + ((DailySummary2Record) nextWeatherRecord).getHiSolar());
//                              " Dir Bin N: " + ((DailySummary2Record) nextWeatherRecord).getNMinutes() +
//                              " Dir Bin NNE: " + ((DailySummary2Record) nextWeatherRecord).getNneMinutes());
          day++;
          numberOfDayRecords = 0;
          previousPackedTime = 0;
        }
        nextWeatherRecord = database.getNextRecord();
      }
      System.out.println("Avg Wind Speed = " + totalWindSpeed / numberOfRecords);
      printStream.println("Number of day Records: " + numberOfDayRecords);
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }

//    database.decodeHeader(year, month, "/Users/bill/Documents/Bill/WeatherStation/TestData/2019-04.wlk");
//    database.decodeHeader(year, month, "/Users/bill/Documents/WeatherLink/Station1/2019-04.wlk");
  }
}
