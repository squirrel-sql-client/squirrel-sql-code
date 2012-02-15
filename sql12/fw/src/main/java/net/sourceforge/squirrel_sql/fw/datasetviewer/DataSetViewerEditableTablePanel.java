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

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * @author gwg
 *
 * Table view that allows editing of the data.
 */
public class DataSetViewerEditableTablePanel extends DataSetViewerTablePanel
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataSetViewerEditableTablePanel.class);


	/* Menu for right-mouse-click when in cell editors */
	TablePopupMenu cellPopupMenu = null;
	
	/**
	 * Internal definitions
	 */
	public void init(IDataSetUpdateableModel updateableModel, IDataModelImplementationDetails dataModelImplementationDetails)
	{
		super.init(updateableModel, dataModelImplementationDetails);
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
	public boolean isColumnEditable(int col, Object originalValue)
	{
		if (_colDefs == null)
			return false;	// cannot edit something that we do not know anything about

		if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == col)
			return false;

		// Cannot edit the rowID column, if present
		if ( ((IDataSetUpdateableTableModel)getUpdateableModel()).getRowidCol() == col)
			return false;

		return CellComponentFactory.isEditableInCell(_colDefs[col], originalValue);
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
		cellPopupMenu = new TablePopupMenu(getUpdateableModel(), this, table, getDataModelImplementationDetails());
		
		for (int i=0; i < _colDefs.length; i++) {
			// use factory to get the appropriate editor
			DefaultCellEditor editor =
				CellComponentFactory.getInCellEditor(table, _colDefs[i]);
			
			// add right-click menu to cell editor
			editor.getComponent().addMouseListener(
				new MouseAdapter()
				{
					public void mousePressed(MouseEvent evt)
					{
						if (evt.isPopupTrigger())
						{
							DataSetViewerEditableTablePanel.this.cellPopupMenu.show(
								evt.getComponent(), evt.getX(), evt.getY());
						}
					}
					public void mouseReleased(MouseEvent evt)
					{
						if (evt.isPopupTrigger())
						{
							DataSetViewerEditableTablePanel.this.cellPopupMenu.show(
								evt.getComponent(), evt.getX(), evt.getY());
						}
					}
				});

			// We have to look for the modelindex because of the Row Number column
			getColumnForModelIndex(i, table.getColumnModel()).setCellEditor(editor);
		}
	}

	private TableColumn getColumnForModelIndex(int modelIndex, TableColumnModel columnModel)
	{
		for (int i = 0; i < columnModel.getColumnCount(); i++)
		{
			if(columnModel.getColumn(i).getModelIndex() == modelIndex)
			{
				return columnModel.getColumn(i);
			}
		}

		throw new IllegalArgumentException("No column for model index " + modelIndex);
	}

	/**
	 * Call the underlaying object to update the data represented by the JTable.
	 * Both the old and the new value are objects of the appropriate
	 * Data Type for the column.  The newValue has been validated as part of
	 * the conversion from the external user representation (a String) into the
	 * internal object.
	 */
	public int[] changeUnderlyingValueAt(
		int row,
		int col,
		Object newValue,
		Object oldValue)
	{
		String message = null;

		// At this point the user input has been validated and both the
		// new and old values are objects of the appropriate data type.
		// Either or both of newValue and oldValue may be null.

		// if there is no updateable model, then we cannot update anything
		// (should never happen - just being safe here)
		if (getUpdateableModelReference() == null)
			return new int[0];	// no underlying data, so cannot be changed


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
			return new int[0];	// the caller does not need to know that nothing happened

		// if either of the values is null and the other is not, then the data has
		// changed and we fall-through to the change process.  Otherwise, check
		// the object contents.
		if (oldValue != null && newValue != null) {
			// ask the DataType object if the two values are the same
			if (CellComponentFactory.areEqual( _colDefs[col], oldValue, newValue))
				return new int[0];	// the caller does not need to know that nothing happened

			// if we reach this point, the value has been changed,
			// so fall through to next section
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
			// i18n[baseDataSetViewerDestination.warning=Warning]
			int option = JOptionPane.showConfirmDialog(null, message, s_stringMgr.getString("baseDataSetViewerDestination.warning"),
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if ( option != JOptionPane.YES_OPTION)
			{
				return new int[0];	// no update done to underlying data
			}
		}

		// call the function in the app code that checks for unexpected
		// conditions in the DB as it will be after doing the update
		if (getUpdateableModelReference() != null)
			message = ((IDataSetUpdateableTableModel)getUpdateableModelReference()).
				getWarningOnProjectedUpdate(getRow(row), _colDefs, col, newValue);

		if (message != null) {
			// set up dialog to ask user if it is ok to proceed
			// IMPORTANT: this dialog is SYNCHRONOUS (ie. we do not proceed until
			// user gives a response).  This is critical since this function provides
			// a return value to its caller that depends on the user input.
			// i18n[baseDataSetViewerDestination.warning2=Warning]
			int option = JOptionPane.showConfirmDialog(null, message, s_stringMgr.getString("baseDataSetViewerDestination.warning2"),
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if ( option != JOptionPane.YES_OPTION)
			{
				return new int[0];	// no update done to underlying data
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
			// i18n[baseDataSetViewerDestination.error=Error]
			JOptionPane.showMessageDialog(null, message, s_stringMgr.getString("baseDataSetViewerDestination.error"),
				JOptionPane.ERROR_MESSAGE);

			// tell caller that the underlying data was not updated
			//?? is this always true, or could the data be updated with a warning?
			return new int[0];
		}


		// No problems, so indicate a successful update of the underlying data.
		// In case we are editing an SQL result that contains the edited colum
		// more than once, we need to tell the caller to update all columns.
		// Otherwise generation of where clauses for further editing will fail.
		ArrayList<Integer> buf = new ArrayList<Integer>();
		for (int i = 0; i < _colDefs.length; i++)
		{
			if(_colDefs[i].getFullTableColumnName().equalsIgnoreCase(_colDefs[col].getFullTableColumnName()))
			{
				buf.add(Integer.valueOf(i));
			}
		}

		int[] ret = new int[buf.size()];

		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = buf.get(i);
		}

		return ret;
	}
	
	/**
	 * Delete a set of rows from the table.
	 * The indexes are the row indexes in the SortableModel.
	 */
	public void deleteRows(int[] rows)
	{
		// The list of rows may be empty, in which case
		// we tell user they should select something first
		if (rows.length == 0) {
			JOptionPane.showMessageDialog(null,
			   // i18n[dataSetViewerEditableTablePanel.selectionNeeded=You must select something in the table to delete.]
				s_stringMgr.getString("dataSetViewerEditableTablePanel.selectionNeeded"));
			return;
		}


		// i18n[dataSetViewerEditableTablePanel.deleteRosQuestion=Do you wish to delete {0} rows from this table?]
		String msg = s_stringMgr.getString("dataSetViewerEditableTablePanel.deleteRosQuestion", rows.length);

		// Non-empty set of rows to delete.  Make sure user wants to delete
		int option = JOptionPane.showConfirmDialog(
			null,
			msg,
			// i18n[dataSetViewerEditableTablePanel.warning=Warning]
			s_stringMgr.getString("dataSetViewerEditableTablePanel.warning"),
			JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE);


		if ( option != JOptionPane.YES_OPTION)
		{
			return;	// no update done to underlying data
		}
		
		//cancel any current cell editing operations
		if (currentCellEditor != null) {
			currentCellEditor.cancelCellEditing();
			currentCellEditor = null;
		}
		
		// create data structure containing contents of rows to be deleted
		// We cannot use the getRow() method because that uses MyJTable whereas
		// the indexes that we have are indexes in the SortableTableModel.
		SortableTableModel tableModel = (SortableTableModel)((JTable)getComponent()).getModel();

		Object[][] rowData = new Object[rows.length][_colDefs.length];
		for (int i=0; i<rows.length; i++) {
			for (int j=0; j<_colDefs.length; j++)
				rowData[i][j] = tableModel.getValueAt(rows[i],j);
		}
		
		// tell creator to delete from DB
		String message = 
			((IDataSetUpdateableTableModel)getUpdateableModel()).deleteRows(rowData, _colDefs);

		if (message != null)
		{
			// tell user that there was a problem
			JOptionPane.showMessageDialog(null,
				// i18n[dataSetViewerEditableTablePanel.noRowsDeleted={0}\nNo rows deleted from database.]
				s_stringMgr.getString("dataSetViewerEditableTablePanel.noRowsDeleted", message),
				// i18n[dataSetViewerEditableTablePanel.error=Error]
				s_stringMgr.getString("dataSetViewerEditableTablePanel.error"),
				JOptionPane.ERROR_MESSAGE);

			return;
		}

		// DB delete worked correctly, so now delete from table
		//IMPORTANT: The user and the creator both work through the
		// SortableTableModel, not the Actual model.  Thus the row
		// indexes to delete are given in the SortableTableModel row numbers,
		// so we must work through that model model to actually do the delete.
		((SortableTableModel)((MyJTable)getComponent()).getModel()).deleteRows(rows);
        ((MyJTable)getComponent()).clearSelection();
	}

	/**
	 * Initiate operations to insert a new row into the table.
	 * This method just creates the panel to get the row input from the user.
	 */
	public void insertRow() {
		JTable table = (JTable)getComponent();
		
		// Setting the starting position is ugly.  I just picked a point.
		Point pt = new Point(10, 200);

		Component comp = SwingUtilities.getRoot(table);

		// get the default values from the DB for the table columns
		String[] dbDefaultValues = 
			((IDataSetUpdateableTableModel)getUpdateableModelReference()).
				getDefaultValues(_colDefs);
		
		// based on defaults from DB, get the default object instance
		// for each column
		Object[] initialValues = new Object[dbDefaultValues.length];
		for (int i=0; i< initialValues.length; i++) {
			initialValues[i] = CellComponentFactory.getDefaultValue(
				_colDefs[i], dbDefaultValues[i]);
		}

		// The following only works if SwingUtilities.getRoot(table) returns
		// and instanceof BaseMDIParentFrame.
		// If SwingTUilities.getRoot(table) returns and instance of Dialog or
		// Frame, then other code must be used.
		RowDataInputFrame rdif = new RowDataInputFrame( table, _colDefs, initialValues, this);
//		((IMainFrame)comp).addInternalFrame(rdif, false);
//		rdif.setLayer(JLayeredPane.POPUP_LAYER);
		rdif.pack();

		Dimension dim = rdif.getSize();
		boolean dimChanged = false;
		if (dim.width < 300)
		{
			dim.width = 300;
			dimChanged = true;
		}

		if (dimChanged)
		{
			rdif.setSize(dim);
		}

			
		// Determine the position to place the new internal frame. Ensure that the right end
		// of the internal frame doesn't exend past the right end the parent frame.	Use a
		// fudge factor as the dim.width doesn't appear to get the final width of the internal
		// frame (e.g. where pt.x + dim.width == parentBounds.width, the new internal frame
		// still extends past the right end of the parent frame).
		int fudgeFactor = 100;
		Rectangle parentBounds = comp.getBounds();
		if (parentBounds.width <= (dim.width + fudgeFactor))
		{
			dim.width = parentBounds.width - fudgeFactor;
			pt.x = fudgeFactor / 2;
			rdif.setSize(dim);
		}
		else 
		{
			if ((pt.x + dim.width + fudgeFactor) > (parentBounds.width))
			{
				pt.x -= (pt.x + dim.width + fudgeFactor) - parentBounds.width;
			}
		}

		rdif.setLocation(pt);
		rdif.setVisible(true);
	}
	
	/**
	 * Insert a new row into the table after the user has entered the row's data.
	 */
	protected String insertRow(Object[] values) {

		String message = 
			((IDataSetUpdateableTableModel)getUpdateableModelReference()).
				insertRow(values, _colDefs);
		
		if (message != null) {
			// there was a problem inserting into the DB
			JOptionPane.showMessageDialog(null,
				// i18n[dataSetViewereditableTablePanel.error2=Error]
				message, s_stringMgr.getString("dataSetViewereditableTablePanel.error2"),
				JOptionPane.ERROR_MESSAGE);
				
			return "Error";	// non-null return tells caller there was a problem
		}

		// add the data to the existing tables
		
		// Do not try to be fancy and insert the data where the user is looking,
		// just stuff it into the actual model and re-paint the table
		// when the 'table changed' event is fired.
		
		SortableTableModel sortedModel =
			(SortableTableModel)((JTable)getComponent()).getModel();
			
		sortedModel.insertRow(values);
		
		// everything is ok
		return null;
	}
}
