/*
 * Copyright (C) 2005 Alexander Buloichik
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
package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This class will display the source for an Oracle view.
 */
public class ViewSourceTab extends OracleSourceTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ViewSourceTab.class);


	/**
	 * This interface defines locale specific strings.
	 */
	private interface i18n
	{
		// i18n[oracle.diplayScriptDetails=Display script details]
		String HINT = s_stringMgr.getString("oracle.diplayScriptDetails");
	}

	/** SQL that retrieves the data. */
	private static final String SQL =
        "select  'CREATE OR REPLACE VIEW ' || VIEW_NAME ||' AS ', TEXT " +
        "FROM SYS.ALL_VIEWS " +
        "WHERE OWNER = ? AND VIEW_NAME = ? ";
    
	public ViewSourceTab()
	{
		super(i18n.HINT);
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
