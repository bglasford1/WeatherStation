/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class displays the snow input dialog box.

  Mods:		  09/01/21 Initial Release.
*/
package gui.snow;

import data.dbrecord.SnowRecord;
import dbif.SnowDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SnowDataInputDialog extends JDialog implements ActionListener
{
  private final JTextField dayField = new JTextField();
  private final JTextField monthField = new JTextField();
  private final JTextField yearField = new JTextField();
  private final JTextField valueField = new JTextField();

  private static final String ADD_STRING = "Add";
  private static final String CANCEL_STRING = "Cancel";

  private final SnowDatabase snowDatabase = SnowDatabase.getInstance();

  /**
   * Constructor that draws the initial dialog box.
   *
   * @param record The initial data.
   */
  SnowDataInputDialog(JFrame frame, SnowRecord record)
  {
    super(frame, "Snow Input Dialog Box", false);

    JLabel dayLabel   = new JLabel("Day:  ", JLabel.CENTER);
    JLabel monthLabel = new JLabel("Month:  ", JLabel.CENTER);
    JLabel yearLabel  = new JLabel("Year:  ", JLabel.CENTER);
    JLabel valueLabel = new JLabel("Value: ", JLabel.CENTER);

    setData(record);

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(4, 2));
    inputPanel.add(dayLabel);
    inputPanel.add(dayField);
    inputPanel.add(monthLabel);
    inputPanel.add(monthField);
    inputPanel.add(yearLabel);
    inputPanel.add(yearField);
    inputPanel.add(valueLabel);
    inputPanel.add(valueField);
    this.getContentPane().add(inputPanel);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1,2));

    JButton addButton = new JButton(ADD_STRING);
    addButton.addActionListener(this);
    buttonPanel.add(addButton);

    JButton cancelButton = new JButton(CANCEL_STRING);
    cancelButton.addActionListener(this);
    buttonPanel.add(cancelButton);

    this.getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    this.setFont(myFont);

    setSize(300, 160);
    setVisible(true);
  }

  /**
   * Method used to set text field values.
   *
   * @param record The record values.
   */
  public void setData(SnowRecord record)
  {
    yearField.setText(Integer.toString(record.getYear()));
    monthField.setText(Integer.toString(record.getMonth()));
    dayField.setText(Integer.toString(record.getDay()));
    valueField.setText(Float.toString(record.getAmount()));
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    String action = e.getActionCommand();
    if (action.equalsIgnoreCase(ADD_STRING))
    {
      try
      {
        SnowRecord snowRecord = new SnowRecord();
        snowRecord.setYear(Integer.parseInt(yearField.getText()));
        snowRecord.setMonth(Integer.parseInt(monthField.getText()));
        snowRecord.setDay(Integer.parseInt(dayField.getText()));
        snowRecord.setAmount(Float.parseFloat(valueField.getText()));

        snowDatabase.insertSnowRecord(snowRecord);
        setVisible(false);
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog(this, "Fields cannot be blank.");
      }
    }
    else if (action.equalsIgnoreCase(CANCEL_STRING))
    {
      setVisible(false);
    }
  }
}
