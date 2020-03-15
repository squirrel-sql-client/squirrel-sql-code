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

import net.sourceforge.squirrel_sql.fw.util.Utilities;
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
		/** Property Name. */
		String NAME = "name";

		/** Property value. */
		String VALUE = "value";

		/** Is specified. */
		String IS_SPECIFIED = "isSpecified";
	}

    /** Name. */
	private String _name;

	/** Value associated with the name. */
	private String _value;

	/** If <TT>true</TT> then this property is to be used. */
	private boolean _isSpecified;

	private transient DriverPropertyInfo _driverPropInfo;

	/**
	 * Needed for XML serialization and deserialization.
	 */
	public SQLDriverProperty()
	{
	}

	/**
	 * Create from a <TT>DriverPropertyInfo</TT> object.
	 */
	public SQLDriverProperty(DriverPropertyInfo parm)
	{
		if (parm == null)
		{
			throw new IllegalArgumentException("DriverPropertyInfo == null");
		}
	
		setName(parm.name);
		setValue(parm.value);
		setDriverPropertyInfo(parm);	
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
	public String getName()
	{
		return _name;
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
	public synchronized void setName(String name)
	{
		_name = name;
		if (_driverPropInfo != null)
		{
			_driverPropInfo.name = name;
		}
	}

	/**
	 * Set the value.
	 *
	 * @param	value	The value.
	 */
	public synchronized void setValue(String value)
	{
		_value = value;
		if (_driverPropInfo != null)
		{
			_driverPropInfo.value = value;
		}
	}

	public void setIsSpecified(boolean value)
	{
		_isSpecified = value;
	}

	public void setDriverPropertyInfo(DriverPropertyInfo parm)
	{
		Utilities.checkNull("setDriverPropertyInfo", "parm", parm);
		if (parm != null)
		{
			if (!parm.name.equals(getName()))
			{
				throw new IllegalArgumentException("DriverPropertyInfo.name != my name");
			}
		}
		_driverPropInfo = parm;
		_driverPropInfo.value = _value;
	}
}

