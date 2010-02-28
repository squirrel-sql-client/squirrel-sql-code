package net.sourceforge.squirrel_sql.plugins.oracle.tokenizer;
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
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.ANON_PROC_EXEC;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.ANON_PROC_EXEC_2;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.CREATE_FUNCTION_SQL;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.CREATE_OR_REPLACE_PACKAGE_BODY_SQL;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.CREATE_OR_REPLACE_PACKAGE_SQL;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.CREATE_OR_REPLACE_STORED_PROC;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.CREATE_STORED_PROC;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.NO_SEP_SLASH_SQL;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.SELECTS_WITH_EMBEDDED_COMMENT;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.SELECT_DUAL;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.SELECT_DUAL_2;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.SET_COMMANDS;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.STUDENTS_NOT_TAKING_CS112;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.UPDATE_TEST;

import java.io.IOException;
import java.util.Arrays;

import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.gui.DummyPlugin;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtil;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePreferenceBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OracleQueryTokenizerTest {

    static String nullSQL = null;       
    static String tmpFilename = null;
    static boolean removeMultilineComment = true;
    
    QueryTokenizer qt = null;
    static int sqlFileStmtCount = 0;
    
    static IQueryTokenizerPreferenceBean _prefs;
    
    @Before
    public void setUp() throws Exception {
        createSQLFile();
        DummyPlugin plugin = new DummyPlugin();
        PluginQueryTokenizerPreferencesManager prefsManager = new PluginQueryTokenizerPreferencesManager();
        prefsManager.initialize(plugin, new OraclePreferenceBean());
        _prefs = prefsManager.getPreferences();
        _prefs.setRemoveMultiLineComments(false);
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void testHasQuery() {
        qt = new OracleQueryTokenizer(_prefs);
        qt.setScriptToTokenize(SELECT_DUAL);
        SQLUtil.checkQueryTokenizer(qt, 1);
        
        qt = new OracleQueryTokenizer(_prefs);
        qt.setScriptToTokenize(SELECT_DUAL_2);
        SQLUtil.checkQueryTokenizer(qt, 1);        
    }

    @Test
    public void testGenericSQL() {
        String script = SQLUtil.getGenericSQLScript();
        qt = new OracleQueryTokenizer(_prefs);
        qt.setScriptToTokenize(script);
        SQLUtil.checkQueryTokenizer(qt, SQLUtil.getGenericSQLCount());
    }
    
    @Test
    public void testCreateStoredProcedure() {
        qt = new OracleQueryTokenizer(_prefs);
        qt.setScriptToTokenize(CREATE_STORED_PROC);
        SQLUtil.checkQueryTokenizer(qt, 1);
    }

    @Test
    public void testCreateOrReplaceStoredProcedure() {
        qt = new OracleQueryTokenizer(_prefs);
        qt.setScriptToTokenize(CREATE_OR_REPLACE_STORED_PROC);
        SQLUtil.checkQueryTokenizer(qt, 1);
    }
    
    @Test
    public void testCreatePackage() {
       qt = new OracleQueryTokenizer(_prefs);
       qt.setScriptToTokenize(CREATE_OR_REPLACE_PACKAGE_SQL);
       SQLUtil.checkQueryTokenizer(qt, 1);   	 
    }

    @Test
    public void testCreatePackageBody() {
       qt = new OracleQueryTokenizer(_prefs);
       qt.setScriptToTokenize(CREATE_OR_REPLACE_PACKAGE_BODY_SQL);
       SQLUtil.checkQueryTokenizer(qt, 1);   	 
    }    
    
    @Test
    public void testHasQueryFromFile() {
        String fileSQL = "@" + tmpFilename + ";\n";
        qt = new OracleQueryTokenizer(_prefs);
        qt.setScriptToTokenize(fileSQL);
        SQLUtil.checkQueryTokenizer(qt, sqlFileStmtCount);
    }

    @Test 
    public void testExecAnonProcedure() {
        qt = new OracleQueryTokenizer(_prefs);
        qt.setScriptToTokenize(ANON_PROC_EXEC);
        SQLUtil.checkQueryTokenizer(qt, 1);
    }    

    @Test
    public void testExecAnonProcedure2() {
        qt = new OracleQueryTokenizer(_prefs);
        qt.setScriptToTokenize(ANON_PROC_EXEC_2);
        SQLUtil.checkQueryTokenizer(qt, 1);
    }    
    
    @Test
    public void testNoSepSlash() {
        qt = new OracleQueryTokenizer(_prefs);
        qt.setScriptToTokenize(NO_SEP_SLASH_SQL);
        SQLUtil.checkQueryTokenizer(qt, 2);        
    }    
    
    @Test
    public void testStripSQLPlusCommands() {
       qt = new OracleQueryTokenizer(_prefs);
       qt.setScriptToTokenize(SET_COMMANDS);
       SQLUtil.checkQueryTokenizer(qt, 2);
    }
    
    @Test
    public void testEmbeddedQuote() {
       qt = new OracleQueryTokenizer(_prefs);
       qt.setScriptToTokenize(SELECTS_WITH_EMBEDDED_COMMENT);
       SQLUtil.checkQueryTokenizer(qt, 2);
    }
    
    private static void createSQLFile() throws IOException {
        if (tmpFilename != null) {
            return;
        }
        String[] sqls = new String[] {
                SELECT_DUAL, 
                UPDATE_TEST,
                CREATE_FUNCTION_SQL,
                CREATE_STORED_PROC,
                CREATE_OR_REPLACE_STORED_PROC,
                ANON_PROC_EXEC,
                SELECT_DUAL,
                STUDENTS_NOT_TAKING_CS112,
                NO_SEP_SLASH_SQL,
        };
        
        tmpFilename = SQLUtil.createSQLFile( Arrays.asList(sqls), true);        
        sqlFileStmtCount = sqls.length;
    }
    
}
