package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
/**
 * This class is an event fired whenever an object is added to or removed from
 * an <CODE>ObjectCache</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectCacheChangeEvent<E extends IHasIdentifier>
{
	/** The object added/removed. */
	private IHasIdentifier _obj;

	/**
	 * Ctor.
	 *
	 * @param   source	The <CODE>ObjectCache</CODE> that change has happened to.
	 * @param   obj		The object added/removed.
	 */
	public ObjectCacheChangeEvent(IHasIdentifier obj)
	{
		_obj = obj;
	}

	/**
	 * Return the object added/removed.
	 */
	public IHasIdentifier getObject()
	{
		return _obj;
	}

}