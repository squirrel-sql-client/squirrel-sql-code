package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.util.ArrayList;

class RCParam
{
   private ArrayList<Class> _types = new ArrayList<Class>();
   private ArrayList<Object> _values = new ArrayList<Object>();

   RCParam()
   {
   }

   RCParam(Object[] params)
   {
      for (int i = 0; i < params.length; i++)
      {
         _types.add(params[i].getClass());
         _values.add(params[i]);
      }
   }


   RCParam add(Object paramValue, Class type)
   {
      _types.add(type);
      _values.add(paramValue);

      return this;
   }

   int size()
   {
      return _values.size();
   }

   Class getType(int i)
   {
      return _types.get(i);
   }

   Object getValue(int i)
   {
      return _values.get(i);
   }
}
