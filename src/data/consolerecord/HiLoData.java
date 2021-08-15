/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that encapsulates the HiLow data.  This data is sent in one
            buffer from the console.  This data basically contains the min/max
            values and times of min/max values.  This data is sent after a HILOWS
            command.

  Mods:		  09/01/21 Initial Release.
*/
package data.consolerecord;

import java.math.BigInteger;

public class HiLoData
{
  private static final float TENTHS = 10;
  private static final float HUNDREDTHS = 100;
  private static final float THOUSANDTHS = 1000;

  // Private class variables.
  private short dailyLowPressure           = 0;
  private short dailyHighPressure          = 0;
  private short monthlyLowPressure         = 0;
  private short monthlyHighPressure        = 0;
  private short yearlyLowPressure          = 0;
  private short yearlyHighPressure         = 0;
  private short timeOfLowPressure          = 0;
  private short timeOfHighPressure         = 0;
  private byte  dailyHighWindSpeed         = 0;
  private byte  monthlyHighWindSpeed       = 0;
  private byte  yearlyHighWindSpeed        = 0;
  private short timeOfHighWindSpeed        = 0;
  private short dailyHighInsideTemp        = 0;
  private short dailyLowInsideTemp         = 0;
  private short monthlyLowInsideTemp       = 0;
  private short monthlyHighInsideTemp      = 0;
  private short yearlyLowInsideTemp        = 0;
  private short yearlyHighInsideTemp       = 0;
  private short timeOfHighInsideTemp       = 0;
  private short timeOfLowInsideTemp        = 0;
  private byte  dailyHighInsideHumidity    = 0;
  private byte  dailyLowInsideHumidity     = 0;
  private byte  monthlyHighInsideHumidity  = 0;
  private byte  monthlyLowInsideHumidity   = 0;
  private byte  yearlyHighInsideHumidity   = 0;
  private byte  yearlyLowInsideHumidity    = 0;
  private short timeOfHighInsideHumidity   = 0;
  private short timeOfLowInsideHumidity    = 0;
  private short dailyHighOutsideTemp       = 0;
  private short dailyLowOutsideTemp        = 0;
  private short monthlyLowOutsideTemp      = 0;
  private short monthlyHighOutsideTemp     = 0;
  private short yearlyLowOutsideTemp       = 0;
  private short yearlyHighOutsideTemp      = 0;
  private short timeOfHighOutsideTemp      = 0;
  private short timeOfLowOutsideTemp       = 0;
  private short dailyHighDewPoint          = 0;
  private short dailyLowDewPoint           = 0;
  private short monthlyLowDewPoint         = 0;
  private short monthlyHighDewPoint        = 0;
  private short yearlyLowDewPoint          = 0;
  private short yearlyHighDewPoint         = 0;
  private short timeOfHighDewPoint         = 0;
  private short timeOfLowDewPoint          = 0;
  private short dailyLowWindChill          = 0;
  private short monthlyLowWindChill        = 0;
  private short yearlyLowWindChill         = 0;
  private short timeOfLowWindChill         = 0;
  private short dailyHighHeatIndex         = 0;
  private short monthlyHighHeatIndex       = 0;
  private short yearlyHighHeatIndex        = 0;
  private short timeOfHighHeatIndex        = 0;
  private short dailyHighTHSW              = 0;
  private short timeOfDailyHighTHSW        = 0;
  private short monthlyHighTHSW            = 0;
  private short yearlyHighTHSW             = 0;
  private short dailyHighSolarRadiation    = 0;
  private short timeOfHighSolarRadiation   = 0;
  private short monthlyHighSolarRadiation  = 0;
  private short yearlyHighSolarRadiation   = 0;
  private byte  dailyHighUV                = 0;
  private short timeOfDailyHighUV          = 0;
  private byte  monthlyHighUV              = 0;
  private byte  yearlyHighUV               = 0;
  private short dailyHighRainRate          = 0;
  private short timeOfHighRainRate         = 0;
  private short hourlyHighRainRate         = 0;
  private short monthlyHighRainRate        = 0;
  private short yearlyHighRainRate         = 0;
  private byte  dailyLowSoil1Temp          = 0;
  private byte  dailyHighSoil1Temp         = 0;
  private short timeOfLowSoil1Temp         = 0;
  private short timeOfHighSoil1Temp        = 0;
  private byte  monthlyHighSoil1Temp       = 0;
  private byte  monthlyLowSoil1Temp        = 0;
  private byte  yearlyHighSoil1Temp        = 0;
  private byte  yearlyLowSoil1Temp         = 0;
  private byte  dailyLowOutsideHumidity    = 0;
  private byte  dailyHighOutsideHumidity   = 0;
  private byte  monthlyHighOutsideHumidity = 0;
  private byte  monthlyLowOutsideHumidity  = 0;
  private byte  yearlyHighOutsideHumidity  = 0;
  private byte  yearlyLowOutsideHumidity   = 0;
  private short timeOfHighOutsideHumidity  = 0;
  private short timeOfLowOutsideHumidity   = 0;


