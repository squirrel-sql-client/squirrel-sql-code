/*
 * Copyright (C) 2008 Michael Romankiewicz
 * mirommail(at)web.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.smarttools;

public class STDataType {
	public static int USE_WHOLE_GROUP = -1;
	public static int GROUP_NULL = 0;
	public static int GROUP_INT = 1;
	public static int GROUP_NUMERIC = 2;
	public static int GROUP_CHAR = 3;
	public static int GROUP_DATE = 4;
	
	private int jdbcType;
	private String jdbcTypeName;
	private int group;
	
	public STDataType(int jdbcType, String jdbcTypeName, int group) {
		this.jdbcType = jdbcType;
		this.jdbcTypeName = jdbcTypeName;
		this.setGroup(group);
	}
	
	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}
	public int getJdbcType() {
		return jdbcType;
	}
	
	public void setJdbcTypeName(String jdbcTypeName) {
		this.jdbcTypeName = jdbcTypeName;
	}
	public String getJdbcTypeName() {
		return jdbcTypeName;
	}
	
	public void setGroup(int group) {
		this.group = group;
	}
	public int getGroup() {
		return group;
	}

	@Override
	public String toString() {
		return getJdbcTypeName();
	}
	
}
