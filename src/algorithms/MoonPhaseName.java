/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Enumeration that defines the moon phase names.

  Mods:		  09/01/21 Initial Release.
*/
package algorithms;

public enum MoonPhaseName
{
  NEW_MOON("New Moon"),
  WAXING_CRESCENT("Waxing Crescent"),
  FIRST_QUARTER("First Quarter"),
  WAXING_GIBBOUS("Waxing Gibbous"),
  FULL_MOON("Full Moon"),
  WANING_GIBBOUS("Waning Gibbous"),
  LAST_QUARTER("Last Quarter"),
  WANING_CRESCENT("Waning Crescent");

  private final String name; // The moon phase name.

  MoonPhaseName(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }
}
