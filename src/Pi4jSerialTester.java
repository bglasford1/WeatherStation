/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class tests the serial comm to the weather station.  It
            provides a simple GUI to select commands to send and displays
            the results.

  Mods:		  09/01/21  Initial Release.
            01/10/22  Added comment on how to launch.
*/
import com.pi4j.io.serial.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Method to test the interface with the console at a low level.
 *
 * To execute this program run: > java -cp .:./pi4j-1.2/lib/pi4j-core.jar Pi4jSerialTester
 */
public class Pi4jSerialTester implements ActionListener
{
  private static final String OPEN = "Open";
  private static final String CLOSE = "Close";
  private static final String WAKEUP = "Wakeup";
  private static final String TEST = "Test";
  private static final String VERSION = "Version";
  private static final String RXCHECK = "RXCheck";
  private static final String LAMP_ON = "LampOn";
  private static final String LAMP_OFF = "LampOff";
  private static final String GET_TIME = "GetTime";
  private static final String VER_NUMBER = "Version Number";
  private static final String HILOWS_CMD = "Hi Lows";
  private static final String LOOP_CMD = "Loop Cmd";
  private static final String DMP_CMD = "Dump Cmd";

  private static final String testCmd    = "TEST\n";
  private static final String rxcheckCmd = "RXCHECK\r";
  private static final String versionCmd = "VERS\r";
  private static final String versionNumberCmd = "NVER\r";
  private static final String loopCmd    = "LOOP 2\n";
  private static final String hilowsCmd  = "HILOWS\n";
  private static final String dmpCmd     = "DMP\n";
  private static final String dmpaftCmd  = "DMPAFT\n";
  private static final String gettimeCmd = "GETTIME\n";
  private static final String lampOnCmd  = "LAMPS 1\n";
  private static final String lampOffCmd = "LAMPS 0\n";
  private static final String wakeupCmd  = "\r";

  private final Serial serial = SerialFactory.createInstance();
  private final JTextArea textArea;
  private JTextField portTextField = new JTextField("/dev/ttyUSB0");


  private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

  private FileWriter fileOut;
  
  private Pi4jSerialTester()
  {
    // !! ATTENTION !!
    // By default, the serial port is configured as a console port
    // for interacting with the Linux OS shell.  If you want to use
    // the serial port in a software program, you must disable the
    // OS from using this port.

    try
    {
      fileOut = new FileWriter("output.dmp");
    }
    catch (IOException e)
    {
      System.out.println("File open failed: " + e.getMessage());
    }

    JFrame frame = new JFrame("Serial Tester");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    
    // Create a text area for output
    textArea = new JTextArea(20, 600);
    textArea.setLineWrap(true);
    JScrollPane scrollPane = new JScrollPane(textArea);
    
    // Create panel of buttons on top and output text box below.
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(6,3));

    JButton openInterfaceButton = new JButton(OPEN);
    openInterfaceButton.setActionCommand(OPEN);
    openInterfaceButton.addActionListener(this);

    JButton closeInterfaceButton = new JButton(CLOSE);
    closeInterfaceButton.setActionCommand(CLOSE);
    closeInterfaceButton.addActionListener(this);

    JButton wakeupButton = new JButton(WAKEUP);
    wakeupButton.setActionCommand(WAKEUP);
    wakeupButton.addActionListener(this);
  
    JButton testButton = new JButton(TEST);
    testButton.setActionCommand(TEST);
    testButton.addActionListener(this);
  
    JButton versionButton = new JButton(VERSION);
    versionButton.setActionCommand(VERSION);
    versionButton.addActionListener(this);
  
    JButton rxcheckButton = new JButton(RXCHECK);
    rxcheckButton.setActionCommand(RXCHECK);
    rxcheckButton.addActionListener(this);
  
    JButton getTimeButton = new JButton(GET_TIME);
    getTimeButton.setActionCommand(GET_TIME);
    getTimeButton.addActionListener(this);
  
    JButton lampOnButton = new JButton(LAMP_ON);
    lampOnButton.setActionCommand(LAMP_ON);
    lampOnButton.addActionListener(this);
  
    JButton lampOffButton = new JButton(LAMP_OFF);
    lampOffButton.setActionCommand(LAMP_OFF);
    lampOffButton.addActionListener(this);

    JButton verNumberButton = new JButton(VER_NUMBER);
    verNumberButton.setActionCommand(VER_NUMBER);
    verNumberButton.addActionListener(this);

    JButton hilowsButton = new JButton(HILOWS_CMD);
    hilowsButton.setActionCommand(HILOWS_CMD);
    hilowsButton.addActionListener(this);

    JButton dumpButton = new JButton(DMP_CMD);
    dumpButton.setActionCommand(DMP_CMD);
    dumpButton.addActionListener(this);

    JButton loopButton = new JButton(LOOP_CMD);
    loopButton.setActionCommand(LOOP_CMD);
    loopButton.addActionListener(this);

