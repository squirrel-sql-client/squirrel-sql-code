package org.squirrelsql.session.graph;

import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.schemainfo.FullyQualifiedTableName;
import org.squirrelsql.table.TableLoader;

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

   public String getFkNameTo(TableInfo toPkTable, ColumnInfo columnInfo)
   {
      for (int i = 0; i < _impKeysAsTableLoader.size(); i++)
      {
         if(   columnInfo.getColName().equalsIgnoreCase(_impKeysAsTableLoader.getCellAsString("FKCOLUMN_NAME", i))
            && toPkTable.getName().equalsIgnoreCase(_impKeysAsTableLoader.getCellAsString("PKTABLE_NAME", i))  )
         {
            return _impKeysAsTableLoader.getCellAsString("FK_NAME", i);
         }
      }

      return null;
   }

   public boolean belongsToFk(String fkName, ColumnInfo columnInfo)
   {
      for (int i = 0; i < _impKeysAsTableLoader.size(); i++)
      {
         if(   columnInfo.getColName().equalsIgnoreCase(_impKeysAsTableLoader.getCellAsString("FKCOLUMN_NAME", i))
               && fkName.equalsIgnoreCase(_impKeysAsTableLoader.getCellAsString("FK_NAME", i))  )
         {
            return true;
         }
      }

      return false;
   }

   public FullyQualifiedTableName getPkTable(String fkName)
   {
      for (int i = 0; i < _impKeysAsTableLoader.size(); i++)
      {
         if( fkName.equalsIgnoreCase(_impKeysAsTableLoader.getCellAsString("FK_NAME", i)) )
         {
            String catalog = _impKeysAsTableLoader.getCellAsString("PKTABLE_CAT", i);
            String schema = _impKeysAsTableLoader.getCellAsString("PKTABLE_SCHEM", i);
            String table = _impKeysAsTableLoader.getCellAsString("PKTABLE_NAME", i);

            return new FullyQualifiedTableName(catalog, schema, table);
         }
      }
      return null;
   }
}
