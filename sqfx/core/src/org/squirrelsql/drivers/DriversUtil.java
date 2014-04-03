package org.squirrelsql.drivers;

import com.google.common.base.Strings;
import javafx.collections.ObservableList;
import org.squirrelsql.services.sqlwrap.SQLDriverClassLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DriversUtil
{
   public static boolean checkDriverLoading(List<String> libFileNames, String driverClassName)
   {
      try
      {
         createDriverClassLoader(libFileNames).loadClass(driverClassName);
         return true;
      }
      catch (ClassNotFoundException e)
      {
         return false;
      }
   }

   public static SQLDriverClassLoader createDriverClassLoader(List<String> fileNames)
   {
      try
      {
         ArrayList<URL> urls = new ArrayList<>();

         for (String fileName : fileNames)
         {
            urls.add(new File(fileName).toURI().toURL());
         }


         return new SQLDriverClassLoader(urls);
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static boolean checkDriverLoading(SQLDriver sqlDriver)
   {
      if(0 == sqlDriver.getJarFileNamesList().size() || Strings.isNullOrEmpty(sqlDriver.getDriverClassName()))
      {
         return false;
      }

      return checkDriverLoading(sqlDriver.getJarFileNamesList(), sqlDriver.getDriverClassName());
   }

   public static SQLDriver findDriver(String driverId)
   {
      return findDriver(driverId, new DriversManager().getDrivers(false));
   }

   public static SQLDriver findDriver(String driverId, ObservableList<SQLDriver> drivers)
   {
      for (SQLDriver driver : drivers)
      {
         if(driver.getId().equals(driverId))
         {
            return driver;
         }
      }

      return null;
   }

   public static String getJarFileNamesListString(SQLDriver driver)
   {
      String ret = null;

      for (String fileName : driver.getJarFileNamesList())
      {

         if(null == ret)
         {
            ret = fileName;
         }
         else
         {
            ret += File.pathSeparator + fileName;
         }
      }

      return ret;
   }
}
