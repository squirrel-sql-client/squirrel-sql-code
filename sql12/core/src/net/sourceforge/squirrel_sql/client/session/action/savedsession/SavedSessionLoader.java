package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.session.MainPanel;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.SwingUtilities;
import java.beans.PropertyVetoException;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class SavedSessionLoader
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionLoader.class);
   private final static ILogger s_log = LoggerController.createLogger(SavedSessionLoader.class);


   public static void load(final SessionInternalFrame sessionInternalFrame, SavedSessionJsonBean savedSessionJsonBean)
   {
      Runnable selectSqlEditorRunnable = null;

      for (final SessionSqlJsonBean sessionSQL : savedSessionJsonBean.getSessionSQLs())
      {
         if(sessionSQL.getPanelType() == SqlPanelType.MAIN_SQL_TAB)
         {
            final ISQLPanelAPI mainSQLPanelAPI = sessionInternalFrame.getMainSQLPanelAPI();
            loadSessionSql(sessionSQL, mainSQLPanelAPI);

            if(sessionSQL.isActiveSqlPanel())
            {
               selectSqlEditorRunnable = () -> {
                  try
                  {
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
         }
         else if(sessionSQL.getPanelType() == SqlPanelType.SQL_TAB)
         {
            final AdditionalSQLTab sqlTab = SessionUtils.createSQLTab(sessionInternalFrame.getSession());
            loadSessionSql(sessionSQL, sqlTab.getSQLPanelAPI());

            if(sessionSQL.isActiveSqlPanel())
            {
               selectSqlEditorRunnable = () -> {
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

         }
         else if(sessionSQL.getPanelType() == SqlPanelType.SQL_INTERNAL_FRAME)
         {
            final SQLInternalFrame sqlInternalFrame = Main.getApplication().getWindowManager().createSQLInternalFrame(sessionInternalFrame.getSession());
            loadSessionSql(sessionSQL, sqlInternalFrame.getMainSQLPanelAPI());

            if(sessionSQL.isActiveSqlPanel())
            {
               selectSqlEditorRunnable = () -> {
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
         }
      }

      SavedSessionUtil.initSessionWithSavedSession(savedSessionJsonBean, sessionInternalFrame.getSession());

      if(null != selectSqlEditorRunnable)
      {
         SwingUtilities.invokeLater(selectSqlEditorRunnable);
      }
   }

   private static void loadSessionSql(SessionSqlJsonBean sessionSQL, ISQLPanelAPI mainSQLPanelAPI)
   {
      if(false == StringUtilities.isEmpty(sessionSQL.getInternalFileName()))
      {
         mainSQLPanelAPI.setEntireSQLScript(loadInternalScript(sessionSQL));
      }
      else
      {
         File file = tryGetExternalFile(sessionSQL);
         if(null != file)
         {
            mainSQLPanelAPI.getFileHandler().fileOpen(file);
         }
      }
   }

   private static File tryGetExternalFile(SessionSqlJsonBean sessionSQL)
   {
      final File file = new File(sessionSQL.getExternalFilePath());

      if(file.exists())
      {
         return file;
      }
      else
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("SavedSessionLoader.external.file.does.not.exist", file.getAbsolutePath()));
         return null;
      }
   }

   private static String loadInternalScript(SessionSqlJsonBean sessionSQL)
   {
      final File internalScriptFile = new File(new ApplicationFiles().getSavedSessionsDir(), sessionSQL.getInternalFileName());
      try
      {
         final List<String> lines = Files.readAllLines(internalScriptFile.toPath());
         return lines.stream().collect(Collectors.joining("\n"));
      }
      catch (Exception e)
      {
         s_log.error("Failed to load internal script " + internalScriptFile.getAbsolutePath(), e);
         return "";
      }
   }

}
