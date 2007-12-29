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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.RenameTableDialog;
import net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.IHibernateDialectExtension;


public class RenameTableCommand extends AbstractRefactoringCommand {
    /**
     * Logger for this class.
     */
    private final ILogger s_log = LoggerController.createLogger(RenameTableCommand.class);

    /**
     * Internationalized strings for this class
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RenameTableCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("RenameTableCommand.sqlDialogTitle");
    }

    protected RenameTableDialog customDialog;


    public RenameTableCommand(ISession session, IDatabaseObjectInfo[] dbInfo) {
        super(session, dbInfo);
    }


    @Override
    protected void onExecute() {
        showCustomDialog();
    }


    private void showCustomDialog() {
        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        customDialog = new RenameTableDialog(_info, RenameTableDialog.DIALOG_TYPE_TABLE);
                        customDialog.addExecuteListener(new ExecuteListener());
                        customDialog.addEditSQLListener(new EditSQLListener(customDialog));
                        customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
                        customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
                        customDialog.setVisible(true);
                    }
                });
            }
        });
    }


    @Override
    protected String[] generateSQLStatements() throws UserCancelledOperationException {
        String result = null;
        try {
            DatabaseObjectQualifier qualifier = new DatabaseObjectQualifier(_info[0].getCatalogName(),
                    _info[0].getSchemaName());

            result = _dialect.getRenameTableSQL(_info[0].getSimpleName(), customDialog.getNewSimpleName(),
                    qualifier, _sqlPrefs);
        } catch (UnsupportedOperationException e2) {
            _session.showMessage(s_stringMgr.getString("RenameTableCommand.unsupportedOperationMsg",
                    _dialect.getDisplayName()));
        }
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
                        _session.getSchemaInfo().reloadAll();
                    }
                });
            }
        });
    }


   /**
	 * Returns a boolean value indicating whether or not this refactoring is supported for the specified 
	 * dialect. 
	 * 
	 * @param dialectExt the IHibernateDialectExtension to check
	 * @return true if this refactoring is supported; false otherwise.
	 */
	@Override
	protected boolean isRefactoringSupportedForDialect(IHibernateDialectExtension dialectExt)
	{
		return dialectExt.supportsRenameTable();
	}
}
