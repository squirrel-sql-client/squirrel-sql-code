package net.sourceforge.squirrel_sql.fw.sql;
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
import java.sql.SQLException;

public class ForeignKeyInfo extends DatabaseObjectInfo
{
	private final String _pkCatalog;
	private final String _pkSchema;
	private final String _pkTableName;
	private final String _fkTableName;
	private final int _updateRule;
	private final int _deleteRule;
	private final String _pkName;
	private final int _deferability; 
	private ForeignKeyColumnInfo[] _columnInfo;

	ForeignKeyInfo(String pkCatalog, String pkSchema, String pkTableName,
					String fkCatalog, String fkSchema, String fkTableName,
					int updateRule, int deleteRule, String fkName,
					String pkName, int deferability,
					ForeignKeyColumnInfo[] columnInfo, SQLDatabaseMetaData md)
	throws SQLException
	{
		super(fkCatalog, fkSchema, fkName, DatabaseObjectType.FOREIGN_KEY, md);
		_pkCatalog = pkCatalog;
		_pkSchema = pkSchema;
		_pkTableName = pkTableName;
		_fkTableName = fkTableName;
		_updateRule = updateRule;
		_deleteRule = deleteRule;
		_pkName = pkName;
		_deferability = deferability;
		setForeignKeyColumnInfo(columnInfo);
	}

	public String getPrimaryKeyCatalogName()
	{
		return _pkCatalog;
	}

	public String getPrimaryKeySchemaName()
	{
		return _pkSchema;
	}

	public String getPrimaryKeyTableName()
	{
		return _pkTableName;
	}

	public String getPrimaryKeyName()
	{
		return _pkName;
	}

	public String getForeignKeyCatalogName()
	{
		return getCatalogName();
	}

	public String getForeignKeySchemaName()
	{
		return getCatalogName();
	}

	public String getForeignKeyTableName()
	{
		return _fkTableName;
	}

	public String getForeignKeyName()
	{
		return getSimpleName();
	}

	public int getUpdateRule()
	{
		return _updateRule;
	}

	public int getDeleteRule()
	{
		return _deleteRule;
	}

	public int getDeferability()
	{
		return _deferability;
	}

	public ForeignKeyColumnInfo[] getForeignKeyColumnInfo()
	{
		return _columnInfo;
	}

	public void setForeignKeyColumnInfo(ForeignKeyColumnInfo[] value)
	{
		_columnInfo = value != null ? value : new ForeignKeyColumnInfo[0];
	}
}
