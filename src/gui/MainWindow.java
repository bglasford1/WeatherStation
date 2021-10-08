/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is the the main window for the weather station.  This
            class creates the menu bar and creates all major objects in the
            GUI class heirarchy.

  Mods:		  09/01/21 Initial Release.
            10/05/21 Added drought analysis.
            10/07/21 Added progress bar.
*/
package gui;

import dbif.DatabaseReader;
import gui.currentreadings.CurrentReadings;
import gui.graph.*;
import gui.rain.RainDataThread;
import gui.rain.RainDataTable;
import gui.rain.RainGraphWindow;
import gui.reports.*;
import gui.snow.SnowDataTable;
import gui.snow.SnowRawDataTable;
import gui.snow.SnowGraphWindow;
import serialdriver.*;
import util.ConfigProperties;
import util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDate;

/**
 * This class is
 */
public class MainWindow extends JFrame implements ActionListener, WindowListener
{
  // FILE sub-menu items.
  private final static String RESET_STRING = "Reset Port";
  private final static String LAMP_STRING  = "Console Light";
  private final static String TIME_STRING  = "Console Time";
  private final static String EXIT_STRING  = "Exit";

  // CONFIG sub-menu items.
  private final static String ALARM_STRING         = "Alarms";
  private final static String STATION_STRING       = "Station";
  private final static String TRANSCEIVERS_STRING  = "Transceivers";
  private final static String COMM_STRING          = "Comm";
  private final static String WXUNDERGROUND_STRING = "WX Underground";

  // DATA sub-menu items.
  private static final String STREAM_STRING        = "Stream Data";
  private static final String GRAPH_STRING         = "Graph Data";
  private static final String MODIFY_STRING        = "Modify Data";
  private static final String DROUGHT_STRING       = "Drought Analysis";
  private static final String SNOW_GRAPH_STRING    = "Snow Graph";
  private static final String SNOW_DATA_STRING     = "Snow Data";
  private static final String SNOW_RAW_DATA_STRING = "Snow Raw Data";
  private static final String RAIN_GRAPH_STRING    = "Rain Graph";
  private static final String RAIN_DATA_STRING     = "Rain Data";
  private static final String RAIN_EXPORT_STRING   = "Export Rain Data";
  private static final String DUMP_INCR_STRING     = "Dump Data";

  // REPORT sub-menu items.
  private static final String SUNRISE_SET_STRING = "Sunrise/Sunset Report";
  private static final String SUNSHINE_STRING    = "Bright Sunshine Report";
  private static final String NOAA_MONTH_STRING  = "NOAA Month Report";
  private static final String NOAA_YEAR_STRING   = "NOAA Year Report";

  // FORECAST sub-menu items.
  private static final String DAILY_FORECAST_STRING  = "Daily Forecasts";
  private static final String HOURLY_FORECAST_STRING = "Hourly Forecasts";

  // COLOR sub-menu items.
  private final static String BACKGROUND_STRING = "Background";
  private final JMenuItem inTempColorItem;
  private final JMenuItem inHumidColorItem;
  private final JMenuItem greenTempColorItem;
  private final JMenuItem outTempColorItem;
  private final JMenuItem highOutTempColorItem;
  private final JMenuItem lowOutTempColorItem;
  private final JMenuItem avgOutTempColorItem;
  private final JMenuItem outHumidColorItem;
  private final JMenuItem pressureColorItem;
  private final JMenuItem issReceptionColorItem;
  private final JMenuItem solarColorItem;
  private final JMenuItem rainColorItem;
  private final JMenuItem windSpeedColorItem;
  private final JMenuItem windDirColorItem;
  private final JMenuItem heatDDColorItem;
  private final JMenuItem coolDDColorItem;
  private final JMenuItem dewPointColorItem;
  private final JMenuItem heatIndexColorItem;
  private final JMenuItem thwColorItem;
  private final JMenuItem thswColorItem;
  private final JMenuItem etColorItem;
  private final JMenuItem windRunColorItem;
  private final JMenuItem hiWindSpeedColorItem;
  private final JMenuItem snowColorItem;

  // TEST sub-menu items.
  private final static String TEST_STRING  = "Console Test";
  private final static String DIAGS_STRING = "Console Diags";
  private final static String PI_DIAGS_STRING = "PI Diags";
  private final static String LOOP_DATA_STRING = "Loop Data";
  private final static String DMP_DATA_STRING = "DMP Data";
  private final static String HILOW_DATA_STRING = "Hi/Low Data";

  private final static String CAPTURE_DATA_NONE_STRING   = "Capture Data None";
  private final static String CAPTURE_DATA_COARSE_STRING = "Capture Data Coarse";
  private final static String CAPTURE_DATA_MEDIUM_STRING = "Capture Data Medium";
  private final static String CAPTURE_DATA_FINE_STRING   = "Capture Data Fine";

  private final JCheckBoxMenuItem loopCheckBox;
  private final JCheckBoxMenuItem dmpCheckBox;
  private final JCheckBoxMenuItem hilowCheckBox;

  // HELP sub-menu items.
  private final static String ABOUT_STRING   = "About";
  private final static String VERSION_STRING = "Console Version";
  private final static String HELP_STRING = "Help Index";

