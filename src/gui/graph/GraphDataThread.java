/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class encapsulates the graph data as well as the jfreechart
            chart.  The y-axis is changed based on the selection of the y-axis
            button.

  Mods:		  09/01/21  Initial Release.
            10/15/21  Fixed ET calculation.
*/
package gui.graph;

import algorithms.Calculations;
import data.dbrecord.*;
import dbif.DatabaseReader;
import gui.graph.data.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.*;
import util.ConfigProperties;
import util.Logger;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

public class GraphDataThread
{
  private JFreeChart lineChart = null;
  private final AbstractDataPlotter[] chartData = new AbstractDataPlotter[24];
  private final DatabaseReader dbReader = DatabaseReader.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private final Logger logger = Logger.getInstance();

  // The data objects that hold the data.
  private final InsideHumidData inHumidData = new InsideHumidData(GraphDefs.WEEK_DATA_SIZE);
  private final InsideTempData inTempData = new InsideTempData(GraphDefs.WEEK_DATA_SIZE);
  private final GreenhouseTempData greenhouseTempData = new GreenhouseTempData(GraphDefs.WEEK_DATA_SIZE);
  private final OutsideTempData outTempData = new OutsideTempData(GraphDefs.WEEK_DATA_SIZE);
  private final HighOutsideTempData highOutTempData = new HighOutsideTempData(GraphDefs.WEEK_DATA_SIZE);
  private final LowOutsideTempData lowOutTempData = new LowOutsideTempData(GraphDefs.WEEK_DATA_SIZE);
  private final AvgOutsideTempData avgOutTempData = new AvgOutsideTempData(GraphDefs.WEEK_DATA_SIZE);
  private final OutsideHumidData outHumidData = new OutsideHumidData(GraphDefs.WEEK_DATA_SIZE);
  private final RainData rainData = new RainData(GraphDefs.WEEK_DATA_SIZE);
  private final IssReceptionData issReceptionData = new IssReceptionData(GraphDefs.WEEK_DATA_SIZE);
  private final SolarData solarData = new SolarData(GraphDefs.WEEK_DATA_SIZE);
  private final WindSpeedData windSpeedData = new WindSpeedData(GraphDefs.WEEK_DATA_SIZE);
  private final WindDirectionData windDirectionData = new WindDirectionData(GraphDefs.WEEK_DATA_SIZE);
  private final PressureData pressureData = new PressureData(GraphDefs.WEEK_DATA_SIZE);
  private final WindChillData windChillData = new WindChillData(GraphDefs.WEEK_DATA_SIZE);
  private final HeatDDData heatDDData = new HeatDDData(GraphDefs.WEEK_DATA_SIZE);
  private final CoolDDData coolDDData = new CoolDDData(GraphDefs.WEEK_DATA_SIZE);
  private final DewPointData dewPointData = new DewPointData(GraphDefs.WEEK_DATA_SIZE);
  private final HeatIndexData heatIndexData = new HeatIndexData(GraphDefs.WEEK_DATA_SIZE);
  private final ThwData thwData = new ThwData(GraphDefs.WEEK_DATA_SIZE);
  private final ThswData thswData = new ThswData(GraphDefs.WEEK_DATA_SIZE);
  private final EtData etData = new EtData(GraphDefs.WEEK_DATA_SIZE);
  private final WindRunData windRunData = new WindRunData(GraphDefs.WEEK_DATA_SIZE);

  private final HashMap<String, AbstractDataPlotter> dataMap = new HashMap<>();

  private static class SingletonHelper
  {
    private static final GraphDataThread INSTANCE = new GraphDataThread();
  }

  public static GraphDataThread getInstance()
  {
    return GraphDataThread.SingletonHelper.INSTANCE;
  }

