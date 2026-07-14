package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.io.IOException;
import java.net.ServerSocket;
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
      int rangeSize = 65535 - PORT_OFFSET;

      for(int attempt = 0; attempt < rangeSize; attempt++)
      {
         if(_nextPort >= 65535)
         {
            // Wrap around. Accepted trouble: a Session is still open and (65535 - PORT_OFFSET)
            // Sessions were created between, so a port in use may be handed out again.
            _nextPort = PORT_OFFSET;
         }

         int candidatePort = _nextPort++;

         if(isPortAvailable(candidatePort))
         {
            return candidatePort;
         }
      }

      throw new IllegalStateException(
            "No free MCP port available in the range " + PORT_OFFSET + " .. " + (65535 - 1) + ".");
   }

   /**
    * Probes whether a TCP port is free by briefly binding a {@link ServerSocket}
    * to it. There is an inherent race: the port may be taken again between this
    * check and the MCP server actually binding it — accepted for this use.
    */
   private static boolean isPortAvailable(int port)
   {
      try(ServerSocket serverSocket = new ServerSocket(port))
      {
         // Bound successfully (and closed on exit of the try) => the port is free.
         return true;
      }
      catch(IOException e)
      {
         return false;
      }
   }
}