  private final JCheckBoxMenuItem lampCheckBox;

  // Main objects in the GUI class hierarchy.
  private AboutDialog aboutDialog = null;
  private HelpIndexWindow helpIndexWindow = null;
  private PiDiagsDialog piDiagsDialog = null;
  private DateInputDialog dateInputDialog = null;
  private DroughtInputDialog droughtInputDialog = null;
  private HrsOfSunshineDialog hrsOfSunshineDialog = null;
  private SunriseSunsetDialog sunriseSunsetDialog = null;
  private NoaaMonthlyDialog noaaMonthlyDialog = null;
  private NoaaAnnualDialog noaaAnnualDialog = null;
  private DailyForecastDialog dailyForecastDialog = null;
  private HourlyForecastDialog hourlyForecastDialog = null;
  private CommConfigDialog commConfigDialog = null;
  private WxServerDialog wxServerDialog = null;
  private MinMaxInterval minMaxInterval = MinMaxInterval.hourly;
  private final ConsoleCmdQueue consoleCmdQueue = ConsoleCmdQueue.getInstance();
  private final CurrentReadings currentReadings;
  private final CommandControl commandControl;
  private GraphWindow graphWindow = null;
  private StreamWindow streamWindow = null;
  private SnowGraphWindow snowGraphWindow = null;
  private RainGraphWindow rainGraphWindow = null;
  private final DatabaseReader databaseReader = DatabaseReader.getInstance();
  private final RainDataThread rainDataThread = RainDataThread.getInstance();
  private final CurrentDataTimer currentDataTimer = CurrentDataTimer.getInstance();
  private final HistoricDataTimer historicDataTimer = HistoricDataTimer.getInstance();
  private final HiLowDataTimer hiLowDataTimer = HiLowDataTimer.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private final Logger logger = Logger.getInstance();


