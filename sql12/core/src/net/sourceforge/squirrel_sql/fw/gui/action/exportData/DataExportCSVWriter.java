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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.TableExportCsvController;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;

/**
 * Exports {@link IExportData} to a CSV file.
 * <b>Note:</b> This class is the result of a refactoring task. The code was taken from TableExportCsvCommand.
 * @author Stefan Willinger
 *
 */
public class DataExportCSVWriter extends AbstractDataExportFileWriter {

	private List<String> headerCells = new ArrayList<String>();
	private List<String> aRow = new ArrayList<String>();
	private BufferedWriter bw;
	

	/**
	 * @param file
	 * @param ctrl
	 * @param progressController 
	 */
	public DataExportCSVWriter(File file, TableExportCsvController ctrl, boolean includeHeaders, ProgressAbortCallback progressController) {
		super(file, ctrl, includeHeaders,progressController);
	}

	protected String getDataCSV(String sepChar, IExportDataCell cellObj, boolean useGlobalFormatting) {
		if (cellObj.getObject() == null) {
			return "";
		} 
		
		String value;
		if(cellObj.getColumnDisplayDefinition() != null && useGlobalFormatting){
			
			value = CellComponentFactory
			.renderObject(cellObj.getObject(), cellObj.getColumnDisplayDefinition());
		}else{
			value = cellObj.getObject().toString();
		}

			return getDataCSV(sepChar, value);
	}
	
	/**
	 * Converts a value into a CSV-value.
	 * @param sepChar separator char to use.
	 * @param value the original strinv value
	 * @return a value, representing a csv column.
	 */
	public static String getDataCSV(String sepChar, String value) {
		if (value == null) {
			return "";
		} 
		
		String ret = value.toString().trim();

			if (0 <= ret.indexOf(sepChar) || 0 <= ret.indexOf('\n') ||  0 <= ret.indexOf('\r') || 0 <= ret.indexOf('"')) {
				ret = "\"" + ret.replaceAll("\"", "\"\"") + "\"";
			}

			return ret;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#afterWorking()
	 */
	@Override
	protected void afterWorking() throws Exception {
		bw.flush();
		bw.close();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#addHeaderCell(int, int, java.lang.String)
	 */
	@Override
	protected void addHeaderCell(int colIdx, String columnName) throws Exception {
		this.headerCells.add(getDataCSV(getCtrl().getSeparatorChar(), columnName));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#beforeWorking(java.io.File)
	 */
	@Override
	protected void beforeWorking(File file) throws Exception {
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
				getCtrl().getCSVCharset()));

	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#addCell(int, int, net.sourceforge.squirrel_sql.fw.gui.action.exportData.IExportDataCell)
	 */
	@Override
	protected void addCell(IExportDataCell cell) throws Exception {
		String cellObjData = getDataCSV(getCtrl().getSeparatorChar(), cell, getCtrl().useGloablPrefsFormatting());
		this.aRow.add(cellObjData);

	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#afterHeader()
	 */
	@Override
	protected void afterHeader() throws Exception {
		writeARow(this.headerCells);
		super.afterHeader();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#beforeRow()
	 */
	@Override
	public void beforeRow(int rowIdx) throws Exception{
		super.beforeRow(rowIdx);
		this.aRow.clear();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#afterRow()
	 */
	@Override
	public void afterRow() throws Exception{
		writeARow(this.aRow);
		super.afterRow();
	}

	private void writeARow(List<String> data) throws IOException {
		Iterator<String> it = data.iterator();
		while (it.hasNext()) {
			bw.write(it.next());
			if (it.hasNext()) {
				bw.write(getCtrl().getSeparatorChar());
			}
		}
		bw.write(getCtrl().getLineSeparator());
	}
	
	
	
	
}
