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
package net.sourceforge.squirrel_sql.fw.dialects;

import org.hibernate.HibernateException;

import java.sql.Types;
import java.sql.SQLException;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

/**
 * Dummy Extension for the Intersystems Cache DB
 */
public class IntersystemsCacheDialectExt extends CommonHibernateDialect
{
   private class CacheHelper extends org.hibernate.dialect.Cache71Dialect {
      public CacheHelper() {
         super();
         registerColumnType(Types.BIT, "BIT");
         registerColumnType(Types.TINYINT, "TINYINT");
         registerColumnType(Types.LONGVARBINARY, 32700, "LONGVARBINARY");
         registerColumnType(Types.VARBINARY, 254, "VARBINARY");
         registerColumnType(Types.LONGVARCHAR, 32700, "LONGVARCHAR");
         registerColumnType(Types.NUMERIC, "NUMERIC($p,$s)");
         registerColumnType(Types.INTEGER, "INTEGER");
         registerColumnType(Types.SMALLINT, "SMALLINT");
         registerColumnType(Types.DOUBLE, "DOUBLE");
         registerColumnType(Types.VARCHAR, 3924, "VARCHAR($l)");
         registerColumnType(Types.DATE, "date");
         registerColumnType(Types.TIME, "time");
         registerColumnType(Types.TIMESTAMP, "timestamp");
      }
   }

   /** extended hibernate dialect used in this wrapper */
   private CacheHelper _dialect = new CacheHelper();


   /**
    * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropConstraintSQL(java.lang.String,
    *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
    *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
    */
   public String getDropConstraintSQL(String tableName, String constraintName,
      DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
   {
      return DialectUtils.getDropConstraintSQL(tableName, constraintName, qualifier, prefs, this);
   }


   /**
    * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getTypeName(int, int, int, int)
    */
   public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
   {
      return _dialect.getTypeName(code, length, precision, scale);
   }

   /**
    * Returns the SQL command to create the specified table.
    * 
    * @param tables
    *           the tables to get create statements for
    * @param md
    *           the metadata from the ISession
    * @param prefs
    *           preferences about how the resultant SQL commands should be formed.
    * @param isJdbcOdbc
    *           whether or not the connection is via JDBC-ODBC bridge.
    * @return the SQL that is used to create the specified table
    */
   public List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
      CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException
   {
      return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
   }


   /**
    * The string which identifies this dialect in the dialect chooser.
    *
    * @return a descriptive name that tells the user what database this dialect is design to work with.
    */
   public String getDisplayName()
   {
      return "Cache";
   }

   /**
    * Returns boolean value indicating whether or not this dialect supports the specified database
    * product/version.
    *
    * @param databaseProductName
    *           the name of the database as reported by DatabaseMetaData.getDatabaseProductName()
    * @param databaseProductVersion
    *           the version of the database as reported by DatabaseMetaData.getDatabaseProductVersion()
    * @return true if this dialect can be used for the specified product name and version; false otherwise.
    */
   public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
   {
      if (databaseProductName == null)
      {
         return false;
      }
      if (databaseProductName.trim().startsWith("Cache"))
      {
         // We don't yet have the need to discriminate by version.
         return true;
      }
      return false;
   }

}