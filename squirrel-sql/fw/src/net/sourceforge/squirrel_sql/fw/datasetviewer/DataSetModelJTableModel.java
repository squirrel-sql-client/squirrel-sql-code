package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelListener;

/**
 * This is an adapter class that will turn a <TT>IDataSetModel</TT>
 * into a <TT>javax.swing.table.TableModel</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DataSetModelJTableModel extends AbstractTableModel
				implements IDataSetModelConverter {
	/** <TT>IDataSetModel</TT> that this object is wrapped around. */
	private IDataSetModel _model;

	/** Listener for events in the <TT>IDataSetModel</TT>. */
	private MyDataModelListener _modelListener = new MyDataModelListener();

	/**
	 * Default ctor.
	 */
	public DataSetModelJTableModel() {
		this(null);
	}

	/**
	 * Ctor specifying the <TT>IDataSetModel</TT> to display
	 * data from.
	 * 
	 * @param	model	<TT>IDataSetMoel</TT> containing the table data.
	 */
	public DataSetModelJTableModel(IDataSetModel model) {
		super();
		setDataSetModel(model);
	}

	/**
	 * Set the <TT>IDataSetModel</TT> to display
	 * data from.
	 * 
	 * @param	model	<TT>IDataSetModel</TT> containing the table data.
	 */
	public synchronized void setDataSetModel(IDataSetModel model) {
		if (_model != null) {
			_model.removeListener(_modelListener);
		}
		_model = model;
		if (_model != null) {
			_model.addListener(_modelListener);
		}
	}

	/**
	 * Return number of rows in model.
	 * 
	 * @return	Number of rows.
	 * 
	 * @throws	IllegalStateException
	 *			if <TT>IDataSetModel</TT> hasn't been specified.
	 */
	public int getRowCount() throws IllegalStateException {
		validate();
		return _model.getRowCount();
	}
	/**
	 * Return number of columns in model.
	 * 
	 * @return	Number of columns.
	 * 
	 * @throws	IllegalStateException
	 *			if <TT>IDataSetModel</TT> hasn't been specified or
	 * 			if the Column Definiitons for the data model is null.
	 */
	public int getColumnCount() throws IllegalStateException {
		return getColumnDefinitions().length;
	}

	/**
	 * @see TableModel#getColumnName(int)
	 */
	public String getColumnName(int arg0) {
		return getColumnDefinitions()[arg0].getLabel();
	}
	/**
	 * @see TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int arg0) {
		return Object.class;	//?? VERY VERY BAD ??
	}

	/**
	 * @see TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	/**
	 * Return the data value for the specified cell.
	 * 
	 * @param	rowIndex	The row whose value is being retrieved.
	 * @param	columnIndex	The column whose value is being retrieved.
	 *
	 * @return	the data value for the specified cell.
	 *
	 * @throws	IllegalStateException
	 *			if <TT>IDataSetModel</TT> hasn't been specified.
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		validate();
		return _model.getValueAt(rowIndex, columnIndex);
	}

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
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		validate();
		_model.setValueAt(value, rowIndex, columnIndex);
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	/**
	 * Create the default component for this converter. In this
	 * case a <TT>JTable</TT>
	 * 
	 * @return	A new instance of a <TT>JTable</TT>.
	 */
	public Component createComponent() {
		return new JTable(this);
	}

	/**
	 * Return the column definitions for the model.
	 * 
	 * @return	the column definitions for the model.
	 * 
	 * @throws	IllegalStateException
	 * 			If model is <TT>null</TT> or the column definitions
	 *			are <TT>null</TT>.
	 */
	protected ColumnDisplayDefinition[] getColumnDefinitions()
			throws IllegalStateException {
		validate();
		ColumnDisplayDefinition[] hdgs = _model.getColumnDefinitions();
		if (hdgs == null) {
			throw new IllegalStateException("Null ColumnDisplayDefinition[]");
		}
		return hdgs;
	}

	/**
	 * Validate that this object is in a state that can be used.
	 * 
	 * <UL>
	 * <LI>The <TT>IDataSetModel</TT> must exist.</LI>
	 * </UL>
	 * 
	 * @throws	IllegalStateException
	 * 			If object is not in a valid state to be used.
	 */	protected void validate() throws IllegalStateException {
		if (_model == null) {
			throw new IllegalStateException("Null IDataSetModel");
		}
	}

	private class MyDataModelListener implements IDataSetModelListener {
		/**
		 * @see DataSetModelListener#allRowsAdded(DataSetModelEvent)
		 */
		public void allRowsAdded(DataSetModelEvent evt) {
			fireTableStructureChanged();//??Overkill
		}

		/**
		 * @see DataSetModelListener#moveToTop(DataSetModelEvent)
		 */
		public void moveToTop(DataSetModelEvent evt) {
		}
	}
}

