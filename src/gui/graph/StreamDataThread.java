/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class encapsulates the stream data as well as the jfreechart
            charts.  The periodic DMP is added as the data is received.  The
            y-axis is changed to match the data selected by the y-axis button.

  Mods:		  09/01/21  Initial Release.
            10/15/21  Fixed ET calculation.
*/
package gui.graph;

import algorithms.Calculations;
import data.consolerecord.DmpDataExtended;
import data.dbrecord.DataFileRecord;
import data.dbrecord.EvapotransRecord;
import data.dbrecord.WeatherRecord;
import data.dbrecord.WeatherRecordExtended;
import dbif.DatabaseCommon;
import dbif.DatabaseReader;
import gui.graph.data.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import util.ConfigProperties;
import util.Logger;
import util.TimeUtil;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

public class StreamDataThread
{
  // The JChart2D chart objects.
  private JFreeChart A_CHART = null;
  private JFreeChart B_CHART = null;
  private JFreeChart C_CHART = null;

  private final AbstractDataPlotter[] chartDataA = new AbstractDataPlotter[12];
  private final AbstractDataPlotter[] chartDataB = new AbstractDataPlotter[6];
  private final AbstractDataPlotter[] chartDataC = new AbstractDataPlotter[8];

  private final DatabaseReader dbReader = DatabaseReader.getInstance();
  private final DatabaseCommon dbCommon = DatabaseCommon.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private final Logger logger = Logger.getInstance();

