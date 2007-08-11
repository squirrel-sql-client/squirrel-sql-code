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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class ResultSetColumnReaderTest extends BaseSQuirreLJUnit4TestCase {

    /* Class under test */
    ResultSetColumnReader readerUnderTest = null;
    
    /* Mock objects */
    ResultSet mockResultSet = null;
    ResultSetMetaData mockResultSetMetaData = null;
    
    
    
    @Before
    public void setUp() throws Exception {
        /* construct mock objects */
        mockResultSet = createMock(ResultSet.class);
        mockResultSetMetaData  = createMock(ResultSetMetaData.class);
    }

    
    
    @After
    public void tearDown() throws Exception {
    }

    private void replayAll() {
        replay(mockResultSet);
        replay(mockResultSetMetaData);        
    }
    
    private void verifyAll() {
        verify(mockResultSet);
        verify(mockResultSetMetaData);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public final void testNullArg() throws SQLException {
        readerUnderTest = new ResultSetColumnReader(null);
    }
    
    @Test
    @Ignore
    public final void testNext() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testGetBoolean() throws SQLException {
        int index = 1;
        
        /* local expectations */
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.BIT);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.BIT);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.BOOLEAN);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.BOOLEAN);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.BOOLEAN);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.VARCHAR);
        
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.VARCHAR);
        
        index = 1;
        expect(mockResultSet.getMetaData()).andReturn(mockResultSetMetaData).anyTimes();        
        expect(mockResultSet.getObject(index++)).andReturn(Boolean.valueOf(true)).anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn(Boolean.valueOf(false)).anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn(Long.valueOf(1)).anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn(Short.valueOf((short)0)).anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn("true").anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn("false").anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn("true").anyTimes();
        
        
        /* execute test */
        index = 1;
        replayAll();
        readerUnderTest = new ResultSetColumnReader(mockResultSet);
        Boolean value = readerUnderTest.getBoolean(index++);
        assertEquals(true, value);
        value = readerUnderTest.getBoolean(index++);
        assertEquals(false, value);
        value = readerUnderTest.getBoolean(index++);
        assertEquals(true, value);
        value = readerUnderTest.getBoolean(index++);
        assertEquals(false, value);
        value = readerUnderTest.getBoolean(index++);
        assertEquals(true, value);
        value = readerUnderTest.getBoolean(index++);
        assertEquals(false, value);
        value = readerUnderTest.getBoolean(index++);
        assertEquals(true, value);
        
        verifyAll();
    }

    @Test
    @Ignore
    public final void testGetDate() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testGetDoubleFromNumber() throws SQLException {        
        int index = 1;
        
        /* local expectations */
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.REAL);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.REAL);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.REAL);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.REAL);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.REAL);
        
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.VARCHAR);
        
        index = 1;
        expect(mockResultSet.getMetaData()).andReturn(mockResultSetMetaData).anyTimes();        
        expect(mockResultSet.getObject(index++)).andReturn(Long.valueOf(123)).anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn(Integer.valueOf(345)).anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn(Float.valueOf((float)123.5)).anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn(Short.valueOf((short)127)).anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn("567").anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn("789").anyTimes();
        
        
        /* execute test */
        index = 1;
        replayAll();
        readerUnderTest = new ResultSetColumnReader(mockResultSet);
        Double value = readerUnderTest.getDouble(index++);
        assertEquals(123, value);
        value = readerUnderTest.getDouble(index++);
        assertEquals(345, value);
        value = readerUnderTest.getDouble(index++);
        assertEquals(123.5, value);
        value = readerUnderTest.getDouble(index++);
        assertEquals(127, value);
        value = readerUnderTest.getDouble(index++);
        assertEquals(567, value);
        value = readerUnderTest.getDouble(index++);
        assertEquals(789, value);
        
        verifyAll();
    }
        
    
    @Test
    public final void testGetLong() throws SQLException {
        int index = 1;
        
        /* local expectations */
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.SMALLINT);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.TINYINT);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.INTEGER);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.BIGINT);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.REAL);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.VARCHAR);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.BIT);
        expect(mockResultSetMetaData.getColumnType(index++)).andReturn(Types.BIT);
        
        index = 1;
        expect(mockResultSet.getMetaData()).andReturn(mockResultSetMetaData).anyTimes();        
        expect(mockResultSet.getObject(index++)).andReturn(Long.valueOf(123)).anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn(Integer.valueOf(345)).anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn("123").anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn(Short.valueOf((short)127)).anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn("567").anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn("789").anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn("true").anyTimes();
        expect(mockResultSet.getObject(index++)).andReturn("false").anyTimes();
        
        
        /* execute test */
        index = 1;
        replayAll();
        readerUnderTest = new ResultSetColumnReader(mockResultSet);
        Long value = readerUnderTest.getLong(index++);
        assertEquals(123, value);
        value = readerUnderTest.getLong(index++);
        assertEquals(345, value);
        value = readerUnderTest.getLong(index++);
        assertEquals(123.5, value);
        value = readerUnderTest.getLong(index++);
        assertEquals(127, value);
        value = readerUnderTest.getLong(index++);
        assertEquals(567, value);
        value = readerUnderTest.getLong(index++);
        assertEquals(789, value);
        value = readerUnderTest.getLong(index++);
        assertEquals(1, value);
        value = readerUnderTest.getLong(index++);
        assertEquals(0, value);
        
        verifyAll();
    }

    @Test
    @Ignore
    public final void testGetObject() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    @Ignore
    public final void testGetString() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    @Ignore
    public final void testGetTime() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    @Ignore
    public final void testGetTimeStamp() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    @Ignore
    public final void testWasNull() {
        fail("Not yet implemented"); // TODO
    }

}
