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
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.client.ApplicationManager;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.test.TestUtil;

public class ObjectTreeTest extends BaseSQuirreLTestCase {

    ObjectTree tree = null;
    ISession session = null;

    public static void main(String[] args) {
        
        junit.textui.TestRunner.run(ObjectTreeTest.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        ApplicationManager.initApplication();
        //session = new MockSession();
        session = TestUtil.getEasyMockSession("Oracle");
        tree = new ObjectTree(session);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * A test to check that the tree can correctly find a previously-selected
     * node when row counts are being shown and the node's row count has 
     * changed (i.e. the user added or deleted records)
     */
    public void testMatchKeyPrefixDeletedRows() {
        Map<String, Object> map = new HashMap<String, Object>();
        String tableKey = "table(100)";
        map.put(tableKey, null);
        
        IDatabaseObjectInfo dbInfo = 
            TestUtil.getEasyMockDatabaseObjectInfo("catalog", 
                                                   "schema", 
                                                   "table", 
                                                   "schema.table",
                                                   DatabaseObjectType.TABLE);
        ObjectTreeNode node = new ObjectTreeNode(session, dbInfo);

        // Test to see that table(100) matches table(0).  It should since only
        // the row count is different.
        assertEquals(true, tree.matchKeyPrefix(map, node, "table(0)"));

        // Test to see if we can fool matchKeyPrefix into assuming that there 
        // will be '(' on the end of the path since row count is enabled.  Yet
        // we'll send in a string that doesn't have this characteristic.
        assertEquals(true, tree.matchKeyPrefix(map, node, "table"));
        
    }

}
