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
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
/**
 * This exception is thrown if an attempt is made to add an object
 * to a <CODE>IObjectCache</CODE> and an object for the same class and with the
 * same ID is already in the cache.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DuplicateObjectException extends BaseException
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DuplicateObjectException.class);

	/** Object that couldn't be added to the cache. */
	private IHasIdentifier _obj;

	/**
	 * Ctor.
	 *
	 * @param	obj	 The object that we tried to add into the cache.
	 */
	public DuplicateObjectException(IHasIdentifier obj)
	{
		super(generateMessage(obj));
	}

	/**
	 * Return the object that couldn't be added to the cache.
	 */
	public IHasIdentifier getObject()
	{
		return _obj;
	}

	/**
	 * Generate error message. Help function for ctor.
	 */
	private static String generateMessage(IHasIdentifier obj)
	{
		final Object[] args =
		{
			obj.getClass().getName(), obj.getIdentifier().toString()
		};
		return s_stringMgr.getString("DuplicateObjectException.msg", args);
	}
}
