package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration;

import javax.swing.*;

public class HibnerateConnector
{
   private HibernatePlugin _plugin;
   private HibnerateConnectorListener _hibnerateConnectorListener;

   public HibnerateConnector(HibernatePlugin plugin, HibnerateConnectorListener hibnerateConnectorListener)
   {
      _plugin = plugin;
      _hibnerateConnectorListener = hibnerateConnectorListener;
   }

   public void connect(final HibernateConfiguration cfg, final ISession session)
   {
      Runnable runnable = new Runnable()
      {
         public void run()
         {
            doConnect(cfg);
         }
      };

      final Thread thread = new Thread(runnable);

      thread.setPriority(Thread.MIN_PRIORITY);


      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            thread.run();
         }
      });
   }

   private void doConnect(HibernateConfiguration cfg)
   {
      try
      {
         HibernateConnection con = HibernateConnectionFactory.createHibernateConnection(cfg, _plugin);
         sendConnection(con, cfg);
      }
      catch (final Throwable t)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _hibnerateConnectorListener.connectFailed(t);
            }
         });
      }
   }

   private void sendConnection(final HibernateConnection con, final HibernateConfiguration cfg)
   {
       SwingUtilities.invokeLater(new Runnable()
       {
          public void run()
          {
             _hibnerateConnectorListener.connected(con, cfg);
          }
       });       
   }

}
