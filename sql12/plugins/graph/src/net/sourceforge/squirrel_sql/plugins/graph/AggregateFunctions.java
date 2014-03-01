package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import static net.sourceforge.squirrel_sql.plugins.graph.GraphPluginResources.IKeys.*;

public enum AggregateFunctions
{
   NONE(0, "<NONE>",AGG_FCT,  AggFctI18n.s_stringMgr.getString("AggregateFunctions.undefined"), AggFctI18n.s_stringMgr.getString("AggregateFunctions.undefinedTT")),
   SUM(1, "SUM", AGG_SUM,  AggFctI18n.s_stringMgr.getString("AggregateFunctions.sum"), AggFctI18n.s_stringMgr.getString("AggregateFunctions.sumTT")),
   MAX(2, "MAX", AGG_MAX, AggFctI18n.s_stringMgr.getString("AggregateFunctions.max"), AggFctI18n.s_stringMgr.getString("AggregateFunctions.maxTT")),
   MIN(3, "MIN", AGG_MIN, AggFctI18n.s_stringMgr.getString("AggregateFunctions.min"), AggFctI18n.s_stringMgr.getString("AggregateFunctions.minTT")),
   COUNT(4, "COUNT", AGG_COUNT, AggFctI18n.s_stringMgr.getString("AggregateFunctions.count"), AggFctI18n.s_stringMgr.getString("AggregateFunctions.countTT"));

   public static final String CLIENT_PROP_NAME = AggregateFunctions.class.getName();

   private int _index;
   private String _image;
   private String _name;
   private String _toolTip;
   private String _sql;


   AggregateFunctions(int index, String sql, String image, String name, String toolTip)
   {
      _index = index;
      _sql = sql;
      _image = image;
      _name = name;
      _toolTip = toolTip;
   }


   public String getImage()
   {
      return _image;
   }

   @Override
   public String toString()
   {
      return _name;
   }


   public String getToolTip()
   {
      return _toolTip;
   }

   public int getIndex()
   {
      return _index;
   }

   public static AggregateFunctions getForIndex(int aggregateFunctionIndex)
   {
      for (AggregateFunctions aggregateFunction : values())
      {
         if(aggregateFunction._index == aggregateFunctionIndex)
         {
            return aggregateFunction;
         }
      }

      throw new IllegalArgumentException("Unknown aggregateFunctionIndex " + aggregateFunctionIndex);

   }

   public String getSQL()
   {
      return _sql;
   }
}

class AggFctI18n
{
   static final StringManager s_stringMgr =  StringManagerFactory.getStringManager(AggFctI18n.class);
}
