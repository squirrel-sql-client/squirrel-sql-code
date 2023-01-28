package net.sourceforge.squirrel_sql.client.session.action.file;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.IFileEditAction;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileHandler;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileSaveResult;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.GitHandler;

import java.awt.event.ActionEvent;

public class FileSaveAsAction extends SquirrelAction  implements IFileEditAction
{
   private FileHandler _fileHandler;

   public FileSaveAsAction()
   {
      super(Main.getApplication());
   }

   public void actionPerformed(ActionEvent e)
   {
      FileSaveResult fileSaveResult = _fileHandler.fileSaveAs();

      if(fileSaveResult.wasSavedToNewFile() && fileSaveResult.isMoveFileRequested())
      {
         GitHandler.moveAndDeleteOld(fileSaveResult.getNewFile(), fileSaveResult.getPreviousFile());

         if(null != _fileHandler.getFileEditorAPI().getSQLPanelAPIOrNull())
         {
            _fileHandler.getFileEditorAPI().getSQLPanelAPIOrNull().getChangeTracker().reInitChangeTrackingOnFileMoved();
         }

         Main.getApplication().getRecentFilesManager().removeFromRecentFiles(fileSaveResult.getPreviousFile());
      }
   }


   @Override
   public void setFileHandler(FileHandler fileHandler)
   {
      _fileHandler = fileHandler;
      setEnabled(null != _fileHandler);
   }
}
