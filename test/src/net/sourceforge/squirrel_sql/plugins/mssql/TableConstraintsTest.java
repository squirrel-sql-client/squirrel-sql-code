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
package net.sourceforge.squirrel_sql.plugins.mssql;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.MssqlConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.TableConstraints;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TableConstraintsTest {

    TableConstraints constraintsUnderTest = null;
    
    @Before
    public void setUp() throws Exception {
        constraintsUnderTest = new TableConstraints();
    }

    @After
    public void tearDown() throws Exception {
        constraintsUnderTest = null;
    }

    @Test
    public final void testGetConstraints() {
        MssqlConstraint[] constraints = constraintsUnderTest.getConstraints();
        assertEquals(0, constraints.length);
    }

    @Test
    public final void testAddConstraint() {
        constraintsUnderTest.addConstraint(new MssqlConstraint());
        MssqlConstraint[] constraints = constraintsUnderTest.getConstraints();
        assertEquals(1, constraints.length);
    }

    @Test
    @Ignore
    public final void testGetDefaultsForColumn() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    @Ignore
    public final void testGetCheckConstraints() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    @Ignore
    public final void testGetForeignKeyConstraints() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    @Ignore
    public final void testGetPrimaryKeyConstraints() {
        fail("Not yet implemented"); // TODO
    }

}