  /**
   * Method called to set the data in one shot based on the
   * buffer received from the console.
   */
  public void setData (byte[] buffer)
  {
    byte[] tempBuffer = new byte[2];

    // Extract the Daily Low Pressure.
    tempBuffer[0] = buffer[1];
    tempBuffer[1] = buffer[0];
    dailyLowPressure = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Daily High Pressure.
    tempBuffer[0] = buffer[3];
    tempBuffer[1] = buffer[2];
    dailyHighPressure = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly Low Pressure.
    tempBuffer[0] = buffer[5];
    tempBuffer[1] = buffer[4];
    monthlyLowPressure = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly High Pressure.
    tempBuffer[0] = buffer[7];
    tempBuffer[1] = buffer[6];
    monthlyHighPressure = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly Low Pressure.
    tempBuffer[0] = buffer[9];
    tempBuffer[1] = buffer[8];
    yearlyLowPressure = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly High Pressure.
    tempBuffer[0] = buffer[11];
    tempBuffer[1] = buffer[10];
    yearlyHighPressure = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of Low Pressure.
    tempBuffer[0] = buffer[13];
    tempBuffer[1] = buffer[12];
    timeOfLowPressure = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of High Pressure.
    tempBuffer[0] = buffer[15];
    tempBuffer[1] = buffer[14];
    timeOfHighPressure = (short) new BigInteger(tempBuffer).intValue();

    dailyHighWindSpeed = buffer[16];

    // Extract the Time of Wind Speed.
    tempBuffer[0] = buffer[18];
    tempBuffer[1] = buffer[17];
    timeOfHighWindSpeed = (short) new BigInteger(tempBuffer).intValue();

    monthlyHighWindSpeed = buffer[19];
    yearlyHighWindSpeed = buffer[20];

    // Extract the Daily High Inside Temp.
    tempBuffer[0] = buffer[22];
    tempBuffer[1] = buffer[21];
    dailyHighInsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Daily Low Inside Temp.
    tempBuffer[0] = buffer[24];
    tempBuffer[1] = buffer[23];
    dailyLowInsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of High Inside Temp.
    tempBuffer[0] = buffer[26];
    tempBuffer[1] = buffer[25];
    timeOfHighInsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of Low Inside Temp.
    tempBuffer[0] = buffer[28];
    tempBuffer[1] = buffer[27];
    timeOfLowInsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly Low Inside Temp.
    tempBuffer[0] = buffer[30];
    tempBuffer[1] = buffer[29];
    monthlyLowInsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly High Inside Temp.
    tempBuffer[0] = buffer[32];
    tempBuffer[1] = buffer[31];
    monthlyHighInsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly Low Inside Temp.
    tempBuffer[0] = buffer[34];
    tempBuffer[1] = buffer[33];
    yearlyLowInsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly High Inside Temp.
    tempBuffer[0] = buffer[36];
    tempBuffer[1] = buffer[35];
    yearlyHighInsideTemp = (short) new BigInteger(tempBuffer).intValue();

    dailyHighInsideHumidity = buffer[37];
    dailyLowInsideHumidity = buffer[38];
  
    // Extract the Time of High Inside Humidity.
    tempBuffer[0] = buffer[40];
    tempBuffer[1] = buffer[39];
    timeOfHighInsideHumidity = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of Low Inside Humidity.
    tempBuffer[0] = buffer[42];
    tempBuffer[1] = buffer[41];
    timeOfLowInsideHumidity = (short) new BigInteger(tempBuffer).intValue();

    monthlyHighInsideHumidity = buffer[43];
    monthlyLowInsideHumidity = buffer[44];
    yearlyHighInsideHumidity = buffer[45];
    yearlyLowInsideHumidity = buffer[46];

    // Extract the Daily Low Outside Temp.
    tempBuffer[0] = buffer[48];
    tempBuffer[1] = buffer[47];
    dailyLowOutsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Daily High Outside Temp.
    tempBuffer[0] = buffer[50];
    tempBuffer[1] = buffer[49];
    dailyHighOutsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of Low Outside Temp.
    tempBuffer[0] = buffer[52];
    tempBuffer[1] = buffer[51];
    timeOfLowOutsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of High Outside Temp.
    tempBuffer[0] = buffer[54];
    tempBuffer[1] = buffer[53];
    timeOfHighOutsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly High Outside Temp.
    tempBuffer[0] = buffer[56];
    tempBuffer[1] = buffer[55];
    monthlyHighOutsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly Low Outside Temp.
    tempBuffer[0] = buffer[58];
    tempBuffer[1] = buffer[57];
    monthlyLowOutsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly High Outside Temp.
    tempBuffer[0] = buffer[60];
    tempBuffer[1] = buffer[59];
    yearlyHighOutsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly Low Outside Temp.
    tempBuffer[0] = buffer[62];
    tempBuffer[1] = buffer[61];
    yearlyLowOutsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Daily Low Dew Point.
    tempBuffer[0] = buffer[64];
    tempBuffer[1] = buffer[63];
    dailyLowDewPoint = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Daily High Dew Point.
    tempBuffer[0] = buffer[66];
    tempBuffer[1] = buffer[65];
    dailyHighDewPoint = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of Low Dew Point.
    tempBuffer[0] = buffer[68];
    tempBuffer[1] = buffer[67];
    timeOfLowDewPoint = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of High Dew Point.
    tempBuffer[0] = buffer[70];
    tempBuffer[1] = buffer[69];
    timeOfHighDewPoint = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly High Dew Point.
    tempBuffer[0] = buffer[72];
    tempBuffer[1] = buffer[71];
    monthlyHighDewPoint = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly Low Dew Point.
    tempBuffer[0] = buffer[74];
    tempBuffer[1] = buffer[73];
    monthlyLowDewPoint = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly High Dew Point.
    tempBuffer[0] = buffer[76];
    tempBuffer[1] = buffer[75];
    yearlyHighDewPoint = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly Low Dew Point.
    tempBuffer[0] = buffer[78];
    tempBuffer[1] = buffer[77];
    yearlyLowDewPoint = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Daily Low Wind Chill.
    tempBuffer[0] = buffer[80];
    tempBuffer[1] = buffer[79];
    dailyLowWindChill = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of Low Wind Chill.
    tempBuffer[0] = buffer[82];
    tempBuffer[1] = buffer[81];
    timeOfLowWindChill = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly Low Wind Chill.
    tempBuffer[0] = buffer[84];
    tempBuffer[1] = buffer[83];
    monthlyLowWindChill = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly Low Wind Chill.
    tempBuffer[0] = buffer[86];
    tempBuffer[1] = buffer[85];
    yearlyLowWindChill = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Daily High Heat Index.
    tempBuffer[0] = buffer[88];
    tempBuffer[1] = buffer[87];
    dailyHighHeatIndex = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of High Heat Index.
    tempBuffer[0] = buffer[90];
    tempBuffer[1] = buffer[89];
    timeOfHighHeatIndex = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly High Heat Index.
    tempBuffer[0] = buffer[92];
    tempBuffer[1] = buffer[91];
    monthlyHighHeatIndex = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly High Heat Index.
    tempBuffer[0] = buffer[94];
    tempBuffer[1] = buffer[93];
    yearlyHighHeatIndex = (short) new BigInteger(tempBuffer).intValue();
  
    // Extract the day's high THSW.
    tempBuffer[0] = buffer[96];
    tempBuffer[1] = buffer[95];
    dailyHighTHSW = (short) new BigInteger(tempBuffer).intValue();
  
    // Extract the time of day's high THSW.
    tempBuffer[0] = buffer[98];
    tempBuffer[1] = buffer[97];
    timeOfDailyHighTHSW = (short) new BigInteger(tempBuffer).intValue();
  
    // Extract the month's high THSW.
    tempBuffer[0] = buffer[100];
    tempBuffer[1] = buffer[99];
    monthlyHighTHSW = (short) new BigInteger(tempBuffer).intValue();
  
    // Extract the year's high THSW.
    tempBuffer[0] = buffer[102];
    tempBuffer[1] = buffer[101];
    yearlyHighTHSW = (short) new BigInteger(tempBuffer).intValue();
    
    // Extract the Daily High Solar Radiation.
    tempBuffer[0] = buffer[104];
    tempBuffer[1] = buffer[103];
    dailyHighSolarRadiation = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of Daily High Solar Radiation.
    tempBuffer[0] = buffer[106];
    tempBuffer[1] = buffer[105];
    timeOfHighSolarRadiation = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly High Solar Radiation.
    tempBuffer[0] = buffer[108];
    tempBuffer[1] = buffer[107];
    monthlyHighSolarRadiation = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly High Solar Radiation.
    tempBuffer[0] = buffer[110];
    tempBuffer[1] = buffer[109];
    yearlyHighSolarRadiation = (short) new BigInteger(tempBuffer).intValue();
  
    dailyHighUV = buffer[111];
  
    // Extract the Time of Daily High UV.
    tempBuffer[0] = buffer[113];
    tempBuffer[1] = buffer[112];
    timeOfDailyHighUV = (short) new BigInteger(tempBuffer).intValue();
    
    monthlyHighUV = buffer[114];
    yearlyHighUV = buffer[115];
  
    // Extract the Daily High Rain Rate.
    tempBuffer[0] = buffer[117];
    tempBuffer[1] = buffer[116];
    dailyHighRainRate = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of High Rain Rate.
    tempBuffer[0] = buffer[119];
    tempBuffer[1] = buffer[118];
    timeOfHighRainRate = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Hourly High Rain Rate.
    tempBuffer[0] = buffer[121];
    tempBuffer[1] = buffer[120];
    hourlyHighRainRate = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly High Rain Rate.
    tempBuffer[0] = buffer[123];
    tempBuffer[1] = buffer[122];
    monthlyHighRainRate = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly High Rain Rate.
    tempBuffer[0] = buffer[125];
    tempBuffer[1] = buffer[124];
    yearlyHighRainRate = (short) new BigInteger(tempBuffer).intValue();

    // Location 126 - 275 are for extra leaf and soil values.
    dailyLowSoil1Temp = buffer[133];
    dailyHighSoil1Temp = buffer[148];

    // Extract the Time of Low Soil1 Temp.
    tempBuffer[0] = buffer[172];
    tempBuffer[1] = buffer[173];
    timeOfLowSoil1Temp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of High Soil1 Temp.
    tempBuffer[0] = buffer[202];
    tempBuffer[1] = buffer[203];
    timeOfHighSoil1Temp = (short) new BigInteger(tempBuffer).intValue();

    monthlyHighSoil1Temp = buffer[223];
    monthlyLowSoil1Temp = buffer[238];
    yearlyHighSoil1Temp = buffer[253];
    yearlyLowSoil1Temp = buffer[268];

    // There are extra interspersed humidity readings in the next section
    dailyLowOutsideHumidity = buffer[276];
    dailyHighOutsideHumidity = buffer[284];

    // Extract the Time of Low Outside Humidity.
    tempBuffer[0] = buffer[293];
    tempBuffer[1] = buffer[292];
    timeOfLowOutsideHumidity = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time of High Outside Humidity.
    tempBuffer[0] = buffer[309];
    tempBuffer[1] = buffer[308];
    timeOfHighOutsideHumidity = (short) new BigInteger(tempBuffer).intValue();

    // Extract the byte values.
    monthlyHighOutsideHumidity = buffer[324];
    monthlyLowOutsideHumidity  = buffer[332];
    yearlyHighOutsideHumidity  = buffer[340];
    yearlyLowOutsideHumidity   = buffer[348];
    
    // Locations 356 - 395 are for soil moisture readings.
    // Locations 396 - 435 are for leaf wetness readings.
  }

