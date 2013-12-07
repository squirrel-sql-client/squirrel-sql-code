package org.squirrelsql.session.schemainfo;

public class SchemaCacheConfig
{
   public static final SchemaCacheConfig ALL = new SchemaCacheConfig(SchemaCacheConfigFlags.ALL);
   public static final SchemaCacheConfig NONE = new SchemaCacheConfig(SchemaCacheConfigFlags.NONE);



   private static enum SchemaCacheConfigFlags
   {
      ALL,
      NONE
   }

   public SchemaCacheConfig(SchemaCacheConfigFlags schemaCacheConfigFlags)
   {

   }

   public boolean shouldLoadTables(StructItemTableType structItemTableType)
   {
      return true;
   }

   public boolean shouldLoadProcedures(StructItemProcedureType structItemProcedureType)
   {
      return true;
   }

   public boolean shouldLoadUDTs(StructItemUDTType structItemUDTType)
   {
      return true;
   }

}
