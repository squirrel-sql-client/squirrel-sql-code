package net.sourceforge.squirrel_sql.plugins.hibernate;

import java.util.ArrayList;

public class RCParam
{
   private ArrayList<Class> _types = new ArrayList<Class>();
   private ArrayList<Object> _values = new ArrayList<Object>();

   public RCParam()
   {
   }

   public RCParam(Object[] params)
   {
      for (int i = 0; i < params.length; i++)
      {
         _types.add(params[i].getClass());
         _values.add(params[i]);
      }
   }


   public RCParam add(Object paramValue, Class type)
   {
      _types.add(type);
      _values.add(paramValue);

      return this;
   }

   public int size()
   {
      return _values.size();
   }

   public Class getType(int i)
   {
      return _types.get(i);
   }

   public Object getValue(int i)
   {
      return _values.get(i);
   }
}
