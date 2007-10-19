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
package net.sourceforge.squirrel_sql.client.session.schemainfo;

import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.test.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchemaInfoCacheTest extends BaseSQuirreLJUnit4TestCase {

    SchemaInfoCache schemaInfoCacheUnderTest = new SchemaInfoCache();
    ISQLDatabaseMetaData mockMetaData = null;
    Exception exceptionEncountered = null;
    
    @Before
    public void setUp() throws Exception {
        mockMetaData = TestUtil.getEasyMockH2SQLMetaData();
        for (int i = 0; i < 20; i++) { 
            String tableName = "table" + i;
            ITableInfo info = new TableInfo("cat", "schema", tableName, "TABLE", null, mockMetaData);
            schemaInfoCacheUnderTest.writeToTableCache(info);
        }
    }

    @After
    public void tearDown() throws Exception {
        mockMetaData = null;
    }


    // This test disabled for now until we figure out a better way to do 
    // concurrent modifications to the schemaInfoCache.
    //@Test
    public final void testGetITableInfosForReadOnly() {
        
        @SuppressWarnings("unchecked")
        Map map = schemaInfoCacheUnderTest.getTableNamesForReadOnly();
        @SuppressWarnings("unchecked")
        IteratorThread thread = new IteratorThread(map.values().iterator());
        
        Thread t = new Thread(thread);
        t.start();
        sleep(500);
        schemaInfoCacheUnderTest.clearTables(null, null, null, null);
        sleep(500);
        if (exceptionEncountered != null) {
            exceptionEncountered.printStackTrace();
            fail("Unexpected exception: "+exceptionEncountered.toString());
            
        }
    }
    
    private class IteratorThread implements Runnable {
        
        private Iterator<ITableInfo> iterator = null;
        
        public IteratorThread(Iterator<ITableInfo> i) {
            iterator = i;
        }
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                while (iterator.hasNext()) {
                    iterator.next();
                    sleep(500);
                }
            } catch (Exception e) {
                exceptionEncountered = e;
            }
        }
        
    }

    private void sleep(long millis) {
        try { Thread.sleep(millis); } catch (Exception e) {}
    }
}
