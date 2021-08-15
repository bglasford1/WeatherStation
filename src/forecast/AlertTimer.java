/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is a thread that times out once every 30 minutes and
            reads the current NOAA forecast alerts to be displayed to the
            operator.

  Mods:		  09/01/21 Initial Release.
*/
package forecast;

import gui.currentreadings.CurrentReadings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlertTimer extends Thread implements ActionListener
{
  private final Timer loopTimer = new Timer(1800000, this);
  private final CurrentReadings parent;

  public AlertTimer(CurrentReadings parent)
  {
    this.parent = parent;
  }

  public void run()
  {
    loopTimer.start();
  }

  public void actionPerformed (ActionEvent event)
  {
    loopTimer.stop();
    parent.setAlertButtonSeverity();
    loopTimer.start();
  }

  public void startTimer()
  {
    loopTimer.start();
  }

  public void stopTimer()
  {
    loopTimer.stop();
  }
}
