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

import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.client.AppTestUtil;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.FwTestUtil;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.easymock.classextension.EasyMock;
import org.junit.Before;

/**
 * @author Stefan Willinger
 * 
 */
public class TreeLoaderTest extends BaseSQuirreLTestCase {
	private ISession mockSession = null;
	private IDatabaseObjectInfo mockDbInfo = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mockSession = AppTestUtil.getEasyMockSession("Oracle");
		mockDbInfo = FwTestUtil.getEasyMockDatabaseObjectInfo("catalog", "schema", "table", "schema.table",
				DatabaseObjectType.TABLE);
	}

	/**
	 * Ensure, that a node is marked with"NoChildrenFoundWithExpander" if a
	 * expander didn't find any children for this node.
	 */
	public void testNoChildrenFoundByExpander() throws Exception {

		ObjectTreeNode node = new ObjectTreeNode(mockSession, mockDbInfo);

		INodeExpander mockExpander = EasyMock.createMock(INodeExpander.class);
		// Simulate, that a expander didn't find any children.
		expect(mockExpander.createChildren(mockSession, node)).andReturn(new ArrayList<ObjectTreeNode>());

		// redefine the mockConnection
		ISQLConnection mockConnection = mockSession.getSQLConnection();
		EasyMock.resetToDefault(mockConnection);
		SQLDatabaseMetaData mockMetadata = EasyMock.createNiceMock(SQLDatabaseMetaData.class);
		EasyMock.expect(mockConnection.getSQLMetaData()).andReturn(mockMetadata);
		EasyMock.replay(mockConnection);
		EasyMock.replay(mockMetadata);

		ObjectTree mockObjectTree = EasyMock.createMock(ObjectTree.class);

		ObjectTreeModel mockModel = EasyMock.createMock(ObjectTreeModel.class);

		EasyMock.replay(mockExpander, mockObjectTree);

		TreeLoader classUnderTest = new TreeLoader(mockSession, mockObjectTree, mockModel, node,
				new INodeExpander[] { mockExpander }, false);

		classUnderTest.execute();

		assertTrue(node.hasNoChildrenFoundWithExpander());
	}

	/**
	 * Ensure, that a node is not marked with"NoChildrenFoundWithExpander" if a
	 * expander find a child node
	 */
	public void testChildrenFoundByExpander() throws Exception {
		// The parent node
		ObjectTreeNode node = new ObjectTreeNode(mockSession, mockDbInfo);

		// Simulate, that a expander didn't find any children.
		INodeExpander mockExpander = EasyMock.createMock(INodeExpander.class);
		List<ObjectTreeNode> childs = new ArrayList<ObjectTreeNode>();
		childs.add(new ObjectTreeNode(mockSession, mockDbInfo));
		expect(mockExpander.createChildren(mockSession, node)).andReturn(childs);


		// Setup some infrastucture
		ISQLConnection mockConnection = mockSession.getSQLConnection();
		EasyMock.resetToDefault(mockConnection);
		SQLDatabaseMetaData mockMetadata = EasyMock.createNiceMock(SQLDatabaseMetaData.class);
		EasyMock.expect(mockConnection.getSQLMetaData()).andReturn(mockMetadata);
		EasyMock.replay(mockConnection);
		EasyMock.replay(mockMetadata);


		// Setup a ObjectTree with a Model
		ObjectTree mockObjectTree = EasyMock.createMock(ObjectTree.class);
		ObjectTreeModel mockModel = EasyMock.createNiceMock(ObjectTreeModel.class);
		EasyMock.expect(mockModel.getExpanders(childs.get(0).getDatabaseObjectType())).andReturn(
				new INodeExpander[] {});
		// We need the mock thread save, because some methods will be called at
		// the AWT-Event thread.
		EasyMock.makeThreadSafe(mockModel, true);

		EasyMock.expect(mockObjectTree.getTypedModel()).andReturn(mockModel);

		EasyMock.replay(mockExpander, mockObjectTree, mockModel);

		// Create a Treeloader
		TreeLoader classUnderTest = new TreeLoader(mockSession, mockObjectTree, mockModel, node,
				new INodeExpander[] { mockExpander }, false);

		classUnderTest.execute();

		// The expander found some childs.
		assertFalse(node.hasNoChildrenFoundWithExpander());
		
		// Because we didn't specify a expander for the new child
		assertFalse(childs.get(0).getAllowsChildren());
	}

}
