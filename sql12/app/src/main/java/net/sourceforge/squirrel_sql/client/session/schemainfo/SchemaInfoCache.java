package net.sourceforge.squirrel_sql.client.session.schemainfo;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;
import net.sourceforge.squirrel_sql.client.gui.db.SchemaLoadInfo;
import net.sourceforge.squirrel_sql.client.gui.db.SchemaNameLoadInfo;
import net.sourceforge.squirrel_sql.client.gui.db.SchemaTableTypeCombination;
import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class is Serializable and yet doesn't declare serialVersionUID.  This is done intentionally so that 
 * the SchemaInfoCacheSerializer can detect incompatible class changes (by catching Exception when attempting
 * to read the serialized file).  This was deemed to be a less error-prone way of handling changes to this
 * class file's definition, then having to remember to consider whether or not serialVersionUID should be 
 * incremented for any given change.  Therefore, it is very important to not introduce a serialVersionUID
 * class member, as forgetting to update it might lead to undetected incompatible class changes that don't 
 * manifest themselves during de-serialization, but occur later in the application when missing members are
 * invoked (for example).  This more conservative approach can lead to the serialized file being removed upon
 * installing a newer version of SQuirreL more often than necessary, but it seemed to us to be better than the 
 * alternative.
 */
@SuppressWarnings("serial")
public class SchemaInfoCache implements Serializable
{
   private static final ILogger s_log =
       LoggerController.createLogger(SchemaInfoCache.class);

   private List<String> _catalogs = new ArrayList<String>();
   private List<String> _schemas = new ArrayList<String>();

   private TreeMap<CaseInsensitiveString, String> _keywords = 
       new TreeMap<CaseInsensitiveString, String>();
   private TreeMap<CaseInsensitiveString, String> _dataTypes = 
       new TreeMap<CaseInsensitiveString, String>();
   private Map<CaseInsensitiveString, String> _functions = 
       Collections.synchronizedMap(new TreeMap<CaseInsensitiveString, String>());

   /////////////////////////////////////////////////////////////////////////////
   // Schema dependent data.
   // Are changed only in this class
   //
   private TreeMap<CaseInsensitiveString, String> _internalTableNameTreeMap =
       new TreeMap<CaseInsensitiveString, String>();
   
   private Map<CaseInsensitiveString, String> _tableNames = 
       Collections.synchronizedMap(_internalTableNameTreeMap);
   

   /** 
    * This data structure can be accessed by multiple concurrent threads.  
    * Traversal via iterators is fast and cannot encounter interference from 
    * other threads otherwise ConcurrentModificationExceptions may 
    * result (Bug #1752089)
    * 
    * One other thing: it must maintain the order in which items were inserted
    * so that traversal yeilds insertion order (Bug 1805954).
    */
   private CopyOnWriteArrayList<ITableInfo> _iTableInfos =
       new CopyOnWriteArrayList<ITableInfo>();
   
   private Hashtable<CaseInsensitiveString, List<ITableInfo>> _tableInfosBySimpleName = 
       new Hashtable<CaseInsensitiveString, List<ITableInfo>>();

   private SchemaInfoColumnCache _schemaInfoColumnCache = new SchemaInfoColumnCache();


   private Map<CaseInsensitiveString, String> _procedureNames = 
       Collections.synchronizedMap(new TreeMap<CaseInsensitiveString, String>());
   
   private Map<IProcedureInfo, IProcedureInfo> _iProcedureInfos = 
       Collections.synchronizedMap(new TreeMap<IProcedureInfo, IProcedureInfo>());
   
   private Hashtable<CaseInsensitiveString, List<IProcedureInfo>> _procedureInfosBySimpleName = 
       new Hashtable<CaseInsensitiveString, List<IProcedureInfo>>();
   //
   ///////////////////////////////////////////////////////////////////////////

   private SQLAliasSchemaProperties _schemaPropsCacheIsBasedOn;

   private transient String[] _viewTableTypesCacheable;
   private transient String[] _tabelTableTypesCacheable;
   //private transient String[] availableTypesInDataBase;

   private transient ISession _session = null;


   void setSession(ISession session)
   {
      _session = session;
      initTypes();
   }


   boolean loadSchemaIndependentMetaData()
   {
      return _session.getAlias().getSchemaProperties().loadSchemaIndependentMetaData(_schemaPropsCacheIsBasedOn);
   }