  /**
   * This constructor sets up and displays the main window.  The first display within
   * the main window is the current settings.
   */
  public MainWindow()
  {
    logger.logData("System Started...");

    // Create the objects.
    currentReadings = new CurrentReadings(minMaxInterval, this);
    commandControl = new CommandControl(this, currentReadings);

    this.setTitle("Bill's Weather Station");

    // Define the menu bar.
    JMenu fileMenu = new JMenu("File");
    JMenuItem resetItem = createMenuItem(RESET_STRING, null);
    JMenuItem timeItem = createMenuItem(TIME_STRING, null);
    JMenuItem exitItem = createMenuItem(EXIT_STRING, null);

    lampCheckBox = new JCheckBoxMenuItem(LAMP_STRING, false);
    lampCheckBox.setActionCommand(LAMP_STRING);
    lampCheckBox.addActionListener(this);

    fileMenu.add(resetItem);
    fileMenu.add(lampCheckBox);
    fileMenu.add(timeItem);
    fileMenu.add(exitItem);

    // Define the config menu
    JMenu configMenu = new JMenu("Config");
    JMenuItem alarmItem = createMenuItem(ALARM_STRING, null);
    JMenuItem stationItem = createMenuItem(STATION_STRING, null);
    JMenuItem transceiversItem = createMenuItem(TRANSCEIVERS_STRING, null);
    JMenuItem commItem = createMenuItem(COMM_STRING, null);
    JMenuItem wxundergroundItem = createMenuItem(WXUNDERGROUND_STRING, null);

    configMenu.add(alarmItem);
    configMenu.add(stationItem);
    configMenu.add(transceiversItem);
    configMenu.add(commItem);
    configMenu.add(wxundergroundItem);

    // Define the data menu.
    JMenu dataMenu = new JMenu("Data");
    JMenuItem streamItem = createMenuItem(STREAM_STRING, null);
    JMenuItem graphItem = createMenuItem(GRAPH_STRING, null);
    JMenuItem modifyItem = createMenuItem(MODIFY_STRING, null);
    JMenuItem droughtItem = createMenuItem(DROUGHT_STRING, null);
    JMenuItem snowGraphItem = createMenuItem(SNOW_GRAPH_STRING, null);
    JMenuItem snowDataItem = createMenuItem(SNOW_DATA_STRING, null);
    JMenuItem snowRawDataItem = createMenuItem(SNOW_RAW_DATA_STRING, null);
    JMenuItem rainGraphItem = createMenuItem(RAIN_GRAPH_STRING, null);
    JMenuItem rainDataItem = createMenuItem(RAIN_DATA_STRING, null);
    JMenuItem rainDataExportItem = createMenuItem(RAIN_EXPORT_STRING, null);
    JMenuItem dumpIncrItem = createMenuItem(DUMP_INCR_STRING, null);

    dataMenu.add(streamItem);
    dataMenu.add(graphItem);
    dataMenu.add(modifyItem);
    dataMenu.add(droughtItem);
    dataMenu.add(new JSeparator());
    dataMenu.add(snowGraphItem);
    dataMenu.add(snowDataItem);
    dataMenu.add(snowRawDataItem);
    dataMenu.add(new JSeparator());
    dataMenu.add(rainGraphItem);
    dataMenu.add(rainDataItem);
    dataMenu.add(rainDataExportItem);
    dataMenu.add(new JSeparator());
    dataMenu.add(dumpIncrItem);

    // Define the reports menu.
    JMenu reportsMenu = new JMenu("Reports");
    JMenuItem sunriseSetItem = createMenuItem(SUNRISE_SET_STRING, null);
    JMenuItem sunshineItem = createMenuItem(SUNSHINE_STRING, null);
    JMenuItem noaaMonthItem = createMenuItem(NOAA_MONTH_STRING, null);
    JMenuItem noaaYearItem = createMenuItem(NOAA_YEAR_STRING, null);

    reportsMenu.add(sunriseSetItem);
    reportsMenu.add(sunshineItem);
    reportsMenu.add(noaaMonthItem);
    reportsMenu.add(noaaYearItem);

    // Define the forecasts menu.
    JMenu forecastMenu = new JMenu("Forecasts");
    JMenuItem dailyForecastItem = createMenuItem(DAILY_FORECAST_STRING, null);
    JMenuItem hourlyForecastItem = createMenuItem(HOURLY_FORECAST_STRING, null);

    forecastMenu.add(dailyForecastItem);
    forecastMenu.add(hourlyForecastItem);

    // Define the current readings menu.
    JMenu currentReadingsMenu = new JMenu("Min/Max");

    JRadioButtonMenuItem hourlyBox = new JRadioButtonMenuItem(GraphDefs.HOUR_STRING, true);
    hourlyBox.setActionCommand(GraphDefs.HOUR_STRING);
    hourlyBox.addActionListener(this);

    JRadioButtonMenuItem dailyBox = new JRadioButtonMenuItem(GraphDefs.DAY_STRING);
    dailyBox.setActionCommand(GraphDefs.DAY_STRING);
    dailyBox.addActionListener(this);

    JRadioButtonMenuItem monthlyBox = new JRadioButtonMenuItem(GraphDefs.MONTH_STRING);
    monthlyBox.setActionCommand(GraphDefs.MONTH_STRING);
    monthlyBox.addActionListener(this);

    JRadioButtonMenuItem yearlyBox = new JRadioButtonMenuItem(GraphDefs.YEAR_STRING);
    yearlyBox.setActionCommand(GraphDefs.YEAR_STRING);
    yearlyBox.addActionListener(this);

    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(hourlyBox);
    buttonGroup.add(dailyBox);
    buttonGroup.add(monthlyBox);
    buttonGroup.add(yearlyBox);

    currentReadingsMenu.add(hourlyBox);
    currentReadingsMenu.add(dailyBox);
    currentReadingsMenu.add(monthlyBox);
    currentReadingsMenu.add(yearlyBox);

    // Define the color menu.
    JMenu colorMenu = new JMenu("Color");
    JMenuItem backgroundColorItem = createMenuItem(BACKGROUND_STRING, PROPS.getBackgroundColor());
    inTempColorItem = createMenuItem(GraphDefs.INTEMP_STRING, PROPS.getInsideTempColor());
    inHumidColorItem = createMenuItem(GraphDefs.INHUMID_STRING, PROPS.getInsideHumidColor());
    greenTempColorItem = createMenuItem(GraphDefs.GREENHOUSE_TEMP_STRING, PROPS.getGreenTempColor());
    outTempColorItem = createMenuItem(GraphDefs.OUTTEMP_STRING, PROPS.getOutsideTempColor());
    highOutTempColorItem = createMenuItem(GraphDefs.HIGH_OUTTEMP_STRING, PROPS.getHighOutsideTempColor());
    lowOutTempColorItem = createMenuItem(GraphDefs.LOW_OUTTEMP_STRING, PROPS.getLowOutsideTempColor());
    avgOutTempColorItem = createMenuItem(GraphDefs.AVG_OUTTEMP_STRING, PROPS.getAvgOutsideTempColor());
    outHumidColorItem = createMenuItem(GraphDefs.OUTHUMID_STRING, PROPS.getOutsideHumidColor());
    rainColorItem = createMenuItem(GraphDefs.RAINFALL_STRING, PROPS.getRainColor());
    issReceptionColorItem = createMenuItem(GraphDefs.ISS_RECEPTION_STRING, PROPS.getIssReceptionColor());
    pressureColorItem = createMenuItem(GraphDefs.PRESSURE_STRING, PROPS.getPressureColor());
    solarColorItem = createMenuItem(GraphDefs.SOLAR_RAD_STRING, PROPS.getSolarColor());
    windSpeedColorItem = createMenuItem(GraphDefs.WIND_SPEED_STRING, PROPS.getWindSpeedColor());
    windDirColorItem = createMenuItem(GraphDefs.WIND_DIR_STRING, PROPS.getWindDirColor());
    heatDDColorItem = createMenuItem(GraphDefs.HEAT_DD_STRING, PROPS.getHeatDDColor());
    coolDDColorItem = createMenuItem(GraphDefs.COOL_DD_STRING, PROPS.getCoolDDColor());
    dewPointColorItem = createMenuItem(GraphDefs.DEW_POINT_STRING, PROPS.getDewPointColor());
    heatIndexColorItem = createMenuItem(GraphDefs.HEAT_INDEX_STRING, PROPS.getHeatIndexColor());
    thwColorItem = createMenuItem(GraphDefs.THW_STRING, PROPS.getThwColor());
    thswColorItem = createMenuItem(GraphDefs.THSW_STRING, PROPS.getThswColor());
    etColorItem = createMenuItem(GraphDefs.ET_STRING, PROPS.getEtColor());
    windRunColorItem = createMenuItem(GraphDefs.WIND_RUN_STRING, PROPS.getWindRunColor());
    hiWindSpeedColorItem = createMenuItem(GraphDefs.HIGH_WIND_SPEED_STRING, PROPS.getHighWindSpeedColor());
    snowColorItem = createMenuItem(GraphDefs.SNOW_STRING, PROPS.getSnowLineColor());

    colorMenu.add(backgroundColorItem);
    colorMenu.add(inTempColorItem);
    colorMenu.add(inHumidColorItem);
    colorMenu.add(greenTempColorItem);
    colorMenu.add(outTempColorItem);
    colorMenu.add(highOutTempColorItem);
    colorMenu.add(lowOutTempColorItem);
    colorMenu.add(avgOutTempColorItem);
    colorMenu.add(outHumidColorItem);
    colorMenu.add(rainColorItem);
    colorMenu.add(issReceptionColorItem);
    colorMenu.add(pressureColorItem);
    colorMenu.add(solarColorItem);
    colorMenu.add(windSpeedColorItem);
    colorMenu.add(windDirColorItem);
    colorMenu.add(heatDDColorItem);
    colorMenu.add(coolDDColorItem);
    colorMenu.add(dewPointColorItem);
    colorMenu.add(heatIndexColorItem);
    colorMenu.add(thwColorItem);
    colorMenu.add(thswColorItem);
    colorMenu.add(etColorItem);
    colorMenu.add(windRunColorItem);
    colorMenu.add(hiWindSpeedColorItem);
    colorMenu.add(snowColorItem);

    // Define the test menu.
    JMenu testMenu = new JMenu("Test");
    JMenuItem testItem = createMenuItem(TEST_STRING, null);
    JMenuItem diagsItem = createMenuItem(DIAGS_STRING, null);
    JMenuItem cpuDiagsItem = createMenuItem(PI_DIAGS_STRING, null);

    loopCheckBox = new JCheckBoxMenuItem(LOOP_DATA_STRING, true);
    loopCheckBox.setActionCommand(LOOP_DATA_STRING);
    loopCheckBox.addActionListener(this);
    loopCheckBox.setState(true);

    dmpCheckBox = new JCheckBoxMenuItem(DMP_DATA_STRING, true);
    dmpCheckBox.setActionCommand(DMP_DATA_STRING);
    dmpCheckBox.addActionListener(this);
    dmpCheckBox.setState(true);

    hilowCheckBox = new JCheckBoxMenuItem(HILOW_DATA_STRING, true);
    hilowCheckBox.setActionCommand(HILOW_DATA_STRING);
    hilowCheckBox.addActionListener(this);
    hilowCheckBox.setState(true);

    // Define the config menu
    JMenu captureMenu = new JMenu("Capture Data");

    JRadioButtonMenuItem noneBox = new JRadioButtonMenuItem(CAPTURE_DATA_NONE_STRING, true);
    noneBox.setActionCommand(CAPTURE_DATA_NONE_STRING);
    noneBox.addActionListener(this);

    JRadioButtonMenuItem coarseBox = new JRadioButtonMenuItem(CAPTURE_DATA_COARSE_STRING);
    coarseBox.setActionCommand(CAPTURE_DATA_COARSE_STRING);
    coarseBox.addActionListener(this);

    JRadioButtonMenuItem mediumBox = new JRadioButtonMenuItem(CAPTURE_DATA_MEDIUM_STRING);
    mediumBox.setActionCommand(CAPTURE_DATA_MEDIUM_STRING);
    mediumBox.addActionListener(this);

    JRadioButtonMenuItem fineBox = new JRadioButtonMenuItem(CAPTURE_DATA_FINE_STRING);
    fineBox.setActionCommand(CAPTURE_DATA_FINE_STRING);
    fineBox.addActionListener(this);

    ButtonGroup captureButtonGroup = new ButtonGroup();
    captureButtonGroup.add(noneBox);
    captureButtonGroup.add(coarseBox);
    captureButtonGroup.add(mediumBox);
    captureButtonGroup.add(fineBox);

    captureMenu.add(noneBox);
    captureMenu.add(coarseBox);
    captureMenu.add(mediumBox);
    captureMenu.add(fineBox);

    testMenu.add(testItem);
    testMenu.add(diagsItem);
    testMenu.add(cpuDiagsItem);
    testMenu.add(new JSeparator());
    testMenu.add(loopCheckBox);
    testMenu.add(dmpCheckBox);
    testMenu.add(hilowCheckBox);
    testMenu.add(captureMenu);

    // Define the help menu.
    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutItem = createMenuItem(ABOUT_STRING , null);
    JMenuItem versionItem = createMenuItem(VERSION_STRING, null);
    JMenuItem helpItem = createMenuItem(HELP_STRING, null);

    helpMenu.add(aboutItem);
    helpMenu.add(versionItem);
    helpMenu.add(helpItem);

    // Build the menu bar.
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(fileMenu);
    menuBar.add(configMenu);
    menuBar.add(dataMenu);
    menuBar.add(reportsMenu);
    menuBar.add(forecastMenu);
    menuBar.add(currentReadingsMenu);
    menuBar.add(colorMenu);
    menuBar.add(testMenu);
    menuBar.add(helpMenu);
    this.setJMenuBar(menuBar);

    // Draw the current readings window.
    this.getContentPane().add(currentReadings);

    // Get current readings.
    consoleCmdQueue.getCurrentData();

    // Get the HiLow readings.
    consoleCmdQueue.getHiLowData();

    // Start the background thread to loop, getting current data.
    currentDataTimer.startTimer();
    historicDataTimer.startTimer();
    hiLowDataTimer.startTimer();

    // Realize the window.
    this.addWindowListener(this);
    this.setSize(new Dimension(PROPS.getWindowWidth(), PROPS.getWindowHeight()));
    this.setVisible(true);
  }

