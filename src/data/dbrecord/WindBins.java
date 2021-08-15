/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class holds the wind bins.  The wind bins are contained within
            the daily summary record.  This class prevents the need to read the
            DB files once evey 5 minutes.

  Mods:		  09/01/21 Initial Release.
*/
package data.dbrecord;

import dbif.DatabaseCommon;
import dbif.DatabaseReader;
import util.TimeUtil;

import java.io.IOException;
import java.util.*;

public class WindBins
{
  private static final DatabaseReader dbReader = DatabaseReader.getInstance();
  private static final DatabaseCommon dbCommon = DatabaseCommon.getInstance();

  // This holds the days wind bins, one per wind direction that holds the total counts within the last hour.
  private final Map<WindDirection, Integer> dayBins = new HashMap<>();

  // This holds the last hour's worth of wind directions.
  private final LinkedList<WindDirection> hourReadings = new LinkedList<>();

  private static class SingletonHelper
  {
    private static final WindBins INSTANCE = new WindBins();
  }

  public static WindBins getInstance()
  {
    return SingletonHelper.INSTANCE;
  }

  private WindBins()
  {
    // Read current daily wind dir bins from day's summary record.
    for (WindDirection direction : WindDirection.values())
    {
      int value = dbReader.getDaysWindValue(direction);
      dayBins.put(direction, value);
    }

    // Read only the last hour's worth of readings in the current day.
    short lastDateTime = dbCommon.getLastDateStamp();
    int year = TimeUtil.getYear(lastDateTime);
    int month = TimeUtil.getMonth(lastDateTime);

    try
    {
      dbReader.readData(year, month, null);

      WeatherRecord nextRecord = dbReader.getNextDaysRecord();
      while (nextRecord != null)
      {
        if (hourReadings.size() < 13)
        {
          hourReadings.addFirst(nextRecord.getWindDirection());
        }
        else
        {
          hourReadings.addFirst(nextRecord.getWindDirection());
          hourReadings.removeLast();
        }
        nextRecord = dbReader.getNextDaysRecord();
      }
    }
    catch (IOException e)
    {
      System.out.println("Wind Bins not read.  I/O error.");
    }
  }

  public int getDayDirectionCount(WindDirection direction)
  {
    return dayBins.get(direction);
  }

  /**
   * This method adds a new direction to the hourly FIFO queue and the daily bins.
   *
   * @param newDirection The new 5 minute direction.
   */
  public void addObservation(WindDirection newDirection)
  {
    // Update daily observations.
    Integer count = dayBins.get(newDirection);
    dayBins.replace(newDirection, count + 1);

    // Update hourly observations.
    hourReadings.addFirst(newDirection);
    hourReadings.removeLast();
  }

  /**
   * Clear the wind bins.
   */
  public void clearDayBins()
  {
    for (WindDirection direction : WindDirection.values())
    {
      dayBins.put(direction, 0);
    }
  }

  /**
   * Method to get the number of readings in the last hour for the given direction.
   *
   * @param direction The direction to read.
   * @return The number of readings.
   */
  public int getHourlyReading(WindDirection direction)
  {
    int numberOfReadings = 0;
    for (Object object : hourReadings)
    {
      if (object == direction)
        numberOfReadings++;
    }
    return numberOfReadings;
  }

  /**
   * Method to get the dominant day's wind direction which is based on the direction bins contained within the
   * daily summary 2 record.
   *
   * @param record The second daily summary record.
   * @return The dominant wind direction value.
   */
  public static int[] getDaysDominantDirectionInfo(DailySummary2Record record)
  {
    int highValue = 0;
    WindDirection domDir = null;
    if (record.getNMinutes() > highValue)
    {
      highValue = record.getNMinutes();
      domDir = WindDirection.N;
    }
    if (record.getNneMinutes() > highValue)
    {
      highValue = record.getNneMinutes();
      domDir = WindDirection.NNE;
    }
    if (record.getNeMinutes() > highValue)
    {
      highValue = record.getNeMinutes();
      domDir = WindDirection.NE;
    }
    if (record.getEneMinutes() > highValue)
    {
      highValue = record.getEneMinutes();
      domDir = WindDirection.ENE;
    }
    if (record.getEMinutes() > highValue)
    {
      highValue = record.getEMinutes();
      domDir = WindDirection.E;
    }
    if (record.getEseMinutes() > highValue)
    {
      highValue = record.getEseMinutes();
      domDir = WindDirection.ESE;
    }
    if (record.getSeMinutes() > highValue)
    {
      highValue = record.getSeMinutes();
      domDir = WindDirection.SE;
    }
    if (record.getSseMinutes() > highValue)
    {
      highValue = record.getSseMinutes();
      domDir = WindDirection.SSE;
    }
    if (record.getSMinutes() > highValue)
    {
      highValue = record.getSMinutes();
      domDir = WindDirection.S;
    }
    if (record.getSswMinutes() > highValue)
    {
      highValue = record.getSswMinutes();
      domDir = WindDirection.SSW;
    }
    if (record.getSwMinutes() > highValue)
    {
      highValue = record.getSwMinutes();
      domDir = WindDirection.SW;
    }
    if (record.getWswMinutes() > highValue)
    {
      highValue = record.getWswMinutes();
      domDir = WindDirection.WSW;
    }
    if (record.getWMinutes() > highValue)
    {
      highValue = record.getWMinutes();
      domDir = WindDirection.W;
    }
    if (record.getWnwMinutes() > highValue)
    {
      highValue = record.getWnwMinutes();
      domDir = WindDirection.WNW;
    }
    if (record.getNwMinutes() > highValue)
    {
      highValue = record.getNwMinutes();
      domDir = WindDirection.NW;
    }
    if (record.getNnwMinutes() > highValue)
    {
      domDir = WindDirection.NNW;
    }
    int[] info = new int[2];
    if (domDir != null)
    {
      info[0] = domDir.value();
      info[1] = highValue;
    }
    return info;
  }
}
