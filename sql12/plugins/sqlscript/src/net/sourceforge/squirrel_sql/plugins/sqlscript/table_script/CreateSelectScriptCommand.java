package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;

import java.util.Vector;
import java.util.Collections;
import java.util.Hashtable;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class CreateSelectScriptCommand implements ICommand
{
   /**
    * Current session.
    */
   private ISession _session;

   /**
    * Current plugin.
    */
   private final SQLScriptPlugin _plugin;


   /**
    * Ctor specifying the current session.
    */
   public CreateSelectScriptCommand(ISession session, SQLScriptPlugin plugin)
   {
      super();
      _session = session;
      _plugin = plugin;
   }

   public void execute()
   {
      IObjectTreeAPI api = FrameWorkAcessor.getObjectTreeAPI(_session, _plugin);
      IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
      scriptSelectsToSQLEntryArea(dbObjs);
   }


   public void scriptSelectsToSQLEntryArea(final IDatabaseObjectInfo[] dbObjs)
   {
      _session.getApplication().getThreadPool().addTask(new Runnable()
      {
         public void run()
         {
            final String script = createSelectScriptString(dbObjs);
            if (null != script)
            {
               GUIUtils.processOnSwingEventThread(new Runnable()
               {
                  public void run()
                  {
                     ISQLPanelAPI api = FrameWorkAcessor.getSQLPanelAPI(_session, _plugin);
                     api.appendSQLScript(script, true);
                     _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
                  }
               });
            }
         }
      });
   }


   public String createSelectScriptString(IDatabaseObjectInfo[] dbObjs)
   {
      StringBuffer sbScript = new StringBuffer(1000);
      StringBuffer sbConstraints = new StringBuffer(1000);
      ISQLConnection conn = _session.getSQLConnection();
      try
      {
        boolean isJdbcOdbc = conn.getSQLMetaData().getURL().startsWith("jdbc:odbc:");
        if (isJdbcOdbc)
        {
           // TODO I18N
           _session.showErrorMessage("JDBC-ODBC Bridge doesn't provide necessary meta data. Script will be incomplete");
        }

        for (int k = 0; k < dbObjs.length; k++)
        {
           if (false == dbObjs[k] instanceof ITableInfo)
           {
              continue;
           }
           ITableInfo ti = (ITableInfo) dbObjs[k];

           sbScript.append("SELECT ");

           ScriptUtil su = new ScriptUtil();
           TableColumnInfo[] infos = conn.getSQLMetaData().getColumnInfo(ti);
           for (int i = 0; i < infos.length; i++)
           {
              if(0 < i)
              {
                 sbScript.append(',');
              }
              sbScript.append(infos[i].getColumnName());
           }

           sbScript.append(" FROM ").append(ScriptUtil.getTableName(ti));
           sbScript.append(ScriptUtil.getStatementSeparator(_session)).append('\n');

         }
      }
      catch (Exception e)
      {
         _session.showErrorMessage(e);
      }

      return sbScript.append("\n").append(sbConstraints).toString();
   }
}
