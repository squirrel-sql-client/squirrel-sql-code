package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISession;
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
         URLClassLoader cl = getClassLoader(cfg);
         Thread.currentThread().setContextClassLoader(cl);

         Object sessionFactoryImpl = null;

         if (cfg.isUserDefinedProvider())
         {
            String provider = cfg.getProvider();
            Class<?> providerClass = cl.loadClass(provider);

            Object sessionFactoryProviderImpl = providerClass.newInstance();

            sessionFactoryImpl =
               new ReflectionCaller(sessionFactoryProviderImpl).callMethod("getSessionFactoryImpl").getCallee();
         }
         else if (cfg.isJPA())
         {
            String persistenceUnitName = cfg.getPersistenceUnitName();
            Class<?> persistenceClass = cl.loadClass("javax.persistence.Persistence");

            Method createMeth = persistenceClass.getMethod("createEntityManagerFactory", String.class);
            Object hibernateEntityManagerFactory = createMeth.invoke(persistenceClass, persistenceUnitName);
            ReflectionCaller rc = new ReflectionCaller(hibernateEntityManagerFactory);
            sessionFactoryImpl = rc.callMethod("getSessionFactory").getCallee();
         }
         else
         {
            Class<?> confiugrationClass = cl.loadClass("org.hibernate.cfg.Configuration");
            ReflectionCaller rc = new ReflectionCaller(confiugrationClass.newInstance());

            sessionFactoryImpl = rc.callMethod("configure").callMethod("buildSessionFactory").getCallee();
         }

         HibernateConnection con = new HibernateConnection(sessionFactoryImpl, cl);
         sendConnection(con, cfg);
             
         Thread.currentThread().setContextClassLoader(null);

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

   private URLClassLoader getClassLoader(HibernateConfiguration cfg)
       throws Exception 
   {
       String[] classpath = cfg.getClassPathEntries();

       URL[] classpathUrls = new URL[classpath.length];

       for (int i = 0; i < classpathUrls.length; i++)
       {
          classpathUrls[i] = new File(classpath[i]).toURL();
       }

       URLClassLoader cl = new URLClassLoader(classpathUrls, null);

       return cl;
   }
}
