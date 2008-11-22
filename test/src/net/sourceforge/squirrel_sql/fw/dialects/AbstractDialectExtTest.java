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
package net.sourceforge.squirrel_sql.fw.dialects;

import static java.sql.Types.VARCHAR;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.sql.Types;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.MappingException;
import org.junit.Test;

import utils.EasyMockHelper;

public abstract class AbstractDialectExtTest extends BaseSQuirreLJUnit4TestCase
{

	protected HibernateDialect classUnderTest = null;

	protected EasyMockHelper mockHelper = new EasyMockHelper();

	private TableColumnInfo mockColumnInfo = mockHelper.createMock(TableColumnInfo.class);

	private DatabaseObjectQualifier mockQualifier = mockHelper.createMock(DatabaseObjectQualifier.class);

	private SqlGenerationPreferences mockPrefs = mockHelper.createMock(SqlGenerationPreferences.class);

	public AbstractDialectExtTest()
	{
		super();
	}

	@Test
	public void testAddColumnQualifiedNamesQuotedIdentifiers()
	{
		expect(mockColumnInfo.getTableName()).andStubReturn("aTestTableName");
		expect(mockColumnInfo.getColumnName()).andStubReturn("aTestColumnName");
		expect(mockColumnInfo.getRemarks()).andStubReturn("aRemark");
		expect(mockQualifier.getSchema()).andStubReturn("aTestSchema");
		expect(mockQualifier.getCatalog()).andStubReturn("aTestCatalog");
		expect(mockColumnInfo.getDataType()).andStubReturn(VARCHAR);
		expect(mockColumnInfo.getColumnSize()).andStubReturn(1024);
		expect(mockColumnInfo.getDecimalDigits()).andStubReturn(0);
		expect(mockColumnInfo.getDefaultValue()).andStubReturn(null);
		expect(mockColumnInfo.isNullable()).andStubReturn("YES");
		expect(mockPrefs.isQualifyTableNames()).andStubReturn(true);
		expect(mockPrefs.isQuoteIdentifiers()).andStubReturn(true);
		
		
		mockHelper.replayAll();

		if (classUnderTest.supportsAddColumn())
		{
			try {
				assertNotNull(classUnderTest.getAddColumnSQL(mockColumnInfo, mockQualifier, mockPrefs));
			} catch (UnsupportedOperationException e) {
				String message = e.getMessage();
				// Some dialects support adding columns, but the logic has not been added yet to the dialect.
				// If that is the case the message is "Not yet implemented" - fail for all other messages.
				if (!message.equals("Not yet implemented")) {
					fail("supportsAddColumn was true, but still got an UnsupportedOperationException: "+
						e.getMessage());
				}
			}
		}
		else
		{
			try
			{
				classUnderTest.getAddColumnSQL(mockColumnInfo, mockQualifier, mockPrefs);
				fail("Expected an UnsupportedOperationException when trying to retrieve SQL for adding a column");
			}
			catch (UnsupportedOperationException e)
			{
				// this is expected.
			}
		}

		mockHelper.verifyAll();
	}

	@Test
	public void testGetTypeNameInt()
	{
		testAllTypes(classUnderTest);
	}

	private void testAllTypes(HibernateDialect d)
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

	private void testType(int type, HibernateDialect dialect)
	{
		try
		{
			dialect.getTypeName(type, 10, 0, 0);
		}
		catch (MappingException e)
		{
			// Here, I don't completely understand how these types are to be used in all databases and I haven't
			// yet had time to implement support in the dialects for them. So, this exclude list will keep these
			// less commonly used types from failing the test. Also, since not all of these types appear in Java5
			// and we currently support compiling the code base with it, I use the integer values for new types
			// that were introduces in Java6, rather than the type constant name.
			if (type != Types.NULL && type != Types.DATALINK && type != Types.OTHER && type != Types.JAVA_OBJECT
				&& type != Types.DISTINCT && type != Types.STRUCT && type != Types.ARRAY && type != Types.REF
				// Java6 types
				&& type != -8 // ROWID
				&& type != -9 // NVARCHAR
				&& type != -15 // NCHAR
				&& type != -16 // LONGNVARCHAR
				&& type != 2011 // NCLOB
				&& type != 2009 // SQLXML
			)
			{
				fail("No mapping for type: " + type + "=" + JDBCTypeMapper.getJdbcTypeName(type));
			}
		}
	}

}