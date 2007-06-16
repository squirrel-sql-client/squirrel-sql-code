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
import junit.framework.JUnit4TestAdapter;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.test.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

public class ColumnDifferenceTest extends BaseSQuirreLJUnit4TestCase {

    // Instance under test
    ColumnDifference columnDifference = null;
    
    // Mock Objects
    ISQLDatabaseMetaData mockMetaData = null;
    TableColumnInfo mockVarcharCol1 = null;
    TableColumnInfo mockIntegerCol1 = null;
    TableColumnInfo mockVarcharCol2 = null;    
    
    
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
                                             "mytable", 
                                             asList(columnNames), 
                                             asList(columnTypes));
        
        mockVarcharCol1 = columns[0];
        mockIntegerCol1 = columns[1];
        mockVarcharCol2 = columns[2];
    }

    @After
    public void tearDown() throws Exception {
        columnDifference = null;
    }

    @Test
    public final void testSetColumns() {
        
        columnDifference.setColumns(mockVarcharCol1, mockIntegerCol1);
        
        assertEquals(10, columnDifference.getCol1Length());
        assertEquals(10, columnDifference.getCol2Length());
        
        assertEquals(VARCHAR, columnDifference.getCol1Type());
        assertEquals(INTEGER, columnDifference.getCol2Type());
        
        assertEquals(true, columnDifference.col1AllowsNull());
        assertEquals(true, columnDifference.col2AllowsNull());
        
    }

    @Test (expected = IllegalArgumentException.class)
    public final void testSetColumnsDifferentName() {
        columnDifference.setColumns(mockVarcharCol1, mockVarcharCol2);
    }
    
    @Test
    public final void testSetColumn1() {
        
    }

    @Test
    public final void testSetColumn2() {
        
    }

    @Test
    public final void testGetCol1Type() {
        
    }

    @Test
    public final void testGetCol1Length() {
        
    }

    @Test
    public final void testCol1AllowsNull() {
        
    }

    @Test
    public final void testGetCol2Type() {
        
    }

    @Test
    public final void testGetCol2Length() {
        
    }

    @Test
    public final void testCol2AllowsNull() {
        
    }

    @Test
    public final void testExecute() {
        
    }

    @Test
    public final void testSetTableName() {
        
    }

    @Test
    public final void testGetTableName() {
        
    }

    @Test
    public final void testSetColumnName() {
        
    }

    @Test
    public final void testGetColumnName() {
        
    }

    @Test
    public final void testSetCol1Exists() {
        
    }

    @Test
    public final void testIsCol1Exists() {
        
    }

    @Test
    public final void testSetCol2Exists() {
        
    }

    @Test
    public final void testIsCol2Exists() {
        
    }
}
