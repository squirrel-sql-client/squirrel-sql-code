package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLPanelAPI;
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
   public static void setSqlFile(ISQLPanelAPI sqlPanelAPI, File file)
   {
      if (getActiveSessionTabWidget(sqlPanelAPI) instanceof SessionInternalFrame)
      {
         if(SessionUtils.getOwningIMainPanelTab(sqlPanelAPI) instanceof SQLTab)
         {
            getActiveSessionTabWidget(sqlPanelAPI).setSqlFile(file);
         }
         else if(SessionUtils.getOwningIMainPanelTab(sqlPanelAPI) instanceof AdditionalSQLTab)
         {
            ((AdditionalSQLTab)SessionUtils.getOwningIMainPanelTab(sqlPanelAPI)).setSqlFile(file);
         }
         else
         {
            throw new IllegalStateException("Don't know where to display the file");
         }
      }
      else
      {
         getActiveSessionTabWidget(sqlPanelAPI).setSqlFile(file);
      }
   }

   public static void setUnsavedEdits(SQLPanelAPI sqlPanelAPI, boolean hasUnsavedEdits)
   {
      if (getActiveSessionTabWidget(sqlPanelAPI) instanceof SessionInternalFrame)
      {
         if(SessionUtils.getOwningIMainPanelTab(sqlPanelAPI) instanceof SQLTab)
         {
            getActiveSessionTabWidget(sqlPanelAPI).setUnsavedEdits(hasUnsavedEdits);
         }
         else if(SessionUtils.getOwningIMainPanelTab(sqlPanelAPI) instanceof AdditionalSQLTab)
         {
            ((AdditionalSQLTab)SessionUtils.getOwningIMainPanelTab(sqlPanelAPI)).setUnsavedEdits(hasUnsavedEdits);
         }
         else
         {
            throw new IllegalStateException("Don't know where to display the file");
         }
      }
      else
      {
         getActiveSessionTabWidget(sqlPanelAPI).setUnsavedEdits(hasUnsavedEdits);
      }

   }


   public static void selectSqlPanel(ISQLPanelAPI sqlPanelAPI)
   {
      if (getActiveSessionTabWidget(sqlPanelAPI) instanceof SessionInternalFrame)
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

   private static SessionTabWidget getActiveSessionTabWidget(ISQLPanelAPI sqlPanelAPI)
   {
      return (SessionTabWidget) sqlPanelAPI.getSession().getActiveSessionWindow();
   }

}
