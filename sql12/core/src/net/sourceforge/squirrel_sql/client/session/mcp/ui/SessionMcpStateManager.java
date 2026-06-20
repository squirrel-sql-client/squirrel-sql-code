package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.util.HashMap;
import java.util.Map;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public class SessionMcpStateManager
{
   private static final int PORT_OFFSET = 23367;
   private int _nextPort = PORT_OFFSET;

   private Map<IIdentifier, SessionMcpState> _session_identifierToSessionMcpState = new HashMap<>();


   public SessionMcpState getSessionMcpState(ISession session)
   {
      SessionMcpState ret = _session_identifierToSessionMcpState.get(session.getIdentifier());

      if(null != ret)
      {
         return ret;
      }

      SessionMcpState sessionMcpState = new SessionMcpState(session.getIdentifier());

      _session_identifierToSessionMcpState.put(session.getIdentifier(), sessionMcpState);

      session.addSimpleSessionListener(() -> _session_identifierToSessionMcpState.remove(session.getIdentifier()));

      return sessionMcpState;
   }

   public int getNextPort()
   {
      if(_nextPort == 65535)
      {
         // Will run into for now accepted trouble a Session is still open and (65535 - PORT_OFFSET) Sessions were created
         // between.
         _nextPort = PORT_OFFSET;
      }

      // TODO AI: Implement check if port is available.
      return _nextPort++;
   }
}
