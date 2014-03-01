package net.sourceforge.squirrel_sql.fw.dialects;

import java.io.Serializable;
import java.util.Comparator;


public class IndexColInfo
{
   private String _colName;
   private Integer _ordinalPosition = Integer.valueOf(0);

   public static final NameComparator NAME_COMPARATOR = new NameComparator();
   public static final OrdinalPositionComparator ORDINAL_POSITION_COMPARATOR = 
       new OrdinalPositionComparator();

   public IndexColInfo(String colName)
   {
      _colName = colName;
   }

   public IndexColInfo(String colName, int ordinalPosition)
   {

      _colName = colName;
      _ordinalPosition = Integer.valueOf(ordinalPosition);
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
       final int PRIME = 31;
       int result = 1;
       result = PRIME * result + ((_colName == null) ? 0 : _colName.hashCode());
       result = PRIME * result + ((_ordinalPosition == null) ? 0 : _ordinalPosition.hashCode());
       return result;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
       if (this == obj)
           return true;
       if (obj == null)
           return false;
       if (getClass() != obj.getClass())
           return false;
       final IndexColInfo other = (IndexColInfo) obj;
       if (_colName == null) {
           if (other._colName != null)
               return false;
       } else if (!_colName.equals(other._colName))
           return false;
       if (_ordinalPosition == null) {
           if (other._ordinalPosition != null)
               return false;
       } else if (!_ordinalPosition.equals(other._ordinalPosition))
           return false;
       return true;
   }

   public String toString()
   {
       return _colName;
   }

   public int compareTo(Object obj)
   {
      return _ordinalPosition.compareTo(((IndexColInfo)obj)._ordinalPosition);
   }

   public static class NameComparator implements Comparator<IndexColInfo>, 
                                                 Serializable
   {
       private static final long serialVersionUID = 1L;

       public int compare(IndexColInfo o1, IndexColInfo o2)
       {
           return o1._colName.compareTo(o2._colName);
       }
   }

   public static class OrdinalPositionComparator implements Comparator<IndexColInfo>,
                                                            Serializable
   {
       private static final long serialVersionUID = 1L;

       public int compare(IndexColInfo o1, IndexColInfo o2)
       {
           return o1._ordinalPosition.compareTo(o2._ordinalPosition);
       }

   }


}
