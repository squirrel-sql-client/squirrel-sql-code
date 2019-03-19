package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.action.file.FileSaveAction;
import net.sourceforge.squirrel_sql.client.util.PrintUtilities;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
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


   public FileHandler(IFileEditorAPI fileEditorAPI, TitleFilePathHandler titleFileHandler)
   {
      _fileManagementCore = new FileManagementCore(fileEditorAPI, titleFileHandler);
      _fileEditorAPI = fileEditorAPI;
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
      }
   }

   public void fileClose()
   {
      _closeFile(true);
   }

   public void fileReload()
   {
      if(null == _fileManagementCore.getFile())
      {
         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SQLPanelAPI.nofileToRelaod"));
         return;
      }

      File file = _fileManagementCore.getFile();

      int caretPosition = _fileEditorAPI.getCaretPosition();

      if(false == _closeFile(true))
      {
         return;
      }

      fileOpen(file);

      _fileEditorAPI.setCaretPosition(Math.min(_fileEditorAPI.getText().length(), caretPosition));

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

      _fileManagementCore.open(f, append);
      _fileOpened = true;
      _fileSaved = false;
      _unsavedEdits = false;
      displayUnsavedEditsInTabComponent(false);
      ActionCollection actions = Main.getApplication().getActionCollection();
      actions.enableAction(FileSaveAction.class, false);

      _fileEditorAPI.setCaretPosition(0);
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
      String msg = s_stringMgr.getString("SQLPanelAPI.unsavedchanges",filename);



      String title =  s_stringMgr.getString("SQLPanelAPI.unsavedchangestitle",": " + _fileEditorAPI.getSession().getAlias().getName());

      JFrame f = (JFrame) _fileEditorAPI.getOwningFrame();

      int option = JOptionPane.showConfirmDialog(f, msg, title, JOptionPane.YES_NO_CANCEL_OPTION);

      if (option == JOptionPane.YES_OPTION)
      {
         return fileSave();
      }
      else if(option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION)
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
       IApplication app = Main.getApplication();
      SquirrelPreferences prefs = app.getSquirrelPreferences();

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
}
