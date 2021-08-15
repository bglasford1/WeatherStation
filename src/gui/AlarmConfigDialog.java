/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the Alarm Config Dialog box.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import data.consolerecord.AlarmData;
import serialdriver.ConsoleCmdQueue;
import serialdriver.ConsoleCommand;
import serialdriver.EepromDefs;
import util.ByteUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlarmConfigDialog extends JDialog implements ActionListener
{
  private ConsoleCmdQueue queue = ConsoleCmdQueue.getInstance();

  // Button text defintions.
  private static final String UPDATE_STRING = "Update";
  private static final String CANCEL_STRING = "Cancel";
  private static final String NOT_SET = "---";

  private final JTextField barRiseTextField;
  private final JTextField barFallTextField;
  private final JTextField timeTextField;
  private final JTextField lowInTempTextField;
  private final JTextField highInTempTextField;
  private final JTextField lowOutTempTextField;
  private final JTextField highOutTempTextField;
  private final JTextField lowSoil1TempTextField;
  private final JTextField highSoil1TempTextField;
  private final JTextField lowInHumidTextField;
  private final JTextField highInHumidTextField;
  private final JTextField lowOutHumidTextField;
  private final JTextField highOutHumidTextField;
  private final JTextField lowDewTextField;
  private final JTextField highDewTextField;
  private final JTextField windChillTextField;
  private final JTextField heatIndexTextField;
  private final JTextField thswTextField;
  private final JTextField windSpeedTextField;
  private final JTextField windSpeed10MinTextField;
  private final JTextField solarTextField;
  private final JTextField rainRateTextField;
  private final JTextField rain15MinTextField;
  private final JTextField rain24HrTextField;
  private final JTextField rainStormTextField;

  /**
   * Constructor that draws the initial dialog box.
   */
  public AlarmConfigDialog(JFrame parent, AlarmData data)
  {
    super(parent, "Alarm Config Dialog Box", true);

    JLabel highLabel = new JLabel("High");
    JLabel lowLabel = new JLabel("Low");
    JLabel pressureLabel = new JLabel("Pressure Trend", SwingConstants.RIGHT);
    JLabel timeLabel = new JLabel("Time", SwingConstants.RIGHT);
    JLabel inTempLabel = new JLabel("In Temp", SwingConstants.RIGHT);
    JLabel outTempLabel = new JLabel("Out Temp", SwingConstants.RIGHT);
    JLabel soil1TempLabel = new JLabel("Soil1 Temp", SwingConstants.RIGHT);
    JLabel inHumidLabel = new JLabel("In Humid", SwingConstants.RIGHT);
    JLabel outHumidLabel = new JLabel("Out Humid", SwingConstants.RIGHT);
    JLabel dewLabel = new JLabel("Dew Point", SwingConstants.RIGHT);
    JLabel windChillLabel = new JLabel("Wind Chill", SwingConstants.RIGHT);
    JLabel heatIndexLabel = new JLabel("Heat Index", SwingConstants.RIGHT);
    JLabel thswLabel = new JLabel("THSW", SwingConstants.RIGHT);
    JLabel windSpeedLabel = new JLabel("Wind Speed", SwingConstants.RIGHT);
    JLabel windSpeed10MinLabel = new JLabel("Speed 10Min", SwingConstants.RIGHT);
    JLabel solarLabel = new JLabel("Solar", SwingConstants.RIGHT);
    JLabel rainRateLabel = new JLabel("Rain Rate", SwingConstants.RIGHT);
    JLabel rain15MinLabel = new JLabel("Rain 15Min", SwingConstants.RIGHT);
    JLabel rain24HrLabel = new JLabel("Rain 24Hr", SwingConstants.RIGHT);
    JLabel rainStormLabel = new JLabel("Rain Storm", SwingConstants.RIGHT);

    barRiseTextField = new JTextField();
    barFallTextField = new JTextField();
    timeTextField = new JTextField();
    lowInTempTextField = new JTextField();
    highInTempTextField = new JTextField();
    lowOutTempTextField = new JTextField();
    highOutTempTextField = new JTextField();
    lowSoil1TempTextField = new JTextField();
    highSoil1TempTextField = new JTextField();
    lowInHumidTextField = new JTextField();
    highInHumidTextField = new JTextField();
    lowOutHumidTextField = new JTextField();
    highOutHumidTextField = new JTextField();
    lowDewTextField = new JTextField();
    highDewTextField = new JTextField();
    windChillTextField = new JTextField();
    heatIndexTextField = new JTextField();
    thswTextField = new JTextField();
    windSpeedTextField = new JTextField();
    windSpeed10MinTextField = new JTextField();
    solarTextField = new JTextField();
    rainRateTextField = new JTextField();
    rain15MinTextField = new JTextField();
    rain24HrTextField = new JTextField();
    rainStormTextField = new JTextField();
    setNewValues(data);

    JPanel commPanel = new JPanel();
    commPanel.setLayout(new GridLayout(12, 8, 10, 1));

    commPanel.add(new JLabel());
    commPanel.add(lowLabel);
    commPanel.add(new JLabel());
    commPanel.add(highLabel);
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());

    commPanel.add(pressureLabel);
    commPanel.add(barFallTextField);
    commPanel.add(new JLabel());
    commPanel.add(barRiseTextField);
    commPanel.add(new JLabel());
    commPanel.add(windSpeedLabel);
    commPanel.add(windSpeedTextField);
    commPanel.add(new JLabel("mph"));

    commPanel.add(inTempLabel);
    commPanel.add(lowInTempTextField);
    commPanel.add(new JLabel("degF"));
    commPanel.add(highInTempTextField);
    commPanel.add(new JLabel("degF"));
    commPanel.add(windSpeed10MinLabel);
    commPanel.add(windSpeed10MinTextField);
    commPanel.add(new JLabel("mph"));

    commPanel.add(outTempLabel);
    commPanel.add(lowOutTempTextField);
    commPanel.add(new JLabel("degF"));
    commPanel.add(highOutTempTextField);
    commPanel.add(new JLabel("degF"));
    commPanel.add(rainRateLabel);
    commPanel.add(rainRateTextField);
    commPanel.add(new JLabel("in."));

    commPanel.add(soil1TempLabel);
    commPanel.add(lowSoil1TempTextField);
    commPanel.add(new JLabel("degF"));
    commPanel.add(highSoil1TempTextField);
    commPanel.add(new JLabel("degF"));
    commPanel.add(rain15MinLabel);
    commPanel.add(rain15MinTextField);
    commPanel.add(new JLabel("in."));

    commPanel.add(inHumidLabel);
    commPanel.add(lowInHumidTextField);
    commPanel.add(new JLabel("%"));
    commPanel.add(highInHumidTextField);
    commPanel.add(new JLabel("%"));
    commPanel.add(rain24HrLabel);
    commPanel.add(rain24HrTextField);
    commPanel.add(new JLabel("in."));

    commPanel.add(outHumidLabel);
    commPanel.add(lowOutHumidTextField);
    commPanel.add(new JLabel("%"));
    commPanel.add(highOutHumidTextField);
    commPanel.add(new JLabel("%"));
    commPanel.add(rainStormLabel);
    commPanel.add(rainStormTextField);
    commPanel.add(new JLabel("in."));

    commPanel.add(dewLabel);
    commPanel.add(lowDewTextField);
    commPanel.add(new JLabel("degF"));
    commPanel.add(highDewTextField);
    commPanel.add(new JLabel("degF"));
    commPanel.add(timeLabel);
    commPanel.add(timeTextField);
    commPanel.add(new JLabel());

    commPanel.add(windChillLabel);
    commPanel.add(windChillTextField);
    commPanel.add(new JLabel("degF"));
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());

    commPanel.add(heatIndexLabel);
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(heatIndexTextField);
    commPanel.add(new JLabel("degF"));
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());

    commPanel.add(thswLabel);
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(thswTextField);
    commPanel.add(new JLabel("degF"));
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());

    commPanel.add(solarLabel);
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(solarTextField);
    commPanel.add(new JLabel("W/m2"));
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 4));

    JButton updateButton = new JButton(UPDATE_STRING);
    updateButton.setActionCommand(UPDATE_STRING);
    updateButton.addActionListener(this);

    JButton cancelButton = new JButton(CANCEL_STRING);
    cancelButton.setActionCommand(CANCEL_STRING);
    cancelButton.addActionListener(this);

    buttonPanel.add(new JLabel());
    buttonPanel.add(updateButton);
    buttonPanel.add(cancelButton);
    buttonPanel.add(new JLabel());

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(commPanel, "Center");
    getContentPane().add(buttonPanel, "South");

    Font myFont = new Font("MyFont", Font.PLAIN, 12);
    setFont(myFont);

    pack();
    setVisible(true);
  }

  /**
   * Set new alarm values once the dialog has been created.
   *
   * @param data The new alarm data.
   */
  public void setNewValues(AlarmData data)
  {
    String barRiseString = NOT_SET;
    if (data.isBarRiseAlarmSet())
      barRiseString = Float.toString(data.getBarRiseAlarm());
    barRiseTextField.setText(barRiseString);

    String barFallString = NOT_SET;
    if (data.isBarFallAlarmSet())
      barFallString = Float.toString(data.getBarFallAlarm());
    barFallTextField.setText(barFallString);

    String timeString = NOT_SET;
    if (data.isTimeAlarmSet())
      timeString = Float.toString(data.getTimeAlarm());
    timeTextField.setText(timeString);

    String lowInTempString = NOT_SET;
    if (data.isLowInTempAlarmSet())
      lowInTempString = Integer.toString(data.getLowInTempAlarm());
    lowInTempTextField.setText(lowInTempString);

    String highInTempString = NOT_SET;
    if (data.isHighInTempAlarmSet())
      highInTempString = Integer.toString(data.getHighInTempAlarm());
    highInTempTextField.setText(highInTempString);

    String lowOutTempString = NOT_SET;
    if (data.isLowOutTempAlarmSet())
      lowOutTempString = Integer.toString(data.getLowOutTempAlarm());
    lowOutTempTextField.setText(lowOutTempString);

    String highOutTempString = NOT_SET;
    if (data.isHighOutTempAlarmSet())
      highOutTempString = Integer.toString(data.getHighOutTempAlarm());
    highOutTempTextField.setText(highOutTempString);

    String lowSoil1TempString = NOT_SET;
    if (data.isLowSoil1TempAlarmSet())
      lowSoil1TempString = Integer.toString(data.getLowSoil1TempAlarm());
    lowSoil1TempTextField.setText(lowSoil1TempString);

    String highSoil1TempString = NOT_SET;
    if (data.isHighSoil1TempAlarmSet())
      highSoil1TempString = Integer.toString(data.getHighSoil1TempAlarm());
    highSoil1TempTextField.setText(highSoil1TempString);

    String lowInHumidString = NOT_SET;
    if (data.isLowInHumidAlarmSet())
      lowInHumidString = Integer.toString(data.getLowInHumidAlarm());
    lowInHumidTextField.setText(lowInHumidString);

    String highInHumidString = NOT_SET;
    if (data.isHighInHumidAlarmSet())
      highInHumidString = Integer.toString(data.getHighInHumidAlarm());
    highInHumidTextField.setText(highInHumidString);

    String lowOutHumidString = NOT_SET;
    if (data.isLowOutHumidAlarmSet())
      lowOutHumidString = Integer.toString(data.getLowOutHumidAlarm());
    lowOutHumidTextField.setText(lowOutHumidString);

    String highOutHumidString = NOT_SET;
    if (data.isHighOutHumidAlarmSet())
      highOutHumidString = Integer.toString(data.getHighOutHumidAlarm());
    highOutHumidTextField.setText(highOutHumidString);

    String lowDewString = NOT_SET;
    if (data.isLowDewAlarmSet())
      lowDewString = Integer.toString(data.getLowDewAlarm());
    lowDewTextField.setText(lowDewString);

    String highDewString = NOT_SET;
    if (data.isHighDewAlarmSet())
      highDewString = Integer.toString(data.getHighDewAlarm());
    highDewTextField.setText(highDewString);

    String windChillString = NOT_SET;
    if (data.isWindChillAlarmSet())
      windChillString = Integer.toString(data.getWindChillAlarm());
    windChillTextField.setText(windChillString);

    String heatIndexString = NOT_SET;
    if (data.isHeatIndexAlarmSet())
      heatIndexString = Integer.toString(data.getHeatIndexAlarm());
    heatIndexTextField.setText(heatIndexString);

    String thswString = NOT_SET;
    if (data.isThswAlarmSet())
      thswString = Integer.toString(data.getThswAlarm());
    thswTextField.setText(thswString);

    String windSpeedString = NOT_SET;
    if (data.isWindSpeedAlarmSet())
      windSpeedString = Integer.toString(data.getWindSpeedAlarm());
    windSpeedTextField.setText(windSpeedString);

    String windSpeed10MinString = NOT_SET;
    if (data.isWindSpeed10MinAlarmSet())
      windSpeed10MinString = Integer.toString(data.getWindSpeed10MinAlarm());
    windSpeed10MinTextField.setText(windSpeed10MinString);

    String solarString = NOT_SET;
    if (data.isSolarAlarmSet())
      solarString = Integer.toString(data.getSolarAlarm());
    solarTextField.setText(solarString);

    String rainRateString = NOT_SET;
    if (data.isRainRateAlarmSet())
      rainRateString = Integer.toString(data.getRainRateAlarm());
    rainRateTextField.setText(rainRateString);

    String rain15MinString = NOT_SET;
    if (data.isRain15MinAlarmSet())
      rain15MinString = Integer.toString(data.getRain15MinAlarm());
    rain15MinTextField.setText(rain15MinString);

    String rain24HrString = NOT_SET;
    if (data.isRain24HrAlarmSet())
      rain24HrString = Integer.toString(data.getRain24HrAlarm());
    rain24HrTextField.setText(rain24HrString);

    String rainStormString = NOT_SET;
    if (data.isRainStormAlarmSet())
      rainStormString = Integer.toString(data.getRainStormAlarm());
    rainStormTextField.setText(rainStormString);
  }

  /**
   * Method called when a button is pressed.
   */
  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equalsIgnoreCase(UPDATE_STRING))
    {
      updateEepromBarData(barRiseTextField.getText(), EepromDefs.BAR_RISE_ALARM_OFFSET, "Bar Rise");
      updateEepromBarData(barFallTextField.getText(), EepromDefs.BAR_FALL_ALARM_OFFSET, "Bar Fall");
      updateEepromTimeData(timeTextField.getText(), EepromDefs.TIME_ALARM_OFFSET, "Time");
      updateEepromTempData(lowInTempTextField.getText(), EepromDefs.LOW_TEMP_IN_ALARM_OFFSET, "Low In");
      updateEepromTempData(highInTempTextField.getText(), EepromDefs.HIGH_TEMP_IN_ALARM_OFFSET, "High In");
      updateEepromTempData(lowOutTempTextField.getText(), EepromDefs.LOW_TEMP_OUT_ALARM_OFFSET, "Low Out");
      updateEepromTempData(highOutTempTextField.getText(), EepromDefs.HIGH_TEMP_OUT_ALARM_OFFSET, "High Out");
      updateEepromTempData(lowSoil1TempTextField.getText(), EepromDefs.LOW_TEMP_SOIL1_ALARM_OFFSET, "Low Soil1");
      updateEepromTempData(highSoil1TempTextField.getText(), EepromDefs.HIGH_TEMP_SOIL1_ALARM_OFFSET, "High Soil1");
      updateEepromHumidWindData(lowInHumidTextField.getText(), EepromDefs.LOW_HUM_IN_ALARM_OFFSET, "Low In Humid");
      updateEepromHumidWindData(highInHumidTextField.getText(), EepromDefs.HIGH_HUM_IN_ALARM_OFFSET, "High In Humid");
      updateEepromHumidWindData(lowOutHumidTextField.getText(), EepromDefs.LOW_HUM_OUT_ALARM_OFFSET, "Low Out Humid");
      updateEepromHumidWindData(highOutHumidTextField.getText(), EepromDefs.HIGH_HUM_OUT_ALARM_OFFSET, "High Out Humid");
      updateEepromOtherTempData(lowDewTextField.getText(), EepromDefs.LOW_DEW_ALARM_OFFSET, "Low Dew");
      updateEepromOtherTempData(highDewTextField.getText(), EepromDefs.HIGH_DEW_ALARM_OFFSET, "High Dew");
      updateEepromOtherTempData(windChillTextField.getText(), EepromDefs.CHILL_ALARM_OFFSET, "Wind Chill");
      updateEepromTempData(heatIndexTextField.getText(), EepromDefs.HEAT_ALARM_OFFSET, "Heat Index");
      updateEepromTempData(thswTextField.getText(), EepromDefs.THSW_ALARM_OFFSET, "THSW");
      updateEepromHumidWindData(windSpeedTextField.getText(), EepromDefs.SPEED_ALARM_OFFSET, "Wind Speed");
      updateEepromHumidWindData(windSpeed10MinTextField.getText(), EepromDefs.SPEED_10MIN_ALARM_OFFSET, "10 Min Wind Speed");
      updateEepromSolarData(solarTextField.getText(), EepromDefs.SOLAR_ALARM_OFFSET, "Solar");
      updateEepromRainData(rainRateTextField.getText(), EepromDefs.RAIN_RATE_ALARM_OFFSET, "Rain Rate");
      updateEepromRainData(rain15MinTextField.getText(), EepromDefs.RAIN_15MIN_ALARM_OFFSET, "15 Min Rain");
      updateEepromRainData(rain24HrTextField.getText(), EepromDefs.RAIN_24HR_ALARM_OFFSET, "24 Hr Rain");
      updateEepromRainData(rainStormTextField.getText(), EepromDefs.RAIN_STORM_ALARM_OFFSET, "Rain Storm");

      setVisible(false);
    }
    else if (e.getActionCommand().equalsIgnoreCase(CANCEL_STRING))
    {
      setVisible(false);
    }
  }

  /**
   * Internal method to check if a time text field is set, if set go ahead and update the value based on an
   * offset value.  The time field is converted to Davis time: hours * 100 + minutes.
   *
   * @param stringValue The string value of what is in the given text field.
   * @param offset If text field is set then use this offset to update.
   * @param dataType The textual description of the field being udpated.  Used in the error dialog box.
   */
  private void updateEepromTimeData(String stringValue, byte offset, String dataType)
  {
    if (!stringValue.equalsIgnoreCase(NOT_SET) && !stringValue.equalsIgnoreCase(""))
    {
      try
      {
        short value = Short.parseShort(stringValue);
        if (value < 0 || value > 2400)
        {
          JOptionPane.showMessageDialog(this, dataType + " (0 to 2400): invalid value");
        }
        else
        {
          byte[] byteArray = ByteUtil.shortToByteArray(value);
          queue.writeEepromData(ConsoleCommand.EEBWR, offset, byteArray);
        }
      }
      catch (NumberFormatException e1)
      {
        JOptionPane.showMessageDialog(this, dataType + " (0 to 2400): invalid value");
      }
    }
  }

  /**
   * Internal method to check if a barometric text field is set, if set go ahead and update the value based on an
   * offset value.  The pressure fields are multiplied by 1000 so that every value is a positive integer.
   *
   * @param stringValue The string value of what is in the given text field.
   * @param offset If text field is set then use this offset to update.
   * @param dataType The textual description of the field being udpated.  Used in the error dialog box.
   */
  private void updateEepromBarData(String stringValue, byte offset, String dataType)
  {
    if (!stringValue.equalsIgnoreCase(NOT_SET) && !stringValue.equalsIgnoreCase(""))
    {
      try
      {
        Float value = Float.parseFloat(stringValue);
        if (value < 0.001 || value > 0.255)
        {
          JOptionPane.showMessageDialog(this, dataType + " (0.001 to 0.255): invalid value");
        }
        else
        {
          byte[] byteArray = {(byte)(value * 1000)};
          queue.writeEepromData(ConsoleCommand.EEBWR, offset, byteArray);
        }
      }
      catch (NumberFormatException e1)
      {
        JOptionPane.showMessageDialog(this, dataType + " (0.001 to 0.255): invalid value");
      }
    }
  }

  /**
   * Internal method to check if a temperature text field is set, if set go ahead and update the value based on an
   * offset value.  The temperature fields are shifted 90 degrees so that every value is positive.
   *
   * @param stringValue The string value of what is in the given text field.
   * @param offset If text field is set then use this offset to update.
   * @param dataType The textual description of the field being udpated.  Used in the error dialog box.
   */
  private void updateEepromTempData(String stringValue, byte offset, String dataType)
  {
    if (!stringValue.equalsIgnoreCase(NOT_SET) && !stringValue.equalsIgnoreCase(""))
    {
      try
      {
        int value = Integer.parseInt(stringValue);
        if (value < -90 || value > 164)
        {
          JOptionPane.showMessageDialog(this, dataType + " Temp (-90 to 164): invalid value");
        }
        else
        {
          byte[] byteArray = {(byte)(value + 90)};
          queue.writeEepromData(ConsoleCommand.EEBWR, offset, byteArray);
        }
      }
      catch (NumberFormatException e1)
      {
        JOptionPane.showMessageDialog(this, dataType + " Temp (-90 to 164): invalid value");
      }
    }
  }

  /**
   * Internal method to check if a temperature text field is set, if set go ahead and update the value based on an
   * offset value.  The temperature fields are shifted 120 degrees so that every value is positive.  This is for the
   * other temperature fields such as dew point and wind chill.
   *
   * @param stringValue The string value of what is in the given text field.
   * @param offset If text field is set then use this offset to update.
   * @param dataType The textual description of the field being udpated.  Used in the error dialog box.
   */
  private void updateEepromOtherTempData(String stringValue, byte offset, String dataType)
  {
    if (!stringValue.equalsIgnoreCase(NOT_SET) && !stringValue.equalsIgnoreCase(""))
    {
      try
      {
        int value = Integer.parseInt(stringValue);
        if (value < -120 || value > 134)
        {
          JOptionPane.showMessageDialog(this, dataType + " Temp (-120 to 134): invalid value");
        }
        else
        {
          byte[] byteArray = {(byte)(value + 120)};
          queue.writeEepromData(ConsoleCommand.EEBWR, offset, byteArray);
        }
      }
      catch (NumberFormatException e1)
      {
        JOptionPane.showMessageDialog(this, dataType + " Temp (-120 to 134): invalid value");
      }
    }
  }

  /**
   * Internal method to check if a humidity or wind text field is set, if set go ahead and update the value based on an
   * offset value.  Values are used straight up.
   *
   * @param stringValue The string value of what is in the given text field.
   * @param offset If text field is set then use this offset to update.
   * @param dataType The textual description of the field being udpated.  Used in the error dialog box.
   */
  private void updateEepromHumidWindData(String stringValue, byte offset, String dataType)
  {
    if (!stringValue.equalsIgnoreCase(NOT_SET) && !stringValue.equalsIgnoreCase(""))
    {
      try
      {
        int value = Integer.parseInt(stringValue);
        if (value < 0 || value > 100)
        {
          JOptionPane.showMessageDialog(this, dataType + " (0 to 100): invalid value");
        }
        else
        {
          byte[] byteArray = {(byte)value};
          queue.writeEepromData(ConsoleCommand.EEBWR, offset, byteArray);
        }
      }
      catch (NumberFormatException e1)
      {
        JOptionPane.showMessageDialog(this, dataType + " (0 to 100): invalid value");
      }
    }
  }

  /**
   * Internal method to check if a solar text field is set, if set go ahead and update the value based on an
   * offset value.  Solar values are two byte value used straight up.
   *
   * @param stringValue The string value of what is in the given text field.
   * @param offset If text field is set then use this offset to update.
   * @param dataType The textual description of the field being udpated.  Used in the error dialog box.
   */
  private void updateEepromSolarData(String stringValue, byte offset, String dataType)
  {
    if (!stringValue.equalsIgnoreCase(NOT_SET) && !stringValue.equalsIgnoreCase(""))
    {
      try
      {
        short value = Short.parseShort(stringValue);
        if (value < 0 || value > 1800)
        {
          JOptionPane.showMessageDialog(this, dataType + " (0 to 1800): invalid value");
        }
        else
        {
          byte[] byteArray = ByteUtil.shortToByteArray(value);
          queue.writeEepromData(ConsoleCommand.EEBWR, offset, byteArray);
        }
      }
      catch (NumberFormatException e1)
      {
        JOptionPane.showMessageDialog(this, dataType + " (0 to 1800): invalid value");
      }
    }
  }

  /**
   * Internal method to check if a rain text field is set, if set go ahead and update the value based on an
   * offset value.  The rain field values are straight up clicks.
   *
   * @param stringValue The string value of what is in the given text field.
   * @param offset If text field is set then use this offset to update.
   * @param dataType The textual description of the field being udpated.  Used in the error dialog box.
   */
  private void updateEepromRainData(String stringValue, byte offset, String dataType)
  {
    if (!stringValue.equalsIgnoreCase(NOT_SET) && !stringValue.equalsIgnoreCase(""))
    {
      try
      {
        short value = Short.parseShort(stringValue);
        if (value < 0 || value > 255)
        {
          JOptionPane.showMessageDialog(this, dataType + " (0 to 65534): invalid value");
        }
        else
        {
          byte[] byteArray = ByteUtil.shortToByteArray(value);
          queue.writeEepromData(ConsoleCommand.EEBWR, offset, byteArray);
        }
      }
      catch (NumberFormatException e1)
      {
        JOptionPane.showMessageDialog(this, dataType + " (0 to 65534): invalid value");
      }
    }
  }

  /**
   * Method called to test the dialog box.
   */
  public static void main (String[] args)
  {
    JFrame frame = new JFrame();
    new AlarmConfigDialog(frame, new AlarmData());
  }
}
