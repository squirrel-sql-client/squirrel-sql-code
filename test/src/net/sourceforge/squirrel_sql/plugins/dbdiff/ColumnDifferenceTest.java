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
package net.sourceforge.squirrel_sql.plugins.dbdiff;

import static java.sql.Types.INTEGER;
import static java.sql.Types.VARCHAR;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.test.TestUtil;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ColumnDifferenceTest extends BaseSQuirreLJUnit4TestCase {

    // Instance under test
    ColumnDifference columnDifference = null;
    
    // Mock Objects
    ISQLDatabaseMetaData mockMetaData = null;
    TableColumnInfo mockVarcharCol1Table1 = null;
    TableColumnInfo mockVarcharCol1Table1Length1000 = null;
    TableColumnInfo mockIntegerCol1Table1 = null;

    TableColumnInfo mockVarcharCol2Table1 = null;
    TableColumnInfo mockVarcharCol2Table1Nullable = null;
    TableColumnInfo mockVarcharCol2Table1NotNullable = null;
    
    TableColumnInfo mockVarcharCol1Table2 = null;
    
    
    @Before
    public void setUp() throws Exception {
        // init class under test
        columnDifference = new ColumnDifference();
        
        // init mocks
        mockMetaData = 
            TestUtil.getEasyMockSQLMetaData("Oracle", 
                                            "oracle:jdbc:thin@localhost:1521:ORCL", 
                                            false);
        
        String[] columnNames = new String[] { "col1", "col1", "col2" };
        Integer[] columnTypes = new Integer[] { VARCHAR , INTEGER, VARCHAR};
        
        TableColumnInfo[] columns = 
            TestUtil.getEasyMockTableColumns("doo", 
                                             "foo", 
                                             "table1", 
                                             asList(columnNames), 
                                             asList(columnTypes));
        
        mockVarcharCol1Table1 = columns[0];
        mockIntegerCol1Table1 = columns[1];
        mockVarcharCol2Table1 = columns[2];
        
        mockVarcharCol1Table2 = 
            TestUtil.getEasyMockTableColumn("doo", "foo", "table2", "col1", VARCHAR);
        
        mockVarcharCol1Table1Length1000 = 
            TestUtil.setEasyMockTableColumnInfoSize(mockVarcharCol1Table1, 1000);
        
        mockVarcharCol2Table1Nullable = 
            TestUtil.setEasyMockTableColumnInfoNullable(mockVarcharCol2Table1, true);
        mockVarcharCol2Table1NotNullable = 
            TestUtil.setEasyMockTableColumnInfoNullable(mockVarcharCol2Table1, false);
        
    }

    @After
    public void tearDown() throws Exception {
        columnDifference = null;
    }

    // setColumns Error conditions
    
    @Test (expected = IllegalArgumentException.class)
    public final void testSetColumnsNullCol1() {
        columnDifference.setColumns(null, mockIntegerCol1Table1);
    }

    @Test (expected = IllegalArgumentException.class)
    public final void testSetColumnsNullCol2() {
        columnDifference.setColumns(mockVarcharCol1Table1, null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public final void testSetColumnsDifferentColumnName() {
        columnDifference.setColumns(mockVarcharCol1Table1, mockVarcharCol2Table1);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public final void testSetColumnsDifferentTableName() {
        columnDifference.setColumns(mockVarcharCol1Table1, mockVarcharCol1Table2);
    }
    
    
    // Sunny day
    
    @Test
    public final void testSetColumns() {
        
        columnDifference.setColumns(mockVarcharCol1Table1, mockIntegerCol1Table1);
        
        assertEquals(10, columnDifference.getCol1Length());
        assertEquals(10, columnDifference.getCol2Length());
        
        assertEquals(VARCHAR, columnDifference.getCol1Type());
        assertEquals(INTEGER, columnDifference.getCol2Type());
        
        assertEquals(true, columnDifference.col1AllowsNull());
        assertEquals(true, columnDifference.col2AllowsNull());
        
    }

    
    @Test
    public final void testSetColumn1() {
        
    }

    @Test
    public final void testSetColumn2() {
        
    }

    @Test
    public final void testGetCol1Type() {
        columnDifference.setColumns(mockVarcharCol1Table1, mockIntegerCol1Table1);
        assertEquals(VARCHAR, columnDifference.getCol1Type());
    }

    @Test
    public final void testGetCol1Length() {
        
    }

    @Test
    public final void testCol1AllowsNull() {
        columnDifference.setColumns(mockVarcharCol2Table1Nullable, 
                                    mockVarcharCol2Table1NotNullable);
        assertTrue(columnDifference.col1AllowsNull());

        columnDifference.setColumns(mockVarcharCol2Table1NotNullable, 
                                    mockVarcharCol2Table1Nullable);
        assertFalse(columnDifference.col1AllowsNull());        
    }

    @Test
    public final void testGetCol2Type() {
        
    }

    @Test
    public final void testGetCol2Length() {
        
    }

    @Test
    public final void testCol2AllowsNull() {
        columnDifference.setColumns(mockVarcharCol2Table1NotNullable, 
                                    mockVarcharCol2Table1Nullable);
        assertTrue(columnDifference.col2AllowsNull());
        
        columnDifference.setColumns(mockVarcharCol2Table1Nullable, 
                                    mockVarcharCol2Table1NotNullable);
        assertFalse(columnDifference.col2AllowsNull());
    }

    @Test
    public final void testExecuteDifferentTypes() {
        columnDifference.setColumns(mockVarcharCol1Table1, mockIntegerCol1Table1);
        assertTrue(columnDifference.execute());
    }

    @Test
    public final void testExecuteDifferentLengths() {
        columnDifference.setColumns(mockVarcharCol1Table1, mockVarcharCol1Table1Length1000);
        assertTrue(columnDifference.execute());
    }
    
    @Test
    public final void testExecuteDifferentNullable() {
        columnDifference.setColumns(mockVarcharCol2Table1Nullable, 
                                    mockVarcharCol2Table1NotNullable);
        assertTrue(columnDifference.execute());
    }
   
    @Test
    public final void testExecuteColumnMissing() {
        columnDifference.setCol1Exists(true);
        columnDifference.setCol2Exists(false);
        assertTrue(columnDifference.execute());
        assertTrue(columnDifference.isCol1Exists());
        assertFalse(columnDifference.isCol2Exists());
        
        columnDifference.setCol1Exists(false);
        columnDifference.setCol2Exists(true);
        assertTrue(columnDifference.execute());
        assertFalse(columnDifference.isCol1Exists());
        assertTrue(columnDifference.isCol2Exists());
    }
    
    @Test
    public final void testExecuteNoDifferences() {
        TableColumnInfo mockVarcharCol1Table1Copy = 
            TestUtil.setEasyMockTableColumnInfoType(mockVarcharCol1Table1, VARCHAR);
        columnDifference.setColumns(mockVarcharCol1Table1Copy, 
                                    mockVarcharCol1Table1);
        assertFalse(columnDifference.execute());
    }
    
    @Test
    public final void testSetGetTableName() {
        columnDifference.setTableName("table1");
        assertEquals("table1", columnDifference.getTableName());
        columnDifference.setTableName("table2");
        assertEquals("table2", columnDifference.getTableName());
    }

    @Test
    public final void testSetGetColumnName() {
        columnDifference.setColumnName("column1");
        assertEquals("column1", columnDifference.getColumnName());
        columnDifference.setColumnName("column2");
        assertEquals("column2", columnDifference.getColumnName());
    }

}
