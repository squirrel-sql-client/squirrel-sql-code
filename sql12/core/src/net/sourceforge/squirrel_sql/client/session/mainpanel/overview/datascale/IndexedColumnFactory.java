package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.sql.Types;
import java.util.List;

public class IndexedColumnFactory
{
   static IndexedColumn create(List<Object[]> rows, int colIx, ColumnDisplayDefinition colDef)
   {
      switch(colDef.getSqlType())
      {
         case Types.INTEGER:
         case Types.BIGINT:
         case Types.SMALLINT:
            return new IntegerIndexedColumn(rows, colIx,colDef);

         case Types.DOUBLE:
         case Types.DECIMAL:
         case Types.FLOAT:
         case Types.NUMERIC:
            return new DoubleIndexedColumn(rows, colIx,colDef);

         case Types.TIME:
         case Types.TIMESTAMP:
         case Types.DATE:
            return new TimestampIndexedColumn(rows, colIx,colDef);

         case Types.BIT:
         case Types.BOOLEAN:
            return new BooleanIndexedColumn(rows, colIx,colDef);

         default:
            return new StringIndexedColumn(rows, colIx,colDef);
      }
   }

   public static boolean isSumable(ColumnDisplayDefinition columnDisplayDefinition)
   {
      switch(columnDisplayDefinition.getSqlType())
      {
         case Types.INTEGER:
         case Types.BIGINT:
         case Types.SMALLINT:
         case Types.DOUBLE:
         case Types.DECIMAL:
         case Types.FLOAT:
         case Types.NUMERIC:
            return true;
         default:
            return false;
      }
   }

   public static boolean isOrderable(ColumnDisplayDefinition columnDisplayDefinition)
   {
      switch(columnDisplayDefinition.getSqlType())
      {
         case Types.INTEGER:
         case Types.BIGINT:
         case Types.SMALLINT:
         case Types.DOUBLE:
         case Types.DECIMAL:
         case Types.FLOAT:
         case Types.NUMERIC:
         case Types.TIME:
         case Types.TIMESTAMP:
         case Types.DATE:
         case Types.BIT:
         case Types.BOOLEAN:
            return true;
         default:
            return false;
      }
   }
}
