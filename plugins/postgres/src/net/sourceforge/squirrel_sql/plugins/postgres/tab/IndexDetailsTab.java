package net.sourceforge.squirrel_sql.plugins.postgres.tab;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This class will display the details for an PostgreSQL index.
 *
 */
public class IndexDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(IndexDetailsTab.class);


	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		// i18n[IndexDetailsTab.title=Details]
		String TITLE = s_stringMgr.getString("IndexDetailsTab.title");
		// i18n[IndexDetailsTab.hint=Display index details]
		String HINT = s_stringMgr.getString("IndexDetailsTab.hint");
	}

	/** SQL that retrieves the data. */
	private static final String SQL =
        "select inds.schemaname as schemaname, " +
        "       inds.relname as tablename, " +
        "       i.indisunique as is_unique, " +
        "       i.indisprimary as is_primary_key, " +
        "       i.indisclustered as is_clustered, " +
        "       inds.idx_scan as num_index_scans, " +
        "       inds.idx_tup_read as num_index_entries_returned, " +
        "       inds.idx_tup_fetch as num_table_rows_fetched " +
        "from pg_catalog.pg_index i, pg_catalog.pg_stat_all_indexes inds " +
        "where i.indexrelid = inds.indexrelid " +
        "and inds.schemaname = ? " +
        "and inds.indexrelname = ? ";
        
	public IndexDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
