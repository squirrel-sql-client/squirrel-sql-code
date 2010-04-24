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
package net.sourceforge.squirrel_sql.client.session;

import static org.easymock.EasyMock.expect;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

import com.gargoylesoftware.base.testing.EqualsTester;

public class ExtendedColumnInfoTest extends AbstractSerializableTest
{

	EasyMockHelper mockHelper = new EasyMockHelper();

	TableColumnInfo tcinfo1 = mockHelper.createMock(TableColumnInfo.class);

	TableColumnInfo tcinfo2 = mockHelper.createMock(TableColumnInfo.class);

	@Before
	public void setUp() throws Exception
	{

		expect(tcinfo1.getCatalogName()).andStubReturn("testCatalog1");
		expect(tcinfo1.getSchemaName()).andStubReturn("testSchema1");
		expect(tcinfo1.getTableName()).andStubReturn("testTable1");
		expect(tcinfo1.getColumnName()).andStubReturn("testColumn1");
		expect(tcinfo1.getTypeName()).andStubReturn("integer");
		expect(tcinfo1.getColumnSize()).andStubReturn(10);
		expect(tcinfo1.getDecimalDigits()).andStubReturn(0);
		expect(tcinfo1.isNullable()).andStubReturn("YES");
		expect(tcinfo1.getRemarks()).andStubReturn("testRemarks");

		expect(tcinfo2.getCatalogName()).andStubReturn("testCatalog2");
		expect(tcinfo2.getSchemaName()).andStubReturn("testSchema2");
		expect(tcinfo2.getTableName()).andStubReturn("testTable2");
		expect(tcinfo2.getColumnName()).andStubReturn("testColumn2");
		expect(tcinfo2.getTypeName()).andStubReturn("integer");
		expect(tcinfo2.getColumnSize()).andStubReturn(10);
		expect(tcinfo2.getDecimalDigits()).andStubReturn(0);
		expect(tcinfo2.isNullable()).andStubReturn("YES");
		expect(tcinfo2.getRemarks()).andStubReturn("testRemarks");

		mockHelper.replayAll();

		super.serializableToTest = new ExtendedColumnInfo(tcinfo1, "testTable1");
	}

	@After
	public void tearDown() throws Exception
	{
		super.serializableToTest = null;

		mockHelper.resetAll();
	}

	@Test
	public final void testEqualsAndHashcode()
	{

		ExtendedColumnInfo info1 = new ExtendedColumnInfo(tcinfo1, "table1");
		ExtendedColumnInfo info2 = new ExtendedColumnInfo(tcinfo1, "table1");
		ExtendedColumnInfo info3 = new ExtendedColumnInfo(tcinfo2, "table2");
		ExtendedColumnInfo info4 = new ExtendedColumnInfo(tcinfo1, "table1")
		{
			private static final long serialVersionUID = 1L;
		};

		new EqualsTester(info1, info2, info3, info4);
	}

}
