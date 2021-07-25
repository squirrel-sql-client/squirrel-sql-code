package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.util.Map;

public class SQLGenerator
{
   static String generateSql(ReflectionCaller rcQuery, ClassLoader cl, FactoryWrapper factoryWrapper)
   {
      try
      {

         String hql = (String) rcQuery.callMethod("unwrap", cl.loadClass("org.hibernate.query.Query")).callMethod("getQueryString").getCallee();

         ReflectionCaller queryTranslatorFactoryRc = new ReflectionCaller(cl.loadClass("org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory").getDeclaredConstructor().newInstance());

         ReflectionCaller sessionFactoryImplementorRc =
            new ReflectionCaller(factoryWrapper.getEntityManagerFactory()).callMethod("unwrap", cl.loadClass("org.hibernate.engine.spi.SessionFactoryImplementor"));


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
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
