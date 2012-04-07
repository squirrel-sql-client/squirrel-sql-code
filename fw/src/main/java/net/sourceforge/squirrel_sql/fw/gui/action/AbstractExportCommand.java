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
package net.sourceforge.squirrel_sql.fw.gui.action;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;

import javax.swing.*;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.DataExportCSVWriter;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.DataExportExcelWriter;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.DataExportXMLWriter;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.ExportDataException;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.IExportData;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Abstract command for exporting Data.
 * This command exports the specified {@link IExportData}. The main configuration of the export could be managed with the {@link TableExportCsvController}.
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
public abstract class AbstractExportCommand {
	static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(AbstractExportCommand.class);
	
	 static interface i18n {
	       //i18n[TableExportCsvCommand.missingClobDataMsg=Found Clob placeholder 
	       //({0}) amongst data to be exported. Continue exporting cell data?]
	       String missingClobDataMsg = 
	           s_stringMgr.getString("TableExportCsvCommand.missingClobDataMsg",
	                                 ClobDescriptor.i18n.CLOB_LABEL);
	       
	       String FAILED = s_stringMgr.getString("AbstractExportCommand.failed");
	       
	       String ANOTHER_EXPORT_IS_ACTIVE = s_stringMgr.getString("AbstractExportCommand.anotherExportIsActive");
	       String TITLE_ANOTHER_EXPORT_IS_ACTIVE = s_stringMgr.getString("AbstractExportCommand.anotherExportIsActive.title");
	   }
	
	static ILogger s_log = LoggerController.createLogger(AbstractExportCommand.class);
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
	public AbstractExportCommand() {

	}

	/**
	 * Exports the data structure.
	 * @param ctrl the controller to use
	 * @param data The data to export
	 * @return the number of written data rows or a negative value, if not the whole data are exported.
	 */
	protected long writeFile(TableExportCsvController ctrl, IExportData data) {
		File file = null;
		try {

			file = ctrl.getFile();
			if (null != file.getParentFile()) {
				file.getParentFile().mkdirs();
			}

			boolean includeHeaders = ctrl.includeHeaders();

			if (TableExportCsvController.EXPORT_FORMAT_CSV == ctrl.getExportFormat()) {
				return new DataExportCSVWriter(file, ctrl, includeHeaders, progressController).write(data);
			} else if (TableExportCsvController.EXPORT_FORMAT_XLS == ctrl.getExportFormat()) {
				return new DataExportExcelWriter(file, ctrl, includeHeaders, progressController).write(data);
			} else if (TableExportCsvController.EXPORT_FORMAT_XML == ctrl.getExportFormat()) {
				return new DataExportXMLWriter(file, ctrl, includeHeaders, progressController).write(data);
			} else {
				throw new IllegalStateException("Unknown export format " + ctrl.getExportFormat());
			}

		} catch (Exception e) {

			Object[] params = new Object[] { file, e.getMessage() };
			// i18n[TableExportCsvCommand.failedToWriteFile=Failed to write
			// file\n{0}\nError message\n{1}\nSee last log entry for details.]
			final String msg = s_stringMgr.getString("TableExportCsvCommand.failedToWriteFile", params);
			s_log.error(msg, e);

         Runnable runnable = new Runnable()
         {
            public void run()
            {
               JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), msg);
            }
         };

         GUIUtils.processOnSwingEventThread(runnable, true);

         return -1;
		} 

	}

	
	   private void executeCommand(String command)
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
                  JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), msg);
               }
            };

            GUIUtils.processOnSwingEventThread(runnable, true);
         }
	   }

   public void execute() throws ExportDataException
   {
      try
      {

         boolean fileIsInUse = false;

         TableExportCsvController ctrl;
         do
         {
            ctrl = createTableExportController();

            if (false == ctrl.isOK())
            {
               return;
            }

            if (checkMissingData(ctrl.getSeparatorChar()))
            {
               int choice = JOptionPane.showConfirmDialog(GUIUtils.getMainFrame(),
                     i18n.missingClobDataMsg);
               if (choice == JOptionPane.YES_OPTION)
               {
                  // Need to somehow call
                  // SQLResultExecuterPanel.reRunSelectedResultTab(true);
                  //
                  // Something like :
                  // SQLResultExecuterPanel panel = getPanel();
                  // panel.reRunSelectedResultTab(true);
                  //
                  // However, that doesn't apply when the user is exporting from the
                  // table contents table.  There needs to be a more generic way to
                  // do this for all tables containing data that is to be exported
                  // where some of the fields contain placeholders.
                  // For now, we just inform the user and let them either continue
                  // or abort and change the configuration manually,
                  // re-run the query / reload the table data and re-export.
               }
               if (choice == JOptionPane.NO_OPTION)
               {
                  // abort the export
                  return;
               }
               if (choice == JOptionPane.CANCEL_OPTION)
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
                     JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), i18n.ANOTHER_EXPORT_IS_ACTIVE, i18n.TITLE_ANOTHER_EXPORT_IS_ACTIVE, JOptionPane.WARNING_MESSAGE);
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
                  JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), i18n.FAILED);
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
               executeCommand(command);
            }
            else
            {
               // i18n[TableExportCsvCommand.writeFileSuccess=Export to file
               // "{0}" is complete.]
               final String msg =
                     s_stringMgr.getString("TableExportCsvCommand.writeFileSuccess", NumberFormat.getIntegerInstance().format(writtenRows),
                           ctrl.getFile().getAbsolutePath());
               if (s_log.isInfoEnabled())
               {
                  s_log.info(msg);
               }

               Runnable runnable = new Runnable()
               {
                  public void run()
                  {
                     JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), msg);
                  }
               };

               GUIUtils.processOnSwingEventThread(runnable, true);

            }
         }
         else
         {
            s_log.info(i18n.FAILED);

            Runnable runnable = new Runnable()
            {
               public void run()
               {
                  JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), i18n.FAILED);
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

   /**
	 * Create a instance of {@link ProgressAbortCallback} if necessary.
	 * Subclasse may override this.
	 * @return returns null.
	 */
	protected ProgressAbortCallback createProgressController() {
		// default null;
		return null;
	}

	/**
	 * @return
	 */
	protected TableExportCsvController createTableExportController() {

      try
      {
         final TableExportCsvController[] buf = new TableExportCsvController[1];

         Runnable runnable = new Runnable()
         {
            public void run()
            {
               buf[0] = new TableExportCsvController();
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
	protected abstract IExportData createExportData(TableExportCsvController ctrl) throws ExportDataException;
	
	/**
	 * @return the progressController
	 */
	public ProgressAbortCallback getProgressController() {
		return progressController;
	}

	/**
	 * @param progressController the progressController to set
	 */
	public void setProgressController(ProgressAbortCallback progressController) {
		this.progressController = progressController;
	}

	/**
	 * @param string
	 */
	protected void progress(String task) {
		if(progressController != null){
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
