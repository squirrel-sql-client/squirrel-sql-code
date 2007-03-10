package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SQLUtilities {

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(SQLUtilities.class);
    
    
    /**
     * Reverses the insertion order list.  Just a convenience method.
     * 
     * @param md
     * @param tables
     * @return
     * @throws SQLException
     */
    public static List<ITableInfo> getDeletionOrder(List<ITableInfo> tables,
                                                    SQLDatabaseMetaData md,
                                                    ProgressCallBack callback)
        throws SQLException
    {
        List<ITableInfo> insertionOrder = 
            getInsertionOrder(tables, md, callback);
        Collections.reverse(insertionOrder);
        return insertionOrder;
    }
        
    /**
     * Returns the specified list of tables in an order such that insertions into
     * all tables will satisfy any foreign key constraints. This will not 
     * correctly handle recursive constraints. 
     * 
     * This algorthim was adapted from SchemaSpy class/method:
     * 
     *  net.sourceforge.schemaspy.SchemaSpy.sortTablesByRI()
     * 
     * @param md
     * @param tables
     * @param listener
     * @return
     * @throws SQLException
     */
    public static List<ITableInfo> getInsertionOrder(List<ITableInfo> tables, 
                                                     SQLDatabaseMetaData md,
                                                     ProgressCallBack callback) 
        throws SQLException                                                     
    {
        List<ITableInfo> result = new ArrayList<ITableInfo>();
        // tables that are netiher children nor parents - utility tables
        List<ITableInfo> unattached = new ArrayList<ITableInfo>();
        // tables that have at least one parent table
        List<ITableInfo> children = new ArrayList<ITableInfo>();
        // tables that have at least one child table
        List<ITableInfo> parents = new ArrayList<ITableInfo>();
        // tables that have at least one child table and have a least one parent table
        List<ITableInfo> sandwiches = new ArrayList<ITableInfo>();
        
        
        for (ITableInfo table : tables) {
            callback.currentlyLoading(table.getSimpleName());
            ForeignKeyInfo[] importedKeys = getImportedKeys(table, md);
            ForeignKeyInfo[] exportedKeys = getExportedKeys(table, md);
            
            if (importedKeys != null && importedKeys.length == 0 && exportedKeys.length == 0)  {
                unattached.add(table);
                continue;
            }
            if (exportedKeys != null && exportedKeys.length > 0) {
                if (importedKeys != null && importedKeys.length > 0) {
                    sandwiches.add(table);
                } else {
                    parents.add(table);
                }
                continue;
            }
            if (importedKeys != null && importedKeys.length > 0) {
                children.add(table);
            }
        }
        reorderTables(sandwiches);
        
        for (ITableInfo info : unattached) {
            result.add(info);
        }
        for (ITableInfo info : parents) {
            result.add(info);
        }
        for (ITableInfo info : sandwiches) {
            result.add(info);
        }
        for (ITableInfo info : children) {
            result.add(info);
        }
        return result;
    }
    
    public static ForeignKeyInfo[] getImportedKeys(ITableInfo ti,
                                                    SQLDatabaseMetaData md) {
        ForeignKeyInfo[] result = ti.getImportedKeys();
        if (result == null) {
            try {
                result = md.getImportedKeysInfo(ti);
                // Avoid the hit next time
                ti.setImportedKeys(result);
            } catch (SQLException e) {
                String tablename = ti.getSimpleName();
                s_log.error(
                    "Unexpected exception while getting imported keys for " +
                    "table "+tablename);
            }
        }
        return result;
    }

    public static ForeignKeyInfo[] getExportedKeys(ITableInfo ti,
                                                   SQLDatabaseMetaData md) {
        ForeignKeyInfo[] result = ti.getExportedKeys();
        if (result == null) {
            try {
                result = md.getExportedKeysInfo(ti);
                // Avoid the hit next time
                ti.setExportedKeys(result);
            } catch (SQLException e) {
                String tablename = ti.getSimpleName();
                s_log.error(
                        "Unexpected exception while getting exported keys for " +
                        "table "+tablename);
            }
        }
        return result;
    }
    
    private static void reorderTables(List<ITableInfo> sandwiches) 
    {
        Collections.sort(sandwiches, new TableComparator());
    }
    
    private static class TableComparator implements Comparator<ITableInfo> {
        
              
        public int compare(ITableInfo t1, ITableInfo t2) {
            ForeignKeyInfo[] t1ImportedKeys = t1.getImportedKeys();
            for (int i = 0; i < t1ImportedKeys.length; i++) {
                ForeignKeyInfo info = t1ImportedKeys[i];
                if (info.getPrimaryKeyTableName().equals(t2.getSimpleName())) {
                    // t1 depends on t2
                    return 1;
                }
            }
            ForeignKeyInfo[] t2ImportedKeys = t2.getImportedKeys();
            for (int i = 0; i < t2ImportedKeys.length; i++) {
                ForeignKeyInfo info = t2ImportedKeys[i];
                if (info.getPrimaryKeyTableName().equals(t1.getSimpleName())) {
                    // t2 depends on t1
                    return -1;
                }
            }
            if (t1.getImportedKeys().length > t2ImportedKeys.length) {
                return 1;
            }
            if (t1.getImportedKeys().length < t2ImportedKeys.length) {
                return -1;
            }
            return 0;
        }
        
    }
    
    /**
     * Returns a list of table names that have Primary Keys that are referenced by 
     * foreign key constraints on columns in the specified list of tables, that 
     * are not also contained in the specified list
     * 
     * @param md 
     * @param tables
     * @return 
     * @throws SQLException
     */
    public static List<String> getExtFKParents(SQLDatabaseMetaData md, 
                                               List<ITableInfo> tables) 
        throws SQLException
    {
        List<String> result = new ArrayList<String>();
        HashSet<String> tableNames = new HashSet<String>();
         
        for (ITableInfo table : tables) {
            tableNames.add(table.getSimpleName());            
        }

        for (ITableInfo table : tables) {
            ForeignKeyInfo[] importedKeys = md.getImportedKeysInfo(table);
            for (int i = 0; i < importedKeys.length; i++) {
                ForeignKeyInfo info = importedKeys[i];
                String pkTable = info.getPrimaryKeyTableName();
                if (!tableNames.contains(pkTable)) {
                    result.add(pkTable);
                }
            }
        }
        return result;
    }

    /**
     * Returns a list of table names that have Foreign keys that reference 
     * Primary Keys in the specified List of tables, but that are not also 
     * contained in the list of tables.
     *  
     * @param md
     * @param tables
     * @return
     * @throws SQLException
     */
    public static List<String> getExtFKChildren(SQLDatabaseMetaData md, 
                                                List<ITableInfo> tables) 
        throws SQLException
    {
        List<String> result = new ArrayList<String>();
        HashSet<String> tableNames = new HashSet<String>();

        for (ITableInfo table : tables) {
            tableNames.add(table.getSimpleName());            
        }

        for (ITableInfo table : tables) {
            ForeignKeyInfo[] exportedKeys = md.getExportedKeysInfo(table);
            for (int i = 0; i < exportedKeys.length; i++) {
                ForeignKeyInfo info = exportedKeys[i];
                String fkTable = info.getForeignKeyTableName();
                if (!tableNames.contains(fkTable)) {
                    result.add(fkTable);
                }
            }
        }
        return result;
    }
    /*
    private static class TableDependInfo extends TableInfo {
        
        ForeignKeyInfo[] exportedKeys = null;
        ForeignKeyInfo[] importedKeys = null;
        
        
        public TableDependInfo(ITableInfo info, 
                               SQLDatabaseMetaData md,
                               ForeignKeyInfo[] expKeys,
                               ForeignKeyInfo[] impKeys
                               ) 
        throws SQLException 
        {
            super(info.getCatalogName(), 
                 info.getSchemaName(), 
                 info.getSimpleName(),
                 info.getType(),
                 info.getRemarks(),
                 md);
            exportedKeys = expKeys;
            importedKeys = impKeys;
        }
        
        public TableDependInfo(ITableInfo info, SQLDatabaseMetaData md) 
            throws SQLException 
        {
            this(info.getCatalogName(), 
                 info.getSchemaName(), 
                 info.getSimpleName(),
                 info.getType(),
                 info.getRemarks(),
                 md); 
        }
        
        public TableDependInfo(String catalog, String schema, String simpleName,
                 String tableType, String remarks,
                 SQLDatabaseMetaData md) throws SQLException {
            super(catalog, schema, simpleName, tableType, remarks, md);
            exportedKeys = md.getExportedKeysInfo(this);
            importedKeys = md.getImportedKeysInfo(this);
        }
        
        
        
        
    } */

}
