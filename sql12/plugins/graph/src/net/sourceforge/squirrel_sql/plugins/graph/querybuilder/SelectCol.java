package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;

public class SelectCol implements SortedColumn
{
   private String _qualifiedCol;


   /**
    * Needed for XML deserialization.
    */
   public SelectCol()
   {
   }

   public SelectCol(String simpleTableName, ColumnInfo columnInfo)
   {
      _qualifiedCol = simpleTableName + "." + columnInfo.getColumnName();

   }

   public String getQualifiedCol()
   {
      return _qualifiedCol;
   }


   /**
    * Needed for XML deserialization.
    */
   public void setQualifiedCol(String qualifiedCol)
   {
      _qualifiedCol = qualifiedCol;
   }


   @Override
   public int hashCode()
   {
      return _qualifiedCol.hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      if(false == obj instanceof SelectCol )
      {
         return false;
      }

      return _qualifiedCol.equals(((SelectCol)obj)._qualifiedCol);
   }
}
