/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Common class for the database reader and writer classes.

  Mods:		  09/01/21 Initial Release.
*/
package dbif;

import util.ByteUtil;
import util.ConfigProperties;
import util.TimeUtil;

import java.io.*;

public class DatabaseCommon
{
  // File location pointers.
  // The first 16 bytes contains a hardcoded "WDAT5.3        53" followed by a long value of the total number of
  // records in the file.  The rest of the header contains 32 records, one per day consisting of the number of
  // records in the day (short) followed by the starting record offset for the day.  These records are filled in
  // once the first day record is written.
  private static final int ID_CODE_SIZE = 16;
  private static final int TOTAL_RECORDS_SIZE = 4;
  public static final int TOTAL_RECORDS_OFFSET = ID_CODE_SIZE;
  private static final int DAY_INDEX_RECORD_SIZE = 6;
  public static final int DAY_INDEX_RECORD_OFFSET = ID_CODE_SIZE + TOTAL_RECORDS_SIZE;
  private static final int NUMBER_OF_DAY_INDEX_RECORDS = 32;
  public static final int RECORD_SIZE = 88;
  public static final int HEADER_BLOCK_SIZE = ID_CODE_SIZE + TOTAL_RECORDS_SIZE + (NUMBER_OF_DAY_INDEX_RECORDS * DAY_INDEX_RECORD_SIZE);

  private static final ConfigProperties PROPS = ConfigProperties.instance();
  public static final String FILE_EXT = ".wlk";
  public static final String FILE_HEADER = "WDAT5.3";
  private static final int RECORDS_IN_DAY = 24 * 60 / PROPS.getArchiveInterval() + 2;

  public static final short UNDEFINED_SHORT_VALUE = (short)0x8000;
  public static final byte  UNDEFINED_BYTE_VALUE = (byte)0xFF;
  public static final String UNDEFINED_STRING_VALUE = "---";

  // This assumes that records are read sequentially and does not go back and fill in missing gaps.
  private short lastTimeStamp = -1;
  private short lastDateStamp = -1;

  private static class SingletonHelper
  {
    private static final DatabaseCommon INSTANCE = new DatabaseCommon();
  }

  public static DatabaseCommon getInstance()
  {
    return DatabaseCommon.SingletonHelper.INSTANCE;
  }

  /**
   * Constructor
   */
  private DatabaseCommon()
  {
    // Get the last packed time.
    String filename = getLatestFilename();
    try (RandomAccessFile updateFile = new RandomAccessFile(getDirectory() + filename, "rw"))
    {
      // Read the total number of records value (4 bytes, little endian).
      byte[] byteArray = new byte[4];
      updateFile.seek(DatabaseCommon.TOTAL_RECORDS_OFFSET);
      byteArray[3] = updateFile.readByte();
      updateFile.seek(DatabaseCommon.TOTAL_RECORDS_OFFSET + 1);
      byteArray[2] = updateFile.readByte();
      int totalRecords = ByteUtil.byteArrayToInt(byteArray);

      int day = totalRecords / DatabaseCommon.RECORDS_IN_DAY + 1;
      int month = TimeUtil.getMonth(filename);
      int year = TimeUtil.getYear(filename);
      lastDateStamp = TimeUtil.getDateStamp(day, month, year);

      int offset = DatabaseCommon.HEADER_BLOCK_SIZE + (totalRecords - 1) * DatabaseCommon.RECORD_SIZE + 4;
      // Convert the packed time of the DB record to a timestamp value minus 5.
      lastTimeStamp = (short)(TimeUtil.convertPackedTimeToTimestamp(readTwoByteValues(updateFile, offset)) - 5);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Method to set the last timestamp value of the last record received.  This is used by the DMPAFT command.
   *
   * @param timeValue  The packed time value to save.
   */
  public void setLastTimeStamp(short timeValue)
  {
    this.lastTimeStamp = timeValue;
  }

  /**
   * Method to get the last timestamp of the last record received.  This is the value sent in a DMP record, the time
   * at the beginning of the archive period.  This method is lazy instantiation.  If the value is not initialized then
   * the last written value is read.  If a value already exists then it is simply returned.
   *
   * @return The short value. If -1 is returned, then the value was invalid.
   */
  public short getLastTimeStamp()
  {
    return lastTimeStamp;
  }

  /**
   * Method to set the date time of the last archived record.
   *
   * @param dateTime The date time of the last record.
   */
  public void setLastDateStamp(short dateTime)
  {
    this.lastDateStamp = dateTime;
  }

  /**
   * Method to get the date time of the last record in the archive.  This is a number that represents the year, month
   * and day of the end of the archive period.  This method is lazy instantiation.  If the value is not initilaized then
   * the last written value is read.  If a value already exists then it is simply returned.
   *
   * @return The short value.  If an error is encountered, zero is returned.  This is an invalid value.
   */
  public short getLastDateStamp()
  {
    return lastDateStamp;
  }

  /**
   * Convenience method to read a two byte short value.
   *
   * @param updateFile The open random access file.
   * @param offset The byte offset.
   * @return The value read.
   * @throws IOException Any I/O exception is rethrown for the caller to handle.
   */
  public short readTwoByteValues(RandomAccessFile updateFile, int offset) throws IOException
  {
    byte[] bytes = new byte[2];
    updateFile.seek(offset);
    bytes[1] = updateFile.readByte();
    updateFile.seek(offset + 1);
    bytes[0] = updateFile.readByte();
    return ByteUtil.byteArrayToShort(bytes);
  }

  /**
   * Convenience method to write a two byte short value to a file output stream.
   *
   * @param out The file output stream to write the data.
   * @param datum The short value to write, little endian.
   * @throws IOException Any I/O exception is rethrown for the caller to handle.
   */
  public void writeShortValue(FileOutputStream out, short datum) throws IOException
  {
    byte[] bytes = ByteUtil.shortToByteArray(datum);
    out.write(bytes[1]);
    out.write(bytes[0]);
  }

  /**
   * Convenience method to write a two byte short value to a random access file.
   *
   * @param out The file output stream to write the data.
   * @param offset The offset to the first byte.
   * @param datum The short value to write, little endian.
   * @throws IOException Any I/O exception is rethrown for the caller to handle.
   */
  public void writeShortValue(RandomAccessFile out, int offset, short datum) throws IOException
  {
    byte[] bytes = ByteUtil.shortToByteArray(datum);
    out.seek(offset);
    out.write(bytes[1]);
    out.seek(offset + 1);
    out.write(bytes[0]);
  }

  /**
   * Retrieve the filename for the newest database file.
   *
   * @return The filename of the newest database file.
   */
  static String getLatestFilename()
  {
    String yearString = String.valueOf(getLatestYear());
    int latestMonth = getLatestMonth();
    String monthString = String.valueOf(latestMonth);
    if (latestMonth < 10)
      monthString = "0" + monthString;

    return yearString + "-" + monthString + FILE_EXT;
  }

  /**
   * Generate and return the filename for the month and year provided.
   *
   * @return The filename of the newest database file.
   */
  static String getFilename(int year, int month)
  {
    String yearString = String.valueOf(year);
    String monthString = String.valueOf(month);
    if (month < 10)
      monthString = "0" + monthString;

    return yearString + "-" + monthString + FILE_EXT;
  }

  /**
   * Get the latest database directory string based on the test mode.
   *
   * @return The database directory location.
   */
  static String getDirectory()
  {
    if (PROPS.getTestMode())
      return PROPS.getTestDatabaseLocation();
    else
      return PROPS.getDatabaseLocation();
  }

  /**
   * Method to determine the last year that data was saved.  This is determined by searching the
   * directory that stores the database files looking for the latest filename.
   * @return The latest year as an integer.
   */
  public static int getLatestYear()
  {
    File folder;
    if (PROPS.getTestMode())
      folder = new File(PROPS.getTestDatabaseLocation());
    else
      folder = new File(PROPS.getDatabaseLocation());

    File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(FILE_EXT));
    if (listOfFiles == null || listOfFiles.length == 0)
      return 0;

    int newestYear = 0;
    for (File file : listOfFiles)
    {
      int nextYear = TimeUtil.getYear(file.getName());
      if (nextYear > newestYear)
        newestYear = nextYear;
    }
    return newestYear;
  }

  /**
   * Method to determine the last month that data was saved.  This is determined by searching the
   * directory that stores the database files looking for the latest filename.
   * @return The latest month as an integer.
   */
  private static int getLatestMonth()
  {
    int latestYear = getLatestYear();

    File folder;
    if (PROPS.getTestMode())
      folder = new File(PROPS.getTestDatabaseLocation());
    else
      folder = new File(PROPS.getDatabaseLocation());

    File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(FILE_EXT));
    if (listOfFiles == null)
      return 0;

    int newestMonth = 0;
    for (File file : listOfFiles)
    {
      int nextYear = TimeUtil.getYear(file.getName());
      if (nextYear == latestYear)
      {
        int nextMonth = TimeUtil.getMonth(file.getName());
        if (nextMonth > newestMonth)
          newestMonth = nextMonth;
      }
    }
    return newestMonth;
  }

