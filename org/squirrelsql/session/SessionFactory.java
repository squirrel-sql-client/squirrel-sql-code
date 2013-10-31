package org.squirrelsql.session;

import javafx.scene.control.Tab;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;

public class SessionFactory
{
   private SessionTabbedPaneCtrl _sessionTabbedPaneController = new SessionTabbedPaneCtrl();

   public void createSession(DbConnectorResult dbConnectorResult)
   {
      SessionCtrl sessionCtrl = new SessionCtrl(dbConnectorResult);
      Tab tab = _sessionTabbedPaneController.addSessionTab(sessionCtrl);
      sessionCtrl.setSessionTab(tab);
   }

   public SessionTabbedPaneCtrl getSessionTabbedPaneCtrl()
   {
      return _sessionTabbedPaneController;
   }
}
