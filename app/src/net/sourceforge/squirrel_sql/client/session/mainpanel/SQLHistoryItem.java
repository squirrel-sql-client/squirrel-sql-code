package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2003-2004 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
/**
 * This JavaBean is the object stored in The <TT>SQLHistoryComboBox</TT>. It
 * represents an SQL query.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLHistoryItem implements Serializable, Cloneable
{
	/** The SQL. */
	private String _sql;

	/**
	 * Cleaned up vesion of the SQL. Appropriate for displaying in
	 * a combobox.
	 */
	private String _shortSql;

	/**
	 * Default ctor.
	 */
	public SQLHistoryItem()
	{
		this("");
	}

	/**
	 * Ctor specifying the SQL.
	 * 
	 * @param	sql		The SQL statement.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> SQL statement passed.
	 */
	public SQLHistoryItem(String sql)
	{
		super();
		if (sql == null)
		{
			throw new IllegalArgumentException("sql == null");
		}
		setSQL(sql);
	}

	/**
	 * Two objects of this class are considered equal if the SQL that they
	 * represent is equal.
	 * 
	 * @param	rhs		The object that this object is being compared to.
	 */
	public boolean equals(Object rhs)
	{
		boolean rc = false;
		if (this == rhs)
		{
			rc = true;
		}
		else if (rhs != null && rhs.getClass().equals(getClass()))
		{
			rc = ((SQLHistoryItem)rhs).getSQL().equals(getSQL());
		}
		return rc;
	}

	/**
	 * Return a copy of this object.
	 * 
	 * @return	The cloned object.
	 */
	public Object clone()
	{
		try
		{
			return (SQLHistoryItem)super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

	/**
	 * Retrieve a string representation of this object. A cleaned up version
	 * of the SQL is used.
	 * 
	 * @return	A string representation of this object.
	 */
	public String toString()
	{
		return _shortSql;
	}

	/**
	 * Retrieve the SQL.
	 * 
	 * @return		The SQL.
	 */
	public String getSQL()
	{
		return _sql;
	}

	/**
	 * Set the SQL.
	 * 
	 * @param	sql		The SQL statement.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> SQL statement passed.
	 */
	public void setSQL(String sql)
	{
		if (sql == null)
		{
			throw new IllegalArgumentException("sql == null");
		}

		_sql = sql.trim();
		_shortSql = StringUtilities.cleanString(sql);
	}
}
