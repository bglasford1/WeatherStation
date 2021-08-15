/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class displays the sunrise/sunset dialog box used to
            input the start and end date.

  Mods:		  09/01/21 Initial Release.
*/
package gui.reports;

import gui.DateLabelFormatter;
import gui.MainWindow;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.Properties;

public class SunriseSunsetDialog extends JDialog implements ActionListener
{
  private static final String OK_STRING = "OK";
  private static final String CANCEL_STRING = "Cancel";

  private final UtilDateModel startDateModel = new UtilDateModel();
  private final UtilDateModel endDateModel = new UtilDateModel();

  private final MainWindow parent;

  /**
   * Constructor that draws the initial dialog box.
   */
  public SunriseSunsetDialog(MainWindow parent)
  {
    super(parent, "Sunrise/Sunset Times Dialog Box", false);
    this.parent = parent;

    JLabel startLabel = new JLabel("Start Date:  ", JLabel.RIGHT);
    JLabel endLabel = new JLabel("End Date:  ", JLabel.RIGHT);

    JTextField startField = new JTextField();
    JTextField endField = new JTextField();
    startField.setText(Integer.toString(LocalDate.now().getMonthValue()));
    endField.setText(Year.now().toString());

    JPanel listPane = new JPanel();

    Properties p = new Properties();
    p.put("text.today", "Today");
    p.put("text.month", "Month");
    p.put("text.year", "Year");

    JDatePanelImpl startDatePickerPanel = new JDatePanelImpl(startDateModel, p);
    JDatePickerImpl startDatePicker = new JDatePickerImpl(startDatePickerPanel, new DateLabelFormatter());
    startDatePicker.setTextEditable(true);
    startDatePicker.setShowYearButtons(true);
    startDatePicker.getModel().setYear(Year.now().getValue());
    startDatePicker.getModel().setMonth(LocalDate.now().getMonthValue() - 1); // Month starts at zero.
    startDatePicker.getModel().setDay(LocalDate.now().getDayOfMonth());
    startDatePicker.getModel().setSelected(true);

    JPanel startDatePanel = new JPanel();
    startDatePanel.setLayout(new GridLayout(1, 2));
    startDatePanel.add(startLabel);
    startDatePanel.add(startDatePicker);
    listPane.add(startDatePanel);

    JDatePanelImpl endDatePickerPanel = new JDatePanelImpl(endDateModel, p);
    JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePickerPanel, new DateLabelFormatter());
    endDatePicker.setTextEditable(true);
    endDatePicker.setShowYearButtons(true);
    endDatePicker.getModel().setYear(Year.now().getValue());
    endDatePicker.getModel().setMonth(LocalDate.now().getMonthValue() - 1); // Month starts at zero.
    endDatePicker.getModel().setDay(LocalDate.now().getDayOfMonth());
    endDatePicker.getModel().setSelected(true);

    JPanel endDatePanel = new JPanel();
    endDatePanel.setLayout(new GridLayout(1, 2));
    endDatePanel.add(endLabel);
    endDatePanel.add(endDatePicker);
    listPane.add(endDatePanel);

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
    this.getContentPane().add(listPane);

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    this.setFont(myFont);

    setSize(400, 150);
    setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equalsIgnoreCase(OK_STRING))
    {
      LocalDate startDate = startDateModel.getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      LocalDate endDate = endDateModel.getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      parent.displaySunriseSunsetWindow(startDate, endDate);
      setVisible(false);
    }
    else if (e.getActionCommand().equalsIgnoreCase(CANCEL_STRING))
    {
      setVisible(false);
    }
  }
}
