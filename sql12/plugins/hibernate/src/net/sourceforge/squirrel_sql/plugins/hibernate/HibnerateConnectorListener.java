package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration;

public interface HibnerateConnectorListener
{
   void connected(HibernateConnection con, HibernateConfiguration cfg);

   public void connectFailed(Throwable e);


}
