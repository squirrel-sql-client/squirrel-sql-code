/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.sql;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

public class MockTableInfo extends MockDatabaseObjectInfo implements ITableInfo {

    private String type = null;
    private String remarks = null;
    private ITableInfo[] childTables = null;
    
    public MockTableInfo(String aSimpleName, String aSchemaName, String aCatalog) {
        super(aSimpleName, aSchemaName, aCatalog);
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param childTables the childTables to set
     */
    public void setChildTables(ITableInfo[] childTables) {
        this.childTables = childTables;
    }

    /**
     * @return the childTables
     */
    public ITableInfo[] getChildTables() {
        return childTables;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ITableInfo#getExportedKeys()
     */
    public ForeignKeyInfo[] getExportedKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ITableInfo#getImportedKeys()
     */
    public ForeignKeyInfo[] getImportedKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ITableInfo#setExportedKeys(net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo[])
     */
    public void setExportedKeys(ForeignKeyInfo[] foreignKeys) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ITableInfo#setImportedKeys(net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo[])
     */
    public void setImportedKeys(ForeignKeyInfo[] foreignKeys) {
        // TODO Auto-generated method stub
        
    }    
    

}
