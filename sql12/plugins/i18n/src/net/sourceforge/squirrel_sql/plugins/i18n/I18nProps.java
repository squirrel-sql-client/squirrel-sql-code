package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.*;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class I18nProps extends Object
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(I18nProps.class);


   private File _file;
   private File _zipFile;
   private String _entryName;

   public I18nProps(File file)
   {
      _file = file;
   }

   public I18nProps(File zipFile, String entryName)
   {
      _zipFile = zipFile;
      _entryName = entryName;
   }

   public String getPath()
   {
      if(null != _file)
      {
         return _file.toString();
      }
      else
      {
         return _zipFile.toString() + File.separator + _entryName;
      }
   }


   public String getName()
   {
      String nameCutOff = File.separator + "squirrel_sql" + File.separator;
      int cutOffPos = getPath().lastIndexOf(nameCutOff);
      if(-1 == cutOffPos)
      {
         return getPath();
      }
      else
      {
         return getPath().substring(cutOffPos);
      }
   }


   Properties getProperties()
   {
      try
      {
         Properties ret = new Properties();
         InputStream is = getInputStream();
         ret.load(is);
         is.close();
         return ret;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private InputStream getInputStream()
   {
      try
      {
         if(null != _file)
         {
            return new FileInputStream(_file);
         }
         else
         {
            ZipFile zf = new ZipFile(_file);

            ZipEntry entry = zf.getEntry(_entryName);

            return zf.getInputStream(entry);
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }


   public void removeProps(Properties toRemoveFrom)
   {
      Properties toRemove = getProperties();

      for(Enumeration e=toRemove.keys(); e.hasMoreElements(); )
      {
         toRemoveFrom.remove(e.nextElement());
      }
   }

   public void copyTo(File toCopyTo)
   {
      try
      {
         InputStream is = getInputStream();

         FileOutputStream fos = new FileOutputStream(toCopyTo);

         int buf = is.read();
         while(-1 != buf)
         {
            fos.write(buf);
            buf = is.read();

         }

         fos.flush();
         fos.close();
         is.close();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public String getLocalizedFileName(Locale loc)
   {
      String name = getPath().substring(getPath().lastIndexOf(File.separator));
      return name.substring(name.lastIndexOf(".properties")) + "_" + loc + ".properties";
   }

   public String getUnlocalizedPath(Locale loc)
   {
      String path = getPath().substring(0, getPath().lastIndexOf(File.separator));
      String name = getPath().substring(getPath().lastIndexOf(File.separator));

      return path + name.replaceAll("_"+ loc.toString(), "");
   }

   public static Locale parseLocaleFromPropsFileName(String propsFileName)
   {
      if(false == propsFileName.endsWith(".properties"))
      {
         return null;
      }

      String buf = propsFileName.substring(0, propsFileName.length() - ".properties".length());

      Locale[] availableLocales = Locale.getAvailableLocales();

      for (int i = 0; i < availableLocales.length; i++)
      {
         if(buf.endsWith("_" + availableLocales[i].toString()))
         {
            return availableLocales[i];
         }
      }

      return null;




   }
}
