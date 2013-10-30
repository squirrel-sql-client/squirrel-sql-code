package org.squirrelsql.session;

import org.squirrelsql.SessionTabbedPaneCtrl;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;

public class SessionFactory
{
   private SessionTabbedPaneCtrl _sessionTabbedPaneController = new SessionTabbedPaneCtrl();

   public void createSession(DbConnectorResult dbConnectorResult)
   {
      _sessionTabbedPaneController.addSessionTab(new SessionCtrl(dbConnectorResult));
   }

   public SessionTabbedPaneCtrl getSessionTabbedPaneCtrl()
   {
      return _sessionTabbedPaneController;
   }
}
