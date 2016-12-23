package org.squirrelsql.session.graph;

public class OrderByData
{
   private String _orderBy = OrderBy.NONE.name();

   public String getOrderBy()
   {
      return _orderBy;
   }

   public void setOrderBy(String orderBy)
   {
      _orderBy = orderBy;
   }
}
