/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that converts and plots the wind direction.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph.data;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import java.awt.*;

public class DirectionDataPlotter extends AbstractDataPlotter
{
  /**
   * Method to change the wind direction from numbers to alphabetic directions.
   *
   * @return The axis.
   */
  @Override
  public NumberAxis getDomainAxis()
  {
    return new SymbolAxis(null, new String[]
      {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"});
  }

  @Override
  protected void configureStreamRenderer(float lineWidth, Color color)
  {
    XYLineAndShapeRenderer streamRenderer = getStreamRenderer();
    streamRenderer.setSeriesPaint(0, color);
    streamRenderer.setSeriesLinesVisible(0, false);
    streamRenderer.setSeriesShape(0, new Rectangle(0, -1, 1, 2));
    streamRenderer.setSeriesShapesFilled(0, true);
    streamRenderer.setSeriesShapesVisible(0, true);
  }

  @Override
  protected void configureGraphRenderer(float lineWidth, Color color)
  {
    XYLineAndShapeRenderer graphRenderer = getGraphRenderer();
    graphRenderer.setSeriesPaint(0, color);
    graphRenderer.setSeriesLinesVisible(0, false);
    graphRenderer.setSeriesShape(0, new Rectangle(0, -1, 1, 2));
    graphRenderer.setSeriesShapesFilled(0, true);
    graphRenderer.setSeriesShapesVisible(0, true);
  }
}
