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
package net.sourceforge.squirrel_sql.plugins.derby.types;


import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.awt.Component;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AbstractDataTypeComponentTest;

public class DerbyClobDataTypeComponentTest extends AbstractDataTypeComponentTest
{

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AbstractDataTypeComponentTest#setUp()
	 */
	@Override
	public void setUp() throws Exception
	{
		mockColumnDisplayDefinition = super.getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DerbyClobDataTypeComponent();
		classUnderTest.setColumnDisplayDefinition(mockColumnDisplayDefinition);
		mockHelper.resetAll();
		
		classUnderTest.setBeepHelper(mockBeepHelper);

		expect(mockMetaData.getDatabaseProductName()).andStubReturn("testDatabaseProductName");
		expect(mockMetaData.getDatabaseProductVersion()).andStubReturn("testDatabaseProductVersion");
		mockBeepHelper.beep(isA(Component.class));
		expectLastCall().anyTimes();
		
		super.defaultValueIsNull = true;

	}

	@Override
	protected Object getEqualsTestObject()
	{
		return new DerbyClobDescriptor("aTestString");
	}
	
	

}
