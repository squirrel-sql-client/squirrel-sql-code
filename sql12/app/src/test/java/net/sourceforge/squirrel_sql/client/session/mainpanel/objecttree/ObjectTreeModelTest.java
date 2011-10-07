/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import static org.easymock.classextension.EasyMock.expect;

import java.util.ArrayList;

import javax.swing.tree.TreePath;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.client.AppTestUtil;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
import net.sourceforge.squirrel_sql.fw.FwTestUtil;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

/**
 * Some Tests about the ObjectTreeModel.
 * @author Stefan Willinger
 *
 */
public class ObjectTreeModelTest extends BaseSQuirreLTestCase {

    private ISession session = null;
    private ObjectTreeModel classUnderTest = null;
    private IDatabaseObjectInfo mockDbInfo = null;

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        session = AppTestUtil.getEasyMockSession("Oracle");
        classUnderTest = new ObjectTreeModel(session);
        
        SessionManager mockSessionManager = session.getApplication().getSessionManager();
    	
    	// redefine the mockSessionManager, to know our mockSession.
    	EasyMock.resetToDefault(mockSessionManager);
    	EasyMock.expect(mockSessionManager.getSession(session.getIdentifier())).andReturn(session).anyTimes();
    	EasyMock.replay(mockSessionManager);
    	
    	 mockDbInfo = 
    	            FwTestUtil.getEasyMockDatabaseObjectInfo("catalog", 
    	                                                   "schema", 
    	                                                   "table", 
    	                                                   "schema.table",
    	                                                   DatabaseObjectType.TABLE);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
     * Ensure, that the expander is not called twice to fetch the children, if no children exists.
     */
    @Test
    public void testPathToDBInfo_NoChildrensFoundByExpander() throws Exception {
        
        ObjectTreeNode node = new ObjectTreeNode(session, mockDbInfo);
        
        INodeExpander mockExpander = EasyMock.createMock(INodeExpander.class);
        // Simulate, that a expander didn't find any children.
        expect(mockExpander.createChildren(session, node)).andReturn(new ArrayList<ObjectTreeNode>());

		EasyMock.replay(mockExpander);
       
       classUnderTest.addExpander(node.getDatabaseObjectType(), mockExpander);
       FilterMatcher matcher = new FilterMatcher("xy", null);
       TreePath treePath = classUnderTest.getPathToDbInfo(mockDbInfo.getCatalogName(), mockDbInfo.getSchemaName(), matcher , node, true);
       assertNull(treePath);
       // after the first call, the node must know, that expanders didn't find any children.
       assertTrue(node.hasNoChildrenFoundWithExpander());
       
       // Try it a second time.
       treePath = classUnderTest.getPathToDbInfo(mockDbInfo.getCatalogName(), mockDbInfo.getSchemaName(), matcher , node, true);
       assertNull(treePath);
       
       // Verify, that the expander is only called once.
       EasyMock.verify(mockExpander);
        
    }
    
    /**
     * Ensure, that the expander is not called, if the node is already marked with "NoChildrenFoundWithExpander".
     */
    @Test
    public void testPathToDBInfo_AlreadyNoChildrensFoundByExpander() throws Exception {
    	
      
        ObjectTreeNode node = new ObjectTreeNode(session, mockDbInfo);
        node.setNoChildrenFoundWithExpander(true);
        
        INodeExpander mockExpander = EasyMock.createMock(INodeExpander.class);
		EasyMock.replay(mockExpander);
       
       classUnderTest.addExpander(node.getDatabaseObjectType(), mockExpander);
       FilterMatcher matcher = new FilterMatcher("xy", null);
       TreePath treePath = classUnderTest.getPathToDbInfo(mockDbInfo.getCatalogName(), mockDbInfo.getSchemaName(), matcher , node, true);
       assertNull(treePath);
       
       treePath = classUnderTest.getPathToDbInfo(mockDbInfo.getCatalogName(), mockDbInfo.getSchemaName(), matcher , node, true);
       assertNull(treePath);
       
       // Verify, that the expander is not called
		EasyMock.verify(mockExpander);

	}
}
