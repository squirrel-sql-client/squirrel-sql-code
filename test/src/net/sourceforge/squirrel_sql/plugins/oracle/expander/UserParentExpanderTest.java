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
package net.sourceforge.squirrel_sql.plugins.oracle.expander;


import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;

import org.junit.Before;

public class UserParentExpanderTest extends AbstractINodeExpanderTest
{

	@Before
	public void setUp() throws Exception
	{
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		
		expect(mockSQLConnection.prepareStatement(isA(String.class))).andStubReturn(mockPreparedStatement);
		expect(mockPreparedStatement.executeQuery()).andStubReturn(mockResultSet);
		mockResultSet.close();
		expect(mockResultSet.getStatement()).andReturn(mockPreparedStatement);
		mockPreparedStatement.close();
		mockHelper.replayAll();
		classUnderTest = new UserParentExpander(mockSession);
		mockHelper.verifyAll();
		mockHelper.resetAll();
	}

}
