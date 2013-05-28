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

   public String getString(String key, String def)
   {
      return Preferences.userRoot().get(key, def);
   }

   public void set(String key, String val)
   {
      Preferences.userRoot().put(key, val);
   }
}
