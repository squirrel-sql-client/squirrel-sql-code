package org.squirrelsql.session;

import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.session.action.ActionManager;
import org.squirrelsql.session.sql.NewSqlTabCtrl;

import java.util.ArrayList;
import java.util.List;

public class SessionManager
{
   private SessionTabbedPaneCtrl _sessionTabbedPaneController = new SessionTabbedPaneCtrl();

   private SessionTabContext _currentlyActiveOrActivatingContext;

   private volatile int _sessionIdSequence;
   private List<SessionManagerListener> _sessionManagerListeners = new ArrayList<>();

   public int getNextSessionContextId()
   {
      return ++_sessionIdSequence;
   }


   public void createSession(DbConnectorResult dbConnectorResult)
   {
      Session session = new Session(dbConnectorResult);
      SessionTabContext sessionTabContext = new SessionTabContext(session, true);
      session.setMainTabContext(sessionTabContext);

      setCurrentlyActiveOrActivatingContext(sessionTabContext);

      SessionCtrl sessionCtrl = new SessionCtrl(_currentlyActiveOrActivatingContext);
      sessionTabContext.setTab(_sessionTabbedPaneController.addSessionTab(sessionCtrl));
      new ActionManager().updateActionUIs();
   }

   public void createSqlTab(SessionTabContext sessionTabContext)
   {
      SessionTabContext newSqlTabContext = new SessionTabContext(sessionTabContext.getSession(), false);
      setCurrentlyActiveOrActivatingContext(newSqlTabContext);


      NewSqlTabCtrl newSqlTabCtrl = new NewSqlTabCtrl(newSqlTabContext);
      newSqlTabContext.setTab(_sessionTabbedPaneController.addSqlTab(newSqlTabCtrl));
      new ActionManager().updateActionUIs();
   }


   public SessionTabbedPaneCtrl getSessionTabbedPaneCtrl()
   {
      return _sessionTabbedPaneController;
   }

   public void addSessionManagerListener(SessionManagerListener sessionManagerListener)
   {
      _sessionManagerListeners.remove(sessionManagerListener);
      _sessionManagerListeners.add(sessionManagerListener);
   }

   public void removeSessionManagerListener(SessionManagerListener sessionManagerListener)
   {
      _sessionManagerListeners.remove(sessionManagerListener);
   }

   public SessionTabContext getCurrentlyActiveOrActivatingContext()
   {
      return _currentlyActiveOrActivatingContext;
   }

   public void setCurrentlyActiveOrActivatingContext(SessionTabContext currentlyActiveOrActivatingContext)
   {
      _currentlyActiveOrActivatingContext = currentlyActiveOrActivatingContext;
      fireContextActiveOrActivating(currentlyActiveOrActivatingContext);
   }

   private void fireContextActiveOrActivating(SessionTabContext currentlyActiveOrActivatingContext)
   {
      for (SessionManagerListener sessionManagerListener : _sessionManagerListeners.toArray(new SessionManagerListener[0]))
      {
         sessionManagerListener.contextActiveOrActivating(currentlyActiveOrActivatingContext);
      }
   }


   public void sessionClose(SessionTabContext sessionTabContext)
   {

      // This codes contract is: Closing is always followed by a call to contextActiveOrActivating()

      fireContextClosing(sessionTabContext);

      if(sessionTabContext.matches(_currentlyActiveOrActivatingContext))
      {
         _currentlyActiveOrActivatingContext = null;
      }

      fireContextActiveOrActivating(_currentlyActiveOrActivatingContext);
   }

   private void fireContextClosing(SessionTabContext sessionTabContext)
   {
      for (SessionManagerListener sessionManagerListener : _sessionManagerListeners.toArray(new SessionManagerListener[0]))
      {
         sessionManagerListener.contextClosing(sessionTabContext);
      }
   }
}
