/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is an internal class that handles the wakeup wait and notify.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

class CommandNotifier
{
  // Local variables.
  private boolean waiting = false;

  /**
   * Method that waits for a notify.
   */
  public synchronized void waitForNotification()
  {
    waiting = true;

    while (waiting)
    {
      try
      {
        wait();
      }
      catch (InterruptedException ie)
      {
        // This exception means that another thread has "interrupted" this wait.
        // This is a condition that should never occur but must be caught.
      }
    }
  }

  /**
   * Method that notifies the waiter.
   */
  public synchronized void notifyWaiter()
  {
    if (waiting)
    {
      waiting = false;
      notifyAll();
    }
  }
}
