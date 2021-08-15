/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that encapsulates the extended record of the weather record.

  Mods:		  09/01/21 Initial Release.
*/
package data.dbrecord;

public class WeatherRecordExtended extends WeatherRecord
{
  private float heatDD;
  private float coolDD;
  private float windRunTotal;

  public float getHeatDD()
  {
    return heatDD;
  }

  public void setHeatDD(float heatDD)
  {
    this.heatDD = heatDD;
  }

  public float getCoolDD()
  {
    return coolDD;
  }

  public void setCoolDD(float coolDD)
  {
    this.coolDD = coolDD;
  }

  public float getWindRunTotal()
  {
    return windRunTotal;
  }

  public void setWindRunTotal(float windRunTotal)
  {
    this.windRunTotal = windRunTotal;
  }
}
