/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import de.muntjak.tinylookandfeel.controlpanel.TinyTableModel;
import de.muntjak.tinylookandfeel.table.SortableTableData;

/**
 * Aside from being the ui delegate for TinyLaF table headers, this class
 * manages user gestures on sortable table headers.
 * <p>
 * At {@link #sortColumns(int[], int[], JTable)} you can initiate sorting.
 * At {@link #setHorizontalAlignments(int[])} you can specify the horizontal
 * alignments of table header renderers.
 * <p>
 * Each table header has its own instance of TinyTableHeaderUI.
 * You can obtain it with code like this:
 * <PRE>
 * JTable table = new JTable(new MySortableTableModel());
 * TableHeaderUI headerUI = table.getTableHeader().getUI();
 * 
 * if(headerUI instanceof TinyTableHeaderUI) {
 *     // do your thing ...
 * }</PRE>
 * 
 * @version 1.0
 * @author Hans Bickel 
 */
public class TinyTableHeaderUI extends BasicTableHeaderUI {

	/** Client property key, not for client use */
	private static final String ROLLOVER_COLUMN_KEY = "rolloverColumn";
	
	/** Client property key, not for client use */
	private static final String SORTED_COLUMN_KEY = "sortedColumn";
	
	/** Client property key, not for client use */
	private static final String SORTING_DIRECTION_KEY = "sortingDirection";
	
	// Action command constants
	private static final int ADD_COLUMN = 0;
	private static final int REMOVE_COLUMN = 1;
	private static final int REPLACE_COLUMN = 2;

	/** The horizontal distance the mouse must move to
	 * be recognized as a mouse drag. */
	private static final int MINIMUM_DRAG_DISTANCE = 5;
	
	private static final HashMap sortingCache = new HashMap();
	// We store the headers default renderer as a client property
	// before we install a TinyTableHeaderRenderer, DEFAULT_RENDERER_KEY
	// is the property key
	private static final String DEFAULT_RENDERER_KEY = "defaultRenderer";
	
	protected SortableTableHandler handler;
	protected TinyTableHeaderRenderer headerRenderer;
	protected boolean rendererInstalled = false;

	public TinyTableHeaderUI() {
		super();
	}

	/**
	 * Returns a new instance of <code>TinyTableHeaderUI</code>.
	 */
	public static ComponentUI createUI(JComponent header) {
		return new TinyTableHeaderUI();
	}
	
