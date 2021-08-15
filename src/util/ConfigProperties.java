/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:  This class encapsulates the configuration properties.

            The initial properties file is contained within the jar file.  The problem is writing the data back out.
            A second configuration file is created to save off values locally so that values such as the line colors
            can be modified.  The code first looks for the local properties file. If that does not exist it then
            gets the one in the jar file.

            The graph and stream chart both have fixed numbers to uniquely identify them.  The graph numbers are
            unique within the graph structure as all data is plotted on a single chart.  The stream numbers are
            unique per chart.  There are 3 charts and the data sets are assigned to a chart.  The data set chart
            assignment can be change.  The following is the graph and chart assignments.

                     Graph  Stream  Stream
            Dataset    #      #     Chart
            -------  -----  ------  ------
            Out Temp   0      0       A
            Out Hum    1      1       A
            Rain       2      2       A
            Solar      3      3       A
            W. Chill   4      4       A
            H. DD      5      5       A
            C. DD      6      6       A
            Dew Pt     7      7       A
            H. Index   8      8       A
            THW        9      9       A
            THSW      10     10       A
            In Temp   11      0       B
            In Hum    12      1       B
            Press     13      3       B
            ISS Rec   14      2       B
            W. Spd    15      0       C
            W. Dir    16      1       C
            Grn Temp  17      2       C
            ET        18      3       C
            W. Run    19      4       C
            Hi OutTmp 20
            Lo OutTmp 21
            Av OutTmp 22
            Hi WndSpd 23

  Mods:		  09/01/21 Initial Release.
*/
package util;

import java.awt.*;
import java.io.*;
import java.util.Properties;

public class ConfigProperties
{
  private static ConfigProperties instance = null;
  private final Properties configProp = new Properties();
  private static final String JAR_FILENAME = "/META-INF/config.properties";
  private static final String LOCAL_FILENAME = "config.properties";

  private static final String PORT_NAME = "port.name";
  private static final String BAUD_RATE = "baud.rate";
  private static final String WX_STATION_ID = "wx.station.id";
  private static final String WX_PASSWORD = "wx.password";
  private static final String WX_UPDATE_INTERVAL = "wx.update.interval";
  private static final String WX_URL = "wx.url";
  private static final String CAPTURE_DATA = "capture.data";

  private static final String BACKGROUND_COLOR = "background.color";
  private static final String INSIDE_TEMP_COLOR = "inside.temp.color";
  private static final String INSIDE_HUMID_COLOR = "inside.humid.color";
  private static final String PRESSURE_COLOR = "pressure.color";
  private static final String GREEN_TEMP_COLOR = "green.temp.color";
  private static final String OUTSIDE_TEMP_COLOR = "outside.temp.color";
  private static final String HIGH_OUTSIDE_TEMP_COLOR = "high.outside.temp.color";
  private static final String LOW_OUTSIDE_TEMP_COLOR = "low.outside.temp.color";
  private static final String AVG_OUTSIDE_TEMP_COLOR = "avg.outside.temp.color";
  private static final String OUTSIDE_HUMID_COLOR = "outside.humid.color";
  private static final String RAIN_COLOR = "rain.color";
  private static final String ISS_RECEPTION_COLOR = "iss.reception.color";
  private static final String WIND_SPEED_COLOR = "wind.speed.color";
  private static final String WIND_DIR_COLOR = "wind.dir.color";
  private static final String SOLAR_COLOR = "solar.color";
  private static final String HIGH_WIND_SPEED_COLOR = "high.wind.speed.color";
  private static final String SNOW_LINE_COLOR = "snow.line.color";
  private static final String WIND_CHILL_COLOR = "wind.chill.color";
  private static final String HEAT_DD_COLOR = "heat.dd.color";
  private static final String COOL_DD_COLOR = "cool.dd.color";
  private static final String DEW_POINT_COLOR = "dew.point.color";
  private static final String HEAT_INDEX_COLOR = "heat.index.color";
  private static final String THW_COLOR = "thw.color";
  private static final String THSW_COLOR = "thsw.color";
  private static final String ET_COLOR = "et.color";
  private static final String WIND_RUN_COLOR = "wind.run.color";

  private static final String INSIDE_TEMP_CHART = "inside.temp.chart";
  private static final String INSIDE_HUMID_CHART = "inside.humid.chart";
  private static final String PRESSURE_CHART = "pressure.chart";
  private static final String GREEN_TEMP_CHART = "green.temp.chart";
  private static final String OUTSIDE_TEMP_CHART = "outside.temp.chart";
  private static final String OUTSIDE_HUMID_CHART = "outside.humid.chart";
  private static final String RAIN_CHART = "rain.chart";
  private static final String ISS_RECEPTION_CHART = "iss.reception.chart";
  private static final String WIND_SPEED_CHART = "wind.speed.chart";
  private static final String WIND_DIR_CHART = "wind.dir.chart";
  private static final String SOLAR_CHART = "solar.chart";
  private static final String WIND_CHILL_CHART = "wind.chill.chart";
  private static final String HEAT_DD_CHART = "heat.dd.chart";
  private static final String COOL_DD_CHART = "cool.dd.chart";
  private static final String DEW_POINT_CHART = "dew.point.chart";
  private static final String HEAT_INDEX_CHART = "heat.index.chart";
  private static final String THW_CHART = "thw.chart";
  private static final String THSW_CHART = "thsw.chart";
  private static final String ET_CHART = "et.chart";
  private static final String WIND_RUN_CHART = "wind.run.chart";

  private static final String INSIDE_TEMP_STREAM_DISPLAYED = "inside.temp.stream.displayed";
  private static final String INSIDE_HUMID_STREAM_DISPLAYED = "inside.humid.stream.displayed";
  private static final String PRESSURE_STREAM_DISPLAYED = "pressure.stream.displayed";
  private static final String GREEN_TEMP_STREAM_DISPLAYED = "green.temp.stream.displayed";
  private static final String OUTSIDE_TEMP_STREAM_DISPLAYED = "outside.temp.stream.displayed";
  private static final String OUTSIDE_HUMID_STREAM_DISPLAYED = "outside.humid.stream.displayed";
  private static final String RAIN_STREAM_DISPLAYED = "rain.stream.displayed";
  private static final String ISS_RECEPTION_STREAM_DISPLAYED = "iss.reception.stream.displayed";
  private static final String WIND_SPEED_STREAM_DISPLAYED = "wind.speed.stream.displayed";
  private static final String WIND_DIR_STREAM_DISPLAYED = "wind.dir.stream.displayed";
  private static final String SOLAR_STREAM_DISPLAYED = "solar.stream.displayed";
  private static final String WIND_CHILL_STREAM_DISPLAYED = "wind.chill.stream.displayed";
  private static final String HEAT_DD_STREAM_DISPLAYED = "heat.dd.stream.displayed";
  private static final String COOL_DD_STREAM_DISPLAYED = "cool.dd.stream.displayed";
  private static final String DEW_POINT_STREAM_DISPLAYED = "dew.point.stream.displayed";
  private static final String HEAT_INDEX_STREAM_DISPLAYED = "heat.index.stream.displayed";
  private static final String THW_STREAM_DISPLAYED = "thw.stream.displayed";
  private static final String THSW_STREAM_DISPLAYED = "thsw.stream.displayed";
  private static final String ET_STREAM_DISPLAYED = "et.stream.displayed";
  private static final String WIND_RUN_STREAM_DISPLAYED = "wind.run.stream.displayed";

