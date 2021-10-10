/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that gets the NOAA Alert Forecasts.  The alerts are returned
            in a JSON format.

  Mods:		  09/01/21  Initial Release.
            10/11/21  Changed hourly data to tabular format.
*/
package forecast;

import data.dbrecord.WindDirection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class NOAAForecastJSON
{
  private final Logger logger = Logger.getInstance();
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

  // The severity of NOAA alerts.
  public enum Severity
  {
    Extreme,
    Severe,
    Moderate,
    Minor,
    Unknown
  }

  /**
   * Private method to get the Alert JSON.
   *
   * @param urlString The NOAA URL.
   * @return The JSON string.
   */
  private String getJSON(String urlString)
  {
    StringBuilder jsonString = new StringBuilder();
    for (int trys = 0; trys < 3; trys++)
    {
      try
      {
        URL url = new URL(urlString);

        // read from the URL
        Scanner scan = new Scanner(url.openStream());
        while (scan.hasNext())
          jsonString.append(scan.nextLine());
        scan.close();
        return jsonString.toString();
      }
      catch (IOException ioe)
      {
        logger.logData("Unable to get NOAA Forecast, try # " + trys + ioe.getMessage());
      }
    }
    return null;
  }

  /**
   * Get the severity of any active alert.  This is hardcoded to the zone in which I live.
   *
   * @return The severity enum or null if alert could not be retreived.
   */
  public Severity getSeverity()
  {
    try
    {
      String jsonString = getJSON("https://api.weather.gov/alerts/active/zone/COZ084");
      if (jsonString == null)
        return Severity.Unknown;

      JSONObject obj = new JSONObject(jsonString);
      JSONArray featuresArray = obj.getJSONArray("features");
      if (featuresArray.length() == 0)
      {
        return Severity.Unknown;
      }
      else
      {
        JSONObject properties  = featuresArray.getJSONObject(0).getJSONObject("properties");
        String severityString = properties.getString("severity");
        return Severity.valueOf(severityString);
      }
    }
    catch (JSONException | IllegalArgumentException e)
    {
      return Severity.Unknown;
    }
  }

  /**
   * Method to get the actual alert.  Portions of the alert are extracted and formatted into a string.
   *
   * @return The alert text.
   */
  public String getAlert()
  {
    String jsonString = getJSON("https://api.weather.gov/alerts/active/zone/COZ084");
    if (jsonString == null)
      return "Alert unavailable...";

    JSONObject obj = new JSONObject(jsonString);
    JSONArray featuresArray = obj.getJSONArray("features");
    if (featuresArray.length() == 0)
    {
      return "No alerts at this time...";
    }
    else
    {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < featuresArray.length(); i++)
      {
        JSONObject json  = featuresArray.getJSONObject(i).getJSONObject("properties");
        String event = json.getString("event");
        builder.append("Event: ").append(event).append("\n");
        String messageType = json.getString("messageType");
        builder.append("Message Type: ").append(messageType).append("\n");
        String severity = json.getString("severity");
        builder.append("Severity: ").append(severity).append("\n\n");
        if (!json.isNull("areaDesc"))
        {
          String areaDescription = json.getString("areaDesc");
          builder.append("Area Description: ").append(areaDescription).append("\n");
        }
        if (!json.isNull("onset"))
        {
          String onsetTime = json.getString("onset");
          builder.append("Onset Time: ").append(onsetTime).append("\n");
        }
        if (!json.isNull("expires"))
        {
          String expireTime = json.getString("expires");
          builder.append("Expire Time: ").append(expireTime).append("\n\n");
        }
        if (!json.isNull("description"))
        {
          String description = json.getString("description");
          builder.append("Description: ").append(description).append("\n\n");
        }
        if (!json.isNull("instruction"))
        {
          String instruction = json.getString("instruction");
          builder.append("Instruction: ").append(instruction);
        }
      }
      return builder.toString();
    }
  }

  /**
   * Method to get the daily forecasts by day as a double array.
   *
   * @return The daily forecasts.
   */
  public String[][] getDailyForecast()
  {
    String dailyForecastURL = "https://api.weather.gov/zones/public/COZ084/forecast";
    String jsonString = getJSON(dailyForecastURL);
    if (jsonString == null)
      return null;

    JSONObject json = new JSONObject(jsonString);

    StringBuilder builder = new StringBuilder();
    JSONObject properties = json.getJSONObject("properties");
    JSONArray periodsArray = properties.getJSONArray("periods");
    String[][] dailyData = new String[periodsArray.length()][6];
    for (int i = 0; i < periodsArray.length(); i++)
    {
      String name = periodsArray.getJSONObject(i).getString("name");
      builder.append(name).append(": ");
      String detailedForecast = periodsArray.getJSONObject(i).getString("detailedForecast");
      builder.append(detailedForecast).append("\n");
    }
    return dailyData;
  }

  /**
   * Method to get the hourly forecasts as a double array.
   *
   * @return The hourly forecast data.
   */
  public String[][] getHourlyForecasts()
  {
    String hourlyForecastURL = "https://api.weather.gov/gridpoints/PUB/92,97/forecast/hourly";
    String jsonString = getJSON(hourlyForecastURL);
    if (jsonString == null)
      return null;

    JSONObject json = new JSONObject(jsonString);

    JSONObject properties = json.getJSONObject("properties");
    JSONArray periodsArray = properties.getJSONArray("periods");
    String[][] hourlyData = new String[periodsArray.length()][5];
    for (int i = 0; i < periodsArray.length(); i++)
    {
      String time = periodsArray.getJSONObject(i).getString("startTime");
      time = time.replace('T', ' ');
      time = time.substring(0, 13);
      LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
      String date = dateTime.getMonthValue() + "/" + dateTime.getDayOfMonth() + "/" + dateTime.getYear() +
                    ":" + dateTime.getHour();
      hourlyData[i][0] = date;

      Number temperature = periodsArray.getJSONObject(i).getNumber("temperature");
      hourlyData[i][1] = temperature.toString();

      String shortForecast = periodsArray.getJSONObject(i).getString("shortForecast");
      shortForecast = shortForecast.replace(" ", "");
      hourlyData[i][2] = NOAAForecast.valueOf(shortForecast).toString();

      String windSpeed = periodsArray.getJSONObject(i).getString("windSpeed");
      String[] parsedValues = windSpeed.split("\\s+");
      hourlyData[i][3] = parsedValues[0];

      String windDir = periodsArray.getJSONObject(i).getString("windDirection");
      hourlyData[i][4] = WindDirection.valueOf(windDir).toString();
    }
    return hourlyData;
  }
}
