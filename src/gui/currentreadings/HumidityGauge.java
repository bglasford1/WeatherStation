/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class responsible for drawing the humidity gauge.  It displays
            the gauge in a graphical representation along with min/max
            readings plus the rate of change.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

class HumidityGauge
{
  private float  currentHumidity = 0f;
  private float  minimumHumidity = 0f;
  private float  maximumHumidity = 0f;

  // Scale temps and labels.  These are set at creation.
  private float minScaleHumidity;
  private float maxScaleHumidity;
  private final String label;

  // Constants
  private static final int RECT_WIDTH = 8;
  private static final int RECT_HEIGHT = 200;
  private final float deltaHumidity;
  private final float ticksPerDegree;

  /**
   * Constructor that sets constants.
   *
   * @param nameLabel Name of the gauge.
   * @param minHumidity   Min temperature value. Must be divisible by 10.
   * @param maxHumidity   Max temperature value. Must be divisible by 10.
   */
  HumidityGauge(String nameLabel, int minHumidity, int maxHumidity)
  {
    this.label = nameLabel;
    this.minScaleHumidity = minHumidity;
    this.maxScaleHumidity = maxHumidity;
    deltaHumidity = maxHumidity - minHumidity;
    ticksPerDegree = RECT_HEIGHT / deltaHumidity;
  }

  /**
   * This method is called to draw the humidity graphic at the position given.  This position is at the
   * center top of the graphic.    For graphics, plus is downward for y values!!!
   */
  void paintGauge(Graphics2D g2, int x, int y)
  {
    // Offset the gauge.
    int yGauge = y + 38;

    // Draw the thermometer gauge rectangle.  The x & yGauge represent the upper left corner of the graphic..
    g2.setStroke(GaugeCommon.STROKE);
    g2.draw(new Rectangle2D.Float(x, yGauge, RECT_WIDTH, RECT_HEIGHT));

    // Fill in the current humidity.
    float currentHeight = (currentHumidity - minScaleHumidity) * ticksPerDegree;
    float heightOffset  = (float)RECT_HEIGHT - currentHeight;
    int   newY = yGauge + (int)heightOffset;
    g2.setPaint(Color.BLUE);
    g2.fill(new Rectangle2D.Float(x, newY, RECT_WIDTH, (int)currentHeight));
    g2.setPaint(GaugeCommon.FG_COLOR);

    // Add the degree tick marks from the top down.
    // The marks will go from minTemp degrees to maxHumidity degrees.
    int numberOfTicks = (int)(deltaHumidity / 10) + 1;
    int yOffset;
    int i;
    for (i = 0; i < numberOfTicks; i++)
    {
      yOffset = yGauge + i * RECT_HEIGHT / (numberOfTicks - 1);
      g2.draw(new Line2D.Float(x - 5, yOffset, x, yOffset));
    }

    // Add degree labels.
    String labelString;
    for (i = 0; i < numberOfTicks; i++)
    {
      int temp = (int)(maxScaleHumidity - i * 10);
      int length = String.valueOf(temp).length();
      switch (length)
      {
        case 0:
          labelString = "  " + temp;
          break;
        case 2:
          labelString = " " + temp;
          break;
        default: // case 3:
          labelString = "" + temp;
          break;
      }
      yOffset = yGauge + 5 + i * RECT_HEIGHT / (numberOfTicks - 1);
      g2.drawString(labelString, x - 30, yOffset);
    }

    // Add the intermediate degree tick marks from the top down.
    numberOfTicks = numberOfTicks - 1;
    for (i = 0; i < numberOfTicks; i++)
    {
      yOffset = yGauge + (RECT_HEIGHT / (numberOfTicks * 2)) + i * RECT_HEIGHT / numberOfTicks;
      g2.draw(new Line2D.Float(x - 3, yOffset, x, yOffset));
    }

    // Add the minimum temperature arrow.
    currentHeight = (minimumHumidity - minScaleHumidity) * ticksPerDegree;
    heightOffset  = (float)RECT_HEIGHT - currentHeight;
    yOffset       = yGauge + (int)heightOffset;
    int newX      = x + RECT_WIDTH;
    g2.draw(new Line2D.Float(newX, yOffset, newX + 6, yOffset + 6));
    g2.draw(new Line2D.Float(newX + 6, yOffset + 6, newX + 12, yOffset + 6));
    g2.draw(new Line2D.Float(newX, yOffset, newX + 2, yOffset - 1));
    String minString = Float.toString(minimumHumidity);
    g2.drawString(minString, newX + 14, yOffset + 11);

    // Add the maximum temperature arrow.
    currentHeight = (maximumHumidity - minScaleHumidity) * ticksPerDegree;
    heightOffset  = (float)RECT_HEIGHT - currentHeight;
    yOffset       = yGauge + (int)heightOffset;
    newX          = x + RECT_WIDTH;
    g2.draw(new Line2D.Float(newX, yOffset, newX + 6, yOffset - 6));
    g2.draw(new Line2D.Float(newX + 6, yOffset - 6, newX + 12, yOffset - 6));
    g2.draw(new Line2D.Float(newX, yOffset, newX + 2, yOffset + 1));
    String maxString = Float.toString(maximumHumidity);
    g2.drawString(maxString, newX + 14, yOffset - 1);

    // Add the text banner.
    FontMetrics metrics = g2.getFontMetrics();
    int width  = metrics.stringWidth(label);
    int height = metrics.getHeight();
    g2.setFont(GaugeCommon.BOLD_FONT);
    g2.drawString(label, x - width/2, y - height/2);
    String tempString = "Humidity";
    width = metrics.stringWidth(tempString);
    g2.drawString(tempString, x - width/2, y + height + 2 - height/2);

    // Add current value.
    String currentString = currentHumidity + " %";
    width = metrics.stringWidth(currentString);
    g2.drawString(currentString, x - width/2, y + height + 12);
    g2.setFont(GaugeCommon.PLAIN_FONT);
  }

  /**
   * Method called to set a new current humidity value.
   */
  public void setCurrent(float humidity)
  {
    this.currentHumidity = humidity;

    if (humidity > maximumHumidity)
      maximumHumidity = humidity;

    if (humidity < minimumHumidity)
      minimumHumidity = humidity;
  }

  /**
   * Method called to set a new minimum humidity value.
   */
  void setMinimum (float humidity)
  {
    this.minimumHumidity = humidity;
  }

  /**
   * Method called to set a new maximum humidity value.
   */
  public void setMaximum (float humidity)
  {
    this.maximumHumidity = humidity;
  }
}
