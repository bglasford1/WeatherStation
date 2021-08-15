/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is a thread that times out once every 5 minutes and
            queues commands to read the DMPAFT data.  This data is archived
            to the flat file database.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HistoricDataTimer extends Thread implements ActionListener
{
  private final Timer historicTimer = new Timer(300000, this);
  private final ConsoleCmdQueue consoleCmdQueue = ConsoleCmdQueue.getInstance();
  private boolean currentState = true;

  private static class SingletonHelper
  {
    private static final HistoricDataTimer INSTANCE = new HistoricDataTimer();
  }

  public static HistoricDataTimer getInstance()
  {
    return HistoricDataTimer.SingletonHelper.INSTANCE;
  }

  public void run()
  {
    historicTimer.start();
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    historicTimer.stop();
    consoleCmdQueue.getHistoricData();
    historicTimer.start();
  }

  public void resetTimer()
  {
    if (currentState)
      historicTimer.start();
    else
      historicTimer.stop();
  }

  public void temporariallyStopTimer()
  {
    historicTimer.stop();
  }

  public void startTimer()
  {
    historicTimer.start();
  }

  public void stopTimer()
  {
    historicTimer.stop();
  }
}
