package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;

import java.util.ArrayList;
import java.util.List;

public class SavedSessionUtil
{
   // The first SQL Panel is the main SQL Panel
   public static List<SQLPanelTyped> getAllSQLPanelsOrderedAndTyped(ISession session)
   {
      List<SQLPanelTyped> ret = new ArrayList<>();

      ret.add(new SQLPanelTyped(session.getSessionPanel().getMainSQLPanel() , SqlPanelType.MAIN_SQL_TAB));

      for (SQLPanel sqlPanel : session.getSessionPanel().getAllSQLPanels())
      {
         if(sqlPanel != session.getSessionPanel().getMainSQLPanel())
         {
            ret.add(new SQLPanelTyped(sqlPanel, SqlPanelType.SQL_TAB));
         }
      }

      final IWidget[] allWidgets = Main.getApplication().getMainFrame().getDesktopContainer().getAllWidgets();
      for (IWidget widget : allWidgets)
      {
         if(widget instanceof SQLInternalFrame)
         {
            final ISession sessionOfSqlInternaFrame = ((SQLInternalFrame) widget).getSQLPanel().getSession();
            if(session.getIdentifier().equals(sessionOfSqlInternaFrame.getIdentifier()))
            {
               ret.add(new SQLPanelTyped(((SQLInternalFrame)widget).getSQLPanel(), SqlPanelType.SQL_INTERNAL_FRAME));
            }
         }
      }

      return ret;
   }
}
