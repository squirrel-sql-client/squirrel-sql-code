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
package net.sourceforge.squirrel_sql.plugins.oracle.tab;


import static java.sql.Types.VARCHAR;
import static org.easymock.EasyMock.expect;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractSourceTabTest;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.junit.Before;

public class SnapshotSourceTabTest extends AbstractSourceTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SnapshotSourceTab();
		ITableInfo mockTableInfo = mockHelper.createMock(ITableInfo.class);
		expect(mockTableInfo.getDatabaseObjectType()).andStubReturn(DatabaseObjectType.VIEW);
		mockDatabaseObjectInfo = mockTableInfo;
		PrimaryKeyInfo[] primaryKeyInfos = new PrimaryKeyInfo[0]; 
		expect(mockMetaData.getPrimaryKey(mockTableInfo)).andStubReturn(primaryKeyInfos);
		IQueryTokenizer mockTokenizer = mockHelper.createMock(IQueryTokenizer.class);
		expect(mockTokenizer.getSQLStatementSeparator()).andStubReturn(";");
		expect(mockSession.getQueryTokenizer()).andStubReturn(mockTokenizer);
		
		
		TableColumnInfo mockTableColumnInfo = mockHelper.createMock(TableColumnInfo.class);
		expect(mockTableColumnInfo.getColumnName()).andStubReturn("testColumnName");
		expect(mockTableColumnInfo.getDefaultValue()).andStubReturn("");
		expect(mockTableColumnInfo.getColumnSize()).andStubReturn(10);
		expect(mockTableColumnInfo.getDataType()).andStubReturn(VARCHAR);
		expect(mockTableColumnInfo.getDecimalDigits()).andStubReturn(0);
		expect(mockTableColumnInfo.isNullable()).andStubReturn("YES");
		final ForeignKeyInfo[] fkInfos = new ForeignKeyInfo[0];
		expect(mockMetaData.getImportedKeysInfo(mockTableInfo)).andStubReturn(fkInfos);
		TableColumnInfo[] columnInfos = new TableColumnInfo[] { mockTableColumnInfo };
		expect(mockMetaData.getColumnInfo(mockTableInfo)).andStubReturn(columnInfos);
	}

	
}
