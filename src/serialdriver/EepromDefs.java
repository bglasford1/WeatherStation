/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class contains the console configuration.  These values are
            set by reading the console configuration values from the console
            upon startup.  Any changes to the configuration via this program
            must go through this configuration class.  This class is a singleton.

  Mods:		  09/01/21 Initial Release.
*/
package serialdriver;

public class EepromDefs
{
  // EEPROM Offsets
  public static final byte BAR_GAIN_OFFSET          = (byte)0x01;
  public static final byte BAR_OFFSET_OFFSET        = (byte)0x03;
  public static final byte BAR_CAL_OFFSET           = (byte)0x05;
  public static final byte HUM33_OFFSET             = (byte)0x07;
  public static final byte HUM80_OFFSET             = (byte)0x09;
  public static final byte LATITUDE_OFFSET          = (byte)0x0B;
  public static final byte LONGITUDE_OFFSET         = (byte)0x0D;
  public static final byte ELEVATION_OFFSET         = (byte)0x0F;
  public static final byte TIME_ZONE_OFFSET         = (byte)0x11;
  public static final byte MANUAL_AUTO_OFFSET       = (byte)0x12;
  public static final byte DAY_SAVINGS_OFFSET       = (byte)0x13;
  public static final byte GMT_OFFSET_OFFSET        = (byte)0x14;
  public static final byte GMT_OR_ZONE_OFFSET       = (byte)0x16;
  public static final byte USETX_OFFSET             = (byte)0x17;
  public static final byte RETRANS_TX_OFFSET        = (byte)0x18;
  public static final byte STATION_LIST_OFFSET      = (byte)0x19; // Two bytes per station, 8 stations.
  public static final byte UNIT_BITS_OFFSET         = (byte)0x29;
  public static final byte UNIT_BITS_COMP_OFFSET    = (byte)0x2A; // One's compliment of unit bits.
  public static final byte SETUP_BITS_OFFSET        = (byte)0x2B;
  public static final byte RAIN_SEASON_START_OFFSET = (byte)0x2C;
  public static final byte ARCHIVE_PERIOD_OFFSET    = (byte)0x2D;

  // ------ Calibration Values -------
  public static final byte TEMP_IN_CAL_OFFSET       = (byte)0x32;
  public static final byte TEMP_IN_COMP_OFFSET      = (byte)0x33;
  public static final byte TEMP_OUT_CAL_OFFSET      = (byte)0x34;
  public static final byte TEMP_CAL_OFFSET          = (byte)0x35;
  public static final byte HUM_IN_CAL_OFFSET        = (byte)0x44;
  public static final byte HUM_CAL_OFFSET           = (byte)0x45;
  public static final byte DIR_CAL_OFFSET           = (byte)0x4D;
  public static final byte DEF_BAR_GRAPH_OFFSET     = (byte)0x4F;
  public static final byte DEF_RAIN_GRAPH_OFFSET    = (byte)0x50;
  public static final byte DEF_SPEED_GRAPH_OFFSET   = (byte)0x51;

