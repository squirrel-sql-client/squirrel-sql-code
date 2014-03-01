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

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ProcedureInfo extends DatabaseObjectInfo implements IProcedureInfo
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ProcedureInfo.class);

	private interface IStrings
	{
		String DATABASE = s_stringMgr.getString("ProcedureInfo.database");
		String MAY_RETURN = s_stringMgr.getString("ProcedureInfo.mayreturn");
		String DOESNT_RETURN = s_stringMgr.getString("ProcedureInfo.doesntreturn");
		String DOES_RETURN = s_stringMgr.getString("ProcedureInfo.returns");
		String UNKNOWN = s_stringMgr.getString("ProcedureInfo.unknown");
	}

	/** Procedure Type. */
	private final int _procType;

	/** Procedure remarks. */
	private final String _remarks;

	public ProcedureInfo(String catalog, String schema, String simpleName,
							String remarks, int procType,
							SQLDatabaseMetaData md)
	{
		super(catalog, schema, simpleName, DatabaseObjectType.PROCEDURE, md);
		_remarks = remarks;
		_procType = procType;
	}

	public int getProcedureType()
	{
		return _procType;
	}

	public String getRemarks()
	{
		return _remarks;
	}

	public String getProcedureTypeDescription()
	{
		switch (_procType)
		{
			case DatabaseMetaData.procedureNoResult :
				return IStrings.DOESNT_RETURN;
			case DatabaseMetaData.procedureReturnsResult :
				return IStrings.DOES_RETURN;
			case DatabaseMetaData.procedureResultUnknown :
				return IStrings.MAY_RETURN;
			default :
				return IStrings.UNKNOWN;
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