  private GraphDataThread()
  {
    // Create a hashmap of data objects based on dataname.
    dataMap.put(GraphDefs.INTEMP_STRING, inTempData);
    dataMap.put(GraphDefs.INHUMID_STRING, inHumidData);
    dataMap.put(GraphDefs.GREENHOUSE_TEMP_STRING, greenhouseTempData);
    dataMap.put(GraphDefs.OUTTEMP_STRING, outTempData);
    dataMap.put(GraphDefs.HIGH_OUTTEMP_STRING, highOutTempData);
    dataMap.put(GraphDefs.LOW_OUTTEMP_STRING, lowOutTempData);
    dataMap.put(GraphDefs.AVG_OUTTEMP_STRING, avgOutTempData);
    dataMap.put(GraphDefs.OUTHUMID_STRING, outHumidData);
    dataMap.put(GraphDefs.RAINFALL_STRING, rainData);
    dataMap.put(GraphDefs.ISS_RECEPTION_STRING, issReceptionData);
    dataMap.put(GraphDefs.SOLAR_RAD_STRING, solarData);
    dataMap.put(GraphDefs.WIND_SPEED_STRING, windSpeedData);
    dataMap.put(GraphDefs.WIND_DIR_STRING, windDirectionData);
    dataMap.put(GraphDefs.PRESSURE_STRING, pressureData);
    dataMap.put(GraphDefs.WIND_CHILL_STRING, windChillData);
    dataMap.put(GraphDefs.HEAT_DD_STRING, heatDDData);
    dataMap.put(GraphDefs.COOL_DD_STRING, coolDDData);
    dataMap.put(GraphDefs.DEW_POINT_STRING, dewPointData);
    dataMap.put(GraphDefs.HEAT_INDEX_STRING, heatIndexData);
    dataMap.put(GraphDefs.THW_STRING, thwData);
    dataMap.put(GraphDefs.THSW_STRING, thswData);
    dataMap.put(GraphDefs.ET_STRING, etData);
    dataMap.put(GraphDefs.WIND_RUN_STRING, windRunData);
  }

  public ChartPanel getChart()
  {
    return new ChartPanel(lineChart);
  }

