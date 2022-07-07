package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.ICompletionCallback;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackTypeEnum;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.SwingUtilities;
import java.io.File;


public class SessionStartupMainSQLTabContentLoader
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionStartupMainSQLTabContentLoader.class);

   private static ILogger s_log = LoggerController.createLogger(ResultFrame.class);

   private static final String NO_STARTUP_LOADING = "Dummy marker to switch of standard startup loading.";

   static void handleLoadFileAtSessionStart(ISession session, final ISQLPanelAPI panelAPI)
   {
      if(NO_STARTUP_LOADING.equals(session.getSessionLocal(SessionStartupMainSQLTabContentLoader.class)))
      {
         return;
      }


      String startupFileForAlias = Main.getApplication().getRecentFilesManager().getOpenAtStartupFileForAlias(session.getAlias());

      if(null != startupFileForAlias)
      {
         File file = new File(startupFileForAlias);

         if(file.exists() && false == file.isDirectory() && file.canRead())
         {
            SwingUtilities.invokeLater(() -> openFileInMainSqlTab(file, session.getSessionInternalFrame()));
            Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SessionStartupMainSQLTabContentLoader.startupFileForAlias.loading", startupFileForAlias));
         }
         else
         {
            String msg = s_stringMgr.getString("SessionStartupMainSQLTabContentLoader.startupFileForAlias.load.failed", startupFileForAlias);
            Main.getApplication().getMessageHandler().showErrorMessage(msg);
            s_log.error(msg);
         }
      }
      else if (Main.getApplication().getSquirrelPreferences().isReloadSqlContents())
      {
         final String sqlContents = ReloadSqlContentsHelper.getLastSqlContent(session.getAlias());

         if (null != sqlContents)
         {
            SwingUtilities.invokeLater(() -> applyLastEditorContents(panelAPI, sqlContents));
         }
      }
   }

   private static void applyLastEditorContents(ISQLPanelAPI panelAPI, String sqlContents)
   {
      panelAPI.setEntireSQLScript(sqlContents);
      panelAPI.resetUnsavedEdits();

      if (ChangeTrackTypeEnum.getPreference() == ChangeTrackTypeEnum.MANUAL)
      {
         panelAPI.getChangeTracker().rebaseChangeTrackingOnToolbarButtonOrMenuClicked();
      }
   }

   public static void startSessionWithFile(SQLAlias selectedAlias, File fileToOpen)
   {
      ICompletionCallback callback = new ICompletionCallback()
      {
         @Override
         public void connected(ISQLConnection conn)
         {
         }

         @Override
         public void sessionCreated(ISession session)
         {
            session.putSessionLocal(SessionStartupMainSQLTabContentLoader.class, NO_STARTUP_LOADING);
         }

         @Override
         public void sessionInternalFrameCreated(SessionInternalFrame sessionInternalFrame)
         {
            onSessionInternalFrameCreated(sessionInternalFrame, fileToOpen);
         }

         @Override
         public void errorOccured(Throwable th, boolean stopConnection)
         {
         }
      };

      new ConnectToAliasCommand(selectedAlias, true, callback).execute();

   }

   private static void onSessionInternalFrameCreated(final SessionInternalFrame sessionInternalFrame, final File fileToOpen)
   {
      SwingUtilities.invokeLater(() -> openFileInMainSqlTab(fileToOpen, sessionInternalFrame));
   }

   private static void openFileInMainSqlTab(File file, SessionInternalFrame sessionInternalFrame)
   {
      sessionInternalFrame.getMainSQLPanelAPI().getFileHandler().fileOpen(file);
      sessionInternalFrame.setMainSqlFile(file);
   }

}
