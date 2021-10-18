/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that defines the daily summary record #2 contained within
            the database file.

  Mods:		  09/01/21  Initial Release.
            10/18/21  Added Summary 1 & 2 data tables.
*/
package data.dbrecord;

import util.TimeUtil;

public class DailySummary2Record extends DataFileRecord
{
  private static final float TENTHS = 10;
  private static final float THOUSANDTHS = 1000;

  private int   day;

  private int   numOfWindPackets;
  private int   hiSolar;
  private short dailySolarEnergy;
  private short minSunlight;
  private short dailyETTotal;
  private short hiHeat;
  private short lowHeat;
  private short avgHeat;
  private short hiTHSW;
  private short lowTHSW;
  private short hiTHW;
  private short lowTHW;
  private short integratedHeatDD65;
  private short hiWetBulbTemp;
  private short lowWetBulbTemp;
  private short avgWetBulbTemp;
  private short integratedCoolDD65;

  private short nMinutes;
  private short nneMinutes;
  private short neMinutes;
  private short eneMinutes;
  private short eMinutes;
  private short eseMinutes;
  private short seMinutes;
  private short sseMinutes;
  private short sMinutes;
  private short sswMinutes;
  private short swMinutes;
  private short wswMinutes;
  private short wMinutes;
  private short wnwMinutes;
  private short nwMinutes;
  private short nnwMinutes;

  private short timeOfHighSolarRad;
  private short timeOfHighOutHeatIndex;
  private short timeOfLowOutHeatIndex;
  private short timeOfHighOutTHSWIndex;
  private short timeOfLowOutTHSWIndex;
  private short timeOfHighOutTHWIndex;
  private short timeOfLowOutTHWIndex;

  public static final int N_INDEX   = 0;
  public static final int NNE_INDEX = 1;
  public static final int NE_INDEX  = 2;
  public static final int ENE_INDEX = 3;
  public static final int E_INDEX   = 4;
  public static final int ESE_INDEX = 5;
  public static final int SE_INDEX  = 6;
  public static final int SSE_INDEX = 7;
  public static final int S_INDEX   = 8;
  public static final int SSW_INDEX = 9;
  public static final int SW_INDEX  = 10;
  public static final int WSW_INDEX = 11;
  public static final int W_INDEX   = 12;
  public static final int WNW_INDEX = 13;
  public static final int NW_INDEX  = 14;
  public static final int NNW_INDEX = 15;

  public static final int TIME_OF_HIGH_SOLAR_RAD_INDEX = 0;
  public static final int TIME_OF_HIGH_OUT_HEAT_INDEX  = 1;
  public static final int TIME_OF_LOW_OUT_HEAT_INDEX   = 2;
  public static final int TIME_OF_HIGH_OUT_THSW_INDEX  = 3;
  public static final int TIME_OF_LOW_OUT_THSW_INDEX   = 4;
  public static final int TIME_OF_HIGH_OUT_THW_INDEX   = 5;
  public static final int TIME_OF_LOW_OUT_THW_INDEX    = 6;

