/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.fw;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Test;

/**
 * Subclasses need only provide a @Before method implement that sets classUnderTest equal 
 * to the specific BeanInfo implementation that is being tested.
 * 
 */
public class AbstractPropertyBeanInfoTest extends BaseSQuirreLJUnit4TestCase
{
	/** subclasses set this to beanInfo impl in @Before method */
	protected BeanInfo classUnderTest = null;

	@Test
	public void testGetPropertyDescriptors()
	{
		PropertyDescriptor[] result1 = classUnderTest.getPropertyDescriptors();
		assertNotNull(result1);
		
		PropertyDescriptor[] result2 = classUnderTest.getPropertyDescriptors();
		assertNotNull(result2);
		
		// Check reference equality to be sure that it isn't the exact same object.
		assertFalse("Expected two calls to getPropertyDescriptors() to produce two distinct objects, but " +
				"both references point to the same object.", result1 == result2);
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

}