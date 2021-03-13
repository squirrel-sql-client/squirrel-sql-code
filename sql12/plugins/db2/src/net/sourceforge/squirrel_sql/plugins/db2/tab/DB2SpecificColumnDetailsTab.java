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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class will display the details for an DB2 index.
 */
public class DB2SpecificColumnDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DB2SpecificColumnDetailsTab.class);

	private final static ILogger s_log = LoggerController.createLogger(DB2SpecificColumnDetailsTab.class);

	/** Object that contains methods for retrieving SQL that works for each DB2 platform */
	private final DB2Sql db2Sql;

	public DB2SpecificColumnDetailsTab(DB2Sql db2Sql)
	{
		super(s_stringMgr.getString("DB2SpecificColumnDetailsTab.title"), s_stringMgr.getString("DB2SpecificColumnDetailsTab.hint"), false);
		this.db2Sql = db2Sql;
	}


	@Override
	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		String sql = db2Sql.getDB2SpecificColumnDetailsSql();

		if(StringUtilities.isEmpty(sql, true))
		{
			return null;
		}

		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(sql);
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
