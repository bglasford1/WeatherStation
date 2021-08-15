/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class encapsulates the Station.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import javax.swing.*;

class StationRx
{
  private int number;
  private byte bitmask;
  private JLabel stationLabel;

  private static final String RESPONDING = ": Responding";
  private static final String NOT_RESPONDING = ": Not Responding";

  private static final String[] STATION_CONFIG_STRINGS =
    { "Off", "ISS", "TEMP", "TEMP/HUMID", "WIND", "LEAF", "SOIL", "LEAF/SOIL" };
  private JComboBox<String> stationCombo;

  StationRx(int number, byte bitmap)
  {
    this.number = number;

    switch (number)
    {
      case 1:
        bitmask = 0x01;
        break;
      case 2:
        bitmask = 0x02;
        break;
      case 3:
        bitmask = 0x04;
        break;
      case 4:
        bitmask = 0x08;
        break;
      case 5:
        bitmask = 0x10;
        break;
      case 6:
        bitmask = 0x20;
        break;
      case 7:
        bitmask = 0x40;
        break;
      case 8:
        bitmask = (byte) 0x80;
        break;
    }

    setStationLabel(bitmap);

    stationCombo = new JComboBox<>(STATION_CONFIG_STRINGS);
  }

  public int getNumber()
  {
    return number;
  }

  public byte getBitmask()
  {
    return bitmask;
  }

  public JLabel getStationLabel()
  {
    return stationLabel;
  }

  public JComboBox getStationCombo()
  {
    return stationCombo;
  }

  private boolean isResponding(byte bitmap)
  {
    return (bitmap & bitmask) > 0;
  }

  public void setStationLabel(byte bitmap)
  {
    if (isResponding(bitmap))
      stationLabel = new JLabel("Station " + number + RESPONDING);
    else
      stationLabel = new JLabel("Station " + number + NOT_RESPONDING);
  }
}
