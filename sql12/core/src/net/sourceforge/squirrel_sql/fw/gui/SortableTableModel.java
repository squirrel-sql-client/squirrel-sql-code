package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2002-2004 Johan Compagner
 * jcompagner@j-com.nl
 *
 * Modifications copyright (C) 2002-2004 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowNumberTableColumn;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SortableTableModel extends AbstractTableModel
{
   transient private MyTableModelListener _actualModelLis = new MyTableModelListener();

	/** Column currently being sorted by. -1 means unsorted. */
	private int _sortedColumn = -1;

	private ColumnOrder _columnOrder = ColumnOrder.NATURAL;

	/** The actual model that this model is wrapped around. */
	private TableModel _actualModel;
   private ArrayList<SortingListener> _sortingListeners = new ArrayList<SortingListener>();

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

	public SortableTableModel(TableModel model)
	{
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
		tableChangedIntern();
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
		if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == col)
		{
			return Integer.valueOf(row + 1);
		}
		else
		{
			if (row < _indexes.length)
			{
				return _actualModel.getValueAt(_indexes[row].intValue(), col);
			}
			else
			{
				return null;
			}
		}
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
	public Class<?> getColumnClass(int col)
	{
		return _actualModel.getColumnClass(col);
	}

	/**
	 * Delete the selected rows in the actual table.
	 *
	 * @param	rows[]	List of row indexes in sorted model
	 */
	public void deleteRows(int[] rows)
	{
		int[] actualRows = new int[rows.length];
		for (int i=0; i< rows.length; ++i)
		{
            if (rows[i] < _indexes.length) {
                actualRows[i] = _indexes[rows[i]].intValue();
            }
		}
		((DataSetViewerTableModel)_actualModel).deleteRows(actualRows);
	}

	/**
	 * Insert a new row into the table.
	 */
	public void insertRow(Object[] values)
	{
		// first attempt to add data to underlying table model
		((DataSetViewerTableModel)_actualModel).addRow(values);

		// tell the rest of the world that the table has changed.
		// The 'fire' method used here is very course - it says that the whole table
		// has been changed when really only one row has been added.
		// However, finer-grained methods did not seem to cause the right
		// effect, so I'm using this one untill someone reports a problem with it.
		// Also, if either of these notifications (the actual model and the sortable
		// model) are eliminated, it either throws an exception or does not update
		// the GUI.  Go figure.
		// Finally, the 'fire' on the _acutalModel is triggered from this method
		// rather than from inside the MyJTable code because the add() method used
		// to add a row is also used when loading the table with lots of rows, and
		// in that case we do not want to generate events until all of the rows
		// have been added, so the 'fire' cannot happen there.
		((DataSetViewerTableModel)_actualModel).fireTableChanged(new TableModelEvent(_actualModel));
		fireTableChanged(new TableModelEvent(this));
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
	public ColumnOrder sortByColumn(int column)
	{
		ColumnOrder newOrder = ColumnOrder.ASC;
		if (column == _sortedColumn)
		{
			newOrder = _columnOrder.next();
		}
		sortByColumn(column, newOrder);
		return newOrder;
	}

	/**
	 * Sorts the table by the specified column.
	 *
	 * @param	column		column to sort by
	 * @param	ascending	sort ascending if <TT>true</TT> else descending.
	 */
	public void sortByColumn(int column, ColumnOrder newOrder)
	{
		_sortedColumn = column;
		_columnOrder = newOrder;
      if (ColumnOrder.NATURAL != newOrder)
      {
         TableModelComparator comparator = new TableModelComparator(column, newOrder);
         // Should the data be first cloned so that the sorting doesn't take place
         // on the array that is used in getValue()
         Arrays.sort(_indexes, comparator);
      }
      else
      {
         for (int i = 0; i < _indexes.length; i++)
         {
            _indexes[i] = i;
         }
      }
      fireTableDataChanged();
      fireSortingListeners(column, _columnOrder);
	}

   private void fireSortingListeners(int column, ColumnOrder columnOrder)
   {
      SortingListener[] listeners = _sortingListeners.toArray(new SortingListener[_sortingListeners.size()]);

      for (SortingListener listener : listeners)
      {
         listener.sortingDone(column, columnOrder);
      }
   }

   public ColumnOrder getColumnOrder()
	{
		return _columnOrder;
	}

	public void tableChanged()
	{
      tableChangedIntern();

      if(-1 != _sortedColumn)
      {
         sortByColumn(_sortedColumn, _columnOrder);
      }
      else
      {
         fireTableDataChanged();
      }
   }

	private void tableChangedIntern()
	{
		_indexes = new Integer[getRowCount()];
		for (int i = 0; i < _indexes.length; ++i)
		{
			_indexes[i] = Integer.valueOf(i);
		}
   }

	/**
	 * When the table is sorted table methods like getSelectedRow() return row indices that
	 * correspond to the view not to the model. This method transforms the view index to
	 * the model index.
	 * @param viewRow The view row index.
	 * @return The model row index. -1 if no model index corresponding to row was found.
	 */
	public int transformToModelRow(int viewRow)
	{
		if(0 > viewRow || viewRow >= _indexes.length)
		{
			return -1;
		}

		return _indexes[viewRow].intValue();
	}

	/**
	 * When the table is sorted table methods like getSelectedRow() return row indices that
	 * correspond to the view not to the model. This method transforms the model index to
	 * the view index.
	 *
	 * Note: This method is more expensive than {@link #transformToModelRow(int)}
	 *
	 * @param modelRow The model row index.
	 * @return The view row index. -1 if no view index corresponding to row was found.
	 */
	public int transformToViewRow(int modelRow)
	{
		Integer viewRowInteger = Integer.valueOf(modelRow);

		for (int i = 0; i < _indexes.length; i++)
		{
			if(viewRowInteger.equals(_indexes[i]))
			{
				return i;
			}
		}

		return -1;
	}

   public void removeSortingListener(SortingListener sortingListener)
   {
      _sortingListeners.remove(sortingListener);
   }

   public void addSortingListener(SortingListener sortingListener)
   {
      _sortingListeners.add(sortingListener);
   }


   class TableModelComparator implements Comparator<Integer>
	{
      private int _iColumn;
      private int _iAscending;
      private final SquirrelTableCellValueCollator _collator = new SquirrelTableCellValueCollator();
      private boolean _allDataIsString = true;

      public TableModelComparator(int iColumn, ColumnOrder compColumnOrder)
		{
			_iColumn = iColumn;
         if (ColumnOrder.ASC == compColumnOrder)
			{
				_iAscending = 1;
			}
			else if (ColumnOrder.DESC == compColumnOrder)
			{
				_iAscending = -1;
			}

			 for (int i = 0, limit = _actualModel.getRowCount(); i < limit; ++i)
			 {
				 final Object data = _actualModel.getValueAt(i, _iColumn);
				 if (!(data instanceof String))
				 {
					 _allDataIsString = false;
					 break;
				 }
			 }
		}

		/*
		 * @see Comparator#compare(Object, Object)
		 */
		public int compare(final Integer i1, final Integer i2)
		{
			final Object data1 = _actualModel.getValueAt(i1.intValue(), _iColumn);
			final Object data2 = _actualModel.getValueAt(i2.intValue(), _iColumn);
			return _collator.compareTableCellValues(data1, data2, _iAscending, _allDataIsString);
		}
	}

	protected class MyTableModelListener implements TableModelListener
	{
		public void tableChanged(TableModelEvent evt)
		{
			SortableTableModel.this.tableChangedIntern();
		}
	}


   public int getSortedColumn()
   {
      return _sortedColumn;
   }
}
