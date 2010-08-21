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
package net.sourceforge.squirrel_sql.client.plugin;

/**
 * This interface provides a single location to collect strings representing product name and versions 
 * reported by various JDBC drivers.  This can be useful for testing plugins.
 * 
 */
public interface DatabaseProductVersionData
{

	String MYSQL_PRODUCT_NAME = "MySQL";
	String MYSQL_4_PRODUCT_VERSION = "4.1.22-community-nt";
	String MYSQL_5_PRODUCT_VERSION = "5.0.45-Debian_1ubuntu3.3-log";
	
	String POSTGRESQL_PRODUCT_NAME = "PostgreSQL";
	String POSTGRESQL_8_2_PRODUCT_VERSION = "PostgreSQL 8.2 JDBC3 with SSL (build 504)";

}
