package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2002 Johan Compagner
 * jcompagner@j-com.nl
 *
 * Modifications copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
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
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SortableTableModel extends AbstractTableModel
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SortableTableModel.class);

	private MyTableModelListener _actualModelLis = new MyTableModelListener();

	/** Column currently being sorted by. -1 means unsorted. */
	protected int _iColumn = -1;

	protected boolean _bAscending;

	/** The autal model that this model is wrapped around. */
	private TableModel _actualModel;

	public TableModel getActualModel()
	{
		return _actualModel;
	}

	/**
	 * Contains the indexes within <TT>_actualModel</TT> after sorting. I.E.
	 * if after sorting <TT>_actualModel[1]</TT> should be the first line and
	 * <TT>_actualModel[0]</TT> should be the second line then <TT>_indexes</TT>
	 * will contain <TT>{1,0}</TT>.
	 */
	private Integer[] _indexes = new Integer[0];

	public SortableTableModel()
	{
		this(null);
	}

	public SortableTableModel(TableModel model)
	{
		super();
		setActualModel(model);
	}

	public void setActualModel(TableModel newModel)
	{
		if (_actualModel != null)
		{
			_actualModel.removeTableModelListener(_actualModelLis);
		}
		_actualModel = newModel;
		if (_actualModel != null)
		{
			_actualModel.addTableModelListener(_actualModelLis);
		}
	}

	/**
	 * Return the number of rows in this table.
	 *
	 * @return	Number of rows in this table.
	 */
	public int getRowCount()
	{
		return _actualModel != null ? _actualModel.getRowCount() : 0;
	}

	/**
	 * Return the number of columns in this table.
	 *
	 * @return	Number of columns in this table.
	 */
	public int getColumnCount()
	{
		return _actualModel != null ? _actualModel.getColumnCount() : 0;
	}

	/**
	 * Return the value at the specified row/column.
	 *
	 * @param	row		Row to return data for.
	 * @param	col		Column to return data for.
	 *
	 * @return	value at the specified row/column.
	 */
	public Object getValueAt(int row, int col)
	{
		return _actualModel.getValueAt(_indexes[row].intValue(), col);
	}

	/**
	 * Set the value at the specified row/column.
	 *
	 * @param	value	Value to place in cell.
	 * @param	row		Row to return data for.
	 * @param	col		Column to return data for.
	 *
	 * @return	value at the specified row/column.
	 */
	public void setValueAt(Object value, int row, int col)
	{
		_actualModel.setValueAt(value, _indexes[row].intValue(), col);
	}

	/*
	 * @see TableModel#getColumnName(int)
	 */
	public String getColumnName(int col)
	{
		return _actualModel.getColumnName(col);
	}

	/*
	 * @see TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int col)
	{
		return _actualModel.getColumnClass(col);
	}
	
	/**
	 * The actual model may or may not be editable, so return
	 * the value returned by the model when asked if this
	 * cell is editable.
	 *
	 * @param	row		Row to return data for.
	 * @param	col		Column to return data for.
	 *
	 * @return	value returned by actual model
	 */ 
	public boolean isCellEditable(int row, int col)
	{
		return _actualModel.isCellEditable(row,col);
	}

	/**
	 * Sorts the column specified in a mode depending if the that column
	 * was last sorted and then inverted that mode. If the column was not
	 * the previous sorted column then it will be sorted in ascending mode.
	 */
	public boolean sortByColumn(int column)
	{
		boolean b = true;
		if (column == _iColumn)
		{
			b = !_bAscending;
		}
		sortByColumn(column, b);
		return b;
	}

	/**
	 * Sorts the table by the specified column.
	 *
	 * @param	column		column to sort by
	 * @param	ascending	sort ascending if <TT>true</TT> else descending.
	 */
	public void sortByColumn(int column, boolean ascending)
	{
		_iColumn = column;
		_bAscending = ascending;
		TableModelComparator comparator =
			new TableModelComparator(column, ascending);
		// Should the data be first cloned so that the sorting doesn't take place
		// on the array that is used in getValue()
		// This is a must if sorting is done in a thread! ??
		Arrays.sort(_indexes, comparator);
		fireTableDataChanged();
	}

	class TableModelComparator implements Comparator
	{
		private int _iColumn;
		private int _iAscending;

		public TableModelComparator(int iColumn)
		{
			this(iColumn, true);
		}

		public TableModelComparator(int iColumn, boolean ascending)
		{
			_iColumn = iColumn;
			if (ascending)
			{
				_iAscending = 1;
			}
			else
			{
				_iAscending = -1;
			}
		}

		/*
		 * @see Comparator#compare(Object, Object)
		 */
		public int compare(Object o1, Object o2)
		{
			final Integer i1 = (Integer)o1;
			final Integer i2 = (Integer)o2;

			final Object data1 = _actualModel.getValueAt(i1.intValue(), _iColumn);
			final Object data2 = _actualModel.getValueAt(i2.intValue(), _iColumn);
			try
			{
				if (data1 == null && data2 == null)
				{
					return 0;
				}
				if (data1 == null)
				{
					return 1 * _iAscending;
				}
				if (data2 == null)
				{
					return -1 * _iAscending;
				}
				Comparable c1 = (Comparable)data1;
				return c1.compareTo(data2) * _iAscending;
			}
			catch (ClassCastException ex)
			{
				return data1.toString().compareTo(data2.toString()) * _iAscending;
			}
		}

	}

	protected class MyTableModelListener implements TableModelListener
	{
		public void tableChanged(TableModelEvent evt)
		{
			_indexes = new Integer[getRowCount()];
			for (int i = 0; i < _indexes.length; ++i)
			{
				_indexes[i] = new Integer(i);
			}
		}
	}
}
