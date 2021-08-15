/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class used to calculate wind chill, dew point and heat index.

  Mods:		  09/01/21 Initial Release.
*/
package algorithms;

import util.ConfigProperties;

import java.lang.StrictMath;
import java.time.*;

public class Calculations
{
  private static final ConfigProperties PROPS = ConfigProperties.instance();

  // Wind component of THSW and THW calculations.
  // First index is temp; start at 50 and increment by 5.
  // Second index is wind speed; start at 0, increment by 5.
  private static final int[][] WIND_MATRIX = new int[][]{
    { 0, -2, -4, -5, -6, -7, -8, -9, -9 },
    { 0, -1, -3, -5, -6, -7, -8, -9, -9 },
    { 0, -1, -3, -5, -6, -7, -8, -9, -9 },
    { 0,  0, -3, -5, -6, -7, -8, -9, -9 },
    { 0,  0, -2, -4, -5, -6, -7, -8, -9 },
    { 0,  0, -2, -3, -4, -5, -6, -7, -7 },
    { 0,  0, -1, -2, -3, -5, -5, -6, -6 },
    { 0,  0, -1, -2, -3, -3, -4, -4, -4 },
    { 0,  0,  0, -1, -2, -2, -2, -2, -2 },
    { 0,  0,  0,  0,  0,  0,  1,  1,  1 },
    { 0,  0,  0,  0,  1,  2,  3,  3,  3 },
    { 0,  0,  0,  1,  2,  3,  4,  5,  5 },
    { 0,  0,  0,  2,  3,  4,  5,  5,  6 },
    { 0,  0,  0,  1,  2,  3,  4,  6,  6 },
    { 0,  0,  0,  1,  1,  2,  3,  4,  4 },
    { 0,  0,  0,  0,  0,  1,  1,  1,  1 },
    { 0,  0,  0,  0,  0,  0,  0,  0,  0 }
  };

  /**
   * Calculate the wind chill based on the current temperature and wind speed.
   *
   * @param temperature Current temp in degrees F.
   * @param windSpeed Current wind speed in MPH.
   * @return The wind chill in degrees F.
   */
  public static float calculateWindChill (float temperature, float windSpeed)
  {
    if (temperature > 50 || windSpeed < 3.0)
      return temperature;

    double speed = StrictMath.pow(windSpeed, 0.16);
    double windChill = 35.74 + 0.6215 * temperature - 35.75 * speed + 0.4275 * temperature * speed;

    return (float)windChill;
  }

  /**
   * Calculate heat index based on the current temperature and humidity.
   *
   * @param temperature Current temperature in degrees F.
   * @param humidity Current % relative humidity.
   * @return The heat index in degrees F.
   */
  public static float calculateHeatIndex (float temperature, float humidity)
  {
    if (temperature < 80)
    {
      // If temperature is less than 80 then calculate a simple heat index and average with the temperature
      return (float)((temperature + (0.5 * (temperature + 61.0 + ((temperature - 68.0) * 1.2) + humidity * 0.094))) / 2.0);
    }

    double tempSquared  = temperature * temperature;
    double humidSquared = humidity * humidity;

    double heatIndex = -42.379 + 2.04901523 * temperature
      + 10.14333127 * humidity
      - 0.22475541 * temperature * humidity
      - 0.00683783 * tempSquared
      - 0.05481717 * humidSquared
      + 0.00122874 * humidity * tempSquared
      + 0.00085282 * temperature * humidSquared
      - 0.00000199 * tempSquared * humidSquared;

    if (humidity < 13 && (80 < temperature) && (temperature < 112))
    {
      return (float)(heatIndex - ((13.0 - humidity) / 4.0) * Math.sqrt(((17.0 - Math.abs(temperature - 95.0)) / 17.0)));
    }

    if ((humidity > 85) && (80 < temperature) && (temperature < 87))
    {
      return (float)(heatIndex + ((humidity - 85.0) / 10.0) * ((87.0 - temperature) / 5.0));
    }

    return (float)heatIndex;
  }

  /**
   * Calculate dew point based on the current temperature and humidity.
   *
   * @param temperature The current temperature in degrees Fahrenheit.
   * @param humidity The current % relative humidity.
   * @return The dew point in degrees F.
   */
  public static float calculateDewPoint (float temperature, float humidity)
  {
    double tempC = (temperature - 32.0) / 1.8;
    double gamma = (Math.log10(humidity) - 2.0) / 0.4343 + (17.62 * tempC) / (243.12 + tempC);
    double dewPointC = 243.12 * gamma / (17.62 - gamma);
    return (float)(dewPointC * 1.8 + 32.0);
  }