	/**
	 * Attaches listeners to the JTableHeader.
	 */
	protected void installListeners() {
		super.installListeners();
		
		// new in 1.3.6
		// install our own renderer so we can realize rollovers
		// and arrow icons (sortable table models only)
		// new in 1.4.0
		// set the renderer only if the table model implements
		// SortableTableData (see SortableTableHandler.getTableModel(JTableHeader))
//		headerRenderer = new TinyTableHeaderRenderer();
//		header.setDefaultRenderer(headerRenderer);

		// new in 1.3.6
		// support for sortable table models
		handler = new SortableTableHandler();
		
		header.addMouseListener(handler);
		header.addMouseMotionListener(handler);
		header.getColumnModel().addColumnModelListener(handler);
		
		// For each table this method is called twice,
		// the first time the table is null, the second
		// time it is non-null
		if(header.getTable() != null) {
			handler.getTableModel(header);
			
			header.getTable().addPropertyChangeListener("model", new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					// Note: Not checking for null header was a bug before 1.4.0
					if(header == null) return;
					
					TableModel model = (TableModel)e.getNewValue();
					
					if(rendererInstalled &&
						(model == null || !(model instanceof SortableTableData)))
					{
						// set the default renderer
						rendererInstalled = false;
						TableCellRenderer defaultRenderer =
							(TableCellRenderer)header.getClientProperty(DEFAULT_RENDERER_KEY);
						header.setDefaultRenderer(defaultRenderer);
					}
					else if(!rendererInstalled && model != null &&
						(model instanceof SortableTableData))
					{
						rendererInstalled = true;
						
						TableCellRenderer defaultRenderer =
							(TableCellRenderer)header.getClientProperty(DEFAULT_RENDERER_KEY);
						
						if(defaultRenderer == null) {
							defaultRenderer = header.getDefaultRenderer();
							header.putClientProperty(DEFAULT_RENDERER_KEY, defaultRenderer);
						}
						
						if(headerRenderer == null) {
							headerRenderer = new TinyTableHeaderRenderer();
						}
						
						header.setDefaultRenderer(headerRenderer);
					}
				}
			});
		}
		
		SortingInformation sortingInfo = (SortingInformation)sortingCache.get(header);
		
		if(sortingInfo != null) {
			handler.restoreSortingInformation(header, sortingInfo);
		}
	}
	
	protected void uninstallListeners() {
		super.uninstallListeners();

		// Remove sorting information - even if we only
		// switch TinyLaF themes, we cannot preserve state
		handler.removeSortingInformation();
		
		header.removeMouseListener(handler);
        header.removeMouseMotionListener(handler);
        header.getColumnModel().removeColumnModelListener(handler);
	}

	/**
	 * Return the preferred size of the header. The preferred height is the 
     * maximum of the preferred heights of all of the components provided 
     * by the header renderers. The preferred width is the sum of the 
     * preferred widths of each column (plus inter-cell spacing).
	 */
	public Dimension getPreferredSize(JComponent c) {
		// this is for the very special case that the header value
		// of the 1st column is an empty string and no custom header
		// renderers were defined.
		// In this case, the dimension returned by super.getPreferredSize()
		// has a height of 2 but that's not what we want...
		Dimension d = super.getPreferredSize(c);
		
		d.height = Math.max(16, d.height);
		
		return d;
	}
	
	/**
	 * Call this method to programmatically initiate sorting on (sortable)
	 * table models. Especially if your data is sorted by default, you
	 * should call this method before the table is displayed the first
	 * time.
	 * @param columns array of column indices sorted by priority (highest
	 * priority first)
	 * @param sortingDirections array containing the sorting direction for
	 * each sorted column. Values are either
	 * <ul>
	 * <li><code>de.muntjak.tinylookandfeel.table.SortableTableData.SORT_ASCENDING</code> or
	 * <li><code>de.muntjak.tinylookandfeel.table.SortableTableData.SORT_DESCENDING</code>
	 * </ul>
	 * 
	 * @param table the table displaying the data
	 * @throws IllegalArgumentException If any of the arguments is
	 * <code>null</code> or arguments <code>colums</code> and <code>sortingDirections</code>
	 * are of different length.
	 */
	public void sortColumns(int[] columns, int[] sortingDirections, JTable table) {
		if(handler == null) return;
		
		handler.sortColumns(columns, sortingDirections, table);
	}
	
	/**
	 * Sets horizontal alignments of table header renderers where an index
	 * in the argument array corresponds to a column index.
	 * <br>
	 * Note: If the length of the argument array is less than the number
	 * of columns, unspecified columns default to <code>CENTER</code> alignment.
	 * If the length of the argument array is greater than the number
	 * of columns, surplus information will be ignored.
	 * @param alignments array of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>,
     *           <code>RIGHT</code>,
     *           <code>LEADING</code> or
     *           <code>TRAILING</code>.
	 */
	public void setHorizontalAlignments(int[] alignments) {
		if(headerRenderer == null || !rendererInstalled) return;
		
		if(handler != null) {
			if(alignments == null) {
				handler.alignments = null;
			}
			else {
				// make a copy
				handler.alignments = new int[alignments.length];
				System.arraycopy(alignments, 0, handler.alignments, 0, alignments.length);
			}
		}
		
		headerRenderer.setHorizontalAlignments(alignments);
	}
	
	/**
	 * A data container used to store sorting information
	 * between adjacent instantiations (LAF change)
	 * @author Hans Bickel
	 *
	 */
	private class SortingInformation {
		
		private Vector sortedViewColumns;
		private Vector sortedModelColumns;
		private Vector sortingDirections;
		private int[] alignments;
		
		SortingInformation(
			Vector sortedViewColumns,
			Vector sortedModelColumns,
			Vector sortingDirections,
			int[] alignments)
		{
			this.sortedViewColumns = sortedViewColumns;
			this.sortedModelColumns = sortedModelColumns;
			this.sortingDirections = sortingDirections;
			this.alignments = alignments;
		}
	}

	/**
	 * This handler is for table headers whose table model
	 * implements SortableTableData. It is attached to the header
	 * in createUI(JComponent) and keeps track of the sorting
	 * direction and the sorted column.
	 * 
	 * @author Hans Bickel
	 * @since 1.3.6
	 *
	 */
	private class SortableTableHandler implements MouseListener,
		MouseMotionListener, TableColumnModelListener
	{
		private int[] alignments;
		// -1 means that no column is currently a rollover candidate,
		// else it is the index of the rollover column
		private int rolloverColumn = -1;
		private int pressedColumn = -1;
		private Vector sortedViewColumns = new Vector();
		private Vector sortedModelColumns = new Vector();
		private Vector sortingDirections = new Vector();
		private boolean mouseInside = false;
		private boolean mouseDragged = false;
		private boolean inDrag = false;
		private Point pressedPoint;

		void sortColumns(int[] columns, int[] directions, JTable table) {
			if(columns == null) {
				throw new IllegalArgumentException("columns argument may not be null");
			}
			
			if(directions == null) {
				throw new IllegalArgumentException("directions argument may not be null");
			}
			
			if(table == null) {
				throw new IllegalArgumentException("table argument may not be null");
			}
			
			if(columns.length != directions.length) {
				throw new IllegalArgumentException("columns argument and directions argument must be of equal length");
			}

			JTableHeader header = table.getTableHeader();
			SortableTableData model = getTableModel(header);
			
			if(model == null) return;
			
			sortedViewColumns.clear();
			sortedModelColumns.clear();
			sortingDirections.clear();
			
			for(int i = 0; i < columns.length; i++) {
				sortedViewColumns.add(new Integer(columns[i]));
				sortedModelColumns.add(new Integer(getModelColumn(header, columns[i])));
				sortingDirections.add(new Integer(directions[i]));
			}

			header.putClientProperty(SORTED_COLUMN_KEY, vectorToIntArray(sortedViewColumns));
			header.putClientProperty(SORTING_DIRECTION_KEY, vectorToIntArray(sortingDirections));
			
			model.sortColumns(
				vectorToIntArray(sortedModelColumns),
				vectorToIntArray(sortingDirections),
				table);
			header.repaint();
		}

// MouseListener implementation
		
		public void mouseEntered(MouseEvent e) {
			mouseInside = true;
			
			if(mouseDragged) return;
			
			SortableTableData model = getTableModel(e.getSource());
			
			if(model == null) return;

			JTableHeader header = (JTableHeader)e.getSource();
			int viewColumn = header.columnAtPoint(e.getPoint());
			int modelColumn = getModelColumnAt(e);
			
			if(!model.isColumnSortable(modelColumn)) {
				if(rolloverColumn != -1) {
					rolloverColumn = -1;
					header.putClientProperty(ROLLOVER_COLUMN_KEY, null);
				}
			}
			else {
				rolloverColumn = viewColumn;
				header.putClientProperty(ROLLOVER_COLUMN_KEY, new Integer(rolloverColumn));
			}
			
			header.repaint();
		}

		public void mouseExited(MouseEvent e) {
			mouseInside = false;
			JTableHeader header = (JTableHeader)e.getSource();
			
			if(inDrag && header.getReorderingAllowed()) return;
			
			SortableTableData model = getTableModel(e.getSource());
			
			if(model == null) return;

			if(rolloverColumn != -1) {
				rolloverColumn = -1;
				header.putClientProperty(ROLLOVER_COLUMN_KEY, null);
				header.repaint();
			}
		}

		public void mouseReleased(MouseEvent e) {
			inDrag = false;
			
			if(e.isPopupTrigger()) {
//				showHeaderPopup(e);
				return;
			}
			
			if(!mouseInside) {
				mouseDragged = false;
				return;
			}
			
			SortableTableData model = getTableModel(e.getSource());
			
			if(model == null) {
				mouseDragged = false;
				return;
			}

			JTableHeader header = (JTableHeader)e.getSource();
			int viewColumn = header.columnAtPoint(e.getPoint());
			
			if(viewColumn == -1) {
				mouseDragged = false;
				return;
			}

			int modelColumn = getModelColumnAt(e);

			if(!model.isColumnSortable(modelColumn)) {
				if(rolloverColumn != -1) {
					rolloverColumn = -1;
					header.putClientProperty(ROLLOVER_COLUMN_KEY, null);
				}
			}
			else {
				rolloverColumn = viewColumn;
				header.putClientProperty(ROLLOVER_COLUMN_KEY, new Integer(rolloverColumn));
			}

			if(mouseDragged) {
				mouseDragged = false;

				return;
			}
			
			if(!model.isColumnSortable(modelColumn)) return;
			if(pressedColumn != viewColumn) return;

			// for header renderer, sorted column must be viewColumn
			Integer vc = new Integer(viewColumn);

			if(sortedViewColumns.contains(vc)) {
				int index = sortedViewColumns.indexOf(vc);
				
				if(e.isAltDown()) {
					// remove clicked column from sorted columns
					sortedViewColumns.remove(index);
					sortedModelColumns.remove(index);
					sortingDirections.remove(index);
				}
				else if((e.isControlDown() && model.supportsMultiColumnSort()) ||
					sortedModelColumns.size() == 1)
				{
					// change sorting direction of clicked column
					int sortingDirection = ((Integer)sortingDirections.get(index)).intValue();
	
					if(sortingDirection != SortableTableData.SORT_DESCENDING) {
						sortingDirection = SortableTableData.SORT_DESCENDING;
					}
					else {
						sortingDirection = SortableTableData.SORT_ASCENDING;
					}
					
					sortingDirections.remove(index);
					sortingDirections.add(index, new Integer(sortingDirection));
				}
				else {
					// change sorting direction of clicked column
					int sortingDirection = ((Integer)sortingDirections.get(index)).intValue();
	
					if(sortingDirection != SortableTableData.SORT_DESCENDING) {
						sortingDirection = SortableTableData.SORT_DESCENDING;
					}
					else {
						sortingDirection = SortableTableData.SORT_ASCENDING;
					}
					
					// remove all sorted columns and initialize
					// sorted columns with clicked column
					sortedViewColumns.clear();
					sortedModelColumns.clear();
					sortingDirections.clear();
					sortedViewColumns.add(vc);
					sortedModelColumns.add(new Integer(getModelColumn(e, viewColumn)));
					sortingDirections.add(new Integer(sortingDirection));
				}
			}
			else {
				if(e.isAltDown()) {
					// ignore
					return;
				}
				else if(e.isControlDown() && model.supportsMultiColumnSort()) {
					// add clicked column to sorted columns
					sortedViewColumns.add(vc);
					sortedModelColumns.add(new Integer(getModelColumn(e, viewColumn)));
					sortingDirections.add(new Integer(SortableTableData.SORT_ASCENDING));
				}
				else {
					// initialize sorted columns with clicked column
					sortedViewColumns.clear();
					sortedModelColumns.clear();
					sortingDirections.clear();
					sortedViewColumns.add(vc);
					sortedModelColumns.add(new Integer(getModelColumn(e, viewColumn)));
					sortingDirections.add(new Integer(SortableTableData.SORT_ASCENDING));
				}
			}
			
			header.putClientProperty(SORTED_COLUMN_KEY, vectorToIntArray(sortedViewColumns));
			header.putClientProperty(SORTING_DIRECTION_KEY, vectorToIntArray(sortingDirections));
			
			model.sortColumns(
				vectorToIntArray(sortedModelColumns),
				vectorToIntArray(sortingDirections),
				header.getTable());
			header.repaint();
		}
		
		private int[] vectorToIntArray(Vector v) {
			int[] ret = new int[v.size()];
			
			for(int i = 0; i < ret.length; i++) {
				ret[i] = ((Integer)v.get(i)).intValue();
			}
			
			return ret;
		}

		public void mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()) {
//				showHeaderPopup(e);
				return;
			}
			
			JTableHeader header = (JTableHeader)e.getSource();
			pressedPoint = e.getPoint();
			pressedColumn = header.columnAtPoint(pressedPoint);
			mouseDragged = false;
		}
		
		public void mouseClicked(MouseEvent e) {}

