package net.sourceforge.squirrel_sql.client.gui.db;

public class SchemaLoadInfo
{
   /**
    * null means load all Schemas
    */
   public String schemaName;

   /**
    * null means load all types
    */
   public String[] tableTypes;

   public boolean loadProcedures = true;
}
