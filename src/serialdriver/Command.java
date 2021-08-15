/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class defines a console command.  This includes the data to
            write and the EEPROM table offset.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

public class Command
{
  private ConsoleCommand command;
  private byte offset;
  private byte[] data;

  /**
   * Constructor for most commands.
   *
   * @param command The command to set
   */
  public Command(ConsoleCommand command)
  {
    this.command = command;
  }

  /**
   * Constructor that defines an EEBWR command to include the offset and the data.
   *
   * @param command The command to set
   * @param offset The offset into the EEPROM table in hex.
   * @param data The data to write.
   */
  public Command(ConsoleCommand command, byte offset, byte[] data)
  {
    this.command = command;
    this.offset = offset;
    this.data = data;
  }

  public ConsoleCommand getCommand()
  {
    return command;
  }

  public byte getOffset()
  {
    return offset;
  }

  public byte[] getData()
  {
    return data;
  }
}
