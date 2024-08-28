package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;

public class PlainValue implements Serializable
{
   private PlainValueRepresentation _value;
   private HibernatePropertyInfo _hibernatePropertyInfo;

   public PlainValue(PlainValueRepresentation value, HibernatePropertyInfo hibernatePropertyInfo)
   {
      _value = value;
      _hibernatePropertyInfo = hibernatePropertyInfo;
   }

   public PlainValueRepresentation getValue()
   {
      return _value;
   }

   public HibernatePropertyInfo getHibernatePropertyInfo()
   {
      return _hibernatePropertyInfo;
   }
}
