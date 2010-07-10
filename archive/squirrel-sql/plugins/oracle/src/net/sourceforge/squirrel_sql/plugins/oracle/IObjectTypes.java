package net.sourceforge.squirrel_sql.plugins.oracle;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
/**
 * This class contains the different database object types for oracle.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IObjectTypes
{
	DatabaseObjectType CONSUMER_GROUP_PARENT = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType FUNCTION_PARENT = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType INDEX_PARENT = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType INSTANCE_PARENT = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType LOB_PARENT = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType PACKAGE_PARENT = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType SEQUENCE_PARENT = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType SESSION_PARENT = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType TRIGGER_PARENT = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType TYPE_PARENT = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType USER_PARENT = DatabaseObjectType.createNewDatabaseObjectType();

	DatabaseObjectType CONSUMER_GROUP = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType INSTANCE = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType LOB = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType PACKAGE = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType SESSION = DatabaseObjectType.createNewDatabaseObjectType();
	DatabaseObjectType TYPE = DatabaseObjectType.createNewDatabaseObjectType();
}
