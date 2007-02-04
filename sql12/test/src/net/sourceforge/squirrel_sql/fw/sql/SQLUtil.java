package net.sourceforge.squirrel_sql.fw.sql;

import junit.framework.Assert;

public class SQLUtil implements GenericSQL {
    
    private static int genericSQLCount = 0;
    
    public static String getGenericSQLScript() {
        StringBuffer result = new StringBuffer();
        result.append(CREATE_STUDENT);
        result.append("\n\n");
        result.append(CREATE_COURSES);
        result.append("\n\n");
        result.append(CREATE_PROFESSOR);
        result.append("\n\n");
        result.append(CREATE_TAKE);
        result.append("\n\n");
        result.append(CREATE_TEACH);
        result.append("\n\n");
        result.append(STUDENTS_NOT_TAKING_CS112);
        result.append("\n\n");
        // Don't forget to set this to the number of statements in result
        genericSQLCount = 6;
        return result.toString();
    }

    public static void checkQueryTokenizer(IQueryTokenizer qt, 
                                           int stmtCount) 
    {
        int count = 0;
        while (qt.hasQuery()) {
            count++;
            System.out.println(" query: "+qt.nextQuery());
        }
        Assert.assertEquals(stmtCount, count);                
    }    
    
    /**
     * @param genericSQLCount the genericSQLCount to set
     */
    public static void setGenericSQLCount(int genericSQLCount) {
        SQLUtil.genericSQLCount = genericSQLCount;
    }

    /**
     * @return the genericSQLCount
     */
    public static int getGenericSQLCount() {
        return genericSQLCount;
    }
    
    
}
