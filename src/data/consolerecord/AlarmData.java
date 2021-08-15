/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	Class that encapsulates the alarm data.  The alarm data is the
            current data values on the console.  The data is written to the EE PROM.

  Mods:		  09/01/21 Initial Release.
*/
package data.consolerecord;

import util.ByteUtil;

import java.math.BigInteger;

public class AlarmData
{
  private static final float HUNDREDTHS = 100;

  private byte barRiseAlarm = 0;   // Zero = no alarm set.  1 to 255 represent .001 to .255
  private byte barFallAlarm = 0;
  private short timeAlarm = 0;     // Of the form HOUR * 100 + MINUTES.
  private short timeCompAlarm = 0; // 0xFFFF = no alarm set. // equals one's compliment of time value.
  private byte lowInTempAlarm = 0; // 255 = no alarm set.  1 degree resolution, +90 is added so every # is positive.
  private byte highInTempAlarm = 0;
  private byte lowOutTempAlarm = 0;
  private byte highOutTempAlarm = 0;
  private byte lowSoil1TempAlarm = 0;
  private byte highSoil1TempAlarm = 0;
  private byte lowInHumidAlarm = 0; // 255 = no alarm set.  1% resolution.
  private byte highInHumidAlarm = 0;
  private byte lowOutHumidAlarm = 0;
  private byte highOutHumidAlarm = 0;
  private byte lowDewAlarm = 0;     // 255 = no alarm set.  1 degree resolution, +120 is added to make positive.
  private byte highDewAlarm = 0;
  private byte windChillAlarm = 0;  // 255 = no alarm set.  1 degree resolution, +120 is added to make positive.
  private byte heatIndexAlarm = 0;
  private byte thswAlarm = 0;
  private byte windSpeedAlarm = 0;  // 255 = no alarm set.  1 mph resolution.
  private byte windSpeed10MinAlarm = 0;
  private short solarAlarm = 0;     // 0xFFFF = no alarm set.  1 wm2 resolution, valid range 0 to 1800.
  private short rainRateAlarm = 0;  // 0xFFFF = no alarm set.  0.01" resolution (rain clicks).
  private short rain15MinAlarm = 0;
  private short rain24HrAlarm = 0;
  private short rainStormAlarm = 0;

  /**
   * Method called to set the data in one shot based on the buffer received from the console.
   */
  public void setData (byte[] buffer)
  {
    System.out.println(ByteUtil.bytesToHex(buffer)); // TODO: remove...
    byte[] tempBuffer = new byte[2];

    // Extract the barometer trends.
    barRiseAlarm = buffer[0];
    barFallAlarm = buffer[1];

    // Extract the time alarm.
    tempBuffer[0] = buffer[3];
    tempBuffer[1] = buffer[2];
    timeAlarm = (short) new BigInteger(tempBuffer).intValue();

    // Extract the time comp alarm.
    tempBuffer[0] = buffer[5];
    tempBuffer[1] = buffer[4];
    timeCompAlarm = (short) new BigInteger(tempBuffer).intValue();

    // Extract the temperature alarms.
    lowInTempAlarm = buffer[6];
    highInTempAlarm = buffer[7];
    lowOutTempAlarm = buffer[8];
    highOutTempAlarm = buffer[9];
    lowSoil1TempAlarm = buffer[17];
    highSoil1TempAlarm = buffer[32];

    // Extract the humidity alarms.
    lowInHumidAlarm = buffer[40];
    highInHumidAlarm = buffer[41];
    lowOutHumidAlarm = buffer[42];
    highOutHumidAlarm = buffer[50];

    // Extract the various calculated temperature alarms.
    lowDewAlarm = buffer[58];
    highDewAlarm = buffer[59];
    windChillAlarm = buffer[60];
    heatIndexAlarm = buffer[61];
    thswAlarm = buffer[62];

    // Extract the wind alarms.
    windSpeedAlarm = buffer[63];
    windSpeed10MinAlarm = buffer[64];

    // Extract the solar alarm.
    tempBuffer[0] = buffer[84];
    tempBuffer[1] = buffer[83];
    solarAlarm = (short) new BigInteger(tempBuffer).intValue();

    // Extract the rain rate alarm.
    tempBuffer[0] = buffer[86];
    tempBuffer[1] = buffer[85];
    rainRateAlarm = (short) new BigInteger(tempBuffer).intValue();

    // Extract the 15 minute rain alarm.
    tempBuffer[0] = buffer[88];
    tempBuffer[1] = buffer[87];
    rain15MinAlarm = (short) new BigInteger(tempBuffer).intValue();

    // Extract the 24 hour rain alarm.
    tempBuffer[0] = buffer[90];
    tempBuffer[1] = buffer[89];
    rain24HrAlarm = (short) new BigInteger(tempBuffer).intValue();

    // Extract the storm rain alarm.
    tempBuffer[0] = buffer[92];
    tempBuffer[1] = buffer[91];
    rainStormAlarm = (short) new BigInteger(tempBuffer).intValue();
  }

