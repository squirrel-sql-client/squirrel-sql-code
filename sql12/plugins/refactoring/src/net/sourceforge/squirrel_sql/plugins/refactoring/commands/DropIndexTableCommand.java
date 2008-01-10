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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DefaultDropDialog;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DefaultListDialog;

/**
 * Defines a command class to drop index(es).
 */
public class DropIndexTableCommand extends AbstractRefactoringCommand {
    /**
     * Logger for this class.
     */
    @SuppressWarnings("unused")
	private final ILogger s_log = LoggerController.createLogger(DropIndexTableCommand.class);
    /**
     * Internationalized strings for this class
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DropIndexTableCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("DropIndexCommand.sqlDialogTitle");
    }

    protected DefaultDropDialog customDialog;

    private IndexInfo[] _dropIndexInfo;
    private DefaultListDialog listDialog;


    public DropIndexTableCommand(ISession session, IDatabaseObjectInfo[] dbInfo) {
        super(session, dbInfo);
    }

   @Override
	protected void onExecute() throws SQLException
	{
		String tableName = null;
		if (_info[0].getDatabaseObjectType() == DatabaseObjectType.INDEX)
		{
			_dropIndexInfo = new IndexInfo[_info.length];
			for (int i = 0; i < _info.length; i++)
			{
				_dropIndexInfo[i] = (IndexInfo) _info[i];
			}
			showCustomDialog();
			return;
		} 
		else
		{
			ITableInfo ti = (ITableInfo) _info[0];
			tableName = ti.getSimpleName();
			_dropIndexInfo =
				_session.getSQLConnection().getSQLMetaData().getIndexInfo(ti).toArray(new IndexInfo[] {});
		}

		// Don't show indexes dialog if only one index exists to be modified
		if (_dropIndexInfo.length == 1)
		{
			showCustomDialog();
		} else
		{
			if (listDialog == null)
			{
				listDialog =
					new DefaultListDialog(_dropIndexInfo, tableName, DefaultListDialog.DIALOG_TYPE_INDEX);
				listDialog.addColumnSelectionListener(new ColumnListSelectionActionListener());
				listDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
			}
			listDialog.setVisible(true);
		}
	}


    protected void showCustomDialog() {
        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        customDialog = new DefaultDropDialog(_dropIndexInfo, DefaultDropDialog.DIALOG_TYPE_INDEX);
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
        ArrayList<String> result = new ArrayList<String>();

        if (_dialect.supportsDropIndex()) {
            for (IndexInfo iInfo : _dropIndexInfo) {
                DatabaseObjectQualifier qualifier = new DatabaseObjectQualifier(iInfo.getCatalogName(), iInfo.getSchemaName());
                result.add(_dialect.getDropIndexSQL(iInfo.getSimpleName(), customDialog.isCascadeSelected(),
                        qualifier, _sqlPrefs));
            }
        } else {
            _session.showMessage(s_stringMgr.getString("DropIndexCommand.unsupportedOperationMsg",
                    _dialect.getDisplayName()));
        }

        return result.toArray(new String[]{});
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
	 * @param dialectExt
	 *           the HibernateDialect to check
	 * @return true if this refactoring is supported; false otherwise.
	 */
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialectExt)
	{
		return dialectExt.supportsDropIndex();
	}

	private class ColumnListSelectionActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (listDialog == null) {
                System.err.println("dialog was null");
                return;
            }
            listDialog.setVisible(false);

            _dropIndexInfo = listDialog.getSelectedItems().toArray(new IndexInfo[]{});
            showCustomDialog();
        }
    }
}
