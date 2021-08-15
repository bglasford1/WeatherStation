/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class used to calculate various sun parameters, eventually calculating
            sunrise and sunset times.

  Mods:		  09/01/21 Initial Release.
*/
package algorithms;

import util.ConfigProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class SunCalculations
{
  private static final ConfigProperties PROPS = ConfigProperties.instance();

  // Save calculated intermediate values for later retrieval.
  private LocalTime sunriseTime;
  private LocalTime sunsetTime;
  private double solarZenithAngle;
  private double solarElevationAngle;

  /**
   * Time is represented as a fraction of a day.  Ex: 0.5 is noon. or time = SECOND(A1)/60+MINUTE(A1)+HOUR(A1)*60
   *
   * @param fractionalDay The fraction of a day.
   * @return The local time
   */
  private LocalTime convertToTime(double fractionalDay)
  {
    double hoursOfDay = fractionalDay * 24;
    int hours = (int)(hoursOfDay);
    double minutesOfDay = (hoursOfDay - hours) * 60;
    int minutes = (int)(minutesOfDay);
    int seconds = (int)Math.ceil((minutesOfDay - minutes) * 60);
    if (seconds == 60)
    {
      minutes++;
      seconds = 0;
    }
    return LocalTime.of(hours, minutes, seconds);
  }

  /**
   * Get the fractional day value based on a local time value. Ex: 0.5 is noon.  Hours, minutes and seconds
   * are converted.
   *
   * @param time The time as LocalTime.
   * @return The fractional day value.
   */
  private double convertToFractionalDay(LocalTime time)
  {
    double fractionalDay = time.getSecond() / 60;
    fractionalDay = (fractionalDay + time.getMinute()) / 60;
    return (fractionalDay + time.getHour()) / 24;
  }

  /**
   * Perform the calculations. This generates all the values which can then be retrieved.
   * The dates are based on OpenOffice epoch which is 30 December 1899.
   *
   * @param time The time of day, no daylight savings time. Only hour and minute are used.
   */
  public void performCalculations(LocalTime time)
  {
    double latitude = PROPS.getLatitude();
    double longitude = PROPS.getLongitude();
    int timeZone = PROPS.getTimeZone();


    try
    {
      // The dates are based on OpenOffice epoch which is 30 December 1899.
      LocalDateTime epoch = LocalDateTime.of(1899, 12, 30, 12, 0);
      LocalDateTime now = LocalDate.now().atTime(time.getHour(), time.getMinute());

//    System.out.println("Date: " + now.toString());
      long diffInDays = ChronoUnit.DAYS.between(epoch, now);
      double julianDays = diffInDays + 2415018.5 + 1 + convertToFractionalDay(time) - timeZone / 24.0;
//    System.out.println("JulianDays: " + julianDays);
      double julianCentury = (julianDays - 2451545.0) / 36525.0;
//    System.out.println("JulianCentury: " + julianCentury);

      // Geom Mean Long Sun (deg)
      double geoMeanLong = (280.46646 + julianCentury * (36000.76983 + julianCentury * 0.0003032)) % 360;
//    System.out.println("Geo Mean Long: " + geoMeanLong);

      // Geom Mean Anom Sun (deg)
      double geoMeanAnomaly = 357.52911 + julianCentury * (35999.05029 - 0.0001537 * julianCentury);
//    System.out.println("Geo Mean Anomaly: " + geoMeanAnomaly);

      // Eccent Earth Orbit
      double eccentricEarthOrbit = 0.016708634 - julianCentury * (0.000042037 + 0.0000001267 * julianCentury);
//    System.out.println("Eccentric Earth Orbit: " + eccentricEarthOrbit);

      // Sun Eq of Ctr
      double eqOfCenter = Math.sin(Math.toRadians(geoMeanAnomaly)) *
        (1.914602 - julianCentury * (0.004817 + 0.000014 * julianCentury)) +
        Math.sin(Math.toRadians(2 * geoMeanAnomaly)) * (0.019993 - 0.000101 * julianCentury) +
        Math.sin(Math.toRadians(3 * geoMeanAnomaly)) * 0.000289;
//    System.out.println("Eq of Center: " + eqOfCenter);

      // Sun True Longitude (deg)
      double trueLongitude = geoMeanLong + eqOfCenter;
//    System.out.println("Sun True Long: " + trueLongitude);

      // Sun True Anomaly (deg)
      double trueAnomaly = geoMeanAnomaly + eqOfCenter;
//    System.out.println("Sun True Anomaly: " + trueAnomaly);

      // Sun Rad Vector (AUs)
      double radVector = (1.000001018 * (1.0 - eccentricEarthOrbit * eccentricEarthOrbit)) /
        (1.0 + eccentricEarthOrbit * Math.cos(Math.toRadians(trueAnomaly)));
//    System.out.println("Sun Rad Vector: " + radVector);

      // Sun Apparent Longitude (deg)
      double apparentLongitude =
        trueLongitude - 0.00569 - 0.00478 * Math.sin(Math.toRadians(125.04 - 1934.136 * julianCentury));
//    System.out.println("Apparent Longitude: " + apparentLongitude);

      // Mean Oblique Ecliptic (deg)
      double meanObliqueEcliptic = 23.0 + (26.0 + ((21.448 - julianCentury * (46.815 + julianCentury *
        (0.00059 - julianCentury * 0.001813)))) / 60.0) / 60.0;
//    System.out.println("Mean Oblique Ecliptic: " + meanObliqueEcliptic);

      // Oblique Correction (deg)
      double obliqueCorrection = meanObliqueEcliptic + 0.00256 * Math.cos(Math.toRadians(125.04 - 1934.136 * julianCentury));
//    System.out.println("Oblique Correction: " + obliqueCorrection);

      // Sun Right Ascension (deg)
      double rightAscension = Math.toDegrees(Math.atan2(Math.cos(Math.toRadians(obliqueCorrection)) *
                                                          Math.sin(Math.toRadians(apparentLongitude)),
                                             Math.cos(Math.toRadians(apparentLongitude))));
//    System.out.println("Right Ascension: " + rightAscension);

      // Sun Declination (deg)
      double declination = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(obliqueCorrection)) *
                                                      Math.sin(Math.toRadians(apparentLongitude))));
