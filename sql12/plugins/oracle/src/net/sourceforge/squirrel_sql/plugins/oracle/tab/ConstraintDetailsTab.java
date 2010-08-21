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
 * This tab displays the details for the constraint
 * 
 * @author bpaulon
 */
public class ConstraintDetailsTab extends BasePreparedStatementTab {

	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(ConstraintDetailsTab.class);

	private interface i18n {
		// i18n[oracle.constraintDetails=Details]
		String TITLE = s_stringMgr.getString("oracle.constraintDetails");
		// i18n[oracle.displayConstraintDetails=Display constraint details]
		String HINT = s_stringMgr.getString("oracle.displayConstraintDetails");
	}

	/*
	 * SQL that retrieves the information from ALL_CONSTRAINTS - ALL_CONSTRAINTS
	 * describes constraint definitions on tables accessible to the current
	 * user.
	 */
	private static String SQL = "select"
			+ " c.owner, c.constraint_name, c.constraint_type,"
			+ " c.table_name, c.search_condition, c.r_owner,"
			+ " c.r_constraint_name, c.delete_rule,"
			+ " c.status, c.deferrable, c.deferred,"
			+ " c.validated, c.generated, c.bad,"
			+ " c.rely, c.last_change, c.index_owner,"
			+ " c.index_name, c.invalid, c.view_related"
			+ " from sys.all_constraints c " + " where c.owner = ?"
			+ " and c.constraint_name = ?";

	public ConstraintDetailsTab() {
		super(i18n.TITLE, i18n.HINT, true);
	}

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
