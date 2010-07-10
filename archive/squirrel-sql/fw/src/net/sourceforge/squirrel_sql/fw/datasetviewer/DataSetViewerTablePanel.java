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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.fw.gui.BaseMDIParentFrame;
import net.sourceforge.squirrel_sql.fw.gui.ButtonTableHeader;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

//TODO: I've made a real mess of the showtextAreaDialog() method. Clean it up!!!  CB
//??RENAME to DataSetViewerTableDestination
public class DataSetViewerTablePanel extends BaseDataSetViewerDestination
{
	private ILogger s_log = LoggerController.createLogger(DataSetViewerTablePanel.class);

	private MyJTable _comp = new MyJTable();
	private MyTableModel _typedModel;

	public DataSetViewerTablePanel()
	{
		super();
	}

	public void clear()
	{
		_typedModel.clear();
		_typedModel.fireTableDataChanged();
	}

	public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
	{
		super.setColumnDefinitions(colDefs);
		_comp.setColumnDefinitions(colDefs);
	}

	public void moveToTop()
	{
		if (_comp.getRowCount() > 0)
		{
			_comp.setRowSelectionInterval(0, 0);
		}
	}

	/**
	 * Get the component for this viewer.
	 *
	 * @return	The component for this viewer.
	 */
	public Component getComponent()
	{
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
	protected void allRowsAdded()
	{
		_typedModel.fireTableStructureChanged();
	}

	/*
	 * @see IDataSetViewer#getRowCount()
	 */
	public int getRowCount()
	{
		return _typedModel.getRowCount();
	}

	private final static class MyTableModel extends AbstractTableModel
	{
		private List _data = new ArrayList();
		private ColumnDisplayDefinition[] _colDefs = new ColumnDisplayDefinition[0];

		MyTableModel()
		{
			super();
		}

		public Object getValueAt(int row, int col)
		{
			return ((Object[]) _data.get(row))[col];
		}

		public int getRowCount()
		{
			return _data.size();
		}

		public int getColumnCount()
		{
			return _colDefs != null ? _colDefs.length : 0;
		}

		public String getColumnName(int col)
		{
			return _colDefs != null ? _colDefs[col].getLabel() : super.getColumnName(col);
		}

		void setHeadings(ColumnDisplayDefinition[] hdgs)
		{
			_colDefs = hdgs;
		}

		public void addRow(Object[] row)
		{
			_data.add(row);
		}

		void clear()
		{
			_data.clear();
		}

		public void allRowsAdded()
		{
			fireTableStructureChanged();
		}
	}

	private final class MyJTable extends JTable
	{
		private final int _multiplier;
		private static final String data = "THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG";

		private TablePopupMenu _tablePopupMenu;
		private ButtonTableHeader _bth;

		MyJTable()
		{
			super(new SortableTableModel(new MyTableModel()));
			_typedModel = (MyTableModel) ((SortableTableModel) getModel()).getActualModel();
			_multiplier =
				Toolkit.getDefaultToolkit().getFontMetrics(getFont()).stringWidth(data) / data.length();
			createUserInterface();
		}

		public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
		{
			TableColumnModel tcm = createColumnModel(colDefs);
			setColumnModel(tcm);
			_typedModel.setHeadings(colDefs);
		}

		MyTableModel getTypedModel()
		{
			return _typedModel;
		}

		/**
		 * Display the popup menu for this component.
		 */
		private void displayPopupMenu(MouseEvent evt)
		{
			_tablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}

		private void showTextAreaDialog(MouseEvent evt)
		{
			Point pt = evt.getPoint();
			int row = rowAtPoint(pt);
			int col = columnAtPoint(pt);

			//Component comp = SwingUtilities.getRoot(this);
//			Component comp = SwingUtilities.windowForComponent(this);
			Component comp = SwingUtilities.getRoot(this);

			Object obj = _comp.getValueAt(row, col);
			if (obj != null)
			{
				obj = obj.toString();
			}
			else
			{
				obj = "";
			}


			Component newComp = null;
			if (comp instanceof BaseMDIParentFrame)
			{
				TextAreaInternalFrame taif = new TextAreaInternalFrame(_typedModel.getColumnName(col), (String)obj);
				((BaseMDIParentFrame)comp).addInternalFrame(taif, false);
				taif.setLayer(JLayeredPane.POPUP_LAYER);
				taif.pack();
				newComp = taif;
			}
			else
			{
				TextAreaDialog tad = null;
				if (comp instanceof Dialog)
				{
					tad = new TextAreaDialog((Dialog)comp, _typedModel.getColumnName(col), (String)obj);
				}
				else if (comp instanceof Frame)
				{
					tad = new TextAreaDialog((Frame)comp, _typedModel.getColumnName(col), (String)obj);
				}			
				else
				{
					s_log.error("Creating TextAreaDialog for invalid parent of: " + comp.getClass().getName());
					return;
				}	
				tad.pack();
				newComp = tad;
			}

			Dimension dim = newComp.getSize();
			boolean dimChanged = false;
			if (dim.width < 250)
			{
				dim.width = 250;
				dimChanged = true;
			}
			if (dim.height < 100)
			{
				dim.height = 100;
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
				// getRoot() doesn't appear to return the deepest Window, but the
				// first one. If you have a dialog owned by a window you get the
				// dialog, not the window.
				// TODO: Need to loop in the call to WindowForComponent
				// in case there are multiple levels of windows.
				Component parent = SwingUtilities.windowForComponent(comp);
				pt = SwingUtilities.convertPoint((Component) evt.getSource(), pt, parent != null ? parent : comp);
			}
			newComp.setLocation(pt);

			newComp.setVisible(true);
		}

		private TableColumnModel createColumnModel(ColumnDisplayDefinition[] colDefs)
		{
			CellRenderer[] renderers = new CellRenderer[colDefs.length];
			for (int i = 0; i < colDefs.length; ++i)
			{
				renderers[i] = new CellRenderer(i);
			}

			//_colDefs = hdgs;
			TableColumnModel cm = new DefaultTableColumnModel();
			for (int i = 0; i < colDefs.length; ++i)
			{
				ColumnDisplayDefinition colDef = colDefs[i];
				int colWidth = colDef.getDisplayWidth() * _multiplier;
				if (colWidth > MAX_COLUMN_WIDTH * _multiplier)
				{
					colWidth = MAX_COLUMN_WIDTH * _multiplier;
				}
				TableColumn col = new TableColumn(i, colWidth, renderers[i], null);
				col.setHeaderValue(colDef.getLabel());
				cm.addColumn(col);
			}
			return cm;
		}

		private void createUserInterface()
		{
			setLayout(new BorderLayout());
			setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			setRowSelectionAllowed(false);
			setColumnSelectionAllowed(false);
			setCellSelectionEnabled(true);
			getTableHeader().setResizingAllowed(true);
			getTableHeader().setReorderingAllowed(true);
			setAutoCreateColumnsFromModel(false);
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			_bth = new ButtonTableHeader();
			setTableHeader(_bth);

			_tablePopupMenu = new TablePopupMenu();
			_tablePopupMenu.setTable(this);

			addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						MyJTable.this.displayPopupMenu(evt);
					}
					else if (evt.getClickCount() == 2)
					{
						MyJTable.this.showTextAreaDialog(evt);
					}
				}
				public void mouseReleased(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						MyJTable.this.displayPopupMenu(evt);
					}
				}
			});

		}
	}

	class TextAreaInternalFrame extends JInternalFrame {
		public TextAreaInternalFrame(String column, String text)
		{
			super("Value of column " + column,true,true,true,true);
			Container con = getContentPane();
			con.setLayout(new BorderLayout());
			JTextArea area = new JTextArea(text);
			area.setEditable(false);
			JScrollPane pane = new JScrollPane(area);
			con.add(pane,BorderLayout.CENTER);
		}
	}

	class TextAreaDialog extends JDialog {
		public TextAreaDialog(Dialog owner, String column, String text)
		{
			super(owner, "Value of column " + column, false);
			createUserInterface(text);
		}

		public TextAreaDialog(Frame owner, String column, String text)
		{
			super(owner, "Value of column " + column, false);
			createUserInterface(text);
		}

		private void createUserInterface(String text)
		{
			Container con = getContentPane();
			con.setLayout(new BorderLayout());
			JTextArea area = new JTextArea(text);
			area.setEditable(false);
			JScrollPane pane = new JScrollPane(area);
			con.add(pane,BorderLayout.CENTER);
		}
	}

	private final class CellRenderer extends DefaultTableCellRenderer
	{
		private final int _idx;

		CellRenderer(int idx)
		{
			super();
			_idx = idx;
		}

		public void setValue(Object value)
		{
			super.setValue(getColumnRenderer(_idx).renderObject(value, _idx));
		}
	}
}