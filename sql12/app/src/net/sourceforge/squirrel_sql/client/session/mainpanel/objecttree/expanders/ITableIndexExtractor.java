package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public interface ITableIndexExtractor {

    /**
     * Returns the SQL to be used as a PreparedStatement which is necessary to 
     * get the trigger definition for a table.
     *  
     * @return
     */
    String getTableIndexQuery();
    
    
    /**
     * Binds an parameter values into the PreparedStatement that was created 
     * from the SQL returned by getTableTriggerQuery();
     * 
     * @param pstmt the PreparedStatement to bind parameter values to.
     * 
     * @param dbo the DatabaseObjectInfo to get info like schema, catalog and 
     *            simple names from.
     * @throws SQLException if an error occurs while binding variable values.            
     */
    void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo)
        throws SQLException;
}
