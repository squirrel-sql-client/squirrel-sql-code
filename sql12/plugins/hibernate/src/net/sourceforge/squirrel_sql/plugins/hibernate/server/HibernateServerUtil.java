package net.sourceforge.squirrel_sql.plugins.hibernate.server;

public class HibernateServerUtil
{
   public static boolean isInitialized(ClassLoader cl, Object obj)
   {
      return (Boolean) new ReflectionCaller().callStaticMethod(cl, "org.hibernate.Hibernate", "isInitialized", new Class[]{Object.class}, new Object[]{obj})
                         .getCallee();
   }
   public static boolean isInitialized(ClassLoader cl, ReflectionCaller rc, Object obj)
   {
      return (Boolean) rc.callStaticMethod(cl, "org.hibernate.Hibernate", "isInitialized", new Class[]{Object.class}, new Object[]{obj})
                         .getCallee();
   }
}
