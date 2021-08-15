/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class creates the PI CPU Temp dialog box.  This box displays
            the CPU temperature of the Raspberry PI.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import com.pi4j.system.SystemInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PiDiagsDialog extends JDialog implements ActionListener
{
  /**
   * Constructor that draws the initial dialog box.
   */
  public PiDiagsDialog(JFrame parent)
  {
    super(parent, "PI CPU Temp Dialog Box", true);

    JPanel aboutPanel = new JPanel();
    aboutPanel.setLayout(new GridLayout(6, 2));
    this.getContentPane().add(aboutPanel, "Center");

    try
    {
      aboutPanel.add(new JLabel("CPU Temperature (F): ", JLabel.RIGHT));
      aboutPanel.add(new JTextField(Float.toString(SystemInfo.getCpuTemperature())));
      aboutPanel.add(new JLabel("CPU Revision: ", JLabel.RIGHT));
      aboutPanel.add(new JTextField(SystemInfo.getCpuRevision()));
      aboutPanel.add(new JLabel("Processor: ", JLabel.RIGHT));
      aboutPanel.add(new JTextField(SystemInfo.getProcessor()));
      aboutPanel.add(new JLabel("Total Memory: ", JLabel.RIGHT));
      aboutPanel.add(new JTextField(Long.toString(SystemInfo.getMemoryTotal())));
      aboutPanel.add(new JLabel("Used Memory: ", JLabel.RIGHT));
      aboutPanel.add(new JTextField(Long.toString(SystemInfo.getMemoryUsed())));
      aboutPanel.add(new JLabel("Free Memory: ", JLabel.RIGHT));
      aboutPanel.add(new JTextField(Long.toString(SystemInfo.getMemoryFree())));
    }
    catch(InterruptedException |IOException exception)
    {
      aboutPanel.add(new JTextField("Error"));
    }

    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton("OK");
    okButton.addActionListener(this);
    buttonPanel.add(okButton);
    this.getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    this.setFont(myFont);

    setSize(350, 200);
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
    new PiDiagsDialog(frame);
  }
}
