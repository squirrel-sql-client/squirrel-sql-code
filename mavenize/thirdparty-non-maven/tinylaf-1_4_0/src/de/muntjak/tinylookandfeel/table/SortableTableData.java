/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.table;

import javax.swing.JTable;


/**
 * <code>TableModel</code>s which want to make use of TinyLaF's table
 * headers for sortable table data must implement this interface.
 * <p>
 * See the example implementation in de.muntjak.tinylaf.controlpanel.TinyTableModel.
 * 
 * @author Hans Bickel
 *
 */
public interface SortableTableData {

	/** value of <code>sortingDirection</code> property: column data sorted in ascending order */
	static final int SORT_ASCENDING 	= 1;
	
	/** value of <code>sortingDirection</code> property: column data sorted in descending order */
	static final int SORT_DESCENDING 	= 2;
	
	/**
	 * Return <code>true</code> if the specified column
	 * is sortable, <code>false</code> otherwise. Non-sortable
	 * column headers will not react to mouse clicks or rollovers.
	 * @param column a column index
	 * @return <code>true</code> if the specified column
	 * is sortable, <code>false</code> otherwise
	 */
	boolean isColumnSortable(int column);
	
	/**
	 * Return <code>true</code> if the table model supports
	 * multiple sorted columns, <code>false</code> otherwise.
	 * (Supporting multi column sort makes sense only with columns
	 * containing some equal values.)
	 * <p>
	 * The TinyLaF user gestures concerning multi column sort are:
	 * <ul>
	 * <li><code>Ctrl-Click</code>: If clicked column was already sorted,
	 * change sorting direction, else add clicked column to sorted columns.
	 * <li><code>Alt-Click</code>: If clicked column was already sorted,
	 * remove from sorted columns. Ignore if clicked column was not sorted.
	 * <li><code>Click</code> (without <code>Ctrl</code> or <code>Alt</code>):
	 * The clicked column becomes the only sorted column. If the clicked column
	 * already was sorted, change sorting direction.
	 * </ul>
	 * 
	 * @return <code>true</code> if the table model supports
	 * multiple sorted columns, <code>false</code> otherwise
	 */
	boolean supportsMultiColumnSort();

	/**
	 * Sort the data according to the given arguments. If argument arrays
	 * are empty, the original state of the data will be restored, if there
	 * is no original state then no action will be performed.
	 * <p>
	 * Note for implementors: If your data is dynamically changing you
	 * should think about storing copies of the arguments so
	 * you can re-sort data after each change. It may also be
	 * a good idea to call <code>fireTableDataChanged()</code> after sorting
	 * (this makes sure that the table is updated).
	 * @param columns array of column indices sorted by priority
	 * (highest priority first)
	 * @param sortingDirections array containing the sorting direction
	 * for each sorted column. Values are either
	 * <ul>
	 * <li><code>SORT_ASCENDING</code> - sort column data in ascending order, or
	 * <li><code>SORT_DESCENDING</code> - sort column data in descending order
	 * </ul>
	 * @param table the table displaying the data. Might be useful, for example,
	 * to restore selected cells after sorting.
	 */
	void sortColumns(int[] columns, int[] sortingDirections, JTable table);
}
