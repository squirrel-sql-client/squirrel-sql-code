package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.concurrent.TimeUnit;

public class FileExportService
{
   /**
    * Constant, for updating the progress bar each x seconds.
    */
   private static final int FEEDBACK_EVRY_N_SECONDS = 2;

   private File _file;
   private TableExportPreferences _prefs;
   private final ProgressAbortCallback _progressController;

   private long timeOfLastStatusUpdate = 0;

   public FileExportService(File file, TableExportPreferences prefs, ProgressAbortCallback progressController)
   {
      _file = file;
      _prefs = prefs;
      _progressController = progressController;
   }

   public File getFile()
   {
      return _file;
   }

   public TableExportPreferences getPrefs()
   {
      return _prefs;
   }

   public void setProgressFinished()
   {
      if (_progressController != null)
      {
         _progressController.setFinished();
      }
   }

   public boolean isStatusUpdateNecessary()
   {
      long time = System.currentTimeMillis();

      if ((timeOfLastStatusUpdate + TimeUnit.SECONDS.toMillis(FEEDBACK_EVRY_N_SECONDS)) < time)
      {
         timeOfLastStatusUpdate = time;
         return true;
      }
      else
      {
         return false;
      }
   }

   public void setFile(File file)
   {
      _file = file;
   }

   public void setPrefs(TableExportPreferences prefs)
   {
      _prefs = prefs;
   }

   public void progress(String task)
   {
      if (_progressController != null)
      {
         _progressController.currentlyLoading(task);
      }

   }

   public void taskStatus(String status)
   {
      if (_progressController != null)
      {
         _progressController.setTaskStatus(status);
      }
   }

   public boolean isUserCanceled()
   {
      if (_progressController == null)
      {
         return false;
      }
      else
      {
         return _progressController.isUserCanceled();
      }
   }

   public Charset getCharset()
   {
      try
      {
         return Charset.forName(getPrefs().getEncoding());
      }
      catch (IllegalCharsetNameException icne)
      {
         return Charset.defaultCharset();
      }
   }
}
