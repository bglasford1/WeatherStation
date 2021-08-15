/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Encapsulates the dew point data and jfreechart trace object.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph.data;

import gui.graph.GraphDefs;

public class DewPointData extends LineDataPlotter
{
  public DewPointData(int dataSize)
  {
    initialize(GraphDefs.DEW_POINT_STRING,
               dataSize,
               1.0f,
               PROPS.getDewPointColor(),
               PROPS.getDewPointChart(),
               PROPS.getDewPointStreamIndex(),
               PROPS.getDewPointGraphIndex(),
               PROPS.isDewPointStreamDisplayed(),
               PROPS.isDewPointGraphDisplayed(),
               null,
               1.0f,
               null,
               1.0f);
  }
}
