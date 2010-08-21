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
package net.sourceforge.squirrel_sql.plugins.oracle.constraint;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;

/**
 * Base class that constructs the SQL source for the constraints
 * @author bpaulon
 */
public abstract class AbstractConstraintSource {

	String dropStmt;
	String addStmt;
	String constraintClause;
	List<ConstraintColumnInfo> columns;

	ConstraintInfo ci;
	ISession session;
	
	public AbstractConstraintSource(ISession session, ConstraintInfo ci) {
		if (session == null) {
			throw new IllegalStateException("Null ISession");
		}
		if (ci == null) {
			throw new IllegalStateException("Null ConstraintInfo");
		}
		
		this.session = session;
		this.ci = ci;
	}
	
	private final String SQL_COLUMNS = "select "
			+ " owner, constraint_name, table_name, column_name, position"
			+ " from all_cons_columns "
			+ " where owner = ? and constraint_name = ? "
			+ " order by position";
	
	public void buildDropStatement() {
		dropStmt = "ALTER TABLE " + ci.getOwner() + "." + ci.getTableName()
				+ "\n  DROP CONSTRAINT " + ci.getConstraintName();
	}

	public void buildAddStatement() {
		addStmt = "ALTER TABLE " + ci.getOwner() + "." + ci.getTableName()
				+ " ADD";
	}
	
	protected String columnNamesAsCSV(List<ConstraintColumnInfo> cols) {
		StringBuffer ret = new StringBuffer();
		boolean first = true;
		for (ConstraintColumnInfo column : cols) {
			if (!first) {
				ret.append(", ");
			} else {
				first = false;
			}
			ret.append(column.getColumnName());
		}
		return ret.toString();
	}
	
	public void readConstraintColumnNames()	throws SQLException {
		columns = readConstraintColumns(ci.getOwner(), 
				ci.getConstraintName());
	}
	
	protected List<ConstraintColumnInfo> readConstraintColumns(String schema, 
			String constraintName) 
			throws SQLException {
		List<ConstraintColumnInfo> columns = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = session.getSQLConnection().prepareStatement(SQL_COLUMNS);
			pstmt.setString(1, schema);
			pstmt.setString(2, constraintName);

			columns = new ArrayList<ConstraintColumnInfo>();
			rs = pstmt.executeQuery();
			while (rs.next()) {
				columns.add(new ConstraintColumnInfo(rs.getString(1),
								rs.getString(2),
								rs.getString(3),
								rs.getString(4),
								rs.getInt(5))
							);
			}
		} finally {
			SQLUtilities.closeResultSet(rs, true);
		}
		
		return columns;
	}

	public abstract void buildConstraintClause() throws Exception ;

	public String getSource() {
		return dropStmt + ";\n\n" + addStmt + " (\n" + constraintClause + ");";
	}
}
