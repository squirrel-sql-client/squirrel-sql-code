package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class PrimitiveValue
{
   private HibernatePropertyReader _hpr;
   private Object _value;

   //private String _toString = "";

   public PrimitiveValue(HibernatePropertyReader hpr)
   {
      _hpr = hpr;
      _value = hpr.getValue();
      //_toString += _hpr.getName() + "=" + getValue(_value) + "; Type:" + _hpr.getTypeName();
   }

   private Object getValue(Object value)
   {
      if(null == value)
      {
         return StringUtilities.NULL_AS_STRING;
      }

      return value;
   }

   @Override
   public String toString()
   {
      return _hpr.getName() + "=" + getValue(_value) + "; Type:" + _hpr.getTypeName();
   }
}
