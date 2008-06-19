/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;

public class GenericDialectExt extends CommonHibernateDialect
{

	private class GenericDialectHelper extends Dialect
	{
		public GenericDialectHelper()
		{
			registerColumnType(Types.BIGINT, "integer");
			registerColumnType(Types.CHAR, "char($l)");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARCHAR, "varchar($l)");
			registerColumnType(Types.SMALLINT, "integer");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "integer");
			registerColumnType(Types.VARCHAR, "varchar($l)");
		}
	}

	/** extended hibernate dialect used in this wrapper */
	private GenericDialectHelper _dialect = new GenericDialectHelper();

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTypeName(int, int, int, int)
	 */
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDisplayName()
	 */
	@Override
	public String getDisplayName()
	{
		return "Generic";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDialectType()
	 */
	@Override
	public DialectType getDialectType()
	{
		return DialectType.GENERIC;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsProduct(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		return true;
	}
	
	
	
}