   private SchemaLoadInfo[] getAllSchemaLoadInfos()
   {
      SQLAliasSchemaProperties schemaProps =  
          _session.getAlias().getSchemaProperties();
      SchemaLoadInfo[] schemaLoadInfos = 
          schemaProps.getSchemaLoadInfos(_schemaPropsCacheIsBasedOn, 
                                         _tabelTableTypesCacheable, 
                                         _viewTableTypesCacheable);
      SessionManager sessionMgr = _session.getApplication().getSessionManager();
      boolean allSchemasAllowed = sessionMgr.areAllSchemasAllowed(_session);
      
      if(   1 == schemaLoadInfos.length
         && null == schemaLoadInfos[0].schemaName
         && false == allSchemasAllowed)
      {
         if(false == allSchemasAllowed)
         {
            String[] allowedSchemas = sessionMgr.getAllowedSchemas(_session);

            ArrayList<SchemaLoadInfo> ret = new ArrayList<SchemaLoadInfo>();

            for (int i = 0; i < allowedSchemas.length; i++)
            {
               SchemaLoadInfo buf = (SchemaLoadInfo) Utilities.cloneObject(
                     schemaLoadInfos[0], getClass().getClassLoader());
               buf.schemaName = allowedSchemas[i];
               
               ret.add(buf);
            }
            schemaLoadInfos = ret.toArray(new SchemaLoadInfo[ret.size()]);
         }
      }
      return schemaLoadInfos;
   }

   SchemaLoadInfo[] getMatchingSchemaLoadInfos(String schemaName)
   {
      return getMatchingSchemaLoadInfos(schemaName, null);
   }

   SchemaLoadInfo[] getMatchingSchemaLoadInfos(String schemaName, String[] tableTypes)
   {
      if(null == schemaName)
      {
         return getAllSchemaLoadInfos();
      }

      SchemaLoadInfo[] schemaLoadInfos = getAllSchemaLoadInfos();
      for (int i = 0; i < schemaLoadInfos.length; i++)
      {
         if(null == schemaLoadInfos[i].schemaName || schemaLoadInfos[i].schemaName.equals(schemaName))
         {
            
            // null == schemaLoadInfos[0].schemaName is the case when there are no _schemas specified
            // schemaLoadInfos.length will then be 1.
            schemaLoadInfos[i].schemaName = schemaName;
            if(null != tableTypes)
            {
               SchemaLoadInfo buf = (SchemaLoadInfo) Utilities.cloneObject(
                     schemaLoadInfos[i], getClass().getClassLoader());
               buf.tableTypes = tableTypes;
               return new SchemaLoadInfo[]{buf};
            }

            return new SchemaLoadInfo[]{schemaLoadInfos[i]};
         }
      }
      throw new IllegalArgumentException("Unknown Schema " + schemaName);
   }

   private void initTypes()
   {
      ArrayList<String> tableTypeCandidates = new ArrayList<String>();
      tableTypeCandidates.add("TABLE");
      tableTypeCandidates.add("SYSTEM TABLE");

      ArrayList<String> viewTypeCandidates = new ArrayList<String>();
      viewTypeCandidates.add("VIEW");

      try
      {
         ArrayList<String> availableBuf = new ArrayList<String>();
         String[] buf = _session.getSQLConnection().getSQLMetaData().getTableTypes();
         availableBuf.addAll(Arrays.asList(buf));

         for(Iterator<String> i=tableTypeCandidates.iterator();i.hasNext();)
         {
            if(false == availableBuf.contains(i.next()))
            {
               i.remove();
            }
         }

         for(Iterator<String> i=viewTypeCandidates.iterator();i.hasNext();)
         {
            if(false == availableBuf.contains(i.next()))
            {
               i.remove();
            }
         }
      }
      catch (SQLException e)
      {
         s_log.error("Could not get table types", e);
      }

      _tabelTableTypesCacheable = tableTypeCandidates.toArray(new String[tableTypeCandidates.size()]);
      _viewTableTypesCacheable = viewTypeCandidates.toArray(new String[viewTypeCandidates.size()]);
   }

   public boolean isCachedTableType(String type)
   {
      boolean found = false;

      for (int i = 0; i < _viewTableTypesCacheable.length; i++)
      {
         if(_viewTableTypesCacheable[i].equals(type))
         {
            found = true;
            break;
         }
      }

      for (int i = 0; i < _tabelTableTypesCacheable.length; i++)
      {
         if(_tabelTableTypesCacheable[i].equals(type))
         {
            found = true;
            break;
         }
      }

      return found;
   }

