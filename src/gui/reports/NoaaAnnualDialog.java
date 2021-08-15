/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class displays the NOAA Annual data dialog box used to
            input the year along with the filename.

  Mods:		  09/01/21 Initial Release.
*/
package gui.reports;

import gui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.Year;

public class NoaaAnnualDialog extends JDialog implements ActionListener
{
  private final JTextField yearField = new JTextField();
  private final JTextField filenameField = new JTextField();

  private static final String OK_STRING = "OK";
  private static final String CANCEL_STRING = "Cancel";

  private final MainWindow parent;

  /**
   * Constructor that draws the initial dialog box.
   */
  public NoaaAnnualDialog(MainWindow parent)
  {
    super(parent, "NOAA Annual Dialog Box", false);
    this.parent = parent;

    JLabel yearLabel = new JLabel("Year:  ", JLabel.RIGHT);
    JLabel filenameLabel = new JLabel("Filename:  ", JLabel.RIGHT);

    yearField.setText(Year.now().toString());
    filenameField.setText("NOAA-" + LocalDate.now().getYear() + ".txt");

    JPanel listPanel = new JPanel();
    listPanel.setLayout(new GridLayout(3, 1));

    JPanel timePanel = new JPanel();
    timePanel.setLayout(new GridLayout(1, 4));
    timePanel.add(yearLabel);
    timePanel.add(yearField);
    listPanel.add(timePanel);

    JPanel filenamePanel = new JPanel();
    filenamePanel.setLayout(new GridLayout(1, 2));
    filenamePanel.add(filenameLabel);
    filenamePanel.add(filenameField);
    listPanel.add(filenamePanel);

    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton(OK_STRING);
    okButton.setActionCommand(OK_STRING);
    okButton.addActionListener(this);

    JButton cancelButton = new JButton(CANCEL_STRING);
    cancelButton.setActionCommand(CANCEL_STRING);
    cancelButton.addActionListener(this);

    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    listPanel.add(buttonPanel);
    this.getContentPane().add(listPanel);

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    this.setFont(myFont);

    setSize(300, 120);
    setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equalsIgnoreCase(OK_STRING))
    {
      parent.displayNoaaAnnualWindow(Integer.valueOf(yearField.getText()), filenameField.getText());
      setVisible(false);
    }
    else if (e.getActionCommand().equalsIgnoreCase(CANCEL_STRING))
    {
      setVisible(false);
    }
  }
}
