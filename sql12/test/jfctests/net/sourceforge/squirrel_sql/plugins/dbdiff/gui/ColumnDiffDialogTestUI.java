/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.plugins.dbdiff.ColumnDifference;
import net.sourceforge.squirrel_sql.test.TestUtil;

public class ColumnDiffDialogTestUI {

    public static void main(String[] args) throws Exception {
        ApplicationArguments.initialize(new String[] {});
        
        ISQLDatabaseMetaData md = TestUtil.getEasyMockSQLMetaData("oracle", "jdbc:oracle");
        ColumnDifference diff = new ColumnDifference();
        TableColumnInfo column1 = TestUtil.getBigintColumnInfo(md, true);
        TableColumnInfo column2 = TestUtil.getVarcharColumnInfo(md, true, 100);
        diff.setColumns(column1, column2);
        diff.execute();

        ColumnDifference diff2 = new ColumnDifference();
        TableColumnInfo column3 = TestUtil.getVarcharColumnInfo(md, true, 200);
        TableColumnInfo column4 = TestUtil.getVarcharColumnInfo(md, true, 100);
        diff2.setColumns(column3, column4);
        diff2.execute();
        
        final ArrayList<ColumnDifference> diffs = new ArrayList<ColumnDifference>();
        diffs.add(diff);
        diffs.add(diff2);
        ApplicationArguments.initialize(new String[] {});
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame();
                ColumnDiffDialog cdd = 
                    new ColumnDiffDialog(f, true);
                cdd.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent e) {
                        System.exit(0);
                    }
                });
                cdd.setSession1Label("Oracle1");
                cdd.setSession2Label("Oracle2");
                cdd.setColumnDifferences(diffs);
                cdd.setVisible(true);
            }
        });        
    }
    
}
