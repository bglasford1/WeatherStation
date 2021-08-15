/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This is an interface to set up the command listener.  The classes
            interested in what command is currently being executed should
            register interest in this data.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

/**
 *
 */
public interface CommandListener
{
  void commandChanged(Command command);
}