  public static final int NUM_OF_WIND_PACKETS_OFFSET = 4;
  public static final int HI_SOLAR_OFFSET            = 6;
  public static final int DAILY_SOLAR_ENERGY_OFFSET  = 8;
  public static final int MIN_SUNLIGHT_OFFSET        = 10;
  public static final int DAILY_ET_TOTAL_OFFSET      = 12;
  public static final int HI_HEAT_OFFSET             = 14;
  public static final int LOW_HEAT_OFFSET            = 16;
  public static final int AVG_HEAT_OFFSET            = 18;
  public static final int HI_THSW_OFFSET             = 20;
  public static final int LOW_THSW_OFFSET            = 22;
  public static final int HI_THW_OFFSET              = 24;
  public static final int LOW_THW_OFFSET             = 26;
  public static final int HEAT_DD_OFFSET             = 28;
  public static final int HI_WET_BULB_OFFSET         = 30;
  public static final int LOW_WET_BULB_OFFSET        = 32;
  public static final int AVG_WET_BULB_OFFSET        = 34;
  // Wind direction bins
  public static final int N_OFFSET_1                 = 36;
  public static final int N_OFFSET_2                 = 38;
  public static final int NNE_OFFSET_1               = 37;
  public static final int NNE_OFFSET_2               = 38;
  public static final int NE_OFFSET_1                = 39;
  public static final int NE_OFFSET_2                = 41;
  public static final int ENE_OFFSET_1               = 40;
  public static final int ENE_OFFSET_2               = 41;
  public static final int E_OFFSET_1                 = 42;
  public static final int E_OFFSET_2                 = 44;
  public static final int ESE_OFFSET_1               = 43;
  public static final int ESE_OFFSET_2               = 44;
  public static final int SE_OFFSET_1                = 45;
  public static final int SE_OFFSET_2                = 47;
  public static final int SSE_OFFSET_1               = 46;
  public static final int SSE_OFFSET_2               = 47;
  public static final int S_OFFSET_1                 = 48;
  public static final int S_OFFSET_2                 = 50;
  public static final int SSW_OFFSET_1               = 49;
  public static final int SSW_OFFSET_2               = 50;
  public static final int SW_OFFSET_1                = 51;
  public static final int SW_OFFSET_2                = 53;
  public static final int WSW_OFFSET_1               = 52;
  public static final int WSW_OFFSET_2               = 53;
  public static final int W_OFFSET_1                 = 54;
  public static final int W_OFFSET_2                 = 56;
  public static final int WNW_OFFSET_1               = 55;
  public static final int WNW_OFFSET_2               = 56;
  public static final int NW_OFFSET_1                = 57;
  public static final int NW_OFFSET_2                = 59;
  public static final int NNW_OFFSET_1               = 58;
  public static final int NNW_OFFSET_2               = 59;
  // Time values
  public static final int TIME_HIGH_SOLAR_OFFSET_1   = 60; // Even
  public static final int TIME_HIGH_SOLAR_OFFSET_2   = 62;
  public static final int TIME_HIGH_HEAT_OFFSET_1    = 61; // Odd
  public static final int TIME_HIGH_HEAT_OFFSET_2    = 62;
  public static final int TIME_LOW_HEAT_OFFSET_1     = 63; // Even
  public static final int TIME_LOW_HEAT_OFFSET_2     = 65;
  public static final int TIME_HIGH_THSW_OFFSET_1    = 64; // Odd
  public static final int TIME_HIGH_THSW_OFFSET_2    = 65;
  public static final int TIME_LOW_THSW_OFFSET_1     = 66; // Even
  public static final int TIME_LOW_THSW_OFFSET_2     = 68;
  public static final int TIME_HIGH_THW_OFFSET_1     = 67; // Odd
  public static final int TIME_HIGH_THW_OFFSET_2     = 68;
  public static final int TIME_LOW_THW_OFFSET_1      = 69; // Even
  public static final int TIME_LOW_THW_OFFSET_2      = 71;
  public static final int TIME_HIGH_WET_BULB_OFFSET_1 = 70; // Odd
  public static final int TIME_HIGH_WET_BULB_OFFSET_2 = 71;
  public static final int TIME_LOW_WET_BULB_OFFSET_1  = 72; // Even
  public static final int TIME_LOW_WET_BULB_OFFSET_2  = 74;

  public static final int COOL_DD_OFFSET             = 75;

  public int getDay()
  {
    return day;
  }

  public void setDay(int day)
  {
    this.day = day;
  }

  public int getNumOfWindPackets()
  {
    return numOfWindPackets;
  }

  public void setNumOfWindPackets(int numOfWindPackets)
  {
    this.numOfWindPackets = numOfWindPackets;
  }

  public int getHiSolar()
  {
    return hiSolar;
  }

  public void setHiSolar(int hiSolar)
  {
    this.hiSolar = hiSolar;
  }

  public float getDailySolarEnergy()
  {
    return dailySolarEnergy / TENTHS;
  }

  public short getDailySolarEnergyNative()
  {
    return dailySolarEnergy;
  }

  public void setDailySolarEnergy(float dailySolarEnergy)
  {
    this.dailySolarEnergy = (short)(dailySolarEnergy * TENTHS);
  }

  public void setDailySolarEnergyNative(short dailySolarEnergy)
  {
    this.dailySolarEnergy = dailySolarEnergy;
  }

  public short getMinSunlight()
  {
    return minSunlight;
  }

  public void setMinSunlight(short minSunlight)
  {
    this.minSunlight = minSunlight;
  }

  public float getDailyETTotal()
  {
    return dailyETTotal / THOUSANDTHS;
  }

  public short getDailyETTotalNative()
  {
    return dailyETTotal;
  }

  public void setDailyETTotal(float dailyETTotal)
  {
    this.dailyETTotal = (short)(dailyETTotal * THOUSANDTHS);
  }

