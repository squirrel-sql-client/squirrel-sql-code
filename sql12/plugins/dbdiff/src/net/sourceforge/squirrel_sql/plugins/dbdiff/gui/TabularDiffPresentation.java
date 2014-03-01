/*
 * Copyright (C) 2007 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.ColumnDifference;
import net.sourceforge.squirrel_sql.plugins.dbdiff.TableDiffExecutor;

/**
 * This is the class that performs the table comparison using database connections to two different database
 * schemas.
 */
public class TabularDiffPresentation extends AbstractDiffPresentation
{

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(TabularDiffPresentation.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TabularDiffPresentation.class);

	static interface i18n
	{
		// i18n[TabularDiffPresentation.noDiffsMessage=No differences were detected]
		String NO_DIFFS_MESSAGE = s_stringMgr.getString("TabularDiffPresentation.noDiffsMessage");
	}

	/** the thread we do the work in */
	private Thread execThread = null;

	private final List<ColumnDifference> colDifferences = new ArrayList<ColumnDifference>();

	/**
	 * Starts the thread that executes the copy operation.
	 */
	public void execute()
	{
		final Runnable runnable = new Runnable()
		{
			public void run()
			{
				try
				{
					_execute();
				}
				catch (final Exception e)
				{
					s_log.error("Unexpected exception encountered while executing diff: " + e.getMessage(), e);
				}
			}
		};
		execThread = new Thread(runnable);
		execThread.setName("DBDiff Executor Thread");
		execThread.start();
	}

	/**
	 * Performs the table diff operation.
	 */
	private void _execute() throws SQLException
	{
		final boolean encounteredException = false;
		final IDatabaseObjectInfo[] sourceObjs = sessionInfoProvider.getSourceSelectedDatabaseObjects();
		final IDatabaseObjectInfo[] destObjs = sessionInfoProvider.getDestSelectedDatabaseObjects();

		if (!sanityCheck(sourceObjs, destObjs))
		{
			return;
		}

		final ISQLDatabaseMetaData sourceMetaData = sessionInfoProvider.getSourceSession().getMetaData();
		final ISQLDatabaseMetaData destMetaData = sessionInfoProvider.getDestSession().getMetaData();

		final Map<String, ITableInfo> tableMap1 = getTableMap(sourceMetaData, sourceObjs);
		final Map<String, ITableInfo> tableMap2 = getTableMap(destMetaData, destObjs);

		final Set<String> tableNames = getAllTableNames(tableMap1);
		tableNames.addAll(getAllTableNames(tableMap2));

		try
		{
			final TableDiffExecutor diff = new TableDiffExecutor(sourceMetaData, destMetaData);

			// Special case: when comparing two tables, ignore the names, letting the user specify any two
			// tables.
			if (sourceObjs.length == 1 && destObjs.length == 1)
			{
				diff.setTableInfos((ITableInfo) sourceObjs[0], (ITableInfo) destObjs[0]);
				diff.execute();
				colDifferences.addAll(diff.getColumnDifferences());
			}
			else
			{
				for (final String table : tableNames)
				{
					if (tableMap1.containsKey(table))
					{
						if (tableMap2.containsKey(table))
						{
							final ITableInfo t1 = tableMap1.get(table);
							final ITableInfo t2 = tableMap2.get(table);
							diff.setTableInfos(t1, t2);
							diff.execute();
							final List<ColumnDifference> columnDiffs = diff.getColumnDifferences();
							if (columnDiffs != null && columnDiffs.size() > 0)
							{
								colDifferences.addAll(columnDiffs);
								for (final ColumnDifference colDiff : columnDiffs)
								{
									if (s_log.isDebugEnabled()) {
										s_log.debug(colDiff.toString());
									}
								}
							}
						}
						else
						{
							// table exists in source db but not dest
							if (s_log.isInfoEnabled()) {
								s_log.info("Skipping Table ("+table+") that exists in database ("+sourceSession+
									"), but not in the database ("+destSession+")");
							}									
						}
					}
					else
					{
						// table doesn't exist in source db
						if (s_log.isInfoEnabled()) {
							s_log.info("Skipping Table ("+table+") that exists in database ("+destSession+
								"), but not in the database ("+sourceSession+")");
						}						
					}
				}
			}
			final MainFrame frame = sourceSession.getApplication().getMainFrame();
			if (colDifferences != null && colDifferences.size() > 0)
			{
				GUIUtils.processOnSwingEventThread(new Runnable()
				{
					public void run()
					{
						final ColumnDiffDialog dialog = new ColumnDiffDialog(frame, false);
						dialog.setColumnDifferences(colDifferences);
						dialog.setSession1Label(sourceSession.getAlias().getName());
						dialog.setSession2Label(destSession.getAlias().getName());
						dialog.setVisible(true);
					}
				});
			}
			else
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						JOptionPane.showMessageDialog(frame, i18n.NO_DIFFS_MESSAGE, "DBDiff",
							JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
		}
		catch (final SQLException e)
		{
			s_log.error("Encountered unexpected exception while executing " + "diff: " + e.getMessage(), e);
		}

		if (encounteredException)
		{
			return;
		}
	}

	private Set<String> getAllTableNames(Map<String, ITableInfo> tables)
	{
		final HashSet<String> result = new HashSet<String>();
		result.addAll(tables.keySet());
		return result;
	}

	private Map<String, ITableInfo> getTableMap(ISQLDatabaseMetaData md, IDatabaseObjectInfo[] objs)
		throws SQLException
	{
		final HashMap<String, ITableInfo> result = new HashMap<String, ITableInfo>();
		if (objs[0].getDatabaseObjectType() == DatabaseObjectType.TABLE)
		{
			for (final IDatabaseObjectInfo info : objs)
			{
				// TODO: allow the user to specify ignore case or preserve case.
				result.put(info.getSimpleName().toUpperCase(), (ITableInfo) info);
			}
		}
		else
		{
			// Assume objs[0] is a schema/catalog
			final String catalog = objs[0].getCatalogName();
			final String schema = objs[0].getSchemaName();
			md.getTables(catalog, schema, null, new String[] { "TABLE" }, null);
		}
		return result;
	}

	/**
	 * Returns a list of column differences.
	 * 
	 * @return Returns null if no diffs exist.
	 */
	public List<ColumnDifference> getColumnDifferences()
	{
		return colDifferences;
	}

	/**
	 * Must have the same number of objects to compare in each set, and they must be the same type of Objects
	 * (Schemas or Tables)
	 * 
	 * @param sourceObjs
	 * @param destObjs
	 * @return
	 */
	private boolean sanityCheck(IDatabaseObjectInfo[] sourceObjs, IDatabaseObjectInfo[] destObjs)
	{
		boolean result = true;
		if (sourceObjs.length != destObjs.length)
		{
			result = false;
		}
		if (sourceObjs[0].getDatabaseObjectType() != destObjs[0].getDatabaseObjectType())
		{
			result = false;
		}
		return result;
	}

}
