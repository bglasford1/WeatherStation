/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Encapsulates the wind chill data and JChart2D trace object.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph.data;

import gui.graph.GraphDefs;

public class WindChillData extends LineDataPlotter
{
  public WindChillData(int dataSize)
  {
    initialize(GraphDefs.WIND_CHILL_STRING,
               dataSize,
               lineWidth,
               PROPS.getWindChillColor(),
               PROPS.getWindChillChart(),
               PROPS.getWindChillStreamIndex(),
               PROPS.getWindChillGraphIndex(),
               PROPS.isWindChillStreamDisplayed(),
               PROPS.isWindChillGraphDisplayed(),
               null,
               10.0f,
               null,
               10.0f);
  }
}
