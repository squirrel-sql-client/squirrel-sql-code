package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("objecttree tests");
		suite.addTestSuite(ObjectTreeTest.class);
		return suite;
	}
}
