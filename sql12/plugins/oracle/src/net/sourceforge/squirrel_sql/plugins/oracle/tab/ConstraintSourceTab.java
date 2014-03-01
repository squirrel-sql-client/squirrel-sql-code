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
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourcePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.oracle.constraint.ConstraintInfo;
import net.sourceforge.squirrel_sql.plugins.oracle.constraint.ConstraintSourceBuilder;

/**
 * This tab displays the source for the constraint
 * 
 * @author bpaulon
 */
public class ConstraintSourceTab extends BaseSourceTab {

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(TriggerSourceTab.class);

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController
			.createLogger(BaseSourceTab.class);

	/**
	 * SQL that retrieves the data we need to build the source for this
	 * constraint
	 */
	private static String SQL = "select owner, constraint_name,"
			+ " constraint_type, table_name, search_condition, r_owner,"
			+ " r_constraint_name, delete_rule, generated"
			+ " from all_constraints"
			+ " where owner = ? and constraint_name = ?";

	public ConstraintSourceTab() {
		// i18n[oracle.showConstraintSource=Show constraint source]
		super(s_stringMgr.getString("oracle.showConstraintSource"));
		
	}

	@Override
	protected PreparedStatement createStatement() throws SQLException {
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}

	/**
	 * The panel that displays the source code for the constraint
	 */
	private final class ConstraintSourcePanel extends BaseSourcePanel {
		private static final long serialVersionUID = 1L;


		ConstraintSourcePanel(ISession session) {
			super(session);
		}

		public void load(ISession session, PreparedStatement stmt) {
			ConstraintSourceBuilder csb = new ConstraintSourceBuilder(session);

			ResultSet rs = null;
			try {
				rs = stmt.executeQuery();

				if (rs.next()) {
					ConstraintInfo ci = new ConstraintInfo(
							rs.getString(1),
							rs.getString(2),
							ConstraintInfo.ConstraintType
								.valueOf(rs.getString(3)),
							rs.getString(4),
							rs.getString(5),
							rs.getString(6),
							rs.getString(7),
							rs.getString(8),
							rs.getString(9));

					csb.buildConstraintSource(ci);
					
					if (csb.getConstraintSource() != null) {
						getTextArea().setText(csb.getConstraintSource());
					} else {
						getTextArea().setText("Source NOT available");
					}
				}
			} catch (Exception ex) {
				if (s_log.isDebugEnabled()) {
					s_log.debug("Unexpected exception while formatting "
							+ "object source code", ex);
				}
				session.showErrorMessage(ex);
			} finally {
				SQLUtilities.closeResultSet(rs, true);
			}
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab#createSourcePanel()
	 */
	@Override
	protected BaseSourcePanel createSourcePanel() {
		return new ConstraintSourcePanel(getSession());
	}
}
