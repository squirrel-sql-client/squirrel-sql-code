package net.sourceforge.squirrel_sql.fw.sql;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("SQL framework tests");
		suite.addTestSuite(SQLDatabaseMetaDataTest.class);
        suite.addTestSuite(QueryTokenizerTest.class);
        suite.addTestSuite(OracleQueryTokenizerTest.class);
        suite.addTestSuite(SQLUtilitiesTest.class);
		return suite;
	}
}
