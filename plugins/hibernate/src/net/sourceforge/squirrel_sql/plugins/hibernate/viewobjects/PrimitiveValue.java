package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

public class PrimitiveValue
{
   private HibernatePropertyReader _hpr;
   private Object _value;

   private String _toString = "";

   public PrimitiveValue(HibernatePropertyReader hpr, Object value)
   {
      _hpr = hpr;
      _value = value;
      _toString += _hpr.getName() + "=" + getValue(_value) + "; Type:" + _hpr.getTypeName();
   }

   private Object getValue(Object value)
   {
      if(null == value)
      {
         return "<null>";
      }

      return value;
   }

   @Override
   public String toString()
   {
      return _toString;
   }
}