  private static final String INSIDE_TEMP_GRAPH_DISPLAYED = "inside.temp.graph.displayed";
  private static final String INSIDE_HUMID_GRAPH_DISPLAYED = "inside.humid.graph.displayed";
  private static final String PRESSURE_GRAPH_DISPLAYED = "pressure.graph.displayed";
  private static final String GREEN_TEMP_GRAPH_DISPLAYED = "green.temp.graph.displayed";
  private static final String OUTSIDE_TEMP_GRAPH_DISPLAYED = "outside.temp.graph.displayed";
  private static final String HIGH_OUTSIDE_TEMP_GRAPH_DISPLAYED = "high.outside.temp.graph.displayed";
  private static final String LOW_OUTSIDE_TEMP_GRAPH_DISPLAYED = "low.outside.temp.graph.displayed";
  private static final String AVG_OUTSIDE_TEMP_GRAPH_DISPLAYED = "avg.outside.temp.graph.displayed";
  private static final String OUTSIDE_HUMID_GRAPH_DISPLAYED = "outside.humid.graph.displayed";
  private static final String RAIN_GRAPH_DISPLAYED = "rain.graph.displayed";
  private static final String ISS_RECEPTION_GRAPH_DISPLAYED = "iss.reception.graph.displayed";
  private static final String WIND_SPEED_GRAPH_DISPLAYED = "wind.speed.graph.displayed";
  private static final String WIND_DIR_GRAPH_DISPLAYED = "wind.dir.graph.displayed";
  private static final String SOLAR_GRAPH_DISPLAYED = "solar.graph.displayed";
  private static final String WIND_CHILL_GRAPH_DISPLAYED = "wind.chill.graph.displayed";
  private static final String HEAT_DD_GRAPH_DISPLAYED = "heat.dd.graph.displayed";
  private static final String COOL_DD_GRAPH_DISPLAYED = "cool.dd.graph.displayed";
  private static final String DEW_POINT_GRAPH_DISPLAYED = "dew.point.graph.displayed";
  private static final String HEAT_INDEX_GRAPH_DISPLAYED = "heat.index.graph.displayed";
  private static final String THW_GRAPH_DISPLAYED = "thw.graph.displayed";
  private static final String THSW_GRAPH_DISPLAYED = "thsw.graph.displayed";
  private static final String ET_GRAPH_DISPLAYED = "et.graph.displayed";
  private static final String WIND_RUN_GRAPH_DISPLAYED = "wind.run.graph.displayed";

  private String testDatabaseLocation;
  private String testSnowDatabaseLocation;
  private String databaseLocation;
  private String snowDatabaseLocation;
  private int archiveInterval;
  private int windowWidth;
  private int windowHeight;
  private Color backgroundColor;
  private int baudRate;
  private String portName;
  private String wxUrl;
  private String wxStationId = "";
  private String wxPassword = "";
  private int wxUpdateInterval; // time in minutes.
  private float latitude;
  private float longitude;
  private float elevation;
  private int timeZone;
  private boolean captureData = false;
  private boolean testMode = true;
  private Color snowLineColor;

  private String insideTempChart;
  private boolean insideTempStreamDisplayed;
  private boolean insideTempGraphDisplayed;
  private int insideTempStreamIndex;
  private Color insideTempColor;
  private int insideTempGraphIndex;

  private String insideHumidChart;
  private boolean insideHumidStreamDisplayed;
  private boolean insideHumidGraphDisplayed;
  private int insideHumidStreamIndex;
  private Color insideHumidColor;
  private int insideHumidGraphIndex;

  private String pressureChart;
  private boolean pressureStreamDisplayed;
  private boolean pressureGraphDisplayed;
  private int pressureStreamIndex;
  private Color pressureColor;
  private int pressureGraphIndex;

  private String greenTempChart;
  private boolean greenTempStreamDisplayed;
  private boolean greenTempGraphDisplayed;
  private int greenTempStreamIndex;
  private Color greenTempColor;
  private int greenTempGraphIndex;

  private String outsideTempChart;
  private boolean outsideTempStreamDisplayed;
  private boolean outsideTempGraphDisplayed;
  private int outsideTempStreamIndex;
  private Color outsideTempColor;
  private int outsideTempGraphIndex;

  private boolean highOutsideTempGraphDisplayed;
  private Color highOutsideTempColor;
  private int highOutsideTempGraphIndex;

  private boolean lowOutsideTempGraphDisplayed;
  private Color lowOutsideTempColor;
  private int lowOutsideTempGraphIndex;

  private boolean avgOutsideTempGraphDisplayed;
  private Color avgOutsideTempColor;
  private int avgOutsideTempGraphIndex;

  private String outsideHumidChart;
  private boolean outsideHumidStreamDisplayed;
  private boolean outsideHumidGraphDisplayed;
  private int outsideHumidStreamIndex;
  private Color outsideHumidColor;
  private int outsideHumidGraphIndex;

  private String rainChart;
  private boolean rainStreamDisplayed;
  private boolean rainGraphDisplayed;
  private int rainStreamIndex;
  private Color rainColor;
  private int rainGraphIndex;

  private String issReceptionChart;
  private boolean issReceptionStreamDisplayed;
  private boolean issReceptionGraphDisplayed;
  private int issReceptionStreamIndex;
  private Color issReceptionColor;
  private int issReceptionGraphIndex;

  private String windSpeedChart;
  private boolean windSpeedStreamDisplayed;
  private boolean windSpeedGraphDisplayed;
  private int windSpeedStreamIndex;
  private Color windSpeedColor;
  private int windSpeedGraphIndex;

  private String windDirChart;
  private boolean windDirStreamDisplayed;
  private boolean windDirGraphDisplayed;
  private int windDirStreamIndex;
  private Color windDirColor;
  private int windDirGraphIndex;

  private String solarChart;
  private boolean solarStreamDisplayed;
  private boolean solarGraphDisplayed;
  private int solarStreamIndex;
  private Color solarColor;
  private int solarGraphIndex;

  private String windChillChart;
  private boolean windChillStreamDisplayed;
  private boolean windChillGraphDisplayed;
  private int windChillStreamIndex;
  private Color windChillColor;
  private int windChillGraphIndex;

  private String heatDDChart;
  private boolean heatDDStreamDisplayed;
  private boolean heatDDGraphDisplayed;
  private int heatDDStreamIndex;
  private Color heatDDColor;
  private int heatDDGraphIndex;

  private String coolDDChart;
  private boolean coolDDStreamDisplayed;
  private boolean coolDDGraphDisplayed;
  private int coolDDStreamIndex;
  private Color coolDDColor;
  private int coolDDGraphIndex;

  private String dewPointChart;
  private boolean dewPointStreamDisplayed;
  private boolean dewPointGraphDisplayed;
  private int dewPointStreamIndex;
  private Color dewPointColor;
  private int dewPointGraphIndex;

  private String heatIndexChart;
  private boolean heatIndexStreamDisplayed;
  private boolean heatIndexGraphDisplayed;
  private int heatIndexStreamIndex;
  private Color heatIndexColor;
  private int heatIndexGraphIndex;

  private String thwChart;
  private boolean thwStreamDisplayed;
  private boolean thwGraphDisplayed;
  private int thwStreamIndex;
  private Color thwColor;
  private int thwGraphIndex;

  private String thswChart;
  private boolean thswStreamDisplayed;
  private boolean thswGraphDisplayed;
  private int thswStreamIndex;
  private Color thswColor;
  private int thswGraphIndex;

