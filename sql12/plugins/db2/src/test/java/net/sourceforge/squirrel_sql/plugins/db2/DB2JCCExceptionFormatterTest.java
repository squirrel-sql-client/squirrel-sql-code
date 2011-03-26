/*
 * Copyright (C) 2010 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.db2;

import static org.junit.Assert.assertTrue;
import net.sourceforge.squirrel_sql.fw.util.AbstractExceptionFormatterTest;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;

import org.junit.Test;

import com.ibm.db2.jcc.c.SqlException;


public class DB2JCCExceptionFormatterTest extends AbstractExceptionFormatterTest
{

	private static final int TEST_CODE = 222;
	private static final int TEST_STATE = 555;
	private static final String TEST_MESSAGE = "a Test message";

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.AbstractExceptionFormatterTest#getExceptionFormatterToTest()
	 */
	@Override
	protected ExceptionFormatter getExceptionFormatterToTest()
	{
		return new DB2JCCExceptionFormatter();
	}

	@Test
	public void testFormatsJccException() {
		
		SqlException testException = new SqlException(TEST_MESSAGE, TEST_CODE, TEST_STATE);
		assertTrue(classUnderTest.formatsException(testException));
	}
	
	@Test
	public void testFormatJccException() throws Exception
	{
		
		SqlException testException = new SqlException(TEST_MESSAGE, TEST_CODE, TEST_STATE);
		String formattedMessage = classUnderTest.format(testException);
		System.err.println("Formatted message = "+formattedMessage);
		
		assertTrue(formattedMessage.contains(formattedMessage));
		assertTrue(formattedMessage.contains(""+TEST_CODE));
		assertTrue(formattedMessage.contains(""+TEST_STATE));
	}
}
