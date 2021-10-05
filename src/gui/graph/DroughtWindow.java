/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This window graphs the drought data, showing the accumulated
            monthly deviation of rainfall amounts from the historic average.

  Mods:		  10/05/21 Initial Release.
*/
package gui.graph;

import org.jfree.chart.ChartPanel;
import util.ConfigProperties;

import javax.swing.*;
import java.awt.*;

public class DroughtWindow extends JFrame
{
  private final DroughtDataThread droughtDataThread = DroughtDataThread.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();

  public void populateDataSet(int year, int month, int duration)
  {
    droughtDataThread.populateDataset(year, month, duration);
    droughtDataThread.initializeChart();

    // Create the graph panel.
    ChartPanel chartPanel = droughtDataThread.getChart();

    JPanel graphPanel = new JPanel();
    graphPanel.setLayout(new BorderLayout());
    graphPanel.add(chartPanel, BorderLayout.CENTER);

    getContentPane().add(graphPanel);
    setTitle("drought Window");
    setSize(new Dimension(PROPS.getWindowWidth(), PROPS.getWindowHeight()));
    setVisible(true);
  }
}
