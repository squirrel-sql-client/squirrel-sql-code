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

/**
 * This is represents a <TT>IDataSetViewerDestination</TT> that doesn't
 * actually display the data, it simply stores it for future use.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IDataSetModel extends IDataSetViewer
{
	/**
	 * Get the column definitions.
	 *
	 * @return	Column definitions.
	 */
	ColumnDisplayDefinition[] getColumnDefinitions();

	/**
	 * Return number of rows in model.
	 *
	 * @return	Number of rows.
	 */
	int getRowCount();

	/**
	 * Return the data value for the specified cell.
	 *
	 * @param	rowIndex	The row whose value is being retrieved.
	 * @param	columnIndex	The column whose value is being retrieved.
	 *
	 * @return	the data value for the specified cell.
	 */
	Object getValueAt(int rowIndex, int columnIndex);

	/**
	 * Set the data value for the specified cell.
	 *
	 * @param	value		The new value for the cell.
	 * @param	rowIndex	The row whose value is being set.
	 * @param	columnIndex	The column whose value is being set.
	 *
	 * @throws	IllegalStateException
	 *			if <TT>IDataSetModel</TT> hasn't been specified.
	 */
	void setValueAt(Object value, int rowIndex, int columnIndex);

	/**
	 * Adds a listener for events in this model.
	 *
	 * @param	lis		<TT>DataSetModelListener</TT> that will be
	 *					notified when events occur in this model.
	 */
	void addListener(IDataSetModelListener lis);

	/**
	 * Removes an event listener fromthis model.
	 *
	 * @param	lis		<TT>DataSetModelListener</TT> to be removed.
	 */
	void removeListener(IDataSetModelListener lis);
}
