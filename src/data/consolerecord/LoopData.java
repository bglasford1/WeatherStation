/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that encapsulates the loop data.  The loop data is the current
            data values on the console.  The data is sent after a LOOP command
            is issued.  The Loop data format is Rev "A".

  Mods:		  09/01/21 Initial Release.
            10/18/21  Got Daily Solar Energy working.
*/
package data.consolerecord;

import util.ByteUtil;

import java.math.BigInteger;

public class LoopData
{
  private static final float TENTHS = 10;
  private static final float HUNDREDTHS = 100;
  private static final float THOUSANDTHS = 1000;

  // Inside alarm values are bit values.  More than one bit can be set.
  public static final byte FALLING_BAR_TREND_ALARM = (byte)0x01;
  public static final byte RISING_BAR_TREND_ALARM = (byte)0x02;
  public static final byte LOW_INSIDE_TEMP_ALARM = (byte)0x04;
  public static final byte HIGH_INSIDE_TEMP_ALARM = (byte)0x08;
  public static final byte LOW_INSIDE_HUMID_ALARM = (byte)0x10;
  public static final byte HIGH_INSIDE_HUMID_ALARM = (byte)0x20;
  public static final byte TIME_ALARM = (byte)0x40;
  
  // Rain alarm values are bit values.  More than one bit can be set.
  public static final byte HIGH_RAIN_RATE_ALARM = (byte)0x01;
  public static final byte FIFTEEN_MINUTE_RAIN_RATE_ALARM = (byte)0x02;
  public static final byte TWENTY_FOUR_HOUR_RAIN_RATE_ALARM = (byte)0x04;
  public static final byte STORM_TOTAL_ALARM = (byte)0x10;
  public static final byte DAILY_ET_ALARM = (byte)0x20;
  
  // Outside alarm values, byte 1 are bit values.  More than one bit can be set.
  public static final byte LOW_OUTSIDE_TEMP_ALARM = (byte)0x01;
  public static final byte HIGH_OUTSIDE_TEMP_ALARM = (byte)0x02;
  public static final byte WIND_SPEED_ALARM = (byte)0x04;
  public static final byte TEN_MINUTE_WIND_SPEED_ALARM = (byte)0x08;
  public static final byte LOW_DEWPOINT_ALARM = (byte)0x10;
  public static final byte HIGH_DEWPOINT_ALARM = (byte)0x20;
  public static final byte HIGH_HEAT_ALARM = (byte)0x40;
  public static final byte LOW_WIND_CHILL_ALARM = (byte)0x80;
  
  // Outside alarm values, byte 2 are bit values.  More than one bit can be set.
  public static final byte HIGH_THSW_ALARM = (byte)0x01;
  public static final byte HIGH_SOLAR_RADIATION_ALARM = (byte)0x02;
  public static final byte HIGH_UV_ALARM = (byte)0x04;
  public static final byte UV_DOSE_ALARM = (byte)0x08;
  public static final byte UV_DOSE_ALARM_ENABLED = (byte)0x10;
  
  // Outside humidity alarm values are bit values.  More than one bit can be set.
  public static final byte LOW_OUT_HUMIDITY_ALARM = (byte)0x01;
  public static final byte HIGH_OUT_HUMIDITY_ALARM = (byte)0x02;

  // Soil temp alarm values.
  public static final byte LOW_SOIL1_TEMP_ALARM = (byte)0x01;
  public static final byte HIGH_SOIL1_TEMP_ALARM = (byte)0x02;
  
  // See pressure trend values above.
  private byte  pressureTrend      = 0;
  // Location of next record in archive.  Can use to detect when next record is written.
  private short nextRecord         = 0;
  private short pressure           = 0;
  private short insideTemp         = 0;
  private byte  insideHumidity     = 0;
  private short outsideTemp        = 0;
  private byte  windSpeed          = 0;
  private byte  averageWindSpeed   = 0;
  private short windDirection      = 0;
  private byte  soilTemp1          = 0;
  private byte  outsideHumidity    = 0;
  private short rainRate           = 0;
  private byte  uv                 = 0;
  private short solarRadiation     = 0; // In Watts/m2
  private short stormRate          = 0;
  private int   startStormDateDay  = 0;
  private int   startStormDateMonth = 0;
  private int   startStormDateYear = 0;
  private boolean noStormDate = true;
  private short dailyRain          = 0;
  private short monthlyRain        = 0;
  private short yearlyRain         = 0;
  private short dailyET            = 0;
  private short monthlyET          = 0;
  private short yearlyET           = 0;
  private byte  insideAlarms       = 0;
  private byte  rainAlarms         = 0;
  private byte  outsideAlarmsByte1 = 0;
  private byte  outsideAlarmsByte2 = 0;
  private byte  outsideHumidAlarms = 0;
  private byte  extraTempAlarms1   = 0;
  private byte  transBatteryStatus = 0;
  private short consoleBatteryVolt = 0;
  private byte  forecastIcons      = 0;
  private byte  forecastRuleNumber = 0;
  private short sunriseTime        = 0;
  private short sunsetTime         = 0;


