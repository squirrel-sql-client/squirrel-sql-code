package net.sourceforge.squirrel_sql.plugins.sessionscript;

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

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

import utils.EasyMockHelper;

/**
 *   Test class for AliasScript
 */
public class AliasScriptTest extends BaseSQuirreLJUnit4TestCase {

	AliasScript classUnderTest = new AliasScript();

	EasyMockHelper mockHelper = new EasyMockHelper();
	
	@Test
	public void testGetIdentifier() throws Exception
	{
		classUnderTest.setIdentifier(null);
		assertNull(classUnderTest.getIdentifier());
	}

	@Test
	public void testGetSQL() throws Exception
	{
		classUnderTest.setSQL("aTestString");
		assertEquals("aTestString", classUnderTest.getSQL());
	}

	@Test
	public void testEqualsAndHashcode() {
		IIdentifier id1 = mockHelper.createMock(IIdentifier.class);
		IIdentifier id2 = mockHelper.createMock(IIdentifier.class);
		
		ISQLAlias alias1 = mockHelper.createMock(ISQLAlias.class);
		EasyMock.expect(alias1.getIdentifier()).andStubReturn(id1);
		ISQLAlias alias2 = mockHelper.createMock(ISQLAlias.class);
		EasyMock.expect(alias2.getIdentifier()).andStubReturn(id2);
		
		mockHelper.replayAll();
		
		AliasScript a = new AliasScript(alias1);
		AliasScript b = new AliasScript(alias1);
		AliasScript c = new AliasScript(alias2);
		AliasScript d = new AliasScript(alias1) {
			private static final long serialVersionUID = 1L;
		};

		new EqualsTester(a, b, c, d); 
		
		mockHelper.verifyAll();
	}
}
