/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This window graphs the drought data, showing the accumulated
            monthly deviation of rainfall amounts from the historic average.

  Mods:		  10/05/21  Initial Release.
            10/07/21  Added progress bar.
*/
package gui.graph;

import org.jfree.chart.ChartPanel;
import util.ConfigProperties;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DroughtWindow extends JPanel implements PropertyChangeListener
{
  private DroughtDataThread droughtDataThread;
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private ProgressMonitor progressMonitor;

  /**
   * The constructor that launches the progress monitor window and starts the background task.
   *
   * @param startYear  The starting year value.
   * @param startMonth The starting month value.
   * @param duration   The duration of the data to display.
   */
  public DroughtWindow(int startYear, int startMonth, int duration)
  {
    progressMonitor = new ProgressMonitor(this, "Analysing Data...",
                                                          "", 0, 100);
    progressMonitor.setProgress(0);

    droughtDataThread = new DroughtDataThread(progressMonitor, startYear, startMonth, duration);
    droughtDataThread.addPropertyChangeListener(this);
    droughtDataThread.execute();
  }

  /**
   * Internal method to create the resulting window once the data is analyzed.
   */
  private void createWindow()
  {
    JFrame frame = new JFrame("Drought Analysis");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Create the graph panel.
    ChartPanel chartPanel = droughtDataThread.createChart();

    JPanel graphPanel = new JPanel();
    graphPanel.setLayout(new BorderLayout());
    graphPanel.add(chartPanel, BorderLayout.CENTER);

    chartPanel.setOpaque(true);
    frame.setContentPane(chartPanel);
    frame.setSize(new Dimension(PROPS.getWindowWidth(), PROPS.getWindowHeight()));
    frame.setVisible(true);
  }

  /**
   * Method invoked when the background task's progress property changes.
   */
  public void propertyChange(PropertyChangeEvent evt)
  {
    if (evt.getPropertyName().equalsIgnoreCase("progress"))
    {
      int progress = (Integer) evt.getNewValue();
      progressMonitor.setProgress(progress);
      String message =
        String.format("Completed %d%%.\n", progress);
      progressMonitor.setNote(message);
      if (progressMonitor.isCanceled() || droughtDataThread.isDone())
      {
        Toolkit.getDefaultToolkit().beep();
        if (progressMonitor.isCanceled())
        {
          droughtDataThread.cancel(true);
        }
        else
        {
          // Display graph.
          createWindow();
        }
      }
    }
  }
}
