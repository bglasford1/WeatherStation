/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that defines the summary record #1 that is read from the
            database files.

  Mods:		  09/01/21 Initial Release.
*/
package data.dbrecord;

import util.TimeUtil;

public class DailySummary1Record extends DataFileRecord
{
  private static final float TENTHS = 10;
  private static final float HUNDREDTHS = 100;
  private static final float THOUSANDTHS = 1000;

  private int day;
  private short dataSpan;

  private short hiOutTemp;
  private short lowOutTemp;
  private short avgOutTemp;

  private short hiInTemp;
  private short lowInTemp;
  private short avgInTemp;

  private short hiChill;
  private short lowChill;
  private short avgChill;

  private short hiDew;
  private short lowDew;
  private short avgDew;

  private short hiOutHumid;
  private short lowOutHumid;
  private short avgOutHumid;

  private short hiInHumid;
  private short lowInHumid;

  private short hiBar;
  private short lowBar;
  private short avgBar;

  private short hiSpeed;
  private byte  dirHiSpeed;
  private short avgSpeed;
  private short dailyWindRunTotal;
  private short hiTenMinSpeed; // Unimplemented by Davis
  private byte  hiTenMinDir;   // Unimplemented by Davis

  private short dailyRainTotal;
  private short hiRainRate;

  private short dailyUVDose;
  private short hiUV;

  private short timeOfHighOutTemp;
  private short timeOfLowOutTemp;
  private short timeOfHighInTemp;
  private short timeOfLowInTemp;
  private short timeOfHighWindChill;
  private short timeOfLowWindChill;
  private short timeOfHighDewPoint;
  private short timeOfLowDewPoint;
  private short timeOfHighOutHumidity;
  private short timeOfLowOutHumidity;
  private short timeOfHighInHumidity;
  private short timeOfLowInHumidity;
  private short timeOfHighPressure;
  private short timeOfLowPressure;
  private short timeOfHighWindSpeed;
  private short timeOfHighAvgWindSpeed;
  private short timeOfHighRainRate;
  private short timeOfHighUV;

  public static final int TIME_OF_HIGH_OUT_TEMP_INDEX       = 0;
  public static final int TIME_OF_LOW_OUT_TEMP_INDEX        = 1;
  public static final int TIME_OF_HIGH_IN_TEMP_INDEX        = 2;
  public static final int TIME_OF_LOW_IN_TEMP_INDEX         = 3;
  public static final int TIME_OF_HIGH_WIND_CHILL_INDEX     = 4;
  public static final int TIME_OF_LOW_WIND_CHILL_INDEX      = 5;
  public static final int TIME_OF_HIGH_DEW_POINT_INDEX      = 6;
  public static final int TIME_OF_LOW_DEW_POINT_INDEX       = 7;
  public static final int TIME_OF_HIGH_OUT_HUMID_INDEX      = 8;
  public static final int TIME_OF_LOW_OUT_HUMID_INDEX       = 9;
  public static final int TIME_OF_HIGH_IN_HUMID_INDEX       = 10;
  public static final int TIME_OF_LOW_IN_HUMID_INDEX        = 11;
  public static final int TIME_OF_HIGH_PRESSURE_INDEX       = 12;
  public static final int TIME_OF_LOW_PRESSURE_INDEX        = 13;
  public static final int TIME_OF_HIGH_WIND_SPEED_INDEX     = 14;
  public static final int TIME_OF_HIGH_AVG_WIND_SPEED_INDEX = 15; // Not implemented by Davis
  public static final int TIME_OF_HIGH_RAIN_RATE_INDEX      = 16;
  public static final int TIME_OF_HIGH_UV_INDEX             = 17; // No Sensor

