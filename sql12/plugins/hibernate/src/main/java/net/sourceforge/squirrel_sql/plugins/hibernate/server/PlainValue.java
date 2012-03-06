package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;

public class PlainValue implements Serializable
{
   private Object _value;
   private HibernatePropertyInfo _hibernatePropertyInfo;

   public PlainValue(Object value, HibernatePropertyInfo hibernatePropertyInfo)
   {
      _value = value;
      _hibernatePropertyInfo = hibernatePropertyInfo;
   }

   public Object getValue()
   {
      return _value;
   }

   public HibernatePropertyInfo getHibernatePropertyInfo()
   {
      return _hibernatePropertyInfo;
   }
}
