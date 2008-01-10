package net.sourceforge.squirrel_sql.plugins.refactoring.commands;

/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
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

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DropColumnCommand extends AbstractRefactoringCommand
{
	/**
	 * Logger for this class.
	 */
	private final static ILogger s_log = LoggerController.createLogger(DropColumnCommand.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DropColumnCommand.class);

	static interface i18n
	{
		// i18n[RemoveColumnCommand.singleColumnMessage=The table's only column cannot be removed - a table
		// must have a least one column.]
		String SINGLE_COLUMN_MESSAGE = s_stringMgr.getString("RemoveColumnAction.singleColumnMessage");

		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("RemoveColumnCommand.sqlDialogTitle");
	}

	/**
	 * Dialog that is displayed to user when this command is executed and there is more than one column in the
	 * table that was selected.
	 */
	protected ColumnListDialog customDialog;

	/**
	 * Creates an instance of ModifyColumnCommand.
	 * 
	 * @param session the current ISession
	 * @param info the list of IDatabaseObjectInfo(s) that the user selected
	 */
	public DropColumnCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.AbstractRefactoringCommand#onExecute()
	 */
	@Override
	protected void onExecute() throws SQLException
	{
		if (!(_info[0] instanceof ITableInfo))
		{
			s_log.error("onExecute: _info[0] isn't an instance of ITableInfo: class="
				+ _info[0].getClass().getName());

			return;
		}

		ITableInfo ti = (ITableInfo) _info[0];
		TableColumnInfo[] columns = _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);

		if (columns.length < 2)
		{
			// If the table has only one column, it cannot be dropped
			_session.showErrorMessage(i18n.SINGLE_COLUMN_MESSAGE);
		} else
		{
			showCustomDialog();
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.AbstractRefactoringCommand#generateSQLStatements()
	 */
	@Override
	protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		TableColumnInfo[] columns = customDialog.getSelectedColumnList();

		String[] result = new String[columns.length];
		try
		{
			// TODO: add configuration for whether or not to qualify names. (SqlGenerationPreferences &
			// DatabaseObjectQualifier can be used, see DialectExtension)
			String tableName = _info[0].getQualifiedName();
			for (int i = 0; i < columns.length; i++)
			{
				TableColumnInfo info = columns[i];
				String columnName = info.getColumnName();
				result[i] = _dialect.getColumnDropSQL(tableName, columnName);
			}
		} catch (UnsupportedOperationException e2)
		{
			_session.showMessage(s_stringMgr.getString("RemoveColumnCommand.unsupportedOperationMsg",
				_dialect.getDisplayName()));
		}
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.AbstractRefactoringCommand#executeScript(java.lang.String)
	 */
	@Override
	protected void executeScript(String script)
	{
		CommandExecHandler handler = new CommandExecHandler(_session);

		SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
		executer.run(); // Execute the sql synchronously

		_session.getApplication().getThreadPool().addTask(new Runnable()
		{
			public void run()
			{
				GUIUtils.processOnSwingEventThread(new Runnable()
				{
					public void run()
					{
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
	 * @param dialect
	 *           the HibernateDialect to check
	 * @return true if this refactoring is supported; false otherwise.
	 */
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialect)
	{
		return dialect.supportsDropColumn();
	}
	
	private void showCustomDialog() throws SQLException
	{
		ITableInfo ti = (ITableInfo) _info[0];
		TableColumnInfo[] columns = _session.getMetaData().getColumnInfo(ti);

		// Show the user a dialog with a list of columns and ask them to select
		customDialog = new ColumnListDialog(columns, ColumnListDialog.DROP_COLUMN_MODE);
		customDialog.setMultiSelection();
		customDialog.setTableName(ti.getQualifiedName());

		customDialog.addColumnSelectionListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setVisible(true);
	}
	
}