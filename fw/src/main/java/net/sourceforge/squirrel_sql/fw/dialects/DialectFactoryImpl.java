package net.sourceforge.squirrel_sql.fw.dialects;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

/*
 * Copyright (C) 2010 Rob Manning
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

/**
 * An implementation of IDialectFactory that delegates to DialectFactory.  Since DialectFactory is mostly 
 * static, and performs some static initialization that cannot be undone once initialized, this wrapper allows
 * classes to have a non-static IDialectFactory injected, rather than rely on the static methods in 
 * DialectFactory which is essentially a big and complex global variable.  This means that alternative (for
 * example, mock) implementations can be substituted for the real DialectFactory, when testing if a real 
 * DialectFactory isn't needed for test purposes. 
 */
public class DialectFactoryImpl implements IDialectFactory
{

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isAxion(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isAxion(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isAxion(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isDaffodil(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isDaffodil(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isDaffodil(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isDB2(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isDB2(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isDB2(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isDerby(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isDerby(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isDerby(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isFirebird(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isFirebird(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isFirebird(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isFrontBase(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isFrontBase(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isFrontBase(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isHADB(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isHADB(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isHADB(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isH2(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isH2(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isH2(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isHSQL(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isHSQL(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isHSQL(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isInformix(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isInformix(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isInformix(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isIngres(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isIngres(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isIngres(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isInterbase(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isInterbase(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isInterbase(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isIntersystemsCacheDialectExt(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isIntersystemsCacheDialectExt(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isIntersystemsCacheDialectExt(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isMaxDB(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isMaxDB(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isMaxDB(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isMcKoi(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isMcKoi(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isMcKoi(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isMSSQLServer(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isMSSQLServer(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isMSSQLServer(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isMySQL(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isMySQL(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isMySQL(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isMySQL5(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isMySQL5(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isMySQL5(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isNetezza(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isNetezza(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isNetezza(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isGreenplum(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isGreenplum(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isGreenplum(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isOracle(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isOracle(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isOracle(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isPointbase(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isPointbase(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isPointbase(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isPostgreSQL(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isPostgreSQL(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isPostgreSQL(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isProgress(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isProgress(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isProgress(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isSyBase(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isSyBase(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isSyBase(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      isTimesTen(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public boolean isTimesTen(ISQLDatabaseMetaData md)
	{
		return DialectFactory.isTimesTen(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      getDialectType(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public DialectType getDialectType(ISQLDatabaseMetaData md)
	{
		return DialectFactory.getDialectType(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#getDialect(java.lang.String)
	 */
	public HibernateDialect getDialect(String dbName)
	{
		return DialectFactory.getDialect(dbName);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#getDialectIgnoreCase(java.lang.String)
	 */
	public HibernateDialect getDialectIgnoreCase(String dbName)
	{
		return DialectFactory.getDialectIgnoreCase(dbName);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#
	 *      getDialect(net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public HibernateDialect getDialect(ISQLDatabaseMetaData md)
	{
		return DialectFactory.getDialect(md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#getDialect(int, javax.swing.JFrame,
	 *      net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public HibernateDialect getDialect(int sessionType, JFrame parent, ISQLDatabaseMetaData md)
		throws UserCancelledOperationException
	{
		return DialectFactory.getDialect(sessionType, parent, md);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#getDbNames()
	 */
	public Object[] getDbNames()
	{
		return DialectFactory.getDbNames();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory#getSupportedDialects()
	 */
	public Object[] getSupportedDialects()
	{
		return DialectFactory.getSupportedDialects();
	}
}
