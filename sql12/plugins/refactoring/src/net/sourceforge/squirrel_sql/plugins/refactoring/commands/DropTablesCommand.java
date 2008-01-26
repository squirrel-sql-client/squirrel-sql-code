package net.sourceforge.squirrel_sql.plugins.refactoring.commands;

/*
 * Copyright (C) 2007 Rob Manning
 * manningr@user.sourceforge.net
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.squirrel_sql.client.gui.ProgessCallBackDialog;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DropTableDialog;

public class DropTablesCommand extends AbstractRefactoringCommand
{
	/**
	 * Logger for this class.
	 */
	private final ILogger s_log = LoggerController.createLogger(DropTablesCommand.class);

	/**
	 * Internationalized strings for this class
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DropTablesCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("DropTablesCommand.sqlDialogTitle");

		String PROGRESS_DIALOG_ANALYZE_TITLE =
			s_stringMgr.getString("DropTablesCommand.progressDialogAnalyzeTitle");

		String PROGRESS_DIALOG_DROP_TITLE = s_stringMgr.getString("DropTablesCommand.progressDialogDropTitle");

		String LOADING_PREFIX = s_stringMgr.getString("DropTablesCommand.loadingPrefix");

		String DROPPING_CONSTRAINT_PREFIX = s_stringMgr.getString("DropTablesCommand.droppingConstraintPrefix");

		String DROPPING_TABLE_PREFIX = s_stringMgr.getString("DropTablesCommand.droppingTablePrefix");
	}

	protected DropTableDialog customDialog;

	private List<ITableInfo> orderedTables;

	ProgessCallBackDialog getOrderedTablesCallBack;

	/**
	 * A set of materialized view names in the same schema as the table(s) being dropped.
	 */
	private HashSet<String> matViewLookup;

	public DropTablesCommand(final ISession session, final IDatabaseObjectInfo[] tables)
	{
		super(session, tables);
	}

	@Override
	protected void onExecute()
	{
		showCustomDialog();
	}

	protected void showCustomDialog()
	{
		final ITableInfo[] tableInfos = new ITableInfo[_info.length];
		for (int i = 0; i < tableInfos.length; i++)
		{
			tableInfos[i] = (ITableInfo) _info[i];
		}

		customDialog = new DropTableDialog(tableInfos);
		customDialog.addExecuteListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setVisible(true);
	}

	@Override
	protected String[] generateSQLStatements() throws UserCancelledOperationException, SQLException
	{
		final ArrayList<String> result = new ArrayList<String>();
		if (s_log.isDebugEnabled())
		{
			s_log.debug("Generating SQL Statements");
		}
		final List<ITableInfo> tables = customDialog.getTableInfoList();
		final boolean cascadeConstraints = customDialog.getCascadeConstraints();

		try
		{
			if (tables.size() > 1)
			{
				orderedTables = getOrderedTables(tables);
			} else
			{
				orderedTables = tables;
			}

			// Drop FK constraints before dropping any tables. Otherwise, we
			// may drop the child table prior to dropping it's FKs, which would
			// be an error.
			// TODO: This should be done in the dialect's getTableDropSQL method for a database specific
			// handling(or ignoring) of this problem.
			if (cascadeConstraints)
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug("Getting DropChildFKConstraintsSQL (CASCADE=true)");
				}
				for (final ITableInfo info : orderedTables)
				{
					final List<String> dropFKSQLs = getDropChildFKConstraints(info);
					result.addAll(dropFKSQLs);
				}
			}

			if (s_log.isDebugEnabled())
			{
				s_log.debug("Getting TableDropSQL");
			}
			for (final ITableInfo info : orderedTables)
			{
				final boolean isMaterializedView = isMaterializedView(info, _session);
				// There are more dependancies then just FKs (like views, etc.),
				// therefore cascadeConstraints is used as a parameter for the TableDropSQL.
				final List<String> sqls = _dialect.getTableDropSQL(info, cascadeConstraints, isMaterializedView);
				result.addAll(sqls);
			}
		} catch (final UnsupportedOperationException e2)
		{
			_session.showMessage(s_stringMgr.getString("DropTablesCommand.unsupportedOperationMsg",
				_dialect.getDisplayName()));
		}
		return result.toArray(new String[] {});
	}

	private List<String> getDropChildFKConstraints(final ITableInfo ti) throws SQLException
	{
		if (s_log.isDebugEnabled())
		{
			s_log.debug("entered getDropChildFKConstraints()");
		}
		final ArrayList<String> result = new ArrayList<String>();
		final ForeignKeyInfo[] fks = _session.getMetaData().getExportedKeysInfo(ti);
		if (fks != null)
		{
			if (s_log.isDebugEnabled())
			{
				s_log.debug("FKs.length: " + fks.length);
			}
			for (final ForeignKeyInfo info : fks)
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug("FK Info: " + info);
				}
				final String fkName = info.getForeignKeyName();
				final String fkTable = info.getForeignKeyTableName();
				result.add(_dialect.getDropForeignKeySQL(fkName, fkTable));
			}
		}
		if (s_log.isDebugEnabled())
		{
			s_log.debug("leaving getDropChildFKConstraints()");
		}
		return result;
	}

	private List<ITableInfo> getOrderedTables(final List<ITableInfo> tables) throws SQLException
	{
		if (tables.size() < 2)
		{
			// No point to ordering less than 2 tables
			return tables;
		}
		final SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();

		// Create the analysis dialog using the EDT, and wait for it to finish.
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				getOrderedTablesCallBack =
					new ProgessCallBackDialog(customDialog, i18n.PROGRESS_DIALOG_ANALYZE_TITLE, tables.size());

				getOrderedTablesCallBack.setLoadingPrefix(i18n.LOADING_PREFIX);
			}
		}, true);

		// Now, get the drop order (same as delete) and update the dialog
		// status while doing so.
		final List<ITableInfo> result = SQLUtilities.getDeletionOrder(tables, md, getOrderedTablesCallBack);
		getOrderedTablesCallBack.setVisible(false);
		getOrderedTablesCallBack = null;
		return result;
	}

	/**
	 * Returns a boolean value indicating whether or not the specified table info is not only a table, but also
	 * a materialized view.
	 * 
	 * @param ti
	 * @param session
	 * @return
	 * @throws java.sql.SQLException
	 */
	private boolean isMaterializedView(final ITableInfo ti, final ISession session) throws SQLException
	{
		if (!DialectFactory.isOracle(session.getMetaData()))
		{
			// Only Oracle supports materialized views directly.
			return false;
		}
		if (matViewLookup == null)
		{
			initMatViewLookup(session, ti.getSchemaName());
		}
		return matViewLookup.contains(ti.getSimpleName());
	}

	/**
	 * There is no good way using JDBC metadata to tell if the table is a materialized view. So, we need to
	 * query the data dictionary to find that out. Get all table names whose comment indicates that they are a
	 * materialized view.
	 * 
	 * @param session
	 *           the session to query data from
	 * @param schema
	 *           the schema whose matviews we are interested in.
	 * @throws SQLException
	 *            if an error occurs
	 */
	private void initMatViewLookup(final ISession session, final String schema) throws SQLException
	{
		matViewLookup = new HashSet<String>();
		final String sql =
			"SELECT TABLE_NAME FROM ALL_TAB_COMMENTS " + "where COMMENTS like 'snapshot%' " + "and OWNER = ? ";

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = session.getSQLConnection().prepareStatement(sql);
			stmt.setString(1, schema);
			rs = stmt.executeQuery();
			if (rs.next())
			{
				final String tableName = rs.getString(1);
				matViewLookup.add(tableName);
			}
		} finally
		{
			SQLUtilities.closeResultSet(rs);
			SQLUtilities.closeStatement(stmt);
		}
	}

	@Override
	protected void executeScript(final String script)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				final DropTableCommandExecHandler handler = new DropTableCommandExecHandler(_session);
				_session.getApplication().getThreadPool().addTask(new Runnable()
				{
					public void run()
					{
						final SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
						executer.setSchemaCheck(false);
						executer.run();
						
						GUIUtils.processOnSwingEventThread(new Runnable()
						{
							public void run()
							{
								handler.hideProgressDialog();	
								customDialog.setVisible(false);
							}
						});
						
						_session.getSchemaInfo().reloadAllTables();
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
	protected boolean isRefactoringSupportedForDialect(final HibernateDialect dialectExt)
	{
		// implemented in all originally supported dialects
		return true;
	}

	private class DropTableCommandExecHandler extends DefaultSQLExecuterHandler
	{
		ProgessCallBackDialog cb = null;

		/**
		 * This is used to track the number of tables seen so far, so that we can pick the right one from the
		 * ordered table list to display as the table name of the table currently being dropped - yes, a hack!
		 */
		int tableCount = 0;

		public DropTableCommandExecHandler(final ISession session)
		{
			super(session);
			cb =
				new ProgessCallBackDialog(	customDialog,
													i18n.PROGRESS_DIALOG_DROP_TITLE,
													DropTablesCommand.this.orderedTables.size());
		}

		/**
		 * hides the progress dialog.
		 */
		public void hideProgressDialog() {
			cb.setVisible(false);
			cb.dispose();
		}
		
		/**
		 * @see net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler#sqlStatementCount(int)
		 */
		@Override
		public void sqlStatementCount(final int statementCount)
		{
			cb.setTotalItems(statementCount);
		}

		/**
		 * @see net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler#sqlToBeExecuted(java.lang.String)
		 */
		@Override
		public void sqlToBeExecuted(final String sql)
		{
			if (s_log.isDebugEnabled())
			{
				s_log.debug("Statement to be executed: " + sql);
			}

			if (sql.startsWith("ALTER"))
			{
				cb.setLoadingPrefix(i18n.DROPPING_CONSTRAINT_PREFIX);
				// Hack!!! hopefully the FK name will always be the last token!
				final String[] parts = StringUtilities.split(sql, ' ');
				cb.currentlyLoading(parts[parts.length - 1]);
			} else
			{
				cb.setLoadingPrefix(i18n.DROPPING_TABLE_PREFIX);
				if (tableCount < DropTablesCommand.this.orderedTables.size())
				{
					final ITableInfo ti = DropTablesCommand.this.orderedTables.get(tableCount);
					cb.currentlyLoading(ti.getSimpleName());
				}
				tableCount++;
			}
		}
	}

}
