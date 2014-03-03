package org.squirrelsql.session.schemainfo;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.services.sqlwrap.SQLConnection;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;
import org.squirrelsql.table.TableLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class SchemaCache
{
   private final Alias _alias;
   private final SQLConnection _sqlConnection;
   private final SchemaCacheConfig _schemaCacheConfig;

   private DatabaseStructure _databaseStructure;

   private TableLoader _dataTypes;
   private TableLoader _dataBaseMetadData;
   private TableLoader _numericFunctions;
   private TableLoader _stringFunctions;
   private TableLoader _systemFunctions;
   private TableLoader _timeDateFunctions;
   private TableLoader _keywords;

   private HashMap<StructItemTableType, ArrayList<TableInfo>> _tableInfos = new HashMap<>();
   private HashMap<QualifiedTableName, ArrayList<TableInfo>> _tableInfosByQualifiedName = new HashMap<>();
   private HashMap<StructItemProcedureType, ArrayList<ProcedureInfo>> _procedureInfos = new HashMap<>();
   private HashMap<StructItemUDTType, ArrayList<UDTInfo>> _udtInfos = new HashMap<>();


   public SchemaCache(Alias alias, SQLConnection sqlConnection, SchemaCacheConfig schemaCacheConfig, DatabaseStructure databaseStructure)
   {
      _alias = alias;
      _sqlConnection = sqlConnection;
      _schemaCacheConfig = schemaCacheConfig;
      _databaseStructure = databaseStructure;
   }

   public void load()
   {
      if(_schemaCacheConfig.shouldNotLoad())
      {
         return;
      }


      _dataBaseMetadData = DataBaseMetaDataLoader.loadMetaData(_alias, _sqlConnection);
      _dataTypes = DataTypesLoader.loadTypes(_sqlConnection);
      _numericFunctions = DataBaseMetaDataLoader.loadNumericFunctions(_sqlConnection);
      _stringFunctions = DataBaseMetaDataLoader.loadStringFunctions(_sqlConnection);
      _systemFunctions = DataBaseMetaDataLoader.loadSystemFunctions(_sqlConnection);
      _timeDateFunctions = DataBaseMetaDataLoader.loadTimeDateFunctions(_sqlConnection);
      _keywords = DataBaseMetaDataLoader.loadKeyWords(_sqlConnection);

      ArrayList<StructItem> leaves = _databaseStructure.getLeaves();

      for (StructItem leaf : leaves)
      {
         if(leaf instanceof StructItemTableType)
         {
            StructItemTableType buf = (StructItemTableType) leaf;
            if (buf.shouldLoad(_schemaCacheConfig))
            {
               ArrayList<TableInfo> tableInfos = _sqlConnection.getTableInfos(buf.getCatalog(), buf.getSchema(), buf.getType());
               _tableInfos.put(buf, tableInfos);


               for (TableInfo tableInfo : tableInfos)
               {
                  QualifiedTableName key = new QualifiedTableName(buf.getCatalog(), buf.getSchema(), tableInfo.getName());
                  ArrayList<TableInfo> arr = _tableInfosByQualifiedName.get(key);

                  if(null == arr)
                  {
                     arr = new ArrayList<>();
                     _tableInfosByQualifiedName.put(key, arr);
                  }
                  arr.add(tableInfo);
               }

            }
         }
         else if(leaf instanceof StructItemProcedureType)
         {
            StructItemProcedureType buf = (StructItemProcedureType) leaf;
            if (buf.shouldLoad(_schemaCacheConfig))
            {
               _procedureInfos.put(buf, _sqlConnection.getProcedureInfos(buf.getCatalog(), buf.getSchema()));
            }
         }
         else if(leaf instanceof StructItemUDTType)
         {
            StructItemUDTType buf = (StructItemUDTType) leaf;
            if (buf.shouldLoad(_schemaCacheConfig))
            {
               _udtInfos.put(buf, _sqlConnection.getUDTInfos(buf.getCatalog(), buf.getSchema()));
            }
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

   public ArrayList<TableInfo> getTableInfosExact(String catalog, String schema, String tableType)
   {
      return convertNullToArray(_tableInfos.get(new StructItemTableType(tableType, catalog, schema)));
   }

   public ArrayList<TableInfo> getTableInfosMatching(String catalog, String schema, String tableType)
   {
      ArrayList<TableInfo> ret = new ArrayList<>();

      for (StructItemTableType structItemTableType : _tableInfos.keySet())
      {
         if(structItemTableType.matchesRespectNull(catalog, schema, tableType))
         {
            ret.addAll(_tableInfos.get(structItemTableType));
         }
      }

      return ret;

   }

   private <T> ArrayList<T> convertNullToArray(ArrayList<T> arr)
   {
      if (null == arr)
      {
         return new ArrayList<>();
      }

      return arr;
   }

   public ArrayList<ProcedureInfo> getProcedureInfos(String catalog, String schema)
   {
      return convertNullToArray(_procedureInfos.get(new StructItemProcedureType(catalog, schema)));
   }

   public ArrayList<UDTInfo> getUDTInfos(String catalog, String schema)
   {
      return convertNullToArray(_udtInfos.get(new StructItemUDTType(catalog, schema)));
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

   public ArrayList<StructItemCatalog> getCatalogs()
   {
      return _databaseStructure.getCatalogs();
   }

   public ArrayList<StructItemSchema> getSchemas()
   {
      return convertNullToArray(_databaseStructure.getSchemas());
   }

   public ArrayList<String> getAllFunctions()
   {
      ArrayList<String> ret = new ArrayList<>();

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

   public ArrayList<StructItemSchema> getSchemasByName(String schemaName)
   {
      return convertNullToArray(_databaseStructure.getSchemasByName(schemaName));
   }

   public ArrayList<StructItemSchema> getSchemaByNameAsArray(String catalogName, String schemaName)
   {
      return convertNullToArray(_databaseStructure.getSchemaByNameAsArray(catalogName, schemaName));
   }

   public ArrayList<TableInfo> getTablesByName(String catalog, String schema, String tableName)
   {
      return convertNullToArray(_tableInfosByQualifiedName.get(new QualifiedTableName(catalog, schema, tableName)));
   }
}
