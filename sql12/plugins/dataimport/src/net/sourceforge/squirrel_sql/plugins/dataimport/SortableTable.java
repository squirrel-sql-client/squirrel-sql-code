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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;


import javax.swing.*;
import javax.swing.table.*;


@Deprecated
public class SortableTable extends JScrollPane{ //JPanel{
    public String[] names;
    public Object[][] data;
    public SortableTable(String[] in_names, Object[][] in_data){
          names = in_names;
          data = in_data;
        // Create a model of the data.
    TableModel dataModel = new AbstractTableModel() {
            // These methods always need to be implemented.
            public int getColumnCount() { return names.length; }
            public int getRowCount() { return data.length;}
            public Object getValueAt(int row, int col) {return data[row][col];}


            // The default implementations of these methods in
            // AbstractTableModel would work, but we can refine them.
            public String getColumnName(int column) {return names[column];}
        public Class getColumnClass(int col) {return getValueAt(0,col).getClass();}
            public boolean isCellEditable(int row, int col) {return true;}
            public void setValueAt(Object aValue, int row, int column) {
                data[row][column] = aValue;
            }
         };


        // Instead of making the table display the data as it would normally with:
        // JTable tableView = new JTable(dataModel);
        // Add a sorter, by using the following three lines instead of the one above.
        TableSorter  sorter = new TableSorter(dataModel);
        JTable    tableView = new JTable(sorter);
    //  tableView = new JTable(sorter);
     tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        sorter.addMouseListenerToHeaderInTable(tableView);


    //  JScrollPane scrollpane= new JScrollPane(tableView);
    //  add(scrollpane);
    //  scrollpane.setPreferredSize(new Dimension(400, 300));
    setViewportView(tableView);
    setPreferredSize(new Dimension(300,300));

        }
}


