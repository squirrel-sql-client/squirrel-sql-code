package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerMainImpl implements ServerMain
{

   public static void main(String[] args) throws Exception
   {
      int port;

      try
      {
         String portString = args[0].substring(PORT_PARAM_PREFIX.length(), args[0].length());
         port = Integer.parseInt(portString);
      }
      catch (Exception e)
      {
         System.out.println("ERROR: Invalid port parameter. Should be " + PORT_PARAM_PREFIX + "<port number>");

         throw e;
      }


      java.rmi.registry.LocateRegistry.createRegistry(port);


      ServerMainImpl obj = new ServerMainImpl();
      ServerMain stub = (ServerMain) UnicastRemoteObject.exportObject(obj, 0);

      Registry registry = LocateRegistry.getRegistry(port);
      registry.rebind(ServerMain.class.getName(), stub);

      System.out.println("Hibernate process ready");

   }


   @Override
   public HibernateServerConnection createHibernateServerConnection(HibernateConfiguration cfg) throws RemoteException
   {
      try
      {
         IntraVmConnectionFactory intraVmConnectionFactory = new IntraVmConnectionFactory();
         HibernateServerConnection ret = intraVmConnectionFactory.createHibernateConnection(cfg, true);
         
         return (HibernateServerConnection) UnicastRemoteObject.exportObject(ret, 0);
      }
      catch (Throwable t)
      {
         throw new RemoteException(t.getMessage(), HibernateServerExceptionUtil.prepareTransport(t));
      }
   }

   @Override
   public void exit()
   {
      System.out.println("Will exit on client request");
      System.exit(0);
   }
}
