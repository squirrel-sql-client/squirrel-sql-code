package net.sourceforge.squirrel_sql.fw.sql;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.ApplicationManager;

import junit.framework.TestCase;

public class SQLUtilitiesTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        ApplicationManager.initApplication();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetInsertionOrder() {
        
        ArrayList<ITableInfo> tables = new ArrayList<ITableInfo>();
        ITableInfo ti = 
            new MockTableInfo("mockTable","mockSchema","mockCatalog");
        ITableInfo ti2 = 
            new MockTableInfo("mockTable2","mockSchema2","mockCatalog2");
        ITableInfo ti3 = 
            new MockTableInfo("mockTable3","mockSchema3","mockCatalog3");
        
        tables.add(ti);
        tables.add(ti2);
        tables.add(ti3);
        
        SQLDatabaseMetaData md = new MockSQLDatabaseMetaData();
        
        try {
            List<ITableInfo> result = 
                SQLUtilities.getInsertionOrder(tables, md, new MyCallback());
            assertEquals(tables.size(), result.size());
        } catch (Exception e) {
            fail("Unexpected exception: "+e.getMessage());
        }
    }

    private static class MyCallback implements ProgressCallBack {

        public void currentlyLoading(String simpleName) {
            // Do Nothing
        }
        
    }
}
