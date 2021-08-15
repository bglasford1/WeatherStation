/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class queues commands destined for the console.  This is needed
            because the interface to the console is very slow.  Some of the
            commands like the DMP/DMPAFT commands are very long threaded.  The
            queue cannot contain data, only console command enumeration values.
            For the EEPROM writes, both the offset into the table and the data
            to set are required.  These are stacked up and pulled off each time
            an EEBWR command is pulled off the queue.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConsoleCmdQueue
{
  private final LinkedList<Command> theQueue = new LinkedList<>();
  private static ConsoleCmdQueue instance = null;
  private Command nextCommand = null;
  private final List<CommandListener> listeners = new ArrayList<>();

  public static ConsoleCmdQueue getInstance()
  {
    if (instance == null)
    {
      instance = new ConsoleCmdQueue();
    }
    return instance;
  }

  public void performTest()
  {
    addCommand(new Command(ConsoleCommand.TEST));
  }

  public void performDiags()
  {
    addCommand(new Command(ConsoleCommand.RXCHECK));
  }

  public void getVersion()
  {
    addCommand(new Command(ConsoleCommand.VERSION));
  }

  public void getReceivers()
  {
    addCommand(new Command(ConsoleCommand.RECEIVERS));
  }

  public void getCurrentData()
  {
    addCommand(new Command(ConsoleCommand.LOOP));
  }

  public void getHistoricData()
  {
    addCommand(new Command(ConsoleCommand.DMPAFT));
  }

  public void getHiLowData()
  {
    addCommand(new Command(ConsoleCommand.HILOWS));
  }

  public void dumpArchivedData()
  {
    addCommand(new Command(ConsoleCommand.DMP));
  }

  public void dumpArchivedDataAfterDate()
  {
    addCommand(new Command(ConsoleCommand.DMPAFT));
  }

  public void getConsoleTime()
  {
    addCommand(new Command(ConsoleCommand.GETTIME));
  }

  public void turnLampOn()
  {
    addCommand(new Command(ConsoleCommand.LAMPON));
  }

  public void turnLampOff()
  {
    addCommand(new Command(ConsoleCommand.LAMPOFF));
  }

  public void getAlarms()
  {
    addCommand(new Command(ConsoleCommand.EEBRD_ALARMS));
  }

  public void writeEepromData(ConsoleCommand command, byte offset, byte[] data)
  {
    addCommand(new Command(command, offset, data));
  }

  /**
   * Get the next command from the queue.  If the queue is empty then wait until another thread places a command
   * on the queue.
   *
   * @return The next command.
   */
  public synchronized Command getConsoleCmd()
  {
    while (theQueue.size() == 0)
    {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.getLocalizedMessage();
      }
    }

    nextCommand = theQueue.removeFirst();

      // Notify everybody that may be interested.
      for (CommandListener nextListener : listeners)
        nextListener.commandChanged(nextCommand);

    return nextCommand;
  }

  /**
   * Used for the comm button on the current display to reset the button when the command has completed.
   */
  public synchronized void resetCommand()
  {
    nextCommand = null;

    // Notify everybody that may be interested.
    for (CommandListener nextListener : listeners)
      nextListener.commandChanged(nextCommand);
  }

  /**
   * Add a command onto the queue.  If the queue is empty then notify the driver that a command is available.
   *
   * @param command The command to add.
   */
  private synchronized void addCommand(Command command)
  {
    theQueue.addLast(command);
    if (theQueue.size() == 1)
    {
      notifyAll();
    }
  }

  /**
   * Method to register interest when the command changes.
   */
  public void addListener(CommandListener listener)
  {
    listeners.add(listener);
  }
}
