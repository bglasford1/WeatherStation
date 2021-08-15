/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the Weather Underground
            configuration in a dialog box.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import util.ConfigProperties;
import wxserverif.WeatherServerIF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WxServerDialog extends JDialog implements ActionListener
{
  // Button text defintions.
  private static final String OK_STRING = "OK";
  private static final String TEST_STRING = "Test";
  private static final String CANCEL_STRING = "Cancel";

  private static final String STATION_ID_STRING = "Station ID";
  private static final String PASSWORD_STRING = "Password";
  private static final String UPDATE_INTERVAL = "Update Interval";

  private final JTextField stationIdTextField;
  private final JTextField passwordTextField;
  private final JTextField updateIntervalTextField;

  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private static final WeatherServerIF WX_INTERFACE = WeatherServerIF.getInstance();

  /**
   * Constructor that draws the initial dialog box.
   */
  public WxServerDialog(JFrame parent)
  {
    super(parent, "PWS Weather Dialog Box", true);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(3, 2));

    JLabel stationIdLabel = new JLabel(STATION_ID_STRING);
    stationIdTextField = new JTextField(PROPS.getWxStationId());

    JLabel passwordLabel = new JLabel(PASSWORD_STRING);
    passwordTextField = new JTextField(PROPS.getWxPassword());

    JLabel updateIntervalLabel = new JLabel(UPDATE_INTERVAL);
    updateIntervalTextField = new JTextField(PROPS.getWxUpdateInterval());

    mainPanel.add(stationIdLabel);
    mainPanel.add(stationIdTextField);
    mainPanel.add(passwordLabel);
    mainPanel.add(passwordTextField);
    mainPanel.add(updateIntervalLabel);
    mainPanel.add(updateIntervalTextField);

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

    // TODO: add panel with start date/end date and upload observations button.

    getContentPane().add(mainPanel, "Center");
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
    if (e.getActionCommand().equalsIgnoreCase(OK_STRING))
    {
      if (!PROPS.getWxStationId().equalsIgnoreCase(stationIdTextField.getText()))
      {
        PROPS.setWxStationId(stationIdTextField.getText());
      }
      if (!PROPS.getWxPassword().equalsIgnoreCase(passwordTextField.getText()))
      {
        PROPS.setWxPassword(passwordTextField.getText());
      }
      if (PROPS.getWxUpdateInterval() != Integer.parseInt(updateIntervalTextField.getText()))
      {
        PROPS.setWxUpdateInterval(Integer.parseInt(updateIntervalTextField.getText()));
      }
      setVisible(false);
    }
    else if (e.getActionCommand().equalsIgnoreCase(TEST_STRING))
    {
      boolean response = WX_INTERFACE.sendData();
      String responseString = response ? "was successful": "failed.";
      JOptionPane.showMessageDialog(getParent(), "Test " + responseString);
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
    new WxServerDialog(frame);
  }
}
