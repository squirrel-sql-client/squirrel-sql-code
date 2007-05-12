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
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.mo.sql.MockDatabaseMetaData;

import com.mockobjects.sql.MockConnection2;


/**
 * Test case for SQLDatabaseMetaData class.
 * 
 * @author manningr
 */
public class SQLDatabaseMetaDataTest extends BaseSQuirreLTestCase {

	SQLDatabaseMetaData iut = null;
    SQLDatabaseMetaData pg_tt_test = null;
	MockConnection2 con = null;
	MockDatabaseMetaData md = null;
	
    // postgres table types test
    java.sql.Connection pg_con = null;
    ISQLConnection pg_sql_con = null;
    ISQLDriver pg_driver = null;
    DatabaseMetaData pg_jmd = null;
    ResultSet pg_tableTypesRS = null;
    
	protected void setUp() throws Exception {
		super.setUp();
		con = new MockConnection2();
		md = new MockDatabaseMetaData("aCatalog", "aSchema");
		con.setupMetaData(md);
		ISQLConnection scon = new SQLConnection(con, null, null);
		iut = new SQLDatabaseMetaData(scon);
		md.setCatalogs(new String[] {"aCatalog"}, iut);
		md.setSchemas(new String[] {"aSchema"}, iut);

        // pg table types test
        pg_driver = createMock(ISQLDriver.class);

        pg_tableTypesRS = createNiceMock(ResultSet.class);
        expect(pg_tableTypesRS.next()).andReturn(true);
        expect(pg_tableTypesRS.getString(1)).andReturn("SYSTEM INDEX");
        expect(pg_tableTypesRS.next()).andReturn(true);
        expect(pg_tableTypesRS.getString(1)).andReturn("SYSTEM VIEW");
        expect(pg_tableTypesRS.next()).andReturn(true);
        expect(pg_tableTypesRS.getString(1)).andReturn("SYSTEM TABLE");
        expect(pg_tableTypesRS.next()).andReturn(true);
        expect(pg_tableTypesRS.getString(1)).andReturn("SYSTEM TOAST INDEX");
        expect(pg_tableTypesRS.next()).andReturn(true);
        expect(pg_tableTypesRS.getString(1)).andReturn("SYSTEM TOAST TABLE");
        expect(pg_tableTypesRS.next()).andReturn(true);
        expect(pg_tableTypesRS.getString(1)).andReturn("SYSTEM VIEW");
        expect(pg_tableTypesRS.next()).andReturn(true);
        expect(pg_tableTypesRS.getString(1)).andReturn("TABLE");
        expect(pg_tableTypesRS.next()).andReturn(true);
        expect(pg_tableTypesRS.getString(1)).andReturn("TEMPORARY INDEX");
        expect(pg_tableTypesRS.next()).andReturn(true);
        expect(pg_tableTypesRS.getString(1)).andReturn("TEMPORARY TABLE");
        expect(pg_tableTypesRS.next()).andReturn(true);
        expect(pg_tableTypesRS.getString(1)).andReturn("VIEW");
        expect(pg_tableTypesRS.next()).andReturn(false);
        //expectLastCall();
        replay(pg_tableTypesRS);
        
        pg_jmd = createMock(DatabaseMetaData.class);
        expect(pg_jmd.getDatabaseProductName()).andReturn("PostgreSQL");
        expect(pg_jmd.getDatabaseProductVersion()).andReturn("8.1.8");
        expect(pg_jmd.getTableTypes()).andReturn(pg_tableTypesRS);
        replay(pg_jmd);
        
        pg_con = createMock(java.sql.Connection.class);
        expect(pg_con.getMetaData()).andReturn(pg_jmd).atLeastOnce();
        replay(pg_con);
        
        pg_sql_con = new SQLConnection(pg_con, null, pg_driver);
        pg_tt_test = new SQLDatabaseMetaData(pg_sql_con);
	}

	public void testGetSchemas() {
		
		try {
			// Check our state initially after setup
			String[] currentSchemas = iut.getSchemas();
			assertEquals(1, currentSchemas.length);
			
			// Now, simulate adding a schema to the database
			md.setSchemas(new String[] {"aSchema", "Schema2"}, iut);
			
			// Now, check to be sure we get both schemas.
			currentSchemas = iut.getSchemas();
			assertEquals(2, currentSchemas.length);
		} catch (SQLException e) {
			fail("Unexpected exception:  "+e.getMessage());
		}	
			
	}

	public void testGetCatalogs() {
		try {
            // Check our state initially after setup
			String[] currentCatalogs = iut.getCatalogs();
			assertEquals(1, currentCatalogs.length);
            
			// Now, simulate adding a catalog to the database 
			md.setCatalogs(new String[] {"aCatalog", "Catalog2"}, iut);
			
			// Now, check to be sure we get both catalogs.
			currentCatalogs = iut.getCatalogs();
			assertEquals(2, currentCatalogs.length);
		} catch (SQLException e) {
			fail("Unexpected exception:  "+e.getMessage());
		}
	}

    public void testPGGetTableTypes() {
        try {
            String[] tableTypes = pg_tt_test.getTableTypes();
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
