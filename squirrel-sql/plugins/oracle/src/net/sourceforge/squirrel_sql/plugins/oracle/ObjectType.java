package net.sourceforge.squirrel_sql.plugins.oracle;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
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

/**
 * This class describes one of the new Oracle object types.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class ObjectType
{
	/**
	 * This is the data in the OBJECT_TYPE column in the SYS.ALL_OBJECTS table
	 * that corresponds to this object type.
	 */
	final String _objectTypeColumnData;

	/**
	 * This is the database object type for objects of this object type.
	 */
	final DatabaseObjectType _dboType;

	/**
	 * Ctor.
	 * 
	 * @param	objectTypeColumnData	data in the OBJECT_TYPE column in the
	 *									SYS.ALL_OBJECTS table that corresponds
	 *									to this object type.
	 * @param	dboType					Database object type to use for nodes of
	 *									this object type.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>objectTypeColumnData</TT> or
	 * 			<TT>DatabaseObjectType</TT> passed.
	 */
	ObjectType(String objectTypeColumnData, DatabaseObjectType dboType)
	{
		super();
		_objectTypeColumnData = objectTypeColumnData;
		_dboType = dboType;
	}
}
