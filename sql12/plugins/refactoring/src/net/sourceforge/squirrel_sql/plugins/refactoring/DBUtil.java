package net.sourceforge.squirrel_sql.plugins.refactoring;

import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;

public class DBUtil {

    /**
     * 
     * @param tableName
     * @param info
     * @param dialect
     * @return
     * @throws UnsupportedOperationException if the specified dialect doesn't
     *         support 
     * @throws HibernateException if the type in the specified info isn't 
     *         supported by this dialect.  
     */
    public static String[] getAlterSQLForColumnAddition(String tableName,
                                                        TableColumnInfo info,
                                                        HibernateDialect dialect)
        throws HibernateException, UnsupportedOperationException 
    {
        ArrayList result = new ArrayList();

        String[] addSQLs = dialect.getColumnAddSQL(tableName, info);
        
        for (int i = 0; i < addSQLs.length; i++) {
            String addSQL = addSQLs[i];
            result.add(addSQL);
        }

        if (dialect.supportsColumnComment()) {
            result.add(dialect.getColumnCommentAlterSQL(tableName, 
                                                        info.getColumnName(), 
                                                        info.getRemarks()));
        }
        return (String[])result.toArray(new String[result.size()]);
    }
    
    public static String getAlterSQLForColumnChange(String tableName,
                                                    TableColumnInfo info,
                                                    HibernateDialect dialect)
    {
        StringBuffer result = new StringBuffer();
        return result.toString();
    }
    
    public static String getAlterSQLForColumnRemoval(String tableName,
                                                     TableColumnInfo info,
                                                     HibernateDialect dialect) 
    {
        // The syntax of the alter command may be db-specific.  May need to add
        // it to the HibernatDialect interface.         
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(tableName);
        result.append(" DROP ");
        result.append(info.getSimpleName());
        return result.toString();
    }
}
