package net.sourceforge.squirrel_sql.plugins.oracle;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
/**
 * This class contains the different database object types for oracle.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IObjectTypes
{
	DatabaseObjectType CONSUMER_GROUP_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Consumer Groups");
	DatabaseObjectType FUNCTION_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Functions");
	DatabaseObjectType INDEX_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Indexes");
	DatabaseObjectType INSTANCE_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Instances");
	DatabaseObjectType LOB_PARENT = DatabaseObjectType.createNewDatabaseObjectType("LOBS");
	DatabaseObjectType PACKAGE_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Packages");
	DatabaseObjectType SEQUENCE_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Sequences");
	DatabaseObjectType SESSION_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Sessions");
	DatabaseObjectType TRIGGER_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Triggers");
	DatabaseObjectType TYPE_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Types");
	DatabaseObjectType USER_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Users");

	DatabaseObjectType CONSUMER_GROUP = DatabaseObjectType.createNewDatabaseObjectType("Consumer Group");
	DatabaseObjectType INSTANCE = DatabaseObjectType.createNewDatabaseObjectType("Instance");
	DatabaseObjectType LOB = DatabaseObjectType.createNewDatabaseObjectType("LOB");
	DatabaseObjectType PACKAGE = DatabaseObjectType.createNewDatabaseObjectType("Package");
	DatabaseObjectType SESSION = DatabaseObjectType.createNewDatabaseObjectType("Session");
	DatabaseObjectType TYPE = DatabaseObjectType.createNewDatabaseObjectType("Type");
}
