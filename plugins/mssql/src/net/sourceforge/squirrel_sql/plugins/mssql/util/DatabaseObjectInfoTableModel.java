package net.sourceforge.squirrel_sql.plugins.mssql.util;

/*
 * Copyright (C) 2004 Ryan Walberg <generalpf@yahoo.com>
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

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public class DatabaseObjectInfoTableModel extends AbstractTableModel {
    
    private static final long serialVersionUID = -1879428906496726350L;

    private ArrayList<IDatabaseObjectInfo> _objectInfo;
    
    public DatabaseObjectInfoTableModel() {
        _objectInfo = new ArrayList<IDatabaseObjectInfo>();
    }
    
    public void addElement(IDatabaseObjectInfo oi) {
        _objectInfo.add(oi);
        int size = _objectInfo.size();
        fireTableRowsInserted(size,size);
    }
    
    public boolean removeElement(IDatabaseObjectInfo oi) {
        int index = _objectInfo.indexOf(oi);
        if (index != -1) {
            _objectInfo.remove(oi);
            fireTableRowsDeleted(index,index);
        }
        return (index != -1);
    }
    
    public int getColumnCount() {
        /* one column for the object name, another column for the object's owner. */
        return 2;
    }
    
    public int getRowCount() {
        return _objectInfo.size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return _objectInfo.get(rowIndex);
            case 1:
                return _objectInfo.get(rowIndex).getSchemaName();
            default:
                return null;
        }
    }
    
    /*public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 0);
    }*/
    
    public ArrayList<IDatabaseObjectInfo> getContents() {
        return _objectInfo;
    }
    
}
