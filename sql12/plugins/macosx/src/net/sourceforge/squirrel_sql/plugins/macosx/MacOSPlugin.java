/*
 * MacOSPlugin.java
 *
 * Created on March 18, 2004, 11:17 AM
 */

package net.sourceforge.squirrel_sql.plugins.macosx;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author rowen
 */
public class MacOSPlugin extends DefaultPlugin
{

   private final static ILogger s_log = LoggerController.createLogger(MacOSPlugin.class);

   public String getAuthor()
   {
      return ("Neville Rowe");
   }

   public String getDescriptiveName()
   {
      return ("Mac OS User Interface");
   }

   public String getInternalName()
   {
      return ("macosx");
   }

   public String getVersion()
   {
      return ("0.1");
   }

   public String getChangeLogFileName()
   {
      return "changes.txt";
   }

   public String getHelpFileName()
   {
      return "readme.txt";
   }

   public String getLicenceFileName()
   {
      return "licence.txt";
   }

   public synchronized void load(IApplication app) throws PluginException
   {
      super.load(app);
   }

   public synchronized void initialize() throws PluginException
   {
      try
      {
         super.initialize();
         final IApplication app = getApplication();


         Class<?> com_apple_eawt_Application;
         Class<?> com_apple_eawt_ApplicationListener;
         try
         {
            com_apple_eawt_Application = Class.forName("com.apple.eawt.Application");
            com_apple_eawt_ApplicationListener = Class.forName("com.apple.eawt.ApplicationListener");
         }
         catch (ClassNotFoundException e)
         {
            s_log.error("MacOSX plugin is loaded, but Apple support class isn't in the Classpath: " + e.getMessage());
            return;
         }

         Method getApplication = com_apple_eawt_Application.getMethod("getApplication", new Class[0]);

         Object applicationInstance = getApplication.invoke(com_apple_eawt_Application, new Object[0]);


         ApplicationListenerInvocationHandler handler = new ApplicationListenerInvocationHandler(app);
         Object proxy = Proxy.newProxyInstance(IApplication.class.getClassLoader(), new Class[]{com_apple_eawt_ApplicationListener}, handler);


         Method addApplicationListener =
               com_apple_eawt_Application.getMethod("addApplicationListener", new Class[]{com_apple_eawt_ApplicationListener});
         addApplicationListener.invoke(applicationInstance, new Object[]{proxy});

         Method addPreferencesMenuItem =
               com_apple_eawt_Application.getMethod("addPreferencesMenuItem", new Class[0]);
         addPreferencesMenuItem.invoke(applicationInstance, new Object[0]);

         Method setEnabledPreferencesMenu =
               com_apple_eawt_Application.getMethod("setEnabledPreferencesMenu", new Class[]{Boolean.TYPE});
         setEnabledPreferencesMenu.invoke(applicationInstance, new Object[]{Boolean.TRUE});
      }
      catch (Exception e)
      {
         s_log.error("Failed to initialize MAC Plugin: encountered exception: " + e.getMessage(), e);
      }
   }
}
