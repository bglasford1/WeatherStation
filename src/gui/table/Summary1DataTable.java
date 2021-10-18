/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the raw daily summary 1
            data table.  This data can be modified.  A string value of "---"
            means no data is available.  In many cases the DB native value
            stored is 0x8000.

  Mods:		  10/18/21  Initial Release.
*/
package gui.table;

import data.dbrecord.DailySummary1Record;
import data.dbrecord.DataFileRecord;
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

public class Summary1DataTable extends JPanel implements TableModelListener
{
  private final DatabaseWriter databaseWriter = new DatabaseWriter();
  private final DatabaseReader dbReader = DatabaseReader.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private final Logger logger = Logger.getInstance();
  private int year;
  private int month;

  private static final String HI_OUT_TEMP_STRING = "Hi Out Temp";
  private static final String LOW_OUT_TEMP_STRING = "Low Out Temp";
  private static final String HI_IN_TEMP_STRING = "Hi In Temp";
  private static final String LOW_IN_TEMP_STRING = "Low In Temp";
  private static final String AVG_OUT_TEMP_STRING = "Avg Out Temp";
  private static final String AVG_IN_TEMP_STRING = "Avg In Temp";
  private static final String HI_WIND_CHILL_STRING = "Hi Wind Chill";
  private static final String LOW_WIND_CHILL_STRING = "Low Wind Chill";
  private static final String HI_DEW_POINT_STRING = "Hi Dew Point";
  private static final String LOW_DEW_POINT_STRING = "Low Dew Point";
  private static final String AVG_WIND_CHILL_STRING = "Avg Wind Chill";
  private static final String AVG_DEW_POINT_STRING = "Avg Dew Point";
  private static final String HI_OUT_HUMID_STRING = "Hi Out Humid";
  private static final String LOW_OUT_HUMID_STRING = "Low Out Humid";
  private static final String HI_IN_HUMID_STRING = "Hi In Humid";
  private static final String LOW_IN_HUMID_STRING = "Low In Humnid";
  private static final String AVG_OUT_HUMID_STRING = "Avg Out Humid";
  private static final String HI_PRESSURE_STRING = "Hi Pressure";
  private static final String LOW_PRESSURE_STRING = "Low Pressure";
  private static final String AVG_PRESSURE_STRING = "Avg Pressure";
  private static final String HI_WIND_SPEED_STRING = "Hi Wind Speed";
  private static final String AVG_WIND_SPEED_STRING = "Avg Wind Speed";
  private static final String DAILY_WIND_RUN_TOTAL_STRING = "Daily Wind Run Total";
  private static final String HI_TEN_MIN_SPEED_STRING = "Hi Ten Min Speed";
  private static final String DIR_HI_WIND_SPEED_STRING = "Dir Hi Wind Speed";
  private static final String DIR_HI_TEN_MIN_SPEED_STRING = "Dir Hi Ten Min Speed";
  private static final String DAILY_RAIN_TOTAL_STRING = "Daily Rain Total";
  private static final String HI_RAIN_RATE_STRING = "Hi Rain Rate";

  private final String[] columnNames =
    {"Date",
     HI_OUT_TEMP_STRING,
     LOW_OUT_TEMP_STRING,
     HI_IN_TEMP_STRING,
     LOW_IN_TEMP_STRING,
     AVG_OUT_TEMP_STRING,
     AVG_IN_TEMP_STRING,
     HI_WIND_CHILL_STRING,
     LOW_WIND_CHILL_STRING,
     HI_DEW_POINT_STRING,
     LOW_DEW_POINT_STRING,
     AVG_WIND_CHILL_STRING,
     AVG_DEW_POINT_STRING,
     HI_OUT_HUMID_STRING,
     LOW_OUT_HUMID_STRING,
     HI_IN_HUMID_STRING,
     LOW_IN_HUMID_STRING,
     AVG_OUT_HUMID_STRING,
     HI_PRESSURE_STRING,
     LOW_PRESSURE_STRING,
     AVG_PRESSURE_STRING,
     HI_WIND_SPEED_STRING,
     AVG_WIND_SPEED_STRING,
     DAILY_WIND_RUN_TOTAL_STRING,
     HI_TEN_MIN_SPEED_STRING,
     DIR_HI_WIND_SPEED_STRING,
     DIR_HI_TEN_MIN_SPEED_STRING,
     DAILY_RAIN_TOTAL_STRING,
     HI_RAIN_RATE_STRING
     };

