package org.squirrelsql;

import java.io.IOException;
import java.util.Properties;

public class I18n
{
   private Class _clazz;

   public I18n(Class clazz)
   {
      _clazz = clazz;
   }

   public String t(String s)
   {
      try
      {
         Properties props = new Properties();
         props.load(_clazz.getResourceAsStream("i18n.properties"));
         String ret = props.getProperty(s);

         if(null == ret)
         {
            ret = "no resource: " + s;
         }

         return ret;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
