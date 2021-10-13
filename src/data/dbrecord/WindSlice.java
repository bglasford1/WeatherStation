/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class holds the data for a given wind rose slice. A slice is a specific
            wind direction.  This class also defines the total percentage of time the
            wind was blowing in this direction and percentages of time the wind was
            blowing at a given intensity in 5 mph slices.  The percentages need to be
            multiplied by 100 before returning the actual value.

  Mods:		  10/13/21  Initial Release.
*/
package data.dbrecord;

import gui.windrose.WindSpeedLevel;

public class WindSlice
{
  private double totalSlicePercentage = 0.0;
  private double[] speedPercentages = new double[WindSpeedLevel.numOfLevels];
  private int[] speedCounts = new int[WindSpeedLevel.numOfLevels];

  /**
   * Constructor that initializes the speed percentage array to zeros.
   */
  WindSlice()
  {
    for (WindSpeedLevel nextLevel : WindSpeedLevel.values())
    {
      speedPercentages[nextLevel.value()] = 0;
    }
  }

  /**
   * Method called to zero out the data for another run.
   */
  public void zeroData()
  {
    totalSlicePercentage = 0.0;
    for (int i = 0; i < speedPercentages.length; i++)
      speedPercentages[i] = 0.0;

    for (int j = 0; j < speedCounts.length; j++)
      speedCounts[j] = 0;
  }

  /**
   * Method to get the percentage in a specified bin.
   *
   * @param binNumber The bin percentage to retrieve.
   * @return The percentage of wind in that bin.
   */
  public double getBinPercentage(int binNumber)
  {
    return speedPercentages[binNumber] * 100.0;
  }

  /**
   * Method to increment the count of a specific bin.
   *
   * @param binNumber The bin to increment.
   */
  public void incrementBinCount(int binNumber)
  {
    speedCounts[binNumber]++;
  }

  /**
   * Method called to calculate the percentages of all bins.
   *
   * @param totalCount The total number of data points binned.
   */
  public void calculatePercentages(int totalCount)
  {
    for (int i = 0; i < WindSpeedLevel.numOfLevels; i++)
    {
      if (speedCounts[i] > 0)
      {
        double binPercent = (double)speedCounts[i]/(double)totalCount;
        speedPercentages[i] = binPercent;
        totalSlicePercentage = totalSlicePercentage + binPercent;
      }
    }
  }
}
