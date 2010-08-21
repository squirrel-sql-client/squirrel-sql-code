package net.sourceforge.squirrel_sql.plugins.editextras;

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
   public static ISQLPanelAPI getSQLPanelAPI(ISession session, EditExtrasPlugin plugin)
   {
      return session.getSQLPanelAPIOfActiveSessionWindow();
   }

   public static IObjectTreeAPI getObjectTreeAPI(ISession session, EditExtrasPlugin  plugin)
   {
      return session.getObjectTreeAPIOfActiveSessionWindow();
   }
}
