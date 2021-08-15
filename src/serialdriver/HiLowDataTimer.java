/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is a thread that times out once every hour and queues
            a HILOW command to read the hi/low data to be displayed to the operator.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HiLowDataTimer extends Thread implements ActionListener
{
  private final Timer hiLowTimer = new Timer(250000, this);
  private final ConsoleCmdQueue consoleCmdQueue = ConsoleCmdQueue.getInstance();
  private boolean currentState = true;

  private static class SingletonHelper
  {
    private static final HiLowDataTimer INSTANCE = new HiLowDataTimer();
  }

  public static HiLowDataTimer getInstance()
  {
    return HiLowDataTimer.SingletonHelper.INSTANCE;
  }

  public void run()
  {
    hiLowTimer.start();
  }

  public void actionPerformed (ActionEvent event)
  {
    hiLowTimer.stop();
    consoleCmdQueue.getHiLowData();
    hiLowTimer.start();
  }

  public void resetTimer()
  {
    if (currentState)
      hiLowTimer.start();
    else
      hiLowTimer.stop();
  }

  public void temporariallyStopTimer()
  {
    hiLowTimer.stop();
  }

  public void startTimer()
  {
    currentState = true;
    hiLowTimer.start();
  }

  public void stopTimer()
  {
    currentState = false;
    hiLowTimer.stop();
  }
}
