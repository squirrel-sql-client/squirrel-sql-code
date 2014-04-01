package org.squirrelsql.aliases;

import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.schemainfo.DatabaseStructure;
import org.squirrelsql.session.schemainfo.StructItemProcedureType;
import org.squirrelsql.session.schemainfo.StructItemSchema;
import org.squirrelsql.session.schemainfo.StructItemTableType;
import org.squirrelsql.table.TableLoader;

import java.util.ArrayList;

public class AliasPropertiesDecorator
{
   public static final int SCHEMA_NAME_COL_IX = 0;

   private static I18n _i18n = new I18n(AliasPropertiesDecorator.class);


   private AliasProperties _aliasProperties;

   public AliasPropertiesDecorator(AliasProperties aliasProperties)
   {
      _aliasProperties = aliasProperties;
   }

   public AliasProperties getAliasProperties()
   {
      return _aliasProperties;
   }

   /**
    *
    * TODO caching
    */
   public boolean shouldLoadTables(StructItemTableType structItemTableType)
   {
      if(_aliasProperties.isLoadAllCacheNon() || _aliasProperties.isLoadAndCacheAll())
      {
         return true;
      }

      for (ArrayList specifiedLoadingRows : _aliasProperties.getSpecifiedLoading())
      {
         String schemaName = (String) specifiedLoadingRows.get(SCHEMA_NAME_COL_IX);

         if("TABLE".equalsIgnoreCase(structItemTableType.getType()))
         {
            if (structItemTableType.getSchema().equalsIgnoreCase(schemaName))
            {
               return SchemaLoadOptions.DONT_LOAD != specifiedLoadingRows.get(AliasPropertiesObjectTypes.TABLE.getColIx());
            }
         }
         else if("VIEW".equalsIgnoreCase(structItemTableType.getType()))
         {
            if (structItemTableType.getSchema().equalsIgnoreCase(schemaName))
            {
               return SchemaLoadOptions.DONT_LOAD != specifiedLoadingRows.get(AliasPropertiesObjectTypes.VIEW.getColIx());
            }
         }

         // TODO other table types are all ignored

      }

      return false;
   }

   public boolean shouldLoadProcedures(StructItemProcedureType structItemProcedureType)
   {
      if(_aliasProperties.isLoadAllCacheNon() || _aliasProperties.isLoadAndCacheAll())
      {
         return true;
      }

      for (ArrayList specifiedLoadingRows : _aliasProperties.getSpecifiedLoading())
      {
         String schemaName = (String) specifiedLoadingRows.get(SCHEMA_NAME_COL_IX);

         if (structItemProcedureType.getSchema().equalsIgnoreCase(schemaName))
         {
            return SchemaLoadOptions.DONT_LOAD != specifiedLoadingRows.get(AliasPropertiesObjectTypes.PROCEDURE.getColIx());
         }
      }

      return false;
   }


   /**
    * This needs to match the ...COL_IX constants
    */
   public static TableLoader createEmptyTableLoader()
   {
      TableLoader tl = new TableLoader();

      tl.addColumn(_i18n.t("alias.properties.schema"));
      tl.addColumn(AliasPropertiesObjectTypes.TABLE.toString()).setSelectableValues(SchemaLoadOptions.values());
      tl.addColumn(AliasPropertiesObjectTypes.VIEW.toString()).setSelectableValues(SchemaLoadOptions.values());
      tl.addColumn(AliasPropertiesObjectTypes.PROCEDURE.toString()).setSelectableValues(SchemaLoadOptions.values());
      return tl;
   }

   /**
    * This needs to match the AliasPropertiesObjectTypes.getColIx()
    */
   public static void fillSchemaTable(DbConnectorResult dbConnectorResult, TableLoader tableLoaderSchemasToFill)
   {
      DatabaseStructure dataBaseStructure = dbConnectorResult.getSchemaCache().getDataBaseStructure();

      ArrayList<StructItemSchema> schemas = dataBaseStructure.getSchemas();


      for (StructItemSchema schema : schemas)
      {
         tableLoaderSchemasToFill.addRow(schema.getQualifiedName(), SchemaLoadOptions.LOAD_BUT_DONT_CACHE, SchemaLoadOptions.LOAD_BUT_DONT_CACHE, SchemaLoadOptions.LOAD_BUT_DONT_CACHE);
      }
   }


   public static AliasProperties convertStringsToSchemaLoadOptions(AliasProperties aliasProperties)
   {
      for (ArrayList row : aliasProperties.getSpecifiedLoading())
      {
         for (AliasPropertiesObjectTypes aliasPropertiesObjectType : AliasPropertiesObjectTypes.values())
         {
            int ix = aliasPropertiesObjectType.getColIx();
            if(row.get(ix) instanceof String)
            {
               row.set(ix, SchemaLoadOptions.valueOf((String)row.get(ix)));
            }
         }
      }

      return aliasProperties;
   }
}
