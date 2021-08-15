/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the Communications
            Configuration Dialog box.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import util.ConfigProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class responsible for drawing the communications configuration dialog box.
 */
public class CommConfigDialog extends JDialog implements ActionListener
{
  // Button text defintions.
  private static final String OK_STRING = "OK";
  private static final String TEST_STRING = "Test";
  private static final String CANCEL_STRING = "Cancel";

  private static final String PROTOCOL_STRING = "Protocol";
  private static final String BAUD_STRING = "BAUD Rate";

  private static final String[] PROTOCOL_STRINGS = { "Serial", "Simulator" };
  private static final String[] BAUD_STRINGS = { "1200", "2400", "4800", "9600", "14400", "19200" };
  private final JComboBox<String> protocolCombo;
  private final JComboBox<String> baudCombo;

  private static final ConfigProperties PROPS = ConfigProperties.instance();

  /**
   * Constructor that draws the initial dialog box.
   */
  CommConfigDialog(JFrame parent)
  {
    super(parent, "Comm Config Dialog Box", true);

    JPanel commPanel = new JPanel();
    commPanel.setLayout(new GridLayout(2, 2));

    JLabel protocolLabel = new JLabel(PROTOCOL_STRING);
    protocolCombo = new JComboBox<>(PROTOCOL_STRINGS);
    protocolCombo.setSelectedIndex(1);  // Defaulted to simulator

    JLabel baudLabel = new JLabel(BAUD_STRING);
    baudCombo = new JComboBox<>(BAUD_STRINGS);
    baudCombo.setSelectedIndex(5); // Defaulted to 19,200

    commPanel.add(protocolLabel);
    commPanel.add(protocolCombo);
    commPanel.add(baudLabel);
    commPanel.add(baudCombo);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 3));

    JButton okButton = new JButton(OK_STRING);
    okButton.setActionCommand(OK_STRING);
    okButton.addActionListener(this);

    JButton testButton = new JButton(TEST_STRING);
    testButton.setActionCommand(TEST_STRING);
    testButton.addActionListener(this);

    JButton cancelButton = new JButton(CANCEL_STRING);
    cancelButton.setActionCommand(CANCEL_STRING);
    cancelButton.addActionListener(this);

    buttonPanel.add(okButton);
    buttonPanel.add(testButton);
    buttonPanel.add(cancelButton);

    getContentPane().add(commPanel, "Center");
    getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    setFont(myFont);

    pack();
    setVisible(true);
  }

  /**
   * Method called when a button is pressed.
   */
  @Override
  public void actionPerformed(ActionEvent e)
  {
    // TODO: deal with protocol change.

    if (e.getActionCommand().equalsIgnoreCase(OK_STRING))
    {
      int newBaudRate = Integer.parseInt((String)baudCombo.getSelectedItem());
      if (PROPS.getBaudRate() != newBaudRate)
      {
        PROPS.setBaudRate(newBaudRate);
      }
      setVisible(false);
    }
    else if (e.getActionCommand().equalsIgnoreCase(TEST_STRING))
    {
      // TODO: make protocol/baud changes and then run test command to console.
    }
    else if (e.getActionCommand().equalsIgnoreCase(CANCEL_STRING))
    {
      setVisible(false);
    }
  }

  /**
   * Method called to test the dialog box.
   */
  public static void main (String[] args)
  {
    JFrame frame = new JFrame();
    new CommConfigDialog(frame);
  }
}
