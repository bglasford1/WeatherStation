/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	The main Weather Station class.

  Mods:		  09/01/21 Initial Release.
*/
import gui.MainWindow;

public class WeatherStation
{
  public static void main (String[] args)
  {
    new WeatherStation();
  }

  private WeatherStation()
  {
    // Initialize the GUI.
    new MainWindow();
  }
}
