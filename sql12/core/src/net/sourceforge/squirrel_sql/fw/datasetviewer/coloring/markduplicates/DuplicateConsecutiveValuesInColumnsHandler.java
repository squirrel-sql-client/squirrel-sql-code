package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.TreeMap;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanelUtil;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowNumberTableColumn;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class DuplicateConsecutiveValuesInColumnsHandler implements DuplicateHandler
{
   private DataSetViewerTable _dataSetViewerTable;

   private HashMap<Point, Color> _colorByCell;

   private Point _cellBuf = new Point();

   public DuplicateConsecutiveValuesInColumnsHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
   }

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

         DataSetViewerTableModel dataSetViewerTableModel = _dataSetViewerTable.getDataSetViewerTableModel();

         if (dataSetViewerTableModel.getRowCount() < 2)
         {
            return;
         }

         TreeMap<Integer, Integer> modelIndexByViewIndex = createModelIndexByViewIndexTreeMap(dataSetViewerTableModel);

         for( int i = 0; i < DataSetViewerTablePanelUtil.getTableColumns(_dataSetViewerTable).size(); i++ )
         {
            ExtTableColumn tableColumn = DataSetViewerTablePanelUtil.getTableColumns(_dataSetViewerTable).get(i);

            int columnModelIndex = tableColumn.getModelIndex();

            Color curColor = SquirrelConstants.DUPLICATE_COLOR;
            int lastColoredViewRow = -100;

            for (int viewIx = 1; viewIx < modelIndexByViewIndex.size(); ++viewIx)
            {
               Object formerVal = dataSetViewerTableModel.getValueAt(modelIndexByViewIndex.get(viewIx - 1), columnModelIndex);
               Object thisVal = dataSetViewerTableModel.getValueAt(modelIndexByViewIndex.get(viewIx), columnModelIndex);

               if (Utilities.equalsRespectNull(formerVal, thisVal))
               {
//                  _colorByCell.put(new Point(tableColumn.getModelIndex(), modelIndexByViewIndex.get(viewIx - 1)), curColor);
//                  _colorByCell.put(new Point(tableColumn.getModelIndex(), modelIndexByViewIndex.get(viewIx)), curColor);

                  _colorByCell.put(new Point(i, viewIx - 1), curColor);
                  _colorByCell.put(new Point(i, viewIx), curColor);
                  lastColoredViewRow = viewIx;
               }
               else if(lastColoredViewRow == viewIx-1)
               {
                  curColor = (SquirrelConstants.DUPLICATE_COLOR == curColor) ? SquirrelConstants.DUPLICATE_COLOR_DARKER : SquirrelConstants.DUPLICATE_COLOR;
               }
            }


//            for (int j = 1; j < dataSetViewerTableModel.getRowCount(); ++j)
//            {
//               Object formerVal = dataSetViewerTableModel.getValueAt(j - 1, columnModelIndex);
//               Object thisVal = dataSetViewerTableModel.getValueAt(j, columnModelIndex);
//
//               if (Utilities.equalsRespectNull(formerVal, thisVal))
//               {
//                  _colorByCell.put(new Point(tableColumn.getModelIndex(), j -1), curColor);
//                  _colorByCell.put(new Point(tableColumn.getModelIndex(), j), curColor);
//                  lastColoredRow = j;
//               }
//               else if(lastColoredRow == j-1)
//               {
//                  curColor = (SquirrelConstants.DUPLICATE_COLOR == curColor) ? SquirrelConstants.DUPLICATE_COLOR_DARKER : SquirrelConstants.DUPLICATE_COLOR;
//               }
//            }
         }
      }
      finally
      {
         _dataSetViewerTable.repaint();
      }
   }

   private TreeMap<Integer, Integer> createModelIndexByViewIndexTreeMap(DataSetViewerTableModel dataSetViewerTableModel)
   {
      TreeMap<Integer, Integer> modelIndexByViewIndex = new TreeMap<>();
      SortableTableModel sortableTableModel = _dataSetViewerTable.getSortableTableModel();

      for (int viewIx = 0; viewIx < dataSetViewerTableModel.getRowCount(); ++viewIx)
      {
         modelIndexByViewIndex.put(viewIx, sortableTableModel.transformToModelRow(viewIx));
      }
      return modelIndexByViewIndex;
   }

   @Override
   public MarkDuplicatesMode getMode()
   {
      return MarkDuplicatesMode.DUPLICATE_CONSECUTIVE_VALUES_IN_COLUMNS;
   }

   public Color getBackgroundForCell(int row, int column, Object value)
   {
      if (null == _colorByCell)
      {
         return null;
      }


      int columnModelIndex = _dataSetViewerTable.getColumnModel().getColumn(column).getModelIndex();

      if (RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == columnModelIndex)
      {
         return null;
      }

      _cellBuf.x = column;
      _cellBuf.y = row;

      return _colorByCell.get(_cellBuf);
   }
}
