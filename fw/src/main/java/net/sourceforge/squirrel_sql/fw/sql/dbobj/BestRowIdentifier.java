package net.sourceforge.squirrel_sql.fw.sql.dbobj;
/*
 * Copyright (C) 2004 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
/**
 * Describes one column in a set of columns that uniquely identifies a row in
 * a table.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class BestRowIdentifier extends DatabaseObjectInfo
{
   final private int _scope;
	final private String _colName;
	final private short _sqlDataType;
	final private String _typeName;
	final private int _precision;
	final private short _scale;
	final private short _pseudoColumn;

	/**
	 * Ctor specifying attributes.
	 */
	public BestRowIdentifier(String catalog, String schema, String tableName,
			int scope, String colName, short sqlDataType, String typeName,
			int precision, short scale, short pseudoColumn,
			SQLDatabaseMetaData md)
	{
		super(catalog, schema, tableName, DatabaseObjectType.FOREIGN_KEY, md);

		_scope = scope;
		_colName = colName;
		_sqlDataType = sqlDataType;
		_typeName = typeName;
		_precision = precision;
		_scale = scale;
		_pseudoColumn = pseudoColumn;
	}

	public int getScope()
	{
		return _scope;
	}

	public String getColumnName()
	{
		return _colName;
	}

	public short getSQLDataType()
	{
		return _sqlDataType;
	}

	public String getTypeName()
	{
		return _typeName;
	}

	public int getPrecision()
	{
		return _precision;
	}

	public short getScale()
	{
		return _scale;
	}

	public short getPseudoColumn()
	{
		return _pseudoColumn;
	}
}
