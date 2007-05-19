package net.sourceforge.squirrel_sql.fw.id;
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
import junit.framework.TestCase;

import com.gargoylesoftware.base.testing.EqualsTester;

public class UidIdentifierTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEquals() {
        UidIdentifier uid1 = createIIdentifier();
        uid1.setString("1");
        UidIdentifier uid2 = createIIdentifier();
        uid2.setString("1");
        UidIdentifier uid3 = createIIdentifier();
        uid3.setString("2");
        UidIdentifier uid4 = new UidIdentifier() {};
        uid4.setString("1");
        new EqualsTester(uid1, uid2, uid3, uid4);
    }

    protected UidIdentifier createIIdentifier() {
        return new UidIdentifier();
    }
}
