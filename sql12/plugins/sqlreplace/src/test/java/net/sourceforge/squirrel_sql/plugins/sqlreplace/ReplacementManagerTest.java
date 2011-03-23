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
package net.sourceforge.squirrel_sql.plugins.sqlreplace;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReplacementManagerTest 
{

	private ReplacementManager classUnderTest = null;
	
	@Mock
	private SQLReplacePlugin mockPlugin = null;
	
	@Mock
	private IApplication mockApplication;
	
	@Mock
	private IMessageHandler mockMessageHandler;
		
	@Before
	public void setUp() throws Exception
	{
		when(mockApplication.getMessageHandler()).thenReturn(mockMessageHandler);
		when(mockPlugin.getApplication()).thenReturn(mockApplication);

	}

	@After
	public void tearDown() throws Exception
	{
		File f = new File("sqlreplacement.xml");
		if (f.exists()) {
			f.delete();
		}
	}

	@Test
	public void testReplaceIllegalCharsInReplaceStr() throws Exception
	{
		classUnderTest = new ReplacementManager(mockPlugin);
		
		classUnderTest.setContentFromEditor("$P{StartDate} = '2008-01-01'\n");
		
		StringBuffer testSql = new StringBuffer("select $P{StartDate} from dual");
		
		String result = classUnderTest.replace(testSql);
		
		assertEquals("select '2008-01-01' from dual", result);
		
	}
	
	
	@Test
	public void testReplaceTableName() throws Exception {

		classUnderTest = new ReplacementManager(mockPlugin);
		
		classUnderTest.setContentFromEditor("bigint_type_tbl = bigint_type_table\n");
		
		StringBuffer testSql = new StringBuffer("SELECT * FROM bigint_type_tbl");
		
		String result = classUnderTest.replace(testSql);
		
		assertEquals("SELECT * FROM bigint_type_table", result);
		
		
	}
	
	
}
