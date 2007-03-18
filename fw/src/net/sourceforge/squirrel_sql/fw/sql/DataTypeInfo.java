package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
// TODO: Put in all the property accessors
public class DataTypeInfo extends DatabaseObjectInfo
{
	static final long serialVersionUID = -3501323961506084527L;
    
    private final int _dataType;
	private final int _precision;
	private final String _literalPrefix;
	private final String _literalSuffix;
	private final String _createParams;
	private final int _nullable;
	private final boolean _caseSensitive;
	private final int _searchable;
	private final boolean _unsigned;
	private final boolean _money;
	private final boolean _autoIncrement;
	private final String _localTypeName;
	private final int _minScale;
	private final int _maxScale;
	private final int _numPrecRadix;

	DataTypeInfo(String typeName, int dataType, int precision,
					String literalPrefix, String literalSuffix,
					String createParams, int nullable, boolean caseSensitive,
					int searchable, boolean unsigned, boolean money,
					boolean autoIncrement, String localTypeName,
					int minScale, int maxScale, int numPrecRadix,
					SQLDatabaseMetaData md)
	{
		super(null, null, typeName, DatabaseObjectType.DATATYPE, md);
		_dataType = dataType;
		_precision = precision;
		_literalPrefix = literalPrefix;
		_literalSuffix = literalSuffix;
		_createParams = createParams;
		_nullable = nullable;
		_caseSensitive = caseSensitive;
		_searchable = searchable;
		_unsigned = unsigned;
		_money = money;
		_autoIncrement = autoIncrement;
		_localTypeName = localTypeName;
		_minScale = minScale;
		_maxScale = maxScale;
		_numPrecRadix = numPrecRadix;
	}

	public int getDataType()
	{
		return _dataType;
	}

	public int getPrecision()
	{
		return _precision;
	}

	public String getLiteralPrefix()
	{
		return _literalPrefix;
	}

	public String getLiteralSuffix()
	{
		return _literalSuffix;
	}

	public String getCreateParams()
	{
		return _createParams;
	}

	public boolean equals(Object obj)
	{
		if (super.equals(obj) && obj instanceof DataTypeInfo)
		{
			DataTypeInfo info = (DataTypeInfo)obj;
			final String name = getSimpleName();
			final String rhsName = info.getSimpleName();
			if ((rhsName == null && name == null)
				|| (name != null && name.equals(rhsName)))
			{
				return true;
			}
		}
		return false;
	}
}
