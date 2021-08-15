/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is the parent class for the various gauge classes.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import java.awt.*;

class GaugeCommon
{
  public final static Color FG_COLOR = Color.black;
  public final static BasicStroke STROKE  = new BasicStroke(1.0f);
  public final static BasicStroke STROKE2 = new BasicStroke(2.0f);
  public final static Font BOLD_FONT = new Font(null, Font.BOLD, 14);
  public final static Font PLAIN_FONT = new Font(null, Font.PLAIN, 12);
}
