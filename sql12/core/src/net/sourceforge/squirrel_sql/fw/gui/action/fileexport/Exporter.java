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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
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
   private final ExportControllerProxy _exportController;

   private ProgressAbortCallback _progressController = null;
   private File _singleExportTargetFile;

   private long writtenRows = -1;
   private ExporterCallback _exporterCallback;

   public Exporter(ExporterCallback exporterCallback, ExportControllerProxy exportController)
   {
      _exporterCallback = exporterCallback;
      _exportController = exportController;
   }

   public void export()
   {
      exportDialogClosed(_exportController);
   }

   private void exportDialogClosed(ExportControllerProxy ctrl)
   {
      try
      {
         if(false == ctrl.isOK())
         {
            return;
         }

         if(ctrl.isUITableMissingBlobData())
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

   private void export(ExportControllerProxy ctrl) throws ExportDataException
   {
      this._progressController = _exporterCallback.createProgressController();

      File firstExportedFile;
      try
      {
         final ExportDataInfoList exportDataInfoList = ctrl.createExportData(_progressController);

         if(exportDataInfoList.isEmpty())
         {
            // Happens when multiple SQL result export was chosen with empty export list.
            if(null != _progressController)
            {
               _progressController.setFinished();
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
      catch (Exception e)
      {
         Runnable runnable = () -> JOptionPane.showMessageDialog(ctrl.getOwningWindow(), s_stringMgr.getString("AbstractExportCommand.failed.new"));
         GUIUtils.processOnSwingEventThread(runnable);
         if (null != _progressController)
         {
            try
            {
               _progressController.setVisible(false);
               _progressController.dispose();
            }
            catch (Exception ex)
            {
               s_log.error("Failed to close progress display.", e);
            }
         }

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
            GUIUtils.processOnSwingEventThread(() -> showExportSuccessMessage(ctrl.getOwningWindow(), writtenRows, firstExportedFile, ctrl.isShowExportCompleteAsDialog() ), true);
         }
      }
      else
      {
         s_log.info("Probably failed to export the result of the SQL Select statement into a file. No rows were written.");

         String noRowsMessage = s_stringMgr.getString("AbstractExportCommand.failed.no.rows.written");
         if (ctrl.isShowExportCompleteAsDialog())
         {
            Runnable runnable = () -> JOptionPane.showMessageDialog(ctrl.getOwningWindow(), noRowsMessage);
            GUIUtils.processOnSwingEventThread(runnable, true);
         }
         else
         {
            Main.getApplication().getMessageHandler().showWarningMessage(noRowsMessage);
         }

      }
   }


   /**
    * Exports the data structure.
    *
    * @param ctrl the controller to use
    * @param exportDataInfoList The data to export
    * @return the number of written data rows or a negative value, if not the whole data are exported.
    */
   private long writeExport(final ExportControllerProxy ctrl, ExportDataInfoList exportDataInfoList)
   {
      return ExportFileWriter.export(exportDataInfoList, ctrl.getPreferences(), _progressController, ctrl.getOwningWindow());
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

   private void showExportSuccessMessage(Window owner, long writtenRows, File exportFile, boolean showExportSuccessAsDialog)
   {
      String formattedWrittenRows = NumberFormat.getIntegerInstance().format(writtenRows);
      String fileName = StringUtilities.shortenBegin(exportFile.getAbsolutePath(), 300, "...");
      String exportSuccessMessage = s_stringMgr.getString("TableExportCsvCommand.writeFileSuccess", formattedWrittenRows, fileName);

      if(false == showExportSuccessAsDialog)
      {
         Main.getApplication().getMessageHandler().showMessage(exportSuccessMessage);
         return;
      }

      String[] selectionValues =
            {
                  s_stringMgr.getString("TableExportCsvCommand.export.completed.ok"),
                  s_stringMgr.getString("TableExportCsvCommand.export.completed.ok.show.in.file.manager"),
            };


      int selectIndex = JOptionPane.showOptionDialog(
            owner,
            exportSuccessMessage,
            s_stringMgr.getString("TableExportCsvCommand.writeFileSuccess.title"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            selectionValues,
            selectionValues[0]);

      if(selectIndex == 1)
      {
         DesktopUtil.openInFileManager(exportFile);
      }
   }


   public void progress(String task)
   {
      if(_progressController != null)
      {
         _progressController.currentlyLoading(task);
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

}
