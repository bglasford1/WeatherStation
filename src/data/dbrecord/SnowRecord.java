/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that encapsulates the snow record.  This data mainly supports
            the generation of graphs and strip charts.  The original Davis program
            does not capture/record snow totals.  This data is hand captured and
            input by hand.  A snow record contains a date stamp (day/month/year)
            and a snow total amount for that day.

  Mods:		  09/01/21 Initial Release.
*/
package data.dbrecord;

public class SnowRecord
{
  private int year;
  private int month;
  private int day;
  private float amount;

  public int getYear()
  {
    return year;
  }

  public void setYear(int year)
  {
    this.year = year;
  }

  public int getMonth()
  {
    return month;
  }

  public void setMonth(int month)
  {
    this.month = month;
  }

  public int getDay()
  {
    return day;
  }

  public void setDay(int day)
  {
    this.day = day;
  }

  public float getAmount()
  {
    return amount;
  }

  public void setAmount(float amount)
  {
    this.amount = amount;
  }

  /**
   * Return the string as written to the database file.
   *
   * @return The database line value.
   */
  @Override
  public String toString()
  {
    return getYear() + "," + getMonth() + "," + getDay() + "," + getAmount();
  }
}
