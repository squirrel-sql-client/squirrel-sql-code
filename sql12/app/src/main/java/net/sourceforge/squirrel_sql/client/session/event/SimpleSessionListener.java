package net.sourceforge.squirrel_sql.client.session.event;


/**
 * Simple because it is directly attached to the Session itself
 * in contrast to SessionListener that is attached to SessionManager.
 */
public interface SimpleSessionListener
{
   public void sessionClosed();
}
