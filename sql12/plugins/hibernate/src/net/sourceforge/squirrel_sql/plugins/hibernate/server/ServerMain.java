package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerMain extends Remote
{
   public static final String PORT_PARAM_PREFIX = "-port:";

   HibernateServerConnection createHibernateServerConnection(HibernateConfiguration cfg)
      throws RemoteException;

   void exit()
      throws RemoteException;
}
