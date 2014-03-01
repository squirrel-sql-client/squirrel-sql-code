package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration;

public interface ConnectionListener
{
   void connectionOpened(HibernateConnection con, HibernateConfiguration cfg);
   void connectionClosed();
}
