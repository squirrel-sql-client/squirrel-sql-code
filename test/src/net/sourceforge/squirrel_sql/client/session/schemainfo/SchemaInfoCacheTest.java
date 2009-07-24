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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.AbstractSerializableTest;
import net.sourceforge.squirrel_sql.fw.FwTestUtil;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchemaInfoCacheTest extends AbstractSerializableTest {

   SchemaInfoCache schemaInfoCacheUnderTest = new SchemaInfoCache();

   ISQLDatabaseMetaData mockMetaData = null;

   Exception exceptionEncountered = null;

   /** Contains test ITableInfos named table0, table1, ... table9 */
   ITableInfo[] tableInfos = new ITableInfo[10];

   String testCatalog = "cat";

   String testSchema = "schema";

   String testTableType = "TABLE";

   @Before
   public void setUp() throws Exception {
   	super.serializableToTest = new SchemaInfoCache();
      mockMetaData = FwTestUtil.getEasyMockH2SQLMetaData();
      for (int i = 0; i < tableInfos.length; i++) {
         String tableName = "table" + i;
         tableInfos[i] = new TableInfo(testCatalog, testSchema, tableName,
               testTableType, null, mockMetaData);
      }
      schemaInfoCacheUnderTest.writeToTableCache(tableInfos);
   }

   @After
   public void tearDown() throws Exception {
   	super.serializableToTest = null;
      mockMetaData = null;
   }

   /**
    * Test that the tables are returned in the same order that they were stored
    * in.
    */
   @Test
   public void testGetITableInfosForReadOnly_order() {
      List<ITableInfo> tis = schemaInfoCacheUnderTest
            .getITableInfosForReadOnly();
      int idx = 0;
      for (ITableInfo iTableInfo : tis) {
         String expectedTableName = tableInfos[idx++].getSimpleName();
         String actualTableName = iTableInfo.getSimpleName();
         assertEquals(expectedTableName, actualTableName);
      }

      /* Pick a tableInfo to remove and replace */
      ITableInfo ti = tableInfos[5];
      
      /* remove table with name table10 */
      schemaInfoCacheUnderTest.clearTables(testCatalog, testSchema, ti.getSimpleName(),
            new String[] { testTableType });

      /* Add it back */ 
      schemaInfoCacheUnderTest.writeToTableCache(ti);
      
      checkSortOrder();
   }

   private void checkSortOrder() {
      List<ITableInfo> tis = schemaInfoCacheUnderTest
            .getITableInfosForReadOnly();

      ITableInfo last = null;
      for (ITableInfo iTableInfo : tis) {
         if (last == null) {
            last = iTableInfo;
         } else {
            String prev = last.getSimpleName();
            String curr = iTableInfo.getSimpleName();
            System.out.println("prev: "+prev+" curr:"+curr);
            if (prev.compareTo(curr) > 0) {
               fail("Table named "+prev+" appeared before "+curr+" in the sorted list");
            }
            last = iTableInfo;
         }
      }
   }
   
   // This test disabled for now until we figure out a better way to do
   // concurrent modifications to the schemaInfoCache.
   // @Test
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
         fail("Unexpected exception: " + exceptionEncountered.toString());

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
      try {
         Thread.sleep(millis);
      } catch (Exception e) {
      }
   }
}
