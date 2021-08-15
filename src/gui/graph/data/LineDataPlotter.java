/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that plots the line data on both the stream and graph charts.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph.data;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import java.awt.*;

public class LineDataPlotter extends AbstractDataPlotter
{
  @Override
  protected void configureStreamRenderer(float lineWidth, Color color)
  {
    XYLineAndShapeRenderer streamRenderer = getStreamRenderer();
    streamRenderer.setSeriesPaint(0, color);
    streamRenderer.setSeriesStroke(0, new BasicStroke(lineWidth));
    streamRenderer.setSeriesShapesVisible(0, false);
  }

  @Override
  protected void configureGraphRenderer(float lineWidth, Color color)
  {
    XYLineAndShapeRenderer graphRenderer = getGraphRenderer();
    graphRenderer.setSeriesPaint(0, color);
    graphRenderer.setSeriesStroke(0, new BasicStroke(lineWidth));
    graphRenderer.setSeriesShapesVisible(0, false);
  }
}
