/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class contains various byte utilities as Java does not handle
            bytes well.  This is because in Java a byte is a signed 32 bit
            integer.

  Mods:		  09/01/21 Initial Release.
*/
package util;

public class ByteUtil
{
  public static String bytesToHex(byte[] bytes)
  {
    StringBuilder builder = new StringBuilder();

    for (byte aByte : bytes)
    {
      builder.append(Integer.toHexString(aByte & 0xFF));
      builder.append(" ");
    }
    return builder.toString();
  }

  public static String byteToHex(byte value)
  {
    return Integer.toHexString(value & 0xFF);
  }

  public static int byteArrayToInt(byte[] b)
  {
    return b[3] & 0xFF |
      (b[2] & 0xFF) << 8 |
      (b[1] & 0xFF) << 16 |
      (b[0] & 0xFF) << 24;
  }

  public static int intArrayToInt(int[] b)
  {
    return b[3] << 24 | b[2] << 16 | b[1] << 8 | b[0];
  }

  public static byte[] intToByteArray(int a)
  {
    return new byte[]
      {
        (byte) ((a >> 24) & 0xFF),
        (byte) ((a >> 16) & 0xFF),
        (byte) ((a >> 8) & 0xFF),
        (byte) (a & 0xFF)
      };
  }

  public static short byteArrayToShort(byte[] b)
  {
    return (short)(b[1] & 0xFF | (b[0] & 0xFF) << 8);
  }

  public static byte[] shortToByteArray(int a)
  {
    return new byte[]
      {
        (byte) ((a >> 8) & 0xFF),
        (byte) (a & 0xFF)
      };
  }

  // -------------------- Storm Date converter methods -----------------------
  // bits 0-6 = year offset by 2000
  // bits 7-11 = day
  // bits 12-15 = month
  //
  // | ------------- High Byte ----------- | -------- Low Byte ----------- |
  // | ------ Month ---- | -------- Day ------ | ----------- Year -------- |
  // | 15 | 14 | 13 | 12 | 11 | 10 | 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
  // -------------------------------------------------------------------------

  /**
   * Method to convert a date (year/month/day) into a storm date.  This is used by the simulator.
   *
   * @param year The year value.
   * @param month The month value.
   * @param day The day value.
   * @return The two byte storm date value.
   */
  public static byte[] convertDateToStormDate(int year, int month, int day)
  {
    int byteYear = year - 2000;
    byte lowByte = (byte)((byteYear & 0x7F) | ((day & 0x01) << 7));
    byte highByte = (byte)(((month & 0x0F) << 4) | ((day & 0x1F) >> 1));
    return new byte[] {lowByte, highByte};
  }

  public static int getStormDateDay(byte[] rawBytes)
  {
    return ((rawBytes[0] & 0x80) >> 7) | ((rawBytes[1] & 0x0F) << 1);
  }

  public static int getStormDateMonth(byte highByte)
  {
    return (highByte >> 4) & 0x0F;
  }

  public static int getStormDateYear(short lowByte)
  {
    return (lowByte & 0x7F) + 2000;
  }

  public static void main(String[] args)
  {
    byte[] stormDate = ByteUtil.convertDateToStormDate(2019, 2, 5);
    System.out.println("Converted Date: " + ByteUtil.bytesToHex(stormDate));
    System.out.println("Month: " + ByteUtil.getStormDateMonth(stormDate[1]));
    System.out.println("Year: " + ByteUtil.getStormDateYear(stormDate[0]));
    System.out.println("Day: " + ByteUtil.getStormDateDay(stormDate));
  }
}
