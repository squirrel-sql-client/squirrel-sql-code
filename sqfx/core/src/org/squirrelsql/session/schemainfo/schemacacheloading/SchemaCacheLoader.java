package org.squirrelsql.session.schemainfo.schemacacheloading;

import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.CaseInsensitiveString;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Utils;
import org.squirrelsql.services.progress.Progressable;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;
import org.squirrelsql.session.schemainfo.*;

import java.util.ArrayList;
import java.util.List;

public class SchemaCacheLoader
{
   private I18n _i18n = new I18n(getClass());

   private final SchemaCacheData _scd;
   private DbConnectorResult _dbConnectorResult;

   public SchemaCacheLoader(DbConnectorResult dbConnectorResult, SchemaCacheConfig schemaCacheConfig, DatabaseStructure databaseStructure)
   {
      _dbConnectorResult = dbConnectorResult;
      _scd = new SchemaCacheData(schemaCacheConfig, databaseStructure);
   }

   public void load(Progressable progressable)
   {
      if(_scd.getSchemaCacheConfig().shouldNotLoad())
      {
         return;
      }

      List<StructItem> leaves = _scd.getDatabaseStructure().getLeaves();


      int stepCount = 6 + leaves.size();

      progressable.update(_i18n.t("Loading.data.types"), 1, stepCount);
      _scd.setDataBaseMetadData(DataBaseMetaDataLoader.loadMetaData(_dbConnectorResult.getAliasDecorator().getAlias(), _dbConnectorResult.getSQLConnection()));
      _scd.setDataTypes(DataTypesLoader.loadTypes(_dbConnectorResult.getSQLConnection()));


      progressable.update(_i18n.t("Loading.numeric.functions"), 2, stepCount);
      _scd.setNumericFunctions(DataBaseMetaDataLoader.loadNumericFunctions(_dbConnectorResult.getSQLConnection()));
      _scd.getNumericFunctions().getRows().forEach(e -> _scd.getCaseInsensitiveCache().addProc((String) e.get(0)));

      progressable.update(_i18n.t("Loading.string.functions"), 3, stepCount);
      _scd.setStringFunctions(DataBaseMetaDataLoader.loadStringFunctions(_dbConnectorResult.getSQLConnection()));
      _scd.getStringFunctions().getRows().forEach(e ->  _scd.getCaseInsensitiveCache().addProc((String) e.get(0)));

      progressable.update(_i18n.t("Loading.system.functions"), 4, stepCount);
      _scd.setSystemFunctions(DataBaseMetaDataLoader.loadSystemFunctions(_dbConnectorResult.getSQLConnection()));
      _scd.getSystemFunctions().getRows().forEach(e ->  _scd.getCaseInsensitiveCache().addProc((String) e.get(0)));

      progressable.update(_i18n.t("Loading.time.date.functions"), 5, stepCount);
      _scd.setTimeDateFunctions(DataBaseMetaDataLoader.loadTimeDateFunctions(_dbConnectorResult.getSQLConnection()));
      _scd.getTimeDateFunctions().getRows().forEach(e ->  _scd.getCaseInsensitiveCache().addProc((String) e.get(0)));

      progressable.update(_i18n.t("Loading.keywords"), 6, stepCount);
      _scd.setKeywords(DataBaseMetaDataLoader.loadKeyWords(_dbConnectorResult.getSQLConnection()));
      _scd.getKeywords().getRows().forEach(e ->  _scd.getCaseInsensitiveCache().addKeyword((String) e.get(0)));

      for (String keyWord : DefaultKeywords.KEY_WORDS)
      {
         _scd.getCaseInsensitiveCache().addKeyword(keyWord);
      }


      for (int i = 0; i < leaves.size(); i++)
      {
         if(progressable.isCancelled())
         {
            progressable.update(_i18n.t("Stopping.schema.loading"));
            return;
         }

         StructItem leaf = leaves.get(i);

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

         progressable.update(_i18n.t("Loading.struct.item", leaf.getItemName()), 7 + i, stepCount);

      }

   }




   private void loadMatchingUDTs(StructItemUDTType udtType, String udtName)
   {
      if (udtType.shouldLoad(_scd.getSchemaCacheConfig()))
      {

         List<UDTInfo> buf = _scd.getUdtInfos().get(udtType);
         if(null == buf)
         {
            buf = new ArrayList<>();
            _scd.getUdtInfos().put(udtType, buf);
         }
         buf.addAll(buf);



         _scd.getUdtInfos().put(udtType, _dbConnectorResult.getSQLConnection().getUDTInfos(udtType.getCatalog(), udtType.getSchema(), udtName));
      }
   }

