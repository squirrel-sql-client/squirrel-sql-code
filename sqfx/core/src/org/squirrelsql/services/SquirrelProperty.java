package org.squirrelsql.services;

import com.google.common.base.Strings;

import java.io.File;

public enum SquirrelProperty
{
   USER_DIR("userdir", new File(new File(System.getProperty("user.home")), ".squirrel-sql-fx/").getAbsolutePath());

   private final String _key;
   private final String _defaultValue;

   SquirrelProperty(String key, String defaultValue)
   {
      _key = key;
      _defaultValue = defaultValue;
   }

   public String getKey()
   {
      return _key;
   }

   public String getDefaultValue()
   {
      return _defaultValue;
   }

   public static String getDefaultsString()
   {
      String ret = "";
      for (SquirrelProperty squirrelProperty : values())
      {
         if(false == Strings.isNullOrEmpty(ret))
         {
            ret += "\n";
         }

         ret += squirrelProperty._key + "=" + squirrelProperty._defaultValue;
      }

      return ret;

   }
}