  // ----- Alarm Offsets -----
  public static final byte BAR_RISE_ALARM_OFFSET         = (byte)0x52;
  public static final byte BAR_FALL_ALARM_OFFSET         = (byte)0x53;
  public static final byte TIME_ALARM_OFFSET             = (byte)0x54;
  public static final byte TIME_COMP_ALARM_OFFSET        = (byte)0x56;
  public static final byte LOW_TEMP_IN_ALARM_OFFSET      = (byte)0x58;
  public static final byte HIGH_TEMP_IN_ALARM_OFFSET     = (byte)0x59;
  public static final byte LOW_TEMP_OUT_ALARM_OFFSET     = (byte)0x5A;
  public static final byte HIGH_TEMP_OUT_ALARM_OFFSET    = (byte)0x5B;
  public static final byte LOW_TEMP_EXTRA1_ALARM_OFFSET  = (byte)0x5C;
  public static final byte LOW_TEMP_EXTRA2_ALARM_OFFSET  = (byte)0x5D;
  public static final byte LOW_TEMP_EXTRA3_ALARM_OFFSET  = (byte)0x5E;
  public static final byte LOW_TEMP_EXTRA4_ALARM_OFFSET  = (byte)0x5F;
  public static final byte LOW_TEMP_EXTRA5_ALARM_OFFSET  = (byte)0x60;
  public static final byte LOW_TEMP_EXTRA6_ALARM_OFFSET  = (byte)0x61;
  public static final byte LOW_TEMP_EXTRA7_ALARM_OFFSET  = (byte)0x62;
  public static final byte LOW_TEMP_SOIL1_ALARM_OFFSET   = (byte)0x63;
  public static final byte LOW_TEMP_SOIL2_ALARM_OFFSET   = (byte)0x64;
  public static final byte LOW_TEMP_SOIL3_ALARM_OFFSET   = (byte)0x65;
  public static final byte LOW_TEMP_SOIL4_ALARM_OFFSET   = (byte)0x66;
  public static final byte LOW_TEMP_LEAF1_ALARM_OFFSET   = (byte)0x67;
  public static final byte LOW_TEMP_LEAF2_ALARM_OFFSET   = (byte)0x68;
  public static final byte LOW_TEMP_LEAF3_ALARM_OFFSET   = (byte)0x69;
  public static final byte LOW_TEMP_LEAF4_ALARM_OFFSET   = (byte)0x6A;
  public static final byte HIGH_TEMP_EXTRA1_ALARM_OFFSET = (byte)0x6B;
  public static final byte HIGH_TEMP_EXTRA2_ALARM_OFFSET = (byte)0x6C;
  public static final byte HIGH_TEMP_EXTRA3_ALARM_OFFSET = (byte)0x6D;
  public static final byte HIGH_TEMP_EXTRA4_ALARM_OFFSET = (byte)0x6E;
  public static final byte HIGH_TEMP_EXTRA5_ALARM_OFFSET = (byte)0x6F;
  public static final byte HIGH_TEMP_EXTRA6_ALARM_OFFSET = (byte)0x70;
  public static final byte HIGH_TEMP_EXTRA7_ALARM_OFFSET = (byte)0x71;
  public static final byte HIGH_TEMP_SOIL1_ALARM_OFFSET  = (byte)0x72;
  public static final byte HIGH_TEMP_SOIL2_ALARM_OFFSET  = (byte)0x73;
  public static final byte HIGH_TEMP_SOIL3_ALARM_OFFSET  = (byte)0x74;
  public static final byte HIGH_TEMP_SOIL4_ALARM_OFFSET  = (byte)0x75;
  public static final byte HIGH_TEMP_LEAF1_ALARM_OFFSET  = (byte)0x76;
  public static final byte HIGH_TEMP_LEAF2_ALARM_OFFSET  = (byte)0x77;
  public static final byte HIGH_TEMP_LEAF3_ALARM_OFFSET  = (byte)0x78;
  public static final byte HIGH_TEMP_LEAF4_ALARM_OFFSET  = (byte)0x79;
  public static final byte LOW_HUM_IN_ALARM_OFFSET       = (byte)0x7A;
  public static final byte HIGH_HUM_IN_ALARM_OFFSET      = (byte)0x7B;
  public static final byte LOW_HUM_OUT_ALARM_OFFSET      = (byte)0x7C;
  public static final byte LOW_EXTRA1_HUM_ALARM_OFFSET   = (byte)0x7D;
  public static final byte LOW_EXTRA2_HUM_ALARM_OFFSET   = (byte)0x7E;
  public static final byte LOW_EXTRA3_HUM_ALARM_OFFSET   = (byte)0x7F;
  public static final byte LOW_EXTRA4_HUM_ALARM_OFFSET   = (byte)0x80;
  public static final byte LOW_EXTRA5_HUM_ALARM_OFFSET   = (byte)0x81;
  public static final byte LOW_EXTRA6_HUM_ALARM_OFFSET   = (byte)0x82;
  public static final byte LOW_EXTRA7_HUM_ALARM_OFFSET   = (byte)0x83;
  public static final byte HIGH_HUM_OUT_ALARM_OFFSET     = (byte)0x84;
  public static final byte HIGH_EXTRA1_HUM_ALARM_OFFSET  = (byte)0x85;
  public static final byte HIGH_EXTRA2_HUM_ALARM_OFFSET  = (byte)0x86;
  public static final byte HIGH_EXTRA3_HUM_ALARM_OFFSET  = (byte)0x87;
  public static final byte HIGH_EXTRA4_HUM_ALARM_OFFSET  = (byte)0x88;
  public static final byte HIGH_EXTRA5_HUM_ALARM_OFFSET  = (byte)0x89;
  public static final byte HIGH_EXTRA6_HUM_ALARM_OFFSET  = (byte)0x8A;
  public static final byte HIGH_EXTRA7_HUM_ALARM_OFFSET  = (byte)0x8B;
  public static final byte LOW_DEW_ALARM_OFFSET          = (byte)0x8C;
  public static final byte HIGH_DEW_ALARM_OFFSET         = (byte)0x8D;
  public static final byte CHILL_ALARM_OFFSET            = (byte)0x8E;
  public static final byte HEAT_ALARM_OFFSET             = (byte)0x8F;
  public static final byte THSW_ALARM_OFFSET             = (byte)0x90;
  public static final byte SPEED_ALARM_OFFSET            = (byte)0x91;
  public static final byte SPEED_10MIN_ALARM_OFFSET      = (byte)0x92;
  public static final byte UV_ALARM_OFFSET               = (byte)0x93;
  public static final byte UV_DOSE_ALARM_OFFSET          = (byte)0x94;
  public static final byte LOW_SOIL1_MOIST_ALARM_OFFSET  = (byte)0x95;
  public static final byte LOW_SOIL2_MOIST_ALARM_OFFSET  = (byte)0x96;
  public static final byte LOW_SOIL3_MOIST_ALARM_OFFSET  = (byte)0x97;
  public static final byte LOW_SOIL4_MOIST_ALARM_OFFSET  = (byte)0x98;
  public static final byte HIGH_SOIL1_MOIST_ALARM_OFFSET = (byte)0x99;
  public static final byte HIGH_SOIL2_MOIST_ALARM_OFFSET = (byte)0x9A;
  public static final byte HIGH_SOIL3_MOIST_ALARM_OFFSET = (byte)0x9B;
  public static final byte HIGH_SOIL4_MOIST_ALARM_OFFSET = (byte)0x9C;
  public static final byte LOW_LEAF1_WET_ALARM_OFFSET    = (byte)0x9D;
  public static final byte LOW_LEAF2_WET_ALARM_OFFSET    = (byte)0x9E;
  public static final byte LOW_LEAF3_WET_ALARM_OFFSET    = (byte)0x9F;
  public static final byte LOW_LEAF4_WET_ALARM_OFFSET    = (byte)0xA0;
  public static final byte HIGH_LEAF1_WET_ALARM_OFFSET   = (byte)0xA1;
  public static final byte HIGH_LEAF2_WET_ALARM_OFFSET   = (byte)0xA2;
  public static final byte HIGH_LEAF3_WET_ALARM_OFFSET   = (byte)0xA3;
  public static final byte HIGH_LEAF4_WET_ALARM_OFFSET   = (byte)0xA4;
  public static final byte SOLAR_ALARM_OFFSET            = (byte)0xA5;
  public static final byte RAIN_RATE_ALARM_OFFSET        = (byte)0xA7;
  public static final byte RAIN_15MIN_ALARM_OFFSET       = (byte)0xA9;
  public static final byte RAIN_24HR_ALARM_OFFSET        = (byte)0xAB;
  public static final byte RAIN_STORM_ALARM_OFFSET       = (byte)0xAD;
  public static final byte ET_DAY_ALARM_OFFSET           = (byte)0xAF;

