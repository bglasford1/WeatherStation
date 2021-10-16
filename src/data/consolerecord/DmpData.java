/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that encapsulates the DMP data.  The DMP data is the data that
            is archived on the console.  This data mainly supports the generation
            of graphs and strip charts.  This data is returned from DMP and DMPAFT
            commands.  This class only deals with Rev "B" of the record.
            Rev "A" is an older version that is not supported.

  Mods:		  09/01/21  Initial Release.
            10/15/21  Fixed ET calculation.
*/
package data.consolerecord;

import util.TimeUtil;

import java.math.BigInteger;

public class DmpData
{
  private static final float TENTHS = 10;
  private static final float HUNDREDTHS = 100;
  private static final float THOUSANDTHS = 1000;

  private short dateStamp          = 0;
  private short timeStamp          = 0;
  private short outsideTemp        = 0;
  private short highOutsideTemp    = 0;
  private short lowOutsideTemp     = 0;
  private short rainfall           = 0;
  private short highRainRate       = 0;
  private short pressure           = 0;
  private short solarRadiation     = 0;
  private short numOfWindSamples   = 0;
  private short insideTemp         = 0;
  private byte  insideHumidity     = 0;
  private byte  outsideHumidity    = 0;
  private byte  averageWindSpeed   = 0;
  private byte  highWindSpeed      = 0;
  private byte  highWindDirection  = 0;
  private byte  prevailingWindDir  = 0;
  private byte  averageUV          = (byte)255; // no sensor
  private byte  evapotranspiration = 0;
  // Records from here down are the Rev "B" variant.
  private short highSolarRadiation = 0;
  private byte  highUVIndex        = (byte)255; // no sensor
  private byte  forecastRule       = 0;
  private byte  soilTemp1          = 0;
  private byte  recordType         = 0; // 0xFF = RevA, 0x00 = RevB,  This should always be 0x00.

  /**
   * Method called to set the data in one shot based on the buffer received from the console.
   * Note that the fields with no sensor are not implemented.
   */
  public void setData (byte[] buffer)
  {
    byte[] tempBuffer = new byte[2];

    // Extract the Date values.
    tempBuffer[0] = buffer[1];
    tempBuffer[1] = buffer[0];
    dateStamp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Time values.
    tempBuffer[0] = buffer[3];
    tempBuffer[1] = buffer[2];
    timeStamp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Outside Temp.
    tempBuffer[0] = buffer[5];
    tempBuffer[1] = buffer[4];
    outsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the High Outside Temp.
    tempBuffer[0] = buffer[7];
    tempBuffer[1] = buffer[6];
    highOutsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Low Outside Temp.
    tempBuffer[0] = buffer[9];
    tempBuffer[1] = buffer[8];
    lowOutsideTemp = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Rainfall.
    tempBuffer[0] = buffer[11];
    tempBuffer[1] = buffer[10];
    rainfall = (short) new BigInteger(tempBuffer).intValue();

    // Extract the High Rain Rate.
    tempBuffer[0] = buffer[13];
    tempBuffer[1] = buffer[12];
    highRainRate = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Pressure.
    tempBuffer[0] = buffer[15];
    tempBuffer[1] = buffer[14];
    pressure = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Solar Radiation.
    tempBuffer[0] = buffer[17];
    tempBuffer[1] = buffer[16];
    solarRadiation = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Number of Wind Samples.
    tempBuffer[0] = buffer[19];
    tempBuffer[1] = buffer[18];
    numOfWindSamples = (short) new BigInteger(tempBuffer).intValue();

    // Extract the Inside Temp.
    tempBuffer[0] = buffer[21];
    tempBuffer[1] = buffer[20];
    insideTemp = (short) new BigInteger(tempBuffer).intValue();

    insideHumidity     = buffer[22];
    outsideHumidity    = buffer[23];
    averageWindSpeed   = buffer[24];
    highWindSpeed      = buffer[25];
    highWindDirection  = buffer[26];
    prevailingWindDir  = buffer[27];
    averageUV          = buffer[28];
    evapotranspiration = buffer[29];

    // Extract the High Solar Radiation.
    tempBuffer[0] = buffer[31];
    tempBuffer[1] = buffer[30];
    highSolarRadiation = (short) new BigInteger(tempBuffer).intValue();
  
    highUVIndex  = buffer[32];
    forecastRule = buffer[33];

    soilTemp1 = buffer[38];

    // Locations 34 - 51 are for soil, leaf and extra humidity readings
  }

  public short getDateStamp()
  {
    return dateStamp;
  }

  public void setDateStamp(short dateStamp)
  {
    this.dateStamp = dateStamp;
  }

