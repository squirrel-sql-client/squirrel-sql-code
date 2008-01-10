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

import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DefaultDropDialog;

public class DropSequenceCommand extends AbstractRefactoringCommand {
    /**
     * Logger for this class.
     */
    @SuppressWarnings("unused")
	private final ILogger s_log = LoggerController.createLogger(DropSequenceCommand.class);
    /**
     * Internationalized strings for this class
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DropSequenceCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("DropSequenceCommand.sqlDialogTitle");
    }

    protected DefaultDropDialog customDialog;


    public DropSequenceCommand(ISession session, IDatabaseObjectInfo[] dbInfo) {
        super(session, dbInfo);
    }


    @Override
    protected void onExecute() {
        showCustomDialog();
    }


    protected void showCustomDialog() {
        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        customDialog = new DefaultDropDialog(_info, DefaultDropDialog.DIALOG_TYPE_SEQUENCE);
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
    protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		ArrayList<String> result = new ArrayList<String>();

		if (_dialect.supportsDropSequence())
		{
			for (IDatabaseObjectInfo dbo : _info)
			{
				DatabaseObjectQualifier qualifier =
					new DatabaseObjectQualifier(dbo.getCatalogName(), dbo.getSchemaName());
				result.add(_dialect.getDropSequenceSQL(dbo.getSimpleName(),
					customDialog.isCascadeSelected(),
					qualifier,
					_sqlPrefs));
			}
		} else
		{
			_session.showMessage(s_stringMgr.getString("DropSequenceCommand.unsupportedOperationMsg",
				_dialect.getDisplayName()));
		}

		return result.toArray(new String[] {});
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
	 * @param dialectExt the HibernateDialect to check
	 * @return true if this refactoring is supported; false otherwise.
	 */
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialectExt)
	{
		return dialectExt.supportsDropSequence();
	}
}
