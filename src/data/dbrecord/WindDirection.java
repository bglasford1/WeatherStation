/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This enumeration defines the wind directions along with their
            internal value and direction.

  Mods:		  09/01/21 Initial Release.
*/
package data.dbrecord;

public enum WindDirection
{
  N(0, 270f),
  NNE(1, 292.5f),
  NE(2, 315f),
  ENE(3, 337.5f),
  E(4, 0f),
  ESE(5, 22.5f),
  SE(6, 45f),
  SSE(7, 67.5f),
  S(8, 90f),
  SSW(9, 112.5f),
  SW(10, 135f),
  WSW(11, 157.5f),
  W(12, 180f),
  WNW(13, 202.5f),
  NW(14, 225f),
  NNW(15, 247.5f);

  private final int value; // The value sent from the console within the dmp command.
  private final float direction; // The java direction, east = 0, moves clockwise.

  WindDirection(int value, float direction)
  {
    this.value = value;
    this.direction = direction;
  }

  public static WindDirection valueOf(int value)
  {
    for (WindDirection direction : values())
    {
      if (direction.value == value)
      {
        return direction;
      }
    }
    return null;
  }

  public int value()
  {
    return this.value;
  }

  public float direction()
  {
    return this.direction;
  }
}
