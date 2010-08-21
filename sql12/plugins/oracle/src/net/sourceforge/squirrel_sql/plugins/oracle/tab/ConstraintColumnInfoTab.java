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
 * This tab displays the column information for the constraint
 * 
 * @author bpaulon
 */
public class ConstraintColumnInfoTab extends BasePreparedStatementTab {

	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(ConstraintDetailsTab.class);

	private interface i18n {
		// i18n[oracle.constraintColumns=Columns]
		String TITLE = s_stringMgr.getString("oracle.constraintColumns");
		// i18n[oracle.displayConstraintColumns=Display constraint column
		// information]
		String HINT = s_stringMgr.getString("oracle.displayConstraintColumns");
	}

	/*
	 * SQL that retrieves the information from view ALL_CONS_COLUMNS -
	 * ALL_CONS_COLUMNS describes only those columns accessible to current user
	 * that are specified in constraints.
	 */
	private static String SQL = "select column_name, position"
			+ " from sys.all_cons_columns where owner = ?"
			+ " and constraint_name = ?";

	public ConstraintColumnInfoTab() {
		super(i18n.TITLE, i18n.HINT);
	}

	@Override
	protected PreparedStatement createStatement() throws SQLException {
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(
				SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
