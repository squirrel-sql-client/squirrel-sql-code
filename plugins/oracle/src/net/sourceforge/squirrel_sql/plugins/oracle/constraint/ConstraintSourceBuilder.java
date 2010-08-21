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

/**
 * Create a source of type @link AbstractConstraintSource
 * and calls the necessary steps to construct the SQL source
 * (see @link buildConstraintSource) 
 * 
 * @author bpaulon
 */
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.oracle.constraint.ConstraintInfo.ConstraintType;

public class ConstraintSourceBuilder {
	
	AbstractConstraintSource constraintSource;
	ISession session;

	public ConstraintSourceBuilder (ISession session) {
		this.session = session;
	}
	
	public String getConstraintSource() {
		if(constraintSource != null) {
			return constraintSource.getSource();
		} else {
			return null;
		}
	}
	
	/**
	 * Does not handle the following oracle constraints
	 *  - Read Only Constraint - Type O
	 *     Read Only on a view
	 *  - Check Option - Type V
	 *     Specify WITH CHECK OPTION to indicate that Oracle prohibits 
	 *     any changes to the table or view that would produce rows that 
	 *     are not included in the subquery
	 * @param ci - constraint info java bean
	 * @throws SQLException
	 */
	public void buildConstraintSource(ConstraintInfo ci) 
			throws Exception {
			
		if (ci.getConstraintType() == ConstraintType.P) {
			this.constraintSource =	new PKConstraintSource(session, ci);
		} else if (ci.getConstraintType() == ConstraintType.C) {
			this.constraintSource = new CKConstraintSource(session, ci);
		} else if (ci.getConstraintType() == ConstraintType.R) {
			this.constraintSource = new FKConstraintSource(session, ci);
		} else if (ci.getConstraintType() == ConstraintType.U) {
			this.constraintSource = new UQConstraintSource(session, ci);
		}
		
		constraintSource.readConstraintColumnNames();
		constraintSource.buildDropStatement();
		constraintSource.buildAddStatement();
		constraintSource.buildConstraintClause();
	}
}
