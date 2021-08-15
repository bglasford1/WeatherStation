/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for displaying the current date and time
            in a dialog box.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimeDialog extends JDialog implements ActionListener
{
  private final JLabel dateLabel;
  private final JLabel timeLabel;

  /**
   * Constructor that draws the initial dialog box.
   */
  public TimeDialog( JFrame parent, String datestamp, String timestamp)
  {
    super(parent, "Time Dialog Box", true);

    JPanel diagsPanel = new JPanel();
    diagsPanel.setLayout(new GridLayout(2, 1));

    dateLabel = new JLabel("Date:  " + datestamp, JLabel.CENTER);
    timeLabel = new JLabel("Time:  " + timestamp, JLabel.CENTER);

    diagsPanel.add(dateLabel);
    diagsPanel.add(timeLabel);
    this.getContentPane().add(diagsPanel);

    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton("OK");
    okButton.addActionListener(this);
    buttonPanel.add(okButton);
    this.getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    this.setFont(myFont);

    setSize(150, 120);
    setVisible(true);
  }

  /**
   * Method called when the OK button is pressed.
   */
  public void actionPerformed (ActionEvent e)
  {
    setVisible(false);
  }

  /**
   * Method called when new data has been received.
   */
  public void setNewValues( String dateString, String timeString)
  {
    dateLabel.setText("Date:  " + dateString);
    timeLabel.setText("Time:  " + timeString);
  }
}
