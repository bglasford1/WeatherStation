/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This enumeration defines the console commands.  These are the
            commands that are sent to the console to control the console.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

public enum ConsoleCommand
{
  WAKEUP(false, true, false, false, 2, "\n"),
  TEST(false, true, false, false, 6, "TEST\n"),
  LAMPON(true, false, false, false, 6, "LAMPS 1\n"),
  LAMPOFF(true, false, false, false, 6, "LAMPS 0\n"),
  RXCHECK(true, true, false, false, 13, "RXCHECK\n"), // Response is variable in length.  This is the minimum
  VERSION(true, true, false, false, 19, "VERS\n"),
  NVERSION(true, true, false, false, 12, "NVER\n"),
  RECEIVERS(true, false, false, false, 7, "RECEIVERS\n"),
  EEBRD_ALARMS(false, false, true, true, 3, "EEBRD 52 5E\n"), // Note that the size does not include the # of bytes requested.
  EEBWR(false, false, true, true, 0, "EEBWR oo nn\n"), // Note that nothing is returned except an ACK.
  GETTIME(false, false, true, true, 9, "GETTIME\n"),
  HILOWS(false, false, true, true, 439, "HILOWS\n"),
  LOOP(false, false, true, true, 100, "LOOP 1\n"),
  DMP(false, false, true, true, 266, "DMP\n"),
  DMPAFT(false, false, true, true, 1, "DMPAFT\n"),
  DMPAFTTIME(false, false, true, false, 7, ""),
  DMPAFTDATA(false, false, false, true, 267, ""),
  DMPAFTACK(false, false, false, false, 0, ""),
  ACK(false, false, false, false, 1, "\006"),
  NAK(false, false, false, false, 1, "!"),
  CANCEL(false, false, false, false, 1, "\030");

  private final boolean ok; // The console responds with "\n\rOK\n\r".
  private final boolean eol; // The console ends the response with "\n\r".
  private final boolean ack; // The console first responds with an ACK (0x06).
  private final boolean crc; // The data, either way, ends with a 2 byte CRC.
  private final int size; // The size of the console response to include the above items.
  private final String command; // The actual command sent to the console.

  ConsoleCommand(boolean ok, boolean eol, boolean ack, boolean crc, int size, String command)
  {
    this.ok = ok;
    this.eol = eol;
    this.ack = ack;
    this.crc = crc;
    this.size = size;
    this.command = command;
  }

  public boolean ok()
  {
    return this.ok;
  }

  public boolean eol()
  {
    return this.eol;
  }

  public boolean ack()
  {
    return this.ack;
  }

  public boolean crc()
  {
    return this.crc;
  }

  public int size()
  {
    return this.size;
  }

  public String command()
  {
    return this.command;
  }
}
