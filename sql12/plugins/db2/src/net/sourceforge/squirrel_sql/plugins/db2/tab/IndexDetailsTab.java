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
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql;

/**
 * This class will display the details for an DB2 index.
 */
public class IndexDetailsTab extends BasePreparedStatementTab
{
	/** i18N strings for this class */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(IndexDetailsTab.class);

	/**
	 * This interface defines locale specific strings. This should be replaced with a property file.
	 */
	private interface i18n
	{
		// i18n[IndexDetailsTab.title=Details]
		String TITLE = s_stringMgr.getString("IndexDetailsTab.title");

		// i18n[IndexDetailsTab.hint=Display index details]
		String HINT = s_stringMgr.getString("IndexDetailsTab.hint");
	}

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(IndexDetailsTab.class);

	/** Object that contains methods for retrieving SQL that works for each DB2 platform */
	private final DB2Sql db2Sql;

	/**
	 * Constructor
	 * 
	 * @param db2Sql
	 *           Object that contains methods for retrieving SQL that works for each DB2 platform
	 */
	public IndexDetailsTab(DB2Sql db2Sql)
	{
		super(i18n.TITLE, i18n.HINT, true);
		this.db2Sql = db2Sql;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab#createStatement()
	 */
	@Override
	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		String sql = db2Sql.getIndexDetailsSql();

		if (s_log.isDebugEnabled())
		{
			s_log.debug("Running SQL for index details: " + sql);
			s_log.debug("schema=" + doi.getSchemaName());
			s_log.debug("index name=" + doi.getSimpleName());
		}
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(sql);
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
