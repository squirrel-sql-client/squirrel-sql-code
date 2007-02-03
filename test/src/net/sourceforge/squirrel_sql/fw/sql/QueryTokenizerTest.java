package net.sourceforge.squirrel_sql.fw.sql;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;

public class QueryTokenizerTest extends TestCase implements OracleSQL {

    static String nullSQL = null;       
    static String tmpFilename = null;
    static boolean removeMultilineComment = true;
    static {
        ApplicationArguments.initialize(new String[] {});        
    }
    
    QueryTokenizer qt = null;
    
    public void setUp() throws Exception {
        createSQLFile();
    }
    
    public void tearDown() {
        
    }
    
    public void testHasQuery() {
        qt = new QueryTokenizer(";", "--", removeMultilineComment);
        qt.setScriptToTokenize(SELECT_DUAL);
        checkQueryTokenizer(qt, 1);
        
        qt = new QueryTokenizer(";", "--", removeMultilineComment);
        qt.setScriptToTokenize(SELECT_DUAL_2);
        checkQueryTokenizer(qt, 1);        
    }

    public void testCreateStoredProcedure() {
        qt = new QueryTokenizer(";", "--", removeMultilineComment);
        qt.setScriptToTokenize(CREATE_STORED_PROC);
        checkQueryTokenizer(qt, 1);
    }

    public void testCreateOrReplaceStoredProcedure() {
        qt = new QueryTokenizer(";", "--", removeMultilineComment);
        qt.setScriptToTokenize(CREATE_OR_REPLACE_STORED_PROC);
        checkQueryTokenizer(qt, 1);
    }
    
    public void testHasQueryFromFile() {
        String fileSQL = "@" + tmpFilename + ";\n";
        qt = new QueryTokenizer(";", "--", removeMultilineComment);
        qt.setScriptToTokenize(fileSQL);
        checkQueryTokenizer(qt, 5);
    }
    
    private void checkQueryTokenizer(QueryTokenizer qt, int stmtCount) {
        int count = 0;
        while (qt.hasQuery()) {
            count++;
            System.out.println(" query: "+qt.nextQuery());
        }
        assertEquals(stmtCount, count);                
    }
    
    private static void createSQLFile() throws IOException {
        tmpFilename  = "C:\\DOCUME~1\\robert\\LOCALS~1\\Temp\\test3622.sql";
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
        out.close();
        tmpFilename = f.getAbsolutePath();
        System.out.println("tmpFilename="+tmpFilename);
    }
    
}
