package net.sourceforge.squirrel_sql.plugins.postgres.commands.handler;
/*
 * Copyright (C) 2007 Daniel Regli & Yannick Winiger
 * http://sourceforge.net/projects/squirrel-sql
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.postgres.gui.MessageDialog;

import java.sql.SQLWarning;
import java.sql.ResultSet;

public abstract class MessageSQLExecuterHandler extends ProgressSQLExecuterHandler {
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MessageSQLExecuterHandler.class);

    protected MessageDialog _mdialog;
    protected long _startTime;

    protected boolean _exceptionOccured = false;


    public MessageSQLExecuterHandler(ISession session, MessageDialog mdialog, String progressDialogTitle, String commandPrefix) {
        super(session, mdialog, progressDialogTitle, commandPrefix);
        _mdialog = mdialog;
    }


    @Override
    public void sqlToBeExecuted(String sql) {
        super.sqlToBeExecuted(sql);
        _mdialog.writeLine("========= " + _commandPrefix + " " + getSuffix(sql) + " =========");
    }


    protected abstract String getSuffix(String sql);


    @Override
    public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount) {
        super.sqlExecutionComplete(info, processedStatementCount, statementCount);
        _mdialog.writeEmptyLine();
    }


    @Override
    public void sqlExecutionWarning(SQLWarning warn) {
        //super.sqlExecutionWarning(warn);
        _mdialog.writeLine(warn.toString());
    }


    @Override
    public void sqlStatementCount(int statementCount) {
        super.sqlStatementCount(statementCount);
        _startTime = System.currentTimeMillis();
    }


    @Override
    public void sqlCloseExecutionHandler() {
        super.sqlCloseExecutionHandler();
        if (!_exceptionOccured) {
            float executionTime = (float) (System.currentTimeMillis() - _startTime) / 1000;
            _mdialog.writeEmptyLine();
            _mdialog.writeLine(s_stringMgr.getString("MessageSQLExecuterHandler.done", _commandPrefix, executionTime));
        }
        _mdialog.enableCloseButton();
    }


    public void sqlExecutionCancelled() {
        super.sqlExecutionCancelled();
        //TODO: ? Dialog Handling on Cancelled ?
    }


    public void sqlDataUpdated(int updateCount) {
        super.sqlDataUpdated(updateCount);
    }


    public void sqlResultSetAvailable(ResultSet rst, SQLExecutionInfo info, IDataSetUpdateableTableModel model) {
        super.sqlResultSetAvailable(rst, info, model);
    }


    public void sqlExecutionException(Throwable th, String postErrorString) {
        super.sqlExecutionException(th, postErrorString);
        _mdialog.writeEmptyLine();
        _mdialog.writeLine(s_stringMgr.getString("MessageSQLExecuterHandler.aborted", _commandPrefix));
        _exceptionOccured = true;
    }
}
