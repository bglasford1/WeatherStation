/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class displays the sunrise/sunset table.

  Mods:		  09/01/21 Initial Release.
*/
package gui.reports;

import algorithms.SunMoonRiseSetTimes;
import util.ConfigProperties;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SunriseSunsetTable extends JPanel
{
  private static final JFrame FRAME = new JFrame("Sunrise/Sunset Data Table");
  private static final ConfigProperties PROPS = ConfigProperties.instance();

  private static final String[] columnNames =
    {"Date",
     "Astro",
     "Naut",
     "Civil",
     "Sunrise",
     "Sunset",
     "Civil",
     "Naut",
     "Astro",
     "Light Hrs"
    };

  private static final String FILENAME = "SunriseSunsetReport.txt";
  private static final String HEADER1_FILE  = "\n        Sunrise/Sunset Times Report";
  private static final String HEADER2_FILE  = "        ----------------------------\n";
  private static final String TABLE_HEADER1_FILE =
    "    Date       Astro   Naut    Civil  Sunrise  Sunset  Civil   Naut    Astro   Light Hrs";
  private static final String TABLE_HEADER2_FILE =
    "  ----------   -----   -----   -----   -----   -----   -----   -----   -----   -----";

  /**
   * Calculate the data and generate the reports, both the data for a JPanel and a txt file.
   *
   * @param startDate The date to start the reporting.
   * @param endDate   The date to end the reporting.
   */
  public SunriseSunsetTable(LocalDate startDate, LocalDate endDate)
  {
    super(new GridLayout(1, 0));

    int daysBetween = (int)ChronoUnit.DAYS.between(startDate, endDate);
    Object[][] tableData = new Object[daysBetween + 2][10];

    File file = new File("reports" + "/" + FILENAME);
    try (PrintWriter writer = new PrintWriter(file.getAbsoluteFile(), "UTF-8"))
    {
      // Write header info.
      writer.println(HEADER1_FILE);
      writer.println(HEADER2_FILE);
      writer.println(TABLE_HEADER1_FILE);
      writer.println(TABLE_HEADER2_FILE);

      // Loop for each date.
      int row = 0;
      boolean endDateReached = false;
      while (!endDateReached)
      {
        SunMoonRiseSetTimes riseSetTimes = new SunMoonRiseSetTimes();
        riseSetTimes.calculateTimes(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth());

        String dateString = padInteger(startDate.getMonthValue()) + "/" + padInteger(startDate.getDayOfMonth()) + "/" +
          startDate.getYear();

        String nextDataLine = "  " + dateString +
          "   " + riseSetTimes.getAstroDawnString(false) +
          "   " + riseSetTimes.getNauticalDawnString(false) +
          "   " + riseSetTimes.getCivilDawnString(false) +
          "   " + riseSetTimes.getSunriseString(false) +
          "   " + riseSetTimes.getSunsetString(false) +
          "   " + riseSetTimes.getCivilDuskString(false) +
          "   " + riseSetTimes.getNauticalDuskString(false) +
          "   " + riseSetTimes.getAstroDuskString(false) +
          "   " + String.format("%.02f", riseSetTimes.getDaylightHours());
        writer.println(nextDataLine);

        tableData[row][0] = dateString;
        tableData[row][1] = riseSetTimes.getAstroDawnString(false);
        tableData[row][2] = riseSetTimes.getNauticalDawnString(false);
        tableData[row][3] = riseSetTimes.getCivilDawnString(false);
        tableData[row][4] = riseSetTimes.getSunriseString(false);
        tableData[row][5] = riseSetTimes.getSunsetString(false);
        tableData[row][6] = riseSetTimes.getCivilDuskString(false);
        tableData[row][7] = riseSetTimes.getNauticalDuskString(false);
        tableData[row][8] = riseSetTimes.getAstroDuskString(false);
        tableData[row][9] = String.format("%.02f", riseSetTimes.getDaylightHours());

        // Increment to next day.
        startDate = startDate.plusDays(1);
        row++;
        if (startDate.isAfter(endDate))
          endDateReached = true;
      }
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

    // Center justify the columns.
    DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
    cellRenderer.setHorizontalAlignment(JLabel.CENTER);
    for (int i = 0; i < 10; i++)
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
