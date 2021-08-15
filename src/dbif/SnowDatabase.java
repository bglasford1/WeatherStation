/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class saves and retrieves snow data observations that are
            manually collected.  The database consists of a CSV file.  For
            this reason there is no export function.  There is one file as
            there are not many observations in a given year.  New records
            are only added to the end of the file.

  Mods:		  09/01/21 Initial Release.
*/
package dbif;

import data.dbrecord.SnowRecord;
import gui.snow.SnowDataListener;
import util.ConfigProperties;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SnowDatabase
{
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private static final String FILENAME = "SnowData.csv";
  private static final String TEMP_FILENAME = "TempSnowData.csv";
  private final List<SnowDataListener> listeners = new ArrayList<>();
  private final String databaseLocation;

  private static final int YEAR_COLUMN = 0;
  private static final int MONTH_COLUMN = 1;
  private static final int DAY_COLUMN = 2;
  private static final int AMOUNT_COLUMN = 3;

  private static class SingletonHelper
  {
    private static final SnowDatabase INSTANCE = new SnowDatabase();
  }

  public static SnowDatabase getInstance()
  {
    return SnowDatabase.SingletonHelper.INSTANCE;
  }

  private SnowDatabase()
  {
    if (PROPS.getTestMode())
      databaseLocation = PROPS.getTestSnowDatabaseLocation();
    else
      databaseLocation = PROPS.getSnowDatabaseLocation();
  }

  /**
   * Method to register interest when a record is added.
   */
  public void addListener(SnowDataListener listener)
  {
    listeners.add(listener);
  }
  
  /**
   * This method reads all the data for the snow data file.
   *
   * @return A list of all snow records.
   */
  public List<SnowRecord> readData()
  {
    BufferedReader breader;
    try
    {
      breader = new BufferedReader(new FileReader(databaseLocation + FILENAME));

      List<SnowRecord> snowRecords = new ArrayList<>();
      String nextLine;
      while ((nextLine = breader.readLine()) != null)
      {
        SnowRecord record = new SnowRecord();
        String[] splitValues = nextLine.split(",");
        record.setYear(Integer.parseInt(splitValues[YEAR_COLUMN]));
        record.setMonth(Integer.parseInt(splitValues[MONTH_COLUMN]));
        record.setDay(Integer.parseInt(splitValues[DAY_COLUMN]));
        record.setAmount(Float.parseFloat(splitValues[AMOUNT_COLUMN]));
        snowRecords.add(record);
      }
      return snowRecords;
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      return null;
    }
  }

  /**
   * This method writes a data record which is derived from the snow data.  This method only writes to the end of the
   * file.
   *
   * @param data  The data to write.
   */
  public void insertSnowRecord(SnowRecord data)
  {
    try
    {
      FileWriter pw = new FileWriter(databaseLocation + FILENAME,true);
      pw.write(data.toString() + "\n");
      pw.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    // Notify everybody that may be interested.
    for (SnowDataListener nextListener : listeners)
      nextListener.dataAdded(data);
  }

  /**
   * Method to modify a record.  The record is searched for based on the year/month/day values.  Once found, the new
   * amount is written out.
   *
   * @param oldRecord The old record to modify.
   * @param newRecord The new record to modify.
   */
  public void modifyRecord(SnowRecord oldRecord, SnowRecord newRecord)
  {
    File tempFile = new File(databaseLocation + TEMP_FILENAME);
    File snowDBFile = new File(databaseLocation + FILENAME);

    try
    {
      // Open snow database file.
      BufferedReader reader = new BufferedReader(new FileReader(databaseLocation + FILENAME));

      // Create and open temp file.
      tempFile.createNewFile();
      FileWriter writer = new FileWriter(databaseLocation + TEMP_FILENAME,true);

      String nextLine;
      while ((nextLine = reader.readLine()) != null)
      {
        String[] splitValues = nextLine.split(",");
        if (oldRecord.getYear() == Integer.valueOf(splitValues[YEAR_COLUMN]) &&
          oldRecord.getMonth() == Integer.valueOf(splitValues[MONTH_COLUMN]) &&
          oldRecord.getDay() == Integer.valueOf(splitValues[DAY_COLUMN]) &&
          oldRecord.getAmount() == Float.valueOf(splitValues[AMOUNT_COLUMN]))
        {
          writer.write(newRecord.toString() + "\n");
        }
        else
        {
          writer.write(nextLine + "\n");
        }
      }
      writer.close();
      reader.close();

      // Delete the original snow database file.
      snowDBFile.delete();

      // Rename the temp file to the original filename
      tempFile.renameTo(snowDBFile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Delete a record in the snow database. The record must match exactly.
   *
   * @param record The record to delete.
   */
  public void deleteRecord(SnowRecord record)
  {
    File tempFile = new File(databaseLocation + TEMP_FILENAME);
    File snowDBFile = new File(databaseLocation + FILENAME);

    try
    {
      // Open snow database file.
      BufferedReader reader = new BufferedReader(new FileReader(databaseLocation + FILENAME));

      // Create and open temp file.
      tempFile.createNewFile();
      FileWriter writer = new FileWriter(databaseLocation + TEMP_FILENAME,true);

      // Read all lines and write to temp file unless this is the line to delete.
      String nextLine;
      while ((nextLine = reader.readLine()) != null)
      {
        String[] splitValues = nextLine.split(",");
        if (record.getYear() != Integer.valueOf(splitValues[YEAR_COLUMN]) ||
          record.getMonth() != Integer.valueOf(splitValues[MONTH_COLUMN]) ||
          record.getDay() != Integer.valueOf(splitValues[DAY_COLUMN]) ||
          record.getAmount() != Float.valueOf(splitValues[AMOUNT_COLUMN]))
        {
          writer.write(nextLine + "\n");
        }
      }
      writer.close();
      reader.close();

      // Delete the original snow database file.
      snowDBFile.delete();

      // Rename the temp file to the original filename
      tempFile.renameTo(snowDBFile);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
  }
}
