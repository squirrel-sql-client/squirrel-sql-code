package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002 Colin Bell
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
/**
 * 
 * Defines the different types of database objects.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IDatabaseObjectTypes
{
	int GENERIC_LEAF = 0;
	int GENERIC_FOLDER = 1;
	int DATABASE = 2;
	int SCHEMA = 3;
	int CATALOG = 4;
	int TABLE = 5;
	int PROCEDURE = 6;
	int UDT = 7;
	int INDEX = 8;

	/**
	 * An object that generates uniques IDs for primary keys. E.G. an Oracle
	 * sequence.
	 */
	int SEQUENCE = 8;

	/**
	 * This isn't an object type but rather is a guarantee that no value in this
	 * interface will ever be greater than <TT>LAST_USED</TT>.
	 */
	int LAST_USED_OBJECT_TYPE = 9999;
}
