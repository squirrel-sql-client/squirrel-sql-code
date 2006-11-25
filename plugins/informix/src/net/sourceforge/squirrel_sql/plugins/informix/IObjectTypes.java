package net.sourceforge.squirrel_sql.plugins.informix;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;


public interface IObjectTypes {

    DatabaseObjectType TRIGGER_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Trigger");
    DatabaseObjectType GENERATOR_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Generator");
    DatabaseObjectType DOMAIN_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Domain");
    DatabaseObjectType VIEW_PARENT = DatabaseObjectType.createNewDatabaseObjectType("View");
    DatabaseObjectType INDEX_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Indices");

}
