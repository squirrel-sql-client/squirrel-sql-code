package org.squirrelsql.session;

import org.squirrelsql.aliases.dbconnector.DbConnectorResult;

public class SessionManager
{
   private SessionTabbedPaneCtrl _sessionTabbedPaneController = new SessionTabbedPaneCtrl();
   private SessionManagerListener _sessionManagerListener;

   private SessionTabContext _currentlyActiveOrActivatingContext;

   private volatile int _sessionIdSequence;

   public int getNextSessionContextId()
   {
      return ++_sessionIdSequence;
   }


   public void createSession(DbConnectorResult dbConnectorResult)
   {
      setCurrentlyActiveOrActivatingContext(new SessionTabContext(new Session(dbConnectorResult)));

      SessionCtrl sessionCtrl = new SessionCtrl(_currentlyActiveOrActivatingContext);
      _sessionTabbedPaneController.addSessionTab(sessionCtrl);
   }

   public SessionTabbedPaneCtrl getSessionTabbedPaneCtrl()
   {
      return _sessionTabbedPaneController;
   }

   public void setSessionManagerListener(SessionManagerListener sessionManagerListener)
   {
      _sessionManagerListener = sessionManagerListener;
   }

   public SessionTabContext getCurrentlyActiveOrActivatingContext()
   {
      return _currentlyActiveOrActivatingContext;
   }

   public void setCurrentlyActiveOrActivatingContext(SessionTabContext currentlyActiveOrActivatingContext)
   {
      _currentlyActiveOrActivatingContext = currentlyActiveOrActivatingContext;
      _sessionManagerListener.contextActiveOrActivating(currentlyActiveOrActivatingContext);
   }


   public void sessionClose(SessionTabContext sessionTabContext)
   {

      // This codes contract is: Closing is always followed by a call to contextActiveOrActivating()

      _sessionManagerListener.contextClosing(sessionTabContext);

      if(sessionTabContext.matches(_currentlyActiveOrActivatingContext))
      {
         _currentlyActiveOrActivatingContext = null;
      }

      _sessionManagerListener.contextActiveOrActivating(_currentlyActiveOrActivatingContext);
   }
}
