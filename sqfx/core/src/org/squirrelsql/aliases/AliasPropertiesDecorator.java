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
         if (isOfTypeTable(structItemTableType) || isOfTypeView(structItemTableType))
         {
            return true;
         }

         return false; // By default we ignore system table types. They can only be included by specified loading, See below.
      }

      for (ArrayList specifiedLoadingRows : _aliasProperties.getSpecifiedLoading())
      {
         String qualifiedSchemaName = (String) specifiedLoadingRows.get(SCHEMA_NAME_COL_IX);

         if(isOfTypeTable(structItemTableType))
         {
            if (structItemTableType.getQualifiedSchema().equalsIgnoreCase(qualifiedSchemaName))
            {
               return SchemaLoadOptions.DONT_LOAD != specifiedLoadingRows.get(AliasPropertiesObjectTypes.TABLE.getColIx());
            }
         }
         else if(isOfTypeView(structItemTableType))
         {
            if (structItemTableType.getQualifiedSchema().equalsIgnoreCase(qualifiedSchemaName))
            {
               return SchemaLoadOptions.DONT_LOAD != specifiedLoadingRows.get(AliasPropertiesObjectTypes.VIEW.getColIx());
            }
         }
         else
         {
            if (structItemTableType.getQualifiedSchema().equalsIgnoreCase(qualifiedSchemaName))
            {
               return SchemaLoadOptions.DONT_LOAD != specifiedLoadingRows.get(AliasPropertiesObjectTypes.OTHER_TABLE_TYPES.getColIx());
            }
         }
      }

      return false;
   }

   private boolean isOfTypeView(StructItemTableType structItemTableType)
   {
      return "VIEW".equalsIgnoreCase(structItemTableType.getType());
   }

   private boolean isOfTypeTable(StructItemTableType structItemTableType)
   {
      return "TABLE".equalsIgnoreCase(structItemTableType.getType());
   }

   public boolean shouldLoadProcedures(StructItemProcedureType structItemProcedureType)
   {
      if(_aliasProperties.isLoadAllCacheNon() || _aliasProperties.isLoadAndCacheAll())
      {
         return true;
      }

      for (ArrayList specifiedLoadingRows : _aliasProperties.getSpecifiedLoading())
      {
         String qualifiedSchemaName = (String) specifiedLoadingRows.get(SCHEMA_NAME_COL_IX);

         if (structItemProcedureType.getQualifiedSchema().equalsIgnoreCase(qualifiedSchemaName))
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
      tl.addColumn(AliasPropertiesObjectTypes.OTHER_TABLE_TYPES.toString()).setSelectableValues(SchemaLoadOptions.values());
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
         tableLoaderSchemasToFill.addRow(
               schema.getQualifiedName(),
               SchemaLoadOptions.LOAD_BUT_DONT_CACHE,
               SchemaLoadOptions.LOAD_BUT_DONT_CACHE,
               SchemaLoadOptions.LOAD_BUT_DONT_CACHE,
               SchemaLoadOptions.DONT_LOAD);
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
