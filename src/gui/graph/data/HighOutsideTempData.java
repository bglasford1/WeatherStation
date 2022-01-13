/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Encapsulates the high outside temperature data and JChart2D trace object.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph.data;

import gui.graph.GraphDefs;

public class HighOutsideTempData extends LineDataPlotter
{
  public HighOutsideTempData(int dataSize)
  {
    initialize(GraphDefs.HIGH_OUTTEMP_STRING,
               dataSize,
               lineWidth,
               PROPS.getHighOutsideTempColor(),
               null,
               -1,
               PROPS.getHighOutsideTempGraphIndex(),
               false,
               PROPS.isHighOutsideTempGraphDisplayed(),
               null,
               10.0f,
               null,
               10.0f);
  }
}
