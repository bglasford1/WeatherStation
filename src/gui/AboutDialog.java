/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class creates the about dialog box.  This box simply displays
            what this application is all about.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutDialog extends JDialog implements ActionListener
{
  private static final String lineOne   = "Weather Station Program V1.0";
  private static final String lineTwo   = "Developed by Bill Glasford";
  private static final String lineThree = "For the Davis Vantage Pro 2";

  /**
   * Constructor that draws the initial dialog box.
   */
  public AboutDialog (JFrame parent)
  {
    super(parent, "About Dialog Box", true);

    JPanel aboutPanel = new JPanel();
    aboutPanel.setLayout(new GridLayout(3, 1));
    aboutPanel.add(new JLabel(lineOne, JLabel.CENTER));
    aboutPanel.add(new JLabel(lineTwo, JLabel.CENTER));
    aboutPanel.add(new JLabel(lineThree, JLabel.CENTER));
    this.getContentPane().add(aboutPanel, "Center");

    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton("OK");
    okButton.addActionListener(this);
    buttonPanel.add(okButton);
    this.getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    this.setFont(myFont);
    FontMetrics fm = this.getFontMetrics(this.getFont());
    int width = Math.max(fm.stringWidth(lineOne),
      Math.max(fm.stringWidth(lineTwo), fm.stringWidth(lineThree)));

    setSize(width + 40, 150);
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
   * Method used to test the class.
   */
  public static void main (String[] args)
  {
    JFrame frame = new JFrame();
    new AboutDialog(frame);
  }
}