    buttonPanel.add(portTextField);
    buttonPanel.add(openInterfaceButton);
    buttonPanel.add(closeInterfaceButton);
    buttonPanel.add(wakeupButton);
    buttonPanel.add(testButton);
    buttonPanel.add(versionButton);
    buttonPanel.add(rxcheckButton);
    buttonPanel.add(getTimeButton);
    buttonPanel.add(lampOnButton);
    buttonPanel.add(lampOffButton);
    buttonPanel.add(verNumberButton);
    buttonPanel.add(hilowsButton);
    buttonPanel.add(dumpButton);
    buttonPanel.add(loopButton);
    
    // Combine button panel and scroll pane
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(2, 1));
    mainPanel.add(buttonPanel);
    mainPanel.add(scrollPane);
    
    frame.setContentPane(mainPanel);
    frame.setSize(600, 400);
    frame.setVisible(true);
    
    // create and register the serial data listener
    serial.addListener(event ->
     {
       // NOTE! - It is extremely important to read the data received from the
       // serial port.  If it does not get read from the receive buffer, the
       // buffer will continue to grow and consume memory.
       try
       {
         textArea.append("[HEX DATA] ");
         String hexString = bytesToHex(event.getBytes());
         textArea.append(hexString);
         textArea.append("[ASCII DATA] " + event.getAsciiString());
         fileOut.append(hexString);
       }
       catch (IOException e)
       {
         e.printStackTrace();
       }
     });
  }
  
  @Override
  public void actionPerformed(ActionEvent e)
  {
    switch (e.getActionCommand())
    {
      case OPEN:
        // create serial config object
        SerialConfig config = new SerialConfig();

        try
        {
          // set default serial settings (device, baud rate, flow control, etc)
          config.device(portTextField.getText())
                .baud(Baud._19200)
                .dataBits(DataBits._8)
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE);

          textArea.append(" Connecting to: " + config.toString());

          // open the default serial device/port with the configuration settings
          serial.open(config);

          textArea.append(" Connected...");
        }
        catch (IOException e1)
        {
          textArea.append("==> SERIAL SETUP FAILED : " + e1.getMessage());
        }
        break;
      case CLOSE:
        try
        {
          fileOut.close();
        }
        catch (IOException e1)
        {
          textArea.append("==> File close Command FAILED : " + e1.getMessage());
        }
        break;
      case WAKEUP:
        try
        {
          serial.write(wakeupCmd);
        }
        catch (IOException e1)
        {
          textArea.append("==> Wakeup Command FAILED : " + e1.getMessage());
        }
        break;
      case TEST:
        try
        {
          serial.write(testCmd);
        }
        catch (IOException e1)
        {
          textArea.append("==> Test Command FAILED : " + e1.getMessage());
        }
        break;
      case VERSION:
        try
        {
          serial.writeln(versionCmd);
        }
        catch (IOException e1)
        {
          textArea.append("==> Version Command FAILED : " + e1.getMessage());
        }
        break;
      case RXCHECK:
        try
        {
          serial.writeln(rxcheckCmd);
        }
        catch (IOException e1)
        {
          textArea.append("==> RXCheck Command FAILED : " + e1.getMessage());
        }
        break;
      case GET_TIME:
        try
        {
          serial.writeln(gettimeCmd);
        }
        catch (IOException e1)
        {
          textArea.append("==> GetTime Command FAILED : " + e1.getMessage());
        }
        break;
      case LAMP_ON:
        try
        {
          serial.writeln(lampOnCmd);
        }
        catch (IOException e1)
        {
          textArea.append("==> Lamp On Command FAILED : " + e1.getMessage());
        }
        break;
      case LAMP_OFF:
        try
        {
          serial.writeln(lampOffCmd);
        }
        catch (IOException e1)
        {
          textArea.append("==> Lamp Off Command FAILED : " + e1.getMessage());
        }
        break;
      case VER_NUMBER:
        try
        {
          serial.writeln(versionNumberCmd);
        }
        catch (IOException e1)
        {
          textArea.append("==> Version numbers Command FAILED : " + e1.getMessage());
        }
        break;
      case HILOWS_CMD:
        try
        {
          serial.writeln(hilowsCmd);
        }
        catch (IOException e1)
        {
          textArea.append("==> Hi Lows Command FAILED : " + e1.getMessage());
        }
        break;
      case DMP_CMD:
        try
        {
          serial.writeln(dmpCmd);
        }
        catch (IOException e1)
        {
          textArea.append("==> Dump Command FAILED : " + e1.getMessage());
        }
        break;
      case LOOP_CMD:
        try
        {
          serial.writeln(loopCmd);
        }
        catch (IOException e1)
        {
          textArea.append("==> Loop Command FAILED : " + e1.getMessage());
        }
        break;
    }
  }

  private static String bytesToHex(byte[] bytes)
  {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++)
    {
      int v = bytes[j] & 0xFF;
      hexChars[j*2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }
  
  public static void main(String args[])
  {
    new Pi4jSerialTester();
  }
}