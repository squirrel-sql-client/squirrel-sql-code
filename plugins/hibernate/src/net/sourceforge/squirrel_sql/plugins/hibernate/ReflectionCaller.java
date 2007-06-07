package net.sourceforge.squirrel_sql.plugins.hibernate;

import org.hibernate.engine.SubselectFetch;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.net.URLClassLoader;
import java.util.List;
import java.util.ArrayList;

public class ReflectionCaller
{
   private ClassLoader _cl;
   private Object _callee;

   public ReflectionCaller(ClassLoader cl)
   {
      this(null, cl);
   }

   public ReflectionCaller(Object callee, ClassLoader cl)
   {
      _cl = cl;
      _callee = callee;
   }


   public ReflectionCaller getClass(String className)
   {
      try
      {
         return new ReflectionCaller(_cl.loadClass(className), _cl);
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
         Constructor constr = ((Class) _callee).getDeclaredConstructor(paramTypes);
         return new ReflectionCaller(constr.newInstance(params), _cl);
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
         Object[] callees = (Object[]) _callee.getClass().getDeclaredMethod(methodName).invoke(_callee);

         List<ReflectionCaller> ret = new ArrayList<ReflectionCaller>();

         for (Object callee : callees)
         {
            ret.add(new ReflectionCaller(callee, _cl));
         }

         return ret;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public ReflectionCaller callMethod(String methodName)
   {
      try
      {
         return new ReflectionCaller(_callee.getClass().getDeclaredMethod(methodName).invoke(_callee), _cl);
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
}
