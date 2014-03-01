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
package net.sourceforge.squirrel_sql.plugins.informix.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Provides the query and parameter binding behavior for H2's trigger catalog.
 *  
 * @author manningr
 */
public class InformixTableTriggerExtractorImpl implements ITableTriggerExtractor {

    /** Logger for this class */
    private final static ILogger s_log = 
        LoggerController.createLogger(InformixTableTriggerExtractorImpl.class);
                
    /** The query that finds the triggers for a given table */
    private static String query = 
        "select T1.trigname " +
        "from informix.systriggers AS T1, informix.systables AS T2 " +
        "where T2.tabid = T1.tabid " +
        "and T2.tabname = ? ";
        
            
    /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor#bindParamters(java.sql.PreparedStatement, net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
     */
    public void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo) 
        throws SQLException 
    {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Binding table name "+dbo.getSchemaName()+
                        " as first bind value");            
        }        
        pstmt.setString(1, dbo.getSimpleName());
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor#getTableTriggerQuery()
     */
    public String getTableTriggerQuery() {
        return query;
    }

}
