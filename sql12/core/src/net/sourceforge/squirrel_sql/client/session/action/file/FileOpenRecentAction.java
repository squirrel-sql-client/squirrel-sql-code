package net.sourceforge.squirrel_sql.client.session.action.file;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.IFileEditAction;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.recentfiles.RecentFilesController;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileHandler;

import java.awt.event.ActionEvent;
import java.io.File;


public class FileOpenRecentAction extends SquirrelAction  implements IFileEditAction
{
   private FileHandler _fileHandler;

   public FileOpenRecentAction(IApplication app)
   {
      super(app);
   }

   public void actionPerformed(ActionEvent e)
   {
      if(null == _fileHandler)
      {
         return;
      }

      RecentFilesController recentFilesController = new RecentFilesController(_fileHandler);
      File fileToOpen = recentFilesController.getFileToOpen();

      if (null != fileToOpen)
      {
         _fileHandler.fileOpen(fileToOpen, recentFilesController.isAppend());
      }
   }

   @Override
   public void setFileHandler(FileHandler fileHandler)
   {
      _fileHandler = fileHandler;
      setEnabled(null != _fileHandler);


   }
}
