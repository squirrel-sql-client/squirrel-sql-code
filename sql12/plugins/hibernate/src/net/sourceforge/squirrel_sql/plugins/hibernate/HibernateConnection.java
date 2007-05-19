package net.sourceforge.squirrel_sql.plugins.hibernate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;

public class HibernateConnection
{
   private Object _sessionFactoryImpl;
   private URLClassLoader _cl;
   private SessionFactoryImplIF _sessionFactoryImplIF;

   public HibernateConnection(Object sessionFactoryImpl, URLClassLoader cl)
   {
      _sessionFactoryImpl = sessionFactoryImpl;
      _cl = cl;

      _sessionFactoryImplIF = (SessionFactoryImplIF)
         Proxy.newProxyInstance(getClass().getClassLoader(),
                                new Class<?>[]{SessionFactoryImplIF.class},
                                new SessionFactoryImplIFCaller(_sessionFactoryImpl));
   }


   public ArrayList<String> generateSQL(String hqlQuery)
   {
      try
      {
//         Class<?> stringClass = _cl.loadClass("java.lang.String");
//         Class<?> booleanTypeClass = _cl.loadClass("java.lang.Boolean").getField("TYPE").getClass();
//         Class<?> mapClass = _cl.loadClass("java.util.Map");


         Class<?> queryPlanCl = _cl.loadClass("org.hibernate.engine.query.HQLQueryPlan");
         
         Class<?> sessionFactoryImplementorClass = _cl.loadClass("org.hibernate.engine.SessionFactoryImplementor");


         Constructor<?> queryPlanConstr = queryPlanCl.getConstructor(new Class[]{String.class, Boolean.TYPE, Map.class, sessionFactoryImplementorClass});

         Object queryPlan = queryPlanConstr.newInstance(new Object[]{hqlQuery, false, Collections.EMPTY_MAP, _sessionFactoryImpl});

         Method getTranslatorsMeth = queryPlanCl.getMethod("getTranslators", new Class[0]);

         Object[] translators = (Object[]) getTranslatorsMeth.invoke(queryPlan, new Object[0]);


         ArrayList<String> ret = new ArrayList<String>();

         Class<?> queryTranslatorCl = _cl.loadClass("org.hibernate.hql.QueryTranslator");
         Method collectSqlStringsMeth = queryTranslatorCl.getMethod("collectSqlStrings");

         for (Object translator : translators)
         {
            List sqls = (List) collectSqlStringsMeth.invoke(translator, new Object[0]);

            for (Object sql : sqls)
            {
               ret.add(sql.toString());
            }
         }

         return ret;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }



   public void close()
   {
      _sessionFactoryImplIF.close();
   }
}
