package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.Serializable;

/**
 * new SchemaLoadInfo(<all types that may be cached>) creates an object that says: load everything
 */
public class SchemaLoadInfo implements Serializable
{
   public SchemaLoadInfo(String[] tableTypes)
   {
      this.tableTypes = tableTypes;
   }

   /**
    * null means load all Schemas
    */
   public String schemaName;

   /**
    * null means load all types.
    * Should not be set to null because of the enormous
    * amount of Synonyms Oracle provides. 
    */
   public String[] tableTypes;

   public boolean loadProcedures = true;
}
