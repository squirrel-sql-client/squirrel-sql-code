package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2002 Johan Compagner
 * jcompagner@j-com.nl
 *
 * Modifications copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import javax.swing.table.TableModel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * @version 	1.0
 * @author
 */
public class SortableTableModel extends AbstractTableModel
{
//	protected Object[][] _data;
//	protected int _iSize;
	
//	protected String[] _columns;
//	protected Class[] _cls;

//	protected boolean _bAscending;

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
/*
	public SortableTableModel()
	{
		this(new String[0],new Class[0]);
	}
	public SortableTableModel(String[] columns, Class[] cls)
	{
		if(columns == null || cls == null) throw new IllegalArgumentException("Column arrays can't be nulll");
		_data = new Object[10][];
		_columns = columns;
		_cls = cls;
		_iSize = 0;
	}

	public SortableTableModel(String[] columns, Class[] cls, Object[][] data)
	{
		if(columns == null || cls == null || data != null) throw new IllegalArgumentException("arrays can't be nulll");
		_columns = columns;
		_cls = cls;
		_data = data;
		_iSize = _data.length;
	}
*/
	public SortableTableModel() {
		this(null);
	}

	public SortableTableModel(TableModel model) {
		super();
		setActualModel(model);
	}

	public void setActualModel(TableModel newModel) {
		if (_actualModel != null) {
			_actualModel.removeTableModelListener(_actualModelLis);
		}
		_actualModel = newModel;
		if(_actualModel != null)
		{
			_actualModel.addTableModelListener(_actualModelLis);
		}
	}

//	public void setColumns(String[] names, Class[] classes)
//	{
//		_columns = names;
//		_cls = classes;
//		_iSize = 0;
//		fireTableStructureChanged();
//	}
	
//	public void addRow(Object[] row)
//	{
		// Check length must be equal or greater then columns length (can have hidden rows)
//		if(row.length < _columns.length) throw new IllegalArgumentException("Number of row length must be equal or greater then the columns");
		
//		try
//		{
//			_data[_iSize] = row;
//			_iSize++;
//		} catch(ArrayIndexOutOfBoundsException array)
//		{
//			Object[][] data = new Object[_data.length*2][];
//			System.arraycopy(_data, 0, data, 0, _data.length);
//			_data = data;
//			addRow(row);
//		}
//	}
	
	/**
	 * Return the number of rows in this table.
	 * 
	 * @return	Number of rows in this table.
	 */
	public int getRowCount() {
		return _actualModel != null ? _actualModel.getRowCount() : 0;
	}

	/**
	 * Return the number of columns in this table.
	 * 
	 * @return	Number of columns in this table.
	 */
	public int getColumnCount() {
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
	public Object getValueAt(int row, int col) {
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
	public void setValueAt(Object value, int row, int col) {
		_actualModel.setValueAt(value, _indexes[row].intValue(), col);
	}

	/*
	 * @see TableModel#getColumnName(int)
	 */
	public String getColumnName(int col) {
		return _actualModel.getColumnName(col);
	}

	/*
	 * @see TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int col) {
		return _actualModel.getColumnClass(col);
	}

	/**
	 * Sorts the column specified in a mode depending if the that column
	 * was last sorted and then inverted that mode. If the column was not
	 * the previous sorted column then it will be sorted in ascending mode.
	 */
	public boolean sortByColumn(int column)
	{
		boolean b = true;
		if(column == _iColumn)
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
		TableModelComparator comparator = new TableModelComparator(column, ascending);
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
			this(iColumn,true);
		}
		
		public TableModelComparator(int iColumn, boolean ascending)
		{
			_iColumn = iColumn;
			if(ascending)
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
			Integer i1 = (Integer)o1;
			Integer i2 = (Integer)o2;
			
			Comparable c1 = ((Comparable)_actualModel.getValueAt(i1.intValue(),_iColumn));
			Object c2 = _actualModel.getValueAt(i2.intValue(),_iColumn);
			
			if(c1 == null && c2 == null) return 0;
			if(c1 == null) return 1;
			if(c2 == null) return -1;
			return c1.compareTo(c2)*_iAscending;
		}

	}

	protected class MyTableModelListener implements TableModelListener {
		public void tableChanged(TableModelEvent evt) {
			_indexes = new Integer[getRowCount()];
			for (int i = 0; i < _indexes.length; ++i) {
				_indexes[i] = new Integer(i);
			}
		}
	}
}
