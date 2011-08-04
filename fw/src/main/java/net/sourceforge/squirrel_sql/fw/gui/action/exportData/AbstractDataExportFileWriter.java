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
package net.sourceforge.squirrel_sql.fw.gui.action.exportData;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.gui.action.TableExportCsvController;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;

/**
 * Exports a data structure into a file.
 * This abstract implementation does not know the format of the target file, e.g. XML or CSV. 
 * It rather knows the structure of {@link IExportData} and provide some callback methods for exporting data.
 * A further scope of this abstract class is the interaction with  {@link TableExportCsvController} and {@link ProgressAbortCallback}.
 * A concrete implementation is responsible for formating and writing the data into the target.
 * @author Stefan Willinger
 * @see DataExportCSVWriter
 * @see DataExportExcelWriter
 * @see DataExportXMLWriter
 */
public abstract class AbstractDataExportFileWriter implements IDataExportWriter{
	
	/**
	 * Constant, for updating the progress bar each x rows.
	 */
	private static final int FEEDBACK_EVRY_N_LINE = 1000;
	/**
	 * The target file.
	 */
	private File file;
	/**
	 * Controller for exporting data. 
	 */
	private TableExportCsvController ctrl;
	/**
	 * Flag, if the header line should be included
	 */
	private boolean includeHeaders;
	
	/**
	 * Progress controller with the opportunity to abort the operation.
	 */
	private ProgressAbortCallback progressController;

	/**
	 * Construct this one.
	 * @param file The target file.
	 * @param ctrl The controller to use
	 * @param includeHeaders  Flag, if the header line should be exported
	 * @param progressController ProgressController to use.
	 */
	public AbstractDataExportFileWriter(File file, TableExportCsvController ctrl, boolean includeHeaders, ProgressAbortCallback progressController) {
		this.file = file;
		this.ctrl = ctrl;
		this.includeHeaders = includeHeaders;
		this.progressController = progressController;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.IDataExportWriter#write(net.sourceforge.squirrel_sql.fw.gui.action.exportData.IExportData)
	 */
	public boolean write(IExportData data) throws Exception {

		beforeWorking(file);

		if (includeHeaders) {
			Iterator<String> headers = data.getHeaders();

			int colIdx = 0;
			beforeHeader();
			while (headers.hasNext()) {
				String columnName = (String) headers.next();
				addHeaderCell(colIdx,columnName);
				colIdx++;
			}
			afterHeader();
		}

		Iterator<IExportDataRow> rows = data.getRows();
		
		progress("Begin writing");
		beforeRows();
		long rowsCount = 1;
		while (rows.hasNext() && isStop() == false) {
			IExportDataRow aRow = rows.next();
			if((rowsCount) % FEEDBACK_EVRY_N_LINE == 0){
				progress(rowsCount + "Rows completed");
			}
			beforeRow(aRow.getRowIndex());

			Iterator<IExportDataCell> cells = aRow.getCells();
			while (cells.hasNext()) {
				IExportDataCell cell = cells.next();
				addCell(cell);
			}
			afterRow();
			rowsCount++;
		}
		afterRows();
		progress("Finished with " + rowsCount + "Rows");
		progress("Closing the file");
		// All sheets and cells added. Now write out the workbook
		afterWorking();

		if (isStop()) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * Callback before processing the first row.
	 */
	public void beforeRows() {
		// Can be overridden.
	}

	/**
	 * Callback after processing all rows
	 */
	public void afterRows() {
		// Can be overridden.
	}

	
	/**
	 * Callback, before a row is processed.
	 */
	public void beforeRow(int rowIdx) throws Exception{
		// Can be overridden.
		
	}
	/**
	 * Callback, after the processing of a row.
	 * 
	 */
	public void afterRow() throws Exception {
		// Can be overridden.
		
	}

	

	/**
	 * Callback, after all rows are processed.
	 * @throws Exception 
	 * 
	 */
	protected abstract void afterWorking() throws  Exception;

	/**
	 * Adds a cell to a row of data.
	 * @param cell cell to be added
	 * @throws Exception
	 */
	protected abstract void addCell(IExportDataCell cell) throws Exception;

	/**
	 * Callback, before the processing of the header line will start.
	 */
	protected void beforeHeader() throws Exception{
		// Can be overridden.
	}
	
	/**
	 * Adds a header cell into the output data.
	 * @param colIdx the index of the cell
	 * @param columnName the name of the column
	 * @throws Exception 
	 */
	protected abstract void addHeaderCell(int colIdx, String columnName) throws Exception;

	/**
	 * Callback, after we have processed all header cells.
	 */
	protected void afterHeader() throws Exception {
		// Can be overridden.
		
	}

	/**
	 * Callback that indicate, that the work is just started.
	 * Normally, a concrete implementation would do some setup at this point. Like the initializing of a output stream. 
	 * @param file The target file.
	 * @throws Exception if a Exception occurs.
	 */
	protected abstract void beforeWorking(File file) throws Exception;

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the ctrl
	 */
	public TableExportCsvController getCtrl() {
		return ctrl;
	}

	/**
	 * @param ctrl the ctrl to set
	 */
	public void setCtrl(TableExportCsvController ctrl) {
		this.ctrl = ctrl;
	}

	/**
	 * @return the includeHeaders
	 */
	public boolean isIncludeHeaders() {
		return includeHeaders;
	}

	/**
	 * @param includeHeaders the includeHeaders to set
	 */
	public void setIncludeHeaders(boolean includeHeaders) {
		this.includeHeaders = includeHeaders;
	}

	/**
	 * Tells the progress controller the current task.
	 * @param task Task to be added to the progress controller.
	 */
	protected void progress(String task){
		if(progressController != null){
			progressController.currentlyLoading(task);
		}
	}

	/**
	 * Checks, if the work should be stopped.
	 * @return true, if the work should be stopped, otherwise false.
	 */
	protected boolean isStop(){
		if(progressController == null){
			return false;
		}else{
			return progressController.isStop();
		}
	}
}
