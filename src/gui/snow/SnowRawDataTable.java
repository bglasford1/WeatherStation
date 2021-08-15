/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class defines and maintains the snow data table which
            encapsulates a java table/table model and displays the table in a
            JPanel.

  Mods:		  09/01/21 Initial Release.
*/
package gui.snow;

import data.dbrecord.SnowRecord;
import dbif.SnowDatabase;
import util.ConfigProperties;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * This class defines and maintains the snow data table which encapsulates a java table/table model and displays the
 * table in a JPanel.
 */
public class SnowRawDataTable extends JPanel implements TableModelListener, ActionListener, SnowDataListener
{
  private static final String ADD_STRING    = "Add";
  private static final String DELETE_STRING = "Delete";

  private static final JFrame FRAME = new JFrame("Snow Data Table");
  private final JTable table;
  private final DefaultTableModel tableModel;
  private SnowDataInputDialog snowDataInputDialog;
  private final SnowDatabase snowDatabase = SnowDatabase.getInstance();
  private static final ConfigProperties PROPS = ConfigProperties.instance();

  private final List<SnowRecord> snowRecords;
  private final SnowRecord oldSnowRecord = new SnowRecord();

  private static final int YEAR_COLUMN = 0;
  private static final int MONTH_COLUMN = 1;
  private static final int DAY_COLUMN = 2;
  private static final int AMOUNT_COLUMN = 3;
  private static final String[] columnNames =
    {"Year",
     "Month",
     "Day",
     "Amount"
    };

  /**
   * Constructor that creates the data table and creates the popup menu.
   */
  public SnowRawDataTable()
  {
    super(new GridLayout(1, 0));

    snowDatabase.addListener(this);

    // Populate the datasets.
    snowRecords = snowDatabase.readData();

    Object[][] tableData = new Object[snowRecords.size()][4];
    int rowCount = 0;

    for (SnowRecord nextRecord : snowRecords)
    {
      tableData[rowCount][YEAR_COLUMN] = nextRecord.getYear();
      tableData[rowCount][MONTH_COLUMN] = nextRecord.getMonth();
      tableData[rowCount][DAY_COLUMN] = nextRecord.getDay();
      tableData[rowCount][AMOUNT_COLUMN] = nextRecord.getAmount();

      rowCount++;
    }

    tableModel = new DefaultTableModel(tableData, columnNames);
    table = new JTable(tableModel);
    table.setPreferredScrollableViewportSize(new Dimension(500, 70));
    table.getModel().addTableModelListener(this);

    // Add a popup menu.
    final JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem addItem = new JMenuItem(ADD_STRING);
    addItem.addActionListener(this);
    JMenuItem deleteItem = new JMenuItem(DELETE_STRING);
    deleteItem.addActionListener(this);

    popupMenu.add(addItem);
    popupMenu.add(deleteItem);
    table.setComponentPopupMenu(popupMenu);

    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);

