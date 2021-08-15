/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class responsible for drawing the solar gauge.  It displays the
            gauge in a graphical representation along with min/max readings
            plus the rate of change.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

class SolarGauge
{
  private float currentSolar = 0f;
  private float maximumSolar = 0f;

  /**
   * This method is called to draw the solar graphic at the position
   * given.  This position is at the center top of the graphic.
   */
  void paintGauge(Graphics2D g2, int x, int y)
  {
    // Offset the thermometer.
    int yTherm = y + 38;

    // Draw the solar graphic.
    int rectWidth  = 8;
    int rectHeight = 200;
    g2.setStroke(GaugeCommon.STROKE);
    g2.draw(new Rectangle2D.Float(x, yTherm, rectWidth, rectHeight));

    // Fill in the current solar.
    float currentHeight = currentSolar / 1400 * (float)rectHeight;
    float heightOffset  = (float)rectHeight - currentHeight;
    int   newY = yTherm + (int)heightOffset;
    g2.setPaint(Color.ORANGE);
    g2.fill(new Rectangle2D.Float(x, newY, rectWidth, (int)currentHeight));
    g2.setPaint(GaugeCommon.FG_COLOR);

    // Add the 10 degree tick marks.
    // The marks will go from 0 degrees to 1000 degrees.
    int yOffset;
    int i;
    for (i = 0; i < 15; i++)
    {
      yOffset = yTherm + i * rectHeight/14;
      g2.draw(new Line2D.Float(x - 5, yOffset, x, yOffset));
    }

    // Add the intermediate 5 degree tick marks.
    for (i = 0; i < 14; i++)
    {
      yOffset = yTherm + 8 + i * rectHeight/14;
      g2.draw(new Line2D.Float(x - 3, yOffset, x, yOffset));
    }

    // Add degree labels.
    String labelString;
    for (i = 0; i < 15; i++)
    {
      // Only draw every other label.
      if ((i % 2) == 0)
      {
        switch (i)
        {
          case 0:
          case 2:
          case 4:
            labelString = "" + (1400 - i * 100);
            break;
          case 14:
            labelString = "     " + (1400 - i * 100);
            break;
          default:
            labelString = " " + (1400 - i * 100);
            break;
        }
        yOffset = yTherm + 5 + i * rectHeight/14;
        g2.drawString(labelString, x - 40, yOffset);
      }
    }

    // Add the text banner.
    FontMetrics metrics = g2.getFontMetrics();
    int height = metrics.getHeight();
    g2.setFont(GaugeCommon.BOLD_FONT);
    String titleString = "Solar";
    int width = metrics.stringWidth(titleString);
    g2.drawString(titleString, x - width/2, y + height + 2 - height/2);

    // Add current value.
    String currentString = currentSolar + " W/m2";
    width = metrics.stringWidth(currentString);
    g2.drawString(currentString, x - width/2, y + height + 12);
    g2.setFont(GaugeCommon.PLAIN_FONT);

    // Add the maximum temperature arrow.
    currentHeight = maximumSolar / 1400 * (float)rectHeight;
    heightOffset  = (float)rectHeight - currentHeight;
    yOffset       = yTherm + (int)heightOffset;
    int newX      = x + rectWidth;
    g2.draw(new Line2D.Float(newX, yOffset, newX + 12, yOffset));
    g2.draw(new Line2D.Float(newX, yOffset, newX + 2, yOffset + 2));
    g2.draw(new Line2D.Float(newX, yOffset, newX + 2, yOffset - 2));
    g2.setStroke(GaugeCommon.STROKE);
    String maxString = Float.toString(maximumSolar);
    g2.drawString(maxString, newX + 14, yOffset + 5);
  }

  /**
   * Method called to set a new current solar value.
   */
  public void setCurrent(float solar)
  {
    this.currentSolar = solar;

    if (solar > maximumSolar)
      maximumSolar = solar;
  }

  /**
   * Method called to set a new maximum solar value.
   */
  public void setMaximum (float solar)
  {
    this.maximumSolar = solar;
  }
}
