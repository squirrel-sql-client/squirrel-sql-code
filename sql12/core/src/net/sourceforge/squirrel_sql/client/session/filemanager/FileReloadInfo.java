package net.sourceforge.squirrel_sql.client.session.filemanager;

import java.io.File;

public class FileReloadInfo
{
   public static final FileReloadInfo USER_REQUEST_RELOAD_INFO = new FileReloadInfo();

   private File _file;

   public FileReloadInfo(File file)
   {
      _file = file;
   }

   private FileReloadInfo()
   {
      this(null);
   }

   public File getFile()
   {
      return _file;
   }

   public boolean isByUserRequest()
   {
      return null == _file;
   }

   public boolean isByFileWatcher()
   {
      return null == _file;
   }

}
