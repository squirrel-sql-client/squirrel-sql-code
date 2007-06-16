package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.MockTableInfo;

public class DropTableDialogTester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        
        String[] data = 
            new String[] { "TableA", "TableB", "TableC", "TableD", "TableE",
                           "TableF", "TableG", "TableH", "TableI", "TableJ" };
        
        ITableInfo[] tables = new MockTableInfo[data.length];
        
        for (int i = 0; i < tables.length; i++) {
            tables[i] = new MockTableInfo(data[i], "ASchema",  "ACat" );
            ((MockTableInfo)tables[i]).setType("TABLE");
            ((MockTableInfo)tables[i]).setRemarks("");
            ((MockTableInfo)tables[i]).setChildTables(null);
        }
        
        DropTableDialog d = new DropTableDialog(tables);
        d.setVisible(true);
    }

}