// MouseMotionListener implementation
		
		public void mouseDragged(MouseEvent e) {
			SortableTableData model = getTableModel(e.getSource());
			
			if(model == null) return;
			
			inDrag = true;
			JTableHeader header = (JTableHeader)e.getSource();

			if(header.getResizingColumn() != null) {
				// It's a resize, not a column move
				if(!mouseDragged) mouseDragged = true;
			}

			if(!header.getReorderingAllowed()) {
				int modelColumn = getModelColumnAt(e);
	
				if(!model.isColumnSortable(modelColumn)) {
					header.putClientProperty(ROLLOVER_COLUMN_KEY, null);
					header.repaint();
					
					return;
				}
			}

			if(!mouseDragged && isMouseDragged(e.getPoint(), pressedPoint)) {
				mouseDragged = true;
			}

			if(!mouseInside) {
				// ! don't set rolloverColumn to -1
				header.putClientProperty(ROLLOVER_COLUMN_KEY, null);
			}
			else {
				if(!header.getReorderingAllowed()) {
					int viewColumn = header.columnAtPoint(e.getPoint());
					
					if(viewColumn != rolloverColumn) {
						rolloverColumn = viewColumn;
					}
				}
				
				if(rolloverColumn != -1) {
					header.putClientProperty(ROLLOVER_COLUMN_KEY, new Integer(rolloverColumn));
				}
			}
			
			header.repaint();
		}

		public void mouseMoved(MouseEvent e) {
			if(!mouseInside) return;

			JTableHeader header = (JTableHeader)e.getSource();
			int viewColumn = header.columnAtPoint(e.getPoint());
			
			if(viewColumn == -1) return;
			
			SortableTableData model = getTableModel(e.getSource());
			
			if(model == null) return;

			int modelColumn = getModelColumnAt(e);
			
			if(!model.isColumnSortable(modelColumn)) {
				if(rolloverColumn != -1) {
					rolloverColumn = -1;
					header.putClientProperty(ROLLOVER_COLUMN_KEY, null);
					header.repaint();
				}
				
				return;
			}

			// rolloverColumn must be viewColumn, not modelColumn
			if(viewColumn != rolloverColumn) {
				rolloverColumn = viewColumn;
				header.putClientProperty(ROLLOVER_COLUMN_KEY, new Integer(rolloverColumn));
				header.repaint();
			}
		}

