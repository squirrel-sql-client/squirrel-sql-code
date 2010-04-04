/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * NonSortableTableModel
 * @author Hans Bickel
 *
 */
public class NonSortableTableModel extends AbstractTableModel {

	public NonSortableTableModel() {
		this(false);
	}
	
	// data holds a vector of Record objects where
	// one Record represents one row of data
	protected Vector data;
	private Record template;
	private Vector columnNames;
	private boolean sortable;
	
	protected NonSortableTableModel(boolean sortable) {
		super();
		
		this.sortable = sortable;
		createData();
	}

	private void createData() {
		columnNames = new Vector();
		
		columnNames.add("C1");
		columnNames.add("C2");
		columnNames.add("C3");
		columnNames.add("C4");
		columnNames.add("C5");
		
		TableColorIcon.reset();
		template = null;
		data = new Vector();
		
		for(int i = 0; i < 50; i++) {
			Record rec = new Record(i + 1);
			
			data.add(rec);
			
			if(template == null) {
				template = rec;
			}
		}
	}

// AbstractTableModel implementation
	
	public int getColumnCount() {
		if(template == null) return 0;
		
		return template.getColumnCount();
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int row, int column) {
		Record rec = (Record)data.get(row);
		
		return rec.getValueAt(column);
	}
	
	public void setValueAt(Object aValue, int row, int column) {
		if(column == 4) {
			Record rec = (Record)data.get(row);
			rec.setValueAt(aValue, column);
			fireTableRowsUpdated(row, row);
		}
	}
	
	public Class getColumnClass(int column) {
		if(template == null) return Object.class;
		
		return template.getColumnClass(column);
	}
	
	public String getColumnName(int column) {
		return (String)columnNames.get(column);
	}
	
	public boolean isCellEditable(int row, int col) {
		return (col == 4);	// Boolean column is editable
	}
	
// Some methods for testing
	
	public void addColumn(Class cl, int column) {
		columnNames.add(column, "N" + (column + 1));
		
		Iterator ii = data.iterator();
		while(ii.hasNext()) {
			Record rec = (Record)ii.next();
			
			rec.addColumn(cl, column);
		}
		
		fireTableStructureChanged();
	}
	
	public void removeColumn(int column) {
		columnNames.remove(column);
		
		Iterator ii = data.iterator();
		while(ii.hasNext()) {
			Record rec = (Record)ii.next();
			
			rec.removeColumn(column);
		}
		
		fireTableStructureChanged();
	}
	
	public void removeAllRows() {
		data.clear();
		fireTableDataChanged();
	}
	
	public void createNewData() {
		createData();
		
		// might be that number of columns changed
		fireTableStructureChanged();
	}

	/**
	 * A Record represents one row of data.
	 * By default a Record consists of 4 values but
	 * columns can be added and can be removed.
	 * 
	 * @author Hans Bickel
	 *
	 */
	protected class Record {

		private Vector values;
		private int oldRow, newRow;
		
		protected Record(int index) {
			values = new Vector();
			Object v1 = new Integer(index);
			Object v2 = createRandomIcon();
			Object v3 = null;
			
			if(sortable) {
				if(index == 1) {
					v3 = "Sortable";
				}
				else if(index == 2) {
					v3 = "Table";
				}
				else if(index == 3) {
					v3 = "Data";
				}
				else {
					v3 = createRandomString();
				}
			}
			else {
				if(index == 1) {
					v3 = "Non-";
				}
				else if(index == 2) {
					v3 = "sortable";
				}
				else if(index == 3) {
					v3 = "Table";
				}
				else if(index == 4) {
					v3 = "Data";
				}
				else {
					v3 = createRandomString();
				}
			}
			
			Object v4 = createRandomInteger();
			Object v5 = createRandomBoolean();
			
			values.add(v1);
			values.add(v2);
			values.add(v3);
			values.add(v4);
			values.add(v5);
		}
		
		public Object getValueAt(int column) {
			if(column < 0 || column >= values.size()) return null;
			
			return values.get(column);
		}
		
		public void setValueAt(Object v, int column) {
			values.remove(column);
			values.add(column, v);
		}
		
		public Class getColumnClass(int column) {
			if(column < 0 || column >= values.size()) return Object.class;
			
			return values.get(column).getClass();
		}
		
		public void addColumn(Class cl, int column) {
			if(Double.class.equals(cl)) {
				values.add(column, createRandomDouble());
			}
			else if(Icon.class.equals(cl)) {
				values.add(column, createRandomIcon());
			}
			else if(Integer.class.equals(cl)) {
				values.add(column, createRandomInteger());
			}
			else if(String.class.equals(cl)) {
				values.add(column, createRandomString());
			}
			else if(Boolean.class.equals(cl)) {
				values.add(column, createRandomBoolean());
			}
		}
		
		public void removeColumn(int column) {
			values.remove(column);
		}
		
		public int getColumnCount() {
			return values.size();
		}

		private String createRandomString() {
			// return a string of 3 random characters
			char[] a = new char[3];
			
			for(int i = 0; i < a.length; i++) {
				a[i] = createRandomChar();
			}

			return new String(a);
		}
		
		private char createRandomChar() {
			// return one uppercase letter
			return (char)('A' + (char)(Math.random() * 3));
		}
		
		private Boolean createRandomBoolean() {
			return new Boolean(Math.random() >= 0.5);
		}

		private Double createRandomDouble() {
			// return a Double between 0.00 and 99.00
			int x = (int)(Math.random() * 10000);
			return new Double(x / 100.0);
		}
		
		private Integer createRandomInteger() {
			// return an Integer between 1 and 20
			return new Integer((int)(Math.random() * 20) + 1);
		}
		
		private Icon createRandomIcon() {
			return new TableColorIcon();
		}

		
		public int getNewRow() {
			return newRow;
		}

		
		public void setNewRow(int newRow) {
			this.newRow = newRow;
		}

		
		public int getOldRow() {
			return oldRow;
		}

		
		public void setOldRow(int oldRow) {
			this.oldRow = oldRow;
		}
	}
	
	/**
	 * A 24x12 sized icon of random color.
	 */
	public static final class TableColorIcon implements Icon {
		
		private Color color;
		private static int hue = 0;
		
		TableColorIcon() {
			color = Color.getHSBColor((float)(hue / 360.0), 0.5f, 0.9f);
			hue += 360 / 20;
		}
		
		static void reset() {
			hue = 0;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.DARK_GRAY);
			g.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
			
			g.setColor(color);
			g.fillRect(x + 1, y + 1, getIconWidth() - 2, getIconHeight() - 2);
		}

		public int getIconWidth() {
			return 24;
		}

		public int getIconHeight() {
			return 12;
		}

		public int compareTo(Object o) {
			if(o == null) return 1;
			if(!(o instanceof TableColorIcon)) return 1;
			
			Color c2 = ((TableColorIcon)o).color;
			
			if(color.getRGB() == c2.getRGB()) {
				return 0;
			}
			else if(color.getRGB() > c2.getRGB()) {
				return 1;
			}
			
			return -1;
		}
	}
}
