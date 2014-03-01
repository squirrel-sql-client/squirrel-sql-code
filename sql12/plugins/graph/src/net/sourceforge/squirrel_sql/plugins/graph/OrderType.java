package net.sourceforge.squirrel_sql.plugins.graph;

import java.util.Comparator;

enum OrderType
{
   ORDER_DB(0),
   ORDER_NAME(1),
   ORDER_PK_CONSTRAINT(2),
   ORDER_FILTERED_SELECTED(3);
   private int _ix;

   OrderType(int ix)
   {
      _ix = ix;
   }

   public int getIx()
   {
      return _ix;
   }

   static OrderType getByIx(int ix)
   {
      for (OrderType orderType : values())
      {
         if(orderType._ix == ix)
         {
            return orderType;
         }
      }
      throw new IllegalArgumentException("Unknown order typ " + ix);
   }

   Comparator<ColumnInfo> getComparator()
   {
      if(ORDER_NAME == this)
      {
         return new Comparator<ColumnInfo>()
         {
            @Override
            public int compare(ColumnInfo c1, ColumnInfo c2)
            {
               return orderNameCompare(c1, c2);
            }
         };
      }
      else if(ORDER_PK_CONSTRAINT == this)
      {
         return new Comparator<ColumnInfo>()
         {
            @Override
            public int compare(ColumnInfo c1, ColumnInfo c2)
            {
               return orderPkConstraintCompare(c1, c2);
            }
         };
      }
      else if(ORDER_FILTERED_SELECTED == this)
      {
         return new Comparator<ColumnInfo>()
         {
            @Override
            public int compare(ColumnInfo c1, ColumnInfo c2)
            {
               return orderFilteredSelectedCompare(c1, c2);
            }
         };
      }
      else
      {
         throw new IllegalArgumentException("No comparator avaialble for " + this);
      }


   }

   private int orderFilteredSelectedCompare(ColumnInfo c1, ColumnInfo c2)
   {
      if(c1.getQueryData().isFiltered() && false == c2.getQueryData().isFiltered())
      {
         return -1;
      }
      else if(false == c1.getQueryData().isFiltered() && c2.getQueryData().isFiltered())
      {
         return 1;
      }
      else
      {
         if(c1.getQueryData().isInSelectClause() && false == c2.getQueryData().isInSelectClause())
         {
            return -1;
         }
         else if(false == c1.getQueryData().isInSelectClause() && c2.getQueryData().isInSelectClause())
         {
            return 1;
         }
         else
         {
            if(c1.getQueryData().isSorted() && false == c2.getQueryData().isSorted())
            {
               return -1;
            }
            else if(false == c1.getQueryData().isSorted() && c2.getQueryData().isSorted())
            {
               return 1;
            }
            else
            {
               return c1.getName().compareTo(c2.getName());
            }
         }
      }
   }

   private int orderPkConstraintCompare(ColumnInfo c1, ColumnInfo c2)
   {
      if(c1.isPrimaryKey() && false == c2.isPrimaryKey())
      {
         return -1;
      }
      else if(false == c1.isPrimaryKey() && c2.isPrimaryKey())
      {
         return 1;
      }
      else
      {
         if(null != c1.getDBConstraintName() && null == c2.getDBConstraintName())
         {
            return -1;
         }
         else if(null == c1.getDBConstraintName() && null != c2.getDBConstraintName())
         {
            return 1;
         }
         else
         {
            if(null != c1.getDBConstraintName() && null != c2.getDBConstraintName())
            {
               String s1 = c1.getDBConstraintName() + "_" + c1.getName();
               String s2 = c2.getDBConstraintName() + "_" + c2.getName();
               return s1.compareTo(s2);
            }
            else
            {
               return c1.getName().compareTo(c2.getName());
            }
         }
      }
   }

   private int orderNameCompare(ColumnInfo c1, ColumnInfo c2)
   {
      return c1.getName().compareTo(c2.getName());
   }

}