  private String etChart;
  private boolean etStreamDisplayed;
  private boolean etGraphDisplayed;
  private int etStreamIndex;
  private Color etColor;
  private int etGraphIndex;

  private String windRunChart;
  private boolean windRunStreamDisplayed;
  private boolean windRunGraphDisplayed;
  private int windRunStreamIndex;
  private Color windRunColor;
  private int windRunGraphIndex;

  private Color highWindSpeedColor;
  private int highWindSpeedGraphIndex;

  static public ConfigProperties instance()
  {
    if (instance == null)
    {
      instance = new ConfigProperties();
    }
    return instance;
  }

  private ConfigProperties()
  {
    InputStream is;
    boolean firstRun = false;

    // First try loading from the current directory.
    try
    {
      is = new FileInputStream(new File(LOCAL_FILENAME));
    }
    catch (Exception e)
    {
      is = null;
    }

    // Try loading from classpath if local file does not exist.
    try
    {
      if (is == null)
      {
        is = getClass().getResourceAsStream(JAR_FILENAME);
        firstRun = true;
        writeConfigFile();
      }

      configProp.load(is);

      // get the property values and save them
      testDatabaseLocation = configProp.getProperty("test.database.location");
      testSnowDatabaseLocation = configProp.getProperty("test.snow.database.location");
      databaseLocation = configProp.getProperty("database.location");
      snowDatabaseLocation = configProp.getProperty("snow.database.location");
      archiveInterval = Integer.parseInt(configProp.getProperty("archive.interval"));
      windowHeight = Integer.parseInt(configProp.getProperty("window.height"));
      windowWidth = Integer.parseInt(configProp.getProperty("window.width"));
      portName = configProp.getProperty(PORT_NAME);
      baudRate = Integer.parseInt(configProp.getProperty(BAUD_RATE));
      captureData = Boolean.parseBoolean(configProp.getProperty(CAPTURE_DATA));
      testMode = Boolean.parseBoolean(configProp.getProperty("test.mode"));
      wxUpdateInterval = Integer.parseInt(configProp.getProperty(WX_UPDATE_INTERVAL));
      latitude = Float.parseFloat(configProp.getProperty("latitude"));
      longitude = Float.parseFloat(configProp.getProperty("longitude"));
      elevation = Float.parseFloat(configProp.getProperty("elevation"));
      timeZone = Integer.parseInt(configProp.getProperty("time.zone"));
      wxUrl = configProp.getProperty(WX_URL);
      wxStationId = configProp.getProperty(WX_STATION_ID);
      if (wxStationId == null)
      {
        wxStationId = "";
      }
      wxPassword = configProp.getProperty(WX_PASSWORD);
      if (wxPassword == null)
      {
        wxPassword = "";
      }

      insideTempChart = configProp.getProperty(INSIDE_TEMP_CHART);
      insideTempStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(INSIDE_TEMP_STREAM_DISPLAYED));
      insideTempGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(INSIDE_TEMP_GRAPH_DISPLAYED));
      insideTempStreamIndex = Integer.parseInt(configProp.getProperty("inside.temp.stream.index"));
      insideTempGraphIndex = Integer.parseInt(configProp.getProperty("inside.temp.graph.index"));

