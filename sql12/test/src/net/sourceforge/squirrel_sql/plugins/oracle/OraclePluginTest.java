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
package net.sourceforge.squirrel_sql.plugins.oracle;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DTProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeTimestamp;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.AbstractPluginTest;
import net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace.NewSGATraceWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.dboutput.NewDBOutputWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.exception.OracleExceptionFormatter;
import net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects.NewInvalidObjectsWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.sessioninfo.NewSessionInfoWorksheetAction;
import net.sourceforge.squirrel_sql.test.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class OraclePluginTest extends AbstractPluginTest {

    OraclePlugin pluginUnderTest = null;
    
    // Mock objects
    ISession session = null;
    IApplication app = null;
    ISQLDatabaseMetaData md = null;
    
    /** class name for the TimeStamp datatype */
    String timestampClassName = DataTypeTimestamp.class.getName();
    
    @Before
    public void setUp() throws Exception {
        pluginUnderTest = new OraclePlugin();
        md = TestUtil.getEasyMockSQLMetaData("oracle",
                "jdbc:oracle:thin:@host:1521:sid", false, false);
        String[] functions = new String[] {
                OracleExceptionFormatter.OFFSET_FUNCTION_NAME
        };        
        expect(md.getStringFunctions()).andReturn(functions);
        replay(md);
        ActionCollection col = getOraclePluginActionCollection();
        app = TestUtil.getEasyMockApplication(col);
        pluginUnderTest.load(app);
        pluginUnderTest.initialize();
        session = TestUtil.getEasyMockSession(md, true);       
        classUnderTest = new OraclePlugin();
    }

    @After
    public void tearDown() throws Exception {
        session = null;
        app = null;
        md = null;
        classUnderTest = null;
    }

    /**
     * This tests the sessionStarted method for 
     * bug #1820214 (2.5.1 DB aliases do not transfer properly to 2.6.1). An NPE
     * is thrown when the use has specified no setting how timestamps are used 
     * in where clauses.
     */
    @Test
    public void testSessionStarted() {
        // we cannot mock DTProperties  because it is designed to be accessed
        // statically. :(
        DTProperties.put(timestampClassName, "", null); // Simulate missing property.
        pluginUnderTest.sessionStarted(session);
    }

    private ActionCollection getOraclePluginActionCollection() {
        ActionCollection result = TestUtil.getEasyMockActionCollection(false);
        Action someAction = createMock(Action.class);
        replay(someAction);
        expect(result.get(NewDBOutputWorksheetAction.class)).andReturn(someAction).anyTimes();
        expect(result.get(NewInvalidObjectsWorksheetAction.class)).andReturn(someAction).anyTimes();
        expect(result.get(NewSessionInfoWorksheetAction.class)).andReturn(someAction).anyTimes();
        expect(result.get(NewSGATraceWorksheetAction.class)).andReturn(someAction).anyTimes();
        result.add(isA(SquirrelAction.class));
        expectLastCall().anyTimes();
        replay(result);
        return result;
    }
    
}

