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
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import java.util.EventObject;

/**
 * @author gwg
 *
 * Table view that allows editing of the data.
 */
public class DataSetViewerEditableTablePanel extends DataSetViewerTablePanel
{
	/**
	 * Internal definitions
	 */
//?? Colin: Ignore this internal class for now...
	public class  CutAndPasteCellEditor extends DefaultCellEditor
	{
		public CutAndPasteCellEditor (JTextField textField)
		{
			super(textField);
		};
		public boolean isCellEditable(EventObject event) {return false;};
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Override the functions that need to be changed to tell the table
	// mechanisms how to do editing.
	//
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * Tell the table that it is editable.  This is called from within
	 * MyTable.isCellEditable().  We do not bother to distinguish between
	 * editable and non-editable cells within the same table since all cells
	 * are supposed to be either editable or not editable.
	 */
	public  boolean isTableEditable()
	{
		return true;
	}
	
	/**
	 * Function to set up CellEditors for each of the data types
	 * to be handled in this table
	 */
	public void setCellEditors(JTable table)
	{
//?? Colin: This function is not ready for prime time...
		table.setDefaultEditor(String.class, new CutAndPasteCellEditor(new JTextField("Not Implemented")));
	}
	
	/**
	 * Call the underlaying object to update the data represented by the JTable.
	 */
	public boolean changeUnderlyingValueAt(int row, int col, Object newValue, Object oldValue)
	{
		// call the function in the app code that saves the data in the
		// persistant storage (e.g. a database).
		// The success or failure of that function determines the result of this call.
		// (Since the table is supposed to be editable, we should have an
		// IDataSetUpdateableTableModel object set in our super class.)
		if (getUpdateableModelReference() != null)
			return ((IDataSetUpdateableTableModel)getUpdateableModelReference()).updateTableComponent(row, col, newValue, oldValue);
		return false;	// no updateable model means data is not updateable
    }
	
	//?? Other functions??


}
