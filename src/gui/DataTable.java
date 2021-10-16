/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the raw data table.

  Mods:		  09/01/21  Initial Release.
            10/15/21  Fixed ET calculation.

*/
package gui;

import data.dbrecord.DataFileRecord;
import data.dbrecord.WeatherRecord;
import data.dbrecord.WindDirection;
import dbif.DatabaseCommon;
import dbif.DatabaseReader;
import dbif.DatabaseWriter;
import util.ConfigProperties;
import util.Logger;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class DataTable extends JPanel implements TableModelListener
{
  private final DatabaseWriter databaseWriter = new DatabaseWriter();
  private final DatabaseReader dbReader = DatabaseReader.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  private final Logger logger = Logger.getInstance();

  private static final String IN_TEMP_STRING = "In Temp";
  private static final String IN_HUMID_STRING = "In Humid";
  private static final String OUT_TEMP_STRING = "Out Temp";
  private static final String OUT_HUMID_STRING = "Out Humid";
  private static final String GREENHOUSE_TEMP_STRING = "Greenhouse Temp";
  private static final String PRESSURE_STRING = "Pressure";
  private static final String RAINFALL_STRING = "Rainfall";
  private static final String HI_RAIN_RATE_STRING = "Hi Rain Rate";
  private static final String AVG_WIND_SPEED_STRING = "Avg Wind Spd";
  private static final String HI_WIND_SPEED_STRING = "Hi Wind Spd";
  private static final String NUM_OF_WIND_SAMPLES_STRING = "# of Wind Samples";
  private static final String HI_WIND_DIR_STRING = "Hi Wind Dir";
  private static final String WIND_DIR_STRING = "Wind Dir";
  private static final String SOLAR_RAD_STRING = "Solar Rad";
  private static final String HI_SOLAR_RAD_STRING = "Hi Solar Rad";
  private static final String ET_STRING = "ET";
  private static final String FORECAST_STRING = "Forecast";
  private static final String ARCHIVE_INTERVAL_STRING = "Archive Interval";

  private final String[] columnNames =
    {"Timestamp",
      IN_TEMP_STRING,
      IN_HUMID_STRING,
      OUT_TEMP_STRING,
      OUT_HUMID_STRING,
      GREENHOUSE_TEMP_STRING,
      PRESSURE_STRING,
      RAINFALL_STRING,
      HI_RAIN_RATE_STRING,
      AVG_WIND_SPEED_STRING,
      HI_WIND_SPEED_STRING,
      NUM_OF_WIND_SAMPLES_STRING,
      HI_WIND_DIR_STRING,
      WIND_DIR_STRING,
      SOLAR_RAD_STRING,
      HI_SOLAR_RAD_STRING,
      ET_STRING,
      FORECAST_STRING,
      ARCHIVE_INTERVAL_STRING
    };

  private JTable table = null;

  /**
   * The class constructor.
   */
  DataTable()
  {
    super(new GridLayout(1, 0));
  }

  /**
   * Method to create the table.
   *
   * @param year The year to populate the table with.
   * @param month The month to populate the table with.
   */
  public void createTable(int year, int month)
  {
    // Populate the datasets.
    try
    {
      dbReader.readData(year, month, null);
      dbReader.reset();
    }
    catch (IOException e)
    {
      logger.logData("DataTable: createTable: Unable to read data: " + e.getLocalizedMessage());
      return;
    }

    int totalRows = dbReader.getRowCount();
    Object[][] tableData = new Object[totalRows][19];
    int rowCount = 0;

    DataFileRecord nextRecord = dbReader.getNextRecord();
    while (nextRecord != null)
    {
      if (nextRecord instanceof WeatherRecord)
      {
        WeatherRecord data = (WeatherRecord) nextRecord;
        try
        {
          String formattedDateTime = data.getTimestamp().format(FORMATTER); // "1986-04-08 12:30"
          tableData[rowCount][0] = formattedDateTime;

          short insideTempNative = data.getInsideTempNative();
          if (insideTempNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][1] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][1] = Float.toString(data.getInsideTemp());
          }

          short insideHumidityNative = data.getInsideHumidityNative();
          if (insideHumidityNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][2] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][2] = Float.toString(data.getInsideHumidity());
          }

          short outsideTempNative = data.getOutsideTempNative();
          if (outsideTempNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][3] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][3] = Float.toString(data.getOutsideTemp());
          }

          short outsideHumidityNative = data.getOutsideHumidityNative();
          if (outsideHumidityNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][4] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][4] = Float.toString(data.getOutsideHumidity());
          }

          short soilTemp1Native = data.getSoilTemp1Native();
          if (soilTemp1Native == DatabaseCommon.UNDEFINED_BYTE_VALUE)
          {
            tableData[rowCount][5] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][5] = Byte.toString(data.getSoilTemp1());
          }

          short pressureNative = data.getPressureNative();
          if (pressureNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][6] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][6] = Float.toString(data.getPressure());
          }

          tableData[rowCount][7] = data.getRainfall();
          tableData[rowCount][8] = data.getHighRainRate();

          float avgWindSpeedNative = data.getAverageWindSpeedNative();
          if (avgWindSpeedNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][9] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][9] = Float.toString(data.getAverageWindSpeed());
          }

          float highWindSpeedNative = data.getHighWindSpeedNative();
          if (highWindSpeedNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][10] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][10] = Float.toString(data.getHighWindSpeed());
          }

          short numOfWindSamples = data.getNumOfWindSamples();
          if (numOfWindSamples == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][11] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][11] = Short.toString(data.getNumOfWindSamples());
          }

          WindDirection hiWindDirNative = data.getHighWindDirection();
          if (hiWindDirNative == null)
          {
            tableData[rowCount][12] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][12] = data.getHighWindDirection().toString();
          }

          WindDirection windDirNative = data.getWindDirection();
          if (windDirNative == null)
          {
            tableData[rowCount][13] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][13] = data.getWindDirection().toString();
          }

          short solarRadiation = data.getSolarRadiation();
          if (solarRadiation == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][14] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][14] = Short.toString(data.getSolarRadiation());
          }

          short highSolarRadiation = data.getHighSolarRadiation();
          if (highSolarRadiation == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][15] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][15] = Short.toString(data.getHighSolarRadiation());
          }

          tableData[rowCount][16] = Short.toString(data.getEt());
          tableData[rowCount][17] = Byte.toString(data.getForecast());
          tableData[rowCount][18] = Byte.toString(data.getArchiveInterval());

          rowCount++;
        }
        catch (Exception exc)
        {
          System.out.println("ModifyDataWindow: error in dataset." + exc);
        }
      }
      nextRecord = dbReader.getNextRecord();
    }

    table = new JTable(tableData, columnNames);
    table.setPreferredScrollableViewportSize(new Dimension(500, 70));
    table.getModel().addTableModelListener(this);
    table.getColumnModel().getColumn(0).setPreferredWidth(220);

    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);

    //Add the scroll pane to this panel.
    add(scrollPane);
  }


  /**
   * Method called when the user changes something in the table.
   *
   * @param event The table model event.
   */
  public void tableChanged(TableModelEvent event)
  {
    int row = event.getFirstRow();
    int column = event.getColumn();

    TableModel model = (TableModel) event.getSource();

    // Fill in a new record, reguardless of what value in the row changed.
    WeatherRecord updatedRecord = new WeatherRecord();
    String timestampString = (String) model.getValueAt(row, 0);
    LocalDateTime dateTime = LocalDateTime.parse(timestampString, FORMATTER);
    updatedRecord.setTimestamp(dateTime);

    // A numeric object already in the table is a float value.  If you input a new numeric it is a string value.
    Object insideTempObject = model.getValueAt(row, 1);
    if (insideTempObject instanceof String)
    {
      String insideTempString = (String)insideTempObject;
      if (insideTempString.equals(" ") || insideTempString.equals("-") || insideTempString.equals("--") ||
        insideTempString.equals("---"))
      {
        updatedRecord.setInsideTempNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setInsideTemp(Float.parseFloat(insideTempString));
      }
    }
    else
    {
      updatedRecord.setInsideTemp((Float)insideTempObject);
    }

    Object insideHumidityObject = model.getValueAt(row, 2);
    if (insideHumidityObject instanceof String)
    {
      String insideHumidityString = (String)insideHumidityObject;
      if (insideHumidityString.equals(" ") || insideHumidityString.equals("-") || insideHumidityString.equals("--") ||
        insideHumidityString.equals("---"))
      {
        updatedRecord.setInsideHumidityNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setInsideHumidity(Float.parseFloat(insideHumidityString));
      }
    }
    else
    {
      updatedRecord.setInsideHumidity((Float)insideHumidityObject);
    }

    Object outsideTempObject = model.getValueAt(row, 3);
    if (outsideTempObject instanceof String)
    {
      String outsideTempString = (String)outsideTempObject;
      if (outsideTempString.equals(" ") || outsideTempString.equals("-") || outsideTempString.equals("--") ||
        outsideTempString.equals("---"))
      {
        updatedRecord.setOutsideTempNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setOutsideTemp(Float.parseFloat(outsideTempString));
      }
    }
    else
    {
      updatedRecord.setOutsideTemp((Float)outsideTempObject);
    }

    Object outsideHumidityObject = model.getValueAt(row, 4);
    if (outsideHumidityObject instanceof String)
    {
      String outsideHumidityString = (String)outsideHumidityObject;
      if (outsideHumidityString.equals(" ") || outsideHumidityString.equals("-") || outsideHumidityString.equals("--") ||
        outsideHumidityString.equals("---"))
      {
        updatedRecord.setOutsideHumidityNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setOutsideHumidity(Float.parseFloat(outsideHumidityString));
      }
    }
    else
    {
      updatedRecord.setOutsideHumidity((Float)outsideHumidityObject);
    }

    Object greenhouseTempObject = model.getValueAt(row, 5);
    if (greenhouseTempObject instanceof String)
    {
      String greenhouseTempString = (String)greenhouseTempObject;
      if (greenhouseTempString.equals(" ") || greenhouseTempString.equals("-") || greenhouseTempString.equals("--") ||
        greenhouseTempString.equals("---"))
      {
        updatedRecord.setSoilTemp1Native(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setSoilTemp1(Byte.parseByte(greenhouseTempString));
      }
    }
    else
    {
      updatedRecord.setSoilTemp1((Byte)greenhouseTempObject);
    }

    Object pressureObject = model.getValueAt(row, 6);
    if (pressureObject instanceof String)
    {
      String pressureString = (String)pressureObject;
      if (pressureString.equals(" ") || pressureString.equals("-") || pressureString.equals("--") ||
        pressureString.equals("---"))
      {
        updatedRecord.setPressureNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setPressure(Float.parseFloat(pressureString));
      }
    }
    else
    {
      updatedRecord.setPressure((Float)pressureObject);
    }

    // Rainfall cannot be set to "---" because zero is a valid non-value and because of the rain collector nibble.
    Object rainfallObject = model.getValueAt(row, 7);
    if (rainfallObject instanceof String)
    {
      String rainfallString = (String)rainfallObject;
      updatedRecord.setRainfall(Float.parseFloat(rainfallString));
      if (rainfallString.equalsIgnoreCase("0"))
      {
        table.getModel().setValueAt("0.0", row, 7);
      }
    }
    else
    {
      updatedRecord.setRainfall((Float)rainfallObject);
    }

    // Rainfall cannot be set to "---" because zero is a valid non-value and because of the rain collector nibble.
    Object hiRainfallObject = model.getValueAt(row, 8);
    if (hiRainfallObject instanceof String)
    {
      String hiRainfallString = (String)hiRainfallObject;
      updatedRecord.setHighRainRate(Float.parseFloat(hiRainfallString));
      if (hiRainfallString.equalsIgnoreCase("0"))
      {
        table.getModel().setValueAt("0.0", row, 8);
      }
    }
    else
    {
      updatedRecord.setHighRainRate((Float)hiRainfallObject);
    }

    Object avgWindSpeedObject = model.getValueAt(row, 9);
    if (avgWindSpeedObject instanceof String)
    {
      String avgWindSpeedString = (String)avgWindSpeedObject;
      if (avgWindSpeedString.equals(" ") || avgWindSpeedString.equals("-") || avgWindSpeedString.equals("--") ||
        avgWindSpeedString.equals("---"))
      {
        updatedRecord.setAverageWindSpeedNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setAverageWindSpeed(Float.parseFloat(avgWindSpeedString));
      }
    }
    else
    {
      updatedRecord.setAverageWindSpeed((Float)avgWindSpeedObject);
    }

    Object hiWindSpeedObject = model.getValueAt(row, 10);
    if (hiWindSpeedObject instanceof String)
    {
      String hiWindSpeedString = (String)hiWindSpeedObject;
      if (hiWindSpeedString.equals(" ") || hiWindSpeedString.equals("-") || hiWindSpeedString.equals("--") ||
        hiWindSpeedString.equals("---"))
      {
        updatedRecord.setHighWindSpeedNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHighWindSpeed(Float.parseFloat(hiWindSpeedString));
      }
    }
    else
    {
      updatedRecord.setHighWindSpeed((Float)hiWindSpeedObject);
    }

    Object numWindSamplesObject = model.getValueAt(row, 11);
    if (numWindSamplesObject instanceof String)
    {
      String numWindSamplesString = (String)numWindSamplesObject;
      if (numWindSamplesString.equals(" ") || numWindSamplesString.equals("-") || numWindSamplesString.equals("--") ||
        numWindSamplesString.equals("---"))
      {
        updatedRecord.setNumOfWindSamples(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setNumOfWindSamples(Short.parseShort(numWindSamplesString));
      }
    }
    else
    {
      updatedRecord.setNumOfWindSamples((Short)numWindSamplesObject);
    }

    Object hiWindDirObject = model.getValueAt(row, 12);
    if (hiWindDirObject instanceof String)
    {
      String hiWindDirString = (String)hiWindDirObject;
      if (hiWindDirString.equals(" ") || hiWindDirString.equals("-") || hiWindDirString.equals("--") ||
        hiWindDirString.equals("---"))
      {
        updatedRecord.setHighWindDirectionNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        WindDirection hiWindDir = WindDirection.valueOf(hiWindDirString);
        updatedRecord.setHighWindDirectionNative((byte)hiWindDir.value());
      }
    }

    Object windDirObject = model.getValueAt(row, 13);
    if (windDirObject instanceof String)
    {
      String windDirString = (String)windDirObject;
      if (windDirString.equals(" ") || windDirString.equals("-") || windDirString.equals("--") ||
        windDirString.equals("---"))
      {
        updatedRecord.setWindDirectionNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        WindDirection windDir = WindDirection.valueOf(windDirString);
        updatedRecord.setWindDirectionNative((byte)windDir.value());
      }
    }

    Object solarRadObject = model.getValueAt(row, 14);
    if (solarRadObject instanceof String)
    {
      String solarRadString = (String)solarRadObject;
      if (solarRadString.equals(" ") || solarRadString.equals("-") || solarRadString.equals("--") ||
        solarRadString.equals("---"))
      {
        updatedRecord.setSolarRadiation(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setSolarRadiation(Short.parseShort(solarRadString));
      }
    }
    else
    {
      updatedRecord.setSolarRadiation((Short)solarRadObject);
    }

    Object hiSolarRadObject = model.getValueAt(row, 15);
    if (hiSolarRadObject instanceof String)
    {
      String hiSolarRadString = (String)hiSolarRadObject;
      if (hiSolarRadString.equals(" ") || hiSolarRadString.equals("-") || hiSolarRadString.equals("--") ||
        hiSolarRadString.equals("---"))
      {
        updatedRecord.setHighSolarRadiation(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHighSolarRadiation(Short.parseShort(hiSolarRadString));
      }
    }
    else
    {
      updatedRecord.setHighSolarRadiation((Short)hiSolarRadObject);
    }

    Object forecastObject = model.getValueAt(row, 16);
    if (forecastObject instanceof String)
    {
      updatedRecord.setForecast(Byte.parseByte((String)forecastObject));
    }
    else
    {
      updatedRecord.setForecast((Byte)forecastObject);
    }

    Object archiveIntervalObject = model.getValueAt(row, 17);
    if (archiveIntervalObject instanceof String)
    {
      updatedRecord.setArchiveInterval(Byte.parseByte((String)archiveIntervalObject));
    }
    else
    {
      updatedRecord.setArchiveInterval((Byte)archiveIntervalObject);
    }

    // Update the record.
    databaseWriter.updateWeatherRecord(updatedRecord);
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
   */
  public static void createAndShowGUI(JPanel dataTable)
  {
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame("Data Table");

    // content panes must be opaque
    dataTable.setOpaque(true);

    frame.setContentPane(dataTable);
    frame.setSize(PROPS.getWindowWidth(), PROPS.getWindowHeight());
    frame.setVisible(true);
  }
}
