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
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.util.EventObject;

//??????????????????????????????????????????????????????????? for testing only!!! ?????????
import java.awt.Color;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
	public void init(IDataSetUpdateableModel updateableModel)
	{
		super.init(updateableModel);
		setUpdateableModelReference(updateableModel);
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Override the functions that need to be changed to tell the table
	// mechanisms how to do editing.
	//
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * Tell the table that it is editable.  This is called from within
	 * MyTable.isCellEditable().  Certain column data types may not be editable.
	 */
	public  boolean isTableEditable()
	{
		return true;
	}
	
	/**
	 * Tell the table whether a particular column may be edited or not
	 * based on whether the class associated with that column is known
	 * or not known, where "not known" is signaled by Object.class.
	 */
	public boolean isColumnEditable(int col)
	{
		if (_colDefs == null)
			return false;	// cannot edit something that we do not know anything about

		// Cannot edit the rowID column, if present
		if ( ((IDataSetUpdateableTableModel)getUpdateableModel()).getRowidCol() == col)
			return false;

		if (_colDefs[col].getClassName().equals("java.lang.Object"))
			return false;
		
		// the class is not the "unknown" class, so it must be editable
		return true;
	}
	
	/**
	 * Function to set up CellEditors for each of the data types
	 * to be handled in this table
	 */
	public void setCellEditors(JTable table)
	{
//??JTextField tf = new JTextField(20);
//??tf.setBackground(Color.GREEN);
//??		table.setDefaultEditor(String.class, new CutAndPasteCellEditor(tf));
//??JTextField tf1 = new JTextField(20);
//??tf1.setBackground(Color.YELLOW);
//??		table.setDefaultEditor(Integer.class, new CutAndPasteCellEditor(tf1));

		// This shouldn't be necessary, but without setting this editor
		// we do not see what we are doing when editing integers
		table.setDefaultEditor(Integer.class, new DefaultCellEditor(new JTextField()));
//??? other data types
	}
	
	/**
	 * Call the underlaying object to update the data represented by the JTable.
	 */
	public boolean changeUnderlyingValueAt(int row, int col, Object newValue, Object oldValue)
	{
		// if there is no updateable model, then we cannot update anything
		// (should never happen - just being safe here)
		if (getUpdateableModelReference() == null)
			return false;	// no underlying data, so cannot be changed

		// call the function in the app code that checks for unexpected
		// conditions in the current DB
		String message = null;
		if (getUpdateableModelReference() != null)
			message = ((IDataSetUpdateableTableModel)getUpdateableModelReference()).
				getWarningOnCurrentData(getRow(row), _colDefs, col, oldValue);

		if (message != null) {
			// set up dialog to ask user if it is ok to proceed
			// IMPORTANT: this dialog is SYNCHRONOUS (ie. we do not proceed until
			// user gives a response).  This is critical since this function provides
			// a return value to its caller that depends on the user input.
			int option = JOptionPane.showConfirmDialog(null, message, "Warning",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if ( option != JOptionPane.YES_OPTION)
			{
				return false;	// no update done to underlying data
			}
		}

		// call the function in the app code that checks for unexpected
		// conditions in the current DB
		if (getUpdateableModelReference() != null)
			message = ((IDataSetUpdateableTableModel)getUpdateableModelReference()).
				getWarningOnProjectedUpdate(getRow(row), _colDefs, col, newValue);

		if (message != null) {
			// set up dialog to ask user if it is ok to proceed
			// IMPORTANT: this dialog is SYNCHRONOUS (ie. we do not proceed until
			// user gives a response).  This is critical since this function provides
			// a return value to its caller that depends on the user input.
			int option = JOptionPane.showConfirmDialog(null, message, "Warning",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if ( option != JOptionPane.YES_OPTION)
			{
				return false;	// no update done to underlying data
			}
		}

		// call the function in the app code that saves the data in the
		// persistant storage (e.g. a database).
		// The success or failure of that function (as indicated by the absance or
		// presence of a result errpor/warning message) determines the result of this call.
		// (Since the table is supposed to be editable, we should have an
		// IDataSetUpdateableTableModel object set in our super class.)

		message = ((IDataSetUpdateableTableModel)getUpdateableModelReference()).
			updateTableComponent(getRow(row), _colDefs, col, oldValue, newValue);

		if (message != null) {
			// tell user that there was a problem
			JOptionPane.showMessageDialog(null, message, "Error",
				JOptionPane.ERROR_MESSAGE);

			// tell caller that the underlying data was not updated
			//?? is this always true, or could the data be updated with a warning?
			return false;
		}
		
		// no problems, so indicate a successful update of the underlying data
		return true;
	}
	
	//?? Other functions??


}
