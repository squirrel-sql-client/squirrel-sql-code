/*
 * Copyright (C) 2003 Greg Mackness
 * gmackness@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

/**
 * @author <A HREF="mailto:gmackness@users.sourceforge.net">Greg Mackness</A> 
 */
public class ObjectTreeTransferSelection {

	// a map of collections of DatabaseObjectInfo objects keyed by DatabaseObjectType
	private HashMap objectTypeCollections;

	public ObjectTreeTransferSelection() {
		super();
		objectTypeCollections = new HashMap();
	}
	
	/**
	 * Adds a DatabaseObjectInfo to the collection.
	 * @param dbObject The object to add.
	 */
	public void addDatabaseObject(IDatabaseObjectInfo dbObject) {
		Object ref = objectTypeCollections.get(dbObject.getDatabaseObjectType());
		if(ref==null) {
			ref = new Vector();
			objectTypeCollections.put(dbObject.getDatabaseObjectType(),ref);
		}
		Collection typeCollection = (Collection)ref;
		typeCollection.add(dbObject);
	}
	
	/**
	 * Returns a collection of DatabaseObjectInfo objects of the requested DatabaseObjectType.
	 * @param ofType The type of collection to return.
	 * @return A collection of objects of the supplied type.
	 */
	public Collection getDatabaseObjects(DatabaseObjectType ofType) {
		Object ref = objectTypeCollections.get(ofType);
		if(ref==null) ref = new Vector();
		return (Collection)ref;
	}
}
