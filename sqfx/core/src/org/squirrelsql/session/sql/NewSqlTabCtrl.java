package org.squirrelsql.session.sql;

import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.AppState;
import org.squirrelsql.session.*;
import org.squirrelsql.session.action.ActionUtil;
import org.squirrelsql.session.action.ActionScope;
import org.squirrelsql.session.action.StdActionCfg;

public class NewSqlTabCtrl
{
   private Tab _newSqlTab;
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

      _newSqlTab = new Tab();
      SessionTabHeaderCtrl sessionTabHeaderCtrl = new SessionTabHeaderCtrl(newSqlTabContext, StdActionCfg.NEW_SQL_TAB.getActionCfg().getIcon());
      _newSqlTab.setGraphic(sessionTabHeaderCtrl.getTabHeader());

      _sqlPaneCtrl = new SqlPaneCtrl(newSqlTabContext);
      _sqlPaneCtrl.requestFocus();

      BorderPane bp = new BorderPane();
      bp.setTop(ActionUtil.createStdActionToolbar());
      bp.setCenter(_sqlPaneCtrl.getSqlPane());

      _newSqlTab.setContent(bp);

      initStandardActions();

      _fileManager = new FileManager(newSqlTabContext, sessionTabHeaderCtrl);


      _newSqlTab.setOnSelectionChanged(this::onSelectionChanged);
      _newSqlTab.setOnClosed(e -> close(_newSqlTabContext));
   }

   private void initStandardActions()
   {
      StdActionCfg.NEW_SQL_TAB.setAction(() -> AppState.get().getSessionManager().createSqlTab(_newSqlTabContext));
   }

   private void onSelectionChanged(Event e)
   {
      if(_newSqlTab.isSelected())
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

      if (null != _newSqlTab.getTabPane())
      {
         _newSqlTab.getTabPane().getTabs().remove(_newSqlTab);
      }
   }

   public Tab getSqlTab()
   {
      return _newSqlTab;
   }
}