    //Add the scroll pane to this panel.
    add(scrollPane);
  }

  /**
   * Create the GUI and show it. For thread safety,
   * this method should be invoked from the event-dispatching thread.
   */
  public static void createAndShowGUI(JPanel dataTable)
  {
    // Make sure we have nice window decorations.
    JFrame.setDefaultLookAndFeelDecorated(true);

    // Set up the content pane.
    dataTable.setOpaque(true);

    // content panes must be opaque
    FRAME.setContentPane(dataTable);

    // Display the window.
    FRAME.setSize(PROPS.getWindowWidth(), PROPS.getWindowHeight());
    FRAME.setVisible(true);
  }

  /**
   * Method called when the user changes something in the table.
   *
   * @param event The table model event.
   */
  @Override
  public void tableChanged(TableModelEvent event)
  {
    int row = event.getFirstRow();
    int column = event.getColumn();
    if (column == -1)
      return;

    TableModel model = (TableModel) event.getSource();
    String columnName = model.getColumnName(column);
    Object data = model.getValueAt(row, column);

    SnowRecord oldSnowRecord = snowRecords.get(row);
    SnowRecord newSnowRecord = new SnowRecord();
    if (columnNames[YEAR_COLUMN].equalsIgnoreCase(columnName))
    {
      // Year value changed.
      int year = Integer.parseInt((String)data);
      newSnowRecord.setYear(year);
      newSnowRecord.setMonth(oldSnowRecord.getMonth());
      newSnowRecord.setDay(oldSnowRecord.getDay());
      newSnowRecord.setAmount(oldSnowRecord.getAmount());
    }
    else if (columnNames[MONTH_COLUMN].equalsIgnoreCase(columnName))
    {
      // Month value changed.
      int month = Integer.parseInt((String)data);
      newSnowRecord.setYear(oldSnowRecord.getYear());
      newSnowRecord.setMonth(month);
      newSnowRecord.setDay(oldSnowRecord.getDay());
      newSnowRecord.setAmount(oldSnowRecord.getAmount());
    }
    else if (columnNames[DAY_COLUMN].equalsIgnoreCase(columnName))
    {
      // Day value changed.
      int day = Integer.parseInt((String)data);
      newSnowRecord.setYear(oldSnowRecord.getYear());
      newSnowRecord.setMonth(oldSnowRecord.getMonth());
      newSnowRecord.setDay(day);
      newSnowRecord.setAmount(oldSnowRecord.getAmount());
    }
    else if (columnNames[AMOUNT_COLUMN].equalsIgnoreCase(columnName))
    {
      // Amount value changed.
      float amount = Float.parseFloat((String)data);
      newSnowRecord.setYear(oldSnowRecord.getYear());
      newSnowRecord.setMonth(oldSnowRecord.getMonth());
      newSnowRecord.setDay(oldSnowRecord.getDay());
      newSnowRecord.setAmount(amount);
    }

    snowDatabase.modifyRecord(oldSnowRecord, newSnowRecord);
  }

  /**
   * Method to handle the menu events.
   *
   * @param event The action event.
   */
  @Override
  public void actionPerformed(ActionEvent event)
  {
    String action = event.getActionCommand();
    if (action.equalsIgnoreCase(ADD_STRING))
    {
      SnowRecord snowRecord = new SnowRecord();
      snowRecord.setYear(Year.now().getValue());
      snowRecord.setMonth(LocalDate.now().getMonthValue());
      snowRecord.setDay(LocalDate.now().getDayOfMonth());
      snowRecord.setAmount(0);

      // Display a dialog box to get the new data.
      if (snowDataInputDialog == null)
      {
        snowDataInputDialog = new SnowDataInputDialog(FRAME, snowRecord);
      }
      else
      {
        snowDataInputDialog.setData(oldSnowRecord);
        snowDataInputDialog.setVisible(true);
      }
    }
    else if (action.equalsIgnoreCase(DELETE_STRING))
    {
      // Delete the line in question.
      int dialogResult = JOptionPane.showConfirmDialog
        (FRAME, "Delete selecte row?","Warning", JOptionPane.YES_NO_OPTION);

      if (dialogResult == JOptionPane.YES_OPTION)
      {
        SnowRecord snowRecord = new SnowRecord();
        snowRecord.setYear((int)tableModel.getValueAt(table.getSelectedRow(), YEAR_COLUMN));
        snowRecord.setMonth((int)tableModel.getValueAt(table.getSelectedRow(), MONTH_COLUMN));
        snowRecord.setDay((int)tableModel.getValueAt(table.getSelectedRow(), DAY_COLUMN));
        snowRecord.setAmount((float)tableModel.getValueAt(table.getSelectedRow(), AMOUNT_COLUMN));

        snowDatabase.deleteRecord(snowRecord);
        tableModel.removeRow(table.getSelectedRow());
      }
    }
  }

  /**
   * Method to handle when snow data is added.
   *
   * @param data The new snow data record.
   */
  @Override
  public void dataAdded(SnowRecord data)
  {
    tableModel.addRow(new Object[] { data.getYear(), data.getMonth(), data.getDay(), data.getAmount() });
  }
}