// TableColumnModelListener implementation
		public void columnAdded(TableColumnModelEvent e) {
			removeSorting();
		}

		public void columnMoved(TableColumnModelEvent e) {
			if(e.getFromIndex() == e.getToIndex()) return;
			if(header == null) return;

			// update rolloverColumn
			if(rolloverColumn == e.getFromIndex()) {
				rolloverColumn = e.getToIndex();
				
				if(mouseInside) {
					header.putClientProperty(ROLLOVER_COLUMN_KEY, new Integer(rolloverColumn));
				}
			}
			
			// update sorted column(s)
			int[] sc = vectorToIntArray(sortedViewColumns);
			boolean changed = false;

			// Note: With multiple sorted columns it might easily
			// be that both the column at FromIndex and the column
			// at ToIndex are sortable
			for(int i = 0; i < sc.length; i++) {
				if(sc[i] == e.getFromIndex()) {
					sc[i] = e.getToIndex();
					changed = true;
				}
				else if(sc[i] == e.getToIndex()) {
					sc[i] = e.getFromIndex();
					changed = true;
				}
			}
			
			if(changed) {
				sortedViewColumns.clear();
				
				for(int i = 0; i < sc.length; i++) {
					sortedViewColumns.add(new Integer(sc[i]));
				}

				header.putClientProperty(SORTED_COLUMN_KEY, vectorToIntArray(sortedViewColumns));
			}
		}

		public void columnRemoved(TableColumnModelEvent e) {
			removeSorting();
		}
		
		public void columnMarginChanged(ChangeEvent e) {}
		public void columnSelectionChanged(ListSelectionEvent e) {}

