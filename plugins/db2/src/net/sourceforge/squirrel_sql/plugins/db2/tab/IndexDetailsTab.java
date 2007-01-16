package net.sourceforge.squirrel_sql.plugins.db2.tab;
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
 * This class will display the details for an DB2 index.
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
        "SELECT T1.IID as index_identifier, " +
        "       T1.DEFINER AS index_owner, " +
        "       T1.INDNAME AS index_name, " +
        "       T2.DEFINER AS table_owner, " +
        "       T2.TABNAME AS table_name, " +
        "       T3.TBSPACE AS table_space, " +
        "       case T1.INDEXTYPE " +
        "         when 'BLOK' then 'Block Index' " +
        "         when 'CLUS' then 'Clustering Index' " +
        "         when 'DIM' then 'Dimension Block Index' " +
        "         when 'REG' then 'Regular Index' " +
        "         when 'XPTH' then 'XML Path Index' " +
        "         when 'XRGN' then 'XML Region Index' " +
        "         when 'XVIL' then 'Index over XML column (Logical)' " +
        "         when 'XVIP' then 'Index over XML column (Physical)' " +
        "       end AS index_type, " +
        "       case T1.UNIQUERULE " +
        "         when 'U' then 'UNIQUE' " +
        "         when 'D' then 'NON-UNIQUE' " +
        "         when 'I' then 'UNIQUE (Implements PK)' " +
        "       end AS uniqueness, " +
        "       T1.NLEAF AS number_of_leaf_pages, " +
        "       T1.NLEVELS AS number_of_levels, " +
        "       T1.CREATE_TIME, " +
        "       T1.STATS_TIME AS last_statistics_update, " +
        "       case T1.REVERSE_SCANS " +
        "         when 'Y' then 'Supported' " +
        "         when 'N' then 'Not Supported' " +
        "       end AS reverse_scans " +
        "FROM    SYSCAT.INDEXES   AS T1, " +
        "        SYSCAT.TABLES    AS T2, " +
        "        SYSCAT.TABLESPACES as T3 " +
        "WHERE  T3.TBSPACEID = T1.TBSPACEID " +
        "and T2.TABNAME = T1.TABNAME " +
        "and T2.TABSCHEMA = T1.TABSCHEMA " +
        "AND     T1.TABSCHEMA = ? " +
        "AND     T1.INDNAME = ? ";
    
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
