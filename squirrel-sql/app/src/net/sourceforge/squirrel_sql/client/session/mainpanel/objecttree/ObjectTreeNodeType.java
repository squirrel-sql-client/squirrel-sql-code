package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
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
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
/**
 * This defines the type of a node in the object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeNodeType implements IHasIdentifier
{
	private final DatabaseObjectType _dbobjType;

	private static Map s_nodeTypes = new HashMap();

	/** Factory to generate unique IDs for these objects. */
	private final static IntegerIdentifierFactory s_idFactory = new IntegerIdentifierFactory();

	/** Uniquely identifies this Object. */
	private final IIdentifier _id;

	private ObjectTreeNodeType(DatabaseObjectType dbobjType)
	{
		super();
		if (dbobjType == null)
		{
			throw new IllegalArgumentException("DatabaseObjectType == null");
		}
		_dbobjType = dbobjType;
		_id = s_idFactory.createIdentifier();
	}

	/**
	 * Return the object that uniquely identifies this object.
	 * 
	 * @return	Unique ID.
	 */
	public IIdentifier getIdentifier()
	{
		return _id;
	}

	public DatabaseObjectType getDatabaseObjectType()
	{
		return _dbobjType;
	}

	public static synchronized ObjectTreeNodeType get(DatabaseObjectType dboType)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("ObjectTreeNodeType == ");
		}
		ObjectTreeNodeType nodeType =
			(ObjectTreeNodeType) s_nodeTypes.get(dboType.getIdentifier());
		if (nodeType == null)
		{
			nodeType = new ObjectTreeNodeType(dboType);
			s_nodeTypes.put(dboType.getIdentifier(), nodeType);
		}
		return nodeType;
	}
}
