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
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Types;
import java.util.Calendar;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.TableExportCsvController;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


/**
 * Exports {@link IExportData} to a Excel file.
 * <b>Note:</b> This class is the result of a refactoring task. The code was taken from TableExportCsvCommand.
 * @author Stefan Willinger
 *
 */
public class DataExportExcelWriter extends AbstractDataExportFileWriter {
    private SXSSFWorkbook workbook; // The streaming api export for (very) large xlsx files
    private Sheet   sheet; // We write the data to this sheet
    private File    file; // File where the export is written to
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

	private Cell getXlsCell(ColumnDisplayDefinition colDef, int colIdx, int curRow, Object cellObj) {
                Row row= sheet.getRow(curRow);
                if (row == null)
                {
                    row= sheet.createRow(curRow);
                }
                Cell retVal= row.createCell(colIdx);

		if (null == cellObj) {
			retVal.setCellValue(getDataXLSAsString(cellObj));
		} else if (null == colDef) {
			retVal.setCellValue(getDataXLSAsString(cellObj));
		}

		int colType = colDef.getSqlType();
		switch (colType) {
		case Types.BIT:
		case Types.BOOLEAN:
			retVal.setCellValue((Boolean) cellObj);
			break;
		case Types.INTEGER:
			retVal.setCellValue(((Number) cellObj).doubleValue());
			break;
		case Types.SMALLINT:
		case Types.TINYINT:
			retVal.setCellValue(((Number) cellObj).doubleValue());
			break;
		case Types.DECIMAL:
			retVal.setCellValue(((Number) cellObj).doubleValue());
			break;
		case Types.NUMERIC:
			retVal.setCellValue(((Number) cellObj).doubleValue());
			break;
		case Types.FLOAT:
			retVal.setCellValue(((Number) cellObj).doubleValue());
			break;
		case Types.DOUBLE:
			retVal.setCellValue(((Number) cellObj).doubleValue());
			break;
		case Types.REAL:
			retVal.setCellValue(((Number) cellObj).doubleValue());
			break;
		case Types.BIGINT:
			retVal.setCellValue(Long.parseLong(cellObj.toString()));
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
			retVal.setCellValue(xlsUTCDate);
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			cellObj = CellComponentFactory.renderObject(cellObj, colDef);
			retVal.setCellValue(getDataXLSAsString(cellObj));
			break;
		default:
			cellObj = CellComponentFactory.renderObject(cellObj, colDef);
			retVal.setCellValue(getDataXLSAsString(cellObj));
		}
		return retVal;
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
		this.workbook = new SXSSFWorkbook(100); // keep 100 rows in memory, exceeding rows will be flushed to disk
                this.file= file;
		this.sheet = workbook.createSheet("Squirrel SQL Export");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#addHeaderCell(int, java.lang.String)
	 */
	@Override
	protected void addHeaderCell(int colIdx, String columnName) throws Exception {
		this.withHeader = true;
                Row headerRow= sheet.getRow(0);
                if (headerRow == null)
                {
                    headerRow= sheet.createRow(0);
                }
                Cell cell= headerRow.createCell(colIdx);
                cell.setCellValue(columnName);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#addCell(net.sourceforge.squirrel_sql.fw.gui.action.TableExportCsvController, int, int, net.sourceforge.squirrel_sql.fw.gui.action.exportData.IExportDataCell)
	 */
	@Override
	protected void addCell(IExportDataCell cell) throws Exception{
		Cell xlsCell;
		if (getCtrl().useGloablPrefsFormatting()) {
			xlsCell = getXlsCell(cell.getColumnDisplayDefinition(), cell.getColumnIndex(),
					calculateRowIdx(cell), cell.getObject());
		} else {
			xlsCell = getXlsCell(null, cell.getColumnIndex(), calculateRowIdx(cell), cell.getObject());
		}
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
            FileOutputStream out = new FileOutputStream(this.file);
            this.workbook.write(out);
            out.close();

            // dispose of temporary files backing this workbook on disk
            this.workbook.dispose();            
	}

}
