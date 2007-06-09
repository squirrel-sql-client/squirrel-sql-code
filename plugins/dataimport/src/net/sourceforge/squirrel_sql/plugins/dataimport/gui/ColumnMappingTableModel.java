package net.sourceforge.squirrel_sql.plugins.dataimport.gui;
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

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This is a table model for the column mapping table at the bottom 
 * of the file import dialog.
 * 
 * @author Thorsten Mürell
 */
public class ColumnMappingTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -7080889957246771971L;

	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(ColumnMappingTableModel.class);

	private TableColumnInfo[] columns = null;
	private Vector<String> mapping = new Vector<String>();
	private Vector<String> defaults = new Vector<String>();

	/**
	 * The default constructor.
	 * 
	 * @param columns These are the columns of the destination table
	 */
	public ColumnMappingTableModel(TableColumnInfo[] columns) {
		this.columns = columns;
		for (int i = 0; i < columns.length; i++) {
			mapping.add("Skip");
			defaults.add("");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 3;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return columns.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return columns[rowIndex].getColumnName();
		} else if (columnIndex == 1) {
			return mapping.get(rowIndex);
		} else if (columnIndex == 2) {
			return defaults.get(rowIndex);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 1 || columnIndex == 2) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (col == 1) {
			mapping.set(row, value.toString());
		} else if (col == 2) {
			defaults.set(row, value.toString());
		}
		fireTableCellUpdated(row, col);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			// i18n[ImportFileDialog.tableColumn=Table column]
			return stringMgr.getString("ImportFileDialog.tableColumn");
		} else if (column == 1) {
			// i18n[ImportFileDialog.importFileColumn=Import file column]
			return stringMgr.getString("ImportFileDialog.importFileColumn");
		} else if (column == 2) {
			// i18n[ImportFileDialog.fixedValue=Fixed value]
			return stringMgr.getString("ImportFileDialog.fixedValue");
		}
		return null;
	}

	/**
	 * Returns the index of the column with the given name.
	 * 
	 * @param columnName The column name
	 * @return The row of the given column
	 */
	public int findTableColumn(String columnName) {
		int i = 0;
		for (i = 0; i < columns.length; i++) {
			if (columnName.equals(columns[i].getColumnName()))
				return i;
		}
		return -1;
	}
}