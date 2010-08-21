/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.mysql.tokenizer;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MysqlQueryTokenizerTest extends BaseSQuirreLJUnit4TestCase {

    private static final String storedProcSQLWithSep = 
        "CREATE PROCEDURE sp_ins (P VARCHAR(10)) \n" +
        "BEGIN \n" +
        "SET @x=CHAR_LENGTH(P); \n" +
        "SET @y = HEX(P); \n" +
        "INSERT INTO sp1(id,txt) VALUES(@x,@y); \n" +
        "END \n" +
        "| \n";

    private static final String storedProcSQLWithoutSep = 
        "CREATE PROCEDURE sp_ins (P VARCHAR(10)) \n" +
        "BEGIN \n" +
        "SET @x=CHAR_LENGTH(P); \n" +
        "SET @y = HEX(P); \n" +
        "INSERT INTO sp1(id,txt) VALUES(@x,@y); \n" +
        "END \n";
    
    MysqlQueryTokenizer tokenizerUnderTest = null;
    
    // Mock Objects
    IQueryTokenizerPreferenceBean prefsBean = 
        EasyMock.createMock(IQueryTokenizerPreferenceBean.class);
    
    @Before
    public void setUp() throws Exception {
        EasyMock.expect(prefsBean.getStatementSeparator()).andReturn(";").anyTimes();
        EasyMock.expect(prefsBean.getProcedureSeparator()).andReturn("|").anyTimes();
        EasyMock.expect(prefsBean.getLineComment()).andReturn("--").anyTimes();
        EasyMock.expect(prefsBean.isRemoveMultiLineComments()).andReturn(false).anyTimes();
        EasyMock.replay(prefsBean);
        tokenizerUnderTest = new MysqlQueryTokenizer(prefsBean);
    }

    @After
    public void tearDown() throws Exception {
        tokenizerUnderTest = null;
    }

    @Test
    public final void testSetScriptToTokenizeWithSep() {
        tokenizerUnderTest.setScriptToTokenize(storedProcSQLWithSep);
        assertEquals(1, tokenizerUnderTest.getQueryCount());
    }
    
    @Test
    public final void testSetScriptToTokenizeWithoutSep() {
        tokenizerUnderTest.setScriptToTokenize(storedProcSQLWithoutSep);
        assertEquals(1, tokenizerUnderTest.getQueryCount());
    }
    
    @Test
    public final void testSetScriptToTokenizeMultipleProcs() {
        StringBuilder script = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            script.append(storedProcSQLWithSep);
            script.append("\n");
        }
        tokenizerUnderTest.setScriptToTokenize(script.toString());
        assertEquals(5, tokenizerUnderTest.getQueryCount());
    }

}
