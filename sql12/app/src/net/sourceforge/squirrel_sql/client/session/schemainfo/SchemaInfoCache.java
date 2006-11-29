package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;
import net.sourceforge.squirrel_sql.client.gui.db.SchemaLoadInfo;
import net.sourceforge.squirrel_sql.client.gui.db.SchemaTableTypeCombination;
import net.sourceforge.squirrel_sql.client.gui.db.SchemaNameLoadInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.util.*;
import java.sql.SQLException;
import java.io.Serializable;

public class SchemaInfoCache implements Serializable
{
   private static final ILogger s_log = LoggerController.createLogger(SchemaInfoCache.class);

   private List _catalogs = new ArrayList();
   private List _schemas = new ArrayList();

   private TreeMap _keywords = new TreeMap();
   private TreeMap _dataTypes = new TreeMap();
   private Map _functions = Collections.synchronizedMap(new TreeMap());

   /////////////////////////////////////////////////////////////////////////////
   // Schema dependent data.
   // Are changed only in this class
   //
   private Map _tableNames = Collections.synchronizedMap(new TreeMap());
   private Map _iTableInfos = Collections.synchronizedMap(new TreeMap());
   private Hashtable _tableInfosBySimpleName = new Hashtable();

   private Map _extendedColumnInfosByTableName = Collections.synchronizedMap(new TreeMap());
   private Map _extColumnInfosByColumnName = Collections.synchronizedMap(new TreeMap());


   private Map _procedureNames = Collections.synchronizedMap(new TreeMap());
   private Map _iProcedureInfos = Collections.synchronizedMap(new TreeMap());
   private Hashtable _procedureInfosBySimpleName = new Hashtable();
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
      SchemaLoadInfo[] schemaLoadInfos = _session.getAlias().getSchemaProperties().getSchemaLoadInfos(_schemaPropsCacheIsBasedOn, _tabelTableTypesCacheable, _viewTableTypesCacheable);

