package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.event.SimpleSessionListener;

import java.util.ArrayList;

public class SimpleSessionListenerManager
{
   private final IApplication _app;
   private final Session _session;
   private ArrayList<SimpleSessionListener> _listeners = new ArrayList<SimpleSessionListener>();
   private final SessionAdapter _sessionAdapter;

   public SimpleSessionListenerManager(IApplication app, Session session)
   {
      _app = app;
      _session = session;

      _sessionAdapter = new SessionAdapter()
      {
         @Override
         public void sessionClosed(SessionEvent evt)
         {
            onSessionClosed(evt);
         }
      };

      _app.getSessionManager().addSessionListener(_sessionAdapter);

   }

   private void onSessionClosed(SessionEvent evt)
   {
      if(false == evt.getSession().getIdentifier().equals(_session.getIdentifier()))
      {
         return;
      }

      SimpleSessionListener[] simpleSessionListeners = _listeners.toArray(new SimpleSessionListener[_listeners.size()]);

      for (SimpleSessionListener simpleSessionListener : simpleSessionListeners)
      {
         simpleSessionListener.sessionClosed();
      }

      _app.getSessionManager().removeSessionListener(_sessionAdapter);
   }

   public void addListener(SimpleSessionListener simpleSessionListener)
   {
      _listeners.add(simpleSessionListener);

   }

   public void removeListener(SimpleSessionListener simpleSessionListener)
   {
      _listeners.remove(simpleSessionListener);
   }
}