  public static final int DATA_SPAN_OFFSET            = 2;
  public static final int HI_OUT_TEMP_OFFSET          = 4;
  public static final int LOW_OUT_TEMP_OFFSET         = 6;
  public static final int HI_IN_TEMP_OFFSET           = 8;
  public static final int LOW_IN_TEMP_OFFSET          = 10;
  public static final int AVG_OUT_TEMP_OFFSET         = 12;
  public static final int AVG_IN_TEMP_OFFSET          = 14;
  public static final int HI_CHILL_OFFSET             = 16;
  public static final int LOW_CHILL_OFFSET            = 18;
  public static final int HI_DEW_OFFSET               = 20;
  public static final int LOW_DEW_OFFSET              = 22;
  public static final int AVG_CHILL_OFFSET            = 24;
  public static final int AVG_DEW_OFFSET              = 26;
  public static final int HI_OUT_HUMID_OFFSET         = 28;
  public static final int LOW_OUT_HUMID_OFFSET        = 30;
  public static final int HI_IN_HUMID_OFFSET          = 32;
  public static final int LOW_IN_HUMID_OFFSET         = 34;
  public static final int AVG_OUT_HUMID_OFFSET        = 36;
  public static final int HI_BAR_OFFSET               = 38;
  public static final int LOW_BAR_OFFSET              = 40;
  public static final int AVG_BAR_OFFSET              = 42;
  public static final int HI_WIND_SPEED_OFFSET        = 44;
  public static final int AVG_WIND_SPEED_OFFSET       = 46;
  public static final int DAILY_WIND_RUN_TOTAL_OFFSET = 48;
  public static final int HI_10_MIN_SPEED_OFFSET      = 50;
  public static final int DIR_HI_WIND_SPEED_OFFSET    = 52;
  public static final int DIR_HI_10_MIN_SPEED_OFFSET  = 53;
  public static final int DAILY_RAIN_TOTAL_OFFSET     = 54;
  public static final int HI_RAIN_RATE_OFFSET         = 56;
  public static final int DAILY_UV_DOSE_OFFSET        = 58;
  public static final int HI_UV_OFFSET                = 60;
  public static final int TIME_HIGH_OUT_TEMP_OFFSET_1 = 61; // even
  public static final int TIME_HIGH_OUT_TEMP_OFFSET_2 = 63;
  public static final int TIME_LOW_OUT_TEMP_OFFSET_1  = 62; // odd
  public static final int TIME_LOW_OUT_TEMP_OFFSET_2  = 63;
  public static final int TIME_HIGH_IN_TEMP_OFFSET_1  = 64; // even
  public static final int TIME_HIGH_IN_TEMP_OFFSET_2  = 66;
  public static final int TIME_LOW_IN_TEMP_OFFSET_1   = 65; // odd
  public static final int TIME_LOW_IN_TEMP_OFFSET_2   = 66;
  public static final int TIME_HIGH_CHILL_OFFSET_1    = 67; // even
  public static final int TIME_HIGH_CHILL_OFFSET_2    = 69;
  public static final int TIME_LOW_CHILL_OFFSET_1     = 68; // odd
  public static final int TIME_LOW_CHILL_OFFSET_2     = 69;
  public static final int TIME_HIGH_DEW_OFFSET_1      = 70; // even
  public static final int TIME_HIGH_DEW_OFFSET_2      = 72;
  public static final int TIME_LOW_DEW_OFFSET_1       = 71; // odd
  public static final int TIME_LOW_DEW_OFFSET_2       = 72;
  public static final int TIME_HIGH_OUT_HUM_OFFSET_1  = 73; // even
  public static final int TIME_HIGH_OUT_HUM_OFFSET_2  = 75;
  public static final int TIME_LOW_OUT_HUM_OFFSET_1   = 74; // odd
  public static final int TIME_LOW_OUT_HUM_OFFSET_2   = 75;
  public static final int TIME_HIGH_IN_HUM_OFFSET_1   = 76; // even
  public static final int TIME_HIGH_IN_HUM_OFFSET_2   = 78;
  public static final int TIME_LOW_IN_HUM_OFFSET_1    = 77; // odd
  public static final int TIME_LOW_IN_HUM_OFFSET_2    = 78;
  public static final int TIME_HIGH_BAR_OFFSET_1      = 79; // even
  public static final int TIME_HIGH_BAR_OFFSET_2      = 81;
  public static final int TIME_LOW_BAR_OFFSET_1       = 80; // odd
  public static final int TIME_LOW_BAR_OFFSET_2       = 81;
  public static final int TIME_HIGH_WIND_OFFSET_1     = 82; // even
  public static final int TIME_HIGH_WIND_OFFSET_2     = 84;
  public static final int TIME_HIGH_AVG_WIND_OFFSET_1 = 83; // odd
  public static final int TIME_HIGH_AVG_WIND_OFFSET_2 = 84;
  public static final int TIME_HIGH_RAIN_OFFSET_1     = 85; // even
  public static final int TIME_HIGH_RAIN_OFFSET_2     = 87;
  public static final int TIME_HIGH_UV_OFFSET_1       = 86; // odd
  public static final int TIME_HIGH_UV_OFFSET_2       = 87;

