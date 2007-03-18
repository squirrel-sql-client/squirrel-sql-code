package net.sourceforge.squirrel_sql.fw.sql;

import java.io.Serializable;

/*
* Copyright (C) 2002 Colin Bell
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
public class ForeignKeyColumnInfo implements Serializable
{
	static final long serialVersionUID = -2645123423172494012L;
    
    private final String _fkColumnName;
	private final String _pkColumnName;
	private final int _keySeq;

	public ForeignKeyColumnInfo(String fkColumnName, String pkColumnName,
									int keySeq)
	{
		super();
		_fkColumnName = fkColumnName;
		_pkColumnName = pkColumnName;
		_keySeq = keySeq;
	}

	public String getPrimaryKeyColumnName()
	{
		return _pkColumnName;
	}

	public String getForeignKeyColumnName()
	{
		return _fkColumnName;
	}

	public int getKeySequence()
	{
		return _keySeq;
	}
}