//    System.out.println("Declination: " + declination);

      // Var y
      double varY = Math.tan(Math.toRadians(obliqueCorrection / 2.0)) * Math.tan(Math.toRadians(obliqueCorrection / 2.0));
//    System.out.println("Var y: " + varY);

      // Equation of Time (minutes)
      double equationOfTime = 4.0 * Math.toDegrees(varY * Math.sin(2 * Math.toRadians(geoMeanLong)) - 2.0 *
        eccentricEarthOrbit * Math.sin(Math.toRadians(geoMeanAnomaly)) + 4.0 * eccentricEarthOrbit * varY *
        Math.sin(Math.toRadians(geoMeanAnomaly)) * Math.cos(2.0 * Math.toRadians(geoMeanLong)) - 0.5 * varY * varY *
        Math.sin(4.0 * Math.toRadians(geoMeanLong)) - 1.25 * eccentricEarthOrbit * eccentricEarthOrbit *
        Math.sin(2.0 * Math.toRadians(geoMeanAnomaly)));
//    System.out.println("Equation Of Time: " + equationOfTime);

      // Hour Angle Sunrise (deg)
      double hourAngleSunrise = Math.toDegrees(
        Math.acos(Math.cos(Math.toRadians(90.833)) / (Math.cos(Math.toRadians(latitude)) *
          Math.cos(Math.toRadians(declination))) -
                    Math.tan(Math.toRadians(latitude)) * Math.tan(Math.toRadians(declination))));