  /**
   * Get the pressure rise alarm.  A non-zero value represents the inches of mercury the alarm is over the threshold.
   *
   * @return The alarm value.
   */
  public float getBarRiseAlarm()
  {
    return (float)(barRiseAlarm * 0.001);
  }

  public boolean isBarRiseAlarmSet()
  {
    return !(barRiseAlarm == 0);
  }

  /**
   * Get the pressure fall alarm value.  A non-zero value represents the inches of mercury the alarm is over the threshold.
   *
   * @return The alarm value.
   */
  public float getBarFallAlarm()
  {
    return (float)(barFallAlarm * 0.001);
  }

  public boolean isBarFallAlarmSet()
  {
    return !(barFallAlarm == 0);
  }

  /**
   * Get the time alarm value.  This is a 2 byte format of HOURS * 100 + MINUTES.
   *
   * @return The time value.
   */
  public short getTimeAlarm()
  {
    return timeAlarm;
  }

  public boolean isTimeAlarmSet()
  {
    return !(timeAlarm == (short)0xFFFF);
  }

  public short getTimeCompAlarm()
  {
    return timeCompAlarm;
  }

  public boolean isTimeCompAlarmSet()
  {
    return !(timeCompAlarm == (short)0xFFFF);
  }

  /**
   * Get the low Inside Temperature alarm value.  1 degree resolution.
   *
   * @return The low inside temperature value.
   */
  public int getLowInTempAlarm()
  {
    return (lowInTempAlarm & 0xFF) - 90;
  }

  public boolean isLowInTempAlarmSet()
  {
    return !(lowInTempAlarm == (byte)0xFF);
  }

  public int getHighInTempAlarm()
  {
    return (highInTempAlarm & 0xFF) - 90;
  }

  public boolean isHighInTempAlarmSet()
  {
    return !(highInTempAlarm == (byte)0xFF);
  }

  public int getLowOutTempAlarm()
  {
    return (lowOutTempAlarm & 0xFF) - 90;
  }

  public boolean isLowOutTempAlarmSet()
  {
    return !(lowOutTempAlarm == (byte)0xFF);
  }

  public int getHighOutTempAlarm()
  {
    return (highOutTempAlarm & 0xFF) - 90;
  }

  public boolean isHighOutTempAlarmSet()
  {
    return !(highOutTempAlarm == (byte)0xFF);
  }

  public int getLowSoil1TempAlarm()
  {
    return (lowSoil1TempAlarm & 0xFF) - 90;
  }

  public boolean isLowSoil1TempAlarmSet()
  {
    return !(lowSoil1TempAlarm == (byte)0xFF);
  }

  public int getHighSoil1TempAlarm()
  {
    return (highSoil1TempAlarm & 0xFF) - 90;
  }

  public boolean isHighSoil1TempAlarmSet()
  {
    return !(highSoil1TempAlarm == (byte)0xFF);
  }

  /**
   * Get the low inside humidity alarm value. 1% resolution.
   *
   * @return The low inside humidity value.
   */
  public int getLowInHumidAlarm()
  {
    return lowInHumidAlarm;
  }

