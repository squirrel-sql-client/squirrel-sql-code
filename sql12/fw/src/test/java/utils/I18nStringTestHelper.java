/*
 * Copyright (C) 2009 Rob Manning
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
package utils;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;

/**
 * A simple utility for testing Internationalization interfaces (which contain Strings which are
 * internationalized using StringManager and I18NStrings.properties file).
 */
public class I18nStringTestHelper
{
	/**
	 * Loops through all of the fields in the specified class (which should be an interface) to check to see if
	 * any of them begin with the dreaded "No resource found for key ..." and fails the test with the first one
	 * of these that were found.
	 * 
	 * @param i18nInterfaceClass
	 *           the class of the interface to test. This must be an interface as all of the fields that are
	 *           found are assumed to be static.
	 */
	public static void testI18nInterface(Class<?> i18nInterfaceClass)
	{
		Field[] ifields = i18nInterfaceClass.getDeclaredFields();

		for (Field ifield : ifields)
		{
			String fieldName = ifield.getName();
			String value;
			try
			{
				value = ifield.get(null).toString();
				if (value.startsWith("No resource found for key"))
				{
					fail("I18NString resource key = " + fieldName
						+ " has no corresponding value in I18NString.properties: " + value);
				}
			}
			catch (Exception e)
			{
				fail("Unexpected exception encountered while attempting to get the value of field (" + fieldName
					+ ") in class: " + i18nInterfaceClass.getCanonicalName());
			}
		}

	}
}