  /**
   * Calculate the wet bulb temperature based on the current temperature and humidity.
   *
   * @param temperature The current temperature in degrees Fahrenheit.
   * @param humidity The current % relative humidity.
   * @return The wet bulb temperature in degrees F.
   */
  public static float calculateWetBulbTemperature (float temperature, float humidity)
  {
    double tempC = (temperature - 32.0) / 1.8;
    double wetBulbC = tempC * Math.atan(0.151977 * Math.pow((humidity + 8.313659), 0.5)) +
      Math.atan(tempC + humidity) - Math.atan(humidity - 1.676331) +
      0.00391838 * Math.pow(humidity, 1.5) * Math.atan(0.023101 * humidity) - 4.686035;
    return (float)(wetBulbC * 1.8 + 32.0);
  }

  /**
   * Calculate the reference ET for a one hour period.  The measurements passed in are the average for a one hour
   * period.  The weather station is not sophisticated enough to calculate an actual ET.  The actual ET can be
   * calculated by multiplying the reference ET by a crop coefficient (Kc).
   *
   * TODO: Each time the temperature is sampled (5 minute intervals), the value of the saturation vapor pressure and
   * actual water vapor pressure are calculated from the current values of temperature and humidity and sampled.
   * These vapor pressure values (in kPa) are used to compute the average saturation vapor pressure and the
   * average water vapor pressure for the hour.
   *
   * @param temperature Average outside temperature in degrees F.
   * @param windSpeed Average wind speed in MPH.
   * @param solarRad Average solar radiation in W/m2.
   * @param humidity Average percent humidity.
   * @param pressure Average atmospheric pressure in inches of mercury.
   * @return The reference ET value.
   */
  public static float calculateET (float temperature, float windSpeed, float solarRad, float humidity, float pressure)
  {
    float tempC = (temperature - 32) * 5 / 9;
    double tempK = tempC + 273.16;
    double pressureKPa = pressure * 33.864;
    double windSpeedMPS = windSpeed * 0.44704;
    double saturatedWaterVP = 0.6108 * Math.exp(17.27 * tempC / (tempC + 237.3)); // in kPa
    double actualWaterVaporPressure = saturatedWaterVP * humidity / 100;
    double vaporCurveSlope = saturatedWaterVP / tempK * (6790.4985 / tempK - 5.02808);
    double psychometricConstant = 0.000646 * (1 + 0.000946 * tempC) * pressureKPa;
    double weightingFactor = vaporCurveSlope / (vaporCurveSlope + psychometricConstant);
    double windFunction;
    if (solarRad > 0)
    {
      windFunction = 0.030 + 0.0576 * windSpeedMPS;
    }
    else
    {
      windFunction = 0.125 * 0.0439 * windSpeedMPS;
    }
    double latentHeatVaporization = 694.5 * (1 - 0.000946 * tempC);

    SunCalculations sunCalculations = new SunCalculations();
    sunCalculations.performCalculations(LocalTime.now());
    double solarElevationAngle = sunCalculations.getSolarElevationAngle();
    double solarZenithAngle = sunCalculations.getSolarZenithAngle();

    double irradiance = 1360.8 * Math.toRadians(solarZenithAngle);
    double clearSkyRad = (0.79 - 3.75 / Math.toRadians(solarZenithAngle)) * irradiance; // TODO: not correct, check units.
    double surfaceAlbedo = 0.26;
    if (solarRad / irradiance >= 0.375)
    {
      surfaceAlbedo = 0.00158 * solarElevationAngle + 0.386 * Math.exp(-0.0188 * solarElevationAngle);
    }
    // Pressure is converted to millibars
    double clearSkyEmissivity = 1.08 * (1 - Math.exp(-Math.pow(pressure * 33.8639, tempK / 2016.0)));
    double temporary = (1.333 - 1.333 * solarRad / clearSkyRad);
    double cloudCoverFraction = Math.pow((1.333 - 1.333 * solarRad / clearSkyRad), 0.294);
    double stefanBotzmannConstant = 0.0000000567; // Watts per meter squared per tempK to the fourth power.

    double rn = 0.89 * ((1 - surfaceAlbedo) * solarRad +
      saturatedWaterVP * clearSkyEmissivity * (1 - cloudCoverFraction) * stefanBotzmannConstant * Math.pow(tempK, 4) +
      cloudCoverFraction * stefanBotzmannConstant * Math.pow(tempK, 4) -
      0.98 * stefanBotzmannConstant * Math.pow(tempK, 4));

    return (float)(weightingFactor * rn / latentHeatVaporization +
      (1 - weightingFactor) * (saturatedWaterVP - actualWaterVaporPressure) * windFunction);
  }

