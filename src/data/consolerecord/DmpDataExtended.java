/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class extends the basic DMP data structure by adding the
            calculated values.  This way the calculations only occur once.

  Mods:		  09/01/21  Initial Release.
            10/15/21  Fixed ET calculation.
            10/16/21  Removed ET print.
*/
package data.consolerecord;

import algorithms.Calculations;
import data.dbrecord.EvapotransRecord;
import dbif.DatabaseReader;
import util.ConfigProperties;
import util.TimeUtil;

import java.time.LocalDateTime;

public class DmpDataExtended extends DmpData
{
  private static final DatabaseReader dbReader = DatabaseReader.getInstance();
  private final ConfigProperties PROPS = ConfigProperties.instance();

  private float windChill;
  private float heatIndex;
  private float dewPoint;
  private float wetBulbTemp;
  private float thw;
  private float thsw;
  private float avgInsideTemp;
  private float avgOutsideTemp;
  private float avgWindChill;
  private float avgDewPoint;
  private float avgOutsideHumidity;
  private float avgPressure;
  private float avgWindSpeed;
  private float avgHeatIndex;
  private float avgWetBulbTemp;
  private float totalWindRun;
  private float heatDD;
  private float coolDD;
  private float et;

  /**
   * Method to calcuate the extended values.  The underlying DMP data must first be set by calling setData().
   */
  public void calculateData(float heatDDTotal, float coolDDTotal)
  {
    windChill = Calculations.calculateWindChill(getOutsideTemp(), getAverageWindSpeed());
    heatIndex = Calculations.calculateHeatIndex(getOutsideTemp(), getOutsideHumidity());
    dewPoint = Calculations.calculateDewPoint(getOutsideTemp(), getOutsideHumidity());
    wetBulbTemp = Calculations.calculateWetBulbTemperature(getOutsideTemp(), getOutsideHumidity());
    thw = Calculations.calculateTHW(getOutsideTemp(), getAverageWindSpeed(), getOutsideHumidity());
    thsw = Calculations.calculateTHSW(getOutsideTemp(), getAverageWindSpeed(), getOutsideHumidity(), getSolarRadiation());
    EvapotransRecord evapotransData = dbReader.getEvapotransData(LocalDateTime.now());
    et = Calculations.calculateET(evapotransData.getMinTemp(), evapotransData.getMaxTemp(),
                                  evapotransData.getAvgWindSpeed(), evapotransData.getAvgSolarRad(),
                                  evapotransData.getMinHumidity(), evapotransData.getMaxHumidity(),
                                  PROPS.getElevation(), PROPS.getLatitude());

    // 288 is 24 hours per day * 60 minutes per day / 5 minutes.
    // TODO: This value should be adjusted whenever a different archive interval is selected.
    heatDD = (float)(heatDDTotal + (65.0 - getOutsideTemp()) / 288.0);
    coolDD = (float)(coolDDTotal + (getOutsideTemp() - 65.0) / 288.0);

    float[] daysAverages = dbReader.readDaysAverages(TimeUtil.getYear(getDateStamp()),
                                                     TimeUtil.getMonth(getDateStamp()),
                                                     TimeUtil.getDay(getDateStamp()));
    avgInsideTemp = daysAverages[0];
    avgOutsideTemp = daysAverages[1];
    avgWindChill = daysAverages[2];
    avgDewPoint = daysAverages[3];
    avgOutsideHumidity = daysAverages[4];
    avgPressure = daysAverages[5];
    avgWindSpeed = daysAverages[6];
    totalWindRun = daysAverages[7];
    avgHeatIndex = daysAverages[8];
    avgWetBulbTemp = daysAverages[9];
  }

  public float getWindChill()
  {
    return windChill;
  }

  public float getHeatIndex()
  {
    return heatIndex;
  }

  public float getDewPoint()
  {
    return dewPoint;
  }

  public float getWetBulbTemp()
  {
    return wetBulbTemp;
  }

  public float getThw()
  {
    return thw;
  }

  public float getThsw()
  {
    return thsw;
  }

  public float getAvgInsideTemp()
  {
    return avgInsideTemp;
  }

  public float getAvgOutsideTemp()
  {
    return avgOutsideTemp;
  }

  public float getAvgWindChill()
  {
    return avgWindChill;
  }

  public float getAvgDewPoint()
  {
    return avgDewPoint;
  }

  public float getAvgOutsideHumidity()
  {
    return avgOutsideHumidity;
  }

  public float getAvgPressure()
  {
    return avgPressure;
  }

  public float getAvgWindSpeed()
  {
    return avgWindSpeed;
  }

  public float getAvgHeatIndex()
  {
    return avgHeatIndex;
  }

  public float getAvgWetBulbTemp()
  {
    return avgWetBulbTemp;
  }

  public float getTotalWindRun()
  {
    return totalWindRun;
  }

  public float getHeatDD()
  {
    return heatDD;
  }

  public float getCoolDD()
  {
    return coolDD;
  }

  public float getEt()
  {
    return et;
  }
}