  public float getDailyLowPressure()
  {
    return dailyLowPressure / THOUSANDTHS;
  }

  public short getDailyLowPressureNative()
  {
    return dailyLowPressure;
  }

  public float getDailyHighPressure()
  {
    return dailyHighPressure / THOUSANDTHS;
  }

  public short getDailyHighPressureNative()
  {
    return dailyHighPressure;
  }

  public float getMonthlyLowPressure()
  {
    return monthlyLowPressure / THOUSANDTHS;
  }

  public short getMonthlyLowPressureNative()
  {
    return monthlyLowPressure;
  }

  public float getMonthlyHighPressure()
  {
    return monthlyHighPressure / THOUSANDTHS;
  }

  public short getMonthlyHighPressureNative()
  {
    return monthlyHighPressure;
  }

  public float getYearlyLowPressure()
  {
    return yearlyLowPressure / THOUSANDTHS;
  }

  public short getYearlyLowPressureNative()
  {
    return yearlyLowPressure;
  }

  public float getYearlyHighPressure()
  {
    return yearlyHighPressure / THOUSANDTHS;
  }

  public short getYearlyHighPressureNative()
  {
    return yearlyHighPressure;
  }

  public short getTimeOfLowPressure()
  {
    return timeOfLowPressure;
  }

  public short getTimeOfHighPressure()
  {
    return timeOfHighPressure;
  }

