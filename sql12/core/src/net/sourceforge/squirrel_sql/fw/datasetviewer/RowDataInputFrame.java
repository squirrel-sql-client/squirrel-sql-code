package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
 * Modifications copyright (C) 2001-2002 Johan Compagner
 * jcompagner@j-com.nl
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * @author gwg
 *
 * This is the frame that gets data from the user for creating
 * a new row in a table.
 */
public class RowDataInputFrame extends JDialog
	implements ActionListener {


	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RowDataInputFrame.class);

	// object that called us and that we want to return data to when done
	DataSetViewerEditableTablePanel _caller;

	// the table containing the user's input
	RowDataJTable table;

	/**
	 * ctor.
	 */
	public RowDataInputFrame(Component comp, ColumnDisplayDefinition[] colDefs,
									 Object[] initialValues,
									 DataSetViewerEditableTablePanel caller) {

		// i18n[rowDataInputFrame.propName=Input New Row Data]
		super(SwingUtilities.windowForComponent(comp), s_stringMgr.getString("rowDataInputFrame.propName"));

		// get the ConentPane into a variable for convenience
		Container pane = getContentPane();

		// save data passed in to us	
		_caller = caller;

		// set layout
		pane.setLayout(new BorderLayout());

		// create the JTable for input and put in the top of window
		table = new RowDataJTable(colDefs, initialValues);
		// tell scrollpane to use table size with the height adjusted to leave
		// room for the scrollbar at the bottom if needed
		Dimension tableDim = table.getPreferredSize();
		tableDim.setSize(tableDim.getWidth(), tableDim.getHeight() + 15);
		table.setPreferredScrollableViewportSize(tableDim);
		JScrollPane scrollPane = new JScrollPane(table);

		// add row headers to help user understand what the second row is
		JPanel rowHeaderPanel = new JPanel();
		rowHeaderPanel.setLayout(new BorderLayout());
		// i18n[rowDataInputFrame.data=Data]
		JTextArea r1 = new JTextArea(s_stringMgr.getString("rowDataInputFrame.data"), 1, 10);
		r1.setBackground(Color.lightGray);
		r1.setBorder(BorderFactory.createLineBorder(Color.black));
		r1.setEditable(false);
		rowHeaderPanel.add(r1, BorderLayout.NORTH);
		// i18n[rowDataInputFrame.colDescription=\nColumn\nDescription\n]
		JTextArea r2 = new JTextArea(s_stringMgr.getString("rowDataInputFrame.colDescription"), 4, 10);
		r2.setBackground(Color.lightGray);
		r2.setBorder(BorderFactory.createLineBorder(Color.black));
		r2.setEditable(false);
		rowHeaderPanel.add(r2, BorderLayout.CENTER);
		scrollPane.setRowHeaderView(rowHeaderPanel);

		pane.add(scrollPane, BorderLayout.NORTH);

		// create the buttons for input done and cancel
		JPanel buttonPanel = new JPanel();


		// i18n[rowDataInputFrame.insert=Insert]
		JButton insertButton = new JButton(s_stringMgr.getString("rowDataInputFrame.insert"));
		buttonPanel.add(insertButton);
		insertButton.setActionCommand("insert");
		insertButton.addActionListener(this);

		// i18n[rowDataInputFrame.cancel=Cancel]
		JButton cancelButton = new JButton(s_stringMgr.getString("rowDataInputFrame.cancel"));
		buttonPanel.add(cancelButton);
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);

		pane.add(buttonPanel, BorderLayout.SOUTH);

		// this frame should really go away when done
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// display the frame
		pack();
		setVisible(true);

	}

	/**
	 * Handle actions on the buttons 
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("cancel")) {
			setVisible(false);
			dispose();
			return;
		}
		else if ( ! e.getActionCommand().equals("insert")) {
			return;	// do not recognize this button request
		}

		// user said to insert, so collect all the data from the
		// JTable and send it to the DataSetViewer for insertion
		// into DB and on-screen tables

		// first make sure that user's last input has been included
		// (It is too easy for user to enter data and forget to click
		// on another field to force it to be set.)
		if (table.isEditing()) {
			int col = table.getEditingColumn();
			table.getCellEditor(0, col).stopCellEditing();
		}

		Object[] rowData = new Object[table.getModel().getColumnCount()];
		for (int i=0; i< table.getModel().getColumnCount(); i++) {
			rowData[i] = table.getValueAt(0, i);
		}

		// put the data into the DB and the on-screen JTable.
		// If there was a failure, do not make this form
		// go away since the user may be able to fix the problem
		// by changing the data.
		if (_caller.insertRow(rowData) == null) {
			// the insert worked, so make this input form go away
			setVisible(false);
			dispose();
		}
	}

	/**
	 * JTable for use in creating data for insertion.
	 */
	class RowDataJTable extends JTable {
		private ColumnDisplayDefinition[] _colDefs = null;

		/**
		 * constructor
		 */
		protected  RowDataJTable(ColumnDisplayDefinition[] colDefs, Object[] initalValues) {

			super();
			setModel(new RowDataModel(colDefs, initalValues));

			// create column model

			final String data = "THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG";
			final int _multiplier =
				getFontMetrics(getFont()).stringWidth(data) / data.length();

			TableColumnModel cm = new DefaultTableColumnModel();
			for (int i = 0; i < colDefs.length; ++i)
			{
				ColumnDisplayDefinition colDef = colDefs[i];
				int colWidth = colDef.getDisplayWidth() * _multiplier;
				if (colWidth > IDataSetViewer.MAX_COLUMN_WIDTH * _multiplier)
				{
					colWidth = IDataSetViewer.MAX_COLUMN_WIDTH * _multiplier;
				}

				TableColumn col = new TableColumn(i, colWidth,
					CellComponentFactory.getTableCellRenderer(colDefs[i]), null);
				col.setHeaderValue(colDef.getColumnName());
				cm.addColumn(col);
			}

			setColumnModel(cm);

			_colDefs = colDefs;

			// set up cell editors on first row
			for (int i=0; i< colDefs.length; i++) {
				cm.getColumn(i).setCellEditor(
					CellComponentFactory.getInCellEditor(this, _colDefs[i]));
			}


			// the second row contains a multi-line description,
			// so make that row high enough to display it
			setRowHeight(1, 80);

			setRowSelectionAllowed(false);
			setColumnSelectionAllowed(false);
			setCellSelectionEnabled(true);
			getTableHeader().setResizingAllowed(true);
			getTableHeader().setReorderingAllowed(true);
			setAutoCreateColumnsFromModel(false);
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

//?? Future: may want to create TablePopupMenu to allow cut/copy/paste operations

			// add mouse listener for Popup
			MouseAdapter m = new MouseAdapter()
			{
				public void mousePressed(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						// for now, ignore popup request
						//RowDataJTable.this.displayPopupMenu(evt);
					}
					else if (evt.getClickCount() == 2)
					{
						// figure out which column the user clicked on
						// so we can pass in the right column description

						Point pt = evt.getPoint();
						int col = RowDataJTable.this.columnAtPoint(pt);
						CellDataPopup.showDialog(RowDataJTable.this, _colDefs[col], evt, true);
					}
				}
				public void mouseReleased(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						// for now, ignore popup request
						//RowDataJTable.this.displayPopupMenu(evt);
					}
				}
			};
			addMouseListener(m);
		}

		public boolean isCellEditable(int row, int col) {
			if (row > 0)
				return false;	// only the first row (containing data) is editable
			return CellComponentFactory.isEditableInCell(_colDefs[col], getValueAt(row,col));
		}

		public TableCellRenderer getCellRenderer(int row, int column) {
			if (row == 0)
				return CellComponentFactory.getTableCellRenderer(_colDefs[column]);
			// for entries past the first one, use the default renderer
			return new RowDataDescriptionRenderer();
		}

		/*
		 * When user leaves a cell after editing it, the contents of
		 * that cell need to be converted from a string into an
		 * object of the appropriate type before updating the table.
		 * However, when the call comes from the Popup window, the data
		 * has already been converted and validated.
		 * We assume that a String being passed in here is a value from
		 * a text field that needs to be converted to an object, and
		 * a non-string object has already been validated and converted.
		 */
		public void setValueAt(Object newValueString, int row, int col)
		{
			if (! (newValueString instanceof java.lang.String))
			{
				// data is an object - assume already validated
				super.setValueAt(newValueString, row, col);
				return;
			}

			// data is a String, so we need to convert to real object
			StringBuffer messageBuffer = new StringBuffer();
			ColumnDisplayDefinition colDef = _colDefs[col];
			Object newValueObject = CellComponentFactory.validateAndConvert(
				colDef, getValueAt(row, col), (String) newValueString, messageBuffer);
			if (messageBuffer.length() > 0)
			{
				// display error message and do not update the table

				// i18n[rowInputDataFrame.conversionToInternErr=The given text cannot be converted into the internal object.\nThe database has not been changed.\nThe conversion error was:\n{0}]
				String msg = s_stringMgr.getString("rowInputDataFrame.conversionToInternErr", messageBuffer);
				JOptionPane.showMessageDialog(this,
					msg,
					// i18n[rowInputDataFrame.conversionErr=Conversion Error]
					s_stringMgr.getString("rowInputDataFrame.conversionErr"),
					JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				// data converted ok, so update the table
				super.setValueAt(newValueObject, row, col);
			}
		}


	}

	/**
	 * Model for use by JTable in creating data for insertion.
	 */
	class RowDataModel extends DefaultTableModel {


		/**
		 * ctor
		 */
		protected RowDataModel(ColumnDisplayDefinition[] colDefs, Object[] initalValues) {
			super();

			// set up the list of column names and the data for the rows
			String[] colNames = new String[colDefs.length];
			Object[][] rowData = new Object[2][colDefs.length];
			for (int i=0; i<colDefs.length; i++) {
				colNames[i] = colDefs[i].getColumnName();	// set column heading
				rowData[0][i] = initalValues[i];	// set data in first row

				// put a description of the field in the following rows
				rowData[1][i] = getColumnDescription(colDefs[i]);
                    
//                    colDefs[i].getSqlTypeName() + "\n" +
//					((colDefs[i].isNullable()) ? "nullable" : "not nullable") + "\n" +
//					"precision="+ colDefs[i].getPrecision() + "\n" +
//					"scale=" + colDefs[i].getScale();
			}

			// put the data and header names into the model
			setDataVector(rowData, colNames);
		}
        
        /**
         * Provides values for several column attributes (nullable, prec, scale)
         * and in the event the column is auto-increment it displays that as a 
         * visual cue to the user that the field cannot be edited.
         * 
         * @param def the ColumnDisplayDefinition that describes the column
         * @return a string of column attributes separated by eol chars.
         */
        private String getColumnDescription(ColumnDisplayDefinition def) {
            StringBuilder result = new StringBuilder();
            result.append(def.getSqlTypeName());
            result.append("\n");
            if (def.isNullable()) {
                result.append("nullable");
            } else {
                result.append("not nullable");
            }
            result.append("\n");
            if (JDBCTypeMapper.isNumberType(def.getSqlType())) {
                result.append("prec=");
                result.append(def.getPrecision());
                result.append("\n");
                result.append("scale=");
                result.append(def.getScale());
                if (def.isAutoIncrement()) {
                    result.append("\n");
                    result.append("(auto-incr)");
                }
            } else {
                result.append("length=");
                result.append(def.getColumnSize());
            }
            return result.toString();
        }
	}

	/**
	 * renderer to display multiple lines in one table cell
	 */
	class RowDataDescriptionRenderer implements TableCellRenderer {

		public Component getTableCellRendererComponent(JTable table, 
                                                       Object value, 
                                                       boolean isSelected, 
                                                       boolean hasFocus, 
                                                       int row, 
                                                       int column) {

				JTextArea ta = new JTextArea((String)value, 8, 20);
				ta.setBackground(Color.lightGray);
				return ta;
			}
	}

}
