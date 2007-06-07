package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HibernateConnection
{
   private static ILogger s_log = LoggerController.createLogger(HibernateConnection.class);
   private Object _sessionFactoryImpl;
   private URLClassLoader _cl;


   public HibernateConnection(Object sessionFactoryImpl, URLClassLoader cl)
   {
      _sessionFactoryImpl = sessionFactoryImpl;
      _cl = cl;
   }


   public ArrayList<String> generateSQL(String hqlQuery)
   {
      try
      {


         Class sessionFactoryImplementorClass = (Class) new ReflectionCaller(_cl).getClass("org.hibernate.engine.SessionFactoryImplementor").getCallee();

         List<ReflectionCaller> translators =
         new ReflectionCaller(_cl)
            .getClass("org.hibernate.engine.query.HQLQueryPlan")
            .callConstructor(new Class[]{String.class, Boolean.TYPE, Map.class, sessionFactoryImplementorClass}, new Object[]{hqlQuery, false, Collections.EMPTY_MAP, _sessionFactoryImpl})
            .callArrayMethod("getTranslators");

         ArrayList<String> ret = new ArrayList<String>();


         for (ReflectionCaller translator : translators)
         {
            List sqls = (List) translator.callMethod("collectSqlStrings").getCallee();

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
      try
      {
         new ReflectionCaller(_sessionFactoryImpl, _cl).callMethod("close");
      }
      catch (Throwable t)
      {
         s_log.error(t);
      }
      _sessionFactoryImpl = null;
      _cl = null;
      System.gc();

   }
}