  /**
   * The class constructor.
   */
  public Summary1DataTable()
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
    this.year = year;
    this.month = month;

    // Populate the datasets.
    try
    {
      dbReader.readData(year, month, null);
      dbReader.reset();
    }
    catch (IOException e)
    {
      logger.logData("Summary1DataTable: createTable: Unable to read data: " + e.getLocalizedMessage());
      return;
    }

    int totalRows = dbReader.getRowCount();
    Object[][] tableData = new Object[totalRows][29];
    int rowCount = 0;
    int dayCount = 1;

    DataFileRecord nextRecord = dbReader.getNextRecord();
    while (nextRecord != null)
    {
      if (nextRecord instanceof DailySummary1Record)
      {
        DailySummary1Record data = (DailySummary1Record) nextRecord;
        try
        {
          tableData[rowCount][0] = year + "/" + month + "/" + dayCount;

          short hiOutTempNative = data.getHiOutTempNative();
          if (hiOutTempNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][1] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][1] = Float.toString(data.getHiOutTemp());
          }

          int lowOutTempNative = data.getLowOutTempNative();
          if (lowOutTempNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][2] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][2] = Float.toString(data.getLowOutTemp());
          }

          short hiInTempNative = data.getHiInTempNative();
          if (hiInTempNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][3] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][3] = Float.toString(data.getHiInTemp());
          }

          short lowInTempNative = data.getLowInTempNative();
          if (lowInTempNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][4] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][4] = Float.toString(data.getLowInTemp());
          }

          short avgOutTempNative = data.getAvgOutTempNative();
          if (avgOutTempNative == DatabaseCommon.UNDEFINED_BYTE_VALUE)
          {
            tableData[rowCount][5] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][5] = Float.toString(data.getAvgOutTemp());
          }

          short avgInTempNative = data.getAvgInTempNative();
          if (avgInTempNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][6] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][6] = Float.toString(data.getAvgInTemp());
          }

          short hiChillNative = data.getHiChillNative();
          if (hiChillNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][7] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][7] = Float.toString(data.getHiChill());
          }

          short lowChillNative = data.getLowChillNative();
          if (lowChillNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][8] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][8] = Float.toString(data.getLowChill());
          }

          float hiDewNative = data.getHiDewNative();
          if (hiDewNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][9] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][9] = Float.toString(data.getHiDew());
          }

          float lowDewNative = data.getLowDewNative();
          if (lowDewNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][10] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][10] = Float.toString(data.getLowDew());
          }

          short avgChillNative = data.getAvgChillNative();
          if (avgChillNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][11] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][11] = Float.toString(data.getAvgChill());
          }

          short avgDewNative = data.getAvgDewNative();
          if (avgDewNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][12] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][12] = Float.toString(data.getAvgDew());
          }

          short hiOutHumidNative = data.getHiOutHumidNative();
          if (hiOutHumidNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][13] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][13] = Float.toString(data.getHiOutHumid());
          }

          short lowOutHumidNative = data.getLowOutHumidNative();
          if (lowOutHumidNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][14] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][14] = Float.toString(data.getLowOutHumid());
          }

          short hiInHumidNative = data.getHiInHumidNative();
          if (hiInHumidNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][15] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][15] = Float.toString(data.getHiInHumid());
          }

          short lowInHumidNative = data.getLowInHumidNative();
          if (lowInHumidNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][16] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][16] = Float.toString(data.getLowInHumid());
          }

          short avgOutHumidNative = data.getAvgOutHumidNative();
          if (avgOutHumidNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][17] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][17] = Float.toString(data.getAvgOutHumid());
          }

          short hiBarNative = data.getHiBarNative();
          if (hiBarNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][18] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][18] = Float.toString(data.getHiBar());
          }

          short lowBarNative = data.getLowBarNative();
          if (lowBarNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][19] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][19] = Float.toString(data.getLowBar());
          }

          short avgBarNative = data.getAvgBarNative();
          if (avgBarNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][20] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][20] = Float.toString(data.getAvgBar());
          }

          short hiSpeedNative = data.getHiSpeedNative();
          if (hiSpeedNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][21] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][21] = Float.toString(data.getHiSpeed());
          }

          short avgSpeedNative = data.getAvgSpeedNative();
          if (avgSpeedNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][22] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][22] = Float.toString(data.getAvgSpeed());
          }

          short dailyWindRunTotalNative = data.getDailyWindRunTotalNative();
          if (dailyWindRunTotalNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][23] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][23] = Float.toString(data.getDailyWindRunTotal());
          }

          short hiTenMinSpeedNative = data.getHiTenMinSpeedNative();
          if (hiTenMinSpeedNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][24] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][24] = Float.toString(data.getHiTenMinSpeed());
          }

          WindDirection dirHiSpeedNative = data.getDirHiSpeed();
          if (dirHiSpeedNative == null)
          {
            tableData[rowCount][25] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][25] = data.getDirHiSpeed().toString();
          }

          WindDirection dirHiTenMinNative = data.getDirHiTenMin();
          if (dirHiTenMinNative == null)
          {
            tableData[rowCount][26] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][26] = data.getDirHiTenMin().toString();
          }

          short dailyRainTotalNative = data.getDailyRainTotalNative();
          if (dailyRainTotalNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][27] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][27] = Float.toString(data.getDailyRainTotal());
          }

          short hiRainRateNative = data.getHiRainRateNative();
          if (hiRainRateNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][28] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][28] = Float.toString(data.getHiRainRate());
          }

          rowCount++;
        }
        catch (Exception exc)
        {
          System.out.println("Summary2DataTable: error in dataset." + exc);
        }
      }
      dayCount++;
      nextRecord = dbReader.getNextRecord();
    }

    JTable table = new JTable(tableData, columnNames);
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

    TableModel model = (TableModel) event.getSource();

    // Fill in a new record, regardless of what value in the row changed.
    DailySummary1Record updatedRecord = new DailySummary1Record();

    // A numeric object already in the table is a float value.  If you input a new numeric it is a string value.
    Object hiOutTempObject = model.getValueAt(row, 1);
    if (hiOutTempObject instanceof String)
    {
      String hiOutTempString = (String)hiOutTempObject;
      if (hiOutTempString.equals(" ") || hiOutTempString.equals("-") || hiOutTempString.equals("--") ||
          hiOutTempString.equals("---"))
      {
        updatedRecord.setHiOutTempNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHiOutTemp(Short.parseShort(hiOutTempString));
      }
    }
    else
    {
      updatedRecord.setHiOutTemp((Short)hiOutTempObject);
    }

    Object lowOutTempObject = model.getValueAt(row, 2);
    if (lowOutTempObject instanceof String)
    {
      String lowOutTempString = (String)lowOutTempObject;
      if (lowOutTempString.equals(" ") || lowOutTempString.equals("-") || lowOutTempString.equals("--") ||
          lowOutTempString.equals("---"))
      {
        updatedRecord.setLowOutTempNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setLowOutTemp(Integer.parseInt(lowOutTempString));
      }
    }
    else
    {
      updatedRecord.setLowOutTemp((Integer) lowOutTempObject);
    }

    Object hiInTempObject = model.getValueAt(row, 3);
    if (hiInTempObject instanceof String)
    {
      String hiInTemnpString = (String)hiInTempObject;
      if (hiInTemnpString.equals(" ") || hiInTemnpString.equals("-") || hiInTemnpString.equals("--") ||
          hiInTemnpString.equals("---"))
      {
        updatedRecord.setHiInTempNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHiInTemp(Float.parseFloat(hiInTemnpString));
      }
    }
    else
    {
      updatedRecord.setHiInTemp((Float)hiInTempObject);
    }

    Object lowInTempObject = model.getValueAt(row, 4);
    if (lowInTempObject instanceof String)
    {
      String lowInTempString = (String)lowInTempObject;
      if (lowInTempString.equals(" ") || lowInTempString.equals("-") || lowInTempString.equals("--") ||
          lowInTempString.equals("---"))
      {
        updatedRecord.setLowInTempNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setLowInTemp(Short.parseShort(lowInTempString));
      }
    }
    else
    {
      updatedRecord.setLowInTemp((Short)lowInTempObject);
    }

    Object avgOutTempObject = model.getValueAt(row, 5);
    if (avgOutTempObject instanceof String)
    {
      String avgOutTempString = (String)avgOutTempObject;
      if (avgOutTempString.equals(" ") || avgOutTempString.equals("-") || avgOutTempString.equals("--") ||
          avgOutTempString.equals("---"))
      {
        updatedRecord.setAvgOutTempNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setAvgOutTemp(Byte.parseByte(avgOutTempString));
      }
    }
    else
    {
      updatedRecord.setAvgOutTemp((Byte)avgOutTempObject);
    }

    Object avgInTempObject = model.getValueAt(row, 6);
    if (avgInTempObject instanceof String)
    {
      String avgInTempString = (String)avgInTempObject;
      if (avgInTempString.equals(" ") || avgInTempString.equals("-") || avgInTempString.equals("--") ||
          avgInTempString.equals("---"))
      {
        updatedRecord.setAvgInTempNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setAvgInTemp(Float.parseFloat(avgInTempString));
      }
    }
    else
    {
      updatedRecord.setAvgInTemp((Float)avgInTempObject);
    }


    Object hiWindChillObject = model.getValueAt(row, 7);
    if (hiWindChillObject instanceof String)
    {
      String hiWindChillString = (String)hiWindChillObject;
      if (hiWindChillString.equals(" ") || hiWindChillString.equals("-") || hiWindChillString.equals("--") ||
          hiWindChillString.equals("---"))
      {
        updatedRecord.setHiChillNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHiChill(Float.parseFloat(hiWindChillString));
      }
    }
    else
    {
      updatedRecord.setHiChill((Float)hiWindChillObject);
    }


    Object hiDewPointObject = model.getValueAt(row, 8);
    if (hiDewPointObject instanceof String)
    {
      String hiDewPointString = (String)hiDewPointObject;
      if (hiDewPointString.equals(" ") || hiDewPointString.equals("-") || hiDewPointString.equals("--") ||
          hiDewPointString.equals("---"))
      {
        updatedRecord.setHiDewNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHiDew(Float.parseFloat(hiDewPointString));
      }
    }
    else
    {
      updatedRecord.setHiDew((Float)hiDewPointObject);
    }

    Object lowDewPointObject = model.getValueAt(row, 9);
    if (lowDewPointObject instanceof String)
    {
      String lowDewPointString = (String)lowDewPointObject;
      if (lowDewPointString.equals(" ") || lowDewPointString.equals("-") || lowDewPointString.equals("--") ||
          lowDewPointString.equals("---"))
      {
        updatedRecord.setLowDewNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setLowDew(Float.parseFloat(lowDewPointString));
      }
    }
    else
    {
      updatedRecord.setLowDew((Float)lowDewPointObject);
    }

    Object avgWindChillObject = model.getValueAt(row, 10);
    if (avgWindChillObject instanceof String)
    {
      String avgWindChillString = (String)avgWindChillObject;
      if (avgWindChillString.equals(" ") || avgWindChillString.equals("-") || avgWindChillString.equals("--") ||
          avgWindChillString.equals("---"))
      {
        updatedRecord.setAvgChillNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setAvgChill(Float.parseFloat(avgWindChillString));
      }
    }
    else
    {
      updatedRecord.setAvgChill((Float)avgWindChillObject);
    }

    Object avgDewPointObject = model.getValueAt(row, 11);
    if (avgDewPointObject instanceof String)
    {
      String avgDewPointString = (String)avgDewPointObject;
      if (avgDewPointString.equals(" ") || avgDewPointString.equals("-") || avgDewPointString.equals("--") ||
          avgDewPointString.equals("---"))
      {
        updatedRecord.setAvgDewNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setAvgDew(Short.parseShort(avgDewPointString));
      }
    }
    else
    {
      updatedRecord.setAvgDew((Short)avgDewPointObject);
    }

    Object hiOutHumidObject = model.getValueAt(row, 12);
    if (hiOutHumidObject instanceof String)
    {
      String hiOutHumidString = (String)hiOutHumidObject;
      if (hiOutHumidString.equals(" ") || hiOutHumidString.equals("-") || hiOutHumidString.equals("--") ||
          hiOutHumidString.equals("---"))
      {
        updatedRecord.setHiOutHumidNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setHiOutHumid(Short.parseShort(hiOutHumidString));
      }
    }
    else
    {
      updatedRecord.setHiOutHumid((Short)hiOutHumidObject);
    }

    Object lowOutHumidObject = model.getValueAt(row, 13);
    if (lowOutHumidObject instanceof String)
    {
      String lowOutHumidString = (String)lowOutHumidObject;
      if (lowOutHumidString.equals(" ") || lowOutHumidString.equals("-") || lowOutHumidString.equals("--") ||
          lowOutHumidString.equals("---"))
      {
        updatedRecord.setLowOutHumidNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setLowOutHumid(Short.parseShort(lowOutHumidString));
      }
    }
    else
    {
      updatedRecord.setLowOutHumid((Short)lowOutHumidObject);
    }

    Object hiInHumidObject = model.getValueAt(row, 14);
    if (hiInHumidObject instanceof String)
    {
      String hiInHumidString = (String)hiInHumidObject;
      if (hiInHumidString.equals(" ") || hiInHumidString.equals("-") || hiInHumidString.equals("--") ||
          hiInHumidString.equals("---"))
      {
        updatedRecord.setHiInHumidNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHiInHumid(Short.parseShort(hiInHumidString));
      }
    }
    else
    {
      updatedRecord.setHiInHumid((Short)hiInHumidObject);
    }

    Object lowInHumidObject = model.getValueAt(row, 15);
    if (lowInHumidObject instanceof String)
    {
      String lowInHumid = (String)lowInHumidObject;
      if (lowInHumid.equals(" ") || lowInHumid.equals("-") || lowInHumid.equals("--") ||
          lowInHumid.equals("---"))
      {
        updatedRecord.setLowInHumidNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setLowInHumid(Short.parseShort(lowInHumid));
      }
    }
    else
    {
      updatedRecord.setLowInHumid((Short)lowInHumidObject);
    }

    Object avgOutHumidObject = model.getValueAt(row, 16);
    if (avgOutHumidObject instanceof String)
    {
      String avgOutHumidString = (String)avgOutHumidObject;
      if (avgOutHumidString.equals(" ") || avgOutHumidString.equals("-") || avgOutHumidString.equals("--") ||
          avgOutHumidString.equals("---"))
      {
        updatedRecord.setAvgOutHumidNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setAvgOutHumid(Short.parseShort(avgOutHumidString));
      }
    }
    else
    {
      updatedRecord.setAvgOutHumid((Short)avgOutHumidObject);
    }

    Object hiPressureObject = model.getValueAt(row, 17);
    if (hiPressureObject instanceof String)
    {
      String hiPressureString = (String)hiPressureObject;
      if (hiPressureString.equals(" ") || hiPressureString.equals("-") || hiPressureString.equals("--") ||
          hiPressureString.equals("---"))
      {
        updatedRecord.setHiBarNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setHiBar(Short.parseShort(hiPressureString));
      }
    }
    else
    {
      updatedRecord.setHiBar((Short)hiPressureObject);
    }

    Object lowPressureObject = model.getValueAt(row, 18);
    if (lowPressureObject instanceof String)
    {
      String lowPressureString = (String)lowPressureObject;
      if (lowPressureString.equals(" ") || lowPressureString.equals("-") || lowPressureString.equals("--") ||
          lowPressureString.equals("---"))
      {
        updatedRecord.setLowBarNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setLowBar(Short.parseShort(lowPressureString));
      }
    }
    else
    {
      updatedRecord.setLowBar((Short)lowPressureObject);
    }

    Object avgPressureObject = model.getValueAt(row, 19);
    if (avgPressureObject instanceof String)
    {
      String avgPressureString = (String)avgPressureObject;
      if (avgPressureString.equals(" ") || avgPressureString.equals("-") || avgPressureString.equals("--") ||
          avgPressureString.equals("---"))
      {
        updatedRecord.setAvgBarNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setAvgBar(Short.parseShort(avgPressureString));
      }
    }
    else
    {
      updatedRecord.setAvgBar((Short)avgPressureObject);
    }

    Object hiWindSpeedObject = model.getValueAt(row, 20);
    if (hiWindSpeedObject instanceof String)
    {
      String hiWindSpeedString = (String)hiWindSpeedObject;
      if (hiWindSpeedString.equals(" ") || hiWindSpeedString.equals("-") || hiWindSpeedString.equals("--") ||
          hiWindSpeedString.equals("---"))
      {
        updatedRecord.setHiSpeedNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setHiSpeed(Short.parseShort(hiWindSpeedString));
      }
    }
    else
    {
      updatedRecord.setHiSpeed((Short)hiWindSpeedObject);
    }

    Object avgWindSpeedObject = model.getValueAt(row, 21);
    if (avgWindSpeedObject instanceof String)
    {
      String avgWindSpeedString = (String)avgWindSpeedObject;
      if (avgWindSpeedString.equals(" ") || avgWindSpeedString.equals("-") || avgWindSpeedString.equals("--") ||
          avgWindSpeedString.equals("---"))
      {
        updatedRecord.setAvgSpeedNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setAvgSpeed(Short.parseShort(avgWindSpeedString));
      }
    }
    else
    {
      updatedRecord.setAvgSpeed((Short)avgWindSpeedObject);
    }

    Object dailyWindRunTotalObject = model.getValueAt(row, 22);
    if (dailyWindRunTotalObject instanceof String)
    {
      String dailyWindRunTotalString = (String)dailyWindRunTotalObject;
      if (dailyWindRunTotalString.equals(" ") || dailyWindRunTotalString.equals("-") || dailyWindRunTotalString.equals("--") ||
          dailyWindRunTotalString.equals("---"))
      {
        updatedRecord.setDailyWindRunTotalNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setDailyWindRunTotal(Short.parseShort(dailyWindRunTotalString));
      }
    }
    else
    {
      updatedRecord.setDailyWindRunTotal((Short)dailyWindRunTotalObject);
    }

    Object hiTenMinSpeedObject = model.getValueAt(row, 23);
    if (hiTenMinSpeedObject instanceof String)
    {
      String hiTenMinSpeedString = (String)hiTenMinSpeedObject;
      if (hiTenMinSpeedString.equals(" ") || hiTenMinSpeedString.equals("-") || hiTenMinSpeedString.equals("--") ||
          hiTenMinSpeedString.equals("---"))
      {
        updatedRecord.setHiTenMinSpeedNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setHiTenMinSpeed(Short.parseShort(hiTenMinSpeedString));
      }
    }
    else
    {
      updatedRecord.setHiTenMinSpeed((Short)hiTenMinSpeedObject);
    }

    Object dirHiWindSpeedObject = model.getValueAt(row, 24);
    if (dirHiWindSpeedObject instanceof String)
    {
      String dirHiWindSpeedString = (String)dirHiWindSpeedObject;
      if (dirHiWindSpeedString.equals(" ") || dirHiWindSpeedString.equals("-") || dirHiWindSpeedString.equals("--") ||
          dirHiWindSpeedString.equals("---"))
      {
        updatedRecord.setDirHiSpeedNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        WindDirection hiWindDir = WindDirection.valueOf(dirHiWindSpeedString);
        updatedRecord.setDirHiSpeedNative((byte)(hiWindDir.value()));
      }
    }

    Object dirHiTenMinSpeedObject = model.getValueAt(row, 25);
    if (dirHiTenMinSpeedObject instanceof String)
    {
      String dirHiTenMinSpeedString = (String)dirHiTenMinSpeedObject;
      if (dirHiTenMinSpeedString.equals(" ") || dirHiTenMinSpeedString.equals("-") || dirHiTenMinSpeedString.equals("--") ||
          dirHiTenMinSpeedString.equals("---"))
      {
        updatedRecord.setDirHiTenMinNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        WindDirection hiTenMinWindDir = WindDirection.valueOf(dirHiTenMinSpeedString);
        updatedRecord.setDirHiTenMinNative((byte)(hiTenMinWindDir.value()));
      }
    }

    Object dailyRainTotalObject = model.getValueAt(row, 26);
    if (dailyRainTotalObject instanceof String)
    {
      String dailyRainTotalString = (String)dailyRainTotalObject;
      if (dailyRainTotalString.equals(" ") || dailyRainTotalString.equals("-") || dailyRainTotalString.equals("--") ||
          dailyRainTotalString.equals("---"))
      {
        updatedRecord.setDailyRainTotalNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setDailyRainTotal(Short.parseShort(dailyRainTotalString));
      }
    }
    else
    {
      updatedRecord.setDailyRainTotal((Short)dailyRainTotalObject);
    }

    Object hiRainRateObject = model.getValueAt(row, 27);
    if (hiRainRateObject instanceof String)
    {
      String hiRainRateString = (String)hiRainRateObject;
      if (hiRainRateString.equals(" ") || hiRainRateString.equals("-") || hiRainRateString.equals("--") ||
          hiRainRateString.equals("---"))
      {
        updatedRecord.setHiRainRateNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setHiRainRate(Short.parseShort(hiRainRateString));
      }
    }
    else
    {
      updatedRecord.setHiRainRate((Short)hiRainRateObject);
    }

    // Update the record.  This assumes there is one record for each day, i.e. no missing days worth of data.
    databaseWriter.updateSummary1Record(updatedRecord, year, month, row);
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
   */
  public static void createAndShowGUI(JPanel dataTable)
  {
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame("Summary 1 Data Table");

    // content panes must be opaque
    dataTable.setOpaque(true);

    frame.setContentPane(dataTable);
    frame.setSize(PROPS.getWindowWidth(), PROPS.getWindowHeight());
    frame.setVisible(true);
  }
}