  public boolean isLowInHumidAlarmSet()
  {
    return !(lowInHumidAlarm == (byte)0xFF);
  }

  public int getHighInHumidAlarm()
  {
    return highInHumidAlarm;
  }

  public boolean isHighInHumidAlarmSet()
  {
    return !(highInHumidAlarm == (byte)0xFF);
  }

  public byte getLowOutHumidAlarm()
  {
    return lowOutHumidAlarm;
  }

  public boolean isLowOutHumidAlarmSet()
  {
    return !(lowOutHumidAlarm == (byte)0xFF);
  }

  public byte getHighOutHumidAlarm()
  {
    return highOutHumidAlarm;
  }

  public boolean isHighOutHumidAlarmSet()
  {
    return !(highOutHumidAlarm == (byte)0xFF);
  }

  /**
   * Get the low dew point alarm value.  1 degree resolution.
   *
   * @return The low dew point temperature alarm.
   */
  public int getLowDewAlarm()
  {
    return (lowDewAlarm & 0xFF) - 120;
  }

  public boolean isLowDewAlarmSet()
  {
    return !(lowDewAlarm == (byte)0xFF);
  }

  public int getHighDewAlarm()
  {
    return (highDewAlarm & 0xFF) - 120;
  }

  public boolean isHighDewAlarmSet()
  {
    return !(highDewAlarm == (byte)0xFF);
  }

  public int getWindChillAlarm()
  {
    return (windChillAlarm & 0xFF) - 120;
  }

  public boolean isWindChillAlarmSet()
  {
    return !(windChillAlarm == (byte)0xFF);
  }

  public int getHeatIndexAlarm()
  {
    return (heatIndexAlarm & 0xFF) - 90;
  }

  public boolean isHeatIndexAlarmSet()
  {
    return !(heatIndexAlarm == (byte)0xFF);
  }

  public int getThswAlarm()
  {
    return (thswAlarm & 0xFF) - 90;
  }

  public boolean isThswAlarmSet()
  {
    return !(thswAlarm == (byte)0xFF);
  }

  /**
   * Get the speed alarm value.  1 mph resolution.
   *
   * @return The speed alarm value.
   */
  public int getWindSpeedAlarm()
  {
    return windSpeedAlarm;
  }

  public boolean isWindSpeedAlarmSet()
  {
    return !(windSpeedAlarm == (byte)0xFF);
  }

  public int getWindSpeed10MinAlarm()
  {
    return windSpeed10MinAlarm;
  }

  public boolean isWindSpeed10MinAlarmSet()
  {
    return !(windSpeed10MinAlarm == (byte)0xFF);
  }

  /**
   * Get the solar alarm value.  1 w/m2 resolution.
   *
   * @return The solar alarm value.  Valid values: 0 to 1800.
   */
  public int getSolarAlarm()
  {
    return solarAlarm;
  }

  public boolean isSolarAlarmSet()
  {
    return !(solarAlarm == (short)0x7FFF);
  }

  /**
   * Get the rain rate alarm value.  0.01 resolution (rain clicks).
   *
   * @return The rain rate alarm value.
   */
  public int getRainRateAlarm()
  {
    return (int)(rainRateAlarm / HUNDREDTHS);
  }

  public boolean isRainRateAlarmSet()
  {
    return !(rainRateAlarm == (short)0xFFFF);
  }

  public int getRain15MinAlarm()
  {
    return (int)(rain15MinAlarm / HUNDREDTHS);
  }

  public boolean isRain15MinAlarmSet()
  {
    return !(rain15MinAlarm == (short)0xFFFF);
  }

  public int getRain24HrAlarm()
  {
    return (int)(rain24HrAlarm / HUNDREDTHS);
  }

  public boolean isRain24HrAlarmSet()
  {
    return !(rain24HrAlarm == (short)0xFFFF);
  }

  public int getRainStormAlarm()
  {
    return (int)(rainStormAlarm / HUNDREDTHS);
  }

  public boolean isRainStormAlarmSet()
  {
    return !(rainStormAlarm == (short)0xFFFF);
  }
}
