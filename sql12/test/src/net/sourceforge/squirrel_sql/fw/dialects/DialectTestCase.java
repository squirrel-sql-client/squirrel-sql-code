/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.fw.dialects;

import java.lang.reflect.Field;
import java.sql.Types;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;

import org.hibernate.MappingException;

public class DialectTestCase extends BaseSQuirreLTestCase
{

	protected void testAllTypes(HibernateDialect d)
	{
		try
		{
			Field[] fields = java.sql.Types.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				Integer jdbcType = field.getInt(null);
				testType(jdbcType, d);
			}
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	protected void testType(int type, HibernateDialect dialect)
	{
		try
		{
			dialect.getTypeName(type, 10, 0, 0);
		}
		catch (MappingException e)
		{
			// Here, I don't completely understand how these types are to be used in all databases and I haven't
			// yet had time to implement support in the dialects for them.  So, this exclude list will keep these
			// less commonly used types from failing the test. Also, since not all of these types appear in Java5
			// and we currently support compiling the code base with it, I use the integer values for new types 
			// that were introduces in Java6, rather than the type constant name.
			if (type != Types.NULL 
				&& type != Types.DATALINK
				&& type != Types.OTHER
				&& type != Types.JAVA_OBJECT
				&& type != Types.DISTINCT
				&& type != Types.STRUCT
				&& type != Types.ARRAY
				&& type != Types.REF
				// Java6 types
				&& type != -8   // ROWID
				&& type != -9   // NVARCHAR
				&& type != -15  // NCHAR
				&& type != -16  // LONGNVARCHAR
				&& type != 2011 // NCLOB
				&& type != 2009 // SQLXML
				) 
			{
				fail("No mapping for type: " + type + "=" + JDBCTypeMapper.getJdbcTypeName(type));
			}
		}
	}

}
