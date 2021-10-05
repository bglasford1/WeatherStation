/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is used to input the month and year values to define
            the start of the rain data to analyze for drought.

  Mods:		  10/05/21 Initial Release.
*/
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.Year;

public class DroughtInputDialog extends JDialog implements ActionListener
{
  private static final String OK_STRING = "OK";
  private static final String CANCEL_STRING = "Cancel";

  private final JTextField monthField = new JTextField();
  private final JTextField yearField = new JTextField();
  private final JTextField durationField = new JTextField();

  private final MainWindow parent;

  /**
   * Constructor that draws the initial dialog box.
   */
  DroughtInputDialog(MainWindow parent)
  {
    super(parent, "Date Input Dialog Box", false);
    this.parent = parent;

    JLabel monthLabel = new JLabel("Month:  ", JLabel.CENTER);
    JLabel yearLabel  = new JLabel("Year:  ", JLabel.CENTER);
    JLabel durationLabel  = new JLabel("Duration:  ", JLabel.CENTER);

    monthField.setText("1");
    yearField.setText(Year.now().toString());
    durationField.setText("12");

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(3, 2));
    inputPanel.add(monthLabel);
    inputPanel.add(monthField);
    inputPanel.add(yearLabel);
    inputPanel.add(yearField);
    inputPanel.add(durationLabel);
    inputPanel.add(durationField);
    this.getContentPane().add(inputPanel);

    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton(OK_STRING);
    okButton.setActionCommand(OK_STRING);
    okButton.addActionListener(this);

    JButton cancelButton = new JButton(CANCEL_STRING);
    cancelButton.setActionCommand(CANCEL_STRING);
    cancelButton.addActionListener(this);

    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    this.getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    this.setFont(myFont);

    setSize(300, 160);
    setVisible(true);
  }

  /**
   * Method called when the OK button is pressed.
   */
  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equalsIgnoreCase(OK_STRING))
    {
      parent.processDroughtInput();
      setVisible(false);
    }
    else if (e.getActionCommand().equalsIgnoreCase(CANCEL_STRING))
    {
      setVisible(false);
    }
  }

  public int getMonth()
  {
    String monthFieldText = monthField.getText();
    if (monthFieldText.equalsIgnoreCase(""))
      return 0;
    else
      return Integer.valueOf(monthField.getText());
  }

  public int getYear()
  {
    String yearFieldText = yearField.getText();
    if (yearFieldText.equalsIgnoreCase(""))
      return 0;
    else
      return Integer.valueOf(yearField.getText());
  }

  public int getDuration()
  {
    String durationFieldText = durationField.getText();
    if (durationFieldText.equalsIgnoreCase(""))
      return 0;
    else
      return Integer.valueOf(durationField.getText());
  }
}
