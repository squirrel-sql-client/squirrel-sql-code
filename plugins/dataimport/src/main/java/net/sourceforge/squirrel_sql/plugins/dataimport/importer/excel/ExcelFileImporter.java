package net.sourceforge.squirrel_sql.plugins.dataimport.importer.excel;
/*
 * Copyright (C) 2007 Thorsten Mürell
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.JComponent;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.UnsupportedFormatException;

/**
 * This implementation of the <code>IFileImporter</code> interface is to 
 * import Microsoft Excel files.
 * 
 * @author Thorsten Mürell
 */
public class ExcelFileImporter implements IFileImporter {

	private File importFile = null;
	private int pointer = -1;
	private int size = 0;
	private Workbook workbook = null;
	private Sheet sheet = null;
	private ExcelSettingsBean settings = null;
	
	/**
	 * The standard constructor
	 * 
	 * @param importFile The import file
	 */
	public ExcelFileImporter(File importFile) {
		this.importFile = importFile;
		this.settings = new ExcelSettingsBean();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#open()
	 */
	public boolean open() throws IOException {
		try {
			workbook = Workbook.getWorkbook(importFile);
		} catch (BiffException be) {
			throw new IOException(be.toString());
		}
		reset();
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#close()
	 */
	public boolean close() throws IOException {
		workbook.close();
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getPreview(int)
	 */
	public String[][] getPreview(int noOfLines) throws IOException {
		String[][] data = null;
		Workbook wb = null;
		Sheet sht = null; 
		try {
			wb = Workbook.getWorkbook(importFile);
			sht = getSheet(wb);
		} catch (BiffException be) {
			throw new IOException(be.toString());
		}

		int y = 0;
		int x = 0;
		int maxLines = (noOfLines < sht.getRows()) ? noOfLines : sht.getRows();
		data = new String[maxLines][sht.getColumns()];

		for (y = 0; y < maxLines; y++) {
			for (x = 0; x < sht.getColumns(); x++) {
				data[y][x] = sht.getCell(x, y).getContents();
			}
		}
		wb.close();
		
		return data;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#reset()
	 */
	public boolean reset() throws IOException {
		sheet = getSheet(workbook);
		size = sheet.getRows();
		pointer = -1;
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getRows()
	 */
	public int getRows() {
		return size;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#next()
	 */
	public boolean next() throws IOException {
		if (pointer >= size - 1) {
			return false;
		}
		pointer++;
		return true;
	}

	private void checkPointer() throws IOException {
		if (pointer < 0)
			throw new IOException("Use next() to get to the first record.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getString(int)
	 */
	public String getString(int column) throws IOException {
		checkPointer();
		return sheet.getCell(column, pointer).getContents();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getInt(int)
	 */
	public Integer getInt(int column) throws IOException, UnsupportedFormatException {
		checkPointer();
		Cell cell = sheet.getCell(column, pointer);
		if (cell.getType() != CellType.NUMBER) {
			throw new UnsupportedFormatException();
		}
		return (new Double(((NumberCell) cell).getValue())).intValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getDate(int)
	 */
	public Date getDate(int column) throws IOException, UnsupportedFormatException {
		checkPointer();
		Cell cell = sheet.getCell(column, pointer);
		if (cell.getType() != CellType.DATE) {
			throw new UnsupportedFormatException();
		}
		return ((DateCell) cell).getDate(); 
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getLong(int)
	 */
	public Long getLong(int column) throws IOException, UnsupportedFormatException {
		checkPointer();
		Cell cell = sheet.getCell(column, pointer);
		if (cell.getType() != CellType.NUMBER) {
			throw new UnsupportedFormatException();
		}
		return (new Double(((NumberCell) cell).getValue())).longValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getConfigurationPanel()
	 */
	public JComponent getConfigurationPanel() {
		return new ExcelSettingsPanel(settings, importFile);
	}
	
	private Sheet getSheet(Workbook wb) {
		Sheet s = null;
		if (settings.getSheetName() != null) {
			s = wb.getSheet(settings.getSheetName());
		}
		if (s == null) {
			s = wb.getSheet(0);
		}
		return s;
	}
}
