package net.sourceforge.squirrel_sql.plugins.dbdiff;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public class TableDiffExecutor {
    
    private ISQLDatabaseMetaData _md1;
    private ISQLDatabaseMetaData _md2;
    
    private ITableInfo _t1;
    private ITableInfo _t2;
    
    private List<ColumnDifference> colDifferences = null;
    
    public TableDiffExecutor(ISQLDatabaseMetaData md1, 
                             ISQLDatabaseMetaData md2) 
    {
        _md1 = md1;
        _md2 = md2;
    }
    
    public void setTableInfos(ITableInfo t1, ITableInfo t2) {
        _t1 = t1;
        _t2 = t2;
        if (colDifferences != null) {
            colDifferences.clear();
        }
    }
    
    public void execute() throws SQLException {
        colDifferences = new ArrayList<ColumnDifference>();
        TableColumnInfo[] t1cols = _md1.getColumnInfo(_t1);
        TableColumnInfo[] t2cols = _md2.getColumnInfo(_t2);
        Set<String> columnNames = getAllColumnNames(t1cols, t2cols);
        Set<String> t1ColumnNames = getAllColumnNames(t1cols);
        Map<String, TableColumnInfo> t1ColMap = getColumnMap(t1cols);
        Set<String> t2ColumnNames = getAllColumnNames(t2cols);
        Map<String, TableColumnInfo> t2ColMap = getColumnMap(t2cols);
        
        for (String columnName : columnNames) {
            ColumnDifference diff = new ColumnDifference();
            if (t1ColumnNames.contains(columnName)) {
                TableColumnInfo c1 = t1ColMap.get(columnName);
                
                if (t2ColumnNames.contains(columnName)) { // Column is in both table 1 and 2
                    TableColumnInfo c2 = t2ColMap.get(columnName);
                    diff.setColumns(c1, c2);
                } else {
                    // Column is in table 1, but not table 2
                    diff.setCol2Exists(false);
                    diff.setColumn1(c1);
                }
            } else {
                // Column is in table 2, but not table 1 - how else would we get
                // here??
                diff.setCol1Exists(false);
                diff.setColumn2(t2ColMap.get(columnName));
            }
            if (diff.execute()) {
                colDifferences.add(diff);
            }
        }
    }
    
    public List<ColumnDifference> getColumnDifferences() {
        return colDifferences;
    }
    
    private Map<String, TableColumnInfo> getColumnMap(TableColumnInfo[] tci) {
        HashMap<String, TableColumnInfo> result = 
            new HashMap<String, TableColumnInfo>();
        for (int i = 0; i < tci.length; i++) {
            TableColumnInfo info = tci[i];
            result.put(info.getColumnName(), info);
        }        
        return result;
    }
    
    /**
     * Build a list of all table column names.
     * 
     * @param tci1
     * @param tci2
     * @return
     */
    private Set<String> getAllColumnNames(TableColumnInfo[] tci1, 
                                          TableColumnInfo[] tci2) 
    {
        HashSet<String> result = new HashSet<String>();
        result.addAll(getAllColumnNames(tci1));
        result.addAll(getAllColumnNames(tci2));
        return result;
    }
    
    private Set<String> getAllColumnNames(TableColumnInfo[] tci) {
        HashSet<String> result = new HashSet<String>();
        for (int i = 0; i < tci.length; i++) {
            TableColumnInfo info = tci[i];
            result.add(info.getColumnName());
        }
        return result;
    }
    
    private static class ColumnComparator implements Comparator<TableColumnInfo> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(TableColumnInfo arg0, TableColumnInfo arg1) {
            return arg0.getColumnName().compareToIgnoreCase(arg1.getColumnName());
        }
        
    }
}