  /*
   * Method called by the DataInputDialog box when the OK button is pressed.
   */
  public void processDataInput()
  {
    if (databaseReader.fileExists(dateInputDialog.getYear(), dateInputDialog.getMonth()))
    {
      // Create the table and display.
      DataTable dataTable = new DataTable();
      dataTable.createTable(dateInputDialog.getYear(), dateInputDialog.getMonth());
      DataTable.createAndShowGUI(dataTable);
    }
    else
    {
      JOptionPane.showMessageDialog(this,
                                    "File " + databaseReader.getFilename(dateInputDialog.getYear(),
                                                                         dateInputDialog.getMonth()) +
                                    " does not exist.");
    }
  }

  /*
   * Method called by the DroughtInputDialog box when the OK button is pressed.
   */
  public void processDroughtInput()
  {
    if (databaseReader.fileExists(droughtInputDialog.getYear(), droughtInputDialog.getMonth()))
    {
      // Create the chart and display.
      DroughtWindow droughtWindow = new DroughtWindow(droughtInputDialog.getYear(),
                                                      droughtInputDialog.getMonth(),
                                                      droughtInputDialog.getDuration());
    }
    else
    {
      JOptionPane.showMessageDialog(this,
                                    "File " + databaseReader.getFilename(droughtInputDialog.getYear(),
                                                                         droughtInputDialog.getMonth()) +
                                    " does not exist.");
    }
  }

