package net.sourceforge.squirrel_sql.plugins.oracle.expander;
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
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

/**
 * This class describes one of the new Oracle object types.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectType
{
	/**
	 * This is the database object type for objects of this object type.
	 */
	public final DatabaseObjectType _dboType;

	/**
	 * This is the data in the OBJECT_TYPE column in the SYS.USER_OBJECTS table
	 * that corresponds to the child object for this object type. E.G. If the
	 * object type is Package group then this data would be PACKAGE.
	 */
	public final String _objectTypeColumnData;

	/**
	 * This is the database object type for child nodes.
	 */
	public final DatabaseObjectType _childDboType;

	/**
	 * Ctor.
	 * 
	 * @param	dboType					Database object type to use for nodes of
	 *									this object type.
	 * @param	objectTypeColumnData	data in the OBJECT_TYPE column in the
	 *									SYS.USER_OBJECTS table that corresponds
	 *									to this object types children.
	 * @param	childDboType			Database object type to use for child
	 *									nodes.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>objectTypeColumnData</TT> or
	 * 			<TT>DatabaseObjectType</TT> passed.
	 */
	public ObjectType(DatabaseObjectType dboType, String objectTypeColumnData,
				DatabaseObjectType childDboType)
	{
		super();
		_dboType = dboType;
		_objectTypeColumnData = objectTypeColumnData;
		_childDboType = childDboType;
	}
}
