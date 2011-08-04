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
import java.sql.Types;
import java.util.Calendar;

import jxl.Workbook;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.TableExportCsvController;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;

/**
 * Exports {@link IExportData} to a Excel file.
 * <b>Note:</b> This class is the result of a refactoring task. The code was taken from TableExportCsvCommand.
 * @author Stefan Willinger
 *
 */
public class DataExportExcelWriter extends AbstractDataExportFileWriter {
	private WritableWorkbook workbook;
	private WritableSheet sheet;
	private boolean withHeader = false;
	
	

	/**
	 * @param file
	 * @param ctrl
	 * @param includeHeaders
	 * @param progressController 
	 */
	public DataExportExcelWriter(File file, TableExportCsvController ctrl, boolean includeHeaders, ProgressAbortCallback progressController) {
		super(file, ctrl, includeHeaders, progressController);
	}

	private WritableCell getXlsCell(ColumnDisplayDefinition colDef, int colIdx, int curRow, Object cellObj) {
		if (null == cellObj) {
			return new jxl.write.Label(colIdx, curRow, getDataXLSAsString(cellObj));
		}

		if (null == colDef) {
			return new jxl.write.Label(colIdx, curRow, getDataXLSAsString(cellObj));
		}

		WritableCell ret;
		int colType = colDef.getSqlType();
		switch (colType) {
		case Types.BIT:
		case Types.BOOLEAN:
			ret = new jxl.write.Boolean(colIdx, curRow, (Boolean) cellObj);
			break;
		case Types.INTEGER:
			ret = new jxl.write.Number(colIdx, curRow, ((Number) cellObj).doubleValue());
			break;
		case Types.SMALLINT:
		case Types.TINYINT:
			ret = new jxl.write.Number(colIdx, curRow, ((Number) cellObj).doubleValue());
			break;
		case Types.DECIMAL:
			ret = new jxl.write.Number(colIdx, curRow, ((Number) cellObj).doubleValue());
			break;
		case Types.NUMERIC:
			ret = new jxl.write.Number(colIdx, curRow, ((Number) cellObj).doubleValue());
			break;
		case Types.FLOAT:
			ret = new jxl.write.Number(colIdx, curRow, ((Number) cellObj).doubleValue());
			break;
		case Types.DOUBLE:
			ret = new jxl.write.Number(colIdx, curRow, ((Number) cellObj).doubleValue());
			break;
		case Types.REAL:
			ret = new jxl.write.Number(colIdx, curRow, ((Number) cellObj).doubleValue());
			break;
		case Types.BIGINT:
			ret = new jxl.write.Number(colIdx, curRow, Long.parseLong(cellObj.toString()));
			break;
		case Types.DATE:
		case Types.TIMESTAMP:
		case Types.TIME:
			/* Work around some UTC and Daylight saving offsets */
			long time = (((java.util.Date) cellObj).getTime());

			Calendar cal = Calendar.getInstance();
			cal.setTime((java.util.Date) cellObj);

			int offset = (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET));

			long utcTime = time + offset;
			/*
			 * Work around Excel's problem with dates before 1900-03-01
			 * http://support.microsoft.com/kb/214058 -2203891200000l is
			 * 1900-03-01 UTC time 8640000 means 24 hours
			 */
			if (utcTime < -2203891200000l) {
				utcTime += 86400000;
			}

			java.util.Date xlsUTCDate = new java.util.Date(utcTime);
			ret = new jxl.write.DateTime(colIdx, curRow, xlsUTCDate, jxl.write.DateTime.GMT);
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			cellObj = CellComponentFactory.renderObject(cellObj, colDef);
			ret = new jxl.write.Label(colIdx, curRow, getDataXLSAsString(cellObj));
			break;
		default:
			cellObj = CellComponentFactory.renderObject(cellObj, colDef);
			ret = new jxl.write.Label(colIdx, curRow, getDataXLSAsString(cellObj));
		}
		return ret;
	}
	
	private String getDataXLSAsString(Object cellObj) {
		if (cellObj == null) {
			return "";
		} else {
			return cellObj.toString().trim();
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#beforeWorking()
	 */
	@Override
	protected void beforeWorking(File file) throws IOException{
		this.workbook = Workbook.createWorkbook(file);
		this.sheet = workbook.createSheet("Squirrel SQL Export", 0);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#addHeaderCell(int, java.lang.String)
	 */
	@Override
	protected void addHeaderCell(int colIdx, String columnName) throws Exception {
		this.withHeader = true;
		jxl.write.Label label = new jxl.write.Label(colIdx, 0, columnName);
		sheet.addCell(label);		
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#addCell(net.sourceforge.squirrel_sql.fw.gui.action.TableExportCsvController, int, int, net.sourceforge.squirrel_sql.fw.gui.action.exportData.IExportDataCell)
	 */
	@Override
	protected void addCell(IExportDataCell cell) throws Exception{
		WritableCell xlsCell;
		if (getCtrl().useGloablPrefsFormatting()) {
			xlsCell = getXlsCell(cell.getColumnDisplayDefinition(), cell.getColumnIndex(),
					calculateRowIdx(cell), cell.getObject());
		} else {
			xlsCell = getXlsCell(null, cell.getColumnIndex(), calculateRowIdx(cell), cell.getObject());
		}
		sheet.addCell(xlsCell);		
	}


	/**
	 * @param cell
	 * @return
	 */
	private int calculateRowIdx(IExportDataCell cell) {
		if(this.withHeader ){
			return cell.getRowIndex()+1;
		}else{
			return cell.getRowIndex();
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#afterWorking()
	 */
	@Override
	protected void afterWorking() throws Exception{
		workbook.write();
		workbook.close();		
	}

}
