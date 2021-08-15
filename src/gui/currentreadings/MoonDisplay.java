/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class draws the moon times banner.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import algorithms.*;

import javax.swing.*;
import java.awt.*;
import java.lang.StrictMath;

class MoonDisplay
{
  /**
   * Draw the moon times banner information.
   *
   * @param component The parent component.
   * @param g2 The Graphics component.
   * @param x The starting X value.
   * @param y The starting Y value.
   */
  void paintGauge (Component component, Graphics2D g2, int x, int y)
  {
    // Get the moon phase info.
    int dayOfMoon = Calculations.calculateMoonPhase();

    // Set up to draw the phase.
    g2.setStroke(GaugeCommon.STROKE);
    g2.setFont(GaugeCommon.PLAIN_FONT);

    // Add the phase name.
    MoonPhaseName phaseName = MoonPhase.getPhaseName(dayOfMoon);
    g2.setFont(GaugeCommon.BOLD_FONT);
    g2.drawString(phaseName.getName(), x, y + 12);
    g2.setFont(GaugeCommon.PLAIN_FONT);

    // Draw the image of the moon.
    String pictureName = "moon" + dayOfMoon + ".png";
    ImageIcon icon = new ImageIcon("moonicons/" + pictureName);
    Image image = icon.getImage(); // transform it
    Image newimg = image.getScaledInstance(70, 70,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
    icon = new ImageIcon(newimg);  // transform it back
    icon.paintIcon(component, g2, x + 20, y + 20);

    // Add the percent full.
//    String percentFullString = "Percent Full: " +
//      rnd(moonPhase.getFullness(), 2) + "%";
//    g2.drawString(percentFullString, x + 190, y + height * 2);
  }


  // General function for rounding to specified precision
  private float rnd (float val, float prec) 
  {
    double newValue = val * StrictMath.pow (10, prec);
    newValue = StrictMath.round(newValue);
    newValue = newValue / StrictMath.pow (10, prec);
    return (float) newValue;
  }
}
