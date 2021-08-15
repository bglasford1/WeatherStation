/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class responsible for drawing the rain gauge on the screen.
            It is passed the minMaxInterval due to the complexity of the
            initial setup.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import java.awt.*;

class RainGauge
{
  private float  dailyAmount   = 0f;
  private float  stormAmount   = 0f;
  private float  monthlyAmount = 0f;
  private float  yearlyAmount  = 0f;
  private float  rate          = 0f;
  private String stormStart    = "mmm/dd/yyyy";
  private String lastRain      = "mmm/dd/yyyy";

  /**
   * This method is called to draw the rain graphic at the position
   * given.  This position is at the center top of the graphic.
   */
  void paintGauge (Graphics2D g2, int x, int y)
  {
    FontMetrics metrics = g2.getFontMetrics();
    int height = metrics.getHeight();

    // Add the text banner.
    g2.setFont(GaugeCommon.BOLD_FONT);
    String string1 = "Rain:";
    g2.drawString(string1, x, y);
    g2.setFont(GaugeCommon.PLAIN_FONT);

    // Add current value.
    String dayString = "Day Amount:     " + dailyAmount + "\"";
    g2.drawString(dayString, x, y + height);

    // Add rate value.
    String rateString = "Rate: (\"/hour)    " + rate;
    g2.drawString(rateString, x, y + height * 2);

    // Add month value.
    String monthString = "Month Amount:  " + monthlyAmount + "\"";
    g2.drawString(monthString, x, y + height * 3);

    // Add current value.
    String yearString = "Yearly Amount:  " + yearlyAmount + "\"";
    g2.drawString(yearString, x, y + height * 4);

    // Add storm value.
    String stormString = "Storm Amount:  " + stormAmount + "\"";
    g2.drawString(stormString, x, y + height * 5);

    // Add start of storm start date.
    String stormStartString = "Storm Start: " + stormStart;
    g2.drawString(stormStartString, x, y + height * 6);

    // Add start of last rain date.
    String lastRainString = "Last Rain:     " + lastRain;
    g2.drawString(lastRainString, x, y + height * 7);
  }

  /**
   * Method called to set a new daily rain amount.
   */
  void setDailyAmount(float dailyAmount)
  {
    this.dailyAmount = dailyAmount;
  }

  /**
   * Method called to set a new storm rain amount.
   */
  void setStormAmount (float stormAmount)
  {
    this.stormAmount = stormAmount;
  }

  /**
   * Method called to set a new monthly rain amount.
   */
  void setMonthlyAmount (float monthlyAmount)
  {
    this.monthlyAmount = monthlyAmount;
  }

  /**
   * Method called to set a new yearly rain amount.
   */
  void setYearlyAmount (float yearlyAmount)
  {
    this.yearlyAmount = yearlyAmount;
  }

  /**
   * Method called to set a new rain rate.
   */
  public void setRate (float rate)
  {
    this.rate = rate;
  }

  /**
   * Method called to set a new start of storm date.
   */
  void setStormDate (String date)
  {
    this.stormStart = date;
  }

  /**
   * Method called to set a new last rain date.
   */
  void setLastRain (String date)
  {
    this.lastRain = date;
  }
}
