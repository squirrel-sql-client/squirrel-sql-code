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
import java.io.Serializable;
import java.sql.DriverPropertyInfo;
/**
 * This represents a property that can be specified when connecting to the database.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLDriverProperty implements Cloneable, Serializable
{
	/** Property names for this bean. */
	public interface IPropertyNames
	{
		/** Property Key. */
		String KEY = "key";

		/** Property value. */
		String VALUE = "value";

		/** Is specified. */
		String IS_SPECIFIED = "isSpecified";
	}

	/** Name. */
	private String _key;

	/** Value associated with the name. */
	private String _value;

	/** If <TT>true</TT> then this property is to be used. */
	private boolean _isSpecified;

	private transient DriverPropertyInfo _driverPropInfo;

	/**
	 * Default ctor. Created with the name and value being <TT>null</TT>.
	 */
	public SQLDriverProperty()
	{
		super();
	}

	/**
	 * Create from a <TT>DriverPropertyInfo</TT> object.
	 */
	public SQLDriverProperty(DriverPropertyInfo value)
	{
		super();
		if (value == null)
		{
			throw new IllegalArgumentException("DriverPropertyInfo == null");
		}
	
		setKey(value.name);
		setValue(value.value);
		setDriverPropertyInfo(value);	
	}

	/**
	 * ctor specifying the name and value.
	 *
	 * @param	name	The name
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

	public boolean isSpecified()
	{
		return _isSpecified;
	}

	public DriverPropertyInfo getDriverPropertyInfo()
	{
		return _driverPropInfo;
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

	public void setIsSpecified(boolean value)
	{
		_isSpecified = value;
	}

	public void setDriverPropertyInfo(DriverPropertyInfo value)
	{
		if (value != null)
		{
			if (!value.name.equals(getKey()))
			{
				throw new IllegalArgumentException("DriverPropertyInfo.name != my name");
			}
		}
		_driverPropInfo = value;
	}
}

