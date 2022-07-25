package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedSessionUtil
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionUtil.class);

   // The first SQL Panel is the main SQL Panel
   public static List<SQLPanelSaveInfo> getAllSQLPanelsOrderedAndTyped(ISession session)
   {
      List<SQLPanelSaveInfo> ret = new ArrayList<>();

      final SQLPanelSaveInfo mainSqlPanelSaveInfo = new SQLPanelSaveInfo(session.getSessionPanel().getMainSQLPanel(), SqlPanelType.MAIN_SQL_TAB);
      ret.add(mainSqlPanelSaveInfo);
      if(session.getSessionPanel().getMainSQLPanel().getSQLPanelAPI() == session.getSQLPanelAPIOfActiveSessionWindow())
      {
         mainSqlPanelSaveInfo.setActiveSqlPanel(session.getSessionPanel().getMainSQLPanel().getSQLPanelAPI().getCaretPosition());
      }

      for (AdditionalSQLTab additionalSQLTab : session.getSessionPanel().getAdditionalSQLTabs())
      {
         final SQLPanelSaveInfo sqlPanelSaveInfo = new SQLPanelSaveInfo(additionalSQLTab.getSQLPanel(), SqlPanelType.SQL_TAB);

         if(additionalSQLTab.getSQLPanel().getSQLPanelAPI() == session.getSQLPanelAPIOfActiveSessionWindow())
         {
            sqlPanelSaveInfo.setActiveSqlPanel(additionalSQLTab.getSQLPanel().getSQLPanelAPI().getCaretPosition());
         }

         ret.add(sqlPanelSaveInfo);
      }

      final IWidget[] allWidgets = Main.getApplication().getMainFrame().getDesktopContainer().getAllWidgets();
      for (IWidget widget : allWidgets)
      {
         if(widget instanceof SQLInternalFrame)
         {
            final SQLPanel sqlWorksheetPanel = ((SQLInternalFrame) widget).getSQLPanel();

            final ISession sessionOfSqlInternaFrame = sqlWorksheetPanel.getSession();
            if(session.getIdentifier().equals(sessionOfSqlInternaFrame.getIdentifier()))
            {
               final SQLPanelSaveInfo sqlPanelSaveInfo = new SQLPanelSaveInfo(sqlWorksheetPanel, SqlPanelType.SQL_INTERNAL_FRAME);

               if(Main.getApplication().getMainFrame().getDesktopContainer().getSelectedWidget() == widget)
               {
                  sqlPanelSaveInfo.setActiveSqlPanel(sqlWorksheetPanel.getSQLPanelAPI().getCaretPosition());
               }

               ret.add(sqlPanelSaveInfo);
            }
         }
      }

      return ret;
   }

   public static boolean isSQLVirgin(ISession session)
   {
      List<SQLPanelSaveInfo> sqlPanelSaveInfoList =  getAllSQLPanelsOrderedAndTyped(session);

      if(1 < sqlPanelSaveInfoList.size())
      {
         return false;
      }

      final ISQLPanelAPI sqlPanelAPI = sqlPanelSaveInfoList.get(0).getSqlPanel().getSQLPanelAPI();
      return null == sqlPanelAPI.getFileHandler().getFile() && StringUtilities.isEmpty(sqlPanelAPI.getEntireSQLScript(), true);
   }

   public static void makeSessionSQLVirgin(ISession session)
   {
      final SQLPanel mainSQLPanel = session.getSessionPanel().getMainSQLPanel();
      mainSQLPanel.getSQLPanelAPI().getFileHandler().resetUnsavedEdits();
      mainSQLPanel.getSQLPanelAPI().getFileHandler().fileClose();
      mainSQLPanel.getSQLPanelAPI().setEntireSQLScript(null);

      for (AdditionalSQLTab additionalSQLTab : session.getSessionPanel().getAdditionalSQLTabs())
      {
         additionalSQLTab.getSQLPanelAPI().getFileHandler().resetUnsavedEdits();
         additionalSQLTab.getSQLPanelAPI().getFileHandler().fileClose();
         additionalSQLTab.close(true);
      }

      final IWidget[] allWidgets = Main.getApplication().getMainFrame().getDesktopContainer().getAllWidgets();
      for (IWidget widget : allWidgets)
      {
         if(widget instanceof SQLInternalFrame)
         {
            final ISession sessionOfSqlInternaFrame = ((SQLInternalFrame) widget).getSQLPanel().getSession();
            if(session.getIdentifier().equals(sessionOfSqlInternaFrame.getIdentifier()))
            {
               ((SQLInternalFrame)widget).getMainSQLPanelAPI().getFileHandler().resetUnsavedEdits();
               ((SQLInternalFrame)widget).getMainSQLPanelAPI().getFileHandler().fileClose();
               ((SQLInternalFrame)widget).closeFrame(true);
            }
         }
      }
   }

   public static void initSessionWithSavedSession(SavedSessionJsonBean savedSessionJsonBean, ISession session)
   {
      session.setSavedSession(savedSessionJsonBean);
      ((SessionManageAction)Main.getApplication().getActionCollection().get(SessionManageAction.class)).updateUI();
   }

   public static ISQLAlias getAliasForIdString(String defaultAliasIdString)
   {
      final UidIdentifier aliasId = new UidIdentifier(defaultAliasIdString);
      return Main.getApplication().getAliasesAndDriversManager().getAlias(aliasId);
   }

   public static boolean isInSavedSessionsDir(File sqlFile)
   {
      if(null == sqlFile)
      {
         return false;
      }

      return getSavedSessionsDir().equals(sqlFile.getParentFile());
   }

   public static File getSavedSessionsDir()
   {
      return new ApplicationFiles().getSavedSessionsDir();
   }

   public static void printSavedSessionDetails(SavedSessionJsonBean savedSession)
   {
      String savedSessionName = savedSession.getName();

      final ISQLAlias alias = getAliasForIdString(savedSession.getDefaultAliasIdString());
      String aliasName = "<unknown>";
      String jdbcUrl = "<unknown>";
      String jdbcUser = "<unknown>";
      if(null != alias)
      {
         aliasName = alias.getName();
         jdbcUrl = alias.getUrl();
         jdbcUser = alias.getUserName();
      }

      final String msg = s_stringMgr.getString("SavedSessionUtil.saved.session.details", savedSessionName, aliasName, jdbcUrl, jdbcUser);
      Main.getApplication().getMessageHandler().showMessage(msg);

      boolean firstInternalFile = true;
      for (SessionSqlJsonBean sessionSQL : savedSession.getSessionSQLs())
      {
         final String fileMsg;

         if(false == StringUtilities.isEmpty(sessionSQL.getExternalFilePath()))
         {
            fileMsg = s_stringMgr.getString("SavedSessionUtil.saved.session.external.file", sessionSQL.getExternalFilePath());
         }
         else
         {
            if(firstInternalFile)
            {
               fileMsg = s_stringMgr.getString("SavedSessionUtil.saved.session.internal.file.first", sessionSQL.getInternalFileName(), getSavedSessionsDir());
               firstInternalFile = false;
            }
            else
            {
               fileMsg = s_stringMgr.getString("SavedSessionUtil.saved.session.internal.file", sessionSQL.getInternalFileName());
            }
         }
         Main.getApplication().getMessageHandler().showMessage(fileMsg);
      }
   }
}
