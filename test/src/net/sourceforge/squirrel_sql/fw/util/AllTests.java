package net.sourceforge.squirrel_sql.fw.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("framework util tests");
		suite.addTestSuite(StringUtilitiesTest.class);
		return suite;
	}
}
