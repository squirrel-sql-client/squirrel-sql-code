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
package net.sourceforge.squirrel_sql.fw.sql;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.test.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class DatabaseObjectInfoTest extends BaseSQuirreLJUnit4TestCase {

    DatabaseObjectInfo dboInfoUnderTest = null;

    ISQLDatabaseMetaData oracleSQLDatabaseMetaData = null;
    
    ISQLDatabaseMetaData h2SQLDatabaseMetaData = null; 

    ISQLDatabaseMetaData sybase12SQLDatabaseMeta = null;
    
    ISQLDatabaseMetaData sybase15SQLDatabaseMeta = null;
    
    String testCatalog = "TestCatalog";

    String testSchema = "TestSchema";

    @Before
    public void setUp() throws Exception {
        oracleSQLDatabaseMetaData = 
            TestUtil.getEasyMockSQLMetaData("oracle", "jdbc:oracle:thin@",
                false, true);
        
        h2SQLDatabaseMetaData = TestUtil.getEasyMockH2SQLMetaData();
        sybase12SQLDatabaseMeta = TestUtil.getEasyMockSybase12SQLMetaData();
        sybase15SQLDatabaseMeta = TestUtil.getEasyMockSybase15SQLMetaData();
    }

    @After
    public void tearDown() throws Exception {
        h2SQLDatabaseMetaData = null;
        oracleSQLDatabaseMetaData = null;
        sybase12SQLDatabaseMeta = null;
        sybase15SQLDatabaseMeta = null;
    }

    @Test
    public final void testGetQualifiedNameH2() throws Exception {
        // Test case for 1742033 (Skipping quoting escape in table dropping)
        String tableName = "foo\"\"bar";  
        dboInfoUnderTest = new DatabaseObjectInfo(testCatalog, testSchema,
                tableName, DatabaseObjectType.TABLE, h2SQLDatabaseMetaData);
        
        String identifierQuoteString = 
            h2SQLDatabaseMetaData.getIdentifierQuoteString();
        String sep = h2SQLDatabaseMetaData.getCatalogSeparator();
        String expectedQualifiedName = 
            identifierQuoteString + testSchema  + identifierQuoteString + sep +
            identifierQuoteString + "foo\"\"\"\"bar" + identifierQuoteString;
        
        String qn = dboInfoUnderTest.getQualifiedName();
        assertEquals(expectedQualifiedName, qn);
    }

    @Test 
    public final void testGetQualifiedNameSybase12() throws Exception {
       String tableName = "mytable";
       final ISQLDatabaseMetaData md = sybase12SQLDatabaseMeta;
       assertEquals("Adaptive Server Enterprise", md.getDatabaseProductName());
       dboInfoUnderTest = 
          new DatabaseObjectInfo(testCatalog, testSchema, tableName, 
                                 DatabaseObjectType.TABLE, md);
       String identifierQuoteString = md.getIdentifierQuoteString();
       
       // Sybase 12.5 doesn't support quotation marks for identifiers, yet 
       // reports " as the identifier quote string - go figure!
       assertEquals("\"", identifierQuoteString);
       
       String sep = md.getCatalogSeparator();
       
       String expected = testCatalog + sep + testSchema + sep + tableName;
       String actual = dboInfoUnderTest.getQualifiedName();
       assertEquals(expected, actual);
    }

    @Test 
    public final void testGetQualifiedNameSybase15() throws Exception {
       String tableName = "mytable";
       final ISQLDatabaseMetaData md = sybase15SQLDatabaseMeta;
       assertEquals("Adaptive Server Enterprise", md.getDatabaseProductName());
       dboInfoUnderTest = 
          new DatabaseObjectInfo(testCatalog, testSchema, tableName, 
                                 DatabaseObjectType.TABLE, md);
       String identifierQuoteString = md.getIdentifierQuoteString();
       
       // Sybase 15 fully supports quotation marks for identifiers, and 
       // reports " as the identifier quote string - yeah, they got it right!
       assertEquals("\"", identifierQuoteString);
       
       String sep = md.getCatalogSeparator();
       
       // Since catalog, schema and table are all identifiers, they all need 
       // to be quoted.
       String expected = identifierQuoteString + testCatalog + identifierQuoteString + sep + 
                         identifierQuoteString + testSchema + identifierQuoteString + sep + 
                         identifierQuoteString + tableName + identifierQuoteString;
       String actual = dboInfoUnderTest.getQualifiedName();
       assertEquals(expected, actual);
    }
    
    @Test
    public final void testEqualsAndHashcode() {
        DatabaseObjectInfo a = new DatabaseObjectInfo(testCatalog, testSchema,
                "table1", DatabaseObjectType.TABLE, h2SQLDatabaseMetaData);
        DatabaseObjectInfo b = new DatabaseObjectInfo(testCatalog, testSchema,
                "table1", DatabaseObjectType.TABLE, h2SQLDatabaseMetaData);
        
        DatabaseObjectInfo c = new DatabaseObjectInfo(testCatalog, testSchema,
                "table2", DatabaseObjectType.TABLE, h2SQLDatabaseMetaData);
        
        DatabaseObjectInfo d = new MyDatabaseObjectInfo(testCatalog, testSchema,
                "table1", DatabaseObjectType.TABLE, h2SQLDatabaseMetaData);
        
        new EqualsTester(a,b,c,d);
    }

    @SuppressWarnings("serial")
    private static class MyDatabaseObjectInfo extends DatabaseObjectInfo {
        
        public MyDatabaseObjectInfo(String catalog, 
                                  String schema, 
                                  String simpleName,
                                  DatabaseObjectType dboType, 
                                  ISQLDatabaseMetaData md) 
        {
            super(catalog,schema,simpleName,dboType,md);
        }        
    }
}
