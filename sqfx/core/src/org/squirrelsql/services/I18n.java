package org.squirrelsql.services;

import org.squirrelsql.AppState;
import org.squirrelsql.I18nCache;

public class I18n
{
   private Class _clazz;

   public I18n(Class clazz)
   {
      _clazz = clazz;
   }

   public String t(String s, Object ... params)
   {
      if (null != AppState.get())
      {
         return AppState.get().getI18nCache().getLocalizedString(_clazz, s, params);
      }
      else
      {
         return I18nCache.getLocalizedStringBootstrapSave(_clazz, s, params);
      }
   }
}