  public byte getDailyHighWindSpeed()
  {
    return dailyHighWindSpeed;
  }

  public byte getMonthlyHighWindSpeed()
  {
    return monthlyHighWindSpeed;
  }

  public byte getYearlyHighWindSpeed()
  {
    return yearlyHighWindSpeed;
  }

  public short getTimeOfHighWindSpeed()
  {
    return timeOfHighWindSpeed;
  }

  public float getDailyLowInsideTemp()
  {
    return dailyLowInsideTemp / TENTHS;
  }

  public short getDailyLowInsideTempNative()
  {
    return dailyLowInsideTemp;
  }

  public float getDailyHighInsideTemp()
  {
    return dailyHighInsideTemp / TENTHS;
  }

  public short getDailyHighInsideTempNative()
  {
    return dailyHighInsideTemp;
  }

  public float getMonthlyLowInsideTemp()
  {
    return monthlyLowInsideTemp / TENTHS;
  }

  public short getMonthlyLowInsideTempNative()
  {
    return monthlyLowInsideTemp;
  }

  public float getMonthlyHighInsideTemp()
  {
    return monthlyHighInsideTemp / TENTHS;
  }

  public short getMonthlyHighInsideTempNative()
  {
    return monthlyHighInsideTemp;
  }

  public float getYearlyLowInsideTemp()
  {
    return yearlyLowInsideTemp / TENTHS;
  }

