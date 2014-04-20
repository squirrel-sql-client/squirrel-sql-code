package org.squirrelsql.session;

import javafx.scene.control.Tab;
import org.squirrelsql.AppState;

public class SessionTabContext
{
   private final int _sessionTabContextId;
   private Session _session;
   private boolean _sessionMainTab;
   private Tab _tab;

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

   public void setTab(Tab tab)
   {
      _tab = tab;
   }

   public Tab getTab()
   {
      return _tab;
   }
}
