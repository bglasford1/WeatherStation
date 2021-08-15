/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class used to calculate sun and moon rise/set times.

  Mods:		  09/01/21 Initial Release.
*/
package algorithms;

import util.ConfigProperties;

/**
 * Calculations for moonrise, moonset and also contains sunrise and sunset calculations as well as the twilights.
 */
public class SunMoonRiseSetTimes
{
  private static final ConfigProperties PROPS = ConfigProperties.instance();

  // Rise and set times are in hours.  The fractional part is the minutes
  // dawn/dusk times and strings are [0] = sun, [1] = civil, [2] = nautical, [3] = astronomical
  private double moonriseTime = 0.0;
  private double moonsetTime = 0.0;
  private final double[] dawnTimes = new double[4];
  private final double[] duskTimes = new double[4];

  // The resulting strings.
  private String moonriseString;
  private String moonsetString;
  private final String[] dawnStrings = new String[4];
  private final String[] duskStrings = new String[4];

  private final String always_up = " ****";
  private final String always_down = " ....";

  private static final double LATITUDE = PROPS.getLatitude();
  private static final double LONGITUDE = PROPS.getLongitude();
  private static final double TIMEZONE = PROPS.getTimeZone();

  private final double rads = 0.0174532925;

  /**
   * Method that calculates the values based on year month and day.
   *
   * @param year The year
   * @param month The month
   * @param day The day
   */
  public void calculateTimes(int year, int month, int day)
  {
    double mj = mjd(day, month, year, 0.0);

    find_sun_and_twi_events_for_date(mj);
    find_moonrise_set(mj);
  }

  public double getDaylightHours()
  {
    return duskTimes[0] - dawnTimes[0];
  }

