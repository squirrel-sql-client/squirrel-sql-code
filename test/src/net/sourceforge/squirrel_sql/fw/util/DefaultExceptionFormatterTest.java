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
package net.sourceforge.squirrel_sql.fw.util;

import java.io.IOException;
import java.sql.DataTruncation;
import java.sql.SQLException;
import java.sql.SQLWarning;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.SQLExecutionException;

import static org.easymock.EasyMock.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author manningr
 */
public class DefaultExceptionFormatterTest extends BaseSQuirreLJUnit4TestCase {

    DefaultExceptionFormatter formatterUnderTest;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        formatterUnderTest = new DefaultExceptionFormatter();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        formatterUnderTest = null;
    }

    /**
     * Test method for {@link net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter#format(java.lang.Throwable)}.
     */
    @Test (expected = IllegalArgumentException.class)
    public final void testNullThrowableFormat() {
        formatterUnderTest.format(null);
    }

    /**
     * Test method for {@link net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter#format(java.lang.Throwable)}.
     */    
    @Test
    public final void testDefaultFormatForDataTruncationRead() {
        DataTruncation dt = new DataTruncation(1, true, true, 20, 3);
        Assert.assertTrue(formatterUnderTest.formatsException(dt));
        String formattedException = formatterUnderTest.format(dt);
        
        //" a read "
        StringBuilder expectedMessage = 
            new StringBuilder("Data Truncation error occured on"); 
        expectedMessage.append(" a read ");
        expectedMessage.append(" of column ");
        expectedMessage.append(1);
        expectedMessage.append("Data was ");
        expectedMessage.append(20);
        expectedMessage.append(" bytes long and ");
        expectedMessage.append(3);
        expectedMessage.append(" bytes were transferred.");

        Assert.assertEquals(expectedMessage.toString(), formattedException);
    }
    
    /**
     * Test method for {@link net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter#format(java.lang.Throwable)}.
     */    
    @Test
    public void testDefaultFormatForDataTruncationWrite() { 
        DataTruncation dt = new DataTruncation(1, true, false, 20, 3);
        Assert.assertTrue(formatterUnderTest.formatsException(dt));
        
        StringBuilder expectedMessage = 
            new StringBuilder("Data Truncation error occured on"); 
        expectedMessage.append(" a write ");
        expectedMessage.append(" of column ");
        expectedMessage.append(1);
        expectedMessage.append("Data was ");
        expectedMessage.append(20);
        expectedMessage.append(" bytes long and ");
        expectedMessage.append(3);
        expectedMessage.append(" bytes were transferred.");

        String formattedException = formatterUnderTest.format(dt);
        Assert.assertEquals(expectedMessage.toString(), formattedException);        
    }

    /**
     * Test method for {@link net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter#format(java.lang.Throwable)}.
     */    
    @Test
    public void testDefaultFormatForSQLException() { 
        SQLException ex = new SQLException("table not found", "FooState", 1000);
        Assert.assertTrue(formatterUnderTest.formatsException(ex));
        
        StringBuilder expectedMessage = 
            new StringBuilder("Error: table not found\n"); 
        expectedMessage.append("SQLState:  FooState\n");
        expectedMessage.append("ErrorCode: 1000");

        String formattedException = formatterUnderTest.format(ex);
        Assert.assertEquals(expectedMessage.toString(), formattedException);        
    }

    /**
     * Test method for {@link net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter#format(java.lang.Throwable)}.
     */    
    @Test
    public void testDefaultFormatForSQLWarning() { 
        SQLWarning ex = new SQLWarning("low on memory", "WarningState", 1000);
        SQLExecutionException ee = new SQLExecutionException(ex, "postError");
        Assert.assertTrue(formatterUnderTest.formatsException(ex));
        Assert.assertTrue(formatterUnderTest.formatsException(ee));
        
        StringBuilder expectedMessage = 
            new StringBuilder("Warning:   low on memory\n"); 
        expectedMessage.append("SQLState:  WarningState\n");
        expectedMessage.append("ErrorCode: 1000\n");
        expectedMessage.append("postError");
        
        String formattedException = formatterUnderTest.format(ee);
        Assert.assertEquals(expectedMessage.toString(), formattedException);        
    }
    
    
    /**
     * Test method for {@link net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter#formatsException(java.lang.Throwable)}.
     */
    @Test
    public final void testFormatsException() {
        Assert.assertTrue(formatterUnderTest.formatsException(new Throwable()));
        Assert.assertTrue(formatterUnderTest.formatsException(new SQLException()));
        Assert.assertTrue(formatterUnderTest.formatsException(new SQLExecutionException(new SQLException(), "")));
        Assert.assertTrue(formatterUnderTest.formatsException(new SQLExecutionException(new SQLException(), "some error")));
        Assert.assertTrue(formatterUnderTest.formatsException(new SQLExecutionException(new SQLException(), null)));
        Assert.assertTrue(formatterUnderTest.formatsException(new IOException()));
        Assert.assertTrue(formatterUnderTest.formatsException(new DataTruncation(0, true, true, 0, 0)));
        Assert.assertTrue(formatterUnderTest.formatsException(new SQLWarning()));
    }

    @Test (expected = IllegalArgumentException.class) 
    public final void testSetNullCustomExceptionFormatter() {
        formatterUnderTest.setCustomExceptionFormatter(null);
    }
    
    /**
     * Test method for {@link net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter#setCustomExceptionFormatter(net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)}.
     */
    @Test
    public final void testSetCustomExceptionFormatter() throws Exception {
        ExceptionFormatter formatter1 = createMock(ExceptionFormatter.class);
        SQLException ex = new SQLException("table does not exist");
        SQLExecutionException ee = new SQLExecutionException(ex, "");
        expect(formatter1.formatsException(isA(SQLException.class))).andReturn(true).anyTimes();
        expect(formatter1.format(ex)).andReturn("foo").anyTimes();
        replay(formatter1);
        formatterUnderTest.setCustomExceptionFormatter(formatter1);
        String formattedEx = formatterUnderTest.format(ee);
        verify(formatter1);
        Assert.assertEquals("foo", formattedEx);
    }
    
    /**
     * Test method for {@link net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter#setCustomExceptionFormatter(net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)}.
     */
    @Test
    public final void testSetMultiCustomExceptionFormatter() throws Exception {
        ExceptionFormatter formatter1 = createMock(ExceptionFormatter.class);
        ExceptionFormatter formatter2 = createMock(ExceptionFormatter.class);
        SQLException ex = new SQLException("table does not exist");
        SQLExecutionException ee = new SQLExecutionException(ex, "");
        expect(formatter1.formatsException(isA(SQLException.class))).andReturn(true).anyTimes();
        expect(formatter1.format(ex)).andReturn("formatter1").anyTimes();
        expect(formatter2.formatsException(isA(SQLException.class))).andReturn(true).anyTimes();
        expect(formatter2.format(ex)).andReturn("formatter2").anyTimes();
        replay(formatter1);
        replay(formatter2);
        formatterUnderTest.setCustomExceptionFormatter(formatter1);
        // This should just produce an error message.
        formatterUnderTest.setCustomExceptionFormatter(formatter2);
        
        String formattedEx = formatterUnderTest.format(ee);
        verify(formatter1);
        verify(formatter2);
        Assert.assertEquals("formatter1", formattedEx);
    }
    
}
