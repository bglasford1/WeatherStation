/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the Test Dialog box.  This
            is used to display the results of the test command.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestDialog extends JDialog implements ActionListener
{
  private final JLabel testResults;

  /**
   * Constructor that draws the initial dialog box.
   */
  public TestDialog (JFrame parent, String results)
  {
    super(parent, "Test Dialog Box", true);

    JPanel testPanel = new JPanel();
    testPanel.setLayout(new GridLayout(1, 1));

    testResults = new JLabel("Results: " + results, JLabel.CENTER);

    testPanel.add(testResults);
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

    setSize(width + 100, 100);
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
   * Method called to change the test text.
   */
  public void setTestText (String testText)
  {
    testResults.setText("Results: " + testText);
  }

  /**
   * Method called to test the dialog box.
   */
  public static void main (String[] args)
  {
    JFrame frame = new JFrame();
    new TestDialog(frame, "Test Message.");
  }
}
