package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.util.EventObject;
/**
 * This class is an event fired for object tree events.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeListenerEvent extends EventObject
{
	/** The <CODE>ObjectTree</CODE> involved. */
	private ObjectTree _tree;

	/**
	 * Ctor.
	 *
	 * @param	source	The <CODE>ObjectTree</CODE> that change has happened to.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT>ObjectTree/TT> passed.
	 */
	ObjectTreeListenerEvent(ObjectTree source)
	{
		super(checkParams(source));
		_tree = source;
	}

	/**
	 * Return the <CODE>ObjectTree</CODE>.
	 */
	public ObjectTree getObjectTree()
	{
		return _tree;
	}

	private static ObjectTree checkParams(ObjectTree source)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("ObjectTree == null");
		}
		return source;
	}
}