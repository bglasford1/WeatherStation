/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class displays the snow graph window.

  Mods:		  09/01/21 Initial Release.
*/
package gui.snow;

import util.ConfigProperties;

import javax.swing.*;
import java.awt.*;

public class SnowGraphWindow extends JFrame
{
  private final SnowDataThread snowDataThread = SnowDataThread.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();

  public SnowGraphWindow()
  {
    snowDataThread.initDataStructures();
    add(snowDataThread.getChart());

    setTitle("Snow Graph");
    setSize(new Dimension(PROPS.getWindowWidth(), PROPS.getWindowHeight()));
    setVisible(true);
  }

  /**
   * Method called to reload the graph data.  The data can change whenever data is edited.
   */
  public void reloadData()
  {
    snowDataThread.populateDatasets();
  }
}
