package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;

public class SelectCol implements SortedColumn
{
   private String _qualifiedCol;

   private String _fullyQualifiedCol;

   /**
    * Needed for XML deserialization.
    */
   public SelectCol()
   {
   }

   public SelectCol(TableFrameController tfc, ColumnInfo columnInfo)
   {
      _qualifiedCol = tfc.getDisplayName() + "." + columnInfo.getColumnName();
      _fullyQualifiedCol = tfc.getTableInfo().getQualifiedName() + "." + columnInfo.getColumnName();
   }

   public String getQualifiedCol()
   {
      return _qualifiedCol;
   }


   //////////////////////////////////////////////////////
   // Needed for XML deserialization.
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
   /////////////////////////////////////////////////

   @Override
   public int hashCode()
   {
      return _fullyQualifiedCol.hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      if(false == obj instanceof SelectCol )
      {
         return false;
      }

      if (null != _fullyQualifiedCol && null != ((SelectCol)obj)._fullyQualifiedCol)
      {
         return _fullyQualifiedCol.equals(((SelectCol)obj)._fullyQualifiedCol);
      }
      else
      {
         return _qualifiedCol.equals(((SelectCol)obj)._qualifiedCol);
      }
   }
}
