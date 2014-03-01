package net.sourceforge.squirrel_sql.client.preferences.codereformat;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;

import java.io.File;

public class FormatSqlPrefReader
{
   public static FormatSqlPref loadPref()
   {
      try
      {
         File xmlFile = getPrefsFile();

         if (false == xmlFile.exists())
         {
            return new FormatSqlPref();
         }

         XMLBeanReader reader = new XMLBeanReader();
         reader.load(xmlFile, IApplication.class.getClassLoader());
         return (FormatSqlPref) reader.iterator().next();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public static File getPrefsFile()
   {
      File userSettingsDirectory = new ApplicationFiles().getUserSettingsDirectory();
      return new File(userSettingsDirectory, "FormatSqlPrefs.xml");
   }
}