      insideHumidChart = configProp.getProperty(INSIDE_HUMID_CHART);
      insideHumidStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(INSIDE_HUMID_STREAM_DISPLAYED));
      insideHumidGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(INSIDE_HUMID_GRAPH_DISPLAYED));
      insideHumidStreamIndex = Integer.parseInt(configProp.getProperty("inside.humid.stream.index"));
      insideHumidGraphIndex = Integer.parseInt(configProp.getProperty("inside.humid.graph.index"));

      pressureChart = configProp.getProperty(PRESSURE_CHART);
      pressureStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(PRESSURE_STREAM_DISPLAYED));
      pressureGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(PRESSURE_GRAPH_DISPLAYED));
      pressureStreamIndex = Integer.parseInt(configProp.getProperty("pressure.stream.index"));
      pressureGraphIndex = Integer.parseInt(configProp.getProperty("pressure.graph.index"));

      greenTempChart = configProp.getProperty(GREEN_TEMP_CHART);
      greenTempStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(GREEN_TEMP_STREAM_DISPLAYED));
      greenTempGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(GREEN_TEMP_GRAPH_DISPLAYED));
      greenTempStreamIndex = Integer.parseInt(configProp.getProperty("green.temp.stream.index"));
      greenTempGraphIndex = Integer.parseInt(configProp.getProperty("green.temp.graph.index"));

      outsideTempChart = configProp.getProperty(OUTSIDE_TEMP_CHART);
      outsideTempStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(OUTSIDE_TEMP_STREAM_DISPLAYED));
      outsideTempGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(OUTSIDE_TEMP_GRAPH_DISPLAYED));
      outsideTempStreamIndex = Integer.parseInt(configProp.getProperty("outside.temp.stream.index"));
      outsideTempGraphIndex = Integer.parseInt(configProp.getProperty("outside.temp.graph.index"));

      highOutsideTempGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(HIGH_OUTSIDE_TEMP_GRAPH_DISPLAYED));
      highOutsideTempGraphIndex = Integer.parseInt(configProp.getProperty("high.outside.temp.graph.index"));

      lowOutsideTempGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(LOW_OUTSIDE_TEMP_GRAPH_DISPLAYED));
      lowOutsideTempGraphIndex = Integer.parseInt(configProp.getProperty("low.outside.temp.graph.index"));

      avgOutsideTempGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(AVG_OUTSIDE_TEMP_GRAPH_DISPLAYED));
      avgOutsideTempGraphIndex = Integer.parseInt(configProp.getProperty("avg.outside.temp.graph.index"));

      outsideHumidChart = configProp.getProperty(OUTSIDE_HUMID_CHART);
      outsideHumidStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(OUTSIDE_HUMID_STREAM_DISPLAYED));
      outsideHumidGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(OUTSIDE_HUMID_GRAPH_DISPLAYED));
      outsideHumidStreamIndex = Integer.parseInt(configProp.getProperty("outside.humid.stream.index"));
      outsideHumidGraphIndex = Integer.parseInt(configProp.getProperty("outside.humid.graph.index"));

      rainChart = configProp.getProperty(RAIN_CHART);
      rainStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(RAIN_STREAM_DISPLAYED));
      rainGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(RAIN_GRAPH_DISPLAYED));
      rainStreamIndex = Integer.parseInt(configProp.getProperty("rain.stream.index"));
      rainGraphIndex = Integer.parseInt(configProp.getProperty("rain.graph.index"));

      issReceptionChart = configProp.getProperty(ISS_RECEPTION_CHART);
      issReceptionStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(ISS_RECEPTION_STREAM_DISPLAYED));
      issReceptionGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(ISS_RECEPTION_GRAPH_DISPLAYED));
      issReceptionStreamIndex = Integer.parseInt(configProp.getProperty("iss.reception.stream.index"));
      issReceptionGraphIndex = Integer.parseInt(configProp.getProperty("iss.reception.graph.index"));

      windSpeedChart = configProp.getProperty(WIND_SPEED_CHART);
      windSpeedStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(WIND_SPEED_STREAM_DISPLAYED));
      windSpeedGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(WIND_SPEED_GRAPH_DISPLAYED));
      windSpeedStreamIndex = Integer.parseInt(configProp.getProperty("wind.speed.stream.index"));
      windSpeedGraphIndex = Integer.parseInt(configProp.getProperty("wind.speed.graph.index"));

      windDirChart = configProp.getProperty(WIND_DIR_CHART);
      windDirStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(WIND_DIR_STREAM_DISPLAYED));
      windDirGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(WIND_DIR_GRAPH_DISPLAYED));
      windDirStreamIndex = Integer.parseInt(configProp.getProperty("wind.dir.stream.index"));
      windDirGraphIndex = Integer.parseInt(configProp.getProperty("wind.dir.graph.index"));

      solarChart = configProp.getProperty(SOLAR_CHART);
      solarStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(SOLAR_STREAM_DISPLAYED));
      solarGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(SOLAR_GRAPH_DISPLAYED));
      solarStreamIndex = Integer.parseInt(configProp.getProperty("solar.stream.index"));
      solarGraphIndex = Integer.parseInt(configProp.getProperty("solar.graph.index"));

      windChillChart = configProp.getProperty(WIND_CHILL_CHART);
      windChillStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(WIND_CHILL_STREAM_DISPLAYED));
      windChillGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(WIND_CHILL_GRAPH_DISPLAYED));
      windChillStreamIndex = Integer.parseInt(configProp.getProperty("wind.chill.stream.index"));
      windChillGraphIndex = Integer.parseInt(configProp.getProperty("wind.chill.graph.index"));

      heatDDChart = configProp.getProperty(HEAT_DD_CHART);
      heatDDStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(HEAT_DD_STREAM_DISPLAYED));
      heatDDGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(HEAT_DD_GRAPH_DISPLAYED));
      heatDDStreamIndex = Integer.parseInt(configProp.getProperty("heat.dd.stream.index"));
      heatDDGraphIndex = Integer.parseInt(configProp.getProperty("heat.dd.graph.index"));

      coolDDChart = configProp.getProperty(COOL_DD_CHART);
      coolDDStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(COOL_DD_STREAM_DISPLAYED));
      coolDDGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(COOL_DD_GRAPH_DISPLAYED));
      coolDDStreamIndex = Integer.parseInt(configProp.getProperty("cool.dd.stream.index"));
      coolDDGraphIndex = Integer.parseInt(configProp.getProperty("cool.dd.graph.index"));

      dewPointChart = configProp.getProperty(DEW_POINT_CHART);
      dewPointStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(DEW_POINT_STREAM_DISPLAYED));
      dewPointGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(DEW_POINT_GRAPH_DISPLAYED));
      dewPointStreamIndex = Integer.parseInt(configProp.getProperty("dew.point.stream.index"));
      dewPointGraphIndex = Integer.parseInt(configProp.getProperty("dew.point.graph.index"));

      heatIndexChart = configProp.getProperty(HEAT_INDEX_CHART);
      heatIndexStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(HEAT_INDEX_STREAM_DISPLAYED));
      heatIndexGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(HEAT_INDEX_GRAPH_DISPLAYED));
      heatIndexStreamIndex = Integer.parseInt(configProp.getProperty("heat.index.stream.index"));
      heatIndexGraphIndex = Integer.parseInt(configProp.getProperty("heat.index.graph.index"));

      thwChart = configProp.getProperty(THW_CHART);
      thwStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(THW_STREAM_DISPLAYED));
      thwGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(THW_GRAPH_DISPLAYED));
      thwStreamIndex = Integer.parseInt(configProp.getProperty("thw.stream.index"));
      thwGraphIndex = Integer.parseInt(configProp.getProperty("thw.graph.index"));

      thswChart = configProp.getProperty(THSW_CHART);
      thswStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(THSW_STREAM_DISPLAYED));
      thswGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(THSW_GRAPH_DISPLAYED));
      thswStreamIndex = Integer.parseInt(configProp.getProperty("thsw.stream.index"));
      thswGraphIndex = Integer.parseInt(configProp.getProperty("thsw.graph.index"));

      etChart = configProp.getProperty(ET_CHART);
      etStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(ET_STREAM_DISPLAYED));
      etGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(ET_GRAPH_DISPLAYED));
      etStreamIndex = Integer.parseInt(configProp.getProperty("et.stream.index"));
      etGraphIndex = Integer.parseInt(configProp.getProperty("et.graph.index"));

      windRunChart = configProp.getProperty(WIND_RUN_CHART);
      windRunStreamDisplayed = Boolean.parseBoolean(configProp.getProperty(WIND_RUN_STREAM_DISPLAYED));
      windRunGraphDisplayed = Boolean.parseBoolean(configProp.getProperty(WIND_RUN_GRAPH_DISPLAYED));
      windRunStreamIndex = Integer.parseInt(configProp.getProperty("wind.run.stream.index"));
      windRunGraphIndex = Integer.parseInt(configProp.getProperty("wind.run.graph.index"));

      highWindSpeedGraphIndex = Integer.parseInt(configProp.getProperty("high.wind.speed.graph.index"));

      // If not first run then get the saved configurable values such as color.
      if (firstRun)
      {
        // Color is not set initially in the config.properties because the value is a cryptic number.
        backgroundColor = new Color(200, 255, 255);
        insideTempColor = Color.RED;
        insideHumidColor = Color.GREEN;
        pressureColor = Color.BLUE;
        greenTempColor = Color.BLACK;
        outsideTempColor = Color.RED;
        outsideHumidColor = Color.GREEN;
        rainColor = Color.GREEN;
        issReceptionColor = Color.CYAN;
        windSpeedColor = Color.DARK_GRAY;
        windDirColor = Color.MAGENTA;
        solarColor = Color.ORANGE;
        windChillColor = Color.BLUE;
        heatDDColor = Color.BLACK;
        coolDDColor = Color.BLUE;
        dewPointColor = Color.MAGENTA;
        heatIndexColor = Color.ORANGE;
        thwColor = Color.DARK_GRAY;
        thswColor = Color.PINK;
        etColor = Color.GREEN;
        windRunColor = Color.CYAN;
        highWindSpeedColor = Color.RED;
        snowLineColor = Color.WHITE;

        configProp.setProperty(BAUD_RATE, "19200");
        configProp.setProperty(BACKGROUND_COLOR, String.valueOf(backgroundColor.getRGB()));
        configProp.setProperty(INSIDE_TEMP_COLOR, String.valueOf(insideTempColor.getRGB()));
        configProp.setProperty(INSIDE_HUMID_COLOR, String.valueOf(insideHumidColor.getRGB()));
        configProp.setProperty(PRESSURE_COLOR, String.valueOf(pressureColor.getRGB()));
        configProp.setProperty(GREEN_TEMP_COLOR, String.valueOf(greenTempColor.getRGB()));
        configProp.setProperty(OUTSIDE_TEMP_COLOR, String.valueOf(outsideTempColor.getRGB()));
        configProp.setProperty(HIGH_OUTSIDE_TEMP_COLOR, String.valueOf(highOutsideTempColor.getRGB()));
        configProp.setProperty(LOW_OUTSIDE_TEMP_COLOR, String.valueOf(lowOutsideTempColor.getRGB()));
        configProp.setProperty(AVG_OUTSIDE_TEMP_COLOR, String.valueOf(avgOutsideTempColor.getRGB()));
        configProp.setProperty(OUTSIDE_HUMID_COLOR, String.valueOf(outsideHumidColor.getRGB()));
        configProp.setProperty(RAIN_COLOR, String.valueOf(rainColor.getRGB()));
        configProp.setProperty(ISS_RECEPTION_COLOR, String.valueOf(issReceptionColor.getRGB()));
        configProp.setProperty(WIND_SPEED_COLOR, String.valueOf(windSpeedColor.getRGB()));
        configProp.setProperty(WIND_DIR_COLOR, String.valueOf(windDirColor.getRGB()));
        configProp.setProperty(SOLAR_COLOR, String.valueOf(solarColor.getRGB()));
        configProp.setProperty(WIND_CHILL_COLOR, String.valueOf(windChillColor.getRGB()));
        configProp.setProperty(HEAT_DD_COLOR, String.valueOf(heatDDColor.getRGB()));
        configProp.setProperty(COOL_DD_COLOR, String.valueOf(coolDDColor.getRGB()));
        configProp.setProperty(DEW_POINT_COLOR, String.valueOf(dewPointColor.getRGB()));
        configProp.setProperty(HEAT_INDEX_COLOR, String.valueOf(heatIndexColor.getRGB()));
        configProp.setProperty(THW_COLOR, String.valueOf(thwColor.getRGB()));
        configProp.setProperty(THSW_COLOR, String.valueOf(thswColor.getRGB()));
        configProp.setProperty(ET_COLOR, String.valueOf(etColor.getRGB()));
        configProp.setProperty(WIND_RUN_COLOR, String.valueOf(windRunColor.getRGB()));
        configProp.setProperty(HIGH_WIND_SPEED_COLOR, String.valueOf(highWindSpeedColor.getRGB()));
        configProp.setProperty(SNOW_LINE_COLOR, String.valueOf(snowLineColor.getRGB()));
        writeConfigFile();
      }
      else
      {
        backgroundColor = new Color(Integer.parseInt(configProp.getProperty(BACKGROUND_COLOR)));
        insideTempColor = new Color(Integer.parseInt(configProp.getProperty(INSIDE_TEMP_COLOR)));
        insideHumidColor = new Color(Integer.parseInt(configProp.getProperty(INSIDE_HUMID_COLOR)));
        greenTempColor = new Color(Integer.parseInt(configProp.getProperty(GREEN_TEMP_COLOR)));
        pressureColor = new Color(Integer.parseInt(configProp.getProperty(PRESSURE_COLOR)));
        outsideTempColor = new Color(Integer.parseInt(configProp.getProperty(OUTSIDE_TEMP_COLOR)));
        highOutsideTempColor = new Color(Integer.parseInt(configProp.getProperty(HIGH_OUTSIDE_TEMP_COLOR)));
        lowOutsideTempColor = new Color(Integer.parseInt(configProp.getProperty(LOW_OUTSIDE_TEMP_COLOR)));
        avgOutsideTempColor = new Color(Integer.parseInt(configProp.getProperty(AVG_OUTSIDE_TEMP_COLOR)));
        outsideHumidColor = new Color(Integer.parseInt(configProp.getProperty(OUTSIDE_HUMID_COLOR)));
        rainColor = new Color(Integer.parseInt(configProp.getProperty(RAIN_COLOR)));
        issReceptionColor = new Color(Integer.parseInt(configProp.getProperty(ISS_RECEPTION_COLOR)));
        windSpeedColor = new Color(Integer.parseInt(configProp.getProperty(WIND_SPEED_COLOR)));
        windDirColor = new Color(Integer.parseInt(configProp.getProperty(WIND_DIR_COLOR)));
        solarColor = new Color(Integer.parseInt(configProp.getProperty(SOLAR_COLOR)));
        windChillColor = new Color(Integer.parseInt(configProp.getProperty(WIND_CHILL_COLOR)));
        heatDDColor = new Color(Integer.parseInt(configProp.getProperty(HEAT_DD_COLOR)));
        coolDDColor = new Color(Integer.parseInt(configProp.getProperty(COOL_DD_COLOR)));
        dewPointColor = new Color(Integer.parseInt(configProp.getProperty(DEW_POINT_COLOR)));
        heatIndexColor = new Color(Integer.parseInt(configProp.getProperty(HEAT_INDEX_COLOR)));
        thwColor = new Color(Integer.parseInt(configProp.getProperty(THW_COLOR)));
        thswColor = new Color(Integer.parseInt(configProp.getProperty(THSW_COLOR)));
        etColor = new Color(Integer.parseInt(configProp.getProperty(ET_COLOR)));
        windRunColor = new Color(Integer.parseInt(configProp.getProperty(WIND_RUN_COLOR)));
        highWindSpeedColor = new Color(Integer.parseInt(configProp.getProperty(HIGH_WIND_SPEED_COLOR)));
        snowLineColor = new Color(Integer.parseInt(configProp.getProperty(SNOW_LINE_COLOR)));
      }
    }
    catch (Exception e)
    {
      System.out.println("Configuration Property file not found: " + e);
      e.printStackTrace();
    }
  }

  public String getTestDatabaseLocation()
  {
    return testDatabaseLocation;
  }

  public String getTestSnowDatabaseLocation()
  {
    return testSnowDatabaseLocation;
  }

  public String getDatabaseLocation()
  {
    return databaseLocation;
  }

  public String getSnowDatabaseLocation()
  {
    return snowDatabaseLocation;
  }

  public int getArchiveInterval()
  {
    return archiveInterval;
  }

  public int getWindowWidth()
  {
    return windowWidth;
  }

  public int getWindowHeight()
  {
    return windowHeight;
  }

  public int getBaudRate()
  {
    return baudRate;
  }

  public String getPortName()
  {
    return portName;
  }

  public int getWxUpdateInterval()
  {
    return wxUpdateInterval;
  }

  public float getLatitude()
  {
    return latitude;
  }

  public float getLongitude()
  {
    return longitude;
  }

  public float getElevation()
  {
    return elevation;
  }

  public int getTimeZone()
  {
    return timeZone;
  }

  public String getWxUrl()
  {
    return wxUrl;
  }

  public String getWxStationId()
  {
    return wxStationId;
  }

  public boolean getCaptureData()
  {
    return captureData;
  }

  public boolean getTestMode()
  {
    return testMode;
  }

  public String getWxPassword()
  {
    return wxPassword;
  }

  //--------------------------------

  public boolean isInsideTempStreamDisplayed()
  {
    return insideTempStreamDisplayed;
  }

  public boolean isInsideHumidStreamDisplayed()
  {
    return insideHumidStreamDisplayed;
  }

  public boolean isPressureStreamDisplayed()
  {
    return pressureStreamDisplayed;
  }

  public boolean isGreenTempStreamDisplayed()
  {
    return greenTempStreamDisplayed;
  }

  public boolean isOutsideTempStreamDisplayed()
  {
    return outsideTempStreamDisplayed;
  }

  public boolean isOutsideHumidStreamDisplayed()
  {
    return outsideHumidStreamDisplayed;
  }

  public boolean isRainStreamDisplayed()
  {
    return rainStreamDisplayed;
  }

  public boolean isIssReceptionStreamDisplayed()
  {
    return issReceptionStreamDisplayed;
  }

  public boolean isWindSpeedStreamDisplayed()
  {
    return windSpeedStreamDisplayed;
  }

  public boolean isWindDirStreamDisplayed()
  {
    return windDirStreamDisplayed;
  }

  public boolean isSolarStreamDisplayed()
  {
    return solarStreamDisplayed;
  }

  public boolean isWindChillStreamDisplayed()
  {
    return windChillStreamDisplayed;
  }

  public boolean isHeatDDStreamDisplayed()
  {
    return heatDDStreamDisplayed;
  }

  public boolean isCoolDDStreamDisplayed()
  {
    return coolDDStreamDisplayed;
  }

  public boolean isDewPointStreamDisplayed()
  {
    return dewPointStreamDisplayed;
  }

  public boolean isHeatIndexStreamDisplayed()
  {
    return heatIndexStreamDisplayed;
  }

  public boolean isThwStreamDisplayed()
  {
    return thwStreamDisplayed;
  }

  public boolean isThswStreamDisplayed()
  {
    return thswStreamDisplayed;
  }

  public boolean isEtStreamDisplayed()
  {
    return etStreamDisplayed;
  }

  public boolean isWindRunStreamDisplayed()
  {
    return windRunStreamDisplayed;
  }

