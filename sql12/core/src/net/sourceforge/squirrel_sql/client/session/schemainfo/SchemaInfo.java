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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;
import net.sourceforge.squirrel_sql.client.gui.db.SchemaLoadInfo;
import net.sourceforge.squirrel_sql.client.gui.db.SchemaNameLoadInfo;
import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IUDTInfo;
import net.sourceforge.squirrel_sql.fw.sql.ProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack;
import net.sourceforge.squirrel_sql.fw.sql.ProgressCallBackAdaptor;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.sql.UDTInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.SwingUtilities;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class SchemaInfo
{
   public static final int TABLE_EXT_NOT_A_TABLE = 0;
   public static final int TABLE_EXT_COLS_LOADED_IN_THIS_CALL = 1;
   public static final int TABLE_EXT_COLS_LOADED_BEFORE = 2;

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SchemaInfo.class);
   private static final ILogger s_log = LoggerController.createLogger(SchemaInfo.class);

   private boolean _loading = false;
   private boolean _loaded = false;

   private SQLDatabaseMetaData _dmd;
   ISession _session = null;


   private SessionAdapter _sessionListener;

   /**
    * The number of load methods
    */
   private static final int LOAD_METHODS_COUNT = 7;

   private static final int MAX_PROGRESS = 100;

   static interface i18n {
       // i18n[SchemaInfo.loadingCatalogs=Loading catalogs]
       String LOADING_CATALOGS_MSG = s_stringMgr.getString("SchemaInfo.loadingCatalogs");
       
       // i18n[SchemaInfo.loadingKeywords=Loading keywords]
       String LOADING_KEYWORDS_MSG = s_stringMgr.getString("SchemaInfo.loadingKeywords");
       
       // i18n[SchemaInfo.loadingDataTypes=Loading data types]
       String LOADING_DATATYPES_MSG = s_stringMgr.getString("SchemaInfo.loadingDataTypes");
       
       // i18n[SchemaInfo.loadingFunctions=Loading functions]
       String LOADING_FUNCTIONS_MSG = s_stringMgr.getString("SchemaInfo.loadingFunctions");

       // i18n[SchemaInfo.loadingTables=Loading tables]
       String LOADING_TABLES_MSG = s_stringMgr.getString("SchemaInfo.loadingTables");
       
       // i18n[SchemaInfo.loadingStoredProcedures=Loading stored procedures]
       String LOADING_PROCS_MSG = s_stringMgr.getString("SchemaInfo.loadingStoredProcedures");
       
       String LOADING_UDTS_MSG = s_stringMgr.getString("SchemaInfo.loadingUDTs");

       // i18n[SchemaInfo.loadingSchemas=Loading schemas]
       String LOADING_SCHEMAS_MSG = s_stringMgr.getString("SchemaInfo.loadingSchemas");
   }
   
   
   final HashMap<CaseInsensitiveString,CaseInsensitiveString> _tablesLoadingColsInBackground = new HashMap<>();


   private SchemaInfoCache _schemaInfoCache;


   private Vector<SchemaInfoUpdateListener> _listeners = new Vector<>();
   private boolean _inInitialLoad;
   private long _initalLoadBeginTime;
   private boolean _sessionStartupTimeHintShown;

   private boolean _schemasAndCatalogsLoaded;
   private boolean _tablesLoaded;
   private boolean _storedProceduresLoaded;
   private boolean _udtsLoaded;

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

         public void sessionClosing(SessionEvent evt)
         {
            SchemaInfoCacheSerializer.store(_session, _schemaInfoCache);
         }
      };

      if (app != null)
      {
         app.getSessionManager().addSessionListener(_sessionListener);
      }
   }


   public void initialLoad(ISession session)
   {
      _session = session;

      breathing();
      _schemaInfoCache = SchemaInfoCacheSerializer.load(_session);

      try
      {
         _inInitialLoad = true;
         _initalLoadBeginTime = System.currentTimeMillis();
         privateLoadAll();
      }
      finally
      {
         _inInitialLoad = false;
         _schemaInfoCache.initialLoadDone();
      }
   }

   public void reloadAll()
   {
      reloadAll(true);
   }

   /**
    * @param fireSchemaInfoUpdate Should only be false when the caller makes sure fireSchemaInfoUpdate() is called later.
    */
   void reloadAll(boolean fireSchemaInfoUpdate)
   {
      SchemaInfoCacheSerializer.deleteCacheFile(_session.getApplication(), _session.getAlias(), false);
      _schemaInfoCache = SchemaInfoCacheSerializer.load(_session);
      privateLoadAll();

      if(fireSchemaInfoUpdate)
      {
      	GUIUtils.processOnSwingEventThread(new Runnable() {
      		public void run() {
      			fireSchemaInfoUpdate();
      		}
      	});
      }
   }

   /**
    * Will re-read all table data into the cache.
    */
   public void reloadAllTables()
   {
   	GUIUtils.processOnSwingEventThread(() -> _session.getSessionPanel().setStatusBarProgress(i18n.LOADING_TABLES_MSG, 0, MAX_PROGRESS, 50));
   	
   	breathing();
   	
      _schemaInfoCache.clearAllTableData();
   	loadTables(null, null, null, null, 0);
      notifyTablesLoaded();   	

   	GUIUtils.processOnSwingEventThread(() -> reloadAllTablesFinished());
   }

   private void reloadAllTablesFinished()
   {
      fireSchemaInfoUpdate();
      _session.getSessionPanel().setStatusBarProgressFinished();
   }

   private void privateLoadAll()
   {
      synchronized (this)
      {
         if(_loading)
         {
            return;
         }

         _loading = true;

         _schemasAndCatalogsLoaded = false;
         _tablesLoaded = false;
         _storedProceduresLoaded = false;
         _udtsLoaded = false;
      }

      breathing();

      try
      {
         ISQLConnection conn = _session.getSQLConnection();
         _dmd = conn.getSQLMetaData();

         _dmd.clearCache();


         int progress = 0;


         progress = loadCatalogs(progress);

         progress = loadSchemas(progress);

         notifySchemasAndCatalogsLoad();

         try
         {
            int beginProgress = getLoadMethodProgress(progress++);
            setProgress(i18n.LOADING_KEYWORDS_MSG, beginProgress);
            loadKeywords(i18n.LOADING_KEYWORDS_MSG, beginProgress);
         }
         catch (Exception ex)
         {
            s_log.error("Error loading keywords", ex);
         }

         try
         {
            int beginProgress = getLoadMethodProgress(progress++);
            setProgress(i18n.LOADING_DATATYPES_MSG, beginProgress);
            loadDataTypes(i18n.LOADING_DATATYPES_MSG, beginProgress);
         }
         catch (Exception ex)
         {
            s_log.error("Error loading data types", ex);
         }

         try
         {
            int beginProgress = getLoadMethodProgress(progress++);
            setProgress(i18n.LOADING_FUNCTIONS_MSG, beginProgress);
            loadGlobalFunctions(i18n.LOADING_FUNCTIONS_MSG, beginProgress);
         }
         catch (Exception ex)
         {
            s_log.error("Error loading functions", ex);
         }

         progress = loadTables(null, null, null, null, progress);
         notifyTablesLoaded();


         progress = loadStoredProcedures(null, null, null, progress);
         notifyStoredProceduresLoaded();

         progress = loadUDTs(null, null, null, progress);
         notifyUDTsLoaded();

      }
      finally
      {
         if (_session != null && _session.getSessionPanel() != null)
         {
            _session.getSessionPanel().setStatusBarProgressFinished();
         }

         _loading = false;
         _loaded = true;
      }
   }

   private void notifyStoredProceduresLoaded()
   {
      synchronized(this)
      {
         _storedProceduresLoaded = true;
         this.notifyAll();
      }
   }

   private void notifyUDTsLoaded()
   {
      synchronized(this)
      {
         _udtsLoaded = true;
         this.notifyAll();
      }
   }

   private void notifyTablesLoaded()
   {
      synchronized(this)
      {
         _tablesLoaded = true;
         this.notifyAll();
      }
   }

   private void notifySchemasAndCatalogsLoad()
   {
      synchronized(this)
      {
         _schemasAndCatalogsLoaded = true;
         this.notifyAll();
      }
   }


   private int loadStoredProcedures(String catalog, String schema, String procNamePattern, int progress)
   {
      try
      {
         int beginProgress = getLoadMethodProgress(progress++);
         setProgress(i18n.LOADING_PROCS_MSG, beginProgress);
         privateLoadStoredProcedures(catalog, 
                                     schema, 
                                     procNamePattern, 
                                     i18n.LOADING_PROCS_MSG, 
                                     beginProgress);
      }
      catch (Exception ex)
      {
         s_log.error("Error loading stored procedures", ex);
      }
      return progress;
   }

   private int loadUDTs(String catalog, String schema, String udtNamePattern, int progress)
   {
      try
      {
         int beginProgress = getLoadMethodProgress(progress++);
         setProgress(i18n.LOADING_UDTS_MSG, beginProgress);
         privateLoadUDTs(catalog,
                         schema,
                         udtNamePattern,
                         i18n.LOADING_UDTS_MSG,
                         beginProgress);

      }
      catch (Exception ex)
      {
         s_log.error("Error loading UDTs", ex);
      }
      return progress;
   }

   private int loadTables(String catalog,
                          String schema, 
                          String tableNamePattern, 
                          String[] types, 
                          int progress)
   {
      try
      {
         int beginProgress = getLoadMethodProgress(progress++);
         setProgress(i18n.LOADING_TABLES_MSG, beginProgress);
         privateLoadTables(catalog, 
                           schema, 
                           tableNamePattern, 
                           types, 
                           i18n.LOADING_TABLES_MSG, 
                           beginProgress);
      }
      catch (Exception ex)
      {
         s_log.error("Error loading tables", ex);
      }
      return progress;
   }

   private int loadSchemas(int progress)
   {
      try
      {
         int beginProgress = getLoadMethodProgress(progress++);
         setProgress(i18n.LOADING_SCHEMAS_MSG, beginProgress);
         privateLoadSchemas();
      }
      catch (Exception ex)
      {
         s_log.error("Error loading schemas", ex);
      }
      return progress;
   }

   private int loadCatalogs(int progress)
   {
      try
      {
         int beginProgress = getLoadMethodProgress(progress++);
         setProgress(i18n.LOADING_CATALOGS_MSG, beginProgress);
         privateLoadCatalogs();
      }
      catch (Exception ex)
      {
         s_log.error("Error loading catalogs", ex);
      }
      return progress;
   }

   private int getLoadMethodProgress(int progress)
   {
      return (int)(((double)progress) / ((double)LOAD_METHODS_COUNT) * (MAX_PROGRESS));
   }

   private void setProgress(final String note, final int value)
   {
      breathing();

      if (_session == null || _session.getSessionPanel() == null)
      {
         return;
      }

     _session.getSessionPanel().setStatusBarProgress(note, 0, MAX_PROGRESS, value);
     
      if(shouldShowLongSessionStartupTimeDialog())
      {
         _sessionStartupTimeHintShown = true;
         SwingUtilities.invokeLater(() -> new SessionStartupTimeHintController(_session));
      }
   }

   private boolean shouldShowLongSessionStartupTimeDialog()
   {
      return _inInitialLoad
         && false == _sessionStartupTimeHintShown
         && false == _session.getAlias().getSchemaProperties().isCacheSchemaIndependentMetaData()
         && SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_ALL_CACHE_NONE == _session.getAlias().getSchemaProperties().getGlobalState()
         && _session.getApplication().getSquirrelPreferences().getShowSessionStartupTimeHint()
         && System.currentTimeMillis() - _initalLoadBeginTime > 3000;
   }

   /**
    * We found that the UI behaves much nicer at startup if
    * loading schema info interupted for little moments.
    */
   private void breathing()
   {
   	// In case this is called by the AWT thread, log a message - this is most likey a bug
      if (SwingUtilities.isEventDispatchThread())
      {
         return;
      }

   	synchronized(this)
      {
	      try
	      {
	         wait(50);
	      }
	      catch (InterruptedException e)
	      {
	      }
   	}
   }

   private void privateLoadStoredProcedures(String catalog, String schema, String procNamePattern, final String msg, final int beginProgress)
   {
      try
      {

         ProgressCallBack pcb = new ProgressCallBackAdaptor()
         {  @Override
            public void currentlyLoading(String simpleName)
            {
               setProgress(msg + " (" + simpleName + ")", beginProgress);
            }
         };


         SchemaLoadInfo[] schemaLoadInfos = _schemaInfoCache.getMatchingSchemaLoadInfos(schema);

         for (int i = 0; i < schemaLoadInfos.length; i++)
         {
            if(schemaLoadInfos[i].isLoadProcedures())
            {
               IProcedureInfo[] procedures = _dmd.getProcedures(catalog, schemaLoadInfos[i].getSchemaName(), procNamePattern, pcb);

               for (int j = 0; j < procedures.length; j++)
               {
                  _schemaInfoCache.writeToProcedureCache(procedures[j]);
               }
            }
         }
      }
      catch (Throwable th)
      {
         s_log.error("Failed to load stored procedures", th);
      }

   }

   private void privateLoadUDTs(String catalog, String schema, String udtNamePattern, final String msg, final int beginProgress)
   {
      try
      {

         ProgressCallBack pcb = new ProgressCallBackAdaptor()
         {  @Override
            public void currentlyLoading(String simpleName)
            {
               setProgress(msg + " (" + simpleName + ")", beginProgress);
            }
         };


         SchemaLoadInfo[] schemaLoadInfos = _schemaInfoCache.getMatchingSchemaLoadInfos(schema);

         for (int i = 0; i < schemaLoadInfos.length; i++)
         {
            if(schemaLoadInfos[i].isLoadUDTs())
            {
               IUDTInfo[] udts = _dmd.getUDTs(catalog, schemaLoadInfos[i].getSchemaName(), udtNamePattern, null, pcb);

               for (int j = 0; j < udts.length; j++)
               {
                  _schemaInfoCache.writeToUDTCache(udts[j]);
               }
            }
         }
      }
      catch (Throwable th)
      {
         s_log.error("Failed to load stored procedures", th);
      }

   }

   private void privateLoadCatalogs()
   {
      try
      {
         if(false == _schemaInfoCache.loadSchemaIndependentMetaData())
         {
            return;
         }

         _schemaInfoCache.writeCatalogs(_dmd.getCatalogs());
      }
      catch (Throwable th)
      {
         s_log.error("failed to load catalog names", th);
      }
   }

   private void privateLoadSchemas()
   {
      try
      {

         _session.getApplication().getSessionManager().clearSchemaCache(_session);

         SchemaNameLoadInfo schemaNameLoadInfo = _schemaInfoCache.getSchemaNameLoadInfo();

         if(SchemaNameLoadInfo.STATE_DONT_REFERESH_SCHEMA_NAMES == schemaNameLoadInfo.state)
         {
            return;
         }

         String[] schemasToWrite;
         if(SchemaNameLoadInfo.STATE_REFERESH_SCHEMA_NAMES_FROM_DB == schemaNameLoadInfo.state)
         {
            schemasToWrite = _session.getApplication().getSessionManager().getAllowedSchemas(_session);
         }
         else if(SchemaNameLoadInfo.STATE_USES_PROVIDED_SCHEMA_NAMES == schemaNameLoadInfo.state)
         {
            schemasToWrite = schemaNameLoadInfo.schemaNames;
         }
         else
         {
            throw new IllegalArgumentException("Unknown SchemaNameLoadInfo.state = " + schemaNameLoadInfo.state);
         }

         _schemaInfoCache.writeSchemas(schemasToWrite);
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
         return _schemaInfoCache.getKeywordsForReadOnly().containsKey(data);
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
         return _schemaInfoCache.getDataTypesForReadOnly().containsKey(data);
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
         return _schemaInfoCache.getFunctionsForReadOnly().containsKey(data);
      }
      return false;
   }

   public boolean isTable(String data)
   {
      return isTable(new CaseInsensitiveString(data));
   }


   public boolean isTable(CaseInsensitiveString data)
   {
      int tableExtRes = isTableExt(data);
      return TABLE_EXT_COLS_LOADED_IN_THIS_CALL == tableExtRes || TABLE_EXT_COLS_LOADED_BEFORE == tableExtRes;

   }

   /**
    * Retrieve whether the passed string is a table and wether this table's colums where loaded before this call.
    *
    * @param	keyword		String to check.
    *
    * @return	<TT>true</TT> if a table.
    */
   public int isTableExt(CaseInsensitiveString data)
   {
      if (!_loading && data != null)
      {
         if(_schemaInfoCache.getTableNamesForReadOnly().containsKey(data))
         {
            if (loadColumns(data))
            {
               return TABLE_EXT_COLS_LOADED_IN_THIS_CALL;
            }
            else
            {
               return TABLE_EXT_COLS_LOADED_BEFORE;
            }
         }
      }
      return TABLE_EXT_NOT_A_TABLE;
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
         return _schemaInfoCache.getExtColumnInfosByColumnNameForReadOnly().containsKey(data);
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
         return _schemaInfoCache.getTableNamesForReadOnly().get(new CaseInsensitiveString(data));
      }
      return null;
   }

   public String getCaseSensitiveProcedureName(String data)
   {
      if (!_loading && data != null)
      {
         return _schemaInfoCache.getProcedureNamesForReadOnly().get(new CaseInsensitiveString(data));
      }
      return null;
   }




   private void loadKeywords(String msg, int beginProgress)
   {
      try
      {
         if(false == _schemaInfoCache.loadSchemaIndependentMetaData())
         {
            return;
         }

         setProgress(msg + " (default keywords)", beginProgress);

         Hashtable<CaseInsensitiveString, String> keywordsBuf = new Hashtable<>();

         for (int i = 0; i < DefaultKeywords.KEY_WORDS.length; i++)
         {
            String kw = DefaultKeywords.KEY_WORDS[i];
            keywordsBuf.put(new CaseInsensitiveString(kw), kw);
         }


         // Extra keywords that this DBMS supports.
         if (_dmd != null)
         {

            setProgress(msg + " (DB specific keywords)", beginProgress);

            String[] sqlKeywords = _dmd.getSQLKeywords();

            for (int i = 0; i < sqlKeywords.length; i++)
            {
            	keywordsBuf.put(new CaseInsensitiveString(sqlKeywords[i]), sqlKeywords[i]);
            }

            String catalogTerm = _dmd.getCatalogTerm();
            if (catalogTerm != null)
            {
               keywordsBuf.put(new CaseInsensitiveString(catalogTerm), catalogTerm);
            }

            String schemaTerm = _dmd.getSchemaTerm();
            if (schemaTerm != null)
            {
               keywordsBuf.put(new CaseInsensitiveString(schemaTerm), schemaTerm);
            }

            String procedureTerm = _dmd.getProcedureTerm();
            if (procedureTerm != null)
            {
               keywordsBuf.put(new CaseInsensitiveString(procedureTerm), procedureTerm);
            }
         }

         _schemaInfoCache.writeKeywords(keywordsBuf);
      }
      catch (Throwable ex)
      {
         s_log.error("Error occurred creating keyword collection", ex);
      }
   }

   private void loadDataTypes(String msg, int beginProgress)
   {
      try
      {
         if(false == _schemaInfoCache.loadSchemaIndependentMetaData())
         {
            return;
         }

         Hashtable<CaseInsensitiveString, String> dataTypesBuf = new Hashtable<>();

         String[] names = _dmd.getDataTypesSimpleNames();
         for (int i = 0; i < names.length; i++)
         {
            String typeName = names[i];
            dataTypesBuf.put(new CaseInsensitiveString(typeName), typeName);

            if(0 == i % 100 )
            {
               setProgress(msg + " (" + typeName + ")", beginProgress);
            }

         }

         _schemaInfoCache.writeDataTypes(dataTypesBuf);
      }
      catch (Throwable ex)
      {
         s_log.error("Error occurred creating data types collection", ex);
      }
   }

   private void loadGlobalFunctions(String msg, int beginProgress)
   {
      if(false == _schemaInfoCache.loadSchemaIndependentMetaData())
      {
         return;
      }

      ArrayList<String> buf = new ArrayList<String>();

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

      Hashtable<CaseInsensitiveString, String> functionsBuf = new Hashtable<>();
      for (int i = 0; i < buf.size(); i++)
      {
         String func = buf.get(i);
         if (func.length() > 0)
         {
            functionsBuf.put(new CaseInsensitiveString(func) ,func);
         }

      }

      _schemaInfoCache.writeFunctions(functionsBuf);
   }



   public String[] getKeywords()
   {
      return _schemaInfoCache.getKeywordsForReadOnly().values().toArray(new String[_schemaInfoCache.getKeywordsForReadOnly().size()]);
   }

   public String[] getDataTypes()
   {
      return _schemaInfoCache.getDataTypesForReadOnly().values().toArray(new String[_schemaInfoCache.getDataTypesForReadOnly().size()]);
   }

   public String[] getFunctions()
   {
      return _schemaInfoCache.getFunctionsForReadOnly().values().toArray(new String[_schemaInfoCache.getFunctionsForReadOnly().size()]);
   }

   public String[] getTables()
   {
      return _schemaInfoCache.getTableNamesForReadOnly().values().toArray(new String[_schemaInfoCache.getTableNamesForReadOnly().size()]);
   }

   public String[] getCatalogs()
   {
      return _schemaInfoCache.getCatalogsForReadOnly().toArray(new String[_schemaInfoCache.getCatalogsForReadOnly().size()]);
   }

   public String[] getSchemas()
   {
      return _schemaInfoCache.getSchemasForReadOnly().toArray(new String[0]);
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
      return getITableInfos(catalog, schema, new ObjFilterMatcher(simpleName), null);
   }

   public ITableInfo[] getITableInfos(String catalog, String schema, ObjFilterMatcher filterMatcher, String[] types)
   {
      ArrayList<ITableInfo> ret = new ArrayList<>();
      if (null != types)
      {
         // By default null == types we return only cached types
         ITableInfo[] tableInfosForUncachedTypes = getTableInfosForUncachedTypes(catalog, schema, filterMatcher, types);
         ret.addAll(Arrays.asList(tableInfosForUncachedTypes));
      }

      List<ITableInfo> tis = _schemaInfoCache.getITableInfosForReadOnly();

      for(ITableInfo iTableInfo : tis)
      {
         if(null != catalog &&
            false == catalog.equalsIgnoreCase(iTableInfo.getCatalogName()) &&
            false == fulfillsPlatformDependendMatches(iTableInfo, catalog)
            )
         {
            continue;
         }

         if(null != schema && false == schema.equalsIgnoreCase(iTableInfo.getSchemaName()) )
         {
            continue;
         }

         if(false == SchemaInfoCache.containsType(types, iTableInfo.getType()))
         {
            continue;
         }

         if(filterMatcher.matches(iTableInfo.getSimpleName()))
         {
            ret.add(iTableInfo);
         }
      }

      return ret.toArray(new ITableInfo[0]);
   }

   private boolean fulfillsPlatformDependendMatches(ITableInfo iTableInfo, String catalog)
   {
      if(SQLDatabaseMetaData.DriverMatch.isComHttxDriver(_session.getSQLConnection()))
      {
         return ( iTableInfo.getCatalogName()==null && "\".\"".equals(catalog));
      }
      else
      {
         return false;
      }


   }

   private ITableInfo[] getTableInfosForUncachedTypes(String catalog, String schema, ObjFilterMatcher filterMatcher, String[] types)
   {
      try
      {
         ArrayList<String> missingTypes = new ArrayList<String>();
         for (int i = 0; i < types.length; i++)
         {
            if(false == _schemaInfoCache.isCachedTableType(types[i]))
            {
               missingTypes.add(types[i]);
            }
         }

         if(0 < missingTypes.size())
         {
            try
            {
               String[] buf = missingTypes.toArray(new String[missingTypes.size()]);
               ProgressCallBack pcb = new ProgressCallBackAdaptor()
               {
               	@Override
                  public void currentlyLoading(String simpleName)
                  {  
                     StringBuilder tmp = new StringBuilder(i18n.LOADING_TABLES_MSG);
                     tmp.append(" (");
                     tmp.append(simpleName);
                     tmp.append(")");
                     setProgress(tmp.toString(), 1);
                  }
               };
               return _dmd.getTables(catalog, schema, filterMatcher.getMetaDataMatchString(), buf, pcb);
            }
            finally
            {
               _session.getSessionPanel().setStatusBarProgressFinished();
            }
         }

      }
      catch (SQLException e)
      {
         s_log.error("Error loading uncached tables", e);
      }

      return new ITableInfo[0];
   }



   public IProcedureInfo[] getStoredProceduresInfos(String catalog, String schema)
   {
      return getStoredProceduresInfos(catalog, schema, new ObjFilterMatcher());
   }


   public IProcedureInfo[] getStoredProceduresInfos(String catalog, String schema, ObjFilterMatcher filterMatcher)
   {
      ArrayList<IProcedureInfo> ret = new ArrayList<>();

      for (Iterator<IProcedureInfo> i = _schemaInfoCache.getIProcedureInfosForReadOnly().keySet().iterator(); i.hasNext();)
      {

         IProcedureInfo iProcInfo = i.next();
         boolean toAdd = true;
         if (null != catalog && false == catalog.equalsIgnoreCase(iProcInfo.getCatalogName()))
         {
            toAdd = false;
         }

         if (null != schema && false == schema.equalsIgnoreCase(iProcInfo.getSchemaName()))
         {
            toAdd = false;
         }
         
         if(false == filterMatcher.matches(iProcInfo.getSimpleName()))
         {
            toAdd = false;
         }


         if (toAdd)
         {
            ret.add(iProcInfo);
         }
      }

      return ret.toArray(new IProcedureInfo[ret.size()]);
   }
   public IUDTInfo[] getUDTInfos(String catalog, String schema)
   {
      return getUDTInfos(catalog, schema, new ObjFilterMatcher());
   }


   public IUDTInfo[] getUDTInfos(String catalog, String schema, ObjFilterMatcher filterMatcher)
   {
      ArrayList<IUDTInfo> ret = new ArrayList<IUDTInfo>();

      for (Iterator<IUDTInfo> i = _schemaInfoCache.getIUDTInfosForReadOnly().keySet().iterator(); i.hasNext();)
      {

         IUDTInfo iProcInfo = i.next();
         boolean toAdd = true;
         if (null != catalog && false == catalog.equalsIgnoreCase(iProcInfo.getCatalogName()))
         {
            toAdd = false;
         }

         if (null != schema && false == schema.equalsIgnoreCase(iProcInfo.getSchemaName()))
         {
            toAdd = false;
         }

         if(false == filterMatcher.matches(iProcInfo.getSimpleName()))
         {
            toAdd = false;
         }


         if (toAdd)
         {
            ret.add(iProcInfo);
         }
      }

      return ret.toArray(new UDTInfo[ret.size()]);
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
         ProgressCallBack pcb = new ProgressCallBackAdaptor()
         {
            @Override
            public void currentlyLoading(String simpleName)
            {
               setProgress(msg + " (" + simpleName + ")", beginProgress);
            }
         };

         SchemaLoadInfo[] schemaLoadInfos = _schemaInfoCache.getMatchingSchemaLoadInfos(schema, types);
         
         for (int i = 0; i < schemaLoadInfos.length; i++)
         {
            ITableInfo[] infos = new ITableInfo[0];

            if (null == schemaLoadInfos[i].getTableTypes() || 0 < schemaLoadInfos[i].getTableTypes().length)
            {
               // With Oracle this takes quite a lot of time if schemaLoadInfos[i].tableTypes has length 0
               // that's why this if is here
               infos = _dmd.getTables(catalog, schemaLoadInfos[i].getSchemaName(), tableNamePattern, schemaLoadInfos[i].getTableTypes(), pcb);
            }

            _schemaInfoCache.writeToTableCache(infos);
         }
      }
      catch (Throwable th)
      {
         s_log.error("failed to load table names", th);
      }
   }

   /**
    *
    * @return true only when the table's columns are loaded within this call.
    */
   private boolean loadColumns(final CaseInsensitiveString tableName)
   {
      try
      {
         if(_schemaInfoCache.didTryLoadingColumns(tableName))
         {
            return false;
         }


         if (_session.getProperties().getLoadColumnsInBackground())
         {
            if(_tablesLoadingColsInBackground.containsKey(tableName))
            {
               return false;
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
                  }
                  catch (Throwable th)
                  {
                     s_log.error("failed to load columns", th);
                  }
                  finally
                  {
                     _tablesLoadingColsInBackground.remove(imutableString);
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

      return true;
   }

   private void accessDbToLoadColumns(CaseInsensitiveString tableName)
   {
      if (null == _dmd)
      {
         s_log.warn(s_stringMgr.getString("SchemaInfo.UnableToLoadColumns", tableName));
         return;
      }

      String name = getCaseSensitiveTableName(tableName.toString());
      TableInfo ti = new TableInfo(null, null, name, "TABLE", null, _dmd);

      try
      {
         TableColumnInfo[] infos = _dmd.getColumnInfo(ti);
         _schemaInfoCache.writeColumsToCache(infos, tableName);
      }
      catch (Throwable th)
      {
         _schemaInfoCache.writeColumsNotAccessible(th, tableName);
      }
   }


   public ExtendedColumnInfo[] getExtendedColumnInfos(String tableName)
   {
      return getExtendedColumnInfos(null, null, tableName);
   }

   public ExtendedColumnInfo[] getExtendedColumnInfos(String catalog, String schema, String tableName)
   {
      CaseInsensitiveString cissTableName = new CaseInsensitiveString(tableName);
      loadColumns(cissTableName);
      List<ExtendedColumnInfo> extColInfo = _schemaInfoCache.getExtendedColumnInfosForReadOnly(cissTableName);

      if (null == extColInfo)
      {
         return new ExtendedColumnInfo[0];
      }

      if (null == catalog && null == schema)
      {
         return extColInfo.toArray(new ExtendedColumnInfo[extColInfo.size()]);
      }
      else
      {
         ArrayList<ExtendedColumnInfo> ret = new ArrayList<ExtendedColumnInfo>();

         for (int i = 0; i < extColInfo.size(); i++)
         {
            ExtendedColumnInfo extendedColumnInfo = extColInfo.get(i);
            boolean toAdd = true;
            if (null != catalog && null != extendedColumnInfo.getCatalog() && false == catalog.equalsIgnoreCase(extendedColumnInfo.getCatalog()))
            {
               toAdd = false;
            }

            if (null != schema && null != extendedColumnInfo.getSchema() && false == schema.equalsIgnoreCase(extendedColumnInfo.getSchema()))
            {
               toAdd = false;
            }

            if (toAdd)
            {
               ret.add(extendedColumnInfo);
            }
         }

         return ret.toArray(new ExtendedColumnInfo[ret.size()]);
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
      return _schemaInfoCache.getProcedureNamesForReadOnly().containsKey(data);
   }

   public void reload(IDatabaseObjectInfo doi)
   {
      reload(doi, true);
   }

   /**
    * @param fireSchemaInfoUpdate Should only be false when the caller makes sure fireSchemaInfoUpdate() is called later.
    */
   void reload(IDatabaseObjectInfo doi, boolean fireSchemaInfoUpdate)
   {
      boolean doReloadAll = false;

      try
      {
         synchronized (this)
         {
            if(_loading)
            {
               return;
            }
            _loading = true;
            _schemasAndCatalogsLoaded = false;
            _tablesLoaded = false;
            _storedProceduresLoaded = false;
            _udtsLoaded = false;

         }


         if (doi instanceof ITableInfo)
         {
            ITableInfo ti = (ITableInfo) doi;

            _schemaInfoCache.clearTables(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName(), null);
            loadTables(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName(), null, 1);

            loadUDTs(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName(), 1);

         }
         else if(doi instanceof IProcedureInfo)
         {
            IProcedureInfo pi = (IProcedureInfo) doi;
            _schemaInfoCache.clearStoredProcedures(pi.getCatalogName(), pi.getSchemaName(), pi.getSimpleName());
            loadStoredProcedures(pi.getCatalogName(), pi.getSchemaName(), pi.getSimpleName(), 1);
         }
         else if(doi instanceof IUDTInfo)
         {
            IUDTInfo udtInfo = (IUDTInfo) doi;
            _schemaInfoCache.clearUDTs(udtInfo.getCatalogName(), udtInfo.getSchemaName(), udtInfo.getSimpleName());
            loadUDTs(udtInfo.getCatalogName(), udtInfo.getSchemaName(), udtInfo.getSimpleName(), 1);
         }
         else if(DatabaseObjectType.TABLE_TYPE_DBO == doi.getDatabaseObjectType())
         {
            // load all table types with catalog = doi.getCatalog() and schema = doi.getSchema()
            _schemaInfoCache.clearTables(doi.getCatalogName(), doi.getSchemaName(), null, null);
            loadTables(doi.getCatalogName(), doi.getSchemaName(), null, null, 0);
         }
         else if(DatabaseObjectType.TABLE == doi.getDatabaseObjectType())
         {
            // load tables with catalog = doi.getCatalog() and schema = doi.getSchema()
            _schemaInfoCache.clearTables(doi.getCatalogName(), doi.getSchemaName(), null, new String[]{"TABLE"});
            loadTables(doi.getCatalogName(), doi.getSchemaName(), null, new String[]{"TABLE"}, 1);
         }
         else if(DatabaseObjectType.VIEW == doi.getDatabaseObjectType())
         {
            // load views with catalog = doi.getCatalog() and schema = doi.getSchema()
            _schemaInfoCache.clearTables(doi.getCatalogName(), doi.getSchemaName(), null, new String[]{"VIEW"});
            loadTables(doi.getCatalogName(), doi.getSchemaName(), null, new String[]{"VIEW"}, 1);
         }
         else if(DatabaseObjectType.PROCEDURE == doi.getDatabaseObjectType() || DatabaseObjectType.PROC_TYPE_DBO == doi.getDatabaseObjectType())
         {
            _schemaInfoCache.clearStoredProcedures(doi.getCatalogName(), doi.getSchemaName(), null);
            loadStoredProcedures(doi.getCatalogName(), doi.getSchemaName(), null, 1);
         }
         else if(DatabaseObjectType.UDT == doi.getDatabaseObjectType() || DatabaseObjectType.UDF_TYPE_DBO == doi.getDatabaseObjectType())
         {
            _schemaInfoCache.clearUDTs(doi.getCatalogName(), doi.getSchemaName(), null);
            loadUDTs(doi.getCatalogName(), doi.getSchemaName(), null, 1);
         }
         else if(DatabaseObjectType.SCHEMA == doi.getDatabaseObjectType())
         {
            //int progress = loadSchemas(1);
            // load tables with catalog = null
            _schemaInfoCache.clearTables(null, doi.getSchemaName(), null, null);
            int progress = loadTables(null, doi.getSchemaName(), null, null, 1);

            // load procedures with catalog = null
            _schemaInfoCache.clearStoredProcedures(null, doi.getSchemaName(), null);
            loadStoredProcedures(null, doi.getSchemaName(), null, progress);

            // load UDTs with catalog = null
            _schemaInfoCache.clearUDTs(null, doi.getSchemaName(), null);
            loadUDTs(null, doi.getSchemaName(), null, progress);
         }
         else if(DatabaseObjectType.CATALOG == doi.getDatabaseObjectType())
         {
            //int progress = loadCatalogs(1);
            // load tables with schema = null
            _schemaInfoCache.clearTables(doi.getCatalogName(), null, null, null);
            int progress = loadTables(doi.getCatalogName(), null, null, null, 1);

            // load procedures with schema = null
            _schemaInfoCache.clearStoredProcedures(doi.getCatalogName(), null, null);
            loadStoredProcedures(doi.getCatalogName(), null, null, progress);

            // load UDTs with schema = null
            _schemaInfoCache.clearUDTs(doi.getCatalogName(), null, null);
            loadUDTs(doi.getCatalogName(), null, null, progress);
         }
         else if(DatabaseObjectType.SESSION == doi.getDatabaseObjectType())
         {
            doReloadAll = true;
         }

         // If called here it is called far to often and restoring selection in the
         // Object tree doesn't work.
         //fireSchemaInfoUpdate();
      }
      finally
      {
         _session.getSessionPanel().setStatusBarProgressFinished();
         _loading = false;
         _schemasAndCatalogsLoaded = true;
         _tablesLoaded = true;
         _storedProceduresLoaded = true;
         _udtsLoaded = true;

         notifySchemasAndCatalogsLoad();
         notifyTablesLoaded();
         notifyStoredProceduresLoaded();

         if(doReloadAll)
         {
            reloadAll(fireSchemaInfoUpdate);
         }
         else
         {
            if(fireSchemaInfoUpdate)
            {
               fireSchemaInfoUpdate();
            }
         }
      }
   }

   public void fireSchemaInfoUpdate()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            SchemaInfoUpdateListener[] listeners = _listeners.toArray(new SchemaInfoUpdateListener[0]);

            for (int i = 0; i < listeners.length; i++)
            {
               listeners[i].schemaInfoUpdated();
            }
         }
      });


   }

   public void addSchemaInfoUpdateListener(SchemaInfoUpdateListener l)
   {
      _listeners.remove(l);
      _listeners.add(l);
   }

   public void removeSchemaInfoUpdateListener(SchemaInfoUpdateListener l)
   {
      _listeners.remove(l);
   }


   public void refreshCacheForSimpleTableName(String simpleTableName)
   {
      refreshCacheForSimpleTableName(simpleTableName, true);
   }

   /**
    * @param fireSchemaInfoUpdate Should only be false when the caller makes sure fireSchemaInfoUpdate() is called later.
    */
   void refreshCacheForSimpleTableName(String simpleTableName, boolean fireSchemaInfoUpdate)
   {
      HashMap<String, String> caseSensitiveTableNames = new HashMap<String, String>();

      CaseInsensitiveString caseInsensitiveTableName = new CaseInsensitiveString(simpleTableName);
      String caseSensitiveTableName = _schemaInfoCache.getTableNamesForReadOnly().get(caseInsensitiveTableName);

      caseSensitiveTableNames.put(caseSensitiveTableName, caseSensitiveTableName);

      ////////////////////////////////////////////////////////////////////////
      // Reload  all matching table types
      for(Iterator<String> i=caseSensitiveTableNames.keySet().iterator(); i.hasNext();)
      {
         String buf = i.next();
         TableInfo ti = new TableInfo(null, null, buf, null, null, _dmd);
         reload(ti, fireSchemaInfoUpdate);
      }
      //
      ////////////////////////////////////////////////////////////////////////
   }

   public void refreshCacheForSimpleProcedureName(String simpleProcName)
   {
      refreshCacheForSimpleProcedureName(simpleProcName, true);
   }

   /**
    * @param fireSchemaInfoUpdate Should only be false when the caller makes sure fireSchemaInfoUpdate() is called later.
    */
   void refreshCacheForSimpleProcedureName(String simpleProcName, boolean fireSchemaInfoUpdate)
   {
      HashMap<String, String> caseSensitiveProcNames = new HashMap<String, String>();

      CaseInsensitiveString caseInsensitiveProcName = new CaseInsensitiveString(simpleProcName);
      String caseSensitiveProcName = _schemaInfoCache.getProcedureNamesForReadOnly().remove(caseInsensitiveProcName);

      caseSensitiveProcNames.put(caseSensitiveProcName, caseSensitiveProcName);

      ////////////////////////////////////////////////////////////////////////
      // Reload  all matching procedure types
      for(Iterator<String> i=caseSensitiveProcNames.keySet().iterator(); i.hasNext();)
      {
         String buf = i.next();
         ProcedureInfo pi = new ProcedureInfo(null, null, buf, null, DatabaseMetaData.procedureResultUnknown, _dmd);
         reload(pi, fireSchemaInfoUpdate);
      }
      //
      ////////////////////////////////////////////////////////////////////////

   }

   public void waitTillSchemasAndCatalogsLoaded()
   {
      try
      {
         synchronized (this)
         {
            while (false == _schemasAndCatalogsLoaded)
            {
               this.wait();
            }
         }
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void waitTillTablesLoaded()
   {
      try
      {
         synchronized (this)
         {
            while (false == _tablesLoaded)
            {
               this.wait();
            }
         }
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void waitTillStoredProceduresLoaded()
   {
      try
      {
         synchronized (this)
         {
            while (false == _storedProceduresLoaded)
            {
               this.wait();
            }
         }
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void waitTillUDTsLoaded()
   {
      try
      {
         synchronized (this)
         {
            while (false == _udtsLoaded)
            {
               this.wait();
            }
         }
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

   public SQLDatabaseMetaData getSQLDatabaseMetaData()
   {
      return _dmd;
   }
}
