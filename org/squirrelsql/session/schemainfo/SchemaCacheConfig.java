package org.squirrelsql.session.schemainfo;

public class SchemaCacheConfig
{
   public static final SchemaCacheConfig LOAD_ALL = new SchemaCacheConfig(SchemaCacheConfigFlags.ALL);
   public static final SchemaCacheConfig LOAD_NOTHING = new SchemaCacheConfig(SchemaCacheConfigFlags.NOTHING);
   private SchemaCacheConfigFlags _schemaCacheConfigFlag;


   private static enum SchemaCacheConfigFlags
   {
      ALL,
      NOTHING
   }

   public SchemaCacheConfig(SchemaCacheConfigFlags schemaCacheConfigFlag)
   {
      _schemaCacheConfigFlag = schemaCacheConfigFlag;
   }

   public boolean shouldNotLoad()
   {
      return SchemaCacheConfigFlags.NOTHING == _schemaCacheConfigFlag;
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
