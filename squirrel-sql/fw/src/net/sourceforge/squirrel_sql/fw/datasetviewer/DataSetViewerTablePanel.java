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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.fw.gui.ButtonTableHeader;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.TextAreaInternalFrame;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
//??RENAME to DataSetViewerTableDestination
public class DataSetViewerTablePanel extends BaseDataSetViewerDestination
{
	private MyJTable _comp = new MyJTable();
	private MyTableModel _typedModel;

	public DataSetViewerTablePanel() {
		super();
	}


	public void clear() {
		_typedModel.clear();
		_typedModel.fireTableDataChanged();
	}

	public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs) {
		super.setColumnDefinitions(colDefs);
		_comp.setColumnDefinitions(colDefs);
	}

	public void moveToTop() {
		if (_comp.getRowCount() > 0) {
			_comp.setRowSelectionInterval(0, 0);
		}
	}

	/**
	 * Get the component for this viewer.
	 * 
	 * @return	The component for this viewer.
	 */
	public Component getComponent() {
		return _comp;
	}

	/*
	 * @see BaseDataSetViewerDestination#addRow(Object[])
	 */
	protected void addRow(Object[] row) 
	{
		_typedModel.addRow(row);
	}

	/*
	 * @see BaseDataSetViewerDestination#allRowsAdded()
	 */
	protected void allRowsAdded() {
		_typedModel.fireTableStructureChanged();
	}

	/*
	 * @see IDataSetViewer#getRowCount()
	 */
	public int getRowCount() {
		return _typedModel.getRowCount();
	}


	private final class MyTableModel extends AbstractTableModel {
		private List _data = new ArrayList();

		MyTableModel() {
			super();
		}

		public Object getValueAt(int row, int col) {
			return ((Object[])_data.get(row))[col];
		}

		public int getRowCount() {
			return _data.size();
		}

		public int getColumnCount() {
			return _colDefs != null ? _colDefs.length : 0;
		}

		public String getColumnName(int col) {
			return _colDefs != null ? _colDefs[col].getLabel() : super.getColumnName(col);
		}

		void setHeadings(ColumnDisplayDefinition[] hdgs) {
			_colDefs = hdgs;
		}

		public void addRow(Object[] row) {
			_data.add(row);
		}

		void clear() {
			_data.clear();
		}

		public void allRowsAdded() {
			fireTableStructureChanged();
		}
	}

	private final class MyJTable extends JTable {
		private final int _multiplier;
		private static final String data = "THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG";

		private TablePopupMenu _tablePopupMenu;
		private ButtonTableHeader _bth;


		MyJTable() {
			super(new SortableTableModel(new MyTableModel()));
			_typedModel = (MyTableModel)((SortableTableModel)getModel()).getActualModel();
			_multiplier = Toolkit.getDefaultToolkit().getFontMetrics(getFont()).stringWidth(data) / data.length();
			createUserInterface();
		}

		public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs) {
			TableColumnModel tcm = createColumnModel(colDefs);
			setColumnModel(tcm);
			_typedModel.setHeadings(colDefs);
		}
	
		MyTableModel getTypedModel() {
			return _typedModel;
		}

		/**
		 * Display the popup menu for this component.
		 */
		private void displayPopupMenu(MouseEvent evt) {
			_tablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}

		private void showTextAreaDialog(MouseEvent evt) 
		{
			// TODO? Better way to do this?? I don't have the IApplication here.
			Component comp = SwingUtilities.getRoot(this);
			Point p = evt.getPoint();
			int row = this.rowAtPoint(p);
			int column = this.columnAtPoint(p);
			Object o = _typedModel.getValueAt(row,column);
			if(o != null) o = o.toString();
			else o = "";
			TextAreaInternalFrame taif = new TextAreaInternalFrame(_typedModel.getColumnName(column),(String)o);
			p = SwingUtilities.convertPoint((Component)evt.getSource(),p,comp);
			((MainFrame)comp).addInternalFrame(taif,false);
			taif.setLayer(JLayeredPane.POPUP_LAYER);
			taif.pack();
			Dimension dim = taif.getSize();
			boolean dimChanged = false;
			if(dim.width < 250)
			{
				dim.width = 250;
				dimChanged = true;
			}
			if(dim.height < 100)
			{
				dim.height = 100;
				dimChanged = true;
			}
			if(dim.width > 500)
			{
				dim.width = 500;
				dimChanged = true;
			}
			if(dim.height > 400)
			{
				dim.height = 400;
				dimChanged = true;
			}
			if(dimChanged) taif.setSize(dim);
			p.y -= dim.height;
			taif.setLocation(p);
			taif.setVisible(true);
		}
		

		private TableColumnModel createColumnModel(ColumnDisplayDefinition[] colDefs) {
			//_colDefs = hdgs;
			TableColumnModel cm = new DefaultTableColumnModel();
			for (int i = 0; i < colDefs.length; ++i) {
				ColumnDisplayDefinition colDef = colDefs[i];
				int colWidth = colDef.getDisplayWidth() * _multiplier;
				if (colWidth > MAX_COLUMN_WIDTH * _multiplier) {
					colWidth = MAX_COLUMN_WIDTH * _multiplier;
				}
				TableColumn col = new TableColumn(i, colWidth);
				col.setHeaderValue(colDef.getLabel());
				cm.addColumn(col);
			}
			return cm;
		}

		private void createUserInterface() {
			setLayout(new BorderLayout());
			setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			setRowSelectionAllowed(false);
			setColumnSelectionAllowed(false);
			setCellSelectionEnabled(true);
			getTableHeader().setResizingAllowed(true);
			getTableHeader().setReorderingAllowed(true);
			setAutoCreateColumnsFromModel(false);
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			_bth = new ButtonTableHeader(null);
			setTableHeader(_bth);

			_tablePopupMenu = new TablePopupMenu();
			_tablePopupMenu.setTable(this);
	
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
					if (evt.isPopupTrigger()) {
						MyJTable.this.displayPopupMenu(evt);
					}
					else if(evt.getClickCount() == 2)
					{
						MyJTable.this.showTextAreaDialog(evt);
					}
				}
				public void mouseReleased(MouseEvent evt) {
					if (evt.isPopupTrigger()) {
						MyJTable.this.displayPopupMenu(evt);
					}
				}
			});
	
		}
	}
}

