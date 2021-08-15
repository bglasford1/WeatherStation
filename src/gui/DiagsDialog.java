/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class displays the diagnostics dialog box.  This involves
            getting diags from the console.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DiagsDialog extends JDialog implements ActionListener
{
  private final JLabel packetsLabel;
  private final JLabel missedLabel;
  private final JLabel resynchLabel;
  private final JLabel contigLabel;
  private final JLabel crcLabel;
  private final JLabel percentLabel;
  private final JLabel batteryLabel;
  private final JLabel statusLabel;

  /**
   * Constructor that draws the initial dialog box.
   */
  public DiagsDialog
    ( JFrame parent,
      String packetsReceived,
      String missedPackets,
      String resynchronizations,
      String maxContigPackets,
      String crcErrors,
      String percentGood,
      String batteryVoltage,
      String batteryStatus
    )
  {
    super(parent, "Diags Dialog Box", true);

    JPanel diagsPanel = new JPanel();
    diagsPanel.setLayout(new GridLayout(8, 1));

    packetsLabel = new JLabel("Packets Received: " + packetsReceived, JLabel.CENTER);
    missedLabel  = new JLabel("Missed Packets: " + missedPackets, JLabel.CENTER);
    resynchLabel = new JLabel("Resynchronizations: " + resynchronizations, JLabel.CENTER);
    contigLabel  = new JLabel("Max Contiguous Packets: " + maxContigPackets, JLabel.CENTER);
    crcLabel     = new JLabel("CRC Errors: " + crcErrors, JLabel.CENTER);
    percentLabel = new JLabel("Percent Good: " + percentGood + "%", JLabel.CENTER);
    batteryLabel = new JLabel("Battery Voltage: " + batteryVoltage, JLabel.CENTER);
    statusLabel  = new JLabel("Trans Battery Status: " + batteryStatus, JLabel.CENTER);

    diagsPanel.add(packetsLabel);
    diagsPanel.add(missedLabel);
    diagsPanel.add(resynchLabel);
    diagsPanel.add(contigLabel);
    diagsPanel.add(crcLabel);
    diagsPanel.add(percentLabel);
    diagsPanel.add(batteryLabel);
    diagsPanel.add(statusLabel);
    this.getContentPane().add(diagsPanel);

    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton("OK");
    okButton.addActionListener(this);
    buttonPanel.add(okButton);
    this.getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    this.setFont(myFont);

    this.setSize(320, 300);
    this.setVisible(true);
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
  public void setNewValues
    ( String packetsReceived,
      String missedPackets,
      String resynchronizations,
      String maxContigPackets,
      String crcErrors,
      String percentGood,
      String batteryVoltage,
      String batteryStatus
    )
  {
    packetsLabel.setText("Packets Received: " + packetsReceived);
    missedLabel.setText("Missed Packets: " + missedPackets);
    resynchLabel.setText("Resynchronizations: " + resynchronizations);
    contigLabel.setText("Max Contiguous Packets: " + maxContigPackets);
    crcLabel.setText("CRC Errors: " + crcErrors);
    percentLabel.setText("Percent Good: " + percentGood + "%");
    batteryLabel.setText("Battery Voltage: " + batteryVoltage);
    statusLabel.setText("Trans Battery Status: " + batteryStatus);
  }


  /**
   * Method used to test this class.
   */
  public static void main (String[] args)
  {
    JFrame frame = new JFrame();
    new DiagsDialog
      ( frame,
        "12345",
        "111",
        "1",
        "1010",
        "0",
	"99",
	"4.53",
	"0"
      );
  }
}
