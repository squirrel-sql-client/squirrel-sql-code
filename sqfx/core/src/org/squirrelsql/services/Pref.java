package org.squirrelsql.services;

import org.squirrelsql.AppState;

import java.io.File;

public class Pref
{
   private Class _clazz;

   public Pref(Class clazz)
   {
      _clazz = clazz;
   }

   public void set(String key, double val)
   {
      AppState.get().getPrefImpl().set(genKey(key), val);
   }

   public void set(String key, int val)
   {
      AppState.get().getPrefImpl().set(genKey(key), val);
   }

   public double getDouble(String key, double def)
   {
      return AppState.get().getPrefImpl().getDouble(genKey(key), def);
   }

   private String genKey(String key)
   {
      return _clazz.getPackage().getName() + "." + key;
   }

   public String getString(String key, String def)
   {
      return AppState.get().getPrefImpl().getString(genKey(key), def);
   }

   public void set(String key, String val)
   {
      AppState.get().getPrefImpl().set(genKey(key), val);
   }

   public boolean getBoolean(String key, boolean def)
   {
      return AppState.get().getPrefImpl().getBoolean(genKey(key), def);
   }

   public void set(String key, boolean val)
   {
      AppState.get().getPrefImpl().set(genKey(key), val);
   }

   public int getInt(String key, int def)
   {
      return AppState.get().getPrefImpl().getInt(genKey(key), def);
   }
}
