package org.firebirdsql.squirrel.tab;

/*
 * Copyright (C) 2004 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanDataSet;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;

import org.firebirdsql.squirrel.util.IndexInfo;
import org.firebirdsql.squirrel.util.SystemTables;

/**
 * This is the tab displaying information about an index.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class IndexInfoTab extends BaseDataSetTab
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(IndexInfoTab.class);

	/** SQL that retrieves info about a stored procedure. */
	private String SQL = "SELECT " + SystemTables.IIndexTable.COL_NAME + ","
	      + SystemTables.IIndexTable.COL_DESCRIPTION + "," + SystemTables.IIndexTable.COL_ID + ","
	      + SystemTables.IIndexTable.COL_RELATION_NAME + "," + SystemTables.IIndexTable.COL_UNIQUE + ","
	      + SystemTables.IIndexTable.COL_SEGMENT_COUNT + "," + SystemTables.IIndexTable.COL_INACTIVE + ","
	      + SystemTables.IIndexTable.COL_SYSTEM + "," + SystemTables.IIndexTable.COL_FOREIGN_KEY + ","
	      + SystemTables.IIndexTable.COL_EXPRESSION_SOURCE + " FROM " + SystemTables.IIndexTable.TABLE_NAME
	      + " WHERE " + SystemTables.IIndexTable.COL_NAME + " = ?";

	/**
	 * Return the title for the tab.
	 * 
	 * @return The title for the tab.
	 */
	public String getTitle()
	{
		return s_stringMgr.getString("IndexInfoTab.title");
	}

	/**
	 * Return the hint for the tab.
	 * 
	 * @return The hint for the tab.
	 */
	public String getHint()
	{
		return s_stringMgr.getString("IndexInfoTab.hint");
	}

	/**
	 * Create the <TT>IDataSet</TT> to be displayed in this tab.
	 */
	protected IDataSet createDataSet() throws DataSetException
	{
		return new JavabeanDataSet(createIndexInfo());
	}

	private IndexInfo createIndexInfo() throws DataSetException
	{
		final ISession session = getSession();
		final ISQLConnection conn = session.getSQLConnection();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			final IDatabaseObjectInfo doi = getDatabaseObjectInfo();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, doi.getSimpleName());
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				return new IndexInfo(
				   rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getInt(6),
				   rs.getInt(7), rs.getInt(8), rs.getString(9), rs.getString(10));
			}
			String msg = s_stringMgr.getString("IndexInfoTab.err.noindex", doi.getSimpleName());
			throw new DataSetException(msg);
		} catch (SQLException ex)
		{
			throw new DataSetException(ex);
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
	}
}