//--------------------------------

  public boolean isInsideTempGraphDisplayed()
  {
    return insideTempGraphDisplayed;
  }

  public boolean isInsideHumidGraphDisplayed()
  {
    return insideHumidGraphDisplayed;
  }

  public boolean isPressureGraphDisplayed()
  {
    return pressureGraphDisplayed;
  }

  public boolean isGreenTempGraphDisplayed()
  {
    return greenTempGraphDisplayed;
  }

  public boolean isOutsideTempGraphDisplayed()
  {
    return outsideTempGraphDisplayed;
  }

  public boolean isHighOutsideTempGraphDisplayed()
  {
    return highOutsideTempGraphDisplayed;
  }

  public boolean isLowOutsideTempGraphDisplayed()
  {
    return lowOutsideTempGraphDisplayed;
  }

  public boolean isAvgOutsideTempGraphDisplayed()
  {
    return avgOutsideTempGraphDisplayed;
  }

  public boolean isOutsideHumidGraphDisplayed()
  {
    return outsideHumidGraphDisplayed;
  }

  public boolean isRainGraphDisplayed()
  {
    return rainGraphDisplayed;
  }

  public boolean isIssReceptionGraphDisplayed()
  {
    return issReceptionGraphDisplayed;
  }

  public boolean isWindSpeedGraphDisplayed()
  {
    return windSpeedGraphDisplayed;
  }

  public boolean isWindDirGraphDisplayed()
  {
    return windDirGraphDisplayed;
  }

  public boolean isSolarGraphDisplayed()
  {
    return solarGraphDisplayed;
  }

  public boolean isWindChillGraphDisplayed()
  {
    return windChillGraphDisplayed;
  }

  public boolean isHeatDDGraphDisplayed()
  {
    return heatDDGraphDisplayed;
  }

  public boolean isCoolDDGraphDisplayed()
  {
    return coolDDGraphDisplayed;
  }

  public boolean isDewPointGraphDisplayed()
  {
    return dewPointGraphDisplayed;
  }

  public boolean isHeatIndexGraphDisplayed()
  {
    return heatIndexGraphDisplayed;
  }

  public boolean isThwGraphDisplayed()
  {
    return thwGraphDisplayed;
  }

  public boolean isThswGraphDisplayed()
  {
    return thswGraphDisplayed;
  }

  public boolean isEtGraphDisplayed()
  {
    return etGraphDisplayed;
  }

  public boolean isWindRunGraphDisplayed()
  {
    return windRunGraphDisplayed;
  }