  // The data objects that hold the data.
  private final InsideHumidData inHumidData = new InsideHumidData(GraphDefs.WEEK_DATA_SIZE);
  private final InsideTempData inTempData = new InsideTempData(GraphDefs.WEEK_DATA_SIZE);
  private final GreenhouseTempData greenhouseTempData = new GreenhouseTempData(GraphDefs.WEEK_DATA_SIZE);
  private final OutsideTempData outTempData = new OutsideTempData(GraphDefs.WEEK_DATA_SIZE);
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
    private static final StreamDataThread INSTANCE = new StreamDataThread();
  }

  public static StreamDataThread getInstance()
  {
    return SingletonHelper.INSTANCE;
  }

  /**
   * Constructor.
   */
  private StreamDataThread()
  {
    // Create the hashmap of data structures.
    dataMap.put(GraphDefs.INTEMP_STRING, inTempData);
    dataMap.put(GraphDefs.INHUMID_STRING, inHumidData);
    dataMap.put(GraphDefs.GREENHOUSE_TEMP_STRING, greenhouseTempData);
    dataMap.put(GraphDefs.OUTTEMP_STRING, outTempData);
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

  /**
   * Initialize the data structures and add them to the chart.
   */
  public void initializeCharts()
  {
    // Create the charts.
    A_CHART = configureChart();
    B_CHART = configureChart();
    C_CHART = configureChart();
  }

  private JFreeChart configureChart()
  {
    final JFreeChart chart = ChartFactory.createTimeSeriesChart(
      null,
      null,
      null,
      null,
      false,
      true,
      false
    );

    final XYPlot plot = chart.getXYPlot();
    plot.setBackgroundPaint(PROPS.getBackgroundColor());
    plot.setDomainGridlinePaint(Color.black);
    plot.setRangeGridlinePaint(Color.black);

    ValueAxis axis = plot.getDomainAxis();
    axis.setAutoRange(true);

    return chart;
  }

  public ChartPanel getAChart()
  {
    return new ChartPanel(A_CHART);
  }

  public ChartPanel getBChart()
  {
    return new ChartPanel(B_CHART);
  }

  public ChartPanel getCChart()
  {
    return new ChartPanel(C_CHART);
  }

  /**
   * Method that initializes the datasets.
   */
  public void addDatasetsToCharts()
  {
    // Add all datasets to all charts.
    addDatasetToChart(outTempData);
    addDatasetToChart(outHumidData);
    addDatasetToChart(rainData);
    addDatasetToChart(solarData);
    addDatasetToChart(windChillData);
    addDatasetToChart(heatDDData);
    addDatasetToChart(coolDDData);
    addDatasetToChart(dewPointData);
    addDatasetToChart(heatIndexData);
    addDatasetToChart(thwData);
    addDatasetToChart(thswData);
    addDatasetToChart(inTempData);
    addDatasetToChart(inHumidData);
    addDatasetToChart(issReceptionData);
    addDatasetToChart(pressureData);
    addDatasetToChart(windSpeedData);
    addDatasetToChart(windDirectionData);
    addDatasetToChart(greenhouseTempData);
    addDatasetToChart(etData);
    addDatasetToChart(windRunData);

    // Add the A chart datasets.
    for (AbstractDataPlotter nextItem : chartDataA)
    {
      if (nextItem != null)
      {
        addDataset(A_CHART.getXYPlot(), nextItem);
      }
    }

    // Add the B chart datasets.
    for (AbstractDataPlotter nextItem : chartDataB)
    {
      if (nextItem != null)
      {
        addDataset(B_CHART.getXYPlot(), nextItem);
      }
    }

    // Add the C chart datasets.
    for (AbstractDataPlotter nextItem : chartDataC)
    {
      if (nextItem != null)
      {
        addDataset(C_CHART.getXYPlot(), nextItem);
      }
    }
  }

  /**
   * Internal method used to add the data to the chart.
   *
   * @param selectedData The data to add.
   */
  private void addDatasetToChart(AbstractDataPlotter selectedData)
  {
    if (selectedData.getChart().equalsIgnoreCase(GraphDefs.A_CHART_NAME))
    {
      chartDataA[selectedData.getStreamIndex()] = selectedData;
    }
    else if (selectedData.getChart().equalsIgnoreCase(GraphDefs.B_CHART_NAME))
    {
      chartDataB[selectedData.getStreamIndex()] = selectedData;
    }
    else if (selectedData.getChart().equalsIgnoreCase(GraphDefs.C_CHART_NAME))
    {
      chartDataC[selectedData.getStreamIndex()] = selectedData;
    }
  }

  /**
   * Internal method to add the dataset and renderer based on what datasets currently exist in the chart.
   *
   * @param plot The XYPlot object containing the datasets.
   * @param data The abstract data to add.
   */
  private void addDataset(XYPlot plot, AbstractDataPlotter data)
  {
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

    plot.setDataset(index, data.getStreamDataset());
    plot.setRenderer(index, data.getStreamRenderer());
    plot.setRangeAxis(index, data.getDomainAxis());

    adjustRangeAxisBounds(data);

    ValueAxis axis = plot.getRangeAxis(index);
    axis.setRange(data.getStreamMinY(), data.getStreamMaxY());
    plot.mapDatasetToRangeAxis(index, index);
    plot.setRangeAxisLocation(index, AxisLocation.BOTTOM_OR_LEFT);

    if (!data.isStreamDisplayed())
      removeDataFromChart(data.getChart(), data.getName());
  }

  /**
   * Internal method to adjust the y-axis min and max values.
   *
   * @param data The data to adjust.
   */
  private void adjustRangeAxisBounds(AbstractDataPlotter data)
  {
    if (data.getChart().equalsIgnoreCase(GraphDefs.A_CHART_NAME))
    {
      if (A_CHART != null)
      {
        ValueAxis axis = A_CHART.getXYPlot().getRangeAxis(data.getStreamIndex());
        axis.setRange(data.getStreamMinY(), data.getStreamMaxY());
      }
    }
    else if (data.getChart().equalsIgnoreCase(GraphDefs.B_CHART_NAME))
    {
      if (B_CHART != null)
      {
        ValueAxis axis = B_CHART.getXYPlot().getRangeAxis(data.getStreamIndex());
        axis.setRange(data.getStreamMinY(), data.getStreamMaxY());
      }
    }
    else if (data.getChart().equalsIgnoreCase(GraphDefs.C_CHART_NAME))
    {
      if (C_CHART != null)
      {
        ValueAxis axis = C_CHART.getXYPlot().getRangeAxis(data.getStreamIndex());
        axis.setRange(data.getStreamMinY(), data.getStreamMaxY());
      }
    }
  }

  /**
   * Get the line color for a given data set.
   *
   * @param datasetName The name of the dataset.
   * @return The color for this data.
   */
  public Color getColor(String datasetName)
  {
    AbstractDataPlotter abstractDataPlotter = dataMap.get(datasetName);
    if (abstractDataPlotter != null)
    {
      return abstractDataPlotter.getColor();
    }
    else
    {
      logger.logData("StreamDataThread.getColor: Invalid color for data " + datasetName);
      return Color.BLACK;
    }
  }

  /**
   * Change the color of the dataset.
   *
   * @param datasetName The name of the data to change.
   * @param color The new color.
   */
  public void setColor(String datasetName, Color color)
  {
    AbstractDataPlotter abstractDataPlotter = dataMap.get(datasetName);
    if (abstractDataPlotter != null)
    {
      abstractDataPlotter.setColor(color);
    }
  }

  /**
   * Method to set the dataset size.
   *
   * @param dataname The dataset to modify.
   * @param size The new size.
   */
  public void setDatasetSize(String dataname, int size)
  {
    AbstractDataPlotter abstractDataPlotter = dataMap.get(dataname);
    abstractDataPlotter.changeStreamDataSetSize(size);

    abstractDataPlotter.eraseStreamSeriesData();
    abstractDataPlotter.eraseStreamListData();
  }

  /**
   * Method that runs when this thread is started.  It starts by adding the last 7 days worth of data.
   * It then waits for additional data.
   */
  public void initData(int dataPoints)
  {
    short lastDateTime = dbCommon.getLastDateStamp();
    int endYear = TimeUtil.getYear(lastDateTime);
    int endMonth = TimeUtil.getMonth(lastDateTime);
    int endDay = TimeUtil.getDay(lastDateTime);
    LocalDate localEndTime = LocalDate.of(endYear, endMonth, endDay);

    int days = dataPoints / 288 + 1;
    LocalDate startTime = localEndTime.minusDays(days);
    long startTimeMillis = startTime.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    int startYear = startTime.getYear();
    int startMonth = startTime.getMonthValue();

    // If previous 7 days spans month or year boundaries, then go read the previous month's data file.
    if (startYear != endYear || startMonth != endMonth)
    {
      // Read the previous  months worth of data.
      addData(startTimeMillis, startYear, startMonth);
    }

    // Read the most recent months worth of data.
    addData(startTimeMillis, endYear, endMonth);
  }

  /**
   * Method to set the range axis to correspond to the dataname provided.
   *
   * @param dataname The name of the data.
   */
  public void setRangeAxis(String dataname)
  {
    AbstractDataPlotter data = dataMap.get(dataname);
    String chart = data.getChart();
    XYPlot xyPlot;
    if (chart.equalsIgnoreCase(GraphDefs.A_CHART_NAME))
    {
      xyPlot = A_CHART.getXYPlot();
    }
    else if (chart.equalsIgnoreCase(GraphDefs.B_CHART_NAME))
    {
      xyPlot = B_CHART.getXYPlot();
    }
    else if (chart.equalsIgnoreCase(GraphDefs.C_CHART_NAME))
    {
      xyPlot = C_CHART.getXYPlot();
    }
    else
      return;

    for (int i = 0; i < xyPlot.getRangeAxisCount(); i++)
    {
      if (xyPlot.getRangeAxis(i) != null)
        xyPlot.getRangeAxis(i).setVisible(false);
    }
    xyPlot.getRangeAxis(data.getStreamIndex()).setVisible(true);
  }

  /**
   * Method used to add the series to the dataset based on what is selected.
   *
   * @param chart The chart.
   * @param dataname The data to add.
   */
  public void addDataToChart(String chart, String dataname)
  {
    AbstractDataPlotter abstractDataPlotter = dataMap.get(dataname);
    abstractDataPlotter.setStreamDisplayed(true);

    if (chart.equalsIgnoreCase(GraphDefs.A_CHART_NAME))
    {
      chartDataA[abstractDataPlotter.getStreamIndex()] = abstractDataPlotter;
      abstractDataPlotter.getStreamDataset().addSeries(abstractDataPlotter.getStreamSeries());
    }
    else if (chart.equalsIgnoreCase(GraphDefs.B_CHART_NAME))
    {
      chartDataB[abstractDataPlotter.getStreamIndex()] = abstractDataPlotter;
      abstractDataPlotter.getStreamDataset().addSeries(abstractDataPlotter.getStreamSeries());
    }
    else if (chart.equalsIgnoreCase(GraphDefs.C_CHART_NAME))
    {
      chartDataC[abstractDataPlotter.getStreamIndex()] = abstractDataPlotter;
      abstractDataPlotter.getStreamDataset().addSeries(abstractDataPlotter.getStreamSeries());
    }
  }

  /**
   * Method used to remove the data from the chart.
   *
   * @param selectedData The data to remove.
   */
  public void removeDataFromChart(String chart, String selectedData)
  {
    AbstractDataPlotter abstractDataPlotter = dataMap.get(selectedData);
    abstractDataPlotter.setStreamDisplayed(false);

    if (chart.equalsIgnoreCase(GraphDefs.A_CHART_NAME))
    {
      chartDataA[abstractDataPlotter.getStreamIndex()] = null;
      abstractDataPlotter.getStreamDataset().removeSeries(abstractDataPlotter.getStreamSeries());
    }
    else if (chart.equalsIgnoreCase(GraphDefs.B_CHART_NAME))
    {
      chartDataB[abstractDataPlotter.getStreamIndex()] = null;
      abstractDataPlotter.getStreamDataset().removeSeries(abstractDataPlotter.getStreamSeries());
    }
    else if (chart.equalsIgnoreCase(GraphDefs.C_CHART_NAME))
    {
      chartDataC[abstractDataPlotter.getStreamIndex()] = null;
      abstractDataPlotter.getStreamDataset().removeSeries(abstractDataPlotter.getStreamSeries());
    }
  }

  /**
   * Method to add a new data record to the graph's data set.
   *
   * @param data The DMP data from the console.
   */
  public void addNewData(DmpDataExtended data)
  {
    // Get the time in milliseconds.
    short timeStamp = data.getTimeStamp();
    short dateStamp = data.getDateStamp();
    int hour = TimeUtil.getHour(timeStamp);
    int minute = TimeUtil.getMinute(timeStamp);

    // This is a bit kludged.  To be more correct the day should be incremented and hence the month/year if appropriate.
    if (hour == 24)
    {
      hour = 23;
      minute = 59;
    }
    LocalDateTime dateTime = LocalDateTime.of(TimeUtil.getYear(dateStamp), TimeUtil.getMonth(dateStamp),
                                              TimeUtil.getDay(dateStamp), hour, minute, 0);
    long dateMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    Date date = new Date(dateMillis);

    // Fill in values as long as they are not undefined.
    if (data.getInsideTempNative() != DatabaseCommon.UNDEFINED_SHORT_VALUE)
    {
      boolean addToTrace = !inTempData.getChart().equalsIgnoreCase(GraphDefs.NONE);
      inTempData.addToStreamDataset(addToTrace, date, data.getInsideTemp());
      adjustRangeAxisBounds(inTempData);
    }

    if (data.getOutsideTempNative() != DatabaseCommon.UNDEFINED_SHORT_VALUE)
    {
      boolean addToTrace = !outTempData.getChart().equalsIgnoreCase(GraphDefs.NONE);
      outTempData.addToStreamDataset(addToTrace, date, data.getOutsideTemp());
      adjustRangeAxisBounds(outTempData);
    }

    if (data.getSoilTemp1Native() != DatabaseCommon.UNDEFINED_BYTE_VALUE)
    {
      boolean addToTrace = !greenhouseTempData.getChart().equalsIgnoreCase(GraphDefs.NONE);
      greenhouseTempData.addToStreamDataset(addToTrace, date, data.getSoilTemp1());
      adjustRangeAxisBounds(greenhouseTempData);
    }

    byte insideHumidityNative = data.getInsideHumidity();
    if (insideHumidityNative != DatabaseCommon.UNDEFINED_BYTE_VALUE)
    {
      boolean addToTrace = !inHumidData.getChart().equalsIgnoreCase(GraphDefs.NONE);
      inHumidData.addToStreamDataset(addToTrace, date, insideHumidityNative);
      adjustRangeAxisBounds(inHumidData);
    }

    byte outsideHumidityNative = data.getOutsideHumidity();
    if (outsideHumidityNative != DatabaseCommon.UNDEFINED_BYTE_VALUE)
    {
      boolean addToTrace = !outHumidData.getChart().equalsIgnoreCase(GraphDefs.NONE);
      outHumidData.addToStreamDataset(addToTrace, date, outsideHumidityNative);
      adjustRangeAxisBounds(outHumidData);
    }

    boolean addToRainTrace = !rainData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    rainData.addToStreamDataset(addToRainTrace, date, data.getRainfall());
    adjustRangeAxisBounds(rainData);

    boolean addToIssTrace = !issReceptionData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    issReceptionData.addToStreamDataset(addToIssTrace, date, data.getNumOfWindSamples());
    adjustRangeAxisBounds(issReceptionData);

    boolean addToWindSpeedTrace = !windSpeedData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    windSpeedData.addToStreamDataset(addToWindSpeedTrace, date, data.getAverageWindSpeed());
    adjustRangeAxisBounds(windSpeedData);

    boolean addToSolarTrace = !solarData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    solarData.addToStreamDataset(addToSolarTrace, date, data.getSolarRadiation());
    adjustRangeAxisBounds(solarData);

    if (data.getPressureNative() != DatabaseCommon.UNDEFINED_SHORT_VALUE)
    {
      boolean addToTrace = !pressureData.getChart().equalsIgnoreCase(GraphDefs.NONE);
      pressureData.addToStreamDataset(addToTrace, date, data.getPressure());
      adjustRangeAxisBounds(pressureData);
    }

    // Wind direction can be 0xFF which means no valid data. 0 = N, 1 = NNE, 2 = NE, ... 15 = NNW
    byte windDirectionByte = data.getPrevailingWindDir();
    if (windDirectionByte != -1)
    {
      boolean addToTrace = !windDirectionData.getChart().equalsIgnoreCase(GraphDefs.NONE);
      windDirectionData.addToStreamDataset(addToTrace, date, windDirectionByte);
      adjustRangeAxisBounds(windDirectionData);
    }

    boolean addToWindChillTrace = !windChillData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    windChillData.addToStreamDataset(addToWindChillTrace, date, data.getWindChill());
    adjustRangeAxisBounds(windChillData);

    boolean addToDewPointTrace = !dewPointData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    dewPointData.addToStreamDataset(addToDewPointTrace, date, data.getDewPoint());
    adjustRangeAxisBounds(dewPointData);

    boolean addToHeatIndexTrace = !heatIndexData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    heatIndexData.addToStreamDataset(addToHeatIndexTrace, date, data.getHeatIndex());
    adjustRangeAxisBounds(heatIndexData);

    boolean addToThwTrace = !thwData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    thwData.addToStreamDataset(addToThwTrace, date, data.getThw());
    adjustRangeAxisBounds(thwData);

    boolean addToThswTrace = !thswData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    thswData.addToStreamDataset(addToThswTrace, date, data.getThsw());
    adjustRangeAxisBounds(thswData);

    boolean addToEtTrace = !etData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    etData.addToStreamDataset(addToEtTrace, date, data.getEt());
    adjustRangeAxisBounds(etData);

    boolean addToHeatDDTrace = !heatDDData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    heatDDData.addToStreamDataset(addToHeatDDTrace, date, data.getHeatDD());
    adjustRangeAxisBounds(heatDDData);

    boolean addToCoolDDTrace = !coolDDData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    coolDDData.addToStreamDataset(addToCoolDDTrace, date, data.getCoolDD());
    adjustRangeAxisBounds(coolDDData);

    boolean addToWindRunTrace = !windRunData.getChart().equalsIgnoreCase(GraphDefs.NONE);
    windRunData.addToStreamDataset(addToWindRunTrace, date, data.getTotalWindRun());
    adjustRangeAxisBounds(windRunData);
  }

  /**
   * Method to add the last 7 days worth of data to the trace.  The data is read from the database reader.
   *
   * @param startTimeMillis The start time in milliseconds since epoch.
   * @param year The year data to read.
   * @param month The month data to read.
   */
  private void addData(long startTimeMillis, int year, int month)
  {
    // First read and calculate the evapotranspiration data as it uses the dbReader and would
    // interfere with the code below.
    EvapotransRecord evapotransData = dbReader.getEvapotransData(LocalDateTime.now());

    // Read a months worth of data.
    try
    {
      dbReader.readData(year, month, null);
      dbReader.reset();
    }
    catch (IOException e)
    {
      logger.logData("Stream Data: addData: Unable to read data: " + e.getLocalizedMessage());
      return;
    }

    // Loop through each data record.
    DataFileRecord nextRecord = dbReader.getNextRecord();
    while (nextRecord != null)
    {
      // Throw out the day summary records.
      if (nextRecord instanceof WeatherRecord)
      {
        WeatherRecordExtended data = (WeatherRecordExtended) nextRecord;

        // Get the time in milliseconds.
        long dateMillis = data.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Date date = new Date(dateMillis);

        // Only add data is newer than the start time.
        if (dateMillis > startTimeMillis)
        {
          if (data.getInsideTempNative() != DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            boolean addToTrace = !inTempData.getChart().equalsIgnoreCase(GraphDefs.NONE);
            inTempData.addToStreamDataset(addToTrace, date, data.getInsideTemp());
          }

          if (data.getOutsideTempNative() != DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            boolean addToTrace = !outTempData.getChart().equalsIgnoreCase(GraphDefs.NONE);
            outTempData.addToStreamDataset(addToTrace, date, data.getOutsideTemp());
          }

          if (data.getSoilTemp1Native() != DatabaseCommon.UNDEFINED_BYTE_VALUE)
          {
            boolean addToTrace = !greenhouseTempData.getChart().equalsIgnoreCase(GraphDefs.NONE);
            greenhouseTempData.addToStreamDataset(addToTrace, date, data.getSoilTemp1());
          }

          if (data.getInsideHumidityNative() != DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            boolean addToTrace = !inHumidData.getChart().equalsIgnoreCase(GraphDefs.NONE);
            inHumidData.addToStreamDataset(addToTrace, date, data.getInsideHumidity());
          }

          if (data.getOutsideHumidityNative() != DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            boolean addToTrace = !outHumidData.getChart().equalsIgnoreCase(GraphDefs.NONE);
            outHumidData.addToStreamDataset(addToTrace, date, data.getOutsideHumidity());
          }

          boolean addToRainTrace = !rainData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          rainData.addToStreamDataset(addToRainTrace, date, data.getRainfall());

          boolean addToIssTrace = !issReceptionData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          issReceptionData.addToStreamDataset(addToIssTrace, date, data.getNumOfWindSamples());

          boolean addToWindSpeedTrace = !windSpeedData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          windSpeedData.addToStreamDataset(addToWindSpeedTrace, date, data.getAverageWindSpeed());

          boolean addToSolarTrace = !solarData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          solarData.addToStreamDataset(addToSolarTrace, date, data.getSolarRadiation());

          boolean addToEtTrace = !etData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          etData.addToStreamDataset(addToEtTrace, date, data.getEt());

          if (data.getPressureNative() != DatabaseCommon.UNDEFINED_SHORT_VALUE)
          {
            boolean addToTrace = !pressureData.getChart().equalsIgnoreCase(GraphDefs.NONE);
            pressureData.addToStreamDataset(addToTrace, date, data.getPressure());
          }

          // Wind direction can be 0xFF which means no valid data.
          byte windDirectionByte = data.getWindDirectionNative();
          if (windDirectionByte != -1)
          {
            boolean addToTrace = !windDirectionData.getChart().equalsIgnoreCase(GraphDefs.NONE);
            windDirectionData.addToStreamDataset(addToTrace, date, windDirectionByte);
          }

          float windChill = Calculations.calculateWindChill(data.getOutsideTemp(), data.getAverageWindSpeed());
          boolean addToWindChillTrace = !windChillData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          windChillData.addToStreamDataset(addToWindChillTrace, date, windChill);

          float dewPoint = Calculations.calculateDewPoint(data.getOutsideTemp(), data.getOutsideHumidity());
          boolean addToDewPointTrace = !dewPointData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          dewPointData.addToStreamDataset(addToDewPointTrace, date, dewPoint);

          float heatIndex = Calculations.calculateHeatIndex(data.getOutsideTemp(), data.getOutsideHumidity());
          boolean addToHeatIndexTrace = !heatIndexData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          heatIndexData.addToStreamDataset(addToHeatIndexTrace, date, heatIndex);

          float thwValue = Calculations.calculateTHW(data.getOutsideTemp(), data.getAverageWindSpeed(),
                                                     data.getOutsideHumidity());
          boolean addToThwTrace = !thwData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          thwData.addToStreamDataset(addToThwTrace, date, thwValue);

          float thswValue = Calculations.calculateTHSW(data.getOutsideTemp(), data.getAverageWindSpeed(),
                                                     data.getOutsideHumidity(), data.getSolarRadiation());
          boolean addToThswTrace = !thswData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          thswData.addToStreamDataset(addToThswTrace, date, thswValue);

          boolean addToHeatDDTrace = !heatDDData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          heatDDData.addToStreamDataset(addToHeatDDTrace, date, data.getHeatDD());

          boolean addToCoolDDTrace = !coolDDData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          coolDDData.addToStreamDataset(addToCoolDDTrace, date, data.getCoolDD());

          boolean addToWindRunTrace = !windRunData.getChart().equalsIgnoreCase(GraphDefs.NONE);
          windRunData.addToStreamDataset(addToWindRunTrace, date, data.getWindRunTotal());
        }
      }
      nextRecord = dbReader.getNextRecord();
    }
  }
}
