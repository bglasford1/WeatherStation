/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Static constants used by the graph.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph;

import java.awt.*;

public class GraphDefs
{
  // Chart Definitions.
  public static final String NONE = "N";
  public static final String A_CHART_NAME = "A";
  public static final String B_CHART_NAME = "B";
  public static final String C_CHART_NAME = "C";

  // Left-AXIS selection items.
  public static final String INTEMP_STRING = "Inside Temp";
  public static final String OUTTEMP_STRING = "Outside Temp";
  public static final String HIGH_OUTTEMP_STRING = "High Outside Temp";
  public static final String LOW_OUTTEMP_STRING = "Low Outside Temp";
  public static final String AVG_OUTTEMP_STRING = "Avg Outside Temp";
  public static final String GREENHOUSE_TEMP_STRING = "Greenhouse Temp";
  public static final String INHUMID_STRING = "Inside Humidity";
  public static final String OUTHUMID_STRING = "Outside Humidity";
  public static final String SOLAR_RAD_STRING = "Solar Radiation";
  public static final String RAINFALL_STRING = "Rainfall";
  public static final String ISS_RECEPTION_STRING = "ISS Reception";
  public static final String PRESSURE_STRING = "Pressure";
  public static final String WIND_SPEED_STRING = "Wind Speed";
  public static final String WIND_DIR_STRING = "Wind Direction";
  public static final String HIGH_WIND_SPEED_STRING = "High Wind Speed";
  public static final String SNOW_STRING = "Snow";
  public static final String WIND_CHILL_STRING = "Wind Chill";
  public static final String HEAT_DD_STRING = "Heat DD";
  public static final String COOL_DD_STRING = "Cool DD";
  public static final String HEAT_INDEX_STRING = "Heat Index";
  public static final String DEW_POINT_STRING = "Dew Point";
  public static final String THW_STRING = "THW";
  public static final String THSW_STRING = "THSW";
  public static final String ET_STRING = "ET";
  public static final String WIND_RUN_STRING = "Wind Run";

  public static final String HOUR_STRING = "Hour";
  public static final String HALF_DAY_STRING = "Half Day";
  public static final String DAY_STRING = "Day";
  public static final String HALF_WEEK_STRING = "Half Week";
  public static final String WEEK_STRING = "Week";
  public static final String HALF_MONTH_STRING = "Half Month";
  public static final String MONTH_STRING = "Month";
  public static final String YEAR_STRING = "Year";

  public static final int DAY_DATA_SIZE = 288;
  public static final int HALF_DAY_DATA_SIZE = DAY_DATA_SIZE / 2;
  public static final int WEEK_DATA_SIZE = DAY_DATA_SIZE * 7;
  public static final int HALF_WEEK_DATA_SIZE = WEEK_DATA_SIZE / 2;
  public static final int HALF_MONTH_DATA_SIZE = WEEK_DATA_SIZE * 2;
  public static final int MONTH_DATA_SIZE = WEEK_DATA_SIZE * 4;
  public static final int YEAR_DATA_SIZE = DAY_DATA_SIZE * 365;

  public static final Font BILLVETICA = new Font("Billvetica", Font.PLAIN, 16);
}
