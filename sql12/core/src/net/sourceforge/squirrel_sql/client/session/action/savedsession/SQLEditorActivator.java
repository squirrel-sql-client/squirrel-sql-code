package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;

import javax.swing.*;

public class SQLEditorActivator
{
   private Runnable _selectSqlEditorRunnable;

   public SQLEditorActivator()
   {
   }

   public void prepareToActivateMainSqlTab(SessionInternalFrame sessionInternalFrame, SessionSqlJsonBean sessionSQL, boolean shouldForceToFocusActiveSqlEditor)
   {
      _selectSqlEditorRunnable = () -> {
         SessionUtils.activateMainSqlTab(sessionInternalFrame, sessionSQL.getCaretPosition(), shouldForceToFocusActiveSqlEditor);
      };
   }


   public void prepareToActivateAdditionalSqlTab(SessionInternalFrame sessionInternalFrame, AdditionalSQLTab sqlTab, SessionSqlJsonBean sessionSQL, boolean shouldForceToFocusActiveSqlEditor)
   {
      _selectSqlEditorRunnable = () -> {
         SessionUtils.activateAdditionalSqlTab(sessionInternalFrame, sqlTab, sessionSQL.getCaretPosition(), shouldForceToFocusActiveSqlEditor);
      };

   }

   public void prepareToActivateSqlInternalFrame(SQLInternalFrame sqlInternalFrame, SessionSqlJsonBean sessionSQL, boolean shouldForceToFocusActiveSqlEditor)
   {
      _selectSqlEditorRunnable = () -> {
         SessionUtils.activateSqlInternalFrame(sqlInternalFrame, sessionSQL.getCaretPosition(), shouldForceToFocusActiveSqlEditor);
      };

   }

   public void activate()
   {
      if(null != _selectSqlEditorRunnable)
      {
         SwingUtilities.invokeLater(_selectSqlEditorRunnable);
      }

   }

   public void prepareToActivateSQLPanelSaveInfo(SQLPanelSaveInfo saveInfo, SessionSqlJsonBean sessionSqlJsonBean, boolean shouldForceToFocusActiveSqlEditor)
   {
      if(false == sessionSqlJsonBean.isActiveSqlPanel())
      {
         return;
      }

      if(saveInfo.getSqlPanelType() == SqlPanelType.MAIN_SQL_TAB)
      {
         prepareToActivateMainSqlTab(saveInfo.getSqlPanel().getSession().getSessionInternalFrame(), sessionSqlJsonBean, shouldForceToFocusActiveSqlEditor);
      }
      else if(saveInfo.getSqlPanelType() == SqlPanelType.SQL_TAB)
      {
         for( AdditionalSQLTab additionalSQLTab : saveInfo.getSqlPanel().getSession().getSessionPanel().getAdditionalSQLTabs() )
         {
            if(additionalSQLTab.getSQLPanel() == saveInfo.getSqlPanel())
            {
               prepareToActivateAdditionalSqlTab(saveInfo.getSqlPanel().getSession().getSessionInternalFrame(), additionalSQLTab, sessionSqlJsonBean,shouldForceToFocusActiveSqlEditor);
               break;
            }
         }
      }
      else if(saveInfo.getSqlPanelType() == SqlPanelType.SQL_INTERNAL_FRAME)
      {
         final IWidget[] allWidgets = Main.getApplication().getMainFrame().getDesktopContainer().getAllWidgets();
         for (IWidget widget : allWidgets)
         {
            if( widget instanceof SQLInternalFrame && ((SQLInternalFrame)widget).getSQLPanel() == saveInfo.getSqlPanel())
            {
               prepareToActivateSqlInternalFrame((SQLInternalFrame) widget, sessionSqlJsonBean, shouldForceToFocusActiveSqlEditor);
               break;
            }
         }
      }
      else
      {
         throw new UnsupportedOperationException("Unknown SqlPanelType " + saveInfo.getSqlPanelType());
      }
   }
}
