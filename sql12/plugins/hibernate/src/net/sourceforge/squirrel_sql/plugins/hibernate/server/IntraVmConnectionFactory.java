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

         String persistenceUnitName = cfg.getPersistenceUnitName();
         Class<?> persistenceClass = cl.loadClass("javax.persistence.Persistence");

         Method createMeth = persistenceClass.getMethod("createEntityManagerFactory", String.class);
         Object entityManagerFactory = createMeth.invoke(persistenceClass, persistenceUnitName);


         ReflectionCaller sessionFactoryImplementorRc =
               new ReflectionCaller(entityManagerFactory).callMethod("unwrap", cl.loadClass("org.hibernate.engine.spi.SessionFactoryImplementor"));

         factoryWrapper = new FactoryWrapper(sessionFactoryImplementorRc.getCallee());
         factoryWrapper.setEntityManagerFactory(entityManagerFactory);

         Thread.currentThread().setContextClassLoader(null);

         return new HibernateServerConnectionImpl(factoryWrapper, cl, isServer);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private URLClassLoader getClassLoader(HibernateConfiguration cfg) throws Exception
   {
      String[] classpath = ClassPathUtil.classPathAsStringArray(cfg.getClassPathItems());

      URL[] classpathUrls = new URL[classpath.length];

      for (int i = 0; i < classpathUrls.length; i++)
      {
         classpathUrls[i] = new File(classpath[i]).toURI().toURL();
      }

      //return new URLClassLoader(classpathUrls, this.getClass().getClassLoader());
      return new URLClassLoader(classpathUrls, ClassLoader.getSystemClassLoader().getParent());
   }
}