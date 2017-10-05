package org.squirrelsql.session.schemainfo.schemacacheloading;

import org.squirrelsql.services.CaseInsensitiveString;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;
import org.squirrelsql.session.schemainfo.CaseInsensitiveCache;
import org.squirrelsql.session.schemainfo.DatabaseStructure;
import org.squirrelsql.session.schemainfo.FullyQualifiedTableName;
import org.squirrelsql.session.schemainfo.SchemaCacheConfig;
import org.squirrelsql.session.schemainfo.SchemaQualifiedTableName;
import org.squirrelsql.session.schemainfo.StructItemProcedureType;
import org.squirrelsql.session.schemainfo.StructItemTableType;
import org.squirrelsql.session.schemainfo.StructItemUDTType;
import org.squirrelsql.table.TableLoader;

import java.util.HashMap;
import java.util.List;

public class SchemaCacheData
{
   private SchemaCacheConfig _schemaCacheConfig;

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


   private CaseInsensitiveCache _caseInsensitiveCache = new CaseInsensitiveCache();

   public SchemaCacheData(SchemaCacheConfig schemaCacheConfig, DatabaseStructure databaseStructure)
   {
      _schemaCacheConfig = schemaCacheConfig;
      _databaseStructure = databaseStructure;
   }

   public SchemaCacheConfig getSchemaCacheConfig()
   {
      return _schemaCacheConfig;
   }

   public DatabaseStructure getDatabaseStructure()
   {
      return _databaseStructure;
   }

   public void setDataTypes(TableLoader dataTypes)
   {
      _dataTypes = dataTypes;
   }

   public TableLoader getDataTypes()
   {
      return _dataTypes;
   }

   public void setDataBaseMetadData(TableLoader dataBaseMetadData)
   {
      _dataBaseMetadData = dataBaseMetadData;
   }

   public TableLoader getDataBaseMetadData()
   {
      return _dataBaseMetadData;
   }

   public TableLoader getNumericFunctions()
   {
      return _numericFunctions;
   }

   public void setNumericFunctions(TableLoader numericFunctions)
   {
      _numericFunctions = numericFunctions;
   }

   public TableLoader getStringFunctions()
   {
      return _stringFunctions;
   }

   public void setStringFunctions(TableLoader stringFunctions)
   {
      _stringFunctions = stringFunctions;
   }

   public TableLoader getSystemFunctions()
   {
      return _systemFunctions;
   }

   public void setSystemFunctions(TableLoader systemFunctions)
   {
      _systemFunctions = systemFunctions;
   }

   public TableLoader getTimeDateFunctions()
   {
      return _timeDateFunctions;
   }

   public void setTimeDateFunctions(TableLoader timeDateFunctions)
   {
      _timeDateFunctions = timeDateFunctions;
   }

   public TableLoader getKeywords()
   {
      return _keywords;
   }

   public void setKeywords(TableLoader keywords)
   {
      _keywords = keywords;
   }

   public HashMap<StructItemTableType, List<TableInfo>> getTableInfos()
   {
      return _tableInfos;
   }

   public HashMap<FullyQualifiedTableName, List<TableInfo>> getTableInfosByFullyQualifiedName()
   {
      return _tableInfosByFullyQualifiedName;
   }

   public HashMap<SchemaQualifiedTableName, List<TableInfo>> getTableInfosBySchemaQualifiedName()
   {
      return _tableInfosBySchemaQualifiedName;
   }

   public HashMap<CaseInsensitiveString, List<TableInfo>> getTableInfosBySimpleName()
   {
      return _tableInfosBySimpleName;
   }

   public HashMap<StructItemProcedureType, List<ProcedureInfo>> getProcedureInfos()
   {
      return _procedureInfos;
   }

   public HashMap<StructItemUDTType, List<UDTInfo>> getUdtInfos()
   {
      return _udtInfos;
   }


   public CaseInsensitiveCache getCaseInsensitiveCache()
   {
      return _caseInsensitiveCache;
   }
}
