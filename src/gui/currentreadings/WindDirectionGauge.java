/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class responsible for drawing the wind direction gauge.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import data.dbrecord.WindBins;
import data.dbrecord.WindDirection;
import gui.MinMaxInterval;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

class WindDirectionGauge
{
  private float currentDir = 0f;
  private float gaugeDir = 0f;
  private final WindBins windBins = WindBins.getInstance();
  private final static int OUTER_RADIUS = 60;
  private final static int INNER_RADIUS = 55;
  private final static float HALF_SEGMENT = 11.25f;
  private Graphics2D g2;
  private int xGauge;
  private int yGauge;
  private MinMaxInterval interval;

  /**
   * Constructor.
   */
  WindDirectionGauge (MinMaxInterval minMaxInterval)
  {
    this.interval = minMaxInterval;
  }

  /**
   * This method is called to draw the wind direction graphic at the position
   * given.  This position is at the center top of the graphics.
   *
   * @param g2 The graphics on which to draw the wind direction graphic.
   * @param x  The X value of the center of the circle.
   * @param y  The Y value of the center of the circle.
   */
  void paintGauge(Graphics2D g2, int x, int y)
  {
    this.g2 = g2;

    // Set up to draw the wind direction gauge.
    // Offset the gauge.
    xGauge = x + 200;
    yGauge = y + 84;

    // Set up to draw the gauge.
    g2.setStroke(GaugeCommon.STROKE);
    FontMetrics metrics = g2.getFontMetrics();

    // Add the text banner.
    String dirString = "Wind Direction";
    int width = metrics.stringWidth(dirString);
    g2.setFont(GaugeCommon.BOLD_FONT);
    g2.drawString(dirString, xGauge - width / 2, y - 20);
    g2.drawString(Float.toString(currentDir) + "\u00B0", xGauge - 16 , y - 5);
    g2.setFont(GaugeCommon.PLAIN_FONT);

    int shortDirRadius = 65;
    int longDirRadius = 70;
    int labelDirRadius = 80;

    // Draw the circle gauge.
    GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 120);

    polygon.moveTo
      (xGauge + OUTER_RADIUS * (float) StrictMath.cos(0.0),
       yGauge + OUTER_RADIUS * (float) StrictMath.sin(0.0)
      );

    // The circle is actually 32 line segments.  Each segment is 11.25 degrees long.
    double angle;
    for (int i = 1; i < 33; i++)
    {
      angle = StrictMath.toRadians(i * HALF_SEGMENT);
      polygon.lineTo
        (xGauge + OUTER_RADIUS * (float) StrictMath.cos(angle),
         yGauge + OUTER_RADIUS * (float) StrictMath.sin(angle)
        );
    }

    polygon.closePath();
    g2.draw(polygon);

    // Add the tick marks.
    for (int i = 0; i < 17; i++)
    {
      angle = StrictMath.toRadians(i * 22.5);
      if (i == 0 || i == 2 || i == 4 || i == 6 || i == 8 || i == 10 || i == 12 || i == 14 || i == 16)
      {
        g2.draw(new Line2D.Float
                  (xGauge + OUTER_RADIUS * (float) StrictMath.cos(angle),
                   yGauge + OUTER_RADIUS * (float) StrictMath.sin(angle),
                   xGauge + longDirRadius * (float) StrictMath.cos(angle),
                   yGauge + longDirRadius * (float) StrictMath.sin(angle))
        );
      }
      else
      {
        g2.draw(new Line2D.Float
                  (xGauge + OUTER_RADIUS * (float) StrictMath.cos(angle),
                   yGauge + OUTER_RADIUS * (float) StrictMath.sin(angle),
                   xGauge + shortDirRadius * (float) StrictMath.cos(angle),
                   yGauge + shortDirRadius * (float) StrictMath.sin(angle))
        );
      }
    }

    // Add speed labels.
    double nextAngle = 0.0;
    String labelString;
    float xVal;
    float yVal;
    for (int i = 0; i < 8; i++)
    {
      switch (i)
      {
        case 0:
          labelString = "E";
          break;
        case 1:
          labelString = "SE";
          break;
        case 2:
          labelString = "S";
          break;
        case 3:
          labelString = "SW";
          break;
        case 4:
          labelString = "W";
          break;
        case 5:
          labelString = "NW";
          break;
        case 6:
          labelString = "N";
          break;
        default:
          labelString = "NE";
          break;
      }

      angle = StrictMath.toRadians(nextAngle);
      xVal = xGauge - 5 + labelDirRadius * (float) StrictMath.cos(angle);
      yVal = yGauge + 3 + labelDirRadius * (float) StrictMath.sin(angle);
      g2.drawString(labelString, xVal, yVal);
      nextAngle += 45.0;
    }

