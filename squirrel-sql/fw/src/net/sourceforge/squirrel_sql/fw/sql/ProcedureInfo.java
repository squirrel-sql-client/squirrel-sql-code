package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class ProcedureInfo extends DatabaseObjectInfo implements IProcedureInfo
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String DATABASE = "Database";
		//String NO_CATALOG = "No Catalog"; // i18n or Replace with md.getCatalogueTerm.
		String MAY_RETURN = "May return a result";
		String DOESNT_RETURN = "Does not return a result";
		String DOES_RETURN = "Returns a result";
		String UNKNOWN = "Unknown";
	}

	/** Procedure Type. */
	private final int _procType;

	/** Procedure remarks. */
	private final String _remarks;

	ProcedureInfo(String catalog, String schema, String simpleName,
							String remarks, int procType,
							SQLDatabaseMetaData md) throws SQLException
	{
		super(catalog, schema, simpleName, DatabaseObjectType.PROCEDURE, md);
		_remarks = remarks;
		_procType = procType;
	}

//TODO: Rename to getProcedureType().
	public int getType()
	{
		return _procType;
	}

	public String getRemarks()
	{
		return _remarks;
	}

//TODO: Rename to getProcedureTypeDescription().
	public String getTypeDescription()
	{
		switch (_procType)
		{
			case DatabaseMetaData.procedureNoResult :
				return i18n.DOESNT_RETURN;
			case DatabaseMetaData.procedureReturnsResult :
				return i18n.DOES_RETURN;
			case DatabaseMetaData.procedureResultUnknown :
				return i18n.MAY_RETURN;
			default :
				return i18n.UNKNOWN;
		}
	}

	public boolean equals(Object obj)
	{
		if (super.equals(obj) && obj instanceof ProcedureInfo)
		{
			ProcedureInfo info = (ProcedureInfo) obj;
			if ((info._remarks == null && _remarks == null)
				|| ((info._remarks != null && _remarks != null)
					&& info._remarks.equals(_remarks)))
			{
				return info._procType == _procType;
			}
		}
		return false;
	}

}
