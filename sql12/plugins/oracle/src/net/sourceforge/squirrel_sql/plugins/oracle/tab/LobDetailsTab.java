/*
 * Copyright (C) 2010 Bogdan Cristian Paulon
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
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This class will display the details for an Oracle LOB
 * @author bpaulon
 */
public class LobDetailsTab extends BasePreparedStatementTab {
	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(LobDetailsTab.class);

	/**
	 * This interface defines locale specific strings.
	 */
	private interface i18n {
		// i18n[oracle.lobDetails=Details]
		String TITLE = s_stringMgr.getString("oracle.lobDetails");
		// i18n[oracle.displayLobDetails=Display LOB details]
		String HINT = s_stringMgr.getString("oracle.displayLobDetails");
	}

	/** SQL that retrieves the data. */
	private static String SQL = 
		      "select l.owner, l.table_name, l.column_name, l.segment_name,"
			+ " l.tablespace_name, l.index_name, l.chunk, l.pctversion, "
			+ " l.retention, l.freepools, l.cache, l.logging, "
			+ " l.in_row, l.format, l.partitioned "
			+ " from sys.all_lobs l "
			+ " where l.owner = ? and l.segment_name = ? ";

	public LobDetailsTab() {
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException {
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection()
			.prepareStatement(SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
