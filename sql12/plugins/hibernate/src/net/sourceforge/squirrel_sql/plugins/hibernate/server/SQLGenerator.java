package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class SQLGenerator
{
   static String generateSql(ReflectionCaller rcQuery, ClassLoader cl, FactoryWrapper sessionFactoryWrapper)
   {
      try
      {
         return extractSQLByHypersistenceUtils(rcQuery, cl, sessionFactoryWrapper);
      }
      catch(Exception hypersistenceUtilsExc)
      {
         String message = "Failed to extract SQL using the method io.hypersistence.utils.hibernate.query.SQLExtractor.from(jakarta.persistence.Query) "
                          + "of the hypersistence-utils library, see https://github.com/vladmihalcea/hypersistence-utils . "
                          + "Make sure the appropriate version of the library is in in your Hibernate class path.";
         System.out.println(message);
         System.out.println("Next trying legacy pre Hibernate 5 way to extract SQL ...");
         IllegalStateException toThrowOnFail = new IllegalStateException(message, hypersistenceUtilsExc);

         try
         {
            return extractLegacyPreHibernate5Way(rcQuery, cl, sessionFactoryWrapper);
         }
         catch (Exception legacyExc)
         {
            System.out.println("Generating SQL the legacy pre Hibernate 5 way failed with error message: " + legacyExc);
            throw toThrowOnFail;
         }
      }
   }

   private static String extractSQLByHypersistenceUtils(ReflectionCaller rcQuery, ClassLoader cl, FactoryWrapper sessionFactoryWrapper)
   {
      Class paramClass = ReflectionCaller.getClassPlain("jakarta.persistence.Query", cl);
      Object param = rcQuery.getCallee();
      ReflectionCaller res = new ReflectionCaller().callStaticMethod(cl, "io.hypersistence.utils.hibernate.query.SQLExtractor", "from",
                                                                     new Class[]{paramClass}, new Object[]{param});

      return (String) res.getCallee();
   }

   private static String extractLegacyPreHibernate5Way(ReflectionCaller rcQuery, ClassLoader cl, FactoryWrapper sessionFactoryWrapper)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
   {
      String hql = (String) rcQuery.callMethod("unwrap", cl.loadClass("org.hibernate.query.Query")).callMethod("getQueryString").getCallee();

      ReflectionCaller queryTranslatorFactoryRc = new ReflectionCaller(
            cl.loadClass("org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory").getDeclaredConstructor().newInstance());

      ReflectionCaller sessionFactoryImplementorRc =
         new ReflectionCaller(sessionFactoryWrapper.getEntityManagerFactory()).callMethod("unwrap", cl.loadClass("org.hibernate.engine.spi.SessionFactoryImplementor"));


      RCParam rcParam;


      rcParam = new RCParam();
      rcParam.add("", String.class);
      rcParam.add(hql, String.class);
      rcParam.add(java.util.Collections.EMPTY_MAP, Map.class);
      rcParam.add(sessionFactoryImplementorRc.getCallee(), cl.loadClass("org.hibernate.engine.spi.SessionFactoryImplementor"));
      rcParam.add(null, cl.loadClass("org.hibernate.engine.query.spi.EntityGraphQueryHint"));

      ReflectionCaller queryTranslatorRc = queryTranslatorFactoryRc.callMethod("createQueryTranslator",rcParam);


      rcParam = new RCParam();
      rcParam.add(java.util.Collections.EMPTY_MAP, Map.class);
      rcParam.add(false, Boolean.TYPE);

      queryTranslatorRc.callMethod("compile", rcParam);


      return (String) queryTranslatorRc.callMethod("getSQLString").getCallee();
   }
}
