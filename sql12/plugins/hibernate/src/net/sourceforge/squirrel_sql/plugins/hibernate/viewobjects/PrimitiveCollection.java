package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

public class PrimitiveCollection
{
   private HibernatePropertyReader _hpr;

   private String _toString = "";

   public PrimitiveCollection(HibernatePropertyReader hpr)
   {
      _hpr = hpr;
      _toString = _hpr.getName() + " (" + _hpr.getTypeName() + ") =" + ViewObjectsUtil.getPrimitivePersistentCollectionString(hpr);
   }

   @Override
   public String toString()
   {
      return _toString;
   }
}