  /**
   * Method to initialize this singleton.  This method must be called before any other methods are called.
   */
  public void initializeChart()
  {
    lineChart = ChartFactory.createTimeSeriesChart
      ("",
       "Time",
       "Value",
       null,
       false,
       true,
       false
      );

    // sets plot background
    XYPlot plot = lineChart.getXYPlot();
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
   * Method that initializes the datasets.
   */
  public void addDatasetsToCharts()
  {
    // Add all datasets to all charts.
    chartData[outTempData.getGraphIndex()] = outTempData;
    chartData[highOutTempData.getGraphIndex()] = highOutTempData;
    chartData[lowOutTempData.getGraphIndex()] = lowOutTempData;
    chartData[avgOutTempData.getGraphIndex()] = avgOutTempData;
    chartData[outHumidData.getGraphIndex()] = outHumidData;
    chartData[rainData.getGraphIndex()] = rainData;
    chartData[solarData.getGraphIndex()] = solarData;
    chartData[windChillData.getGraphIndex()] = windChillData;
    chartData[heatDDData.getGraphIndex()] = heatDDData;
    chartData[coolDDData.getGraphIndex()] = coolDDData;
    chartData[dewPointData.getGraphIndex()] = dewPointData;
    chartData[heatIndexData.getGraphIndex()] = heatIndexData;
    chartData[thwData.getGraphIndex()] = thwData;
    chartData[thswData.getGraphIndex()] = thswData;
    chartData[inTempData.getGraphIndex()] = inTempData;
    chartData[inHumidData.getGraphIndex()] = inHumidData;
    chartData[issReceptionData.getGraphIndex()] = issReceptionData;
    chartData[pressureData.getGraphIndex()] = pressureData;
    chartData[windSpeedData.getGraphIndex()] = windSpeedData;
    chartData[windDirectionData.getGraphIndex()] = windDirectionData;
    chartData[greenhouseTempData.getGraphIndex()] = greenhouseTempData;
    chartData[etData.getGraphIndex()] = etData;
    chartData[windRunData.getGraphIndex()] = windRunData;

    // Add the chart datasets.
    for (AbstractDataPlotter nextItem : chartData)
    {
      if (nextItem != null)
      {
        addDataset(nextItem);
      }
    }
  }

  /**
   * Internal method to add the dataset and renderer based on what datasets currently exist in the chart.
   *
   * @param data The abstract data to add.
   */
  private void addDataset(AbstractDataPlotter data)
  {
    XYPlot plot = lineChart.getXYPlot();
    boolean datasetFound = false;
    int index = 0;
    for (int i = 0; i < plot.getDatasetCount(); i++)
    {
      if (plot.getDataset(i) == null)
      {
        index = i;
        datasetFound = true;
        break;
      }
    }

    if (!datasetFound)
    {
      index = plot.getDatasetCount();
    }

    plot.setDataset(index, data.getGraphDataset());
    plot.setRenderer(index, data.getGraphRenderer());
    plot.setRangeAxis(index, data.getDomainAxis());

    adjustRangeAxisBounds(data);

    plot.mapDatasetToRangeAxis(index, index);
    plot.setRangeAxisLocation(index, AxisLocation.BOTTOM_OR_LEFT);

    if (!data.isGraphDisplayed())
      removeDataFromChart(data.getName());
  }

  /**
   * Internal method to adjust the y-axis min and max values.
   *
   * @param data The data to adjust.
   */
  private void adjustRangeAxisBounds(AbstractDataPlotter data)
  {
    ValueAxis axis = lineChart.getXYPlot().getRangeAxis(data.getGraphIndex());
    if (axis != null)
    {
      axis.setRange(data.getGraphMinY(), data.getGraphMaxY());
    }
  }

  /**
   * Method to change the graph range axis (y-axis) based on the user selection.
   *
   * @param addData Whether to add or remove the given line.
   * @param dataname  The data line to add/remove.
   */
  // TODO: The like values such as temperatures should have the same axis, otherwise the data skews.
  public void setRangeAxis(boolean addData, String dataname)
  {
    AbstractDataPlotter data = dataMap.get(dataname);
    XYPlot plot = lineChart.getXYPlot();
    XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
    renderer.setSeriesVisible(data.getGraphIndex(), addData);

    // Adjust the y-axis.
    for (int i = 0; i < plot.getRangeAxisCount(); i++)
    {
      if (plot.getRangeAxis(i) != null)
        plot.getRangeAxis(i).setVisible(false);
    }
    plot.getRangeAxis(data.getGraphIndex()).setVisible(true);
  }

  /**
   * Method to get the color assigned to a data set.
   *
   * @param dataname The string value of the data ID.
   * @return The assigned color
   */
  public Color getColor(String dataname)
  {
    AbstractDataPlotter data = dataMap.get(dataname);
    if (data != null)
    {
      return dataMap.get(dataname).getColor();
    }
    else
    {
      System.out.println("GraphDataThread.getColor: Invalid color for data " + dataname);
      return Color.BLACK;
    }
  }

  /**
   * Method to set the dataset size.
   *
   * @param dataname The dataset to modify.
   * @param duration The new duration string.
   */
  public void setDatasetSize(String dataname, String duration)
  {
    AbstractDataPlotter abstractDataPlotter = dataMap.get(dataname);
    if (duration.equalsIgnoreCase(GraphDefs.YEAR_STRING))
    {
      abstractDataPlotter.changeGraphDataSetSize(GraphDefs.YEAR_DATA_SIZE);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.MONTH_STRING))
    {
      abstractDataPlotter.changeGraphDataSetSize(GraphDefs.MONTH_DATA_SIZE);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.HALF_MONTH_STRING))
    {
      abstractDataPlotter.changeGraphDataSetSize(GraphDefs.HALF_MONTH_DATA_SIZE);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.WEEK_STRING))
    {
      abstractDataPlotter.changeGraphDataSetSize(GraphDefs.WEEK_DATA_SIZE);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.HALF_WEEK_STRING))
    {
      abstractDataPlotter.changeGraphDataSetSize(GraphDefs.HALF_WEEK_DATA_SIZE);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.DAY_STRING))
    {
      abstractDataPlotter.changeGraphDataSetSize(GraphDefs.DAY_DATA_SIZE);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.HALF_DAY_STRING))
    {
      abstractDataPlotter.changeGraphDataSetSize(GraphDefs.HALF_DAY_DATA_SIZE);
    }

    abstractDataPlotter.eraseGraphSeriesData();
    abstractDataPlotter.eraseGraphListData();
  }

  /**
   * Method used to add the series to the dataset based on what is selected.
   *
   * @param dataname The data to add.
   */
  public void addDataToChart(String dataname)
  {
    AbstractDataPlotter abstractDataPlotter = dataMap.get(dataname);
    abstractDataPlotter.setGraphDisplayed(true);

    chartData[abstractDataPlotter.getGraphIndex()] = abstractDataPlotter;
    abstractDataPlotter.getGraphDataset().addSeries(abstractDataPlotter.getGraphSeries());
  }

  /**
   * Method used to remove the data from the chart.
   *
   * @param selectedData The data to remove.
   */
  public void removeDataFromChart(String selectedData)
  {
    AbstractDataPlotter abstractDataPlotter = dataMap.get(selectedData);
    abstractDataPlotter.setGraphDisplayed(false);

    chartData[abstractDataPlotter.getGraphIndex()] = null;
    abstractDataPlotter.getGraphDataset().removeSeries(abstractDataPlotter.getGraphSeries());
  }

  /**
   * Method to populate the dataset.  This called during initialization and whenever the user selects
   * another date period to display.
   *
   * @param endYear  The year to retrieve.
   * @param endMonth The month of the year to retrieve.
   * @param endDay   The day of the month to retrieve.
   * @param duration The duration string.
   */
  public void populateDataset(int endYear, int endMonth, int endDay, String duration)
  {
    // Determine the end year/month/day by incrementing start date by duration.
    LocalDate endDate = LocalDate.of(endYear, endMonth, endDay);
    LocalDate startDate;
    if (duration.equalsIgnoreCase(GraphDefs.YEAR_STRING))
    {
      startDate = endDate.minusYears(1);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.MONTH_STRING))
    {
      startDate = endDate.minusMonths(1);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.HALF_MONTH_STRING))
    {
      startDate = endDate.minusDays(14);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.WEEK_STRING))
    {
      startDate = endDate.minusDays(7);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.HALF_WEEK_STRING))
    {
      startDate = endDate.minusDays(3);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.DAY_STRING))
    {
      startDate = endDate.minusDays(1);
    }
    else if (duration.equalsIgnoreCase(GraphDefs.HALF_DAY_STRING))
    {
      startDate = endDate.minusDays(1);
    }
    else
    {
      startDate = endDate;
    }
    int startYear = startDate.getYear();
    int startMonth = startDate.getMonthValue();
    int startDay = startDate.getDayOfMonth();

    // First read and calculate the evapotranspiration data as it uses the dbReader and would
    // interfere with the code below.
    EvapotransRecord evapotransData = dbReader.getEvapotransData(LocalDateTime.now());

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
      return;
    }

    if (startYear == endYear && startMonth == endMonth)
    {
      addData(startDay, endDay, evapotransData);
      addSummaryData(startDay, endDay, startMonth, startYear);
    }
    else
    {
      addData(startDay, 0, evapotransData);
      addSummaryData(startDay, 0, startMonth, startYear);

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
          return;
        }

        if (startYear == endYear && startMonth == endMonth)
        {
          addData(0, endDay, evapotransData);
          addSummaryData(0, endDay, startMonth, startYear);
          endReached = true;
        }
        else
        {
          addData(0, 0, evapotransData);
          addSummaryData(0, 0, startMonth, startYear);

          startMonth++;
          if (startMonth == 13)
          {
            startYear++;
            startMonth = 1;
          }
        }
      }
    }

    adjustRangeAxisBounds(inHumidData);
    adjustRangeAxisBounds(inTempData);
    adjustRangeAxisBounds(greenhouseTempData);
    adjustRangeAxisBounds(outHumidData);
    adjustRangeAxisBounds(outTempData);
    adjustRangeAxisBounds(highOutTempData);
    adjustRangeAxisBounds(lowOutTempData);
    adjustRangeAxisBounds(avgOutTempData);
    adjustRangeAxisBounds(issReceptionData);
    adjustRangeAxisBounds(solarData);
    adjustRangeAxisBounds(pressureData);
    adjustRangeAxisBounds(rainData);
    adjustRangeAxisBounds(windSpeedData);
    adjustRangeAxisBounds(windChillData);
    adjustRangeAxisBounds(dewPointData);
    adjustRangeAxisBounds(heatDDData);
    adjustRangeAxisBounds(coolDDData);
    adjustRangeAxisBounds(heatIndexData);
    adjustRangeAxisBounds(thwData);
    adjustRangeAxisBounds(thswData);
    adjustRangeAxisBounds(windDirectionData);
    adjustRangeAxisBounds(etData);
    adjustRangeAxisBounds(windRunData);
  }

  private void addSummaryData(int startDay, int endDay, int month, int year)
  {
    DataFileRecord nextRecord = dbReader.getNextSummaryRecord();
    while (nextRecord != null)
    {
      if (nextRecord instanceof DailySummary1Record)
      {
        DailySummary1Record data = (DailySummary1Record) nextRecord;
        Minute minute = new Minute(0, 12, data.getDay(), month, year);

        if (startDay != 0 && data.getDay() < startDay)
        {
          nextRecord = dbReader.getNextSummaryRecord();
          continue;
        }
        else if (endDay != 0 && data.getDay() > endDay)
        {
          return;
        }

        highOutTempData.getGraphSeries().add(minute, data.getHiOutTemp());
        lowOutTempData.getGraphSeries().add(minute, data.getLowOutTemp());
        avgOutTempData.getGraphSeries().add(minute, data.getAvgOutTemp());
      }
      nextRecord = dbReader.getNextSummaryRecord();
    }
  }

  /**
   * Internal record to read the records for a month's worth of data, only reading the days that are between the
   * start and end day values.  If either startDay or endDay are zero, then these values are not set, i.e. the endDay
   * is not within this dataset.
   *
   * @param startDay The start day value, if not zero then exclude days before this value.
   * @param endDay   The end day value, if not zero then exclude days after this value.
   */
  private void addData(int startDay, int endDay, EvapotransRecord evapotransData)
  {
    DataFileRecord nextRecord = dbReader.getNextRecord();
    while (nextRecord != null)
    {
      if (nextRecord instanceof WeatherRecord)
      {
        WeatherRecordExtended data = (WeatherRecordExtended) nextRecord;
        LocalDateTime timestamp = data.getTimestamp();
        int day = timestamp.getDayOfMonth();
        Date date = Date.from(timestamp.atZone(ZoneId.systemDefault()).toInstant());
        Minute minute = new Minute(date);

        if (startDay != 0 && day < startDay)
        {
          nextRecord = dbReader.getNextRecord();
          continue;
        }
        else if (endDay != 0 && day > endDay)
        {
          return;
        }

        try
        {
          inTempData.getGraphSeries().add(minute, data.getInsideTemp());
        }
        catch (Exception e)
        {
          System.out.println("----------");
        }
        // TODO: this may also need a check against DatabaseCommon.UNDEFINED_SHORT_VALUE
        outTempData.getGraphSeries().add(minute, data.getOutsideTemp());
        greenhouseTempData.getGraphSeries().add(minute, data.getSoilTemp1());
        inHumidData.getGraphSeries().add(minute, data.getInsideHumidity());
        outHumidData.getGraphSeries().add(minute, data.getOutsideHumidity());
        issReceptionData.getGraphSeries().add(minute, data.getNumOfWindSamples());
        solarData.getGraphSeries().add(minute, data.getHighSolarRadiation());
        pressureData.getGraphSeries().add(minute, data.getPressure());
        rainData.getGraphSeries().add(minute, data.getRainfall());
        windSpeedData.getGraphSeries().add(minute, data.getAverageWindSpeed());
        etData.getGraphSeries().add(minute, data.getEt());

        float windChill = Calculations.calculateWindChill(data.getOutsideTemp(), data.getAverageWindSpeed());
        windChillData.getGraphSeries().add(minute, windChill);

        float dewPoint = Calculations.calculateDewPoint(data.getOutsideTemp(), data.getOutsideHumidity());
        dewPointData.getGraphSeries().add(minute, dewPoint);

        float heatIndex = Calculations.calculateHeatIndex(data.getOutsideTemp(), data.getOutsideHumidity());
        heatIndexData.getGraphSeries().add(minute, heatIndex);

        float thwValue = Calculations.calculateTHW(data.getOutsideTemp(), data.getAverageWindSpeed(),
                                                   data.getOutsideHumidity());
        thwData.getGraphSeries().add(minute, thwValue);

        float thswValue = Calculations.calculateTHSW(data.getOutsideTemp(), data.getAverageWindSpeed(),
                                                     data.getOutsideHumidity(), data.getSolarRadiation());
        thswData.getGraphSeries().add(minute, thswValue);

        boolean addToHeatDDTrace = !heatDDData.getChart().equalsIgnoreCase(GraphDefs.NONE);
        heatDDData.addToGraphDataset(addToHeatDDTrace, date, data.getHeatDD());

        boolean addToCoolDDTrace = !coolDDData.getChart().equalsIgnoreCase(GraphDefs.NONE);
        coolDDData.addToGraphDataset(addToCoolDDTrace, date, data.getCoolDD());

        boolean addToWindRunTrace = !windRunData.getChart().equalsIgnoreCase(GraphDefs.NONE);
        windRunData.addToGraphDataset(addToWindRunTrace, date, data.getWindRunTotal());

        // Wind direction can be 0xFF which means no valid data.
        byte windDirectionByte = data.getWindDirectionNative();
        if (windDirectionByte != -1)
        {
          windDirectionData.getGraphSeries().add(minute, windDirectionByte);
        }
      }
      nextRecord = dbReader.getNextRecord();
    }
  }
}
