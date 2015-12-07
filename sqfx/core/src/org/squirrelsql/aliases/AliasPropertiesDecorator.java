package org.squirrelsql.aliases;

import org.apache.commons.lang3.SerializationUtils;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.AliasPropertiesSpecifiedLoading;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.schemainfo.*;
import org.squirrelsql.table.RowObjectTableLoader;

import java.util.List;
import java.util.function.Predicate;

public class AliasPropertiesDecorator
{

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

      return _checkPredicateOnTableType(structItemTableType, tableOpt -> SchemaLoadOptions.DONT_LOAD != tableOpt);
   }

   public boolean shouldCacheTables(StructItemTableType structItemTableType)
   {
      if(_aliasProperties.isLoadAndCacheAll())
      {
         if (isOfTypeTable(structItemTableType) || isOfTypeView(structItemTableType))
         {
            return true;
         }

         return false; // By default we ignore system table types. They can only be included by specified loading, See below.
      }

      return _checkPredicateOnTableType(structItemTableType, tableOpt -> SchemaLoadOptions.LOAD_AND_CACHE == tableOpt);
   }

   private boolean _checkPredicateOnTableType(StructItemTableType structItemTableType, Predicate<SchemaLoadOptions> schemaLoadOptionTest)
   {
      for (AliasPropertiesSpecifiedLoading specifiedLoading : _aliasProperties.getSpecifiedLoadings())
      {

         if(isOfTypeTable(structItemTableType))
         {
            if (specifiedLoading.getAliasPropertiesSchema().matches(structItemTableType))
            {
               return schemaLoadOptionTest.test(specifiedLoading.getTableOpt());
            }
         }
         else if(isOfTypeView(structItemTableType))
         {
            if (specifiedLoading.getAliasPropertiesSchema().matches(structItemTableType))
            {
               return schemaLoadOptionTest.test(specifiedLoading.getViewOpt());
            }
         }
         else
         {
            if (specifiedLoading.getAliasPropertiesSchema().matches(structItemTableType))
            {
               return schemaLoadOptionTest.test(specifiedLoading.getOtherTableOpt());
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



   public boolean shouldCacheProcedures(StructItemProcedureType structItemProcedureType)
   {
      if(_aliasProperties.isLoadAndCacheAll())
      {
         return true;
      }

      return _checkPredicateOnProcedureType(structItemProcedureType, procedureOpt -> SchemaLoadOptions.LOAD_AND_CACHE == procedureOpt);
   }


   public boolean shouldLoadProcedures(StructItemProcedureType structItemProcedureType)
   {
      if(_aliasProperties.isLoadAllCacheNon() || _aliasProperties.isLoadAndCacheAll())
      {
         return true;
      }

      return _checkPredicateOnProcedureType(structItemProcedureType, procedureOpt -> SchemaLoadOptions.DONT_LOAD != procedureOpt);
   }

   private boolean _checkPredicateOnProcedureType(StructItemProcedureType structItemProcedureType, Predicate<SchemaLoadOptions> schemaLoadOptionTest)
   {

      for (AliasPropertiesSpecifiedLoading specifiedLoading : _aliasProperties.getSpecifiedLoadings())
      {
         if (specifiedLoading.getAliasPropertiesSchema().matches(structItemProcedureType))
         {
            return schemaLoadOptionTest.test(specifiedLoading.getProcedureOpt());
         }
      }

      return false;
   }


   public static RowObjectTableLoader createEmptyTableLoader()
   {
      RowObjectTableLoader<AliasPropertiesSpecifiedLoading> tl = new RowObjectTableLoader<>();

      tl.addColumn(_i18n.t("alias.properties.schema"));
      tl.addColumn(AliasPropertiesObjectTypes.TABLE.toString(), SchemaLoadOptions.values());
      tl.addColumn(AliasPropertiesObjectTypes.VIEW.toString(), SchemaLoadOptions.values());
      tl.addColumn(AliasPropertiesObjectTypes.PROCEDURE.toString(), SchemaLoadOptions.values());
      tl.addColumn(AliasPropertiesObjectTypes.OTHER_TABLE_TYPES.toString(), SchemaLoadOptions.values());
      return tl;
   }

   public static void fillSchemaTableDefault(DbConnectorResult dbConnectorResult, RowObjectTableLoader<AliasPropertiesSpecifiedLoading> tableLoaderSchemasToFill)
   {
      DatabaseStructure dataBaseStructure = dbConnectorResult.getSchemaCacheProperty().get().getDataBaseStructure();

      List<StructItemSchema> schemas = dataBaseStructure.getSchemas();

      AliasPropertiesSpecifiedLoading.TableLoaderAccess tableLoaderAccess = new AliasPropertiesSpecifiedLoading.TableLoaderAccess();


      if (0 < schemas.size())
      {
         for (StructItemSchema schema : schemas)
         {
            tableLoaderSchemasToFill.addRowObject(new AliasPropertiesSpecifiedLoading(new AliasPropertiesSchema(schema)), tableLoaderAccess);
         }
      }
      else
      {
         List<StructItemCatalog> catalogs = dataBaseStructure.getCatalogs();
         for (StructItemCatalog catalog : catalogs)
         {
            tableLoaderSchemasToFill.addRowObject(new AliasPropertiesSpecifiedLoading(new AliasPropertiesSchema(catalog)), tableLoaderAccess);
         }

      }
   }

   public boolean isHideEmptySchemasInObjectTree()
   {
      return _aliasProperties.isHideEmptySchemasInObjectTree();
   }

   public AliasPropertiesDecorator copyToAlias(Alias target)
   {
      AliasProperties targetProperties = SerializationUtils.clone(_aliasProperties);

      targetProperties.setAliasId(target.getId());

      return new AliasPropertiesDecorator(targetProperties);
   }

}
