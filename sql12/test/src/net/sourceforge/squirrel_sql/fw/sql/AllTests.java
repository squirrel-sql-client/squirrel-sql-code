package net.sourceforge.squirrel_sql.fw.sql;
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
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("SQL framework tests");
        suite.addTest(new JUnit4TestAdapter(DatabaseObjectInfoTest.class));
        suite.addTestSuite(JDBCTypeMapperTest.class);
        suite.addTestSuite(QueryTokenizerTest.class);
        suite.addTest(new JUnit4TestAdapter(ResultSetReader.class));
        suite.addTestSuite(SQLDatabaseMetaDataTest.class);
        suite.addTestSuite(SQLUtilitiesTest.class);
		return suite;
	}
}
