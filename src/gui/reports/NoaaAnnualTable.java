/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class displays the NOAA Annual table.

  Mods:		  09/01/21 Initial Release.
*/
package gui.reports;

import data.dbrecord.*;
import dbif.DatabaseReader;
import util.ConfigProperties;

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

public class NoaaAnnualTable extends JPanel
{
  private static final JFrame FRAME = new JFrame();
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private static final DatabaseReader dbReader = DatabaseReader.getInstance();
  private final DecimalFormat decimalFormat = new DecimalFormat();
  private final DecimalFormat twoDigitFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);

  // Yearly summary temperature values.
  private float totalYearMaxTemp = 0;
  private float totalYearMinTemp = 0;
  private float totalYearAvgTemp = 0;
  private float totalYearHdd = 0;
  private float totalYearCdd = 0;
  private float yearHighTemp = 0;
  private int monthOfYearHighTemp = 0;
  private float yearLowTemp = 999;
  private int monthOfYearLowTemp = 0;
  private int yearHighDaysAbove90 = 0;
  private int yearHighDaysBelow32 = 0;
  private int yearLowDaysBelow32 = 0;
  private int yearLowDaysBelow0 = 0;
  private int lastMonth = 0;

  // Yearly summary rain/wind values.
  private float totalYearMaxRain = 0;
  private float yearMaxRain = 0;
  private int monthOfYearMaxRain = 0;
  private int yearDaysOverPoint01 = 0;
  private int yearDaysOverPoint1 = 0;
  private int yearDaysOver1 = 0;
  private float totalYearAverageWindSpeed = 0;
  private float yearHighWindSpeed = 0;
  private int monthOfYearHighWindSpeed = 0;
  private int[] monthlyDirections = new int[16];

  private static final String[] temperatureColumnNames =
    {"Year",
     "Month",
     "Mean Max",
     "Mean Min",
     "Mean",
     "Heat Degree Days",
     "Cool Degree Days",
     "High",
     "Date",
     "Low",
     "Date",
     "Max >= 90",
     "Max <= 32",
     "Min <= 32",
     "Min <= 0"
    };

  private static final String[] rainWindColumnNames =
    {"Year",
     "Month",
     "Total Rain",
     "Max per Day",
     "Date",
     "Days > 0.01",
     "Days > 0.1",
     "Days > 1.0",
     "Avg Wind",
     "High Wind",
     "Date",
     "Dom. Dir."
    };

  private static final String HEADER1 = "\n               ANNUAL NOAA CLIMATOLOGY SUMMARY";
  private static final String HEADER_2 = "Name: Station1    City: Colorado Springs    State: Colorado\n\n";
  private static final String HEADER_4 = "          Temperature (" + "\u00b0" + "F),  Heat Base: 65.0,  Cool Base: 65.0\n";
  private static final String HEADER_5 = "\n\n                Precipitation (in)                              Wind Speed (mph)\n";
  private static final String TABLE_HEADER1_FILE =
    "                                       Heat       Cool";
  private static final String TABLE_HEADER2_FILE =
    "             Mean     Mean             Degree     Degree                                 Max    Max    Min    Min";
  private static final String TABLE_HEADER3_FILE =
    "Year  Mo     Max      Min     Mean     Days       Days        Hi     Date  Low   Date    >= 90  <= 32  <= 32  <= 0";
  private static final String TABLE_HEADER4_FILE =
    "------------------------------------------------------------------------------------------------------------------";
  private static final String TABLE_HEADER5_FILE =
    "                      Max           Days of Rain";
  private static final String TABLE_HEADER6_FILE =
    "            Total     per               Over                                        Dom";
  private static final String TABLE_HEADER7_FILE =
    "Year  Mo    Rain      Day   Date  0.01  0.1  1.0             Avg     High    Date   Dir";
  private static final String TABLE_HEADER8_FILE =
    "-------------------------------------------------           ----------------------------";

  /**
   * Calculate the data and generate the reports, both the data for a JPanel and a txt file.
   *
   * @param year The year to report.
   * @param filename The filename of the report.
   */
  public NoaaAnnualTable(int year, String filename)
  {
    super(new GridLayout(1, 0));
    FRAME.setTitle("NOAA Annual Data Table: " + year);

    decimalFormat.setMaximumFractionDigits(2);
    twoDigitFormatter.applyPattern("00.00");

    Object[][] temperatureTableData = new Object[15][15];
    Object[][] rainWindTableData = new Object[15][12];

    for (int i = 0; i < 16; i++)
      monthlyDirections[i] = 0;

    // Create the txt file and fill in the table data.
    PrintWriter temperatureWriter;
    PrintWriter rainWindWriter;
    try
    {
      File temperatureFile = new File("reports/" + filename);
      temperatureWriter = new PrintWriter(temperatureFile.getAbsoluteFile(), "UTF-8");

      File tempFile = new File("reports/temp.txt");
      rainWindWriter = new PrintWriter(tempFile.getAbsoluteFile(), "UTF-8");

      writeTemperatureTableHeaderInfo(temperatureWriter);
      writeRainWindTableHeaderInfo(rainWindWriter);

      int nextMonth;
      for (nextMonth = 1; nextMonth < 13; nextMonth++)
      {
        if (dbReader.fileExists(year, nextMonth))
        {
          dbReader.readSummaryData(year, nextMonth);

          lastMonth = nextMonth;

          createTemperatureTable(temperatureWriter, year, temperatureTableData, nextMonth);
          dbReader.resetSummary();
          createRainWindTable(rainWindWriter, year, rainWindTableData, nextMonth);
        }
      else
        {
          String nextDayLine = padInteger(year) + "  " + padInteger(nextMonth);
          temperatureWriter.println(nextDayLine);
          rainWindWriter.println(nextDayLine);

          temperatureTableData[nextMonth][0] = year;
          temperatureTableData[nextMonth][1] = padInteger(nextMonth);

          rainWindTableData[nextMonth][0] = year;
          rainWindTableData[nextMonth][1] = padInteger(nextMonth);
        }
      }

      writeTemperatureTableSummaryInfo(temperatureWriter, temperatureTableData);
      writeRainWindTableSummaryInfo(rainWindWriter, rainWindTableData);

      // Append the temp file (rain/wind info) to the real file.
      rainWindWriter.close();
      BufferedReader reader = new BufferedReader(new FileReader(tempFile));
      String line;
      while ((line = reader.readLine()) != null)
      {
        temperatureWriter.println(line);
      }
      temperatureWriter.close();
      rainWindWriter.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    // Create the tables to display.
    JTabbedPane tabbedPane = new JTabbedPane();

    DefaultTableModel temperatureTableModel = new DefaultTableModel(temperatureTableData, temperatureColumnNames);
    JTable temperatureTable = new JTable(temperatureTableModel);
    temperatureTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
    temperatureTable.setGridColor(Color.LIGHT_GRAY);
    temperatureTable.setShowHorizontalLines(true);
    temperatureTable.setShowVerticalLines(true);
    ((DefaultTableCellRenderer)temperatureTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    temperatureTable.getColumnModel().getColumn(0).setPreferredWidth(44);
    temperatureTable.getColumnModel().getColumn(1).setPreferredWidth(30);
    temperatureTable.getColumnModel().getColumn(8).setPreferredWidth(33);
    temperatureTable.getColumnModel().getColumn(10).setPreferredWidth(33);

    // Center justify the columns.
    DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
    cellRenderer.setHorizontalAlignment(JLabel.CENTER);
    for (int i = 2; i < 15; i++)
    {
      temperatureTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
    }

    JScrollPane temperatureScrollPane = new JScrollPane(temperatureTable);
    tabbedPane.addTab("Temperature", null, temperatureScrollPane, "");

    DefaultTableModel rainWindTableModel = new DefaultTableModel(rainWindTableData, rainWindColumnNames);
    JTable rainWindTable = new JTable(rainWindTableModel);
    rainWindTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
    rainWindTable.setGridColor(Color.LIGHT_GRAY);
    rainWindTable.setShowHorizontalLines(true);
    rainWindTable.setShowVerticalLines(true);
    ((DefaultTableCellRenderer)rainWindTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    rainWindTable.getColumnModel().getColumn(0).setPreferredWidth(44);
    rainWindTable.getColumnModel().getColumn(1).setPreferredWidth(36);

    // Right justify the columns.
    for (int i = 2; i < 12; i++)
    {
      rainWindTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
    }

    JScrollPane rainWindScrollPane = new JScrollPane(rainWindTable);
    tabbedPane.addTab("Rain/Wind", null, rainWindScrollPane, "");

    add(tabbedPane);
  }

  /**
   * Write the temperature header into to the file.
   *
   * @param writer The print writer.
   */
  private void writeTemperatureTableHeaderInfo(PrintWriter writer)
  {
    // Write header info.
    writer.println(HEADER1);
    writer.println(HEADER_2);

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

    writer.println(HEADER_4);

    writer.println(TABLE_HEADER1_FILE);
    writer.println(TABLE_HEADER2_FILE);
    writer.println(TABLE_HEADER3_FILE);
    writer.println(TABLE_HEADER4_FILE);
  }

  /**
   * Internal method to create the temperature summary table and file.
   *
   * @param writer The file to write the data.
   * @param year The year of the data.
   * @param tableData The table to write the data.
   */
  private void createTemperatureTable(PrintWriter writer, int year, Object[][] tableData, int nextMonth)
  {
    // Monthly values.
    float totalMaxTemp = 0;
    float totalMinTemp = 0;
    float totalAvgTemp = 0;
    float totalCdd = 0;
    float totalHdd = 0;
    float highTemp = 0;
    int dayOfHighTemp = 0;
    float lowTemp = 999;
    int dayOfLowTemp = 0;
    int highDaysAbove90 = 0;
    int highDaysBelow32 = 0;
    int lowDaysBelow32 = 0;
    int lowDaysBelow0 = 0;

    // Loop through the month's data gathering day's data.
    int nextDay = 1;
    DataFileRecord nextRecord = dbReader.getNextSummaryRecord();
    while (nextRecord != null)
    {
      if (nextRecord instanceof DailySummary1Record)
      {
        DailySummary1Record record = (DailySummary1Record)nextRecord;

        nextRecord = dbReader.getNextSummaryRecord();
        DailySummary2Record record2 = (DailySummary2Record)nextRecord;

        // Update summary values.
        totalMaxTemp += record.getHiOutTemp();
        totalMinTemp += record.getLowOutTemp();
        totalAvgTemp += record.getAvgOutTemp();
        totalHdd += record2.getIntegratedHeatDD65();
        totalCdd += record2.getIntegratedCoolDD65();

        if (record.getHiOutTemp() > highTemp)
        {
          highTemp = record.getHiOutTemp();
          dayOfHighTemp = nextDay;
        }

        if (record.getLowOutTemp() < lowTemp)
        {
          lowTemp = record.getLowOutTemp();
          dayOfLowTemp = nextDay;
        }

        if (record.getHiOutTemp() >= 90f)
          highDaysAbove90++;
        else if (record.getHiOutTemp() <= 32f)
          highDaysBelow32++;

        if (record.getLowOutTemp() < lowTemp)
        {
          lowTemp = record.getLowOutTemp();
          dayOfLowTemp = nextDay;
        }

        if (record.getLowOutTemp() <= 32f)
          lowDaysBelow32++;
        if (record.getLowOutTemp() <= 0f)
          lowDaysBelow0++;

        nextDay++;
      }
      nextRecord = dbReader.getNextSummaryRecord();
    }
    int daysInMonth = nextDay - 1;

    // Update yearly summary values.
    totalYearMaxTemp += totalMaxTemp / daysInMonth;
    totalYearMinTemp += totalMinTemp / daysInMonth;
    totalYearAvgTemp += totalAvgTemp / daysInMonth;
    totalYearHdd += totalHdd;
    totalYearCdd += totalCdd;

    if (highTemp > yearHighTemp)
    {
      yearHighTemp = highTemp;
      monthOfYearHighTemp = nextMonth;
    }

    if (lowTemp < yearLowTemp)
    {
      yearLowTemp = lowTemp;
      monthOfYearLowTemp = nextMonth;
    }
    yearHighDaysAbove90 += highDaysAbove90;
    yearHighDaysBelow32 += highDaysBelow32;
    yearLowDaysBelow32 += lowDaysBelow32;
    yearLowDaysBelow0 += lowDaysBelow0;

    // Format and display next line.
    StringBuilder nextLine = new StringBuilder();
    nextLine.append(year).append("  ").append(padInteger(nextMonth));
    nextLine.append("    ").append(twoDigitFormatter.format(totalMaxTemp / daysInMonth));
    nextLine.append("    ").append(twoDigitFormatter.format(totalMinTemp / daysInMonth));
    nextLine.append("    ").append(twoDigitFormatter.format(totalAvgTemp / daysInMonth));

    if (totalHdd == 0)
    {
      nextLine.append("          ").append(decimalFormat.format(totalHdd));
    }
    else if (totalHdd < 10)
    {
      nextLine.append("        ").append(decimalFormat.format(totalHdd));
    }
    else if (totalHdd < 100)
    {
      nextLine.append("       ").append(decimalFormat.format(totalHdd));
    }
    else if (totalHdd < 1000)
    {
      nextLine.append("      ").append(decimalFormat.format(totalHdd));
    }
    else
    {
      nextLine.append("    ").append(decimalFormat.format(totalHdd));
    }

    if (totalCdd == 0)
    {
      nextLine.append("         ").append(decimalFormat.format(totalCdd));
    }
    else if (totalCdd < 10)
    {
      nextLine.append("       ").append(decimalFormat.format(totalCdd));
    }
    else if (totalCdd < 100)
    {
      nextLine.append("      ").append(decimalFormat.format(totalCdd));
    }
    else if (totalCdd < 1000)
    {
      nextLine.append("     ").append(decimalFormat.format(totalCdd));
    }
    else
    {
      nextLine.append("   ").append(decimalFormat.format(totalCdd));
    }
    nextLine.append("    ").append(twoDigitFormatter.format(highTemp));

    if (dayOfHighTemp > 10)
      nextLine.append("   ");
    else
      nextLine.append("    ");
    nextLine.append(dayOfHighTemp);

    if (lowTemp < 0)
    {
      nextLine.append("   ").append(twoDigitFormatter.format(lowTemp));
    }
    else
    {
      nextLine.append("    ").append(twoDigitFormatter.format(lowTemp));
    }

    if (dayOfLowTemp > 10)
      nextLine.append("   ");
    else
      nextLine.append("    ");
    nextLine.append(dayOfLowTemp);

    if (highDaysAbove90 > 10)
      nextLine.append("     ");
    else
      nextLine.append("      ");
    nextLine.append(highDaysAbove90);

    if (highDaysBelow32 > 10)
      nextLine.append("     ");
    else
      nextLine.append("      ");
    nextLine.append(highDaysBelow32);

    if (lowDaysBelow32 > 10)
      nextLine.append("     ");
    else
      nextLine.append("      ");
    nextLine.append(lowDaysBelow32);

    if (lowDaysBelow0 > 10)
      nextLine.append("     ");
    else
      nextLine.append("      ");
    nextLine.append(lowDaysBelow0);
    writer.println(nextLine);

    tableData[nextMonth][0] = year;
    tableData[nextMonth][1] = padInteger(nextMonth);
    tableData[nextMonth][2] = twoDigitFormatter.format(totalMaxTemp / daysInMonth);
    tableData[nextMonth][3] = twoDigitFormatter.format(totalMinTemp / daysInMonth);
    tableData[nextMonth][4] = twoDigitFormatter.format(totalAvgTemp / daysInMonth);
    tableData[nextMonth][5] = decimalFormat.format(totalHdd);
    tableData[nextMonth][6] = decimalFormat.format(totalCdd);
    tableData[nextMonth][7] = twoDigitFormatter.format(highTemp);
    tableData[nextMonth][8] = dayOfHighTemp;
    tableData[nextMonth][9] = twoDigitFormatter.format(lowTemp);
    tableData[nextMonth][10] = dayOfLowTemp;
    tableData[nextMonth][11] = highDaysAbove90;
    tableData[nextMonth][12] = highDaysBelow32;
    tableData[nextMonth][13] = lowDaysBelow32;
    tableData[nextMonth][14] = lowDaysBelow0;
  }

  /**
   * Write the temperature summary info to both the file and the table.
   *
   * @param writer The print writer.
   * @param tableData The table data.
   */
  private void writeTemperatureTableSummaryInfo(PrintWriter writer, Object[][] tableData)
  {
    writer.println(TABLE_HEADER4_FILE);

    // The summary line time values represent the day on which the hi/low value occurred.
    String monthOfYearHighString = new DateFormatSymbols().getMonths()[monthOfYearHighTemp - 1].substring(0, 3);
    String monthOfYearLowString = new DateFormatSymbols().getMonths()[monthOfYearLowTemp - 1].substring(0, 3);

    StringBuilder summaryLine = new StringBuilder();
    summaryLine.append("Totals:     ").append(twoDigitFormatter.format(totalYearMaxTemp / lastMonth));
    summaryLine.append("    ").append(twoDigitFormatter.format(totalYearMinTemp / lastMonth));
    summaryLine.append("    ").append(twoDigitFormatter.format(totalYearAvgTemp / lastMonth));
    summaryLine.append("    ").append(decimalFormat.format(totalYearHdd));
    summaryLine.append("    ").append(decimalFormat.format(totalYearCdd));
    summaryLine.append("    ").append(twoDigitFormatter.format(yearHighTemp));
    summaryLine.append("    ").append(monthOfYearHighString);
    summaryLine.append("    ").append(twoDigitFormatter.format(yearLowTemp));
    summaryLine.append("   ").append(monthOfYearLowString);
    summaryLine.append("     ").append(yearHighDaysAbove90);
    summaryLine.append("     ").append(yearHighDaysBelow32);
    summaryLine.append("     ").append(yearLowDaysBelow32);
    summaryLine.append("     ").append(yearLowDaysBelow0).append("\n");
    writer.println(summaryLine);

    tableData[14][0] = "Totals:";
    tableData[14][2] = twoDigitFormatter.format(totalYearMaxTemp / lastMonth);
    tableData[14][3] = twoDigitFormatter.format(totalYearMinTemp / lastMonth);
    tableData[14][4] = twoDigitFormatter.format(totalYearAvgTemp / lastMonth);
    tableData[14][5] = decimalFormat.format(totalYearHdd);
    tableData[14][6] = decimalFormat.format(totalYearCdd);
    tableData[14][7] = twoDigitFormatter.format(yearHighTemp);
    tableData[14][8] = monthOfYearHighString;
    tableData[14][9] = twoDigitFormatter.format(yearLowTemp);
    tableData[14][10] = monthOfYearLowString;
    tableData[14][11] = yearHighDaysAbove90;
    tableData[14][12] = yearHighDaysBelow32;
    tableData[14][13] = yearLowDaysBelow32;
    tableData[14][14] = yearLowDaysBelow0;

    writer.flush();
  }

  /**
   * Write the rain/wind table header info which is the file based header info.
   *
   * @param writer The print writer.
   */
  private void writeRainWindTableHeaderInfo(PrintWriter writer)
  {
    // Write header info.
    writer.println(HEADER_5);
    writer.println(TABLE_HEADER5_FILE);
    writer.println(TABLE_HEADER6_FILE);
    writer.println(TABLE_HEADER7_FILE);
    writer.println(TABLE_HEADER8_FILE);
  }

  /**
   * Internal method to create the rain/wind table and summary report.
   *
   * @param writer The file to write the summary report info.
   * @param year The year being summarized.
   * @param tableData The table to write the data.
   */
  private void createRainWindTable(PrintWriter writer, int year, Object[][] tableData, int nextMonth)
  {
    // Monthly values.
    float totalRain = 0;
    float maxRain = 0;
    int maxRainDay = 0;
    int daysOverPoint01 = 0;
    int daysOverPoint1 = 0;
    int daysOver1 = 0;
    float totalWindSpeed = 0;
    float highWindSpeed = 0;
    int highWindDay = 0;
    int highDomDir = 0;
    WindDirection domDir = null;

    // Loop through the month's data gathering day's data.
    int nextDay = 1;
    DataFileRecord nextRecord = dbReader.getNextSummaryRecord();
    while (nextRecord != null)
    {
      if (nextRecord instanceof DailySummary1Record)
      {
        DailySummary1Record record = (DailySummary1Record)nextRecord;

        nextRecord = dbReader.getNextSummaryRecord();
        DailySummary2Record record2 = (DailySummary2Record)nextRecord;

        // Update summary values.
        float dailyRainTotal = record.getDailyRainTotal();
        totalRain += dailyRainTotal;

        if (dailyRainTotal > maxRain)
        {
          maxRain = dailyRainTotal;
          maxRainDay = nextDay;
        }

        if (dailyRainTotal > 0.01f)
        {
          daysOverPoint01++;
        }
        if (dailyRainTotal > 0.1f)
        {
          daysOverPoint1++;
        }
        if (dailyRainTotal > 1.0f)
        {
          daysOver1++;
        }

        totalWindSpeed += record.getAvgSpeed();

        if (record.getHiSpeed() > highWindSpeed)
        {
          highWindSpeed = record.getHiSpeed();
          highWindDay = nextDay;
        }

        int[] directionInfo = WindBins.getDaysDominantDirectionInfo(record2);
        if (directionInfo[1] > highDomDir)
        {
          highDomDir = directionInfo[1];
          domDir = WindDirection.valueOf(directionInfo[0]);
        }
        nextDay++;
      }
      nextRecord = dbReader.getNextSummaryRecord();
    }

    int daysInMonth = nextDay - 1;

    // Update yearly summary values.
    totalYearMaxRain += totalRain;

    if (maxRain > yearMaxRain)
    {
      yearMaxRain = maxRain;
      monthOfYearMaxRain = nextMonth;
    }

    yearDaysOverPoint01 += daysOverPoint01;
    yearDaysOverPoint1 += daysOverPoint1;
    yearDaysOver1 += daysOver1;

    totalYearAverageWindSpeed += totalWindSpeed / daysInMonth;

    if (highWindSpeed > yearHighWindSpeed)
    {
      yearHighWindSpeed = highWindSpeed;
      monthOfYearHighWindSpeed = nextMonth;
    }

    if (domDir != null)
    {
      monthlyDirections[domDir.value()]++;
    }

    // Format and display next line.
    StringBuilder nextLine = new StringBuilder();
    nextLine.append(year).append("  ").append(padInteger(nextMonth));
    nextLine.append("    ").append(twoDigitFormatter.format(totalRain));
    nextLine.append("    ").append(twoDigitFormatter.format(maxRain));
    if (maxRainDay < 10)
    {
      nextLine.append("     ").append(maxRainDay);
    }
    else
    {
      nextLine.append("    ").append(maxRainDay);
    }
    nextLine.append("    ").append(daysOverPoint01);
    nextLine.append("     ").append(daysOverPoint1);
    nextLine.append("    ").append(daysOver1);
    nextLine.append("              ").append(twoDigitFormatter.format(totalWindSpeed / daysInMonth));
    nextLine.append("    ").append(twoDigitFormatter.format(highWindSpeed));
    if (highWindDay < 10)
    {
      nextLine.append("     ").append(highWindDay);
    }
    else
    {
      nextLine.append("    ").append(highWindDay);
    }
    nextLine.append("     ").append(domDir);
    writer.println(nextLine);

    tableData[nextMonth][0] = year;
    tableData[nextMonth][1] = padInteger(nextMonth);
    tableData[nextMonth][2] = twoDigitFormatter.format(totalRain);
    tableData[nextMonth][3] = twoDigitFormatter.format(maxRain);
    tableData[nextMonth][4] = maxRainDay;
    tableData[nextMonth][5] = daysOverPoint01;
    tableData[nextMonth][6] = daysOverPoint1;
    tableData[nextMonth][7] = daysOver1;
    tableData[nextMonth][8] = twoDigitFormatter.format(totalWindSpeed / daysInMonth);
    tableData[nextMonth][9] = twoDigitFormatter.format(highWindSpeed);
    tableData[nextMonth][10] = highWindDay;
    tableData[nextMonth][11] = domDir;
  }

  /**
   * Write the rain/wind summary info to the file and the table.  This data is the totals.
   *
   * @param writer The print writer.
   * @param tableData The table data.
   */
  private void writeRainWindTableSummaryInfo(PrintWriter writer, Object[][] tableData)
  {
    int highYearWindDir = 0;
    for (int i = 0; i < 16; i++)
    {
      if (monthlyDirections[i] > highYearWindDir)
        highYearWindDir = i;
    }
    WindDirection domYearDir = WindDirection.valueOf(highYearWindDir);

    writer.println(TABLE_HEADER8_FILE);

    // The summary line time values represent the day on which the hi/low value occurred.
    String monthOfYearHighRainString = null;
    if (monthOfYearMaxRain != 0)
    {
      monthOfYearHighRainString = new DateFormatSymbols().getMonths()[monthOfYearMaxRain - 1].substring(0, 3);
    }
    String monthOfYearHighWindString = null;
    if (monthOfYearHighWindSpeed != 0)
    {
      monthOfYearHighWindString = new DateFormatSymbols().getMonths()[monthOfYearHighWindSpeed - 1].substring(0, 3);
    }

    StringBuilder summaryLine = new StringBuilder();
    summaryLine.append("Totals:     ").append(twoDigitFormatter.format(totalYearMaxRain));
    summaryLine.append("    ").append(twoDigitFormatter.format(yearMaxRain));
    summaryLine.append("    ").append(monthOfYearHighRainString);
    summaryLine.append("    ").append(yearDaysOverPoint01);
    summaryLine.append("     ").append(yearDaysOverPoint1);
    summaryLine.append("    ").append(yearDaysOver1);
    summaryLine.append("            ").append(twoDigitFormatter.format(totalYearAverageWindSpeed / lastMonth));
    summaryLine.append("    ").append(twoDigitFormatter.format(yearHighWindSpeed));
    summaryLine.append("    ").append(monthOfYearHighWindString);
    summaryLine.append("    ").append(domYearDir).append("\n");
    writer.println(summaryLine);

    tableData[14][0] = "Totals:";
    tableData[14][2] = twoDigitFormatter.format(totalYearMaxRain);
    tableData[14][3] = twoDigitFormatter.format(yearMaxRain);
    tableData[14][4] = monthOfYearHighRainString;
    tableData[14][5] = yearDaysOverPoint01;
    tableData[14][6] = yearDaysOverPoint1;
    tableData[14][7] = yearDaysOver1;
    tableData[14][8] = twoDigitFormatter.format(totalYearAverageWindSpeed / lastMonth);
    tableData[14][9] = twoDigitFormatter.format(yearHighWindSpeed);
    tableData[14][10] = monthOfYearHighWindString;
    tableData[14][11] = domYearDir;

    writer.flush();
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
