package org.squirrelsql.services;

import org.squirrelsql.AppState;

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

   public double getDouble(String key, double def)
   {
      return AppState.get().getPrefImpl().getDouble(genKey(key), def);
   }

   private String genKey(String key)
   {
      return _clazz.getPackage().getName() + "." + key;
   }
}
