/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is used by various dialog boxes that have date pickers.
            This formats the date that is displayed.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter
{
  private final String datePattern = "yyyy-MM-dd";
  private final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

  @Override
  public Object stringToValue(String text) throws ParseException
  {
    return dateFormatter.parseObject(text);
  }

  @Override
  public String valueToString(Object value)
  {
    if (value != null)
    {
      Calendar cal = (Calendar) value;
      return dateFormatter.format(cal.getTime());
    }
    return "";
  }
}
