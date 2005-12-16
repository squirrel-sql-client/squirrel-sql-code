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
package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This database expander limits the schemas that are displayed in the object 
 * tree to only those that the user has privileges to access.  In the event that
 * the session account has the DBA privilege, all schemas in the database will 
 * be shown (DBA privilege connotes access to all database schemas). 
 */
public class DefaultDatabaseExpander extends DatabaseExpander {

    /** logger for this class */
    private static ILogger s_log =
        LoggerController.createLogger(DefaultDatabaseExpander.class);
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DefaultDatabaseExpander.class);  
    
    /** SQL to find schemas to which the logged in user has access */
    private static String SCHEMA_ACCESS_SQL =
        "SELECT DISTINCT OWNER FROM USER_TAB_PRIVS";
    
    /** SQL to determine whether or not this account is a DBA account */
    private static String DBA_ROLE_SQL =
        "SELECT GRANTED_ROLE FROM USER_ROLE_PRIVS";
        
    
    public DefaultDatabaseExpander(ISession session) {
        super(session);
    }
    
    protected List createSchemaNodes(ISession session, 
                                     SQLDatabaseMetaData md,
                                     String catalogName)
    throws SQLException
    {
        final List childNodes = new ArrayList();
        if (session.getProperties().getLoadSchemasCatalogs())
        {
            final String[] schemas = getAccessibleSchemas(session);
            final String[] schemaPrefixArray =
                session.getProperties().getSchemaPrefixArray();
            for (int i = 0; i < schemas.length; ++i)
            {
                boolean found = (schemaPrefixArray.length > 0) ? false : true;
                for (int j = 0; j < schemaPrefixArray.length; ++j)
                {
                    if (schemas[i].startsWith(schemaPrefixArray[j]))
                    {
                        found = true;
                        break;
                    }
                }
                if (found)
                {
                    IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName, null,
                            schemas[i],
                            DatabaseObjectType.SCHEMA, md);
                    childNodes.add(new ObjectTreeNode(session, dbo));
                }
            }
        }
        return childNodes;
    }
    
    /**
     * Returns an array of schema names that represent schemas in which there
     * exist tables that the user associated with the specified session has 
     * privilege to access.
     * 
     * @param session the session to retrieve schemas for
     * 
     * @return an array of strings representing the names of accessible schemas
     */
    private static String[] getAccessibleSchemas(ISession session) {
        String[] result = null;
        ResultSet rs = null;
        Statement stmt = null;
        SQLConnection con = session.getSQLConnection();
        SQLDatabaseMetaData md = con.getSQLMetaData();
        String currentUserName = null;
        try {
            if (hasSystemPrivilege(session)) {
                result = md.getSchemas();
            } else {
                currentUserName = md.getUserName();
                stmt = con.getConnection().createStatement();
                rs = stmt.executeQuery(SCHEMA_ACCESS_SQL);
                ArrayList tmp = new ArrayList();
                while (rs.next()) {
                    tmp.add(rs.getString(1));
                }
                if (currentUserName != null && !tmp.contains(currentUserName)) {
                    tmp.add(currentUserName);
                }        
                tmp.add("SYS");
                result = (String[])tmp.toArray(new String[tmp.size()]);
            }
        } catch (SQLException e) {
            // i18n[DefaultDatabaseExpander.error.retrieveschemaprivs=Unable to retrieve schema privileges]
            String msg = 
                s_stringMgr.getString("DefaultDatabaseExpander.error.retrieveschemaprivs");
            s_log.error(msg, e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
        }
        return result;
    }
    
    /**
     * Checks whether or not the user associated with the specified session has 
     * been granted the DBA privilege.  
     * 
     * @param session the session to check 
     * @return true if the user has the DBA privilege; false otherwise.
     */
    private static boolean hasSystemPrivilege(ISession session) {
        boolean result = false;
        Statement stmt = null;
        ResultSet rs = null;
        SQLConnection con = session.getSQLConnection();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(DBA_ROLE_SQL);
            while (rs.next()) {
                String role = rs.getString(1);
                if ("DBA".equalsIgnoreCase(role)) {
                    result = true;
                    break;
                }
            }
        } catch (SQLException e) {
            // i18n[DefaultDatabaseExpander.error.retrieveuserroles=Unable to retrieve user roles]
            String msg = 
                s_stringMgr.getString("DefaultDatabaseExpander.error.retrieveuserroles");
            s_log.error(msg, e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}            
        }
        return result;
    }
}
