/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class generates and holds the data for the wind rose.

  Mods:		  10/13/21  Initial Release.
*/
package data.dbrecord;

import dbif.DatabaseReader;
import gui.graph.GraphDefs;
import gui.windrose.WindSpeedLevel;

import java.io.IOException;
import java.time.LocalDateTime;

public class WindRoseData
{
  private static final DatabaseReader dbReader = DatabaseReader.getInstance();

  private static final int numOfSlices = 16;
  private WindSlice[] slices = new WindSlice[numOfSlices];
  private int dataPoints = 0;
  private int numOfCalmPoints = 0;

  /**
   * Constructor that creates the WindSlice objects.
   */
  public WindRoseData()
  {
    for (WindDirection nextDirection : WindDirection.values())
    {
      slices[nextDirection.value()] = new WindSlice();
    }
  }

  /**
   * Method that returns the number of slices.
   *
   * @return The number of slices
   */
  public int getNumOfSlices()
  {
    return numOfSlices;
  }

  /**
   * Method to get a specific WindSlice object.
   *
   * @param sliceNumber The number of the slice, based on wind direction.
   * @return The WindSlice object.
   */
  public WindSlice getSlice(int sliceNumber)
  {
    return slices[sliceNumber];
  }

  /**
   * Method to get the percentage of time the wind was calm.
   *
   * @return The calm percentage.
   */
  public double getCalmSlicePercentage()
  {
    if (numOfCalmPoints == 0)
      return 0.0;
    else
      return ((double)numOfCalmPoints/(double)dataPoints) / 100.0;
  }

  /**
   * Method that generates the data to display on the wind rose.  The time period is counted back from time now to
   * determine the time to start retrieving data.  The data is parsed into 16 separate slices and into 5 mph bins
   * within the slices.
   *
   * @param timePeriod The time period as Hour, Half Day, Day, Half Week and Week.
   */
  public void generateData(String timePeriod)
  {
    // Zero the data.
    dataPoints = 0;
    numOfCalmPoints = 0;
    for (WindSlice nextSlice : slices)
      nextSlice.zeroData();

    // Determine the start date/time.
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate;
    if (timePeriod.equalsIgnoreCase(GraphDefs.HOUR_STRING))
      startDate = endDate.minusHours(1);
    else if (timePeriod.equalsIgnoreCase(GraphDefs.HALF_DAY_STRING))
      startDate = endDate.minusHours(12);
    else if (timePeriod.equalsIgnoreCase(GraphDefs.DAY_STRING))
      startDate = endDate.minusDays(1);
    else if (timePeriod.equalsIgnoreCase(GraphDefs.HALF_WEEK_STRING))
      startDate = endDate.minusDays(3);
    else if (timePeriod.equalsIgnoreCase(GraphDefs.WEEK_STRING))
      startDate = endDate.minusWeeks(1);
    else
      return;

    // The start of the data values are in this file.
    try
    {
      setupDBRead(startDate.getYear(), startDate.getMonthValue());
    }
    catch (IOException e)
    {
      System.out.println("Invalid input data for Wind Rose...");
      return;
    }

    // Extract the data.
    boolean startFound = false;
    DataFileRecord nextRecord = dbReader.getNextRecord();
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
            nextRecord = dbReader.getNextRecord();
            continue;
          }
        }

        // Put the wind data into the appropriate slice's bin.
        dataPoints++;
        float windSpeed = record.getAverageWindSpeed();
        WindDirection windDirection = record.getWindDirection();

        WindSpeedLevel windSpeedLevel = WindSpeedLevel.getWindSpeedLevel(windSpeed);
        if (windSpeed == 0.0)
          numOfCalmPoints++;
        else
          slices[windDirection.value()].incrementBinCount(windSpeedLevel.value());
      }
      nextRecord = dbReader.getNextRecord();
    }

    // If the data crosses the month boundary then there is another file's worth of data to read.
    if (startDate.getYear() != endDate.getYear() || startDate.getMonthValue() != endDate.getMonthValue())
    {
      try
      {
        setupDBRead(endDate.getYear(), endDate.getMonthValue());
      }
      catch (IOException e)
      {
        System.out.println("Invalid input data for Wind Rose...");
        return;
      }

      nextRecord = dbReader.getNextRecord();
      while (nextRecord != null)
      {
        if (nextRecord instanceof WeatherRecord)
        {
          WeatherRecord record = (WeatherRecord) nextRecord;

          // Put the wind data into the appropriate slice's bin.
          dataPoints++;
          float windSpeed = record.getAverageWindSpeed();
          WindDirection windDirection = record.getWindDirection();

          WindSpeedLevel windSpeedLevel = WindSpeedLevel.getWindSpeedLevel(windSpeed);
          if (windSpeed == 0.0)
            numOfCalmPoints++;
          else
            slices[windDirection.value()].incrementBinCount(windSpeedLevel.value());
        }
        nextRecord = dbReader.getNextRecord();
      }
    }

    // Loop through all slice bins, calculating percentages.
    for (WindSlice nextSlice : slices)
    {
      nextSlice.calculatePercentages(dataPoints);
    }
  }

  /**
   * Internal method that sets up the database reader to read a specific year/month file.
   *
   * @param year The year of the DB file.
   * @param month The month of the DB file.
   * @throws IOException Thrown if a file this year/month does not exist.
   */
  private void setupDBRead(int year, int month) throws IOException
  {
    dbReader.readData(year, month, dbReader.getFilename(year, month));
    dbReader.reset();
  }
}
