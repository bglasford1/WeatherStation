/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class charts the snow data.

  Mods:		  09/01/21 Initial Release.
*/
package gui.snow;

import data.dbrecord.SnowRecord;
import dbif.SnowDatabase;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SnowDataThread
{
  private ChartPanel chartPanel = null;
  private final SnowDatabase snowDatabase = SnowDatabase.getInstance();
  private DefaultCategoryDataset stackedDataset = new DefaultCategoryDataset();
  private DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

  // Data bins: monthly and yearly.
  private final HashMap<String, Float> monthlyBins = new HashMap<>(); // time index is by month with a value of the 1st.
  private final HashMap<String, Float> yearlyBins = new HashMap<>();  // time index is by season with a value of January 1st.

  private static class SingletonHelper
  {
    private static final SnowDataThread INSTANCE = new SnowDataThread();
  }

  public static SnowDataThread getInstance()
  {
    return SnowDataThread.SingletonHelper.INSTANCE;
  }

  private SnowDataThread() { }

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
    CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

    BarRenderer barRenderer = (BarRenderer)plot.getRenderer();
    barRenderer.setBarPainter(new StandardBarPainter());
    barRenderer.setSeriesPaint(0, Color.blue); // July
    barRenderer.setSeriesPaint(1, Color.yellow);
    barRenderer.setSeriesPaint(2, Color.red);
    barRenderer.setSeriesPaint(3, Color.black);
    barRenderer.setSeriesPaint(4, new Color(128, 128, 0)); // Olive
    barRenderer.setSeriesPaint(5, Color.magenta);
    barRenderer.setSeriesPaint(6, Color.green); // January
    barRenderer.setSeriesPaint(7, Color.gray);
    barRenderer.setSeriesPaint(8, Color.cyan);
    barRenderer.setSeriesPaint(9, Color.orange);
    barRenderer.setSeriesPaint(10, new Color(202, 88, 0)); // Brown
    barRenderer.setSeriesPaint(11, new Color(0, 128, 128)); // Teal

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
    int firstYear = 9999;
    int lastYear = 0;

    // Add the snow data to the monthly bins.
    List<SnowRecord> records = snowDatabase.readData();
    for (SnowRecord nextRecord : records)
    {
      int year = nextRecord.getYear();
      int month = nextRecord.getMonth();

      if (year < firstYear)
        firstYear = year;

      if (year > lastYear)
        lastYear = year;

      // Data is collected by season and not year.  Year value is January 1st.  If month is after July then add to next year.
      String yearString;
      if (month > 7)
      {
        yearString = Integer.toString(year) + "-" + Integer.toString(year + 1);
      }
      else
      {
        yearString = Integer.toString(year - 1) + "-" + Integer.toString(year);
      }

      // Add year value to yearly bin.
      Float yearValue = yearlyBins.get(yearString);
      if (yearValue == null)
        yearValue = 0.0f;

      yearValue += nextRecord.getAmount();
      // Do not graph the first partial year, 2004
      if (year != 2004 || month > 7)
      {
        if (yearlyBins.containsKey(yearString))
        {
          yearlyBins.replace(yearString, yearValue);
        }
        else
        {
          yearlyBins.put(yearString, yearValue);
        }
      }

      // Add month value to monthly bin.
      String monthString = Integer.toString(year) + "-" + Integer.toString(month);

      Float monthValue = monthlyBins.get(monthString);
      if (monthValue == null)
        monthValue = 0.0f;

      monthValue += nextRecord.getAmount();
      // Do not graph the first partial year, 2004
      if (year != 2004 || month > 7)
      {
        if (monthlyBins.containsKey(monthString))
        {
          monthlyBins.replace(monthString, monthValue);
        }
        else
        {
          monthlyBins.put(monthString, monthValue);
        }
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

    // Sort the data set before processing
    List<String> monthKeys = new ArrayList<>(monthlyBins.keySet());
    monthKeys.sort(new KeyComparator());

    HashMap<String, Float> tempBins = new HashMap<>();
    int i = 0;
    while (i < monthKeys.size())
    {
      String key = monthKeys.get(i);
      String[] strings = key.split("-");

      String yearString;
      int year = Integer.parseInt(strings[0]);
      if (Integer.parseInt(strings[1]) > 7)
      {
        yearString = Integer.toString(year) + "-" + Integer.toString(year + 1);
      }
      else
      {
        yearString = Integer.toString(year - 1) + "-" + Integer.toString(year);
      }

      tempBins.put(yearString + ":" + strings[1], monthlyBins.get(key));
      i++;
    }

    // Reorder stacked dataset so the months are in order.
    // Start by sorting the data set before processing.
    List<String> tempKeys = new ArrayList<>(tempBins.keySet());
    tempKeys.sort(new ComplexComparator());

    // Add the tempBin values to the Stacked Dataset.  Start with the first season month - 1.
    int previousMonth = 6;
    String previousYearRange = "";
    for (String nextKey : tempKeys)
    {
      String[] strings = nextKey.split(":");
      int currentMonth = Integer.valueOf(strings[1]);

      // Fill in missing months with a zero value.
      if (previousYearRange.equalsIgnoreCase(strings[0]))
      {
        if ((previousMonth == 12 && currentMonth != 1) || (previousMonth != 12 && previousMonth + 1 != currentMonth))
        {
          int month;
          if (previousMonth == 12)
            previousMonth = 0;
          for (month = previousMonth + 1; month < currentMonth; month++)
          {
            stackedDataset.addValue(0.0, getMonthString(month), strings[0]);
          }
        }
      }
      else // Season changeover, so fill in gap of months with zero values.
      {
        int month;
        for (month = previousMonth + 1; month < currentMonth; month++)
        {
          stackedDataset.addValue(0.0, getMonthString(month), strings[0]);
        }
      }

      String month = getMonthString(currentMonth);
      Float value = tempBins.get(nextKey);

      previousMonth = Integer.valueOf(strings[1]);
      previousYearRange = strings[0];

      stackedDataset.addValue(value, month, strings[0]);
    }

    // Sort the data set before processing
    List<String> yearKeys = new ArrayList<>(yearlyBins.keySet());
    yearKeys.sort(new KeyComparator());

    int j = 0;
    while (j < yearKeys.size())
    {
      String key = yearKeys.get(j);
      barDataset.addValue(yearlyBins.get(key),"" , key);
      j++;
    }
  }

  /**
   * Internal method to convert an integer month value to a three letter acronym.
   *
   * @param month The integer month.
   * @return The string representation.
   */
  private String getMonthString(int month)
  {
    if (month == 1)
      return "Jan";
    else if (month == 2)
      return "Feb";
    else if (month == 3)
      return "Mar";
    else if (month == 4)
      return "Apr";
    else if (month == 5)
      return "May";
    else if (month == 6)
      return "Jun";
    else if (month == 7)
      return "Jul";
    else if (month == 8)
      return "Aug";
    else if (month == 9)
      return "Sep";
    else if (month == 10)
      return "Oct";
    else if (month == 11)
      return "Nov";
    else if (month == 12)
      return "Dec";
    else
      return "";
  }

  /**
   * Internal class to provide a comparator function. This compares values of "year-month", ex: "2005-6".
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

  /**
   * Internal class to provide a comparator function.  This compares two values of "year-year:month", ex: "2005-2006:5".
   */
  class ComplexComparator implements Comparator<String>
  {
    /**
     * Comparator for year-year:month string values.
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
      String[] aStrings = a.split(":");
      String[] bStrings = b.split(":");
      Integer aMonth = Integer.valueOf(aStrings[1]);
      Integer bMonth = Integer.valueOf(bStrings[1]);
      String[] aYearStrings = aStrings[0].split("-");
      String[] bYearStrings = bStrings[0].split("-");
      int aYear = Integer.valueOf(aYearStrings[0]);
      int bYear = Integer.valueOf(bYearStrings[0]);

      if (aYear == bYear)
      {
        if (aMonth < 7 && bMonth >= 7)
          return 1;
        else if (aMonth >= 7 && bMonth < 7)
          return -1;
        else
          return aMonth.compareTo(bMonth);
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