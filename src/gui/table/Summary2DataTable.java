/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the raw daily summary 2
            data table.  This data can be modified.  A string value of "---"
            means no data is available.  In many cases the DB native value
            stored is 0x8000.

  Mods:		  10/18/21  Initial Release.
*/
package gui.table;

import data.dbrecord.DailySummary2Record;
import data.dbrecord.DataFileRecord;
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

public class Summary2DataTable extends JPanel implements TableModelListener
{
  private final DatabaseWriter databaseWriter = new DatabaseWriter();
  private final DatabaseReader dbReader = DatabaseReader.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private final Logger logger = Logger.getInstance();
  private int year;
  private int month;

  private static final String NUM_WIND_PACKETS_STRING = "# Wind Packets";
  private static final String HI_SOLAR_STRING = "Hi Solar";
  private static final String DAILY_SOLAR_ENERGY_STRING = "Daily Solar Energy";
  private static final String MIN_SUNLIGHT_STRING = "Min Sunlight";
  private static final String DAILY_ET_TOTAL_STRING = "Daily ET Total";
  private static final String HI_HEAT_INDEX_STRING = "Hi Heat Index";
  private static final String LOW_HEAT_INDEX_STRING = "Low Heat Index";
  private static final String AVG_HEAT_INDEX_STRING = "Avg Heat Index";
  private static final String HI_THSW_STRING = "Hi THSW";
  private static final String LOW_THSW_STRING = "Low THSW";
  private static final String HI_THW_STRING = "Hi THW";
  private static final String LOW_THW_STRING = "Low THW";
  private static final String HEAT_DD65_STRING = "Heat DD65";
  private static final String HI_WET_BULB_STRING = "Hi Wet Bulb";
  private static final String LOW_WET_BULB_STRING = "Low Wet Bulb";
  private static final String AVG_WET_BULB_STRING = "Avg Wet Bulb";
  private static final String COOL_DD65_STRING = "Cool DD65";

  private final String[] columnNames =
    {"Date",
     NUM_WIND_PACKETS_STRING,
     HI_SOLAR_STRING,
     DAILY_SOLAR_ENERGY_STRING,
     MIN_SUNLIGHT_STRING,
     DAILY_ET_TOTAL_STRING,
     HI_HEAT_INDEX_STRING,
     LOW_HEAT_INDEX_STRING,
     AVG_HEAT_INDEX_STRING,
     HI_THSW_STRING,
     LOW_THSW_STRING,
     HI_THW_STRING,
     LOW_THW_STRING,
     HEAT_DD65_STRING,
     HI_WET_BULB_STRING,
     LOW_WET_BULB_STRING,
     AVG_WET_BULB_STRING,
     COOL_DD65_STRING
    };

