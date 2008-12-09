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
package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import static net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_DONT_CACHE;
import static org.easymock.EasyMock.expect;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties;
import net.sourceforge.squirrel_sql.client.update.gui.AbstractTableModelTest;

import org.junit.Before;

public class SchemaTableModelTest extends AbstractTableModelTest
{

	@Before
	public void setUp() throws Exception
	{
		SQLAliasSchemaDetailProperties mockProps = mockHelper.createMock(SQLAliasSchemaDetailProperties.class);
		expect(mockProps.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(mockProps.getTable()).andStubReturn(SCHEMA_LOADING_ID_LOAD_DONT_CACHE);
		expect(mockProps.getProcedure()).andStubReturn(SCHEMA_LOADING_ID_LOAD_DONT_CACHE);
		expect(mockProps.getView()).andStubReturn(SCHEMA_LOADING_ID_LOAD_DONT_CACHE);
		SQLAliasSchemaDetailProperties[] props = new SQLAliasSchemaDetailProperties[] { mockProps };
		classUnderTest = new SchemaTableModel(props);
		super.editableColumns = new int[] { 1, 2, 3 };
		mockHelper.replayAll();
	}

}
