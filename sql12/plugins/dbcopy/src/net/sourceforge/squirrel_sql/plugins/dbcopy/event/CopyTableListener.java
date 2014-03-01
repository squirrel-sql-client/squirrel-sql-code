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
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

/**
 * A listener interface that should be implemented by parties interested in 
 * getting notifications regarding the current state of the table copy 
 * operation.
 */
public interface CopyTableListener {

    public void tableAnalysisStarted(AnalysisEvent e);
    
    public void analyzingTable(TableEvent e);
    
    /**
     * Indicates that a copy operation involving one or more tables has begun.
     */
    public void copyStarted(CopyEvent e);
    
    /**
     * Indicates that a table copy operation described by the specified 
     * TableEvent has begun.
     * 
     * @param e the TableEvent describing which table is being copied.
     */
    public void tableCopyStarted(TableEvent e);
    
    /**
     * Indicates that a table copy operation has completed for table described 
     * by the specified TableEvent.
     * 
     * @param e the TableEvent describing which table was copied.
     */
    public void tableCopyFinished(TableEvent e);
    
    /**
     * Indicates that a record was copied.
     * 
     * @param e the RecordEvent describing which record was copied.
     */
    public void recordCopied(RecordEvent e);
    
    /**
     * Indicates that a statement was executed.
     * 
     * @param e the StatementEvent describing which SQL statement (and possibly
     *          bind values) was executed.
     */
    public void statementExecuted(StatementEvent e);
    
    /**
     * Indicates that the copy operation involving one or more tables has 
     * completed successfully.
     * 
     * @param seconds the number of seconds it took to complete the copy 
     *                operation.
     */
    public void copyFinished(int seconds);
    
    /**
     * Indicates that the copy operation failed unrecoverably in the way that is
     * described by the specified ErrorEvent.
     * 
     * @param e
     */
    public void handleError(ErrorEvent e);
}
