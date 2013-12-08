package org.squirrelsql.session.schemainfo;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.services.sqlwrap.SQLConnection;
import org.squirrelsql.session.DBSchema;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;
import org.squirrelsql.session.objecttree.TableLoader;

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
               _tableInfos.put(buf, _sqlConnection.getTableInfos(buf.getCatalog(), buf.getSchema(), buf.getType()));
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

   public ArrayList<TableInfo> getTableInfos(String catalog, String schema, String tableType)
   {
      return _tableInfos.get(new StructItemTableType(tableType, catalog, schema));
   }

   public ArrayList<ProcedureInfo> getProcedureInfos(String catalog, String schema)
   {
      return _procedureInfos.get(new StructItemProcedureType(catalog, schema));
   }

   public ArrayList<UDTInfo> getUDTInfos(String catalog, String schema)
   {
      return _udtInfos.get(new StructItemUDTType(catalog, schema));
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
}
