package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.OrderCol;

public class OrderStructureXmlBean
{
   private OrderCol[] _orderCols = new OrderCol[0];

   /**
    * Constructor for XML deserialization
    */
   public OrderStructureXmlBean()
   {
   }

   public OrderStructureXmlBean(OrderCol[] orderCols)
   {
      _orderCols = orderCols;
   }

   public OrderCol[] getOrderCols()
   {
      return _orderCols;
   }

   public void setOrderCols(OrderCol[] orderCols)
   {
      _orderCols = orderCols;
   }
}
