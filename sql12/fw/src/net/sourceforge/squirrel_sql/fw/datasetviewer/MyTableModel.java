package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
 * Modifications copyright (C) 2001-2002 Johan Compagner
 * jcompagner@j-com.nl
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


import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import javax.swing.table.AbstractTableModel;
import javax.swing.JOptionPane;


public final class MyTableModel extends AbstractTableModel
{
	private List _data = new ArrayList();
	private ColumnDisplayDefinition[] _colDefs = new ColumnDisplayDefinition[0];
	private IDataSetTableControls _creator = null;


	MyTableModel(IDataSetTableControls creator)
	{
		super();
		_creator = creator;
	}

	/**
	 * Determine whether the cell is editable by asking the creator whether
	 * the table is editable or not
	 */
	public boolean isCellEditable(int row, int col)
	{
		return _creator.isColumnEditable(col, getValueAt(row, col));
	}

	public Object getValueAt(int row, int col)
	{
		return ((Object[])_data.get(row))[col];
	}

	public int getRowCount()
	{
		return _data.size();
	}

	public int getColumnCount()
	{
		return _colDefs != null ? _colDefs.length : 0;
	}

	public String getColumnName(int col)
	{
		return _colDefs != null ? _colDefs[col].getLabel() : super.getColumnName(col);
	}

	public Class getColumnClass(int col)
	{
		try
		{
			// if no columns defined, return a generic class
			// to avoid anything throwing an exception.
			if (_colDefs == null)
			{
				return Object.class;
			}
		
			return Class.forName(_colDefs[col].getClassName());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	void setHeadings(ColumnDisplayDefinition[] hdgs)
	{
		_colDefs = hdgs;
	}

	public void addRow(Object[] row)
	{
		_data.add(row);
	}

	void clear()
	{
		_data.clear();
	}

	public void allRowsAdded()
	{
		fireTableStructureChanged();
	}

	/**
	 * Let creator handle saving the data, if anything is to be done with it.
	 * If the creator succeeds in changing the underlying data,
	 * then update the JTable as well.
	 */
	public void setValueAt(Object newValue, int row, int col) {
		if ( _creator.changeUnderlyingValueAt(row, col, newValue, getValueAt(row, col)))
		{
			((Object[])_data.get(row))[col] = newValue;
		}
	}
	
	/**
	 * Delete a set of selected rows.
	 */
	public void deleteRows(int[] rows) {
		// The list of rows may be empty, in which case we just return.
		if (rows.length == 0)
			return;
		
		
		
		// We want to delete rows from the end of the table towards the beginning
		// of the table.  If we delete from the front to the back, that throws off
		// the indexes of the rows after each delete and we would have to compensate
		// for that.
		// Example: if we want to delete rows 2 and 4 and do it in that
		// order, then after deleteing row 2 the row that used to be row 4 is now
		// actually row 3, so we would have to subtract 1 from the index to get the
		// row to delete.
		// On the other hand, if we delete row 4 first, then the indexes of all
		// rows prior to that one have not been affected, so we can then delete
		// row 2 without any complications.
		
		// sort the indexes into ascending order (because that is the
		// only function easilly available from the Arrays class)
		Arrays.sort(rows);
		
		// The indexes are in ascending order, but we want to delete in
		// descending order (see previous comment), so run through the
		// list backwards.
		for (int i=rows.length - 1; i>=0; i--) {
			// delete the row from the table
			_data.remove(rows[i]);
		}

		// notify table that rows have changed
		// The deleted rows may not be contiguous in the actual data model
		// because the gui may be showing a version of the data sorted in
		// some other order, so we cannot use fireRowsDeleted.

		fireTableStructureChanged();
	}
}