  public short getYearlyLowInsideTempNative()
  {
    return yearlyLowInsideTemp;
  }

  public float getYearlyHighInsideTemp()
  {
    return yearlyHighInsideTemp / TENTHS;
  }

  public short getYearlyHighInsideTempNative()
  {
    return yearlyHighInsideTemp;
  }

  public short getTimeOfLowInsideTemp()
  {
    return timeOfLowInsideTemp;
  }

  public short getTimeOfHighInsideTemp()
  {
    return timeOfHighInsideTemp;
  }

  public byte getDailyLowInsideHumidity()
  {
    return dailyLowInsideHumidity;
  }

  public byte getDailyHighInsideHumidity()
  {
    return dailyHighInsideHumidity;
  }

  public byte getMonthlyLowInsideHumidity()
  {
    return monthlyLowInsideHumidity;
  }

  public byte getMonthlyHighInsideHumidity()
  {
    return monthlyHighInsideHumidity;
  }

  public byte getYearlyLowInsideHumidity()
  {
    return yearlyLowInsideHumidity;
  }

  public byte getYearlyHighInsideHumidity()
  {
    return yearlyHighInsideHumidity;
  }

  public short getTimeOfLowInsideHumidity()
  {
    return timeOfLowInsideHumidity;
  }

  public short getTimeOfHighInsideHumidity()
  {
    return timeOfHighInsideHumidity;
  }

