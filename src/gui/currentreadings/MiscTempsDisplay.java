/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class responsible for displaying the various temperature values
            as well as the solar radiation value.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import algorithms.Calculations;

import java.awt.*;
import java.text.DecimalFormat;

class MiscTempsDisplay
{
  private String heatIndex = "--- F";
  private String windChill = "--- F";
  private String dewPoint  = "--- F";
  private String thw       = "--- F";
  private String thsw      = "--- F";

  private final DecimalFormat df = new DecimalFormat("##.##");

  /**
   * This method is called to draw the various values on the graphic at the position given.  This position is
   * at the upper right hand corner of the graphic.
   */
  void paintGauge (Graphics2D g2, int x, int y)
  {
    // Set up to draw the times.
    g2.setStroke(GaugeCommon.STROKE);
    g2.setFont(GaugeCommon.PLAIN_FONT);
    FontMetrics metrics = g2.getFontMetrics();
    int height = metrics.getHeight();

    // Add the header.
    g2.setFont(GaugeCommon.BOLD_FONT);
    String headerString = "Misc Temps:";
    g2.drawString(headerString, x, y + height);
    g2.setFont(GaugeCommon.PLAIN_FONT);

    // Add the wind chill.
    String windChillString = " Wind Chill:";
    g2.drawString(windChillString, x, y + height * 2);
    g2.drawString(windChill, x + 80, y + height * 2);

    // Add the heat index.
    String heatIndexString = " Heat Index:";
    g2.drawString(heatIndexString, x, y + height * 3);
    g2.drawString(heatIndex, x + 80, y + height * 3);

    // Add the dew point.
    String dewPointString = " Dew Point:";
    g2.drawString(dewPointString, x, y + height * 4);
    g2.drawString(dewPoint, x + 80, y + height * 4);

    // Add the THW temperature.
    String thwString = " THW:";
    g2.drawString(thwString, x, y + height * 5);
    g2.drawString(thw, x + 80, y + height * 5);

    // Add the THSW temperature.
    String thswString = " THSW:";
    g2.drawString(thswString, x, y + height * 6);
    g2.drawString(thsw, x + 80, y + height * 6);
  }

  void setValues (float temperature, byte humidity, byte windSpeed, short solarRad)
  {
    windChill = df.format(Calculations.calculateWindChill(temperature, windSpeed)) + "\u00B0" + "F";
    heatIndex = df.format(Calculations.calculateHeatIndex(temperature, humidity)) + "\u00B0" + "F";
    dewPoint  = df.format(Calculations.calculateDewPoint (temperature, humidity)) + "\u00B0" + "F";
    thw       = df.format(Calculations.calculateTHW(temperature, windSpeed, humidity)) + "\u00B0" + "F";
    thsw      = df.format(Calculations.calculateTHSW(temperature, windSpeed, humidity, solarRad)) + "\u00B0" + "F";
  }
}