  public double getMoonriseTime(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return moonriseTime + 1;
    }
    else
    {
      return moonriseTime;
    }
  }

  public double getMoonsetTime(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return moonsetTime + 1;
    }
    else
    {
      return moonsetTime;
    }
  }

  public String getMoonriseString(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return hrsmin(moonriseTime + 1);
    }
    else
    {
      return hrsmin(moonriseTime);
    }
  }

  public String getMoonsetString(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return hrsmin(moonsetTime + 1);
    }
    else
    {
      return hrsmin(moonsetTime);
    }
  }

  public double getSunriseTime(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return dawnTimes[0] + 1;
    }
    else
    {
      return dawnTimes[0];
    }
  }

  public double getSunsetTime(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return duskTimes[0] + 1;
    }
    else
    {
      return duskTimes[0];
    }
  }

  public String getSunriseString(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return hrsmin(dawnTimes[0] + 1);
    }
    else
    {
      return hrsmin(dawnTimes[0]);
    }
  }

  public String getSunsetString(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return hrsmin(duskTimes[0] + 1);
    }
    else
    {
      return hrsmin(duskTimes[0]);
    }
  }

  public double getCivilDawnTime(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return dawnTimes[1] + 1;
    }
    else
    {
      return dawnTimes[1];
    }
  }

  public double getCivilDuskTime(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return duskTimes[1] + 1;
    }
    else
    {
      return duskTimes[1];
    }
  }

  public String getCivilDawnString(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return hrsmin(dawnTimes[1] + 1);
    }
    else
    {
      return hrsmin(dawnTimes[1]);
    }
  }

  public String getCivilDuskString(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return hrsmin(duskTimes[1] + 1);
    }
    else
    {
      return hrsmin(duskTimes[1]);
    }
  }

  public double getNauticalDawnTime(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return dawnTimes[2] + 1;
    }
    else
    {
      return dawnTimes[2];
    }
  }

  public double getNauticalDuskTime(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return duskTimes[2] + 1;
    }
    else
    {
      return duskTimes[2];
    }
  }

  public String getNauticalDawnString(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return hrsmin(dawnTimes[2] + 1);
    }
    else
    {
      return hrsmin(dawnTimes[2]);
    }
  }

  public String getNauticalDuskString(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return hrsmin(duskTimes[2] + 1);
    }
    else
    {
      return hrsmin(duskTimes[2]);
    }
  }

  public double getAstroDawnTime(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return dawnTimes[3] + 1;
    }
    else
    {
      return dawnTimes[3];
    }
  }

  public double getAstroDuskTime(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return duskTimes[3] + 1;
    }
    else
    {
      return duskTimes[3];
    }
  }

  public String getAstroDawnString(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return hrsmin(dawnTimes[3] + 1);
    }
    else
    {
      return hrsmin(dawnTimes[3]);
    }
  }

  public String getAstroDuskString(boolean inDaylightTime)
  {
    if (inDaylightTime)
    {
      return hrsmin(duskTimes[3] + 1);
    }
    else
    {
      return hrsmin(duskTimes[3]);
    }
  }

  /**
   *   Takes decimal hours and returns a string in hh:mm am/pm format
   */
  private String hrsmin(double hours)
  {
    double hrs = StrictMath.floor(hours * 60.0 + 0.5) / 60.0;
    double h = Math.floor(hrs);
    double m = Math.floor(60.0 * (hrs - h) + 0.5);
    String adder = " am";

    if (h > 12)
    {
      h = h - 12;
      adder = " pm";
    }
    else if (h == 0)
    {
      h = 12;
    }

    String result;
    if (h < 10)
      result = " " + Integer.toString((int)h);
    else
      result = Integer.toString((int)h);

    result = result + ":";

    if (m < 10)
      result = result + "0" + Integer.toString((int)m);
    else
      result = result + Integer.toString((int)m);
    return result + adder;
  }

  // Returns the integer part - like int() in basic
  private int ipart(double x)
  {
    double a;
    if (x > 0)
      a = StrictMath.floor(x);
    else
      a = StrictMath.ceil(x);
    return (int)a;
  }

  // Returns the fractional part of x as used in minimoon and minisun
  private double frac(double x)
  {
    double a;
    a = x - StrictMath.floor(x);
    if (a < 0)
    {
      a += 1;
    }
    return a;
  }

  // Round rounds the number num to dp decimal places the second line is some C like jiggery pokery I
  // found in an OReilly book which means if dp is null you get 2 decimal places.
  private double round(double num, double dp)
  {
    //   dp = (!dp ? 2: dp);
    return Math.round(num * Math.pow(10, dp)) / Math.pow(10, dp);
  }

  // Returns an angle in degrees in the range 0 to 360
  private double range(double x)
  {
    double a, b;
    b = x / 360;
    a = 360 * (b - ipart(b));
    if (a < 0)
    {
      a = a + 360;
    }
    return a;
  }

  // Takes the day, month, year and hours in the day and returns the modified julian day number defined as
  // mjd = jd - 2400000.5 checked OK for Greg era dates - 26th Dec 02
  private double mjd(double day, double month, double year, double hour)
  {
    if (month <= 2)
    {
      month = month + 12;
      year = year - 1;
    }

    double a = 10000.0 * year + 100.0 * month + day;

    double b;
    if (a <= 15821004.1)
    {
      b = -2 * Math.floor((year + 4716) / 4) - 1179;
    }
    else
    {
      b = Math.floor(year / 400) - Math.floor(year / 100) + Math.floor(year / 4);
    }
    a = 365.0 * year - 679004.0;

    return (a + b + Math.floor(30.6001 * (month + 1)) + day + hour / 24.0);
  }

  //Finds the parabola throuh the three points (-1, ym), (0, yz), (1, yp) and returns the coordinates of the
  // max/min (if any) xe, ye the values of x where the parabola crosses zero (roots of the quadratic)
  // and the number of roots (0, 1 or 2) within the interval [-1, 1].
  // Results passed as array [nz, z1, z2, xe, ye]
  private double[] quad(double ym, double yz, double yp)
  {
    double nz = 0;
    double a = 0.5 * (ym + yp) - yz;
    double b = 0.5 * (yp - ym);
    double xe = -b / (2 * a);
    double ye = (a * xe + b) * xe + yz;
    double dis = b * b - 4.0 * a * yz;

    double z1 = 0.0;
    double z2 = 0.0;
    if (dis > 0)
    {
      double dx = 0.5 * Math.sqrt(dis) / Math.abs(a);
      z1 = xe - dx;
      z2 = xe + dx;
      if (Math.abs(z1) <= 1.0)
      {
        nz += 1;
      }
      if (Math.abs(z2) <= 1.0)
      {
        nz += 1;
      }
      if (z1 < -1.0)
      {
        z1 = z2;
      }
    }

    double[] quadout = new double[5];
    quadout[0] = nz;
    quadout[1] = z1;
    quadout[2] = z2;
    quadout[3] = xe;
    quadout[4] = ye;
    return quadout;
  }

  // Takes the mjd and the longitude (west negative) and then returns the local sidereal time in hours.
  // I'm using Meeus formula 11.4 instead of messing about with UTo and so on
  private double lmst(double mjd)
  {
    double d = mjd - 51544.5;
    double t = d / 36525.0;
    double lst = range(280.46061837 + 360.98564736629 * d + 0.000387933 * t * t - t * t * t / 38710000);

    return (lst / 15.0 + LONGITUDE / 15);
  }

  // Returns the ra and dec of the Sun in an array called suneq[] in decimal hours, degs referred to the equinox
  // of date and using obliquity of the ecliptic at J2000.0 (small error for +- 100 yrs)
  // takes t centuries since J2000.0. Claimed good to 1 arcmin
  private double[] minisun(double t)
  {
    double p2 = 6.283185307;
    double coseps = 0.91748;
    double sineps = 0.39778;

    double M = p2 * frac(0.993133 + 99.997361 * t);
    double DL = 6893.0 * Math.sin(M) + 72.0 * Math.sin(2 * M);
    double L = p2 * frac(0.7859453 + M / p2 + (6191.2 * t + DL) / 1296000);
    double SL = Math.sin(L);
    double X = Math.cos(L);
    double Y = coseps * SL;
    double Z = sineps * SL;
    double RHO = Math.sqrt(1 - Z * Z);
    double dec = (360.0 / p2) * Math.atan(Z / RHO);
    double ra = (48.0 / p2) * Math.atan(Y / (X + RHO));

    if (ra < 0)
    {
      ra += 24;
    }

    double[] suneq = new double[2];
    suneq[0] = dec;
    suneq[1] = ra;
    return suneq;
  }

  // Takes t and returns the geocentric ra and dec in an array mooneq claimed good to 5' (angle) in ra and 1' in dec
  // tallies with another approximate method and with ICE for a couple of dates
  private double[] minimoon(double t)
  {
    double p2 = 6.283185307;
    double arc = 206264.8062;
    double coseps = 0.91748;
    double sineps = 0.39778;

    double L0 = frac(0.606433 + 1336.855225 * t);  // mean longitude of moon
    double L = p2 * frac(0.374897 + 1325.552410 * t); //mean anomaly of Moon
    double LS = p2 * frac(0.993133 + 99.997361 * t); //mean anomaly of Sun
    double D = p2 * frac(0.827361 + 1236.853086 * t); //difference in longitude of moon and sun
    double F = p2 * frac(0.259086 + 1342.227825 * t); //mean argument of latitude

    // corrections to mean longitude in arcsec
    double DL = 22640 * Math.sin(L);
    DL += -4586 * Math.sin(L - 2 * D);
    DL += +2370 * Math.sin(2 * D);
    DL += +769 * Math.sin(2 * L);
    DL += -668 * Math.sin(LS);
    DL += -412 * Math.sin(2 * F);
    DL += -212 * Math.sin(2 * L - 2 * D);
    DL += -206 * Math.sin(L + LS - 2 * D);
    DL += +192 * Math.sin(L + 2 * D);
    DL += -165 * Math.sin(LS - 2 * D);
    DL += -125 * Math.sin(D);
    DL += -110 * Math.sin(L + LS);
    DL += +148 * Math.sin(L - LS);
    DL += -55 * Math.sin(2 * F - 2 * D);

    // simplified form of the latitude terms
    double S = F + (DL + 412 * Math.sin(2 * F) + 541 * Math.sin(LS)) / arc;
    double H = F - 2 * D;
    double N = -526 * Math.sin(H);
    N += +44 * Math.sin(L + H);
    N += -31 * Math.sin(-L + H);
    N += -23 * Math.sin(LS + H);
    N += +11 * Math.sin(-LS + H);
    N += -25 * Math.sin(-2 * L + F);
    N += +21 * Math.sin(-L + F);

    // ecliptic long and lat of Moon in rads
    double L_moon = p2 * frac(L0 + DL / 1296000);
    double B_moon = (18520.0 * Math.sin(S) + N) / arc;

    // equatorial coord conversion - note fixed obliquity
    double CB = Math.cos(B_moon);
    double X = CB * Math.cos(L_moon);
    double V = CB * Math.sin(L_moon);
    double W = Math.sin(B_moon);
    double Y = coseps * V - sineps * W;
    double Z = sineps * V + coseps * W;
    double RHO = Math.sqrt(1.0 - Z * Z);
    double dec = (360.0 / p2) * Math.atan(Z / RHO);
    double ra = (48.0 / p2) * Math.atan(Y / (X + RHO));

    if (ra < 0)
    {
      ra += 24;
    }

    double[] mooneq = new double[2];
    mooneq[0] = dec;
    mooneq[1] = ra;
    return mooneq;
  }

  // This rather mickey mouse function takes a lot of arguments and then returns the sine of the altitude of
  // the object labelled by iobj. iobj = 1 is moon, iobj = 2 is sun
  private double sin_alt(double iobj, double mjd0, double hour, double cglat, double sglat)
  {
    double rads = 0.0174532925;

    double mjd = mjd0 + hour / 24.0;
    double t = (mjd - 51544.5) / 36525.0;

    double[] objpos;
    if (iobj == 1)
    {
      objpos = minimoon(t);
    }
    else
    {
      objpos = minisun(t);
    }

    double ra = objpos[1];
    double dec = objpos[0];

    // hour angle of object
    double tau = 15.0 * (lmst(mjd) - ra);

    // sin(alt) of object using the conversion formulas
    return sglat * StrictMath.sin(rads * dec) +
      cglat * StrictMath.cos(rads * dec) * StrictMath.cos(rads * tau);
  }

  // This is my attempt to encapsulate most of the program in a function
  // then this function can be generalised to find all the Sun events.
  private void find_sun_and_twi_events_for_date(double mjd)
  {
    // Set up the array with the 4 values of sinho needed for the 4
    // kinds of sun event
    double[] sinho = new double[4];
    sinho[0] = StrictMath.sin(rads * -0.833);    //sunset upper limb simple refraction
    sinho[1] = StrictMath.sin(rads * -6.0);    //civil twi
    sinho[2] = StrictMath.sin(rads * -12.0);    //nautical twi
    sinho[3] = StrictMath.sin(rads * -18.0);    //astro twi
    double sglat = StrictMath.sin(rads * LATITUDE);
    double cglat = StrictMath.cos(rads * LATITUDE);
    double date = mjd - TIMEZONE / 24;

    // main loop takes each value of sinho in turn and finds the rise/set
    // events associated with that altitude of the Sun
    int j;
    for (j = 0; j < 4; j++)
    {
      boolean rise = false;
      boolean sett = false;
      boolean above = false;
      double hour = 1.0;

      double ym = sin_alt(2, date, hour - 1.0, cglat, sglat) - sinho[j];

      if (ym > 0.0)
      {
        above = true;
      }

      // the while loop finds the sin(alt) for sets of three consecutive
      // hours, and then tests for a single zero crossing in the interval
      // or for two zero crossings in an interval or for a grazing event
      // The flags rise and sett are set accordingly
      while (hour < 25 && (!sett || !rise))
      {
        double yz = sin_alt(2, date, hour, cglat, sglat) - sinho[j];
        double yp = sin_alt(2, date, hour + 1.0, cglat, sglat) - sinho[j];
        double[] quadout = quad(ym, yz, yp);
        double nz = quadout[0];
        double z1 = quadout[1];
        double z2 = quadout[2];
        double xe = quadout[3];
        double ye = quadout[4];

        // case when one event is found in the interval
        if (nz == 1)
        {
          if (ym < 0.0)
          {
            dawnTimes[j] = hour + z1;
            rise = true;
          }
          else
          {
            duskTimes[j] = hour + z1;
            sett = true;
          }
        }

        // case where two events are found in this interval
        // (rare but whole reason we are not using simple iteration)
        if (nz == 2)
        {
          if (ye < 0.0)
          {
            dawnTimes[j] = hour + z2;
            duskTimes[j] = hour + z1;
          }
          else
          {
            dawnTimes[j] = hour + z1;
            duskTimes[j] = hour + z2;
          }
        }

        // set up the next search interval
        ym = yp;
        hour += 2.0;
      }

      if (rise || sett)
      {
        if (rise)
          dawnStrings[j] = hrsmin(dawnTimes[j]);
        else
          dawnStrings[j] = " ----";

        if (sett)
          duskStrings[j] = hrsmin(duskTimes[j]);
        else
          duskStrings[j] = " ----";
      }
      else
      {
        if (above)
        {
          dawnStrings[j] = always_up;
          duskStrings[j] = always_up;
        }
        else
        {
          dawnStrings[j] = always_down;
          duskStrings[j] = always_down;
        }
      }
    }
  }

  // A separate function is used for moonrise/set to allow for different tabulations
  // of moonrise and sun events i.e. weekly for sun and daily for moon. The logic of
  // the function is identical to find_sun_and_twi_events_for_date()
  private void find_moonrise_set(double mjd)
  {
    double sinho = Math.sin(rads * 8.0 / 60.0);    //moonrise taken as centre of moon at +8 arcmin
    double sglat = Math.sin(rads * LATITUDE);
    double cglat = Math.cos(rads * LATITUDE);
    double date = mjd - TIMEZONE / 24.0;

    boolean rise = false;
    boolean sett = false;
    boolean above = false;
    double hour = 1.0;

    double ym = sin_alt(1, date, hour - 1.0, cglat, sglat) - sinho;

    if (ym > 0.0)
    {
      above = true;
    }

    while (hour < 25 && (!sett || !rise))
    {
      double yz = sin_alt(1, date, hour, cglat, sglat) - sinho;
      double yp = sin_alt(1, date, hour + 1.0, cglat, sglat) - sinho;

      double[] quadout = quad(ym, yz, yp);

      double nz = quadout[0];
      double z1 = quadout[1];
      double z2 = quadout[2];
      double xe = quadout[3];
      double ye = quadout[4];

      // case when one event is found in the interval
      if (nz == 1)
      {
        if (ym < 0.0)
        {
          moonriseTime = hour + z1;
          rise = true;
        }
        else
        {
          moonsetTime = hour + z1;
          sett = true;
        }
      }

      // case where two events are found in this interval
      // (rare but whole reason we are not using simple iteration)
      if (nz == 2)
      {
        if (ye < 0.0)
        {
          moonriseTime = hour + z2;
          moonsetTime = hour + z1;
        }
        else
        {
          moonriseTime = hour + z1;
          moonsetTime = hour + z2;
        }
      }

      // set up the next search interval
      ym = yp;
      hour += 2.0;
    }

    if (rise || sett)
    {
      if (rise)
        moonriseString = hrsmin(moonriseTime);
      else
        moonriseString = " ----";

      if (sett)
        moonsetString = hrsmin(moonsetTime);
      else
        moonsetString = " ----";
    }
    else
    {
      if (above)
      {
        moonriseString = always_up;
        moonsetString = always_up;
      }
      else
      {
        moonriseString = always_down;
        moonsetString = always_down;
      }
    }
  }

  /**
   * Test main
   */
  public static void main(String[] args)
  {
    boolean inDaylightTime = true;
    SunMoonRiseSetTimes sunMoonRiseSetTimes = new SunMoonRiseSetTimes();
    sunMoonRiseSetTimes.calculateTimes(2019, 2, 10);
    System.out.println("Moonrise: " + sunMoonRiseSetTimes.getMoonriseString(inDaylightTime));
    System.out.println("Moonset: " + sunMoonRiseSetTimes.getMoonsetString(inDaylightTime));
    System.out.println("Sunrise: " + sunMoonRiseSetTimes.getSunriseString(inDaylightTime));
    System.out.println("Sunset: " + sunMoonRiseSetTimes.getSunsetString(inDaylightTime));
    System.out.println("Civil Dawn: " + sunMoonRiseSetTimes.getCivilDawnString(inDaylightTime));
    System.out.println("Civil Dusk: " + sunMoonRiseSetTimes.getCivilDuskString(inDaylightTime));
    System.out.println("Nautical Dawn: " + sunMoonRiseSetTimes.getNauticalDawnString(inDaylightTime));
    System.out.println("Nautical Dusk: " + sunMoonRiseSetTimes.getNauticalDuskString(inDaylightTime));
    System.out.println("Astro Dawn: " + sunMoonRiseSetTimes.getAstroDawnString(inDaylightTime));
    System.out.println("Astro Dusk: " + sunMoonRiseSetTimes.getAstroDuskString(inDaylightTime));
  }
}
