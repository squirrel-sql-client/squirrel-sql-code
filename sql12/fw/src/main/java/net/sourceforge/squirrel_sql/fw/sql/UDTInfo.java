package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2002 Colin Bell
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

public class UDTInfo extends DatabaseObjectInfo implements IUDTInfo
{
    /** Java class name. */
	private final String _javaClassName;

	/** UDT Data Type. */
	private final String _dataType;

	/** UDT remarks. */
	private final String _remarks;

	UDTInfo(String catalog, String schema, String simpleName, String javaClassName,
			String dataType, String remarks, SQLDatabaseMetaData md)
	{
		super(catalog, schema, simpleName, DatabaseObjectType.UDT, md);
		_javaClassName = javaClassName;
		_dataType = dataType;
		_remarks = remarks;
	}

	public String getJavaClassName()
	{
		return _javaClassName;
	}

	public String getDataType()
	{
		return _dataType;
	}

	public String getRemarks()
	{
		return _remarks;
	}

	public boolean equals(Object obj)
	{
		if (super.equals(obj) && obj instanceof UDTInfo)
		{
			UDTInfo info = (UDTInfo) obj;
			if ((info._dataType == null && _dataType == null)
				|| ((info._dataType != null && _dataType != null)
					&& info._dataType.equals(_dataType)))
			{
				if ((info._javaClassName == null && _javaClassName == null)
					|| ((info._javaClassName != null && _javaClassName != null)
						&& info._javaClassName.equals(_javaClassName)))
				{
					return (
						(info._remarks == null && _remarks == null)
							|| ((info._remarks != null && _remarks != null)
								&& info._remarks.equals(_remarks)));
				}
			}
		}
		return false;
	}
}
