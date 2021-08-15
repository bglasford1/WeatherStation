/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class sends the most recent data to send to PWS weather.
            This is a thread that runs once every "configured" time period.
            The PWS weather interface is encapsulated within this class.
            This interface was almost the same as the Weather Underground interface.

  Mods:		  09/01/21 Initial Release.
*/
package wxserverif;

import data.consolerecord.DmpDataExtended;
import data.consolerecord.LoopData;
import util.ConfigProperties;
import util.TimeUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class WeatherServerIF
{
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private static WeatherServerIF instance = null;

  // Last data readings.
  private float outTemp;
  private float inTemp;
  private float outHumid;
  private float inHumid;
  private float pressure;
  private int windDir;
  private float windSpeed;
  private int hiWindDir;
  private float hiWindSpeed;
  private float greenTemp;
  private float solar;
  private float rain = 0; // Total over last hour.
  private final HashMap<Short, Float> rainValues = new HashMap<>();

  public static WeatherServerIF getInstance()
  {
    if (instance == null)
    {
      instance = new WeatherServerIF();
    }
    return instance;
  }

  /**
   * Set the local current data from the newest received loop data.
   *
   * @param data The loop data.
   */
  public void setCurrentData(LoopData data)
  {
    this.outTemp = data.getOutsideTemp();
    this.inTemp = data.getInsideTemp();
    this.pressure = data.getPressure();
    this.inHumid = data.getInsideHumidity();
    this.outHumid = data.getOutsideHumidity();
    this.windSpeed = data.getWindSpeed();
    this.windDir = data.getWindDirection();
    this.solar = data.getSolarRadiation();
    this.greenTemp = data.getSoilTemp1();
  }

  /**
   * Set the local current data from the newest received dmp data.  Should this be from the loop data???
   *
   * @param data The dmp data.
   */
  public void setDmpData(DmpDataExtended data)
  {
    this.hiWindSpeed = data.getHighWindSpeed();
    this.hiWindDir = data.getHighWindDirection();

    // Save current rain value if not zero and add to rain total.
    if (data.getRainfall() != 0.0f)
    {
      rainValues.put(data.getTimeStamp(), (float)(data.getRainfall() / 10));
      rain += data.getRainfall();

      // Drop old values outside the hour range and subtract from rain total.
      LocalDateTime now = LocalDateTime.now();
      now.minus(1, ChronoUnit.HOURS);
      short packedTime = TimeUtil.getPackedTime(now.getHour(), now.getMinute());
      for (short nextTimestamp : rainValues.keySet())
      {
        if (nextTimestamp < packedTime)
        {
          Float rainValue = rainValues.remove(nextTimestamp);
          rain -= rainValue;
        }
      }
    }
  }

  /**
   * Send the current local data.
   *
   * @return Whether or not the call to weather underground worked.
   */
  public boolean sendData() // TODO: need to call this once every "configured" minutes.
  {
    LocalDate dateNow = LocalDate.now();
    LocalTime timeNow = LocalTime.now();

    // Format is "YYYY-MM-DD+HH:MM:SS", ex: "2018-10-25+10:12:00".  Note: ":" = ascii 3A.
    String utcDateString =
      String.valueOf(dateNow.getYear()) + "-" +
      String.valueOf(dateNow.getMonthValue()) + "-" +
      String.valueOf(dateNow.getDayOfMonth()) + "+" +
      String.valueOf(timeNow.getHour()) + "%3A" +
      String.valueOf(timeNow.getMinute()) + "%3A" +
      String.valueOf(timeNow.getSecond());

    String http_url = PROPS.getWxUrl();
    http_url += "ID=" + PROPS.getWxStationId();
    http_url += "&PASSWORD=" + PROPS.getWxPassword();
    http_url += "&dateutc=" + utcDateString;
    http_url += "&winddir=" + Integer.toString(windDir);
    http_url += "&windspeedmph=" + Float.toString(windSpeed);
    http_url += "&windgustmph=" + Float.toString(hiWindSpeed);
//    https_url += "&winggustdir=" + Integer.toString(hiWindDir);
    http_url += "&humidity=" + Float.toString(outHumid);
    http_url += "&tempf=" + Float.toString(outTemp);
    http_url += "&rainin=" + Float.toString(rain);
    http_url += "&baromin=" + Float.toString(pressure);
    http_url += "&dewptf=" + "68.1";
//    https_url += "&soiltempf=" + Float.toString(greenTemp);
    http_url += "&solarradiation=" + Float.toString(solar);
//    https_url += "&indoortempf=" + Float.toString(inTemp);
//    https_url += "&indoorhumidity=" + Float.toString(inHumid);
    // TODO: add dailyrainin
    http_url += "&softwaretype=wtgwx_ver1.0";
    http_url += "&action=updateraw";

    try
    {
      URL url = new URL(http_url);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();

      if (connection.getResponseCode() != 200)
      {
        System.out.println("Bad response code: " + connection.getResponseCode());
        return false;
      }
      readResponse(connection);
      return true;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Read and interpret the response code.
   *
   * @param connection The URL connection.
   */
  private void readResponse(HttpURLConnection connection)
  {
    if (connection != null)
    {
      try
      {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String input;
        while ((input = br.readLine()) != null)
        {
          System.out.println(input);
        }
        br.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args)
  {
    WeatherServerIF wxInterface = new WeatherServerIF();
    wxInterface.outTemp = 44.4f;
    wxInterface.outHumid = 33.3f;
    wxInterface.pressure = 32.2f;
    wxInterface.windDir = 3;
    wxInterface.windSpeed = 10;
    wxInterface.hiWindSpeed = 12;
    wxInterface.solar = 333;
    wxInterface.rain = 0;
    boolean response = wxInterface.sendData();
    System.out.println(response);
  }
}