//--------------------------------

  public String getInsideTempChart()
  {
    return insideTempChart;
  }

  public int getInsideTempStreamIndex()
  {
    return insideTempStreamIndex;
  }

  public String getInsideHumidChart()
  {
    return insideHumidChart;
  }

  public int getInsideHumidStreamIndex()
  {
    return insideHumidStreamIndex;
  }

  public String getPressureChart()
  {
    return pressureChart;
  }

  public int getPressureStreamIndex()
  {
    return pressureStreamIndex;
  }

  public String getGreenTempChart()
  {
    return greenTempChart;
  }

  public int getGreenTempStreamIndex()
  {
    return greenTempStreamIndex;
  }

  public String getOutsideTempChart()
  {
    return outsideTempChart;
  }

  public int getOutsideTempStreamIndex()
  {
    return outsideTempStreamIndex;
  }

  public String getOutsideHumidChart()
  {
    return outsideHumidChart;
  }

  public int getOutsideHumidStreamIndex()
  {
    return outsideHumidStreamIndex;
  }

  public String getRainChart()
  {
    return rainChart;
  }

  public int getRainStreamIndex()
  {
    return rainStreamIndex;
  }

  public String getIssReceptionChart()
  {
    return issReceptionChart;
  }

  public int getIssReceptionStreamIndex()
  {
    return issReceptionStreamIndex;
  }

  public String getWindSpeedChart()
  {
    return windSpeedChart;
  }

  public int getWindSpeedStreamIndex()
  {
    return windSpeedStreamIndex;
  }

  public String getWindDirChart()
  {
    return windDirChart;
  }

  public int getWindDirStreamIndex()
  {
    return windDirStreamIndex;
  }

  public String getSolarChart()
  {
    return solarChart;
  }

  public int getSolarStreamIndex()
  {
    return solarStreamIndex;
  }

  public String getWindChillChart()
  {
    return windChillChart;
  }

  public int getWindChillStreamIndex()
  {
    return windChillStreamIndex;
  }

  public String getHeatDDChart()
  {
    return heatDDChart;
  }

  public int getHeatDDStreamIndex()
  {
    return heatDDStreamIndex;
  }

  public String getCoolDDChart()
  {
    return coolDDChart;
  }

  public int getCoolDDStreamIndex()
  {
    return coolDDStreamIndex;
  }

  public String getDewPointChart()
  {
    return dewPointChart;
  }

  public int getDewPointStreamIndex()
  {
    return dewPointStreamIndex;
  }

  public String getHeatIndexChart()
  {
    return heatIndexChart;
  }

  public int getHeatIndexStreamIndex()
  {
    return heatIndexStreamIndex;
  }

  public String getThwChart()
  {
    return thwChart;
  }

  public int getThwStreamIndex()
  {
    return thwStreamIndex;
  }

  public String getThswChart()
  {
    return thswChart;
  }

  public int getThswStreamIndex()
  {
    return thswStreamIndex;
  }

  public String getEtChart()
  {
    return etChart;
  }

  public int getEtStreamIndex()
  {
    return etStreamIndex;
  }

  public String getWindRunChart()
  {
    return windRunChart;
  }

  public int getWindRunStreamIndex()
  {
    return windRunStreamIndex;
  }