      if(   1 == schemaLoadInfos.length
         && null == schemaLoadInfos[0].schemaName
         && false == _session.getApplication().getSessionManager().areAllSchemasAllowed(_session))
      {
         if(false == _session.getApplication().getSessionManager().areAllSchemasAllowed(_session))
         {
            String[] allowedSchemas = _session.getApplication().getSessionManager().getAllowedSchemas(_session);

            ArrayList ret = new ArrayList();

            for (int i = 0; i < allowedSchemas.length; i++)
            {
               SchemaLoadInfo buf = (SchemaLoadInfo) Utilities.cloneObject(schemaLoadInfos[0], getClass().getClassLoader());
               buf.schemaName = allowedSchemas[i];
               
               ret.add(buf);
            }

            schemaLoadInfos = (SchemaLoadInfo[]) ret.toArray(new SchemaLoadInfo[ret.size()]);
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
               SchemaLoadInfo buf = (SchemaLoadInfo) Utilities.cloneObject(schemaLoadInfos[i], getClass().getClassLoader());
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
      ArrayList tableTypeCandidates = new ArrayList();
      tableTypeCandidates.add("TABLE");
      tableTypeCandidates.add("SYSTEM TABLE");

      ArrayList viewTypeCandidates = new ArrayList();
      viewTypeCandidates.add("VIEW");

      try
      {
         ArrayList availableBuf = new ArrayList();
         String[] buf = _session.getSQLConnection().getSQLMetaData().getTableTypes();
         availableBuf.addAll(Arrays.asList(buf));

         for(Iterator i=tableTypeCandidates.iterator();i.hasNext();)
         {
            if(false == availableBuf.contains(i.next()))
            {
               i.remove();
            }
         }

         for(Iterator i=viewTypeCandidates.iterator();i.hasNext();)
         {
            if(false == availableBuf.contains(i.next()))
            {
               i.remove();
            }
         }

//         availableTypesInDataBase = (String[]) availableBuf.toArray(new String[availableBuf.size()]);
      }
      catch (SQLException e)
      {
         s_log.error("Could not get table types", e);
      }

      _tabelTableTypesCacheable = (String[]) tableTypeCandidates.toArray(new String[tableTypeCandidates.size()]);
      _viewTableTypesCacheable = (String[]) viewTypeCandidates.toArray(new String[viewTypeCandidates.size()]);
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


   public void writeToTableCache(ITableInfo info)
   {
      String tableName = info.getSimpleName();
      CaseInsensitiveString ciTableName = new CaseInsensitiveString(tableName);

      _tableNames.put(ciTableName, tableName);
      _iTableInfos.put(info, info);

      ArrayList aITabInfos = (ArrayList) _tableInfosBySimpleName.get(ciTableName);
      if(null == aITabInfos)
      {
         aITabInfos = new ArrayList();
         _tableInfosBySimpleName.put(ciTableName, aITabInfos);
      }
      aITabInfos.add(info);

   }


   public void writeToProcedureCache(IProcedureInfo procedure)
   {
      String proc = procedure.getSimpleName();
      if (proc.length() > 0)
      {
         CaseInsensitiveString ciProc = new CaseInsensitiveString(proc);
         _procedureNames.put(ciProc ,proc);

         ArrayList aIProcInfos = (ArrayList) _procedureInfosBySimpleName.get(ciProc);
         if(null == aIProcInfos)
         {
            aIProcInfos = new ArrayList();
            _procedureInfosBySimpleName.put(ciProc, aIProcInfos);
         }
         aIProcInfos.add(procedure);
      }
      _iProcedureInfos.put(procedure, procedure);
   }


   public void writeColumsToCache(TableColumnInfo[] infos, CaseInsensitiveString simpleTableName)
   {
      ArrayList ecisInTable = new ArrayList();
      for (int i = 0; i < infos.length; i++)
      {
         ExtendedColumnInfo eci = new ExtendedColumnInfo(infos[i], simpleTableName.toString());
         ecisInTable.add(eci);

         CaseInsensitiveString ciColName = new CaseInsensitiveString(eci.getColumnName());
         ArrayList ecisInColName = (ArrayList) _extColumnInfosByColumnName.get(ciColName);
         if(null == ecisInColName)
         {
            ecisInColName = new ArrayList();
            _extColumnInfosByColumnName.put(ciColName, ecisInColName);
         }
         ecisInColName.add(eci);
      }

      // Note: A CaseInsensitiveString can be a mutable string.
      // In fact it is a mutable string here because this is usually called from
      // within Syntax coloring which uses a mutable string.
      CaseInsensitiveString imutableString = new CaseInsensitiveString(simpleTableName.toString());
      _extendedColumnInfosByTableName.put(imutableString, ecisInTable);
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

   void clearAll()
   {
      clearSchemaIndependentData();


      clearAllSchemaDependentData();

   }

   private void clearAllSchemaDependentData()
   {
      _tableNames.clear();
      _iTableInfos.clear();
      _tableInfosBySimpleName.clear();

      _extColumnInfosByColumnName.clear();
      _extendedColumnInfosByTableName.clear();


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

   void clearTables(String catalogName, String schemaName, String simpleName, String[] types)
   {
      for(Iterator i = _iTableInfos.keySet().iterator(); i.hasNext();)
      {
         ITableInfo ti = (ITableInfo) i.next();


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
            i.remove();

            CaseInsensitiveString ciSimpleName = new CaseInsensitiveString(ti.getSimpleName());
            ArrayList tableInfos = (ArrayList) _tableInfosBySimpleName.get(ciSimpleName);
            tableInfos.remove(ti);
            if(0 == tableInfos.size())
            {
               _tableInfosBySimpleName.remove(ciSimpleName);
               _tableNames.remove(ciSimpleName);
            }

            ArrayList ecisInTable = (ArrayList) _extendedColumnInfosByTableName.get(ciSimpleName);

            if(null == ecisInTable)
            {
               // Columns have not yet been loaded 
               continue;
            }

            for(Iterator j=ecisInTable.iterator();j.hasNext();)
            {
               ExtendedColumnInfo eci = (ExtendedColumnInfo) j.next();

               String qn1 = ti.getCatalogName() + "." + ti.getSchemaName() + "." + ti.getSimpleName();
               String qn2 = eci.getCatalog() + "." + eci.getSchema() + "." + eci.getSimpleTableName();
               if(new CaseInsensitiveString(qn1).equals(new CaseInsensitiveString(qn2)))
               {
                  j.remove();
               }

               CaseInsensitiveString ciColName = new CaseInsensitiveString(eci.getColumnName());
               ArrayList ecisInColumn = (ArrayList) _extColumnInfosByColumnName.get(ciColName);

               if (ecisInColumn != null) {
                   ecisInColumn.remove(eci);
    
                   if (0 == ecisInColumn.size())
                   {
                      _extColumnInfosByColumnName.remove(ciColName);
                   }
               } else {
                   if (s_log.isDebugEnabled()) {
                       s_log.debug(
                           "clearTables: no entries in " +
                           "_extColumnInfosByColumnName for column - "+ciColName);
                   }
               }
            }

            if(0 == ecisInTable.size())
            {
               _extendedColumnInfosByTableName.remove(ciSimpleName);
            }
         }
      }

   }

   void clearStoredProcedures(String catalogName, String schemaName, String simpleName)
   {
      for(Iterator i = _iProcedureInfos.keySet().iterator(); i.hasNext();)
      {
         IProcedureInfo pi = (IProcedureInfo) i.next();


         boolean matches = matchesMetaString(pi.getCatalogName(), catalogName);
         matches &= matchesMetaString(pi.getSchemaName(), schemaName);
         matches &= matchesMetaString(pi.getSimpleName(), simpleName);


         if(matches)
         {
            i.remove();

            CaseInsensitiveString ciSimpleName = new CaseInsensitiveString(pi.getSimpleName());
            ArrayList procedureInfos = (ArrayList) _procedureInfosBySimpleName.get(ciSimpleName);
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


   void writeKeywords(Hashtable keywordsBuf)
   {
      _keywords.clear();
      _keywords.putAll(keywordsBuf);
   }


   void writeDataTypes(Hashtable dataTypesBuf)
   {
      _dataTypes.clear();
      _dataTypes.putAll(dataTypesBuf);
   }

   void writeFunctions(Hashtable functionsBuf)
   {
      _functions.clear();
      _functions.putAll(functionsBuf);
   }

   List getCatalogsForReadOnly()
   {
      return _catalogs;
   }

   List getSchemasForReadOnly()
   {
      return _schemas;
   }

   TreeMap getKeywordsForReadOnly()
   {
      return _keywords;
   }

   TreeMap getDataTypesForReadOnly()
   {
      return _dataTypes;
   }

   Map getFunctionsForReadOnly()
   {
      return _functions;
   }

   Map getTableNamesForReadOnly()
   {
      return _tableNames;
   }

   Map getITableInfosForReadOnly()
   {
      return _iTableInfos;
   }

   Hashtable getTableInfosBySimpleNameForReadOnly()
   {
      return _tableInfosBySimpleName;
   }

   Map getExtendedColumnInfosByTableNameForReadOnly()
   {
      return _extendedColumnInfosByTableName;
   }

   Map getExtColumnInfosByColumnNameForReadOnly()
   {
      return _extColumnInfosByColumnName;
   }

   Map getProcedureNamesForReadOnly()
   {
      return _procedureNames;
   }

   Map getIProcedureInfosForReadOnly()
   {
      return _iProcedureInfos;
   }

}