  public int getDay()
  {
    return day;
  }

  public void setDay(int day)
  {
    this.day = day;
  }

  public short getDataSpan()
  {
    return dataSpan;
  }

  public void setDataSpan(short dataSpan)
  {
    this.dataSpan = dataSpan;
  }

  public float getAvgChill()
  {
    return avgChill / TENTHS;
  }

  public short getAvgChillNative()
  {
    return avgChill;
  }

  public void setAvgChill(float avgChill)
  {
    this.avgChill = (short)(avgChill * TENTHS);
  }

  public void setAvgChillNative(short avgChill)
  {
    this.avgChill = avgChill;
  }

  public float getAvgDew()
  {
    return avgDew / TENTHS;
  }

  public short getAvgDewNative()
  {
    return avgDew;
  }

  public void setAvgDew(float avgDew)
  {
    this.avgDew = (short)(avgDew * TENTHS);
  }

  public void setAvgDewNative(short avgDew)
  {
    this.avgDew = avgDew;
  }

  public float getHiOutHumid()
  {
    return hiOutHumid / TENTHS;
  }

  public short getHiOutHumidNative()
  {
    return hiOutHumid;
  }

  public void setHiOutHumid(float hiOutHumid)
  {
    this.hiOutHumid = (short)(hiOutHumid * TENTHS);
  }

  public void setHiOutHumidNative(short hiOutHumid)
  {
    this.hiOutHumid = hiOutHumid;
  }

  public float getLowOutHumid()
  {
    return lowOutHumid / TENTHS;
  }

  public short getLowOutHumidNative()
  {
    return lowOutHumid;
  }

  public void setLowOutHumid(float lowOutHumid)
  {
    this.lowOutHumid = (short)(lowOutHumid * TENTHS);
  }

  public void setLowOutHumidNative(short lowOutHumid)
  {
    this.lowOutHumid = lowOutHumid;
  }

  public float getAvgOutHumid()
  {
    return avgOutHumid / TENTHS;
  }

  public short getAvgOutHumidNative()
  {
    return avgOutHumid;
  }

  public void setAvgOutHumid(float avgOutHumid)
  {
    this.avgOutHumid = (short)(avgOutHumid * TENTHS);
  }

  public void setAvgOutHumidNative(short avgOutHumid)
  {
    this.avgOutHumid = avgOutHumid;
  }

  public float getHiInHumid()
  {
    return hiInHumid / TENTHS;
  }

  public short getHiInHumidNative()
  {
    return hiInHumid;
  }

  public void setHiInHumid(float hiInHumid)
  {
    this.hiInHumid = (short)(hiInHumid * TENTHS);
  }

  public void setHiInHumidNative(short hiInHumid)
  {
    this.hiInHumid = hiInHumid;
  }