  private JMenuItem createMenuItem(String name, Color color)
  {
    JMenuItem menuItem = new JMenuItem(name);
    menuItem.setActionCommand(name);
    menuItem.addActionListener(this);
    if (color != null)
      menuItem.setForeground(color);
    return menuItem;
  }

  /**
   * Method callback to display the sunrise/sunset report window.  This is called by the dialog box that gets
   * the input parameters for the report generator.
   *
   * @param startDate The start date (day/month/year) for the report data.
   * @param endDate   The end date for the report data.
   */
  public void displaySunriseSunsetWindow(LocalDate startDate, LocalDate endDate)
  {
    SunriseSunsetTable sunriseSunsetTable = new SunriseSunsetTable(startDate, endDate);
    SunriseSunsetTable.createAndShowGUI(sunriseSunsetTable);
  }

  /**
   * Method callback to display the hours of sunshine report window.  This is called by the dialog box that gets
   * the input parameters for the report generator.
   *
   * @param startDate The start date (day/month/year) for the report data.
   * @param endDate   The end date for the report data.
   * @param threshold The threshold value, below which data should not be included.
   */
  public void displayHrsOfSunshineWindow(LocalDate startDate, LocalDate endDate, int threshold)
  {
    HrsOfSunshineTable hrsOfSunshineTable = new HrsOfSunshineTable(startDate, endDate, threshold);
    HrsOfSunshineTable.createAndShowGUI(hrsOfSunshineTable);
  }

  /**
   * Method callback to display the monthly NOAA report window.  This is called by the dialog box that gets
   * the input parameters for the report generator.
   *
   * @param year The year to report.
   * @param month The month to report.
   * @param filename The filename to write the report.
   */
  public void displayNoaaMonthlyWindow(int year, int month, String filename)
  {
    NoaaMonthlyTable noaaMonthlyTable = new NoaaMonthlyTable(year, month, filename);
    NoaaMonthlyTable.createAndShowGUI(noaaMonthlyTable);
  }

  /**
   * Method callback to display the annual NOAA report window.  This is called by the dialog box that gets
   * the input parameters for the report generator.
   *
   * @param year The year to report.
   * @param filename The filename to write the report.
   */
  public void displayNoaaAnnualWindow(int year, String filename)
  {
    NoaaAnnualTable noaaAnnualTable = new NoaaAnnualTable(year, filename);
    NoaaAnnualTable.createAndShowGUI(noaaAnnualTable);
  }

