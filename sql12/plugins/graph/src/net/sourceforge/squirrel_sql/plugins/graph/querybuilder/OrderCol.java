package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.plugins.graph.AggregateFunctions;
import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;

public class OrderCol implements SortedColumn
{
   private String _qualifiedCol;

   private String _fullyQualifiedCol;

   private boolean _descending;
   private boolean _aggregated;


   /**
    * Needed for XML deserialization.
    */
   public OrderCol()
   {
   }

   public OrderCol(TableFrameController tfc, ColumnInfo columnInfo)
   {
      _qualifiedCol = tfc.getDisplayName() + "." + columnInfo.getColumnName();
      _fullyQualifiedCol = tfc.getTableInfo().getQualifiedName() + "." + columnInfo.getColumnName();

      _descending = false == columnInfo.getQueryData().isSortedAsc();
      _aggregated = (AggregateFunctions.NONE != columnInfo.getQueryData().getAggregateFunction());

   }

   public String getQualifiedCol()
   {
      return _qualifiedCol;
   }


   //////////////////////////////////////////////////
   //Needed for XML deserialization.
   public void setQualifiedCol(String qualifiedCol)
   {
      _qualifiedCol = qualifiedCol;
   }

   public String getFullyQualifiedCol()
   {
      return _fullyQualifiedCol;
   }

   public void setFullyQualifiedCol(String fullyQualifiedCol)
   {
      _fullyQualifiedCol = fullyQualifiedCol;
   }
   //
   //////////////////////////////////////////////////

   public boolean isDescending()
   {
      return _descending;
   }

   public void setDescending(boolean b)
   {
      _descending = b;
   }

   public boolean isAggregated()
   {
      return _aggregated;
   }

   public void setAggregated(boolean aggregated)
   {
      _aggregated = aggregated;
   }

   @Override
   public int hashCode()
   {
      return _qualifiedCol.hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      if(false == obj instanceof OrderCol )
      {
         return false;
      }

      //return _qualifiedCol.equals(((OrderCol)obj)._qualifiedCol);

      if (null != _fullyQualifiedCol && null != ((OrderCol)obj)._fullyQualifiedCol)
      {
         return _fullyQualifiedCol.equals(((OrderCol)obj)._fullyQualifiedCol);
      }
      else
      {
         return _qualifiedCol.equals(((OrderCol)obj)._qualifiedCol);
      }

   }
}
