package net.sourceforge.squirrel_sql.fw.util;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { StringUtilitiesTest.class, 
                 DefaultExceptionFormatterTest.class,
                 MyURLClassLoaderTest.class})
public class AllTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite("framework util tests");
		suite.addTestSuite(StringUtilitiesTest.class);
        suite.addTestSuite(MyURLClassLoaderTest.class);
        suite.addTest(new JUnit4TestAdapter(DefaultExceptionFormatterTest.class));
		return suite;
	}

}