  public float getLowInHumid()
  {
    return lowInHumid / TENTHS;
  }

  public short getLowInHumidNative()
  {
    return lowInHumid;
  }

  public void setLowInHumid(float lowInHumid)
  {
    this.lowInHumid = (short)(lowInHumid * TENTHS);
  }

  public void setLowInHumidNative(short lowInHumid)
  {
    this.lowInHumid = lowInHumid;
  }

  public float getHiBar()
  {
    return hiBar / THOUSANDTHS;
  }

  public short getHiBarNative()
  {
    return hiBar;
  }

  public void setHiBar(float hiBar)
  {
    this.hiBar = (short)(hiBar * THOUSANDTHS);
  }

  public void setHiBarNative(short hiBar)
  {
    this.hiBar = hiBar;
  }

  public float getLowBar()
  {
    return lowBar / THOUSANDTHS;
  }

  public short getLowBarNative()
  {
    return lowBar;
  }

  public void setLowBar(float lowBar)
  {
    this.lowBar = (short)(lowBar * THOUSANDTHS);
  }

  public void setLowBarNative(short lowBar)
  {
    this.lowBar = lowBar;
  }

  public float getAvgBar()
  {
    return avgBar / THOUSANDTHS;
  }

  public short getAvgBarNative()
  {
    return avgBar;
  }

  public void setAvgBar(float avgBar)
  {
    this.avgBar = (short)(avgBar * THOUSANDTHS);
  }

  public void setAvgBarNative(short avgBar)
  {
    this.avgBar = avgBar;
  }

  public float getHiSpeed()
  {
    return hiSpeed / TENTHS;
  }

  public short getHiSpeedNative()
  {
    return hiSpeed;
  }

  public void setHiSpeed(float hiSpeed)
  {
    this.hiSpeed = (short)(hiSpeed * TENTHS);
  }

  public void setHiSpeedNative(short hiSpeed)
  {
    this.hiSpeed = hiSpeed;
  }

  public WindDirection getDirHiSpeed()
  {
    if (dirHiSpeed < 15 && dirHiSpeed >= 0)
      return WindDirection.values()[dirHiSpeed];
    else
      return null;
  }

  public byte getDirHiSpeedNative()
  {
    return dirHiSpeed;
  }

  public void setDirHiSpeed(WindDirection dirHiSpeed)
  {
    this.dirHiSpeed = (byte)dirHiSpeed.ordinal();
  }

  public void setDirHiSpeedNative(byte dirHiSpeed)
  {
    this.dirHiSpeed = dirHiSpeed;
  }

  public float getAvgSpeed()
  {
    return avgSpeed / TENTHS;
  }

  public short getAvgSpeedNative()
  {
    return avgSpeed;
  }

  public void setAvgSpeed(float avgSpeed)
  {
    this.avgSpeed = (short)(avgSpeed * TENTHS);
  }

  public void setAvgSpeedNative(short avgSpeed)
  {
    this.avgSpeed = avgSpeed;
  }

  public float getDailyWindRunTotal()
  {
    return dailyWindRunTotal / TENTHS;
  }

  public short getDailyWindRunTotalNative()
  {
    return dailyWindRunTotal;
  }

  public void setDailyWindRunTotal(float dailyWindRunTotal)
  {
    this.dailyWindRunTotal = (short)(dailyWindRunTotal * TENTHS);
  }

  public void setDailyWindRunTotalNative(short dailyWindRunTotal)
  {
    this.dailyWindRunTotal = dailyWindRunTotal;
  }

  public float getHiTenMinSpeed()
  {
    return hiTenMinSpeed / TENTHS;
  }

  public short getHiTenMinSpeedNative()
  {
    return hiTenMinSpeed;
  }

  public void setHiTenMinSpeed(float hiTenMinSpeed)
  {
    this.hiTenMinSpeed = (short)(hiTenMinSpeed * TENTHS);
  }

