/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that defines the NOAA Forecast strings.

  Mods:		  10/11/21  Initial Release.
*/
package forecast;

public enum  NOAAForecast
{
  Sunny,
  Clear, // Used at night
  MostlySunny,
  MostlyClear, // Used at night
  PartlyCloudy,
  MostlyCloudy,
  SlightChanceRainShowers,
  SlightChanceSnowShowers,
  SlightChanceRainAndSnowShowers,
  ChanceRainShowers,
  ChanceSnowShowers,
  ChanceRainAndSnowShowers;
}
