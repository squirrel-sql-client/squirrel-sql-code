package net.sourceforge.squirrel_sql.plugins.sessionscript;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

/**
 * An SQL script run when a session is started.
 * 
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasScript implements Serializable, IHasIdentifier
{
	/**
	 * The <TT>IIdentifier</TT> that uniquely identifies this object. This is
	 * actually the identifier of the <TT>ISQLALias</TT> that this script is
	 * for.
	 */
	private IIdentifier _id;

	/** The SQL. */
	private String _sql;

	/**
	 * Default ctor. Should only be used by to/from XML code.
	 */
	public AliasScript()
	{
		super();
	}

	/**
	 * Ctor specifying the <TT>ISQLAlias</TT>.
	 * 
	 * @param	alias	<TT>ISQLAlias</TT> we have creatign this script for.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public AliasScript(ISQLAlias alias)
	{
		super();

		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == null");
		}

		_id = alias.getIdentifier();
	}

	/**
	 * Returns <TT>true</TT> if this objects is equal to the passed one. Two
	 * <TT>AliasScript</TT> objects are considered equal if they have the same
	 * identifier.
	 */
	public boolean equals(Object rhs)
	{
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass()))
		{
			rc = ((AliasScript) rhs).getIdentifier().equals(getIdentifier());
		}
		return rc;
	}

	/**
	 * Returns a hash code value for this object.
	 */
	public int hashCode()
	{
		return getIdentifier().hashCode();
	}

	/**
	 * Return the SQL as a string representaion of this object.
	 * 
	 * @return	The SQL as a string representation of this object.
	 */
	public String toString()
	{
		return _sql != null ? _sql : "";
	}

	/**
	 * Return the identifier that uniquely identifies this object.
	 * 
	 * @return	the identifier that uniquely identifies this object.
	 */
	public IIdentifier getIdentifier()
	{
		return _id;
	}

	/**
	 * Return the SQL to be run.
	 * 
	 * @return	the SQL to be run.
	 */
	public String getSQL()
	{
		return _sql;
	}

	/**
	 * Set the identifier that uniquely identifies this object. This should
	 * be the ID of the <TT>ISQLALais</TT> that this script is for.
	 * 
	 * @param	the identifier that uniquely identifies this object.
	 */
	public void setIdentifier(IIdentifier id)
	{
		_id = id;
	}

	/**
	 * Set the SQL to be run.
	 * 
	 * @param	value	The SQL.
	 */
	public void setSQL(String value)
	{
		_sql = value;
	}
}
