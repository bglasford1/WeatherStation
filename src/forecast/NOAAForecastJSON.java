/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that gets the NOAA Alert Forecasts.  The alerts are returned
            in a JSON format.

  Mods:		  09/01/21 Initial Release.
*/
package forecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class NOAAForecastJSON
{
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
    try
    {
      URL url = new URL(urlString);

      // read from the URL
      Scanner scan = new Scanner(url.openStream());
      while (scan.hasNext())
        jsonString.append(scan.nextLine());
      scan.close();
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
    return jsonString.toString();
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
   * Method to get the daily forecasts.
   *
   * @return The daily forecasts.
   */
  public String getDailyForecast()
  {
    String dailyForecastURL = "https://api.weather.gov/zones/public/COZ084/forecast";
    String jsonString = getJSON(dailyForecastURL);
    JSONObject json = new JSONObject(jsonString);

    StringBuilder builder = new StringBuilder();
    JSONArray periodsArray = json.getJSONArray("periods");
    for (int i = 0; i < periodsArray.length(); i++)
    {
      String name = periodsArray.getJSONObject(i).getString("name");
      builder.append(name).append(": ");
      String detailedForecast = periodsArray.getJSONObject(i).getString("detailedForecast");
      builder.append(detailedForecast).append("\n");
    }
    return builder.toString();
  }

  /**
   * Method to get the hourly forecasts.
   *
   * @return The hourly forecasts.
   */
  public String getHourlyForecast()
  {
    String hourlyForecastURL = "https://api.weather.gov/gridpoints/PUB/92,97/forecast/hourly";
    String jsonString = getJSON(hourlyForecastURL);
    JSONObject json = new JSONObject(jsonString);

    StringBuilder builder = new StringBuilder();
    JSONObject properties = json.getJSONObject("properties");
    JSONArray periodsArray = properties.getJSONArray("periods");
    for (int i = 0; i < periodsArray.length(); i++)
    {
      String time = periodsArray.getJSONObject(i).getString("startTime");
      builder.append("Time: ").append(time.substring(0, 10)).append(":").append(time.substring(11, 13));
      Number temperature = periodsArray.getJSONObject(i).getNumber("temperature");
      builder.append(" Temp: ").append(temperature);
      String windSpeed = periodsArray.getJSONObject(i).getString("windSpeed");
      builder.append(" Wind Speed: ").append(windSpeed);
      String windDir = periodsArray.getJSONObject(i).getString("windDirection");
      builder.append(" Wind Dir: ").append(windDir);
      String shortForecast = periodsArray.getJSONObject(i).getString("shortForecast");
      builder.append(" Forecast: ").append(shortForecast).append("\n");
    }
    return builder.toString();
  }

  public static void main(String[] args)
  {
    NOAAForecastJSON noaaForecastJSON = new NOAAForecastJSON();

    System.out.println(noaaForecastJSON.getDailyForecast());
    System.out.println("---------------------------------------");

    System.out.println(noaaForecastJSON.getHourlyForecast());
    System.out.println("---------------------------------------");

    String alert = noaaForecastJSON.getAlert();
    System.out.println(alert);
  }
}
