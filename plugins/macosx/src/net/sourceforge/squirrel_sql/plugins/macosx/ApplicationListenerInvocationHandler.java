package net.sourceforge.squirrel_sql.plugins.macosx;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.action.AboutCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.GlobalPreferencesCommand;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class ApplicationListenerInvocationHandler implements InvocationHandler
{
   private Object _com_apple_eawt_ApplicationAdapter_Instance;
   private IApplication _app;
   private Class<?> _com_apple_eawt_ApplicationEvent;
   private Method _setHandled;

   ApplicationListenerInvocationHandler(IApplication app)
   {
      _app = app;
      try
      {
         _com_apple_eawt_ApplicationAdapter_Instance = Class.forName("com.apple.eawt.ApplicationAdapter").newInstance();
         _com_apple_eawt_ApplicationEvent = Class.forName("com.apple.eawt.ApplicationEvent");
         _setHandled = _com_apple_eawt_ApplicationEvent.getMethod("setHandled", new Class[]{Boolean.TYPE});

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      if(   method.getName().equals("handleAbout")
         && 1 == method.getParameterTypes().length
         && method.getParameterTypes()[0].equals(_com_apple_eawt_ApplicationEvent))
      {
         handleAbout(args[0]);
         return null;
      }
      else if(   method.getName().equals("handlePreferences")
         && 1 == method.getParameterTypes().length
         && method.getParameterTypes()[0].equals(_com_apple_eawt_ApplicationEvent))
      {
         handlePreferences(args[0]);
         return null;
      }
      else if(   method.getName().equals("handleQuit")
         && 1 == method.getParameterTypes().length
         && method.getParameterTypes()[0].equals(_com_apple_eawt_ApplicationEvent))
      {
         handleQuit(args[0]);
         return null;
      }
      else
      {
         Method adapterMethod =
            _com_apple_eawt_ApplicationAdapter_Instance.getClass().getMethod(method.getName(), method.getParameterTypes());

         return adapterMethod.invoke(_com_apple_eawt_ApplicationAdapter_Instance, args);
      }
   }

   public void handleAbout(Object applicationEvent)
   {
      try
      {
         _setHandled.invoke(applicationEvent, new Object[]{Boolean.TRUE});
         new AboutCommand(_app).execute();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   public void handlePreferences(Object applicationEvent)
   {
      try
      {
         _setHandled.invoke(applicationEvent, new Object[]{Boolean.TRUE});
         new GlobalPreferencesCommand(_app).execute();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   public void handleQuit(Object applicationEvent)
   {
      try
      {
         _setHandled.invoke(applicationEvent, new Object[]{Boolean.TRUE});
         _app.getMainFrame().dispose();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
