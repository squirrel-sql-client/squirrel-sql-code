package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.easymock.EasyMock.expect;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;

import utils.EasyMockHelper;

/*
 * Copyright (C) 2006 Rob Manning
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

/**
 * JUnit test for DataTypeClob class.
 * 
 * @author manningr
 */
public class DataTypeClobTest extends AbstractDataTypeComponentTest {

	EasyMockHelper localMockHelper = new EasyMockHelper();
	
	@Before
	public void setUp() throws Exception {
		ColumnDisplayDefinition mockColumnDisplayDefinition = 
			localMockHelper.createMock("testCDD", ColumnDisplayDefinition.class);
		org.easymock.classextension.EasyMock.expect(mockColumnDisplayDefinition.isNullable()).andStubReturn(false);
		org.easymock.classextension.EasyMock.replay(mockColumnDisplayDefinition);
		
		classUnderTest = new DataTypeClob(null, mockColumnDisplayDefinition);

		super.setUp();
		super.defaultValueIsNull = true;
	}

	@Override
	protected Object getEqualsTestObject()
	{
		ClobDescriptor result = mockHelper.createMock("testClobDescriptor", ClobDescriptor.class);
		expect(result.getWholeClobRead()).andStubReturn(true);
		expect(result.getData()).andStubReturn("aTestString");
		ClobDescriptor nullClobDesc = null;
		expect(result.equals(nullClobDesc)).andStubReturn(false);
		return result;
	}

}
