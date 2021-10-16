/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that encapsulates the standard archive record.  This data
            mainly supports the generation of graphs and strip charts.

  Mods:		  09/01/21  Initial Release.
            10/15/21  Fixed ET calculation.
*/
package data.dbrecord;

import java.time.LocalDateTime;

public class WeatherRecord extends DataFileRecord
{
  private static final float TENTHS = 10;
  private static final float HUNDREDTHS = 100;
  private static final float THOUSANDTHS = 1000;

  private LocalDateTime timestamp;

  private byte   archiveInterval; // Number of minutes that this record represents
  private short  packedTime; // minutes past midnight of the end of the archive period
  private short  outsideTemp;
  private short  highOutsideTemp;
  private short  lowOutsideTemp;
  private short  insideTemp;
  private short  pressure;
  private short  outsideHumidity;
  private short  insideHumidity;
  private short  rainfall; // Native is number of clicks, 1/100th of an inch
  private short  highRainRate;
  private short  averageWindSpeed;
  private short  highWindSpeed;
  private byte   windDirection; // A value of 0xFF means no data.
  private byte   highWindDirection; // A value of 0xFF means no data.
  private short  numOfWindSamples;
  private short  solarRadiation;
  private short  highSolarRadiation;
//  private short UV;
//  private short hiUV;
//  private short leafTemp1, leafTemp2, leafTemp3, leafTemp4;
//  private int extraRad;
  private byte   forecast;
  private short  et;
  private byte   soilTemp1; // This is a short and not a byte to get the math to work.
//  private short soilTemp2;
//  private short soilTemp3;
//  private short soilTemp4;
//  private short soilTemp5;
//  private short soilTemp6;
//  private short soilMoisture1;
//  private short soilMoisture2;
//  private short soilMoisture3;
//  private short soilMoisture4;
//  private short soilMoisture5;
//  private short soilMoisture6;
//  private short leafWetness1;
//  private short leafWetness2;
//  private short leafWetness3;
//  private short leafWetness4;
//  private short extraTemp1;
//  private short extraTemp2;
//  private short extraTemp3;
//  private short extraTemp4;
//  private short extraTemp5;
//  private short extraTemp6;
//  private short extraTemp7;
//  private short extraHum1;
//  private short extraHum2;
//  private short extraHum3;
//  private short extraHum4;
//  private short extraHum5;
//  private short extraHum6;
//  private short extraHum7;

  public static final int DATA_TYPE_OFFSET        = 0;
  public static final int ARCHIVE_INTERVAL_OFFSET = 1;
  public static final int ICON_FLAGS_OFFSET       = 2;
  public static final int MORE_FLAGS_OFFSET       = 3;
  public static final int PACKED_TIME_OFFSET      = 4;
  public static final int OUTSIDE_TEMP_OFFSET     = 6;
  public static final int HI_OUTSIDE_TEMP_OFFSET  = 8;
  public static final int LOW_OUTSIDE_TEMP_OFFSET = 10;
  public static final int INSIDE_TEMP_OFFSET      = 12;
  public static final int BAROMETER_OFFSET        = 14;
  public static final int OUTSIDE_HUMID_OFFSET    = 16;
  public static final int INSIDE_HUMID_OFFSET     = 18;
  public static final int RAIN_OFFSET             = 20;
  public static final int HI_RAIN_RATE_OFFSET     = 22;
  public static final int WIND_SPEED_OFFSET       = 24;
  public static final int HI_WIND_SPEED_OFFSET    = 26;
  public static final int WIND_DIR_OFFSET         = 28;
  public static final int HI_WIND_DIR_OFFSET      = 29;
  public static final int NUM_WIND_SAMPLES_OFFSET = 30;
  public static final int SOLAR_RAD_OFFSET        = 32;
  public static final int HI_SOLAR_OFFSET         = 34;
  public static final int UV_OFFSET               = 36;
  public static final int HI_UV_OFFSET            = 37;
  public static final int FORECAST_OFFSET         = 56;
  public static final int ET_OFFSET               = 57;
  public static final int SOIL_TEMP_1_OFFSET      = 58;


  public LocalDateTime getTimestamp()
  {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp)
  {
    this.timestamp = timestamp;
  }

  public byte getArchiveInterval()
  {
    return archiveInterval;
  }

  public void setArchiveInterval(byte archiveInterval)
  {
    this.archiveInterval = archiveInterval;
  }

  public short getPackedTime()
  {
    return packedTime;
  }

