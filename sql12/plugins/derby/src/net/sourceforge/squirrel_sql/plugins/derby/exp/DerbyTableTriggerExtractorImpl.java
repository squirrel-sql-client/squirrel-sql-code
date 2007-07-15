/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.derby.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

/**
 * Provides the query and parameter binding behavior for Derby's trigger catalog.
 *  
 * @author manningr
 */
public class DerbyTableTriggerExtractorImpl implements ITableTriggerExtractor {

    /** The query that finds the trigger definition */
    private static String SQL = 
        "select tr.TRIGGERNAME " +
        "from SYS.SYSTRIGGERS tr, SYS.SYSTABLES t, SYS.SYSSCHEMAS s " +
        "where tr.TABLEID = t.TABLEID " +
        "and s.SCHEMAID = t.SCHEMAID " +
        "and t.TABLENAME = ? " +
        "and s.SCHEMANAME = ? ";
    
    /**
     * @see net.sourceforge.squirrel_sql.plugins.derby.exp.ITableTriggerExtractor#bindParamters(java.sql.PreparedStatement, net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
     */
    public void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo) 
        throws SQLException 
    {
        pstmt.setString(1, dbo.getSimpleName());
        pstmt.setString(2, dbo.getSchemaName());        
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.derby.exp.ITableTriggerExtractor#getTableTriggerQuery()
     */
    public String getTableTriggerQuery() {
        return SQL;
    }

}
