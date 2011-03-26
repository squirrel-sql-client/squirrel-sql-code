package net.sourceforge.squirrel_sql.fw.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

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

public abstract class AbstractExceptionFormatterTest
{
	/** The class that will be tested. See getExceptionFormatterToTest. */
	protected ExceptionFormatter classUnderTest = null;

	/**
	 * Sub-class tests need to implement this and return an instance of ExceptionFormatter to test.
	 * 
	 * @return an instance of ExceptionFormatter to test
	 */
	protected abstract ExceptionFormatter getExceptionFormatterToTest() throws Exception;

	public AbstractExceptionFormatterTest()
	{
		super();
	}

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = getExceptionFormatterToTest();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFormatNullArg() throws Exception
	{
		classUnderTest.format(null);
	}

	@Test
	public void testFormat() throws Exception
	{
		SQLException testException = new SQLException("Test SQL Exception");
		if (classUnderTest.formatsException(testException))
		{
			final String formattedMessage = classUnderTest.format(testException);
			assertNotNull(formattedMessage);
			if ("".equals(formattedMessage))
			{
				fail("Expected formatted message to be non-empty");
			}
		}
	}

	/**
	 * For this test of a null Throwable argument, we accept either an IllegalArgumentException, or false to 
	 * indicate that this exception can't be handled by the custom ExceptionFormatter.
	 */
	@Test
	public void testFormatsExceptionNullArg()
	{
		try
		{
			boolean result = classUnderTest.formatsException(null);
			assertFalse(result);
		}
		catch (Exception e)
		{
			if (! (e instanceof IllegalArgumentException)) {
				fail("Expected IllegalArgumentException. Instead encountered: "+e.getClass());
			}
		}
	}

}