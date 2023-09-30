package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

public class CreateSelectScriptCommand
{
   private static ILogger s_log = LoggerController.createLogger(CreateSelectScriptCommand.class);

   private IObjectTreeAPI _objectTreeAPI;

   private final SQLScriptPlugin _plugin;

   public CreateSelectScriptCommand(IObjectTreeAPI objectTreeAPI, SQLScriptPlugin plugin)
   {
      _objectTreeAPI = objectTreeAPI;
      _plugin = plugin;
   }

   public void execute()
   {
      IDatabaseObjectInfo[] dbObjs = _objectTreeAPI.getSelectedDatabaseObjects();
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
               final String script = ScriptUtil.createSelectScriptString(dbObjs, CreateSelectScriptCommand.this._objectTreeAPI);
               if (null != script)
               {
                  GUIUtils.processOnSwingEventThread(() -> FrameWorkAcessor.appendScriptToEditor(script, _objectTreeAPI));
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
}
