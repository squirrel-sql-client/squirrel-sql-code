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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;

public class DataSetViewerTablePanel extends JTable implements IDataSetViewerDestination {
    private boolean _showHeadings = true;
    private MyTableModel _model;

    private ColumnDisplayDefinition[] _hdgs;

    private final int _multiplier;

    /** Popup menu for table component. */
    private TablePopupMenu _tablePopupMenu;

    private static final String data = "THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG";

    public DataSetViewerTablePanel() {
        super(new MyTableModel());
        _model = (MyTableModel)getModel();
        _multiplier = Toolkit.getDefaultToolkit().getFontMetrics(getFont()).stringWidth(data) / data.length();
        createUserInterface();
    }

    public void showHeadings(boolean show) {
        _showHeadings = show;
    }

    public void clear() {
        _model.clear();
        _model.fireTableDataChanged();
    }

    public void setColumnDefinitions(ColumnDisplayDefinition[] hdgs) {
        _model.setHeadings(hdgs);
        setColumnModel(createColumnModel(hdgs));
    }

    public void addRow(String[] row) {
        _model.addRow(row);
    }

    public void moveToTop() {
        if (getRowCount() > 0) {
            setRowSelectionInterval(0, 0);
        }
    }

    public void allRowsAdded() {
        _model.allRowsAdded();
    }

    /**
     * Display the popup menu for this component.
     */
    protected void displayPopupMenu(MouseEvent evt) {
        _tablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }

    private TableColumnModel createColumnModel(ColumnDisplayDefinition[] hdgs) {
        _hdgs = hdgs;
        TableColumnModel cm = new DefaultTableColumnModel();
        for (int i = 0; i < hdgs.length; ++i) {
            ColumnDisplayDefinition colDef = hdgs[i];
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
        setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);
        setCellSelectionEnabled(true);
        getTableHeader().setResizingAllowed(true);
        getTableHeader().setReorderingAllowed(true);
        setAutoCreateColumnsFromModel(false);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        _tablePopupMenu = new TablePopupMenu();
        _tablePopupMenu.setTable(this);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    DataSetViewerTablePanel.this.displayPopupMenu(evt);
                }
            }
            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    DataSetViewerTablePanel.this.displayPopupMenu(evt);
                }
            }
        });

    }

    private static final class MyTableModel extends AbstractTableModel {

        private List _data = new ArrayList();
        private ColumnDisplayDefinition[] _hdgs;

        MyTableModel() {
            super();
        }

        public Object getValueAt(int row, int col) {
            return ((String[])_data.get(row))[col];
        }

        public int getRowCount() {
            return _data.size();
        }

        public int getColumnCount() {
            return _hdgs != null ? _hdgs.length : 0;
        }

        public String getColumnName(int col) {
            return _hdgs != null ? _hdgs[col].getLabel() : super.getColumnName(col);
        }

        void setHeadings(ColumnDisplayDefinition[] hdgs) {
            _hdgs = hdgs;
        }

        public void addRow(String[] row) {
            _data.add(row);
        }

        void clear() {
            _data.clear();
        }

        public void allRowsAdded() {
            fireTableStructureChanged();
        }
    }
}
