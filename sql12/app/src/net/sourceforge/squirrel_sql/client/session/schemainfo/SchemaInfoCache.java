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

   List catalogs = new ArrayList();
   List schemas = new ArrayList();

   TreeMap keywords = new TreeMap();
   TreeMap dataTypes = new TreeMap();
   Map functions = Collections.synchronizedMap(new TreeMap());

   /////////////////////////////////////////////////////////////////////////////
   // Schema dependent data.
   // Are changed only in this class
   //
   Map tableNames = Collections.synchronizedMap(new TreeMap());
   Map iTableInfos = Collections.synchronizedMap(new TreeMap());
   Hashtable tableInfosBySimpleName = new Hashtable();

   Map extendedColumnInfosByTableName = Collections.synchronizedMap(new TreeMap());
   Map extColumnInfosByColumnName = Collections.synchronizedMap(new TreeMap());


   Map procedureNames = Collections.synchronizedMap(new TreeMap());
   Map iProcedureInfos = Collections.synchronizedMap(new TreeMap());
   Hashtable procedureInfosBySimpleName = new Hashtable();
   //
   ///////////////////////////////////////////////////////////////////////////

   private java.util.Date _lastCacheRefershDate = new Date();

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


   public boolean loadSchemaIndependentMetaData()
   {
      return _session.getAlias().getSchemaProperties().loadSchemaIndependentMetaData(_schemaPropsCacheIsBasedOn);
   }

   public SchemaLoadInfo[] getAllSchemaLoadInfos()
   {
      return _session.getAlias().getSchemaProperties().getSchemaLoadInfos(_schemaPropsCacheIsBasedOn, _tabelTableTypesCacheable, _viewTableTypesCacheable);
   }

   public SchemaLoadInfo[] getMatchingSchemaLoadInfos(String schemaName)
   {
      return getMatchingSchemaLoadInfos(schemaName, null);
   }

   public SchemaLoadInfo[] getMatchingSchemaLoadInfos(String schemaName, String[] tableTypes)
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
            // null == schemaLoadInfos[0].schemaName is the case when there are no schemas specified
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
         if(type.equals(types[i]))
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

      tableNames.put(ciTableName, tableName);
      iTableInfos.put(info, info);

      ArrayList aITabInfos = (ArrayList) tableInfosBySimpleName.get(ciTableName);
      if(null == aITabInfos)
      {
         aITabInfos = new ArrayList();
         tableInfosBySimpleName.put(ciTableName, aITabInfos);
      }
      aITabInfos.add(info);

      if(null == _schemaPropsCacheIsBasedOn)
      {
         // We are not in initial load, so set refresh date
         _lastCacheRefershDate = new Date();
      }

   }


   public void writeToProcedureCache(IProcedureInfo procedure)
   {
      String proc = procedure.getSimpleName();
      if (proc.length() > 0)
      {
         CaseInsensitiveString ciProc = new CaseInsensitiveString(proc);
         procedureNames.put(ciProc ,proc);

         ArrayList aIProcInfos = (ArrayList) procedureInfosBySimpleName.get(ciProc);
         if(null == aIProcInfos)
         {
            aIProcInfos = new ArrayList();
            procedureInfosBySimpleName.put(ciProc, aIProcInfos);
         }
         aIProcInfos.add(procedure);
      }
      iProcedureInfos.put(procedure, procedure);

      if(null == _schemaPropsCacheIsBasedOn)
      {
         // We are not in initial load, so set refresh date
         _lastCacheRefershDate = new Date();
      }


   }


   public void writeColumsToCache(TableColumnInfo[] infos, CaseInsensitiveString simpleTableName)
   {
      ArrayList ecisInTable = new ArrayList();
      for (int i = 0; i < infos.length; i++)
      {
         ExtendedColumnInfo eci = new ExtendedColumnInfo(infos[i], simpleTableName.toString());
         ecisInTable.add(eci);

         CaseInsensitiveString ciColName = new CaseInsensitiveString(eci.getColumnName());
         ArrayList ecisInColName = (ArrayList) extColumnInfosByColumnName.get(ciColName);
         if(null == ecisInColName)
         {
            ecisInColName = new ArrayList();
            extColumnInfosByColumnName.put(ciColName, ecisInColName);
         }
         ecisInColName.add(eci);
      }

      // Note: A CaseInsensitiveString can be a mutable string.
      // In fact it is a mutable string here because this is usually called from
      // within Syntax coloring which uses a mutable string.
      CaseInsensitiveString imutableString = new CaseInsensitiveString(simpleTableName.toString());
      extendedColumnInfosByTableName.put(imutableString, ecisInTable);
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

   public void clearAll()
   {
      clearSchemaIndependentData();


      clearAllSchemaDependentData();

   }

   private void clearAllSchemaDependentData()
   {
      tableNames.clear();
      iTableInfos.clear();
      tableInfosBySimpleName.clear();

      extColumnInfosByColumnName.clear();
      extendedColumnInfosByTableName.clear();


      procedureNames.clear();
      iProcedureInfos.clear();
      procedureInfosBySimpleName.clear();

      schemas.clear();

   }

   private void clearSchemaIndependentData()
   {
      catalogs.clear();

      keywords.clear();
      dataTypes.clear();
      functions.clear();
   }

   public void clearTables(String catalogName, String schemaName, String simpleName, String[] types)
   {
      for(Iterator i = iTableInfos.keySet().iterator(); i.hasNext();)
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
            ArrayList tableInfos = (ArrayList) tableInfosBySimpleName.get(ciSimpleName);
            tableInfos.remove(ti);
            if(0 == tableInfos.size())
            {
               tableInfosBySimpleName.remove(ciSimpleName);
               tableNames.remove(ciSimpleName);
            }

            ArrayList ecisInTable = (ArrayList) extendedColumnInfosByTableName.get(ciSimpleName);

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
               if(qn1.equals(qn2))
               {
                  j.remove();
               }

               CaseInsensitiveString ciColName = new CaseInsensitiveString(eci.getColumnName());
               ArrayList ecisInColumn = (ArrayList) extColumnInfosByColumnName.get(ciColName);
               ecisInColumn.remove(eci);

               if(0 == ecisInColumn.size())
               {
                  extColumnInfosByColumnName.remove(ciColName);
               }
            }

            if(0 == ecisInTable.size())
            {
               extendedColumnInfosByTableName.remove(ciSimpleName);
            }
         }
      }

   }

   public void clearStoredProcedures(String catalogName, String schemaName, String simpleName)
   {
      for(Iterator i = iProcedureInfos.keySet().iterator(); i.hasNext();)
      {
         IProcedureInfo pi = (IProcedureInfo) i.next();


         boolean matches = matchesMetaString(pi.getCatalogName(), catalogName);
         matches &= matchesMetaString(pi.getSchemaName(), schemaName);
         matches &= matchesMetaString(pi.getSimpleName(), simpleName);


         if(matches)
         {
            i.remove();

            CaseInsensitiveString ciSimpleName = new CaseInsensitiveString(pi.getSimpleName());
            ArrayList procedureInfos = (ArrayList) procedureInfosBySimpleName.get(ciSimpleName);
            procedureInfos.remove(pi);
            if(0 == procedureInfos.size())
            {
               procedureInfosBySimpleName.remove(ciSimpleName);
               procedureNames.remove(ciSimpleName);
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

   public boolean loadSchemaNames()
   {
      return _session.getAlias().getSchemaProperties().loadSchemaNames(_schemaPropsCacheIsBasedOn);
   }

   public SchemaNameLoadInfo getSchemaNameLoadInfo()
   {
      return _session.getAlias().getSchemaProperties().getSchemaNameLoadInfo(_schemaPropsCacheIsBasedOn);
   }
}