  public short getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(short timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public float getOutsideTemp()
  {
    return outsideTemp / TENTHS;
  }

  public short getOutsideTempNative()
  {
    return outsideTemp;
  }

  public float getHighOutsideTemp()
  {
    return highOutsideTemp / TENTHS;
  }

  public short getHighOutsideTempNative()
  {
    return highOutsideTemp;
  }

  public float getLowOutsideTemp()
  {
    return lowOutsideTemp / TENTHS;
  }

  public short getLowOutsideTempNative()
  {
    return lowOutsideTemp;
  }

  public float getInsideTemp()
  {
    return insideTemp / TENTHS;
  }

  public short getInsideTempNative()
  {
    return insideTemp;
  }

  public short getNumOfWindSamples()
  {
    return numOfWindSamples;
  }

  public float getPressure()
  {
    return pressure / THOUSANDTHS;
  }

  public short getPressureNative()
  {
    return pressure;
  }

  public void setPressure (short pressure)
  {
    this.pressure = pressure;
  }

  public short getRainfallNative()
  {
    return (short)(0x1000 | ((int)(rainfall) & 0x0FFF));
  }

  public float getRainfall()
  {
    return rainfall / HUNDREDTHS;
  }

  public void setRainfall (short rainfall)
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

  public byte getOutsideHumidity()
  {
    return outsideHumidity;
  }

  public byte getInsideHumidity()
  {
    return insideHumidity;
  }

  public short getAverageWindSpeedNative()
  {
    return (short)(averageWindSpeed * 10);
  }

  public short getAverageWindSpeed()
  {
    return (short)(averageWindSpeed);
  }

  public short getHighWindSpeedNative()
  {
    return (short)(highWindSpeed * 10);
  }

  public short getHighWindSpeed()
  {
    return (short)(highWindSpeed);
  }

  public byte getHighWindDirection()
  {
    return highWindDirection;
  }

  public byte getPrevailingWindDir()
  {
    return prevailingWindDir;
  }

  public short getHighSolarRadiation()
  {
    return highSolarRadiation;
  }

  public short getSolarRadiation()
  {
    return solarRadiation;
  }

  public byte getAverageUV()
  {
    return averageUV;
  }

  public byte getEvapotranspiration()
  {
    return evapotranspiration;
  }

  public void setEvapotranspiration(byte evapotranspiration)
  {
    this.evapotranspiration = evapotranspiration;
  }
  
  public byte getHighUVIndex()
  {
    return highUVIndex;
  }

  public int getForecastRule()
  {
    return forecastRule & 0xFF;
  }

  public int getSoilTemp1()
  {
    return (soilTemp1 & 0xFF) - 90;
  }

  public byte getSoilTemp1Native()
  {
    return soilTemp1;
  }

  public byte getRecordType()
  {
    return recordType;
  }

  @Override
  public String toString()
  {
    return "Dmp Record: \n" +
      "  DateStamp: " + getDateStamp() +
      ", year: " + TimeUtil.getYear(getDateStamp()) +
      ", month: " + TimeUtil.getMonth(getDateStamp()) +
      ", day: " + TimeUtil.getDay(getDateStamp()) + "\n" +
      "  TimeStamp: " + getTimeStamp() +
      ", Hour: " + TimeUtil.getHour(getTimeStamp()) +
      ", Minute: " + TimeUtil.getMinute(getTimeStamp()) + "\n" +
      "  Out Temp: " + getOutsideTempNative() + "\n" +
      "  High Temp: " + getHighOutsideTempNative() + "\n" +
      "  Low Temp: " + getLowOutsideTempNative() + "\n" +
      "  Out Humid: " + getOutsideHumidity() + "\n" +
      "  Wind Speed: " + getAverageWindSpeed() + "\n" +
      "  Prevailing Wind Dir: " + getPrevailingWindDir() + "\n" +
      "  High Wind Spd: " + getHighWindSpeed() + "\n" +
      "  High Wind Dir: " + getHighWindDirection() + "\n" +
      "  Bar: " + getPressureNative() + "\n" +
      "  Rain: " + getRainfallNative() + "\n" +
      "  Rain Rate: " + getHighRainRateNative() + "\n" +
      "  Solar Rad: " + getSolarRadiation() + "\n" +
      "  High Solar Rad: " + getHighSolarRadiation() + "\n" +
      "  Forecast: " + getForecastRule() + "\n" +
      "  In Temp: " + getInsideTempNative() + "\n" +
      "  In Humid: " + getInsideHumidity() + "\n" +
      "  Wind Samp: " + getNumOfWindSamples() + "\n" +
      "  Average UV: " + getAverageUV() + "\n" +
      "  Evapotranspiration: " + getEvapotranspiration() + "\n" +
      "  High UV: " + getHighUVIndex() + "\n" +
      "  Soil Temp 1: " + getSoilTemp1Native() + "\n";
  }
}
