/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableAdaptor;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.ErrorEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.RecordEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent;

public class MockCopyTableListener extends CopyTableAdaptor 
                                   implements CopyTableListener {

    private boolean showSqlStatements = false;
    
    public MockCopyTableListener() {
    }
    
    public void copyStarted(CopyEvent e) {
   	 // Ignored
    }

    public void tableCopyStarted(TableEvent e) {
        int total = e.getTableCount();
        int num = e.getTableNumber();
        System.out.println("Started copying table "+e.getTableName()+" ( "+
                           num+" of "+total+" )");
    }

    public void tableCopyFinished(TableEvent e) {
        int total = e.getTableCount();
        int num = e.getTableNumber();
        System.out.println("Finished copying table "+e.getTableName()+" ( "+
                           num+" of "+total+" )");
    }

    public void recordCopied(RecordEvent e) {
   	 // Ignored
    }

    public void statementExecuted(StatementEvent e) {
        if (showSqlStatements) {
            System.out.println(e.getStatement());
            String[] bindVarVals = e.getBindValues();
            if (bindVarVals != null && bindVarVals.length > 0) {
                System.out.println("bindVarVals: ");
                for (int i = 0; i < bindVarVals.length; i++) {
                    String string = bindVarVals[i];
                    System.out.println("bindVarVals["+i+"] = "+string);
                }
            }
        }
    }

    public void copyFinished(int seconds) {
        System.out.println("Copy operation finished in "+seconds+" seconds");
    }

    public void handleError(ErrorEvent e) {
        Exception ex = e.getException();
        System.err.println("ErrorEvent type = "+e.getType());
        System.err.println("Encountered unexpected exception - "+ex.getMessage());
        ex.printStackTrace();
    }

    /**
     * @param showSqlStatements The showSqlStatements to set.
     */
    public void setShowSqlStatements(boolean showSqlStatements) {
        this.showSqlStatements = showSqlStatements;
    }

    /**
     * @return Returns the showSqlStatements.
     */
    public boolean isShowSqlStatements() {
        return showSqlStatements;
    }

}
