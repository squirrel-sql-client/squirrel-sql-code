package net.sourceforge.squirrel_sql.fw.sql;
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
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
/**
 * This <CODE>ICommand</CODE> allows the user to connect to
 * an <TT>ISQLAlias</TT>.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class WrappedSQLException extends BaseException
{
	static final long serialVersionUID = 8923509127367847605L;

    /**
	 * Ctor specifying the <TT>SQLException</TT> that this exception
	 * is wrapped around.
	 * 
	 * @param	ex		<TT>SQLException</TT> this exception is wrapped around.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>SQLException</TT> passed.
	 */
	public WrappedSQLException(SQLException ex)
	{
		super(checkParams(ex));
	}

	/**
	 * Retrieve the <TT>SQLException</TT> that this exception is
	 * wrapped around.
	 * 
	 * @return	The <TT>SQLException</TT>.
	 */
	public SQLException getSQLExeption()
	{
		return (SQLException)getWrappedThrowable();
	}

	private static SQLException checkParams(SQLException ex)
	{
		if (ex == null)
		{
			throw new IllegalArgumentException("SQLException == null");
		}
		return ex;
	}
}
