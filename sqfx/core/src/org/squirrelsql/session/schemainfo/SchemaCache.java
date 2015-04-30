package org.squirrelsql.session.schemainfo;

import org.squirrelsql.aliases.AliasPropertiesDecorator;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.CaseInsensitiveString;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.*;
import org.squirrelsql.session.completion.TableTypes;
import org.squirrelsql.session.objecttree.TableDetailsReader;
import org.squirrelsql.table.TableLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SchemaCache
{
   private final SchemaCacheConfig _schemaCacheConfig;

   private DatabaseStructure _databaseStructure;

   private TableLoader _dataTypes;
   private TableLoader _dataBaseMetadData;
   private TableLoader _numericFunctions;
   private TableLoader _stringFunctions;
   private TableLoader _systemFunctions;
   private TableLoader _timeDateFunctions;
   private TableLoader _keywords;

   private HashMap<StructItemTableType, List<TableInfo>> _tableInfos = new HashMap<>();

   private HashMap<FullyQualifiedTableName, List<TableInfo>> _tableInfosByFullyQualifiedName = new HashMap<>();
   private HashMap<SchemaQualifiedTableName, List<TableInfo>> _tableInfosBySchemaQualifiedName = new HashMap<>();
   private HashMap<CaseInsensitiveString, List<TableInfo>> _tableInfosBySimpleName = new HashMap<>();

   private HashMap<StructItemProcedureType, List<ProcedureInfo>> _procedureInfos = new HashMap<>();
   private HashMap<StructItemUDTType, List<UDTInfo>> _udtInfos = new HashMap<>();
   private DbConnectorResult _dbConnectorResult;


   private CaseInsensitiveCache _caseInsensitiveCache = new CaseInsensitiveCache();

   public SchemaCache(DbConnectorResult dbConnectorResult, SchemaCacheConfig schemaCacheConfig, DatabaseStructure databaseStructure)
   {
      _dbConnectorResult = dbConnectorResult;
      _schemaCacheConfig = schemaCacheConfig;
      _databaseStructure = databaseStructure;
   }

   public void load()
   {
      if(_schemaCacheConfig.shouldNotLoad())
      {
         return;
      }


      _dataBaseMetadData = DataBaseMetaDataLoader.loadMetaData(_dbConnectorResult.getAlias(), _dbConnectorResult.getSQLConnection());
      _dataTypes = DataTypesLoader.loadTypes(_dbConnectorResult.getSQLConnection());

      _numericFunctions = DataBaseMetaDataLoader.loadNumericFunctions(_dbConnectorResult.getSQLConnection());
      _numericFunctions.getRows().forEach(e -> _caseInsensitiveCache.addProc((String) e.get(0)));

      _stringFunctions = DataBaseMetaDataLoader.loadStringFunctions(_dbConnectorResult.getSQLConnection());
      _stringFunctions.getRows().forEach(e ->  _caseInsensitiveCache.addProc((String) e.get(0)));

      _systemFunctions = DataBaseMetaDataLoader.loadSystemFunctions(_dbConnectorResult.getSQLConnection());
      _systemFunctions.getRows().forEach(e ->  _caseInsensitiveCache.addProc((String) e.get(0)));

      _timeDateFunctions = DataBaseMetaDataLoader.loadTimeDateFunctions(_dbConnectorResult.getSQLConnection());
      _timeDateFunctions.getRows().forEach(e ->  _caseInsensitiveCache.addProc((String) e.get(0)));

      _keywords = DataBaseMetaDataLoader.loadKeyWords(_dbConnectorResult.getSQLConnection());
      _keywords.getRows().forEach(e ->  _caseInsensitiveCache.addKeyword((String) e.get(0)));

      for (String keyWord : DefaultKeywords.KEY_WORDS)
      {
         _caseInsensitiveCache.addKeyword(keyWord);
      }

      List<StructItem> leaves = _databaseStructure.getLeaves();

      for (StructItem leaf : leaves)
      {
         if(leaf instanceof StructItemTableType)
         {
            loadMatchingTables((StructItemTableType) leaf, null);
         }
         else if(leaf instanceof StructItemProcedureType)
         {
            loadMatchingProcedures((StructItemProcedureType) leaf, null);
         }
         else if(leaf instanceof StructItemUDTType)
         {
            loadMatchingUDTs((StructItemUDTType) leaf, null);
         }
      }
   }

//   public void reloadMatchingUDTs(String udtName)
//   {
//      List<UDTInfo> udtInfos = _dbConnectorResult.getSQLConnection().getUDTInfos(null, null, udtName);
//
//      for (UDTInfo udtInfo : udtInfos)
//      {
//         StructItemUDTType udtType = new StructItemUDTType(udtInfo.getCatalog(), udtInfo.getSchema());
//
//         List<UDTInfo> toRemoveFrom = _udtInfos.get(udtType);
//
//         if(null != toRemoveFrom)
//         {
//            toRemoveFrom.remove(udtInfo);
//         }
//
//         loadMatchingUDTs(udtType, udtName);
//      }
//
//
//
//   }

   private void loadMatchingUDTs(StructItemUDTType udtType, String udtName)
   {
      if (udtType.shouldLoad(_schemaCacheConfig))
      {

         List<UDTInfo> buf = _udtInfos.get(udtType);
         if(null == buf)
         {
            buf = new ArrayList<>();
            _udtInfos.put(udtType, buf);
         }
         buf.addAll(buf);



         _udtInfos.put(udtType, _dbConnectorResult.getSQLConnection().getUDTInfos(udtType.getCatalog(), udtType.getSchema(), udtName));
      }
   }

   public void reloadMatchingProcedures(String procedureName)
   {

      if(null == procedureName)
      {
         return;
      }

      List<String> caseSensitiveProcedureNames = _caseInsensitiveCache.getMatchingCaseSensitiveProcedureNames(procedureName);

      for (String caseSensitiveProcedureName : caseSensitiveProcedureNames)
      {
         if(false == _reloadMatchingProcedures(caseSensitiveProcedureName))
         {
            // There several ways database systems store names. One of these three should always match.
            if(false == _reloadMatchingProcedures(caseSensitiveProcedureName.toLowerCase()))
            {
               _reloadMatchingProcedures(caseSensitiveProcedureName.toUpperCase());
            }
         }
      }
   }

   private boolean _reloadMatchingProcedures(String procedureName)
   {
      ArrayList<ProcedureInfo> procedureInfos = new ArrayList<>();

      procedureInfos.addAll(Utils.convertNullToArray(_dbConnectorResult.getSQLConnection().getProcedureInfos(null, null, procedureName)));
      procedureInfos.addAll(Utils.convertNullToArray(_caseInsensitiveCache.getProcedures(procedureName)));

      boolean reloaded = false;

      for (ProcedureInfo procedureInfo : procedureInfos)
      {
         StructItemProcedureType procedureType = new StructItemProcedureType(procedureInfo.getCatalog(), procedureInfo.getSchema());
         List<ProcedureInfo> toRemoveFrom = _procedureInfos.get(procedureType);

         if(null != toRemoveFrom)
         {
            toRemoveFrom.remove(procedureInfo);
         }

         _caseInsensitiveCache.removeProc(procedureName);


         loadMatchingProcedures(procedureType, procedureName);

         reloaded = true;
      }

      return reloaded;
   }

   private void loadMatchingProcedures(StructItemProcedureType procedureType, String procedureName)
   {
      if (procedureType.shouldLoad(_schemaCacheConfig))
      {
         List<ProcedureInfo> procedureInfos = _dbConnectorResult.getSQLConnection().getProcedureInfos(procedureType.getCatalog(), procedureType.getSchema(), procedureName);


         _procedureInfos.put(procedureType, procedureInfos);

         List<ProcedureInfo> buf = _procedureInfos.get(procedureType);
         if(null == buf)
         {
            buf = new ArrayList<>();
            _procedureInfos.put(procedureType, buf);
         }
         buf.addAll(procedureInfos);



         for (ProcedureInfo procedureInfo : procedureInfos)
         {
            _caseInsensitiveCache.addProc(procedureInfo.getName());
         }
      }
   }

   public void reloadMatchingTables(String tableName)
   {
      if(null == tableName)
      {
         return;
      }

      List<String> caseSensitiveTableNames = _caseInsensitiveCache.getMatchingCaseSensitiveTableNames(tableName);

      for (String caseSensitiveTableName : caseSensitiveTableNames)
      {
         if(false == _reloadMatchingTables(caseSensitiveTableName))
         {
            // There several ways database systems store names. One of these three should always match.
            if(false == _reloadMatchingTables(caseSensitiveTableName.toLowerCase()))
            {
               _reloadMatchingTables(caseSensitiveTableName.toUpperCase());
            }
         }
      }
   }

   private boolean _reloadMatchingTables(String tableName)
   {
      ArrayList<TableInfo> tableInfos = new ArrayList<>();

      tableInfos.addAll(Utils.convertNullToArray(_dbConnectorResult.getSQLConnection().getTableInfos(null, null, null, tableName)));
      tableInfos.addAll(Utils.convertNullToArray(_caseInsensitiveCache.getTables(tableName)));

      boolean reloaded = false;

      for (TableInfo tableInfo : tableInfos)
      {
         StructItemTableType tableType = new StructItemTableType(tableInfo.getTableType(), tableInfo.getCatalog(), tableInfo.getSchema());
         FullyQualifiedTableName fullyQualifiedTableName = new FullyQualifiedTableName(tableType.getCatalog(), tableType.getSchema(), tableInfo.getName());
         SchemaQualifiedTableName schemaQualifiedTableName = new SchemaQualifiedTableName(tableType.getSchema(), tableInfo.getName());


         List<TableInfo> buf = _tableInfos.get(tableType);
         if(null != buf)
         {
            buf.remove(tableInfo);
         }


         _tableInfosByFullyQualifiedName.remove(fullyQualifiedTableName);
         _tableInfosBySchemaQualifiedName.remove(schemaQualifiedTableName);
         _tableInfosBySimpleName.remove(new CaseInsensitiveString(tableInfo.getName()));
         _caseInsensitiveCache.removeTable(tableInfo);

         loadMatchingTables(tableType, tableName);

         reloaded = true;
      }

      return reloaded;
   }

   private void loadMatchingTables(StructItemTableType tableType, String tableName)
   {
      if (tableType.shouldLoad(_schemaCacheConfig))
      {
         List<TableInfo> tableInfos = _dbConnectorResult.getSQLConnection().getTableInfos(tableType.getCatalog(), tableType.getSchema(), tableType.getType(), tableName);

         List<TableInfo> buf = _tableInfos.get(tableType);
         if(null == buf)
         {
            buf = new ArrayList<>();
            _tableInfos.put(tableType, buf);
         }
         buf.addAll(tableInfos);


         for (TableInfo tableInfo : tableInfos)
         {
            List<TableInfo> arr;


            FullyQualifiedTableName fullyQualifiedTableName = new FullyQualifiedTableName(tableType.getCatalog(), tableType.getSchema(), tableInfo.getName());
            arr = _tableInfosByFullyQualifiedName.get(fullyQualifiedTableName);

            if(null == arr)
            {
               arr = new ArrayList<>();
               _tableInfosByFullyQualifiedName.put(fullyQualifiedTableName, arr);
            }
            arr.add(tableInfo);

            SchemaQualifiedTableName schemaQualifiedTableName = new SchemaQualifiedTableName(tableType.getSchema(), tableInfo.getName());
            arr = _tableInfosBySchemaQualifiedName.get(schemaQualifiedTableName);

            if(null == arr)
            {
               arr = new ArrayList<>();
               _tableInfosBySchemaQualifiedName.put(schemaQualifiedTableName, arr);
            }
            arr.add(tableInfo);

            arr = _tableInfosBySimpleName.get(new CaseInsensitiveString(tableInfo.getName()));

            if(null == arr)
            {
               arr = new ArrayList<>();
               _tableInfosBySimpleName.put(new CaseInsensitiveString(tableInfo.getName()), arr);
            }
            arr.add(tableInfo);

            _caseInsensitiveCache.addTable(tableInfo);

         }

      }
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
      return Utils.convertNullToArray(_tableInfos.get(new StructItemTableType(tableType, catalog, schema)));
   }

   public List<TableInfo> getTableInfosMatching(String catalog, String schema, TableTypes[] allowedTypes)
   {
      List<TableInfo> ret = new ArrayList<>();

      for (StructItemTableType structItemTableType : _tableInfos.keySet())
      {
         if(structItemTableType.matchesRespectNull(catalog, schema, allowedTypes))
         {
            ret.addAll(_tableInfos.get(structItemTableType));
         }
      }

      return ret;
   }

   public List<ProcedureInfo> getProcedureInfosExact(String catalog, String schema)
   {
      return Utils.convertNullToArray(_procedureInfos.get(new StructItemProcedureType(catalog, schema)));
   }

   public List<ProcedureInfo> getProcedureInfosMatching(String catalog, String schema)
   {
      List<ProcedureInfo> ret = new ArrayList<>();

      for (StructItemProcedureType structItemProcedureType : _procedureInfos.keySet())
      {
         if(structItemProcedureType.matchesRespectNull(catalog, schema))
         {
            ret.addAll(_procedureInfos.get(structItemProcedureType));
         }
      }

      return ret;
   }

   public List<UDTInfo> getUDTInfosExact(String catalog, String schema)
   {
      return Utils.convertNullToArray(_udtInfos.get(new StructItemUDTType(catalog, schema)));
   }


   public List<UDTInfo> getUDTInfosMatching(String catalog, String schema)
   {
      List<UDTInfo> ret = new ArrayList<>();

      for (StructItemUDTType structItemUDTType : _udtInfos.keySet())
      {
         if(structItemUDTType.matchesRespectNull(catalog, schema))
         {
            ret.addAll(_udtInfos.get(structItemUDTType));
         }
      }

      return ret;
   }

   public TableLoader getTypes()
   {
      return _dataTypes;
   }

   public TableLoader getDatabaseMetaData()
   {
      return _dataBaseMetadData;
   }

   public TableLoader getNumericFunctions()
   {
      return _numericFunctions;
   }

   public TableLoader getStringFunctions()
   {
      return _stringFunctions;
   }

   public TableLoader getSystemFunctions()
   {
      return _systemFunctions;
   }

   public TableLoader getTimeDateFunctions()
   {
      return _timeDateFunctions;
   }

   public TableLoader getKeywords()
   {
      return _keywords;
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

      ret.addAll(_stringFunctions.getCellsAsString(0));
      ret.addAll(_numericFunctions.getCellsAsString(0));
      ret.addAll(_timeDateFunctions.getCellsAsString(0));
      ret.addAll(_systemFunctions.getCellsAsString(0));

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
      return Utils.convertNullToArray(_tableInfosByFullyQualifiedName.get(new FullyQualifiedTableName(catalog, schema, tableName)));
   }

   public List<TableInfo> getTablesBySchemaQualifiedName(String schema, String tableName)
   {
      return Utils.convertNullToArray(_tableInfosBySchemaQualifiedName.get(new SchemaQualifiedTableName(schema, tableName)));
   }

   public List<TableInfo> getTablesBySimpleName(String tableName)
   {
      return Utils.convertNullToArray(_tableInfosBySimpleName.get(new CaseInsensitiveString(tableName)));
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
         _caseInsensitiveCache.addColumn(ColumnMetaProps.COLUMN_NAME.getCellAsString(cols, i));
      }

      table.setColumnsAsTableLoader(cols);

   }

   public AliasPropertiesDecorator getAliasPropertiesDecorator()
   {
      return _schemaCacheConfig.getAliasPropertiesDecorator();
   }

   public List<TableInfo> getTables(char[] buffer, int offset, int len)
   {
      List<TableInfo> tables = _caseInsensitiveCache.getTables(buffer, offset, len);

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
      return _caseInsensitiveCache.isProcedure(buffer, offset, len);
   }

   public boolean isKeyword(char[] buffer, int offset, int len)
   {
      return _caseInsensitiveCache.isKeyword(buffer, offset, len);
   }

   public boolean isColumn(char[] buffer, int offset, int len)
   {
      return _caseInsensitiveCache.isColumn(buffer, offset, len);
   }
}
