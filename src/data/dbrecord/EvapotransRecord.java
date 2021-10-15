/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class encapsulates the data needed to calculate the
            evapotranspiration value.  This data is min/max values and
            average solar radiation over a 24 hour period.

  Mods:		  10/15/21  Initial Release.
*/
package data.dbrecord;

public class EvapotransRecord
{
  private float minHumidity = 100;
  private float maxHumidity = 0;
  private float minTemp = 100;
  private float maxTemp = 0;
  private float avgSolarRad = 0;
  private float avgWindSpeed = 0;

  public float getMinHumidity()
  {
    return minHumidity;
  }

  public void setMinHumidity(float minHumidity)
  {
    this.minHumidity = minHumidity;
  }

  public float getMaxHumidity()
  {
    return maxHumidity;
  }

  public void setMaxHumidity(float maxHumidity)
  {
    this.maxHumidity = maxHumidity;
  }

  public float getAvgSolarRad()
  {
    return avgSolarRad;
  }

  public void setAvgSolarRad(float avgSolarRad)
  {
    this.avgSolarRad = avgSolarRad;
  }

  public float getMinTemp()
  {
    return minTemp;
  }

  public void setMinTemp(float minTemp)
  {
    this.minTemp = minTemp;
  }

  public float getMaxTemp()
  {
    return maxTemp;
  }

  public void setMaxTemp(float maxTemp)
  {
    this.maxTemp = maxTemp;
  }

  public float getAvgWindSpeed()
  {
    return avgWindSpeed;
  }

  public void setAvgWindSpeed(float avgWindSpeed)
  {
    this.avgWindSpeed = avgWindSpeed;
  }
}
