package net.sourceforge.squirrel_sql.plugins.refactoring.commands;

/*
 * Copyright (C) 2007 Rob Manning
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
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Implements showing a list of primay key columns for a selected table to the user, allowing the user to drop
 * the primary key or view the SQL that will do this.
 * 
 * @author manningr
 */
public class DropPrimaryKeyCommand extends AbstractRefactoringCommand
{
	/**
	 * Logger for this class.
	 */
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(DropColumnCommand.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DropPrimaryKeyCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("DropPrimaryKeyCommand.sqlDialogTitle");
	}

	/** the dialog used by this refactoring */
	protected ColumnListDialog customDialog;

	/**
	 * @param session
	 * @param info
	 */
	public DropPrimaryKeyCommand(ISession session, IDatabaseObjectInfo[] info)
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
			return;
		}
		ITableInfo ti = (ITableInfo) _info[0];
		PrimaryKeyCommandUtility pkcUtil = new PrimaryKeyCommandUtility(super._session, _info);
		if (!pkcUtil.tableHasPrimaryKey())
		{
			_session.showErrorMessage(s_stringMgr.getString("DropPrimaryKeyCommand.noKeyToDrop",
				ti.getSimpleName()));
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
		String result = null;

		try
		{
			result =
				_dialect.getDropPrimaryKeySQL(customDialog.getPrimaryKeyName(), customDialog.getTableName());
		} catch (UnsupportedOperationException e2)
		{
			_session.showMessage(s_stringMgr.getString("DropPrimaryKeyCommand.unsupportedOperationMsg",
				_dialect.getDisplayName()));
		}
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
						_session.getSchemaInfo().reloadAll();
					}
				});
			}
		});
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.AbstractRefactoringCommand#isRefactoringSupportedForDialect(net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect)
	 */
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialectExt)
	{
		// implemented in all originally supported dialects
		return true;
	}

	/**
	 * @throws SQLException
	 */
	private void showCustomDialog() throws SQLException
	{
		ITableInfo ti = (ITableInfo) _info[0];
		TableColumnInfo[] columns = getPkTableColumns(ti);

		// Show the user a dialog with a list of columns and ask them to select
		customDialog = new ColumnListDialog(columns, ColumnListDialog.DROP_PRIMARY_KEY_MODE);
		customDialog.addColumnSelectionListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setMultiSelection();
		customDialog.setTableName(ti.getQualifiedName());

		SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
		PrimaryKeyInfo[] infos = md.getPrimaryKey(ti);
		String pkName = infos[0].getSimpleName();
		customDialog.setPrimaryKeyName(pkName);
		customDialog.setVisible(true);
	}

	/**
	 * @param ti
	 * @return
	 * @throws SQLException
	 */
	private TableColumnInfo[] getPkTableColumns(ITableInfo ti) throws SQLException
	{
		ArrayList<TableColumnInfo> result = new ArrayList<TableColumnInfo>();
		PrimaryKeyInfo[] pkCols = _session.getMetaData().getPrimaryKey(ti);
		TableColumnInfo[] colInfos = _session.getMetaData().getColumnInfo(ti);

		for (PrimaryKeyInfo pkInfo : pkCols)
		{
			String pkColName = pkInfo.getQualifiedColumnName();
			for (TableColumnInfo colInfo : colInfos)
			{
				if (colInfo.getSimpleName().equals(pkColName))
				{
					result.add(colInfo);
				}
			}
		}
		return result.toArray(new TableColumnInfo[result.size()]);
	}
	
}