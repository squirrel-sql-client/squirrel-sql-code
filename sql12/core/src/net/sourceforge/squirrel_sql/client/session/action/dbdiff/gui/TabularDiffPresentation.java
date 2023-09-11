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

package net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.ColumnDifference;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.TableDiffExecutor;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the class that performs the table comparison using database connections to two different database
 * schemas.
 */
public class TabularDiffPresentation implements IDiffPresentation
{
	private final static ILogger s_log = LoggerController.createLogger(TabularDiffPresentation.class);

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TabularDiffPresentation.class);

	static interface i18n
	{
		// i18n[TabularDiffPresentation.noDiffsMessage=No differences were detected]
		String NO_DIFFS_MESSAGE = s_stringMgr.getString("TabularDiffPresentation.noDiffsMessage");
	}

	private final List<ColumnDifference> colDifferences = new ArrayList<ColumnDifference>();

	/**
	 * Starts the thread that executes the copy operation.
	 */
	public void execute()
	{
		_execute();
	}

	/**
	 * Performs the table diff operation.
	 */
	private void _execute()
	{
		final IDatabaseObjectInfo[] sourceObjs = Main.getApplication().getDBDiffState().getSourceSelectedDatabaseObjects();
		final IDatabaseObjectInfo[] destObjs = Main.getApplication().getDBDiffState().getDestSelectedDatabaseObjects();

		if (!sanityCheck(sourceObjs, destObjs))
		{
			return;
		}

		final ISQLDatabaseMetaData sourceMetaData = Main.getApplication().getDBDiffState().getSourceSession().getMetaData();
		final ISQLDatabaseMetaData destMetaData = Main.getApplication().getDBDiffState().getDestSession().getMetaData();

		final TableDiffExecutor diff = new TableDiffExecutor(sourceMetaData, destMetaData);

		// Special case: when comparing two tables, ignore the names, letting the user specify any two
		// tables.
		if( sourceObjs.length == 1 && destObjs.length == 1 )
		{
			diff.setTableInfos((ITableInfo) sourceObjs[0], (ITableInfo) destObjs[0]);
			diff.execute();
			colDifferences.addAll(diff.getColumnDifferences());
		}
		else
		{
			final List<ITableInfo> tableList1 = getTableList(sourceMetaData, sourceObjs);
			final List<ITableInfo> tableList2 = getTableList(destMetaData, destObjs);

			// sanityCheck() ensures lists have the same length
			for (int i = 0; i < tableList1.size(); i++)
			{
				final ITableInfo t1 = tableList1.get(i);
				final ITableInfo t2 = tableList2.get(i);
				diff.setTableInfos(t1, t2);
				diff.execute();
				final List<ColumnDifference> columnDiffs = diff.getColumnDifferences();
				if( columnDiffs != null && columnDiffs.size() > 0 )
				{
					colDifferences.addAll(columnDiffs);
					for( final ColumnDifference colDiff : columnDiffs )
					{
						if( s_log.isDebugEnabled() )
						{
							s_log.debug(colDiff.toString());
						}
					}
				}
			}
		}

		if( colDifferences != null && colDifferences.size() > 0 )
		{
			final ColumnDiffDialog dialog = new ColumnDiffDialog(Main.getApplication().getMainFrame(), false);
			dialog.setColumnDifferences(colDifferences);
			dialog.setSession1Label(Main.getApplication().getDBDiffState().getSourceSession().getAlias().getName());
			dialog.setSession2Label(Main.getApplication().getDBDiffState().getDestSession().getAlias().getName());
			dialog.setVisible(true);
		}
		else
		{
			JOptionPane.showMessageDialog(Main.getApplication().getMainFrame(), i18n.NO_DIFFS_MESSAGE, "DBDiff", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private List<ITableInfo> getTableList(ISQLDatabaseMetaData md, IDatabaseObjectInfo[] objs)
	{
		final List<ITableInfo> result = new ArrayList<>();
		if (objs[0].getDatabaseObjectType() == DatabaseObjectType.TABLE)
		{
			for (final IDatabaseObjectInfo info : objs)
			{
				if (info instanceof ITableInfo)
				{
					result.add((ITableInfo) info);
				}
			}
		}
		return result;
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
			JOptionPane.showMessageDialog(Main.getApplication().getMainFrame(),
					s_stringMgr.getString("TabularDiffPresentation.number.of.dest.and.source.objects.mismatch"));
			result = false;
		}
		if (sourceObjs[0].getDatabaseObjectType() != destObjs[0].getDatabaseObjectType())
		{
			JOptionPane.showMessageDialog(Main.getApplication().getMainFrame(),
					s_stringMgr.getString("TabularDiffPresentation.objects.type.mismatch"));
			result = false;
		}
		return result;
	}

}
