package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;

import java.awt.Color;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeMap;

public class DuplicateRowsHandler implements DuplicateHandler
{
   private DataSetViewerTable _dataSetViewerTable;
   private TreeMap<RowWrapper, Color> _colorByDuplicateRow;

   public DuplicateRowsHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
   }

   public void markDuplicates(boolean selected)
   {
      try
      {

         if(false == selected)
         {
            _colorByDuplicateRow = null;
            return;
         }

         // Without this comparator TreeMap breaks on null keys
         Comparator treeMapKeyComparator = Comparator.nullsFirst(Comparator.naturalOrder());

         _colorByDuplicateRow = new TreeMap<>(treeMapKeyComparator);


         DataSetViewerTableModel dataSetViewerTableModel = _dataSetViewerTable.getDataSetViewerTableModel();

         HashSet<RowWrapper> buf = new HashSet<>();
         for (int j = 0; j < dataSetViewerTableModel.getRowCount(); ++j)
         {
            RowWrapper row = new RowWrapper(dataSetViewerTableModel, j);


            if(buf.contains(row))
            {
               _colorByDuplicateRow.put(row, null);
            }
            else
            {
               buf.add(row);
            }
         }

         int count = 0;
         for (RowWrapper row : _colorByDuplicateRow.keySet())
         {
            if(0 == ++count % 2)
            {
               _colorByDuplicateRow.put(row, SquirrelConstants.DUPLICATE_COLOR_DARKER);
            }
            else
            {
               _colorByDuplicateRow.put(row, SquirrelConstants.DUPLICATE_COLOR);
            }
         }
      }
      finally
      {
         _dataSetViewerTable.repaint();
      }
   }


   @Override
   public MarkDuplicatesMode getMode()
   {
      return MarkDuplicatesMode.DUPLICATE_ROWS;
   }

   public Color getBackgroundForCell(int row, int column, Object value)
   {
      if(null == _colorByDuplicateRow)
      {
         return null;
      }

      RowWrapper rowWrapperBuf = new RowWrapper();
      rowWrapperBuf.init(_dataSetViewerTable.getDataSetViewerTableModel(), row);

      return _colorByDuplicateRow.get(rowWrapperBuf);
   }
}
