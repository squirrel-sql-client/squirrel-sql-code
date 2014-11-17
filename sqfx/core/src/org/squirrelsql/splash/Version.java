package org.squirrelsql.splash;

import org.squirrelsql.services.I18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version
{

   private static String shortVersion = null;

   private static final String COPYRIGHT = new I18n(Version.class).t("Version.copyright");

   private static final String APP_NAME = new I18n(Version.class).t("Version.appname");

   public static String getCopyrightStatement()
   {
      return COPYRIGHT;
   }

   public static String getVersion()
   {
      StringBuffer buf = new StringBuffer();
      buf.append(APP_NAME);
      buf.append(" ");
      if (!isSnapshotVersion()) {
         buf.append("Version ");
      }
      buf.append(getShortVersion());
      return buf.toString();
   }

   public static boolean isSnapshotVersion() {
      return getShortVersion().toLowerCase().startsWith("snapshot");
   }

   synchronized public static String getShortVersion()
   {
      if (shortVersion == null)
      {
         InputStream is = Version.class.getResourceAsStream("Version.properties");
         Properties props = new Properties();
         try
         {
            props.load(is);
            shortVersion = props.getProperty("squirrelsql.version");
         }
         catch (IOException e)
         {
            shortVersion = "Unknown Version";
         }
      }
      return shortVersion;
   }


}