  public void setDailyETTotalNative(short dailyETTotal)
  {
    this.dailyETTotal = dailyETTotal;
  }

  public float getHiHeat()
  {
    return hiHeat / TENTHS;
  }

  public short getHiHeatNative()
  {
    return hiHeat;
  }

  public void setHiHeat(float hiHeat)
  {
    this.hiHeat = (short)(hiHeat * TENTHS);
  }

  public void setHiHeatNative(short hiHeat)
  {
    this.hiHeat = hiHeat;
  }

  public float getLowHeat()
  {
    return lowHeat / TENTHS;
  }

  public short getLowHeatNative()
  {
    return lowHeat;
  }

  public void setLowHeat(float lowHeat)
  {
    this.lowHeat = (short)(lowHeat * TENTHS);
  }

  public void setLowHeatNative(short lowHeat)
  {
    this.lowHeat = lowHeat;
  }

  public float getAvgHeat()
  {
    return avgHeat / TENTHS;
  }

  public short getAvgHeatNative()
  {
    return avgHeat;
  }

  public void setAvgHeat(float avgHeat)
  {
    this.avgHeat = (short)(avgHeat * TENTHS);
  }

  public void setAvgHeatNative(short avgHeat)
  {
    this.avgHeat = avgHeat;
  }

  public float getHiTHSW()
  {
    return hiTHSW / TENTHS;
  }

  public short getHiTHSWNative()
  {
    return hiTHSW;
  }

  public void setHiTHSW(float hiTHSW)
  {
    this.hiTHSW = (short)(hiTHSW * TENTHS);
  }

  public void setHiTHSWNative(short hiTHSW)
  {
    this.hiTHSW = hiTHSW;
  }

  public float getLowTHSW()
  {
    return lowTHSW / TENTHS;
  }

  public short getLowTHSWNative()
  {
    return lowTHSW;
  }

  public void setLowTHSW(float lowTHSW)
  {
    this.lowTHSW = (short)(lowTHSW * TENTHS);
  }

  public void setLowTHSWNative(short lowTHSW)
  {
    this.lowTHSW = lowTHSW;
  }

  public float getHiTHW()
  {
    return hiTHW / TENTHS;
  }

  public short getHiTHWNative()
  {
    return hiTHW;
  }

  public void setHiTHW(float hiTHW)
  {
    this.hiTHW = (short)(hiTHW * TENTHS);
  }

  public void setHiTHWNative(short hiTHW)
  {
    this.hiTHW = hiTHW;
  }

  public float getLowTHW()
  {
    return lowTHW / TENTHS;
  }

  public short getLowTHWNative()
  {
    return lowTHW;
  }

  public void setLowTHW(float lowTHW)
  {
    this.lowTHW = (short)(lowTHW * TENTHS);
  }

  public void setLowTHWNative(short lowTHW)
  {
    this.lowTHW = lowTHW;
  }

  public float getIntegratedHeatDD65()
  {
    return integratedHeatDD65 / TENTHS;
  }

  public short getIntegratedHeatDD65Native()
  {
    return integratedHeatDD65;
  }

  public void setIntegratedHeatDD65(float integratedHeatDD65)
  {
    this.integratedHeatDD65 = (short)(integratedHeatDD65 * TENTHS);
  }

  public void setIntegratedHeatDD65Native(short integratedHeatDD65)
  {
    this.integratedHeatDD65 = integratedHeatDD65;
  }

  public float getIntegratedCoolDD65()
  {
    return integratedCoolDD65 / TENTHS;
  }

  public short getIntegratedCoolDD65Native()
  {
    return integratedCoolDD65;
  }

  public void setIntegratedCoolDD65(float integratedCoolDD65)
  {
    this.integratedCoolDD65 = (short)(integratedCoolDD65 * TENTHS);
  }

  public void setIntegratedCoolDD65Native(short integratedCoolDD65)
  {
    this.integratedCoolDD65 = integratedCoolDD65;
  }

  public float getHiWetBuldTemp()
  {
    return hiWetBulbTemp / TENTHS;
  }

  public short getHiWetBuldTempNative()
  {
    return hiWetBulbTemp;
  }

  public void setHiWetBulbTemp(float hiWetBulbTemp)
  {
    this.hiWetBulbTemp = (short)(hiWetBulbTemp * TENTHS);
  }

  public void setHiWetBulbTempNative(short hiWetBulbTemp)
  {
    this.hiWetBulbTemp = hiWetBulbTemp;
  }

