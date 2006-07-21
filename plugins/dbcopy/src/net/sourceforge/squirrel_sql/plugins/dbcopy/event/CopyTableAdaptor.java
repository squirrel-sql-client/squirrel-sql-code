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
 * An adapter that implements CopyTableListener with No-Op implementations.
 */

public abstract class CopyTableAdaptor implements CopyTableListener {

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#analyzingTable(net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent)
     */
    public void analyzingTable(TableEvent e) {}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#tableAnalysisStarted(net.sourceforge.squirrel_sql.plugins.dbcopy.event.AnalysisEvent)
     */
    public void tableAnalysisStarted(AnalysisEvent e) {}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#copyStarted(net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent)
     */
    public void copyStarted(CopyEvent e) {}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#tableCopyStarted(net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent)
     */
    public void tableCopyStarted(TableEvent e) {}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#tableCopyFinished(net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent)
     */
    public void tableCopyFinished(TableEvent e) {}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#recordCopied(net.sourceforge.squirrel_sql.plugins.dbcopy.event.RecordEvent)
     */
    public void recordCopied(RecordEvent e) {}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#statementExecuted(net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent)
     */
    public void statementExecuted(StatementEvent e) {}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#copyFinished(int)
     */
    public void copyFinished(int seconds) {}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#handleError(net.sourceforge.squirrel_sql.plugins.dbcopy.event.ErrorEvent)
     */
    public void handleError(ErrorEvent e) {}

}