  public void setHiTenMinSpeedNative(short hiTenMinSpeed)
  {
    this.hiTenMinSpeed = hiTenMinSpeed;
  }

  public WindDirection getHiTenMinDir()
  {
    if (hiTenMinDir < 15 && hiTenMinDir > 0)
      return WindDirection.values()[hiTenMinDir];
    else
      return null;
  }

  public byte getHiTenMinDirNative()
  {
    return hiTenMinDir;
  }

  public void setHiTenMinDir(WindDirection hiTenMinDir)
  {
    this.hiTenMinDir = (byte)hiTenMinDir.ordinal();
  }

  public void setHiTenMinDirNative(byte hiTenMinDir)
  {
    this.hiTenMinDir = hiTenMinDir;
  }

  public float getDailyRainTotal()
  {
    return dailyRainTotal / THOUSANDTHS;
  }

  public short getDailyRainTotalNative()
  {
    return dailyRainTotal;
  }

  public void setDailyRainTotal(float dailyRainTotal)
  {
    this.dailyRainTotal = (short)(dailyRainTotal * THOUSANDTHS);
  }

  public void setDailyRainTotalNative(short dailyRainTotal)
  {
    this.dailyRainTotal = dailyRainTotal;
  }

  public float getHiRainRate()
  {
    return hiRainRate / HUNDREDTHS;
  }

  public short getHiRainRateNative()
  {
    return hiRainRate;
  }

  public void setHiRainRate(float hiRainRate)
  {
    this.hiRainRate = (short)(hiRainRate * HUNDREDTHS);
  }

  public void setHiRainRateNative(short hiRainRate)
  {
    this.hiRainRate = hiRainRate;
  }

  public float getDailyUVDose()
  {
    return dailyUVDose / TENTHS;
  }

  public short getDailyUVDoseNative()
  {
    return dailyUVDose;
  }

  public void setDailyUVDose(float dailyUVDose)
  {
    this.dailyUVDose = (short)(dailyUVDose * TENTHS);
  }

  public void setDailyUVDoseNative(short dailyUVDose)
  {
    this.dailyUVDose = dailyUVDose;
  }

  public float getHiUV()
  {
    return hiUV / TENTHS;
  }

  public short getHiUVNative()
  {
    return hiUV;
  }

  public void setHiUV(float hiUV)
  {
    this.hiUV = (short)(hiUV * TENTHS);
  }

  public void setHiUVNative(short hiUV)
  {
    this.hiUV = hiUV;
  }

  public float getLowDew()
  {
    return lowDew / TENTHS;
  }

  public short getLowDewNative()
  {
    return lowDew;
  }

  public void setLowDew(float lowDew)
  {
    this.lowDew = (short)(lowDew * TENTHS);
  }

  public void setLowDewNative(short lowDew)
  {
    this.lowDew = lowDew;
  }

  public float getHiOutTemp()
  {
    return hiOutTemp / TENTHS;
  }

  public short getHiOutTempNative()
  {
    return hiOutTemp;
  }

  public void setHiOutTemp(float hiOutTemp)
  {
    this.hiOutTemp = (short)(hiOutTemp * TENTHS);
  }

  public void setHiOutTempNative(short hiOutTemp)
  {
    this.hiOutTemp = hiOutTemp;
  }

  public float getLowOutTemp()
  {
    return lowOutTemp / TENTHS;
  }

  public short getLowOutTempNative()
  {
    return lowOutTemp;
  }

  public void setLowOutTemp(float lowOutTemp)
  {
    this.lowOutTemp = (short)(lowOutTemp * TENTHS);
  }

  public void setLowOutTempNative(short lowOutTemp)
  {
    this.lowOutTemp = lowOutTemp;
  }

  public float getHiInTemp()
  {
    return hiInTemp / TENTHS;
  }

  public short getHiInTempNative()
  {
    return hiInTemp;
  }

