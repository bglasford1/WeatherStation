/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class responsible for drawing the solar gauge.  It displays the
            gauge in a graphical representation along with moon data.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import algorithms.SunMoonRiseSetTimes;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CelestialGauge
{
  private final SunMoonRiseSetTimes sunMoonRiseSetTimes = new SunMoonRiseSetTimes();

  private static final int RECT_WIDTH = 950;
  private static final int RECT_HEIGHT = 8;

  /**
   * Constructor
   */
  CelestialGauge()
  {
    // Set up to generate the sun and moon rise/set times once every day at midnight.
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Denver"));
    ZonedDateTime nextRun = now.withHour(0).withMinute(0).withSecond(0);

    if (now.compareTo(nextRun) > 0)
      nextRun = nextRun.plusDays(1);

    Duration duration = Duration.between(now, nextRun);
    long initalDelay = duration.getSeconds();

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(new PeriodicTask(sunMoonRiseSetTimes),
                                  initalDelay,
                                  TimeUnit.DAYS.toSeconds(1),
                                  TimeUnit.SECONDS);

    sunMoonRiseSetTimes.calculateTimes(LocalDate.now().getYear(),
                                       LocalDate.now().getMonthValue(),
                                       LocalDate.now().getDayOfMonth());
  }

  /**
   * This method is called to draw the various times on the graphic at the position given.  This position is
   * at the upper right hand corner of the graphic.
   */
  void paintTimes(Graphics2D g2, int x, int y)
  {
    // Set up to display the various times.
    g2.setStroke(GaugeCommon.STROKE);
    g2.setFont(GaugeCommon.PLAIN_FONT);
    FontMetrics metrics = g2.getFontMetrics();
    int height = metrics.getHeight();

    // Add the header.
    g2.setFont(GaugeCommon.BOLD_FONT);
    String headerString = "Celestial:";
    g2.drawString(headerString, x, y + height);
    g2.setFont(GaugeCommon.PLAIN_FONT);

    boolean inDaylightTime = TimeZone.getDefault().inDaylightTime(new Date());

    // Add the astro twilight dawn value.
    String astroDawnString = " Astro Dawn:";
    g2.drawString(astroDawnString, x, y + height * 2);
    g2.drawString(sunMoonRiseSetTimes.getAstroDawnString(inDaylightTime), x + 80, y + height * 2);

    // Add the nautical twilight dawn value.
    String nauticalDawnString = " Naut Dawn:";
    g2.drawString(nauticalDawnString, x, y + height * 3);
    g2.drawString(sunMoonRiseSetTimes.getNauticalDawnString(inDaylightTime), x + 80, y + height * 3);

    // Add the civil twilight dawn value.
    String civilDawnString = " Civil Dawn:";
    g2.drawString(civilDawnString, x, y + height * 4);
    g2.drawString(sunMoonRiseSetTimes.getCivilDawnString(inDaylightTime), x + 80, y + height * 4);

    // Add the sunrise time.
    String sunriseString = " Sunrise:";
    g2.drawString(sunriseString, x, y + height * 5);
    g2.drawString(sunMoonRiseSetTimes.getSunriseString(inDaylightTime), x + 80, y + height * 5);

    // Add the sunset time.
    String sunsetString = " Sunset:";
    g2.drawString(sunsetString, x, y + height * 6);
    g2.drawString(sunMoonRiseSetTimes.getSunsetString(inDaylightTime), x + 80, y + height * 6);

    // Add the civil twilight dusk value.
    String civilDuskString = " Civil Dusk:";
    g2.drawString(civilDuskString, x, y + height * 7);
    g2.drawString(sunMoonRiseSetTimes.getCivilDuskString(inDaylightTime), x + 80, y + height * 7);

    // Add the nautical twilight dusk value.
    String nauticalDuskString = " Naut Dusk:";
    g2.drawString(nauticalDuskString, x, y + height * 8);
    g2.drawString(sunMoonRiseSetTimes.getNauticalDuskString(inDaylightTime), x + 80, y + height * 8);

    // Add the astro twilight dusk value.
    String astroDuskString = " Astro Dusk:";
    g2.drawString(astroDuskString, x, y + height * 9);
    g2.drawString(sunMoonRiseSetTimes.getAstroDuskString(inDaylightTime), x + 80, y + height * 9);

    // Add the moonrise time.
    String moonriseStringLabel = " Moonrise:";
    g2.drawString(moonriseStringLabel, x, y + height * 11);
    g2.drawString(sunMoonRiseSetTimes.getMoonriseString(inDaylightTime), x + 80, y + height * 11);

    // Add the moonset time.
    String moonsetStringLabel = " Moonset:";
    g2.drawString(moonsetStringLabel, x, y + height * 12);
    g2.drawString(sunMoonRiseSetTimes.getMoonsetString(inDaylightTime), x + 80, y + height * 12);
  }

  /**
   * This method is called to draw the sun/moon graphic at the position given.  This position is
   * at the upper right hand corner of the graphic.
   */
  void paintGauge(Graphics2D g2, int x, int y)
  {
    // Draw the solar gauge rectangle.  The x & y represent the upper left corner of the graphic.
    g2.setPaint(GaugeCommon.FG_COLOR);
    g2.setStroke(GaugeCommon.STROKE);
    g2.draw(new Rectangle2D.Float(x, y, RECT_WIDTH, RECT_HEIGHT));

    // Draw hour ticks.
    int numberOfTicks = 25;
    int xOffset;
    int i;
    for (i = 0; i < numberOfTicks; i++)
    {
      xOffset = x + i * RECT_WIDTH / (numberOfTicks - 1);
      g2.draw(new Line2D.Float(xOffset, y - 5, xOffset, y));
    }

    // Black out the bar.
    g2.setPaint(Color.BLACK);
    g2.fill(new Rectangle2D.Float(x, y, RECT_WIDTH, RECT_HEIGHT));
    g2.setPaint(GaugeCommon.FG_COLOR);

    boolean inDaylightTime = TimeZone.getDefault().inDaylightTime(new Date());

    // Fill in the sun time.
    float tickWidth = RECT_WIDTH / (numberOfTicks - 1);
    float xStart = (float)(sunMoonRiseSetTimes.getCivilDawnTime(inDaylightTime) * tickWidth);
    float xLength = (float)((sunMoonRiseSetTimes.getCivilDuskTime(inDaylightTime) -
                             sunMoonRiseSetTimes.getCivilDawnTime(inDaylightTime)) * tickWidth);
    g2.setPaint(Color.YELLOW);
    g2.fill(new Rectangle2D.Float(x + xStart, y, x + xLength, RECT_HEIGHT));
    g2.setPaint(GaugeCommon.FG_COLOR);

    // Draw the hour labels.
    for (i = 0; i < (numberOfTicks - 1); i++)
    {
      String labelString = Integer.toString(i);
      if (i > 12)
        labelString = Integer.toString(i - 12);
      else if (i == 12)
        labelString = "Noon";

      int iAdjust = 0;
      if (i > 0 & i < 12)
        iAdjust -= 2;
      else if (i == 12)
        iAdjust -= 4;
      else if (i > 12)
        iAdjust += 5;

      float xAdjust = (tickWidth * i) + iAdjust;
      g2.drawString(labelString, x + xAdjust, y - 8);
    }

    // Draw the moon gauge rectangle.  The x & y represent the upper left corner of the graphic.
    g2.setPaint(GaugeCommon.FG_COLOR);
    g2.setStroke(GaugeCommon.STROKE);
    g2.draw(new Rectangle2D.Float(x, y + RECT_HEIGHT, RECT_WIDTH, RECT_HEIGHT));

    // Black out the bar.
    g2.setPaint(Color.BLACK);
    g2.fill(new Rectangle2D.Float(x, y + RECT_HEIGHT, RECT_WIDTH, RECT_HEIGHT));
    g2.setPaint(GaugeCommon.FG_COLOR);

    // Fill in the moon time.
    g2.setPaint(Color.LIGHT_GRAY);
    double moonriseTime = sunMoonRiseSetTimes.getMoonriseTime(inDaylightTime);
    double moonsetTime = sunMoonRiseSetTimes.getMoonsetTime(inDaylightTime);
    if (moonriseTime < moonsetTime)
    {
      float xMoonStart = (float)(moonriseTime * tickWidth);
      float xMoonLength = (float)(moonsetTime * tickWidth) - xMoonStart;
      g2.fill(new Rectangle2D.Float(xMoonStart, y + RECT_HEIGHT, x + xMoonLength, RECT_HEIGHT));
    }
    else
    {
      float xMoonLength = (float)(RECT_WIDTH - moonriseTime * tickWidth);
      g2.fill(new Rectangle2D.Float((float)(moonriseTime * tickWidth), y + RECT_HEIGHT, xMoonLength, RECT_HEIGHT));

      float xMoonEnd = (float)(moonsetTime * tickWidth);
      g2.fill(new Rectangle2D.Float(x, y + RECT_HEIGHT, xMoonEnd, RECT_HEIGHT));
    }
    g2.setPaint(GaugeCommon.FG_COLOR);
  }

  /**
   * Internal class to generate sun and moon rise/set times.
   */
  private class PeriodicTask implements Runnable
  {
    private SunMoonRiseSetTimes sunMoonRiseSetTimes;

    PeriodicTask(SunMoonRiseSetTimes sunMoonRiseSetTimes)
    {
      this.sunMoonRiseSetTimes = sunMoonRiseSetTimes;
    }

    @Override
    public void run()
    {
      sunMoonRiseSetTimes.calculateTimes(LocalDate.now().getYear(),
                                         LocalDate.now().getMonthValue(),
                                         LocalDate.now().getDayOfMonth());
    }
  }
}
