/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This window graphs the data, showing the last day, week, month of
            data with the current values being added on to the end.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph;

import gui.DatePickerDialog;
import org.jfree.chart.ChartPanel;
import util.ConfigProperties;
import util.RotatedIcon;
import util.TextIcon;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class GraphWindow extends JFrame implements ActionListener
{
  private final GraphDataThread graphDataThread = GraphDataThread.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();

  // Display menu check boxes.
  private final JCheckBox inTempCheckBox = new JCheckBox(GraphDefs.INTEMP_STRING);
  private final JCheckBox inHumidCheckBox = new JCheckBox(GraphDefs.INHUMID_STRING);
  private final JCheckBox greenTempCheckBox = new JCheckBox(GraphDefs.GREENHOUSE_TEMP_STRING);
  private final JCheckBox outTempCheckBox = new JCheckBox(GraphDefs.OUTTEMP_STRING);
  private final JCheckBox highOutTempCheckBox = new JCheckBox(GraphDefs.HIGH_OUTTEMP_STRING);
  private final JCheckBox lowOutTempCheckBox = new JCheckBox(GraphDefs.LOW_OUTTEMP_STRING);
  private final JCheckBox avgOutTempCheckBox = new JCheckBox(GraphDefs.AVG_OUTTEMP_STRING);
  private final JCheckBox outHumidCheckBox = new JCheckBox(GraphDefs.OUTHUMID_STRING);
  private final JCheckBox rainCheckBox = new JCheckBox(GraphDefs.RAINFALL_STRING);
  private final JCheckBox issRecepCheckBox = new JCheckBox(GraphDefs.ISS_RECEPTION_STRING);
  private final JCheckBox windSpeedCheckBox = new JCheckBox(GraphDefs.WIND_SPEED_STRING);
  private final JCheckBox windDirCheckBox = new JCheckBox(GraphDefs.WIND_DIR_STRING);
  private final JCheckBox pressureCheckBox = new JCheckBox(GraphDefs.PRESSURE_STRING);
  private final JCheckBox solarRadCheckBox = new JCheckBox(GraphDefs.SOLAR_RAD_STRING);
  private final JCheckBox windChillCheckBox = new JCheckBox(GraphDefs.WIND_CHILL_STRING);
  private final JCheckBox heatDDCheckBox = new JCheckBox(GraphDefs.HEAT_DD_STRING);
  private final JCheckBox coolDDCheckBox = new JCheckBox(GraphDefs.COOL_DD_STRING);
  private final JCheckBox dewPointCheckBox = new JCheckBox(GraphDefs.DEW_POINT_STRING);
  private final JCheckBox heatIndexCheckBox = new JCheckBox(GraphDefs.HEAT_INDEX_STRING);
  private final JCheckBox thwCheckBox = new JCheckBox(GraphDefs.THW_STRING);
  private final JCheckBox thswCheckBox = new JCheckBox(GraphDefs.THSW_STRING);
  private final JCheckBox etCheckBox = new JCheckBox(GraphDefs.ET_STRING);
  private final JCheckBox windRunCheckBox = new JCheckBox(GraphDefs.WIND_RUN_STRING);

  private DatePickerDialog datePickerDialog;
  private final ChartButton yAxisButton = new ChartButton();
  private static final String BUTTON_ACTION = "Button";

  private static final String MAKE_DEFAULT = "Make Default";
  private static final String DATA_PICKER = "Date/Duration";

  /**
   * Constructor that creates the Graph Window.
   */
  public GraphWindow()
  {
    // Define the add/remove menu bar.
    JMenu addRemoveMenu = new JMenu("Add/Remove");

    inTempCheckBox.setSelected(PROPS.isInsideTempGraphDisplayed());
    inTempCheckBox.setActionCommand(GraphDefs.INTEMP_STRING);
    inTempCheckBox.addActionListener(this);

    inHumidCheckBox.setSelected(PROPS.isInsideHumidGraphDisplayed());
    inHumidCheckBox.setActionCommand(GraphDefs.INHUMID_STRING);
    inHumidCheckBox.addActionListener(this);

    greenTempCheckBox.setSelected(PROPS.isGreenTempGraphDisplayed());
    greenTempCheckBox.setActionCommand(GraphDefs.GREENHOUSE_TEMP_STRING);
    greenTempCheckBox.addActionListener(this);

    outTempCheckBox.setSelected(PROPS.isOutsideTempGraphDisplayed());
    outTempCheckBox.setActionCommand(GraphDefs.OUTTEMP_STRING);
    outTempCheckBox.addActionListener(this);

    highOutTempCheckBox.setSelected(PROPS.isHighOutsideTempGraphDisplayed());
    highOutTempCheckBox.setActionCommand(GraphDefs.HIGH_OUTTEMP_STRING);
    highOutTempCheckBox.addActionListener(this);

    lowOutTempCheckBox.setSelected(PROPS.isLowOutsideTempGraphDisplayed());
    lowOutTempCheckBox.setActionCommand(GraphDefs.LOW_OUTTEMP_STRING);
    lowOutTempCheckBox.addActionListener(this);

    avgOutTempCheckBox.setSelected(PROPS.isAvgOutsideTempGraphDisplayed());
    avgOutTempCheckBox.setActionCommand(GraphDefs.AVG_OUTTEMP_STRING);
    avgOutTempCheckBox.addActionListener(this);

    outHumidCheckBox.setSelected(PROPS.isOutsideHumidGraphDisplayed());
    outHumidCheckBox.setActionCommand(GraphDefs.OUTHUMID_STRING);
    outHumidCheckBox.addActionListener(this);

    rainCheckBox.setSelected(PROPS.isRainGraphDisplayed());
    rainCheckBox.setActionCommand(GraphDefs.RAINFALL_STRING);
    rainCheckBox.addActionListener(this);

    issRecepCheckBox.setSelected(PROPS.isIssReceptionGraphDisplayed());
    issRecepCheckBox.setActionCommand(GraphDefs.ISS_RECEPTION_STRING);
    issRecepCheckBox.addActionListener(this);

    windSpeedCheckBox.setSelected(PROPS.isWindSpeedGraphDisplayed());
    windSpeedCheckBox.setActionCommand(GraphDefs.WIND_SPEED_STRING);
    windSpeedCheckBox.addActionListener(this);

    windDirCheckBox.setSelected(PROPS.isWindDirGraphDisplayed());
    windDirCheckBox.setActionCommand(GraphDefs.WIND_DIR_STRING);
    windDirCheckBox.addActionListener(this);

    pressureCheckBox.setSelected(PROPS.isPressureGraphDisplayed());
    pressureCheckBox.setActionCommand(GraphDefs.PRESSURE_STRING);
    pressureCheckBox.addActionListener(this);

    solarRadCheckBox.setSelected(PROPS.isSolarGraphDisplayed());
    solarRadCheckBox.setActionCommand(GraphDefs.SOLAR_RAD_STRING);
    solarRadCheckBox.addActionListener(this);

    windChillCheckBox.setSelected(PROPS.isWindChillGraphDisplayed());
    windChillCheckBox.setActionCommand(GraphDefs.WIND_CHILL_STRING);
    windChillCheckBox.addActionListener(this);

    heatDDCheckBox.setSelected(PROPS.isHeatDDGraphDisplayed());
    heatDDCheckBox.setActionCommand(GraphDefs.HEAT_DD_STRING);
    heatDDCheckBox.addActionListener(this);

    coolDDCheckBox.setSelected(PROPS.isCoolDDGraphDisplayed());
    coolDDCheckBox.setActionCommand(GraphDefs.COOL_DD_STRING);
    coolDDCheckBox.addActionListener(this);

    dewPointCheckBox.setSelected(PROPS.isDewPointGraphDisplayed());
    dewPointCheckBox.setActionCommand(GraphDefs.DEW_POINT_STRING);
    dewPointCheckBox.addActionListener(this);

    heatIndexCheckBox.setSelected(PROPS.isHeatIndexGraphDisplayed());
    heatIndexCheckBox.setActionCommand(GraphDefs.HEAT_INDEX_STRING);
    heatIndexCheckBox.addActionListener(this);

    thwCheckBox.setSelected(PROPS.isThwGraphDisplayed());
    thwCheckBox.setActionCommand(GraphDefs.THW_STRING);
    thwCheckBox.addActionListener(this);

    thswCheckBox.setSelected(PROPS.isThswGraphDisplayed());
    thswCheckBox.setActionCommand(GraphDefs.THSW_STRING);
    thswCheckBox.addActionListener(this);

    etCheckBox.setSelected(PROPS.isEtGraphDisplayed());
    etCheckBox.setActionCommand(GraphDefs.ET_STRING);
    etCheckBox.addActionListener(this);

    windRunCheckBox.setSelected(PROPS.isWindRunGraphDisplayed());
    windRunCheckBox.setActionCommand(GraphDefs.WIND_RUN_STRING);
    windRunCheckBox.addActionListener(this);

    modifyButton(PROPS.isInsideTempGraphDisplayed(), GraphDefs.INTEMP_STRING);
    modifyButton(PROPS.isInsideHumidGraphDisplayed(), GraphDefs.INHUMID_STRING);
    modifyButton(PROPS.isGreenTempGraphDisplayed(), GraphDefs.GREENHOUSE_TEMP_STRING);
    modifyButton(PROPS.isOutsideTempGraphDisplayed(), GraphDefs.OUTTEMP_STRING);
    modifyButton(PROPS.isHighOutsideTempGraphDisplayed(), GraphDefs.HIGH_OUTTEMP_STRING);
    modifyButton(PROPS.isLowOutsideTempGraphDisplayed(), GraphDefs.LOW_OUTTEMP_STRING);
    modifyButton(PROPS.isAvgOutsideTempGraphDisplayed(), GraphDefs.AVG_OUTTEMP_STRING);
    modifyButton(PROPS.isOutsideHumidGraphDisplayed(), GraphDefs.OUTHUMID_STRING);
    modifyButton(PROPS.isRainGraphDisplayed(), GraphDefs.RAINFALL_STRING);
    modifyButton(PROPS.isIssReceptionGraphDisplayed(), GraphDefs.ISS_RECEPTION_STRING);
    modifyButton(PROPS.isWindSpeedGraphDisplayed(), GraphDefs.WIND_SPEED_STRING);
    modifyButton(PROPS.isWindDirGraphDisplayed(), GraphDefs.WIND_DIR_STRING);
    modifyButton(PROPS.isPressureGraphDisplayed(), GraphDefs.PRESSURE_STRING);
    modifyButton(PROPS.isSolarGraphDisplayed(), GraphDefs.SOLAR_RAD_STRING);
    modifyButton(PROPS.isWindChillGraphDisplayed(), GraphDefs.WIND_CHILL_STRING);
    modifyButton(PROPS.isHeatDDGraphDisplayed(), GraphDefs.HEAT_DD_STRING);
    modifyButton(PROPS.isCoolDDGraphDisplayed(), GraphDefs.COOL_DD_STRING);
    modifyButton(PROPS.isDewPointGraphDisplayed(), GraphDefs.DEW_POINT_STRING);
    modifyButton(PROPS.isHeatIndexGraphDisplayed(), GraphDefs.HEAT_INDEX_STRING);
    modifyButton(PROPS.isThwGraphDisplayed(), GraphDefs.THW_STRING);
    modifyButton(PROPS.isThswGraphDisplayed(), GraphDefs.THSW_STRING);
    modifyButton(PROPS.isEtGraphDisplayed(), GraphDefs.ET_STRING);
    modifyButton(PROPS.isWindRunGraphDisplayed(), GraphDefs.WIND_RUN_STRING);

    addRemoveMenu.add(inTempCheckBox);
    addRemoveMenu.add(inHumidCheckBox);
    addRemoveMenu.add(greenTempCheckBox);
    addRemoveMenu.add(outTempCheckBox);
    addRemoveMenu.add(highOutTempCheckBox);
    addRemoveMenu.add(lowOutTempCheckBox);
    addRemoveMenu.add(avgOutTempCheckBox);
    addRemoveMenu.add(outHumidCheckBox);
    addRemoveMenu.add(rainCheckBox);
    addRemoveMenu.add(issRecepCheckBox);
    addRemoveMenu.add(windSpeedCheckBox);
    addRemoveMenu.add(windDirCheckBox);
    addRemoveMenu.add(pressureCheckBox);
    addRemoveMenu.add(solarRadCheckBox);
    addRemoveMenu.add(windChillCheckBox);
    addRemoveMenu.add(heatDDCheckBox);
    addRemoveMenu.add(coolDDCheckBox);
    addRemoveMenu.add(dewPointCheckBox);
    addRemoveMenu.add(heatIndexCheckBox);
    addRemoveMenu.add(thwCheckBox);
    addRemoveMenu.add(thswCheckBox);
    addRemoveMenu.add(etCheckBox);
    addRemoveMenu.add(windRunCheckBox);

    // Define the Make Default menu.
    JMenuItem makeDefaultMenu = new JMenuItem(MAKE_DEFAULT);
    makeDefaultMenu.setActionCommand(MAKE_DEFAULT);
    makeDefaultMenu.addActionListener(this);

    // Create the date picker menu.
    JMenuItem dateDurationMenuItem = new JMenuItem(DATA_PICKER);
    dateDurationMenuItem.setActionCommand(DATA_PICKER);
    dateDurationMenuItem.addActionListener(this);

    // Define the Graph menu.
    JMenu graphMenu = new JMenu("Graph");
    graphMenu.add(makeDefaultMenu);
    graphMenu.add(dateDurationMenuItem);

    // Build the menu bar.
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(addRemoveMenu);
    menuBar.add(graphMenu);
    setJMenuBar(menuBar);

    // Build the graph button.
    Insets bm = UIManager.getInsets("Button.margin");
    Insets margin = new Insets(bm.top, bm.left, bm.bottom, bm.right);

    setButtonText(yAxisButton.getFirstItem());
    yAxisButton.setMargin(margin);
    yAxisButton.setActionCommand(BUTTON_ACTION);
    yAxisButton.addActionListener(this::popupAction);

    graphDataThread.initializeChart();

    // Create the graph panel.
    ChartPanel chartPanel = graphDataThread.getChart();

    JPanel graphPanel = new JPanel();
    graphPanel.setLayout(new BorderLayout());
    graphPanel.add(chartPanel, BorderLayout.CENTER);
    graphPanel.add(yAxisButton, BorderLayout.WEST);

    getContentPane().add(graphPanel);
    setTitle("Graph Window");
    setSize(new Dimension(PROPS.getWindowWidth(), PROPS.getWindowHeight()));
    setVisible(true);

    // Populate the dataset to graph.
    graphDataThread.addDatasetsToCharts();
    graphDataThread.setRangeAxis(true, yAxisButton.getFirstItem());
    graphDataThread.populateDataset(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
                                    LocalDate.now().getDayOfMonth(), GraphDefs.WEEK_STRING);
  }

  /**
   * Internal method to add/remove a data item to/from a button.
   *
   * @param isDisplayed Should the name be added?
   * @param dataname The data name to add.
   */
  private void modifyButton(boolean isDisplayed, String dataname)
  {
    if (isDisplayed)
      yAxisButton.addMenuItem(dataname);
    else
      yAxisButton.removeMenuItem(dataname);
  }

  /**
   * Internal method to set the vertical button text.
   *
   * @param text The text to add to the button.
   */
  private void setButtonText(String text)
  {
    if (yAxisButton.getFirstItem() != null)
    {
      TextIcon cText = new TextIcon(yAxisButton, text, TextIcon.Layout.HORIZONTAL);
      cText.setFont(GraphDefs.BILLVETICA);
      cText.setForeground(graphDataThread.getColor(text));
      yAxisButton.setIcon(new RotatedIcon(cText, RotatedIcon.Rotate.UP));
    }
    else
    {
      TextIcon cText = new TextIcon(yAxisButton, "", TextIcon.Layout.HORIZONTAL);
      cText.setForeground(Color.WHITE);
      yAxisButton.setIcon(new RotatedIcon(cText, RotatedIcon.Rotate.UP));
    }
  }

  /**
   * Method called by the date picker dialog once a date is selected to change the graph data.
   *
   * @param endDate The end date.  Only the year, month and day are valid.
   */
  public void changeDatasetSize(Date endDate, String duration)
  {
    graphDataThread.setDatasetSize(GraphDefs.INTEMP_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.INHUMID_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.GREENHOUSE_TEMP_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.OUTTEMP_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.HIGH_OUTTEMP_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.LOW_OUTTEMP_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.AVG_OUTTEMP_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.OUTHUMID_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.PRESSURE_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.RAINFALL_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.ISS_RECEPTION_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.WIND_SPEED_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.WIND_DIR_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.SOLAR_RAD_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.WIND_CHILL_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.HEAT_DD_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.COOL_DD_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.DEW_POINT_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.HEAT_INDEX_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.THW_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.THSW_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.ET_STRING, duration);
    graphDataThread.setDatasetSize(GraphDefs.WIND_RUN_STRING, duration);

    if (endDate != null)
    {
      LocalDate localDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      int year  = localDate.getYear();
      int month = localDate.getMonthValue();
      int day = localDate.getDayOfMonth();
      graphDataThread.populateDataset(year, month, day, duration);
    }
  }

  /**
   * Listener called when one of the check boxes is called.  The new data is either selected or unselected and the
   * y-axis button is changed.
   *
   * @param event The event.
   */
  @Override
  public void actionPerformed(ActionEvent event)
  {
    String actionCommand = event.getActionCommand();

    if (actionCommand.equalsIgnoreCase(MAKE_DEFAULT))
    {
      PROPS.setInsideTempGraphDisplayed(inTempCheckBox.isSelected());
      PROPS.setInsideHumidGraphDisplayed(inHumidCheckBox.isSelected());
      PROPS.setGreenTempGraphDisplayed(greenTempCheckBox.isSelected());
      PROPS.setOutsideTempGraphDisplayed(outTempCheckBox.isSelected());
      PROPS.setHighOutsideTempGraphDisplayed(highOutTempCheckBox.isSelected());
      PROPS.setLowOutsideTempGraphDisplayed(lowOutTempCheckBox.isSelected());
      PROPS.setAvgOutsideTempGraphDisplayed(avgOutTempCheckBox.isSelected());
      PROPS.setOutsideHumidGraphDisplayed(outHumidCheckBox.isSelected());
      PROPS.setIssReceptionGraphDisplayed(issRecepCheckBox.isSelected());
      PROPS.setSolarGraphDisplayed(solarRadCheckBox.isSelected());
      PROPS.setPressureGraphDisplayed(pressureCheckBox.isSelected());
      PROPS.setWindDirGraphDisplayed(windDirCheckBox.isSelected());
      PROPS.setWindSpeedGraphDisplayed(windSpeedCheckBox.isSelected());
      PROPS.setRainGraphDisplayed(rainCheckBox.isSelected());
      PROPS.setWindChillGraphDisplayed(windChillCheckBox.isSelected());
      PROPS.setHeatDDGraphDisplayed(heatDDCheckBox.isSelected());
      PROPS.setCoolDDGraphDisplayed(coolDDCheckBox.isSelected());
      PROPS.setDewPointGraphDisplayed(dewPointCheckBox.isSelected());
      PROPS.setHeatIndexGraphDisplayed(heatIndexCheckBox.isSelected());
      PROPS.setTHWGraphDisplayed(thwCheckBox.isSelected());
      PROPS.setTHSWGraphDisplayed(thswCheckBox.isSelected());
      PROPS.setETGraphDisplayed(etCheckBox.isSelected());
      PROPS.setWindRunGraphDisplayed(windRunCheckBox.isSelected());

      JOptionPane.showConfirmDialog(this,
                                    "Default Graph Saved", "Make Default", JOptionPane.DEFAULT_OPTION);
    }
    else if (actionCommand.equalsIgnoreCase(DATA_PICKER))
    {
      if (datePickerDialog == null)
      {
        datePickerDialog = new DatePickerDialog(this);
      }
      else
      {
        datePickerDialog.setVisible(true);
      }
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.INTEMP_STRING))
    {
      handleCheckBoxAction(GraphDefs.INTEMP_STRING, inTempCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.INHUMID_STRING))
    {
      handleCheckBoxAction(GraphDefs.INHUMID_STRING, inHumidCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.GREENHOUSE_TEMP_STRING))
    {
      handleCheckBoxAction(GraphDefs.GREENHOUSE_TEMP_STRING, greenTempCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.OUTTEMP_STRING))
    {
      handleCheckBoxAction(GraphDefs.OUTTEMP_STRING, outTempCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.HIGH_OUTTEMP_STRING))
    {
      handleCheckBoxAction(GraphDefs.HIGH_OUTTEMP_STRING, highOutTempCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.LOW_OUTTEMP_STRING))
    {
      handleCheckBoxAction(GraphDefs.LOW_OUTTEMP_STRING, lowOutTempCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.AVG_OUTTEMP_STRING))
    {
      handleCheckBoxAction(GraphDefs.AVG_OUTTEMP_STRING, avgOutTempCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.OUTHUMID_STRING))
    {
      handleCheckBoxAction(GraphDefs.OUTHUMID_STRING, outHumidCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.RAINFALL_STRING))
    {
      handleCheckBoxAction(GraphDefs.RAINFALL_STRING, rainCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.ISS_RECEPTION_STRING))
    {
      handleCheckBoxAction(GraphDefs.ISS_RECEPTION_STRING, issRecepCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.WIND_SPEED_STRING))
    {
      handleCheckBoxAction(GraphDefs.WIND_SPEED_STRING, windSpeedCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.WIND_DIR_STRING))
    {
      handleCheckBoxAction(GraphDefs.WIND_DIR_STRING, windDirCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.PRESSURE_STRING))
    {
      handleCheckBoxAction(GraphDefs.PRESSURE_STRING, pressureCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.SOLAR_RAD_STRING))
    {
      handleCheckBoxAction(GraphDefs.SOLAR_RAD_STRING, solarRadCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.WIND_CHILL_STRING))
    {
      handleCheckBoxAction(GraphDefs.WIND_CHILL_STRING, windChillCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.HEAT_DD_STRING))
    {
      handleCheckBoxAction(GraphDefs.HEAT_DD_STRING, heatDDCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.COOL_DD_STRING))
    {
      handleCheckBoxAction(GraphDefs.COOL_DD_STRING, coolDDCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.DEW_POINT_STRING))
    {
      handleCheckBoxAction(GraphDefs.DEW_POINT_STRING, dewPointCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.HEAT_INDEX_STRING))
    {
      handleCheckBoxAction(GraphDefs.HEAT_INDEX_STRING, heatIndexCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.THW_STRING))
    {
      handleCheckBoxAction(GraphDefs.THW_STRING, thwCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.THSW_STRING))
    {
      handleCheckBoxAction(GraphDefs.THSW_STRING, thswCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.ET_STRING))
    {
      handleCheckBoxAction(GraphDefs.ET_STRING, etCheckBox.isSelected(), event);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.WIND_RUN_STRING))
    {
      handleCheckBoxAction(GraphDefs.WIND_RUN_STRING, windRunCheckBox.isSelected(), event);
    }
  }

  /**
   * Internal method to handle the checkbox actions which includes modifying the chart button and adding/removing
   * the data from the chart.
   *
   * @param dataName The name of the dataset.
   * @param isSelected Whether the checkbox has been selected.
   * @param event The event.
   */
  private void handleCheckBoxAction(String dataName, boolean isSelected, ActionEvent event)
  {
    modifyButton(isSelected, dataName);
    yAxisButton.moveItemToFirst(event.getActionCommand());
    setButtonText(yAxisButton.getFirstItem());
    graphDataThread.setRangeAxis(isSelected, event.getActionCommand());

    if (isSelected)
    {
      graphDataThread.addDataToChart(dataName);
    }
    else
    {
      graphDataThread.removeDataFromChart(dataName);
    }
  }

  /**
   * Listener called when the graph panel y-axis button is called.  This simply displays a popup menu for the
   * user to select to change the y-axis.  The button text alters the y-axis values to match the data.
   *
   * @param event The event.
   */
  private void popupAction(ActionEvent event)
  {
    if (event.getActionCommand().equalsIgnoreCase(BUTTON_ACTION))
    {
      JPopupMenu aPopupMenu = new JPopupMenu();
      aPopupMenu.setBorder(new BevelBorder(BevelBorder.RAISED));

      ArrayList<String> menuItems = yAxisButton.getMenuItems();
      for (String nextMenuItem : menuItems)
      {
        JMenuItem nextItem = new JMenuItem(nextMenuItem);
        nextItem.setActionCommand(nextMenuItem);
        nextItem.addActionListener(this::menuAction);
        aPopupMenu.add(nextItem);
      }
      aPopupMenu.show(this, yAxisButton.getX() + 35, yAxisButton.getY() + 50);
    }
  }

  /**
   * Button press handler for the graph panel button popup menus.
   *
   * @param event The event.
   */
  private void menuAction(ActionEvent event)
  {
    yAxisButton.moveItemToFirst(event.getActionCommand());
    setButtonText(event.getActionCommand());
    graphDataThread.setRangeAxis(true, event.getActionCommand());
  }
}
