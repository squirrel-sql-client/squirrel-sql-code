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

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * Class that creates the SQL source for a primary key constraint.
 *
 * @author bpaulon
 */
public class PKConstraintSource extends AbstractConstraintSource {
	
	public PKConstraintSource(ISession session, ConstraintInfo ci) {
		super(session, ci);
	}
	
	@Override
	public void buildConstraintClause() throws Exception {
		// CONSTRAINT + <CONSTRAINT_NAME>
		// PRIMARY KEY
		// (COLUMN1, COLUMN2, COLUMN3);

		constraintClause = "CONSTRAINT " + ci.getConstraintName()
				+ " PRIMARY KEY ( " + columnNamesAsCSV(columns) + " )";
	}
}
