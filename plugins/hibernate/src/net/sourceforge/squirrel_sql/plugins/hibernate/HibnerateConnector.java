package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfiguration;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class HibnerateConnector
{
   private HibnerateConnectorListener _hibnerateConnectorListener;

   public HibnerateConnector(HibnerateConnectorListener hibnerateConnectorListener)
   {
      _hibnerateConnectorListener = hibnerateConnectorListener;
   }

   public void connect(final HibernateConfiguration cfg)
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
         String provider = cfg.getProvider();

         String[] classpath = cfg.getClassPathEntries();

         URL[] classpathUrls = new URL[classpath.length];

         for (int i = 0; i < classpathUrls.length; i++)
         {
            classpathUrls[i] = new File(classpath[i]).toURL();
         }

         URLClassLoader cl = new URLClassLoader(classpathUrls, null);

         Thread.currentThread().setContextClassLoader(cl);
         

         Class<?> providerClass = cl.loadClass(provider);

         Object sessionFactoryProviderImpl = providerClass.newInstance();

         Method meth = providerClass.getMethod("getSessionFactoryImpl", new Class[0]);
         Object sessionFactoryImpl = meth.invoke(sessionFactoryProviderImpl, new Object[0]);

         final HibernateConnection con = new HibernateConnection(sessionFactoryImpl, cl);


         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _hibnerateConnectorListener.connected(con);
            }
         });

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
}