    // Add the direction indicator.
    g2.setStroke(GaugeCommon.STROKE2);
    angle = StrictMath.toRadians(gaugeDir);
    float dirX = xGauge + OUTER_RADIUS * (float) StrictMath.cos(angle);
    float dirY = yGauge + OUTER_RADIUS * (float) StrictMath.sin(angle);
    g2.draw(new Line2D.Float(xGauge, yGauge, dirX, dirY));
    g2.setStroke(GaugeCommon.STROKE);

    // Add prevailing wind direction indicators.
    switch (interval)
    {
      case hourly:
        addHourlyPrevailingDirections();
        break;
      case daily:
      case monthly:
      case yearly:
        addDailyPrevailingDirections();
        break;
    }
  }

  /**
   * Internal method to set the wind segments.  The wind segment is based on the last hours worth of observations.
   *
   * @param color     The color of the segment.  If removing the segment then use the background color.
   * @param direction The direction to color.
   */
  private void drawWindSegment(Color color, WindDirection direction)
  {
    GeneralPath nPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 20);
    float nearDir = direction.direction() - HALF_SEGMENT;
    float farDir = direction.direction() + HALF_SEGMENT;

    double angle = StrictMath.toRadians(farDir);
    nPolygon.moveTo
      (xGauge + OUTER_RADIUS * (float) StrictMath.cos(angle),
       yGauge + OUTER_RADIUS * (float) StrictMath.sin(angle)
      );

    for (int i = 1; i < 3; i++)
    {
      angle = StrictMath.toRadians(farDir - (i * HALF_SEGMENT));
      nPolygon.lineTo
        (xGauge + OUTER_RADIUS * (float) StrictMath.cos(angle),
         yGauge + OUTER_RADIUS * (float) StrictMath.sin(angle)
        );
    }

    angle = StrictMath.toRadians(nearDir);
    nPolygon.lineTo
      (xGauge + INNER_RADIUS * (float) StrictMath.cos(angle),
       yGauge + INNER_RADIUS * (float) StrictMath.sin(angle)
      );

    for (int i = 1; i < 3; i++)
    {
      angle = StrictMath.toRadians(nearDir + (i * HALF_SEGMENT));
      nPolygon.lineTo
        (xGauge + INNER_RADIUS * (float) StrictMath.cos(angle),
         yGauge + INNER_RADIUS * (float) StrictMath.sin(angle)
        );
    }
    nPolygon.closePath();

    try
    {
      g2.setColor(color);
      g2.fill(nPolygon);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }


  /**
   * Method used to set a new wind direction.  This data comes from the loop data.
   *
   * @param direction The current wind direction.
   */
  public void setDirection(float direction)
  {
    this.currentDir = direction;
    gaugeDir = direction - 90.0f;
    if (gaugeDir > 359)
      gaugeDir = gaugeDir - 360;
  }


  /**
   * Method used to add a new prevailing wind direction reading from the DMP data.
   *
   * NOTE: the prevailing wind direction should take wind speed into consideration.  Google it...
   *
   * @param newDirection The new wind direction.
   */
  public void addPrevailingDirection(WindDirection newDirection)
  {
    windBins.addObservation(newDirection);

    switch (interval)
    {
      case hourly:
        redrawPrevailingDirection(newDirection, windBins.getHourlyReading(newDirection), 1);
        break;
      case daily:
        redrawPrevailingDirection(newDirection, windBins.getDayDirectionCount(newDirection), 5);
        break;
    }
  }

  /**
   * Redraw a specific prevailing wind direction segment.  This works for all intervals.
   *
   * @param direction The wind direction to draw.
   */
  private void redrawPrevailingDirection(WindDirection direction, int count, int multiplier)
  {
    int lowValue = 3 * multiplier;
    int midValue = 7 * multiplier;

    // Redraw new segment if needed.
    if (count > 0 && count < lowValue)
    {
      drawWindSegment(Color.YELLOW, direction);
    }
    else if (count >= lowValue && count < midValue)
    {
      drawWindSegment(Color.ORANGE, direction);
    }
    else if (count >= midValue)
    {
      drawWindSegment(Color.RED, direction);
    }
  }

  /**
   * Add the prevailing daily wind direction indicators.  This is simply coloring sectors based on the number of times
   * the wind direction was the current wind direction in the DMP data.
   */
  private void addDailyPrevailingDirections()
  {
    redrawPrevailingDirection(WindDirection.N, windBins.getDayDirectionCount(WindDirection.N), 5);
    redrawPrevailingDirection(WindDirection.NNE, windBins.getDayDirectionCount(WindDirection.NNE), 5);
    redrawPrevailingDirection(WindDirection.NE, windBins.getDayDirectionCount(WindDirection.NE), 5);
    redrawPrevailingDirection(WindDirection.ENE, windBins.getDayDirectionCount(WindDirection.ENE), 5);
    redrawPrevailingDirection(WindDirection.E, windBins.getDayDirectionCount(WindDirection.E), 5);
    redrawPrevailingDirection(WindDirection.ESE, windBins.getDayDirectionCount(WindDirection.ESE), 5);
    redrawPrevailingDirection(WindDirection.SE, windBins.getDayDirectionCount(WindDirection.SE), 5);
    redrawPrevailingDirection(WindDirection.SSE, windBins.getDayDirectionCount(WindDirection.SSE), 5);
    redrawPrevailingDirection(WindDirection.S, windBins.getDayDirectionCount(WindDirection.S), 5);
    redrawPrevailingDirection(WindDirection.SSW, windBins.getDayDirectionCount(WindDirection.SSW), 5);
    redrawPrevailingDirection(WindDirection.SW, windBins.getDayDirectionCount(WindDirection.SW), 5);
    redrawPrevailingDirection(WindDirection.WSW, windBins.getDayDirectionCount(WindDirection.WSW), 5);
    redrawPrevailingDirection(WindDirection.W, windBins.getDayDirectionCount(WindDirection.W), 5);
    redrawPrevailingDirection(WindDirection.WNW, windBins.getDayDirectionCount(WindDirection.WNW), 5);
    redrawPrevailingDirection(WindDirection.NW, windBins.getDayDirectionCount(WindDirection.NW), 5);
    redrawPrevailingDirection(WindDirection.NNW, windBins.getDayDirectionCount(WindDirection.NNW), 5);
  }

  /**
   * Add the prevailing daily wind direction indicators.  This is simply coloring sectors based on the number of times
   * the wind direction was the current wind direction in the DMP data.
   */
  private void addHourlyPrevailingDirections()
  {
    redrawPrevailingDirection(WindDirection.N, windBins.getHourlyReading(WindDirection.N), 1);
    redrawPrevailingDirection(WindDirection.NNE, windBins.getHourlyReading(WindDirection.NNE), 1);
    redrawPrevailingDirection(WindDirection.NE, windBins.getHourlyReading(WindDirection.NE), 1);
    redrawPrevailingDirection(WindDirection.ENE, windBins.getHourlyReading(WindDirection.ENE), 1);
    redrawPrevailingDirection(WindDirection.E, windBins.getHourlyReading(WindDirection.E), 1);
    redrawPrevailingDirection(WindDirection.ESE, windBins.getHourlyReading(WindDirection.ESE), 1);
    redrawPrevailingDirection(WindDirection.SE, windBins.getHourlyReading(WindDirection.SE), 1);
    redrawPrevailingDirection(WindDirection.SSE, windBins.getHourlyReading(WindDirection.SSE), 1);
    redrawPrevailingDirection(WindDirection.S, windBins.getHourlyReading(WindDirection.S), 1);
    redrawPrevailingDirection(WindDirection.SSW, windBins.getHourlyReading(WindDirection.SSW), 1);
    redrawPrevailingDirection(WindDirection.SW, windBins.getHourlyReading(WindDirection.SW), 1);
    redrawPrevailingDirection(WindDirection.WSW, windBins.getHourlyReading(WindDirection.WSW), 1);
    redrawPrevailingDirection(WindDirection.W, windBins.getHourlyReading(WindDirection.W), 1);
    redrawPrevailingDirection(WindDirection.WNW, windBins.getHourlyReading(WindDirection.WNW), 1);
    redrawPrevailingDirection(WindDirection.NW, windBins.getHourlyReading(WindDirection.NW), 1);
    redrawPrevailingDirection(WindDirection.NNW, windBins.getHourlyReading(WindDirection.NNW), 1);
  }

  /**
   * Method used to set a new maximum wind speed.
   */
  public void setTimePeriod(MinMaxInterval interval)
  {
    this.interval = interval;
  }
}
