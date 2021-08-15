/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the Alert Dialog box.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import forecast.NOAAForecastJSON;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlertDialog extends JDialog implements ActionListener
{
  /**
   * Constructor that draws the initial dialog box.
   */
  public AlertDialog (JFrame parent)
  {
    super(parent, "Alert Dialog Box", true);

    this.getContentPane().setLayout(new BorderLayout());

    NOAAForecastJSON noaaForecast = new NOAAForecastJSON();
    String alertText = noaaForecast.getAlert();

    JPanel aboutPanel = new JPanel();

    JTextArea textArea = new JTextArea();
    textArea.setEditable(false);
    textArea.append(alertText);
    textArea.setCaretPosition(0);

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    aboutPanel.add(scrollPane);
    this.getContentPane().add(scrollPane);

    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton("OK");
    okButton.addActionListener(this);
    buttonPanel.add(okButton);
    this.getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    this.setFont(myFont);
    FontMetrics fm = this.getFontMetrics(this.getFont());

    setSize(600, 400);
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
