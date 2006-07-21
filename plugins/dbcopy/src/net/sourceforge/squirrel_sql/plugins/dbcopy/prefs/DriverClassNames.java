/*
 * Copyright (C) 2006 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.dbcopy.prefs;

/**
 * A simple interface to store the driver class names for various JDBC drivers
 * that we support.
 */
public interface DriverClassNames {
    
    String AXION_CLASS_NAME = "org.axion";
    String DAFFODIL_CLASS_NAME = "in.co.daffodil";
    String DB2_CLASS_NAME = "com.ibm.db2";
    String DERBY_CLASS_NAME = "org.apache.derby";
    String FIREBIRD_CLASS_NAME = "org.firebirdsql";
    String FRONTBASE_CLASS_NAME = "jdbc.FrontBase.FBJDriver";
    String H2_CLASS_NAME = "org.h2.Driver";
    String HSQL_CLASS_NAME = "org.hsqldb";
    String INGRES_CLASS_NAME = "ca.ingres";
    String MAXDB_CLASS_NAME = "com.sap.dbtech.jdbc";
    String MCKOI_CLASS_NAME = "com.mckoi";
    String MYSQL_CLASS_NAME = "com.mysql org.gjt.mysql";
    String ORACLE_CLASS_NAME = "oracle";
    String POINTBASE_CLASS_NAME = "com.pointbase";
    String POSTGRES_CLASS_NAME = "org.postgresql";
    String MSSQL_CLASS_NAME = "com.microsoft.sqlserver weblogic.jdbc.mssqlserver";
    String SYBASE_CLASS_NAME = "com.sybase";
    String PROGRESS_CLASS_NAME = "com.progress";
}
