/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

/**
 * Exporting Data.
 * This class exports the specified {@link IExportData}. The main configuration of the export could be managed with the {@link ExportController}.
 * A {@link ProgressAbortCallback} can monitor the progress, but there must not be a such monitor.
 * There are several target formats supported:
 * <li>CSV</li>
 * <li>XML</li>
 * <li>XLS</li>
 * But be aware, exporting Excel format may use huge heap space, because the data structure must be build in memory.
 * <p>
 * The basic idea is, that Squirrel is able to export each table organized data structure by the same way.
 * Independent, if the source of the data is a JTable, a result set or whatever. Its only important, that the data is tabulated. This is ensured by the
 * interface {@link IExportData}. There are existing some DataExportWriters for various target formats.
 *
 * <b>Note:</b> This class is the result of a re-factoring task. The code was taken from TableExportCsvCommand.
 *
 * @author Stefan Willinger
 * @see DataExportCSVWriter
 * @see DataExportXMLWriter
 * @see DataExportExcelWriter
 */
public class Exporter
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(Exporter.class);
   private static ILogger s_log = LoggerController.createLogger(Exporter.class);
   private final ExportController _exportController;

   private ProgressAbortCallback progressController = null;
   private File _singleExportTargetFile;

   private long writtenRows = -1;
   private ExporterCallback _exporterCallback;

   public Exporter(ExporterCallback exporterCallback, ExportController exportController)
   {
      _exporterCallback = exporterCallback;
      _exportController = exportController;
   }

   public void export()
   {
      exportDialogClosed(_exportController);
   }

   private void exportDialogClosed(ExportController ctrl)
   {
      try
      {
         if(false == ctrl.isOK())
         {
            return;
         }

         if(_exporterCallback.checkMissingData(ctrl.getSeparatorChar()))
         {
            int choice = JOptionPane.showConfirmDialog(ctrl.getOwningWindow(), s_stringMgr.getString("TableExportCsvCommand.missingClobDataMsg", ClobDescriptor.i18n.CLOB_LABEL));
            if(choice != JOptionPane.YES_OPTION)
            {
               return;
            }
         }

         this._singleExportTargetFile = ctrl.getSingleExportTargetFile();

         export(ctrl);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private void export(ExportController ctrl) throws ExportDataException
   {
      this.progressController = _exporterCallback.createProgressController();

      File firstExportedFile;
      try
      {
         final ExportDataInfoList exportDataInfoList = _exporterCallback.createExportData(ctrl);

         if(exportDataInfoList.isEmpty())
         {
            // Happens when multiple SQL result export was chosen with empty export list.
            if(null != progressController)
            {
               progressController.setFinished();
            }
            Runnable runnable = () -> JOptionPane.showMessageDialog(ctrl.getOwningWindow(),
                                                                    s_stringMgr.getString("Exporter.no.files.to.export"),
                                                                    s_stringMgr.getString("Exporter.no.files.to.export.title"),
                                                                    JOptionPane.WARNING_MESSAGE);
            GUIUtils.processOnSwingEventThread(runnable);
            return;
         }

         writtenRows = writeExport(ctrl, exportDataInfoList);

         firstExportedFile = exportDataInfoList.getFirstExportFile(TableExportPreferencesDAO.loadPreferences());
      }
      catch (ExportDataException e)
      {
         // Show an error and re-throw the exception.
         s_log.error(s_stringMgr.getString("AbstractExportCommand.failed"));

         Runnable runnable = () -> JOptionPane.showMessageDialog(ctrl.getOwningWindow(), s_stringMgr.getString("AbstractExportCommand.failed"));
         GUIUtils.processOnSwingEventThread(runnable);

         throw e;
      }

      if(writtenRows >= 0)
      {
         String command = ctrl.getCommand(firstExportedFile);

         if(null != command)
         {
            executeOpenExportedFileCommand(command, ctrl.getOwningWindow());
         }
         else
         {
            // i18n[TableExportCsvCommand.writeFileSuccess=Export to file
            // "{0}" is complete.]
            ExportController finalCtrl = ctrl;
            GUIUtils.processOnSwingEventThread(() -> showExportSuccessMessage(ctrl.getOwningWindow(), writtenRows, firstExportedFile), true);
         }
      }
      else
      {
         s_log.info(s_stringMgr.getString("AbstractExportCommand.failed"));

         Runnable runnable = new Runnable()
         {
            public void run()
            {
               JOptionPane.showMessageDialog(ctrl.getOwningWindow(), s_stringMgr.getString("AbstractExportCommand.failed"));
            }
         };

         GUIUtils.processOnSwingEventThread(runnable, true);

      }
   }


   /**
    * Exports the data structure.
    *
    * @param ctrl the controller to use
    * @param exportDataInfoList The data to export
    * @return the number of written data rows or a negative value, if not the whole data are exported.
    */
   private long writeExport(final ExportController ctrl, ExportDataInfoList exportDataInfoList)
   {
      return ExportFileWriter.export(exportDataInfoList, _exporterCallback.getExportPreferences(), progressController, ctrl.getOwningWindow());
   }


   private void executeOpenExportedFileCommand(String command, final Window owner)
   {
      try
      {
         Runtime.getRuntime().exec(command);
      }
      catch (IOException e)
      {
         Object[] params = new Object[]{command, e.getMessage()};
         final String msg = s_stringMgr.getString("TableExportCsvCommand.failedToExecuteCommand", params);
         s_log.error(msg, e);

         Runnable runnable = new Runnable()
         {
            public void run()
            {
               JOptionPane.showMessageDialog(owner, msg);
            }
         };

         GUIUtils.processOnSwingEventThread(runnable, true);
      }
   }

   private void showExportSuccessMessage(Window owner, long writtenRows, File exportFile)
   {
      try
      {
         String[] selectionValues =
               {
                     s_stringMgr.getString("TableExportCsvCommand.export.completed.ok"),
                     s_stringMgr.getString("TableExportCsvCommand.export.completed.ok.show.in.file.manager"),
               };

         String formattedWrittenRows = NumberFormat.getIntegerInstance().format(writtenRows);

         String fileName = StringUtilities.shortenBegin(exportFile.getAbsolutePath(), 300, "...");

         int selectIndex = JOptionPane.showOptionDialog(
               owner,
               s_stringMgr.getString("TableExportCsvCommand.writeFileSuccess", formattedWrittenRows, fileName),
               s_stringMgr.getString("TableExportCsvCommand.writeFileSuccess.title"),
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.INFORMATION_MESSAGE,
               null,
               selectionValues,
               selectionValues[0]);

         if(selectIndex == 1)
         {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(exportFile.getParentFile());
         }
      }
      catch (IOException e)
      {
         s_log.error("Failed to open path to file " + exportFile.getAbsolutePath(), e);
      }
   }


   public void progress(String task)
   {
      if(progressController != null)
      {
         progressController.currentlyLoading(task);
      }
   }

   /**
    * @return the targetFile
    */
   public File getSingleExportTargetFile()
   {
      return _singleExportTargetFile;
   }

   /**
    * @return the writtenRows
    */
   public long getWrittenRows()
   {
      return writtenRows;
   }

   public ExportDataInfoList getMultipleSqlResults()
   {
      return _exportController.getMultipleSqlResults();
   }
}
