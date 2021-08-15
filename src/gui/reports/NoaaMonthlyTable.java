/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class displays the NOAA Monthly table.

  Mods:		  09/01/21 Initial Release.
*/
package gui.reports;

import data.dbrecord.*;
import dbif.DatabaseReader;
import util.ConfigProperties;
import util.Logger;
import util.TimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.util.Locale;

public class NoaaMonthlyTable extends JPanel
{
  private static final JFrame FRAME = new JFrame();
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private final Logger logger = Logger.getInstance();

  private static final String[] columnNames =
    {"Day",
     "Mean Temp",
     "High Temp",
     "High Time",
     "Low Temp",
     "Low Time",
     "Heat Degree Days",
     "Cool Degree Days",
     "Rain Total",
     "Avg. Wind Speed",
     "High Wind Speed",
     "High Time",
     "Dom. Dir."
    };

  private static final String HEADER1 = "\n               Monthly NOAA Climatology Summary for ";
  private static final String TABLE_HEADER1_FILE =
    "                                                   Heat     Cool              Avg";
  private static final String TABLE_HEADER2_FILE =
    "      Mean                                         Degree   Degree            Wind                       Dom";
  private static final String TABLE_HEADER3_FILE =
    "Day   Temp     High     Time      Low     Time     Days     Days     Rain     Speed    High     Time     Dir";
  private static final String TABLE_HEADER4_FILE =
    "------------------------------------------------------------------------------------------------------------";

  private static final DatabaseReader dbReader = DatabaseReader.getInstance();

