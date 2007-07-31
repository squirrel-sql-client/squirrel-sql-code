package net.sourceforge.squirrel_sql.client.db;
/*
 * Copyright (C) 2002-2006 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * Objects of this class hold a collection of <TT>ISQLAlias</TT> objects.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasGroup implements Cloneable, Serializable, Comparable<AliasGroup>
{
    private static final long serialVersionUID = 1L;

    /** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AliasGroup.class);

	/**
	 * JavaBean property names for this class.
	 */
	public interface IPropertyNames
	{
		String ID = "identifier";
		String NAME = "name";
	}

	/** The <CODE>IIdentifier</CODE> that uniquely identifies this object. */
	private IIdentifier _id;

	/** Name that describes this object. */
	private String _name;

	/** Object to handle property change events. */
	private transient PropertyChangeReporter _propChgReporter;

	/**
	 * Default ctor.
	 */
	public AliasGroup()
	{
		super();
		_name = "";
	}

	/**
	 * Returns <CODE>true</CODE> if this object is valid.<P>
	 * Implementation for <CODE>IPersistable</CODE>.
	 */
	public synchronized boolean isValid()
	{
		return _name.length() > 0;
	}

	/**
	 * Returns <TT>true</TT> if this objects is equal to the passed one. Two
	 * <TT>AliasGroup</TT> objects are considered equal if they have the same
	 * identifier.
	 */
	public boolean equals(Object rhs)
	{
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass()))
		{
			rc = ((AliasGroup)rhs).getIdentifier().equals(getIdentifier());
		}
		return rc;
	}

	/**
	 * Return a clone of this object.
	 */
	public Object clone()
	{
		try
		{
			final AliasGroup obj = (AliasGroup)super.clone();
			obj._propChgReporter = null;
			return obj;
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

	/**
	 * Returns a hash code value for this object.
	 */
	public synchronized int hashCode()
	{
		return getIdentifier().hashCode();
	}

	/**
	 * Compare this <TT>SQLAlias</TT> to another object. If the passed object
	 * is a <TT>AliasGroup</TT>, then the <TT>getName()</TT> functions of the two
	 * objects are used to compare them. Otherwise, it throws a ClassCastException
	 */
	public int compareTo(AliasGroup rhs)
	{
		return _name.compareTo((rhs).getName());
	}

	/**
	 * Retrieve the unique identifier for this object.
	 *
	 * @return	Unique ID.
	 */
	public IIdentifier getIdentifier()
	{
		return _id;
	}

	public void setIdentifier(IIdentifier id)
	{
		_id = id;
	}

	/**
	 * Retrieve the name that describes this object.
	 *
	 * @return	This objects name.
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * Set the name that describes this object.
	 *
	 * @param	value	This objects new name.
	 */
	public void setName(String value)
		throws ValidationException
	{
		String data = getString(value);
		if (data.length() == 0)
		{
			throw new ValidationException(s_stringMgr.getString("AliasGroup.error.blankname"));
		}
		if (!_name.equals(data))
		{
			final String oldValue = _name;
			_name = data;
            getPropertyChangeReporter().firePropertyChange(IPropertyNames.NAME,
												           oldValue, 
                                                           _name);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().removePropertyChangeListener(listener);
	}

	private synchronized PropertyChangeReporter getPropertyChangeReporter()
	{
		if (_propChgReporter == null)
		{
			_propChgReporter = new PropertyChangeReporter(this);
		}
		return _propChgReporter;
	}

	private String getString(String data)
	{
		return data != null ? data.trim() : "";
	}
}
