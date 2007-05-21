package net.sourceforge.squirrel_sql.plugins.hibernate;

public interface HibnerateConnectorListener
{
   void connected(HibernateConnection con);

   public void connectFailed(Throwable e);


}
