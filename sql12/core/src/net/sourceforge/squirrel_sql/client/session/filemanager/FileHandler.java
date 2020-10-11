package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.action.file.FileSaveAction;
import net.sourceforge.squirrel_sql.client.util.PrintUtilities;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainDialog;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainResult;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JFrame;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.io.File;

public class FileHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FileHandler.class);

   private final FileManagementCore _fileManagementCore;
   private IFileEditorAPI _fileEditorAPI;

   private boolean _fileOpened = false;
   private boolean _fileSaved = false;
   private boolean _unsavedEdits = false;
   private FileHandlerListener _fileHandlerListener;


   public FileHandler(IFileEditorAPI fileEditorAPI, TitleFilePathHandler titleFileHandler)
   {
      _fileManagementCore = new FileManagementCore(fileEditorAPI, titleFileHandler);
      _fileEditorAPI = fileEditorAPI;

      final FileNotifierListener fileNotifierListener = file -> fileReload(new FileReloadInfo(file));
      Main.getApplication().getFileNotifier().addFileNotifierListener(fileNotifierListener);
      _fileEditorAPI.getSession().addSimpleSessionListener(() -> Main.getApplication().getFileNotifier().removeFileNotifierListener(fileNotifierListener));
   }

   public IFileEditorAPI getFileEditorAPI()
   {
      return _fileEditorAPI;
   }

   public boolean fileSave()
   {
      if (_fileManagementCore.save())
      {
         _fileSaved = true;
         _unsavedEdits = false;
         displayUnsavedEditsInTabComponent(false);
         ActionCollection actions = Main.getApplication().getActionCollection();
         actions.enableAction(FileSaveAction.class, false);
         fireFileHandlerListener();
         return true;
      }
      else
      {
         return false;
      }
   }

   public void fileAppend()
   {
      if (_fileManagementCore.open(true))
      {
         _fileOpened = true;
         _fileSaved = false;
         _unsavedEdits = false;
         displayUnsavedEditsInTabComponent(_unsavedEdits);
         ActionCollection actions = Main.getApplication().getActionCollection();
         actions.enableAction(FileSaveAction.class, true);
         fireFileHandlerListener();
      }
   }

   public void fileClose()
   {
      _closeFile(true);
   }

   public void fileReload(FileReloadInfo info)
   {
      File file = _fileManagementCore.getFile();

      if(info.isByUserRequest())
      {
         if (null == file)
         {
            Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SQLPanelAPI.nofileToRelaod"));
            return;
         }
      }
      else // if(info.isByFileWatcher())
      {
         if (false == Utilities.equalsRespectNull(info.getFile(), file))
         {
            return;
         }

         DontShowAgainDialog reloadReqDlg = new DontShowAgainDialog(
               _fileEditorAPI.getOwningFrame(),
               s_stringMgr.getString("FileHandler.fileChangeDetected.ReloadRequest", file.getAbsolutePath()),
               s_stringMgr.getString("FileHandler.fileChangeDetected.switchBackOnHint"));

         DontShowAgainResult reloadReqRes = reloadReqDlg.showAndGetResult("FileHandler.fileChangeDetected", 400, 200);

         if(reloadReqRes.isDontShowAgain())
         {
            Main.getApplication().getFileNotifier().setNotifyExternalFileChanges(false);
         }

         if(false == reloadReqRes.isYes())
         {
            return;
         }
      }


      int caretPosition = _fileEditorAPI.getCaretPosition();

      if(false == _closeFile(true))
      {
         return;
      }

      fileOpen(file);

      _fileEditorAPI.setCaretPosition(Math.min(_fileEditorAPI.getText().length(), caretPosition));
      fireFileHandlerListener();
   }

   public void fileOpen()
   {
      if (_unsavedEdits)
      {
         if(false == showConfirmSaveDialog())
         {
            return;
         }
      }

      if (_fileManagementCore.open(false))
      {
         _fileOpened = true;
         _fileSaved = false;
         _unsavedEdits = false;
         displayUnsavedEditsInTabComponent(_unsavedEdits);
         ActionCollection actions = Main.getApplication().getActionCollection();
         actions.enableAction(FileSaveAction.class, false);
      }

      _fileEditorAPI.setCaretPosition(0);
      fireFileHandlerListener();
   }

   public void fileOpen(File f)
   {
      fileOpen(f, false);
   }

   public void fileOpen(File f, boolean append)
   {
      if (_unsavedEdits)
      {
         showConfirmSaveDialog();
      }

      if(false == _fileManagementCore.open(f, append))
      {
         return;
      }

      _fileOpened = true;
      _fileSaved = false;
      _unsavedEdits = false;
      displayUnsavedEditsInTabComponent(false);
      ActionCollection actions = Main.getApplication().getActionCollection();
      actions.enableAction(FileSaveAction.class, false);

      _fileEditorAPI.setCaretPosition(0);
      fireFileHandlerListener();

   }

   public void fileNew()
   {
      fileClose();
   }

   public void fileDetach()
   {
      _closeFile(false);
   }

   public void fileSaveAs()
   {
      if (_fileManagementCore.saveAs())
      {
         _fileSaved = true;
         _unsavedEdits = false;
         displayUnsavedEditsInTabComponent(false);
         ActionCollection actions = Main.getApplication().getActionCollection();
         actions.enableAction(FileSaveAction.class, false);
         fireFileHandlerListener();
      }
   }

   public boolean showConfirmCloseIfNecessary()
   {
      if (_unsavedEdits)
      {
         return showConfirmSaveDialog();
      }

      return true;
   }

   public void resetUnsavedEdits()
   {
      _unsavedEdits = false;
   }

   public void filePrint()
   {
      PrintUtilities.printComponent(_fileEditorAPI.getTextComponent());
   }



   private boolean _closeFile(boolean clearEditor)
   {
      if (_unsavedEdits)
      {
         if(false == showConfirmSaveDialog())
         {
            return false;
         }

      }
      if (clearEditor)
      {
         _fileEditorAPI.setEntireSQLScript("");
      }
      clearSqlFile();
      _fileOpened = false;
      _fileSaved = false;
      _unsavedEdits = false;
      ActionCollection actions = Main.getApplication().getActionCollection();
      actions.enableAction(FileSaveAction.class, true);
      _fileManagementCore.clearCurrentFile();
      fireFileHandlerListener(false, FileChangeType.FILE_CLOSED);

      return true;
   }

   private boolean showConfirmSaveDialog()
   {
      File file = _fileManagementCore.getFile();

      // i18n[SQLPanelAPI.untitledLabel=Untitled]
      String filename = s_stringMgr.getString("SQLPanelAPI.untitledLabel");

      if (file != null)
      {
         filename = file.getAbsolutePath();
      }

      String msg = s_stringMgr.getString("SQLPanelAPI.unsavedchanges", filename);

      String switchBackOnHowTo;

      final boolean isBufferEdit = (null == file);

      if(isBufferEdit)
      {
         switchBackOnHowTo = s_stringMgr.getString("SQLPanelAPI.switchBackOnHowTo.buffer");
      }
      else
      {
         switchBackOnHowTo = s_stringMgr.getString("SQLPanelAPI.switchBackOnHowTo.file");
      }

      String title =  s_stringMgr.getString("SQLPanelAPI.unsavedchangestitle",": " + _fileEditorAPI.getSession().getAlias().getName());

      JFrame owner = (JFrame) _fileEditorAPI.getOwningFrame();

      DontShowAgainDialog dontShowAgainDialog = new DontShowAgainDialog(owner, msg, switchBackOnHowTo);
      dontShowAgainDialog.setTitle(title);

      final DontShowAgainResult res = dontShowAgainDialog.showAndGetResult("FileHandler.dontShowgAgainId", 400, 180);

      if(res.isDontShowAgain())
      {
         if (isBufferEdit)
         {
            Main.getApplication().getSquirrelPreferences().setWarnForUnsavedBufferEdits(false);
         }
         else
         {
            Main.getApplication().getSquirrelPreferences().setWarnForUnsavedFileEdits(false);
         }
      }

      if (res.isYes())
      {
         return fileSave();
      }
      else if(res.isCancel())
      {
         return false;
      }

      return true;
   }

   public UndoableEditListener createEditListener()
   {
      return e -> onEditHappened(e);
   }

   public void onEditHappened(UndoableEditEvent e)
   {
      SquirrelPreferences prefs = Main.getApplication().getSquirrelPreferences();

      if (_fileOpened || _fileSaved)
      {
         if (prefs.getWarnForUnsavedFileEdits())
         {
            _unsavedEdits = true;
         }
         displayUnsavedEditsInTabComponent(true);
         ActionCollection actions = Main.getApplication().getActionCollection();
         actions.enableAction(FileSaveAction.class, true);
      }
      else if (prefs.getWarnForUnsavedBufferEdits())
      {
         _unsavedEdits = true;
      }
   }


   private void clearSqlFile()
   {
      _fileManagementCore.clearSqlFile();
   }

   private void displayUnsavedEditsInTabComponent(boolean b)
   {
      _fileManagementCore.displayUnsavedEditsInTabComponent(b);
   }

   public void replaceSqlFileExtensionFilterBy(FileExtensionFilter fileExtensionFilter, String fileEndingWithDot)
   {
      _fileManagementCore.replaceSqlFileExtensionFilterBy(fileExtensionFilter, fileEndingWithDot);
   }

   /**
    * This setter immediately fires the listener when a file is open.
    */
   public void setFileHandlerListener(FileHandlerListener fileHandlerListener)
   {
      _fileHandlerListener = fileHandlerListener;

      fireFileHandlerListener(true, FileChangeType.FILE_CHANGED);
   }

   private void fireFileHandlerListener()
   {
      fireFileHandlerListener(false, FileChangeType.FILE_CHANGED);
   }

   private void fireFileHandlerListener(boolean reReadFile, FileChangeType fileChangeType)
   {
      if(null == _fileHandlerListener)
      {
         return;
      }

      if(fileChangeType == FileChangeType.FILE_CLOSED)
      {
         _fileHandlerListener.fileChanged(null);
         return;
      }
      else if (fileChangeType == FileChangeType.FILE_CHANGED)
      {
         if(null != _fileManagementCore.getFile())
         {
            if (reReadFile)
            {
               _fileHandlerListener.fileChanged(FileManagementUtil.readFileAsString(_fileManagementCore.getFile()));
            }
            else
            {
               _fileHandlerListener.fileChanged(_fileEditorAPI.getEntireSQLScript());
            }
         }
      }
   }

   public File getFile()
   {
      return _fileManagementCore.getFile();
   }
}
