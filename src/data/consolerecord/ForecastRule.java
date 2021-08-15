/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This enumeration defines the forecast rules by number and their
            associated text.

  Mods:		  09/01/21 Initial Release.
*/
package data.consolerecord;

public enum ForecastRule
{
  RULE_0("Mostly clear and cooler.", ForecastIcon.SUNNY),
  RULE_1("Mostly clear with little temperature change.", ForecastIcon.SUNNY),
  RULE_2("Mostly clear for 12 hours with little temperature change.", ForecastIcon.SUNNY),
  RULE_3("Mostly clear for 12 to 24 hours and cooler.", ForecastIcon.SUNNY),
  RULE_4("Partly cloudy and cooler.", ForecastIcon.PARTLY_CLOUDY),
  RULE_5("Partly cloudy with little temperature change.", ForecastIcon.PARTLY_CLOUDY),
  RULE_6("Mostly clear and warmer.", ForecastIcon.SUNNY),
  RULE_7("Increasing clouds and warmer. Precipitation possible within 24 to 48 hours.", ForecastIcon.PARTLY_CLOUDY),
  RULE_8("Increasing clouds with little temperature change. Precipitation possible within 24 hours.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_9("Increasing clouds with little temperature change. Precipitation possible within 12 hours.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_10("Increasing clouds and warmer. Precipitation possible within 24 hours.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_11("Mostly clear and warmer. Increasing winds.", ForecastIcon.SUNNY),
  RULE_12("Increasing clouds and warmer. Precipitation possible within 12 hours. Increasing winds.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_13("Increasing clouds and warmer.", ForecastIcon.PARTLY_CLOUDY),
  RULE_14("Mostly clear and warmer. Precipitation possible within 48 hours.", ForecastIcon.SUNNY),
  RULE_15("Increasing clouds with little temperature change. Precipitation possible within 24 to 48 hours.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_16("Increasing clouds with little temperature change.", ForecastIcon.PARTLY_CLOUDY),
  RULE_17("Increasing clouds and warmer. Precipitation possible within 12 to 24 hours.", ForecastIcon.PARTLY_CLOUDY),
  RULE_18("Increasing clouds and warmer. Precipitation possible within 12 to 24 hours. Windy.", ForecastIcon.PARTLY_CLOUDY),
  RULE_19("Increasing clouds and warmer. Precipitation possible within 6 to 12 hours.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_20("Increasing clouds and warmer. Precipitation possible within 6 to 12 hours. Windy.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_21("Increasing clouds and warmer. Precipitation possible within 12 hours.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_22("Increasing clouds and warmer. Precipitation likley.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_23("Clearing and cooler. Precipitation ending within 6 hours.", ForecastIcon.SUNNY),
  RULE_24("Mostly cloudy and cooler. Precipitation continuing.", ForecastIcon.CLOUDY_RAIN),
  RULE_25("Mostly cloudy and cooler. Precipitation likely.", ForecastIcon.CLOUDY_RAIN),
  RULE_26("Mostly cloudy with little temperature change. Precipitation continuing.", ForecastIcon.CLOUDY_RAIN),
  RULE_27("Mostly cloudy with little temperature change. Precipitation likely.", ForecastIcon.CLOUDY_RAIN),
  RULE_28("Increasing clouds and cooler. Precipitation possible and windy within 6 hours.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_29("Increasing clouds with little temperature change. Precipitation possible and windy within 6 hours.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_30("Mostly cloudy and cooler. Precipitation continuing. Increasing winds.", ForecastIcon.CLOUDY_RAIN),
  RULE_31("Mostly cloudy and cooler. Precipitation likely. Increasing winds.", ForecastIcon.CLOUDY_RAIN),
  RULE_32("Mostly cloudy with little temperature change. Precipitation continuing. Increasing winds.", ForecastIcon.CLOUDY_RAIN),
  RULE_33("Mostly cloudy with little temperature change. Precipitation likely. Increasing winds.", ForecastIcon.CLOUDY_RAIN),
  RULE_34("Increasing clouds and cooler. Precipitation possible within 12 to 24 hours possible wind shift to the W NW or N.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_35("Increasing clouds with little temperature change. Precipitation possible within 12 to 24 hours possible wind shift to the W NW or N.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_36("Increasing clouds and cooler. Precipitation possible within 6 hours possible wind shift to the W NW or N.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_37("Increasing clouds with little temperature change. Precipitation possible within 6 hours possible wind shift to the W NW or N.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_38("Mostly cloudy and cooler. Precipitation ending within 12 hours possible wind shift to the W NW or N.", ForecastIcon.CLOUDY_RAIN),
  RULE_39("Mostly cloudy and cooler. Possible wind shift to the W NW or N.", ForecastIcon.CLOUDY),
  RULE_40("Mostly cloudy with little temperature change. Precipitation ending within 12 hours possible wind shift to the W NW or N.", ForecastIcon.CLOUDY_RAIN),
  RULE_41("Mostly cloudy with little temperature change. Possible wind shift to the W NW or N.", ForecastIcon.CLOUDY),
  RULE_42("Mostly cloudy and cooler. Precipitation possible within 24 hours possible wind shift to the W NW or N.", ForecastIcon.CLOUDY_RAIN),
  RULE_43("Mostly cloudy with little temperature change. Precipitation possible within 24 hours possible wind shift to the W NW or N.", ForecastIcon.CLOUDY_RAIN),
  RULE_44("Clearing cooler and windy. Precipitation ending within 6 hours.", ForecastIcon.SUNNY),
  RULE_45("Clearing cooler and windy.", ForecastIcon.SUNNY),
  RULE_46("Mostly cloudy and cooler. Precipitation ending within 6 hours. Windy with possible wind shift to the W NW or N.", ForecastIcon.CLOUDY_RAIN),
  RULE_47("Mostly cloudy and cooler. Windy with possible wind shift to the W NW or N.", ForecastIcon.CLOUDY),
  RULE_48("Mostly cloudy with little temperature change. Precipitation possible within 12 hours. Windy.", ForecastIcon.CLOUDY_RAIN),
  RULE_49("Increasing clouds and cooler. Precipitation possible within 12 hours possibly heavy at times. Windy.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_50("Mostly cloudy and cooler. Precipitation ending within 6 hours. Windy.", ForecastIcon.CLOUDY_RAIN),
  RULE_51("Mostly cloudy and cooler. Precipitation possible within 12 hours. Windy.", ForecastIcon.CLOUDY_RAIN),
  RULE_52("Mostly cloudy and cooler. Precipitation ending in 12 to 24 hours.", ForecastIcon.CLOUDY_RAIN),
  RULE_53("Mostly cloudy and cooler.", ForecastIcon.CLOUDY),
  RULE_54("Mostly cloudy and cooler. Precipitation continuing possible heavy at times. Windy.", ForecastIcon.CLOUDY_RAIN),
  RULE_55("Mostly cloudy and cooler. Precipitation possible within 6 to 12 hours. Windy.", ForecastIcon.CLOUDY_RAIN),
  RULE_56("Mostly cloudy with little temperature change. Precipitation continuing possibly heavy at times. Windy.", ForecastIcon.CLOUDY_RAIN),
  RULE_57("Mostly cloudy with little temperature change. Precipitation possible within 6 to 12 hours. Windy.", ForecastIcon.CLOUDY_RAIN),
  RULE_58("Increasing clouds with little temperature change. Precipitation possible within 12 hours possibly heavy at times. Windy.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_59("Mostly cloudy and cooler. Windy.", ForecastIcon.CLOUDY),
  RULE_60("Mostly cloudy and cooler. Precipitation continuing possibly heavy at times. Windy.", ForecastIcon.CLOUDY_RAIN),
  RULE_61("Mostly cloudy and cooler. Precipitation likely possibly heavy at times. Windy.", ForecastIcon.CLOUDY_RAIN),
  RULE_62("Mostly cloudy with little temperature change. Precipitation likely possibly heavy at times. Windy.", ForecastIcon.CLOUDY_RAIN),
  RULE_63("Increasing clouds and cooler. Precipitation possible within 6 hours. Windy.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_64("Increasing clouds with little temperature change. Precipitation possible within 6 hours. Windy.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_65("Increasing clouds and cooler. Precipitation continuing. Windy with possible wind shift to the W NW or N.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_66("Mostly cloudy and cooler. Precipitation likely. Windy with possible wind shift to the W NW or N.", ForecastIcon.CLOUDY_RAIN),
  RULE_67("Mostly cloudy with little temperature change. Precipitation continuing. Windy with possible wind shift to the W NW or N.",ForecastIcon.CLOUDY_RAIN),
  RULE_68("Mostly cloudy with little temperature change. Precipitation likely. Windy with possible wind shift to the W NW or N.", ForecastIcon.CLOUDY_RAIN),
  RULE_69("Increasing clouds and cooler. Precipitation possible within 6 hours. Windy with possible wind shift to the W NW or N.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_70("Increasing clouds with little temperature change. Precipitation possible within 6 hours. Windy with possible wind shift to the W NW or N.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_71("Increasing clouds and cooler. Precipitation possible within 12 to 24 hours. Windy with possible wind shift to the W NW or N.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_72("Increasing clouds with little temperature change. Precipitation possible within 12 to 24 hours. Windy with possible wind shift to the W NW or N.", ForecastIcon.PARTLY_CLOUDY_RAIN),
  RULE_73("Mostly cloudy and cooler. Precipitation possibly heavy at times and ending within 12 hours. Windy with possible wind shift to the W NW or N.", ForecastIcon.CLOUDY_RAIN),
  RULE_74("Mostly cloudy and cooler. Precipitation possible within 6 to 12 hours possibly heavy at times. Windy with possible wind shift to the W NW or N.", ForecastIcon.CLOUDY_RAIN),
  RULE_75("Mostly cloudy with little temperature change. Precipitation ending within 12 hours. Windy with possible wind shift to the W NW or N.", ForecastIcon.CLOUDY_RAIN),
  RULE_76("Mostly cloudy with little temperature change. Precipitation possible within 6 to 12 hours possibly heavy at times. Windy with possible wind shift to the W NW or N.", ForecastIcon.CLOUDY_RAIN),
  RULE_77("Mostly cloudy and cooler. Precipitation possible within 12 hours possibly heavy at times. Windy.", ForecastIcon.CLOUDY_RAIN),
  RULE_78("FORECAST REQUIRES 3 HOURS OF RECENT DATA", ForecastIcon.UNKNOWN),
  RULE_79("Unknown forecast rule.", ForecastIcon.UNKNOWN);

  private final String text; // The text to display.
  private final ForecastIcon icon; // The icon value to display.

  /**
   * The constructor.
   * @param text The text to display.
   * @param icon The forecast icon.
   */
  ForecastRule(String text, ForecastIcon icon)
  {
    this.text = text;
    this.icon = icon;
  }

  public ForecastIcon getIcon()
  {
    return icon;
  }

  @Override
  public String toString()
  {
    return text;
  }

  // Array that maps the values to the rules.
  static final ForecastRule[] valueMap;
  static
  {
    valueMap = new ForecastRule[201];
    valueMap[0] = RULE_0; //Mostly clear and cooler.
    valueMap[1] = RULE_1; //Mostly clear with little temperature change.
    valueMap[2] = RULE_2; //Mostly clear for 12 hours with little temperature change.
    valueMap[3] = RULE_3; //Mostly clear for 12 to 24 hours and cooler.
    valueMap[4] = RULE_1; //Mostly clear with little temperature change.
    valueMap[5] = RULE_4; //Partly cloudy and cooler.
    valueMap[6] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[7] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[8] = RULE_6; //Mostly clear and warmer.
    valueMap[9] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[10] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[11] = RULE_1; //Mostly clear with little temperature change.
    valueMap[12] = RULE_7; //Increasing clouds and warmer. Precipitation possible within 24 to 48 hours.
    valueMap[13] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[14] = RULE_1; //Mostly clear with little temperature change.
    valueMap[15] = RULE_8; //Increasing clouds with little temperature change. Precipitation possible within 24 hours.
    valueMap[16] = RULE_1; //Mostly clear with little temperature change.
    valueMap[17] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[18] = RULE_1; //Mostly clear with little temperature change.
    valueMap[19] = RULE_9; //Increasing clouds with little temperature change. Precipitation possible within 12 hours.
    valueMap[20] = RULE_1; //Mostly clear with little temperature change.
    valueMap[21] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[22] = RULE_1; //Mostly clear with little temperature change.
    valueMap[23] = RULE_10; //Increasing clouds and warmer. Precipitation possible within 24 hours.
    valueMap[24] = RULE_11; //Mostly clear and warmer. Increasing winds.
    valueMap[25] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[26] = RULE_1; //Mostly clear with little temperature change.
    valueMap[27] = RULE_12; //Increasing clouds and warmer. Precipitation possible within 12 hours. Increasing winds.
    valueMap[28] = RULE_11; //Mostly clear and warmer. Increasing winds.
    valueMap[29] = RULE_13; //Increasing clouds and warmer.
    valueMap[30] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[31] = RULE_1; //Mostly clear with little temperature change.
    valueMap[32] = RULE_12; //Increasing clouds and warmer. Precipitation possible within 12 hours. Increasing winds.
    valueMap[33] = RULE_11; //Mostly clear and warmer. Increasing winds.
    valueMap[34] = RULE_13; //Increasing clouds and warmer.
    valueMap[35] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[36] = RULE_1; //Mostly clear with little temperature change.
    valueMap[37] = RULE_12; //Increasing clouds and warmer. Precipitation possible within 12 hours. Increasing winds.
    valueMap[38] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[39] = RULE_1; //Mostly clear with little temperature change.
    valueMap[40] = RULE_14; //Mostly clear and warmer. Precipitation possible within 48 hours.
    valueMap[41] = RULE_6; //Mostly clear and warmer.
    valueMap[42] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[43] = RULE_1; //Mostly clear with little temperature change.
    valueMap[44] = RULE_15; //Increasing clouds with little temperature change. Precipitation possible within 24 to 48 hours.
    valueMap[45] = RULE_16; //Increasing clouds with little temperature change.
    valueMap[46] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[47] = RULE_1; //Mostly clear with little temperature change.
    valueMap[48] = RULE_17; //Increasing clouds and warmer. Precipitation possible within 12 to 24 hours.
    valueMap[49] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[50] = RULE_1; //Mostly clear with little temperature change.
    valueMap[51] = RULE_18; //Increasing clouds and warmer. Precipitation possible within 12 to 24 hours. Windy.
    valueMap[52] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[53] = RULE_1; //Mostly clear with little temperature change.
    valueMap[54] = RULE_18; //Increasing clouds and warmer. Precipitation possible within 12 to 24 hours. Windy.
    valueMap[55] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[56] = RULE_1; //Mostly clear with little temperature change.
    valueMap[57] = RULE_19; //Increasing clouds and warmer. Precipitation possible within 6 to 12 hours.
    valueMap[58] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[59] = RULE_1; //Mostly clear with little temperature change.
    valueMap[60] = RULE_20; //Increasing clouds and warmer. Precipitation possible within 6 to 12 hours. Windy.
    valueMap[61] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[62] = RULE_1; //Mostly clear with little temperature change.
    valueMap[63] = RULE_18; //Increasing clouds and warmer. Precipitation possible within 12 to 24 hours. Windy.
    valueMap[64] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[65] = RULE_1; //Mostly clear with little temperature change.
    valueMap[66] = RULE_21; //Increasing clouds and warmer. Precipitation possible within 12 hours.
    valueMap[67] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[68] = RULE_1; //Mostly clear with little temperature change.
    valueMap[69] = RULE_22; //Increasing clouds and warmer. Precipitation likley.
    valueMap[70] = RULE_23; //Clearing and cooler. Precipitation ending within 6 hours.
    valueMap[71] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[72] = RULE_23; //Clearing and cooler. Precipitation ending within 6 hours.
    valueMap[73] = RULE_1; //Mostly clear with little temperature change.
    valueMap[74] = RULE_23; //Clearing and cooler. Precipitation ending within 6 hours.
    valueMap[75] = RULE_4; //Partly cloudy and cooler.
    valueMap[76] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[77] = RULE_0; //Mostly clear and cooler.
    valueMap[78] = RULE_23; //Clearing and cooler. Precipitation ending within 6 hours.
    valueMap[79] = RULE_1; //Mostly clear with little temperature change.
    valueMap[80] = RULE_23; //Clearing and cooler. Precipitation ending within 6 hours.
    valueMap[81] = RULE_0; //Mostly clear and cooler.
    valueMap[82] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[83] = RULE_1; //Mostly clear with little temperature change.
    valueMap[84] = RULE_8; //Increasing clouds with little temperature change. Precipitation possible within 24 hours.
    valueMap[85] = RULE_24; //Mostly cloudy and cooler. Precipitation continuing.
    valueMap[86] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[87] = RULE_1; //Mostly clear with little temperature change.
    valueMap[88] = RULE_25; //Mostly cloudy and cooler. Precipitation likely.
    valueMap[89] = RULE_26; //Mostly cloudy with little temperature change. Precipitation continuing.
    valueMap[90] = RULE_27; //Mostly cloudy with little temperature change. Precipitation likely.
    valueMap[91] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[92] = RULE_1; //Mostly clear with little temperature change.
    valueMap[93] = RULE_28; //Increasing clouds and cooler. Precipitation possible and windy within 6 hours.
    valueMap[94] = RULE_29; //Increasing clouds with little temperature change. Precipitation possible and windy within 6 hours.
    valueMap[95] = RULE_30; //Mostly cloudy and cooler. Precipitation continuing. Increasing winds.
    valueMap[96] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[97] = RULE_1; //Mostly clear with little temperature change.
    valueMap[98] = RULE_31; //Mostly cloudy and cooler. Precipitation likely. Increasing winds.
    valueMap[99] = RULE_32; //Mostly cloudy with little temperature change. Precipitation continuing. Increasing winds.
    valueMap[100] = RULE_33; //Mostly cloudy with little temperature change. Precipitation likely. Increasing winds.
    valueMap[101] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[102] = RULE_1; //Mostly clear with little temperature change.
    valueMap[103] = RULE_34; //Increasing clouds and cooler. Precipitation possible within 12 to 24 hours possible wind shift to the W NW or N.
    valueMap[104] = RULE_35; //Increasing clouds with little temperature change. Precipitation possible within 12 to 24 hours possible wind shift to the W NW or N.
    valueMap[105] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[106] = RULE_1; //Mostly clear with little temperature change.
    valueMap[107] = RULE_36; //Increasing clouds and cooler. Precipitation possible within 6 hours possible wind shift to the W NW or N.
    valueMap[108] = RULE_37; //Increasing clouds with little temperature change. Precipitation possible within 6 hours possible wind shift to the W NW or N.
    valueMap[109] = RULE_38; //Mostly cloudy and cooler. Precipitation ending within 12 hours possible wind shift to the W NW or N.
    valueMap[110] = RULE_39; //Mostly cloudy and cooler. Possible wind shift to the W NW or N.
    valueMap[111] = RULE_40; //Mostly cloudy with little temperature change. Precipitation ending within 12 hours possible wind shift to the W NW or N.
    valueMap[112] = RULE_41; //Mostly cloudy with little temperature change. Possible wind shift to the W NW or N.
    valueMap[113] = RULE_38; //Mostly cloudy and cooler. Precipitation ending within 12 hours possible wind shift to the W NW or N.
    valueMap[114] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[115] = RULE_1; //Mostly clear with little temperature change.
    valueMap[116] = RULE_42; //Mostly cloudy and cooler. Precipitation possible within 24 hours possible wind shift to the W NW or N.
    valueMap[117] = RULE_40; //Mostly cloudy with little temperature change. Precipitation ending within 12 hours possible wind shift to the W NW or N.
    valueMap[118] = RULE_43; //Mostly cloudy with little temperature change. Precipitation possible within 24 hours possible wind shift to the W NW or N.
    valueMap[119] = RULE_44; //Clearing cooler and windy. Precipitation ending within 6 hours.
    valueMap[120] = RULE_45; //Clearing cooler and windy.
    valueMap[121] = RULE_46; //Mostly cloudy and cooler. Precipitation ending within 6 hours. Windy with possible wind shift to the W NW or N.
    valueMap[122] = RULE_47; //Mostly cloudy and cooler. Windy with possible wind shift to the W NW or N.
    valueMap[123] = RULE_45; //Clearing cooler and windy.
    valueMap[124] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[125] = RULE_1; //Mostly clear with little temperature change.
    valueMap[126] = RULE_48; //Mostly cloudy with little temperature change. Precipitation possible within 12 hours. Windy.
    valueMap[127] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[128] = RULE_1; //Mostly clear with little temperature change.
    valueMap[129] = RULE_49; //Increasing clouds and cooler. Precipitation possible within 12 hours possibly heavy at times. Windy.
    valueMap[130] = RULE_50; //Mostly cloudy and cooler. Precipitation ending within 6 hours. Windy.
    valueMap[131] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[132] = RULE_1; //Mostly clear with little temperature change.
    valueMap[133] = RULE_51; //Mostly cloudy and cooler. Precipitation possible within 12 hours. Windy.
    valueMap[134] = RULE_52; //Mostly cloudy and cooler. Precipitation ending in 12 to 24 hours.
    valueMap[135] = RULE_53; //Mostly cloudy and cooler.
    valueMap[136] = RULE_54; //Mostly cloudy and cooler. Precipitation continuing possible heavy at times. Windy.
    valueMap[137] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[138] = RULE_1; //Mostly clear with little temperature change.
    valueMap[139] = RULE_55; //Mostly cloudy and cooler. Precipitation possible within 6 to 12 hours. Windy.
    valueMap[140] = RULE_56; //Mostly cloudy with little temperature change. Precipitation continuing possibly heavy at times. Windy.
    valueMap[141] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[142] = RULE_1; //Mostly clear with little temperature change.
    valueMap[143] = RULE_57; //Mostly cloudy with little temperature change. Precipitation possible within 6 to 12 hours. Windy.
    valueMap[144] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[145] = RULE_1; //Mostly clear with little temperature change.
    valueMap[146] = RULE_58; //Increasing clouds with little temperature change. Precipitation possible within 12 hours possibly heavy at times. Windy.
    valueMap[147] = RULE_59; //Mostly cloudy and cooler. Windy.
    valueMap[148] = RULE_60; //Mostly cloudy and cooler. Precipitation continuing possibly heavy at times. Windy.
    valueMap[149] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[150] = RULE_1; //Mostly clear with little temperature change.
    valueMap[151] = RULE_61; //Mostly cloudy and cooler. Precipitation likely possibly heavy at times. Windy.
    valueMap[152] = RULE_56; //Mostly cloudy with little temperature change. Precipitation continuing possibly heavy at times. Windy.
    valueMap[153] = RULE_62; //Mostly cloudy with little temperature change. Precipitation likely possibly heavy at times. Windy.
    valueMap[154] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[155] = RULE_1; //Mostly clear with little temperature change.
    valueMap[156] = RULE_63; //Increasing clouds and cooler. Precipitation possible within 6 hours. Windy.
    valueMap[157] = RULE_64; //Increasing clouds with little temperature change. Precipitation possible within 6 hours. Windy
    valueMap[158] = RULE_65; //Increasing clouds and cooler. Precipitation continuing. Windy with possible wind shift to the W NW or N.
    valueMap[159] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[160] = RULE_1; //Mostly clear with little temperature change.
    valueMap[161] = RULE_66; //Mostly cloudy and cooler. Precipitation likely. Windy with possible wind shift to the W NW or N.
    valueMap[162] = RULE_67; //Mostly cloudy with little temperature change. Precipitation continuing. Windy with possible wind shift to the W NW or N.
    valueMap[163] = RULE_68; //Mostly cloudy with little temperature change. Precipitation likely. Windy with possible wind shift to the W NW or N.
    valueMap[164] = RULE_69; //Increasing clouds and cooler. Precipitation possible within 6 hours. Windy with possible wind shift to the W NW or N.
    valueMap[165] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[166] = RULE_1; //Mostly clear with little temperature change.
    valueMap[167] = RULE_36; //Increasing clouds and cooler. Precipitation possible within 6 hours possible wind shift to the W NW or N.
    valueMap[168] = RULE_70; //Increasing clouds with little temperature change. Precipitation possible within 6 hours. Windy with possible wind shift to the W NW or N.
    valueMap[169] = RULE_37; //Increasing clouds with little temperature change. Precipitation possible within 6 hours possible wind shift to the W NW or N.
    valueMap[170] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[171] = RULE_1; //Mostly clear with little temperature change.
    valueMap[172] = RULE_69; //Increasing clouds and cooler. Precipitation possible within 6 hours. Windy with possible wind shift to the W NW or N.
    valueMap[173] = RULE_70; //Increasing clouds with little temperature change. Precipitation possible within 6 hours. Windy with possible wind shift to the W NW or N.
    valueMap[174] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[175] = RULE_1; //Mostly clear with little temperature change.
    valueMap[176] = RULE_71; //Increasing clouds and cooler. Precipitation possible within 12 to 24 hours. Windy with possible wind shift to the W NW or N.
    valueMap[177] = RULE_72; //Increasing clouds with little temperature change. Precipitation possible within 12 to 24 hours. Windy with possible wind shift to the W NW or N.
    valueMap[178] = RULE_73; //Mostly cloudy and cooler. Precipitation possibly heavy at times and ending within 12 hours. Windy with possible wind shift to the W NW or N.
    valueMap[179] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[180] = RULE_1; //Mostly clear with little temperature change.
    valueMap[181] = RULE_74; //Mostly cloudy and cooler. Precipitation possible within 6 to 12 hours possibly heavy at times. Windy with possible wind shift to the W NW or N.
    valueMap[182] = RULE_75; //Mostly cloudy with little temperature change. Precipitation ending within 12 hours. Windy with possible wind shift to the W NW or N.
    valueMap[183] = RULE_76; //Mostly cloudy with little temperature change. Precipitation possible within 6 to 12 hours possibly heavy at times. Windy with possible wind shift to the W NW or N.
    valueMap[184] = RULE_24; //Mostly cloudy and cooler. Precipitation continuing.
    valueMap[185] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[186] = RULE_1; //Mostly clear with little temperature change.
    valueMap[187] = RULE_66; //Mostly cloudy and cooler. Precipitation likely. Windy with possible wind shift to the W NW or N.
    valueMap[188] = RULE_26; //Mostly cloudy with little temperature change. Precipitation continuing.
    valueMap[189] = RULE_27; //Mostly cloudy with little temperature change. Precipitation likely.
    valueMap[190] = RULE_5; //Partly cloudy with little temperature change.
    valueMap[191] = RULE_1; //Mostly clear with little temperature change.
    valueMap[192] = RULE_77; //Mostly cloudy and cooler. Precipitation possible within 12 hours possibly heavy at times. Windy.
    valueMap[193] = RULE_78; //FORECAST REQUIRES 3 HOURS OF RECENT DATA
    valueMap[194] = RULE_0; //Mostly clear and cooler.
    valueMap[195] = RULE_0; //Mostly clear and cooler.
    valueMap[196] = RULE_0; //Mostly clear and cooler.
    valueMap[200] = RULE_79; //Unknown forecast rule.
  }

  /**
   * Get a forecast rule based on the rule number.
   *
   * @param value The rule number.
   * @return The associated forecast rule.
   */
  public static ForecastRule getForecastRule(int value)
  {
    try
    {
      return valueMap[value];
    }
    catch (Exception e)
    {
      System.out.println("Unknown Rule: value = " + value);
      return RULE_79;
    }
  }
}
