package org.squirrelsql.session.graph;

public class AggregateFunctionData
{
   private boolean _inSelect;
   private AggregateFunction _aggregateFunction = AggregateFunction.NONE;

   public boolean isInSelect()
   {
      return _inSelect;
   }

   public void setInSelect(boolean inSelect)
   {
      _inSelect = inSelect;
   }

   public void setAggregateFunction(AggregateFunction aggregateFunction)
   {
      _aggregateFunction = aggregateFunction;
   }

   public AggregateFunction getAggregateFunction()
   {
      return _aggregateFunction;
   }
}