  public float getDailyLowOutsideTemp()
  {
    return dailyLowOutsideTemp / TENTHS;
  }

  public short getDailyLowOutsideTempNative()
  {
    return dailyLowOutsideTemp;
  }

  public float getDailyHighOutsideTemp()
  {
    return dailyHighOutsideTemp / TENTHS;
  }

  public short getDailyHighOutsideTempNative()
  {
    return dailyHighOutsideTemp;
  }

  public float getMonthlyLowOutsideTemp()
  {
    return monthlyLowOutsideTemp / TENTHS;
  }

  public short getMonthlyLowOutsideTempNative()
  {
    return monthlyLowOutsideTemp;
  }

  public float getMonthlyHighOutsideTemp()
  {
    return monthlyHighOutsideTemp / TENTHS;
  }

  public short getMonthlyHighOutsideTempNative()
  {
    return monthlyHighOutsideTemp;
  }

  public float getYearlyLowOutsideTemp()
  {
    return yearlyLowOutsideTemp / TENTHS;
  }

  public short getYearlyLowOutsideTempNative()
  {
    return yearlyLowOutsideTemp;
  }

  public float getYearlyHighOutsideTemp()
  {
    return yearlyHighOutsideTemp / TENTHS;
  }

  public short getYearlyHighOutsideTempNative()
  {
    return yearlyHighOutsideTemp;
  }

  public short getTimeOfLowOutsideTemp()
  {
    return timeOfLowOutsideTemp;
  }

  public short getTimeOfHighOutsideTemp()
  {
    return timeOfHighOutsideTemp;
  }

  public short getDailyLowDewPoint()
  {
    return dailyLowDewPoint;
  }

  public short getDailyHighDewPoint()
  {
    return dailyHighDewPoint;
  }

  public short getMonthlyLowDewPoint()
  {
    return monthlyLowDewPoint;
  }

  public short getMonthlyHighDewPoint()
  {
    return monthlyHighDewPoint;
  }

  public short getYearlyLowDewPoint()
  {
    return yearlyLowDewPoint;
  }

  public short getYearlyHighDewPoint()
  {
    return yearlyHighDewPoint;
  }

  public short getTimeOfLowDewPoint()
  {
    return timeOfLowDewPoint;
  }

  public short getTimeOfHighDewPoint()
  {
    return timeOfHighDewPoint;
  }

  public short getDailyLowWindChill()
  {
    return dailyLowWindChill;
  }

  public short getMonthlyLowWindChill()
  {
    return monthlyLowWindChill;
  }

  public short getYearlyLowWindChill()
  {
    return yearlyLowWindChill;
  }

  public short getTimeOfLowWindChill()
  {
    return timeOfLowWindChill;
  }

  public short getDailyHighHeatIndex()
  {
    return dailyHighHeatIndex;
  }

  public short getMonthlyHighHeatIndex()
  {
    return monthlyHighHeatIndex;
  }

  public short getYearlyHighHeatIndex()
  {
    return yearlyHighHeatIndex;
  }

  public short getTimeOfHighHeatIndex()
  {
    return timeOfHighHeatIndex;
  }

