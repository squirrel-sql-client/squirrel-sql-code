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

import net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.HibernateException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Do not remove final to inherit from this class. Look at {@link SQLiteDialectExt} for an example
 */
public final class GenericDialectExt extends CommonHibernateDialect
{
	private final static ILogger s_log = LoggerController.createLogger(GenericDialectExt.class);

	private GenericDialectHelper _dialect = new GenericDialectHelper();

	@Override
	public String getTypeName(int javaSqlTypesConst, int length, int precision, int scale, String typeNameOrNull) throws HibernateException
	{
		return _dialect.getTypeName(javaSqlTypesConst, length, precision, scale);
	}
	
	@Override
	public String getDisplayName()
	{
		return "Generic";
	}

	@Override
	public DialectType getDialectType()
	{
		// A fallback if this class is tried to make non final.
		if (false == GenericDialectExt.class.equals(this.getClass()))
		{
			s_log.error(new IllegalStateException("Classes derived from " + GenericDialectExt.class.getName() + " must implement getDialectType()."));
		}

		return DialectType.GENERIC;
	}

	/**
	 * No other implementation of {@link CommonHibernateDialect} is supposed to without checks return true;
	 */
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		return true;
	}
}
