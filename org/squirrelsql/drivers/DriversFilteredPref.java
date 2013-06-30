package org.squirrelsql.drivers;

import org.squirrelsql.services.Pref;

public class DriversFilteredPref
{
   public static final String PREF_DRIVERS_FILTERED = "drivers.filtered";

   public static boolean isFiltered()
   {
      return new Pref(DriversManager.class).getBoolean(PREF_DRIVERS_FILTERED, false);
   }

   public static void setFiltered(boolean filtered)
   {
      new Pref(DriversManager.class).set(PREF_DRIVERS_FILTERED, filtered);
   }
}
