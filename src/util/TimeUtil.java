/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class encapsulates the time functions.  Davis time takes on
            the form of a date stamp and time stamp that are retrieved from
            the DMP data.  The date stamp contains the year/month/day values
            and the time stamp contains the hour/minute values.  The time used
            in the database records contain a packed time which is the number
            of intervals from midnight for a given day.

  Mods:		  09/01/21 Initial Release.
*/
package util;

public class TimeUtil
{
  /**
   * Method to generate the datestamp and timestamp value expected by the console.
   *
   * @param day The day value
   * @param month The month value
   * @param year The year value
   * @param hour The hour value
   * @param minute The minute value
   * @return An array of 4 bytes to send to the console.
   */
  public static byte[] getDateTimestamp(int day, int month, int year, int hour, int minute)
  {
    short dateStamp = getDateStamp(day, month, year);
    short timestamp = getTimestamp(hour, minute);

    byte[] dateTimeStamp = new byte[6];
    dateTimeStamp[0] = (byte)(dateStamp & 0xFF);
    dateTimeStamp[1] = (byte)((dateStamp >> 8) & 0xFF);
    dateTimeStamp[2] = (byte)(timestamp & 0xff);
    dateTimeStamp[3] = (byte)((timestamp >> 8) & 0xFF);
    return dateTimeStamp;
  }

  public static byte[] getDateTimestamp(short dateStamp, short timestamp)
  {
    byte[] dateTimeStamp = new byte[6];
    dateTimeStamp[0] = (byte)(dateStamp & 0xFF);
    dateTimeStamp[1] = (byte)((dateStamp >> 8) & 0xFF);
    dateTimeStamp[2] = (byte)(timestamp & 0xff);
    dateTimeStamp[3] = (byte)((timestamp >> 8) & 0xFF);
    return dateTimeStamp;
  }

  /**
   * Method to generate the date stamp expected by the console based on the day, month and year values.
   *
   * @param day The day value
   * @param month The month value
   * @param year The year value
   * @return The short value of the date stamp.
   */
  public static short getDateStamp(int day, int month, int year)
  {
    return (short)(day + month * 32 + (year - 2000) * 512);
  }

  /**
   * Method to get the timestamp expected by the console based on the hour and minute values.
   *
   * @param hour The hour of the day.
   * @param minute The minute of the day.
   * @return The timestamp.
   */
  public static short getTimestamp(int hour, int minute)
  {
    return (short)(hour * 100 + minute);
  }

  /**
   * Method to get the day contained within a date stamp.
   *
   * @param dateStamp The datestamp value.
   * @return The day portion of the datestamp
   */
  public static int getDay(short dateStamp)
  {
    return dateStamp - (getMonth(dateStamp) * 32) - ((getYear(dateStamp) - 2000) * 512);
  }

  /**
   * Method to get the month contained within a date stamp.
   *
   * @param dateStamp The datestamp value.
   * @return The month portion of the datestamp
   */
  public static int getMonth(short dateStamp)
  {
    return (dateStamp - ((getYear(dateStamp) - 2000) * 512)) / 32;
  }

  /**
   * Method to get the year contained within a date stamp.
   *
   * @param dateStamp The datestamp value.
   * @return The year portion of the datestamp
   */
  public static int getYear(short dateStamp)
  {
    return (int)(dateStamp / 512f + 2000f);
  }

  /**
   * Method to get the year contained within a database filename.
   *
   * @param filename The filename
   * @return The integer year value.
   */
  public static int getYear(String filename)
  {
    return Integer.parseInt(filename.substring(0, 4));
  }

  /**
   * Method to get the month contained within a database filename.
   *
   * @param filename The filename.
   * @return The integer month value.
   */
  public static int getMonth(String filename)
  {
    return Integer.parseInt(filename.substring(5, 7));
  }

  /**
   * Method to get the hour value contained within the timestamp returned by the console.
   *
   * @param timestamp The internal Davis timestamp
   * @return The hour value
   */
  public static int getHour(short timestamp)
  {
    return timestamp / 100;
  }

  /**
   * Method to get the minute value contained within the timestamp returned by the console.
   *
   * @param timestamp The internal Davis timestamp
   * @return The minute value
   */
  public static int getMinute(short timestamp)
  {
    return timestamp - getHour(timestamp) * 100;
  }

  /**
   * Method to get the packed time based on the hour and minute values.
   *
   * @param hour The hour value
   * @param minute The minute value
   * @return The Davis timestamp
   */
  public static short getPackedTime(int hour, int minute)
  {
    return (short)(minute + hour * 60);
  }

  /**
   * Method to convert from the timestamp value retrieved from the DMP data to the packed time required
   * by the weather database value.  The DB time is the number of minutes past midnight.
   *
   * @param timestamp The internal Davis timestamp
   * @return The packed time value written to the Davis DB flat files.
   */
  public static short getPackedTime(short timestamp)
  {
    return (short)(getHour(timestamp) * 60 + getMinute(timestamp));
  }

  /**
   * Method to convert from a packed time as written to the Davis DB WeatherRecord files to a timestamp that is
   * required by the Davis console.
   *
   * @param packedTime The packed time from a DB weather record.
   * @return The Davis timestamp.
   */
  public static short convertPackedTimeToTimestamp(short packedTime)
  {
    int hour = packedTime / 60;
    int min = packedTime - (60 * hour);
    return getTimestamp(hour, min);
  }

  /**
   * Method to convert a packed time to a string representation.
   *
   * @param packedTime The packed time to convert.
   * @return A string representation of the time.
   */
  public static String toString(short packedTime)
  {
    int hour = packedTime / 60;
    int min = packedTime - (60 * hour);
    String adder = " am";

    if (hour > 12)
    {
      hour = hour - 12;
      adder = " pm";
    }
    else if (hour == 0)
    {
      hour = 12;
    }

    String hourString;
    if (hour < 10)
      hourString = " " + hour;
    else
      hourString = Integer.toString(hour);

    String minString;
    if (min < 10)
      minString = "0" + min;
    else
      minString = Integer.toString(min);

    return hourString + ":" + minString + adder;
  }

  public static void main(String[] args)
  {
    short dateStamp = TimeUtil.getDateStamp(5, 2, 1960);
    System.out.println("Day : " + TimeUtil.getDay(dateStamp));
    System.out.println("Month : " + TimeUtil.getMonth(dateStamp));
    System.out.println("Year : " + TimeUtil.getYear(dateStamp));

    short packedTime = TimeUtil.getPackedTime(4, 25);
    System.out.println("\nPacked Time: " + packedTime);

    short timeStamp = convertPackedTimeToTimestamp(packedTime);
    System.out.println("\nTimestamp: " + timeStamp);
    System.out.println("Hour : " + getHour(timeStamp));
    System.out.println("Min : " + getMinute(timeStamp));

    short packedTime1 = getPackedTime(timeStamp);
    System.out.println("\nPacked Time: " + packedTime1);
  }
}
