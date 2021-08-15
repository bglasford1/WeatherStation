/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Data classes that implement this class encapsulate the series data
            for graphing as well as the characteristics of the series such as
            color and stroke of the line on the graph for this data set.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph.data;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import util.ConfigProperties;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class AbstractDataPlotter
{
  private String name;
  private Color color;
  private String chart;
  private int streamIndex;
  private int graphIndex;
  private boolean streamDisplayed;
  private boolean graphDisplayed;
  private Float minFixed;
  private Float minDelta;
  private Float maxFixed;
  private Float maxDelta;

  // Items used for the stream chart
  private TimeSeriesCollection streamDataset;
  private final XYLineAndShapeRenderer streamRenderer = new XYLineAndShapeRenderer();
  private TimeSeries streamSeries;

  // Items used for the graph chart
  private TimeSeriesCollection graphDataset;
  private final XYLineAndShapeRenderer graphRenderer = new XYLineAndShapeRenderer();
  private TimeSeries graphSeries;

  // Save the original points.  This is due to the scaling that goes on in the series data.
  private final ArrayList<TimeSeriesDataItem> streamList = new ArrayList<>();
  private final ArrayList<TimeSeriesDataItem> graphList = new ArrayList<>();

  static final ConfigProperties PROPS = ConfigProperties.instance();

  /**
   * Method that must be called to initialize the data object.  Various configuration items are passed in.
   *
   * @param name      The name of the streamDataset.
   * @param dataSize  The size of the streamDataset.
   * @param lineWidth The width of the line.
   * @param color     The color of the line.
   * @param chart     The chart the streamDataset is assigned to.
   * @param streamIndex The stream chart's data index.
   * @param graphIndex The graph chart's data index.
   * @param streamDisplayed Whether or not the stream data is displayed.
   * @param graphDisplayed  Whether or not the graph data is displayed.
   * @param minFixed  The minimum y value if set.
   * @param minDelta  The minimum y value offset if set.
   * @param maxFixed  The maximum y value if set.
   * @param maxDelta  The maximum y value offset if set.
   */
  void initialize(String name, int dataSize, float lineWidth, Color color, String chart, int streamIndex,
                  int graphIndex, boolean streamDisplayed, boolean graphDisplayed,
                  Float minFixed, Float minDelta, Float maxFixed, Float maxDelta)
  {
    this.name = name;
    this.color = color;
    this.chart = chart;
    this.streamIndex = streamIndex;
    this.graphIndex = graphIndex;
    this.streamDisplayed = streamDisplayed;
    this.graphDisplayed = graphDisplayed;
    this.minFixed = minFixed;
    this.minDelta = minDelta;
    this.maxFixed = maxFixed;
    this.maxDelta = maxDelta;

    streamSeries = new TimeSeries(name);
    streamSeries.setMaximumItemCount(dataSize);
    streamDataset = new TimeSeriesCollection(streamSeries);

    configureStreamRenderer(lineWidth, color);

    graphSeries = new TimeSeries(name);
    graphSeries.setMaximumItemCount(dataSize);
    graphDataset = new TimeSeriesCollection(graphSeries);

    configureGraphRenderer(lineWidth, color);
  }

  protected void configureStreamRenderer(float lineWidth, Color color) { }

  protected void configureGraphRenderer(float lineWidth, Color color) { }

  public String getName()
  {
    return name;
  }

  public void setColor(Color color)
  {
    this.color = color;
    streamRenderer.setSeriesPaint(0, color);
    graphRenderer.setSeriesPaint(0, color);
  }

  public Color getColor()
  {
    return color;
  }

  public String getChart()
  {
    return chart;
  }

  public void setChart(String chart)
  {
    this.chart = chart;
  }

  public int getStreamIndex()
  {
    return streamIndex;
  }

  public int getGraphIndex()
  {
    return graphIndex;
  }

  public boolean isStreamDisplayed()
  {
    return streamDisplayed;
  }

  public void setStreamDisplayed(boolean streamDisplayed)
  {
    this.streamDisplayed = streamDisplayed;
  }

  public boolean isGraphDisplayed()
  {
    return graphDisplayed;
  }

  public void setGraphDisplayed(boolean graphDisplayed)
  {
    this.graphDisplayed = graphDisplayed;
  }

  public TimeSeries getStreamSeries()
  {
    return streamSeries;
  }

  public TimeSeriesCollection getStreamDataset()
  {
    return streamDataset;
  }

  public XYLineAndShapeRenderer getStreamRenderer()
  {
    return streamRenderer;
  }

  public TimeSeriesCollection getGraphDataset()
  {
    return graphDataset;
  }

  public XYLineAndShapeRenderer getGraphRenderer()
  {
    return graphRenderer;
  }

  public TimeSeries getGraphSeries()
  {
    return graphSeries;
  }

  /**
   * Method to return a new number axis.
   *
   * @return The axis.
   */
  public NumberAxis getDomainAxis()
  {
    return new NumberAxis();
  }

  /**
   * Method to change the streamDataset being streamDisplayed.
   *
   * @param datasize The side of the data to display.
   */
  public void changeStreamDataSetSize(int datasize)
  {
    streamSeries.setMaximumItemCount(datasize);
  }

  /**
   * Method to add a point to the streamDataset.  The data is kept in a JChart2D series object and a linked streamList.
   * The linked streamList contains the original data.  The series data is scaled for display.
   *
   * @param addToTrace Whether or not to add to the series.
   * @param date The date in milliseconds since epoch.
   * @param datum  The data at this time.
   */
  public void addToStreamDataset(boolean addToTrace, Date date, float datum)
  {
    Millisecond time = new Millisecond(date);
    TimeSeriesDataItem newPoint = new TimeSeriesDataItem(time, datum);
    streamList.add(newPoint);
    if (addToTrace)
    {
      streamSeries.addOrUpdate(newPoint);
    }
  }

  /**
   * Method to get the minimum Y value of the current data set.
   *
   * @return The minimum Y value.
   */
  public double getStreamMinY()
  {
    if (minFixed != null)
    {
      if (streamSeries.getMinY() < minFixed)
      {
        return streamSeries.getMinY();
      }
      else
      {
        return minFixed;
      }
    }
    else if (minDelta != null)
      return streamSeries.getMinY() - minDelta;
    else
      return streamSeries.getMinY();
  }

  /**
   * Method to get the maximum Y value of the current data set.
   *
   * @return The maximum Y value.
   */
  public double getStreamMaxY()
  {
    if (maxFixed != null)
    {
      if (streamSeries.getMaxY() > maxFixed)
      {
        return streamSeries.getMaxY();
      }
      else
      {
        return maxFixed;
      }
    }
    else if (maxDelta != null)
      return streamSeries.getMaxY() + maxDelta;
    else
      return streamSeries.getMaxY();
  }

  public void eraseStreamSeriesData()
  {
    streamSeries.clear();
  }

  public void eraseStreamListData()
  {
    streamList.clear();
  }

  /**
   * Method to change the graphDataset being displayed.
   *
   * @param datasize The side of the data to display.
   */
  public void changeGraphDataSetSize(int datasize)
  {
    graphSeries.setMaximumItemCount(datasize);
  }

  /**
   * Method to add a point to the graphDataset.  The data is kept in a JChart2D series object and a linked graphList.
   * The linked graphList contains the original data.  The series data is scaled for display.
   *
   * @param addToTrace Whether or not to add to the series.
   * @param date The date in milliseconds since epoch.
   * @param datum  The data at this time.
   */
  public void addToGraphDataset(boolean addToTrace, Date date, float datum)
  {
    Millisecond time = new Millisecond(date);
    TimeSeriesDataItem newPoint = new TimeSeriesDataItem(time, datum);
    graphList.add(newPoint);
    if (addToTrace)
    {
      graphSeries.addOrUpdate(newPoint);
    }
  }

  /**
   * Method to get the minimum Y value of the current data set.
   *
   * @return The minimum Y value.
   */
  public double getGraphMinY()
  {
    if (minFixed != null)
    {
      if (graphSeries.getMinY() < minFixed)
      {
        return graphSeries.getMinY();
      }
      else
      {
        return minFixed;
      }
    }
    else if (minDelta != null)
      return graphSeries.getMinY() - minDelta;
    else
      return graphSeries.getMinY();
  }

  /**
   * Method to get the maximum Y value of the current data set.
   *
   * @return The maximum Y value.
   */
  public double getGraphMaxY()
  {
    if (maxFixed != null)
    {
      if (graphSeries.getMaxY() > maxFixed)
      {
        return graphSeries.getMaxY();
      }
      else
      {
        return maxFixed;
      }
    }
    else if (maxDelta != null)
      return graphSeries.getMaxY() + maxDelta;
    else
      return graphSeries.getMaxY();
  }

  public void eraseGraphSeriesData()
  {
    graphSeries.clear();
  }

  public void eraseGraphListData()
  {
    graphList.clear();
  }
}
