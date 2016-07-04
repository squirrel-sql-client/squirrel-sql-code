package net.sourceforge.squirrel_sql.plugins.hibernate.server;

/**
 * Created by GWA on 03.07.2016.
 */
public class VersionInfo
{
   public static boolean isVersion3(ClassLoader cl)
   {
      ReflectionCaller versionClass = new ReflectionCaller(ReflectionCaller.getClassPlain("org.hibernate.Version", cl));
      String version = (String) versionClass.callMethod("getVersionString").getCallee();
      if(version.startsWith("5"))
      {
         return false;
      }

      return true;
   }

   public static boolean isVersion5_2(ClassLoader cl)
   {
      ReflectionCaller versionClass = new ReflectionCaller(ReflectionCaller.getClassPlain("org.hibernate.Version", cl));
      String version = (String) versionClass.callMethod("getVersionString").getCallee();
      if(version.startsWith("5.2"))
      {
         return true;
      }

      return false;
   }

   //public static boolean isEntityManagerTheSessionFactory(ClassLoader cl)
   //{
   //   ReflectionCaller versionClass = new ReflectionCaller(ReflectionCaller.getClassPlain("org.hibernate.Version", cl));
   //   String version = (String) versionClass.callMethod("getVersionString").getCallee();
   //   if(version.startsWith("5.2"))
   //   {
   //      return true;
   //   }
   //
   //   return false;
   //}
}
