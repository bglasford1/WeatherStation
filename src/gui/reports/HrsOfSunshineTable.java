/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class displays the hours of sunshine table.

  Mods:		  09/01/21 Initial Release.
*/
package gui.reports;

import data.dbrecord.DailySummary1Record;
import data.dbrecord.DataFileRecord;
import data.dbrecord.WeatherRecord;
import dbif.DatabaseReader;
import util.ConfigProperties;
import util.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class HrsOfSunshineTable extends JPanel
{
  private static final JFrame FRAME = new JFrame("Hours of Sunshine Data Table");
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private final Logger logger = Logger.getInstance();

  private static final String[] columnNames =
    {"Date",
     "Hrs Sunlight"
    };

  private static final String FILENAME = "HrsOfSunshineReport.txt";
  private static final String HEADER1_FILE  = "\n    Hours Of Bright Sunshine Report";
  private static final String HEADER2_FILE  = "    -------------------------------\n";

  private static final DatabaseReader dbReader = DatabaseReader.getInstance();

  /**
   * Calculate the data and generate the reports, both the data for a JPanel and a txt file.
   *
   * @param startDate The date to start the reporting.
   * @param endDate   The date to end the reporting.
   * @param threshold The threshold of solar radiation, below which is not considered sunny.
   */
  public HrsOfSunshineTable(LocalDate startDate, LocalDate endDate, int threshold)
  {
    super(new GridLayout(1, 0));

    int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate);
    Object[][] tableData = new Object[daysBetween + 3][2];

    File file = new File("reports" + "/" + FILENAME);
    try (PrintWriter writer = new PrintWriter(file.getAbsoluteFile(), "UTF-8"))
    {
      // Write header info.
      writer.println(HEADER1_FILE);
      writer.println(HEADER2_FILE);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
      String inputDates = "From " + startDate.format(formatter) + " to " + endDate.format(formatter);
      writer.println(inputDates);

      String inputThreshold = "Threshold: " + threshold + "\n";
      writer.println(inputThreshold);

      // Read the first month's worth of data.
      int year = startDate.getYear();
      int month = startDate.getMonthValue();
      int nextDay = startDate.getDayOfMonth();

      try
      {
        dbReader.readData(year, month, dbReader.getFilename(year, month));
        dbReader.reset();
      }
      catch (IOException e)
      {
        logger.logData("HrsOfSunshineTable: constructor: Unable to read data: " + e.getLocalizedMessage());
        return;
      }

      // Loop through the months extracting data.
      float totalHourSunshine = 0;
      int totalDays = 0;
      boolean dayFound = false;
      DataFileRecord nextRecord = dbReader.getNextRecord();
      while (nextRecord != null)
      {
        if (nextDay == 1)
        {
          // Skip to the first weather record.
          dbReader.getNextRecord();
          nextRecord = dbReader.getNextRecord();
        }
        else
        {
          // Skip records looking for the first day.
          int day = 0;
          while (!dayFound && nextRecord != null)
          {
            if (nextRecord instanceof DailySummary1Record)
            {
              day++;
              if (day == nextDay)
              {
                dayFound = true;
                // Skip to the first weather record.
                dbReader.getNextRecord();
                nextRecord = dbReader.getNextRecord();
                break;
              }
            }
            nextRecord = dbReader.getNextRecord();
          }
        }

        float hoursOfSunshine = 0;
        while (nextRecord != null && nextRecord instanceof WeatherRecord)
        {
          WeatherRecord record = (WeatherRecord) nextRecord;
          short solarRadiation = record.getSolarRadiation();
          if (solarRadiation >= threshold)
          {
            // This solar rad measure represents 5 minutes, convert to hours.
            hoursOfSunshine += 5.0 / 60.0;
          }
          nextRecord = dbReader.getNextRecord();
        }

        String dayString = padInteger(month) + "/" + padInteger(nextDay) + "/" + year;
        String nextLine = "      " + dayString + "      " + String.format("%.2f", hoursOfSunshine);
        writer.println(nextLine);

        tableData[totalDays][0] = dayString;
        tableData[totalDays][1] = String.format("%.2f", hoursOfSunshine);

        totalHourSunshine += hoursOfSunshine;

        // Skip to the first weather record.
        dbReader.getNextRecord();
        nextRecord = dbReader.getNextRecord();
        nextDay++;
        totalDays++;

        // If end date is reached then stop processing.
        if (year == endDate.getYear() && month == endDate.getMonthValue() && nextDay == (endDate.getDayOfMonth() + 1))
        {
          break;
        }

        // If not at the end date then read the next months worth of data and keep going.
        if (nextRecord == null)
        {
          if (month == 12)
          {
            month = 1;
            year++;
          }
          else
          {
            month++;
          }
          nextDay = 1;

          if (year <= endDate.getYear() && month <= endDate.getMonthValue())
          {
            try
            {
              dbReader.readData(year, month, dbReader.getFilename(year, month));
              dbReader.reset();
            }
            catch (IOException e)
            {
              logger.logData("HrsOfSunshineTable: constructor: Unable to read data: " + e.getLocalizedMessage());
              return;
            }

            // Skip to the first weather record.
            dbReader.getNextRecord();
            dbReader.getNextRecord();
            nextRecord = dbReader.getNextRecord();
          }
        }
      }

      float avgHoursSunshine = totalHourSunshine / totalDays;
      String avgHoursString = "\nAverage Hours per Day = " + String.format("%.2f", avgHoursSunshine);
      writer.println(avgHoursString);
      writer.flush();

      tableData[totalDays + 1][0] = "Avg:";
      tableData[totalDays + 1][1] = String.format("%.2f", avgHoursSunshine);
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
    for (int i = 0; i < 2; i++)
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
    FRAME.setSize(300, PROPS.getWindowHeight());
    FRAME.setVisible(true);
  }
}
