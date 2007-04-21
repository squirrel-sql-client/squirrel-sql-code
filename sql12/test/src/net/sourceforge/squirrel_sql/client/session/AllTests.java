package net.sourceforge.squirrel_sql.client.session;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("Client Session tests");
        suite.addTestSuite(SQLExecuterTaskTest.class);
		return suite;
	}
}
