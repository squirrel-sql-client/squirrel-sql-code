/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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

package net.sourceforge.squirrel_sql.plugins.dbdiff.util;

public class AbstractDifference
{

	public static enum DiffType
	{
		TABLE_COLUMN_COUNT, TABLE_PK_NAME, TABLE_PK_COLUMN, TABLE_FK_NAME, TABLE_FK_COLUMN, TABLE_FK_COUNT, TABLE_INDEX_NAME, TABLE_INDEX_COLUMNS, TABLE_INDEX_UNIQUENESS, COLUMN_TYPE, COLUMN_LENGTH, COLUMN_PRECISION, COLUMN_SCALE, COLUMN_DEFAULT_VALUE, COLUMN_NULLABILITY, COLUMN_COMMENT
	}

	/** Catalog of the first table / column being compared */
	protected String _catalog1;

	/** Schema of the first table / column being compared */
	protected String _schema1;

	/** Table name of the first table / column being compared */
	protected String _tableName1;

	/** Catalog of the second table / column being compared */
	protected String _catalog2;

	/** Schema of the second table / column being compared */
	protected String _schema2;

	/** Table name of the second table / column being compared */
	protected String _tableName2;

	/** The type of difference being reported */
	protected DiffType _differenceType;

	/** The value that is different with the first item */
	protected Object _differenceVal1;

	/** The value that is different with the second item */
	protected Object _differenceVal2;

	/**
	 * @param _differenceType
	 *           the _differenceType to set
	 */
	public void setDifferenceType(DiffType _differenceType)
	{
		this._differenceType = _differenceType;
	}

	/**
	 * @return the _differenceType
	 */
	public DiffType getDifferenceType()
	{
		return _differenceType;
	}

	/**
	 * @param _tableName
	 *           the _tableName to set
	 */
	public void setTableName1(String _tableName)
	{
		this._tableName1 = _tableName;
	}

	/**
	 * @return the _tableName
	 */
	public String getTableName1()
	{
		return _tableName1;
	}

	/**
	 * @param _schema
	 *           the _schema to set
	 */
	public void setSchema1(String _schema)
	{
		this._schema1 = _schema;
	}

	/**
	 * @return the _schema
	 */
	public String getSchema1()
	{
		return _schema1;
	}

	/**
	 * @param _catalog1
	 *           the _catalog1 to set
	 */
	public void setCatalog1(String _catalog1)
	{
		this._catalog1 = _catalog1;
	}

	/**
	 * @return the _catalog1
	 */
	public String getCatalog1()
	{
		return _catalog1;
	}

	/**
	 * @param _catalog2
	 *           the _catalog2 to set
	 */
	public void setCatalog2(String _catalog2)
	{
		this._catalog2 = _catalog2;
	}

	/**
	 * @return the _catalog2
	 */
	public String getCatalog2()
	{
		return _catalog2;
	}

	/**
	 * @param _schema2
	 *           the _schema2 to set
	 */
	public void setSchema2(String _schema2)
	{
		this._schema2 = _schema2;
	}

	/**
	 * @return the _schema2
	 */
	public String getSchema2()
	{
		return _schema2;
	}

	/**
	 * @param _tableName2
	 *           the _tableName2 to set
	 */
	public void setTableName2(String _tableName2)
	{
		this._tableName2 = _tableName2;
	}

	/**
	 * @return the _tableName2
	 */
	public String getTableName2()
	{
		return _tableName2;
	}

	/**
	 * @param _differenceVal1
	 *           the _differenceVal1 to set
	 */
	public void setDifferenceVal1(Object _differenceVal1)
	{
		this._differenceVal1 = _differenceVal1;
	}

	/**
	 * @return the _differenceVal1
	 */
	public Object getDifferenceVal1()
	{
		return _differenceVal1;
	}

	/**
	 * @param _differenceVal2
	 *           the _differenceVal2 to set
	 */
	public void setDifferenceVal2(Object _differenceVal2)
	{
		this._differenceVal2 = _differenceVal2;
	}

	/**
	 * @return the _differenceVal2
	 */
	public Object getDifferenceVal2()
	{
		return _differenceVal2;
	}

}
