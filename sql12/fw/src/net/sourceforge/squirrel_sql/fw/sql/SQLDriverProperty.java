package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002-2003 Colin Bell.
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
 * This represents a property that can be specified when connecting to the database.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLDriverProperty implements Cloneable, Serializable
{
	/** Name. */
	private String _key;

	/** Value associated with the name. */
	private String _value;

	/**
	 * Default ctor. Created with the name and value being <TT>null</TT>.
	 */
	public SQLDriverProperty()
	{
		super();
	}

	/**
	 * ctor specifying the name and value.
	 *
	 * @param	name		The name
	 * @param	value	The value associated with the name.
	 */
	public SQLDriverProperty(String name, String value)
	{
		super();
		_key = name;
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
	 * Retrieve the name.
	 *
	 * @return	The name.
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
	 * Set the name.
	 *
	 * @param	name	The name.
	 */
	public void setKey(String name)
	{
		_key = name;
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
