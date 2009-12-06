package net.sourceforge.squirrel_sql.plugins.derby.tab;

/*
 * Copyright (C) 2009 Rob Manning
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
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ProcedureSourceTab  extends BaseSourceTab {

	private static String SQL = 
		"SELECT "+ 
		  " 'CREATE PROCEDURE '||SCHEMAA.SCHEMANAME||'.'||ALIAS.ALIAS||'\n '||"+
		  " SUBSTR(CAST(ALIASINFO AS VARCHAR(4000)),LOCATE('(',CAST(ALIASINFO AS VARCHAR(4000))))||'\n '||"+
		  " 'EXTERNAL NAME '''||ALIAS.JAVACLASSNAME||'.'||SUBSTR(CAST(ALIASINFO AS VARCHAR(4000)),1,LOCATE('(',CAST(ALIASINFO AS VARCHAR(4000)))-1)||''''"+
		" FROM"+ 
		"  SYS.SYSALIASES ALIAS,"+
		"  SYS.SYSSCHEMAS SCHEMAA"+
		" WHERE"+ 
		"  ALIAS.SCHEMAID = SCHEMAA.SCHEMAID AND" +
		"  ALIAS = ? and SCHEMAA.SCHEMANAME = ?";		
	public ProcedureSourceTab(String hint) {
		super(hint);
	}

	private final static ILogger s_log = LoggerController.createLogger(ProcedureSourceTab.class);

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		if (s_log.isDebugEnabled())
		{
			s_log.debug("Running SQL: " + SQL);
			s_log.debug("Trigger Name=" + doi.getSimpleName());
			s_log.debug("Schema Name=" + doi.getSchemaName());
		}
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, doi.getSimpleName());
		pstmt.setString(2, doi.getSchemaName());
		return pstmt;
	}

}