  public float getLowWetBulbTemp()
  {
    return lowWetBulbTemp / TENTHS;
  }

  public short getLowWetBulbTempNative()
  {
    return lowWetBulbTemp;
  }

  public void setLowWetBulbTemp(float lowWetBulbTemp)
  {
    this.lowWetBulbTemp = (short)(lowWetBulbTemp * TENTHS);
  }

  public void setLowWetBulbTempNative(short lowWetBulbTemp)
  {
    this.lowWetBulbTemp = lowWetBulbTemp;
  }

  public float getAvgWetBulbTemp()
  {
    return avgWetBulbTemp / TENTHS;
  }

  public short getAvgWetBulbTempNative()
  {
    return avgWetBulbTemp;
  }

  public void setAvgWetBulbTemp(float avgWetBulbTemp)
  {
    this.avgWetBulbTemp = (short)(avgWetBulbTemp * TENTHS);
  }

  public void setAvgWetBulbTempNative(short avgWetBulbTemp)
  {
    this.avgWetBulbTemp = avgWetBulbTemp;
  }

  public void setWindDirMinutes(int index, short value)
  {
    switch (index)
    {
      case N_INDEX:
        nMinutes = value;
        break;
      case NNE_INDEX:
        nneMinutes = value;
        break;
      case NE_INDEX:
        neMinutes = value;
        break;
      case ENE_INDEX:
        eneMinutes = value;
        break;
      case E_INDEX:
        eMinutes = value;
        break;
      case ESE_INDEX:
        eseMinutes = value;
        break;
      case SE_INDEX:
        seMinutes = value;
        break;
      case SSE_INDEX:
        sseMinutes = value;
        break;
      case S_INDEX:
        sMinutes = value;
        break;
      case SSW_INDEX:
        sswMinutes = value;
        break;
      case SW_INDEX:
        swMinutes = value;
        break;
      case WSW_INDEX:
        wswMinutes = value;
        break;
      case W_INDEX:
        wMinutes = value;
        break;
      case WNW_INDEX:
        wnwMinutes = value;
        break;
      case NW_INDEX:
        nwMinutes = value;
        break;
      case NNW_INDEX:
        nnwMinutes = value;
        break;
    }
  }

  public short getNMinutes()
  {
    return nMinutes;
  }

  public short getNneMinutes()
  {
    return nneMinutes;
  }

  public short getNeMinutes()
  {
    return neMinutes;
  }

  public short getEneMinutes()
  {
    return eneMinutes;
  }

  public short getEMinutes()
  {
    return eMinutes;
  }

  public short getEseMinutes()
  {
    return eseMinutes;
  }

  public short getSeMinutes()
  {
    return seMinutes;
  }

  public short getSseMinutes()
  {
    return sseMinutes;
  }

  public short getSMinutes()
  {
    return sMinutes;
  }

  public short getSswMinutes()
  {
    return sswMinutes;
  }

  public short getSwMinutes()
  {
    return swMinutes;
  }

  public short getWswMinutes()
  {
    return wswMinutes;
  }

  public short getWMinutes()
  {
    return wMinutes;
  }

  public short getWnwMinutes()
  {
    return wnwMinutes;
  }

  public short getNwMinutes()
  {
    return nwMinutes;
  }

  public short getNnwMinutes()
  {
    return nnwMinutes;
  }

  public void setTimeValue(int index, short value)
  {
    switch (index)
    {
      case TIME_OF_HIGH_SOLAR_RAD_INDEX:
        timeOfHighSolarRad = value;
        break;
      case TIME_OF_HIGH_OUT_HEAT_INDEX:
        timeOfHighOutHeatIndex = value;
        break;
      case TIME_OF_LOW_OUT_HEAT_INDEX:
        timeOfLowOutHeatIndex = value;
        break;
      case TIME_OF_HIGH_OUT_THSW_INDEX:
        timeOfHighOutTHSWIndex = value;
        break;
      case TIME_OF_LOW_OUT_THSW_INDEX:
        timeOfLowOutTHSWIndex = value;
        break;
      case TIME_OF_HIGH_OUT_THW_INDEX:
        timeOfHighOutTHWIndex = value;
        break;
      case TIME_OF_LOW_OUT_THW_INDEX:
        timeOfLowOutTHWIndex = value;
        break;
    }
  }

  public short getTimeOfHighSolarRad()
  {
    return timeOfHighSolarRad;
  }

