package net.sourceforge.squirrel_sql.plugins.i18n;

import java.io.File;

public class I18nProps extends Object
{
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

   public String getPackage()
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
}
