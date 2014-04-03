package org.squirrelsql.session;

import javafx.scene.control.Tab;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;

public class SessionFactory
{
   private SessionTabbedPaneCtrl _sessionTabbedPaneController = new SessionTabbedPaneCtrl();

   public void createSession(DbConnectorResult dbConnectorResult)
   {
      SessionCtrl sessionCtrl = new SessionCtrl(dbConnectorResult);
      _sessionTabbedPaneController.addSessionTab(sessionCtrl);
   }

   public SessionTabbedPaneCtrl getSessionTabbedPaneCtrl()
   {
      return _sessionTabbedPaneController;
   }
}
