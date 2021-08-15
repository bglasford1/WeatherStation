/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class defines and maintains the snow data table which
            encapsulates a java table/table model and displays the table in
            a JPanel.  The data is presented and totaled by season which
            starts in July.

  Mods:		  09/01/21 Initial Release.
*/
package gui.snow;

import data.dbrecord.SnowRecord;
import dbif.DatabaseCommon;
import dbif.SnowDatabase;
import util.ConfigProperties;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnowDataTable extends JPanel
{
  private static final JFrame FRAME = new JFrame("Snow Data Table");
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private static final DecimalFormat df = new DecimalFormat("##0.00");
  private final SnowDatabase snowDatabase = SnowDatabase.getInstance();

  private static final String[] columnNames =
    {"Year",
     "July",
     "Aug",
     "Sep",
     "Oct",
     "Nov",
     "Dec",
     "Jan",
     "Feb",
     "March",
     "April",
     "May",
     "June",
     "Total"
    };

  /**
   * Constructor that creates the data table and creates the popup menu.
   */
  public SnowDataTable()
  {
    super(new GridLayout(1, 0));

    // Populate the datasets.
    HashMap<Long, Float> monthlyBins = getMonthlyData();

    int firstYear = 9999;
    int lastYear = 0;
    List<SnowRecord> snowRecords = snowDatabase.readData();
    for (SnowRecord nextRecord : snowRecords)
    {
      if (nextRecord.getYear() < firstYear)
        firstYear = nextRecord.getYear();
      if (nextRecord.getYear() > lastYear)
        lastYear = nextRecord.getYear();
    }

    int numberOfYearRows = lastYear - firstYear;
    for (SnowRecord nextRecord : snowRecords)
    {
      if (nextRecord.getYear() == lastYear && nextRecord.getMonth() > 6)
      {
        numberOfYearRows++;
        break;
      }
    }

    int numberOfRows = 6 + numberOfYearRows;
    Object[][] tableData = new Object[numberOfRows][14];

    // Prepopulate table data rows with "---"
    for (int x = 0; x < numberOfYearRows; x++)
      for (int y = 1; y < 13; y++)
        tableData[x][y] = DatabaseCommon.UNDEFINED_STRING_VALUE;

    float monthMins[] = {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999};
    float monthMaxs[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    float monthTotals[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    float monthMeasures[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    float yearMin = 999;
    float yearMax = 0;
    float avgTotal = 0;
    int avgMeasures = 0;

    // Fill in the cells with real values and the year totals.
    for (Map.Entry<Long, Float> entry : monthlyBins.entrySet())
    {
      Instant instant = Instant.ofEpochMilli(entry.getKey());
      OffsetDateTime dateGMT = OffsetDateTime.ofInstant(instant, ZoneOffset.ofHours(0));

      int originalMonth = dateGMT.getMonthValue();
      int month;
      int row;
      if (dateGMT.getMonthValue() > 6)
      {
        month = originalMonth - 6;
        row = dateGMT.getYear() - firstYear;
      }
      else
      {
        month = originalMonth + 6;
        row = dateGMT.getYear() - firstYear - 1;
      }

      String cellString = (String)tableData[row][month];
      float cellValue = 0;
      if (!cellString.equalsIgnoreCase(DatabaseCommon.UNDEFINED_STRING_VALUE))
      {
        cellValue = Float.valueOf(cellString);
      }
      cellValue += entry.getValue();

      tableData[row][month] =  cellValue;
    }

    // Now loop through the table totaling the rows.
    float rowTotal = 0;
    for (int x = 0; x < numberOfYearRows; x++)
    {
      // Fill in the year row.
      tableData[x][0] = (firstYear + x) + "-" + (firstYear + x + 1);

      for (int month = 1; month < 13; month++)
      {
        if (tableData[x][month] instanceof Float)
        {
          float cellValue = (Float)tableData[x][month];
          rowTotal += cellValue;

          // Fill in month min, max & total.
          if (cellValue < monthMins[month])
            monthMins[month] = cellValue;

          if (cellValue > monthMaxs[month])
            monthMaxs[month] = cellValue;

          monthTotals[month] = monthTotals[month] + cellValue;
          monthMeasures[month] += 1;
        }
      }

      // Add row total and reset to next row.
      if (rowTotal >= 10)
        tableData[x][13] = String.format("%6s", df.format(rowTotal));
      else
        tableData[x][13] = String.format("%7s", df.format(rowTotal));

      // Update the year totals.
      if (rowTotal < yearMin)
        yearMin = rowTotal;

      if (rowTotal > yearMax)
        yearMax = rowTotal;

      avgTotal = avgTotal + rowTotal;
      avgMeasures++;

      rowTotal = 0;
    }

    // Fill in Mins
    int rowCount = numberOfYearRows + 1;
    tableData[rowCount][0] = "Min";
    for (int i = 1; i < 13; i++)
    {
      if (monthMins[i] == 999)
      {
        monthMins[i] = 0;
      }
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
        if (tableData[j][i] instanceof Float)
        {
          numArray[arrayIndex] = (Float)tableData[j][i];
          arrayIndex++;
        }
        else if (tableData[j][i] instanceof String)
        {
          if (!((String)tableData[j][i]).equalsIgnoreCase(DatabaseCommon.UNDEFINED_STRING_VALUE))
          {
            numArray[arrayIndex] = Float.parseFloat((String)tableData[j][i]);
            arrayIndex++;
          }
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
    table.getColumnModel().getColumn(0).setPreferredWidth(100);

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

  /**
   * Internal method to get the monthly data amounts.
   *
   * @return The monthly values; date, amount.
   */
  private HashMap<Long, Float> getMonthlyData()
  {
    HashMap<Long, Float> returnBins = new HashMap<>();
    List<SnowRecord> snowRecords = snowDatabase.readData();

    for (SnowRecord nextRecord: snowRecords)
    {
      int year = nextRecord.getYear();
      int month = nextRecord.getMonth();
      LocalDateTime localMonthDate = LocalDateTime.of(year, month, 1, 0, 0);
      long monthDate = localMonthDate.atZone(ZoneId.of("America/Denver")).toInstant().toEpochMilli();
      Float monthValue = returnBins.get(monthDate);
      if (monthValue == null)
      {
        monthValue = nextRecord.getAmount();
        returnBins.put(monthDate, monthValue);
      }
      else
      {
        monthValue = monthValue + nextRecord.getAmount();
        returnBins.replace(monthDate, monthValue);
      }
    }
    return returnBins;
  }
}
