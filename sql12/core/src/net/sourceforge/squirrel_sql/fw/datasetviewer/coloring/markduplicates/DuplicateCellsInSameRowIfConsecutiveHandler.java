package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanelUtil;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.gui.table.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;

public class DuplicateCellsInSameRowIfConsecutiveHandler implements DuplicateHandler
{
   private DataSetViewerTable _dataSetViewerTable;

   private HashMap<Point, Color> _colorByCell;

   private Point _cellBuf = new Point();


   public DuplicateCellsInSameRowIfConsecutiveHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
      _dataSetViewerTable.getSortableTableModel().addSortingListener((modelColumnIx, columnOrder) -> onSorted());
   }

   private void onSorted()
   {
      if(null != _colorByCell)
      {
         markDuplicates(true);
      }
   }


   @Override
   public void markDuplicates(boolean selected)
   {
      try
      {
         if (false == selected)
         {
            _colorByCell = null;
            return;
         }

         _colorByCell = new HashMap<>();

         SortableTableModel dataSetViewerTableModel = _dataSetViewerTable.getSortableTableModel();

         for (int j = 0; j < dataSetViewerTableModel.getRowCount(); ++j)
         {
            Color curColor = SquirrelConstants.DUPLICATE_COLOR;
            boolean lastWasColored = false;

            List<ExtTableColumn> tableColumns = DataSetViewerTablePanelUtil.getTableColumns(_dataSetViewerTable);
            for (int i = 1; i < tableColumns.size(); i++)
            {
               int columnModelIndex = tableColumns.get(i).getModelIndex();
               int previousColumnModelIndex = tableColumns.get(i - 1).getModelIndex();

               Object val = dataSetViewerTableModel.getValueAt(j, columnModelIndex);
               Object previousVal = dataSetViewerTableModel.getValueAt(j, previousColumnModelIndex);

               if (Utilities.equalsRespectNull(val, previousVal))
               {
                  _colorByCell.put(new Point(columnModelIndex, j), curColor);
                  _colorByCell.put(new Point(previousColumnModelIndex, j), curColor);
                  lastWasColored = true;
               }
               else if (lastWasColored)
               {
                  if (curColor == SquirrelConstants.DUPLICATE_COLOR)
                  {
                     curColor = SquirrelConstants.DUPLICATE_COLOR_DARKER;
                  }
                  else
                  {
                     curColor = SquirrelConstants.DUPLICATE_COLOR;
                  }

                  lastWasColored = false;
               }
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
      return MarkDuplicatesMode.DUPLICATE_CONSECUTIVE_CELLS_IN_ROW;
   }

   @Override
   public Color getBackgroundForCell(int row, int column, Object value)
   {
      if(null == _colorByCell)
      {
         return null;
      }

      int columnModelIndex = _dataSetViewerTable.getColumnModel().getColumn(column).getModelIndex();

      _cellBuf.x = columnModelIndex;
      _cellBuf.y = row;

      return _colorByCell.get(_cellBuf);
   }
}
