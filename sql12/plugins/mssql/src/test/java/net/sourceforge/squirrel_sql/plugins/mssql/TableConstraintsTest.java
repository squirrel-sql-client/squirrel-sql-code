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

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.CheckConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.DefaultConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.ForeignKeyConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.MssqlConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.PrimaryKeyConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.TableConstraints;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TableConstraintsTest {

    private static final String TEST_COLUMN = "testColumn";
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
    public final void testGetDefaultsForColumn() {
   	 DefaultConstraint constraint = new DefaultConstraint();
   	 constraint.addConstraintColumn(TEST_COLUMN);
   	 constraintsUnderTest.addConstraint(constraint);
   	 List<DefaultConstraint> list = constraintsUnderTest.getDefaultsForColumn(TEST_COLUMN);
   	 assertEquals(1, list.size());
    }

    @Test
    public final void testGetCheckConstraints() {
   	 CheckConstraint constraint = new CheckConstraint();
   	 constraintsUnderTest.addConstraint(constraint);
   	 List<CheckConstraint> list = constraintsUnderTest.getCheckConstraints();
   	 assertEquals(1, list.size());
    }

    @Test
    public final void testGetForeignKeyConstraints() {
   	 ForeignKeyConstraint constraint = new ForeignKeyConstraint();
   	 constraintsUnderTest.addConstraint(constraint);
   	 List<ForeignKeyConstraint> list = constraintsUnderTest.getForeignKeyConstraints();
   	 assertEquals(1, list.size());
    }

    @Test
    public final void testGetPrimaryKeyConstraints() {
   	 PrimaryKeyConstraint constraint = new PrimaryKeyConstraint();
   	 constraintsUnderTest.addConstraint(constraint);
   	 List<PrimaryKeyConstraint> list = constraintsUnderTest.getPrimaryKeyConstraints();
   	 assertEquals(1, list.size());
    }

}
