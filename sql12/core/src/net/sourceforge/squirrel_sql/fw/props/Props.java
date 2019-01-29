package net.sourceforge.squirrel_sql.fw.props;

import net.sourceforge.squirrel_sql.client.Main;

public class Props
{
   public static void putInt(String propKey, int intValue)
   {
      Main.getApplication().getPropsImpl().put(propKey, intValue);
   }


   public static int getInt(String propKey, int defaultIntValue)
   {
      return Main.getApplication().getPropsImpl().getInt(propKey, defaultIntValue);
   }

   public static boolean getBoolean(String propKey, boolean defaultBooleanValue)
   {
      return Main.getApplication().getPropsImpl().getBoolean(propKey, defaultBooleanValue);
   }

   public static void putBoolean(String propKey, boolean booleanValue)
   {
      Main.getApplication().getPropsImpl().put(propKey, booleanValue);
   }

   public static String getString(String propKey, String defaultString)
   {
      return getString(propKey, defaultString, false);
   }

   public static String getString(String propKey, String defaultString, boolean allowWhiteSpacesOnly)
   {
      return Main.getApplication().getPropsImpl().getString(propKey, defaultString, allowWhiteSpacesOnly);
   }

   public static void putString(String propKey, String stringValue)
   {
      Main.getApplication().getPropsImpl().put(propKey, stringValue);
   }
}
