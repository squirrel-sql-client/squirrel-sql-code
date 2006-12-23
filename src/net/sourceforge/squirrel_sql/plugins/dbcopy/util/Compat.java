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
package net.sourceforge.squirrel_sql.plugins.dbcopy.util;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class is just a pass through at the moment.  It's compatibility function
 * is no longer needed and will soon be removed.
 */
public class Compat {

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(Compat.class);
    
    public static ITableInfo[] getTables(ISession session, 
                                          String catalog, 
                                          String schema, 
                                          String tableName) 
    {
        ITableInfo[] result = new ITableInfo[0];
        
        try {
            SchemaInfo schemaInfo = session.getSchemaInfo();
            result = schemaInfo.getITableInfos(catalog, schema, tableName);
        } catch (Exception e) {
            s_log.error("Encountered unexpected exception when attempting to "+
                        "call schemaInfo.getTables with catalog = "+
                        catalog + " schema = " + schema + " tableName = " +
                        tableName);
              
        }
 
        if (result == null || result.length == 0) {
        	// Fallback to the old method, going directly to the database instead
        	// of using SchemaInfo, since SchemaInfo didn't have it.
        	SQLDatabaseMetaData d = session.getSQLConnection().getSQLMetaData();
        	result = getTables(d, catalog, schema, tableName);
        }
        
        return result;
    }

    private static ITableInfo[] getTables(SQLDatabaseMetaData data,
            							  String catalog, 
            							  String schema, 
            							  String tableName) 
    { 

        ITableInfo[] result = new ITableInfo[0];

        try {
            result = data.getTables(catalog, schema, tableName, null, null);
        } catch (Exception e) {
            s_log.error("Encountered unexpected exception when attempting to "+
                        "call SQLDatabaseMetaData.getTables with catalog = "+
                        catalog + " schema = " + schema + " tableName = " +
                        tableName);
                        
        }
        return result;
    }								
    
    /**
     * Figures out which method of ISession that gets the IObjectTreeAPI is 
     * available and invokes it, returning the result.
     * 
     * @param session
     * @param plugin
     * @return
     */
    public static IObjectTreeAPI getIObjectTreeAPI(ISession session, 
                                                   IPlugin plugin) 
    {
        return session.getObjectTreeAPIOfActiveSessionWindow();
    }
    
    /**
     * Reloads the specified db object using the specified Session.
     * @param session
     * @param info
     */
    public static void reloadSchema(ISession session, 
    								IDatabaseObjectInfo info) 
    {
        session.getSchemaInfo().reload(info);
        session.getSchemaInfo().fireSchemaInfoUpdate();
    }
    
    /**
     * Whether the specified session's database considers the specified string
     * to be a keyword.
     * 
     * @param session
     * @param data
     * @return
     */
    public static boolean isKeyword(ISession session, String data) {
        return session.getSchemaInfo().isKeyword(data);
    }
     
    /**
     * Retrieves whether this session treats mixed case unquoted SQL 
     * identifiers as case insensitive and stores them in upper case.
	 *
     * @param session
     * @return true if so; false otherwise
     * @throws SQLException
     */
    public static boolean storesUpperCaseIdentifiers(ISession session) 
    {
    	boolean result = false;
    	SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
    	try {
    		result = md.storesUpperCaseIdentifiers();
    	} catch (Exception e) {
    		try {
    			DatabaseMetaData dbmd = 
    				session.getSQLConnection().getConnection().getMetaData();
    			result = dbmd.storesUpperCaseIdentifiers();
    		} catch (Exception ex) {
    			s_log.debug(
    				"Unable to determine if the session "+session.getTitle()+
    				" stores uppercase identifiers: "+ex.getMessage());
    		}
    	}
    	return result;
    }
    
    public static boolean isTableTypeDBO(Object o) {
    	if (DatabaseObjectType.TABLE_TYPE_DBO.equals(o)) {
    		return true;
    	}
    	return false;
    }
        
    public static void addToPopupForTableFolder(IObjectTreeAPI api, 
    										    Action action) 
    {
        api.addToPopup(DatabaseObjectType.TABLE_TYPE_DBO, action);
    }
        
}
