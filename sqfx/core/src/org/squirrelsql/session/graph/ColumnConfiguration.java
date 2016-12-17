package org.squirrelsql.session.graph;


public class ColumnConfiguration
{
   private AggregateFunctionData _aggregateFunctionData = new AggregateFunctionData();

   public AggregateFunctionData getAggregateFunctionData()
   {
      return _aggregateFunctionData;
   }

   public void setAggregateFunctionData(AggregateFunctionData aggregateFunctionData)
   {
      _aggregateFunctionData = aggregateFunctionData;
   }
}