  /**
   * Method called to set the data in one shot based on the
   * buffer received from the console.
   */
  public void setData (byte[] buffer)
  {
    byte[] tempBuffer = new byte[2];

    // Extract the barometer trend.
    pressureTrend = buffer[3];

    // location 3 is packet type.  0 = LOOP, 1 = LOOP2.  Old console = 0, new console = ???
  
    // Extract the next record location.
    tempBuffer[0] = buffer[6];
    tempBuffer[1] = buffer[5];
    nextRecord = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Pressure.
    tempBuffer[0] = buffer[8];
    tempBuffer[1] = buffer[7];
    pressure = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Inside Temp.
    tempBuffer[0] = buffer[10];
    tempBuffer[1] = buffer[9];
    insideTemp = (short) new BigInteger(tempBuffer).intValue();
  
    insideHumidity = buffer[11];
  
    // Extract the Outside Temp.
    tempBuffer[0] = buffer[13];
    tempBuffer[1] = buffer[12];
    outsideTemp = (short) new BigInteger(tempBuffer).intValue();
  
    windSpeed        = buffer[14];
    averageWindSpeed = buffer[15]; // 10 minute avg wind speed
  
    // Extract the Wind Direction
    tempBuffer[0] = buffer[17];
    tempBuffer[1] = buffer[16];
    windDirection = (short) new BigInteger(tempBuffer).intValue();

    // locations 18-24 are for up to 7 extra temperature stations.
    // locations 25-28 are for up to 4 soil temperature sensor.
    soilTemp1 = buffer[25];
    // locations 29-32 are for up to 4 leaf temperature sensor.
  
    outsideHumidity = buffer[33];
    // locations 34-40 are for extra humidity sensors.
  
    // Extract the Rain Rate.
    tempBuffer[0] = buffer[42];
    tempBuffer[1] = buffer[41];
    rainRate = (short) new BigInteger(tempBuffer).intValue();
    
    uv = buffer[43];

    // Extract the Solar Radiation.
    tempBuffer[0] = buffer[45];
    tempBuffer[1] = buffer[44];
    solarRadiation = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Storm Rate.
    tempBuffer[0] = buffer[47];
    tempBuffer[1] = buffer[46];
    stormRate = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Storm Date.
    if (buffer[48] == (byte)0xFF && buffer[49] == (byte)0xFF)
    {
      noStormDate = true;
    }
    else
    {
      noStormDate = false;
      startStormDateMonth = ByteUtil.getStormDateMonth(buffer[49]);
      startStormDateYear = ByteUtil.getStormDateYear(buffer[48]);
      startStormDateDay = ByteUtil.getStormDateDay(new byte[]{buffer[48], buffer[49]});
    }

    // Extract the Daily Rain.
    tempBuffer[0] = buffer[51];
    tempBuffer[1] = buffer[50];
    dailyRain = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Monthly Rain.
    tempBuffer[0] = buffer[53];
    tempBuffer[1] = buffer[52];
    monthlyRain = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Yearly Rain.
    tempBuffer[0] = buffer[55];
    tempBuffer[1] = buffer[54];
    yearlyRain = (short) new BigInteger(tempBuffer).intValue();

    // Extract the daily evapotransporation.
    tempBuffer[0] = buffer[57];
    tempBuffer[1] = buffer[56];
    dailyET = (short) new BigInteger(tempBuffer).intValue();
  
    // Extract the monthly evapotransporation.
    tempBuffer[0] = buffer[59];
    tempBuffer[1] = buffer[58];
    monthlyET = (short) new BigInteger(tempBuffer).intValue();
  
    // Extract the yearly evapotransporation.
    tempBuffer[0] = buffer[61];
    tempBuffer[1] = buffer[60];
    yearlyET = (short) new BigInteger(tempBuffer).intValue();
  
    // locations 62-65 are for up to 4 soil moisture sensors.
    // locations 66-69 are for up to 4 leaf wetness sensors.
  
    insideAlarms = buffer[70];
    rainAlarms = buffer[71];
    outsideAlarmsByte1 = buffer[72];
    outsideAlarmsByte2 = buffer[73];
    outsideHumidAlarms = buffer[74];
  
    // locations 75-81 are for extra temperature and humidity alarms.
    extraTempAlarms1 = buffer[75];

    // locations 82-85 are for leaf and soil alarms.
    
    transBatteryStatus = buffer[86];
  
    // Extract the Console Battery Voltage.
    tempBuffer[0] = buffer[88];
    tempBuffer[1] = buffer[87];
    consoleBatteryVolt = (short) new BigInteger(tempBuffer).intValue();

    forecastIcons = buffer[89];
    forecastRuleNumber = buffer[90];

    // Extract the Sunrise Time.
    tempBuffer[0] = buffer[92];
    tempBuffer[1] = buffer[91];
    sunriseTime = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Sunset Time.
    tempBuffer[0] = buffer[94];
    tempBuffer[1] = buffer[93];
    sunsetTime = (short) new BigInteger(tempBuffer).intValue();
  }

