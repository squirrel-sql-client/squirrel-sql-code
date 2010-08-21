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

import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * Class that creates the SQL source for a foreign key constraint.
 *
 * @author bpaulon
 */
public class FKConstraintSource extends AbstractConstraintSource {

	public FKConstraintSource(ISession session, ConstraintInfo ci) {
		super(session, ci);
	}

	private String createReferencesClause() throws Exception {
		List<ConstraintColumnInfo> cols = readConstraintColumns(ci.getROwner(), 
				ci.getRConstraintName());
		String colsString = columnNamesAsCSV(cols);
		
		// all columns belong to the same table 
		String clause = "REFERENCES " + ci.getROwner() + "." 
				+ cols.get(0).getTableName()
				+ " ( " + colsString + " )";
		return clause;
	}

	protected String buildDeleteClause() {
		String clause = null;
		if (!"NO ACTION".equals(ci.getDeleteRule())) {
			clause = "ON DELETE " + ci.getDeleteRule();
		}
		return clause;
	}

	@Override
	public void buildConstraintClause() throws Exception {
		// CONSTRAINT <CONSTRAINT_NAME>
		// FOREIGN KEY (<COLUMN_01>, <COLUMN_02>)
		// REFERENCES GENESI_TO.BERSAGLI (<RCOLUMN_01>,<RCOLUMN_02>)
		// ON DELETE <DELETE_RULE>);

		constraintClause = "CONSTRAINT " + ci.getConstraintName()
				+ " FOREIGN KEY ( " + columnNamesAsCSV(columns) + " ) \n";
		constraintClause += createReferencesClause();
		
		String deleteClause = buildDeleteClause();
		if(deleteClause != null) {
			constraintClause += " \n  " + buildDeleteClause();
		}
	}
}
