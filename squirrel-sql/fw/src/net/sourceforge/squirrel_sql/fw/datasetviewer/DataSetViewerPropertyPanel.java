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
import java.awt.Font;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;

public class DataSetViewerPropertyPanel extends PropertyPanel implements IDataSetViewerDestination {
    private boolean _showHeadings;
    private ColumnDisplayDefinition[] _hdgs = new ColumnDisplayDefinition[0];
    private List _leftData = new ArrayList();
    private List _rightData = new ArrayList();

    public DataSetViewerPropertyPanel() {
        super();
    }

    public void showHeadings(boolean show) {
        _showHeadings = show;
    }

    public void clear() {
        _leftData.clear();
        _rightData.clear();
    }

    public void setColumnDefinitions(ColumnDisplayDefinition[] hdgs) {
        _hdgs = hdgs;
    }

    public void addRow(String[] row) {
        _leftData.add(row[0]);
        _rightData.add(row[1]);
    }

    public void moveToTop() {
    }

    public void allRowsAdded() {
        for (int i = 0, limit = Math.max(_leftData.size(), _rightData.size());
                i < limit; ++i) {
            JLabel left = new JLabel(i < _leftData.size() ? (String)_leftData.get(i) : " ", SwingConstants.RIGHT);
            JLabel right = new JLabel( i < _rightData.size() ? (String)_rightData.get(i) : " ");
            add(left, right);
        }
    }
}