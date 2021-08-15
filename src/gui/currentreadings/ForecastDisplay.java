/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for displaying the forecast rules graphic.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import data.consolerecord.ForecastIcon;
import data.consolerecord.ForecastRule;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

class ForecastDisplay
{
  private int forecastRule = 192; // This is set for debug purposes.

  /**
   * This method is called to draw the forecastRule graphic at the position given.
   *
   * @param component The parent component.
   * @param g2 The Graphics component.
   * @param x The starting X value.
   * @param y The starting Y value.
   */
  void paintGauge (Component component, Graphics2D g2, int x, int y)
  {
    // Change icon from rain to snow depending on the month.
    ForecastIcon forecastIcon = ForecastRule.getForecastRule(forecastRule).getIcon();
    int month = LocalDate.now().getMonthValue();
    if (month >= 10 || month <= 3)
    {
      if (forecastIcon.equals(ForecastIcon.CLOUDY_RAIN))
        forecastIcon = ForecastIcon.CLOUDY_SNOW;
      else if (forecastIcon.equals(ForecastIcon.PARTLY_CLOUDY_RAIN))
        forecastIcon = ForecastIcon.PARTLY_CLOUDY_SNOW;
    }
    else if (month == 9 || month == 4)
    {
      if (forecastIcon.equals(ForecastIcon.CLOUDY_RAIN))
        forecastIcon = ForecastIcon.CLOUDY_RAIN_SNOW;
      else if (forecastIcon.equals(ForecastIcon.PARTLY_CLOUDY_RAIN))
        forecastIcon = ForecastIcon.PARTLY_CLOUDY_RAIN_SNOW;
    }

    ImageIcon icon = new ImageIcon(forecastIcon.getFilename());
    icon.paintIcon(component, g2, x + 10, y + 30);

    String forecastString = ForecastRule.getForecastRule(forecastRule).toString();
    g2.setStroke(GaugeCommon.STROKE);
    g2.setFont(GaugeCommon.PLAIN_FONT);

    // Split string into ~40 character chunks.
    String[] wordArray = forecastString.split(" ");
    StringBuilder string1 = new StringBuilder();
    StringBuilder string2 = new StringBuilder();
    StringBuilder string3 = new StringBuilder();
    boolean string1Full = false;
    boolean string2Full = false;
    int maxLength = 72;
    for (String nextWord : wordArray)
    {
      if (!string1Full &&(string1.length() + nextWord.length()) < maxLength)
      {
        string1.append(nextWord).append(" ");
      }
      else if (!string2Full && (string2.length() + nextWord.length()) < maxLength)
      {
        string1Full = true;
        string2.append(nextWord).append(" ");
      }
      else if ((string3.length() + nextWord.length()) < maxLength)
      {
        string2Full = true;
        string3.append(nextWord).append(" ");
      }
    }

    if (string2.length() == 0)
    {
      g2.drawString(string1.toString(), x + 80, y + 25);
    }
    else
    {
      g2.drawString(string1.toString(), x + 80, y + 13);
      g2.drawString(string2.toString(), x + 80, y + 25);
      g2.drawString(string3.toString(), x + 80, y + 37);
    }
  }

  /**
   * Set the current forecastRule value.
   *
   * @param forecastRule The forecastRule byte.
   */
  public void setForecastRule(int forecastRule)
  {
    this.forecastRule = forecastRule;
  }
}