  public void setPackedTime(short packedTime)
  {
    this.packedTime = packedTime;
  }

  public float getOutsideTemp()
  {
    return outsideTemp / TENTHS;
  }

  public short getOutsideTempNative()
  {
    return outsideTemp;
  }

  public void setOutsideTemp (float outsideTemp)
  {
    this.outsideTemp = (short)(outsideTemp * TENTHS);
  }

  public void setOutsideTempNative (short outsideTemp)
  {
    this.outsideTemp = outsideTemp;
  }

  public float getHighOutsideTemp()
  {
    return highOutsideTemp / TENTHS;
  }

  public short getHighOutsideTempNative()
  {
    return highOutsideTemp;
  }

  public void setHighOutsideTemp (float highOutsideTemp)
  {
    this.highOutsideTemp = (short)(highOutsideTemp * TENTHS);
  }

  public void setHighOutsideTempNative (short highOutsideTemp)
  {
    this.highOutsideTemp = highOutsideTemp;
  }

  public float getLowOutsideTemp()
  {
    return lowOutsideTemp / TENTHS;
  }

  public short getLowOutsideTempNative()
  {
    return lowOutsideTemp;
  }

  public void setLowOutsideTemp (float lowOutsideTemp)
  {
    this.lowOutsideTemp = (short)(lowOutsideTemp * TENTHS);
  }

  public void setLowOutsideTempNative (short lowOutsideTemp)
  {
    this.lowOutsideTemp = lowOutsideTemp;
  }

  public float getInsideTemp()
  {
    return insideTemp / TENTHS;
  }

  public short getInsideTempNative()
  {
    return insideTemp;
  }

  public void setInsideTemp (float insideTemp)
  {
    this.insideTemp = (short)(insideTemp * TENTHS);
  }

  public void setInsideTempNative (short insideTemp)
  {
    this.insideTemp = insideTemp;
  }

  public short getNumOfWindSamples()
  {
    return numOfWindSamples;
  }

  public void setNumOfWindSamples (short numOfWindSamples)
  {
    this.numOfWindSamples = numOfWindSamples;
  }

  public float getPressure()
  {
    return pressure / THOUSANDTHS;
  }

  public short getPressureNative()
  {
    return pressure;
  }

  public void setPressure (float pressure)
  {
    this.pressure = (short)(pressure * THOUSANDTHS);
  }

  public void setPressureNative (short pressure)
  {
    this.pressure = pressure;
  }

  public float getRainfall()
  {
    return rainfall / HUNDREDTHS;
  }

  // Add in the rain collector type, which is hardcoded to 0.01 inches.
  public short getRainfallNative()
  {
    return (short)(rainfall | 0x1000);
  }

  public void setRainfall (float rainfall)
  {
    this.rainfall = (short)(rainfall * HUNDREDTHS);
  }

  public void setRainfallNative (short rainfall)
  {
    this.rainfall = rainfall;
  }

  public float getHighRainRate()
  {
    return highRainRate / HUNDREDTHS;
  }

  public short getHighRainRateNative()
  {
    return highRainRate;
  }

  public void setHighRainRate (float highRainRate)
  {
    this.highRainRate = (short)(highRainRate * HUNDREDTHS);
  }

  public void setHighRainRateNative (short highRainRate)
  {
    this.highRainRate = highRainRate;
  }

  public float getOutsideHumidity()
  {
    return outsideHumidity / TENTHS;
  }

  public short getOutsideHumidityNative()
  {
    return outsideHumidity;
  }

  public void setOutsideHumidity (float outsideHumidity)
  {
    this.outsideHumidity = (short)(outsideHumidity * TENTHS);
  }

  public void setOutsideHumidityNative (short outsideHumidity)
  {
    this.outsideHumidity = outsideHumidity;
  }

  public float getInsideHumidity()
  {
    return insideHumidity / TENTHS;
  }

  public short getInsideHumidityNative()
  {
    return insideHumidity;
  }

  public void setInsideHumidity (float insideHumidity)
  {
    this.insideHumidity = (short)(insideHumidity * TENTHS);
  }

  public void setInsideHumidityNative (short insideHumidity)
  {
    this.insideHumidity = insideHumidity;
  }

  public float getAverageWindSpeed()
  {
    return averageWindSpeed / TENTHS;
  }

  public short getAverageWindSpeedNative()
  {
    return averageWindSpeed;
  }