  /**
   * Method called when one of the menu items has been selected.  The event object
   * tells you what menu item was selected.
   */
  public void actionPerformed(ActionEvent event)
  {
    String action = event.getActionCommand();

    if (action.equalsIgnoreCase(EXIT_STRING))
    {
      // Graceful exit.
      commandControl.terminateCommunications();
      System.exit(0);
    }

    else if (action.equalsIgnoreCase(TIME_STRING))
    {
      consoleCmdQueue.getConsoleTime();
    }

    else if (action.equalsIgnoreCase(RESET_STRING))
    {
      commandControl.resetCommunications();
    }

    else if (action.equalsIgnoreCase(LAMP_STRING))
    {
      if (lampCheckBox.isSelected())
        consoleCmdQueue.turnLampOn();
      else
        consoleCmdQueue.turnLampOff();
    }

    else if (action.equalsIgnoreCase(ALARM_STRING))
    {
      consoleCmdQueue.getAlarms();
    }

    else if (action.equalsIgnoreCase(STATION_STRING))
    {
      // TODO: implement
      System.out.println("Need to implement Station configuration.");
    }

    else if (action.equalsIgnoreCase(TRANSCEIVERS_STRING))
    {
      consoleCmdQueue.getReceivers();
    }

    else if (action.equalsIgnoreCase(COMM_STRING))
    {
      if (commConfigDialog == null)
        commConfigDialog = new CommConfigDialog(this);
      else
        commConfigDialog.setVisible(true);
    }

    else if (action.equalsIgnoreCase(WXUNDERGROUND_STRING))
    {
      if (wxServerDialog == null)
        wxServerDialog = new WxServerDialog(this);
      else
        wxServerDialog.setVisible(true);
    }

    else if (action.equalsIgnoreCase(GRAPH_STRING))
    {
      if (graphWindow == null)
      {
        graphWindow = new GraphWindow();
      }
      graphWindow.setVisible(true);
    }

    else if (action.equalsIgnoreCase(MODIFY_STRING))
    {
      // Display a dialog box to get the month and year.
      if (dateInputDialog == null)
        dateInputDialog = new DateInputDialog(this);
      else
        dateInputDialog.setVisible(true);
    }

    else if (action.equalsIgnoreCase(DROUGHT_STRING))
    {
      // Display a dialog box to get the month and year.
      if (droughtInputDialog == null)
        droughtInputDialog = new DroughtInputDialog(this);
      else
        droughtInputDialog.setVisible(true);
    }

    else if (action.equalsIgnoreCase(STREAM_STRING))
    {
      if (streamWindow == null)
        streamWindow = new StreamWindow();
      else
        streamWindow.setVisible(true);
    }

    else if (action.equalsIgnoreCase(SNOW_GRAPH_STRING))
    {
      if (snowGraphWindow == null)
      {
        snowGraphWindow = new SnowGraphWindow();
      }
      else
      {
        snowGraphWindow.reloadData();
        snowGraphWindow.setVisible(true);
      }
    }

    else if (action.equalsIgnoreCase(SNOW_DATA_STRING))
    {
      SnowDataTable snowDataTable = new SnowDataTable();
      SnowDataTable.createAndShowGUI(snowDataTable);
    }

    else if (action.equalsIgnoreCase(SNOW_RAW_DATA_STRING))
    {
      // Create and display the table.
      SnowRawDataTable snowRawDataTable = new SnowRawDataTable();
      SnowRawDataTable.createAndShowGUI(snowRawDataTable);
    }

    else if (action.equalsIgnoreCase(RAIN_GRAPH_STRING))
    {
      if (rainGraphWindow == null)
      {
        rainGraphWindow = new RainGraphWindow();
      }
      else
      {
        rainGraphWindow.reloadData();
        rainGraphWindow.setVisible(true);
      }
    }

    else if (action.equalsIgnoreCase(RAIN_DATA_STRING))
    {
      RainDataTable rainDataTable = new RainDataTable();
      RainDataTable.createAndShowGUI(rainDataTable);
    }

    else if (action.equalsIgnoreCase(RAIN_EXPORT_STRING))
    {
      rainDataThread.exportData();
    }

    else if (action.equalsIgnoreCase(DUMP_INCR_STRING))
    {
      consoleCmdQueue.dumpArchivedDataAfterDate();
    }

    else if (action.equalsIgnoreCase(SUNRISE_SET_STRING))
    {
      if (sunriseSunsetDialog == null)
        sunriseSunsetDialog = new SunriseSunsetDialog(this);
      else
        sunriseSunsetDialog.setVisible(true);
    }

    else if (action.equalsIgnoreCase(SUNSHINE_STRING))
    {
      if (hrsOfSunshineDialog == null)
        hrsOfSunshineDialog = new HrsOfSunshineDialog(this);
      else
        hrsOfSunshineDialog.setVisible(true);
    }

    else if (action.equalsIgnoreCase(NOAA_MONTH_STRING))
    {
      if (noaaMonthlyDialog == null)
        noaaMonthlyDialog = new NoaaMonthlyDialog(this);
      else
        noaaMonthlyDialog.setVisible(true);
    }

    else if (action.equalsIgnoreCase(NOAA_YEAR_STRING))
    {
      if (noaaAnnualDialog == null)
        noaaAnnualDialog = new NoaaAnnualDialog(this);
      else
        noaaAnnualDialog.setVisible(true);
    }

    else if (action.equalsIgnoreCase(DAILY_FORECAST_STRING))
    {
      if (dailyForecastDialog == null)
      {
        dailyForecastDialog = new DailyForecastDialog(this);
      }
      else
      {
        dailyForecastDialog.updateForecast();
        dailyForecastDialog.setVisible(true);
      }
    }

    else if (action.equalsIgnoreCase(HOURLY_FORECAST_STRING))
    {
      if (hourlyForecastDialog == null)
      {
        hourlyForecastDialog = new HourlyForecastDialog(this);
      }
      else
      {
        hourlyForecastDialog.updateForecast();
        hourlyForecastDialog.setVisible(true);
      }
    }

    else if (action.equalsIgnoreCase(GraphDefs.HOUR_STRING))
    {
      // Set the interval to hourly, set the min/max values and repaint.
      minMaxInterval = MinMaxInterval.hourly;
      currentReadings.setMinMaxInterval(minMaxInterval);
      currentReadings.repaint();

      // Get new hi/low data.
      consoleCmdQueue.getHiLowData();
    }

    else if (action.equalsIgnoreCase(GraphDefs.DAY_STRING))
    {
      // Set the interval to daily, set the min/max values and repaint.
      minMaxInterval = MinMaxInterval.daily;
      currentReadings.setMinMaxInterval(minMaxInterval);
      currentReadings.repaint();

      // Get new hi/low data.
      consoleCmdQueue.getHiLowData();
    }

    else if (action.equalsIgnoreCase(GraphDefs.MONTH_STRING))
    {
      // Set the interval to monthly, set the min/max values and repaint.
      minMaxInterval = MinMaxInterval.monthly;
      currentReadings.setMinMaxInterval(minMaxInterval);
      currentReadings.repaint();

      // Get new hi/low data.
      consoleCmdQueue.getHiLowData();
    }

    else if (action.equalsIgnoreCase(GraphDefs.YEAR_STRING))
    {
      // Set the interval to yearly, set the min/max values and repaint.
      minMaxInterval = MinMaxInterval.yearly;
      currentReadings.setMinMaxInterval(minMaxInterval);
      currentReadings.repaint();

      // Get new hi/low data.
      consoleCmdQueue.getHiLowData();
    }

    else if (action.equalsIgnoreCase(ABOUT_STRING))
    {
      // Display the about dialog box.
      if (aboutDialog == null)
        aboutDialog = new AboutDialog(this);
      else
        aboutDialog.setVisible(true);
    }

    else if (action.equalsIgnoreCase(HELP_STRING))
    {
      // Display the help index window.
      if (helpIndexWindow == null)
        helpIndexWindow = new HelpIndexWindow();
      else
        helpIndexWindow.setVisible(true);
    }

    else if (action.equalsIgnoreCase(TEST_STRING))
    {
      consoleCmdQueue.performTest();
    }

    else if (action.equalsIgnoreCase(VERSION_STRING))
    {
      consoleCmdQueue.getVersion();
    }

    else if (action.equalsIgnoreCase(DIAGS_STRING))
    {
      consoleCmdQueue.performDiags();
    }

    else if (action.equalsIgnoreCase(PI_DIAGS_STRING))
    {
      // Display the PI Diagnostics dialog box.
      if (piDiagsDialog == null)
        piDiagsDialog = new PiDiagsDialog(this);
      else
        piDiagsDialog.setVisible(true);
    }

    else if (action.equalsIgnoreCase(LOOP_DATA_STRING))
    {
      if (loopCheckBox.isSelected())
        currentDataTimer.startTimer();
      else
        currentDataTimer.stopTimer();
    }

    else if (action.equalsIgnoreCase(DMP_DATA_STRING))
    {
      if (dmpCheckBox.isSelected())
        historicDataTimer.startTimer();
      else
        historicDataTimer.stopTimer();
    }

    else if (action.equalsIgnoreCase(HILOW_DATA_STRING))
    {
      if (hilowCheckBox.isSelected())
        hiLowDataTimer.startTimer();
      else
        hiLowDataTimer.stopTimer();
    }

    else if (action.equalsIgnoreCase(CAPTURE_DATA_NONE_STRING))
    {
      logger.setLevel(Logger.Level.NONE);
    }

    else if (action.equalsIgnoreCase(CAPTURE_DATA_COARSE_STRING))
    {
      logger.setLevel(Logger.Level.COARSE);
    }

    else if (action.equalsIgnoreCase(CAPTURE_DATA_MEDIUM_STRING))
    {
      logger.setLevel(Logger.Level.MEDIUM);
    }

    else if (action.equalsIgnoreCase(CAPTURE_DATA_FINE_STRING))
    {
      logger.setLevel(Logger.Level.FINE);
    }

    else if (action.equalsIgnoreCase(BACKGROUND_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getBackgroundColor());
      if (newColor != null)
      {
        PROPS.setBackgroundColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.INTEMP_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getInsideTempColor());
      if (newColor != null)
      {
        inTempColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.INTEMP_STRING, newColor);
        }
        PROPS.setInsideTempColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.INHUMID_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getInsideHumidColor());
      if (newColor != null)
      {
        inHumidColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.INHUMID_STRING, newColor);
        }
        PROPS.setInsideHumidColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.GREENHOUSE_TEMP_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getGreenTempColor());
      if (newColor != null)
      {
        greenTempColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.GREENHOUSE_TEMP_STRING, newColor);
        }
        PROPS.setGreenTempColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.OUTTEMP_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getOutsideTempColor());
      if (newColor != null)
      {
        outTempColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.OUTTEMP_STRING, newColor);
        }
        PROPS.setOutsideTempColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.HIGH_OUTTEMP_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getHighOutsideTempColor());
      if (newColor != null)
      {
        highOutTempColorItem.setForeground(newColor);
        PROPS.setHighOutsideTempColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.LOW_OUTTEMP_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getLowOutsideTempColor());
      if (newColor != null)
      {
        lowOutTempColorItem.setForeground(newColor);
        PROPS.setLowOutsideTempColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.AVG_OUTTEMP_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getAvgOutsideTempColor());
      if (newColor != null)
      {
        avgOutTempColorItem.setForeground(newColor);
        PROPS.setAvgOutsideTempColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.OUTHUMID_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getOutsideHumidColor());
      if (newColor !=null)
      {
        outHumidColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.OUTHUMID_STRING, newColor);
        }
        PROPS.setOutsideHumidColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.RAINFALL_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getRainColor());
      if (newColor != null)
      {
        rainColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.RAINFALL_STRING, newColor);
        }
        PROPS.setRainColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.ISS_RECEPTION_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getIssReceptionColor());
      if (newColor != null)
      {
        issReceptionColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.ISS_RECEPTION_STRING, newColor);
        }
        PROPS.setIssReceptionColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.PRESSURE_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getPressureColor());
      if (newColor != null)
      {
        pressureColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.PRESSURE_STRING, newColor);
        }
        PROPS.setPressureColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.SOLAR_RAD_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getSolarColor());
      if (newColor != null)
      {
        solarColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.SOLAR_RAD_STRING, newColor);
        }
        PROPS.setSolarColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.WIND_SPEED_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getWindSpeedColor());
      if (newColor != null)
      {
        windSpeedColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.WIND_SPEED_STRING, newColor);
        }
        PROPS.setWindSpeedColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.WIND_DIR_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getWindDirColor());
      if (newColor != null)
      {
        windDirColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.WIND_DIR_STRING, newColor);
        }
        PROPS.setWindDirColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.HEAT_DD_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getHeatDDColor());
      if (newColor != null)
      {
        heatDDColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.HEAT_DD_STRING, newColor);
        }
        PROPS.setHeatDDColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.COOL_DD_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getCoolDDColor());
      if (newColor != null)
      {
        coolDDColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.COOL_DD_STRING, newColor);
        }
        PROPS.setCoolDDColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.DEW_POINT_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getDewPointColor());
      if (newColor != null)
      {
        dewPointColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.DEW_POINT_STRING, newColor);
        }
        PROPS.setDewPointColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.HEAT_INDEX_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getHeatIndexColor());
      if (newColor != null)
      {
        heatIndexColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.HEAT_INDEX_STRING, newColor);
        }
        PROPS.setHeatIndexColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.THW_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getThwColor());
      if (newColor != null)
      {
        thwColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.THW_STRING, newColor);
        }
        PROPS.setThwColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.THSW_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getThswColor());
      if (newColor != null)
      {
        thswColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.THSW_STRING, newColor);
        }
        PROPS.setThswColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.ET_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getThswColor());
      if (newColor != null)
      {
        etColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.ET_STRING, newColor);
        }
        PROPS.setEtColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.WIND_RUN_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getThswColor());
      if (newColor != null)
      {
        windRunColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.WIND_RUN_STRING, newColor);
        }
        PROPS.setWindRunColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.HIGH_WIND_SPEED_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getHighWindSpeedColor());
      if (newColor != null)
      {
        hiWindSpeedColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.HIGH_WIND_SPEED_STRING, newColor);
        }
        PROPS.setHighWindSpeedColor(newColor);
      }
    }
    else if (action.equalsIgnoreCase(GraphDefs.SNOW_STRING))
    {
      Color newColor = JColorChooser.showDialog(null, "Choose a color", PROPS.getHighWindSpeedColor());
      if (newColor != null)
      {
        snowColorItem.setForeground(newColor);
        if (streamWindow != null)
        {
          streamWindow.setLineColor(GraphDefs.SNOW_STRING, newColor);
        }
        PROPS.setSnowLineColor(newColor);
      }
    }
    else
    {
      logger.logData(" Main Window: Unimplemented menu action: " + action);
    }
  }

  /**
   * Method called when the "X" in the upper right hand corner of the window
   * is pressed.
   */
  public void windowClosing(WindowEvent event)
  {
    // Perform a graceful shutdown.
    commandControl.terminateCommunications();
    System.exit(0);
  }

  // These methods are required as part of being a window listener.
  public void windowOpened(WindowEvent event) { }
  public void windowClosed(WindowEvent event) { }
  public void windowActivated(WindowEvent event) { }
  public void windowDeactivated(WindowEvent event) { }
  public void windowDeiconified(WindowEvent event) { }
  public void windowIconified(WindowEvent event) { }
}
