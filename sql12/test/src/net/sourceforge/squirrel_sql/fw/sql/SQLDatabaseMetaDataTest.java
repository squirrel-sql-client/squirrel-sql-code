package net.sourceforge.squirrel_sql.fw.sql;
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
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;


/**
 * Test case for SQLDatabaseMetaData class.
 * 
 * @author manningr
 */
public class SQLDatabaseMetaDataTest extends BaseSQuirreLTestCase {

	SQLDatabaseMetaData iut = null;
	
	/* Mock Objects */
	Connection mockConnection = null;
    ISQLConnection mockSqlConnection = null;
    DatabaseMetaData mockDatabaseMetaData = null;
    
	protected void setUp() throws Exception {
		super.setUp();
        
        mockDatabaseMetaData = createMock(DatabaseMetaData.class);
        expect(mockDatabaseMetaData.getDatabaseProductName()).andReturn("PostgreSQL");
        expect(mockDatabaseMetaData.getDatabaseProductVersion()).andReturn("8.1.8");

        /* Build the table types returned by PostgreSQL */
        ResultSet mockTableTypeResultSet = 
            buildVarcharResultSet(new String[] { "SYSTEM INDEX", 
                    "SYSTEM VIEW", "SYSTEM TABLE", "SYSTEM TOAST INDEX", 
                    "SYSTEM TOAST TABLE", "SYSTEM VIEW", "TABLE", 
                    "TEMPORARY INDEX", "TEMPORARY TABLE", "VIEW"});        
        expect(mockDatabaseMetaData.getTableTypes()).andReturn(mockTableTypeResultSet);
        
        /* The first time that catalogs are asked for, return just one */
        ResultSet catalogResultSet1 = 
            buildVarcharResultSet(new String[] { "aCatalog" });
        expect(mockDatabaseMetaData.getCatalogs()).andReturn(catalogResultSet1);
        
        /* The second time that catalogs are asked for, return two */
        ResultSet catalogResultSet2 = 
            buildVarcharResultSet(new String[] { "aCatalog", "aCatalog2" });
        expect(mockDatabaseMetaData.getCatalogs()).andReturn(catalogResultSet2);
        
        /* The first time that schemas are asked for, return just one */
        ResultSet schemaResultSet1 = 
            buildVarcharResultSet(new String[] { "aSchema" });
        expect(mockDatabaseMetaData.getSchemas()).andReturn(schemaResultSet1);
        
        /* The second time that schemas are asked for, return two */
        ResultSet schemaResultSet2 = 
            buildVarcharResultSet(new String[] { "aSchema", "aSchema2" });
        expect(mockDatabaseMetaData.getSchemas()).andReturn(schemaResultSet2);
        replay(mockDatabaseMetaData);

        mockConnection = createMock(Connection.class);
        expect(mockConnection.getMetaData()).andReturn(mockDatabaseMetaData).anyTimes();
        replay(mockConnection);        
        
        mockSqlConnection = createMock(ISQLConnection.class);
        expect(mockSqlConnection.getConnection()).andReturn(mockConnection).anyTimes();
        replay(mockSqlConnection);
        
        
        iut = new SQLDatabaseMetaData(mockSqlConnection);
	}

	public void testGetSchemas() {
		
		try {
			// Check to be sure we get only one schema
			String[] currentSchemas = iut.getSchemas();
			assertEquals(1, currentSchemas.length);
						
			// Now, check to be sure we get both schemas.
			currentSchemas = iut.getSchemas();
			assertEquals(2, currentSchemas.length);
		} catch (SQLException e) {
			fail("Unexpected exception:  "+e.getMessage());
		}	
			
	}

	public void testGetCatalogs() {
		try {
            // Check to be sure we get only one schema
			String[] currentCatalogs = iut.getCatalogs();
			assertEquals(1, currentCatalogs.length);
            			
			// Now, check to be sure we get both catalogs.
			currentCatalogs = iut.getCatalogs();
			assertEquals(2, currentCatalogs.length);
		} catch (SQLException e) {
			fail("Unexpected exception:  "+e.getMessage());
		}
	}

    private ResultSet buildVarcharResultSet(String[] values) throws SQLException {
        ResultSetMetaData rsmd = createMock(ResultSetMetaData.class);
        expect(rsmd.getColumnCount()).andReturn(1);
        expect(rsmd.getColumnType(1)).andReturn(java.sql.Types.VARCHAR).anyTimes();
        expect(rsmd.getColumnTypeName(1)).andReturn("varchar").anyTimes();
        replay(rsmd);
        ResultSet rs = createMock(ResultSet.class);
        expect(rs.getMetaData()).andReturn(rsmd);
        for (String value : values) {
            expect(rs.next()).andReturn(true);
            expect(rs.getString(1)).andReturn(value);    
            expect(rs.wasNull()).andReturn(false);
        }
        expect(rs.next()).andReturn(false);
        rs.close();
        replay(rs);
        return rs;
    }
	
	
    public void testPGGetTableTypes() {
        try {
            String[] tableTypes = iut.getTableTypes();
            for (int i = 0; i < tableTypes.length; i++) {
                String type = tableTypes[i];
                assertFalse(
                    "'SYSTEM INDEX' is a type returned from " +
                    "SQLDatabaseMetaData.getTableTypes for PostgreSQL - " +
                    "it should not be.", 
                    "SYSTEM INDEX".equals(type));
            }
        } catch (SQLException e) {
            fail("Unexpected exception: "+e.getMessage());
        }
    }
    
    /**
     * Test for bug 1716859 (Can't see data in content tab or row count tab)
     * SQLServer with a dash in the name needs to be quoted.
     * @throws SQLException
     */
    public void testGetIdentifierQuoteStringMSSQL() throws SQLException {
        Connection con = createNiceMock(Connection.class);
        DatabaseMetaData md = createNiceMock(DatabaseMetaData.class);
        expect(md.getIdentifierQuoteString()).andReturn("foo").anyTimes();
        expect(md.getDatabaseProductName())
                    .andReturn("microsoft")
                    .andReturn("sybase")
                    .andReturn("adaptive")
                    .andReturn("sql server");
        expect(con.getMetaData()).andReturn(md).anyTimes();
        replay(con);
        replay(md);
        SQLConnection sqlcon = new SQLConnection(con, null, null);
        SQLDatabaseMetaData sqlmd = new SQLDatabaseMetaData(sqlcon);
        try {
            String quoteString = sqlmd.getIdentifierQuoteString();
            assertEquals("foo", quoteString);
            quoteString = sqlmd.getIdentifierQuoteString();
            assertEquals("foo", quoteString);
            quoteString = sqlmd.getIdentifierQuoteString();
            assertEquals("foo", quoteString);
            quoteString = sqlmd.getIdentifierQuoteString();
            assertEquals("foo", quoteString);
            
        } catch (SQLException e) {
            fail("Unexpected exception: "+e.getMessage());
        }        
    }
    
    
}
