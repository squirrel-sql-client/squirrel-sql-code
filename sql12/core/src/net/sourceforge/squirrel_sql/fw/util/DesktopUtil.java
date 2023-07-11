package net.sourceforge.squirrel_sql.fw.util;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.*;
import java.io.File;

public class DesktopUtil
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DesktopUtil.class);

   private static final ILogger s_log = LoggerController.createLogger(DesktopUtil.class);

   public static void openInFileManager(File file)
   {
      try
      {
         if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR))
         {
            Desktop.getDesktop().browseFileDirectory(file);
         }
         else
         {
            if (file.isDirectory())
            {
               Desktop.getDesktop().open(file);
            }
            else
            {
               Desktop.getDesktop().open(file.getParentFile());
            }
         }
      }
      catch (Exception e)
      {
         s_log.error("Failed to open file:", e);
         String msg = s_stringMgr.getString("DesktopUtil.failed.to.open.file", Utilities.getExceptionStringSave(e));
         Main.getApplication().getMessageHandler().showErrorMessage(msg);
      }
   }
}
