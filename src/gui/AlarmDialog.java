/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the Alarm Dialog box.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import data.consolerecord.LoopData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlarmDialog extends JDialog implements ActionListener
{
  // Button text defintions.
  private static final String OK_STRING = "OK";
  private static final String ON_STRING = "ON";
  private static final String OFF_STRING = "OFF";
  private Font boldFont = new Font("Bold", Font.BOLD, 14);

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
  public AlarmDialog(JFrame parent, LoopData data)
  {
    super(parent, "Alarm Dialog Box", true);

    JLabel highLabel = new JLabel("High");
    JLabel lowLabel = new JLabel("Low");
    JLabel pressureLabel = new JLabel("Pressure Trend:", SwingConstants.RIGHT);
    JLabel timeLabel = new JLabel("Time:", SwingConstants.RIGHT);
    JLabel inTempLabel = new JLabel("In Temp:", SwingConstants.RIGHT);
    JLabel outTempLabel = new JLabel("Out Temp:", SwingConstants.RIGHT);
    JLabel soil1TempLabel = new JLabel("Soil1 Temp:", SwingConstants.RIGHT);
    JLabel inHumidLabel = new JLabel("In Humid:", SwingConstants.RIGHT);
    JLabel outHumidLabel = new JLabel("Out Humid:", SwingConstants.RIGHT);
    JLabel dewLabel = new JLabel("Dew Point:", SwingConstants.RIGHT);
    JLabel windChillLabel = new JLabel("Wind Chill:", SwingConstants.RIGHT);
    JLabel heatIndexLabel = new JLabel("Heat Index:", SwingConstants.RIGHT);
    JLabel thswLabel = new JLabel("THSW:", SwingConstants.RIGHT);
    JLabel windSpeedLabel = new JLabel("Wind Speed:", SwingConstants.RIGHT);
    JLabel windSpeed10MinLabel = new JLabel("Speed 10Min:", SwingConstants.RIGHT);
    JLabel solarLabel = new JLabel("Solar:", SwingConstants.RIGHT);
    JLabel rainRateLabel = new JLabel("Rain Rate:", SwingConstants.RIGHT);
    JLabel rain15MinLabel = new JLabel("Rain 15Min:", SwingConstants.RIGHT);
    JLabel rain24HrLabel = new JLabel("Rain 24Hr:", SwingConstants.RIGHT);
    JLabel rainStormLabel = new JLabel("Rain Storm:", SwingConstants.RIGHT);

    barRiseTextField = new JTextField();
    barRiseTextField.setEnabled(false);
    barFallTextField = new JTextField();
    barFallTextField.setEnabled(false);
    timeTextField = new JTextField();
    timeTextField.setEnabled(false);
    lowInTempTextField = new JTextField();
    lowInTempTextField.setEnabled(false);
    highInTempTextField = new JTextField();
    highInTempTextField.setEnabled(false);
    lowOutTempTextField = new JTextField();
    lowOutTempTextField.setEnabled(false);
    highOutTempTextField = new JTextField();
    highOutTempTextField.setEnabled(false);
    lowSoil1TempTextField = new JTextField();
    lowSoil1TempTextField.setEnabled(false);
    highSoil1TempTextField = new JTextField();
    highSoil1TempTextField.setEnabled(false);
    lowInHumidTextField = new JTextField();
    lowInHumidTextField.setEnabled(false);
    highInHumidTextField = new JTextField();
    highInHumidTextField.setEnabled(false);
    lowOutHumidTextField = new JTextField();
    lowOutHumidTextField.setEnabled(false);
    highOutHumidTextField = new JTextField();
    highOutHumidTextField.setEnabled(false);
    lowDewTextField = new JTextField();
    lowDewTextField.setEnabled(false);
    highDewTextField = new JTextField();
    highDewTextField.setEnabled(false);
    windChillTextField = new JTextField();
    windChillTextField.setEnabled(false);
    heatIndexTextField = new JTextField();
    heatIndexTextField.setEnabled(false);
    thswTextField = new JTextField();
    thswTextField.setEnabled(false);
    windSpeedTextField = new JTextField();
    windSpeedTextField.setEnabled(false);
    windSpeed10MinTextField = new JTextField();
    windSpeed10MinTextField.setEnabled(false);
    solarTextField = new JTextField();
    solarTextField.setEnabled(false);
    rainRateTextField = new JTextField();
    rainRateTextField.setEnabled(false);
    rain15MinTextField = new JTextField();
    rain15MinTextField.setEnabled(false);
    rain24HrTextField = new JTextField();
    rain24HrTextField.setEnabled(false);
    rainStormTextField = new JTextField();
    rainStormTextField.setEnabled(false);

    setNewValues(data);

    JPanel commPanel = new JPanel();
    commPanel.setLayout(new GridLayout(12, 5, 10, 1));

    commPanel.add(new JLabel());
    commPanel.add(lowLabel);
    commPanel.add(highLabel);
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());

    commPanel.add(pressureLabel);
    commPanel.add(barFallTextField);
    commPanel.add(barRiseTextField);
    commPanel.add(windSpeedLabel);
    commPanel.add(windSpeedTextField);

    commPanel.add(inTempLabel);
    commPanel.add(lowInTempTextField);
    commPanel.add(highInTempTextField);
    commPanel.add(windSpeed10MinLabel);
    commPanel.add(windSpeed10MinTextField);

    commPanel.add(outTempLabel);
    commPanel.add(lowOutTempTextField);
    commPanel.add(highOutTempTextField);
    commPanel.add(rainRateLabel);
    commPanel.add(rainRateTextField);

    commPanel.add(soil1TempLabel);
    commPanel.add(lowSoil1TempTextField);
    commPanel.add(highSoil1TempTextField);
    commPanel.add(rain15MinLabel);
    commPanel.add(rain15MinTextField);

    commPanel.add(inHumidLabel);
    commPanel.add(lowInHumidTextField);
    commPanel.add(highInHumidTextField);
    commPanel.add(rain24HrLabel);
    commPanel.add(rain24HrTextField);

    commPanel.add(outHumidLabel);
    commPanel.add(lowOutHumidTextField);
    commPanel.add(highOutHumidTextField);
    commPanel.add(rainStormLabel);
    commPanel.add(rainStormTextField);

    commPanel.add(dewLabel);
    commPanel.add(lowDewTextField);
    commPanel.add(highDewTextField);
    commPanel.add(timeLabel);
    commPanel.add(timeTextField);

    commPanel.add(windChillLabel);
    commPanel.add(windChillTextField);
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());

    commPanel.add(heatIndexLabel);
    commPanel.add(new JLabel());
    commPanel.add(heatIndexTextField);
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());

    commPanel.add(thswLabel);
    commPanel.add(new JLabel());
    commPanel.add(thswTextField);
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());

    commPanel.add(solarLabel);
    commPanel.add(new JLabel());
    commPanel.add(solarTextField);
    commPanel.add(new JLabel());
    commPanel.add(new JLabel());

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 4));

    JButton okButton = new JButton(OK_STRING);
    okButton.setActionCommand(OK_STRING);
    okButton.addActionListener(this);

    buttonPanel.add(new JLabel());
    buttonPanel.add(okButton);
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
  public void setNewValues(LoopData data)
  {
    setTextField (barRiseTextField, (data.getInsideAlarms() & LoopData.RISING_BAR_TREND_ALARM) == 0);
    setTextField (barFallTextField, (data.getInsideAlarms() & LoopData.FALLING_BAR_TREND_ALARM) == 0);
    setTextField (timeTextField, (data.getInsideAlarms() & LoopData.TIME_ALARM) == 0);
    setTextField (lowInTempTextField, (data.getInsideAlarms() & LoopData.LOW_INSIDE_TEMP_ALARM) == 0);
    setTextField (highInTempTextField, (data.getInsideAlarms() & LoopData.HIGH_INSIDE_TEMP_ALARM) == 0);
    setTextField (lowOutTempTextField, (data.getOutsideAlarmsByte1() & LoopData.LOW_OUTSIDE_TEMP_ALARM) == 0);
    setTextField (highOutTempTextField,(data.getOutsideAlarmsByte1() & LoopData.HIGH_OUTSIDE_TEMP_ALARM) == 0);
    setTextField (lowSoil1TempTextField, (data.getExtraTempAlarms1() & LoopData.LOW_SOIL1_TEMP_ALARM) == 0);
    setTextField (highSoil1TempTextField, (data.getExtraTempAlarms1() & LoopData.HIGH_SOIL1_TEMP_ALARM) == 0);
    setTextField (lowInHumidTextField, (data.getInsideAlarms() & LoopData.LOW_INSIDE_HUMID_ALARM) == 0);
    setTextField (highInHumidTextField, (data.getInsideAlarms() & LoopData.HIGH_INSIDE_HUMID_ALARM) == 0);
    setTextField (lowOutHumidTextField, (data.getOutsideHumidAlarms() & LoopData.LOW_OUT_HUMIDITY_ALARM) == 0);
    setTextField (highOutHumidTextField, (data.getOutsideHumidAlarms() & LoopData.HIGH_OUT_HUMIDITY_ALARM) == 0);
    setTextField (lowDewTextField, (data.getOutsideAlarmsByte1() & LoopData.LOW_DEWPOINT_ALARM) == 0);
    setTextField (highDewTextField, (data.getOutsideAlarmsByte1() & LoopData.HIGH_DEWPOINT_ALARM) == 0);
    setTextField (windChillTextField, (data.getOutsideAlarmsByte1() & LoopData.LOW_WIND_CHILL_ALARM) == 0);
    setTextField (heatIndexTextField, (data.getOutsideAlarmsByte1() & LoopData.HIGH_HEAT_ALARM) == 0);
    setTextField (thswTextField, (data.getOutsideAlarmsByte2() & LoopData.HIGH_THSW_ALARM) == 0);
    setTextField (windSpeedTextField, (data.getOutsideAlarmsByte1() & LoopData.WIND_SPEED_ALARM) == 0);
    setTextField (windSpeed10MinTextField, (data.getOutsideAlarmsByte2() & LoopData.TEN_MINUTE_WIND_SPEED_ALARM) == 0);
    setTextField (solarTextField, (data.getOutsideAlarmsByte2() & LoopData.HIGH_SOLAR_RADIATION_ALARM) == 0);
    setTextField (rainRateTextField, (data.getRainAlarms() & LoopData.HIGH_RAIN_RATE_ALARM) == 0);
    setTextField (rain15MinTextField, (data.getRainAlarms() & LoopData.FIFTEEN_MINUTE_RAIN_RATE_ALARM) == 0);
    setTextField (rain24HrTextField, (data.getRainAlarms() & LoopData.TWENTY_FOUR_HOUR_RAIN_RATE_ALARM) == 0);
    setTextField (rainStormTextField, (data.getRainAlarms() & LoopData.STORM_TOTAL_ALARM) == 0);
  }

  /**
   * Internal method to set the given text field as either on or off.
   *
   * @param field The text field to set.
   * @param state The state of the text field.
   */
  private void setTextField(JTextField field, boolean state)
  {
    if (state)
      field.setText(OFF_STRING);
    else
    {
      field.setText(ON_STRING);
      field.setFont(boldFont);
      field.setBackground(Color.YELLOW);
    }
  }

  /**
   * Method called when a button is pressed.
   */
  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equalsIgnoreCase(OK_STRING))
    {
      // TODO: implement

      setVisible(false);
    }
  }
}
