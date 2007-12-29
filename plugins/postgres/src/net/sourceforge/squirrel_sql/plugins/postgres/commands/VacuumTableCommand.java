package net.sourceforge.squirrel_sql.plugins.postgres.commands;
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
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.plugins.postgres.gui.VacuumTableDialog;
import net.sourceforge.squirrel_sql.plugins.postgres.gui.MessageDialog;
import net.sourceforge.squirrel_sql.plugins.postgres.commands.handler.MessageSQLExecuterHandler;

import java.util.ArrayList;

public class VacuumTableCommand extends AbstractPostgresDialogCommand {
    /** Main dialog */
    protected VacuumTableDialog _mainDialog;

    /** Selected database object(s) */
    protected final ITableInfo[] _infos;

    /** Logger for this class. */
    @SuppressWarnings("unused")
    private final static ILogger s_log = LoggerController.createLogger(VacuumTableCommand.class);

    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(VacuumTableCommand.class);


    protected interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("VacuumTableCommand.sqlDialogTitle");
        String PROGRESS_DIALOG_TITLE = s_stringMgr.getString("VacuumTableCommand.progressDialogTitle");
        String COMMAND_PREFIX = s_stringMgr.getString("VacuumTableCommand.commandPrefix");
    }


    public VacuumTableCommand(ISession session, IDatabaseObjectInfo[] infos) {
        super(session);

        ITableInfo[] tableinfos = new ITableInfo[infos.length];
        for (int i = 0; i < infos.length; i++) {
            if (infos[i] instanceof ITableInfo) {
                tableinfos[i] = (ITableInfo) infos[i];
            } else {
                //s_log.info("Not all selected objects where tables.");
                throw new IllegalArgumentException("Not all selected objects where tables.");
            }
        }
        _infos = tableinfos;
    }


    /** Execute this command. */
    public void execute() throws BaseException {
        showDialog(_infos);
    }


    protected void showDialog(ITableInfo[] tableinfos) {
        _mainDialog = new VacuumTableDialog(tableinfos);
        _mainDialog.addExecuteListener(new ExecuteListener());
        _mainDialog.addEditSQLListener(new EditSQLListener(_mainDialog));
        _mainDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, _mainDialog));
        _mainDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
        _mainDialog.setVisible(true);
    }


    /** Generates the SQL command */
    @Override
    protected String[] generateSQLStatements() {
        ArrayList<String> result = new ArrayList<String>();
        final String sep = _session.getQueryTokenizer().getSQLStatementSeparator();

        String full = (_mainDialog.getFullOption() ? "FULL " : "");
        String analyze = (_mainDialog.getAnalyzeOption() ? "ANALYZE " : "");

        for (ITableInfo info : _mainDialog.getContent()) {
            StringBuilder stmt = new StringBuilder();
            stmt.append("VACUUM ").append(full).append("VERBOSE ").append(analyze).append(info.getQualifiedName());
            if (stmt.length() > 0) {
                stmt.append(sep);
                result.add(stmt.toString());
            }
        }

        return result.toArray(new String[result.size()]);
    }


    /** Executes an sql script. */
    @Override
    protected void executeScript(String script) {
        final MessageDialog messageDialog = new MessageDialog();
        messageDialog.setTitle("SQL Execution Output");        
        messageDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());

        final VacuumTableSQLExecuterHandler handler = new VacuumTableSQLExecuterHandler(_session, messageDialog, i18n.PROGRESS_DIALOG_TITLE, i18n.COMMAND_PREFIX);
        final SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
        executer.setSchemaCheck(false);

        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        _mainDialog.setVisible(false);
                        messageDialog.setVisible(true);
                    }
                });
                executer.run();
            }
        });
    }


    private class VacuumTableSQLExecuterHandler extends MessageSQLExecuterHandler {
        public VacuumTableSQLExecuterHandler(ISession session, MessageDialog mdialog, String progressDialogTitle, String commandPrefix) {
            super(session, mdialog, progressDialogTitle, commandPrefix);
        }


        protected String getSuffix(String sql) {
            String[] parts = sql.split(" ");
            return parts[parts.length - 1];
        }
    }
}
