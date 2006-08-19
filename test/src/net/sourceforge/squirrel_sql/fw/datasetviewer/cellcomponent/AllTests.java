package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("cellcomponent tests");
		suite.addTestSuite(DataTypeBigDecimalTest.class);
		suite.addTestSuite(DataTypeBinaryTest.class);
		suite.addTestSuite(DataTypeBooleanTest.class);
		suite.addTestSuite(DataTypeByteTest.class);
		suite.addTestSuite(DataTypeClobTest.class);
		suite.addTestSuite(DataTypeDateTest.class);
		suite.addTestSuite(DataTypeDoubleTest.class);
		suite.addTestSuite(DataTypeFloatTest.class);
		suite.addTestSuite(DataTypeIntegerTest.class);
		suite.addTestSuite(DataTypeLongTest.class);
		suite.addTestSuite(DataTypeOtherTest.class);
		suite.addTestSuite(DataTypeShortTest.class);
		suite.addTestSuite(DataTypeStringTest.class);
		suite.addTestSuite(DataTypeTimestampTest.class);
		suite.addTestSuite(DataTypeUnknownTest.class);
		return suite;
	}
}
