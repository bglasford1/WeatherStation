/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Encapsulates the heat degree days data and jfreechart trace object.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph.data;

import gui.graph.GraphDefs;

public class HeatDDData extends LineDataPlotter
{
  public HeatDDData(int dataSize)
  {
    initialize(GraphDefs.HEAT_DD_STRING,
               dataSize,
               lineWidth,
               PROPS.getHeatDDColor(),
               PROPS.getHeatDDChart(),
               PROPS.getHeatDDStreamIndex(),
               PROPS.getHeatDDGraphIndex(),
               PROPS.isHeatDDStreamDisplayed(),
               PROPS.isHeatDDGraphDisplayed(),
               0.0f,
               null,
               null,
               0.1f);
  }
}
