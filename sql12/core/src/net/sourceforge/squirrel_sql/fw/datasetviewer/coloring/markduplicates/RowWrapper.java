package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;
import net.sourceforge.squirrel_sql.fw.gui.SquirrelTableCellValueCollator;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class RowWrapper implements Comparable<RowWrapper>
{
   private DataSetViewerTableModel _dataSetViewerTableModel;
   private int _modelRow;

   private SquirrelTableCellValueCollator _collator = new SquirrelTableCellValueCollator();

   public RowWrapper(DataSetViewerTableModel dataSetViewerTableModel, int modelRow)
   {
      init(dataSetViewerTableModel, modelRow);
   }

   /**
    * Only to be able to use an object of this class as a buffer
    */
   public RowWrapper()
   {
   }

   public void init(DataSetViewerTableModel dataSetViewerTableModel, int rowIndex)
   {
      _dataSetViewerTableModel = dataSetViewerTableModel;
      _modelRow = rowIndex;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      RowWrapper other = (RowWrapper) o;

      for (int i = 0; i < _dataSetViewerTableModel.getColumnCount(); i++)
      {
         if(false == Utilities.equalsRespectNull(getCellValueAtColumn(i), other.getCellValueAtColumn(i)))
         {
            return false;
         }
      }

      return true;
   }

   private Object getCellValueAtColumn(int col)
   {
      return _dataSetViewerTableModel.getValueAt(_modelRow, col);
   }

   @Override
   public int hashCode()
   {
      int result = 0;

      for (int i = 0; i < _dataSetViewerTableModel.getColumnCount(); i++)
      {
         int buf = (getCellValueAtColumn(i) != null ? getCellValueAtColumn(i).hashCode() : 0);
         result = 31 * buf + result;
      }

      return result;
   }

   @Override
   public int compareTo(RowWrapper other)
   {
      for (int i = 0; i < _dataSetViewerTableModel.getColumnCount(); i++)
      {
         int compareResult = _collator.compareTableCellValues(getCellValueAtColumn(i), other.getCellValueAtColumn(i), 1, false);
         if(0 != compareResult)
         {
            return compareResult;
         }
      }

      return 0;
   }

//   public static void main(String[] args)
//   {
//      int v = 4;
//      v+= Integer.MAX_VALUE;
//
//      System.out.println("v = " + v);
//   }
}
