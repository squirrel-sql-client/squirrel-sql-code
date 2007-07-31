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
package net.sourceforge.squirrel_sql.client.session.mainpanel;

import junit.framework.TestCase;

import com.gargoylesoftware.base.testing.EqualsTester;

public class SQLHistoryItemTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEqualsObject() {
        String sql1 = "select foo from foo";
        String sql2 = "select foo2 from foo2";
        String aliasName1 = "TestAlias";
        String aliasName2 = "TestAlias2";
        SQLHistoryItem item1 = new SQLHistoryItem(sql1, aliasName1);
        SQLHistoryItem item2 = new SQLHistoryItem(sql1, aliasName1);
        SQLHistoryItem item3 = new SQLHistoryItem(sql2, aliasName2);
        SQLHistoryItem item4 = new SQLHistoryItem(sql1, aliasName1) {
            private static final long serialVersionUID = 1L;
        } ;
        
        new EqualsTester(item1, item2, item3, item4);
    }

}
