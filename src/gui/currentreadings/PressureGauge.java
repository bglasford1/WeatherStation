/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class responsible for drawing the pressure gauge graphic.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

class PressureGauge
{
  private float  currentPressure = 0f;
  private float  minimumPressure = 0f;
  private float  maximumPressure = 0f;
  private String trend = "Steady";

  /**
   * This method is called to draw the pressure graphic at the position
   * given.  This position is at the center top of the graphic.
   */
  void paintGauge (Graphics2D g2, int x, int y)
  {
    // Offset the gauge.
    int yGauge = y + 146;

    // Set up to draw the gauge.
    g2.setStroke(GaugeCommon.STROKE);
    FontMetrics metrics = g2.getFontMetrics();

    // Add the text banner.
    String pressureString = "Pressure";
    int width  = metrics.stringWidth(pressureString);
    int height = metrics.getHeight();
    g2.setFont(GaugeCommon.BOLD_FONT);
    g2.drawString(pressureString, x - width/2, y - height/2);

    // Add current value.
    String currentString = currentPressure + " in. Hg";
    width = metrics.stringWidth(currentString);
    g2.drawString(currentString, x - width/2, y + 10);
    g2.setFont(GaugeCommon.PLAIN_FONT);

    // Add rate value.
    String trendLabel = "Trend: ";
    g2.drawString(trendLabel, x + 20, yGauge - 20);
    g2.drawString(trend, x + 20, yGauge - 8);

    // Create the gauge.
    float innerRadius     = 100.0f;
    float outerRadius     = 110.0f;
    float shortTickRadius = 113.0f;
    float longTickRadius  = 116.0f;
    float labelRadius     = 123.0f;

    // Draw the outline of the polygon in foreground color.
    GeneralPath polygon = new GeneralPath
      (GeneralPath.WIND_EVEN_ODD, 60);

    polygon.moveTo
      ( x      + innerRadius * (float)StrictMath.cos(0.0),
	      yGauge + innerRadius * (float)StrictMath.sin(0.0)
	    );

    double angle;
    int i;
    for (i = 1; i < 31; i++)
    {
      angle = StrictMath.toRadians(-i * 6.0);
      polygon.lineTo
        ( x      + innerRadius * (float)StrictMath.cos(angle),
	        yGauge + innerRadius * (float)StrictMath.sin(angle)
	      );
    }

    angle = StrictMath.toRadians(-180.0);
    polygon.lineTo
      ( x      + outerRadius * (float)StrictMath.cos(angle),
        yGauge + outerRadius * (float)StrictMath.sin(angle)
      );

    for (i = 1; i < 31; i++)
    {
      angle = StrictMath.toRadians(-180.0 + i * 6.0);
      polygon.lineTo
        ( x      + outerRadius * (float)StrictMath.cos(angle),
	        yGauge + outerRadius * (float)StrictMath.sin(angle)
	      );
    }

    angle = StrictMath.toRadians(-180.0);
    polygon.lineTo
      ( x      + innerRadius * (float)StrictMath.cos(angle),
        yGauge + outerRadius * (float)StrictMath.sin(angle)
      );

    polygon.closePath();
    g2.draw(polygon);

    // Fill in the right hand third of the polygon in yellow.
    GeneralPath yellowPolygon = new GeneralPath
      (GeneralPath.WIND_EVEN_ODD, 20);

    yellowPolygon.moveTo
      ( x      + innerRadius * (float)StrictMath.cos(0.0),
	      yGauge + innerRadius * (float)StrictMath.sin(0.0)
	    );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-i * 6.0);
      yellowPolygon.lineTo
        ( x      + innerRadius * (float)StrictMath.cos(angle),
	        yGauge + innerRadius * (float)StrictMath.sin(angle)
	      );
    }

    angle = StrictMath.toRadians(-60.0);
    yellowPolygon.lineTo
      ( x      + outerRadius * (float)StrictMath.cos(angle),
        yGauge + outerRadius * (float)StrictMath.sin(angle)
      );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-60.0 + i * 6.0);
      yellowPolygon.lineTo
        ( x      + outerRadius * (float)StrictMath.cos(angle),
	        yGauge + outerRadius * (float)StrictMath.sin(angle)
	      );
    }

    yellowPolygon.closePath();
    g2.setColor(Color.YELLOW);
    g2.fill(yellowPolygon);

    // Fill in the middle third of the polygon in green.
    GeneralPath greenPolygon = new GeneralPath
      (GeneralPath.WIND_EVEN_ODD, 20);

    angle = StrictMath.toRadians(-60.0);
    greenPolygon.moveTo
      ( x      + innerRadius * (float)StrictMath.cos(angle),
	      yGauge + innerRadius * (float)StrictMath.sin(angle)
	    );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-60 - (i * 6.0));
      greenPolygon.lineTo
        ( x      + innerRadius * (float)StrictMath.cos(angle),
	        yGauge + innerRadius * (float)StrictMath.sin(angle)
	      );
    }

    angle = StrictMath.toRadians(-120.0);
    greenPolygon.lineTo
      ( x      + outerRadius * (float)StrictMath.cos(angle),
        yGauge + outerRadius * (float)StrictMath.sin(angle)
      );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-120.0 + i * 6.0);
      greenPolygon.lineTo
        ( x      + outerRadius * (float)StrictMath.cos(angle),
	        yGauge + outerRadius * (float)StrictMath.sin(angle)
	      );
    }

    greenPolygon.closePath();
    g2.setColor(Color.GREEN);
    g2.fill(greenPolygon);

    // Fill in the left hand third of the polygon in red.
    GeneralPath redPolygon = new GeneralPath
      (GeneralPath.WIND_EVEN_ODD, 20);

    angle = StrictMath.toRadians(-120.0);
    redPolygon.moveTo
      ( x      + innerRadius * (float)StrictMath.cos(angle),
	      yGauge + innerRadius * (float)StrictMath.sin(angle)
	    );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-120 - (i * 6.0));
      redPolygon.lineTo
        ( x      + innerRadius * (float)StrictMath.cos(angle),
	        yGauge + innerRadius * (float)StrictMath.sin(angle)
	      );
    }

    angle = StrictMath.toRadians(-180.0);
    redPolygon.lineTo
      ( x      + outerRadius * (float)StrictMath.cos(angle),
        yGauge + outerRadius * (float)StrictMath.sin(angle)
      );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-180.0 + i * 6.0);
      redPolygon.lineTo
        ( x      + outerRadius * (float)StrictMath.cos(angle),
	        yGauge + outerRadius * (float)StrictMath.sin(angle)
	      );
    }

    redPolygon.closePath();
    g2.setColor(Color.RED);
    g2.fill(redPolygon);
    g2.setColor(GaugeCommon.FG_COLOR);

    // Add the tick marks.
    for (i = 0; i < 41; i++)
    {
      angle = StrictMath.toRadians(-i * 4.5);
      if (i == 0 || i ==  5 || i == 10 || i == 15 || i == 20 || i == 25 || i == 30 || i == 35 || i == 40)
      {
        g2.draw(new Line2D.Float
          ( x      + outerRadius * (float)StrictMath.cos(angle),
            yGauge + outerRadius * (float)StrictMath.sin(angle),
            x      + longTickRadius * (float)StrictMath.cos(angle),
            yGauge + longTickRadius * (float)StrictMath.sin(angle)));
      }
      else
      {
        g2.draw(new Line2D.Float
          ( x      + outerRadius * (float)StrictMath.cos(angle),
            yGauge + outerRadius * (float)StrictMath.sin(angle),
            x      + shortTickRadius * (float)StrictMath.cos(angle),
            yGauge + shortTickRadius * (float)StrictMath.sin(angle))
          );
      }
    }

    // Add pressure labels.
    String labelString;
    double nextAngle   = -157.5;
    float  xVal;
    float  yVal;
    for (i = 28; i < 32; i++)
    {
      labelString = "" + (i);
      angle = StrictMath.toRadians(nextAngle);
      xVal = x      - 7 + labelRadius * (float)StrictMath.cos(angle);
      yVal = yGauge + 3 + labelRadius * (float)StrictMath.sin(angle);
      g2.drawString(labelString, xVal, yVal);
      nextAngle += 45.0;
    }

    // Add the pressure indicator.
    g2.setStroke(GaugeCommon.STROKE2);
    angle = StrictMath.toRadians((currentPressure - 27.5) * 45.0 - 180);
    float newX = x      + outerRadius * (float)StrictMath.cos(angle);
    float newY = yGauge + outerRadius * (float)StrictMath.sin(angle);

    g2.draw(new Line2D.Float(x, yGauge, newX, newY));

    // Add the maximum pressure indicator.
    angle = StrictMath.toRadians((maximumPressure - 27.5) * 45.0 - 180);
    float innerX = x      + outerRadius * (float)StrictMath.cos(angle);
    float innerY = yGauge + outerRadius * (float)StrictMath.sin(angle);
    float outerX = x      + (outerRadius + 15) * (float)StrictMath.cos(angle);
    float outerY = yGauge + (outerRadius + 15) * (float)StrictMath.sin(angle);
    g2.draw(new Line2D.Float(innerX, innerY, outerX, outerY));
    String maxString = "Max";
    if (StrictMath.toDegrees(angle) < -90)
    {
      g2.draw(new Line2D.Float(outerX, outerY, outerX - 15, outerY));
      g2.drawString(maxString, outerX - 40, outerY + 3);
    }
    else
    {
      g2.draw(new Line2D.Float(outerX, outerY, outerX + 15, outerY));
      g2.drawString(maxString, outerX + 20, outerY + 3);
    }

    // Add the minimum pressure indicator.
    angle = StrictMath.toRadians((minimumPressure - 27.5) * 45.0 - 180);
    innerX = x      + outerRadius * (float)StrictMath.cos(angle);
    innerY = yGauge + outerRadius * (float)StrictMath.sin(angle);
    outerX = x      + (outerRadius + 15) * (float)StrictMath.cos(angle);
    outerY = yGauge + (outerRadius + 15) * (float)StrictMath.sin(angle);
    g2.draw(new Line2D.Float(innerX, innerY, outerX, outerY));
    String minString = "Min";
    if (StrictMath.toDegrees(angle) < -90)
    {
      g2.draw(new Line2D.Float(outerX, outerY, outerX - 15, outerY));
      g2.drawString(minString, outerX - 40, outerY + 3);
    }
    else
    {
      g2.draw(new Line2D.Float(outerX, outerY, outerX + 15, outerY));
      g2.drawString(minString, outerX + 20, outerY + 3);
    }
    g2.setStroke(GaugeCommon.STROKE);
  }

  /**
   * Method called to set a new current pressure value.
   */
  public void setCurrent(float pressure)
  {
    this.currentPressure = pressure;

    if (pressure > maximumPressure)
      maximumPressure = pressure;

    if (pressure < minimumPressure)
      minimumPressure = pressure;
  }

  /**
   * Method called to set a new minimum pressure value.
   */
  void setMinimum (float pressure)
  {
    this.minimumPressure = pressure;
  }

  /**
   * Method called to set a new maximum pressure value.
   */
  public void setMaximum (float pressure)
  {
    this.maximumPressure = pressure;
  }

  /**
   * Method called to set a new pressure trend value.
   */
  void setTrend (String trend)
  {
    this.trend = trend;
  }
}
