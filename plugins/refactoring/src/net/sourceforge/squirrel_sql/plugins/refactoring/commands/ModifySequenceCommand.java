package net.sourceforge.squirrel_sql.plugins.refactoring.commands;
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddModifySequenceDialog;

public class ModifySequenceCommand extends AbstractRefactoringCommand {
    /**
     * Logger for this class.
     */
    @SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(ModifySequenceCommand.class);

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ModifySequenceCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("ModifySequenceCommand.sqlDialogTitle");
        String SQL_DIALOG_TITLE = s_stringMgr.getString("ModifySequenceCommand.sqlDialogTitle");
        String SQL_ERROR_SEQUENCE_DATA = s_stringMgr.getString("ModifySequenceCommand.sqlErrorNoSequenceData");
    }

    protected AddModifySequenceDialog customDialog;


    public ModifySequenceCommand(ISession session, IDatabaseObjectInfo[] info) {
        super(session, info);
    }


    @Override
    protected void onExecute() throws SQLException {
        showCustomDialog();
    }


    private void showCustomDialog() throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = _session.getSQLConnection().createStatement();
            String simpleName = _info[0].getSimpleName();
            DatabaseObjectQualifier qualifier = new DatabaseObjectQualifier(_info[0].getCatalogName(),
                    _info[0].getSchemaName());
            rs = stmt.executeQuery(_dialect.getSequenceInformationSQL(simpleName, qualifier, _sqlPrefs));
            if (rs.next()) {
                String max_value = rs.getString("max_value");
                String min_value = rs.getString("min_value");
                String cache_value = rs.getString("cache_value");
                String increment_by = rs.getString("increment_by");
                boolean is_cyled = rs.getBoolean("is_cycled");

                customDialog = new AddModifySequenceDialog(AddModifySequenceDialog.MODIFY_MODE, simpleName,
                        increment_by, min_value, max_value, cache_value, is_cyled);
                customDialog.addExecuteListener(new ExecuteListener());
                customDialog.addEditSQLListener(new EditSQLListener(customDialog));
                customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
                customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
                customDialog.setVisible(true);
            }
        } finally {
           SQLUtilities.closeResultSet(rs);
           SQLUtilities.closeStatement(stmt);
        }
    }


    @Override
    protected String[] generateSQLStatements() throws UserCancelledOperationException {
        DatabaseObjectQualifier qualifier = new DatabaseObjectQualifier(_info[0].getCatalogName(),
                _info[0].getSchemaName());

        String result = _dialect.getAlterSequenceSQL(customDialog.getSequenceName(), customDialog.getIncrement(),
                customDialog.getMinimum(), customDialog.getMaximum(), customDialog.getStart(), customDialog.getCache(),
                customDialog.isCycled(), qualifier, _sqlPrefs);

        return new String[]{result};
    }


    @Override
    protected void executeScript(String script) {
        CommandExecHandler handler = new CommandExecHandler(_session);

        SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
        executer.run(); // Execute the sql synchronously

        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        customDialog.setVisible(false);
                        _session.getSchemaInfo().reload(_info[0]);
                    }
                });
            }
        });
    }


	/**
	 * Returns a boolean value indicating whether or not this refactoring is supported for the specified 
	 * dialect. 
	 * 
	 * @param dialectExt the HibernateDialect to check
	 * @return true if this refactoring is supported; false otherwise.
	 */
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialectExt)
	{
		return dialectExt.supportsAlterSequence();
	}
}
