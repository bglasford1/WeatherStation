/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class displays the rain graph window.

  Mods:		  09/01/21 Initial Release.
*/
package gui.rain;

import util.ConfigProperties;

import javax.swing.*;
import java.awt.*;

public class RainGraphWindow extends JFrame
{
  private final RainDataThread rainDataThread = RainDataThread.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();

  public RainGraphWindow()
  {
    rainDataThread.initDataStructures();
    add(rainDataThread.getChart());

    setTitle("Rain Graph");
    setSize(new Dimension(PROPS.getWindowWidth(), PROPS.getWindowHeight()));
    setVisible(true);
  }

  /**
   * Method called to reload the graph data.  The data can change whenever data is edited.
   */
  public void reloadData()
  {
    rainDataThread.populateDatasets();
  }
}
