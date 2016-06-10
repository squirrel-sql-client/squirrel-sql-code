package org.squirrelsql.session.sql;

import javafx.event.Event;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.AppState;
import org.squirrelsql.session.*;
import org.squirrelsql.session.action.ActionUtil;
import org.squirrelsql.session.action.ActionScope;
import org.squirrelsql.session.action.StdActionCfg;

public class NewSqlTabCtrl
{
   private SessionTabAdmin _sessionTabAdmin;
   private final SessionManagerListener _sessionManagerListener;
   private final SqlPaneCtrl _sqlPaneCtrl;
   private SessionTabContext _newSqlTabContext;

   private final FileManager _fileManager;

   public NewSqlTabCtrl(SessionTabContext newSqlTabContext)
   {
      _newSqlTabContext = newSqlTabContext;
      _sessionManagerListener = new SessionManagerListener()
      {
         @Override
         public void contextActiveOrActivating(SessionTabContext sessionTabContext) {}

         @Override
         public void contextClosing(SessionTabContext sessionTabContext)
         {
            close(sessionTabContext);
         }
      };

      ActionUtil.setActionScope(ActionScope.SQL_EDITOR);


      AppState.get().getSessionManager().addSessionManagerListener(_sessionManagerListener);


      _sqlPaneCtrl = new SqlPaneCtrl(newSqlTabContext);
      _sqlPaneCtrl.requestFocus();

      BorderPane bp = new BorderPane();
      bp.setTop(ActionUtil.createStdActionToolbar());
      bp.setCenter(_sqlPaneCtrl.getSqlPane());

      _sessionTabAdmin = new SessionTabAdmin(_newSqlTabContext, bp, SessionTabType.SQL_TAB);
      SessionTabHeaderCtrl sessionTabHeaderCtrl = new SessionTabHeaderCtrl(newSqlTabContext, StdActionCfg.NEW_SQL_TAB.getActionCfg().getIcon());

      initStandardActions();

      _fileManager = new FileManager(_sqlPaneCtrl.getSQLTextAreaServices(), sessionTabHeaderCtrl);


      _sessionTabAdmin.addOnSelectionChanged(this::onSelectionChanged);
      _sessionTabAdmin.addOnCloseRequest(_fileManager::closeRequest);
      _sessionTabAdmin.addOnClosed(e -> close(_newSqlTabContext));
   }

   private void initStandardActions()
   {
      StdActionCfg.NEW_SQL_TAB.setAction(() -> AppState.get().getSessionManager().createSqlTab(_newSqlTabContext));
   }

   private void onSelectionChanged(Event e)
   {
      if(_sessionTabAdmin.isSelected())
      {
         AppState.get().getSessionManager().setCurrentlyActiveOrActivatingContext(_newSqlTabContext);
      }
   }


   private void close(SessionTabContext sessionTabContext)
   {
      if(false == _newSqlTabContext.equalsSession(sessionTabContext))
      {
         return;
      }


      _sqlPaneCtrl.close();
      AppState.get().getSessionManager().removeSessionManagerListener(_sessionManagerListener);

      _sessionTabAdmin.removeFromTabPane();
   }

   public SessionTabAdmin getSessionTabAdmin()
   {
      return _sessionTabAdmin;
   }
}
