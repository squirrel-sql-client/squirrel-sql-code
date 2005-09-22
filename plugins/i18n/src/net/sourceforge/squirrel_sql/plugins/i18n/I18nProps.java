package net.sourceforge.squirrel_sql.plugins.i18n;

import java.io.File;

public class I18nProps extends Object
{
   private File _file;

   public I18nProps(File file)
   {
      _file = file;
   }

   public String getPackage()
   {
      return _file.toString();
   }
}
