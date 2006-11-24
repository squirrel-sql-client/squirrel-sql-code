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
import java.sql.SQLException;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationManager;
import net.sourceforge.squirrel_sql.mo.sql.MockDatabaseMetaData;

import com.mockobjects.sql.MockConnection2;

/**
 * Test case for SQLDatabaseMetaData class.
 * 
 * @author manningr
 */
public class SQLDatabaseMetaDataTest extends TestCase {

	SQLDatabaseMetaData iut = null;
	MockConnection2 con = null;
	MockDatabaseMetaData md = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		ApplicationManager.initApplication();
		con = new MockConnection2();
		md = new MockDatabaseMetaData("aCatalog", "aSchema");
		con.setupMetaData(md);
		SQLConnection scon = new SQLConnection(con, null, null);
		iut = new SQLDatabaseMetaData(scon);
		md.setCatalogs(new String[] {"aCatalog"}, iut);
		md.setSchemas(new String[] {"aSchema"}, iut);

		
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

}
