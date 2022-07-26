package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.dialects.DialectUtils2;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesManager;

public class CreateSelectScriptCommand
{
   private static ILogger s_log = LoggerController.createLogger(CreateSelectScriptCommand.class);

   private IObjectTreeAPI _iObjectTreeAPI;

   private final SQLScriptPlugin _plugin;

   public CreateSelectScriptCommand(IObjectTreeAPI objectTreeAPI, SQLScriptPlugin plugin)
   {
      _iObjectTreeAPI = objectTreeAPI;
      _plugin = plugin;
   }

   public void execute()
   {
      IDatabaseObjectInfo[] dbObjs = _iObjectTreeAPI.getSelectedDatabaseObjects();
      scriptSelectsToSQLEntryArea(dbObjs);
   }


   public void scriptSelectsToSQLEntryArea(final IDatabaseObjectInfo[] dbObjs)
   {
      Main.getApplication().getThreadPool().addTask(new Runnable()
      {
         public void run()
         {
            try
            {
               final String script = createSelectScriptString(dbObjs);
               if (null != script)
               {
                  GUIUtils.processOnSwingEventThread(() -> FrameWorkAcessor.appendScriptToEditor(script, _iObjectTreeAPI));
               }
            }
            catch (Exception e)
            {
               Main.getApplication().getMessageHandler().showErrorMessage(e);
               s_log.error(e);
            }
         }
      });
   }


   public String createSelectScriptString(IDatabaseObjectInfo[] dbObjs)
   {
      StringBuffer sbScript = new StringBuffer(1000);
      StringBuffer sbConstraints = new StringBuffer(1000);
      ISQLConnection conn = _iObjectTreeAPI.getSession().getSQLConnection();
      try
      {
        boolean isJdbcOdbc = conn.getSQLMetaData().getURL().startsWith("jdbc:odbc:");
        if (isJdbcOdbc)
        {
           // TODO I18N
           Main.getApplication().getMessageHandler().showErrorMessage("JDBC-ODBC Bridge doesn't provide necessary meta data. Script will be incomplete");
        }

        for (int k = 0; k < dbObjs.length; k++)
        {
           if (false == dbObjs[k] instanceof ITableInfo)
           {
              continue;
           }
           ITableInfo ti = (ITableInfo) dbObjs[k];

           sbScript.append("SELECT ");

           TableColumnInfo[] infos = conn.getSQLMetaData().getColumnInfo(ti);
           for (int i = 0; i < infos.length; i++)
           {
              if(0 < i)
              {
                 sbScript.append(',');
              }

              DialectType dialectType = DialectFactory.getDialectType(_iObjectTreeAPI.getSession().getMetaData());

              if(SQLScriptPreferencesManager.getPreferences().isUseDoubleQuotes())
              {
                 sbScript.append(ScriptUtil.getColumnName(infos[i], SQLScriptPreferencesManager.getPreferences().isUseDoubleQuotes()));
              }
              else
              {
                 // Former version before Preferences.isUseDoubleQuotes() was respected.
                 // Maybe this whole if-else should simply be replaced by return ScriptUtil.getColumnName(infos[i]);
                 sbScript.append(DialectUtils2.checkColumnDoubleQuotes(dialectType, infos[i].getColumnName()));
              }
           }

           sbScript.append(" FROM ").append(ScriptUtil.getTableName(ti));
           sbScript.append(ScriptUtil.getStatementSeparator(_iObjectTreeAPI.getSession())).append('\n');

         }
      }
      catch (Exception e)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(e);
         s_log.error(e);
      }

      return sbScript.append("\n").append(sbConstraints).toString();
   }
}
