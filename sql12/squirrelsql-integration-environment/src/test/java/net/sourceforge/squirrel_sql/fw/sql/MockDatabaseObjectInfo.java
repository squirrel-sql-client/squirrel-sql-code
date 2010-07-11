/*
 * Copyright (C) 2005 Rob Manning
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

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public class MockDatabaseObjectInfo implements IDatabaseObjectInfo {

    private String simpleName = null;
    
    private String schemaName = null;
    
    private String catalogName = null;
    
    public MockDatabaseObjectInfo(String aSimpleName, String aSchemaName, String aCatalog) {
        simpleName = aSimpleName;
        schemaName = aSchemaName;
        catalogName = aCatalog;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo#getCatalogName()
     */
    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String aName) {
        catalogName = aName;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo#getDatabaseObjectType()
     */
    public DatabaseObjectType getDatabaseObjectType() {
        return DatabaseObjectType.TABLE;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo#getQualifiedName()
     */
    public String getQualifiedName() {
        return simpleName;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo#getSchemaName()
     */
    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String aName) {
        schemaName = aName;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo#getSimpleName()
     */
    public String getSimpleName() {
        return simpleName;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(IDatabaseObjectInfo o) {
        // TODO Auto-generated method stub
        System.err.println("MockDatabaseObjectInfo.compareTo: stub not yet implemented");
        return 0;
    }

    public String toString() {
    	StringBuffer result = new StringBuffer();
    	result.append("catalog=");
        result.append(catalogName);
        result.append(" schema=");
        result.append(schemaName);
        result.append(" simpleName=");
        result.append(simpleName);
    	return result.toString();
    }
}