  /**
   * Calculate the data and generate the reports, both the data for a JPanel and a txt file.
   *
   * @param year The year to report.
   * @param month The month to report.
   * @param filename The filename of the report.
   */
  public  NoaaMonthlyTable(int year, int month, String filename)
  {
    super(new GridLayout(1, 0));
    String monthString = new DateFormatSymbols().getMonths()[month-1];
    FRAME.setTitle("NOAA Monthly Data Table: " + monthString + " " + year);

    YearMonth yearMonthObject = YearMonth.of(year, month);
    int daysInMonth = yearMonthObject.lengthOfMonth();
    Object[][] tableData = new Object[daysInMonth + 3][13];

    File file = new File("reports" + "/" + filename);
    try (PrintWriter writer = new PrintWriter(file.getAbsoluteFile(), "UTF-8"))
    {
      // Write header info.
      String header1String = HEADER1 + new DateFormatSymbols().getMonths()[month - 1] + " " + year + "\n";
      writer.println(header1String);

      String header2String = "Name: Station1    City: Colorado Springs    State: Colorado\n";
      writer.println(header2String);

      float latDecimal = PROPS.getLatitude();
      int latDegrees = (int)latDecimal;
      double tempLat = (latDecimal - latDegrees) * 60;
      int latMinutes = (int)tempLat;
      double latSeconds = (tempLat - latMinutes) * 60;

      float lonDecimal = PROPS.getLongitude();
      int lonDegrees = (int)lonDecimal;
      double tempLon = (lonDecimal - lonDegrees) * 60;
      int lonMinutes = (int)tempLon;
      double lonSeconds = (tempLon - lonMinutes) * 60;

      String header3String = "Elev: " + PROPS.getElevation() + " ft     Lat: " + latDegrees + "\u00b0 " +
        latMinutes + "' " + String.format("%.02f", latSeconds) + "\"" + "    Long: " +
        lonDegrees + "\u00b0 " + lonMinutes + "' " + String.format("%.02f", lonSeconds) + "\"\n";
      writer.println(header3String);

      String header4String = "          Temperature (" + "\u00b0" + "F),  Rain (in),  Wind Speed (MPH)\n";
      writer.println(header4String);

      writer.println(TABLE_HEADER1_FILE);
      writer.println(TABLE_HEADER2_FILE);
      writer.println(TABLE_HEADER3_FILE);
      writer.println(TABLE_HEADER4_FILE);

      try
      {
        dbReader.readData(year, month, dbReader.getFilename(year, month));
        dbReader.reset();
      }
      catch (IOException e)
      {
        logger.logData("NoaaMonthlyTable: constructor: Unable to read data: " + e.getLocalizedMessage());
        return;
      }

      DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
      formatter.applyPattern("00.00");

      // Monthly summary values.
      float totalAvgTemp = 0;
      float highTemp = 0;
      int dayOfHighTemp = 0;
      float lowTemp = 999;
      int dayOfLowTemp = 0;
      float totalHdd = 0;
      float totalCdd = 0;
      float totalRain = 0;
      int totalWindSpeed = 0;
      float highWindSpeed = 0;
      int dayOfHighWindSpeed = 0;
      int highDomDir = 0;
      WindDirection domDir = null;
      int highDaysAbove90 = 0;
      int highDaysBelow32 = 0;
      int lowDaysBelow32 = 0;
      int lowDaysBelow0 = 0;
      float maxRain = 0;
      int dayOfMaxRain = 0;
      int daysRainAbovePoint01 = 0;
      int daysRainAbovePoint1 = 0;
      int daysRainAbove1 = 0;

      // Loop through the month's data gathering day's data.
      int nextDay = 1;
      DataFileRecord nextRecord = dbReader.getNextRecord();
      while (nextRecord != null)
      {
        if (nextRecord instanceof DailySummary1Record)
        {
          DailySummary1Record record = (DailySummary1Record)nextRecord;

          nextRecord = dbReader.getNextRecord();
          DailySummary2Record record2 = (DailySummary2Record)nextRecord;

          // Update summary values.
          totalAvgTemp += record.getAvgOutTemp();

          if (record.getHiOutTemp() > highTemp)
          {
            highTemp = record.getHiOutTemp();
            dayOfHighTemp = nextDay;
          }

          if (record.getHiOutTemp() >= 90)
            highDaysAbove90++;
          else if (record.getHiOutTemp() <= 32)
            highDaysBelow32++;

          if (record.getLowOutTemp() < lowTemp)
          {
            lowTemp = record.getLowOutTemp();
            dayOfLowTemp = nextDay;
          }

          if (record.getLowOutTemp() <= 32)
            lowDaysBelow32++;
          if (record.getLowOutTemp() <= 0)
            lowDaysBelow0++;

          totalHdd += record2.getIntegratedHeatDD65();
          totalCdd += record2.getIntegratedCoolDD65();
          totalRain += record.getDailyRainTotal();
          totalWindSpeed += record.getAvgSpeed();

          if (record.getDailyRainTotal() > maxRain)
          {
            maxRain = record.getDailyRainTotal();
            dayOfMaxRain = nextDay;
          }

          if (record.getDailyRainTotal() > 0.01)
            daysRainAbovePoint01++;
          if (record.getDailyRainTotal() > 0.1)
            daysRainAbovePoint1++;
          if (record.getDailyRainTotal() > 1.0)
            daysRainAbove1++;

          if (record.getHiSpeed() > highWindSpeed)
          {
            highWindSpeed = record.getHiSpeed();
            dayOfHighWindSpeed = nextDay;
          }

          int[] directionInfo = WindBins.getDaysDominantDirectionInfo(record2);
          if (directionInfo[1] > highDomDir)
          {
            highDomDir = directionInfo[1];
            domDir = WindDirection.valueOf(directionInfo[0]);
          }

          // Format and display next line.
          StringBuilder nextLine = new StringBuilder();
          nextLine.append(padInteger(nextDay));
          nextLine.append("    ").append(formatter.format(record.getAvgOutTemp()));
          nextLine.append("    ").append(formatter.format(record.getHiOutTemp()));
          nextLine.append("    ").append(TimeUtil.toString(record.getTimeOfHighOutTemp()));
          nextLine.append("    ").append(formatter.format(record.getLowOutTemp()));
          nextLine.append("    ").append(TimeUtil.toString(record.getTimeOfLowOutTemp()));

          if (record2.getIntegratedHeatDD65() > 100)
            nextLine.append("   ");
          else
            nextLine.append("    ");
          nextLine.append(formatter.format(record2.getIntegratedHeatDD65()));

          if (record2.getIntegratedCoolDD65() > 100)
            nextLine.append("   ");
          else
            nextLine.append("    ");
          nextLine.append(formatter.format(record2.getIntegratedCoolDD65()));

          nextLine.append("    ").append(formatter.format(record.getDailyRainTotal()));
          nextLine.append("    ").append(formatter.format(record.getAvgSpeed()));
          nextLine.append("    ").append(formatter.format(record.getHiSpeed()));
          nextLine.append("    ").append(TimeUtil.toString(record.getTimeOfHighWindSpeed()));
          nextLine.append("    ").append(WindDirection.valueOf(directionInfo[0]));
          writer.println(nextLine);

          tableData[nextDay][0] = padInteger(nextDay);
          tableData[nextDay][1] = record.getAvgOutTemp();
          tableData[nextDay][2] = record.getHiOutTemp();
          tableData[nextDay][3] = TimeUtil.toString(record.getTimeOfHighOutTemp());
          tableData[nextDay][4] = record.getLowOutTemp();
          tableData[nextDay][5] = TimeUtil.toString(record.getTimeOfLowOutTemp());
          tableData[nextDay][6] = record2.getIntegratedHeatDD65();
          tableData[nextDay][7] = record2.getIntegratedCoolDD65();
          tableData[nextDay][8] = record.getDailyRainTotal();
          tableData[nextDay][9] = record.getAvgSpeed();
          tableData[nextDay][10] = record.getHiSpeed();
          tableData[nextDay][11] = TimeUtil.toString(record.getTimeOfHighWindSpeed());
          tableData[nextDay][12] = WindDirection.valueOf(directionInfo[0]);

          nextDay++;
        }
        nextRecord = dbReader.getNextRecord();
      }

      if (nextDay < daysInMonth)
      {
        for (int i = nextDay; i < daysInMonth + 1; i++)
        {
          String nextDayLine = padInteger(i);
          writer.println(nextDayLine);

          tableData[i][0] = nextDayLine;
        }
      }

      // The summary line time values represent the day on which the hi/low value occurred.
      StringBuilder summaryLine = new StringBuilder();
      summaryLine.append("      ").append(formatter.format(totalAvgTemp / daysInMonth));
      summaryLine.append("    ").append(formatter.format(highTemp));
      summaryLine.append("       ").append(dayOfHighTemp);
      summaryLine.append("    ").append(formatter.format(lowTemp));
      summaryLine.append("        ").append(dayOfLowTemp);

      if (totalHdd > 1000)
        summaryLine.append("  ");
      else if (totalHdd > 100)
        summaryLine.append("   ");
      else
        summaryLine.append("    ");
      summaryLine.append(formatter.format(totalHdd));

      if (totalCdd > 1000)
        summaryLine.append("  ");
      else if (totalCdd > 100)
        summaryLine.append("   ");
      else
        summaryLine.append("    ");
      summaryLine.append(formatter.format(totalCdd));

      summaryLine.append("    ").append(formatter.format(totalRain));
      summaryLine.append("    ").append(formatter.format(totalWindSpeed / daysInMonth));
      summaryLine.append("    ").append(formatter.format(highWindSpeed));
      summaryLine.append("       ").append(dayOfHighWindSpeed);
      summaryLine.append("    ").append(domDir).append("\n");
      writer.println(summaryLine);

      tableData[daysInMonth + 1][0] = "Totals:";
      tableData[daysInMonth + 1][1] = formatter.format(totalAvgTemp / daysInMonth);
      tableData[daysInMonth + 1][2] = highTemp;
      tableData[daysInMonth + 1][3] = dayOfHighTemp;
      tableData[daysInMonth + 1][4] = lowTemp;
      tableData[daysInMonth + 1][5] = dayOfLowTemp;
      tableData[daysInMonth + 1][6] = formatter.format(totalHdd);
      tableData[daysInMonth + 1][7] = formatter.format(totalCdd);
      tableData[daysInMonth + 1][8] = formatter.format(totalRain);
      tableData[daysInMonth + 1][9] = formatter.format(totalWindSpeed / daysInMonth);
      tableData[daysInMonth + 1][10] = highWindSpeed;
      tableData[daysInMonth + 1][11] = dayOfHighWindSpeed;
      tableData[daysInMonth + 1][12] = domDir;

      String max90Line = "    Max >= 90.0: " + highDaysAbove90;
      writer.println(max90Line);

      String max32Line = "    Max <= 32.0: " + highDaysBelow32;
      writer.println(max32Line);

      String min32Line = "    Min <= 32.0: " + lowDaysBelow32;
      writer.println(min32Line);

      String min0Line = "    Min <= 0.0: " + lowDaysBelow0 + "\n";
      writer.println(min0Line);

      String maxRainLine = "    Max Rain: " + maxRain + " on " + padInteger(month) + "/" + padInteger(dayOfMaxRain) +
        "/" + year;
      writer.println(maxRainLine);

      String rainDaysPoint01Line = "    Days of Rain > 0.01: " + daysRainAbovePoint01;
      writer.println(rainDaysPoint01Line);

      String rainDaysPoint1Line = "    Days of Rain > 0.1: " + daysRainAbovePoint1;
      writer.println(rainDaysPoint1Line);

      String rainDays1Line = "    Days of Rain > 1.0: " + daysRainAbove1 + "\n";
      writer.println(rainDays1Line);

      String degreeDayLine = "    Heat Base: 65.0  Cool Base: 65.0  Method: Integration \n";
      writer.println(degreeDayLine);

      writer.flush();
    }
    catch (FileNotFoundException | UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }

    DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames);
    JTable table = new JTable(tableModel);
    table.setPreferredScrollableViewportSize(new Dimension(500, 70));
    table.setGridColor(Color.LIGHT_GRAY);
    table.setShowHorizontalLines(true);
    table.setShowVerticalLines(true);
    ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    table.getColumnModel().getColumn(0).setPreferredWidth(44);

    // Center justify the columns.
    DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
    cellRenderer.setHorizontalAlignment(JLabel.CENTER);
    for (int i = 0; i < 13; i++)
    {
      table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
    }

    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);

    //Add the scroll pane to this panel.
    add(scrollPane);
  }

  private String padInteger(int value)
  {
    String valueString = "";
    if ((value > 0) && (value < 10))
    {
      valueString = "0" + value;
    }
    else
    {
      valueString = valueString + value;
    }
    return valueString;
  }

  /**
   * Create the GUI and show it. For thread safety,
   * this method should be invoked from the event-dispatching thread.
   */
  public static void createAndShowGUI(JPanel dataTable)
  {
    // Make sure we have nice window decorations.
    JFrame.setDefaultLookAndFeelDecorated(true);

    // Set up the content pane.
    dataTable.setOpaque(true);

    // content panes must be opaque
    FRAME.setContentPane(dataTable);

    // Display the window.
    FRAME.setSize(PROPS.getWindowWidth(), PROPS.getWindowHeight());
    FRAME.setVisible(true);
  }
}
