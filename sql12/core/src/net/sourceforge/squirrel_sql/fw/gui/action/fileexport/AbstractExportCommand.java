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
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

/**
 * Abstract command for exporting Data.
 * This command exports the specified {@link IExportData}. The main configuration of the export could be managed with the {@link TableExportController}.
 * A {@link ProgressAbortCallback} can monitor the progress, but there must not be a such monitor.
 * There are several target formats supported:
 * <li>CSV</li>
 * <li>XML</li>
 * <li>XLS</li>
 * But be aware, exporting to the excel format may use huge heap space, because the data structure must be build in memory.
 * <p>
 * The basic idea is, that Squirrel is able to export each table organized data structure by the same way.
 * Independent, if the source of the data is a JTable, a result set or whatever. Its only important, that the data is tabulated. This is ensured by the
 * interface {@link IExportData}. There are existing some DataExportWriters for various target formats.
 * 
 * <b>Note:</b> This class is the result of a re-factoring task. The code was taken from TableExportCsvCommand.
 * @see DataExportCSVWriter
 * @see DataExportXMLWriter
 * @see DataExportExcelWriter
 * 
 * @author Stefan Willinger
 * 
 */
public abstract class AbstractExportCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AbstractExportCommand.class);
   private static ILogger s_log = LoggerController.createLogger(AbstractExportCommand.class);

	 static interface i18n {
	       //i18n[TableExportCsvCommand.missingClobDataMsg=Found Clob placeholder 
	       //({0}) amongst data to be exported. Continue exporting cell data?]
	       String missingClobDataMsg =  s_stringMgr.getString("TableExportCsvCommand.missingClobDataMsg", ClobDescriptor.i18n.CLOB_LABEL);
	       
	       String FAILED = s_stringMgr.getString("AbstractExportCommand.failed");
	       
	       String ANOTHER_EXPORT_IS_ACTIVE = s_stringMgr.getString("AbstractExportCommand.anotherExportIsActive");
	       String TITLE_ANOTHER_EXPORT_IS_ACTIVE = s_stringMgr.getString("AbstractExportCommand.anotherExportIsActive.title");
	   }

	private ProgressAbortCallback progressController = null;
	private File targetFile;
	
	/**
	 * Container to store all files, referenced by a currently running export process.
	 */
	private ExportFileContainer fileContainer = ExportFileContainer.getInstance();
	

	private long writtenRows = -1;
	/**
	 * @param progressController
	 */
	public AbstractExportCommand()
   {

	}

	/**
	 * Exports the data structure.
	 * @param ctrl the controller to use
	 * @param data The data to export
	 * @return the number of written data rows or a negative value, if not the whole data are exported.
	 */
   protected long writeFile(final TableExportController ctrl, IExportData data)
   {
      return ExportFileWriter.writeFile(TableExportPreferencesDAO.loadPreferences(), data, progressController, ctrl.getOwningWindow());
   }


   private void executeCommand(String command, final Window owner)
	   {
	      try
	      {
	         Runtime.getRuntime().exec(command);
	      }
	      catch (IOException e)
	      {
	         Object[] params = new Object[]{command, e.getMessage()};
	         // i18n[TableExportCsvCommand.failedToExecuteCommand=Failed to execute\n{0}\nError message\n{1}\nSee last log entry for details.]
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

   public void execute(final Window owner) throws ExportDataException
   {
      try
      {

         boolean fileIsInUse = false;

         TableExportController ctrl;
         do
         {
            ctrl = createTableExportController(owner);

            if (false == ctrl.isOK())
            {
               return;
            }

            if (checkMissingData(ctrl.getSeparatorChar()))
            {
               int choice = JOptionPane.showConfirmDialog(owner, i18n.missingClobDataMsg);
               if (choice != JOptionPane.YES_OPTION)
               {
                  // abort the export
                  return;
               }
            }

            this.targetFile = ctrl.getFile();

            /*
                 * Allow only one export at a time.
                 */
            if (fileContainer.add(this.targetFile) == false)
            {
               fileIsInUse = true;
               Runnable runnable = new Runnable()
               {
                  public void run()
                  {
                     JOptionPane.showMessageDialog(owner, i18n.ANOTHER_EXPORT_IS_ACTIVE, i18n.TITLE_ANOTHER_EXPORT_IS_ACTIVE, JOptionPane.WARNING_MESSAGE);
                  }
               };

               GUIUtils.processOnSwingEventThread(runnable, true);

            }
            else
            {
               fileIsInUse = false;
            }

         } while (fileIsInUse);


         this.progressController = createProgressController();


         try
         {
            writtenRows = writeFile(ctrl, createExportData(ctrl));
         }
         catch (ExportDataException e)
         {
            // Show an error and re-throw the exception.
            s_log.error(i18n.FAILED);

            Runnable runnable = new Runnable()
            {
               public void run()
               {
                  JOptionPane.showMessageDialog(owner, i18n.FAILED);
               }
            };

            GUIUtils.processOnSwingEventThread(runnable);

            throw e;
         }

         if (writtenRows >= 0)
         {
            String command = ctrl.getCommand();

            if (null != command)
            {
               executeCommand(command, owner);
            }
            else
            {
               // i18n[TableExportCsvCommand.writeFileSuccess=Export to file
               // "{0}" is complete.]
               TableExportController finalCtrl = ctrl;
               GUIUtils.processOnSwingEventThread(() -> showExportSuccessMessage(owner, writtenRows, finalCtrl.getFile()), true);

            }
         }
         else
         {
            s_log.info(i18n.FAILED);

            Runnable runnable = new Runnable()
            {
               public void run()
               {
                  JOptionPane.showMessageDialog(owner, i18n.FAILED);
               }
            };

            GUIUtils.processOnSwingEventThread(runnable, true);

         }
      }
      finally
      {
         if (this.targetFile != null)
         {
            this.fileContainer.remove(this.targetFile);
         }
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

         String fileName = StringUtilities.shorten(exportFile.getAbsolutePath(), 300, "...");

         int selectIndex = JOptionPane.showOptionDialog(
               owner,
               s_stringMgr.getString("TableExportCsvCommand.writeFileSuccess", formattedWrittenRows, fileName),
               s_stringMgr.getString("TableExportCsvCommand.writeFileSuccess.title"),
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.INFORMATION_MESSAGE,
               null,
               selectionValues,
               selectionValues[0]);

         if (selectIndex == 1)
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

   /**
	 * Create a instance of {@link ProgressAbortCallback} if necessary.
	 * Subclasse may override this.
	 * @return returns null.
	 */
   protected ProgressAbortCallback createProgressController()
   {
      return null;
   }

	/**
	 * @return
    * @param owner
	 */
	protected TableExportController createTableExportController(final Window owner) {

      try
      {
         final TableExportController[] buf = new TableExportController[1];

         Runnable runnable = new Runnable()
         {
            public void run()
            {
               buf[0] = new TableExportController(owner);
            }
         };
         GUIUtils.processOnSwingEventThread(runnable, true);

         return buf[0];
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

	/**
	 * @param separatorChar
	 * @return
	 */
	protected abstract boolean checkMissingData(String separatorChar);

	/**
	 * Creates the export data from the original source.
	 * @param ctrl the controller to use.
	 * @return the data for the export.
	 * @throws ExportDataException if any problem occurs while creating the data.
	 */
	protected abstract IExportData createExportData(TableExportController ctrl) throws ExportDataException;

   /**
	 * @param string
	 */
   protected void progress(String task)
   {
      if (progressController != null)
      {
         progressController.currentlyLoading(task);
      }
   }

	/**
	 * @return the targetFile
	 */
	public File getTargetFile() {
		return targetFile;
	}

	/**
	 * @return the writtenRows
	 */
	public long getWrittenRows() {
		return writtenRows;
	}
	
	
	
}
