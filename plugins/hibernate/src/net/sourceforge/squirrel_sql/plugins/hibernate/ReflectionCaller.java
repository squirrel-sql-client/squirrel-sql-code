package net.sourceforge.squirrel_sql.plugins.hibernate;

import org.hibernate.engine.SubselectFetch;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.net.URLClassLoader;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class ReflectionCaller
{
   private Object _callee;


   public ReflectionCaller(Object callee)
   {
      _callee = callee;
   }

   public ReflectionCaller()
   {
      this(null);
   }


   public ReflectionCaller getClass(String className, ClassLoader cl)
   {
      try
      {
         return new ReflectionCaller(cl.loadClass(className));
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

   public ReflectionCaller callConstructor(Class[] paramTypes, Object[] params)
   {
      try
      {
         Constructor constr = getCalleeClass().getDeclaredConstructor(paramTypes);
         return new ReflectionCaller(constr.newInstance(params));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

   }

   public List<ReflectionCaller> callArrayMethod(String methodName)
   {
      try
      {
         Method meth = getDeclaredMethodIncludingSuper(methodName);

         Object[] callees = (Object[]) meth.invoke(_callee);

         List<ReflectionCaller> ret = new ArrayList<ReflectionCaller>();

         for (Object callee : callees)
         {
            ret.add(new ReflectionCaller(callee));
         }

         return ret;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   public Object getCallee()
   {
      return _callee;
   }

   public Collection<ReflectionCaller> callCollectionMethod(String methodName)
   {
      try
      {
         Method meth = getDeclaredMethodIncludingSuper(methodName);
         meth.setAccessible(true);

         Collection callees = (Collection) meth.invoke(_callee);

         List<ReflectionCaller> ret = new ArrayList<ReflectionCaller>();

         for (Object callee : callees)
         {
            ret.add(new ReflectionCaller(callee));
         }

         return ret;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public ReflectionCaller getField(String fieldName)
   {
      try
      {
         return new ReflectionCaller(getCalleeClass().getDeclaredField(fieldName).get(_callee));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public Class getCalleeClass()
   {
      if(_callee instanceof Class)
      {
         return (Class) _callee;
      }
      else
      {
         return _callee.getClass();
      }

   }

   /**
    * Though this method should normaly be redundant with
    * callMethod(String methodName, Object... params)
    * NoSuchMethodErrors occur if it isn't there. 
    */
   public ReflectionCaller callMethod(String methodName)
   {
      return callMethod(methodName, new RCParam(new Object[0]));
   }

   public ReflectionCaller callMethod(String methodName, Object... params)
   {
      return callMethod(methodName, new RCParam(params));
   }


   public ReflectionCaller callMethod(String methodName, RCParam param)
   {
      try
      {
         Class[] paramTypes = new Class[param.size()];
         Object[] paramValues = new Object[param.size()];

         for (int i = 0; i < paramTypes.length; i++)
         {
            paramTypes[i] = param.getType(i);
            paramValues[i] = param.getValue(i);
         }

         Method meth = getDeclaredMethodIncludingSuper(methodName, paramTypes);
         meth.setAccessible(true);

         return new ReflectionCaller(meth.invoke(_callee, paramValues));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }



   private Method getDeclaredMethodIncludingSuper(String methodName, Class... paramTypes)
      throws NoSuchMethodException
   {
      Class clazz = getCalleeClass();

      NoSuchMethodException throwBuf = new NoSuchMethodException(methodName);

      while(null != clazz)
      {
         try
         {
            Method ret = clazz.getDeclaredMethod(methodName, paramTypes);
            ret.setAccessible(true);
            return ret;
         }
         catch (NoSuchMethodException e)
         {
            throwBuf = e;
         }
         clazz = clazz.getSuperclass();
      }

      throw throwBuf;

   }

   public static void main(String[] args)
   {
      ReflectionCaller c = new ReflectionCaller(Integer.class);

      System.out.println(c.getCalleeClass().getName());


   }

}
