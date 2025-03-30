package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;

import java.util.List;

public class SQLPanelApiInfo
{
   public static SQLPanelApiInfo ofSQLInternalFrame(SQLInternalFrame sqlInternalFrame)
   {
      sqlInternalFrame.getMainSQLPanelAPI();
      // ...
      return null;
   }

   public static List<SQLPanelApiInfo> ofSessionMainWindow(SessionInternalFrame sessionMainWindow)
   {
      sessionMainWindow.getMainSQLPanelAPI();
      sessionMainWindow.getSession().getSessionPanel().getAllSQLPanels();

      return null;
   }
}
