/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class charts the rain data.

  Mods:		  09/01/21 Initial Release.
*/
package gui.rain;

import dbif.DatabaseReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.List;

public class RainDataThread
{
  private ChartPanel chartPanel;

  private final DatabaseReader databaseReader = DatabaseReader.getInstance();
  private DefaultCategoryDataset stackedDataset = new DefaultCategoryDataset();
  private DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

  // Data bins: monthly and yearly.
  private final HashMap<String, Float> yearlyBins = new HashMap<>();  // time index is by season with a value of January 1st.
  private final HashMap<String, Float> monthlyBins = new HashMap<>(); // time index is by month with a value of the 1st.

  private static class SingletonHelper
  {
    private static final RainDataThread INSTANCE = new RainDataThread();
  }

  public static RainDataThread getInstance()
  {
    return RainDataThread.SingletonHelper.INSTANCE;
  }

  private RainDataThread() { }

  public ChartPanel getChart()
  {
    return chartPanel;
  }

  /**
   * Method to initialize this singleton.  This method must be called before any other methods are called.
   */
  public void initDataStructures()
  {
    populateDatasets();

    JFreeChart barChart = ChartFactory.createStackedBarChart
      (null,
       "Year",
       "Amount",
       stackedDataset,
        PlotOrientation.VERTICAL,
       true, true, false);

    CategoryPlot plot = barChart.getCategoryPlot();
    plot.setRangeGridlinePaint(Color.BLACK);

    BarRenderer barRenderer = (BarRenderer)plot.getRenderer();
    barRenderer.setBarPainter(new StandardBarPainter());
    barRenderer.setSeriesPaint(0, Color.green); // January
    barRenderer.setSeriesPaint(1, Color.gray);
    barRenderer.setSeriesPaint(2, Color.cyan);
    barRenderer.setSeriesPaint(3, Color.orange);
    barRenderer.setSeriesPaint(4, new Color(202, 88, 0)); // Brown
    barRenderer.setSeriesPaint(5, new Color(0, 128, 128)); // Teal
    barRenderer.setSeriesPaint(6, Color.blue); // July
    barRenderer.setSeriesPaint(7, Color.yellow);
    barRenderer.setSeriesPaint(8, Color.red);
    barRenderer.setSeriesPaint(9, Color.black);
    barRenderer.setSeriesPaint(10, new Color(128, 128, 0)); // Olive
    barRenderer.setSeriesPaint(11, Color.magenta);

    // Add the second dataset and render as a single transparent bar with the total at the top.
    CategoryItemRenderer itemRenderer = new BarRenderer();
    plot.setDataset(1, barDataset);
    plot.setRenderer(1, itemRenderer);

    // Make bar transparent
    itemRenderer.setSeriesPaint(0, new Color(0, 0, 255, 0));
    itemRenderer.setBaseItemLabelsVisible(true);
    itemRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());

