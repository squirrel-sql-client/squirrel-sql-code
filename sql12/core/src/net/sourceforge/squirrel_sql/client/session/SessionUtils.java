package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.*;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.*;
import java.beans.PropertyVetoException;

public class SessionUtils
{
   private final static ILogger s_log = LoggerController.createLogger(SessionUtils.class);

   public static Frame getOwningFrame(ISession destSession)
   {
      Frame owningFrame;
      if(destSession.getActiveSessionWindow().hasSQLPanelAPI())
      {
         owningFrame = GUIUtils.getOwningFrame(destSession.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel().getTextComponent());
      }
      else
      {
         owningFrame = GUIUtils.getOwningFrame(destSession.getObjectTreeAPIOfActiveSessionWindow().getDetailTabComp());
      }
      return owningFrame;
   }

   public static Frame getOwningFrame(ISQLPanelAPI sqlPanelAPI)
   {
      return GUIUtils.getOwningFrame(sqlPanelAPI.getSQLEntryPanel().getTextComponent());
   }

   static ISQLPanelAPI getSqlPanelApi(IIdentifier entryPanelIdentifier, IIdentifier sessionIdentifier)
   {
      ISessionWidget[] frames = Main.getApplication().getWindowManager().getAllFramesOfSession(sessionIdentifier);

      for (int i = 0; i < frames.length; i++)
      {
         if(frames[i] instanceof SQLInternalFrame)
         {
            ISQLPanelAPI sqlPanelAPI = ((SQLInternalFrame)frames[i]).getMainSQLPanelAPI();
            IIdentifier id = sqlPanelAPI.getSQLEntryPanel().getIdentifier();

            if(id.equals(entryPanelIdentifier))
            {
               return sqlPanelAPI;
            }
            else if(Utilities.equalsRespectNull(sqlPanelAPI.getSQLPanelSplitter().getFindEntryPanelIdentifier(), entryPanelIdentifier))
            {
               return null;
            }
         }

         if(frames[i] instanceof SessionInternalFrame)
         {
            IIdentifier sqlEditorID;
            ISQLPanelAPI sqlPanelAPI;

            sqlPanelAPI = ((SessionInternalFrame) frames[i]).getMainSQLPanelAPI();
            sqlEditorID = sqlPanelAPI.getSQLEntryPanel().getIdentifier();

            if(sqlEditorID.equals(entryPanelIdentifier))
            {
               return sqlPanelAPI;
            }
            else if(Utilities.equalsRespectNull(sqlPanelAPI.getSQLPanelSplitter().getFindEntryPanelIdentifier(), entryPanelIdentifier))
            {
               return null;
            }

            IObjectTreeAPI objectTreeApi = ((SessionInternalFrame)frames[i]).getObjectTreeAPI();

            SessionPanel sessionPanel = ((SessionInternalFrame) frames[i]).getSessionPanel();

            for (int j = 0; j < sessionPanel.getTabCount(); j++)
            {

               if(sessionPanel.getMainPanelTabAt(j) instanceof AdditionalSQLTab)
               {
                  sqlPanelAPI = ((AdditionalSQLTab) sessionPanel.getMainPanelTabAt(j)).getSQLPanel().getSQLPanelAPI();
                  sqlEditorID = sqlPanelAPI.getSQLEntryPanel().getIdentifier();

                  if(sqlEditorID.equals(entryPanelIdentifier))
                  {
                     return sqlPanelAPI;
                  }
                  else if(Utilities.equalsRespectNull(sqlPanelAPI.getSQLPanelSplitter().getFindEntryPanelIdentifier(), entryPanelIdentifier))
                  {
                     return null;
                  }
               }
            }

            IIdentifier findEditorID = objectTreeApi.getFindController().getFindEntryPanel().getIdentifier();
            if(findEditorID.equals(entryPanelIdentifier))
            {
               return null;
            }
         }

         if(frames[i] instanceof ObjectTreeInternalFrame)
         {
            IObjectTreeAPI objectTreeApi = ((ObjectTreeInternalFrame)frames[i]).getObjectTreeAPI();
            IIdentifier findEditorID = objectTreeApi.getFindController().getFindEntryPanel().getIdentifier();

            if(findEditorID.equals(entryPanelIdentifier))
            {
               return null;
            }
         }
      }

      throw new IllegalStateException("Session has no entry panel for ID=" + entryPanelIdentifier);
   }