  public short getDailyHighSolarRadiation()
  {
    return dailyHighSolarRadiation;
  }

  public short getMonthlyHighSolarRadiation()
  {
    return monthlyHighSolarRadiation;
  }

  public short getYearlyHighSolarRadiation()
  {
    return yearlyHighSolarRadiation;
  }

  public short getTimeOfHighSolarRadiation()
  {
    return timeOfHighSolarRadiation;
  }

  public float getDailyHighRainRate()
  {
    return dailyHighRainRate / HUNDREDTHS;
  }

  public short getDailyHighRainRateNative()
  {
    return dailyHighRainRate;
  }

  public float getHourlyHighRainRate()
  {
    return hourlyHighRainRate / HUNDREDTHS;
  }

  public short getHourlyHighRainRateNative()
  {
    return hourlyHighRainRate;
  }

  public float getMonthlyHighRainRate()
  {
    return monthlyHighRainRate / HUNDREDTHS;
  }

  public short getMonthlyHighRainRateNative()
  {
    return monthlyHighRainRate;
  }

  public float getYearlyHighRainRate()
  {
    return yearlyHighRainRate / HUNDREDTHS;
  }

  public short getYearlyHighRainRateNative()
  {
    return yearlyHighRainRate;
  }

  public short getTimeOfHighRainRate()
  {
    return timeOfHighRainRate;
  }

  public byte getDailyLowOutsideHumidity()
  {
    return dailyLowOutsideHumidity;
  }

  public byte getDailyHighOutsideHumidity()
  {
    return dailyHighOutsideHumidity;
  }

  public byte getMonthlyLowOutsideHumidity()
  {
    return monthlyLowOutsideHumidity;
  }

  public byte getMonthlyHighOutsideHumidity()
  {
    return monthlyHighOutsideHumidity;
  }

  public byte getYearlyLowOutsideHumidity()
  {
    return yearlyLowOutsideHumidity;
  }

  public byte getYearlyHighOutsideHumidity()
  {
    return yearlyHighOutsideHumidity;
  }

  public short getTimeOfLowOutsideHumidity()
  {
    return timeOfLowOutsideHumidity;
  }

  public short getTimeOfHighOutsideHumidity()
  {
    return timeOfHighOutsideHumidity;
  }
  
  public short getDailyHighTHSW()
  {
    return dailyHighTHSW;
  }
  
  public short getTimeOfDailyHighTHSW()
  {
    return timeOfDailyHighTHSW;
  }
  
  public short getMonthlyHighTHSW()
  {
    return monthlyHighTHSW;
  }
  
  public short getYearlyHighTHSW()
  {
    return yearlyHighTHSW;
  }
  
  public byte getDailyHighUV()
  {
    return dailyHighUV;
  }
  
  public short getTimeOfDailyHighUV()
  {
    return timeOfDailyHighUV;
  }
  
  public byte getMonthlyHighUV()
  {
    return monthlyHighUV;
  }
  
  public byte getYearlyHighUV()
  {
    return yearlyHighUV;
  }

  public int getDailyLowSoil1Temp()
  {
    return (dailyLowSoil1Temp & 0xFF) - 90;
  }

  public int getDailyHighSoil1Temp()
  {
    return (dailyHighSoil1Temp & 0xFF) - 90;
  }

  public short getTimeOfLowSoil1Temp()
  {
    return timeOfLowSoil1Temp;
  }

  public short getTimeOfHighSoil1Temp()
  {
    return timeOfHighSoil1Temp;
  }

  public int getMonthlyHighSoil1Temp()
  {
    return (monthlyHighSoil1Temp & 0xFF) - 90;
  }

  public int getMonthlyLowSoil1Temp()
  {
    return (monthlyLowSoil1Temp & 0xFF) - 90;
  }

  public int getYearlyHighSoil1Temp()
  {
    return (yearlyHighSoil1Temp & 0xFF) - 90;
  }

  public int getYearlyLowSoil1Temp()
  {
    return (yearlyLowSoil1Temp & 0xFF) - 90;
  }
}
