/*
 * Copyright (C) 2008 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddLookupTableDialog.i18n;

class AddLookupTableColumnTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 2291876910684420431L;

	private final ArrayList<String[]> _rowData = new ArrayList<String[]>();

	private final String[] _columnNames =
		{ i18n.LOOKUP_COLUMNSTABLE_HEADER1, i18n.LOOKUP_COLUMNSTABLE_HEADER2 };

	public String getColumnName(int col)
	{
		return _columnNames[col];
	}

	public int getRowCount()
	{
		return _rowData.size();
	}

	public int getColumnCount()
	{
		return _columnNames.length;
	}

	public Object getValueAt(int row, int col)
	{
		return _rowData.get(row)[col];
	}

	public boolean isCellEditable(int row, int col)
	{
		return col == 0;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		_rowData.get(rowIndex)[columnIndex] = (String) aValue;
	}

	public void addRow(String[] rowData)
	{
		_rowData.add(rowData);
		fireTableDataChanged();
	}

	public String[] deleteRow(int row)
	{
		String[] removedRow = _rowData.remove(row);
		fireTableDataChanged();
		return removedRow;
	}

	public ArrayList<String[]> getData()
	{
		return _rowData;
	}

	public void clear()
	{
		_rowData.clear();
	}
}