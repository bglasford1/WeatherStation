/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is used to pick a start date and duration for the graph
            panel.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import gui.graph.GraphDefs;
import gui.graph.GraphWindow;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.Year;
import java.util.Date;
import java.util.Properties;

/**
 * Class that is used to pick a start date and duration for the graph panel.
 */
public class DatePickerDialog extends JDialog implements ActionListener
{
  private final UtilDateModel dateModel = new UtilDateModel();
  private final GraphWindow parent;
  private final JComboBox<String> durationBox;

  private static final String DURATION_STRING = "Duration";
  private static final String OK_STRING = "OK";
  private static final String CANCEL_STRING = "Cancel";

  public DatePickerDialog(GraphWindow parent)
  {
    super(parent, "Date Picker Dialog Box", true);
    this.parent = parent;
    JPanel listPane = new JPanel();
    setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

    JLabel dateLabel = new JLabel("End Date: ");
    listPane.add(dateLabel);

    Properties p = new Properties();
    p.put("text.today", "Today");
    p.put("text.month", "Month");
    p.put("text.year", "Year");
    JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, p);
    JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
    datePicker.setTextEditable(true);
    datePicker.setShowYearButtons(true);
    datePicker.getModel().setYear(Year.now().getValue());
    datePicker.getModel().setMonth(LocalDate.now().getMonthValue() - 1); // Month starts at zero.
    datePicker.getModel().setDay(LocalDate.now().getDayOfMonth());
    datePicker.getModel().setSelected(true);
    listPane.add(datePicker);

    String[] durationStrings =
      { GraphDefs.HALF_DAY_STRING,
        GraphDefs.DAY_STRING,
        GraphDefs.HALF_WEEK_STRING,
        GraphDefs.WEEK_STRING,
        GraphDefs.HALF_MONTH_STRING,
        GraphDefs.MONTH_STRING,
        GraphDefs.YEAR_STRING
      };
    durationBox = new JComboBox<>(durationStrings);
    durationBox.setSelectedItem(GraphDefs.WEEK_STRING);
    durationBox.setActionCommand(DURATION_STRING);
    durationBox.addActionListener(this);
    listPane.add(durationBox);

    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton(OK_STRING);
    okButton.setActionCommand(OK_STRING);
    okButton.addActionListener(this);

    JButton cancelButton = new JButton(CANCEL_STRING);
    cancelButton.setActionCommand(CANCEL_STRING);
    cancelButton.addActionListener(this);

    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    listPane.add(buttonPanel);

    getContentPane().add(listPane);
    setSize(250, 150);
    setVisible(true);
  }

  public Date getDate()
  {
    return dateModel.getValue();
  }

  public String getDuration()
  {
    return (String)durationBox.getSelectedItem();
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equalsIgnoreCase(OK_STRING))
    {
      setVisible(false);
      parent.changeDatasetSize(dateModel.getValue(), durationBox.getSelectedItem().toString());
    }
    else if (e.getActionCommand().equalsIgnoreCase(CANCEL_STRING))
    {
      setVisible(false);
    }
  }
}
