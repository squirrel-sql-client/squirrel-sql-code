package net.sourceforge.squirrel_sql.plugins.dataimport;
/*
 * Copyright (C) 2001 Like Gao
 * lgao@gmu.edu
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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;


import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;


/*This table model combine the comoBox with the table to assign the
 *attribut datatype
 *V0.1  11/06/2000
 *Like Gao, lgao@gmu.edu
*/

@Deprecated
public class CombTable extends JScrollPane{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CombTable.class);

	 public String[] names;
    public Object[][] data;
    public CombTable(String[] _names, Object _data[][]) {
        // Create a model of the data.
    names=_names;
    data=_data;
        TableModel dataModel = new AbstractTableModel() {
            // These methods always need to be implemented.
            public int getColumnCount() { return names.length; }
            public int getRowCount() { return data.length;}
            public Object getValueAt(int row, int col) {return data[row][col];}


            // The default implementations of these methods in
            // AbstractTableModel would work, but we can refine them.
            public String getColumnName(int column) {return names[column];}
            public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
            public boolean isCellEditable(int row, int col)
        { if (col == 0)
            return false;
          else
            return true;
        }
            public void setValueAt(Object aValue, int row, int column) {
                System.out.println("Setting value to: " + aValue);
                data[row][column] = aValue;
            }
         };


        // Create the table
        JTable tableView = new JTable(dataModel);
        // Turn off auto-resizing so that we can set column sizes programmatically.
    // In this mode, all columns will get their preferred widths, as set blow.
        tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);


    // Create a combo box to show that you can use one in a table.
    JComboBox comboBox = new JComboBox();
    comboBox.addItem("FLOAT");
    comboBox.addItem("INTEGER");
    comboBox.addItem("VARCHAR(50)");
    comboBox.addItem("VARCHAR(30)");


        TableColumn colorColumn = tableView.getColumn("DataType");
        // Use the combo box as the editor in the "DataType" column.
        colorColumn.setCellEditor(new DefaultCellEditor(comboBox));


        // Set a pink background and tooltip for the Color column renderer.
         DefaultTableCellRenderer colorColumnRenderer = new DefaultTableCellRenderer();
        colorColumnRenderer.setBackground(Color.pink);
		 // i18n[dataimport.clickForCbo=Click for combo box]
		  colorColumnRenderer.setToolTipText(s_stringMgr.getString("dataimport.clickForCbo"));
        colorColumn.setCellRenderer(colorColumnRenderer);
    colorColumn.setPreferredWidth(120);


        // Set a tooltip for the header of the colors column.
    TableCellRenderer headerRenderer = colorColumn.getHeaderRenderer();
    if (headerRenderer instanceof DefaultTableCellRenderer)
	 {
		 // i18n[dataimport.hiMom=Hi Mom!]
		  ((DefaultTableCellRenderer)headerRenderer).setToolTipText(s_stringMgr.getString("dataimport.hiMom"));
	 }


	 // Set the width of the "NewName" column.
		 // i18n[dataimport.newName=New Name]
		  TableColumn newNameColumn = tableView.getColumn(s_stringMgr.getString("dataimport.newName"));
        newNameColumn.setPreferredWidth(100);


    // Show the values in the "Original Name" column in different colors.
		 // i18n[dataimport.origName=Original Name]
			 TableColumn originalColumn = tableView.getColumn(s_stringMgr.getString("dataimport.origName"));
        DefaultTableCellRenderer numberColumnRenderer = new DefaultTableCellRenderer() {
        public void setValue(Object value) {
            setForeground(Color.red);
        }
        };
    //        numberColumnRenderer.setHorizontalAlignment(JLabel.RIGHT);
    //        numbersColumn.setCellRenderer(numberColumnRenderer);
        originalColumn.setPreferredWidth(110);


    setViewportView(tableView);
        setPreferredSize(new Dimension(320, 200));
    }
}






