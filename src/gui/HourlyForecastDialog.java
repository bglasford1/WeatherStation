/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the Hourly Forecast Dialog box.

  Mods:		  09/01/21  Initial Release.
            10/11/21  Changed hourly data to tabular form.
*/
package gui;

import forecast.NOAAForecastJSON;
import util.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HourlyForecastDialog extends JDialog implements ActionListener
{
  private final Logger logger = Logger.getInstance();
  private NOAAForecastJSON noaaForecast = new NOAAForecastJSON();
  private JTable table;

  /**
   * Constructor that draws the initial dialog box.
   */
  HourlyForecastDialog (JFrame parent)
  {
    super(parent, "Hourly Forecast Dialog Box", true);
    this.getContentPane().setLayout(new BorderLayout());

    String[][] data = noaaForecast.getHourlyForecasts();
    if (data == null)
    {
      logger.logData("NOAA returned no data...");
      return;
    }

    String column[]={"Date", "Temp", "Forecast", "Wind Speed", "Wind Dir."};
    DefaultTableModel tableModel = new DefaultTableModel(column, 0);
    for (String[] rowData : data)
    {
      tableModel.addRow(rowData);
    }
    table = new JTable(tableModel);

    JPanel dataPanel = new JPanel();
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    dataPanel.add(scrollPane);
    this.getContentPane().add(scrollPane);

    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton("OK");
    okButton.addActionListener(this);
    buttonPanel.add(okButton);
    this.getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    this.setFont(myFont);

    setSize(800, 500);
    setVisible(true);
  }

  /**
   * Call to update the forecast data.
   */
  public void updateForecast()
  {
    String[][] data = noaaForecast.getHourlyForecasts();
    if (data == null)
    {
      logger.logData("NOAA returned no data...");
      return;
    }

    DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
    int rowCount = tableModel.getRowCount();
    for (int i = rowCount - 1; i >= 0; i--)
    {
      tableModel.removeRow(i);
    }

    for (String[] rowData : data)
    {
      tableModel.addRow(rowData);
    }

    setVisible(true);
  }

  /**
   * Method called when the OK button is pressed.
   */
  public void actionPerformed (ActionEvent e)
  {
    setVisible(false);
  }
}
