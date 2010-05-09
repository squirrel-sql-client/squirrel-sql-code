package net.sourceforge.squirrel_sql.client.session;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.gui.ChooserPreviewer;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilitiesImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;


public class FileManager
{
   private ISQLPanelAPI _sqlPanelAPI;

   private File _toSaveTo = null;

   private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(FileManager.class);
   
   private JFileChooser fileChooser = null;
   
   private HashMap<FileExtensionFilter, String> fileAppenixes = 
       new HashMap<FileExtensionFilter, String>();
   
   private IOUtilities ioUtil = new IOUtilitiesImpl();
   public void setIOUtilities(IOUtilities ioutilities) {
   	this.ioUtil = ioutilities;
   }
   
   FileManager(ISQLPanelAPI sqlPanelAPI)
   {
      _sqlPanelAPI = sqlPanelAPI;
   }

   public boolean save()
   {
      return saveIntern(false);
   }

   public boolean saveAs()
   {
      return saveIntern(true);
   }
      
   public boolean open(File f) {
       boolean result = false;
       _sqlPanelAPI.getSession().selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
       result = true;
       _sqlPanelAPI.setEntireSQLScript("");
       loadScript(f);
       return result;
   }
   
   public boolean open(boolean appendToExisting)
   {
       boolean result = false;
      JFileChooser chooser = getFileChooser();
      chooser.setAccessory(new ChooserPreviewer());

      SquirrelPreferences prefs = _sqlPanelAPI.getSession().getApplication().getSquirrelPreferences();
      MainFrame frame = _sqlPanelAPI.getSession().getApplication().getMainFrame();


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
      _sqlPanelAPI.getSession().selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
      if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
      {
          result = true;
         File selectedFile = chooser.getSelectedFile();
         if (!appendToExisting) {
             _sqlPanelAPI.setEntireSQLScript("");
         }
         loadScript(selectedFile);
         
      }
      return result;
   }

   private void loadScript(File file)
   {
       SquirrelPreferences prefs = _sqlPanelAPI.getSession().getApplication().getSquirrelPreferences();
      FileInputStream fis = null;
      BufferedInputStream bis = null;
      try
      {
         StringBuffer sb = new StringBuffer();
         fis = new FileInputStream(file);
         bis = new BufferedInputStream(fis);
         byte[] bytes = new byte[2048];
         int iRead = bis.read(bytes);
         while (iRead != -1)
         {
            sb.append(new String(bytes, 0, iRead));
            iRead = bis.read(bytes);
         }
         _sqlPanelAPI.appendSQLScript(convertPlatformEOLToLineFeed(sb.toString()), true);
         setFile(file);
         prefs.setFilePreviousDir(file.getAbsolutePath());
      }
      catch (java.io.IOException io)
      {
         _sqlPanelAPI.getSession().showErrorMessage(io);
      }
      finally
      {
      	ioUtil.closeInputStream(bis);
      	ioUtil.closeInputStream(fis);
      }
   }

   public boolean saveIntern(boolean toNewFile)
   {
       boolean result = false;
      if (toNewFile)
      {
         _toSaveTo = null;
      }

      JFileChooser chooser = getFileChooser();

      SquirrelPreferences prefs = _sqlPanelAPI.getSession().getApplication().getSquirrelPreferences();
      MainFrame frame = _sqlPanelAPI.getSession().getApplication().getMainFrame();

      for (; ;)
      {
         if (null == _toSaveTo)
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

         _sqlPanelAPI.getSession().selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);

         if (null != _toSaveTo)
         {
             if (saveScript(frame, _toSaveTo, false)) {
                 result = true;
             }
            break;
         }

         if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
         {
             
            _toSaveTo = chooser.getSelectedFile();

            if (!_toSaveTo.exists() && null != fileAppenixes.get(chooser.getFileFilter()))
            {
               if (!_toSaveTo.getAbsolutePath().endsWith(fileAppenixes.get(chooser.getFileFilter())))
               {
                  _toSaveTo = new File(_toSaveTo.getAbsolutePath() + fileAppenixes.get(chooser.getFileFilter()));
               }
            }

            if (saveScript(frame, _toSaveTo, true))
            {
               result = true;
               break;
            }
            else
            {
               _toSaveTo = null;
               result = false;
               break;
            }
         }
         else
         {
            break;
         }
      }
      return result;
   }

   private boolean saveScript(JFrame frame, File file, boolean askReplace)
   {
      boolean doSave = false;
      if (askReplace && file.exists())
      {
          // i18n[FileManager.confirm.filereplace={0} \nalready exists. Do you want to replace it?]
         String confirmMsg = 
             s_stringMgr.getString("FileManager.confirm.filereplace", 
                                   file.getAbsolutePath());
          doSave =
            Dialogs.showYesNo(frame, confirmMsg);
         //i18n
         if (!doSave)
         {
            return false;
         }
         if (!file.canWrite())
         {
             // i18n[FileManager.error.cannotwritefile=File {0} \ncannot be written to.]
             String msg = 
                 s_stringMgr.getString("FileManager.error.cannotwritefile", 
                                       file.getAbsolutePath());
            Dialogs.showOk(frame, msg);
            return false;
         }
         file.delete();
      }
      else
      {
         doSave = true;
      }


      SquirrelPreferences prefs = _sqlPanelAPI.getSession().getApplication().getSquirrelPreferences();

      if (doSave)
      {
         prefs.setFilePreviousDir(file.getParent());

         FileOutputStream fos = null;
         try
         {
            fos = new FileOutputStream(file);

            String sScript = getEntireSQLScriptWithPlatformEolChar();

            fos.write(sScript.getBytes());
            setFile(file);
            // i18n[FileManager.savedfile=Saved to {0}]
            String msg = s_stringMgr.getString("FileManager.savedfile",
                                               file.getAbsolutePath());
            _sqlPanelAPI.getSession().showMessage(msg);
         }
         catch (IOException ex)
         {
            _sqlPanelAPI.getSession().showErrorMessage(ex);
         }
         finally
         {
         	ioUtil.closeOutputStream(fos);
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

      String result  = _sqlPanelAPI.getEntireSQLScript();

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
      _toSaveTo = file;
      getActiveSessionTabWidget().setSqlFile(file);
   }

   private SessionTabWidget getActiveSessionTabWidget()
   {
      return (SessionTabWidget)_sqlPanelAPI.getSession().getActiveSessionWindow();
   }

   public File getFile() {
       return _toSaveTo;
   }
   
   private JFileChooser getFileChooser() {
       if (fileChooser == null) {
           fileChooser = new JFileChooser();
           FileExtensionFilter filter = 
               new FileExtensionFilter("Text files", new String[]{".txt"});
           fileChooser.addChoosableFileFilter(filter);
           fileAppenixes.put(filter, ".txt");
           filter = new FileExtensionFilter("SQL files", new String[]{".sql"});
           fileChooser.addChoosableFileFilter(filter);
           fileAppenixes.put(filter, ".sql");
       }
       return fileChooser;
   }
   
   public void clearCurrentFile()
   {
      _toSaveTo = null;
   }
   
}
