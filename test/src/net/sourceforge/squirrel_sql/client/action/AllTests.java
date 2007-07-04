package net.sourceforge.squirrel_sql.client.action;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { ActionCollectionTest.class })
public class AllTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite("framework util tests");
        suite.addTest(new JUnit4TestAdapter(ActionCollectionTest.class));
		return suite;
	}

}
