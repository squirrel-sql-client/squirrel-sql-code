package net.sourceforge.squirrel_sql.fw.sql;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.plugins.oracle.tokenizer.OracleQueryTokenizer;

public class OracleQueryTokenizerTest extends TestCase
                                      implements OracleSQL {

    static String nullSQL = null;       
    static String tmpFilename = null;
    static boolean removeMultilineComment = true;
    static {
        ApplicationArguments.initialize(new String[] {});        
    }
    
    QueryTokenizer qt = null;
    static int sqlFileStmtCount = 0;
    
    public void setUp() throws Exception {
        createSQLFile();
    }
    
    public void tearDown() {
        
    }
    
    public void testHasQuery() {
        qt = new OracleQueryTokenizer();
        qt.setScriptToTokenize(SELECT_DUAL);
        SQLUtil.checkQueryTokenizer(qt, 1);
        
        qt = new OracleQueryTokenizer();
        qt.setScriptToTokenize(SELECT_DUAL_2);
        SQLUtil.checkQueryTokenizer(qt, 1);        
    }

    public void testGenericSQL() {
        String script = SQLUtil.getGenericSQLScript();
        qt = new OracleQueryTokenizer();
        qt.setScriptToTokenize(script);
        SQLUtil.checkQueryTokenizer(qt, SQLUtil.getGenericSQLCount());
    }
    
    public void testCreateStoredProcedure() {
        qt = new OracleQueryTokenizer();
        qt.setScriptToTokenize(CREATE_STORED_PROC);
        SQLUtil.checkQueryTokenizer(qt, 1);
    }

    public void testCreateOrReplaceStoredProcedure() {
        qt = new OracleQueryTokenizer();
        qt.setScriptToTokenize(CREATE_OR_REPLACE_STORED_PROC);
        SQLUtil.checkQueryTokenizer(qt, 1);
    }
    
    public void testHasQueryFromFile() {
        String fileSQL = "@" + tmpFilename + ";\n";
        qt = new OracleQueryTokenizer();
        qt.setScriptToTokenize(fileSQL);
        SQLUtil.checkQueryTokenizer(qt, 6);
    }
    
    private static void createSQLFile() throws IOException {
        if (tmpFilename != null) {
            return;
        }
        File f = File.createTempFile("test", ".sql");
        //f.deleteOnExit();
        PrintWriter out = new PrintWriter(new FileWriter(f));
        out.println(SELECT_DUAL);
        out.println();
        out.print(UPDATE_TEST);
        out.println();
        out.println(CREATE_STORED_PROC);
        out.println();
        out.println(CREATE_OR_REPLACE_STORED_PROC);
        out.println();
        out.println(SELECT_DUAL);
        out.println();
        out.println(STUDENTS_NOT_TAKING_CS112);
        out.println();
        out.close();
        tmpFilename = f.getAbsolutePath();
        System.out.println("tmpFilename="+tmpFilename);
        
        // important to set this to the number of statements in the file 
        // above.
        sqlFileStmtCount = 6;
    }
    
}
