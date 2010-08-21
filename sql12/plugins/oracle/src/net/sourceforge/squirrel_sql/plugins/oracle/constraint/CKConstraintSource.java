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

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * Class that creates the SQL source for a check constraint.
 *
 * @author bpaulon
 */
public class CKConstraintSource extends AbstractConstraintSource {

	private AbstractConstraintSource cs;
	
	/* 
	 * Creates the SQL for the not null constraint
	 * The not null constraint is used to specify that a column may never 
	 * contain a NULL value. 
	 */
	private class NotNullCKConstraintSource extends AbstractConstraintSource {

		public NotNullCKConstraintSource(ISession session, ConstraintInfo ci) {
			super(session, ci);
		}
		
		@Override
		public void buildDropStatement() {
			// ALTER TABLE <SCHEMA>.<CONSTRAINT_NAME> MODIFY NULL
			//
			// The check constraint defined on a table must refer to only columns 
			// in that table. It can not refer to columns in other tables.
			dropStmt = "ALTER TABLE " + ci.getOwner() + "." + ci.getTableName()
					+ "\n  MODIFY " 
					+ CKConstraintSource.this.columns.get(0).getColumnName()
					+ " NULL";
		}

		@Override
		public void buildAddStatement() {
			// ALTER TABLE <SCHEMA>.<CONSTRAINT_NAME> MODIFY NOT NULL
			addStmt = "ALTER TABLE " + ci.getOwner() + "." + ci.getTableName()
					+ "\n  MODIFY " 
					+ CKConstraintSource.this.columns.get(0).getColumnName()
					+ " NOT NULL";
		}

		@Override
		public void buildConstraintClause() {
			// NOP
		}
		
		@Override
		public String getSource() {
			return dropStmt + ";\n\n" + addStmt + ";";
		}
	}

	public CKConstraintSource(ISession session, ConstraintInfo ci) {
		super(session, ci);
	}

	public CKConstraintSource(ISession session, ConstraintInfo ci,
			AbstractConstraintSource cs) {
		super(session, ci);
		this.cs = cs;
	}

	@Override
	public void readConstraintColumnNames() throws SQLException {
		super.readConstraintColumnNames();

		if ("GENERATED NAME".equals(ci.getGenerated()) && isNotNullConstraint()) {
			// use a NOT NULL policy
			cs = new NotNullCKConstraintSource(session, ci);
		} else {
			// use a CHECK policy
			cs = new AbstractConstraintSource(session, ci) {

				@Override
				public void buildConstraintClause() {
					constraintClause = "CONSTRAINT " + ci.getConstraintName()
							+ "\n  CHECK ( " + ci.getSearchCondition() + " )";
				}
			};
		}
	}
	
	@Override
	public void buildDropStatement() {
		cs.buildDropStatement();
	}
	
	@Override
	public void buildAddStatement() {
		cs.buildAddStatement();
	}

	@Override
	public void buildConstraintClause() throws Exception {
		cs.buildConstraintClause();
	}
	
	@Override
	public String getSource() {
		return cs.getSource();
	}

	private boolean isNotNullConstraint() {
		String expectedSearchCondition = "\"" 
				+ columns.get(0).getColumnName() 
				+ "\""
				+ " IS NOT NULL";
		if (expectedSearchCondition.equals(ci.getSearchCondition())) {
			return true;
		}
		return false;
	}
}
