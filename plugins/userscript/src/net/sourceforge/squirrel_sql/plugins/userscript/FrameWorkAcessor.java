package net.sourceforge.squirrel_sql.plugins.userscript;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;


/**
 * This class was introduced to make the plugin compilable for the time it takes
 * to completely introduce the multible session windows framework.
 * It may be removed after that.
 */
public class FrameWorkAcessor
{
   public static ISQLPanelAPI getSQLPanelAPI(ISession session, UserScriptPlugin plugin)
   {
      //	_session.getSQLPanelAPI(_plugin)
      return session.getSessionSheet().getSQLPaneAPI();
   }

   public static IObjectTreeAPI getObjectTreeAPI(ISession session, UserScriptPlugin plugin)
   {
      //IObjectTreeAPI api = session.getObjectTreeAPI(this);
      return session.getSessionSheet().getObjectTreePanel();
   }
}
