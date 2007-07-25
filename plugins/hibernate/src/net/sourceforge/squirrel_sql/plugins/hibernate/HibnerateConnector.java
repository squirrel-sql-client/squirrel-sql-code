package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfiguration;
import net.sourceforge.squirrel_sql.plugins.hibernate.util.SessionFactoryImplFromSquirrelSessionProvider;

import javax.swing.*;

import org.hibernate.impl.SessionFactoryImpl;

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
            doConnect(cfg, session);
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

   private void doConnect(HibernateConfiguration cfg, ISession session)
   {
      try
      {
         String provider = cfg.getProvider();

         
         URLClassLoader cl = getClassLoader(cfg);
         
         
         
         HibernateConnection con = null;
         Object sessionFactoryImpl = null;
         
         if (provider == null) {
             //TODO:
             //     When I attempt to use the user-specified classpath, for some 
             //     reason Hibernate internally cannot cast it's Oracle9Dialect 
             //     to Dialect which is the abstract base class - it throws a 
             //     ClassCastException.  Yet without this, the user would be 
             //     forced to package up their app and stick it into SQuirreL's
             //     classpath somehow - lib or script.
             Thread.currentThread().setContextClassLoader(cl);
             sessionFactoryImpl = doConnectUsingSession(cfg, session);
         } else {
             Thread.currentThread().setContextClassLoader(cl);             
             Class<?> providerClass = cl.loadClass(provider);
    
             Object sessionFactoryProviderImpl = providerClass.newInstance();
    
             Method meth = providerClass.getMethod("getSessionFactoryImpl", new Class[0]);
             sessionFactoryImpl = meth.invoke(sessionFactoryProviderImpl, new Object[0]);
         }
         con = new HibernateConnection(sessionFactoryImpl, cl);
         sendConnection(con);
             
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
   
   private void sendConnection(final HibernateConnection con) {
       SwingUtilities.invokeLater(new Runnable()
       {
          public void run()
          {
             _hibnerateConnectorListener.connected(con);
          }
       });       
   }
   
   private SessionFactoryImpl doConnectUsingSession(HibernateConfiguration cfg, 
                                                    ISession session) {
       SessionFactoryImplFromSquirrelSessionProvider prov = 
           new SessionFactoryImplFromSquirrelSessionProvider();
       prov.setHibernateConfig(cfg);
       prov.setSession(session);
       return prov.getSessionFactoryImpl();
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
