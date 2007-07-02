package net.sourceforge.squirrel_sql;
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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	
	public static Test suite() {
		TestSuite result = new TestSuite("squirrel_sql tests");
        result.addTest(net.sourceforge.squirrel_sql.client.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.db.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.gui.db.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.mainframe.action.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.session.mainpanel.AllTests.suite());
		result.addTest(net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.session.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.codereformat.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.dialects.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.gui.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.id.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.sql.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.util.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.plugins.dbcopy.AllTests.suite());
		result.addTest(net.sourceforge.squirrel_sql.plugins.dbcopy.util.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.plugins.dbdiff.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.plugins.derby.tokenizer.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.plugins.oracle.tokenizer.AllTests.suite());        
		return result;
	}
}
