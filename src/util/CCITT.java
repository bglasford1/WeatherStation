/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class performs a CCITT Cyclical Redundancy Check (CRC) on
            the data that is transferred to/from the console.

  Mods:		  09/01/21 Initial Release.
*/
package util;

public class CCITT
{
  private final short[] crc_table =
    { (short)0x0000, (short)0x1021, (short)0x2042, (short)0x3063,
      (short)0x4084, (short)0x50a5, (short)0x60c6, (short)0x70e7,  // 0x00
      (short)0x8108, (short)0x9129, (short)0xa14a, (short)0xb16b,
      (short)0xc18c, (short)0xd1ad, (short)0xe1ce, (short)0xf1ef,  // 0x08
      (short)0x1231, (short)0x0210, (short)0x3273, (short)0x2252,
      (short)0x52b5, (short)0x4294, (short)0x72f7, (short)0x62d6,  // 0x10
      (short)0x9339, (short)0x8318, (short)0xb37b, (short)0xa35a,
      (short)0xd3bd, (short)0xc39c, (short)0xf3ff, (short)0xe3de,  // 0x18
      (short)0x2462, (short)0x3443, (short)0x0420, (short)0x1401,
      (short)0x64e6, (short)0x74c7, (short)0x44a4, (short)0x5485,  // 0x20
      (short)0xa56a, (short)0xb54b, (short)0x8528, (short)0x9509,
      (short)0xe5ee, (short)0xf5cf, (short)0xc5ac, (short)0xd58d,  // 0x28
      (short)0x3653, (short)0x2672, (short)0x1611, (short)0x0630,
      (short)0x76d7, (short)0x66f6, (short)0x5695, (short)0x46b4,  // 0x30
      (short)0xb75b, (short)0xa77a, (short)0x9719, (short)0x8738,
      (short)0xf7df, (short)0xe7fe, (short)0xd79d, (short)0xc7bc,  // 0x38
      (short)0x48c4, (short)0x58e5, (short)0x6886, (short)0x78a7,
      (short)0x0840, (short)0x1861, (short)0x2802, (short)0x3823,  // 0x40
      (short)0xc9cc, (short)0xd9ed, (short)0xe98e, (short)0xf9af,
      (short)0x8948, (short)0x9969, (short)0xa90a, (short)0xb92b,  // 0x48
      (short)0x5af5, (short)0x4ad4, (short)0x7ab7, (short)0x6a96,
      (short)0x1a71, (short)0x0a50, (short)0x3a33, (short)0x2a12,  // 0x50
      (short)0xdbfd, (short)0xcbdc, (short)0xfbbf, (short)0xeb9e,
      (short)0x9b79, (short)0x8b58, (short)0xbb3b, (short)0xab1a,  // 0x58
      (short)0x6ca6, (short)0x7c87, (short)0x4ce4, (short)0x5cc5,
      (short)0x2c22, (short)0x3c03, (short)0x0c60, (short)0x1c41,  // 0x60
      (short)0xedae, (short)0xfd8f, (short)0xcdec, (short)0xddcd,
      (short)0xad2a, (short)0xbd0b, (short)0x8d68, (short)0x9d49,  // 0x68
      (short)0x7e97, (short)0x6eb6, (short)0x5ed5, (short)0x4ef4,
      (short)0x3e13, (short)0x2e32, (short)0x1e51, (short)0x0e70,  // 0x70
      (short)0xff9f, (short)0xefbe, (short)0xdfdd, (short)0xcffc,
      (short)0xbf1b, (short)0xaf3a, (short)0x9f59, (short)0x8f78,  // 0x78
      (short)0x9188, (short)0x81a9, (short)0xb1ca, (short)0xa1eb,
      (short)0xd10c, (short)0xc12d, (short)0xf14e, (short)0xe16f,  // 0x80
      (short)0x1080, (short)0x00a1, (short)0x30c2, (short)0x20e3,
      (short)0x5004, (short)0x4025, (short)0x7046, (short)0x6067,  // 0x88
      (short)0x83b9, (short)0x9398, (short)0xa3fb, (short)0xb3da,
      (short)0xc33d, (short)0xd31c, (short)0xe37f, (short)0xf35e,  // 0x90
      (short)0x02b1, (short)0x1290, (short)0x22f3, (short)0x32d2,
      (short)0x4235, (short)0x5214, (short)0x6277, (short)0x7256,  // 0x98
      (short)0xb5ea, (short)0xa5cb, (short)0x95a8, (short)0x8589,
      (short)0xf56e, (short)0xe54f, (short)0xd52c, (short)0xc50d,  // 0xA0
      (short)0x34e2, (short)0x24c3, (short)0x14a0, (short)0x0481,
      (short)0x7466, (short)0x6447, (short)0x5424, (short)0x4405,  // 0xA8
      (short)0xa7db, (short)0xb7fa, (short)0x8799, (short)0x97b8,
      (short)0xe75f, (short)0xf77e, (short)0xc71d, (short)0xd73c,  // 0xB0
      (short)0x26d3, (short)0x36f2, (short)0x0691, (short)0x16b0,
      (short)0x6657, (short)0x7676, (short)0x4615, (short)0x5634,  // 0xB8
      (short)0xd94c, (short)0xc96d, (short)0xf90e, (short)0xe92f,
      (short)0x99c8, (short)0x89e9, (short)0xb98a, (short)0xa9ab,  // 0xC0
      (short)0x5844, (short)0x4865, (short)0x7806, (short)0x6827,
      (short)0x18c0, (short)0x08e1, (short)0x3882, (short)0x28a3,  // 0xC8
      (short)0xcb7d, (short)0xdb5c, (short)0xeb3f, (short)0xfb1e,
      (short)0x8bf9, (short)0x9bd8, (short)0xabbb, (short)0xbb9a,  // 0xD0
      (short)0x4a75, (short)0x5a54, (short)0x6a37, (short)0x7a16,
      (short)0x0af1, (short)0x1ad0, (short)0x2ab3, (short)0x3a92,  // 0xD8
      (short)0xfd2e, (short)0xed0f, (short)0xdd6c, (short)0xcd4d,
      (short)0xbdaa, (short)0xad8b, (short)0x9de8, (short)0x8dc9,  // 0xE0
      (short)0x7c26, (short)0x6c07, (short)0x5c64, (short)0x4c45,
      (short)0x3ca2, (short)0x2c83, (short)0x1ce0, (short)0x0cc1,  // 0xE8
      (short)0xef1f, (short)0xff3e, (short)0xcf5d, (short)0xdf7c,
      (short)0xaf9b, (short)0xbfba, (short)0x8fd9, (short)0x9ff8,  // 0xF0
      (short)0x6e17, (short)0x7e36, (short)0x4e55, (short)0x5e74,
      (short)0x2e93, (short)0x3eb2, (short)0x0ed1, (short)0x1ef0,  // 0xF8
    };


