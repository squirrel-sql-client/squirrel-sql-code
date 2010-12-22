/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IObjectTypes;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;


import org.easymock.classextension.EasyMock;
import org.junit.Test;

public abstract class AbstractINodeExpanderTest extends BaseSQuirreLJUnit4TestCase
{

	protected INodeExpander classUnderTest = null;
	protected ISession mockSession = mockHelper.createMock("mockSession", ISession.class);
	protected ObjectTreeNode mockObjectTreeNode = mockHelper.createMock("mockObjectTreeNode", ObjectTreeNode.class);
	protected ISQLConnection mockSQLConnection = mockHelper.createMock("mockSQLConnection", ISQLConnection.class);
	protected SQLDatabaseMetaData mockSQLDatabaseMetaData = mockHelper.createMock("mockSQLDatabaseMetaData", SQLDatabaseMetaData.class);
	protected IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock("mockDatabaseObjectInfo", IDatabaseObjectInfo.class);
	protected PreparedStatement mockPreparedStatement = mockHelper.createMock("mockPreparedStatement", PreparedStatement.class);
	protected ResultSet mockResultSet = mockHelper.createMock("mockResultSet", ResultSet.class);
	protected IApplication mockApplication = mockHelper.createMock("mockApplication", IApplication.class);
	protected IIdentifier mockIdentifier = mockHelper.createMock("mockIdentifier", IIdentifier.class);
	protected SessionProperties mockSessionProperties = mockHelper.createMock("mockSessionProperties", SessionProperties.class);
	protected Class<?> clazz = null;
	protected SchemaInfo mockSchemaInfo = mockHelper.createMock("mockSchemaInfo", SchemaInfo.class);
	protected IObjectTypes mockObjectTypes = mockHelper.createMock("mockObjectTypes", IObjectTypes.class);

	@Test
	public void testCreateChildren() throws SQLException
	{
		setupMockObjects();
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getIdentifier()).andStubReturn(mockIdentifier);
		expect(mockSession.getProperties()).andStubReturn(mockSessionProperties);
		expect(mockSession.getSchemaInfo()).andStubReturn(mockSchemaInfo);
		
		expect(mockSQLConnection.getSQLMetaData()).andStubReturn(mockSQLDatabaseMetaData);
		expect(mockObjectTreeNode.getDatabaseObjectInfo()).andStubReturn(mockDatabaseObjectInfo);
		expect(mockSQLConnection.prepareStatement(EasyMock.isA(String.class))).andReturn(mockPreparedStatement);
		
		expect(mockPreparedStatement.executeQuery()).andReturn(mockResultSet);
		mockPreparedStatement.setString(EasyMock.anyInt(),EasyMock.isA(String.class));
		expectLastCall().anyTimes();
		mockPreparedStatement.close();
		
		expect(mockResultSet.next()).andReturn(true).andReturn(false);
		expect(mockResultSet.getString(1)).andStubReturn("aTestResultSetStringValue");
		expect(mockResultSet.getStatement()).andStubReturn(mockPreparedStatement);
		
		expect(mockSQLDatabaseMetaData.getDatabaseProductName()).andStubReturn("Oracle");
		expect(mockSQLDatabaseMetaData.getDatabaseProductVersion()).andStubReturn("10g");
		expect(mockSQLDatabaseMetaData.supportsSchemasInDataManipulation()).andStubReturn(true);
		expect(mockSQLDatabaseMetaData.supportsCatalogsInDataManipulation()).andStubReturn(false);
		expect(mockSQLDatabaseMetaData.supportsSchemasInTableDefinitions()).andStubReturn(true);
		expect(mockSQLDatabaseMetaData.getCatalogSeparator()).andStubReturn("");
		expect(mockSQLDatabaseMetaData.getIdentifierQuoteString()).andStubReturn("'");
		
		expect(mockSessionProperties.getObjectFilterExclude()).andStubReturn("");
		expect(mockSessionProperties.getObjectFilterInclude()).andStubReturn("");
		
		
		mockResultSet.close();
		expectLastCall().anyTimes();
		
		mockHelper.replayAll();
		classUnderTest.createChildren(mockSession, mockObjectTreeNode);
		mockHelper.resetAll();
	}

	protected void setupMockObjects()
	{
		expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(mockDatabaseObjectInfo.getCatalogName()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockDatabaseObjectInfo.getSimpleName()).andStubReturn(TEST_SIMPLE_NAME);
		expect(mockDatabaseObjectInfo.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);
		expect(mockDatabaseObjectInfo.getDatabaseObjectType()).andStubReturn(DatabaseObjectType.TABLE);
		
		expect(mockObjectTypes.getConstraint()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestConstraint"));
		expect(mockObjectTypes.getConstraintParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestConstraintParent"));
		expect(mockObjectTypes.getConsumerGroup()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestConsumerGroup"));
		expect(mockObjectTypes.getConsumerGroupParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestConsumerGroupParent"));
		expect(mockObjectTypes.getFunctionParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestFunctionParent"));
		expect(mockObjectTypes.getIndexParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestIndexParent"));
		expect(mockObjectTypes.getInstance()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestInstance"));
		expect(mockObjectTypes.getInstanceParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestInstanceParent"));
		expect(mockObjectTypes.getLob()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestLob"));
		expect(mockObjectTypes.getLobParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestLobParent"));
		expect(mockObjectTypes.getPackage()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestPackage"));	
		expect(mockObjectTypes.getPackageParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestPackageParent"));
		expect(mockObjectTypes.getSession()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestSession"));
		expect(mockObjectTypes.getSessionParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestSessionParent"));
		expect(mockObjectTypes.getSequenceParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestSequenceParent"));
		expect(mockObjectTypes.getTriggerParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestTriggerParent"));
		expect(mockObjectTypes.getType()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestType"));
		expect(mockObjectTypes.getTypeParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestTypeParent"));
		expect(mockObjectTypes.getUserParent()).andStubReturn(DatabaseObjectType.createNewDatabaseObjectType("TestUserParent"));
	}

}