package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class ReflectionCaller
{
   private Object _callee;
   private boolean _treatClassCalleeAsType;

   ReflectionCaller()
   {
      this(null);
   }

   ReflectionCaller(Object callee)
   {
      this(callee, true);
   }

   ReflectionCaller(Object callee, boolean treatClassCalleeAsType)
   {
      _callee = callee;
      _treatClassCalleeAsType = treatClassCalleeAsType;
   }


   ReflectionCaller getClass(String className, ClassLoader cl)
   {
      return new ReflectionCaller(getClassPlain(className, cl), _treatClassCalleeAsType);
   }

   public static Class getClassPlain(String className, ClassLoader cl)
   {
      try
      {
         return cl.loadClass(className);
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }


   ReflectionCaller callConstructor(Class[] paramTypes, Object[] params)
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

   public ReflectionCaller newInstance()
   {
      try
      {
         return new ReflectionCaller(getCalleeClass().newInstance(), _treatClassCalleeAsType);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }



   List<ReflectionCaller> callArrayMethod(String methodName)
   {
      try
      {
         Method meth = getDeclaredMethodIncludingSuper(methodName);

         Object[] callees = (Object[]) meth.invoke(_callee);

         List<ReflectionCaller> ret = new ArrayList<ReflectionCaller>();

         for (Object callee : callees)
         {
            ret.add(new ReflectionCaller(callee, _treatClassCalleeAsType));
         }

         return ret;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   Object getCallee()
   {
      return _callee;
   }

   Collection<ReflectionCaller> callCollectionMethod(String methodName)
   {
      try
      {
         Method meth = getDeclaredMethodIncludingSuper(methodName);
         meth.setAccessible(true);

         Collection callees = (Collection) meth.invoke(_callee);

         List<ReflectionCaller> ret = new ArrayList<ReflectionCaller>();

         for (Object callee : callees)
         {
            ret.add(new ReflectionCaller(callee, _treatClassCalleeAsType));
         }

         return ret;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   ReflectionCaller getField(String fieldName)
   {
      try
      {
         return new ReflectionCaller(getCalleeClass().getDeclaredField(fieldName).get(_callee), _treatClassCalleeAsType);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   Class getCalleeClass()
   {
      if(_treatClassCalleeAsType && _callee instanceof Class)
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
   ReflectionCaller callMethod(String methodName)
   {
      return callMethod(methodName, new RCParam(new Object[0]));
   }

   ReflectionCaller callMethod(String methodName, Object... params)
   {
      return callMethod(methodName, new RCParam(params));
   }


   ReflectionCaller callMethod(String methodName, RCParam param)
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

         return new ReflectionCaller(meth.invoke(_callee, paramValues), _treatClassCalleeAsType);
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

         for (Class anInterface : clazz.getInterfaces())
         {
            try
            {
               Method ret = anInterface.getDeclaredMethod(methodName, paramTypes);

               if(ret.isDefault())
               {
                  ret.setAccessible(true);
                  return ret;
               }
            }
            catch (NoSuchMethodException e)
            {
               throwBuf = e;
            }
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

   ReflectionCaller callStaticMethod(ClassLoader cl, String className, String methName, Class[] paramTypes, Object[] args)
   {
      try
      {
         Class<?> clazz = cl.loadClass(className);
         Method method = clazz.getDeclaredMethod(methName, paramTypes);
         method.setAccessible(true);
         return new ReflectionCaller(method.invoke(clazz, args), _treatClassCalleeAsType);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public ReflectionCaller setTreatClassCalleeAsType(boolean b)
   {
      _treatClassCalleeAsType = b;
      return this;
   }
}
