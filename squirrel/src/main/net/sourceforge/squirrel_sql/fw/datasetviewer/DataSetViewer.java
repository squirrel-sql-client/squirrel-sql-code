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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class DataSetViewer {

    private static IColumnRenderer s_dftColumnRenderer = new DefaultColumnRenderer();
    private static IDataSetViewerDestination s_nullResults = new NullDataSetViewerDestination();

    private boolean _showAllColumns = true;

    private IDataSetViewerDestination _dest;

    public DataSetViewer() {
        super();
        setDestination(s_nullResults);
    }

    public DataSetViewer(IDataSetViewerDestination dest) {
        super();
        setDestination(dest);
    }

    public void setDestination(IDataSetViewerDestination dest) {
        _dest = dest != null ? dest : s_nullResults;
    }

    public synchronized void show(IDataSet ds) throws DataSetException {
        show(ds, null);
    }

    public synchronized void show(IDataSet ds, IMessageHandler msgHandler) throws DataSetException {
        _dest.clear();
        _dest.setColumnDefinitions(ds.getDataSetDefinition().getColumnDefinitions());
        final int colCount = ds.getColumnCount();
        while (ds.next(msgHandler)) {
            addRow(ds, colCount);
        }
        _dest.allRowsAdded();
        _dest.moveToTop();
    }

    protected void clearDestination() {
        _dest.clear();
    }

    protected void addRow(IDataSet ds, int columnCount) throws DataSetException {
        String[] row = new String[columnCount];
        for (int i = 0; i < columnCount; ++i) {
            IColumnRenderer renderer = getColumnRenderer(i);
            if (renderer != null) {
                Object obj = ds.get(i);
                if (obj != null) {
                    row[i] = renderer.renderObject(obj);
                } else {
                    row[i] = renderer.renderNull();
                }
            }
        }
        addRow(row);
    }

    protected void addRow(String[] row) {
        _dest.addRow(row);
    }

    private IColumnRenderer getColumnRenderer(int idx) {
        return s_dftColumnRenderer;
    }

    protected static class DefaultColumnRenderer implements IColumnRenderer {
        public String renderObject(Object obj) {
            return obj.toString();
        }
        public String renderNull() {
            return "<null>"; // i18n
        }

    }

    private static class NullDataSetViewerDestination implements IDataSetViewerDestination {
        public void clear() {
        }

        public void setColumnDefinitions(ColumnDisplayDefinition[] hdgs) {
        }

        public void showHeadings(boolean show) {
        }

        public void addRow(String[] row) {
        }

        public void allRowsAdded() {
        }

        public void moveToTop() {
        }
    }

}