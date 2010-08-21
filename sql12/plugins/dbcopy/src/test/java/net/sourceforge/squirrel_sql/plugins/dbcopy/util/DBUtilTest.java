package net.sourceforge.squirrel_sql.plugins.dbcopy.util;
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
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;


public class DBUtilTest extends BaseSQuirreLTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetForeignKeySQL() throws Exception {
        // Test for NPE in DBUtil.getForeignKeySQL when the TableInfo
        // returns null for getImportedKeys
        ITableInfo ti = createNiceMock(ITableInfo.class);
        DBUtil.getForeignKeySQL(null, ti, null);
    }

    // Bug #1714476 (DB copy uses wrong case for table names):  When the 
    // catalog/schema/object names come from the source session, don't mess
    // with the case, as the case is provided by the driver for the existing
    // table, and doesn't need to be fixed.    
    public void testGetQualifiedObjectName() throws SQLException {
        ISQLDatabaseMetaData md = createNiceMock(ISQLDatabaseMetaData.class);
        expect(md.getCatalogSeparator()).andReturn(".");
        expect(md.supportsCatalogsInTableDefinitions()).andReturn(true);
        expect(md.supportsSchemasInTableDefinitions()).andReturn(true);
        replay(md);
        ISession session = createNiceMock(ISession.class);
        expect(session.getMetaData()).andReturn(md).anyTimes();
        replay(session);
        String catalog = "TestCatalog";
        String schema = "TestSchema";
        String table = "TestTable";
        // case shouldn't be changed in this test because the context is the 
        // source database.
        String expQualifiedName = catalog + "." + schema + "." + table;
        String actQualifiedName = 
            DBUtil.getQualifiedObjectName(session, 
                                          catalog, 
                                          schema, 
                                          table, 
                                          DialectFactory.SOURCE_TYPE);
        assertEquals(expQualifiedName, actQualifiedName);
    }
}
