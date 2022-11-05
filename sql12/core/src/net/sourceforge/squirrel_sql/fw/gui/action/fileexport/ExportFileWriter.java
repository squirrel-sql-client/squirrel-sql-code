package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JOptionPane;
import java.awt.Window;
import java.io.File;

public class ExportFileWriter
{
   static ILogger s_log = LoggerController.createLogger(ExportFileWriter.class);
   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportFileWriter.class);


   /**
    * Exports the data structure.
    *
    * @param ctrl               the controller to use
    * @param data               The data to export
    */
   public static long writeFile(IExportData data, TableExportPreferences prefs, ProgressAbortCallback progressController)
   {
      return export(ExportDataInfoList.single(data), prefs, progressController, null);
   }

   public static long export(ExportDataInfoList exportDataInfoList, TableExportPreferences prefs, ProgressAbortCallback progressController, Window ownerFrame)
   {

      long ret = 0;
      File file = null;
      try
      {
         if (prefs.isFormatXLS() || prefs.isFormatXLSOld())
         {
            file = exportDataInfoList.getMultipleSqlResultExportDestinationInfo().getExcelExportFile();

            file = checkAndPrepareExportFile(file);

            ret += new DataExportExcelWriter(file, prefs, progressController).write(exportDataInfoList);
         }
         else
         {
            for (ExportDataInfo exportDataInfo : exportDataInfoList.getExportDataInfos())
            {
               file = checkAndPrepareExportFile(exportDataInfo.getFile(prefs));

               if (prefs.isFormatCSV())
               {
                  ret += new DataExportCSVWriter(file, prefs, progressController).write(exportDataInfo.getExportData());
               }
               else if (prefs.isFormatXML())
               {
                  ret += new DataExportXMLWriter(file, prefs, progressController).write(exportDataInfo.getExportData());
               }
               else if (prefs.isFormatJSON())
               {
                  ret += new DataExportJSONWriter(file, prefs, progressController).write(exportDataInfo.getExportData());
               }
               else
               {
                  throw new IllegalStateException("None of the format flags is true");
               }
            }

            if(null != progressController)
            {
               progressController.setFinished();
            }
         }

         return ret;
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

   private static File checkAndPrepareExportFile(File file)
   {
      // Checks if file name is valid.
      // Raises an InvalidPathException if not.
      file.toPath();

      if (null != file.getParentFile())
      {
         file.getParentFile().mkdirs();
      }

      return file;
   }
}