  /**
   * Method used to verify the CRC.  It is important to note that the
   * verification is the same as the initial generation except for the
   * verification, the two byte CRC (MSB first) is added onto the end of
   * the buffer.  The result should be zero for a good CRC.
   */
  public byte[] calculateCRC(byte[] buffer, int length)
  {
    short crc = 0; // The accumulated value.

    // Loop once for each byte in the buffer.
    short i;
    for (i = 0; i < length; i++)
    {
      // Mask off the sign bits of the index.
      int index = ((crc >> 8) ^ buffer[i]) & 0x00FF;
      short lookupValue = crc_table[index];
      crc = (short)(lookupValue ^ (crc << 8));
    }
    return ByteUtil.shortToByteArray(crc & 0xFFFF);
  }

  /**
   *   Method used to calculate the two byte CRC.
   */
  public byte[] calculateCRCByteArray(byte[] buffer, int length)
  {
    byte[] returnArray = new byte[2];
    byte[] byteArray = calculateCRC(buffer, length);
    if (byteArray.length == 1)
    {
      returnArray[0] = 0;
      returnArray[1] = byteArray[0];
    }
    else
    {
      returnArray[0] = byteArray[0];
      returnArray[1] = byteArray[1];
    }
    return returnArray;
  }


  /**
   * Method used to test the class.
   */
  public static void main(String[] args)
  {
    byte[] buffer = new byte[4];
    buffer[0] = (byte)0x95;
    buffer[1] = (byte)0x26;
    buffer[2] = (byte)0xA4;
    buffer[3] = (byte)0x06;

    CCITT ccitt = new CCITT();

    // Calculate CRC value.
    byte[] crcArray = ccitt.calculateCRCByteArray(buffer, buffer.length);
    System.out.println("CRC = " + ByteUtil.bytesToHex(crcArray));

    // Add the CRC to the end of the buffer.
    byte[] newBuffer = new byte[6];
    newBuffer[0] = (byte)0x95;
    newBuffer[1] = (byte)0x26;
    newBuffer[2] = (byte)0xA4;
    newBuffer[3] = (byte)0x06;
    newBuffer[4] = crcArray[0];
    newBuffer[5] = crcArray[1];

    // Now verify CRC.
    byte[] finalCRC = ccitt.calculateCRC(newBuffer, newBuffer.length);
    if (finalCRC[0] == 0 && finalCRC[1] == 0)
      System.out.println("Buffer verified.");
    else
      System.out.println("Buffer did not verify.");
  }
}