   public static IMainPanelTab getOwningIMainPanelTab(IFileEditorAPI fileEditorAPI)
   {
      SessionPanel sessionSheet = fileEditorAPI.getSession().getSessionPanel();
      for (int i = 0; i < sessionSheet.getTabCount(); i++)
      {
         IMainPanelTab mainPanelTab = sessionSheet.getMainPanelTabAt(i);

         if(mainPanelTab.getActiveFileEditorAPIOrNull() == fileEditorAPI)
         {
            return mainPanelTab;
         }
      }

      return null;
   }

   public static AdditionalSQLTab createSQLTab(ISession session)
   {
      AdditionalSQLTab additionalSQLTab = new AdditionalSQLTab(session);
      int tabIndex = session.getSessionPanel().addMainTab(additionalSQLTab);
      session.getSessionPanel().selectMainTab(tabIndex);
      Main.getApplication().getPluginManager().additionalSQLTabOpened(additionalSQLTab);
      session.getSessionInternalFrame().moveToFront();
      return additionalSQLTab;
   }

   public static void activateMainSqlTab(SessionInternalFrame sessionInternalFrame, int caretPosition, boolean shouldForceToFocusActiveSqlEditor)
   {
      try
      {
         final ISQLPanelAPI mainSQLPanelAPI = sessionInternalFrame.getMainSQLPanelAPI();
         sessionInternalFrame.setSelected(true);
         sessionInternalFrame.getSession().selectMainTab(MainPanel.ITabIndexes.SQL_TAB);
         if( 0 <= caretPosition && caretPosition < mainSQLPanelAPI.getEntireSQLScript().length())
         {
            forceFocusAndSetCaret(caretPosition, mainSQLPanelAPI, shouldForceToFocusActiveSqlEditor);
         }
      }
      catch (PropertyVetoException e)
      {
         s_log.error("Failed to select Mains Session SQL Tab", e);
      }
   }

   public static void activateAdditionalSqlTab(SessionInternalFrame sessionInternalFrame, AdditionalSQLTab sqlTab, int caretPosition, boolean shouldForceToFocusActiveSqlEditor)
   {
      try
      {
         sessionInternalFrame.setSelected(true);
         final int mainPanelTabIndex = sessionInternalFrame.getSession().getMainPanelTabIndex(sqlTab);
         sessionInternalFrame.getSession().selectMainTab(mainPanelTabIndex);
         if( 0 <= caretPosition && caretPosition < sqlTab.getSQLPanelAPI().getEntireSQLScript().length())
         {
            forceFocusAndSetCaret(caretPosition, sqlTab.getSQLPanelAPI(), shouldForceToFocusActiveSqlEditor);
         }
      }
      catch (PropertyVetoException e)
      {
         s_log.error("Failed to select SQL Tab", e);
      }
   }

   public static void activateSqlInternalFrame(SQLInternalFrame sqlInternalFrame, int caretPosition, boolean shouldForceToFocusActiveSqlEditor)
   {
      try
      {
         sqlInternalFrame.setSelected(true);
         forceFocusAndSetCaret(caretPosition, sqlInternalFrame.getMainSQLPanelAPI(), shouldForceToFocusActiveSqlEditor);
      }
      catch (PropertyVetoException e)
      {
         s_log.error("Failed to select SQL Worksheet", e);
      }
   }

   private static void forceFocusAndSetCaret(int caretPosition, ISQLPanelAPI sqlPanelAPI, boolean shouldForceToFocusActiveSqlEditor)
   {
      if (shouldForceToFocusActiveSqlEditor)
      {
         GUIUtils.forceFocus(sqlPanelAPI.getSQLEntryPanel().getTextComponent(), () -> sqlPanelAPI.setCaretPosition(caretPosition), "SessionUtils.forceFocusAndSetCaret");
      }
      else
      {
         sqlPanelAPI.setCaretPosition(caretPosition);
      }
   }

}
