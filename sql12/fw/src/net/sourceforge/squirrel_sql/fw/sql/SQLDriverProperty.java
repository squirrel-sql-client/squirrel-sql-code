package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002 Colin Bell.
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
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
/**
 * This represents a propert that can be specified when connecting to the database.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLDriverProperty implements Cloneable, Serializable
{
	/** Key. */
	private String _key;

	/** Value associated with the key. */
	private String _value;

	/**
	 * Default ctor. Created with the key and value being <TT>null</TT>.
	 */
	public SQLDriverProperty()
	{
		super();
	}

	/**
	 * ctor specifying the key and value.
	 *
	 * @param	key		The key
	 * @param	value	The value associated with the key.
	 */
	public SQLDriverProperty(String key, String value)
	{
		super();
		_key = key;
		_value = value;
	}

	/**
	 * Return a clone of this object.
	 *
	 * @return	The cloned object.
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

	/**
	 * Retrieve the key.
	 *
	 * @return	The key.
	 */
	public String getKey()
	{
		return _key;
	}

	/**
	 * Retrieve the value.
	 *
	 * @return	The value.
	 */
	public String getValue()
	{
		return _value;
	}

	/**
	 * Set the key.
	 *
	 * @param	key		The key.
	 */
	public void setKey(String key)
	{
		_key = key;
	}

	/**
	 * Set the value.
	 *
	 * @param	value	The value.
	 */
	public void setValue(String value)
	{
		_value = value;
	}
}