//--------------------------------

  public int getInsideTempGraphIndex()
  {
    return insideTempGraphIndex;
  }

  public int getInsideHumidGraphIndex()
  {
    return insideHumidGraphIndex;
  }

  public int getPressureGraphIndex()
  {
    return pressureGraphIndex;
  }

  public int getGreenTempGraphIndex()
  {
    return greenTempGraphIndex;
  }

  public int getHighOutsideTempGraphIndex()
  {
    return highOutsideTempGraphIndex;
  }

  public int getLowOutsideTempGraphIndex()
  {
    return lowOutsideTempGraphIndex;
  }

  public int getAvgOutsideTempGraphIndex()
  {
    return avgOutsideTempGraphIndex;
  }

  public int getOutsideTempGraphIndex()
  {
    return outsideTempGraphIndex;
  }

  public int getOutsideHumidGraphIndex()
  {
    return outsideHumidGraphIndex;
  }

  public int getRainGraphIndex()
  {
    return rainGraphIndex;
  }

  public int getIssReceptionGraphIndex()
  {
    return issReceptionGraphIndex;
  }

  public int getWindSpeedGraphIndex()
  {
    return windSpeedGraphIndex;
  }

  public int getWindDirGraphIndex()
  {
    return windDirGraphIndex;
  }

  public int getSolarGraphIndex()
  {
    return solarGraphIndex;
  }

  public int getWindChillGraphIndex()
  {
    return windChillGraphIndex;
  }

  public int getHighWindSpeedGraphIndex()
  {
    return highWindSpeedGraphIndex;
  }

  public int getHeatDDGraphIndex()
  {
    return heatDDGraphIndex;
  }

  public int getCoolDDGraphIndex()
  {
    return coolDDGraphIndex;
  }

  public int getDewPointGraphIndex()
  {
    return dewPointGraphIndex;
  }

  public int getHeatIndexGraphIndex()
  {
    return heatIndexGraphIndex;
  }

  public int getThwGraphIndex()
  {
    return thwGraphIndex;
  }

  public int getThswGraphIndex()
  {
    return thswGraphIndex;
  }

  public int getEtGraphIndex()
  {
    return etGraphIndex;
  }

  public int getWindRunGraphIndex()
  {
    return windRunGraphIndex;
  }

//--------------------------------

  public Color getInsideTempColor()
  {
    return insideTempColor;
  }

  public Color getInsideHumidColor()
  {
    return insideHumidColor;
  }

  public Color getPressureColor()
  {
    return pressureColor;
  }

  public Color getGreenTempColor()
  {
    return greenTempColor;
  }

  public Color getOutsideTempColor()
  {
    return outsideTempColor;
  }

  public Color getHighOutsideTempColor()
  {
    return highOutsideTempColor;
  }

  public Color getLowOutsideTempColor()
  {
    return lowOutsideTempColor;
  }

  public Color getAvgOutsideTempColor()
  {
    return avgOutsideTempColor;
  }

  public Color getOutsideHumidColor()
  {
    return outsideHumidColor;
  }

  public Color getRainColor()
  {
    return rainColor;
  }

  public Color getIssReceptionColor()
  {
    return issReceptionColor;
  }

  public Color getWindSpeedColor()
  {
    return windSpeedColor;
  }

  public Color getWindDirColor()
  {
    return windDirColor;
  }

  public Color getSolarColor()
  {
    return solarColor;
  }

  public Color getWindChillColor()
  {
    return windChillColor;
  }

  public Color getHighWindSpeedColor()
  {
    return highWindSpeedColor;
  }

  public Color getBackgroundColor()
  {
    return backgroundColor;
  }

  public Color getSnowLineColor()
  {
    return snowLineColor;
  }

  public Color getHeatDDColor()
  {
    return heatDDColor;
  }

  public Color getCoolDDColor()
  {
    return coolDDColor;
  }

  public Color getDewPointColor()
  {
    return dewPointColor;
  }

  public Color getHeatIndexColor()
  {
    return heatIndexColor;
  }

  public Color getThwColor()
  {
    return thwColor;
  }

  public Color getThswColor()
  {
    return thswColor;
  }

  public Color getEtColor()
  {
    return etColor;
  }

  public Color getWindRunColor()
  {
    return windRunColor;
  }

