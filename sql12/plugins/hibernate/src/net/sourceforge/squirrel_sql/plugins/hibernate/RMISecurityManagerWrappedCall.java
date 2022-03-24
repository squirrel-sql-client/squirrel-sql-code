package net.sourceforge.squirrel_sql.plugins.hibernate;

import java.security.Permission;
import java.util.concurrent.Callable;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class RMISecurityManagerWrappedCall
{
   private static ILogger s_log = LoggerController.createLogger(RMISecurityManagerWrappedCall.class);

   private final SecurityManager _rmiSecurityManager;

   public RMISecurityManagerWrappedCall()
   {
      _rmiSecurityManager = new SecurityManager()
      {
         @Override
         public void checkPermission(Permission perm)
         {
         }
      };
   }

   public <T> T call(Callable<T> callable)
   {
      SecurityManager old = getSecurityManagerSave();

      try
      {
         setSecurityManagerSave(_rmiSecurityManager);
         return callable.call();
      }
      catch(Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
      finally
      {
         setSecurityManagerSave(old);
      }
   }

   private void setSecurityManagerSave(SecurityManager old)
   {
      try
      {
         System.setSecurityManager(old);
      }
      catch(UnsupportedOperationException uoe)
      {
         if(uoe.toString().endsWith("The Security Manager is deprecated and will be removed in a future release"))
         {
            s_log.warn("Calling RMISecurityManager raised UnsupportedOperationException introduced with Java 18: " + uoe);
         }
         else
         {
            s_log.error(uoe);
         }
      }
      catch(Exception e)
      {
         s_log.error(e);
      }
   }

   private SecurityManager getSecurityManagerSave()
   {
      try
      {
         return System.getSecurityManager();
      }
      catch(UnsupportedOperationException uoe)
      {
         if(uoe.toString().endsWith("The Security Manager is deprecated and will be removed in a future release"))
         {
            s_log.warn("Calling RMISecurityManager raised UnsupportedOperationException introduced with Java 18: " + uoe);
         }
         else
         {
            s_log.error(uoe);
         }
      }
      catch(Exception e)
      {
         s_log.error(e);
      }

      return null;
   }
}
