package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.CellEditor;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.BaseMDIParentFrame;
import net.sourceforge.squirrel_sql.fw.gui.ButtonTableHeader;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;

/**
 * Generate a popup window to display and manipulate the
 * complete contents of a cell.
 */
public class CellDataPopup
{
		
	/**
	 * function to create the popup display when called from JTable
	 */
	public static void showDialog(JTable table,
		ColumnDisplayDefinition colDef,
		MouseEvent evt)
	{;
		CellDataPopup popup = new CellDataPopup();
		popup.createAndShowDialog(table, evt, colDef);
	}

	private void createAndShowDialog(JTable table, MouseEvent evt,
		ColumnDisplayDefinition colDef)
	{	
				
		ILogger s_log = LoggerController.createLogger(CellDataPopup.class);

		Point pt = evt.getPoint();
		int row = table.rowAtPoint(pt);
		int col = table.columnAtPoint(pt);

		Object obj = table.getValueAt(row, col);
			
		// since user is now using popup, stop editing
		// using the in-cell editor, if any
		CellEditor editor = table.getCellEditor(row, col);
		if (editor != null)
			editor.cancelCellEditing();
			
		IDataSetTableControls creator =
			((DataSetViewerTablePanel.MyJTable)table).getCreator();

		Component comp = SwingUtilities.getRoot(table);
		Component newComp = null;

		// The following only works if SwingUtilities.getRoot(table) returns
		// and instanceof BaseMDIParentFrame.
		// If SwingTUilities.getRoot(table) returns and instance of Dialog or
		// Frame, then other code must be used.
		TextAreaInternalFrame taif = 
			new TextAreaInternalFrame(table.getColumnName(col), colDef, obj,
				row, col, creator, table);
		((BaseMDIParentFrame)comp).addInternalFrame(taif, false);
		taif.setLayer(JLayeredPane.POPUP_LAYER);
		taif.pack();
		newComp = taif;

		Dimension dim = newComp.getSize();
		boolean dimChanged = false;
		if (dim.width < 250)
		{
			dim.width = 250;
			dimChanged = true;
		}
		if (dim.height < 100)
		{
			dim.height = 200;
			dimChanged = true;
		}
		if (dim.width > 500)
		{
			dim.width = 500;
			dimChanged = true;
		}
		if (dim.height > 400)
		{
			dim.height = 400;
			dimChanged = true;
		}
		if (dimChanged)
		{
			newComp.setSize(dim);
		}
		if (comp instanceof BaseMDIParentFrame)
		{
			pt = SwingUtilities.convertPoint((Component) evt.getSource(), pt, comp);
			pt.y -= dim.height;
		}
		else
		{
			// getRoot() doesn't appear to return the deepest Window, but the first one. 
			// If you have a dialog owned by a window you get the dialog, not the window.
			Component parent = SwingUtilities.windowForComponent(comp);
			while ((parent != null) &&
				!(parent instanceof BaseMDIParentFrame) &&
				!(parent.equals(comp)))
			{
				comp = parent;
				parent = SwingUtilities.windowForComponent(comp);
			}
			comp = (parent != null) ? parent : comp;
			pt = SwingUtilities.convertPoint((Component) evt.getSource(), pt, comp);
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
			newComp.setSize(dim);
		}
		else 
		{
			if ((pt.x + dim.width + fudgeFactor) > (parentBounds.width))
			{
				pt.x -= (pt.x + dim.width + fudgeFactor) - parentBounds.width;
			}
		}
		newComp.setLocation(pt);
		newComp.setVisible(true);
	}


	//
	// inner class for the data display pane
	//
	private static class ColumnDataPopupPanel extends JPanel {

		private final PopupEditableIOPanel ioPanel;
		private JInternalFrame _parentFrame = null;
		private int _row;
		private int _col;
		private JTable _table;

		ColumnDataPopupPanel(Object cellContents,
			ColumnDisplayDefinition colDef,
			boolean tableIsEditable)
		{
			super(new BorderLayout());

			if (tableIsEditable && CellComponentFactory.isEditableInPopup(colDef)) {
				// data is editable in popup
				ioPanel = new PopupEditableIOPanel(colDef, cellContents, true);
				
				// Since data is editable, we need to add control panel
				// to manage user requests for DB update, file IO, etc.
				JPanel editingControls = createPopupEditingControls(ioPanel, colDef);
				add(editingControls, BorderLayout.SOUTH);
			}
			else {
				// data is not editable in popup
				ioPanel = new PopupEditableIOPanel(colDef, cellContents, false);
			}

			add(ioPanel, BorderLayout.CENTER);

		}
		
		/**
		 * Set up user controls to stop editing and update DB.
		 */
		private JPanel createPopupEditingControls(PopupEditableIOPanel ioPanel,
			ColumnDisplayDefinition colDef) {
				
			final ColumnDisplayDefinition _colDef = colDef;
				
			JPanel panel = new JPanel(new BorderLayout());
			
			// create update/cancel controls using default layout
			JPanel updateControls = new JPanel();
			
			// set up Update button
			JButton updateButton = new JButton("Update DB");
			updateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {

					// try to convert the text in the popup into a valid
					// instance of type of data object being held in the table cell
					StringBuffer messageBuffer = new StringBuffer();
					Object newValue = ColumnDataPopupPanel.this.ioPanel.getObject(messageBuffer);
					if (messageBuffer.length() > 0) {
						// handle an error in conversion of text to object
						messageBuffer.insert(0,
							"The given text cannot be converted into the internal object.\n"+
							"Please change the data or cancel editing.\n"+
							"The conversion error was:\n");
						JOptionPane.showMessageDialog(ColumnDataPopupPanel.this,
							messageBuffer,
							"Conversion Error",
							JOptionPane.ERROR_MESSAGE);
						ColumnDataPopupPanel.this.ioPanel.requestFocus();

					}
					else {
						// no problem in conversion - proceed with update
						((DataSetViewerTablePanel.MyJTable)_table).setConvertedValueAt(
							newValue, _row, _col);
						ColumnDataPopupPanel.this._parentFrame.setVisible(false);
						ColumnDataPopupPanel.this._parentFrame.dispose();
					}
				}
			});
			
			// set up Cancel button
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					ColumnDataPopupPanel.this._parentFrame.setVisible(false);
					ColumnDataPopupPanel.this._parentFrame.dispose();
				}
			});
			
			// add buttons to button panel
			updateControls.add(updateButton);
			updateControls.add(cancelButton);
			
			// add button panel to main panel
			panel.add(updateControls, BorderLayout.SOUTH);
			
			return panel;
		}
		
		/*
		 * Save various information which is needed to do Update & Cancel.
		 */
		 public void setUserActionInfo(JInternalFrame parent, int row, int col,
		 	JTable table) {
		 	_parentFrame = parent;
		 	_row = row;
		 	_col = col;
		 	_table = table;
		 }
		 

	}



	// The following is only useable for a root type of InternalFrame.  If the
	// root type is Dialog or Frame, then other code must be used.
	class TextAreaInternalFrame extends JInternalFrame
	{
		public TextAreaInternalFrame(String columnName, ColumnDisplayDefinition colDef,
			Object value, int row, int col,
			IDataSetTableControls creator, JTable table)
		{
			super("Value of column " + columnName, true, true, true, true);
			ColumnDataPopupPanel popup =
				new ColumnDataPopupPanel(value, colDef, creator.isTableEditable());
			popup.setUserActionInfo(this, row, col, table);
			setContentPane(popup);
		}
	}

}
