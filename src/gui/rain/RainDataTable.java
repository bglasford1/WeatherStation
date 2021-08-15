/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class builds and displays the rain data table.

  Mods:		  09/01/21 Initial Release.
*/
package gui.rain;

import dbif.DatabaseCommon;
import dbif.DatabaseReader;
import util.ConfigProperties;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class defines and maintains the rain data table which encapsulates a java table/table model and displays the
 * table in a JPanel.
 */
public class RainDataTable extends JPanel
{
  private static final JFrame FRAME = new JFrame("Rain Data Table");
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private static final DecimalFormat df = new DecimalFormat("##0.00");

  private static final String[] columnNames =
    {"Year",
     "Jan",
     "Feb",
     "March",
     "April",
     "May",
     "June",
     "July",
     "Aug",
     "Sep",
     "Oct",
     "Nov",
     "Dec",
     "Total"
    };

  /**
   * Constructor that creates the data table and creates the popup menu.
   */
  public RainDataTable()
  {
    super(new GridLayout(1, 0));

    // Populate the datasets.
    DatabaseReader.getInstance().readRainData();
    HashMap<Long, Float> monthlyBins = DatabaseReader.getInstance().getMonthlyRainData();

    // Sort the keys
    Map<Long, Float> sortedBins =
      monthlyBins.entrySet().stream().sorted(Map.Entry.comparingByKey())
                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                           (oldValue, newValue) -> oldValue, LinkedHashMap::new));

    // TODO: the number of years should be based on first and last year values and not # of records
    int numberOfYearRows = (monthlyBins.size() / 12) + 2;
    int numberOfRows = 6 + numberOfYearRows;
    Object[][] tableData = new Object[numberOfRows][15];
    int rowCount = 0;
    int columnCount = 0;
    float rowTotal = 0;
    float monthMins[] = {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999};
    float monthMaxs[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    float monthTotals[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    float monthMeasures[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    float yearMin = 999;
    float yearMax = 0;
    float avgTotal = 0;
    int avgMeasures = 0;
    boolean missingData = false;
    int lastMonth = 0;

    // Fill in the cells with real values and the year totals.
    for (Map.Entry<Long, Float> entry : sortedBins.entrySet())
    {
      Instant instant = Instant.ofEpochMilli(entry.getKey());
      OffsetDateTime dateGMT = OffsetDateTime.ofInstant(instant, ZoneOffset.ofHours(0));

      int month = dateGMT.getMonthValue();
      lastMonth = month;
      if (columnCount == 0)
      {
        tableData[rowCount][0] = dateGMT.getYear();
        columnCount++;
        for (int i = 1; i < month; i++)
        {
          missingData = true;
          tableData[rowCount][i] = DatabaseCommon.UNDEFINED_STRING_VALUE;
          columnCount++;
        }
      }

      while (columnCount != month && columnCount < 14)
      {
        missingData = true;
        tableData[rowCount][columnCount] = DatabaseCommon.UNDEFINED_STRING_VALUE;
        columnCount++;
      }

      // Fill in actual month value.
      float value = entry.getValue();
      tableData[rowCount][month] = df.format(value);
      columnCount++;
      rowTotal = rowTotal + value;

      // Fill in month min, max & total.
      if (value < monthMins[month])
        monthMins[month] = value;

      if (value > monthMaxs[month])
        monthMaxs[month] = value;

      monthTotals[month] = monthTotals[month] + value;
      monthMeasures[month] += 1;

      if (month == 12)
      {
        // Fill in the year min, max & total, but not for first and last years or years with missing data.
        if (missingData)
        {
          missingData = false;
        }
        else
        {
          if (rowTotal < yearMin)
            yearMin = rowTotal;

          if (rowTotal > yearMax)
            yearMax = rowTotal;

          avgTotal = avgTotal + rowTotal;
          avgMeasures++;
        }

        // add row total and reset to next row.
        if (rowTotal >= 10)
          tableData[rowCount][columnCount] = String.format("%6s", df.format(rowTotal));
        else
          tableData[rowCount][columnCount] = String.format("%7s", df.format(rowTotal));

        rowCount++;
        columnCount = 0;
        rowTotal = 0;
      }
    }

    // Avoid overflow if last month = 12.
    if (lastMonth == 12)
    {
      rowCount--;
      columnCount = 13;
    }

    // Fill in the remaining cells with "---"
    for (int i = columnCount; i < 13; i++)
    {
      tableData[rowCount][i] = DatabaseCommon.UNDEFINED_STRING_VALUE;
    }
    if (rowTotal >= 10)
      tableData[rowCount][13] = String.format("%6s", df.format(rowTotal));
    else
      tableData[rowCount][13] = String.format("%7s", df.format(rowTotal));

    // Fill in Mins
    rowCount = rowCount + 2;
    tableData[rowCount][0] = "Min";
    for (int i = 1; i < 13; i++)
    {
      tableData[rowCount][i] = df.format(monthMins[i]);
    }
    if (yearMin >= 10)
      tableData[rowCount][13] = String.format("%6s", df.format(yearMin));
    else
      tableData[rowCount][13] = String.format("%7s", df.format(yearMin));

    // Fill in Maxs
    rowCount++;
    tableData[rowCount][0] = "Max";
    for (int i = 1; i < 13; i++)
    {
      tableData[rowCount][i] = df.format(monthMaxs[i]);
    }
    if (yearMax >= 10)
      tableData[rowCount][13] = String.format("%6s", df.format(yearMax));
    else
      tableData[rowCount][13] = String.format("%7s", df.format(yearMax));

    // Fill in Averages.
    rowCount++;
    tableData[rowCount][0] = "Avg";

    for (int i = 1; i < 13; i++)
    {
      float monthAvg = 0;
      if (monthTotals[i] > 0)
      {
        monthAvg = monthTotals[i] / monthMeasures[i];
      }
      tableData[rowCount][i] = df.format(monthAvg);
    }

    float average = 0;
    if (avgTotal > 0)
    {
      average = avgTotal / avgMeasures;
    }
    if (avgTotal >= 10)
      tableData[rowCount][13] = String.format("%6s", df.format(average));
    else
      tableData[rowCount][13] = String.format("%7s", df.format(average));

    // Fill in the median values.
    rowCount++;
    tableData[rowCount][0] = "Median";

    for (int i = 1; i < 14; i++)
    {
      float[] numArray = new float[numberOfYearRows];
      int arrayIndex = 0;

      for (int j = 0; j < numberOfYearRows; j++)
      {
        if (!((String)tableData[j][i]).equalsIgnoreCase(DatabaseCommon.UNDEFINED_STRING_VALUE))
        {
          numArray[arrayIndex] = Float.parseFloat((String)tableData[j][i]);
          arrayIndex++;
        }
      }

      float[] newArray = new float[arrayIndex];
      System.arraycopy(numArray, 0, newArray, 0, arrayIndex);
      Arrays.sort(newArray);
      double median;
      if (arrayIndex == 0)
        median = 0;
      else if (arrayIndex % 2 == 0)
        median = ((double)newArray[arrayIndex/2] + (double)newArray[arrayIndex/2 - 1])/2;
      else
        median = (double) newArray[arrayIndex/2];

      tableData[rowCount][i] = df.format(median);
    }

    // Create the table.
    DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames);
    JTable table = new JTable(tableModel);
    table.setPreferredScrollableViewportSize(new Dimension(500, 70));

    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);

    //Add the scroll pane to this panel.
    add(scrollPane);
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
