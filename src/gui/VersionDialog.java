/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the current version of the
            firmware on the console.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VersionDialog extends JDialog implements ActionListener
{
  private final JLabel versionLabel;

  /**
   * Constructor that draws the inital dialog box.
   */
  public VersionDialog (JFrame parent, String results)
  {
    super(parent, "Version Dialog Box", true);

    JPanel testPanel = new JPanel();
    testPanel.setLayout(new GridLayout(1, 1));

    versionLabel = new JLabel("Version: " + results, JLabel.CENTER);

    testPanel.add(versionLabel);
    this.getContentPane().add(testPanel, "Center");

    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton("OK");
    okButton.addActionListener(this);
    buttonPanel.add(okButton);
    this.getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    this.setFont(myFont);
    FontMetrics fm = this.getFontMetrics(this.getFont());
    int width = fm.stringWidth(results);

    setSize(width + 80, 100);
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
  public void setNewVersion
    ( String versionString
    )
  {
    versionLabel.setText("Version:  " + versionString);
  }


  /**
   * Method called to test this class.
   */
  public static void main (String[] args)
  {
    JFrame frame = new JFrame();
    new TestDialog(frame, "Test Message.");
  }
}
