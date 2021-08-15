/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Encapsulates the ISS reception data and JChart2D trace object.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph.data;

import gui.graph.GraphDefs;

public class IssReceptionData extends LineDataPlotter
{
  private static final float EXPECTED_NUMBER_OF_PACKETS = 117.073f;

  public IssReceptionData(int dataSize)
  {
    initialize(GraphDefs.ISS_RECEPTION_STRING,
               dataSize,
               1.0f,
               PROPS.getIssReceptionColor(),
               PROPS.getIssReceptionChart(),
               PROPS.getIssReceptionStreamIndex(),
               PROPS.getIssReceptionGraphIndex(),
               PROPS.isIssReceptionStreamDisplayed(),
               PROPS.isIssReceptionGraphDisplayed(),
               75.0f,
               null,
               100.0f,
               null);
  }
}
