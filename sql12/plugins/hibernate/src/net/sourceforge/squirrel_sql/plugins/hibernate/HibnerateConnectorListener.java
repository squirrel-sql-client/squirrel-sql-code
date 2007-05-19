package net.sourceforge.squirrel_sql.plugins.hibernate;

import java.net.URLClassLoader;

public interface HibnerateConnectorListener
{
   void connected(HibernateConnection con);

   public void connectFailed(Throwable e);


}