  public void setHiInTemp(float hiInTemp)
  {
    this.hiInTemp = (short)(hiInTemp * TENTHS);
  }

  public void setHiInTempNative(short hiInTemp)
  {
    this.hiInTemp = hiInTemp;
  }

  public float getLowInTemp()
  {
    return lowInTemp / TENTHS;
  }

  public short getLowInTempNative()
  {
    return lowInTemp;
  }

  public void setLowInTemp(float lowInTemp)
  {
    this.lowInTemp = (short)(lowInTemp * TENTHS);
  }

  public void setLowInTempNative(short lowInTemp)
  {
    this.lowInTemp = lowInTemp;
  }

  public float getAvgOutTemp()
  {
    return avgOutTemp / TENTHS;
  }

  public short getAvgOutTempNative()
  {
    return avgOutTemp;
  }

  public void setAvgOutTemp(float avgOutTemp)
  {
    this.avgOutTemp = (short)(avgOutTemp * TENTHS);
  }

  public void setAvgOutTempNative(short avgOutTemp)
  {
    this.avgOutTemp = avgOutTemp;
  }

  public float getAvgInTemp()
  {
    return avgInTemp / TENTHS;
  }

  public short getAvgInTempNative()
  {
    return avgInTemp;
  }

  public void setAvgInTemp(float avgInTemp)
  {
    this.avgInTemp = (short)(avgInTemp * TENTHS);
  }

  public void setAvgInTempNative(short avgInTemp)
  {
    this.avgInTemp = avgInTemp;
  }

  public float getHiChill()
  {
    return hiChill / TENTHS;
  }

  public short getHiChillNative()
  {
    return hiChill;
  }

  public void setHiChill(float hiChill)
  {
    this.hiChill = (short)(hiChill * TENTHS);
  }

  public void setHiChillNative(short hiChill)
  {
    this.hiChill = hiChill;
  }

  public float getLowChill()
  {
    return lowChill / TENTHS;
  }

  public short getLowChillNative()
  {
    return lowChill;
  }

  public void setLowChill(float lowChill)
  {
    this.lowChill = (short)(lowChill * TENTHS);
  }

  public void setLowChillNative(short lowChill)
  {
    this.lowChill = lowChill;
  }

  public float getHiDew()
  {
    return hiDew / TENTHS;
  }

  public short getHiDewNative()
  {
    return hiDew;
  }

  public void setHiDew(float hiDew)
  {
    this.hiDew = (short)(hiDew * TENTHS);
  }

  public void setHiDewNative(short hiDew)
  {
    this.hiDew = hiDew;
  }

  public void setTimeValue(int index, short value)
  {
    switch (index)
    {
      case TIME_OF_HIGH_OUT_TEMP_INDEX:
        timeOfHighOutTemp = value;
        break;
      case TIME_OF_LOW_OUT_TEMP_INDEX:
        timeOfLowOutTemp = value;
        break;
      case TIME_OF_HIGH_IN_TEMP_INDEX:
        timeOfHighInTemp = value;
        break;
      case TIME_OF_LOW_IN_TEMP_INDEX:
        timeOfLowInTemp = value;
        break;
      case TIME_OF_HIGH_WIND_CHILL_INDEX:
        timeOfHighWindChill = value;
        break;
      case TIME_OF_LOW_WIND_CHILL_INDEX:
        timeOfLowWindChill = value;
        break;
      case TIME_OF_HIGH_DEW_POINT_INDEX:
        timeOfHighDewPoint = value;
        break;
      case TIME_OF_LOW_DEW_POINT_INDEX:
        timeOfLowDewPoint = value;
        break;
      case TIME_OF_HIGH_OUT_HUMID_INDEX:
        timeOfHighOutHumidity = value;
        break;
      case TIME_OF_LOW_OUT_HUMID_INDEX:
        timeOfLowOutHumidity = value;
        break;
      case TIME_OF_HIGH_IN_HUMID_INDEX:
        timeOfHighInHumidity = value;
        break;
      case TIME_OF_LOW_IN_HUMID_INDEX:
        timeOfLowInHumidity = value;
        break;
      case TIME_OF_HIGH_PRESSURE_INDEX:
        timeOfHighPressure = value;
        break;
      case TIME_OF_LOW_PRESSURE_INDEX:
        timeOfLowPressure = value;
        break;
      case TIME_OF_HIGH_WIND_SPEED_INDEX:
        timeOfHighWindSpeed = value;
        break;
      case TIME_OF_HIGH_AVG_WIND_SPEED_INDEX:
        timeOfHighAvgWindSpeed = value;
        break;
      case TIME_OF_HIGH_RAIN_RATE_INDEX:
        timeOfHighRainRate = value;
        break;
      case TIME_OF_HIGH_UV_INDEX:
        timeOfHighUV = value;
        break;
    }
  }

