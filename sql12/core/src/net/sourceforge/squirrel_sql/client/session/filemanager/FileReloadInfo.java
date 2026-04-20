package net.sourceforge.squirrel_sql.client.session.filemanager;

import java.io.File;
import org.apache.commons.lang3.ObjectUtils;

public class FileReloadInfo
{
   public static final FileReloadInfo USER_REQUEST_RELOAD_INFO = new FileReloadInfo();

   private File _file;
   private boolean _displayReloadMessageBox;

   /**
    * For {@link #USER_REQUEST_RELOAD_INFO} only.
    */
   private FileReloadInfo()
   {
   }

   public FileReloadInfo(File file, boolean displayReloadMessageBox)
   {
      ObjectUtils.requireNonEmpty(file, "file parameter must not be empty.");

      _file = file;
      _displayReloadMessageBox = displayReloadMessageBox;
   }


   public File getFile()
   {
      return _file;
   }

   public boolean isByUserRequest()
   {
      return this == USER_REQUEST_RELOAD_INFO;
   }

   public boolean isDisplayReloadMessageBox()
   {
      return _displayReloadMessageBox;
   }
}
