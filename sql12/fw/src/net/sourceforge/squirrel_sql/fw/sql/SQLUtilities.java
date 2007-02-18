package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class SQLUtilities {

    /**
     * Reverses the insertion order list.  Just a convenience method.
     * 
     * @param md
     * @param tables
     * @return
     * @throws SQLException
     */
    public static List<ITableInfo> getDeletionOrder(SQLDatabaseMetaData md, 
                                                    List<ITableInfo> tables)
        throws SQLException
    {
        List<ITableInfo> insertionOrder = getInsertionOrder(md, tables);
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
     * @return
     * @throws SQLException
     */
    public static List<ITableInfo> getInsertionOrder(SQLDatabaseMetaData md, 
                                                     List<ITableInfo> tables) 
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
        List<TableDependInfo> sandwiches = new ArrayList<TableDependInfo>();
        
        
        for (ITableInfo table : tables) {
            ForeignKeyInfo[] importedKeys = md.getImportedKeysInfo(table);
            ForeignKeyInfo[] exportedKeys = md.getExportedKeysInfo(table);
            
            if (importedKeys.length == 0 && exportedKeys.length == 0)  {
                unattached.add(table);
                continue;
            }
            if (exportedKeys.length > 0) {
                if (importedKeys.length > 0) {
                    sandwiches.add(new TableDependInfo(table, 
                                                       md, 
                                                       exportedKeys, 
                                                       importedKeys));
                } else {
                    parents.add(table);
                }
                continue;
            }
            if (importedKeys.length > 0) {
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
    
    private static void reorderTables(List<TableDependInfo> sandwiches) 
    {
        Collections.sort(sandwiches, new TableComparator());
    }
    
    private static class TableComparator implements Comparator<TableDependInfo> {
        
              
        public int compare(TableDependInfo t1, TableDependInfo t2) {
            ForeignKeyInfo[] t1ImportedKeys = t1.importedKeys;
            for (int i = 0; i < t1ImportedKeys.length; i++) {
                ForeignKeyInfo info = t1ImportedKeys[i];
                if (info.getPrimaryKeyTableName().equals(t2.getSimpleName())) {
                    // t1 depends on t2
                    return 1;
                }
            }
            ForeignKeyInfo[] t2ImportedKeys = t2.importedKeys;
            for (int i = 0; i < t2ImportedKeys.length; i++) {
                ForeignKeyInfo info = t2ImportedKeys[i];
                if (info.getPrimaryKeyTableName().equals(t1.getSimpleName())) {
                    // t2 depends on t1
                    return -1;
                }
            }
            if (t1.importedKeys.length > t2ImportedKeys.length) {
                return 1;
            }
            if (t1.importedKeys.length < t2ImportedKeys.length) {
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
        
        
        
        
    }
}
