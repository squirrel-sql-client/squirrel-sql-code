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
 * An interface for a non-static inject-able DialectFactory.
 */
public interface IDialectFactory
{

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is Axion
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isAxion(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is Daffodil
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isDaffodil(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is DB2
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isDB2(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is derby
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isDerby(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is firebird
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isFirebird(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is frontbase
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isFrontBase(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is HADB
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isHADB(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is H2
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isH2(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is HSQL
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isHSQL(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is Informix
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isInformix(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is Ingres
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isIngres(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is Interbase
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isInterbase(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is
	 * InterSystems Cache
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isIntersystemsCacheDialectExt(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is MaxDB
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isMaxDB(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is McKoi
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isMcKoi(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is MSSQL
	 * Server
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isMSSQLServer(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is MySQL 4 or
	 * below
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isMySQL(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is MySQL 5
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isMySQL5(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is Netezza
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isNetezza(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is Greenplum
	 *
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isGreenplum(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is Oracle
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isOracle(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is Pointbase
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isPointbase(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is PostgreSQL
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isPostgreSQL(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is Progress
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isProgress(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is SyBase
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isSyBase(ISQLDatabaseMetaData md);

	/**
	 * Returns a boolean value indicating whether or not the specified metadata indicates that it is Oracle
	 * Times Ten
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return boolean indicating whether or not the specified metadata matches the database type
	 */
	boolean isTimesTen(ISQLDatabaseMetaData md);

	/**
	 * Returns a DialectType for the specified ISQLDatabaseMetaData
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return a dialect type
	 */
	DialectType getDialectType(ISQLDatabaseMetaData md);

	/**
	 * Returns the HibernateDialect that corresponds with the specified database display name.
	 * 
	 * @param dbName
	 *           the database display name
	 * @return a HibernateDialect matching the specified database display name exactly, or null.
	 */
	HibernateDialect getDialect(String dbName);

	/**
	 * Returns the HibernateDialect that corresponds with the specified metadata.
	 * 
	 * @param dbName
	 *           the database display name
	 * @return a HibernateDialect matching the specified database display name ignoring case, or null.
	 */
	HibernateDialect getDialectIgnoreCase(String dbName);

	/**
	 * Returns the HibernateDialect that corresponds with the specified metadata.
	 * 
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return the HibernateDialect that corresponds with the specified metadata. If no specific implementation
	 *         matches the specified metadata, then a generic dialect is returned.
	 */
	HibernateDialect getDialect(ISQLDatabaseMetaData md);

	/**
	 * Shows the user a dialog explaining that we failed to detect the dialect of the destination database, and
	 * we are offering the user the opportunity to pick one from our supported dialects list. If the user
	 * cancels this dialog, null is returned to indicate that the user doesn't wish to continue the paste
	 * operation.
	 * 
	 * @param sessionType
	 *           the type of the session (source or destination). This is a left over from DBCopy Plugin and
	 *           should be refactored to not need this at some point.
	 * @param parent
	 *           the JFrame to use to display the dialog over.
	 * @param md
	 *           The SQLDatabaseMetaData retrieved from either ISQLConnection.getSQLMetaData() or
	 *           ISession.getMetaData()
	 * @return the dialect that the user picked.
	 */
	HibernateDialect getDialect(int sessionType, JFrame parent, ISQLDatabaseMetaData md)
		throws UserCancelledOperationException;

	/**
	 * Returns a list of Database display names that can be presented to the user whenever we want the user to
	 * pick a dialect. It is from this list, that the string parameter in getDialect(String) should be chosen.
	 * 
	 * @return a list of database display names
	 */
	Object[] getDbNames();

	/**
	 * Returns an array of HibernateDialect instances, one for each supported dialect.
	 * 
	 * @return an array of HibernateDialect instances. This array doesn't include the generic dialect.
	 */
	Object[] getSupportedDialects();

}