package net.sourceforge.squirrel_sql.plugins.refactoring;

import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;

public class DBUtil {

    /**
     * 
     * @param info
     * @param dialect
     * @return
     * @throws UnsupportedOperationException if the specified dialect doesn't
     *         support 
     * @throws HibernateException if the type in the specified info isn't 
     *         supported by this dialect.  
     */
    public static String[] getAlterSQLForColumnAddition(TableColumnInfo info,
                                                        HibernateDialect dialect)
        throws HibernateException, UnsupportedOperationException 
    {
        ArrayList result = new ArrayList();

        String[] addSQLs = dialect.getColumnAddSQL(info);
        
        for (int i = 0; i < addSQLs.length; i++) {
            String addSQL = addSQLs[i];
            result.add(addSQL);
        }

        return (String[])result.toArray(new String[result.size()]);
    }
    
    public static String[] getAlterSQLForColumnChange(TableColumnInfo from,
                                                      TableColumnInfo to,
                                                      HibernateDialect dialect)
    {
        ArrayList result = new ArrayList();
        // It is important to process the name change first - so that we can use
        // the new name instead of the old in subsequent alterations 
        String nameSQL = getColumnNameAlterSQL(from, to, dialect);
        if (nameSQL != null)  {
            result.add(nameSQL);
        }        
        String nullSQL = getNullAlterSQL(from, to, dialect);
        if (nullSQL != null) {
            result.add(nullSQL);
        }
        String commentSQL = getCommentAlterSQL(from, to, dialect);
        if (commentSQL != null) {
            result.add(commentSQL);
        }
        return (String[])result.toArray(new String[result.size()]);
    }
    
    public static String getColumnNameAlterSQL(TableColumnInfo from, 
                                               TableColumnInfo to,
                                               HibernateDialect dialect)
    {
        if (from.getColumnName().equals(to.getColumnName())) {
            return null;
        }
        return dialect.getColumnNameAlterSQL(from, to);
    }
    
    public static String getNullAlterSQL(TableColumnInfo from, 
                                         TableColumnInfo to,
                                         HibernateDialect dialect) 
    {
        if (from.isNullable().equalsIgnoreCase(to.isNullable())) {
            return null;
        }
        return dialect.getColumnNullableAlterSQL(to);
    }
    
    public static String getCommentAlterSQL(TableColumnInfo from, 
                                            TableColumnInfo to,
                                            HibernateDialect dialect)
    {
        String oldComment = from.getRemarks();
        String newComment = to.getRemarks();
        if ((oldComment == null && newComment != null)
                || (oldComment != null && newComment == null) 
                || (!oldComment.equals(newComment))) 
        {
            return dialect.getColumnCommentAlterSQL(to);
        }
        return null;        
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
