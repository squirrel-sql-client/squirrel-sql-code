package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class IntraVmConnectionFactory
{                                              
   public HibernateServerConnection createHibernateConnection(HibernateConfiguration cfg)
   {
      try
      {
         ClassLoader cl = getClassLoader(cfg);

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

         Thread.currentThread().setContextClassLoader(null);

         return new HibernateServerConnectionImpl(sessionFactoryImpl, cl);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private static URLClassLoader getClassLoader(HibernateConfiguration cfg)
         throws Exception
   {
      String[] classpath = cfg.getClassPathEntries();

      URL[] classpathUrls = new URL[classpath.length];

      for (int i = 0; i < classpathUrls.length; i++)
      {
         classpathUrls[i] = new File(classpath[i]).toURI().toURL();
      }

      return new URLClassLoader(classpathUrls, null);
   }
}