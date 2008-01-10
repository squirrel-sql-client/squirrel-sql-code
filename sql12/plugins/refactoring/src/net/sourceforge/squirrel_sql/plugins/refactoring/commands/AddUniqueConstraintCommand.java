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

import java.sql.SQLException;
import java.util.TreeSet;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
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
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddUniqueConstraintDialog;

public class AddUniqueConstraintCommand extends AbstractRefactoringCommand
{
	/**
	 * Logger for this class.
	 */
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(AddUniqueConstraintCommand.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddUniqueConstraintCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("AddUniqueConstraintCommand.sqlDialogTitle");
	}

	protected AddUniqueConstraintDialog customDialog;

	public AddUniqueConstraintCommand(ISession session, IDatabaseObjectInfo[] info)
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
			return;

		showCustomDialog();
	}

	protected void showCustomDialog() throws SQLException
	{
		ITableInfo selectedTable = (ITableInfo) _info[0];
		TableColumnInfo[] tableColumnInfos = _session.getMetaData().getColumnInfo(selectedTable);
		TreeSet<String> localColumns = new TreeSet<String>();
		for (TableColumnInfo columns : tableColumnInfos)
		{
			localColumns.add(columns.getColumnName());
		}

		customDialog =
			new AddUniqueConstraintDialog(selectedTable.getSimpleName(), localColumns.toArray(new String[] {}));
		customDialog.addExecuteListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setVisible(true);

	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.AbstractRefactoringCommand#generateSQLStatements()
	 */
	@Override
	protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		DatabaseObjectQualifier qualifier =
			new DatabaseObjectQualifier(_info[0].getCatalogName(), _info[0].getSchemaName());

		String result =
			_dialect.getAddUniqueConstraintSQL(_info[0].getSimpleName(),
				customDialog.getConstraintName(),
				customDialog.getUniqueColumns().toArray(new String[] {}),
				qualifier,
				_sqlPrefs);

		return new String[] { result };
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
		return dialect.supportsAddUniqueConstraint();
	}
}
