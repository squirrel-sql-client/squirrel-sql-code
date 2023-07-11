package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionUtil;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.gui.filechooser.PreviewFileChooser;
import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.InvalidPathException;


public class FileManagementCore
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FileManagementCore.class);
   private static ILogger s_log = LoggerController.createLogger(FileManagementCore.class);


   private IFileEditorAPI _fileEditorAPI;
   private TitleFilePathHandler _titleFilePathHandler;

   private FileKeeper _toSaveTo = new FileKeeper();

   private FileChooserManager _fileChooserManager = new FileChooserManager();

   private IOUtilities ioUtil = new IOUtilitiesImpl();

   public FileManagementCore(IFileEditorAPI fileEditorAPI, TitleFilePathHandler titleFilePathHandler)
   {
      _fileEditorAPI = fileEditorAPI;

      if(null == titleFilePathHandler)
      {
         throw new IllegalStateException("titleFilePathHandler must not be null");
      }

      _titleFilePathHandler = titleFilePathHandler;
   }

   public boolean save()
   {
      return saveIntern(false).isSuccess();
   }

   public FileSaveResult saveAs()
   {
      return saveIntern(true);
   }

   /**
    * The caller is responsible to make sure toFile does not exist and can be created and written to.
    */
   public boolean saveInitiallyTo(File toFile)
   {
      return saveScript(_fileEditorAPI.getOwningFrame(), toFile, false);
   }


   public boolean open(File f, boolean appendToExisting)
   {
      _fileEditorAPI.selectWidgetOrTab();

      return loadScript(f, appendToExisting);
   }


   public boolean open(boolean appendToExisting)
   {
       boolean result = false;

      JFileChooser chooser = _fileChooserManager.initNewFileChooser();

      SquirrelPreferences prefs = Main.getApplication().getSquirrelPreferences();


      if (prefs.isFileOpenInPreviousDir())
      {
         String fileName = prefs.getFilePreviousDir();
         if (fileName != null)
         {
            chooser.setCurrentDirectory(new File(fileName));
         }
      }
      else
      {
         String dirName = prefs.getFileSpecifiedDir();
         if (dirName != null)
         {
            chooser.setCurrentDirectory(new File(dirName));
         }
      }

      Frame frame = _fileEditorAPI.getOwningFrame();

      _fileEditorAPI.selectWidgetOrTab();


      if (PreviewFileChooser.showOpenDialog(frame, chooser) == JFileChooser.APPROVE_OPTION)
      {
         File selectedFile = chooser.getSelectedFile();
         result = loadScript(selectedFile, appendToExisting);
      }

      _fileChooserManager.saveDisplayPrefs();

      return result;
   }

   private String checkFileOk(File selectedFile)
   {
      String errorMessage = null;
      if(null == selectedFile)
      {
         errorMessage = s_stringMgr.getString("FileManager.error.chosen.no.file.selected");
      }
      else if(false == selectedFile.exists())
      {
         errorMessage = s_stringMgr.getString("FileManager.error.chosen.File.does.not.exist", selectedFile.getAbsolutePath());
      }
      else if(false == selectedFile.isFile())
      {
         errorMessage = s_stringMgr.getString("FileManager.error.chosen.File.is.not.a.file", selectedFile.getAbsolutePath());
      }
      else if(false == selectedFile.canRead())
      {
         errorMessage = s_stringMgr.getString("FileManager.error.chosen.File.can.not.be.read", selectedFile.getAbsolutePath());
      }
      return errorMessage;
   }

   private boolean loadScript(File file, boolean appendToExisting)
   {
      String errorMessage = checkFileOk(file);

      if(null != errorMessage)
      {
         String titel = s_stringMgr.getString("FileManager.error.file.open.failed.title");
         JOptionPane.showMessageDialog(_fileEditorAPI.getOwningFrame(), errorMessage, titel, JOptionPane.ERROR_MESSAGE);
         Main.getApplication().getMessageHandler().showErrorMessage(errorMessage);
         return false;
      }

      SquirrelPreferences prefs = Main.getApplication().getSquirrelPreferences();


      try
      {
         StringBuffer sb = FileManagementUtil.readFile(file);

         if (false == appendToExisting)
         {
            _fileEditorAPI.setEntireSQLScript("");
         }

         _fileEditorAPI.appendSQLScript(convertPlatformEOLToLineFeed(sb.toString()), true);
         setFile(file);
         memorizeFile(file, prefs);

         return true;
      }
      catch (Exception e)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(e);
         s_log.error(e);
         return false;
      }

   }

   private void memorizeFile(File file, SquirrelPreferences prefs)
   {
      if(SavedSessionUtil.isInSavedSessionsDir(file))
      {
         // We don't memorize internally saved files.
         return;
      }

      prefs.setFilePreviousDir(file.getAbsolutePath());
      Main.getApplication().getRecentFilesManager().fileTouched(file.getAbsolutePath(), _fileEditorAPI.getSession().getAlias());
   }

   private FileSaveResult saveIntern(boolean toNewFile)
   {
      FileSaveResult result = new FileSaveResult(false);

      File toSaveToBuf = null;
      boolean wasSavedToNewFile = false;

      Frame frame = _fileEditorAPI.getOwningFrame();
      FileChooserWithMoveOptionDialog chooser = new FileChooserWithMoveOptionDialog(_fileChooserManager.initNewFileChooser(), frame);

      if (toNewFile)
      {
         toSaveToBuf = _toSaveTo.get();
         _toSaveTo.set(null);
         chooser.setSelectedFile(toSaveToBuf);
      }

      SquirrelPreferences prefs = _fileEditorAPI.getSession().getApplication().getSquirrelPreferences();

      for (; ;)
      {
         if (null == _toSaveTo.get())
         {
            if (prefs.isFileOpenInPreviousDir())
            {
               String dirName = prefs.getFilePreviousDir();
               if (dirName != null)
               {
                  chooser.setCurrentDirectory(new File(dirName));
               }
            }
            else
            {
               String dirName = prefs.getFileSpecifiedDir();
               if (dirName != null)
               {
                  chooser.setCurrentDirectory(new File(dirName));
               }
            }
         }

         _fileEditorAPI.selectWidgetOrTab();

         if (null != _toSaveTo.get())
         {
            if (saveScript(frame, _toSaveTo.get(), false))
            {
               result.setSuccess(true);
            }
            break;
         }

         if (chooser.showSaveDialog() == JFileChooser.APPROVE_OPTION)
         {
            _fileChooserManager.saveWasApproved();
            _toSaveTo.set(chooser.getSelectedFile());

            if (!_toSaveTo.get().exists() && null != _fileChooserManager.getSelectedFileEnding())
            {
               if (!_toSaveTo.get().getAbsolutePath().endsWith(_fileChooserManager.getSelectedFileEnding()))
               {
                  _toSaveTo.set(new File(_toSaveTo.get().getAbsolutePath() + _fileChooserManager.getSelectedFileEnding()));
               }
            }

            if (saveScript(frame, _toSaveTo.get(), true))
            {
               result.setSuccess(true);
               wasSavedToNewFile = true;
               result.setSavedToNewFile(toSaveToBuf, _toSaveTo.get(), chooser.isMoveFile());
               break;
            }
            else
            {
               _toSaveTo.set(null);
               result.setSuccess(false);
               break;
            }
         }
         else
         {
            break;
         }
      }
      _fileChooserManager.saveDisplayPrefs();

      if(false == wasSavedToNewFile && null != toSaveToBuf)
      {
         _toSaveTo.set(toSaveToBuf);
      }

      return result;
   }

   /**
    * Supposed to be the only place where Files are written.
    */
   private boolean saveScript(Frame frame, File file, boolean askReplace)
   {
      boolean doSave = false;

      if (file.exists() && !file.canWrite())
      {
          // i18n[FileManager.error.cannotwritefile=File {0} \ncannot be written to.]
         String msg = s_stringMgr.getString("FileManager.error.cannotwritefile", file.getAbsolutePath());
         s_log.warn(msg);
         Dialogs.showOk(frame, msg);
         return false;
      }


      try
      {
         file.toPath();
      }
      catch (InvalidPathException e)
      {
         s_log.error("Invalid file name: Call to File.toPath() raised error", e);

         String msg = s_stringMgr.getString("FileManager.error.invalid.file.name", file.getAbsolutePath(), Utilities.getExceptionStringSave(e));
         s_log.error(msg, e);
         Dialogs.showError(frame, msg);
         return false;
      }

      if (askReplace && file.exists())
      {
          // i18n[FileManager.confirm.filereplace={0} \nalready exists. Do you want to replace it?]
         String confirmMsg =  s_stringMgr.getString("FileManager.confirm.filereplace", file.getAbsolutePath());
          doSave = Dialogs.showYesNo(frame, confirmMsg);
         //i18n
         if (!doSave)
         {
            return false;
         }

         file.delete();
      }
      else
      {
         doSave = true;
      }


      SquirrelPreferences prefs = _fileEditorAPI.getSession().getApplication().getSquirrelPreferences();

      if (doSave)
      {
         memorizeFile(file, prefs);

         // NOTE: Changing this code may result in severe
         // encoding problems for languages that are not
         // close to the english character set,
         // e.g. see Bug #1304 "Saving script ruins greek words"
         Main.getApplication().getFileNotifier().beginFileWrite(file);
         FileOutputStream fos = null;
         try
         {
            fos = new FileOutputStream(file);
            fos.write(getBytesForSave());
            setFile(file);

            String msg = s_stringMgr.getString("FileManager.savedfile", file.getAbsolutePath());
            _fileEditorAPI.getSession().showMessage(msg);
         }
         catch (IOException ex)
         {
            _fileEditorAPI.getSession().showErrorMessage(ex);
         }
         finally
         {
         	ioUtil.closeOutputStream(fos);
            Main.getApplication().getFileNotifier().endFileWrite(file);
         }
      }
      return true;
   }

   /**
    * Bug: 2119937 (Windows EOL chars (CRLF) are converted to Linux EOL (LF))
    * Internally, SQuirreL prefers to represent EOL as "\n".  This is fine for Unix, but in Windows, EOL is 
    * "\r\n".  So, if the platform-specific EOL isn't "\n", this method will replace all "\n", with "\r\n".
    * Other editors on Windows will then properly display the EOL characters.
    *  
    * @return a String that represents the SQL Script being saved with adjusted (if necessary) EOL characters.
    */
   private String getEntireSQLScriptWithPlatformEolChar() {

      String result  = _fileEditorAPI.getEntireSQLScript();

      return convertLineFeedToPlatformEOL(result);
   }

   private String convertLineFeedToPlatformEOL(String result)
   {
      String platformEolStr = StringUtilities.getEolStr();
      if (result != null && !"".equals(result))
      {
         // We eagerly take care that no redundant CRs exist
         // because they hide in files and cause any kind of trouble.
         result = result.replaceAll("\\r", "");

         if (!platformEolStr.equals("\n"))
         {
            result = result.replaceAll("\\n", platformEolStr);
         }
      }
      return result;
   }

   /**
    * Without calling this method when loading a file on Windows
    * method convertLineFeedToPlatformEOL() which is called when saving a file
    * would create duplicate \r each time a file is opened and saved.
    */
   private String convertPlatformEOLToLineFeed(String s)
   {
      String platformEolStr = StringUtilities.getEolStr();

      if (null == s || "".equals(s))
      {
         return s;
      }

      if (false == platformEolStr.equals("\n"))
      {
         s = s.replaceAll(platformEolStr, "\n");
      }

      // We eagerly take care that no redundant CRs exist
      // because they hide in files and cause any kind of trouble.
      return s.replaceAll("\\r", "");
   }


   private void setFile(File file)
   {
      _toSaveTo.set(file);
      _titleFilePathHandler.setSqlFile(file);
   }

   public File getFile()
   {
      return _toSaveTo.get();
   }


   public void clearCurrentFile()
   {
      _toSaveTo.set(null);
   }

   public void clearSqlFile()
   {
      //SQLPanelSelectionHandler.displayFileInTabComponent(_fileEditorAPI, null);
      _titleFilePathHandler.setSqlFile(null);
   }

   public void displayUnsavedEditsInTabComponent(boolean unsavedEdits)
   {
      //SQLPanelSelectionHandler.displayUnsavedEditsInTabComponent(_fileEditorAPI, b);
      _titleFilePathHandler.setUnsavedEdits(unsavedEdits);
   }

   public void replaceSqlFileExtensionFilterBy(FileExtensionFilter fileExtensionFilter)
   {
      _fileChooserManager.replaceSqlFileExtensionFilterBy(fileExtensionFilter);
   }

   public byte[] getBytesForSave()
   {
      String sScript = getEntireSQLScriptWithPlatformEolChar();
      return sScript.getBytes();
   }
}
