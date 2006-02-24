package net.sourceforge.squirrel_sql.client.session;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DataTypeInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SchemaInfo
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SchemaInfo.class);

	private boolean _loading = false;
	private boolean _loaded = false;

	private SQLDatabaseMetaData _dmd;
	private final HashMap _keywords = new HashMap();
	private final HashMap _dataTypes = new HashMap();
	private final HashMap _functions = new HashMap();
	private final HashMap _tables = new HashMap();
	private final HashMap _columns = new HashMap();
	private Hashtable _extendedColumnInfosByTableName = new Hashtable();
	private HashMap _tablesLoadingColsInBackground = new HashMap();
	private final List _catalogs = new ArrayList();
	private final List _schemas = new ArrayList();
	private final List _extendedtableInfos = new ArrayList();
	private ISession _session = null;
	private IProcedureInfo[] _procInfos = new IProcedureInfo[0];
	private HashMap _procedures = new HashMap();
    private ProgressMonitor progress = null; 

	/** Logger for this class. */
	private static final ILogger s_log =
				LoggerController.createLogger(SchemaInfo.class);
	private SessionAdapter _sessionListener;

	public SchemaInfo(IApplication app)
	{
		_sessionListener = new SessionAdapter()
		{
			public void connectionClosedForReconnect(SessionEvent evt)
			{
				if(null != _session && _session.getIdentifier().equals(evt.getSession().getIdentifier()))
				{
					_dmd = null;
				}
			}

			public void reconnected(SessionEvent evt)
			{
				if(null != _session && _session.getIdentifier().equals(evt.getSession().getIdentifier()))
				{
					_dmd = _session.getSQLConnection().getSQLMetaData();
					if(null != _dmd)
					{
						s_log.info(s_stringMgr.getString("SchemaInfo.SuccessfullyRestoredDatabaseMetaData"));
					}
				}
			}
		};
        if (app != null) {
            app.getSessionManager().addSessionListener(_sessionListener);
        }
	}

	public void load(ISession session)
	{
        long mstart = System.currentTimeMillis();
        initProgressMonitor(session);
        String msg = null;
        if (session == null)
		{
			throw new IllegalArgumentException("Session == null");
		}

		_loading = true;
		try
		{
			_session = session;
			SQLConnection conn = _session.getSQLConnection();
			final SQLDatabaseMetaData sqlDmd = conn.getSQLMetaData();
			_dmd = sqlDmd;

			try
			{
                // i18n[SchemaInfo.loadingKeywords=Loading keywords]
                msg = s_stringMgr.getString("SchemaInfo.loadingKeywords");
				s_log.debug(msg);
                setNote(msg);
                long start = System.currentTimeMillis();
				loadKeywords(sqlDmd);
                setProgress(1);
                long finish = System.currentTimeMillis();
				s_log.debug("Keywords loaded in "+(finish-start)+" ms");
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
                setNote(msg);
                long start = System.currentTimeMillis();
				loadDataTypes(sqlDmd);
                setProgress(2);
                long finish = System.currentTimeMillis();
				s_log.debug("Data types loaded in "+(finish-start)+" ms");
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
                setNote(msg);
                long start = System.currentTimeMillis();
				loadFunctions(sqlDmd);
                setProgress(3);
                long finish = System.currentTimeMillis();
				s_log.debug("Functions loaded in "+(finish-start)+" ms");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading functions", ex);
			}

			try
			{
                // i18n[SchemaInfo.loadingCatalogs=Loading catalogs]
                msg = s_stringMgr.getString("SchemaInfo.loadingCatalogs");
				s_log.debug(msg);
                setNote(msg);
                long start = System.currentTimeMillis();
				loadCatalogs(sqlDmd);
                setProgress(4);
                long finish = System.currentTimeMillis();
				s_log.debug("Catalogs loaded in "+(finish-start)+" ms");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading catalogs", ex);
			}

			try
			{
                // i18n[SchemaInfo.loadingSchemas=Loading schemas]
                msg = s_stringMgr.getString("SchemaInfo.loadingSchemas");
				s_log.debug(msg);
                setNote(msg);
                long start = System.currentTimeMillis();
				loadSchemas(sqlDmd);
                setProgress(5);
                long finish = System.currentTimeMillis();
				s_log.debug("Schemas loaded in "+(finish-start)+" ms");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading schemas", ex);
			}


			try
			{
                // i18n[SchemaInfo.loadingTables=Loading tables]
                msg = s_stringMgr.getString("SchemaInfo.loadingTables");
				s_log.debug(msg);
                setNote(msg);
                long start = System.currentTimeMillis();
				loadTables(sqlDmd);
                setProgress(6);
                long finish = System.currentTimeMillis();
				s_log.debug("Tables loaded in "+(finish-start)+" ms");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading tables", ex);
			}

			try
			{
                // i18n[SchemaInfo.loadingStoredProcedures=Loading stored procedures]
                msg = s_stringMgr.getString("SchemaInfo.loadingStoredProcedures");
				s_log.debug(msg);
                setNote(msg);
                long start = System.currentTimeMillis();
                loadStoredProcedures(sqlDmd);
                setProgress(7);
                long finish = System.currentTimeMillis();
				s_log.debug("stored procedures loaded in "+(finish-start)+" ms");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading stored procedures", ex);
			}
		}
		finally
		{
			_loading = false;
			_loaded = true;
		}
        long mfinish = System.currentTimeMillis();
        s_log.debug("SchemaInfo.load took "+(mfinish-mstart)+" ms");
	}

    private void initProgressMonitor(final ISession session) {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                String message = null;
                String note = "note";
                
                progress = new ProgressMonitor(session.getApplication().getMainFrame(),
                                               message,
                                               note,
                                               0,
                                               7);
                progress.setMillisToDecideToPopup(500);
                progress.setMillisToPopup(500);                        
            }
        });
    }
    
    private void setNote(final String note) {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                progress.setNote(note);
            }
        });
    }
    
    private void setProgress(final int nv) {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                progress.setProgress(nv);
            }
        });        
    }
    
	private void loadStoredProcedures(SQLDatabaseMetaData dmd)
	{

		final String objFilter = _session.getProperties().getObjectFilter();
		try
		{
			s_log.debug("Loading stored procedures with filter "+objFilter);
			_procInfos = dmd.getProcedures(null, null,objFilter != null && objFilter.length() > 0 ? objFilter :"%");

			for (int i = 0; i < _procInfos.length; i++)
			{
				String proc = (String) _procInfos[i].getSimpleName();
				if (proc.length() > 0)
				{
					_procedures.put(new CaseInsensitiveString(_procInfos[i].getSimpleName()) ,proc);
				}

			}

		}
		catch (Throwable th)
		{
			s_log.error("Failed to load stored procedures", th);
		}

	}

	private void loadCatalogs(SQLDatabaseMetaData dmd)
	{
		try
		{
            _catalogs.addAll(Arrays.asList(dmd.getCatalogs()));
		}
		catch (Throwable th)
		{
			s_log.error("failed to load catalog names", th);
		}
	}

	private void loadSchemas(SQLDatabaseMetaData dmd)
	{
		try
		{
            _schemas.addAll(Arrays.asList(dmd.getSchemas()));
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
			return _keywords.containsKey(data);
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
			return _dataTypes.containsKey(data);
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
			return _functions.containsKey(data);
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
			if(_tables.containsKey(data))
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
			return _columns.containsKey(data);
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
			return (String) _tables.get(new CaseInsensitiveString(data));
		}
		return null;
	}

	private void loadKeywords(SQLDatabaseMetaData dmd)
	{
		try
		{
			_keywords.put(new CaseInsensitiveString("ABSOLUTE"), "ABSOLUTE");
			_keywords.put(new CaseInsensitiveString("ACTION"), "ACTION");
			_keywords.put(new CaseInsensitiveString("ADD"), "ADD");
			_keywords.put(new CaseInsensitiveString("ALL"), "ALL");
			_keywords.put(new CaseInsensitiveString("ALTER"), "ALTER");
			_keywords.put(new CaseInsensitiveString("AND"), "AND");
			_keywords.put(new CaseInsensitiveString("AS"), "AS");
			_keywords.put(new CaseInsensitiveString("ASC"), "ASC");
			_keywords.put(new CaseInsensitiveString("ASSERTION"), "ASSERTION");
			_keywords.put(new CaseInsensitiveString("AUTHORIZATION"), "AUTHORIZATION");
			_keywords.put(new CaseInsensitiveString("AVG"), "AVG");
			_keywords.put(new CaseInsensitiveString("BETWEEN"), "BETWEEN");
			_keywords.put(new CaseInsensitiveString("BY"), "BY");
			_keywords.put(new CaseInsensitiveString("CASCADE"), "CASCADE");
			_keywords.put(new CaseInsensitiveString("CASCADED"), "CASCADED");
			_keywords.put(new CaseInsensitiveString("CATALOG"), "CATALOG");
			_keywords.put(new CaseInsensitiveString("CHARACTER"), "CHARACTER");
			_keywords.put(new CaseInsensitiveString("CHECK"), "CHECK");
			_keywords.put(new CaseInsensitiveString("COLLATE"), "COLLATE");
			_keywords.put(new CaseInsensitiveString("COLLATION"), "COLLATION");
			_keywords.put(new CaseInsensitiveString("COLUMN"), "COLUMN");
			_keywords.put(new CaseInsensitiveString("COMMIT"), "COMMIT");
			_keywords.put(new CaseInsensitiveString("COMMITTED"), "COMMITTED");
			_keywords.put(new CaseInsensitiveString("CONNECT"), "CONNECT");
			_keywords.put(new CaseInsensitiveString("CONNECTION"), "CONNECTION");
			_keywords.put(new CaseInsensitiveString("CONSTRAINT"), "CONSTRAINT");
			_keywords.put(new CaseInsensitiveString("COUNT"), "COUNT");
			_keywords.put(new CaseInsensitiveString("CORRESPONDING"), "CORRESPONDING");
			_keywords.put(new CaseInsensitiveString("CREATE"), "CREATE");
			_keywords.put(new CaseInsensitiveString("CROSS"), "CROSS");
			_keywords.put(new CaseInsensitiveString("CURRENT"), "CURRENT");
			_keywords.put(new CaseInsensitiveString("CURSOR"), "CURSOR");
			_keywords.put(new CaseInsensitiveString("DECLARE"), "DECLARE");
			_keywords.put(new CaseInsensitiveString("DEFAULT"), "DEFAULT");
			_keywords.put(new CaseInsensitiveString("DEFERRABLE"), "DEFERRABLE");
			_keywords.put(new CaseInsensitiveString("DEFERRED"), "DEFERRED");
			_keywords.put(new CaseInsensitiveString("DELETE"), "DELETE");
			_keywords.put(new CaseInsensitiveString("DESC"), "DESC");
			_keywords.put(new CaseInsensitiveString("DIAGNOSTICS"), "DIAGNOSTICS");
			_keywords.put(new CaseInsensitiveString("DISCONNECT"), "DISCONNECT");
			_keywords.put(new CaseInsensitiveString("DISTINCT"), "DISTINCT");
			_keywords.put(new CaseInsensitiveString("DOMAIN"), "DOMAIN");
			_keywords.put(new CaseInsensitiveString("DROP"), "DROP");
			_keywords.put(new CaseInsensitiveString("ESCAPE"), "ESCAPE");
			_keywords.put(new CaseInsensitiveString("EXCEPT"), "EXCEPT");
			_keywords.put(new CaseInsensitiveString("EXISTS"), "EXISTS");
			_keywords.put(new CaseInsensitiveString("EXTERNAL"), "EXTERNAL");
			_keywords.put(new CaseInsensitiveString("FALSE"), "FALSE");
			_keywords.put(new CaseInsensitiveString("FETCH"), "FETCH");
			_keywords.put(new CaseInsensitiveString("FIRST"), "FIRST");
			_keywords.put(new CaseInsensitiveString("FOREIGN"), "FOREIGN");
			_keywords.put(new CaseInsensitiveString("FROM"), "FROM");
			_keywords.put(new CaseInsensitiveString("FULL"), "FULL");
			_keywords.put(new CaseInsensitiveString("GET"), "GET");
			_keywords.put(new CaseInsensitiveString("GLOBAL"), "GLOBAL");
			_keywords.put(new CaseInsensitiveString("GRANT"), "GRANT");
			_keywords.put(new CaseInsensitiveString("GROUP"), "GROUP");
			_keywords.put(new CaseInsensitiveString("HAVING"), "HAVING");
			_keywords.put(new CaseInsensitiveString("IDENTITY"), "IDENTITY");
			_keywords.put(new CaseInsensitiveString("IMMEDIATE"), "IMMEDIATE");
			_keywords.put(new CaseInsensitiveString("IN"), "IN");
			_keywords.put(new CaseInsensitiveString("INITIALLY"), "INITIALLY");
			_keywords.put(new CaseInsensitiveString("INNER"), "INNER");
			_keywords.put(new CaseInsensitiveString("INSENSITIVE"), "INSENSITIVE");
			_keywords.put(new CaseInsensitiveString("INSERT"), "INSERT");
			_keywords.put(new CaseInsensitiveString("INTERSECT"), "INTERSECT");
			_keywords.put(new CaseInsensitiveString("INTO"), "INTO");
			_keywords.put(new CaseInsensitiveString("IS"), "IS");
			_keywords.put(new CaseInsensitiveString("ISOLATION"), "ISOLATION");
			_keywords.put(new CaseInsensitiveString("JOIN"), "JOIN");
			_keywords.put(new CaseInsensitiveString("KEY"), "KEY");
			_keywords.put(new CaseInsensitiveString("LAST"), "LAST");
			_keywords.put(new CaseInsensitiveString("LEFT"), "LEFT");
			_keywords.put(new CaseInsensitiveString("LEVEL"), "LEVEL");
			_keywords.put(new CaseInsensitiveString("LIKE"), "LIKE");
			_keywords.put(new CaseInsensitiveString("LOCAL"), "LOCAL");
			_keywords.put(new CaseInsensitiveString("MATCH"), "MATCH");
			_keywords.put(new CaseInsensitiveString("MAX"), "MAX");
			_keywords.put(new CaseInsensitiveString("MIN"), "MIN");
			_keywords.put(new CaseInsensitiveString("NAMES"), "NAMES");
			_keywords.put(new CaseInsensitiveString("NEXT"), "NEXT");
			_keywords.put(new CaseInsensitiveString("NO"), "NO");
			_keywords.put(new CaseInsensitiveString("NOT"), "NOT");
			_keywords.put(new CaseInsensitiveString("NULL"), "NULL");
			_keywords.put(new CaseInsensitiveString("OF"), "OF");
			_keywords.put(new CaseInsensitiveString("ON"), "ON");
			_keywords.put(new CaseInsensitiveString("ONLY"), "ONLY");
			_keywords.put(new CaseInsensitiveString("OPEN"), "OPEN");
			_keywords.put(new CaseInsensitiveString("OPTION"), "OPTION");
			_keywords.put(new CaseInsensitiveString("OR"), "OR");
			_keywords.put(new CaseInsensitiveString("ORDER"), "ORDER");
			_keywords.put(new CaseInsensitiveString("OUTER"), "OUTER");
			_keywords.put(new CaseInsensitiveString("OVERLAPS"), "OVERLAPS");
			_keywords.put(new CaseInsensitiveString("PARTIAL"), "PARTIAL");
			_keywords.put(new CaseInsensitiveString("PRESERVE"), "PRESERVE");
			_keywords.put(new CaseInsensitiveString("PRIMARY"), "PRIMARY");
			_keywords.put(new CaseInsensitiveString("PRIOR"), "PRIOR");
			_keywords.put(new CaseInsensitiveString("PRIVILIGES"), "PRIVILIGES");
			_keywords.put(new CaseInsensitiveString("PUBLIC"), "PUBLIC");
			_keywords.put(new CaseInsensitiveString("READ"), "READ");
			_keywords.put(new CaseInsensitiveString("REFERENCES"), "REFERENCES");
			_keywords.put(new CaseInsensitiveString("RELATIVE"), "RELATIVE");
			_keywords.put(new CaseInsensitiveString("REPEATABLE"), "REPEATABLE");
			_keywords.put(new CaseInsensitiveString("RESTRICT"), "RESTRICT");
			_keywords.put(new CaseInsensitiveString("REVOKE"), "REVOKE");
			_keywords.put(new CaseInsensitiveString("RIGHT"), "RIGHT");
			_keywords.put(new CaseInsensitiveString("ROLLBACK"), "ROLLBACK");
			_keywords.put(new CaseInsensitiveString("ROWS"), "ROWS");
			_keywords.put(new CaseInsensitiveString("SCHEMA"), "SCHEMA");
			_keywords.put(new CaseInsensitiveString("SCROLL"), "SCROLL");
			_keywords.put(new CaseInsensitiveString("SELECT"), "SELECT");
			_keywords.put(new CaseInsensitiveString("SERIALIZABLE"), "SERIALIZABLE");
			_keywords.put(new CaseInsensitiveString("SESSION"), "SESSION");
			_keywords.put(new CaseInsensitiveString("SET"), "SET");
			_keywords.put(new CaseInsensitiveString("SIZE"), "SIZE");
			_keywords.put(new CaseInsensitiveString("SOME"), "SOME");
			_keywords.put(new CaseInsensitiveString("SUM"), "SUM");
			_keywords.put(new CaseInsensitiveString("TABLE"), "TABLE");
			_keywords.put(new CaseInsensitiveString("TEMPORARY"), "TEMPORARY");
			_keywords.put(new CaseInsensitiveString("THEN"), "THEN");
			_keywords.put(new CaseInsensitiveString("TIME"), "TIME");
			_keywords.put(new CaseInsensitiveString("TO"), "TO");
			_keywords.put(new CaseInsensitiveString("TRANSACTION"), "TRANSACTION");
			_keywords.put(new CaseInsensitiveString("TRIGGER"), "TRIGGER");
			_keywords.put(new CaseInsensitiveString("TRUE"), "TRUE");
			_keywords.put(new CaseInsensitiveString("UNCOMMITTED"), "UNCOMMITTED");
			_keywords.put(new CaseInsensitiveString("UNION"), "UNION");
			_keywords.put(new CaseInsensitiveString("UNIQUE"), "UNIQUE");
			_keywords.put(new CaseInsensitiveString("UNKNOWN"), "UNKNOWN");
			_keywords.put(new CaseInsensitiveString("UPDATE"), "UPDATE");
			_keywords.put(new CaseInsensitiveString("USAGE"), "USAGE");
			_keywords.put(new CaseInsensitiveString("USER"), "USER");
			_keywords.put(new CaseInsensitiveString("USING"), "USING");
			_keywords.put(new CaseInsensitiveString("VALUES"), "VALUES");
			_keywords.put(new CaseInsensitiveString("VIEW"), "VIEW");
			_keywords.put(new CaseInsensitiveString("WHERE"), "WHERE");
			_keywords.put(new CaseInsensitiveString("WITH"), "WITH");
			_keywords.put(new CaseInsensitiveString("WORK"), "WORK");
			_keywords.put(new CaseInsensitiveString("WRITE"), "WRITE");
			_keywords.put(new CaseInsensitiveString("ZONE"), "ZONE");

			// Not actually in the std.
			_keywords.put(new CaseInsensitiveString("INDEX"), "INDEX");

			// Extra _keywords that this DBMS supports.
			if (dmd != null)
			{

				String[] sqlKeywords = dmd.getSQLKeywords();

				for (int i = 0; i < sqlKeywords.length; i++)
				{
					_keywords.put(new CaseInsensitiveString(sqlKeywords[i]), sqlKeywords[i]);
				}


				try
				{
					addSingleKeyword(dmd.getCatalogTerm());
				}
				catch (Throwable ex)
				{
					s_log.error("Error", ex);
				}

				try
				{
					addSingleKeyword(dmd.getSchemaTerm());
				}
				catch (Throwable ex)
				{
					s_log.error("Error", ex);
				}

				try
				{
					addSingleKeyword(dmd.getProcedureTerm());
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

	private void loadDataTypes(SQLDatabaseMetaData dmd)
	{
		try
		{
            DataTypeInfo[] infos = dmd.getDataTypes();
            for (int i = 0; i < infos.length; i++) {
                String typeName = infos[i].getSimpleName();
                _dataTypes.put(new CaseInsensitiveString(typeName), typeName);
            }
		}
		catch (Throwable ex)
		{
			s_log.error("Error occured creating data types collection", ex);
		}
	}

	private void loadFunctions(SQLDatabaseMetaData dmd)
	{
		ArrayList buf = new ArrayList();

		try
		{
			buf.addAll(Arrays.asList(dmd.getNumericFunctions()));
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}

		try
		{
			buf.addAll(Arrays.asList((dmd.getStringFunctions())));
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}

		try
		{
			buf.addAll(Arrays.asList(dmd.getTimeDateFunctions()));
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
				_functions.put(new CaseInsensitiveString(func) ,func);
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
				_keywords.put(new CaseInsensitiveString(keyword), keyword);
			}
		}
	}

	public String[] getKeywords()
	{
		return (String[]) _keywords.values().toArray(new String[_keywords.size()]);
	}

	public String[] getDataTypes()
	{
		return (String[]) _dataTypes.values().toArray(new String[_dataTypes.size()]);
	}

	public String[] getFunctions()
	{
		return (String[]) _functions.values().toArray(new String[_functions.size()]);
	}

	public String[] getTables()
	{
		return (String[]) _tables.values().toArray(new String[_tables.size()]);
	}

	public String[] getCatalogs()
	{
		return (String[]) _catalogs.toArray(new String[_catalogs.size()]);
	}

	public String[] getSchemas()
	{
		return (String[]) _schemas.toArray(new String[_schemas.size()]);
	}

	public ExtendedTableInfo[] getExtendedTableInfos()
	{
		return getExtendedTableInfos(null, null);
	}

	public ExtendedTableInfo[] getExtendedTableInfos(String catalog, String schema)
	{
		if(null == catalog && null == schema)
		{
			return (ExtendedTableInfo[]) _extendedtableInfos.toArray(new ExtendedTableInfo[_extendedtableInfos.size()]);
		}
		else
		{
			ArrayList ret = new ArrayList();

			for (int i = 0; i < _extendedtableInfos.size(); i++)
			{
				ExtendedTableInfo extendedTableInfo = (ExtendedTableInfo) _extendedtableInfos.get(i);
				boolean toAdd = true;
				if(null != catalog && false == catalog.equalsIgnoreCase(extendedTableInfo.getCatalog()) )
				{
					toAdd = false;
				}

				if(null != schema && false == schema.equalsIgnoreCase(extendedTableInfo.getSchema()) )
				{
					toAdd = false;
				}

				if(toAdd)
				{
					ret.add(extendedTableInfo);
				}
			}

			return (ExtendedTableInfo[]) ret.toArray(new ExtendedTableInfo[ret.size()]);
		}
	}


	public IProcedureInfo[] getStoredProceduresInfos()
	{
		return getStoredProceduresInfos(null, null);
	}

	public IProcedureInfo[] getStoredProceduresInfos(String catalog, String schema)
	{
		if(null == catalog && null == schema)
		{
			return _procInfos;
		}
		else
		{
			ArrayList ret = new ArrayList();

			for (int i = 0; i < _procInfos.length; i++)
			{
				boolean toAdd = true;
				if(null != catalog && false == catalog.equalsIgnoreCase(_procInfos[i].getCatalogName()) )
				{
					toAdd = false;
				}

				if(null != schema && false == schema.equalsIgnoreCase(_procInfos[i].getSchemaName()) )
				{
					toAdd = false;
				}

				if(toAdd)
				{
					ret.add(_procInfos[i]);
				}
			}

			return (IProcedureInfo[]) ret.toArray(new IProcedureInfo[ret.size()]);
		}
	}


	public boolean isLoaded()
	{
		return _loaded;
	}

	private void loadTables(SQLDatabaseMetaData dmd)
	{
		try
		{
			final String[] tabTypes = new String[] { "TABLE", "VIEW" };
            ITableInfo[] infos = dmd.getTables(null, null, null, tabTypes);
            for (int i = 0; i < infos.length; i++) {
                String tableName = infos[i].getSimpleName();
                _tables.put(new CaseInsensitiveString(tableName), tableName);
                ExtendedTableInfo info = 
                    new ExtendedTableInfo(tableName, 
                                          infos[i].getType(), 
                                          infos[i].getCatalogName(), 
                                          infos[i].getSchemaName());
                _extendedtableInfos.add(info);
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
			if(_extendedColumnInfosByTableName.containsKey(tableName))
			{
				return;
			}


			if (_session.getProperties().getLoadColumnsInBackground())
			{
				if(_tablesLoadingColsInBackground.containsKey(tableName))
				{
					return;
				}

				_tablesLoadingColsInBackground.put(tableName, tableName);
				_session.getApplication().getThreadPool().addTask(new Runnable()
				{
					public void run()
					{
						try
						{
							accessDbToLoadColumns(tableName);
							_tablesLoadingColsInBackground.remove(tableName);
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
			_columns.put(new CaseInsensitiveString(buf.getColumnName()), buf.getColumnName());

		}
		_extendedColumnInfosByTableName.put(tableName, result);
	}

	public ExtendedColumnInfo[] getExtendedColumnInfos(String tableName)
	{
		return getExtendedColumnInfos(null, null, tableName);
	}

	public ExtendedColumnInfo[] getExtendedColumnInfos(String catalog, String schema, String tableName)
	{
		CaseInsensitiveString cissTableName = new CaseInsensitiveString(tableName);
		loadColumns(cissTableName);
		ArrayList extColInfo = (ArrayList) _extendedColumnInfosByTableName.get(cissTableName);

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
		return _procedures.containsKey(data);
	}
}