// Helper methods
		private void removeSorting() {
			if(header == null) return;
			
			// remove rolloverColumn
			if(rolloverColumn != -1) {
				rolloverColumn = -1;
				header.putClientProperty(ROLLOVER_COLUMN_KEY, new Integer(rolloverColumn));
			}
			
			sortedModelColumns.clear();
			sortedViewColumns.clear();
			sortingDirections.clear();
			header.putClientProperty(SORTING_DIRECTION_KEY, null);
			header.putClientProperty(SORTED_COLUMN_KEY, null);
			header.repaint();
		}
		
		void removeSortingInformation() {
			if(header == null) return;
			
			SortableTableData model = getTableModel(header);
			
			if(model == null) return;
			
			// cache sorting information
			sortingCache.put(header,
				new SortingInformation(
					sortedViewColumns,
					sortedModelColumns,
					sortingDirections,
					alignments));
			
			// We don't have to call removeSorting()
			model.sortColumns(
				new int[] {},
				new int[] {},
				header.getTable());
			header.repaint();
		}
		
		void restoreSortingInformation(JTableHeader header, SortingInformation sortingInfo) {
			if(header == null) return;
			
			SortableTableData model = getTableModel(header);

			if(model == null) return;

			sortedViewColumns = sortingInfo.sortedViewColumns;
			sortedModelColumns = sortingInfo.sortedModelColumns;
			sortingDirections = sortingInfo.sortingDirections;
			alignments = sortingInfo.alignments;
			
			if(alignments != null && alignments.length > 0) {
				setHorizontalAlignments(alignments);
			}
			
			model.sortColumns(
				vectorToIntArray(sortedModelColumns),
				vectorToIntArray(sortingDirections),
				header.getTable());
			header.repaint();
		}

		/**
		 * 
		 * @param source the table header
		 * @return the TableModel to work on or <code>null</code> if
		 * either table is <code>null</code>, table model is <code>null</code>
		 * or table model doesn't implement <code>SortableTableData</code>
		 */
		private SortableTableData getTableModel(Object source) {
			return getTableModel((JTableHeader)source);
		}
		
		/**
		 * If the table model implements SortableTableData,
		 * returns the table model, casted to SortableTableData,
		 * else return null.
		 * If the table model implements SortableTableData then
		 * TinyTableHeaderRenderer is installed on the header.
		 * @param header
		 * @return
		 */
		private SortableTableData getTableModel(JTableHeader header) {
			JTable table = header.getTable();
			
			if(table == null) return null;

			TableModel model = table.getModel();

			if(model == null || !(model instanceof SortableTableData)) return null;
			
			if(!rendererInstalled) {
				rendererInstalled = true;
				TableCellRenderer defaultRenderer =
					(TableCellRenderer)header.getClientProperty(DEFAULT_RENDERER_KEY);
				
				if(defaultRenderer == null) {
					defaultRenderer = header.getDefaultRenderer();
					header.putClientProperty(DEFAULT_RENDERER_KEY, defaultRenderer);
				}
				
				if(headerRenderer == null) {
					headerRenderer = new TinyTableHeaderRenderer();
				}
				
				header.setDefaultRenderer(headerRenderer);
			}

			return (SortableTableData)model;
		}
		
		/**
		 * 
		 * @param e
		 * @return the logical column index
		 */
		private int getModelColumnAt(MouseEvent e) {
			JTableHeader header = (JTableHeader)e.getSource();
			int viewColumn = header.columnAtPoint(e.getPoint());
			
			if(viewColumn == -1) return -1;

			return header.getColumnModel().getColumn(viewColumn).getModelIndex();
		}
		
		/**
		 * 
		 * @param e
		 * @param viewColumn
		 * @return the model column index corresponding to the
		 * view column index
		 */
		private int getModelColumn(MouseEvent e, int viewColumn) {
			if(viewColumn == -1) return -1;
			
			JTableHeader header = (JTableHeader)e.getSource();

			return getModelColumn(header, viewColumn);
		}
		
		private int getModelColumn(JTableHeader header, int viewColumn) {
			return header.getColumnModel().getColumn(viewColumn).getModelIndex();
		}
		
		private boolean isMouseDragged(Point p1, Point p2) {
			if(Math.abs(p1.x - p2.x) >= MINIMUM_DRAG_DISTANCE) return true;
			
			return false;
		}
		
		private void showHeaderPopup(MouseEvent e) {
			final SortableTableData model = getTableModel(e.getSource());
			
			if(model == null) return;
			
			JTableHeader header = (JTableHeader)e.getSource();
			final int viewColumn = header.columnAtPoint(e.getPoint());
			
			JPopupMenu menu = new JPopupMenu();
			JMenu sub = new JMenu("Add");
			JMenuItem item = new JMenuItem("Double Column");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((TinyTableModel)model).addColumn(Double.class, viewColumn);
				}
			});
			sub.add(item);
			
			item = new JMenuItem("Icon Column");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((TinyTableModel)model).addColumn(Icon.class, viewColumn);
				}
			});
			sub.add(item);
			
			item = new JMenuItem("Integer Column");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((TinyTableModel)model).addColumn(Integer.class, viewColumn);
				}
			});
			sub.add(item);
			
			item = new JMenuItem("String Column");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((TinyTableModel)model).addColumn(String.class, viewColumn);
				}
			});
			sub.add(item);
			
			menu.add(sub);
			menu.addSeparator();
			
			item = new JMenuItem("Remove Column");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((TinyTableModel)model).removeColumn(viewColumn);
				}
			});
			if(((TinyTableModel)model).getColumnCount() < 2) {
				item.setEnabled(false);
			}
			menu.add(item);
			
			menu.addSeparator();
			
			item = new JMenuItem("Remove all Rows");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((TinyTableModel)model).removeAllRows();
				}
			});
			if(((TinyTableModel)model).getRowCount() == 0) {
				item.setEnabled(false);
			}
			menu.add(item);
			
			menu.addSeparator();
			
			item = new JMenuItem("Create new Data");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((TinyTableModel)model).createNewData();
				}
			});
			if(((TinyTableModel)model).getRowCount() > 0) {
				item.setEnabled(false);
			}
			menu.add(item);
			
			menu.show(header, e.getX(), 0);
		}
	}
	
	static class TinyTableHeaderRenderer extends DefaultTableCellRenderer implements UIResource {

		private static final Icon arrowNo = new Arrow(true, -1);
		
		// arrows array will be lazily filled
		private static final Icon[][] arrows = new Icon[2][4];
		
		private int[] horizontalAlignments;
		
		/**
		 * Initializes the renderer to a horizontal alignment
		 * of <code>CENTER</code> and a horizontal text position
		 * of <code>LEFT</code>.
		 *
		 */
		public TinyTableHeaderRenderer() {
			setHorizontalAlignment(CENTER);
			setHorizontalTextPosition(LEFT);
		}
		
		/**
		 * Sets horizontal alignments of renderers where an index
		 * in the argument array corresponds to a column index.
		 * @param alignments array of the following constants
	     *           defined in <code>SwingConstants</code>:
	     *           <code>LEFT</code>,
	     *           <code>CENTER</code>,
	     *           <code>RIGHT</code>,
	     *           <code>LEADING</code> or
	     *           <code>TRAILING</code>.
		 */
		public void setHorizontalAlignments(int[] alignments) {
			horizontalAlignments = alignments;
		}

		/**
		 * Returns the default table cell renderer.
		 */
		public Component getTableCellRendererComponent(JTable table, Object value,
	        boolean isSelected, boolean hasFocus, int row, int column)
		{
			LookAndFeel laf = UIManager.getLookAndFeel();

			if(laf == null || !"TinyLookAndFeel".equals(laf.getName())) {
				if(table != null) {
					JTableHeader header = table.getTableHeader();
					
					if(header != null) {
						setBackground(header.getBackground());
					    setForeground(header.getForeground());
					    setFont(header.getFont());
					}
				}
				
				setIcon(null);
				setText((value == null) ? "" : value.toString());
				setBorder(UIManager.getBorder("TableHeader.cellBorder"));
				
				return this;
			}
			
			boolean isRolloverColumn = false;
			Icon icon = arrowNo;
			int sortingDirection = SortableTableData.SORT_ASCENDING;
			int priority = -1;
			boolean paintArrow = false;

			if(table != null) {
				JTableHeader header = table.getTableHeader();
				
				if(header != null) {
					Object o = header.getClientProperty(TinyTableHeaderUI.ROLLOVER_COLUMN_KEY);
					
					if(o != null) {
						int rolloverColumn = ((Integer)o).intValue();
						
						if(rolloverColumn == column) {
							isRolloverColumn = true;
						}
					}
					
					o = header.getClientProperty(TinyTableHeaderUI.SORTED_COLUMN_KEY);
					
					if(o != null) {
						int sc[] = (int[])o;
						priority = -1;
						
						for(int i = 0; i < sc.length; i++) {
							if(sc[i] == column) {
								priority = i;
							}
						}
						
						if(priority > -1) {
							paintArrow = true;
							o = header.getClientProperty(TinyTableHeaderUI.SORTING_DIRECTION_KEY);
							
							if(o != null) {
								int[] sd = (int[])o;
								sortingDirection = sd[priority];
							}
						}
					}

				    if(isRolloverColumn) {
				    	setBackground(Theme.tableHeaderRolloverBackColor.getColor());
				    }
				    else {
				    	setBackground(header.getBackground());
				    }
				    
				    setForeground(header.getForeground());
				    setFont(header.getFont());
				}
				
				TableModel model = table.getModel();
				
				if(model instanceof SortableTableData) {
					if(paintArrow) {
						int pri = Math.min(3, priority);
						
						if(sortingDirection == SortableTableData.SORT_ASCENDING) {
							if(arrows[0][pri] == null) {
								arrows[0][pri] = new Arrow(false, priority);
							}
							
							icon = arrows[0][pri];
						}
						else {
							if(arrows[1][pri] == null) {
								arrows[1][pri] = new Arrow(true, priority);
							}
							
							icon = arrows[1][pri];
						}
					}
					else if(column >= 0) {
						
						int modelColumn = table.getColumnModel().getColumn(column).getModelIndex();
						
						if(!((SortableTableData)model).isColumnSortable(modelColumn)) {
							icon = null;
						}
						
					}
				}
				else {
					icon = null;
					setToolTipText(null);
				}
				
				if(column >= 0) {
					int modelColumn = table.getColumnModel().getColumn(column).getModelIndex();
					
					if(horizontalAlignments != null && horizontalAlignments.length > modelColumn) {
						setHorizontalAlignment(horizontalAlignments[modelColumn]);
					}
				}
			}

			setIcon(icon);
			setText((value == null) ? "" : value.toString());

			if(isRolloverColumn) {
				setBorder(UIManager.getBorder("TableHeader.cellRolloverBorder"));
		    }
		    else {
		    	setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		    }

			return this;
		}

		private static class Arrow implements Icon {

			private static final int height = 11;
			private boolean descending;
			private int priority;

			public Arrow(boolean descending, int priority) {
				this.descending = descending;
				this.priority = Math.min(3, priority);
			}

			public void paintIcon(Component c, Graphics g, int x, int y) {
				if(priority == -1) return;	// empty icon
				
				// In a compound sort, make each successive triangle
				// smaller than the previous one
				int dx = priority;
				int dy = (height - 5 + priority) / 2;

				g.translate(x + dx, y + dy);
				g.setColor(Theme.tableHeaderArrowColor.getColor());
				
				if(descending) {
					switch(priority) {
						case 0:
							g.drawLine(4, 4, 4, 4);
							g.drawLine(3, 3, 5, 3);
							g.drawLine(2, 2, 6, 2);
							g.drawLine(1, 1, 7, 1);
							g.drawLine(0, 0, 8, 0);
							break;
						case 1:
							g.drawLine(3, 3, 3, 3);
							g.drawLine(2, 2, 4, 2);
							g.drawLine(1, 1, 5, 1);
							g.drawLine(0, 0, 6, 0);
							break;
						case 2:
							g.drawLine(2, 2, 2, 2);
							g.drawLine(1, 1, 3, 1);
							g.drawLine(0, 0, 4, 0);
							break;
						case 3:
						default:
							g.drawLine(1, 1, 1, 1);
							g.drawLine(0, 0, 2, 0);
							break;
					}
				}
				else {
					switch(priority) {
						case 0:
							g.drawLine(4, 0, 4, 0);
							g.drawLine(3, 1, 5, 1);
							g.drawLine(2, 2, 6, 2);
							g.drawLine(1, 3, 7, 3);
							g.drawLine(0, 4, 8, 4);
							break;
						case 1:
							g.drawLine(3, 0, 3, 0);
							g.drawLine(2, 1, 4, 1);
							g.drawLine(1, 2, 5, 2);
							g.drawLine(0, 3, 6, 3);
							break;
						case 2:
							g.drawLine(2, 0, 2, 0);
							g.drawLine(1, 1, 3, 1);
							g.drawLine(0, 2, 4, 2);
							break;
						case 3:
						default:
							g.drawLine(1, 0, 1, 0);
							g.drawLine(0, 1, 2, 1);
							break;
					}
				}
				
				g.translate(-(x + dx), -(y + dy));
			}

			public int getIconWidth() {
				return 9;
			}

			public int getIconHeight() {
				return height;
			}
		}
	}
}
