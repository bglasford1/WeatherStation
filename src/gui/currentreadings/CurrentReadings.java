/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for displaying the current readings
            window.  It also handles min/max interval changes.

  Mods:		  09/01/21 Initial Release.
*/
package gui.currentreadings;

import data.consolerecord.HiLoData;
import data.consolerecord.LoopData;
import data.dbrecord.WindDirection;
import dbif.DatabaseReader;
import forecast.AlertTimer;
import forecast.NOAAForecastJSON;
import gui.AlarmDialog;
import gui.AlertDialog;
import gui.MinMaxInterval;
import serialdriver.Command;
import serialdriver.CommandListener;
import serialdriver.ConsoleCmdQueue;
import serialdriver.ConsoleCommand;
import util.ConfigProperties;
import util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class CurrentReadings extends JPanel implements ActionListener, CommandListener
{
  // Definitions of subordinate objects.
  private MinMaxInterval minMaxInterval;
  private HiLoData hiloData;
  private final JFrame parent;
  private LoopData loopData;

  private final TemperatureGauge indoorThermometer = new TemperatureGauge("Indoor", 60, 80);
  private final TemperatureGauge outdoorThermometer = new TemperatureGauge("Outdoor", 0, 100);
  private final TemperatureGauge greenhouseThermometer = new TemperatureGauge("Greenhouse", 0, 130);
  private final HumidityGauge indoorHumidity = new HumidityGauge("Indoor",0, 50);
  private final HumidityGauge outdoorHumidity = new HumidityGauge("Outdoor", 0, 100);
  private final SolarGauge solarGauge = new SolarGauge();
  private final PressureGauge pressureGauge = new PressureGauge();
  private final WindSpeedGauge windSpeedGauge = new WindSpeedGauge();
  private final WindDirectionGauge windDirGauge = new WindDirectionGauge(MinMaxInterval.hourly);
  private final MoonDisplay moonDisplay = new MoonDisplay();
  private final MiscTempsDisplay miscTempsDisplay = new MiscTempsDisplay();
  private final RainGauge rainGauge = new RainGauge();
  private final CelestialGauge celestialGauge = new CelestialGauge();
  private final ForecastDisplay forecastDisplay = new ForecastDisplay();
  private final NOAAForecastJSON noaaForecast = new NOAAForecastJSON();
  private final DatabaseReader dbReader = DatabaseReader.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private final Logger logger = Logger.getInstance();

  private static final String ALERT_STRING = "ALERT";
  private static final String ALARM_STRING = "ALARM";
  private final JButton alertButton = new JButton("Alert");
  private final JButton commButton = new JButton("        ");
  private final JButton alarmButton = new JButton("Alarm");
  private AlarmDialog alarmDialog;

  /**
   * Constructor.
   */
  public CurrentReadings(MinMaxInterval minMaxInterval, JFrame parent)
  {
    this.minMaxInterval = minMaxInterval;
    this.parent = parent;

    setBackground(Color.white);
    setForeground(Color.black);

    SpringLayout layout = new SpringLayout();
    this.setLayout(layout);

    // Setup Alert button.
    layout.putConstraint(SpringLayout.WEST, alertButton,
                         2,
                         SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.NORTH, alertButton,
                         2,
                         SpringLayout.NORTH, this);
    alertButton.setActionCommand(ALERT_STRING);
    alertButton.addActionListener(this);

    // Setup Alarm button.
    layout.putConstraint(SpringLayout.WEST, alarmButton,
                         780,
                         SpringLayout.WEST, alertButton);
    layout.putConstraint(SpringLayout.NORTH, alarmButton,
                         2,
                         SpringLayout.NORTH, this);
    alarmButton.setActionCommand(ALARM_STRING);
    alarmButton.addActionListener(this);

    // Setup Comm button.
    layout.putConstraint(SpringLayout.WEST, commButton,
                         80,
                         SpringLayout.WEST, alarmButton);
    layout.putConstraint(SpringLayout.NORTH, commButton,
                         2,
                         SpringLayout.NORTH, this);

    // Set initial color for alert button
    setAlertButtonSeverity();

    this.add(alertButton);
    this.add(alarmButton);
    this.add(commButton);

    ConsoleCmdQueue.getInstance().addListener(this);
    AlertTimer alertTimer = new AlertTimer(this);
    alertTimer.startTimer();
  }

  /**
   * Method called to set a new min/max interval.
   */
  public void setMinMaxInterval(MinMaxInterval interval)
  {
    this.minMaxInterval = interval;

    // Update the min/max data on the screen.
    updateMinMax(hiloData);
  }

  /**
   * Method to change the alert button severity (i.e. color) based on current NOAA forecast.
   */
  public void setAlertButtonSeverity()
  {
    try
    {
      NOAAForecastJSON.Severity severity = noaaForecast.getSeverity();
      if (severity != null)
      {
        switch (severity)
        {
          case Extreme:
            alertButton.setBackground(Color.RED);
            alertButton.setOpaque(true);
            break;
          case Severe:
            alertButton.setBackground(Color.PINK);
            alertButton.setOpaque(true);
            break;
          case Moderate:
            alertButton.setBackground(Color.ORANGE);
            alertButton.setOpaque(true);
            break;
          case Minor:
            alertButton.setBackground(Color.YELLOW);
            alertButton.setOpaque(true);
            break;
          case Unknown: // clear alarm
            alertButton.setBackground(PROPS.getBackgroundColor());
            alertButton.setOpaque(false);
            break;
          default:
            alertButton.setBackground(PROPS.getBackgroundColor());
            alertButton.setOpaque(false);
            break;
        }
      }
    }
    catch (Exception e)
    {
      logger.logData(e.getLocalizedMessage());
    }
  }

  /**
   * Method called whenever it is time to paint the panel.
   */
  public void paintComponent(Graphics g)
  {
    // Always paint the parent.
    super.paintComponent(g);

    // Cast to a 2D graphic object and set the rendering hint.
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Draw the gauges.
    forecastDisplay.paintGauge(this, g2, 0, 0);
    moonDisplay.paintGauge(this, g2, 530, 0);
    miscTempsDisplay.paintGauge(g2, 790, 30);
    rainGauge.paintGauge(g2, 790, 150);
    pressureGauge.paintGauge(g2, 150, 52);
    windSpeedGauge.paintGauge(g2, 550, 52);
    windDirGauge.paintGauge(g2, 500, 52);
    indoorThermometer.paintGauge(g2, 70, 225);
    outdoorThermometer.paintGauge(g2, 190, 225);
    indoorHumidity.paintGauge(g2, 310, 225);
    outdoorHumidity.paintGauge(g2, 420, 225);
    greenhouseThermometer.paintGauge(g2, 540, 225);
    solarGauge.paintGauge(g2, 680, 225);
    celestialGauge.paintTimes(g2, 790, 280);
    celestialGauge.paintGauge(g2, 0, 492);
  }

  /**
   * Method called to update the readings on the screen when new loop date is
   * received.  This data is typically current data.
   */
  public void updateReadings(LoopData loopData)
  {
    this.loopData = loopData;

    // Update each reading.
    pressureGauge.setCurrent(loopData.getPressure());
    pressureGauge.setTrend(loopData.getPressureTrend());
    windSpeedGauge.setCurrent(loopData.getWindSpeed());
    windSpeedGauge.setAverageSpeed(loopData.getAverageWindSpeed());
    windDirGauge.setDirection(loopData.getWindDirection());
    indoorThermometer.setCurrent(loopData.getInsideTemp());
    outdoorThermometer.setCurrent(loopData.getOutsideTemp());
    greenhouseThermometer.setCurrent(loopData.getSoilTemp1());
    indoorHumidity.setCurrent(loopData.getInsideHumidity());
    outdoorHumidity.setCurrent(loopData.getOutsideHumidity());
    solarGauge.setCurrent(loopData.getSolarRadiation());
    rainGauge.setStormAmount(loopData.getStormRate());
    rainGauge.setDailyAmount(loopData.getDailyRain());
    rainGauge.setMonthlyAmount(loopData.getMonthlyRain());
    rainGauge.setYearlyAmount(loopData.getYearlyRain());
    rainGauge.setRate(loopData.getRainRate());
    rainGauge.setStormDate(loopData.getStartStormDate());
    if (loopData.getDailyRain() > 0)
    {
      rainGauge.setLastRain(LocalDate.now().getMonthValue() + "/" + LocalDate.now().getDayOfMonth() + "/" +
                              LocalDate.now().getYear());
    }
    else
    {
      rainGauge.setLastRain(dbReader.getLastRainDate());
    }

    miscTempsDisplay.setValues
      (loopData.getOutsideTemp(),
       loopData.getOutsideHumidity(),
       loopData.getWindSpeed(),
       loopData.getSolarRadiation()
      );

    if (loopData.getInsideAlarms() != 0 ||
      loopData.getRainAlarms() != 0 ||
      loopData.getOutsideAlarmsByte1() != 0 ||
      loopData.getOutsideAlarmsByte2() != 0 ||
      loopData.getOutsideHumidAlarms() != 0)
    {
      alarmButton.setBackground(Color.RED);
      alarmButton.setOpaque(true);
    }

    // Now update the screen.
    this.repaint();
  }

  /**
   * Method that simply redirects the new wind direction to the wind gauge.
   *
   * @param direction The new direction.
   */
  public void addPrevailingDirection (WindDirection direction)
  {
    windDirGauge.addPrevailingDirection(direction);
  }

  /**
   * When an new DMP data record comes in, update the forecast rule on the current readings display.
   *
   * @param ruleNumber The new rule number.
   */
  public void updateForecastRule(int ruleNumber)
  {
    forecastDisplay.setForecastRule(ruleNumber);
  }

  /**
   * Method called to update the Hi/Low data on the screen.  This is based on
   * what the current interval is set to because most gauges only display one set
   * of min/max values.
   */
  public void updateMinMax(HiLoData hiloData)
  {
    // Save the hilow data.
    this.hiloData = hiloData;

    // Update each reading.
    switch (minMaxInterval)
    {
      case hourly: // Everything is set to daily except the wind gauge.
        pressureGauge.setMinimum(hiloData.getDailyLowPressure());
        pressureGauge.setMaximum(hiloData.getDailyHighPressure());
        windSpeedGauge.setMaximum(hiloData.getDailyHighWindSpeed());
        indoorThermometer.setMinimum(hiloData.getDailyLowInsideTemp());
        indoorThermometer.setMaximum(hiloData.getDailyHighInsideTemp());
        outdoorThermometer.setMinimum(hiloData.getDailyLowOutsideTemp());
        outdoorThermometer.setMaximum(hiloData.getDailyHighOutsideTemp());
        greenhouseThermometer.setMinimum(hiloData.getDailyLowSoil1Temp());
        greenhouseThermometer.setMaximum(hiloData.getDailyHighSoil1Temp());
        indoorHumidity.setMinimum(hiloData.getDailyLowInsideHumidity());
        indoorHumidity.setMaximum(hiloData.getDailyHighInsideHumidity());
        outdoorHumidity.setMinimum(hiloData.getDailyLowOutsideHumidity());
        outdoorHumidity.setMaximum(hiloData.getDailyHighOutsideHumidity());
        solarGauge.setMaximum(hiloData.getDailyHighSolarRadiation());
        windDirGauge.setTimePeriod(MinMaxInterval.hourly);
        break;
      case daily:
        pressureGauge.setMinimum(hiloData.getDailyLowPressure());
        pressureGauge.setMaximum(hiloData.getDailyHighPressure());
        windSpeedGauge.setMaximum(hiloData.getDailyHighWindSpeed());
        windDirGauge.setTimePeriod(MinMaxInterval.daily);
        indoorThermometer.setMinimum(hiloData.getDailyLowInsideTemp());
        indoorThermometer.setMaximum(hiloData.getDailyHighInsideTemp());
        outdoorThermometer.setMinimum(hiloData.getDailyLowOutsideTemp());
        outdoorThermometer.setMaximum(hiloData.getDailyHighOutsideTemp());
        greenhouseThermometer.setMinimum(hiloData.getDailyLowSoil1Temp());
        greenhouseThermometer.setMaximum(hiloData.getDailyHighSoil1Temp());
        indoorHumidity.setMinimum(hiloData.getDailyLowInsideHumidity());
        indoorHumidity.setMaximum(hiloData.getDailyHighInsideHumidity());
        outdoorHumidity.setMinimum(hiloData.getDailyLowOutsideHumidity());
        outdoorHumidity.setMaximum(hiloData.getDailyHighOutsideHumidity());
        solarGauge.setMaximum(hiloData.getDailyHighSolarRadiation());
        break;
      case monthly:
        pressureGauge.setMinimum(hiloData.getMonthlyLowPressure());
        pressureGauge.setMaximum(hiloData.getMonthlyHighPressure());
        windSpeedGauge.setMaximum(hiloData.getMonthlyHighWindSpeed());
        windDirGauge.setTimePeriod(MinMaxInterval.monthly);
        indoorThermometer.setMinimum(hiloData.getMonthlyLowInsideTemp());
        indoorThermometer.setMaximum(hiloData.getMonthlyHighInsideTemp());
        outdoorThermometer.setMinimum(hiloData.getMonthlyLowOutsideTemp());
        outdoorThermometer.setMaximum(hiloData.getMonthlyHighOutsideTemp());
        greenhouseThermometer.setMinimum(hiloData.getMonthlyLowSoil1Temp());
        greenhouseThermometer.setMaximum(hiloData.getMonthlyHighSoil1Temp());
        indoorHumidity.setMinimum(hiloData.getMonthlyLowInsideHumidity());
        indoorHumidity.setMaximum(hiloData.getMonthlyHighInsideHumidity());
        outdoorHumidity.setMinimum(hiloData.getMonthlyLowOutsideHumidity());
        outdoorHumidity.setMaximum(hiloData.getMonthlyHighOutsideHumidity());
        solarGauge.setMaximum(hiloData.getMonthlyHighSolarRadiation());
        break;
      case yearly:
        pressureGauge.setMinimum(hiloData.getYearlyLowPressure());
        pressureGauge.setMaximum(hiloData.getYearlyHighPressure());
        windSpeedGauge.setMaximum(hiloData.getYearlyHighWindSpeed());
        windDirGauge.setTimePeriod(MinMaxInterval.yearly);
        indoorThermometer.setMinimum(hiloData.getYearlyLowInsideTemp());
        indoorThermometer.setMaximum(hiloData.getYearlyHighInsideTemp());
        outdoorThermometer.setMinimum(hiloData.getYearlyLowOutsideTemp());
        outdoorThermometer.setMaximum(hiloData.getYearlyHighOutsideTemp());
        greenhouseThermometer.setMinimum(hiloData.getYearlyLowSoil1Temp());
        greenhouseThermometer.setMaximum(hiloData.getYearlyHighSoil1Temp());
        indoorHumidity.setMinimum(hiloData.getYearlyLowInsideHumidity());
        indoorHumidity.setMaximum(hiloData.getYearlyHighInsideHumidity());
        outdoorHumidity.setMinimum(hiloData.getYearlyLowOutsideHumidity());
        outdoorHumidity.setMaximum(hiloData.getYearlyHighOutsideHumidity());
        solarGauge.setMaximum(hiloData.getYearlyHighSolarRadiation());
        break;
    }

    // Now update the screen.
    this.repaint();
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equalsIgnoreCase(ALERT_STRING))
    {
      // Display the alert dialog box.
      setAlertButtonSeverity();
      new AlertDialog(parent);
    }
    else if (e.getActionCommand().equalsIgnoreCase(ALARM_STRING))
    {
      if (alarmDialog == null)
      {
        alarmDialog = new AlarmDialog(parent, loopData);
      }
      else
      {
        alarmDialog.setNewValues(loopData);
        alarmDialog.setVisible(true);
      }

      alarmButton.setBackground(Color.WHITE);
      alarmButton.setOpaque(false);
    }
  }

  /**
   * Method called when the command being executed changes.
   *
   * @param command The new command.
   */
  @Override
  public void commandChanged(Command command)
  {
    if (command == null)
    {
      commButton.setText("        ");
      commButton.setBackground(Color.WHITE);
      commButton.setOpaque(false);
    }
    else if (command.getCommand().equals(ConsoleCommand.WAKEUP))
    {
      commButton.setText("WAKEUP");
      commButton.setBackground(Color.YELLOW);
      commButton.setOpaque(true);
    }
    else
    {
      commButton.setText(command.getCommand().command().substring(0, command.getCommand().command().length() - 1));
      commButton.setBackground(Color.ORANGE);
      commButton.setOpaque(true);
    }

    // Now update the screen.
    this.repaint();
  }
}
