package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.SQLTab;

import java.io.File;


/**
 * Finds out the owner of an ISQLPanelAPI instance and calls several methods on it.
 */
public class SQLPanelSelectionHandler
{
   /**
    * Selects the tab to which the sqlPanelAPI belongs
    * @param sqlPanelAPI
    */
   public static void selectSqlPanel(ISQLPanelAPI sqlPanelAPI)
   {
      if (getActiveSessionTabWidget(sqlPanelAPI.getSession()) instanceof SessionInternalFrame)
      {
         IMainPanelTab mainPanelTab = SessionUtils.getOwningIMainPanelTab(sqlPanelAPI);

         if (null != mainPanelTab)
         {
            sqlPanelAPI.getSession().getSessionPanel().selectMainTab(mainPanelTab);
         }
         else
         {
            sqlPanelAPI.getSession().selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
         }
      }
   }

   private static SessionTabWidget getActiveSessionTabWidget(ISession session)
   {
      return (SessionTabWidget) session.getActiveSessionWindow();
   }

}
