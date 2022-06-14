package net.sourceforge.squirrel_sql.fw.util;

import java.util.Locale;

public final class SystemInfo
{

   public static boolean isWindows()
   {
      return System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows");
   }

   public static boolean isMacOS()
   {
      return System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("mac");
   }

   public static boolean isLinux()
   {
      return System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("linux");
   }

   public static boolean isIsKDE()
   {
      return (System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("linux") && System.getenv("KDE_FULL_SESSION") != null);
   }
}