//--------------------------------

  public void setWxUpdateInterval(int updateInterval)
  {
    this.wxUpdateInterval = updateInterval;
    configProp.setProperty(WX_UPDATE_INTERVAL, Integer.toString(updateInterval));
    writeConfigFile();
  }

  public void setWxUrl(String wxUrl)
  {
    this.wxUrl = wxUrl;
    configProp.setProperty(WX_URL, wxUrl);
    writeConfigFile();
  }

  public void setBaudRate(int baudRate)
  {
    this.baudRate = baudRate;
    configProp.setProperty(BAUD_RATE, Integer.toString(baudRate));
    writeConfigFile();
  }

  public void setWxStationId(String stationId)
  {
    this.wxStationId = stationId;
    configProp.setProperty(WX_STATION_ID, stationId);
    writeConfigFile();
  }

  public void setWxPassword(String password)
  {
    this.wxPassword = password;
    configProp.setProperty(WX_PASSWORD, password);
    writeConfigFile();
  }

  public void setCaptureData(boolean capture)
  {
    this.captureData = capture;
    configProp.setProperty(CAPTURE_DATA, Boolean.toString(capture));
    writeConfigFile();
  }

  public void setBackgroundColor(Color color)
  {
    this.backgroundColor = color;
    configProp.setProperty(BACKGROUND_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setSnowLineColor(Color color)
  {
    this.snowLineColor = color;
    configProp.setProperty(SNOW_LINE_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setInsideTempColor(Color color)
  {
    this.insideTempColor = color;
    configProp.setProperty(INSIDE_TEMP_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setInsideHumidColor(Color color)
  {
    this.insideHumidColor = color;
    configProp.setProperty(INSIDE_HUMID_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setPressureColor(Color color)
  {
    this.pressureColor = color;
    configProp.setProperty(PRESSURE_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setGreenTempColor(Color color)
  {
    this.greenTempColor = color;
    configProp.setProperty(GREEN_TEMP_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setOutsideTempColor(Color color)
  {
    this.outsideTempColor = color;
    configProp.setProperty(OUTSIDE_TEMP_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setHighOutsideTempColor(Color color)
  {
    this.highOutsideTempColor = color;
    configProp.setProperty(HIGH_OUTSIDE_TEMP_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setLowOutsideTempColor(Color color)
  {
    this.lowOutsideTempColor = color;
    configProp.setProperty(LOW_OUTSIDE_TEMP_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setAvgOutsideTempColor(Color color)
  {
    this.avgOutsideTempColor = color;
    configProp.setProperty(AVG_OUTSIDE_TEMP_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setOutsideHumidColor(Color color)
  {
    this.outsideHumidColor = color;
    configProp.setProperty(OUTSIDE_HUMID_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setRainColor(Color color)
  {
    this.rainColor = color;
    configProp.setProperty(RAIN_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setIssReceptionColor(Color color)
  {
    this.issReceptionColor = color;
    configProp.setProperty(ISS_RECEPTION_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setWindSpeedColor(Color color)
  {
    this.windSpeedColor = color;
    configProp.setProperty(WIND_SPEED_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setWindDirColor(Color color)
  {
    this.windDirColor = color;
    configProp.setProperty(WIND_DIR_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setSolarColor(Color color)
  {
    this.solarColor = color;
    configProp.setProperty(SOLAR_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setHighWindSpeedColor(Color color)
  {
    this.highWindSpeedColor = color;
    configProp.setProperty(HIGH_WIND_SPEED_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setHeatDDColor(Color color)
  {
    this.heatDDColor = color;
    configProp.setProperty(HEAT_DD_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setCoolDDColor(Color color)
  {
    this.coolDDColor = color;
    configProp.setProperty(HEAT_DD_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setDewPointColor(Color color)
  {
    this.dewPointColor = color;
    configProp.setProperty(DEW_POINT_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setHeatIndexColor(Color color)
  {
    this.heatIndexColor = color;
    configProp.setProperty(HEAT_INDEX_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setThwColor(Color color)
  {
    this.thwColor = color;
    configProp.setProperty(THW_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setThswColor(Color color)
  {
    this.thswColor = color;
    configProp.setProperty(THSW_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setEtColor(Color color)
  {
    this.etColor = color;
    configProp.setProperty(ET_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setWindRunColor(Color color)
  {
    this.windRunColor = color;
    configProp.setProperty(WIND_RUN_COLOR, String.valueOf(color.getRGB()));
    writeConfigFile();
  }

  public void setInsideTempGraphDisplayed(boolean value)
  {
    this.insideTempGraphDisplayed = value;
    configProp.setProperty(INSIDE_TEMP_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setInsideHumidGraphDisplayed(boolean value)
  {
    this.insideHumidGraphDisplayed = value;
    configProp.setProperty(INSIDE_HUMID_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setPressureGraphDisplayed(boolean value)
  {
    this.pressureGraphDisplayed = value;
    configProp.setProperty(PRESSURE_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setGreenTempGraphDisplayed(boolean value)
  {
    this.greenTempGraphDisplayed = value;
    configProp.setProperty(GREEN_TEMP_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setOutsideTempGraphDisplayed(boolean value)
  {
    this.outsideTempGraphDisplayed = value;
    configProp.setProperty(OUTSIDE_TEMP_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setHighOutsideTempGraphDisplayed(boolean value)
  {
    this.highOutsideTempGraphDisplayed = value;
    configProp.setProperty(HIGH_OUTSIDE_TEMP_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setLowOutsideTempGraphDisplayed(boolean value)
  {
    this.lowOutsideTempGraphDisplayed = value;
    configProp.setProperty(LOW_OUTSIDE_TEMP_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setAvgOutsideTempGraphDisplayed(boolean value)
  {
    this.avgOutsideTempGraphDisplayed = value;
    configProp.setProperty(AVG_OUTSIDE_TEMP_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setOutsideHumidGraphDisplayed(boolean value)
  {
    this.outsideHumidGraphDisplayed = value;
    configProp.setProperty(OUTSIDE_HUMID_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setRainGraphDisplayed(boolean value)
  {
    this.rainGraphDisplayed = value;
    configProp.setProperty(RAIN_GRAPH_DISPLAYED, rainChart);
    writeConfigFile();
  }

  public void setIssReceptionGraphDisplayed(boolean value)
  {
    this.issReceptionGraphDisplayed = value;
    configProp.setProperty(ISS_RECEPTION_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setWindSpeedGraphDisplayed(boolean value)
  {
    this.windSpeedGraphDisplayed = value;
    configProp.setProperty(WIND_SPEED_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setWindDirGraphDisplayed(boolean value)
  {
    this.windDirGraphDisplayed = value;
    configProp.setProperty(WIND_DIR_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setSolarGraphDisplayed(boolean value)
  {
    this.solarGraphDisplayed = value;
    configProp.setProperty(SOLAR_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setWindChillGraphDisplayed(boolean value)
  {
    this.windChillGraphDisplayed = value;
    configProp.setProperty(WIND_CHILL_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setHeatDDGraphDisplayed(boolean value)
  {
    this.heatDDGraphDisplayed = value;
    configProp.setProperty(HEAT_DD_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setCoolDDGraphDisplayed(boolean value)
  {
    this.coolDDGraphDisplayed = value;
    configProp.setProperty(COOL_DD_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setDewPointGraphDisplayed(boolean value)
  {
    this.dewPointGraphDisplayed = value;
    configProp.setProperty(DEW_POINT_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setHeatIndexGraphDisplayed(boolean value)
  {
    this.heatIndexGraphDisplayed = value;
    configProp.setProperty(HEAT_INDEX_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setTHWGraphDisplayed(boolean value)
  {
    this.thwGraphDisplayed = value;
    configProp.setProperty(THW_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setTHSWGraphDisplayed(boolean value)
  {
    this.thswGraphDisplayed = value;
    configProp.setProperty(THSW_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setETGraphDisplayed(boolean value)
  {
    this.etGraphDisplayed = value;
    configProp.setProperty(ET_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setWindRunGraphDisplayed(boolean value)
  {
    this.windRunGraphDisplayed = value;
    configProp.setProperty(WIND_RUN_GRAPH_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setInsideTempStreamDisplayed(boolean value)
  {
    this.insideTempStreamDisplayed = value;
    configProp.setProperty(INSIDE_TEMP_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setInsideHumidStreamDisplayed(boolean value)
  {
    this.insideHumidStreamDisplayed = value;
    configProp.setProperty(INSIDE_HUMID_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setPressureStreamDisplayed(boolean value)
  {
    this.pressureStreamDisplayed = value;
    configProp.setProperty(PRESSURE_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setGreenTempStreamDisplayed(boolean value)
  {
    this.greenTempStreamDisplayed = value;
    configProp.setProperty(GREEN_TEMP_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setOutsideTempStreamDisplayed(boolean value)
  {
    this.outsideTempStreamDisplayed = value;
    configProp.setProperty(OUTSIDE_TEMP_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setOutsideHumidStreamDisplayed(boolean value)
  {
    this.outsideHumidStreamDisplayed = value;
    configProp.setProperty(OUTSIDE_HUMID_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setRainStreamDisplayed(boolean value)
  {
    this.rainStreamDisplayed = value;
    configProp.setProperty(RAIN_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setIssReceptionStreamDisplayed(boolean value)
  {
    this.issReceptionStreamDisplayed = value;
    configProp.setProperty(ISS_RECEPTION_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setWindSpeedStreamDisplayed(boolean value)
  {
    this.windSpeedStreamDisplayed = value;
    configProp.setProperty(WIND_SPEED_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setWindDirStreamDisplayed(boolean value)
  {
    this.windDirStreamDisplayed = value;
    configProp.setProperty(WIND_DIR_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setSolarStreamDisplayed(boolean value)
  {
    this.solarStreamDisplayed = value;
    configProp.setProperty(SOLAR_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setWindChillStreamDisplayed(boolean value)
  {
    this.windChillStreamDisplayed = value;
    configProp.setProperty(WIND_CHILL_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setHeatDDStreamDisplayed(boolean value)
  {
    this.heatDDStreamDisplayed = value;
    configProp.setProperty(HEAT_DD_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setCoolDDStreamDisplayed(boolean value)
  {
    this.coolDDStreamDisplayed = value;
    configProp.setProperty(COOL_DD_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setDewPointStreamDisplayed(boolean value)
  {
    this.dewPointStreamDisplayed = value;
    configProp.setProperty(DEW_POINT_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setHeatIndexStreamDisplayed(boolean value)
  {
    this.heatIndexStreamDisplayed = value;
    configProp.setProperty(HEAT_INDEX_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setTHWSteamDisplayed(boolean value)
  {
    this.thwStreamDisplayed = value;
    configProp.setProperty(THW_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setTHSWStreamDisplayed(boolean value)
  {
    this.thswStreamDisplayed = value;
    configProp.setProperty(THSW_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setETStreamDisplayed(boolean value)
  {
    this.etStreamDisplayed = value;
    configProp.setProperty(ET_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  public void setWindRunStreamDisplayed(boolean value)
  {
    this.windRunStreamDisplayed = value;
    configProp.setProperty(WIND_RUN_STREAM_DISPLAYED, Boolean.toString(value));
    writeConfigFile();
  }

  private void writeConfigFile()
  {
    try (FileOutputStream outputStream = new FileOutputStream(LOCAL_FILENAME))
    {
      configProp.store(outputStream, null);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
