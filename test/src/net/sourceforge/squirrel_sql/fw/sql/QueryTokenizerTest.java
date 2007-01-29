package net.sourceforge.squirrel_sql.fw.sql;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;

public class QueryTokenizerTest extends TestCase implements OracleSQL {

    static String nullSQL = null;
    static String sql3 =  "update test set /*PARAM1*/ thing /*C*/ = 'default value' /*/PARAM1*/;";
       
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
        qt = new QueryTokenizer(SELECT_DUAL, ";", "--", removeMultilineComment, true);
        checkQueryTokenizer(qt, 1);
    }

    public void testCreateStoredProcedure() {
        qt = new QueryTokenizer(CREATE_STORED_PROC, ";", "--", removeMultilineComment, true);
        checkQueryTokenizer(qt, 1);
    }

    public void testCreateOrReplaceStoredProcedure() {
        qt = new QueryTokenizer(CREATE_OR_REPLACE_STORED_PROC, ";", "--", removeMultilineComment, true);
        checkQueryTokenizer(qt, 1);
    }

    public void testCreateOrReplaceStoredProcedure2() {
        qt = new QueryTokenizer(CREATE_OR_REPLACE_STORED_PROC2, ";", "--", removeMultilineComment, true);
        checkQueryTokenizer(qt, 1);
    }
    
    public void testHasQueryFromFile() {
        String fileSQL = "@" + tmpFilename + ";\n";
        qt = new QueryTokenizer(fileSQL, ";", "--", removeMultilineComment, true);
        checkQueryTokenizer(qt, 3);
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
        if (tmpFilename != null) {
            return;
        }
        File f = File.createTempFile("test", ".sql");
        f.deleteOnExit();
        PrintWriter out = new PrintWriter(new FileWriter(f));
        out.print(SELECT_DUAL);
        out.println();
        out.print(SELECT_DUAL);
        out.println();
        out.print(sql3);
        out.println();
        out.print(CREATE_STORED_PROC);
        out.println();
        out.print(CREATE_OR_REPLACE_STORED_PROC);
        out.print(";");
        out.println();
        
        out.close();
        tmpFilename = f.getAbsolutePath();
        //System.out.println("tmpFilename="+tmpFilename);
    }
    
}
