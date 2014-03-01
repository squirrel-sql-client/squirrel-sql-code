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
import java.util.Date;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
/**
 * This JavaBean is the object stored in The <TT>SQLHistoryComboBox</TT>. It
 * represents an SQL query.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLHistoryItem implements Serializable, Cloneable
{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;


    /** The SQL. */
	private String _sql;


   private java.util.Date _lastUsageTime;

   /**
	 * Cleaned up vesion of the SQL. Appropriate for displaying in
	 * a combobox.
	 */
	private String _shortSql;
   private String _aliasName;

   /**
	 * Default ctor.
	 */
	public SQLHistoryItem()
	{
		this("", "");
	}

	/**
	 * Ctor specifying the SQL.
	 * 
	 * @param	sql		The SQL statement.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> SQL statement passed.
	 */
	public SQLHistoryItem(String sql, String aliasName)
	{
		super();
      if (sql == null)
		{
			throw new IllegalArgumentException("sql == null");
		}

      _aliasName = aliasName;

      if(0 < sql.length())
      {
         _lastUsageTime = new Date();
      }

      setSQL(sql);
	}

	/**
	 * Two objects of this class are considered equal if the SQL that they
	 * represent is equal.
	 * 
	 * @param	rhs		The object that this object is being compared to.
	 */
    @Override
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

    @Override
    public int hashCode() {
        return getSQL().hashCode();
    }
    
	/**
	 * Return a copy of this object.
	 * 
	 * @return	The cloned object.
	 */
    @Override
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
	 * Retrieve a string representation of this object. A cleaned up version
	 * of the SQL is used.
	 * 
	 * @return	A string representation of this object.
	 */
    @Override
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

   public Date getLastUsageTime()
   {
      return _lastUsageTime;
   }

   public void setLastUsageTime(Date _creationTime)
   {
      this._lastUsageTime = _creationTime;
   }


   public String getAliasName()
   {
      return _aliasName;
   }

   public void setAliasName(String _aliasName)
   {
      this._aliasName = _aliasName;
   }
}
