package net.sourceforge.squirrel_sql;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	
	public static Test suite() {
		TestSuite result = new TestSuite("squirrel_sql tests");
        result.addTest(net.sourceforge.squirrel_sql.client.session.mainpanel.AllTests.suite());
		result.addTest(net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.session.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.dialects.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.sql.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.util.AllTests.suite());
		result.addTest(net.sourceforge.squirrel_sql.plugins.dbcopy.util.AllTests.suite());
		return result;
	}
}
