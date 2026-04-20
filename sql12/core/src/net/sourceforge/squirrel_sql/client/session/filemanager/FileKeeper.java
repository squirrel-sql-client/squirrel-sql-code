package net.sourceforge.squirrel_sql.client.session.filemanager;

import java.io.File;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class FileKeeper
{
   private File _file;

   public File get()
   {
      return _file;
   }

   public void set(File file)
   {
      if (Utilities.equalsRespectNull(file, _file))
      {
         return;
      }

      if (null != _file)
      {
         // Note: FileHandler.fileReload(...) temporarily sets the file to null.
         // This is why we do not call FileNotifier.unwatchFileCustom(...) here.
         Main.getApplication().getFileNotifier().unwatchFileDefault(_file);
      }

      _file = file;
      if (null != _file)
      {
         Main.getApplication().getFileNotifier().watchFileDefaultIfNotAlreadyWatchedCustom(_file);
      }
   }
}
