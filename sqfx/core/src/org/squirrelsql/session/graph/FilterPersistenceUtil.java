package org.squirrelsql.session.graph;

public class FilterPersistenceUtil
{
   public static boolean isEmpty(FilterPersistence filterPersistence)
   {
      return null == filterPersistence.getFilter() && Operator.valueOf(filterPersistence.getOperatorAsString()).requiresValue();
   }

}
