package net.sourceforge.squirrel_sql.client.session.mainpanel;
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
import java.io.Serializable;
/**
 * This JavaBean is the object stored in The <TT>SQLHistoryComboBox</TT>. It
 * represents an SQL query.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLHistoryItem implements Serializable
{
	/** The SQL. */
	private String _sql;

	/** The first line of the SQL. Appropriate for displaying in a combobox. */
	private String _firstLine;

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
	 * Retrieve a string representation of this object. The first line of the
	 * SQL is used.
	 * 
	 * @return	A string representation of this object.
	 */
	public String toString()
	{
		return _firstLine;
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
		_sql = sql.trim();
		_firstLine = getFirstLine(sql);
	}

	private String getFirstLine(String sql)
	{
		int idx1 = sql.indexOf('\n');
		int idx2 = sql.indexOf('\r');
		if (idx1 == -1)
		{
			idx1 = idx2;
		}
		if (idx2 != -1 && idx2 < idx1)
		{
			idx1 = idx2;
		}
		sql = idx1 == -1 ? sql : sql.substring(0, idx1);
		return sql.replace('\t', ' ');
	}
}
