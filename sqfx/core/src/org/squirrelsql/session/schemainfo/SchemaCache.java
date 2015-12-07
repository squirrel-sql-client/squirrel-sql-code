package org.squirrelsql.session.schemainfo;

import org.squirrelsql.aliases.AliasPropertiesDecorator;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.CaseInsensitiveString;
import org.squirrelsql.services.Utils;
import org.squirrelsql.services.progress.Progressable;
import org.squirrelsql.session.*;
import org.squirrelsql.session.completion.TableTypes;
import org.squirrelsql.session.objecttree.TableDetailsReader;
import org.squirrelsql.session.schemainfo.schemacacheloading.SchemaCacheData;
import org.squirrelsql.session.schemainfo.schemacacheloading.SchemaCacheLoader;
import org.squirrelsql.table.TableLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchemaCache
{
   private  SchemaCacheConfig _schemaCacheConfig;
   private DatabaseStructure _databaseStructure;
   private DbConnectorResult _dbConnectorResult;

   private SchemaCacheLoader _schemaCacheLoader;

   public SchemaCache(DbConnectorResult dbConnectorResult, SchemaCacheConfig schemaCacheConfig, DatabaseStructure databaseStructure)
   {
      _dbConnectorResult = dbConnectorResult;
      _schemaCacheConfig = schemaCacheConfig;
      _databaseStructure = databaseStructure;
   }

   public void load(Progressable progressable)
   {
      if(_schemaCacheConfig.shouldNotLoad())
      {
         return;
      }

      _schemaCacheLoader = new SchemaCacheLoader(_dbConnectorResult, _schemaCacheConfig, _databaseStructure);

      _schemaCacheLoader.load(progressable);
   }

   public void reloadMatchingTables(String tableName)
   {
      _schemaCacheLoader.reloadMatchingTables(tableName);
   }

   public void reloadMatchingProcedures(String procedureName)
   {
      _schemaCacheLoader.reloadMatchingProcedures(procedureName);
   }

   private SchemaCacheData scd()
   {
      return _schemaCacheLoader.getSchemaCacheDataReadOnly();
   }

   public DatabaseStructure getDataBaseStructure()
   {
      return _databaseStructure;
   }

   public SchemaCacheConfig getSchemaCacheConfig()
   {
      return _schemaCacheConfig;
   }

   public List<TableInfo> getTableInfosExact(String catalog, String schema, String tableType)
   {
      return Utils.convertNullToArray(scd().getTableInfos().get(new StructItemTableType(tableType, catalog, schema)));
   }

   public List<TableInfo> getTableInfosMatching(String catalog, String schema, TableTypes[] allowedTypes)
   {
      List<TableInfo> ret = new ArrayList<>();

      for (StructItemTableType structItemTableType : scd().getTableInfos().keySet())
      {
         if(structItemTableType.matchesRespectNull(catalog, schema, allowedTypes))
         {
            ret.addAll(scd().getTableInfos().get(structItemTableType));
         }
      }

      return ret;
   }

   public List<ProcedureInfo> getProcedureInfosExact(String catalog, String schema)
   {
      return Utils.convertNullToArray(scd().getProcedureInfos().get(new StructItemProcedureType(catalog, schema)));
   }

   public List<ProcedureInfo> getProcedureInfosMatching(String catalog, String schema)
   {
      List<ProcedureInfo> ret = new ArrayList<>();

      for (StructItemProcedureType structItemProcedureType : scd().getProcedureInfos().keySet())
      {
         if(structItemProcedureType.matchesRespectNull(catalog, schema))
         {
            ret.addAll(scd().getProcedureInfos().get(structItemProcedureType));
         }
      }

      return ret;
   }

   public List<UDTInfo> getUDTInfosExact(String catalog, String schema)
   {
      return Utils.convertNullToArray(scd().getUdtInfos().get(new StructItemUDTType(catalog, schema)));
   }


   public List<UDTInfo> getUDTInfosMatching(String catalog, String schema)
   {
      List<UDTInfo> ret = new ArrayList<>();

      for (StructItemUDTType structItemUDTType : scd().getUdtInfos().keySet())
      {
         if(structItemUDTType.matchesRespectNull(catalog, schema))
         {
            ret.addAll(scd().getUdtInfos().get(structItemUDTType));
         }
      }

      return ret;
   }

   public TableLoader getTypes()
   {
      return scd().getDataTypes();
   }

   public TableLoader getDatabaseMetaData()
   {
      return scd().getDataBaseMetadData();
   }

   public TableLoader getNumericFunctions()
   {
      return scd().getNumericFunctions();
   }

   public TableLoader getStringFunctions()
   {
      return scd().getStringFunctions();
   }

   public TableLoader getSystemFunctions()
   {
      return scd().getSystemFunctions();
   }

   public TableLoader getTimeDateFunctions()
   {
      return scd().getTimeDateFunctions();
   }

   public TableLoader getKeywords()
   {
      return scd().getKeywords();
   }

   public List<StructItemCatalog> getCatalogs()
   {
      return _databaseStructure.getCatalogs();
   }

   public List<StructItemSchema> getSchemas()
   {
      return Utils.convertNullToArray(_databaseStructure.getSchemas());
   }

   public List<String> getAllFunctions()
   {
      List<String> ret = new ArrayList<>();

      ret.addAll(scd().getStringFunctions().getCellsAsString(0));
      ret.addAll(scd().getNumericFunctions().getCellsAsString(0));
      ret.addAll(scd().getTimeDateFunctions().getCellsAsString(0));
      ret.addAll(scd().getSystemFunctions().getCellsAsString(0));

      return ret;
   }

   public StructItemCatalog getCatalogByName(String catalogName)
   {
      return _databaseStructure.getCatalogByName(catalogName);
   }

   public List<StructItemSchema> getSchemasByName(String schemaName)
   {
      return Utils.convertNullToArray(_databaseStructure.getSchemasByName(schemaName));
   }

   public List<StructItemSchema> getSchemaByNameAsArray(String catalogName, String schemaName)
   {
      return Utils.convertNullToArray(_databaseStructure.getSchemaByNameAsArray(catalogName, schemaName));
   }

   public List<TableInfo> getTablesByFullyQualifiedName(String catalog, String schema, String tableName)
   {
      return getTablesByFullyQualifiedName(new FullyQualifiedTableName(catalog, schema, tableName));
   }

   public List<TableInfo> getTablesByFullyQualifiedName(FullyQualifiedTableName fullyQualifiedTableName)
   {
      return Utils.convertNullToArray(scd().getTableInfosByFullyQualifiedName().get(fullyQualifiedTableName));
   }

   public List<TableInfo> getTablesBySchemaQualifiedName(String schema, String tableName)
   {
      return Utils.convertNullToArray(scd().getTableInfosBySchemaQualifiedName().get(new SchemaQualifiedTableName(schema, tableName)));
   }

   public List<TableInfo> getTablesBySimpleName(String tableName)
   {
      return Utils.convertNullToArray(scd().getTableInfosBySimpleName().get(new CaseInsensitiveString(tableName)));
   }



   public String[] getDefaultKeywords()
   {
      return DefaultKeywords.KEY_WORDS;
   }

   public TableLoader getColumnsAsTableLoader(TableInfo table)
   {
      initCols(table);
      return table.getColumnsAsTableLoader();
   }

   public List<ColumnInfo> getColumns(TableInfo table)
   {
      initCols(table);
      return table.getColumns();
   }

   private void initCols(TableInfo table)
   {
      if(null != table.getColumnsAsTableLoader())
      {
         // already loaded
         return;
      }

      TableLoader cols = TableDetailsReader.readColumns(table, _dbConnectorResult);

      for (int i = 0; i < cols.getRows().size(); i++)
      {
         scd().getCaseInsensitiveCache().addColumn(ColumnMetaProps.COLUMN_NAME.getCellAsString(cols, i));
      }

      table.setColumnsAsTableLoader(cols);

   }

   public AliasPropertiesDecorator getAliasPropertiesDecorator()
   {
      return _schemaCacheConfig.getAliasPropertiesDecorator();
   }

   public List<TableInfo> getTables(char[] buffer, int offset, int len)
   {
      List<TableInfo> tables = scd().getCaseInsensitiveCache().getTables(buffer, offset, len);

      if(null == tables)
      {
         return Collections.emptyList();
      }

      for (TableInfo table : tables)
      {
         initCols(table);
      }

      return tables;

   }

   public boolean isProcedure(char[] buffer, int offset, int len)
   {
      return scd().getCaseInsensitiveCache().isProcedure(buffer, offset, len);
   }

   public boolean isKeyword(char[] buffer, int offset, int len)
   {
      return scd().getCaseInsensitiveCache().isKeyword(buffer, offset, len);
   }

   public boolean isColumn(char[] buffer, int offset, int len)
   {
      return scd().getCaseInsensitiveCache().isColumn(buffer, offset, len);
   }

   public void writeCache()
   {
      _schemaCacheLoader.writeCache();
   }

}
