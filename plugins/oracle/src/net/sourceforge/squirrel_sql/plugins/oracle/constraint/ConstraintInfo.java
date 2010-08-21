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
 * Java bean containing connection constraint  information.
 * 
 * @author bpaulon
 */
public class ConstraintInfo {
	public static enum ConstraintType {
		P, //primary key
		U, //unique
		R, //reference
		C  //check
	}
	
	private String owner;
	private String constraintName;
	private ConstraintType constraintType;
	private String tableName;
	private String searchCondition;
	private String rOwner;
	private String rConstraintName;
	private String deleteRule;
	private String generated;
	
	public ConstraintInfo(String owner,
			String constraintName,
			ConstraintType constraintType,
			String tableName,
			String searchCondition,
			String rOwner,
			String rConstraintName,
			String deleteRule,
			String generated) {
		
		this.owner = owner;
		this.constraintName = constraintName;
		this.constraintType = constraintType;
		this.tableName = tableName;
		this.searchCondition = searchCondition;
		this.rOwner = rOwner;
		this.rConstraintName = rConstraintName;
		this.deleteRule = deleteRule;
		this.generated = generated;
	}
	
	public String getOwner() {
		return owner;
	}

	public String getConstraintName() {
		return constraintName;
	}

	public ConstraintType getConstraintType() {
		return constraintType;
	}

	public String getTableName() {
		return tableName;
	}

	public String getSearchCondition() {
		return searchCondition;
	}

	public String getROwner() {
		return rOwner;
	}

	public String getRConstraintName() {
		return rConstraintName;
	}

	public String getDeleteRule() {
		return deleteRule;
	}

	public String getGenerated() {
		return generated;
	}
}