  /**
   * Get the THW (Temperature-Humidity-Wind index.  This was developed by Steadman (1979).  This is an
   * expansion on the heat index that includes a wind component.
   *
   * @param temperature  The current temperature in degrees Fahrenheit.
   * @param windSpeed The current wind speed in miles per hour.
   * @param humidity The current % relative humidity.
   * @return The THW index.
   */
  public static float calculateTHW(float temperature, float windSpeed, float humidity)
  {
    // Start with the heat index.
    float baseTemp = calculateHeatIndex(temperature, humidity);

    // Now add the wind component.
    return baseTemp + getTHSWWindComponent(temperature, windSpeed);
  }

  /**
   * Get the THSW (Temperature-Humidity-Sun-Wind index.  This was developed by Steadman (1979).  This is an
   * expansion on the heat index that includes sun and wind components.
   *
   * @param temperature  The current temperature in degrees Fahrenheit.
   * @param windSpeed The current wind speed in miles per hour.
   * @param humidity The current % relative humidity.
   * @param solarRad The current solar radiation.
   * @return The THSW index.
   */
  public static float calculateTHSW(float temperature, float windSpeed, float humidity, float solarRad)
  {
    // Start with the THW index.
//    System.out.println("Temp: " + temperature);
    double baseTemp = calculateHeatIndex(temperature, humidity);
//    System.out.println("Base Temp: " + baseTemp);

    // Now add the wind component.
    baseTemp = baseTemp + getTHSWWindComponent(temperature, windSpeed);
//    System.out.println("Base Temp + Wind: " + baseTemp);

    //***
    //*** Now add the solar component which consists of 4 components: Q1 + Q2 + Q3 + Q4
    //***
    // Calculate clear sky radiation
    SunCalculations sunCalculations = new SunCalculations();
    sunCalculations.performCalculations(LocalTime.now());
    double clearSkyRad = sunCalculations.getClearSkyRad();
//    System.out.println("Clear Sky Radiation: " + clearSkyRad);
    // TODO: this is lower than the actual solar radiation.
    // TODO: look to make it a table look-up from actual clear sky data versus time.

    // Calculate the Sky Cover, c.
    double c;
    if (clearSkyRad < solarRad)
    {
      clearSkyRad = solarRad;
    }
    c = Math.pow(Math.E, Math.log((solarRad / clearSkyRad + 1) / 0.75) / 0.29412) / 100; // TODO: this is horked up...
//    System.out.println("C: " + c);

    // Normalize the solar radiation.
    double solarElevationAngle = sunCalculations.getSolarElevationAngle();
    double solarRadNormal;
    if (c > .6)
    {
      solarRadNormal = solarRad;
    }
    else
    {
      double angelSquared = solarElevationAngle * solarElevationAngle;
      double angleCubed = angelSquared * solarElevationAngle;
      solarRadNormal = (0.000005 * angleCubed - 0.0002 * angelSquared + 0.0029 * solarElevationAngle + 1) * solarRad;
    }

    double bodyAreaFactor;
    if (solarElevationAngle < 2)
      bodyAreaFactor = 0.11;
    else if (solarElevationAngle > 70)
      bodyAreaFactor = 0.325;
    else
      bodyAreaFactor = 0.386 - 0.0032 * (90 - solarElevationAngle);

    // Calculate the Direct Incoming Solar Radiation Term (Q1)
    double Q1 = 0.56 * solarRadNormal * bodyAreaFactor;

    // Calculate the Indirect Incoming Solar Radiation Term (Q2)
    double Q2 = 0.224 * 0.1 * solarRadNormal * (1.0 - c * c);

    // Calculate the Terrestrial Radiation (Q3)
    double Q3 = 0.028 * solarRad;

    // Calculate the Sky Radiation (Q4)
    float latitude = PROPS.getLatitude();
    double elevation = PROPS.getElevation() * 0.0003048; // in kilometeres
    double tempC = (temperature - 32.0) * 5/9;
    double vaporPressure = 0.6112 * Math.exp(17.62 * tempC / (tempC + 243.12)); // in kPa
    double Q4 = 150.0 * (1.0 - c * c * (0.5 - 0.0043 * latitude)) * (1.0 - 0.62 * Math.exp(-0.108 * elevation) -
        0.16 * (Math.pow(vaporPressure, 0.5)));

    double Qg = Q1 + Q2 + Q3 - Q4;

    if (windSpeed < 7)
      Qg = 0.101 * Qg;
    else
      Qg = 1.10 * Qg / (8.0 + 0.45 * windSpeed);

    return (float)(baseTemp + Qg);
  }

