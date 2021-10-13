/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class draws a wind rose based on the data interval defined.
            There are 16 wind directions plotted that are defined by the
            WindDirection enumeration.  The length of each wind direction
            slice is the percentage of time the wind was blowing in that
            direction.  Each wind direction slice is divided into bins of
            wind intensity defined by the WindSpeedLevel enumeration.  If
            the wind is not blowing at all there is a center circle defining
            the percentage of time the wind was calm.

  Mods:		  10/13/21  Initial Release.
*/
package gui.windrose;

import data.dbrecord.WindDirection;
import data.dbrecord.WindRoseData;
import data.dbrecord.WindSlice;
import gui.graph.GraphDefs;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;
import util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class WindRosePlot extends JFrame implements ActionListener
{
  private WindRoseData data = new WindRoseData();
  private XYSeriesCollection dataset = new XYSeriesCollection();
  private DefaultPolarItemRenderer renderer;
  private final Color binColor[] = {Color.blue, Color.cyan, Color.green, Color.yellow, Color.red, Color.black};
  private final Logger logger = Logger.getInstance();

  /**
   * Constructor that creates the wind rose chart and displays it in the JFrame window.
   */
  public WindRosePlot()
  {
    // Create a menu bar item to modify the interval
    JMenu intervalMenu = new JMenu("Interval");
    ButtonGroup intervalGroup = new ButtonGroup();
    JRadioButtonMenuItem hourItem = new JRadioButtonMenuItem(GraphDefs.HOUR_STRING);
    hourItem.setActionCommand(GraphDefs.HOUR_STRING);
    hourItem.addActionListener(this);
    intervalGroup.add(hourItem);
    intervalMenu.add(hourItem);

    JRadioButtonMenuItem halfDayItem = new JRadioButtonMenuItem(GraphDefs.HALF_DAY_STRING);
    halfDayItem.setActionCommand(GraphDefs.HALF_DAY_STRING);
    halfDayItem.addActionListener(this);
    intervalGroup.add(halfDayItem);
    intervalMenu.add(halfDayItem);

    JRadioButtonMenuItem dayItem = new JRadioButtonMenuItem(GraphDefs.DAY_STRING);
    dayItem.setActionCommand(GraphDefs.DAY_STRING);
    dayItem.setSelected(true);
    dayItem.addActionListener(this);
    intervalGroup.add(dayItem);
    intervalMenu.add(dayItem);

    JRadioButtonMenuItem halfWeekItem = new JRadioButtonMenuItem(GraphDefs.HALF_WEEK_STRING);
    halfWeekItem.setActionCommand(GraphDefs.HALF_WEEK_STRING);
    halfWeekItem.addActionListener(this);
    intervalGroup.add(halfWeekItem);
    intervalMenu.add(halfWeekItem);

    JRadioButtonMenuItem weekItem = new JRadioButtonMenuItem(GraphDefs.WEEK_STRING);
    weekItem.setActionCommand(GraphDefs.WEEK_STRING);
    weekItem.addActionListener(this);
    intervalGroup.add(weekItem);
    intervalMenu.add(weekItem);

    JMenuBar myMenuBar = new JMenuBar();
    myMenuBar.add(intervalMenu);
    this.setJMenuBar(myMenuBar);

    // Create chart.
    JFreeChart chart = createChart(dataset);

    // Create dataset and add to chart.
    data.generateData(GraphDefs.DAY_STRING);
    populateDataset();

    PolarPlot plot = (PolarPlot)chart.getPlot();
    plot.setDataset(dataset);

    ChartPanel panel = new ChartPanel(chart);
    panel.setMouseZoomable(false);
    this.setTitle("Wind Rose Plot - Day");
    this.setContentPane(panel);
    this.setSize(600, 600);
    this.setVisible(true);
  }

  /**
   * Create the Polar chart, overriding the method that draws the tick labels around the graph so that it
   * looks like a compass.
   *
   * @param dataset The dataset to add to the chart.
   * @return The JFreeChart object.
   */
  private JFreeChart createChart (XYDataset dataset)
  {
    ValueAxis radiusAxis = new NumberAxis ();
    radiusAxis.setTickLabelsVisible (false);

    renderer = new DefaultPolarItemRenderer();
    renderer.setSeriesFilled (0, false);

    // Override the refreshAngleTicks so as to draw the compass points.
    PolarPlot plot = new PolarPlot (dataset, radiusAxis, renderer)
    {
      @Override
      protected java.util.List refreshAngleTicks ()
      {
        java.util.List ticks = new ArrayList();

        ticks.add (new NumberTick (0, "N", TextAnchor.CENTER, TextAnchor.TOP_LEFT, 0));
        ticks.add (new NumberTick (22.5, "NNE", TextAnchor.TOP_LEFT, TextAnchor.TOP_RIGHT, 0));
        ticks.add (new NumberTick (45, "NE", TextAnchor.TOP_LEFT, TextAnchor.TOP_RIGHT, 0));
        ticks.add (new NumberTick (67.5, "ENE", TextAnchor.TOP_LEFT, TextAnchor.TOP_RIGHT, 0));
        ticks.add (new NumberTick (90, "E", TextAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, 0));
        ticks.add (new NumberTick (112.5, "ESE", TextAnchor.TOP_LEFT, TextAnchor.TOP_RIGHT, 0));
        ticks.add (new NumberTick (135, "SE", TextAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, 0));
        ticks.add (new NumberTick (157.5, "SSE", TextAnchor.TOP_LEFT, TextAnchor.TOP_RIGHT, 0));
        ticks.add (new NumberTick (180, "S", TextAnchor.CENTER, TextAnchor.TOP_LEFT, 0));
        ticks.add (new NumberTick (202.5, "SSW", TextAnchor.TOP_RIGHT, TextAnchor.TOP_RIGHT, 0));
        ticks.add (new NumberTick (225, "SW", TextAnchor.TOP_RIGHT, TextAnchor.TOP_LEFT, 0));
        ticks.add (new NumberTick (247.5, "WSW", TextAnchor.TOP_RIGHT, TextAnchor.TOP_RIGHT, 0));
        ticks.add (new NumberTick (270, "W", TextAnchor.TOP_RIGHT, TextAnchor.TOP_LEFT, 0));
        ticks.add (new NumberTick (294.5, "WNW", TextAnchor.TOP_RIGHT, TextAnchor.TOP_RIGHT, 0));
        ticks.add (new NumberTick (315, "NW", TextAnchor.TOP_RIGHT, TextAnchor.TOP_LEFT, 0));
        ticks.add (new NumberTick (337.5, "NNW", TextAnchor.TOP_RIGHT, TextAnchor.TOP_RIGHT, 0));

        return ticks;
      }
    };

    plot.setOutlinePaint (new Color (0, 0, 0, 0));
    plot.setBackgroundPaint (Color.white);
    plot.setRadiusGridlinePaint (Color.gray);
    plot.setRadiusGridlinesVisible (true);
    plot.setAngleGridlinesVisible (true);
    plot.setAngleLabelsVisible (true);
    plot.setOutlineVisible (true);
    plot.setAngleGridlinePaint (Color.BLACK);
    plot.setRenderer(renderer);
    plot.setFixedLegendItems(getLegendItems());
    plot.addCornerTextItem("Calm Percent: " + data.getCalmSlicePercentage() + "%");

    JFreeChart chart = new JFreeChart ("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    chart.setBackgroundPaint (Color.white);
    chart.setBorderVisible (false);

    NumberAxis rangeAxis = (NumberAxis) plot.getAxis ();
    rangeAxis.setTickUnit(new NumberTickUnit(2.0));
    rangeAxis.setTickLabelsVisible (true);

    return chart;
  }

  /**
   * Method to update the data using a newly selected interval.
   *
   * @param interval The new interval.
   */
  private void updateData(String interval)
  {
    data.generateData(interval);
    populateDataset();
    this.setTitle("Wind Rose Plot - " + interval);
  }

  /**
   * TMethod to load the wind rose data into a jFreeChart dataset.  Each slice bin of data has its own series with
   * the colors assigned by level.
   */
  private void populateDataset()
  {
    // Empty the dataset before adding more.
    int size = dataset.getSeriesCount();
    for (int i = 0; i < size; i++)
    {
      dataset.removeSeries(0);
    }

    int numSlices = data.getNumOfSlices();
    double arcLength = (360.0 / numSlices) / 2.0;
    double halfArcLength = arcLength / 2.0;

    // Loop through each wind direction slice.
    for (int i = 0; i < numSlices; i++)
    {
      WindSlice slice = data.getSlice(i);

      // The length of the pie slice is determined by the percentage of the wind that was blowing within
      // the slice. Calm winds entries are ignored.
      double lastLength = 0.0;
      WindDirection direction = WindDirection.valueOf(i);
      if (direction == null)
      {
        logger.logData("WindRosePlot: null wind direction.");
        continue;
      }

      // Each slice's bins are then processed. Each bin is represented by a color and the length
      // of each slice segment is determined by the percentage of time the wind was blowing within the bin.
      for (WindSpeedLevel nextLevel : WindSpeedLevel.values())
      {
        XYSeries series = new XYSeries(direction.toString() + nextLevel.value(), false);

        double directionValue = direction.direction();
        double binPercentage = slice.getBinPercentage(nextLevel.value());

        double length = lastLength;

        if (binPercentage != 0.0)
        {
          length += binPercentage;
        }

        double left = directionValue - halfArcLength;
        double right = directionValue + halfArcLength;

        series.add(left, lastLength);
        series.add(right, lastLength);
        series.add(right, length);
        series.add(left, length);
        dataset.addSeries(series);

        renderer.setSeriesPaint(dataset.indexOf(series), binColor[nextLevel.value()]);
        renderer.setSeriesFilled(dataset.indexOf(series), true);
        renderer.setShapesVisible(false);

        lastLength = length;
      }
    }
  }

  /**
   * Method that generates legend values for the wind bin levels.
   */
  private LegendItemCollection getLegendItems()
  {
    LegendItemCollection items = new LegendItemCollection();
    LegendItem defaultItem = new LegendItem("Dummy");

    if (data != null)
    {
      for (WindSpeedLevel nextLevel : WindSpeedLevel.values())
      {
        LegendItem item = new LegendItem(nextLevel.label(), "Speed Bin", "", "",
                                         defaultItem.getShape(), binColor[nextLevel.value()], defaultItem.getOutlineStroke(),
                                         defaultItem.getOutlinePaint());

        items.add(item);
      }
    }
    return items;
  }

  /**
   * Method to handle the change in interval values from the main menu.
   *
   * @param e The action event that contains the action.
   */
  @Override
  public void actionPerformed(ActionEvent e)
  {
    String action = e.getActionCommand();

    if (action.equalsIgnoreCase(GraphDefs.HOUR_STRING))
    {
      updateData(GraphDefs.HOUR_STRING);
    }
    else if (action.equalsIgnoreCase(GraphDefs.HALF_DAY_STRING))
    {
      updateData(GraphDefs.HALF_DAY_STRING);
    }
    else if (action.equalsIgnoreCase(GraphDefs.DAY_STRING))
    {
      updateData(GraphDefs.DAY_STRING);
    }
    else if (action.equalsIgnoreCase(GraphDefs.HALF_WEEK_STRING))
    {
      updateData(GraphDefs.HALF_WEEK_STRING);
    }
    else if (action.equalsIgnoreCase(GraphDefs.WEEK_STRING))
    {
      updateData(GraphDefs.WEEK_STRING);
    }
  }
}