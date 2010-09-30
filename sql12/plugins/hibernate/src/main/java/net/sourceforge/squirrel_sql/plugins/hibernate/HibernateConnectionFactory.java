package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class HibernateConnectionFactory
{
   private static ILogger s_log = LoggerController.createLogger(HibernateConnectionFactory.class);

   public static HibernateConnection createHibernateConnection(HibernateConfiguration cfg, HibernatePlugin plugin)
         throws Exception
   {
      if(cfg.isUseProcess())
      {
         ServerMain serverMain = createProcessAndDoLookup(cfg, plugin);
         return new HibernateConnection(serverMain.createHibernateServerConnection(cfg), cfg.isUseProcess(), serverMain, cfg.isEndProcessOnDisconnect());
      }
      else
      {
         final IntraVmConnectionFactory intraVmConnectionFactory = new IntraVmConnectionFactory();
         return new HibernateConnection(intraVmConnectionFactory.createHibernateConnection(cfg), false, null, cfg.isEndProcessOnDisconnect());
      }
   }

   private static ServerMain createProcessAndDoLookup(HibernateConfiguration cfg, HibernatePlugin plugin)
   {
      try
      {
         IMessageHandler mh = plugin.getApplication().getMessageHandler();

         ServerMain stub;
         if (cfg.isEndProcessOnDisconnect())
         {
            mh.showMessage("Launching Hibernate process ...");
            try
            {
               launchProcess(cfg);
            }
            catch (Throwable e)
            {
               s_log.error("Error launching process. Maybe port is in use. Tryin to connect and kill existing process:", e);
               try
               {
                  stub = attachToProcess(cfg, mh, true);
                  if (null != stub)
                  {
                     stub.exit();
                  }
               }
               catch (Throwable e1)
               {
                  // Nothing
               }

               launchProcess(cfg);
            }

            stub = attachToProcess(cfg, mh, false);
         }
         else
         {
            stub = attachToProcess(cfg, mh, false);
            if(null == stub)
            {
               mh.showMessage("Attaching to existing Hibernate process failed. Now will launch new process ...");
               launchProcess(cfg);
               stub = attachToProcess(cfg, mh, false);
            }
         }


         if (null == stub)
         {
            throw new IllegalStateException("Could not attach to Hibernate process");
         }

         mh.showMessage("Successfully attached to Hibernate process. Now creating Hibernate session.");
         return stub;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private static void launchProcess(HibernateConfiguration cfg)
         throws IOException
   {
      String command = cfg.getCommand().trim();
      Runtime.getRuntime().exec(toCommandArray(command));
   }

   private static ServerMain attachToProcess(HibernateConfiguration cfg, IMessageHandler mh, boolean silent)
   {
      ServerMain stub = null;
      Throwable reThrow = null;

      Object sync = new Object();
      synchronized (sync)
      {
         for(int i=0; i < 10; ++i)
         {
            try
            {
               sync.wait(150);
               Registry registry = LocateRegistry.getRegistry("localhost", cfg.getProcessPort());
               stub = (ServerMain) registry.lookup(ServerMain.class.getName());

               break;
            }
            catch (Throwable t)
            {
               reThrow = t;
            }
         }
      }

      if(false == silent && null == stub)
      {
         mh.showErrorMessage("Failed to attach to Hibernate process: " + reThrow);
         s_log.debug("Failed to attach to Hibernate process: " + reThrow, reThrow);
      }
      return stub;
   }



   private static String[] toCommandArray(String cmd)
   {
      ArrayList<String> ret = new ArrayList<String>();

      boolean inQuotes = false;

      int startPos = 0;
      for(int i=0; i < cmd.length(); ++i)
      {
         char c = cmd.charAt(i);

         if('"' == c)
         {
            inQuotes = !inQuotes;
         }

         if(inQuotes)
         {
            continue;
         }

         if(Character.isWhitespace(c))
         {
            String arrItem = cmd.substring(startPos, i).trim();
            if (0 < arrItem.length())
            {
               ret.add(removeLeadingAndTrailingQuotes(arrItem));
            }
            startPos = i;
         }
      }

      if(startPos < cmd.length() - 1)
      {
         String arrItem = cmd.substring(startPos, cmd.length()).trim();
         if (0 < arrItem.length())
         {
            ret.add(removeLeadingAndTrailingQuotes(arrItem));
         }
      }

      return ret.toArray(new String[ret.size()]);
   }

   private static String removeLeadingAndTrailingQuotes(String s)
   {
      String ret = s;

      if(ret.startsWith("\""))
      {
         ret = ret.substring(1);
      }

      if(ret.endsWith("\""))
      {
         ret = ret.substring(0, ret.length()-1);
      }

      return ret;
   }

}
