package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;

public class MarkDuplicatesHandler
{
   private DataSetViewerTable _dataSetViewerTable;
   private HashMap<Integer, HashSet<Object>> _duplicateValuesByColumnModelIndex;

   public MarkDuplicatesHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
   }

   public void markDuplicates(boolean selected)
   {
      try
      {

         if(false == selected)
         {
            _duplicateValuesByColumnModelIndex = null;
            return;
         }

         _duplicateValuesByColumnModelIndex = new HashMap<>();


         DataSetViewerTableModel dataSetViewerTableModel = _dataSetViewerTable.getDataSetViewerTableModel();
         for (int i = 0; i < dataSetViewerTableModel.getColumnCount(); ++i)
         {
            int columnModelIndex = _dataSetViewerTable.getColumnModel().getColumn(i).getModelIndex();

            HashSet<Object> duplicateValues = new HashSet<>();
            _duplicateValuesByColumnModelIndex.put(columnModelIndex, duplicateValues);

            HashSet<Object> buf = new HashSet<>();

            for (int j = 0; j < dataSetViewerTableModel.getRowCount(); ++j)
            {
               Object val = dataSetViewerTableModel.getValueAt(j, i);


               if(buf.contains(val))
               {
                  duplicateValues.add(val);
               }
               else
               {
                  buf.add(val);
               }
            }
         }
      }
      finally
      {
         _dataSetViewerTable.repaint();
      }
   }

   public Color getBackgroundForCell(int row, int column, Object value)
   {
      if(null == _duplicateValuesByColumnModelIndex)
      {
         return null;
      }


      int columnModelIndex = _dataSetViewerTable.getColumnModel().getColumn(column).getModelIndex();

      if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == columnModelIndex)
      {
         return null;
      }


      if(_duplicateValuesByColumnModelIndex.get(columnModelIndex).contains(value))
      {
         return SquirrelConstants.DUPLICATE_COLOR;
      }

      return null;
   }


   public boolean isMarkDuplicates()
   {
      return null != _duplicateValuesByColumnModelIndex;
   }
}
