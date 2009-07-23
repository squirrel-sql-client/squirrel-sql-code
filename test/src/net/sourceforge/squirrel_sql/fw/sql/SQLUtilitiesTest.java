package net.sourceforge.squirrel_sql.fw.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SQLUtilitiesTest extends BaseSQuirreLJUnit4TestCase {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        
    }

    @Test
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
            Assert.assertEquals(tables.size(), result.size());
        } catch (Exception e) {
            fail("Unexpected exception: "+e.getMessage());
        }
    }

    @Test 
    public void testNullQuoteIdentifier() {
        Assert.assertNull(SQLUtilities.quoteIdentifier(null));
    }
    
    @Test
    public void testQuoteIdentifierWithEmbeddedQuotes() {
        String tableNameWithAnEmbeddedQuote = "foo\"bar";
        String newTableName = 
            SQLUtilities.quoteIdentifier(tableNameWithAnEmbeddedQuote);
        assertEquals("foo\"\"bar", newTableName);
        
        tableNameWithAnEmbeddedQuote = "\"foo\"bar\"";
        newTableName = 
            SQLUtilities.quoteIdentifier(tableNameWithAnEmbeddedQuote);
        assertEquals("\"foo\"\"bar\"", newTableName);
        
        String tableName = "MyTable";
        String quotedTableName = SQLUtilities.quoteIdentifier(tableName);
        assertEquals(tableName, quotedTableName);
    }
    
    
    private static class MyCallback implements ProgressCallBack {

        public void currentlyLoading(String simpleName) {
            // Do Nothing
        }
        
    }
}