   static boolean containsType(String[] types, String type)
   {
      if(null == types)
      {
         return true;
      }

      for (int i = 0; i < types.length; i++)
      {
         if(type.trim().equalsIgnoreCase(types[i]))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Adds the specified array of ITableInfos to the internal list(s), and sorts
    * the combination.
    *  
    * @param infos the array of ITableInfos to add.
    */
   public void writeToTableCache(ITableInfo[] infos) {
      for (ITableInfo info : infos) {
         String tableName = info.getSimpleName();
         CaseInsensitiveString ciTableName = new CaseInsensitiveString(tableName);
         _tableNames.put(ciTableName, tableName);
         
         List<ITableInfo> aITabInfos = _tableInfosBySimpleName.get(ciTableName);
         if(null == aITabInfos)
         {
            aITabInfos = new ArrayList<ITableInfo>();
            _tableInfosBySimpleName.put(ciTableName, aITabInfos);
         }
         aITabInfos.add(info);         
      }
      // CopyOnWriteArrayList is unfortunately not sort-able as a List.  So this
      // will throw an UnsupportedOperationException:
      //
      // Collections.sort(_iTableInfos, new TableInfoSimpleNameComparator());
      //
      // The following is the best approach according to concurrency master 
      // Doug Lea, in this post: 
      // http://osdir.com/ml/java.jsr.166-concurrency/2004-06/msg00001.html
      //
      // Here we copy the existing internal array into a new array that
      // is large enough to hold the original and new elements.  Then sort it.  
      // And finally, create a new CopyOnWriteArrayList with the sorted array.
      
      /* Now, create an array large enough to hold the original and the new */
      int currSize = _iTableInfos.size();
      ITableInfo[] tableArr = 
         _iTableInfos.toArray(new ITableInfo[currSize+infos.length]);
      /* 
       * Append the new tables to the new array, starting at the end of the 
       * original 
       */
      for (int i = 0; i < infos.length; i++) {
         tableArr[currSize + i] = infos[i];
      }
      
      /* Sort it and store in a new CopyOnWriteArrayList */
      Arrays.sort(tableArr, new TableInfoSimpleNameComparator());
      _iTableInfos = new CopyOnWriteArrayList<ITableInfo>(tableArr);
   }
   
   /**
    * Adds a single ITableInfo to the internal list(s) and re-sorts.  This 
    * should not be called in a tight loop iterating over a list of ITableInfos.
    * If the caller is looping over an array of ITableInfo objects, please use 
    * the version that accepts the ITableInfo array instead.
    * 
    * @param info the ITableInfo to add.
    */
   public void writeToTableCache(ITableInfo info)
   {
      writeToTableCache(new ITableInfo[] { info });      
   }


   public void writeToProcedureCache(IProcedureInfo procedure)
   {
      String proc = procedure.getSimpleName();
      if (proc.length() > 0)
      {
         CaseInsensitiveString ciProc = new CaseInsensitiveString(proc);
         _procedureNames.put(ciProc ,proc);

         List<IProcedureInfo> aIProcInfos = _procedureInfosBySimpleName.get(ciProc);
         if(null == aIProcInfos)
         {
            aIProcInfos = new ArrayList<IProcedureInfo>();
            _procedureInfosBySimpleName.put(ciProc, aIProcInfos);
         }
         aIProcInfos.add(procedure);
      }
      _iProcedureInfos.put(procedure, procedure);
   }


   public void writeColumsToCache(TableColumnInfo[] infos, CaseInsensitiveString simpleTableName)
   {
      _schemaInfoColumnCache.writeColumsToCache(infos, simpleTableName);
   }

   public void writeColumsNotAccessible(Throwable th, CaseInsensitiveString tableName)
   {
      _schemaInfoColumnCache.writeColumsNotAccessible(th, tableName);
   }



   void initialLoadDone()
   {
      /**
       * When _schemaPropsCacheIsBasedOn is null all loading will be done like there was no cache.
       *
       * This will make sure loading only heeds the cache during initial loading.
       *
       * Any further loading (via Object tree or tool bar) will be treated as a Cache refresh.
       */
      _schemaPropsCacheIsBasedOn = null;
   }

   void prepareSerialization()
   {
      _schemaPropsCacheIsBasedOn = _session.getAlias().getSchemaProperties();

      if(false == _schemaPropsCacheIsBasedOn.isCacheSchemaIndependentMetaData())
      {
         clearSchemaIndependentData();
      }

      if(SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_ALL_CACHE_NONE == _schemaPropsCacheIsBasedOn.getGlobalState())
      {
         clearAllSchemaDependentData();
      }
      else if(SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS == _schemaPropsCacheIsBasedOn.getGlobalState())
      {
         SchemaTableTypeCombination[] tableTypeCombis =
            _schemaPropsCacheIsBasedOn.getAllSchemaTableTypeCombinationsNotToBeCached(_tabelTableTypesCacheable, _viewTableTypesCacheable);

         for (int i = 0; i < tableTypeCombis.length; i++)
         {
            clearTables(null, tableTypeCombis[i].schemaName, null, tableTypeCombis[i].types);
         }

         String[] procedureSchemas = _schemaPropsCacheIsBasedOn.getAllSchemaProceduresNotToBeCached();
         for (int i = 0; i < procedureSchemas.length; i++)
         {
            clearStoredProcedures(null, procedureSchemas[i], null);
         }


      }

   }

   private void clearAllSchemaDependentData()
   {
      _tableNames.clear();

      _internalTableNameTreeMap.clear();

      synchronized(_iTableInfos) {
          _iTableInfos.clear();
      }
      _tableInfosBySimpleName.clear();

      _schemaInfoColumnCache.clearColumns();


      _procedureNames.clear();
      _iProcedureInfos.clear();
      _procedureInfosBySimpleName.clear();

      _schemas.clear();

   }

   private void clearSchemaIndependentData()
   {
      _catalogs.clear();

      _keywords.clear();
      _dataTypes.clear();
      _functions.clear();
   }

   void clearAllTableData() {
   	_iTableInfos = new CopyOnWriteArrayList<ITableInfo>();
   	_tableInfosBySimpleName = new Hashtable<CaseInsensitiveString, List<ITableInfo>>();
   	_tableNames = Collections.synchronizedMap(_internalTableNameTreeMap);
   }
   
   void clearTables(String catalogName, String schemaName, String simpleName, String[] types)
   {
      for(Iterator<ITableInfo> i = _iTableInfos.iterator(); i.hasNext();)
      {
         ITableInfo ti = i.next();

         boolean matches = matchesMetaString(ti.getCatalogName(), catalogName);
         matches &= matchesMetaString(ti.getSchemaName(), schemaName);
         matches &= matchesMetaString(ti.getSimpleName(), simpleName);

         if(null != types)
         {
            boolean found = false;
            for (int j = 0; j < types.length; j++)
            {
               if(types[j].equals(ti.getType()))
               {
                  found = true;
                  break;
               }
            }

            matches &= found;
         }

         if(matches)
         {
             // CopyOnWriteArrayList has snapshot iterators that don't support 
             // iterator.remove()
             _iTableInfos.remove(ti);

            CaseInsensitiveString ciSimpleTableName = new CaseInsensitiveString(ti.getSimpleName());
            List<ITableInfo> tableInfos = _tableInfosBySimpleName.get(ciSimpleTableName);
            tableInfos.remove(ti);
            if(0 == tableInfos.size())
            {
               _tableInfosBySimpleName.remove(ciSimpleTableName);
               _tableNames.remove(ciSimpleTableName);
            }

            _schemaInfoColumnCache.clearColumns(ciSimpleTableName);
         }
      }

   }

   void clearStoredProcedures(String catalogName, String schemaName, String simpleName)
   {
      for(Iterator<IProcedureInfo> i = _iProcedureInfos.keySet().iterator(); i.hasNext();)
      {
         IProcedureInfo pi = i.next();


         boolean matches = matchesMetaString(pi.getCatalogName(), catalogName);
         matches &= matchesMetaString(pi.getSchemaName(), schemaName);
         matches &= matchesMetaString(pi.getSimpleName(), simpleName);


         if(matches)
         {
            i.remove();

            CaseInsensitiveString ciSimpleName = new CaseInsensitiveString(pi.getSimpleName());
            List<IProcedureInfo> procedureInfos = _procedureInfosBySimpleName.get(ciSimpleName);
            procedureInfos.remove(pi);
            if(0 == procedureInfos.size())
            {
               _procedureInfosBySimpleName.remove(ciSimpleName);
               _procedureNames.remove(ciSimpleName);
            }

         }
      }
   }


   private boolean matchesMetaString(String s, String toCheck)
   {
      if(null == s || null == toCheck)
      {
         return true;
      }

      return s.equals(toCheck);
   }

   SchemaNameLoadInfo getSchemaNameLoadInfo()
   {
      return _session.getAlias().getSchemaProperties().getSchemaNameLoadInfo(_schemaPropsCacheIsBasedOn);
   }

   void writeCatalogs(String[] catalogs)
   {
      this._catalogs.clear();
      this._catalogs.addAll(Arrays.asList(catalogs));
   }

   void writeSchemas(String[] schemasToWrite)
   {
      _schemas.clear();
      _schemas.addAll(Arrays.asList(schemasToWrite));
   }


   void writeKeywords(Hashtable<CaseInsensitiveString, String> keywordsBuf)
   {
      _keywords.clear();
      _keywords.putAll(keywordsBuf);
   }


   void writeDataTypes(Hashtable<CaseInsensitiveString, String> dataTypesBuf)
   {
      _dataTypes.clear();
      _dataTypes.putAll(dataTypesBuf);
   }

   void writeFunctions(Hashtable<CaseInsensitiveString, String> functionsBuf)
   {
      _functions.clear();
      _functions.putAll(functionsBuf);
   }

   List<String> getCatalogsForReadOnly()
   {
      return _catalogs;
   }

   List<String> getSchemasForReadOnly()
   {
      return _schemas;
   }

   TreeMap<CaseInsensitiveString, String> getKeywordsForReadOnly()
   {
      return _keywords;
   }

   TreeMap<CaseInsensitiveString, String> getDataTypesForReadOnly()
   {
      return _dataTypes;
   }

   Map<CaseInsensitiveString, String> getFunctionsForReadOnly()
   {
      return _functions;
   }

   Map<CaseInsensitiveString, String> getTableNamesForReadOnly()
   {
      return _internalTableNameTreeMap;
   }

   List<ITableInfo> getITableInfosForReadOnly()
   {
      return _iTableInfos;
   }

   Hashtable<CaseInsensitiveString, List<ITableInfo>> getTableInfosBySimpleNameForReadOnly()
   {
      return _tableInfosBySimpleName;
   }


   public boolean didTryLoadingColumns(CaseInsensitiveString tableName)
   {
      return _schemaInfoColumnCache.didTryLoadingColumns(tableName);
   }

   public List<ExtendedColumnInfo> getExtendedColumnInfosForReadOnly(CaseInsensitiveString cissTableName)
   {
      return _schemaInfoColumnCache.getExtendedColumnInfosForReadOnly(cissTableName);
   }


   Map<CaseInsensitiveString, List<ExtendedColumnInfo>> getExtColumnInfosByColumnNameForReadOnly()
   {
      return _schemaInfoColumnCache.getExtColumnInfosByColumnNameForReadOnly();
   }

   Map<CaseInsensitiveString, String> getProcedureNamesForReadOnly()
   {
      return _procedureNames;
   }

   Map<IProcedureInfo, IProcedureInfo> getIProcedureInfosForReadOnly()
   {
      return _iProcedureInfos;
   }

   /**
    * When SchemaInfoCache has been deserialized the the constants in DatabaseObjectType
    * still come from the last serialisation. Thus the == operator won't work
    * unless we replace the DatabaseObjectTypes
    *
    */
   void replaceDatabaseObjectTypeConstantObjectsByConstantObjectsOfThisVM()
   {
      for (ITableInfo iTableInfo : _iTableInfos)
      {
         if(iTableInfo instanceof TableInfo)
         {
            ((TableInfo)iTableInfo).replaceDatabaseObjectTypeConstantObjectsByConstantObjectsOfThisVM();
         }
      }

      for (IProcedureInfo iProcedureInfo : _iProcedureInfos.keySet())
      {
         if(iProcedureInfo instanceof DatabaseObjectInfo)
         {
            ((DatabaseObjectInfo)iProcedureInfo).replaceDatabaseObjectTypeConstantObjectsByConstantObjectsOfThisVM(DatabaseObjectType.PROCEDURE);
         }
      }

   }

   /**
    * A comparator for ITableInfos that compares them using their simple name.
    * All other data (such as schema) is ignored, since it isn't likely that we 
    * will need to compare tables in multiple schemas/catalogs in the same list.
    */
   private class TableInfoSimpleNameComparator implements
         Comparator<ITableInfo> {
      public int compare(ITableInfo o1, ITableInfo o2) {
         return o1.getSimpleName().compareTo(o2.getSimpleName());
      }
   }

}
