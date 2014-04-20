package org.squirrelsql.session.sql;

import javafx.scene.control.Tab;
import org.squirrelsql.AppState;
import org.squirrelsql.session.SessionManagerListener;
import org.squirrelsql.session.SessionTabContext;

public class NewSqlTabController
{
   private Tab _sqlSessionTab = new Tab("Hallo");
   private final SessionManagerListener _sessionManagerListener;

   public NewSqlTabController(SessionTabContext sessionTabContext)
   {
      _sessionManagerListener = new SessionManagerListener()
      {
         @Override
         public void contextActiveOrActivating(SessionTabContext sessionTabContext)
         {

         }

         @Override
         public void contextClosing(SessionTabContext sessionTabContext)
         {
            close();
         }
      };

      AppState.get().getSessionManager().addSessionManagerListener(_sessionManagerListener);
      _sqlSessionTab.setOnClosed(e -> close());
   }

   private void close()
   {
      AppState.get().getSessionManager().removeSessionManagerListener(_sessionManagerListener);

      if (null != _sqlSessionTab.getTabPane())
      {
         _sqlSessionTab.getTabPane().getTabs().remove(_sqlSessionTab);
      }
   }

   public Tab getSqlTab()
   {
      return _sqlSessionTab;
   }
}
