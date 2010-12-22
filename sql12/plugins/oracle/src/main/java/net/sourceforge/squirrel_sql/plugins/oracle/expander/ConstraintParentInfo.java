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
package net.sourceforge.squirrel_sql.plugins.oracle.expander;

/**
 * Stores information about an Oracle Constraint parent
 * 
 * @author bpaulon
 */
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IObjectTypes;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

public class ConstraintParentInfo extends DatabaseObjectInfo
{
	private static final long serialVersionUID = 1L;
	
	private final IDatabaseObjectInfo _tableInfo;

	public ConstraintParentInfo(IDatabaseObjectInfo tableInfo, String schema,
                               SQLDatabaseMetaData md, IObjectTypes objectTypes)
	{
		super(null, schema, "CONSTRAINT", objectTypes.getConstraintParent(), md);
		_tableInfo = tableInfo;
	}

	public IDatabaseObjectInfo getTableInfo()
	{
		return _tableInfo;
	}
}