  public short getTimeOfHighOutTemp()
  {
    return timeOfHighOutTemp;
  }

  public short getTimeOfLowOutTemp()
  {
    return timeOfLowOutTemp;
  }

  public short getTimeOfHighInTemp()
  {
    return timeOfHighInTemp;
  }

  public short getTimeOfLowInTemp()
  {
    return timeOfLowInTemp;
  }

  public short getTimeOfHighWindChill()
  {
    return timeOfHighWindChill;
  }

  public short getTimeOfLowWindChill()
  {
    return timeOfLowWindChill;
  }

  public short getTimeOfHighDewPoint()
  {
    return timeOfHighDewPoint;
  }

  public short getTimeOfLowDewPoint()
  {
    return timeOfLowDewPoint;
  }

  public short getTimeOfHighOutHumidity()
  {
    return timeOfHighOutHumidity;
  }

  public short getTimeOfLowOutHumidity()
  {
    return timeOfLowOutHumidity;
  }

  public short getTimeOfHighInHumidity()
  {
    return timeOfHighInHumidity;
  }

  public short getTimeOfLowInHumidity()
  {
    return timeOfLowInHumidity;
  }

  public short getTimeOfHighPressure()
  {
    return timeOfHighPressure;
  }

  public short getTimeOfLowPressure()
  {
    return timeOfLowPressure;
  }

  public short getTimeOfHighWindSpeed()
  {
    return timeOfHighWindSpeed;
  }

  public short getTimeOfHighAvgWindSpeed()
  {
    return timeOfHighAvgWindSpeed;
  }

  public short getTimeOfHighRainRate()
  {
    return timeOfHighRainRate;
  }

  public short getTimeOfHighUV()
  {
    return timeOfHighUV;
  }

