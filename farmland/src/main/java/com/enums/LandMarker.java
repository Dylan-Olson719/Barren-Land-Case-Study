package com.enums;

/**
 * Farmable - denotes land that is farmable, but that has not yet been counted toward a plot of land
 * Farmable_Marked - denotes land that is farmable, but has been counted toward a plot of land already
 * InProgress - denotes farmable land that is in progress of checking the land around itself for other land in the Farmable enum state
 * Barren - denotes barren land that has not yet been checked to see if there is farmable land around it 
 * Barren_Marked - denotes barren land that has been checked for farmable land around it. This should only ever be applied to barren land on the border of the original rectangles read in as input
 */
public enum LandMarker{
Farmable,
Farmable_Marked, 
InQueue,
Barren,
Barren_Marked
};
