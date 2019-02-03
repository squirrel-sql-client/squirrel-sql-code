package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.DataExportCSVWriter;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.DataExportExcelWriter;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.DataExportJSONWriter;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.DataExportXMLWriter;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.IExportData;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;

public class ExportFileWriter
{
   static ILogger s_log = LoggerController.createLogger(ExportFileWriter.class);
   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportFileWriter.class);


   /**
    * Exports the data structure.
    * @param ctrl the controller to use
    * @param data The data to export
    * @param progressController
    */
   public static long writeFile(TableExportPreferences prefs, IExportData data, ProgressAbortCallback progressController)
   {
      return writeFile(prefs, data, progressController, null);
   }
   public static long writeFile(TableExportPreferences prefs, IExportData data, ProgressAbortCallback progressController, Window ownerFrame)
   {

      File file = null;
      try
      {

         file = new File(prefs.getCsvFile());
         if (null != file.getParentFile())
         {
            file.getParentFile().mkdirs();
         }

         if (prefs.isFormatCSV())
         {
            return new DataExportCSVWriter(file, prefs, progressController).write(data);
         }
         else if (prefs.isFormatXLS() || prefs.isFormatXLSOld())
         {
            return new DataExportExcelWriter(file, prefs, progressController).write(data);
         }
         else if (prefs.isFormatXML())
         {
            return new DataExportXMLWriter(file, prefs, progressController).write(data);
         }
         else if (prefs.isFormatJSON())
         {
            return new DataExportJSONWriter(file, prefs, progressController).write(data);
         }
         else
         {
            throw new IllegalStateException("None of the format flags is true");
         }

      }
      catch (Exception e)
      {

         Object[] params = new Object[]{file, e.getMessage()};
         // i18n[TableExportCsvCommand.failedToWriteFile=Failed to write
         // file\n{0}\nError message\n{1}\nSee last log entry for details.]
         final String msg = s_stringMgr.getString("TableExportCsvCommand.failedToWriteFile", params);
         s_log.error(msg, e);

         if (null != ownerFrame)
         {
            GUIUtils.processOnSwingEventThread(() -> JOptionPane.showMessageDialog(ownerFrame, msg), true);
         }
         else
         {
            Main.getApplication().getMessageHandler().showErrorMessage(msg);
         }

         throw new RuntimeException(e);
      }
   }
}