  @Override
  public String toString()
  {
    return "Daily Summary 1:\n  " +
      "dateSpan: " + getDataSpan() + "\n  " +
      "hiOutTemp: " + getHiOutTemp() + "\n  " +
      "lowOutTemp: " + getLowOutTemp() + "\n  " +
      "avgOutTemp: " + getAvgOutTemp() + "\n  " +
      "hiInTemp: " + getHiInTemp() + "\n  " +
      "lowInTemp: " + getLowInTemp() + "\n  " +
      "avgInTemp: " + getAvgInTemp() + "\n  " +
      "hiChill: " + getHiChill() + "\n  " +
      "lowChill: " + getLowChill() + "\n  " +
      "avgChill: " + getAvgChill() + "\n  " +
      "hiDew: " + getHiDew() + "\n  " +
      "lowDew: " + getLowDew() + "\n  " +
      "avgDew: " + getAvgDew() + "\n  " +
      "hiOutHumid: " + getHiOutHumid() + "\n  " +
      "lowOutHumid: " + getLowOutHumid() + "\n  " +
      "avgOutHumid: " + getAvgOutHumid() + "\n  " +
      "hiInHumid: " + getHiInHumid() + "\n  " +
      "lowInHumid: " + getLowInHumid() + "\n  " +
      "hiBar: " + getHiBar() + "\n  " +
      "lowBar: " + getLowBar() + "\n  " +
      "avgBar: " + getAvgBar() + "\n  " +
      "hiSpeed: " + getHiSpeed() + "\n  " +
      "dirHiSpeed: " + getDirHiSpeed() + "\n  " +
      "avgSpeed: " + getAvgSpeed() + "\n  " +
      "dailyWindRunTotal: " + getDailyWindRunTotal() + "\n  " +
      "hiTenMinSpeed: " + getHiTenMinSpeed() + " <-- Not Implemented by Davis\n  " +
      "hiTenMinDir: " + getHiTenMinDir() + " <-- Not Implemented by Davis\n  " +
      "dailyRainTotal: " + getDailyRainTotal() + "\n  " +
      "hiRainRate: " + getHiRainRate() + "\n  " +
      "dailyUVDose: " + getDailyUVDose() + " <-- No Sensor\n  " +
      "hiUV: " + getHiUV() + " <-- No Sensor\n  " +
      "Time of High Out Temp: " + getTimeOfHighOutTemp() + " or " + TimeUtil.toString(getTimeOfHighOutTemp()) + "\n  " +
      "Time of Low Out Temp: " + getTimeOfLowOutTemp() + " or " + TimeUtil.toString(getTimeOfLowOutTemp()) + "\n  " +
      "Time of High In Temp: " + getTimeOfHighInTemp() + " or " + TimeUtil.toString(getTimeOfHighInTemp()) + "\n  " +
      "Time of Low In Temp: " + getTimeOfLowInTemp() + " or " + TimeUtil.toString(getTimeOfLowInTemp()) + "\n  " +
      "Time of High Wind Chill: " + getTimeOfHighWindChill() + " or " + TimeUtil.toString(getTimeOfHighWindChill()) + "\n  " +
      "Time of Low Wind Chill: " + getTimeOfLowWindChill() + " or " + TimeUtil.toString(getTimeOfLowWindChill()) + "\n  " +
      "Time of High Dew Point: " + getTimeOfHighDewPoint() + " or " + TimeUtil.toString(getTimeOfHighDewPoint()) + "\n  " +
      "Time of Low Dew Point: " + getTimeOfLowDewPoint() + " or " + TimeUtil.toString(getTimeOfLowDewPoint()) + "\n  " +
      "Time of High Out Humidity: " + getTimeOfHighOutHumidity() + " or " + TimeUtil.toString(getTimeOfHighOutHumidity()) + "\n  " +
      "Time of Low Out Humidity: " + getTimeOfLowOutHumidity() + " or " + TimeUtil.toString(getTimeOfLowOutHumidity()) + "\n  " +
      "Time of High In Humidity: " + getTimeOfHighInHumidity() + " or " + TimeUtil.toString(getTimeOfHighInHumidity()) + "\n  " +
      "Time of Low In Humidity: " + getTimeOfLowInHumidity() + " or " + TimeUtil.toString(getTimeOfLowInHumidity()) + "\n  " +
      "Time of High Pressure: " + getTimeOfHighPressure() + " or " + TimeUtil.toString(getTimeOfHighPressure()) + "\n  " +
      "Time of Low Pressure: " + getTimeOfLowPressure() + " or " + TimeUtil.toString(getTimeOfLowPressure()) + "\n  " +
      "Time of High Wind Speed: " + getTimeOfHighWindSpeed() + " or " + TimeUtil.toString(getTimeOfHighWindSpeed()) + "\n  " +
      "Time of High Average Wind Speed: <-- Not implemented by Davis" + "\n  " +
      "Time of High Rain Rate: " + getTimeOfHighRainRate() + " or " + TimeUtil.toString(getTimeOfHighRainRate()) + "\n  " +
      "Time of High UV: <-- No sensor" + "\n  ";
  }
}
