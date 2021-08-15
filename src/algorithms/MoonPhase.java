/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class used to calculate the moon phases.

  Mods:		  09/01/21 Initial Release.
*/
package algorithms;

public class MoonPhase
{
  public static MoonPhaseName getPhaseName(int phase)
  {
    if (phase == 0 || phase == 30)
      return MoonPhaseName.NEW_MOON;
    else if (phase >= 1 && phase <= 6)
      return MoonPhaseName.WAXING_CRESCENT;
    else if (phase == 7)
      return MoonPhaseName.FIRST_QUARTER;
    else if (phase >= 8 && phase <= 13)
      return MoonPhaseName.WAXING_GIBBOUS;
    else if (phase == 14)
      return MoonPhaseName.FULL_MOON;
    else if (phase >= 15 && phase <= 21)
      return MoonPhaseName.WANING_GIBBOUS;
    else if (phase == 22)
      return MoonPhaseName.LAST_QUARTER;
    else if (phase >= 23 && phase <= 29)
      return MoonPhaseName.WANING_CRESCENT;
    else
      return null;
  }
}
