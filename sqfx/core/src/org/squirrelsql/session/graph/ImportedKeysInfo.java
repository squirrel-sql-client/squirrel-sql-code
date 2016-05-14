package org.squirrelsql.session.graph;

import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.table.TableLoader;

import java.sql.Connection;

public class ImportedKeysInfo
{
   private TableLoader _impKeysAsTableLoader;

   public ImportedKeysInfo(TableLoader impKeysAsTableLoader)
   {
      _impKeysAsTableLoader = impKeysAsTableLoader;
   }

   public boolean isFk(ColumnInfo columnInfo)
   {
      for (int i = 0; i < _impKeysAsTableLoader.size(); i++)
      {
         if(columnInfo.getColName().equalsIgnoreCase(_impKeysAsTableLoader.getCellAsString("FKCOLUMN_NAME", i)))
         {
            return true;
         }
      }

      return false;
   }
}
