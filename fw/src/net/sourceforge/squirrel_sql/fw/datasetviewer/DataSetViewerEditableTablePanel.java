package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
//??????????????????????????????????????????????????????????? for testing only!!! ?????????
import java.awt.Color;
import java.awt.Component;
import java.sql.Types;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.*;
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

		return CellComponentFactory.isEditableInCell(_colDefs[col]);
	}
	
	/**
	 * Function to set up CellEditors for each of the data types
	 * to be handled in this table. Since different columns have different
	 * parameters (e.g. nullable or not nullable) we set the cell editors on the columns
	 * rather than on the table as a whole.
	 */
	public void setCellEditors(JTable table)
	{
		// we need to table column model to be able to add CellEditors to the
		// individual columns
		TableColumnModel columnModel = table.getColumnModel();
		
		for (int i=0; i < _colDefs.length; i++) {
			// use factory to get the appropriate editor
			columnModel.getColumn(i).setCellEditor(
				CellComponentFactory.getInCellEditor(table, _colDefs[i]));
		}
	}
	
	/**
	 * Call the underlaying object to update the data represented by the JTable.
	 * Both the old and the new value are objects of the appropriate
	 * Data Type for the column.  The newValue has been validated as part of
	 * the conversion from the external user representation (a String) into the
	 * internal object.
	 */
	public boolean changeUnderlyingValueAt(
		int row,
		int col,
		Object newValue,
		Object oldValue)
	{
		String message = null;

		//
		// Special case: When we have to force the string representation of the data in
		// the JTextField to "<null>", the oldValue may be passed as that string rather
		// than as the appropriate object.  If so, convert it here.
		if (oldValue instanceof java.lang.String) {
			if (oldValue.toString().equals("<null>"))
				oldValue = null;
		}

/*	
		// Validate and Convert the newValue to an object
		//
		// This operation is a bit different than the others in this function in that
		// it is working on the data entered by the user rather than dealing with
		// the DB.  There are a couple of other places that this could be done,
		// so doing it here is a matter of style/taste.  It could be done:
		//	- in the CellEditor Component (e.g. DataTypeInteger) when focus is lost,
		//		but it is not clear that the sequence of operations will be correct
		//		and how the value would get passed as an object from the Component
		//		through to this function
		//	- in setValueAt() in DataSetViewerTablePanel, but that class does not
		//		currently know anything about CellEditors and their components, and
		//		there does not seem to be a good reason to put that knowledge there.
		// The main reason for including this here is that it is similar to the other
		// operations here in that it puts up a message to the user if there is a problem,
		// so that test-and-display mechanism is the same throughout this function.
		StringBuffer messageBuffer = new StringBuffer();

		Object newValue = CellComponentFactory.validateAndConvert(
			_colDefs[col], (String)newValueString, messageBuffer);
		if (messageBuffer.length() > 0) {
			// there was a problem in the validate/conversion
			// tell user and do not update data
			JOptionPane.showMessageDialog(null, 
				"Error in validation:\n" + messageBuffer.toString() + "\nData not updated",
				"Error",
				JOptionPane.ERROR_MESSAGE);
				
			return false;
		}
*/

		// At this point the user input has been validated and both the
		// new and old values are objects of the appropriate data type.
		// Either or both of newValue and oldValue may be null.

		// if there is no updateable model, then we cannot update anything
		// (should never happen - just being safe here)
		if (getUpdateableModelReference() == null)
			return false;	// no underlying data, so cannot be changed

		// check to see if new data is same as old data, in which case we
		// do not update the underlying data.
		//
		// This is NOT an optimization (though it does
		// speed things up).  We need to do this to avoid an error when we check for
		// rows being changed in the DB.  If the new value and old value are the same,
		// when we look to see if any rows already exist with the new value, it will find
		// the existing row and claim that the update will make one row identical to the
		// changed row (i.e. that there will be two identical rows in the DB) which is
		// not true.  So we avoid the problem by not updating the DB if the data has not
		// been changed.  This can happen if user changes the cell contents, then changes
		// them back before exiting the cell.
		
		// first look to see if they are identical objects, e.g. both null
		if (newValue == oldValue)
			return true;	// the caller does not need to know that nothing happened

		// if either of the values is null and the other is not, then the data has
		// changed and we fall-through to the change process.  Otherwise, check
		// the object contents.
		if (oldValue != null && newValue != null) {
			//
			// ASSUME:
			//	- all editable data has a string representation
			//	- if the string representations of the old data and the new data
			//		are not identical, then the data has changed.  This includes
			//		capitalization, so "A" is not the same as "a".
			//
			//?? Note: if we can guarantee that ALL data objects that we deal with
			//?? such as Integer, String, etc, have an equals() function correctly
			//?? implemented, then we should use that instead of doing this
			//?? string-to-string comparison.
			if (newValue.toString().equals(oldValue.toString()))
				return true;	// the caller does not need to know that nothing happened
					
			// if we reach this point, value has changed, so fall through to next section
		}

		// call the function in the app code that checks for unexpected
		// conditions in the current DB
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
