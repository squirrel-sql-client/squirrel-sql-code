package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class IntraVmConnectionFactory
{                                              
   public HibernateServerConnection createHibernateConnection(HibernateConfiguration cfg, boolean isServer)
   {
      try
      {
         ClassLoader cl = getClassLoader(cfg);

         Thread.currentThread().setContextClassLoader(cl);

         FactoryWrapper factoryWrapper;


         if (cfg.isUserDefinedProvider())
         {
            String provider = cfg.getProvider();
            Class<?> providerClass = cl.loadClass(provider);

            Object sessionFactoryProviderImpl = providerClass.newInstance();

            factoryWrapper =
                  new FactoryWrapper(new ReflectionCaller(sessionFactoryProviderImpl).callMethod("getSessionFactoryImpl").getCallee());
         }
         else if (cfg.isJPA())
         {
            String persistenceUnitName = cfg.getPersistenceUnitName();
            Class<?> persistenceClass = cl.loadClass("javax.persistence.Persistence");

            Method createMeth = persistenceClass.getMethod("createEntityManagerFactory", String.class);
            Object entityManagerFactory = createMeth.invoke(persistenceClass, persistenceUnitName);

            if( VersionInfo.isVersion3(cl))
            {
               ReflectionCaller rc = new ReflectionCaller(entityManagerFactory);
               factoryWrapper = new FactoryWrapper(rc.callMethod("getSessionFactory").getCallee());
            }
            else
            {
               if( VersionInfo.isVersion5_2(cl) )
               {
                  factoryWrapper = new FactoryWrapper(entityManagerFactory);
                  factoryWrapper.setEntityManagerFactory(entityManagerFactory);
               }
               else
               {
                  ReflectionCaller rc = new ReflectionCaller(entityManagerFactory);
                  factoryWrapper = new FactoryWrapper(rc.callMethod("getSessionFactory").getCallee());
                  factoryWrapper.setEntityManagerFactory(entityManagerFactory);
               }
            }
         }
         else
         {
            Class<?> confiugrationClass = cl.loadClass("org.hibernate.cfg.Configuration");
            ReflectionCaller rc = new ReflectionCaller(confiugrationClass.newInstance());

            factoryWrapper = new FactoryWrapper(rc.callMethod("configure").callMethod("buildSessionFactory").getCallee());
         }

         Thread.currentThread().setContextClassLoader(null);

         return new HibernateServerConnectionImpl(factoryWrapper, cl, isServer);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private static URLClassLoader getClassLoader(HibernateConfiguration cfg)
         throws Exception
   {
      String[] classpath = ClassPathUtil.classPathAsStringArray(cfg.getClassPathItems());

      URL[] classpathUrls = new URL[classpath.length];

      for (int i = 0; i < classpathUrls.length; i++)
      {
         classpathUrls[i] = new File(classpath[i]).toURI().toURL();
      }

      return new URLClassLoader(classpathUrls, null);
   }
}