   public void reloadMatchingProcedures(String procedureName)
   {
      List<String> caseSensitiveProcedureNames = _scd.getCaseInsensitiveCache().getMatchingCaseSensitiveProcedureNames(procedureName);

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
      procedureInfos.addAll(Utils.convertNullToArray(_scd.getCaseInsensitiveCache().getProcedures(procedureName)));

      boolean reloaded = false;

      for (ProcedureInfo procedureInfo : procedureInfos)
      {
         StructItemProcedureType procedureType = new StructItemProcedureType(procedureInfo.getCatalog(), procedureInfo.getSchema());
         List<ProcedureInfo> toRemoveFrom = _scd.getProcedureInfos().get(procedureType);

         if(null != toRemoveFrom)
         {
            toRemoveFrom.remove(procedureInfo);
         }

         _scd.getCaseInsensitiveCache().removeProc(procedureName);


         loadMatchingProcedures(procedureType, procedureName);

         reloaded = true;
      }

      return reloaded;
   }

   private void loadMatchingProcedures(StructItemProcedureType procedureType, String procedureName)
   {
      if (procedureType.shouldLoad(_scd.getSchemaCacheConfig()))
      {
         List<ProcedureInfo> procedureInfos = _dbConnectorResult.getSQLConnection().getProcedureInfos(procedureType.getCatalog(), procedureType.getSchema(), procedureName);


         _scd.getProcedureInfos().put(procedureType, procedureInfos);

         List<ProcedureInfo> buf = _scd.getProcedureInfos().get(procedureType);
         if(null == buf)
         {
            buf = new ArrayList<>();
            _scd.getProcedureInfos().put(procedureType, buf);
         }
         buf.addAll(procedureInfos);



         for (ProcedureInfo procedureInfo : procedureInfos)
         {
            _scd.getCaseInsensitiveCache().addProc(procedureInfo.getName());
         }
      }
   }

   public void reloadMatchingTables(String tableName)
   {
      List<String> caseSensitiveTableNames = _scd.getCaseInsensitiveCache().getMatchingCaseSensitiveTableNames(tableName);

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
      tableInfos.addAll(Utils.convertNullToArray(_scd.getCaseInsensitiveCache().getTables(tableName)));

      boolean reloaded = false;

      for (TableInfo tableInfo : tableInfos)
      {
         StructItemTableType tableType = new StructItemTableType(tableInfo.getTableType(), tableInfo.getCatalog(), tableInfo.getSchema());
         FullyQualifiedTableName fullyQualifiedTableName = new FullyQualifiedTableName(tableType.getCatalog(), tableType.getSchema(), tableInfo.getName());
         SchemaQualifiedTableName schemaQualifiedTableName = new SchemaQualifiedTableName(tableType.getSchema(), tableInfo.getName());


         List<TableInfo> buf = _scd.getTableInfos().get(tableType);
         if(null != buf)
         {
            buf.remove(tableInfo);
         }


         _scd.getTableInfosByFullyQualifiedName().remove(fullyQualifiedTableName);
         _scd.getTableInfosBySchemaQualifiedName().remove(schemaQualifiedTableName);
         _scd.getTableInfosBySimpleName().remove(new CaseInsensitiveString(tableInfo.getName()));
         _scd.getCaseInsensitiveCache().removeTable(tableInfo);

         loadMatchingTables(tableType, tableName);

         reloaded = true;
      }

      return reloaded;
   }

   private void loadMatchingTables(StructItemTableType tableType, String tableName)
   {
      if (tableType.shouldLoad(_scd.getSchemaCacheConfig()))
      {
         List<TableInfo> tableInfos = _dbConnectorResult.getSQLConnection().getTableInfos(tableType.getCatalog(), tableType.getSchema(), tableType.getType(), tableName);
         
         

         List<TableInfo> buf = _scd.getTableInfos().get(tableType);
         if(null == buf)
         {
            buf = new ArrayList<>();
            _scd.getTableInfos().put(tableType, buf);
         }
         buf.addAll(tableInfos);


         for (TableInfo tableInfo : tableInfos)
         {
            List<TableInfo> arr;


            FullyQualifiedTableName fullyQualifiedTableName = new FullyQualifiedTableName(tableType.getCatalog(), tableType.getSchema(), tableInfo.getName());
            arr = _scd.getTableInfosByFullyQualifiedName().get(fullyQualifiedTableName);

            if(null == arr)
            {
               arr = new ArrayList<>();
               _scd.getTableInfosByFullyQualifiedName().put(fullyQualifiedTableName, arr);
            }
            arr.add(tableInfo);

            SchemaQualifiedTableName schemaQualifiedTableName = new SchemaQualifiedTableName(tableType.getSchema(), tableInfo.getName());
            arr = _scd.getTableInfosBySchemaQualifiedName().get(schemaQualifiedTableName);

            if(null == arr)
            {
               arr = new ArrayList<>();
               _scd.getTableInfosBySchemaQualifiedName().put(schemaQualifiedTableName, arr);
            }
            arr.add(tableInfo);

            arr = _scd.getTableInfosBySimpleName().get(new CaseInsensitiveString(tableInfo.getName()));

            if(null == arr)
            {
               arr = new ArrayList<>();
               _scd.getTableInfosBySimpleName().put(new CaseInsensitiveString(tableInfo.getName()), arr);
            }
            arr.add(tableInfo);

            _scd.getCaseInsensitiveCache().addTable(tableInfo);

         }

      }
   }

   public SchemaCacheData getSchemaCacheData()
   {
      return _scd;
   }
}
