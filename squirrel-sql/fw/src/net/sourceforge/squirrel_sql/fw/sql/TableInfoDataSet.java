package net.sourceforge.squirrel_sql.fw.sql;
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
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class TableInfoDataSet implements IDataSet {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        interface ColumnHeadings {
            String PROPERTY = "Property";
            String VALUE = "Value";
        }

        interface RowHeadings {
            String NAME = "Table Name";
            String QUALIFIED_NAME = "Qualified Name";
            String CATALOG = "Catalogue";
            String SCHEMA = "Schema";
            String TYPE = "Type";
            String REMARKS = "Remarks";
        }
        String UNSUPPORTED = "<Unsupported>";
        String UNKNOWN = "<Unknown>";
    }

    private final static String[] s_hdgs = new String[] {
        i18n.ColumnHeadings.PROPERTY,
        i18n.ColumnHeadings.VALUE,
    };

    private DataSetDefinition _dsDef;

    private int _curRow = -1;

    private String[][] _data = new String[][] {
        {i18n.RowHeadings.NAME, null},
        {i18n.RowHeadings.QUALIFIED_NAME, null},
        {i18n.RowHeadings.CATALOG, null},
        {i18n.RowHeadings.SCHEMA, null},
        {i18n.RowHeadings.TYPE, null},
        {i18n.RowHeadings.REMARKS, null},
    };

    public TableInfoDataSet(ITableInfo ti) throws DataSetException {
        super();
        _dsDef = new DataSetDefinition(createColumnDefinitions());
        load(ti);
    }

    public final int getColumnCount() {
        return s_hdgs.length;
    }

    public DataSetDefinition getDataSetDefinition() {
        return _dsDef;
    }

    public synchronized boolean next(IMessageHandler msgHandler) {
        if (_curRow >= _data.length - 1) {
            return false;
        }
        ++_curRow;
        return true;
    }

    public synchronized Object get(int columnIndex) {
        return _data[_curRow][columnIndex];
    }

    private ColumnDisplayDefinition[] createColumnDefinitions() {
        final int columnCount = getColumnCount();
        ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[columnCount];
        for (int i = 0; i < columnCount; ++i) {
            columnDefs[i] = new ColumnDisplayDefinition(100, s_hdgs[i]);
        }
        return columnDefs;
    }

    private void load(ITableInfo ti) {
        _data[0][1] = ti.getSimpleName();
        _data[1][1] = ti.getQualifiedName();
        _data[2][1] = ti.getCatalogName();
        _data[3][1] = ti.getSchemaName();
        _data[4][1] = ti.getType();
        _data[5][1] = ti.getRemarks();

        _curRow = -1;
    }
}
