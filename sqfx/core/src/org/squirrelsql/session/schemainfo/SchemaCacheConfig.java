package org.squirrelsql.session.schemainfo;

import org.squirrelsql.aliases.AliasPropertiesDecorator;

public class SchemaCacheConfig
{
   public static final SchemaCacheConfig LOAD_NOTHING = new SchemaCacheConfig(SchemaCacheConfigFlags.NOTHING);

   private SchemaCacheConfigFlags _schemaCacheConfigFlag;
   private AliasPropertiesDecorator _aliasPropertiesDecorator;

   public SchemaCacheConfig(AliasPropertiesDecorator aliasPropertiesDecorator)
   {
      _aliasPropertiesDecorator = aliasPropertiesDecorator;
   }


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
      return LOAD_NOTHING == this;
   }


   public boolean shouldLoadTables(StructItemTableType structItemTableType)
   {
      if(LOAD_NOTHING == this)
      {
         return false;
      }

      return _aliasPropertiesDecorator.shouldLoadTables(structItemTableType);
   }

   public boolean shouldLoadProcedures(StructItemProcedureType structItemProcedureType)
   {
      if(LOAD_NOTHING == this)
      {
         return false;
      }
      return _aliasPropertiesDecorator.shouldLoadProcedures(structItemProcedureType);
   }

   public boolean shouldLoadUDTs(StructItemUDTType structItemUDTType)
   {
      return false; // _aliasPropertiesDecorator.shouldLoadUDTs(structItemUDTType);
   }

}
