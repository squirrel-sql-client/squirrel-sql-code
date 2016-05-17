package org.squirrelsql.session.graph;

import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.table.TableLoader;

public class PrimaryKeyInfo
{
   private TableLoader _pkAsTableLoader;

   public PrimaryKeyInfo(TableLoader pkAsTableLoader)
   {

      _pkAsTableLoader = pkAsTableLoader;
   }

   public boolean belongsToPk(ColumnInfo columnInfo)
   {
      for (int i = 0; i < _pkAsTableLoader.size(); i++)
      {
         if(columnInfo.getColName().equalsIgnoreCase(_pkAsTableLoader.getCellAsString("COLUMN_NAME", i)))
         {
            return true;
         }
      }

      return false;
   }
}