    chartPanel = new ChartPanel(barChart);
  }

  /**
   * Method to populate the datasets.  This called during initialization and whenever the user selects
   * another date period to display.
   */
  private void loadData()
  {
    databaseReader.readRainData();
    HashMap<Long, Float> yearlyRainData = databaseReader.getYearlyRainData();
    HashMap<Long, Float> monthlyRainData = databaseReader.getMonthlyRainData();

    // Convert the time values to string values for plots.
    for (Map.Entry<Long, Float> entry : yearlyRainData.entrySet())
    {
      Instant instant = Instant.ofEpochMilli(entry.getKey());
      OffsetDateTime dateGMT = OffsetDateTime.ofInstant(instant, ZoneOffset.ofHours(0));
      int year = dateGMT.getYear();

      // Do not graph the first partial year, 2004
      if (year != 2004)
      {
        yearlyBins.put(Integer.toString(year), entry.getValue());
      }
    }

    for (Map.Entry<Long, Float> entry : monthlyRainData.entrySet())
    {
      Instant instant = Instant.ofEpochMilli(entry.getKey());
      OffsetDateTime dateGMT = OffsetDateTime.ofInstant(instant, ZoneOffset.ofHours(0));
      int year = dateGMT.getYear();
      int month = dateGMT.getMonthValue();
      String monthString = Integer.toString(year) + "-" + Integer.toString(month);

      // Do not graph the first partial year, 2004
      if (year != 2004)
      {
        monthlyBins.put(monthString, entry.getValue());
      }
    }
  }

  /**
   * Method called to erase the trace and re-load with data depending on the data type.
   */
  public void populateDatasets()
  {
    stackedDataset.clear();
    barDataset.clear();
    monthlyBins.clear();
    yearlyBins.clear();
    loadData();

    // Sort the monthly data set before processing
    List<String> monthKeys = new ArrayList<>(monthlyBins.keySet());
    monthKeys.sort(new RainDataThread.KeyComparator());

    int i = 0;
    while (i < monthKeys.size())
    {
      String key = monthKeys.get(i);
      String[] strings = key.split("-");

      String month = "";
      if (strings[1].equalsIgnoreCase("1"))
        month = "Jan";
      else if (strings[1].equalsIgnoreCase("2"))
        month = "Feb";
      else if (strings[1].equalsIgnoreCase("3"))
        month = "Mar";
      else if (strings[1].equalsIgnoreCase("4"))
        month = "Apr";
      else if (strings[1].equalsIgnoreCase("5"))
        month = "May";
      else if (strings[1].equalsIgnoreCase("6"))
        month = "Jun";
      else if (strings[1].equalsIgnoreCase("7"))
        month = "Jul";
      else if (strings[1].equalsIgnoreCase("8"))
        month = "Aug";
      else if (strings[1].equalsIgnoreCase("9"))
        month = "Sep";
      else if (strings[1].equalsIgnoreCase("10"))
        month = "Oct";
      else if (strings[1].equalsIgnoreCase("11"))
        month = "Nov";
      else if (strings[1].equalsIgnoreCase("12"))
        month = "Dec";

      Float value = monthlyBins.get(key);
      stackedDataset.addValue(value, month, strings[0]);
      i++;
    }

    // Sort the yearly data set before processing
    List<String> yearKeys = new ArrayList<>(yearlyBins.keySet());
    yearKeys.sort(new RainDataThread.KeyComparator());

    int j = 0;
    while (j < yearKeys.size())
    {
      String key = yearKeys.get(j);
      barDataset.addValue(yearlyBins.get(key), "" , key);
      j++;
    }
  }

  /**
   * Method to export the data to a spreadsheet.
   */
  public void exportData()
  {
    File file = new File("reports" + "/RainData.csv");
    try (PrintWriter pw = new PrintWriter(file.getAbsoluteFile()))
    {
      StringBuilder sb = new StringBuilder();

      sb.append("Year");
      sb.append(',');
      sb.append("Month");
      sb.append(',');
      sb.append("Amount");
      sb.append('\n');

      databaseReader.readRainData();
      HashMap<Long, Float> monthlyRainData = databaseReader.getMonthlyRainData();

      for (Map.Entry<Long, Float> entry : monthlyRainData.entrySet())
      {
        Instant instant = Instant.ofEpochMilli(entry.getKey());
        OffsetDateTime dateGMT = OffsetDateTime.ofInstant(instant, ZoneOffset.ofHours(0));
        int year = dateGMT.getYear();
        int month = dateGMT.getMonthValue();

        sb.append(year);
        sb.append(',');
        sb.append(month);
        sb.append(',');
        sb.append(String.format("%.2f", entry.getValue()));
        sb.append('\n');
      }
      pw.write(sb.toString());
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Internal class to provide a comparator function.
   */
  class KeyComparator implements Comparator<String>
  {
    /**
     * Comparator for year-month string values.
     * Return 1 if rhs should be before lhs
     * Return -1 if lhs should be before rhs
     * Return 0 otherwise
     *
     * @param a Right hand side value.
     * @param b Left hand side value;
     * @return Results, see above.
     */
    @Override
    public int compare(String a, String b)
    {
      String[] aStrings = a.split("-");
      String[] bStrings = b.split("-");
      int aYear = Integer.valueOf(aStrings[0]);
      int bYear = Integer.valueOf(bStrings[0]);

      if (aYear == bYear)
      {
        return Integer.valueOf(aStrings[1]).compareTo(Integer.valueOf(bStrings[1]));
      }
      else if (aYear < bYear)
      {
        return -1;
      }
      else // aYear > bYear
      {
        return 1;
      }
    }
  }
}
