/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This window streams the data, showing the last day, week, month of
            data with the current values being added on to the end.

  Mods:		  09/01/21 Initial Release.
*/
package gui.graph;

import org.jfree.chart.ChartPanel;
import util.ConfigProperties;
import util.RotatedIcon;
import util.TextIcon;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class StreamWindow extends JFrame implements ActionListener
{
  private final StreamDataThread dataThread = StreamDataThread.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();

  // Display menu check boxes.
  private final JCheckBox inTempCheckBox = new JCheckBox(GraphDefs.INTEMP_STRING);
  private final JCheckBox inHumidCheckBox = new JCheckBox(GraphDefs.INHUMID_STRING);
  private final JCheckBox greenTempCheckBox = new JCheckBox(GraphDefs.GREENHOUSE_TEMP_STRING);
  private final JCheckBox outTempCheckBox = new JCheckBox(GraphDefs.OUTTEMP_STRING);
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

  // Graph buttons.
  private final ChartButton aButton = new ChartButton();
  private final ChartButton bButton = new ChartButton();
  private final ChartButton cButton = new ChartButton();
  private static final String aButtonString = "aButton";
  private static final String bButtonString = "bButton";
  private static final String cButtonString = "cButton";

  private static final String MAKE_DEFAULT = "Make Default";

  /**
   * Constructor that creates the Stream Window.
   */
  public StreamWindow()
  {
    // Define the Interval menu.
    JMenu intervalMenu = new JMenu("Interval");

    JRadioButtonMenuItem halfDayBox = new JRadioButtonMenuItem(GraphDefs.HALF_DAY_STRING);
    halfDayBox.setActionCommand(GraphDefs.HALF_DAY_STRING);
    halfDayBox.addActionListener(this);

    JRadioButtonMenuItem dayBox = new JRadioButtonMenuItem(GraphDefs.DAY_STRING);
    dayBox.setActionCommand(GraphDefs.DAY_STRING);
    dayBox.addActionListener(this);

    JRadioButtonMenuItem halfWeekBox = new JRadioButtonMenuItem(GraphDefs.HALF_WEEK_STRING);
    halfWeekBox.setActionCommand(GraphDefs.HALF_WEEK_STRING);
    halfWeekBox.addActionListener(this);

    JRadioButtonMenuItem weekBox = new JRadioButtonMenuItem(GraphDefs.WEEK_STRING, true);
    weekBox.setActionCommand(GraphDefs.WEEK_STRING);
    weekBox.addActionListener(this);

    JRadioButtonMenuItem halfMonthBox = new JRadioButtonMenuItem(GraphDefs.HALF_MONTH_STRING);
    halfMonthBox.setActionCommand(GraphDefs.HALF_MONTH_STRING);
    halfMonthBox.addActionListener(this);

    JRadioButtonMenuItem monthBox = new JRadioButtonMenuItem(GraphDefs.MONTH_STRING);
    monthBox.setActionCommand(GraphDefs.MONTH_STRING);
    monthBox.addActionListener(this);

    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(halfDayBox);
    buttonGroup.add(dayBox);
    buttonGroup.add(halfWeekBox);
    buttonGroup.add(weekBox);
    buttonGroup.add(halfMonthBox);
    buttonGroup.add(monthBox);

    intervalMenu.add(halfDayBox);
    intervalMenu.add(dayBox);
    intervalMenu.add(halfWeekBox);
    intervalMenu.add(weekBox);
    intervalMenu.add(halfMonthBox);
    intervalMenu.add(monthBox);

    // Define the Add/Remove chart menu.
    JMenu addRemoveMenu = new JMenu("Add/Remove");

    inTempCheckBox.setSelected(PROPS.isInsideTempStreamDisplayed());
    inTempCheckBox.setActionCommand(GraphDefs.INTEMP_STRING);
    inTempCheckBox.addActionListener(this);

    inHumidCheckBox.setSelected(PROPS.isInsideHumidStreamDisplayed());
    inHumidCheckBox.setActionCommand(GraphDefs.INHUMID_STRING);
    inHumidCheckBox.addActionListener(this);

    greenTempCheckBox.setSelected(PROPS.isGreenTempStreamDisplayed());
    greenTempCheckBox.setActionCommand(GraphDefs.GREENHOUSE_TEMP_STRING);
    greenTempCheckBox.addActionListener(this);

    outTempCheckBox.setSelected(PROPS.isOutsideTempStreamDisplayed());
    outTempCheckBox.setActionCommand(GraphDefs.OUTTEMP_STRING);
    outTempCheckBox.addActionListener(this);

    outHumidCheckBox.setSelected(PROPS.isOutsideHumidStreamDisplayed());
    outHumidCheckBox.setActionCommand(GraphDefs.OUTHUMID_STRING);
    outHumidCheckBox.addActionListener(this);

    rainCheckBox.setSelected(PROPS.isRainStreamDisplayed());
    rainCheckBox.setActionCommand(GraphDefs.RAINFALL_STRING);
    rainCheckBox.addActionListener(this);

    issRecepCheckBox.setSelected(PROPS.isIssReceptionStreamDisplayed());
    issRecepCheckBox.setActionCommand(GraphDefs.ISS_RECEPTION_STRING);
    issRecepCheckBox.addActionListener(this);

    windSpeedCheckBox.setSelected(PROPS.isWindSpeedStreamDisplayed());
    windSpeedCheckBox.setActionCommand(GraphDefs.WIND_SPEED_STRING);
    windSpeedCheckBox.addActionListener(this);

    windDirCheckBox.setSelected(PROPS.isWindDirStreamDisplayed());
    windDirCheckBox.setActionCommand(GraphDefs.WIND_DIR_STRING);
    windDirCheckBox.addActionListener(this);

    pressureCheckBox.setSelected(PROPS.isPressureStreamDisplayed());
    pressureCheckBox.setActionCommand(GraphDefs.PRESSURE_STRING);
    pressureCheckBox.addActionListener(this);

    solarRadCheckBox.setSelected(PROPS.isSolarStreamDisplayed());
    solarRadCheckBox.setActionCommand(GraphDefs.SOLAR_RAD_STRING);
    solarRadCheckBox.addActionListener(this);

    windChillCheckBox.setSelected(PROPS.isWindChillStreamDisplayed());
    windChillCheckBox.setActionCommand(GraphDefs.WIND_CHILL_STRING);
    windChillCheckBox.addActionListener(this);

    heatDDCheckBox.setSelected(PROPS.isHeatDDStreamDisplayed());
    heatDDCheckBox.setActionCommand(GraphDefs.HEAT_DD_STRING);
    heatDDCheckBox.addActionListener(this);

    coolDDCheckBox.setSelected(PROPS.isCoolDDStreamDisplayed());
    coolDDCheckBox.setActionCommand(GraphDefs.COOL_DD_STRING);
    coolDDCheckBox.addActionListener(this);

    dewPointCheckBox.setSelected(PROPS.isDewPointStreamDisplayed());
    dewPointCheckBox.setActionCommand(GraphDefs.DEW_POINT_STRING);
    dewPointCheckBox.addActionListener(this);

    heatIndexCheckBox.setSelected(PROPS.isHeatIndexStreamDisplayed());
    heatIndexCheckBox.setActionCommand(GraphDefs.HEAT_INDEX_STRING);
    heatIndexCheckBox.addActionListener(this);

    thwCheckBox.setSelected(PROPS.isThwStreamDisplayed());
    thwCheckBox.setActionCommand(GraphDefs.THW_STRING);
    thwCheckBox.addActionListener(this);

    thswCheckBox.setSelected(PROPS.isThswStreamDisplayed());
    thswCheckBox.setActionCommand(GraphDefs.THSW_STRING);
    thswCheckBox.addActionListener(this);

    etCheckBox.setSelected(PROPS.isEtStreamDisplayed());
    etCheckBox.setActionCommand(GraphDefs.ET_STRING);
    etCheckBox.addActionListener(this);

    windRunCheckBox.setSelected(PROPS.isWindRunStreamDisplayed());
    windRunCheckBox.setActionCommand(GraphDefs.WIND_RUN_STRING);
    windRunCheckBox.addActionListener(this);

    modifyButton(PROPS.isInsideTempStreamDisplayed(), PROPS.getInsideTempChart(), GraphDefs.INTEMP_STRING);
    modifyButton(PROPS.isInsideHumidStreamDisplayed(), PROPS.getInsideHumidChart(), GraphDefs.INHUMID_STRING);
    modifyButton(PROPS.isGreenTempStreamDisplayed(), PROPS.getGreenTempChart(), GraphDefs.GREENHOUSE_TEMP_STRING);
    modifyButton(PROPS.isOutsideTempStreamDisplayed(), PROPS.getOutsideTempChart(), GraphDefs.OUTTEMP_STRING);
    modifyButton(PROPS.isOutsideHumidStreamDisplayed(), PROPS.getOutsideHumidChart(), GraphDefs.OUTHUMID_STRING);
    modifyButton(PROPS.isRainStreamDisplayed(), PROPS.getRainChart(), GraphDefs.RAINFALL_STRING);
    modifyButton(PROPS.isIssReceptionStreamDisplayed(), PROPS.getIssReceptionChart(), GraphDefs.ISS_RECEPTION_STRING);
    modifyButton(PROPS.isWindSpeedStreamDisplayed(), PROPS.getWindSpeedChart(), GraphDefs.WIND_SPEED_STRING);
    modifyButton(PROPS.isWindDirStreamDisplayed(), PROPS.getWindDirChart(), GraphDefs.WIND_DIR_STRING);
    modifyButton(PROPS.isPressureStreamDisplayed(), PROPS.getPressureChart(), GraphDefs.PRESSURE_STRING);
    modifyButton(PROPS.isSolarStreamDisplayed(), PROPS.getSolarChart(), GraphDefs.SOLAR_RAD_STRING);
    modifyButton(PROPS.isWindChillStreamDisplayed(), PROPS.getWindChillChart(), GraphDefs.WIND_CHILL_STRING);
    modifyButton(PROPS.isHeatDDStreamDisplayed(), PROPS.getHeatDDChart(), GraphDefs.HEAT_DD_STRING);
    modifyButton(PROPS.isCoolDDStreamDisplayed(), PROPS.getCoolDDChart(), GraphDefs.COOL_DD_STRING);
    modifyButton(PROPS.isDewPointStreamDisplayed(), PROPS.getDewPointChart(), GraphDefs.DEW_POINT_STRING);
    modifyButton(PROPS.isHeatIndexStreamDisplayed(), PROPS.getHeatIndexChart(), GraphDefs.HEAT_INDEX_STRING);
    modifyButton(PROPS.isThwStreamDisplayed(), PROPS.getThwChart(), GraphDefs.THW_STRING);
    modifyButton(PROPS.isThswStreamDisplayed(), PROPS.getThswChart(), GraphDefs.THSW_STRING);
    modifyButton(PROPS.isEtStreamDisplayed(), PROPS.getEtChart(), GraphDefs.ET_STRING);
    modifyButton(PROPS.isWindRunStreamDisplayed(), PROPS.getWindRunChart(), GraphDefs.WIND_RUN_STRING);

    addRemoveMenu.add(inTempCheckBox);
    addRemoveMenu.add(inHumidCheckBox);
    addRemoveMenu.add(greenTempCheckBox);
    addRemoveMenu.add(outTempCheckBox);
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

    // Build the menu bar.
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(intervalMenu);
    menuBar.add(addRemoveMenu);
    menuBar.add(makeDefaultMenu);
    setJMenuBar(menuBar);

    // Build the right panel buttons.
    Insets bm = UIManager.getInsets("Button.margin");
    Insets margin = new Insets(bm.top, bm.left, bm.bottom, bm.right);

    setButtonText(aButton);
    aButton.setMargin(margin);
    aButton.setActionCommand(aButtonString);
    aButton.addActionListener(this);

    setButtonText(bButton);
    bButton.setMargin(margin);
    bButton.setActionCommand(bButtonString);
    bButton.addActionListener(this);

    setButtonText(cButton);
    cButton.setMargin(margin);
    cButton.setActionCommand(cButtonString);
    cButton.addActionListener(this);

    // Create the stream panels.
    dataThread.initializeCharts();

    ChartPanel aChartPanel = dataThread.getAChart();
    aChartPanel.setPreferredSize(new java.awt.Dimension(600, 200));

    JPanel aStreamPanel = new JPanel();
    aStreamPanel.setLayout(new BorderLayout());
    aStreamPanel.add(aChartPanel);
    aStreamPanel.setBorder(new BevelBorder(BevelBorder.RAISED));

    ChartPanel bChartPanel = dataThread.getBChart();
    bChartPanel.setPreferredSize(new java.awt.Dimension(600, 200));

    JPanel bStreamPanel = new JPanel();
    bStreamPanel.setLayout(new BorderLayout());
    bStreamPanel.add(bChartPanel);
    bStreamPanel.setBorder(new BevelBorder(BevelBorder.RAISED));

    ChartPanel cChartPanel = dataThread.getCChart();
    cChartPanel.setPreferredSize(new java.awt.Dimension(600, 200));

    JPanel cStreamPanel = new JPanel();
    cStreamPanel.setLayout(new BorderLayout());
    cStreamPanel.add(cChartPanel);
    cStreamPanel.setBorder(new BevelBorder(BevelBorder.RAISED));

    // Layout the aPanel.
    JPanel aPanel = new JPanel();
    SpringLayout aLayout = new SpringLayout();
    aLayout.putConstraint(SpringLayout.VERTICAL_CENTER, aButton, 0, SpringLayout.VERTICAL_CENTER, aPanel);
    aPanel.setLayout(aLayout);
    aPanel.add(aButton);
    aPanel.add(aStreamPanel);

    SpringLayout.Constraints aButtonCsts = aLayout.getConstraints(aButton);
    aButtonCsts.setX(Spring.constant(5));
    aButtonCsts.setY(Spring.constant(10));

    SpringLayout.Constraints aStreamPanelCsts = aLayout.getConstraints(aStreamPanel);
    aStreamPanelCsts.setY(Spring.constant(0));
    aStreamPanelCsts.setX(Spring.sum(Spring.constant(5), aButtonCsts.getConstraint("East")));

    SpringLayout.Constraints aPanelCsts = aLayout.getConstraints(aPanel);
    aPanelCsts.setConstraint("East", Spring.sum(Spring.constant(0), aStreamPanelCsts.getConstraint("East")));
    aPanelCsts.setConstraint("South", Spring.sum(Spring.constant(0), aStreamPanelCsts.getConstraint("South")));

    // Layout the bPanel
    JPanel bPanel = new JPanel();
    SpringLayout bLayout = new SpringLayout();
    bLayout.putConstraint(SpringLayout.VERTICAL_CENTER, bButton, 0, SpringLayout.VERTICAL_CENTER, bPanel);
    bPanel.setLayout(bLayout);
    bPanel.add(bButton);
    bPanel.add(bStreamPanel);

    SpringLayout.Constraints bButtonCsts = bLayout.getConstraints(bButton);
    bButtonCsts.setX(Spring.constant(5));
    bButtonCsts.setY(Spring.constant(10));

    SpringLayout.Constraints bStreamPanelCsts = bLayout.getConstraints(bStreamPanel);
    bStreamPanelCsts.setY(Spring.constant(0));
    bStreamPanelCsts.setX(Spring.sum(Spring.constant(5), bButtonCsts.getConstraint("East")));

    SpringLayout.Constraints bPanelCsts = bLayout.getConstraints(bPanel);
    bPanelCsts.setConstraint("East", Spring.sum(Spring.constant(0), bStreamPanelCsts.getConstraint("East")));
    bPanelCsts.setConstraint("South", Spring.sum(Spring.constant(0), bStreamPanelCsts.getConstraint("South")));

    // Layout the cPanel.
    JPanel cPanel = new JPanel();
    SpringLayout cLayout = new SpringLayout();
    cLayout.putConstraint(SpringLayout.VERTICAL_CENTER, cButton, 0, SpringLayout.VERTICAL_CENTER, cPanel);
    cPanel.setLayout(cLayout);
    cPanel.add(cButton);
    cPanel.add(cStreamPanel);

    SpringLayout.Constraints cButtonCsts = cLayout.getConstraints(cButton);
    cButtonCsts.setX(Spring.constant(5));
    cButtonCsts.setY(Spring.constant(10));

    SpringLayout.Constraints cStreamPanelCsts = cLayout.getConstraints(cStreamPanel);
    cStreamPanelCsts.setY(Spring.constant(0));
    cStreamPanelCsts.setX(Spring.sum(Spring.constant(5), cButtonCsts.getConstraint("East")));

    SpringLayout.Constraints cPanelCsts = cLayout.getConstraints(cPanel);
    cPanelCsts.setConstraint("East", Spring.sum(Spring.constant(0), cStreamPanelCsts.getConstraint("East")));
    cPanelCsts.setConstraint("South", Spring.sum(Spring.constant(0), cStreamPanelCsts.getConstraint("South")));

    Container contentPane = getContentPane();
    SpringLayout layout = new SpringLayout();
    contentPane.setLayout(layout);

    contentPane.add(aPanel);
    contentPane.add(bPanel);
    contentPane.add(cPanel);

    // Layout the frame panel.
    SpringLayout.Constraints aConstraints = layout.getConstraints(aPanel);
    aConstraints.setX(Spring.constant(0));
    aConstraints.setY(Spring.constant(0));

    SpringLayout.Constraints bConstraints = layout.getConstraints(bPanel);
    bConstraints.setX(Spring.constant(0));
    bConstraints.setY(Spring.sum(Spring.constant(0), aConstraints.getConstraint("South")));

    SpringLayout.Constraints cConstraints = layout.getConstraints(cPanel);
    cConstraints.setX(Spring.constant(0));
    cConstraints.setY(Spring.sum(Spring.constant(0), bConstraints.getConstraint("South")));

    SpringLayout.Constraints panelConstraints = layout.getConstraints(contentPane);
    panelConstraints.setConstraint("South", Spring.sum(Spring.constant(0), cConstraints.getConstraint("South")));
    panelConstraints.setConstraint("East", Spring.sum(Spring.constant(0), aConstraints.getConstraint("East")));
    layout.putConstraint(SpringLayout.EAST, bPanel, 0, SpringLayout.EAST, contentPane);
    layout.putConstraint(SpringLayout.EAST, cPanel, 0, SpringLayout.EAST, contentPane);

    setTitle("Stream Window");
    setSize(new Dimension(PROPS.getWindowWidth(), PROPS.getWindowHeight()));
    setVisible(true);

    // Create the datasets, load the data and set the y-axis.
    dataThread.initData(GraphDefs.WEEK_DATA_SIZE);
    dataThread.addDatasetsToCharts();
    dataThread.setRangeAxis(aButton.getFirstItem());
    dataThread.setRangeAxis(bButton.getFirstItem());
    dataThread.setRangeAxis(cButton.getFirstItem());
 }

  /**
   * Internal method to add/remove a data item to/from a button.
   *
   * @param isDisplayed Should the name be added?
   * @param chart The chart name.
   * @param dataname The data name to add.
   */
 private void modifyButton(boolean isDisplayed, String chart, String dataname)
 {
   if (isDisplayed)
   {
     if (chart.equalsIgnoreCase(GraphDefs.A_CHART_NAME))
       aButton.addMenuItem(dataname);
     else if (chart.equalsIgnoreCase(GraphDefs.B_CHART_NAME))
       bButton.addMenuItem(dataname);
     else if (chart.equalsIgnoreCase(GraphDefs.C_CHART_NAME))
       cButton.addMenuItem(dataname);
   }
   else
   {
     if (chart.equalsIgnoreCase(GraphDefs.A_CHART_NAME))
       aButton.removeMenuItem(dataname);
     else if (chart.equalsIgnoreCase(GraphDefs.B_CHART_NAME))
       bButton.removeMenuItem(dataname);
     else if (chart.equalsIgnoreCase(GraphDefs.C_CHART_NAME))
       cButton.removeMenuItem(dataname);
   }
 }

  /**
   * Internal method to set the vertical button text.
   *
   * @param button The button.
   */
  private void setButtonText(ChartButton button)
  {
    String firstItem = button.getFirstItem();
    if (firstItem != null)
    {
      TextIcon cText = new TextIcon(button, firstItem, TextIcon.Layout.HORIZONTAL);
      cText.setFont(GraphDefs.BILLVETICA);
      cText.setForeground(dataThread.getColor(firstItem));
      button.setIcon(new RotatedIcon(cText, RotatedIcon.Rotate.UP));
    }
    else
    {
      TextIcon cText = new TextIcon(button, "", TextIcon.Layout.HORIZONTAL);
      cText.setForeground(Color.WHITE);
      button.setIcon(new RotatedIcon(cText, RotatedIcon.Rotate.UP));
    }
  }

  /**
   * Button press handler for the stream panel button popup menus.
   */
  private void buttonAction(ActionEvent e)
  {
    String command = e.getActionCommand();
    int length = command.length();
    String actualCommand = command.substring(0, length - 1);
    String chart = command.substring(length - 1, length);

    // Move button text to top of list and redraw button.
    if (chart.equalsIgnoreCase(GraphDefs.A_CHART_NAME))
    {
      aButton.moveItemToFirst(actualCommand);
      setButtonText(aButton);
      dataThread.setRangeAxis(actualCommand);
    }
    else if (chart.equalsIgnoreCase(GraphDefs.B_CHART_NAME))
    {
      bButton.moveItemToFirst(actualCommand);
      setButtonText(bButton);
      dataThread.setRangeAxis(actualCommand);
    }
    else if (chart.equalsIgnoreCase(GraphDefs.C_CHART_NAME))
    {
      cButton.moveItemToFirst(actualCommand);
      setButtonText(cButton);
      dataThread.setRangeAxis(actualCommand);
    }
  }

  /**
   * Listener called when one of the stream panel buttons is called.  This simply displays a popup menu for the
   * user to select to change the y-axis.
   *
   * @param event The event.
   */
  @Override
  public void actionPerformed(ActionEvent event)
  {
    String actionCommand = event.getActionCommand();

    if (actionCommand.equalsIgnoreCase(MAKE_DEFAULT))
    {
      PROPS.setInsideTempStreamDisplayed(inTempCheckBox.isSelected());
      PROPS.setInsideHumidStreamDisplayed(inHumidCheckBox.isSelected());
      PROPS.setGreenTempStreamDisplayed(greenTempCheckBox.isSelected());
      PROPS.setOutsideTempStreamDisplayed(outTempCheckBox.isSelected());
      PROPS.setOutsideHumidStreamDisplayed(outHumidCheckBox.isSelected());
      PROPS.setIssReceptionStreamDisplayed(issRecepCheckBox.isSelected());
      PROPS.setSolarStreamDisplayed(solarRadCheckBox.isSelected());
      PROPS.setPressureStreamDisplayed(pressureCheckBox.isSelected());
      PROPS.setWindDirStreamDisplayed(windDirCheckBox.isSelected());
      PROPS.setWindSpeedStreamDisplayed(windSpeedCheckBox.isSelected());
      PROPS.setRainStreamDisplayed(rainCheckBox.isSelected());
      PROPS.setWindChillStreamDisplayed(windChillCheckBox.isSelected());
      PROPS.setHeatDDStreamDisplayed(heatDDCheckBox.isSelected());
      PROPS.setCoolDDStreamDisplayed(coolDDCheckBox.isSelected());
      PROPS.setDewPointStreamDisplayed(dewPointCheckBox.isSelected());
      PROPS.setHeatIndexStreamDisplayed(heatIndexCheckBox.isSelected());
      PROPS.setTHWSteamDisplayed(thwCheckBox.isSelected());
      PROPS.setTHSWStreamDisplayed(thswCheckBox.isSelected());
      PROPS.setETStreamDisplayed(etCheckBox.isSelected());
      PROPS.setWindRunStreamDisplayed(windRunCheckBox.isSelected());

      JOptionPane.showConfirmDialog(this,
                                    "Default Graph Saved", "Make Default", JOptionPane.DEFAULT_OPTION);
    }
    else if (actionCommand.equalsIgnoreCase(aButtonString))
    {
      JPopupMenu aPopupMenu = new JPopupMenu();
      aPopupMenu.setBorder(new BevelBorder(BevelBorder.RAISED));

      ArrayList<String> menuItems = aButton.getMenuItems();
      for (String nextMenuItem : menuItems)
      {
        JMenuItem nextItem = new JMenuItem(nextMenuItem);
        nextItem.setActionCommand(nextMenuItem + GraphDefs.A_CHART_NAME);
        nextItem.addActionListener(this::buttonAction);
        aPopupMenu.add(nextItem);
      }
      aPopupMenu.show(this, aButton.getX() + 35, aButton.getY() + 50);
    }
    else if (actionCommand.equalsIgnoreCase(bButtonString))
    {
      JPopupMenu bPopupMenu = new JPopupMenu();
      bPopupMenu.setBorder(new BevelBorder(BevelBorder.RAISED));

      ArrayList<String> menuItems = bButton.getMenuItems();
      for (String nextMenuItem : menuItems)
      {
        JMenuItem nextItem = new JMenuItem(nextMenuItem);
        nextItem.setActionCommand(nextMenuItem + GraphDefs.B_CHART_NAME);
        nextItem.addActionListener(this::buttonAction);
        bPopupMenu.add(nextItem);
      }
      bPopupMenu.show(this, bButton.getX() + 35, bButton.getY() + 230);
    }
    else if (actionCommand.equalsIgnoreCase(cButtonString))
    {
      JPopupMenu cPopupMenu = new JPopupMenu();
      cPopupMenu.setBorder(new BevelBorder(BevelBorder.RAISED));

      ArrayList<String> menuItems = cButton.getMenuItems();
      for (String nextMenuItem : menuItems)
      {
        JMenuItem nextItem = new JMenuItem(nextMenuItem);
        nextItem.setActionCommand(nextMenuItem + GraphDefs.C_CHART_NAME);
        nextItem.addActionListener(this::buttonAction);
        cPopupMenu.add(nextItem);
      }
      cPopupMenu.show(this, cButton.getX() + 35, cButton.getY() + 410);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.HALF_DAY_STRING))
    {
      changeDatasetSize(GraphDefs.HALF_DAY_DATA_SIZE);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.DAY_STRING))
    {
      changeDatasetSize(GraphDefs.DAY_DATA_SIZE);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.HALF_WEEK_STRING))
    {
      changeDatasetSize(GraphDefs.HALF_WEEK_DATA_SIZE);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.WEEK_STRING))
    {
      changeDatasetSize(GraphDefs.WEEK_DATA_SIZE);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.HALF_MONTH_STRING))
    {
      changeDatasetSize(GraphDefs.HALF_MONTH_DATA_SIZE);
    }
    else if (actionCommand.equalsIgnoreCase(GraphDefs.MONTH_STRING))
    {
      changeDatasetSize(GraphDefs.MONTH_DATA_SIZE);
    }
    else // One of the main menu check boxes was changed.
    {
      if (actionCommand.equalsIgnoreCase(GraphDefs.INTEMP_STRING))
      {
        handleCheckBoxAction(PROPS.getInsideTempChart(), GraphDefs.INTEMP_STRING,
                             inTempCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.INHUMID_STRING))
      {
        handleCheckBoxAction(PROPS.getInsideHumidChart(), GraphDefs.INHUMID_STRING,
                             inHumidCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.GREENHOUSE_TEMP_STRING))
      {
        handleCheckBoxAction(PROPS.getGreenTempChart(), GraphDefs.GREENHOUSE_TEMP_STRING,
                             greenTempCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.OUTTEMP_STRING))
      {
        handleCheckBoxAction(PROPS.getOutsideTempChart(), GraphDefs.OUTTEMP_STRING,
                             outTempCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.OUTHUMID_STRING))
      {
        handleCheckBoxAction(PROPS.getOutsideHumidChart(), GraphDefs.OUTHUMID_STRING,
                             outHumidCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.RAINFALL_STRING))
      {
        handleCheckBoxAction(PROPS.getRainChart(), GraphDefs.RAINFALL_STRING,
                             rainCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.ISS_RECEPTION_STRING))
      {
        handleCheckBoxAction(PROPS.getIssReceptionChart(), GraphDefs.ISS_RECEPTION_STRING,
                             issRecepCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.WIND_SPEED_STRING))
      {
        handleCheckBoxAction(PROPS.getWindSpeedChart(), GraphDefs.WIND_SPEED_STRING,
                             windSpeedCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.WIND_DIR_STRING))
      {
        handleCheckBoxAction(PROPS.getWindDirChart(), GraphDefs.WIND_DIR_STRING,
                             windDirCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.PRESSURE_STRING))
      {
        handleCheckBoxAction(PROPS.getPressureChart(), GraphDefs.PRESSURE_STRING,
                             pressureCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.SOLAR_RAD_STRING))
      {
        handleCheckBoxAction(PROPS.getSolarChart(), GraphDefs.SOLAR_RAD_STRING,
                             solarRadCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.WIND_CHILL_STRING))
      {
        handleCheckBoxAction(PROPS.getWindChillChart(), GraphDefs.WIND_CHILL_STRING,
                             windChillCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.HEAT_DD_STRING))
      {
        handleCheckBoxAction(PROPS.getHeatDDChart(), GraphDefs.HEAT_DD_STRING,
                             heatDDCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.COOL_DD_STRING))
      {
        handleCheckBoxAction(PROPS.getCoolDDChart(), GraphDefs.COOL_DD_STRING,
                             coolDDCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.DEW_POINT_STRING))
      {
        handleCheckBoxAction(PROPS.getDewPointChart(), GraphDefs.DEW_POINT_STRING,
                             dewPointCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.HEAT_INDEX_STRING))
      {
        handleCheckBoxAction(PROPS.getHeatIndexChart(), GraphDefs.HEAT_INDEX_STRING,
                             heatIndexCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.THW_STRING))
      {
        handleCheckBoxAction(PROPS.getThwChart(), GraphDefs.THW_STRING,
                             thwCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.THSW_STRING))
      {
        handleCheckBoxAction(PROPS.getThswChart(), GraphDefs.THSW_STRING,
                             thswCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.ET_STRING))
      {
        handleCheckBoxAction(PROPS.getEtChart(), GraphDefs.ET_STRING,
                             etCheckBox.isSelected(), event);
      }
      else if (actionCommand.equalsIgnoreCase(GraphDefs.WIND_RUN_STRING))
      {
        handleCheckBoxAction(PROPS.getWindRunChart(), GraphDefs.WIND_RUN_STRING,
                             windRunCheckBox.isSelected(), event);
      }
    }
  }

  /**
   * Internal method to handle the checkbox actions which includes modifying the chart button and adding/removing
   * the data from the chart.
   *
   * @param chart The chart.
   * @param dataName The name of the dataset.
   * @param isSelected Whether the checkbox has been selected.
   * @param event The event.
   */
  private void handleCheckBoxAction(String chart, String dataName, boolean isSelected, ActionEvent event)
  {
    modifyButton(isSelected, chart, dataName);
    buttonAction(new ActionEvent(event.getSource(), event.getID(), dataName + chart));

    if (isSelected)
    {
      dataThread.addDataToChart(chart, dataName);
    }
    else
    {
      dataThread.removeDataFromChart(chart, dataName);
    }
  }

  /**
   * Internal method to change the dataset size being displayed.
   *
   * @param size The new size of the dataset.
   */
  private void changeDatasetSize(int size)
  {
    dataThread.setDatasetSize(GraphDefs.INTEMP_STRING, size);
    dataThread.setDatasetSize(GraphDefs.INHUMID_STRING, size);
    dataThread.setDatasetSize(GraphDefs.GREENHOUSE_TEMP_STRING, size);
    dataThread.setDatasetSize(GraphDefs.OUTTEMP_STRING, size);
    dataThread.setDatasetSize(GraphDefs.OUTHUMID_STRING, size);
    dataThread.setDatasetSize(GraphDefs.RAINFALL_STRING, size);
    dataThread.setDatasetSize(GraphDefs.PRESSURE_STRING, size);
    dataThread.setDatasetSize(GraphDefs.ISS_RECEPTION_STRING, size);
    dataThread.setDatasetSize(GraphDefs.WIND_SPEED_STRING, size);
    dataThread.setDatasetSize(GraphDefs.WIND_DIR_STRING, size);
    dataThread.setDatasetSize(GraphDefs.SOLAR_RAD_STRING, size);
    dataThread.setDatasetSize(GraphDefs.WIND_CHILL_STRING, size);
    dataThread.setDatasetSize(GraphDefs.HEAT_DD_STRING, size);
    dataThread.setDatasetSize(GraphDefs.COOL_DD_STRING, size);
    dataThread.setDatasetSize(GraphDefs.DEW_POINT_STRING, size);
    dataThread.setDatasetSize(GraphDefs.HEAT_INDEX_STRING, size);
    dataThread.setDatasetSize(GraphDefs.THW_STRING, size);
    dataThread.setDatasetSize(GraphDefs.THSW_STRING, size);
    dataThread.setDatasetSize(GraphDefs.ET_STRING, size);
    dataThread.setDatasetSize(GraphDefs.WIND_RUN_STRING, size);

    dataThread.initData(size);
  }

  /**
   * Set the line to a new color.
   *
    * @param lineItem The item to change.
   * @param color The new color.
   */
  public void setLineColor(String lineItem, Color color)
  {
    dataThread.setColor(lineItem, color);
  }
}