//    System.out.println("Hour Angle Sunrise: " + hourAngleSunrise);

      // Solar Noon (LST)
      double solarNoon = (720.0 - 4.0 * longitude - equationOfTime + timeZone * 60.0) / 1440.0;
//    System.out.println("Solar Noon: " + solarNoon);
//    LocalTime solarNoonTime = convertToTime(solarNoon);
//    System.out.println("Solar Noon Time: " + solarNoonTime.toString());

      // Sunrise Time (LST)
      double sunrise = (solarNoon * 1440.0 - hourAngleSunrise * 4.0) / 1440.0;
//    System.out.println("Sunrise: " + sunrise);
      sunriseTime = convertToTime(sunrise);
//    System.out.println("Sunrise Time: " + sunriseTime.toString());

      // Sunset Time (LST)
      double sunset = (solarNoon * 1440.0 + hourAngleSunrise * 4.0) / 1440.0;
//    System.out.println("Sunset: " + sunset);
      sunsetTime = convertToTime(sunset);
//    System.out.println("Sunset Time: " + sunsetTime.toString());

      // True Solar Time (min)
      double trueSolarTime =
        (convertToFractionalDay(time) * 1440.0 + equationOfTime + 4.0 * longitude - 60.0 * timeZone) % 1440.0;
//    System.out.println("True Solar Time: " + trueSolarTime);

      // Hour Angle (deg)
      double hourAngle;
      if (trueSolarTime / 4.0 < 0)
        hourAngle = trueSolarTime / 4.0 + 180.0;
      else
        hourAngle = trueSolarTime / 4.0 - 180.0;
//    System.out.println("Hour Angle: " + hourAngle);

      // Solar Zenith Angle (deg)
      solarZenithAngle = Math.toDegrees(Math.acos(
        Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(declination)) +
          Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(declination)) *
            Math.cos(Math.toRadians(hourAngle))));
//    System.out.println("Solar Zenith Angle: " + solarZenithAngle);

      // Solar Elevation (deg)
      solarElevationAngle = 90.0 - solarZenithAngle;
//    System.out.println("Solar Elevation: " + solarElevationAngle);
    }
    catch (Exception e)
    {
      // TODO: there are still errors occasionally due to time errors.
      e.printStackTrace();
    }
  }

  /**
   * Get clear sky solar radiation estimate for a given time of day.  The calculation of solar elevation angle
   * must be re-calculated based on time of day.  This is based on the EPA[1971] model.
   */
  public double getClearSkyRad()
  {
    double angleSquared = solarElevationAngle * solarElevationAngle;
    double angleCubed = angleSquared * solarElevationAngle;
    double angleFourth = angleCubed * solarElevationAngle;
    return 24.0 * (2.044 * solarElevationAngle + 0.1296 * angleSquared -
      0.001941 * angleCubed + 0.000007591 * angleFourth) * 0.1314;
  }

  /**
   * Get the sunrise time.
   *
   * @return The sunrise time.
   */
  private LocalTime getSunriseTime()
  {
    return sunriseTime;
  }

  /**
   * Get the sunset time.
   *
   * @return the sunset time.
   */
  private LocalTime getSunsetTime()
  {
    return sunsetTime;
  }

  /**
   * Get the solar zenith angle in degrees.  This is the angle of the sun from zenith or high noon.
   *
   * @return The angle in degrees.
   */
  public double getSolarZenithAngle()
  {
    return solarZenithAngle;
  }

  /**
   * Get the solar elevation angle in degrees.  This is the angle of the sun above the horizon.
   *
   * @return The angle in degrees.
   */
  public double getSolarElevationAngle()
  {
    return solarElevationAngle;
  }

  public static void main(String[] args)
  {
    SunCalculations calculations = new SunCalculations();
    LocalTime time = LocalTime.of(12, 0);
    calculations.performCalculations(time);
    System.out.println("Sunrise Time: " + calculations.getSunriseTime());
    System.out.println("Sunset Time: " + calculations.getSunsetTime());
  }
}