  /**
   * Method used to determine the offset in bytes of the last summary record.  This would be the start of the
   * first summary record for the day.  This information is contained in the day index array of the header.
   *
   * @param updateFile A pointer to the open random access file.
   * @return The number of bytes into the file for the start of summary record 1.
   * @throws IOException Any I/O exceptions are thrown to the caller.
   */
  public int getSummaryRecordOffset(RandomAccessFile updateFile) throws IOException
  {
    // Get the offset for the last valid day count in the day index records to get the starting position.
    boolean validRecordFound = false;
    int offset = DatabaseCommon.DAY_INDEX_RECORD_OFFSET + 6;
    for (int i = 1; i < 32; i++)
    {
      int recordsInDay = readTwoByteValues(updateFile, offset);
      if (!validRecordFound && recordsInDay > 0)
      {
        validRecordFound = true;
      }
      else if (validRecordFound && recordsInDay == 0)
      {
        // Read starting position which is previous 4 bytes.
        byte[] byteArray = new byte[4];
        updateFile.seek(offset - 4);
        byteArray[3] = updateFile.readByte();
        updateFile.seek(offset - 3);
        byteArray[2] = updateFile.readByte();
        updateFile.seek(offset - 2);
        byteArray[1] = updateFile.readByte();
        updateFile.seek(offset - 1);
        byteArray[0] = updateFile.readByte();
        return ByteUtil.byteArrayToInt(byteArray) * DatabaseCommon.RECORD_SIZE + DatabaseCommon.HEADER_BLOCK_SIZE;
      }
      else if (validRecordFound && i == 31)
      {
        // Read starting position of the current record, which is the last record.
        byte[] byteArray = new byte[4];
        updateFile.seek(offset + 2);
        byteArray[3] = updateFile.readByte();
        updateFile.seek(offset + 3);
        byteArray[2] = updateFile.readByte();
        updateFile.seek(offset + 4);
        byteArray[1] = updateFile.readByte();
        updateFile.seek(offset + 5);
        byteArray[0] = updateFile.readByte();
        return ByteUtil.byteArrayToInt(byteArray) * DatabaseCommon.RECORD_SIZE + DatabaseCommon.HEADER_BLOCK_SIZE;
      }
      offset = offset + 6;
    }
    throw new FileNotFoundException("No valid day index record.");
  }
}
