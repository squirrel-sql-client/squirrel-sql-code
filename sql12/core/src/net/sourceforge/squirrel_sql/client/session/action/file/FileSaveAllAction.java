package net.sourceforge.squirrel_sql.client.session.action.file;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileHandler;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;

import java.awt.event.ActionEvent;
import java.util.Optional;

public class FileSaveAllAction extends SquirrelAction  implements ISessionAction
{
   private ISession _session;

   public FileSaveAllAction()
   {
      super(Main.getApplication());
      setEnabled(false);
   }

   public void actionPerformed(ActionEvent evt)
   {

      ISQLPanelAPI activeSqlPanelApi = null;

      int caretPosition = 0;
      if(null != Main.getApplication().getSessionManager().getActiveSession())
      {
         activeSqlPanelApi = Main.getApplication().getSessionManager().getActiveSession().getSQLPanelAPIOfActiveSessionWindow(true);

         if( null != activeSqlPanelApi )
         {
            caretPosition = activeSqlPanelApi.getCaretPosition();
         }
      }

      for (ISession session : Main.getApplication().getSessionManager().getOpenSessions())
      {
         for (SQLPanel sqlPanel : session.getSessionPanel().getAllSQLPanels())
         {
            final FileHandler fileHandler = sqlPanel.getSQLPanelAPI().getFileHandler();
            if(null != fileHandler.getFile())
            {
               fileHandler.fileSave();
            }
         }

         for (ISessionWidget sessionWidget : Main.getApplication().getWindowManager().getAllFramesOfSession(session.getIdentifier()))
         {
            if(sessionWidget instanceof SQLInternalFrame)
            {
               final FileHandler fileHandler = ((SQLInternalFrame) sessionWidget).getMainSQLPanelAPI().getFileHandler();
               if(null != fileHandler.getFile())
               {
                  fileHandler.fileSave();
               }
            }
         }
      }

      if(null != activeSqlPanelApi)
      {
         // The SQLEditorActivator is needed because externally saving a file selects the associated SQL Tab.
         // But we want the active SQL-Editor to remain the same after saving the files.
         reactivateSqlPanel(activeSqlPanelApi, caretPosition);
      }
   }

   private void reactivateSqlPanel(ISQLPanelAPI activeSqlPanelApi, int caretPosition)
   {
      switch( activeSqlPanelApi.getSQLPanelPosition() )
      {
         case MAIN_TAB_IN_SESSION_WINDOW:
            SessionUtils.activateMainSqlTab(activeSqlPanelApi.getSession().getSessionInternalFrame(), caretPosition, true);
            break;
         case ADDITIONAL_TAB_IN_SESSION_WINDOW:
            ISQLPanelAPI finalActiveSqlPanelApi = activeSqlPanelApi;
            Optional<AdditionalSQLTab> additionalSQLTab =
                  _session.getSessionPanel().getAdditionalSQLTabs().stream().filter(t -> t.getSQLPanelAPI() == finalActiveSqlPanelApi).findFirst();

            if( additionalSQLTab.isPresent() )
            {
               SessionUtils.activateAdditionalSqlTab(_session.getSessionInternalFrame(), additionalSQLTab.get(), caretPosition, true);
            }
            break;
         case IN_SQL_WORKSHEET:
            for (IWidget widget : Main.getApplication().getMainFrame().getDesktopContainer().getAllWidgets() )
            {
               if( widget instanceof SQLInternalFrame && ((SQLInternalFrame)widget).getSQLPanel().getSQLPanelAPI() == activeSqlPanelApi )
               {
                  SessionUtils.activateSqlInternalFrame((SQLInternalFrame)widget, caretPosition, true);
                  break;
               }
            }
            break;
         default:
            throw new IllegalStateException("Unknown SQLPanelPosition: " + activeSqlPanelApi.getSQLPanelPosition());
      }
   }

   @Override
   public void setSession(ISession session)
   {
      _session = session;
      setEnabled(null != _session);
   }
}
