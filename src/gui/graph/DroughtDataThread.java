/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This thread graphs the drought data, showing the accumulated
            monthly deviation of rainfall amounts from the historic average.

  Mods:		  10/05/21  Initial Release.
            10/07/21  Added progress bar.
*/
package gui.graph;

import data.dbrecord.DataFileRecord;
import data.dbrecord.WeatherRecord;
import data.dbrecord.WeatherRecordExtended;
import dbif.DatabaseReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import util.ConfigProperties;
import util.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

public class DroughtDataThread extends SwingWorker<Void, Void>
{
  private JFreeChart barChart = null;
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private final DatabaseReader dbReader = DatabaseReader.getInstance();
  private DefaultCategoryDataset dataset = new DefaultCategoryDataset();
  private final Logger logger = Logger.getInstance();
  private ProgressMonitor progressMonitor;

  private int startYear;
  private int startMonth;
  private int duration;
  private double progress;
  private float droughtTotal = 0;
  private final float[] myAverageRainValues= new float[]
    { 0.00f, 0.06f, 0.12f, 0.55f, 1.10f, 1.70f, 1.36f, 2.76f, 2.03f, 0.86f, 0.31f, 0.14f, 0.10f};

  DroughtDataThread(ProgressMonitor progressMonitor, int year, int month, int duration)
  {
    startYear = year;
    startMonth = month;
    this.duration = duration;
    this.progressMonitor = progressMonitor;
  }

  /**
   * Method to initialize this singleton.  This method must be called before any other methods are called.
   */
  private void initializeChart()
  {
    //Create chart
    barChart= ChartFactory.createBarChart(
      "",
      "Year",
      "Amount",
      dataset,
      PlotOrientation.VERTICAL,
      true,
      true,
      false
    );

    final CategoryPlot plot = barChart.getCategoryPlot();
    ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());

    CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

    // sets plot background
    plot.setBackgroundPaint(PROPS.getBackgroundColor());
    plot.setDomainGridlinePaint(Color.black);
    plot.setRangeGridlinePaint(Color.black);

    // sets paint color for the grid lines
    plot.setRangeGridlinesVisible(true);
    plot.setRangeGridlinePaint(Color.BLACK);

    plot.setDomainGridlinesVisible(true);
    plot.setDomainGridlinePaint(Color.BLACK);
  }

  /**
   * Method to create the bar chart.
   *
   * @return The chart panel to display.
   */
  public ChartPanel createChart()
  {
    initializeChart();
    return new ChartPanel(barChart);
  }

  /**
   * Method called when the background task is complete.
   */
  @Override
  public void done()
  {
    progressMonitor.close();
  }

  /**
   * Method called to start the background task.  This task reads the data from the database files and
   * places the pertinent data into the dataset for the bar chart to display.
   *
   * @return null
   */
  @Override
  public Void doInBackground()
  {
    droughtTotal = 0;
    dataset.clear();

    // Determine the end year/month/day by incrementing start date by duration.
    LocalDate startDate = LocalDate.of(startYear, startMonth, 1);
    LocalDate endDate = startDate.plusMonths(duration);
    int endYear = endDate.getYear();
    int endMonth = endDate.getMonthValue();

    // Read first year/month file records.
    try
    {
      dbReader.readData(startYear, startMonth, null);
      dbReader.reset();
      dbReader.readSummaryData(startYear, startMonth);
      dbReader.resetSummary();
    }
    catch (IOException e)
    {
      logger.logData("Graph Data: populateDataset: Unable to get data: " + e.getLocalizedMessage());
      return null;
    }

    if (startYear == endYear && startMonth == endMonth)
    {
      processMonthsRain(startYear, startMonth);
    }
    else
    {
      processMonthsRain(startYear, startMonth);

      startMonth++;
      if (startMonth == 13)
      {
        startYear++;
        startMonth = 1;
      }

      boolean endReached = false;
      while (!endReached)
      {
        try
        {
          dbReader.readData(startYear, startMonth, null);
          dbReader.reset();
          dbReader.readSummaryData(startYear, startMonth);
          dbReader.resetSummary();
        }
        catch (IOException e)
        {
          logger.logData("Graph Data: populateDataset: Unable to get data: " + e.getLocalizedMessage());
          return null;
        }

        if (startYear == endYear && startMonth == endMonth)
        {
          processMonthsRain(startYear, startMonth);
          endReached = true;
        }
        else
        {
          processMonthsRain(startYear, startMonth);

          startMonth++;
          if (startMonth == 13)
          {
            startYear++;
            startMonth = 1;
          }
        }
        progress = progress + 100/duration;
        setProgress((int)progress);
      }
      setProgress(100);
    }
    return null;
  }

  /**
   * Internal method to extract the data, calculate the drought total and place the data into the dataset.
   * This method processes the monthly data in the file defined by the month and year values.
   *
   * @param year  The year of the data.
   * @param month The month of the data.
   */
  private void processMonthsRain(int year, int month)
  {
    float monthValue = 0;

    DataFileRecord nextRecord = dbReader.getNextRecord();
    while (nextRecord != null)
    {
      if (nextRecord instanceof WeatherRecord)
      {
        WeatherRecordExtended data = (WeatherRecordExtended) nextRecord;

        if (data.getRainfall() > 0)
        {
          monthValue = monthValue + data.getRainfall();
        }
      }
      nextRecord = dbReader.getNextRecord();
    }
    droughtTotal = droughtTotal + monthValue - myAverageRainValues[month];

    dataset.addValue(monthValue, "Rain Amount", String.valueOf(year) + "-" + String.valueOf(month));
    dataset.addValue(droughtTotal, "Drought Total", String.valueOf(year) + "-" + String.valueOf(month));
  }
}
