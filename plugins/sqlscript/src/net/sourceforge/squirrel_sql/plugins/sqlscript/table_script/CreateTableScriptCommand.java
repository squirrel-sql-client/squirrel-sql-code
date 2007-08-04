package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesManager;

public class CreateTableScriptCommand implements ICommand
{
   /**
    * Current session.
    */
   private ISession _session;

   /**
    * Current plugin.
    */
   private final SQLScriptPlugin _plugin;

    /** Logger for this class. */
    private static ILogger s_log = 
        LoggerController.createLogger(CreateTableScriptCommand.class);
   
    /** i18n strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(CreateTableScriptCommand.class);

    static interface i18n {
        //i18n[CreateTableScriptCommand.jdbcOdbcMessage=JDBC-ODBC Bridge doesn't 
        //provide all of the necessary metadata. The script may be incomplete.]
        String JDBCODBC_MESSAGE = 
            s_stringMgr.getString("CreateTableScriptCommand.jdbcOdbcMessage"); 
    }
    
    private static SQLScriptPreferenceBean prefs = 
            SQLScriptPreferencesManager.getPreferences();
    
    
   /**
    * Ctor specifying the current session.
    */
   public CreateTableScriptCommand(ISession session, SQLScriptPlugin plugin)
   {
      super();
      _session = session;
      _plugin = plugin;
   }

   /**
    * Execute this command. Use the database meta data to construct a Create Table
    * SQL script and place it in the SQL entry panel.
    */
   public void execute()
   {
      IObjectTreeAPI api = FrameWorkAcessor.getObjectTreeAPI(_session, _plugin);
      IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
      scriptTablesToSQLEntryArea(dbObjs);
   }


   public void scriptTablesToSQLEntryArea(final IDatabaseObjectInfo[] dbObjs)
   {
       _session.getApplication().getThreadPool().addTask(new Runnable() {
           public void run() {
               final String script = createTableScriptString(dbObjs);
                if(null != script)
                {
                    GUIUtils.processOnSwingEventThread(new Runnable() {
                        public void run() {
                            ISQLPanelAPI api = 
                                FrameWorkAcessor.getSQLPanelAPI(_session, _plugin);
                            api.appendSQLScript(script, true);
                            _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);                            
                        }
                    });
                }               
           }
       });
	}
   
   public String createTableScriptString(IDatabaseObjectInfo dbObj) {
       return createTableScriptString(new IDatabaseObjectInfo[] { dbObj });
   }

   public String createTableScriptString(IDatabaseObjectInfo[] dbObjs) {
        StringBuilder result = new StringBuilder(1000);
        ISQLDatabaseMetaData md = _session.getMetaData();
        try {
            boolean isJdbcOdbc = md.getURL().startsWith("jdbc:odbc:");
            if (isJdbcOdbc) {
                _session.showErrorMessage(i18n.JDBCODBC_MESSAGE);
                s_log.error(i18n.JDBCODBC_MESSAGE);
            }

            TableScriptConfigCtrl tscc = new TableScriptConfigCtrl(_session
                    .getApplication().getMainFrame());
            if (1 < dbObjs.length) {
                tscc.doModal();
                if (false == tscc.isOk()) {
                    return null;
                }
            }
            
            CreateScriptPreferences csprefs = new CreateScriptPreferences();
            csprefs.setConstraintsAtEnd(tscc.isConstAndIndAtEnd());
            csprefs.setIncludeExternalReferences(
                    tscc.includeConstToTablesNotInScript());
            csprefs.setDeleteAction(prefs.getDeleteAction());
            csprefs.setDeleteRefAction(prefs.isDeleteRefAction());
            csprefs.setUpdateAction(prefs.getUpdateAction());
            csprefs.setUpdateRefAction(prefs.isUpdateRefAction());
            csprefs.setQualifyTableNames(prefs.isQualifyTableNames());
            
            List<ITableInfo> tables = convertArrayToList(dbObjs);
            
            HibernateDialect dialect = 
                DialectFactory.getDialect(DialectFactory.SOURCE_TYPE, 
                                          _session.getApplication().getMainFrame(), 
                                          md);
            List<String> sqls = 
                dialect.getCreateTableSQL(tables, md, csprefs, isJdbcOdbc);
            String sep = _session.getQueryTokenizer().getSQLStatementSeparator();
            
            for (String sql : sqls) {
                result.append(sql);
                result.append("\n");
                result.append(sep);
                result.append("\n");
            }
        } catch (Exception e) {
            _session.showErrorMessage(e);
        }
        return result.toString();
    }
   
   private List<ITableInfo> convertArrayToList(IDatabaseObjectInfo[] dbObjs) {
       List<ITableInfo> result = new ArrayList<ITableInfo>();
       for (IDatabaseObjectInfo dbObj : dbObjs) {
           if (dbObj instanceof ITableInfo) {
               ITableInfo ti = (ITableInfo)dbObj;
               result.add(ti);
           }
       }
       return result;
   }
}