package org.squirrelsql.session.schemainfo;

import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.session.DBSchema;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;
import org.squirrelsql.session.objecttree.TableLoader;

import java.util.ArrayList;

public class SchemaCache
{
   private DbConnectorResult _dbConnectorResult;
   private TableLoader _dataTypes;
   private TableLoader _dataBaseMetadData;
   private TableLoader _numericFunctions;
   private TableLoader _stringFunctions;
   private TableLoader _systemFunctions;
   private TableLoader _timeDateFunctions;
   private TableLoader _keywords;

   public SchemaCache(DbConnectorResult dbConnectorResult)
   {
      _dbConnectorResult = dbConnectorResult;
   }

   public boolean shouldLoadSchema(DBSchema schema)
   {
      return true;
   }

   public ArrayList<TableInfo> getTableInfos(String catalog, String schema, String tableType)
   {
      return _dbConnectorResult.getSQLConnection().getTableInfos(catalog, schema, tableType);
   }

   public ArrayList<ProcedureInfo> getProcedureInfos(String catalog, String schema)
   {
      return _dbConnectorResult.getSQLConnection().getProcedureInfos(catalog, schema);
   }

   public ArrayList<UDTInfo> getUDTInfos(String catalog, String schema)
   {
      return _dbConnectorResult.getSQLConnection().getUDTInfos(catalog, schema);
   }

   public TableLoader getTypes()
   {
      if (null == _dataTypes)
      {
         _dataTypes = DataTypesLoader.loadTypes(_dbConnectorResult);
      }
      return _dataTypes;
   }

   public TableLoader getDatabaseMetaData()
   {
      if (null == _dataBaseMetadData)
      {
         _dataBaseMetadData = DataBaseMetaDataLoader.loadMetaData(_dbConnectorResult);
      }
      return _dataBaseMetadData;
   }

   public TableLoader getNumericFunctions()
   {
      if (null == _numericFunctions)
      {
         _numericFunctions = DataBaseMetaDataLoader.loadNumericFunctions(_dbConnectorResult);
      }
      return _numericFunctions;
   }

   public TableLoader getStringFunctions()
   {
      if (null == _stringFunctions)
      {
         _stringFunctions = DataBaseMetaDataLoader.loadStringFunctions(_dbConnectorResult);
      }
      return _stringFunctions;
   }

   public TableLoader getSystemFunctions()
   {
      if (null == _systemFunctions)
      {
         _systemFunctions = DataBaseMetaDataLoader.loadSystemFunctions(_dbConnectorResult);
      }
      return _systemFunctions;
   }

   public TableLoader getTimeDateFunctions()
   {
      if (null == _timeDateFunctions)
      {
         _timeDateFunctions = DataBaseMetaDataLoader.loadTimeDateFunctions(_dbConnectorResult);
      }
      return _timeDateFunctions;
   }

   public TableLoader getKeywords()
   {
      if (null == _keywords)
      {
         _keywords = DataBaseMetaDataLoader.loadKeyWords(_dbConnectorResult);
      }
      return _keywords;
   }
}
