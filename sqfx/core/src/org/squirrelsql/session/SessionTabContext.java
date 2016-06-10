package org.squirrelsql.session;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Tab;
import org.squirrelsql.AppState;

import java.util.ArrayList;

public class SessionTabContext
{
   private final int _sessionTabContextId;
   private Session _session;
   private boolean _sessionMainTab;
   private SessionTabAdmin _sessionTabAdmin;

   private SimpleBooleanProperty _bookmarksChanged = new SimpleBooleanProperty();
   private ArrayList<SessionTabCloseListener> _sessionTabCloseListeners = new ArrayList<>();

   public SessionTabContext(Session session, boolean sessionMainTab)
   {
      _session = session;
      _sessionMainTab = sessionMainTab;
      _sessionTabContextId = AppState.get().getSessionManager().getNextSessionContextId();
   }

   public int getSessionTabContextId()
   {
      return _sessionTabContextId;
   }

   public Session getSession()
   {
      return _session;
   }

   public boolean matches(SessionTabContext sessionTabContext)
   {
      if(null == sessionTabContext)
      {
         return false;
      }

      return _sessionTabContextId == sessionTabContext._sessionTabContextId;
   }

   public boolean isSessionMainTab()
   {
      return _sessionMainTab;
   }

   public void setTabAdmin(SessionTabAdmin sessionTabAdmin)
   {
      _sessionTabAdmin = sessionTabAdmin;
      _sessionTabAdmin.addOnClosed(e -> fireClosed());
   }

   public SessionTabAdmin getTab()
   {
      return _sessionTabAdmin;
   }

   public boolean equalsSession(SessionTabContext other)
   {
      return _session.getMainTabContext().matches(other._session.getMainTabContext());
   }

   public String getSessionTabTitle()
   {
      return SessionUtil.getSessionTabTitle(this);
   }

   public SimpleBooleanProperty bookmarksChangedProperty()
   {
      return _bookmarksChanged;
   }

   public void addOnSessionTabClosed(SessionTabCloseListener sessionTabCloseListener)
   {
      _sessionTabCloseListeners.remove(sessionTabCloseListener);
      _sessionTabCloseListeners.add(sessionTabCloseListener);
   }

   private void fireClosed()
   {
      SessionTabCloseListener[] sessionTabCloseListeners = _sessionTabCloseListeners.toArray(new SessionTabCloseListener[_sessionTabCloseListeners.size()]);

      for (SessionTabCloseListener sessionTabCloseListener : sessionTabCloseListeners)
      {
         sessionTabCloseListener.sessionClosed(this);
      }
   }

}
