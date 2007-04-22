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
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.MockSession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.MockDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.DBUtil;

public class MockSessionInfoProvider implements SessionInfoProvider {

    ISession sourceSession = null;
    
    ISession destSession = null;
    
    ArrayList<ITableInfo> selectedDatabaseObjects = new ArrayList<ITableInfo>();
    
    IDatabaseObjectInfo destSelectedDatabaseObject = null;
    
    ResourceBundle bundle = null;
    
    String sourceSchema = null;
    String sourceCatalog = null;
    String destSchema=null;
    String destCatalog = null;
    
    boolean dropOnly = false;
    
    public MockSessionInfoProvider(String propertyFile, 
    							   boolean dropOnly) 
    	throws Exception 
    {
        this.dropOnly = dropOnly;
        initialize(propertyFile);
    }
    
    private void initialize(String propertyFile) throws Exception {
        bundle = ResourceBundle.getBundle(propertyFile);
        String sourceDriver = bundle.getString("sourceDriver");
        String sourceJdbcUrl = bundle.getString("sourceJdbcUrl"); 
        String sourceUser = bundle.getString("sourceUser");
        String sourcePass = bundle.getString("sourcePass");
        
        sourceSession = new MockSession(sourceDriver,
                                        sourceJdbcUrl,
                                        sourceUser,
                                        sourcePass);
        sourceSchema = fixCase(bundle.getString("sourceSchema"),
                               sourceSession);
        sourceCatalog = fixCase(bundle.getString("sourceCatalog"),
                				sourceSession);
        String destDriver = bundle.getString("destDriver");
        String destJdbcUrl = bundle.getString("destJdbcUrl"); 
        String destUser = bundle.getString("destUser");
        String destPass = bundle.getString("destPass");
        destSession = new MockSession(destDriver,
                                      destJdbcUrl,
                                      destUser,
                                      destPass);
        destCatalog = fixCase(bundle.getString("destCatalog"),
				  		      destSession);
        destSchema = fixCase(bundle.getString("destSchema"),
				  			 destSession);
        initializeDBObjs();
    }
    
    private void initializeDBObjs() 
        throws SQLException, UserCancelledOperationException 
    {
        List<ITableInfo> tables = getTableNames(sourceSession);
        String destSchema = fixCase(bundle.getString("destSchema"), 
                                    destSession);
        if (tables.size() == 0) {
        	throw new SQLException("No tables found to copy");
        }
        
        for (ITableInfo info : tables) {
        	String sourceTable = fixCase(info.getSimpleName(), sourceSession);
        	if (!shouldIncludeTable(sourceTable)) {
        		continue;
        	}
            dropDestinationTable(sourceTable, destSchema);
            if (!dropOnly) {
            	selectedDatabaseObjects.add(info);
            }
		}
        /*
        if (DialectFactory.isMySQLSession(sourceSession)) {
            destSelectedDatabaseObject = 
                new MockDatabaseObjectInfo(destSchema, null, destSchema);
        } else {
            destSelectedDatabaseObject = 
                new MockDatabaseObjectInfo(destSchema, destSchema, null);            
        }
        */
        destSelectedDatabaseObject = new MockDatabaseObjectInfo(destSchema, destSchema, destCatalog);
        System.out.println("destSelectedDatabaseObject: "+destSelectedDatabaseObject);
    }
    
    private boolean shouldIncludeTable(String tableName) {
    	boolean result = true;
        // Hack to deal with Ingres IIE* meta tables.
        if (tableName.startsWith("IIE") 
                || tableName.startsWith("iie")) 
        {
            result = false;
        }
        // Hack to deal with Axion AXION_* tables.
        if (tableName.startsWith("AXION") 
                || tableName.startsWith("axion")) 
        {
        	result = false;
        }
        // Hack to deal with Firebird's RDB meta tables.
        if (tableName.startsWith("RDB$")) {
        	result = false;
        }
        // Hack to deal with Sybase's sys tables
        if (tableName.startsWith("sys")) {
        	//result = false;
        }

    	return result;
    }
    
    private void dropDestinationTable(String tableName, String schema) 
        throws SQLException, UserCancelledOperationException 
    {
        String destTable = fixCase(tableName, destSession);
        if (DialectFactory.isFrontBase(destSession.getMetaData())) {
            DBUtil.dropTable(destTable, schema, null, destSession, true, DialectFactory.DEST_TYPE);
        } else {
            DBUtil.dropTable(destTable, schema, null, destSession, false, DialectFactory.DEST_TYPE);
        }
    }
    
    private List<ITableInfo> getTableNames(ISession sourceSession) throws SQLException {
        List<ITableInfo> result = null;
        String tableStr = bundle.getString("tablesToCopy");
        if ("*".equals(tableStr)) {
            result = getAllTables(sourceSession);
        } else {
        	result = new ArrayList<ITableInfo>();
            String[] tableNames = tableStr.split(",");
            for (int i = 0; i < tableNames.length; i++) {
				String tableName = tableNames[i];
				TableInfo info = new TableInfo(sourceCatalog, 
											   sourceSchema,
											   tableName,
											   "TABLE",
											   "",
											   null);
											   
				result.add(info);
			}
        }
        return result;
    }
    
    private List<ITableInfo> getAllTables(ISession sourceSession) throws SQLException {
        ISQLConnection sourceConn = sourceSession.getSQLConnection();
        SQLDatabaseMetaData data = sourceConn.getSQLMetaData(); 
        ITableInfo[] tableInfos = data.getTables(sourceCatalog, sourceSchema, "%", new String[] {"TABLE"}, null);
        
        ArrayList<ITableInfo> tables = new ArrayList<ITableInfo>();
        for (int i = 0; i < tableInfos.length; i++) {
            String tiSchema = tableInfos[i].getSchemaName();
            if (sourceSchema.equals(tiSchema)
                    || ("".equals(sourceSchema) && tiSchema == null) ) 
            {
                if (tableInfos[i].getDatabaseObjectType() == DatabaseObjectType.TABLE) {
                    System.out.println("Adding table "+tableInfos[i].getSimpleName());
                    tables.add(tableInfos[i]);
                }
            }
        }
        return tables;
    }
    
    private String fixCase(String identifier, ISession session) 
        throws SQLException 
    {
        ISQLConnection con = session.getSQLConnection();
        String result = identifier;
        if (con.getSQLMetaData().getJDBCMetaData().storesUpperCaseIdentifiers()
                && !DialectFactory.isFrontBase(session.getMetaData())) {
            result = identifier.toUpperCase();
        }
        return result;
    }
    
    public void setCopySourceSession(ISession session) {
        sourceSession = session;
    }

    public ISession getCopySourceSession() {
        return sourceSession;
    }

    public IDatabaseObjectInfo[] getSourceSelectedDatabaseObjects() {
        int size = selectedDatabaseObjects.size();
        IDatabaseObjectInfo[] result = new IDatabaseObjectInfo[size];
        Iterator i = selectedDatabaseObjects.iterator();
        int index = 0;
        while (i.hasNext()) { 
            result[index++] = (IDatabaseObjectInfo)i.next();
        }
        return result;
    }

    public void setDestCopySession(ISession session) {
        destSession = session;
    }

    public ISession getCopyDestSession() {
        return destSession;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getDestSelectedDatabaseObject()
     */
    public IDatabaseObjectInfo getDestSelectedDatabaseObject() {
        return destSelectedDatabaseObject;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#setDestSelectedDatabaseObject(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
     */
    public void setDestSelectedDatabaseObject(IDatabaseObjectInfo info) {
        destSelectedDatabaseObject = info;
    }
    
    

}