  public void setAverageWindSpeed (float averageWindSpeed)
  {
    this.averageWindSpeed = (short)(averageWindSpeed * TENTHS);
  }

  public void setAverageWindSpeedNative (short averageWindSpeed)
  {
    this.averageWindSpeed = averageWindSpeed;
  }

  public float getHighWindSpeed()
  {
    return highWindSpeed / TENTHS;
  }

  public short getHighWindSpeedNative()
  {
    return highWindSpeed;
  }

  public void setHighWindSpeed (float highWindSpeed)
  {
    this.highWindSpeed = (short)(highWindSpeed * TENTHS);
  }

  public void setHighWindSpeedNative (short highWindSpeed)
  {
    this.highWindSpeed = highWindSpeed;
  }

  public WindDirection getHighWindDirection()
  {
    if (highWindDirection < 15 && highWindDirection >= 0)
      return WindDirection.values()[highWindDirection];
    else
      return null;
  }

  public byte getHighWIndDirectionNative()
  {
    return highWindDirection;
  }

  public void setHighWindDirection (byte highWindDirection)
  {
    this.highWindDirection = highWindDirection;
  }

  public void setHighWindDirectionNative (byte highWindDirection)
  {
    this.highWindDirection = highWindDirection;
  }

  public short getHighSolarRadiation()
  {
    return highSolarRadiation;
  }

  public void setHighSolarRadiation (short highSolarRadiation)
  {
    this.highSolarRadiation = highSolarRadiation;
  }

  public WindDirection getWindDirection()
  {
    if (windDirection <= 15 && windDirection >= 0)
      return WindDirection.values()[windDirection];
    else
      return null;
  }

  public byte getWindDirectionNative()
  {
    return windDirection;
  }

  public void setWindDirection(byte windDirection)
  {
    this.windDirection = windDirection;
  }

  public void setWindDirectionNative(byte windDirection)
  {
    this.windDirection = windDirection;
  }

  public short getSolarRadiation()
  {
    return solarRadiation;
  }

  public void setSolarRadiation(short solarRadiation)
  {
    this.solarRadiation = solarRadiation;
  }

  public byte getForecast()
  {
    return forecast;
  }

  public void setForecast(byte forecast)
  {
    this.forecast = forecast;
  }

  public void setEt(short evapotranspiration)
  {
    this.et = evapotranspiration;
  }

  public short getEt()
  {
    return et;
  }

  public byte getSoilTemp1()
  {
    return (byte)(soilTemp1 - 90);
  }

  public byte getSoilTemp1Native()
  {
    return soilTemp1;
  }

  public void setSoilTemp1(byte soilTemp1)
  {
    this.soilTemp1 = (byte)((soilTemp1 & 0x00FF) + 90);
  }

  public void setSoilTemp1Native(byte soilTemp1)
  {
    this.soilTemp1 = (byte)(soilTemp1 & 0x00FF);
  }

  @Override
  public String toString()
  {
    return "Weather Record:\n  " +
      "Date: " + this.timestamp.toString() + "\n  " +
      "Archive Interval: " + getArchiveInterval() + "\n  " +
      "Packed Time: " + this.packedTime + "\n  " +
      "Out Temp: " + getOutsideTemp() + "\n  " +
      "High Out Temp: " + getHighOutsideTemp() + "\n  " +
      "Low Out Temp: " + getLowOutsideTemp() + "\n  " +
      "In Temp: " + getInsideTemp() + "\n  " +
      "Pressure: " + getPressure() + "\n  " +
      "Out Humid: " + getOutsideHumidity() + "\n  " +
      "In Humid: " + getInsideHumidity() + "\n  " +
      "Rain: " + getRainfall() + "\n  " +
      "High Rain Rate: " + getHighRainRate() + "\n  " +
      "Wind Speed: " + getAverageWindSpeed() + "\n  " +
      "Wind Dir: " + getWindDirection() + "\n  " +
      "High Wind Speed: " + getHighWindSpeed() + "\n  " +
      "High Wind Dir: " + getHighWindDirection() + "\n  " +
      "Number Wind Samples: " + getNumOfWindSamples() + "\n  " +
      "Solar Rad: " + this.solarRadiation + "\n  " +
      "High Solar Rad: " + this.highSolarRadiation + "\n  " +
      "Forecast: " + this.forecast + "\n  " +
      "ET: " + this.et + "\n  " +
      "Soil Temp 1: " + getSoilTemp1() + "\n  ";
  }
}