  public static final byte WIND_CUP_SIZE_OFFSET      = (byte)0xC3;
  public static final short GRAPH_OFFSET             = 0x145; // SEE graph config class for details of this section.
  public static final short LOG_AVG_TEMP_OFFSET      = 0xFFC;
  public static final short PASSWORD_CRC_OFFSET      = 0xFFE;

  // EEPROM Sizes.
  public static final int BAR_GAIN_SIZE          = 2;
  public static final int BAR_OFFSET_SIZE        = 2;
  public static final int BAR_CAL_SIZE           = 2;
  public static final int HUM33_SIZE             = 2;
  public static final int HUM80_SIZE             = 2;
  public static final int LATITUDE_SIZE          = 2;
  public static final int LONGITUDE_SIZE         = 2;
  public static final int ELEVATION_SIZE         = 2;
  public static final int TIME_ZONE_SIZE         = 1;
  public static final int MANUAL_AUTO_SIZE       = 1;
  public static final int DAY_SAVINGS_SIZE       = 1;
  public static final int GMT_OFFSET_SIZE        = 2;
  public static final int GMT_OR_ZONE_SIZE       = 1;
  public static final int USETX_SIZE             = 1;
  public static final int RETRANS_TX_SIZE        = 1;
  public static final int STATION_LIST_SIZE      = 16;
  public static final int UNIT_BITS_SIZE         = 1;
  public static final int UNIT_BITS_COMP_SIZE    = 1;
  public static final int SETUP_BITS_SIZE        = 1;
  public static final int RAIN_SEASON_START_SIZE = 1;
  public static final int ARCHIVE_PERIOD_SIZE    = 1;
  public static final int TEMP_IN_CAL_SIZE       = 1;
  public static final int TEMP_IN_COMP_SIZE      = 1;
  public static final int TEMP_OUT_CAL_SIZE      = 1;
  public static final int TEMP_CAL_SIZE          = 15;
  public static final int HUM_IN_CAL_SIZE        = 1;
  public static final int HUM_CAL_SIZE           = 8;
  public static final int DIR_CAL_SIZE           = 2;
  public static final int DEF_BAR_GRAPH_SIZE     = 1;
  public static final int DEF_RAIN_GRAPH_SIZE    = 1;
  public static final int DEF_SPEED_GRAPH_SIZE   = 1;
  public static final int ALARM_START_SIZE       = 94;   // See alarm config class for details of this section.
  public static final int GRAPH_SIZE             = 3874; // SEE graph config class for details of this section.
  public static final int WIND_CUP_SIZE_SIZE     = 1;
  public static final int LOG_AVG_TEMP_SIZE      = 1;
  public static final int PASSWORD_CRC_SIZE      = 2;
}
