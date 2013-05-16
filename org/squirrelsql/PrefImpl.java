package org.squirrelsql;

import java.util.prefs.Preferences;

public class PrefImpl
{
   public Double getDouble(String key, double def)
   {
      return Preferences.userRoot().getDouble(key, def);
   }

   public void set(String key, double val)
   {
      Preferences.userRoot().putDouble(key, val);
   }
}
