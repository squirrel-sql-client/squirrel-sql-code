package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanelUtil;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowNumberTableColumn;
import net.sourceforge.squirrel_sql.fw.gui.ButtonTableHeader;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;

import javax.swing.table.TableColumn;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class MarkDuplicatesHandler
{
   private DataSetViewerTable _dataSetViewerTable;
   private HashMap<Integer, TreeMap<Object, Color>> _duplicateValuesByColumnModelIndex;

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


         // Without this comparator TreeMap breaks on null keys
         Comparator treeMapKeyComparator = Comparator.nullsFirst(Comparator.naturalOrder());


         for (ExtTableColumn tableColumn : DataSetViewerTablePanelUtil.getTableColumns(_dataSetViewerTable))
         {
            int columnModelIndex = tableColumn.getModelIndex();

            TreeMap<Object, Color> colorByDuplicateValue = new TreeMap<>(treeMapKeyComparator);

            _duplicateValuesByColumnModelIndex.put(columnModelIndex, colorByDuplicateValue);

            HashSet<Object> buf = new HashSet<>();

            for (int j = 0; j < dataSetViewerTableModel.getRowCount(); ++j)
            {
               Object val = dataSetViewerTableModel.getValueAt(j, columnModelIndex);


               if(buf.contains(val))
               {
                  colorByDuplicateValue.put(val, null);
               }
               else
               {
                  buf.add(val);
               }
            }

            int count = 0;
            for (Object value : colorByDuplicateValue.keySet())
            {
               if(0 == ++count % 2)
               {
                  colorByDuplicateValue.put(value, SquirrelConstants.DUPLICATE_COLOR_DARKER);
               }
               else
               {
                  colorByDuplicateValue.put(value, SquirrelConstants.DUPLICATE_COLOR);
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


      return _duplicateValuesByColumnModelIndex.get(columnModelIndex).get(value);
   }


   public boolean isMarkDuplicates()
   {
      return null != _duplicateValuesByColumnModelIndex;
   }
}
