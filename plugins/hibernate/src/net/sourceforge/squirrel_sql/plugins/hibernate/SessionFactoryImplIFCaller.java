package net.sourceforge.squirrel_sql.plugins.hibernate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SessionFactoryImplIFCaller implements InvocationHandler
{
   private Object _sessionFactoryImpl;

   public SessionFactoryImplIFCaller(Object sessionFactoryImpl)
   {
      _sessionFactoryImpl = sessionFactoryImpl;
   }


   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      Method meth = _sessionFactoryImpl.getClass().getMethod(method.getName(), method.getParameterTypes());
      return meth.invoke(_sessionFactoryImpl);
   }
}
