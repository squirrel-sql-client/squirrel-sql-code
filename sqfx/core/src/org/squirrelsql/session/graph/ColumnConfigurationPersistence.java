package org.squirrelsql.session.graph;


public class ColumnConfigurationPersistence
{
   private AggregateFunctionPersistence _aggregateFunctionPersistence = new AggregateFunctionPersistence();
   private FilterPersistence _filterPersistence = new FilterPersistence();
   private OrderByPersistence _orderByPersistence = new OrderByPersistence();

   public AggregateFunctionPersistence getAggregateFunctionPersistence()
   {
      return _aggregateFunctionPersistence;
   }

   public void setAggregateFunctionPersistence(AggregateFunctionPersistence aggregateFunctionPersistence)
   {
      _aggregateFunctionPersistence = aggregateFunctionPersistence;
   }


   public FilterPersistence getFilterPersistence()
   {
      return _filterPersistence;
   }

   public void setFilterPersistence(FilterPersistence filterPersistence)
   {
      _filterPersistence = filterPersistence;
   }

   public OrderByPersistence getOrderByPersistence()
   {
      return _orderByPersistence;
   }

   public void setOrderByPersistence(OrderByPersistence orderByPersistence)
   {
      _orderByPersistence = orderByPersistence;
   }
}
