/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Enumeration that defines the pressure trends.

  Mods:		  09/01/21 Initial Release.
*/
package data.consolerecord;

public enum PressureTrend
{
  P(80),
  RISING_RAPIDLY(60),
  RISING_SLOWLY(20),
  STEADY(0),
  FALLING_SLOWLY(-20),
  FALLING_RAPIDLY(-60);

  private final int value; // The value sent from the console within the loop command.

  PressureTrend(int value)
  {
    this.value = value;
  }

  public int value()
  {
    return this.value;
  }
}
