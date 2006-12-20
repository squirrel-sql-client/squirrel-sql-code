package net.sourceforge.squirrel_sql.plugins.dbcopy.util;

/*
 * Copyright (C) 2006 Rob Manning
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

import java.sql.SQLException;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationManager;
import net.sourceforge.squirrel_sql.client.session.MockSession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.mo.sql.MockDatabaseMetaData;

public class CompatTest extends TestCase {

	private MockSession session = null;
	private MockDatabaseMetaData mdata = null; 
	
	protected void setUp() throws Exception {
		super.setUp();
		ApplicationManager.initApplication();
		session = new MockSession();
        
		mdata = session.getMockDatabaseMetaData();
	}

	public void testGetTables() {
		//fail("Not yet implemented"); // TODO
	}

	public void testGetIObjectTreeAPI() {
		//fail("Not yet implemented"); // TODO
	}

	public void testReloadSchema() {
		//fail("Not yet implemented"); // TODO
	}

	public void testIsKeyword() throws SQLException {
		mdata.setSQLKeywords(new String[] {"table"});
		assertEquals(true, Compat.isKeyword(session, "table"));
		assertEquals(false, Compat.isKeyword(session, "notakeyword"));
	}

	public void testStoresUpperCaseIdentifiers() {
		//fail("Not yet implemented"); // TODO
	}

	public void testIsTableTypeDBO() {
		assertEquals(true, Compat.isTableTypeDBO(DatabaseObjectType.TABLE_TYPE_DBO));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.BEST_ROW_ID));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.CATALOG));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.COLUMN));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.DATABASE_TYPE_DBO));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.DATATYPE));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.FOREIGN_KEY));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.FUNCTION));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.INDEX));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.OTHER));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.PRIMARY_KEY));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.PROC_TYPE_DBO));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.PROCEDURE));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.SCHEMA));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.SESSION));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.TABLE));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.TRIGGER));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.UDT));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.UDT_TYPE_DBO));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.USER));
		assertEquals(false, Compat.isTableTypeDBO(DatabaseObjectType.VIEW));
	}

	public void testAddToPopupForTableFolder() {
		//fail("Not yet implemented"); // TODO
	}

}
