package net.sourceforge.squirrel_sql.plugins.refactoring;

import java.util.ArrayList;
import java.util.List;

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
        ArrayList<String> result = new ArrayList<String>();

        String[] addSQLs = dialect.getColumnAddSQL(info);
        
        for (int i = 0; i < addSQLs.length; i++) {
            String addSQL = addSQLs[i];
            result.add(addSQL);
        }

        return result.toArray(new String[result.size()]);
    }
    
    public static String[] getAlterSQLForColumnChange(TableColumnInfo from,
                                                      TableColumnInfo to,
                                                      HibernateDialect dialect)
    {
        ArrayList<String> result = new ArrayList<String>();
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
        List<String> typeSQL = getTypeAlterSQL(from, to, dialect);
        if (typeSQL != null) {
            result.addAll(typeSQL);
        }
        String defaultSQL = getAlterSQLForColumnDefault(from, to, dialect);
        if (defaultSQL != null) {
            result.add(defaultSQL);
        }
        return result.toArray(new String[result.size()]);
    }
    
    public static List<String> getTypeAlterSQL(TableColumnInfo from, 
                                         TableColumnInfo to,
                                         HibernateDialect dialect)
    {
        if (from.getDataType() == to.getDataType()
                && from.getColumnSize() == to.getColumnSize()) 
        {
            return null;
        }
        return dialect.getColumnTypeAlterSQL(from, to);
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
        if (!dialect.supportsColumnComment()) {
            return null;
        }
        if (oldComment == null || newComment == null) {
            return null;
        }
        if (!oldComment.equals(newComment)) {
            return dialect.getColumnCommentAlterSQL(to);
        }
        return null;        
    }
    
    public static String getAlterSQLForColumnDefault(TableColumnInfo from,
                                                     TableColumnInfo to,
                                                     HibernateDialect dialect) 
    {
        String oldDefault = from.getDefaultValue();
        String newDefault = to.getDefaultValue();
        if (!dialect.supportsAlterColumnDefault()) {
            return null;
        }
        // empty string ('') seems to be represented as null in some drivers. 
        // Not sure if this is the best thing to do here, but it fixes an issue
        // where SQL returns is set default to '', when it is already null.
        if (oldDefault == null) {
            oldDefault = "";
        }
        if (newDefault == null) {
            newDefault = "";
        }
        if (!oldDefault.equals(newDefault)) {
            return dialect.getColumnDefaultAlterSQL(to);
        }
        return null;
    }    
}
