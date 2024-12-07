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

   public static ArrayList<MeanAndColumn> calculateMeans(DataSetViewerTable table)
   {
      int[] selectedColumns = table.getSelectedColumns();

      int[] selectedRows = table.getSelectedRows();

      ArrayList<MeanAndColumn> means = new ArrayList<>();

      for (int selectedColumn : selectedColumns)
      {
         MeanAndColumn meanAndCol = createMeanAndColumn(table, selectedRows, selectedColumn);

         if (null != meanAndCol)
         {
            means.add(meanAndCol);
         }

      }
      return means;
   }

   public static ArrayList<DeviationAndColumn> calculateDeviations(DataSetViewerTable table)
   {
      int[] selectedColumns = table.getSelectedColumns();

      int[] selectedRows = table.getSelectedRows();

      ArrayList<DeviationAndColumn> deviations = new ArrayList<>();

      for (int selectedColumn : selectedColumns)
      {
         DeviationAndColumn meanAndCol = createDeviationAndColumn(table, selectedRows, selectedColumn);

         if (null != meanAndCol)
         {
            deviations.add(meanAndCol);
         }

      }
      return deviations;
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
            Object raw = table.getValueAt(selectedRow, selectedColumn);
            if (raw instanceof Number)
            {
               sum += ((Number)raw).longValue();
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

   private static MeanAndColumn createMeanAndColumn(DataSetViewerTable table, int[] selectedRows, int selectedColumn)
   {
      if(false == table.getColumnModel().getColumn(selectedColumn) instanceof ExtTableColumn)
      {
         return null;
      }

      ExtTableColumn column = (ExtTableColumn) table.getColumnModel().getColumn(selectedColumn);

      ColumnDisplayDefinition columnDisplayDefinition = column.getColumnDisplayDefinition();

      MeanAndColumn meanAndColumn = null;

      if(isIntegral(columnDisplayDefinition) || isReal(columnDisplayDefinition))
      {
         double sum = getSumAsDouble(table, selectedRows, selectedColumn);

         double mean;
         if(0 == selectedRows.length)
         {
            mean = 0;
         }
         else
         {
            mean = sum / (double) selectedRows.length;
         }

         meanAndColumn = new MeanAndColumn(columnDisplayDefinition, mean);
      }

      return meanAndColumn;
   }

   private static DeviationAndColumn createDeviationAndColumn(DataSetViewerTable table, int[] selectedRows, int selectedColumn)
   {
      if(false == table.getColumnModel().getColumn(selectedColumn) instanceof ExtTableColumn)
      {
         return null;
      }

      ExtTableColumn column = (ExtTableColumn) table.getColumnModel().getColumn(selectedColumn);

      ColumnDisplayDefinition columnDisplayDefinition = column.getColumnDisplayDefinition();

      DeviationAndColumn deviationAndColumn = null;

      if(isIntegral(columnDisplayDefinition) || isReal(columnDisplayDefinition))
      {
         double sum = getSumAsDouble(table, selectedRows, selectedColumn);
         double mean = sum / selectedRows.length;

         double deviationSquaredSum = 0;
         for (int selectedRow : selectedRows)
         {
            Number number = (Number) table.getValueAt(selectedRow, selectedColumn);
            if (null != number)
            {
               deviationSquaredSum += (mean - number.doubleValue()) * (mean - number.doubleValue());
            }
         }

         double deviation;
         if(2 > selectedRows.length)
         {
            deviation = 0;
         }
         else
         {
            deviation = Math.sqrt(deviationSquaredSum / (selectedRows.length - 1));
         }

         deviationAndColumn = new DeviationAndColumn(columnDisplayDefinition, deviation);
      }

      return deviationAndColumn;
   }

   private static double getSumAsDouble(DataSetViewerTable table, int[] selectedRows, int selectedColumn)
   {
      double sum = 0;
      for (int selectedRow : selectedRows )
      {
         Number number = (Number) table.getValueAt(selectedRow, selectedColumn);
         if (null != number)
         {
            sum += number.doubleValue();
         }
      }
      return sum;
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
