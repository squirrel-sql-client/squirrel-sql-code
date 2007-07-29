package net.sourceforge.squirrel_sql.plugins.hibernate;

public interface ConnectionListener
{
   void connectionOpened(HibernateConnection con);
   void connectionClosed();
}
