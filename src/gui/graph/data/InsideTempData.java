/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Encapsulates the inside temperature data and JChart2D trace object.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph.data;

import gui.graph.GraphDefs;

public class InsideTempData extends LineDataPlotter
{
  public InsideTempData(int dataSize)
  {
    initialize(GraphDefs.INTEMP_STRING,
               dataSize,
               lineWidth,
               PROPS.getInsideTempColor(),
               PROPS.getInsideTempChart(),
               PROPS.getInsideTempStreamIndex(),
               PROPS.getInsideTempGraphIndex(),
               PROPS.isInsideTempStreamDisplayed(),
               PROPS.isInsideTempGraphDisplayed(),
               null,
               1.0f,
               null,
               1.0f);
  }
}
