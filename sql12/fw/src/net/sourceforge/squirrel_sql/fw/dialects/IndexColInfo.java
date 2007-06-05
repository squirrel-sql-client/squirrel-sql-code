package net.sourceforge.squirrel_sql.fw.dialects;

import java.util.Comparator;


public class IndexColInfo
{
   private String _colName;
   private Integer _ordinalPosition = new Integer(0);

   public static final NameComparator NAME_COMPARATOR = new NameComparator();
   public static final OrdinalPositionComparator ORDINAL_POSITION_COMPARATOR = new OrdinalPositionComparator();

   public IndexColInfo(String colName)
   {
      _colName = colName;
   }

   public IndexColInfo(String colName, int ordinalPosition)
   {

      _colName = colName;
      _ordinalPosition = new Integer(ordinalPosition);
   }

   public boolean equals(Object obj)
   {
      return ((IndexColInfo)obj)._colName.equals(_colName);
   }

   public String toString()
   {
      return _colName;
   }

   public int compareTo(Object obj)
   {
      return _ordinalPosition.compareTo(((IndexColInfo)obj)._ordinalPosition);
   }

   static class NameComparator implements Comparator
   {
      public int compare(Object o1, Object o2)
      {
         return ((IndexColInfo)o1)._colName.compareTo(((IndexColInfo)o2)._colName);
      }
   }

   static class OrdinalPositionComparator implements Comparator
   {
      public int compare(Object o1, Object o2)
      {
         return ((IndexColInfo)o1)._ordinalPosition.compareTo(((IndexColInfo)o2)._ordinalPosition);
      }

   }


}
