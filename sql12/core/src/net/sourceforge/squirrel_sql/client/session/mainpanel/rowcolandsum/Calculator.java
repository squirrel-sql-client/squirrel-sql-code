package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;

import java.sql.Types;
import java.util.ArrayList;

public class Calculator
{
   static ArrayList<SumAndColumn> calculateSums(DataSetViewerTable table)
   {
      int[] selectedColumns = table.getSelectedColumns();

      int[] selectedRows = table.getSelectedRows();


      ArrayList<SumAndColumn> sums = new ArrayList<>();

      for (int selectedColumn : selectedColumns)
      {
         SumAndColumn sumAndCol = createSumAndColumn(table, selectedRows, selectedColumn);

         if (null != sumAndCol)
         {
            sums.add(sumAndCol);
         }

      }
      return sums;
   }

   public static SumAndColumn calculateFirstSum(DataSetViewerTable table)
   {
      int selectedColumn = table.getSelectedColumn();

      if(-1 == selectedColumn)
      {
         return null;
      }

      int[] selectedRows = table.getSelectedRows();

      return createSumAndColumn(table, selectedRows, selectedColumn);
   }



   private static SumAndColumn createSumAndColumn(DataSetViewerTable table, int[] selectedRows, int selectedColumn)
   {
      if(false == table.getColumnModel().getColumn(selectedColumn) instanceof ExtTableColumn)
      {
         return null;
      }

      ExtTableColumn column = (ExtTableColumn) table.getColumnModel().getColumn(selectedColumn);

      ColumnDisplayDefinition columnDisplayDefinition = column.getColumnDisplayDefinition();

      SumAndColumn sumAndCol = null;

      if(isIntegral(columnDisplayDefinition))
      {
         long sum = 0;
         for (int selectedRow : selectedRows)
         {
            Number number = (Number) table.getValueAt(selectedRow, selectedColumn);
            if (null != number)
            {
               sum += number.longValue();
            }
         }

         sumAndCol = new SumAndColumn(columnDisplayDefinition, sum);
      }
      else if(isReal(columnDisplayDefinition))
      {
         double sum = 0;
         for (int selectedRow : selectedRows)
         {
            Number number = (Number) table.getValueAt(selectedRow, selectedColumn);
            if (null != number)
            {
               sum += number.doubleValue();
            }
         }

         sumAndCol = new SumAndColumn(columnDisplayDefinition, sum);
      }
      return sumAndCol;
   }

   /**
    * See {@link ResultSetReader#doRead()} and {@link ResultSetReader#doContentTabRead()}.
    */
   private static boolean isIntegral(ColumnDisplayDefinition columnDisplayDefinition)
   {
      switch (columnDisplayDefinition.getSqlType())
      {
         case Types.BIGINT:
         case Types.INTEGER:
         case Types.SMALLINT:
         case Types.TINYINT:
            return true;
         default:
            return false;
      }
   }

   private static boolean isReal(ColumnDisplayDefinition columnDisplayDefinition)
   {
      switch (columnDisplayDefinition.getSqlType())
      {
         case Types.DOUBLE:
         case Types.FLOAT:
         case Types.REAL:
         case Types.DECIMAL:
         case Types.NUMERIC:
            return true;
         default:
            return false;
      }
   }
}
