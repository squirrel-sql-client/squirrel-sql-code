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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;

/**
 * The implementation of {@link IExportData} for exporting a {@link JTable}.
 * This class encapsulate the access to the model of a JTable. 
 * Its possible, to handle the full table, or only the selected part of it. 
 * <p>
 * <b>Note:</b> This class is the result of a refactoring task. The code was taken from TableExportCsvCommand.
 * @author Stefan Willinger
 * 
 */
public class JTableExportData implements IExportData {

	private JTable table;

	int nbrSelRows;
	int nbrSelCols;
	int[] selRows;
	int[] selCols;

	/**
	 * Constructor using a JTable.
	 * @param table the JTable to use.
	 * @param complete flag, if the complete table or only the selection should be used.
	 * 
	 */
	public JTableExportData(JTable table, boolean complete) {
		this.table = table;

		nbrSelRows = table.getSelectedRowCount();
		if (0 == nbrSelRows || complete) {
			nbrSelRows = table.getRowCount();
		}

		nbrSelCols = table.getSelectedColumnCount();
		if (0 == nbrSelCols || complete) {
			nbrSelCols = table.getColumnCount();
		}

		selRows = table.getSelectedRows();
		if (0 == selRows.length || complete) {
			selRows = new int[nbrSelRows];
			for (int i = 0; i < selRows.length; i++) {
				selRows[i] = i;
			}
		}

		selCols = table.getSelectedColumns();
		if (0 == selCols.length || complete) {
			selCols = new int[nbrSelCols];
			for (int i = 0; i < selCols.length; i++) {
				selCols[i] = i;
			}
		}

	}

	/**
	 * Reads the header of the table.
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.IExportData#getHeaders()
	 */
	@Override
	public Iterator<String> getHeaders() {
		List<String> headers = new ArrayList<String>();
		for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx) {
			String columnName = table.getColumnName(selCols[colIdx]);
			headers.add(columnName);
		}
		return headers.iterator();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.IExportData#getRows(boolean)
	 */
	@Override
	public Iterator<IExportDataRow> getRows() {
		List<IExportDataRow> rows = new ArrayList<IExportDataRow>(nbrSelRows);
		for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx) {
			List<IExportDataCell> cells = new ArrayList<IExportDataCell>(nbrSelCols);
			for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx) {
				ExportDataColumn cellObj;
				
				final Object obj = table.getValueAt(selRows[rowIdx], selCols[colIdx]);
				
				if (table.getColumnModel().getColumn(colIdx) instanceof ExtTableColumn) {
					ExtTableColumn col = (ExtTableColumn) table.getColumnModel().getColumn(colIdx);
					cellObj = new ExportDataColumn(col.getColumnDisplayDefinition(), obj, rowIdx, colIdx);
				} else {
					cellObj = new ExportDataColumn(null, obj, rowIdx, colIdx);
				}
				cells.add(cellObj);
			}
			rows.add(new ExportDataRow(cells, rowIdx));
		}
		return rows.iterator();
	}

}
