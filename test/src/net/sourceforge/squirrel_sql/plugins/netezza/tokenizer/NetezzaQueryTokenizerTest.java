/*
 * Copyright (C) 2009 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.netezza.tokenizer;

import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class NetezzaQueryTokenizerTest extends BaseSQuirreLJUnit4TestCase
{
	private static final String storedProcSQL = 

		"CREATE OR REPLACE PROCEDURE exec_nzplsql_block(text) RETURNS BOOLEAN \n" +
		"LANGUAGE NZPLSQL AS \n" +
		"BEGIN_PROC \n" +
		"DECLARE lRet BOOLEAN; \n" +
		"DECLARE sid INTEGER; \n" +
		"DECLARE nm varchar; \n" +
		"DECLARE cr varchar; \n" +
		"BEGIN \n" +
		"sid := current_sid; \n" +
		"nm := 'any_block' || sid || '()'; \n" +
		"cr = 'CREATE OR REPLACE PROCEDURE ' || nm || \n" +
		"' RETURNS BOOL LANGUAGE NZPLSQL AS BEGIN_PROC ' \n" +
		"|| $1 || ' END_PROC'; \n" +
		"EXECUTE IMMEDIATE cr; \n" +
		"EXECUTE IMMEDIATE 'SELECT ' || nm; \n" +
		"EXECUTE IMMEDIATE 'DROP PROCEDURE ' || nm; \n" +
		"RETURN TRUE; \n" +
		"END; \n" +
		"END_PROC; \n";
	
	private NetezzaQueryTokenizer classUnderTest = null;
	
   // Mock Objects
   IQueryTokenizerPreferenceBean prefsBean = 
       EasyMock.createMock(IQueryTokenizerPreferenceBean.class);

   @Before
   public void setUp() throws Exception {
       EasyMock.expect(prefsBean.getStatementSeparator()).andReturn(";").anyTimes();
       EasyMock.expect(prefsBean.getProcedureSeparator()).andReturn("END_PROC").anyTimes();
       EasyMock.expect(prefsBean.getLineComment()).andReturn("--").anyTimes();
       EasyMock.expect(prefsBean.isRemoveMultiLineComments()).andReturn(false).anyTimes();
       EasyMock.replay(prefsBean);
       classUnderTest = new NetezzaQueryTokenizer(prefsBean);
   }

   @After
   public void tearDown() throws Exception {
   	classUnderTest = null;
   }

   @Test
   public final void testSetScriptToTokenizeOneProc() {
   	classUnderTest.setScriptToTokenize(storedProcSQL);
   	while (classUnderTest.hasQuery()) {
   		System.err.println("query: "+classUnderTest.nextQuery());
   	}
   	assertEquals(1, classUnderTest.getQueryCount());
   	
   }
   
   @Test
   public final void testSetScriptToTokenizeMultipleProcs() {
       StringBuilder script = new StringBuilder();
       for (int i = 0; i < 5; i++) {
           script.append(storedProcSQL);
           script.append("\n");
       }
       classUnderTest.setScriptToTokenize(script.toString());
       assertEquals(5, classUnderTest.getQueryCount());
   }
   
	
}
