/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class responsible for drawing the wind speed gauge.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

class WindSpeedGauge
{
  private float  currentSpeed = 0f;
  private float  averageSpeed = 0f;
  private float  maximumSpeed = 0f;

  /**
   * This method is called to draw the wind speed graphic at the position
   * given.  This position is at the center top of the graphics.
   */
  void paintGauge (Graphics2D g2, int x, int y)
  {
    // Set up to draw the wind speed gauge first.
    // Offset the gauge.
    int yGauge = y + 146;
    int xGauge = x - 100;

    // Set up to draw the gauge.
    g2.setStroke(GaugeCommon.STROKE);
    FontMetrics metrics = g2.getFontMetrics();

    // Add the text banner.
    String pressureString = "Wind Speed";
    int width  = metrics.stringWidth(pressureString);
    int height = metrics.getHeight();
    g2.setFont(GaugeCommon.BOLD_FONT);
    g2.drawString(pressureString, xGauge - width/2, y - height/2);

    // Add current value.
    String currentString = currentSpeed + " MPH";
    width = metrics.stringWidth(currentString);
    g2.drawString(currentString, xGauge - width/2, y + 10);
    g2.setFont(GaugeCommon.PLAIN_FONT);

    float innerRadius     = 100.0f;
    float outerRadius     = 110.0f;
    float shortTickRadius = 113.0f;
    float longTickRadius  = 116.0f;
    float labelRadius     = 123.0f;

    // Draw the outline of the gauge in the foreground color.
    GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 60);

    polygon.moveTo
      ( xGauge + innerRadius * (float)StrictMath.cos(0.0),
	      yGauge + innerRadius * (float)StrictMath.sin(0.0)
	    );

    double angle;
    int i;
    for (i = 1; i < 31; i++)
    {
      angle = StrictMath.toRadians(-i * 6.0);
      polygon.lineTo
        ( xGauge + innerRadius * (float)StrictMath.cos(angle),
	        yGauge + innerRadius * (float)StrictMath.sin(angle)
	      );
    }

    angle = StrictMath.toRadians(-180.0);
    polygon.lineTo
      ( xGauge + outerRadius * (float)StrictMath.cos(angle),
        yGauge + outerRadius * (float)StrictMath.sin(angle)
      );

    for (i = 1; i < 31; i++)
    {
      angle = StrictMath.toRadians(-180.0 + i * 6.0);
      polygon.lineTo
        ( xGauge + outerRadius * (float)StrictMath.cos(angle),
	        yGauge + outerRadius * (float)StrictMath.sin(angle)
	      );
    }

    angle = StrictMath.toRadians(-180.0);
    polygon.lineTo
      ( xGauge + innerRadius * (float)StrictMath.cos(angle),
        yGauge + outerRadius * (float)StrictMath.sin(angle)
      );

    polygon.closePath();
    g2.draw(polygon);

