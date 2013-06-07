package org.squirrelsql.services;

import java.io.IOException;
import java.util.Properties;

public class I18n
{
   public static final String I18N_PROPERTIES_FILE_NAME = "i18n.properties";
   private Class _clazz;

   public I18n(Class clazz)
   {
      _clazz = clazz;
   }

   public String t(String s, Object ... params)
   {
      try
      {
         Properties props = new Properties();
         props.load(_clazz.getResourceAsStream(I18N_PROPERTIES_FILE_NAME));
         String ret = props.getProperty(s);

         if(null == ret)
         {
            ret = "Resource " + s + " missing in " + _clazz.getPackage().getName() + "." + I18N_PROPERTIES_FILE_NAME;
         }

         return String.format(ret, params);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