  private static int getTHSWWindComponent(float temp, float windSpeed)
  {
    int tempIndex = 0;
    if (temp < 50)
    {
      // TODO: implement, see THSW Index Davis sheet. "use the wind chill calculation as the base temperature."
      // "For WeatherLink 5.2 and newer: use the new heat index formula as the base temperature and calculate the
      // wind chill increment using the difference between the air temperature and the wind chill (which is
      // always a negative number)."
    }
    else if (temp >= 50 && temp <= 52.5)
      tempIndex = 0;
    else if (temp > 52.5 && temp <= 57.5)
      tempIndex = 1;
    else if (temp > 57.5 && temp <= 62.5)
      tempIndex = 2;
    else if (temp > 62.5 && temp <= 67.5)
      tempIndex = 3;
    else if (temp > 67.5 && temp <= 72.5)
      tempIndex = 4;
    else if (temp > 72.5 && temp <= 77.5)
      tempIndex = 5;
    else if (temp > 77.5 && temp <= 82.5)
      tempIndex = 6;
    else if (temp > 82.5 && temp <= 87.5)
      tempIndex = 7;
    else if (temp > 87.5 && temp <= 92.5)
      tempIndex = 8;
    else if (temp > 92.5 && temp <= 97.5)
      tempIndex = 9;
    else if (temp > 97.5 && temp <= 102.5)
      tempIndex = 10;
    else if (temp > 102.5 && temp <= 107.5)
      tempIndex = 11;
    else if (temp > 107.5 && temp <= 112.5)
      tempIndex = 12;
    else if (temp > 112.5 && temp <= 117.5)
      tempIndex = 13;
    else if (temp > 117.5 && temp <= 122.5)
      tempIndex = 14;
    else if (temp > 122.5 && temp <= 127.5)
      tempIndex = 15;
    else if (temp > 127.5)
      tempIndex = 16;

    int windIndex = 0;
    if (windSpeed > 2.5 && windSpeed <= 7.5)
      windIndex = 1;
    else if (windSpeed > 7.5 && windSpeed <= 12.5)
      windIndex = 2;
    else if (windSpeed > 12.5 && windSpeed <= 17.5)
      windIndex = 3;
    else if (windSpeed > 17.5 && windSpeed <= 22.5)
      windIndex = 4;
    else if (windSpeed > 22.5 && windSpeed <= 27.5)
      windIndex = 5;
    else if (windSpeed > 27.5 && windSpeed <= 32.5)
      windIndex = 6;
    else if (windSpeed > 32.5 && windSpeed <= 37.5)
      windIndex = 7;
    else if (windSpeed > 37.5)
      windIndex = 8;

    return WIND_MATRIX[tempIndex][windIndex];
  }

  /**
   * Calculate the moon phase.  This uses a simplified method by Ben Danglish.  This returns a phase from 0 to 29 where
   *  0=new moon, 15=full etc.
   *
   * @return The moon phase.
   */
  public static int calculateMoonPhase()
  {
    // Calculate UTC, taking into account daylight savings time.
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

    // Calculate days since Jan 1st 2000 at 12:00 midnight.
    ZoneOffset offset = now.getOffset();
    OffsetDateTime baseline =
      OffsetDateTime.of(1970, 1, 7, 20, 35, 0, 0, offset);
    Duration duration = Duration.between (baseline , now);
    long seconds = duration.toMillis() / 1000;

    // Calculate the phase.
    return (int)Math.floor((seconds % 2551443) / (24 * 3600)) + 1;
  }

  private static float getMoonPercentIllumination()
  {
    // TODO: implement...
    return 0.0f;
  }

  public static void main(String[] args)
  {
    float temperature = 26.6f;
    float windSpeed = 3.0f;
    float humidity = 30;
    float solarRad = 564.453f;
    float pressure = 29.999f;
    System.out.println("wind chill = " + Float.toString(calculateWindChill(temperature, windSpeed)));
    System.out.println("heat index = " + Float.toString(calculateHeatIndex(temperature, humidity)));
    System.out.println("dew point = " + Float.toString(calculateDewPoint (temperature, humidity)));
    System.out.println("wet bulb temp = " + Float.toString(calculateWetBulbTemperature(temperature, humidity)));
    System.out.println("ET = " + Float.toString(calculateET(temperature, windSpeed, solarRad, humidity, pressure)));
    System.out.println("Wind Component: " + getTHSWWindComponent(temperature, windSpeed));
    System.out.println("THW index: " + calculateTHW(temperature, windSpeed, humidity));
    System.out.println("THSW index: " + calculateTHSW(temperature, windSpeed, humidity, solarRad));
    System.out.println("Moon Phase = " + calculateMoonPhase());
    System.out.println("Moon Phase Illumination = " + getMoonPercentIllumination());
  }
}
