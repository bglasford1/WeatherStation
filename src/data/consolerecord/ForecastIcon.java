/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This enumeration lists the available forecast icons and their
            associated filenames.

  Mods:		  09/01/21 Initial Release.
*/
package data.consolerecord;

public enum ForecastIcon
{
  UNKNOWN("wxicons/wxIcon_99.png"),
  SUNNY("wxicons/wxIcon_8.png"),
  PARTLY_CLOUDY("wxicons/wxIcon_6.png"),
  CLOUDY("wxicons/wxIcon_2.png"),
  CLOUDY_RAIN("wxicons/wxIcon_3.png"),
  CLOUDY_SNOW("wxicons/wxIcon_18.png"),
  CLOUDY_RAIN_SNOW("wxicons/wxIcon_19.png"),
  PARTLY_CLOUDY_RAIN("wxicons/wxIcon_7.png"),
  PARTLY_CLOUDY_SNOW("wxicons/wxIcon_22.png"),
  PARTLY_CLOUDY_RAIN_SNOW("wxicons/wxIcon_23.png");

  private final String filename; // The filename of the icon.

  /**
   * The constructor.
   * @param filename The text to display.
   */
  ForecastIcon(String filename)
  {
    this.filename = filename;
  }

  public String getFilename()
  {
    return filename;
  }
}
