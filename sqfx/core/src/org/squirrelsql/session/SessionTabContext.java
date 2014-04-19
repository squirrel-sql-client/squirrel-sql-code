package org.squirrelsql.session;

import org.squirrelsql.AppState;

public class SessionTabContext
{
   private final int _sessionTabContextId;
   private Session _session;

   public SessionTabContext(Session session)
   {
      _session = session;
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
}
