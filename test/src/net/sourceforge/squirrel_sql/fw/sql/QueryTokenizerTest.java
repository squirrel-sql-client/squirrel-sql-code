package net.sourceforge.squirrel_sql.fw.sql;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;

public class QueryTokenizerTest extends TestCase implements GenericSQL {

    static String nullSQL = null;       
    static String tmpFilename = null;
    static boolean removeMultilineComment = true;
    static {
        ApplicationArguments.initialize(new String[] {});        
    }
    QueryTokenizer qt = null;
    
    
    public void testHasQueryOne() {
        qt = new QueryTokenizer(";", "--", removeMultilineComment);
        qt.setScriptToTokenize(CREATE_STUDENT);
        SQLUtil.checkQueryTokenizer(qt, 1);
        
        qt = new QueryTokenizer(";", "--", removeMultilineComment);
        qt.setScriptToTokenize(CREATE_COURSES);
        SQLUtil.checkQueryTokenizer(qt, 1);    
        
        qt = new QueryTokenizer(";", "--", removeMultilineComment);
        qt.setScriptToTokenize(STUDENTS_NOT_TAKING_CS112);
        SQLUtil.checkQueryTokenizer(qt, 1);            
    }

    public void testHasQueryAll() {
        qt = new QueryTokenizer(";", "--", removeMultilineComment);
        qt.setScriptToTokenize(SQLUtil.getGenericSQLScript());
        SQLUtil.checkQueryTokenizer(qt, SQLUtil.getGenericSQLCount()); 
    }
        
            
}
