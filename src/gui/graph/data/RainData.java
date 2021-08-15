/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Encapsulates the rainfall data and JChart2D trace object.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph.data;

import gui.graph.GraphDefs;

public class RainData extends LineDataPlotter
{
  /**
   * Constructor for a line chart.
   *
   * @param dataSize The size of the data set.
   */
  public RainData(int dataSize)
  {
    initialize(GraphDefs.RAINFALL_STRING,
               dataSize,
               4.0f,
               PROPS.getRainColor(),
               PROPS.getRainChart(),
               PROPS.getRainStreamIndex(),
               PROPS.getRainGraphIndex(),
               PROPS.isRainStreamDisplayed(),
               PROPS.isRainGraphDisplayed(),
               0.0f,
               null,
               0.05f,
               null);
  }
}