  /**
   * Method used to get the pressure trend.  This is a general
   * trend value.
   */
  public String getPressureTrend()
  {
    switch (pressureTrend)
    {
      case -60:
        return "Falling Rapidly";
      case -20:
        return "Falling Slowly";
      case 0:
        return "Steady";
      case 20:
        return "Rising Slowly";
      case 60:
        return "Rising Rapidly";
      default:
        return "No Data Available";
    }
  }

  public float getPressure()
  {
    return pressure / THOUSANDTHS;
  }

  public short getPressureNative()
  {
    return pressure;
  }

  public float getInsideTemp()
  {
    return insideTemp / TENTHS;
  }

  public short getInsideTempNative()
  {
    return insideTemp;
  }

  public byte getInsideHumidity()
  {
    return insideHumidity;
  }

  public float getOutsideTemp()
  {
    return outsideTemp / TENTHS;
  }

  public short getOutsideTempNative()
  {
    return outsideTemp;
  }

  public byte getWindSpeed()
  {
    return windSpeed;
  }

  public byte getAverageWindSpeed()
  {
    return averageWindSpeed;
  }

  public short getWindDirection()
  {
    return windDirection;
  }

  public int getSoilTemp1()
  {
    return (soilTemp1 & 0xFF) - 90;
  }

  public byte getOutsideHumidity()
  {
    return outsideHumidity;
  }

  public float getRainRate()
  {
    return rainRate / HUNDREDTHS;
  }

  public short getRainRateNative()
  {
    return rainRate;
  }

  public short getSolarRadiation()
  {
    return solarRadiation;
  }

  public float getStormRate()
  {
    return stormRate / HUNDREDTHS;
  }

  public short getStormRateNative()
  {
    return stormRate;
  }

  public String getStartStormDate()
  {
    if (noStormDate)
      return " No Storm";
    else
      return startStormDateMonth + "/" + startStormDateDay + "/" + startStormDateYear;
  }

  public float getDailyRain()
  {
    return dailyRain / HUNDREDTHS;
  }

  public short getDailyRainNative()
  {
    return dailyRain;
  }

  public float getMonthlyRain()
  {
    return monthlyRain / HUNDREDTHS;
  }

  public short getMonthlyRainNative()
  {
    return monthlyRain;
  }

  public float getYearlyRain()
  {
    return yearlyRain / HUNDREDTHS;
  }

  public short getYearlyRainNative()
  {
    return yearlyRain;
  }

  public byte getTransBatteryStatus()
  {
    return transBatteryStatus;
  }

  public float getConsoleBatteryVolt()
  {
    return consoleBatteryVolt * 300.0f/512.0f/100.0f;
  }

  public short getConsoleBatteryVoltNative()
  {
    return consoleBatteryVolt;
  }

  public short getNextRecord()
  {
    return nextRecord;
  }
  
  public byte getUv()
  {
    return uv;
  }

  public float getDailyET()
  {
    return dailyET / THOUSANDTHS;
  }

  public short getDailyETNative()
  {
    return dailyET;
  }

  public float getMonthlyET()
  {
    return monthlyET / HUNDREDTHS;
  }

  public short getMonthlyETNative()
  {
    return monthlyET;
  }

  public float getYearlyET()
  {
    return yearlyET / HUNDREDTHS;
  }

  public short getYearlyETNative()
  {
    return yearlyET;
  }
  
  public byte getInsideAlarms()
  {
    return insideAlarms;
  }
  
  public byte getRainAlarms()
  {
    return rainAlarms;
  }
  
  public byte getOutsideAlarmsByte1()
  {
    return outsideAlarmsByte1;
  }
  
  public byte getOutsideAlarmsByte2()
  {
    return outsideAlarmsByte2;
  }

  public byte getOutsideHumidAlarms()
  {
    return outsideHumidAlarms;
  }

  public byte getExtraTempAlarms1()
  {
    return extraTempAlarms1;
  }
  
  public byte getForecastIcons()
  {
    return forecastIcons;
  }
  
  public byte getForecastRuleNumber()
  {
    return forecastRuleNumber;
  }
  
  public String getSunriseTime()
  {
    // Convert sunriseTime to a string
    int hour   = this.sunriseTime / 100;
    int minute = this.sunriseTime - hour * 100;
    String minuteString = "" + minute;
    if (minuteString.length() == 1)
      minuteString = "0" + minuteString;

    return hour + ":" + minuteString;
  }

  public String getSunsetTime()
  {
    // Convert sunriseTime to a string
    int hour   = this.sunsetTime / 100;
    int minute = this.sunsetTime - hour * 100;
    String minuteString = "" + minute;
    if (minuteString.length() == 1)
      minuteString = "0" + minuteString;

    return hour + ":" + minuteString;
  }
  
}
