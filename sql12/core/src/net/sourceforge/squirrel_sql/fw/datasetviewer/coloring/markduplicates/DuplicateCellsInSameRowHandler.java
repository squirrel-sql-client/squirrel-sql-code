package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanelUtil;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowNumberTableColumn;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;

import java.awt.Color;
import java.awt.Point;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class DuplicateCellsInSameRowHandler implements DuplicateHandler
{
   private DataSetViewerTable _dataSetViewerTable;

   private HashMap<Integer, TreeMap<Object, Color>> _duplicateValuesByRowIndex;

   private Point _cellBuf = new Point();

   public DuplicateCellsInSameRowHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
   }

   @Override
   public void markDuplicates(boolean selected)
   {
      if (false == selected)
      {
         _duplicateValuesByRowIndex = null;
      }

      _duplicateValuesByRowIndex = new HashMap<>();


      DataSetViewerTableModel dataSetViewerTableModel = _dataSetViewerTable.getDataSetViewerTableModel();

      // Without this comparator TreeMap breaks on null keys
      Comparator treeMapKeyComparator = Comparator.nullsFirst((o1, o2) -> compareRespectClassName((Comparable) o1, (Comparable)o2));

      for (int j = 0; j < dataSetViewerTableModel.getRowCount(); ++j)
      {
         HashSet<Object> buf = new HashSet<>();

         TreeMap<Object, Color> colorByDuplicateValue = new TreeMap<>(treeMapKeyComparator);

         _duplicateValuesByRowIndex.put(j, colorByDuplicateValue);

         for (ExtTableColumn tableColumn : DataSetViewerTablePanelUtil.getTableColumns(_dataSetViewerTable))
         {
            int columnModelIndex = tableColumn.getModelIndex();

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

   private int compareRespectClassName(Comparable o1, Comparable o2)
   {
      if(null == o1 && null != o2)
      {
         return 1;
      }
      else if(null != o1 && null == o2)
      {
         return -1;
      }
      else if (0 != o1.getClass().getName().compareTo(o2.getClass().getName()))
      {
         return o1.getClass().getName().compareTo(o2.getClass().getName());
      }
      else
      {
         return o1.compareTo(o2);
      }
   }

   @Override
   public MarkDuplicatesMode getMode()
   {
      return MarkDuplicatesMode.DUPLICATE_CELLS_IN_ROW;
   }

   @Override
   public Color getBackgroundForCell(int row, int column, Object value)
   {
      if(null == _duplicateValuesByRowIndex)
      {
         return null;
      }

      int columnModelIndex = _dataSetViewerTable.getColumnModel().getColumn(column).getModelIndex();

      if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == columnModelIndex)
      {
         return null;
      }

      return _duplicateValuesByRowIndex.get(row).get(value);
   }
}
