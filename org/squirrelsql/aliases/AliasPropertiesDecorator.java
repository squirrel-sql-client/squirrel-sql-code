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
   public static final int TABLE_COL_IX = 1;
   public static final int VIEW_COL_IX = 2;
   public static final int PROCEDURE_COL_IX = 3;

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
         if("TABLE".equalsIgnoreCase(structItemTableType.getType()))
         {
            if (structItemTableType.getSchema().equalsIgnoreCase((String) specifiedLoadingRows.get(SCHEMA_NAME_COL_IX)))
            {
               return false == SchemaLoadOptions.DONT_LOAD.equals(SchemaLoadOptions.valueOf((String) specifiedLoadingRows.get(TABLE_COL_IX)));
            }
         }
         else if("VIEW".equalsIgnoreCase(structItemTableType.getType()))
         {
            if (structItemTableType.getSchema().equalsIgnoreCase((String) specifiedLoadingRows.get(SCHEMA_NAME_COL_IX)))
            {
               return false == SchemaLoadOptions.DONT_LOAD.equals(SchemaLoadOptions.valueOf((String) specifiedLoadingRows.get(VIEW_COL_IX)));
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
         if (structItemProcedureType.getSchema().equalsIgnoreCase((String) specifiedLoadingRows.get(SCHEMA_NAME_COL_IX)))
         {
            return false == SchemaLoadOptions.DONT_LOAD.equals(SchemaLoadOptions.valueOf((String) specifiedLoadingRows.get(PROCEDURE_COL_IX)));
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
      tl.addColumn(_i18n.t("alias.properties.tables")).setSelectableValues(SchemaLoadOptions.values());
      tl.addColumn(_i18n.t("alias.properties.views")).setSelectableValues(SchemaLoadOptions.values());
      tl.addColumn(_i18n.t("alias.properties.procedures")).setSelectableValues(SchemaLoadOptions.values());
      return tl;
   }

   /**
    * This needs to match the ...COL_IX constants
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


}
