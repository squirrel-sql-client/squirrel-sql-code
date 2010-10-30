package net.sourceforge.squirrel_sql.client.session.action;

/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
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
import java.util.HashSet;
import java.util.List;

import net.sourceforge.squirrel_sql.client.gui.IProgressCallBackFactory;
import net.sourceforge.squirrel_sql.client.gui.ProgressCallBackFactory;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * @version $Id: DeleteTablesCommand.java,v 1.9 2008-10-11 22:55:54 manningr Exp $
 * @author Rob Manning
 */
public class DeleteTablesCommand implements ICommand
{
	/** Logger for this class. */
	private final ILogger s_log = LoggerController.createLogger(DeleteTablesCommand.class);

	/** Internationalized strings for this class */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DeleteTablesCommand.class);

	static interface i18n
	{

		// i18n[DeleteTablesCommand.progressDialogTitle=Analyzing tables to delete]
		String PROGRESS_DIALOG_TITLE = s_stringMgr.getString("DeleteTablesCommand.progressDialogTitle");

		// i18n[DeleteTablesCommand.loadingPrefix=Analyzing table:]
		String LOADING_PREFIX = s_stringMgr.getString("DeleteTablesCommand.loadingPrefix");

	}

	/** Current session. */
	private final ISession _session;

	/** Tables that have records to be deleted. */
	private final List<ITableInfo> _tables;

	/**
	 * A set of materialized view names in the same schema as the table(s) being deleted
	 */
	private HashSet<String> matViewLookup = null;

	/** API for the current tree. */
	private IObjectTreeAPI _tree;

	private IProgressCallBackFactory progressCallBackFactory = new ProgressCallBackFactory();
	
	/**
	 * Ctor.
	 * 
	 * @param session
	 *           Current session..
	 * @param tables
	 *           Array of <TT>IDatabaseObjectInfo</TT> objects representing the tables to be deleted.
	 * @throws IllegalArgumentException
	 *            Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public DeleteTablesCommand(IObjectTreeAPI tree, List<ITableInfo> tables)
	{
		super();
		if (tree == null) { throw new IllegalArgumentException("ISession == null"); }
		if (tables == null) { throw new IllegalArgumentException("IDatabaseObjectInfo[] == null"); }

		_session = tree.getSession();
		_tree = tree;
		_tables = tables;
	}

	/**
	 * Delete records from the selected tables in the object tree.
	 */
	public void execute()
	{		
		ProgressCallBack cb =
			progressCallBackFactory.create(_session.getApplication().getMainFrame(), 
				i18n.PROGRESS_DIALOG_TITLE, _tables.size());
			
		cb.setLoadingPrefix(i18n.LOADING_PREFIX);
		DeleteExecuter executer = new DeleteExecuter(cb);
		_session.getApplication().getThreadPool().addTask(executer);
	}

	private class DeleteExecuter implements Runnable
	{

		ProgressCallBack _cb = null;

		public DeleteExecuter(ProgressCallBack cb)
		{
			Utilities.checkNull("DeleteExecuter.init", "cb", cb);
			_cb = cb;
		}

		public void run()
		{
			final SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
			List<ITableInfo> orderedTables = _tables;
			try
			{
				orderedTables = SQLUtilities.getDeletionOrder(_tables, md, _cb);
			}
			catch (Exception e)
			{
				s_log.error("Unexpected exception while attempting to order tables", e);
			} finally {
				GUIUtils.processOnSwingEventThread(new Runnable()
				{
					public void run()				
					{
						_cb.setVisible(false);
					}
				});
			}
			final String sqlSep = _session.getQueryTokenizer().getSQLStatementSeparator();
			String cascadeClause = null;
			try
			{
				cascadeClause = md.getCascadeClause();
			}
			catch (SQLException e)
			{
				s_log.error("Unexpected exception while attempting to get cascade clause", e);
			}
			final StringBuilder buf = new StringBuilder();
			for (ITableInfo ti : orderedTables)
			{
				// Can't delete records in snapshots (Oracle materialized views)
				if (isMaterializedView(ti, _session))
				{
					continue;
				}
				buf.append("DELETE FROM ").append(ti.getQualifiedName());
				if (cascadeClause != null && !cascadeClause.equals(""))
				{
					buf.append(" ").append(cascadeClause);
				}
				buf.append(" ").append(sqlSep).append(" ").append('\n');
			}
			if (buf.length() == 0) { return; }
			SQLExecuterTask executer = new SQLExecuterTask(_session, buf.toString(), null);

			// Execute the sql synchronously
			executer.run();			

			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()				
				{
					_tree.refreshSelectedNodes();
				}
			});
		}
	}

	/**
	 * Returns a boolean value indicating whether or not the specified table info is not only a table, but also
	 * a materialized view.
	 * 
	 * @param ti
	 * @param session
	 * @return
	 */
	private boolean isMaterializedView(ITableInfo ti, ISession session)
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

	private void initMatViewLookup(ISession session, String schema)
	{
		matViewLookup = new HashSet<String>();
		// There is no good way using JDBC metadata to tell if the table is a
		// materialized view. So, we need to query the data dictionary to find
		// that out. Get all table names whose comment indicates that they are
		// a materialized view.
		String sql =
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
				String tableName = rs.getString(1);
				matViewLookup.add(tableName);
			}
		}
		catch (SQLException e)
		{
			s_log.error("Unexpected exception while attempting to find mat. views " + "in schema: " + schema, e);
		}
		finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}

	}

	public void setProgressCallBackFactory(IProgressCallBackFactory progressCallBackFactory)
	{
		this.progressCallBackFactory = progressCallBackFactory;
	}

}
