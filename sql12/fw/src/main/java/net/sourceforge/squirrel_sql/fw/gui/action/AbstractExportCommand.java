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

import javax.swing.JOptionPane;

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
	   }
	
	static ILogger s_log = LoggerController.createLogger(AbstractExportCommand.class);
	private ProgressAbortCallback progressController = null;

	/**
	 * @param progressController
	 */
	public AbstractExportCommand() {

	}
	
	/**
	 * @param progressController
	 */
	public AbstractExportCommand(ProgressAbortCallback progressController) {
		this.progressController =progressController;
	}


	protected boolean writeFile(TableExportCsvController ctrl, IExportData data) {
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
			String msg = s_stringMgr.getString("TableExportCsvCommand.failedToWriteFile", params);
			s_log.error(msg, e);
			JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), msg);
			return false;
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
	         String msg = s_stringMgr.getString("TableExportCsvCommand.failedToExecuteCommand", params);
	         s_log.error(msg, e);
	         JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), msg);
	      }
	   }
	   
	   public void execute() throws ExportDataException
	   {
	      TableExportCsvController ctrl = createTableExportController();

	      if(false == ctrl.isOK())
	      {
	         return;
	      }

	      if (checkMissingData(ctrl.getSeparatorChar())) {
	          int choice = JOptionPane.showConfirmDialog(GUIUtils.getMainFrame(), 
	                                                     i18n.missingClobDataMsg);
	          if (choice == JOptionPane.YES_OPTION) {
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
	          if (choice == JOptionPane.NO_OPTION) {
	              // abort the export
	              return;
	          }
	          if (choice == JOptionPane.CANCEL_OPTION) {
	              // abort the export
	              return;
	          }
	      }
	      
	      boolean writeFileSuccess = false;
	      
	      try {
	    	  writeFileSuccess = writeFile(ctrl, createExportData(ctrl));
		} catch (ExportDataException e) {
			// Show an error and re-throw the exception.
			s_log.error(i18n.FAILED);
	    	JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), i18n.FAILED);
	    	throw e;
		}
	      
	      if(writeFileSuccess){
	         String command = ctrl.getCommand();

	         if(null != command)
	         {
	            executeCommand(command);
	         } else {
	             // i18n[TableExportCsvCommand.writeFileSuccess=Export to file 
	             // "{0}" is complete.] 
	             String msg = 
	                 s_stringMgr.getString("TableExportCsvCommand.writeFileSuccess", 
	                                       ctrl.getFile().getAbsolutePath());
	             if (s_log.isInfoEnabled()) {
	                 s_log.info(msg);
	             }
	             JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), msg);
	         }
	      }else{
	    	  s_log.info(i18n.FAILED);
	    	  JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), i18n.FAILED);
	      }
	   }

	/**
	 * @return
	 */
	protected TableExportCsvController createTableExportController() {
		return new TableExportCsvController();
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
	
	
	
}
