
package net.sourceforge.squirrel_sql.fw.sql;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationManager;

public class QueryTokenizerTest extends TestCase implements GenericSQL {

    static String nullSQL = null;       
    static String tmpFilename = null;
    static boolean removeMultilineComment = true;
    static {
        ApplicationManager.initApplication();        
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

    public void testEmbeddedComments() {
        qt = new QueryTokenizer(";", "--", false);
        qt.setScriptToTokenize(ANSI_SQL_92_PROCEDURE);
        StringBuffer sql = new StringBuffer();
        while (qt.hasQuery()) {
            sql.append(qt.nextQuery());
            sql.append(";");
        }
/*        System.out.println("length="+sql.length());
        System.out.println("sql="+sql);
        System.out.println("comment at "+sql.indexOf("/* remove"));
        System.out.println("comment at "+sql.indexOf("/* add"));
*/
        
        int firstCommentIndex = sql.indexOf("/* remove");
        assertTrue("first comment not found", firstCommentIndex != -1);
        int secondCommentIndex =  sql.indexOf("/* add");
        assertTrue("second comment not found", secondCommentIndex != -1);
        
        
        
    }
    
    public void testHasQueryAll() {
        qt = new QueryTokenizer(";", "--", removeMultilineComment);
        qt.setScriptToTokenize(SQLUtil.getGenericSQLScript());
        SQLUtil.checkQueryTokenizer(qt, SQLUtil.getGenericSQLCount()); 
    }
        
            
}
