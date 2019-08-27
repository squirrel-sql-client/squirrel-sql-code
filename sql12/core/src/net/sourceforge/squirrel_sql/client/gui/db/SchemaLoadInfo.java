package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.Serializable;

/**
 * new SchemaLoadInfo(<all types that may be cached>) creates an object that says: load everything
 */
public class SchemaLoadInfo implements Serializable
{
   private String _schemaName;

   private String[] _tableTypes;

   private boolean _loadProcedures = true;

   private boolean _loadUDTs = true;


   public SchemaLoadInfo(String[] tableTypes)
   {
      this._tableTypes = tableTypes;
   }

   /**
    * null means load all Schemas
    */
   public String getSchemaName()
   {
      return _schemaName;
   }

   public void setSchemaName(String schemaName)
   {
      this._schemaName = schemaName;
   }

   /**
    * null means load all types.
    * Should not be set to null because of the enormous
    * amount of Synonyms Oracle provides.
    */
   public String[] getTableTypes()
   {
      return _tableTypes;
   }

   public void setTableTypes(String[] tableTypes)
   {
      this._tableTypes = tableTypes;
   }

   public boolean isLoadProcedures()
   {
      return _loadProcedures;
   }

   public void setLoadProcedures(boolean loadProcedures)
   {
      this._loadProcedures = loadProcedures;
   }

   public boolean isLoadUDTs()
   {
      return _loadUDTs;
   }

   public void setLoadUDTs(boolean loadUDTs)
   {
      this._loadUDTs = loadUDTs;
   }
}