    // Draw the green third of the wind speed gauge.
    GeneralPath greenPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 20);

    angle = StrictMath.toRadians(-120);
    greenPolygon.moveTo
      ( xGauge + innerRadius * (float)StrictMath.cos(angle),
	      yGauge + innerRadius * (float)StrictMath.sin(angle)
	    );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-120 - (i * 6.0));
      greenPolygon.lineTo
        ( xGauge + innerRadius * (float)StrictMath.cos(angle),
	        yGauge + innerRadius * (float)StrictMath.sin(angle)
	      );
    }

    angle = StrictMath.toRadians(-180.0);
    greenPolygon.lineTo
      ( xGauge + outerRadius * (float)StrictMath.cos(angle),
        yGauge + outerRadius * (float)StrictMath.sin(angle)
      );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-180.0 + (i * 6.0));
      greenPolygon.lineTo
        ( xGauge + outerRadius * (float)StrictMath.cos(angle),
	        yGauge + outerRadius * (float)StrictMath.sin(angle)
	      );
    }

    greenPolygon.closePath();
    g2.setColor(Color.GREEN);
    g2.fill(greenPolygon);

    // Draw the yellow middle third of the wind speed gauge.
    GeneralPath yellowPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 20);

    angle = StrictMath.toRadians(-60);
    yellowPolygon.moveTo
      ( xGauge + innerRadius * (float)StrictMath.cos(angle),
	      yGauge + innerRadius * (float)StrictMath.sin(angle)
	    );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-60 - (i * 6.0));
      yellowPolygon.lineTo
        ( xGauge + innerRadius * (float)StrictMath.cos(angle),
	        yGauge + innerRadius * (float)StrictMath.sin(angle)
	      );
    }

    angle = StrictMath.toRadians(-120.0);
    yellowPolygon.lineTo
      ( xGauge + outerRadius * (float)StrictMath.cos(angle),
        yGauge + outerRadius * (float)StrictMath.sin(angle)
      );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-120.0 + (i * 6.0));
      yellowPolygon.lineTo
        ( xGauge + outerRadius * (float)StrictMath.cos(angle),
	        yGauge + outerRadius * (float)StrictMath.sin(angle)
	      );
    }

    yellowPolygon.closePath();
    g2.setColor(Color.YELLOW);
    g2.fill(yellowPolygon);

    // Draw the red third of the wind speed gauge.
    GeneralPath redPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 20);

    redPolygon.moveTo
      ( xGauge + innerRadius * (float)StrictMath.cos(0.0),
	      yGauge + innerRadius * (float)StrictMath.sin(0.0)
	    );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-i * 6.0);
      redPolygon.lineTo
        ( xGauge + innerRadius * (float)StrictMath.cos(angle),
	        yGauge + innerRadius * (float)StrictMath.sin(angle)
	      );
    }

    angle = StrictMath.toRadians(-60.0);
    redPolygon.lineTo
      ( xGauge + outerRadius * (float)StrictMath.cos(angle),
        yGauge + outerRadius * (float)StrictMath.sin(angle)
      );

    for (i = 1; i < 11; i++)
    {
      angle = StrictMath.toRadians(-60.0 + (i * 6.0));
      redPolygon.lineTo
        ( xGauge + outerRadius * (float)StrictMath.cos(angle),
	        yGauge + outerRadius * (float)StrictMath.sin(angle)
	      );
    }

    redPolygon.closePath();
    g2.setColor(Color.RED);
    g2.fill(redPolygon);
    g2.setColor(GaugeCommon.FG_COLOR);

    // Add the tick marks.
    for (i = 0; i < 61; i++)
    {
      angle = StrictMath.toRadians(-i * 3.0);
      if (i ==  0 || i == 10 || i == 20 || i == 30 ||
          i == 40 || i == 50 || i == 60)
      {
        g2.draw(new Line2D.Float
          ( xGauge + outerRadius * (float)StrictMath.cos(angle),
	          yGauge + outerRadius * (float)StrictMath.sin(angle),
            xGauge + longTickRadius * (float)StrictMath.cos(angle),
	          yGauge + longTickRadius * (float)StrictMath.sin(angle))
          );
      }
      else
      {
        g2.draw(new Line2D.Float
          ( xGauge + outerRadius * (float)StrictMath.cos(angle),
            yGauge + outerRadius * (float)StrictMath.sin(angle),
            xGauge + shortTickRadius * (float)StrictMath.cos(angle),
            yGauge + shortTickRadius * (float)StrictMath.sin(angle))
          );
      }
    }

    // Add speed labels.
    String labelString;
    double nextAngle = -180.0;
    float  xVal;
    float  yVal;
    for (i = 0; i < 7; i++)
    {
      labelString = "" + (i * 10);
      angle = StrictMath.toRadians(nextAngle);
      xVal = xGauge - 5  + labelRadius * (float)StrictMath.cos(angle);
      yVal = yGauge + 3 + labelRadius * (float)StrictMath.sin(angle);
      g2.drawString(labelString, xVal, yVal);
      nextAngle += 30.0;
    }

    // Add the speed indicator.
    g2.setStroke(GaugeCommon.STROKE2);
    angle = StrictMath.toRadians(currentSpeed * 3.0 - 180.0);
    float newX = xGauge + outerRadius * (float)StrictMath.cos(angle);
    float newY = yGauge + outerRadius * (float)StrictMath.sin(angle);
    g2.draw(new Line2D.Float(xGauge, yGauge, newX, newY));

    // Add the maximum speed indicator.
    angle = StrictMath.toRadians(maximumSpeed * 3.0 - 180.0);
    float innerX = xGauge + outerRadius * (float)StrictMath.cos(angle);
    float innerY = yGauge + outerRadius * (float)StrictMath.sin(angle);
    float outerX = xGauge + (outerRadius + 15) * (float)StrictMath.cos(angle);
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
      g2.drawString (maxString, outerX + 20, outerY + 3);
    }

    // Add the average speed indicator.
    angle = StrictMath.toRadians(averageSpeed * 3.0 - 180.0);
    innerX = xGauge + outerRadius * (float)StrictMath.cos(angle);
    innerY = yGauge + outerRadius * (float)StrictMath.sin(angle);
    outerX = xGauge + (outerRadius + 15) * (float)StrictMath.cos(angle);
    outerY = yGauge + (outerRadius + 15) * (float)StrictMath.sin(angle);
    g2.draw(new Line2D.Float(innerX, innerY, outerX, outerY));
    String avgString = "Avg";
    if (StrictMath.toDegrees(angle) < -90)
    {
      g2.draw(new Line2D.Float(outerX, outerY, outerX - 15, outerY));
      g2.drawString(avgString, outerX - 40, outerY + 3);
    }
    else
    {
      g2.draw(new Line2D.Float(outerX, outerY, outerX + 15, outerY));
      g2.drawString (avgString, outerX + 20, outerY + 3);
    }
  }

  /**
   * Method used to set a new current wind speed.
   */
  public void setCurrent(float speed)
  {
    this.currentSpeed = speed;

    if (speed > maximumSpeed)
      maximumSpeed = speed;
  }

  /**
   * Method used to set a new average wind speed.
   */
  void setAverageSpeed(float speed)
  {
    this.averageSpeed = speed;
  }

  /**
   * Method used to set a new maximum wind speed.
   */
  public void setMaximum (float maxSpeed)
  {
    this.maximumSpeed = maxSpeed;
  }
}
