/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for logging data to both a log file and
            a debug file.

  Mods:		  09/01/21 Initial Release.
*/
package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger
{
  public enum Level
  {
    NONE,
    COARSE,
    MEDIUM,
    FINE
  }

  private static final String DEBUG_FILENAME = "tmp/debug.txt";
  private static final String LOG_FILENAME = "tmp/log.txt";
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private Level currentLevel = Level.NONE;

  private static class SingletonHelper
  {
    private static final Logger INSTANCE = new Logger();
  }

  public static Logger getInstance()
  {
    return SingletonHelper.INSTANCE;
  }

  /**
   * Constructor that opens the logger output file.
   */
  private Logger()
  {
    try
    {
      File file = new File(DEBUG_FILENAME);
      if (file.exists())
      {
        file.delete();
      }
      file.createNewFile();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void setLevel(Level level)
  {
    this.currentLevel = level;
  }

  /**
   * Test method to log the details of the console protocol.  The byte array received is written to a file named
   * based on the command that was sent to the console.  This is for capturing data such as DMP/DMPAFT, LOOP and
   * HILOW data for analysis and subsequent playback.  There are three levels of logging: coarse, medium and fine.
   *
   * @param data The byte array received.
   * @param level The level to log.
   */
  public synchronized void captureData(String data, Level level)
  {
    if (PROPS.getCaptureData())
    {
      if (level.ordinal() <= currentLevel.ordinal())
      {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(DEBUG_FILENAME),true)))
        {
          out.println(data);
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Method to log data that is important to the health and status of the system.  The data is written to a log file.
   * This is the second place to look if something has gone wrong, the first place is the window the application was
   * launched from.  The log file will contain expected problems.  Unexpected problems will hopefully be output to
   * the launch window.
   *
   * @param data The string explanation of the problem.
   */
  public synchronized void logData(String data)
  {
    try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(LOG_FILENAME),true)))
    {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
      LocalDateTime now = LocalDateTime.now();
      out.println(dtf.format(now) + " : " + data);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
