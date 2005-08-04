package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
 
import javax.swing.JTable;
/**
 * @author gwg
 *
 * Controls on how the JTable and concrete AbstractTableModel work
 * for a particular instance.
 * When we create the JTable and its associated (concrete) AbstractTableModel
 * objects, we do so within wrapper functions that define how the various
 * features of those objects operate.  This is currently done in MyJTable and
 * MyTable within DataSetViewerTablePanel.  However, since some of the tables
 * are read-only and others are editable, there are some aspects of the table
 * operation that must be defined by the caller/creator of the table rather than
 * within the MyJTable and MyTable wrapper functions.  This means that the
 * wrapper functions must be able to make a callback to the creator object
 * so that the creator handle those operations.  We put the actual work into the
 * creator/caller object rather than MyJTable and MyTable.
 */
public interface IDataSetTableControls
{
	/**
	 * Tell the table that it is editable.  At this level we just want to know
	 * whether the table as a whole may be edited, so we do not distinguish
	 * between different columns in the table.
	 */
	public boolean isTableEditable();

	/**
	 * See if a particular column is editable.
	 */
	public boolean isColumnEditable(int col, Object originalValue);
	
	/**
	 * See if a value in a column has been limited in some way and
	 * needs to be re-read before being used for editing.
	 * For read-only tables this may actually return true since we want
	 * to be able to view the entire contents of the cell even if it was not
	 * completely loaded during the initial table setup.
	 */
	public boolean needToReRead(int col, Object originalValue);
	
	/**
	 * Re-read the contents of this cell from the database.
	 * If there is a problem, the message will have a non-zero length after return.
	 */
	public Object reReadDatum(Object[] values, int col, StringBuffer message);
	
	/**
	 * Set up the CellEditors for the various types of data.
	 */
	public void setCellEditors(JTable table);
	
	/**
	 * Call the object that represents the underlaying data model
	 * to change the actual data (e.g. in a database).
    *
    * @return the column indexes to update with the new value
	 */
	public int[] changeUnderlyingValueAt(
		int rowIndex,
		int columnIndex,
		Object newValue,
		Object oldValue);
	
	/**
	 * Delete a set of rows from the table.
	 * The indexes are the row indexes in the SortableModel.
	 */
	public void deleteRows(int[] rows);
	
	/**
	 * Initiate operations to insert a new row into the table.
	 */
	public void insertRow();
}
