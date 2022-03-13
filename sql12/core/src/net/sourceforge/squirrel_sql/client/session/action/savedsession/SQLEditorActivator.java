package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import java.beans.PropertyVetoException;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.session.MainPanel;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SQLEditorActivator
{
   private final static ILogger s_log = LoggerController.createLogger(SQLEditorActivator.class);

   private Runnable _selectSqlEditorRunnable;

   public void prepareToActivateMainSqlTab(SessionInternalFrame sessionInternalFrame, SessionSqlJsonBean sessionSQL)
   {
      _selectSqlEditorRunnable = () -> {
         try
         {
            final ISQLPanelAPI mainSQLPanelAPI = sessionInternalFrame.getMainSQLPanelAPI();
            sessionInternalFrame.setSelected(true);
            sessionInternalFrame.getSession().selectMainTab(MainPanel.ITabIndexes.SQL_TAB);
            if(0 <= sessionSQL.getCaretPosition() &&  sessionSQL.getCaretPosition() < mainSQLPanelAPI.getEntireSQLScript().length())
            {
               mainSQLPanelAPI.setCaretPosition(sessionSQL.getCaretPosition());
            }
         }
         catch (PropertyVetoException e)
         {
            s_log.error("Failed to select Mains Session SQL Tab", e);
         }
      };
   }


   public void prepareToActivateAdditionalSqlTab(SessionInternalFrame sessionInternalFrame, AdditionalSQLTab sqlTab, SessionSqlJsonBean sessionSQL)
   {
      _selectSqlEditorRunnable = () -> {
         try
         {
            sessionInternalFrame.setSelected(true);
            final int mainPanelTabIndex = sessionInternalFrame.getSession().getMainPanelTabIndex(sqlTab);
            sessionInternalFrame.getSession().selectMainTab(mainPanelTabIndex);
            if(0 <= sessionSQL.getCaretPosition() &&  sessionSQL.getCaretPosition() < sqlTab.getSQLPanelAPI().getEntireSQLScript().length())
            {
               sqlTab.getSQLPanelAPI().setCaretPosition(sessionSQL.getCaretPosition());
            }
         }
         catch (PropertyVetoException e)
         {
            s_log.error("Failed to select SQL Tab", e);
         }
      };

   }

   public void prepareToActivateSqlInternalFrame(SQLInternalFrame sqlInternalFrame, SessionSqlJsonBean sessionSQL)
   {
      _selectSqlEditorRunnable = () -> {
         try
         {
            sqlInternalFrame.setSelected(true);
            sqlInternalFrame.getMainSQLPanelAPI().setCaretPosition(sessionSQL.getCaretPosition());
         }
         catch (PropertyVetoException e)
         {
            s_log.error("Failed to select SQL Worksheet", e);
         }
      };

   }

   public void activate()
   {
      if(null != _selectSqlEditorRunnable)
      {
         SwingUtilities.invokeLater(_selectSqlEditorRunnable);
      }

   }

   public void prepareToActivateSQLPanelSaveInfo(SQLPanelSaveInfo saveInfo, SessionSqlJsonBean sessionSqlJsonBean)
   {
      if(false == sessionSqlJsonBean.isActiveSqlPanel())
      {
         return;
      }

      if(saveInfo.getSqlPanelType() == SqlPanelType.MAIN_SQL_TAB)
      {
         prepareToActivateMainSqlTab(saveInfo.getSqlPanel().getSession().getSessionInternalFrame(), sessionSqlJsonBean);
      }
      else if(saveInfo.getSqlPanelType() == SqlPanelType.SQL_TAB)
      {
         for( AdditionalSQLTab additionalSQLTab : saveInfo.getSqlPanel().getSession().getSessionPanel().getAdditionalSQLTabs() )
         {
            if(additionalSQLTab.getSQLPanel() == saveInfo.getSqlPanel())
            {
               prepareToActivateAdditionalSqlTab(saveInfo.getSqlPanel().getSession().getSessionInternalFrame(), additionalSQLTab, sessionSqlJsonBean);
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
               prepareToActivateSqlInternalFrame((SQLInternalFrame) widget, sessionSqlJsonBean);
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