  /**
   * The class constructor.
   */
  public Summary2DataTable()
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
      logger.logData("Summary2DataTable: createTable: Unable to read data: " + e.getLocalizedMessage());
      return;
    }

    int totalRows = dbReader.getRowCount();
    Object[][] tableData = new Object[totalRows][19];
    int rowCount = 0;
    int dayCount = 1;

    DataFileRecord nextRecord = dbReader.getNextRecord();
    while (nextRecord != null)
    {
      if (nextRecord instanceof DailySummary2Record)
      {
        DailySummary2Record data = (DailySummary2Record) nextRecord;
        try
        {
          tableData[rowCount][0] = year + "/" + month + "/" + dayCount;

          tableData[rowCount][1] = Integer.toString(data.getNumOfWindPackets());

          int hiSolar = data.getHiSolar();
          if (hiSolar == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][2] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][2] = Float.toString(data.getHiSolar());
          }

          short dailySolarEnergyNative = data.getDailySolarEnergyNative();
          if (dailySolarEnergyNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][3] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][3] = Float.toString(data.getDailySolarEnergy());
          }

          short minSunlight = data.getMinSunlight();
          if (minSunlight == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][4] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][4] = Float.toString(data.getMinSunlight());
          }

          short dailyEtTotalNative = data.getDailyETTotalNative();
          if (dailyEtTotalNative == DatabaseCommon.UNDEFINED_BYTE_VALUE)
          {
            tableData[rowCount][5] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][5] = Float.toString(data.getDailyETTotal());
          }

          short hiHeatNative = data.getHiHeatNative();
          if (hiHeatNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][6] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][6] = Float.toString(data.getHiHeat());
          }

          short lowHeatNative = data.getLowHeatNative();
          if (lowHeatNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][7] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][7] = Float.toString(data.getLowHeat());
          }

          short avgHeatNative = data.getAvgHeatNative();
          if (avgHeatNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][8] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][8] = Float.toString(data.getAvgHeat());
          }

          float hiTHSWNative = data.getHiTHSWNative();
          if (hiTHSWNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][9] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][9] = Float.toString(data.getHiTHSW());
          }

          float lowTHSWNative = data.getLowTHSWNative();
          if (lowTHSWNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][10] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][10] = Float.toString(data.getLowTHSW());
          }

          short hiTHWNative = data.getHiTHWNative();
          if (hiTHWNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][11] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][11] = Float.toString(data.getHiTHW());
          }

          short lowTHWNative = data.getLowTHWNative();
          if (lowTHWNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][12] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][12] = Float.toString(data.getLowTHW());
          }

          short integHeatDD65Native = data.getIntegratedHeatDD65Native();
          if (integHeatDD65Native == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][13] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][13] = Float.toString(data.getIntegratedHeatDD65());
          }

          short hiWetBuldTempNative = data.getHiWetBuldTempNative();
          if (hiWetBuldTempNative == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][14] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][14] = Float.toString(data.getHiWetBuldTemp());
          }

          short lowWetBulbTempNataive = data.getLowWetBulbTempNative();
          if (lowWetBulbTempNataive == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][15] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][15] = Float.toString(data.getLowWetBulbTemp());
          }

          short avgWetBulbTempNataive = data.getAvgWetBulbTempNative();
          if (avgWetBulbTempNataive == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][16] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][16] = Float.toString(data.getAvgWetBulbTemp());
          }

          short integCoolDD65Native = data.getIntegratedCoolDD65Native();
          if (integCoolDD65Native == DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            tableData[rowCount][17] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          }
          else
          {
            tableData[rowCount][17] = Float.toString(data.getIntegratedCoolDD65());
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

    // Fill in a new record, reguardless of what value in the row changed.
    DailySummary2Record updatedRecord = new DailySummary2Record();

    // A numeric object already in the table is a float value.  If you input a new numeric it is a string value.
    Object numWindPacketsObject = model.getValueAt(row, 1);
    if (numWindPacketsObject instanceof String)
    {
      String numWindPacketsString = (String)numWindPacketsObject;
      if (numWindPacketsString.equals(" ") || numWindPacketsString.equals("-") || numWindPacketsString.equals("--") ||
          numWindPacketsString.equals("---"))
      {
        updatedRecord.setNumOfWindPackets(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setNumOfWindPackets(Short.parseShort(numWindPacketsString));
      }
    }
    else
    {
      updatedRecord.setNumOfWindPackets((Short)numWindPacketsObject);
    }

    Object hiSolarObject = model.getValueAt(row, 2);
    if (hiSolarObject instanceof String)
    {
      String hiSolarString = (String)hiSolarObject;
      if (hiSolarString.equals(" ") || hiSolarString.equals("-") || hiSolarString.equals("--") ||
          hiSolarString.equals("---"))
      {
        updatedRecord.setHiSolar(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHiSolar(Integer.parseInt(hiSolarString));
      }
    }
    else
    {
      updatedRecord.setHiSolar((Integer) hiSolarObject);
    }

    Object dailySolarEnergyObject = model.getValueAt(row, 3);
    if (dailySolarEnergyObject instanceof String)
    {
      String dailySolarEnergyString = (String)dailySolarEnergyObject;
      if (dailySolarEnergyString.equals(" ") || dailySolarEnergyString.equals("-") || dailySolarEnergyString.equals("--") ||
          dailySolarEnergyString.equals("---"))
      {
        updatedRecord.setDailySolarEnergyNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setDailySolarEnergy(Float.parseFloat(dailySolarEnergyString));
      }
    }
    else
    {
      updatedRecord.setDailySolarEnergy((Float)dailySolarEnergyObject);
    }

    Object minSunlightObject = model.getValueAt(row, 4);
    if (minSunlightObject instanceof String)
    {
      String minSunlightString = (String)minSunlightObject;
      if (minSunlightString.equals(" ") || minSunlightString.equals("-") || minSunlightString.equals("--") ||
          minSunlightString.equals("---"))
      {
        updatedRecord.setMinSunlight(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setMinSunlight(Short.parseShort(minSunlightString));
      }
    }
    else
    {
      updatedRecord.setMinSunlight((Short)minSunlightObject);
    }

    Object dailyEtTotalObject = model.getValueAt(row, 5);
    if (dailyEtTotalObject instanceof String)
    {
      String dailyEtTotalString = (String)dailyEtTotalObject;
      if (dailyEtTotalString.equals(" ") || dailyEtTotalString.equals("-") || dailyEtTotalString.equals("--") ||
          dailyEtTotalString.equals("---"))
      {
        updatedRecord.setDailyETTotalNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setDailyETTotal(Byte.parseByte(dailyEtTotalString));
      }
    }
    else
    {
      updatedRecord.setDailyETTotal((Byte)dailyEtTotalObject);
    }

    Object hiHeatIndexObject = model.getValueAt(row, 6);
    if (hiHeatIndexObject instanceof String)
    {
      String hiHeatIndexString = (String)hiHeatIndexObject;
      if (hiHeatIndexString.equals(" ") || hiHeatIndexString.equals("-") || hiHeatIndexString.equals("--") ||
          hiHeatIndexString.equals("---"))
      {
        updatedRecord.setHiHeatNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHiHeat(Float.parseFloat(hiHeatIndexString));
      }
    }
    else
    {
      updatedRecord.setHiHeat((Float)hiHeatIndexObject);
    }


    Object lowHeatIndexObject = model.getValueAt(row, 7);
    if (lowHeatIndexObject instanceof String)
    {
      String lowHeatIndexString = (String)lowHeatIndexObject;
      if (lowHeatIndexString.equals(" ") || lowHeatIndexString.equals("-") || lowHeatIndexString.equals("--") ||
          lowHeatIndexString.equals("---"))
      {
        updatedRecord.setLowHeatNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setLowHeat(Float.parseFloat(lowHeatIndexString));
      }
    }
    else
    {
      updatedRecord.setLowHeat((Float)lowHeatIndexObject);
    }


    Object avgHeatIndexObject = model.getValueAt(row, 8);
    if (avgHeatIndexObject instanceof String)
    {
      String avgHeatIndexString = (String)avgHeatIndexObject;
      if (avgHeatIndexString.equals(" ") || avgHeatIndexString.equals("-") || avgHeatIndexString.equals("--") ||
          avgHeatIndexString.equals("---"))
      {
        updatedRecord.setAvgHeatNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setAvgHeat(Float.parseFloat(avgHeatIndexString));
      }
    }
    else
    {
      updatedRecord.setAvgHeat((Float)avgHeatIndexObject);
    }

    Object hiTHSWObject = model.getValueAt(row, 9);
    if (hiTHSWObject instanceof String)
    {
      String hiTHSWString = (String)hiTHSWObject;
      if (hiTHSWString.equals(" ") || hiTHSWString.equals("-") || hiTHSWString.equals("--") ||
          hiTHSWString.equals("---"))
      {
        updatedRecord.setHiTHSWNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHiTHSW(Float.parseFloat(hiTHSWString));
      }
    }
    else
    {
      updatedRecord.setHiTHSW((Float)hiTHSWObject);
    }

    Object lowTHSWObject = model.getValueAt(row, 10);
    if (lowTHSWObject instanceof String)
    {
      String lowTHSWString = (String)lowTHSWObject;
      if (lowTHSWString.equals(" ") || lowTHSWString.equals("-") || lowTHSWString.equals("--") ||
          lowTHSWString.equals("---"))
      {
        updatedRecord.setLowTHSWNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setLowTHSW(Float.parseFloat(lowTHSWString));
      }
    }
    else
    {
      updatedRecord.setLowTHSW((Float)lowTHSWObject);
    }

    Object hiTHWObject = model.getValueAt(row, 11);
    if (hiTHWObject instanceof String)
    {
      String hiTHWString = (String)hiTHWObject;
      if (hiTHWString.equals(" ") || hiTHWString.equals("-") || hiTHWString.equals("--") ||
          hiTHWString.equals("---"))
      {
        updatedRecord.setHiTHWNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHiTHW(Short.parseShort(hiTHWString));
      }
    }
    else
    {
      updatedRecord.setHiTHW((Short)hiTHWObject);
    }

    Object lowTHWObject = model.getValueAt(row, 12);
    if (lowTHWObject instanceof String)
    {
      String lowTHWString = (String)lowTHWObject;
      if (lowTHWString.equals(" ") || lowTHWString.equals("-") || lowTHWString.equals("--") ||
          lowTHWString.equals("---"))
      {
        updatedRecord.setLowTHWNative(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setLowTHW(Short.parseShort(lowTHWString));
      }
    }
    else
    {
      updatedRecord.setLowTHW((Short)lowTHWObject);
    }

    Object integHeatDD65Object = model.getValueAt(row, 13);
    if (integHeatDD65Object instanceof String)
    {
      String integHeatDD65String = (String)integHeatDD65Object;
      if (integHeatDD65String.equals(" ") || integHeatDD65String.equals("-") || integHeatDD65String.equals("--") ||
          integHeatDD65String.equals("---"))
      {
        updatedRecord.setIntegratedHeatDD65Native(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setIntegratedHeatDD65(Short.parseShort(integHeatDD65String));
      }
    }
    else
    {
      updatedRecord.setIntegratedHeatDD65((Short)integHeatDD65Object);
    }

    Object hiWetBulbObject = model.getValueAt(row, 14);
    if (hiWetBulbObject instanceof String)
    {
      String hiWetBulbString = (String)hiWetBulbObject;
      if (hiWetBulbString.equals(" ") || hiWetBulbString.equals("-") || hiWetBulbString.equals("--") ||
          hiWetBulbString.equals("---"))
      {
        updatedRecord.setHiWetBulbTempNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setHiWetBulbTemp(Short.parseShort(hiWetBulbString));
      }
    }
    else
    {
      updatedRecord.setHiWetBulbTemp((Short)hiWetBulbObject);
    }

    Object lowWetBulbObject = model.getValueAt(row, 15);
    if (lowWetBulbObject instanceof String)
    {
      String lowWetBulbString = (String)lowWetBulbObject;
      if (lowWetBulbString.equals(" ") || lowWetBulbString.equals("-") || lowWetBulbString.equals("--") ||
          lowWetBulbString.equals("---"))
      {
        updatedRecord.setLowWetBulbTempNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setLowWetBulbTemp(Short.parseShort(lowWetBulbString));
      }
    }
    else
    {
      updatedRecord.setLowWetBulbTemp((Short)lowWetBulbObject);
    }

    Object avgWetBulbObject = model.getValueAt(row, 16);
    if (avgWetBulbObject instanceof String)
    {
      String avgWetBulbString = (String)avgWetBulbObject;
      if (avgWetBulbString.equals(" ") || avgWetBulbString.equals("-") || avgWetBulbString.equals("--") ||
          avgWetBulbString.equals("---"))
      {
        updatedRecord.setAvgWetBulbTempNative(DatabaseCommon.UNDEFINED_SHORT_VALUE);
      }
      else
      {
        updatedRecord.setAvgWetBulbTemp(Short.parseShort(avgWetBulbString));
      }
    }
    else
    {
      updatedRecord.setAvgWetBulbTemp((Short)avgWetBulbObject);
    }

    Object integCoolDD65Object = model.getValueAt(row, 17);
    if (integCoolDD65Object instanceof String)
    {
      String integCoolDD65String = (String)integCoolDD65Object;
      if (integCoolDD65String.equals(" ") || integCoolDD65String.equals("-") || integCoolDD65String.equals("--") ||
          integCoolDD65String.equals("---"))
      {
        updatedRecord.setIntegratedCoolDD65Native(DatabaseCommon.UNDEFINED_BYTE_VALUE);
      }
      else
      {
        updatedRecord.setIntegratedCoolDD65(Short.parseShort(integCoolDD65String));
      }
    }
    else
    {
      updatedRecord.setIntegratedCoolDD65((Short)integCoolDD65Object);
    }

    // Update the record.  This assumes there is one record for each day, i.e. no missing days worth of data.
    databaseWriter.updateSummary2Record(updatedRecord, year, month, row);
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
   */
  public static void createAndShowGUI(JPanel dataTable)
  {
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame("Summary 2 Data Table");

    // content panes must be opaque
    dataTable.setOpaque(true);

    frame.setContentPane(dataTable);
    frame.setSize(PROPS.getWindowWidth(), PROPS.getWindowHeight());
    frame.setVisible(true);
  }
}
