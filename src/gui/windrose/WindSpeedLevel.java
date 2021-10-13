/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This enumeration defines the wind speed levels for the wind rose.

  Mods:		  10/13/21  Initial Release.
*/
package gui.windrose;

public enum WindSpeedLevel
{
  zeroToFive(0, "0-5"),
  fiveToTen(1, "5-10"),
  tenToFifteen(2, "10-15"),
  fifteenToTwenty(3, "15-20"),
  twentyToThirty(4, "20-30"),
  thirtyToForty(5, "30-40");

  private final int value;
  private final String label;
  public static final int numOfLevels = 6;

  WindSpeedLevel(int value, String label)
  {
    this.value = value;
    this.label = label;
  }

  /**
   * Method to get the value of the wind speed level.  This is the first number defined, an index number
   * if you will.
   *
   * @return The value.
   */
  public int value()
  {
    return this.value;
  }

  /**
   * Method to get the label sting value of the wind speed level.  This is the string value defined above.
   *
   * @return The label string.
   */
  public String label()
  {
    return this.label;
  }

  /**
   * Method to get a specific wind speed level based on a given wind speed.
   *
   * @param speed The specific wind speed.
   * @return The enumeration the wind is contained within.
   */
  public static WindSpeedLevel getWindSpeedLevel(float speed)
  {
    if (speed < 5.0)
      return WindSpeedLevel.zeroToFive;
    else if (speed < 10.0)
      return WindSpeedLevel.fiveToTen;
    else if (speed < 15.0)
      return WindSpeedLevel.tenToFifteen;
    else if (speed < 20.0)
      return WindSpeedLevel.fifteenToTwenty;
    else if (speed < 30.0)
      return WindSpeedLevel.twentyToThirty;
    else
      return WindSpeedLevel.thirtyToForty;
  }
}
