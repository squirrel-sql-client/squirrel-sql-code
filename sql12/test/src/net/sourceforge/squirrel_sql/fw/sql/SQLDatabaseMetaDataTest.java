package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.SQLException;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.mo.sql.MockDatabaseMetaData;

import com.mockobjects.sql.MockConnection2;

public class SQLDatabaseMetaDataTest extends TestCase {

	SQLDatabaseMetaData iut = null;
	MockConnection2 con = null;
	MockDatabaseMetaData md = null;
	private static boolean appArgsInitialized = false;
	
	protected void setUp() throws Exception {
		super.setUp();
		if (!appArgsInitialized) {
			appArgsInitialized = true;
			ApplicationArguments.initialize(new String[0]);
		}				
		con = new MockConnection2();
		md = new MockDatabaseMetaData("aCatalog", "aSchema");
		con.setupMetaData(md);
		SQLConnection scon = new SQLConnection(con, null);
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
