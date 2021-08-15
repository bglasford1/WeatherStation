/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class responsible for displaying the temperature gauge on the screen.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

class TemperatureGauge
{
  // Display temps.
  private float  currentTemp = 0f;
  private float  minimumTemp = 0f;
  private float  maximumTemp = 0f;

  // Scale temps and labels.  These are set at creation.
  private float minScaleTemp;
  private float maxScaleTemp;
  private final String label;

  // Constants
  private static final int RECT_WIDTH = 8;
  private static final int RECT_HEIGHT = 200;
  private final float deltaTemp;
  private final float ticksPerDegree;

  /**
   * Constructor that sets constants.
   *
   * @param nameLabel Name of the gauge.
   * @param minTemp   Min temperature value. Must be divisible by 10.
   * @param maxTemp   Max temperature value. Must be divisible by 10.
   */
  TemperatureGauge (String nameLabel, int minTemp, int maxTemp)
  {
    this.label = nameLabel;
    this.minScaleTemp = minTemp;
    this.maxScaleTemp = maxTemp;
    deltaTemp = maxTemp - minTemp;
    ticksPerDegree = RECT_HEIGHT / deltaTemp;
  }

  /**
   * This method is called to draw the thermometer graphic at the position given.  This position is at the
   * center top of the graphic.  For graphics, plus is downward for y values!!!
   */
  void paintGauge (Graphics2D g2, int x, int y)
  {
    // Offset the thermometer.
    int yTherm = y + 38;

    // Draw the thermometer gauge rectangle.  The x & yTherm represent the upper left corner of the graphic.
    g2.setPaint(GaugeCommon.FG_COLOR);
    g2.setStroke(GaugeCommon.STROKE);
    g2.draw(new Rectangle2D.Float(x, yTherm, RECT_WIDTH, RECT_HEIGHT));

    // Fill in the current temperature.
    float currentHeight = (currentTemp - minScaleTemp) * ticksPerDegree;
    float heightOffset  = (float)RECT_HEIGHT - currentHeight;
    int   newY = yTherm + (int)heightOffset;
    g2.setPaint(Color.RED);
    g2.fill(new Rectangle2D.Float(x, newY, RECT_WIDTH, (int)currentHeight));
    g2.setPaint(GaugeCommon.FG_COLOR);

    // Add the degree tick marks from the top down.
    // The marks will go from minTemp degrees to maxTemp degrees.
    int numberOfTicks = (int)(deltaTemp / 10) + 1;
    int yOffset;
    int i;
    for (i = 0; i < numberOfTicks; i++)
    {
      yOffset = yTherm + i * RECT_HEIGHT / (numberOfTicks - 1);
      g2.draw(new Line2D.Float(x - 5, yOffset, x, yOffset));
    }

    // Add degree labels.
    String labelString;
    for (i = 0; i < numberOfTicks; i++)
    {
      int temp = (int)(maxScaleTemp - i * 10);
      int length = String.valueOf(temp).length();
      switch (length)
      {
        case 1:
          labelString = "  " + temp;
          break;
        case 2:
          labelString = " " + temp;
          break;
        default: // case 3:
          labelString = "" + temp;
          break;
      }
      yOffset = yTherm + 5 + i * RECT_HEIGHT / (numberOfTicks - 1);
      g2.drawString(labelString, x - 30, yOffset);
    }

    // Add the intermediate degree tick marks from the top down.
    numberOfTicks = numberOfTicks - 1;
    for (i = 0; i < numberOfTicks; i++)
    {
      yOffset = yTherm + (RECT_HEIGHT / (numberOfTicks * 2)) + i * RECT_HEIGHT / numberOfTicks;
      g2.draw(new Line2D.Float(x - 3, yOffset, x, yOffset));
    }

    // Add the minimum temperature arrow.
    currentHeight = (minimumTemp - minScaleTemp) * ticksPerDegree;
    heightOffset  = (float)RECT_HEIGHT - currentHeight;
    yOffset       = yTherm + (int)heightOffset;
    int newX      = x + RECT_WIDTH;
    g2.draw(new Line2D.Float(newX, yOffset, newX + 6, yOffset + 6));
    g2.draw(new Line2D.Float(newX + 6, yOffset + 6, newX + 12, yOffset + 6));
    g2.draw(new Line2D.Float(newX, yOffset, newX + 2, yOffset - 1));
    String minString = Float.toString(minimumTemp);
    g2.drawString(minString, newX + 14, yOffset + 11);

    // Add the maximum temperature arrow.
    currentHeight = (maximumTemp - minScaleTemp) * ticksPerDegree;
    heightOffset  = (float)RECT_HEIGHT - currentHeight;
    yOffset       = yTherm + (int)heightOffset;
    newX          = x + RECT_WIDTH;
    g2.draw(new Line2D.Float(newX, yOffset, newX + 6, yOffset - 6));
    g2.draw(new Line2D.Float(newX + 6, yOffset - 6, newX + 12, yOffset - 6));
    g2.draw(new Line2D.Float(newX, yOffset, newX + 2, yOffset + 1));
    String maxString = Float.toString(maximumTemp);
    g2.drawString(maxString, newX + 14, yOffset - 1);

    // Add the text banner.
    FontMetrics metrics = g2.getFontMetrics();
    int width  = metrics.stringWidth(label);
    int height = metrics.getHeight();
    g2.setFont(GaugeCommon.BOLD_FONT);
    g2.drawString(label, x - width/2, y - height/2);
    String tempString = "Temperature";
    width = metrics.stringWidth(tempString);
    g2.drawString(tempString, x - width/2, y + height + 2 - height/2);

    // Add current value.
    String currentString = currentTemp + "\u00B0" + "F";
    width = metrics.stringWidth(currentString);
    g2.drawString(currentString, x - width/2, y + height + 12);
    g2.setFont(GaugeCommon.PLAIN_FONT);
  }

  public void setCurrent(float temperature)
  {
    this.currentTemp = temperature;

    if (temperature > maximumTemp)
      maximumTemp = temperature;

    if (temperature < minimumTemp)
      minimumTemp = temperature;
  }

  void setMinimum (float temperature)
  {
    this.minimumTemp = temperature;
  }

  public void setMaximum (float temperature)
  {
    this.maximumTemp = temperature;
  }
}
