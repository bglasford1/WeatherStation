/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is a thread that times out once every 7 seconds and
            queues a LOOP command to read current data to be displayed to the
            operator.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CurrentDataTimer extends Thread implements ActionListener
{
  private final Timer loopTimer = new Timer(7000, this);
  private final ConsoleCmdQueue consoleCmdQueue = ConsoleCmdQueue.getInstance();
  private boolean currentState = true;

  private static class SingletonHelper
  {
    private static final CurrentDataTimer INSTANCE = new CurrentDataTimer();
  }

  public static CurrentDataTimer getInstance()
  {
    return CurrentDataTimer.SingletonHelper.INSTANCE;
  }

  public void run()
  {
    loopTimer.start();
  }

  public void actionPerformed (ActionEvent event)
  {
    loopTimer.stop();
    consoleCmdQueue.getCurrentData();
    loopTimer.start();
  }

  public synchronized void resetTimer()
  {
    if (currentState)
      loopTimer.start();
    else
      loopTimer.stop();
  }

  public synchronized void temporariallyStopTimer()
  {
    loopTimer.stop();
  }

  public synchronized void startTimer()
  {
    currentState = true;
    loopTimer.start();
  }

  public synchronized void stopTimer()
  {
    currentState = false;
    loopTimer.stop();
  }
}
