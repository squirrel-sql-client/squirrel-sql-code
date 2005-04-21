package net.sourceforge.squirrel_sql.plugins.sqlscript;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;


/**
 * This class was introduced to make the plugin compilable for the time it takes
 * to completely introduce the multible session windows framework.
 * It may be removed after that.
 */
public class FrameWorkAcessor
{
   public static ISQLPanelAPI getSQLPanelAPI(ISession session, SQLScriptPlugin plugin)
   {
      // old version before multible sesssion windows
      //return session.getSQLPanelAPI(plugin);

      if(session.getActiveSessionWindow() instanceof ObjectTreeInternalFrame)
      {
         session.getMessageHandler().showMessage("Script is written to the SQL editor in the main session window.");
         return session.getSessionSheet().getSQLPaneAPI();
      }
      else
      {
         return session.getSQLPanelAPIOfActiveSessionWindow();
      }
   }

   public static IObjectTreeAPI getObjectTreeAPI(ISession session, SQLScriptPlugin sqlScriptPlugin)
   {
      // old version
      //return session.getObjectTreeAPI(sqlScriptPlugin);

      return session.getObjectTreeAPIOfActiveSessionWindow();
   }
}
