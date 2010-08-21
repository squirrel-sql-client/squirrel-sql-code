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
package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePreferenceBean;

import org.junit.Before;

public class OracleTableParentExpanderTest extends AbstractINodeExpanderTest
{

	@Before
	public void setUp() throws Exception
	{
		OraclePreferenceBean prefs = mockHelper.createMock(OraclePreferenceBean.class);
		classUnderTest = new OracleTableParentExpander(prefs);

		expect(prefs.isExcludeRecycleBinTables()).andStubReturn(true);
		ITableInfo mockTableInfo = mockHelper.createMock(ITableInfo.class);
		expect(mockTableInfo.getSimpleName()).andReturn(TEST_SIMPLE_NAME);
		String[] tableTypes = new String[] { TEST_SIMPLE_NAME };
		ITableInfo[] tables = new ITableInfo[] { mockTableInfo };
		expect(
			mockSchemaInfo.getITableInfos(eq(TEST_CATALOG_NAME), eq(TEST_SCHEMA_NAME),
				isA(ObjFilterMatcher.class), aryEq(tableTypes))).andStubReturn(tables);
		mockSchemaInfo.waitTillTablesLoaded();
		expectLastCall().anyTimes();
		expect(mockSessionProperties.getShowRowCount()).andStubReturn(false);

	}

}