  public short getTimeOfHighOutHeatIndex()
  {
    return timeOfHighOutHeatIndex;
  }

  public short getTimeOfLowOutHeatIndex()
  {
    return timeOfLowOutHeatIndex;
  }

  public short getTimeOfHighOutTHSWIndex()
  {
    return timeOfHighOutTHSWIndex;
  }

  public short getTimeOfLowOutTHSWIndex()
  {
    return timeOfLowOutTHSWIndex;
  }

  public short getTimeOfHighOutTHWIndex()
  {
    return timeOfHighOutTHWIndex;
  }

  public short getTimeOfLowOutTHWIndex()
  {
    return timeOfLowOutTHWIndex;
  }

  @Override
  public String toString()
  {
    return "Daily Summary 2:\n  " +
      "numOfWindPackets: " + getNumOfWindPackets() + "\n  " +
      "hiSolar: " + getHiSolar() + "\n  " +
      "dailySolarEnergy: " + getDailySolarEnergy() + "\n  " +
      "minSunlight: " + getMinSunlight() + " <-- Not Implemented by Davis\n  " +
      "dailyETTotal: " + getDailyETTotal() + "\n  " +
      "hiHeat: " + getHiHeat() + "\n  " +
      "lowHeat: " + getLowHeat() + "\n  " +
      "avgHeat: " + getAvgHeat() + "\n  " +
      "hiTHSW: " + getHiTHSW() + "\n  " +
      "lowTHSW: " + getLowTHSW() + "\n  " +
      "hiTHW: " + getHiTHW() + "\n  " +
      "lowTHW: " + getLowTHW() + "\n  " +
      "integratedHeatDD65: " + getIntegratedHeatDD65() + "\n  " +
      "integratedCoolDD65: " + getIntegratedCoolDD65() + "\n  " +
      "hiWetBulbTemp: " + getHiWetBuldTemp() + "\n  " +
      "lowWetBulbTemp: " + getLowWetBulbTemp() + "\n  " +
      "avgWetBulbTemp: " + getAvgWetBulbTemp() + "\n  " +
      "N Mins: " + getNMinutes() + "\n  " +
      "NNE Mins: " + getNneMinutes() + "\n  " +
      "NE Mins: " + getNeMinutes() + "\n  " +
      "ENE Mins: " + getEneMinutes() + "\n  " +
      "E Mins: " + getEMinutes() + "\n  " +
      "ESE Mins: " + getEseMinutes() + "\n  " +
      "SE Mins: " + getSeMinutes() + "\n  " +
      "SSE Mins: " + getSseMinutes() + "\n  " +
      "S Mins: " + getSMinutes() + "\n  " +
      "SSW Mins: " + getSswMinutes() + "\n  " +
      "SW Mins: " + getSwMinutes() + "\n  " +
      "WSW Mins: " + getWswMinutes() + "\n  " +
      "W Mins: " + getWMinutes() + "\n  " +
      "WNW Mins: " + getWnwMinutes() + "\n  " +
      "NW Mins: " + getNwMinutes() + "\n  " +
      "NNW Mins: " + getNnwMinutes() + "\n  " +
      "Time of High Solar Rad: " + getTimeOfHighSolarRad() + " or " + TimeUtil.toString(getTimeOfHighSolarRad()) + "\n  " +
      "Time of High Out Heat Index: " + getTimeOfHighOutHeatIndex() + " or " + TimeUtil.toString(getTimeOfHighOutHeatIndex()) + "\n  " +
      "Time of Low Out Heat Index: " + getTimeOfLowOutHeatIndex() + " or " + TimeUtil.toString(getTimeOfLowOutHeatIndex()) + "\n  " +
      "Time of High Out THSW Index: " + getTimeOfHighOutTHSWIndex() + " or " + TimeUtil.toString(getTimeOfHighOutTHSWIndex()) + "\n  " +
      "Time of Low Out THSW Index: " + getTimeOfLowOutTHSWIndex() + " or " + TimeUtil.toString(getTimeOfLowOutTHSWIndex()) + "\n  " +
      "Time of High Out THW Index: " + getTimeOfHighOutTHWIndex() + " or " + TimeUtil.toString(getTimeOfHighOutTHWIndex()) + "\n  " +
      "Time of Low Out THW Index: " + getTimeOfLowOutTHWIndex() + " or " + TimeUtil.toString(getTimeOfLowOutTHWIndex()) + "\n  ";
  }
}
