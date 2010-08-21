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
 * Java bean containing connection constraint column information.
 *
 * @author bpaulon
 */
public class ConstraintColumnInfo {
	private String owner;
	private String constraintName;
	private String tableName;
	private String columnName;
	private int position;
	
	public ConstraintColumnInfo(String owner, 
			String constraintName,
			String tableName,
			String columnName,
			int position)
	{
		this.owner = owner;
		this.constraintName = constraintName;
		this.tableName = tableName;
		this.columnName = columnName;
		this.position = position;
	}
	
	public String getOwner() {
		return owner;
	}
	public String getConstraintName() {
		return constraintName;
	}
	public String getTableName() {
		return tableName;
	}
	public String getColumnName() {
		return columnName;
	}
	public int getPosition() {
		return position;
	}
}
