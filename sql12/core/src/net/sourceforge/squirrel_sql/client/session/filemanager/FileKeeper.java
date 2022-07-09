package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.File;

public class FileKeeper
{
   private File _file;

   public File get()
   {
      return _file;
   }

   public void set(File file)
   {
      if (false == Utilities.equalsRespectNull(file, _file))
      {
         if (null != _file)
         {
            Main.getApplication().getFileNotifier().unwatchFile(_file);
         }

         _file = file;
         if (null != _file)
         {
            Main.getApplication().getFileNotifier().watchFile(_file);
         }
      }
   }
}
