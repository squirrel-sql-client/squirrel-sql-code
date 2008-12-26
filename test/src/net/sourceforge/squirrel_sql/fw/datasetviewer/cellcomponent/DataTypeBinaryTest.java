package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.junit.Assert.assertNotNull;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

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
 * JUnit test for DataTypeBinary class.
 * 
 * @author manningr
 */
public class DataTypeBinaryTest extends AbstractDataTypeComponentTest
{

	public void setUp() throws Exception
	{
		ColumnDisplayDefinition columnDisplayDefinition = super.getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeBinary(null, columnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
	}

	@Override
	protected Object getEqualsTestObject()
	{
		return "aTestString".getBytes();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AbstractDataTypeComponentTest#testGetClassName()
	 */
	@Override
	public void testGetClassName() throws Exception
	{
		// Cannot pass byte array to Class.forName
		assertNotNull(classUnderTest.getClassName());
	}

}
