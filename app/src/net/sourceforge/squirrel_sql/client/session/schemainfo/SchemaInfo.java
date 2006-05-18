package net.sourceforge.squirrel_sql.client.session.schemainfo;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.util.*;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;

public class SchemaInfo
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SchemaInfo.class);

   private boolean _loading = false;
   private boolean _loaded = false;

   private SQLDatabaseMetaData _dmd;
   ISession _session = null;


   /** Logger for this class. */
   private static final ILogger s_log = LoggerController.createLogger(SchemaInfo.class);
   private SessionAdapter _sessionListener;
   private static final int LOAD_METHODS_COUNT = 7;
   private static final int MAX_PROGRESS = 100;

   final HashMap _tablesLoadingColsInBackground = new HashMap();


   private SchemaInfoCache _schemaInfoCache;


   private Vector _listeners = new Vector();

   /** the status message that was written by the object tree to be restored */

   public SchemaInfo(IApplication app)
   {
      _sessionListener = new SessionAdapter()
      {
         public void connectionClosedForReconnect(SessionEvent evt)
         {
            if (null != _session && _session.getIdentifier().equals(evt.getSession().getIdentifier()))
            {
               _dmd = null;
            }
         }

         public void reconnected(SessionEvent evt)
         {
            if (null != _session && _session.getIdentifier().equals(evt.getSession().getIdentifier()))
            {
               _dmd = _session.getSQLConnection().getSQLMetaData();
               if (null != _dmd)
               {
                  s_log.info(s_stringMgr.getString("SchemaInfo.SuccessfullyRestoredDatabaseMetaData"));
               }
            }
         }
      };

      if (app != null)
      {
         app.getSessionManager().addSessionListener(_sessionListener);
      }
   }


   public void load(ISession session)
   {
      synchronized (this)
      {
         if(_loading)
         {
            return;
         }

         _loading = true;
      }

      breathing();

      long mstart = System.currentTimeMillis();
      String msg = null;
      if (session == null)
      {
         throw new IllegalArgumentException("Session == null");
      }

      try
      {
         _session = session;
         SQLConnection conn = _session.getSQLConnection();
         _dmd = conn.getSQLMetaData();

         _dmd.clearCache();
         _schemaInfoCache = new SchemaInfoCache();


         int progress = 0;

         try
         {
            // i18n[SchemaInfo.loadingKeywords=Loading keywords]
            msg = s_stringMgr.getString("SchemaInfo.loadingKeywords");
            s_log.debug(msg);
            long start = System.currentTimeMillis();

            int beginProgress = getLoadMethodProgress(progress++);
            int endProgress = getLoadMethodProgress(progress);
            setProgress(msg, beginProgress);
            loadKeywords(msg, beginProgress);

            long finish = System.currentTimeMillis();
            s_log.debug("Keywords loaded in " + (finish - start) + " ms");
         }
         catch (Exception ex)
         {
            s_log.error("Error loading keywords", ex);
         }

         try
         {
            // i18n[SchemaInfo.loadingDataTypes=Loading data types]
            msg = s_stringMgr.getString("SchemaInfo.loadingDataTypes");
            s_log.debug(msg);
            long start = System.currentTimeMillis();

            int beginProgress = getLoadMethodProgress(progress++);
            int endProgress = getLoadMethodProgress(progress);
            setProgress(msg, beginProgress);
            loadDataTypes(msg, beginProgress);

            long finish = System.currentTimeMillis();
            s_log.debug("Data types loaded in " + (finish - start) + " ms");
         }
         catch (Exception ex)
         {
            s_log.error("Error loading data types", ex);
         }

         try
         {
            // i18n[SchemaInfo.loadingFunctions=Loading functions]
            msg = s_stringMgr.getString("SchemaInfo.loadingFunctions");
            s_log.debug(msg);

            long start = System.currentTimeMillis();
            int beginProgress = getLoadMethodProgress(progress++);
            int endProgress = getLoadMethodProgress(progress);
            setProgress(msg, beginProgress);
            loadFunctions(msg, beginProgress);

            long finish = System.currentTimeMillis();
            s_log.debug("Functions loaded in " + (finish - start) + " ms");
         }
         catch (Exception ex)
         {
            s_log.error("Error loading functions", ex);
         }

         progress = loadCatalogs(progress);

         progress = loadSchemas(progress);

         String[] tableTypesToPreload = getTableTypesToCache();
         progress = loadTables(null, null, null, tableTypesToPreload, progress);



         loadStoredProcedures(null, null, null, progress);
      }
      finally
      {
         if (_session != null && _session.getSessionSheet() != null)
         {
            _session.getSessionSheet().setStatusBarProgressFinished();
         }

         _loading = false;
         _loaded = true;
      }
      long mfinish = System.currentTimeMillis();
      s_log.debug("SchemaInfo.load took " + (mfinish - mstart) + " ms");
   }

   private String[] getTableTypesToCache()
   {
      String typeTable = "TABLE";
      String typeView = "VIEW";
      String[] availableTableTypes = new String[]{typeTable, typeView};
      try
      {
         availableTableTypes = _dmd.getTableTypes();
      }
      catch (SQLException e)
      {
         s_log.error("Could not get table types", e);
      }

      if(containsType(availableTableTypes, typeTable))
      {
         _schemaInfoCache.cachedTableTypes.put(typeTable, typeTable);
      }

      if (containsType(availableTableTypes, typeView))
      {
         _schemaInfoCache.cachedTableTypes.put(typeView, typeView);
      }

      String typeSystemTable = "SYSTEM TABLE";
      if (containsType(availableTableTypes, typeSystemTable))
      {
         _schemaInfoCache.cachedTableTypes.put(typeSystemTable, typeSystemTable);
      }

      return (String[]) _schemaInfoCache.cachedTableTypes.keySet().toArray(new String[0]);
   }

   private int loadStoredProcedures(String catalog, String schema, String procNamePattern, int progress)
   {
      String msg;
      try
      {
         // i18n[SchemaInfo.loadingStoredProcedures=Loading stored procedures]
         msg = s_stringMgr.getString("SchemaInfo.loadingStoredProcedures");
         s_log.debug(msg);
         long start = System.currentTimeMillis();

         int beginProgress = getLoadMethodProgress(progress++);
         int endProgress = getLoadMethodProgress(progress);
         setProgress(msg, beginProgress);
         privateLoadStoredProcedures(catalog, schema, procNamePattern, msg, beginProgress);

         long finish = System.currentTimeMillis();
         s_log.debug("stored procedures loaded in " + (finish - start) + " ms");
      }
      catch (Exception ex)
      {
         s_log.error("Error loading stored procedures", ex);
      }
      return progress;
   }

   private int loadTables(String catalog, String schema, String tableNamePattern, String[] types, int progress)
   {
      String msg;
      try
      {
         // i18n[SchemaInfo.loadingTables=Loading tables]
         msg = s_stringMgr.getString("SchemaInfo.loadingTables");
         s_log.debug(msg);
         long start = System.currentTimeMillis();

         int beginProgress = getLoadMethodProgress(progress++);
         setProgress(msg, beginProgress);
         privateLoadTables(catalog, schema, tableNamePattern, types, msg, beginProgress);

         long finish = System.currentTimeMillis();
         s_log.debug("Tables loaded in " + (finish - start) + " ms");
      }
      catch (Exception ex)
      {
         s_log.error("Error loading tables", ex);
      }
      return progress;
   }

   private int loadSchemas(int progress)
   {
      String msg;
      try
      {
         // i18n[SchemaInfo.loadingSchemas=Loading schemas]
         msg = s_stringMgr.getString("SchemaInfo.loadingSchemas");
         s_log.debug(msg);
         long start = System.currentTimeMillis();

         int beginProgress = getLoadMethodProgress(progress++);
         int endProgress = getLoadMethodProgress(progress);
         setProgress(msg, beginProgress);
         privateLoadSchemas(msg, beginProgress, endProgress);

         long finish = System.currentTimeMillis();
         s_log.debug("Schemas loaded in " + (finish - start) + " ms");
      }
      catch (Exception ex)
      {
         s_log.error("Error loading schemas", ex);
      }
      return progress;
   }

   private int loadCatalogs(int progress)
   {
      String msg;
      try
      {
         // i18n[SchemaInfo.loadingCatalogs=Loading catalogs]
         msg = s_stringMgr.getString("SchemaInfo.loadingCatalogs");
         s_log.debug(msg);
         long start = System.currentTimeMillis();

         int beginProgress = getLoadMethodProgress(progress++);
         int endProgress = getLoadMethodProgress(progress);
         setProgress(msg, beginProgress);
         privateLoadCatalogs(msg, beginProgress, endProgress);

         long finish = System.currentTimeMillis();
         s_log.debug("Catalogs loaded in " + (finish - start) + " ms");
      }
      catch (Exception ex)
      {
         s_log.error("Error loading catalogs", ex);
      }
      return progress;
   }

   private int getLoadMethodProgress(int progress)
   {
      return (int)(((double)progress) / ((double)LOAD_METHODS_COUNT) * ((double)MAX_PROGRESS));
   }

   private void setProgress(final String note, final int value)
   {
      breathing();

      if (_session == null || _session.getSessionSheet() == null)
      {
         return;
      }

     _session.getSessionSheet().setStatusBarProgress(note, 0, MAX_PROGRESS, value);
   }

   /**
    * We found that the UI behaves much nicer at startup if
    * loading schema info interupted for little moments.
    */
   private void breathing()
   {
      synchronized(this)
      {
         try
         {
            wait(100);
         }
         catch (InterruptedException e)
         {
            s_log.info("Interrupted", e);
         }
      }
   }

   private void privateLoadStoredProcedures(String catalog, String schema, String procNamePattern, final String msg, final int beginProgress)
   {

      final String objFilter = _session.getProperties().getObjectFilter();
      try
      {
         s_log.debug("Loading stored procedures with filter "+objFilter);

         ProgressCallBack pcb = new ProgressCallBack()
         {
            public void currentlyLoading(String simpleName)
            {
               setProgress(msg + " (" + simpleName + ")", beginProgress);
            }
         };

         IProcedureInfo[] procedures = _dmd.getProcedures(catalog, schema, procNamePattern, pcb);

         for (int i = 0; i < procedures.length; i++)
         {
            String proc = (String) procedures[i].getSimpleName();
            if (proc.length() > 0)
            {
               CaseInsensitiveString ciProc = new CaseInsensitiveString(proc);
               _schemaInfoCache.procedureNames.put(ciProc ,proc);

               ArrayList aIProcInfos = (ArrayList) _schemaInfoCache.procedureInfosBySimpleName.get(ciProc);
               if(null == aIProcInfos)
               {
                  aIProcInfos = new ArrayList();
                  _schemaInfoCache.procedureInfosBySimpleName.put(ciProc, aIProcInfos);
               }
               aIProcInfos.add(procedures[i]);
            }
            _schemaInfoCache.iProcedureInfos.put(procedures[i], procedures[i]);
         }

      }
      catch (Throwable th)
      {
         s_log.error("Failed to load stored procedures", th);
      }

   }

   private void privateLoadCatalogs(String msg, int beginProgress, int endProgress)
   {
      try
      {
         _schemaInfoCache.catalogs.addAll(Arrays.asList(_dmd.getCatalogs()));
      }
      catch (Throwable th)
      {
         s_log.error("failed to load catalog names", th);
      }
   }

   private void privateLoadSchemas(String msg, int beginProgress, int endProgress)
   {
      try
      {
         _schemaInfoCache.schemas.addAll(Arrays.asList(_dmd.getSchemas()));
      }
      catch (Throwable th)
      {
         s_log.error("failed to load schema names", th);
      }
   }

   public boolean isKeyword(String data)
   {
      return isKeyword(new CaseInsensitiveString(data));
   }

   /**
    * Retrieve whether the passed string is a keyword.
    *
    * @param	keyword		String to check.
    *
    * @return	<TT>true</TT> if a keyword.
    */
   public boolean isKeyword(CaseInsensitiveString data)
   {
      if (!_loading && data != null)
      {
         return _schemaInfoCache.keywords.containsKey(data);
      }
      return false;
   }


   public boolean isDataType(String data)
   {
      return isDataType(new CaseInsensitiveString(data));
   }


   /**
    * Retrieve whether the passed string is a data type.
    *
    * @param	keyword		String to check.
    *
    * @return	<TT>true</TT> if a data type.
    */
   public boolean isDataType(CaseInsensitiveString data)
   {
      if (!_loading && data != null)
      {
         return _schemaInfoCache.dataTypes.containsKey(data);
      }
      return false;
   }


   public boolean isFunction(String data)
   {
      return isFunction(new CaseInsensitiveString(data));
   }

   /**
    * Retrieve whether the passed string is a function.
    *
    * @param	keyword		String to check.
    *
    * @return	<TT>true</TT> if a function.
    */
   public boolean isFunction(CaseInsensitiveString data)
   {
      if (!_loading && data != null)
      {
         return _schemaInfoCache.functions.containsKey(data);
      }
      return false;
   }

   public boolean isTable(String data)
   {
      return isTable(new CaseInsensitiveString(data));
   }

   /**
    * Retrieve whether the passed string is a table.
    *
    * @param	keyword		String to check.
    *
    * @return	<TT>true</TT> if a table.
    */
   public boolean isTable(CaseInsensitiveString data)
   {
      if (!_loading && data != null)
      {
         if(_schemaInfoCache.tableNames.containsKey(data))
         {
            loadColumns(data);
            return true;
         }
      }
      return false;
   }


   public boolean isColumn(String data)
   {
      return isColumn(new CaseInsensitiveString(data));
   }


   /**
    * Retrieve whether the passed string is a column.
    *
    * @param	keyword		String to check.
    *
    * @return	<TT>true</TT> if a column.
    */
   public boolean isColumn(CaseInsensitiveString data)
   {
      if (!_loading && data != null)
      {
         return _schemaInfoCache.columnNames.containsKey(data);
      }
      return false;
   }

   /**
    * This method returns the case sensitive name of a table as it is stored
    * in the database.
    * The case sensitive name is needed for example if you want to retrieve
    * a table's meta data. Quote from the API doc of DataBaseMetaData.getTables():
    * Parameters:
    * ...
    * tableNamePattern - a table name pattern; must match the table name as it is stored in the database
    *
    *
    * @param data The tables name in arbitrary case.
    * @return the table name as it is stored in the database
    */
   public String getCaseSensitiveTableName(String data)
   {
      if (!_loading && data != null)
      {
         return (String) _schemaInfoCache.tableNames.get(new CaseInsensitiveString(data));
      }
      return null;
   }

   public String getCaseSensitiveProcedureName(String data)
   {
      if (!_loading && data != null)
      {
         return (String) _schemaInfoCache.procedureNames.get(new CaseInsensitiveString(data));
      }
      return null;
   }




   private void loadKeywords(String msg, int beginProgress)
   {
      try
      {
         setProgress(msg + " (default keywords)", beginProgress);

         for (int i = 0; i < DefaultKeywords.KEY_WORDS.length; i++)
         {
            String kw = DefaultKeywords.KEY_WORDS[i];
            _schemaInfoCache.keywords.put(new CaseInsensitiveString(kw), kw);
         }


         // Extra keywords that this DBMS supports.
         if (_dmd != null)
         {

            setProgress(msg + " (DB specific keywords)", beginProgress);

            String[] sqlKeywords = _dmd.getSQLKeywords();

            for (int i = 0; i < sqlKeywords.length; i++)
            {
               _schemaInfoCache.keywords.put(new CaseInsensitiveString(sqlKeywords[i]), sqlKeywords[i]);
            }


            try
            {
               addSingleKeyword(_dmd.getCatalogTerm());
            }
            catch (Throwable ex)
            {
               s_log.error("Error", ex);
            }

            try
            {
               addSingleKeyword(_dmd.getSchemaTerm());
            }
            catch (Throwable ex)
            {
               s_log.error("Error", ex);
            }

            try
            {
               addSingleKeyword(_dmd.getProcedureTerm());
            }
            catch (Throwable ex)
            {
               s_log.error("Error", ex);
            }
         }
      }
      catch (Throwable ex)
      {
         s_log.error("Error occured creating keyword collection", ex);
      }
   }

   private void loadDataTypes(String msg, int beginProgress)
   {
      try
      {

         DataTypeInfo[] infos = _dmd.getDataTypes();
         for (int i = 0; i < infos.length; i++)
         {
            String typeName = infos[i].getSimpleName();
            _schemaInfoCache.dataTypes.put(new CaseInsensitiveString(typeName), typeName);

            if(0 == i % 100 )
            {
               setProgress(msg + " (" + typeName + ")", beginProgress);
            }

         }
      }
      catch (Throwable ex)
      {
         s_log.error("Error occured creating data types collection", ex);
      }
   }

   private void loadFunctions(String msg, int beginProgress)
   {
      ArrayList buf = new ArrayList();

      try
      {
         setProgress(msg + " (numeric functions)", beginProgress);
         buf.addAll(Arrays.asList(_dmd.getNumericFunctions()));
      }
      catch (Throwable ex)
      {
         s_log.error("Error", ex);
      }

      try
      {
         setProgress(msg + " (string functions)", beginProgress);
         buf.addAll(Arrays.asList((_dmd.getStringFunctions())));
      }
      catch (Throwable ex)
      {
         s_log.error("Error", ex);
      }

      try
      {
         setProgress(msg + " (time/date functions)", beginProgress);
         buf.addAll(Arrays.asList(_dmd.getTimeDateFunctions()));
      }
      catch (Throwable ex)
      {
         s_log.error("Error", ex);
      }

      for (int i = 0; i < buf.size(); i++)
      {
         String func = (String) buf.get(i);
         if (func.length() > 0)
         {
            _schemaInfoCache.functions.put(new CaseInsensitiveString(func) ,func);
         }

      }
   }


   private void addSingleKeyword(String keyword)
   {
      if (keyword != null)
      {
         keyword = keyword.trim();

         if (keyword.length() > 0)
         {
            _schemaInfoCache.keywords.put(new CaseInsensitiveString(keyword), keyword);
         }
      }
   }

   public String[] getKeywords()
   {
      return (String[]) _schemaInfoCache.keywords.values().toArray(new String[_schemaInfoCache.keywords.size()]);
   }

   public String[] getDataTypes()
   {
      return (String[]) _schemaInfoCache.dataTypes.values().toArray(new String[_schemaInfoCache.dataTypes.size()]);
   }

   public String[] getFunctions()
   {
      return (String[]) _schemaInfoCache.functions.values().toArray(new String[_schemaInfoCache.functions.size()]);
   }

   public String[] getTables()
   {
      return (String[]) _schemaInfoCache.tableNames.values().toArray(new String[_schemaInfoCache.tableNames.size()]);
   }

   public String[] getCatalogs()
   {
      return (String[]) _schemaInfoCache.catalogs.toArray(new String[_schemaInfoCache.catalogs.size()]);
   }

   public String[] getSchemas()
   {
      return (String[]) _schemaInfoCache.schemas.toArray(new String[_schemaInfoCache.schemas.size()]);
   }

   public ITableInfo[] getITableInfos()
   {
      return getITableInfos(null, null);
   }

   public ITableInfo[] getITableInfos(String catalog, String schema)
   {
      return getITableInfos(catalog, schema, null);
   }


   public ITableInfo[] getITableInfos(String catalog, String schema, String simpleName)
   {
      String[] types = (String[]) _schemaInfoCache.cachedTableTypes.keySet().toArray(new String[0]);
      return getITableInfos(catalog, schema, simpleName, types);
   }

   public ITableInfo[] getITableInfos(String catalog, String schema, String tableNamePattern, String[] types)
   {
      ITableInfo[] tableInfosForUncachedTypes = getTableInfosForUncachedTypes(catalog, schema, tableNamePattern, types);

      ArrayList ret = new ArrayList();

      ret.addAll(Arrays.asList(tableInfosForUncachedTypes));

      for(Iterator i=_schemaInfoCache.iTableInfos.keySet().iterator(); i.hasNext();)
      {
         ITableInfo iTableInfo = (ITableInfo) i.next();

         if(null != catalog && false == catalog.equalsIgnoreCase(iTableInfo.getCatalogName()) )
         {
            continue;
         }

         if(null != schema && false == schema.equalsIgnoreCase(iTableInfo.getSchemaName()) )
         {
            continue;
         }

         if(false == containsType(types, iTableInfo.getType()))
         {
            continue;
         }

         if(null != tableNamePattern && false == tableNamePattern.endsWith("%") && false == iTableInfo.getSimpleName().equals(tableNamePattern))
         {
            continue;
         }

         if(null != tableNamePattern && tableNamePattern.endsWith("%"))
         {
            String tableNameBegin = tableNamePattern.substring(0, tableNamePattern.length() - 1);
            if(false == iTableInfo.getSimpleName().startsWith(tableNameBegin))
            {
               continue;
            }
         }

         ret.add(iTableInfo);
      }

      return (ITableInfo[]) ret.toArray(new ITableInfo[ret.size()]);
   }

   private ITableInfo[] getTableInfosForUncachedTypes(String catalog, String schema, String tableNamePattern, String[] types)
   {
      try
      {
         ArrayList missingTypes = new ArrayList();
         for (int i = 0; i < types.length; i++)
         {
            if(false == _schemaInfoCache.cachedTableTypes.containsKey(types[i]))
            {
               missingTypes.add(types[i]);
            }
         }

         if(0 < missingTypes.size())
         {
            try
            {
               String[] buf = (String[]) missingTypes.toArray(new String[missingTypes.size()]);
               ProgressCallBack pcb = new ProgressCallBack()
               {
                  public void currentlyLoading(String simpleName)
                  {
                     String msg = s_stringMgr.getString("SchemaInfo.loadingTables");
                     setProgress(msg + " (" + simpleName + ")", 1);
                  }
               };
               return _dmd.getTables(catalog, schema, tableNamePattern, buf, pcb);
            }
            finally
            {
               _session.getSessionSheet().setStatusBarProgressFinished();
            }
         }

      }
      catch (SQLException e)
      {
         s_log.error("Error loading uncached tables", e);
      }

      return new ITableInfo[0];
   }

   private boolean containsType(String[] types, String type)
   {
      for (int i = 0; i < types.length; i++)
      {
         if(type.equals(types[i]))
         {
            return true;
         }
      }
      return false;
   }


   public IProcedureInfo[] getStoredProceduresInfos()
   {
      return getStoredProceduresInfos(null, null);
   }

   public IProcedureInfo[] getStoredProceduresInfos(String catalog, String schema)
   {
      return getStoredProceduresInfos(catalog, schema, null);
   }


   public IProcedureInfo[] getStoredProceduresInfos(String catalog, String schema, String procNamePattern)
   {
      ArrayList ret = new ArrayList();

      for (Iterator i = _schemaInfoCache.iProcedureInfos.keySet().iterator(); i.hasNext();)
      {

         IProcedureInfo iProcInfo = (IProcedureInfo) i.next();
         boolean toAdd = true;
         if (null != catalog && false == catalog.equalsIgnoreCase(iProcInfo.getCatalogName()))
         {
            toAdd = false;
         }

         if (null != schema && false == schema.equalsIgnoreCase(iProcInfo.getSchemaName()))
         {
            toAdd = false;
         }

         if (null != procNamePattern && false == procNamePattern.endsWith("%") && false == iProcInfo.getSimpleName().equals(procNamePattern))
         {
            toAdd = false;
         }

         if (null != procNamePattern && procNamePattern.endsWith("%"))
         {
            String tableNameBegin = procNamePattern.substring(0, procNamePattern.length() - 1);
            if (false == iProcInfo.getSimpleName().startsWith(tableNameBegin))
            {
               toAdd = false;
            }
         }


         if (toAdd)
         {
            ret.add(iProcInfo);
         }
      }

      return (IProcedureInfo[]) ret.toArray(new IProcedureInfo[ret.size()]);
   }


   public boolean isLoaded()
   {
      return _loaded;
   }

   private void privateLoadTables(String catalog,
                                  String schema,
                                  String tableNamePattern,
                                  String[] types,
                                  final String msg,
                                  final int beginProgress)
   {
      try
      {
         ProgressCallBack pcb = new ProgressCallBack()
         {
            public void currentlyLoading(String simpleName)
            {
               setProgress(msg + " (" + simpleName + ")", beginProgress);
            }
         };


         ITableInfo[] infos = _dmd.getTables(catalog, schema, tableNamePattern, types, pcb);
         for (int i = 0; i < infos.length; i++)
         {
            String tableName = infos[i].getSimpleName();
            CaseInsensitiveString ciTableName = new CaseInsensitiveString(tableName);

            _schemaInfoCache.tableNames.put(ciTableName, tableName);
            _schemaInfoCache.iTableInfos.put(infos[i], infos[i]);

            ArrayList aITabInfos = (ArrayList) _schemaInfoCache.tableInfosBySimpleName.get(ciTableName);
            if(null == aITabInfos)
            {
               aITabInfos = new ArrayList();
               _schemaInfoCache.tableInfosBySimpleName.put(ciTableName, aITabInfos);
            }
            aITabInfos.add(infos[i]);

            _schemaInfoCache.extendedColumnInfosByTableName.remove(ciTableName);

            // Note: do not clear _schemaInfoCache.columnNames because the same column
            // names might be used in more than one table.
            // We live with a bit of inexact column names.
         }
      }
      catch (Throwable th)
      {
         s_log.error("failed to load table names", th);
      }
   }

   private void loadColumns(final CaseInsensitiveString tableName)
   {
      try
      {
         if(_schemaInfoCache.extendedColumnInfosByTableName.containsKey(tableName))
         {
            return;
         }


         if (_session.getProperties().getLoadColumnsInBackground())
         {
            if(_tablesLoadingColsInBackground.containsKey(tableName))
            {
               return;
            }

            // Note: A CaseInsensitiveString can be a mutable string.
            // In fact it is a mutable string here because this is usually called from
            // within Syntax coloring which uses a mutable string.
            final CaseInsensitiveString imutableString = new CaseInsensitiveString(tableName.toString());
            _tablesLoadingColsInBackground.put(imutableString, imutableString);
            _session.getApplication().getThreadPool().addTask(new Runnable()
            {
               public void run()
               {
                  try
                  {
                     accessDbToLoadColumns(imutableString);
                     _tablesLoadingColsInBackground.remove(imutableString);
                  }
                  catch (SQLException e)
                  {
                     throw new RuntimeException(e);
                  }
               }
            });
         }
         else
         {
            accessDbToLoadColumns(tableName);
         }
      }
      catch (Throwable th)
      {
         s_log.error("failed to load table names", th);
      }
   }

   private void accessDbToLoadColumns(CaseInsensitiveString tableName)
      throws SQLException
   {
      if (null == _dmd)
      {
         s_log.warn(s_stringMgr.getString("SchemaInfo.UnableToLoadColumns", tableName));
         return;
      }
      String name = getCaseSensitiveTableName(tableName.toString());
      TableInfo ti =
         new TableInfo(null, null, name, "TABLE", null, _dmd);
      TableColumnInfo[] infos = _dmd.getColumnInfo(ti);
      ArrayList result = new ArrayList();
      for (int i = 0; i < infos.length; i++)
      {
         ExtendedColumnInfo buf = new ExtendedColumnInfo(infos[i]);
         result.add(buf);
         _schemaInfoCache.columnNames.put(new CaseInsensitiveString(buf.getColumnName()), buf.getColumnName());

      }


      // Note: A CaseInsensitiveString can be a mutable string.
      // In fact it is a mutable string here because this is usually called from
      // within Syntax coloring which uses a mutable string.
      CaseInsensitiveString imutableString = new CaseInsensitiveString(tableName.toString());
      _schemaInfoCache.extendedColumnInfosByTableName.put(imutableString, result);
   }

   public ExtendedColumnInfo[] getExtendedColumnInfos(String tableName)
   {
      return getExtendedColumnInfos(null, null, tableName);
   }

   public ExtendedColumnInfo[] getExtendedColumnInfos(String catalog, String schema, String tableName)
   {
      CaseInsensitiveString cissTableName = new CaseInsensitiveString(tableName);
      loadColumns(cissTableName);
      ArrayList extColInfo = (ArrayList) _schemaInfoCache.extendedColumnInfosByTableName.get(cissTableName);

      if (null == extColInfo)
      {
         return new ExtendedColumnInfo[0];
      }

      if (null == catalog && null == schema)
      {
         return (ExtendedColumnInfo[]) extColInfo.toArray(new ExtendedColumnInfo[extColInfo.size()]);
      }
      else
      {
         ArrayList ret = new ArrayList();

         for (int i = 0; i < extColInfo.size(); i++)
         {
            ExtendedColumnInfo extendedColumnInfo = (ExtendedColumnInfo) extColInfo.get(i);
            boolean toAdd = true;
            if (null != catalog && false == catalog.equalsIgnoreCase(extendedColumnInfo.getCatalog()))
            {
               toAdd = false;
            }

            if (null != schema && false == schema.equalsIgnoreCase(extendedColumnInfo.getSchema()))
            {
               toAdd = false;
            }

            if (toAdd)
            {
               ret.add(extendedColumnInfo);
            }
         }

         return (ExtendedColumnInfo[]) ret.toArray(new ExtendedColumnInfo[ret.size()]);
      }
   }

   public void dispose()
   {
      // The SessionManager is global to SQuirreL.
      // If we don't remove the listeners the
      // Session won't get Garbeage Collected.
      _session.getApplication().getSessionManager().removeSessionListener(_sessionListener);
   }

   public boolean isProcedure(CaseInsensitiveString data)
   {
      return _schemaInfoCache.procedureNames.containsKey(data);
   }

   public void reloadCache(IDatabaseObjectInfo doi)
   {
      try
      {
         if(doi instanceof ITableInfo)
         {
            ITableInfo ti = (ITableInfo) doi;
            DatabaseObjectType dot = ti.getDatabaseObjectType();

            String[] types = (String[]) _schemaInfoCache.cachedTableTypes.keySet().toArray(new String[0]);
            if(DatabaseObjectType.TABLE == dot)
            {
               types = new String[]{"TABLE"};
            }
            else if (DatabaseObjectType.VIEW == dot)
            {
               types = new String[]{"VIEW"};
            }

            loadTables(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName(), types, 1);
         }
         else if(doi instanceof IProcedureInfo)
         {
            IProcedureInfo pi = (IProcedureInfo) doi;
            loadStoredProcedures(pi.getCatalogName(), pi.getSchemaName(), pi.getSimpleName(), 1);
         }
         else if(DatabaseObjectType.TABLE_TYPE_DBO == doi.getDatabaseObjectType())
         {
            // load all table types with catalog = doi.getCatalog() and schema = doi.getSchema()
            String[] types = (String[]) _schemaInfoCache.cachedTableTypes.keySet().toArray(new String[0]);
            loadTables(doi.getCatalogName(), doi.getSchemaName(), null, types, 0);
         }
         else if(DatabaseObjectType.TABLE == doi.getDatabaseObjectType())
         {
            // load tables with catalog = doi.getCatalog() and schema = doi.getSchema()
            loadTables(doi.getCatalogName(), doi.getSchemaName(), null, new String[]{"TABLE"}, 1);
         }
         else if(DatabaseObjectType.VIEW == doi.getDatabaseObjectType())
         {
            // load views with catalog = doi.getCatalog() and schema = doi.getSchema()
            loadTables(doi.getCatalogName(), doi.getSchemaName(), null, new String[]{"VIEW"}, 1);
         }
         else if(DatabaseObjectType.PROCEDURE == doi.getDatabaseObjectType() || DatabaseObjectType.PROC_TYPE_DBO == doi.getDatabaseObjectType())
         {
            loadStoredProcedures(doi.getCatalogName(), doi.getSchemaName(), null, 1);
         }
         else if(DatabaseObjectType.SCHEMA == doi.getDatabaseObjectType())
         {
            int progress = loadSchemas(1);
            // load tables with catalog = null
            String[] types = (String[]) _schemaInfoCache.cachedTableTypes.keySet().toArray(new String[0]);
            progress = loadTables(null, doi.getSchemaName(), null, types, progress);

            // load procedures with catalog = null
            loadStoredProcedures(null, doi.getSchemaName(), null, progress);
         }
         else if(DatabaseObjectType.CATALOG == doi.getDatabaseObjectType())
         {
            int progress = loadCatalogs(1);
            // load tables with schema = null
            String[] types = (String[]) _schemaInfoCache.cachedTableTypes.keySet().toArray(new String[0]);
            progress = loadTables(doi.getCatalogName(), null, null, types, progress);

            // load procedures with schema = null
            loadStoredProcedures(doi.getCatalogName(), null, null, progress);
         }
         else if(DatabaseObjectType.SESSION == doi.getDatabaseObjectType())
         {
            load(_session);
         }

         fireSchemaInfoUpdate();
      }
      finally
      {
         _session.getSessionSheet().setStatusBarProgressFinished();
      }
   }

   private void fireSchemaInfoUpdate()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            SchemaInfoUpdateListener[] listeners = (SchemaInfoUpdateListener[]) _listeners.toArray(new SchemaInfoUpdateListener[0]);

            for (int i = 0; i < listeners.length; i++)
            {
               listeners[i].schemaInfoUpdated();
            }
         }
      });


   }

   public void addSchemaInfoUpdateListener(SchemaInfoUpdateListener l)
   {
      _listeners.add(l);
   }

   public void removeSchemaInfoUpdateListener(SchemaInfoUpdateListener l)
   {
      _listeners.remove(l);
   }

   public void cleanAndReloadCacheForSimpleTableName(String simpleTableName)
   {
      /////////////////////////////////////////////////////////////////////////////////////////
      // Remove all matching table types from cache
      HashMap caseSensitiveTableNames = new HashMap();

      CaseInsensitiveString caseInsensitiveTableName = new CaseInsensitiveString(simpleTableName);
      String caseSensitiveTableName = (String) _schemaInfoCache.tableNames.remove(caseInsensitiveTableName);

      caseSensitiveTableNames.put(caseSensitiveTableName, caseSensitiveTableName);

      ArrayList iTableInfos = (ArrayList) _schemaInfoCache.tableInfosBySimpleName.remove(caseInsensitiveTableName);

      for (int i = 0; i < iTableInfos.size(); i++)
      {
         ITableInfo iTableInfo = (ITableInfo) iTableInfos.get(i);
         _schemaInfoCache.iTableInfos.remove(iTableInfo);
         caseSensitiveTableNames.put(iTableInfo.getSimpleName(), iTableInfo.getSimpleName());
      }

      _schemaInfoCache.extendedColumnInfosByTableName.remove(caseInsensitiveTableName);

      // Note: do not clear _schemaInfoCache.columnNames because the same column
      // names might be used in more than one table.
      // We live with a bit of inexact column names.

      //
      /////////////////////////////////////////////////////////////////////



      ////////////////////////////////////////////////////////////////////////
      // Reload  all matching table types
      for(Iterator i=caseSensitiveTableNames.keySet().iterator(); i.hasNext();)
      {
         String buf = (String) i.next();
         TableInfo ti = new TableInfo(null, null, buf, null, null, _dmd);
         reloadCache(ti);
      }
      //
      ////////////////////////////////////////////////////////////////////////
   }

   public void cleanAndReloadCacheForSimpleProcedureName(String simpleProcName)
   {
      /////////////////////////////////////////////////////////////////////////////////////////
      // Remove all matching procedures from cache
      HashMap caseSensitiveProcNames = new HashMap();

      CaseInsensitiveString caseInsensitiveProcName = new CaseInsensitiveString(simpleProcName);
      String caseSensitiveProcName = (String) _schemaInfoCache.procedureNames.remove(caseInsensitiveProcName);

      caseSensitiveProcNames.put(caseSensitiveProcName, caseSensitiveProcName);

      ArrayList iProcInfos = (ArrayList) _schemaInfoCache.procedureInfosBySimpleName.remove(caseInsensitiveProcName);

      for (int i = 0; i < iProcInfos.size(); i++)
      {
         IProcedureInfo iProcInfo = (IProcedureInfo) iProcInfos.get(i);
         _schemaInfoCache.iProcedureInfos.remove(iProcInfo);
         caseSensitiveProcNames.put(iProcInfo.getSimpleName(), iProcInfo.getSimpleName());
      }
      //
      /////////////////////////////////////////////////////////////////////



      ////////////////////////////////////////////////////////////////////////
      // Reload  all matching procedure types
      for(Iterator i=caseSensitiveProcNames.keySet().iterator(); i.hasNext();)
      {
         String buf = (String) i.next();
         ProcedureInfo pi = new ProcedureInfo(null, null, buf, null, DatabaseMetaData.procedureResultUnknown, _dmd);
         reloadCache(pi);
      }
      //
      ////////////////////////////////////////////////////////////////////////
   }


   private static class SchemaInfoCache
   {
      final List catalogs = new ArrayList();
      final List schemas = new ArrayList();
      final HashMap cachedTableTypes = new HashMap();

      final TreeMap keywords = new TreeMap();
      final TreeMap dataTypes = new TreeMap();
      final Map functions = Collections.synchronizedMap(new TreeMap());
      final Map tableNames = Collections.synchronizedMap(new TreeMap());
      final Map columnNames = Collections.synchronizedMap(new TreeMap());
      final Map procedureNames = Collections.synchronizedMap(new TreeMap());

      final Map extendedColumnInfosByTableName = Collections.synchronizedMap(new TreeMap());
      final Map iTableInfos = Collections.synchronizedMap(new TreeMap());
      final Map iProcedureInfos = Collections.synchronizedMap(new TreeMap());

      final Hashtable tableInfosBySimpleName = new Hashtable();
      final Hashtable procedureInfosBySimpleName = new Hashtable();

   }


}
