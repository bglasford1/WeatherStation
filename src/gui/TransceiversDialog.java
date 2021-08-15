/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the Trancievers configuration
            Dialog box.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransceiversDialog extends JDialog implements ActionListener
{
  // Button text defintions.
  private static final String OK_STRING = "OK";
  private static final String CANCEL_STRING = "Cancel";

  private final StationRx[] stations = new StationRx[8];

  /**
   * Constructor that draws the initial dialog box.
   */
  public TransceiversDialog(JFrame parent, byte bitmap)
  {
    super(parent, "Comm Config Dialog Box", true);

    JPanel xceiverPanel = new JPanel();
    xceiverPanel.setLayout(new GridLayout(8, 2));

    for (int i = 0; i < 8; i++)
    {
      stations[i] = new StationRx(i + 1, bitmap);

      // TODO: convert the following to a configuration setting.
      if (i == 0)
        stations[0].getStationCombo().setSelectedIndex(1);  // Default is ISS
      else if (i == 1)
        stations[1].getStationCombo().setSelectedIndex(6);  // Default is SOIL

      xceiverPanel.add(stations[i].getStationLabel());
      xceiverPanel.add(stations[i].getStationCombo());
    }

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 2));

    JButton okButton = new JButton(OK_STRING);
    okButton.setActionCommand(OK_STRING);
    okButton.addActionListener(this);

    JButton cancelButton = new JButton(CANCEL_STRING);
    cancelButton.setActionCommand(CANCEL_STRING);
    cancelButton.addActionListener(this);

    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    getContentPane().add(xceiverPanel, "Center");
    getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    setFont(myFont);

    pack();
    setVisible(true);
  }

  public void setBitmap(byte bitmap)
  {
    for (int i = 0; i < 8; i++)
    {
      stations[i].setStationLabel(bitmap);
    }
  }

  /**
   * Method called when a button is pressed.
   */
  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equalsIgnoreCase(OK_STRING))
    {
//      int newStation1Setting = Integer.parseInt((String)station1Combo.getSelectedItem());
      // TODO: Update configuration and use for ???
      setVisible(false);
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
    new TransceiversDialog(frame, (byte)0x03);
  }
}
