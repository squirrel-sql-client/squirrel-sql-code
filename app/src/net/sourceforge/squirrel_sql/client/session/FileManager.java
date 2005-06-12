package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.gui.ChooserPreviewer;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;


public class FileManager
{
   private ISQLPanelAPI _sqlPanelAPI;

   private File _toSaveTo = null;

   FileManager(ISQLPanelAPI sqlPanelAPI)
   {
      _sqlPanelAPI = sqlPanelAPI;
   }

   public void save()
   {
      saveIntern(false);
   }

   public void saveAs()
   {
      saveIntern(true);
   }


   public void open()
   {
      JFileChooser chooser = new JFileChooser();
      chooser.addChoosableFileFilter(new FileExtensionFilter("Text files", new String[]{".txt"}));
      chooser.addChoosableFileFilter(new FileExtensionFilter("SQL files", new String[]{".sql"}));
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
         loadScript(chooser.getSelectedFile());
      }
   }

   private void loadScript(File file)
   {
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
         _sqlPanelAPI.appendSQLScript(sb.toString(), true);
         setFile(file);
      }
      catch (java.io.IOException io)
      {
         _sqlPanelAPI.getSession().getMessageHandler().showErrorMessage(io);
      }
      finally
      {
         try
         {
            if (bis != null)
            {
               bis.close();
            }
         }
         catch (IOException ignore)
         {
         }
         try
         {
            fis.close();
         }
         catch (IOException io)
         {
         }
      }
   }


   public void saveIntern(boolean toNewFile)
   {
      if (toNewFile)
      {
         _toSaveTo = null;
      }


      JFileChooser chooser = new JFileChooser();

      HashMap fileAppenixes = new HashMap();
      FileExtensionFilter filter;
      filter = new FileExtensionFilter("Text files", new String[]{".txt"});
      chooser.addChoosableFileFilter(filter);
      fileAppenixes.put(filter, ".txt");

      filter = new FileExtensionFilter("SQL files", new String[]{".sql"});
      chooser.addChoosableFileFilter(filter);
      fileAppenixes.put(filter, ".sql");

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
            saveScript(frame, _toSaveTo, false);
            break;
         }

         if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
         {
            _toSaveTo = chooser.getSelectedFile();

            if (!_toSaveTo.exists() && null != fileAppenixes.get(chooser.getFileFilter()))
            {
               if (!_toSaveTo.getAbsolutePath().endsWith(fileAppenixes.get(chooser.getFileFilter()).toString()))
               {
                  _toSaveTo = new File(_toSaveTo.getAbsolutePath() + fileAppenixes.get(chooser.getFileFilter()));
               }
            }

            if (saveScript(frame, _toSaveTo, true))
            {
               break;
            }
         }
         else
         {
            break;
         }
      }
   }

   private boolean saveScript(JFrame frame, File file, boolean askReplace)
   {
      boolean doSave = false;
      if (askReplace && file.exists())
      {
         doSave =
            Dialogs.showYesNo(frame,
               file.getAbsolutePath() + "\nalready exists. Do you want to replace it?");
         //i18n
         if (!doSave)
         {
            return false;
         }
         if (!file.canWrite())
         {
            Dialogs.showOk(frame,
               "File " + file.getAbsolutePath() + "\ncannot be written to.");
            //i18n
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

            String sScript = _sqlPanelAPI.getEntireSQLScript();

            fos.write(sScript.getBytes());
            setFile(file);
            _sqlPanelAPI.getSession().getMessageHandler().showMessage("Saved to " + file.getAbsolutePath());
         }
         catch (IOException ex)
         {
            _sqlPanelAPI.getSession().getMessageHandler().showErrorMessage(ex);
         }
         finally
         {
            if (fos != null)
            {
               try
               {
                  fos.close();
               }
               catch (IOException ignore)
               {
               }
            }
         }
      }
      return true;
   }

   private void setFile(File file)
   {
      _toSaveTo = file;
      _sqlPanelAPI.getSession().getActiveSessionWindow().setSqlFile(file);
   }

}
