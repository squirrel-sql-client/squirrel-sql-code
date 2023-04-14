package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;
import net.sourceforge.squirrel_sql.fw.gui.table.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.awt.Color;
import java.util.HashMap;
import java.util.TreeMap;

public class DuplicateConsecutiveRowsHandler implements DuplicateHandler
{
   private DataSetViewerTable _dataSetViewerTable;

   private HashMap<Integer, Color> _colorByViewRow;

   public DuplicateConsecutiveRowsHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
   }

   public void markDuplicates(boolean selected)
   {
      try
      {
         if (false == selected)
         {
            _colorByViewRow = null;
            return;
         }

         _colorByViewRow = new HashMap<>();

         DataSetViewerTableModel dataSetViewerTableModel = _dataSetViewerTable.getDataSetViewerTableModel();

         if (dataSetViewerTableModel.getRowCount() < 2)
         {
            return;
         }

         TreeMap<Integer, Integer> modelIndexByViewIndex = createModelIndexByViewIndexTreeMap(dataSetViewerTableModel);


         Color curColor = SquirrelConstants.DUPLICATE_COLOR;
         int lastColoredViewRow = -100;

         for (int viewIx = 1; viewIx < modelIndexByViewIndex.size(); ++viewIx)
         {
            RowWrapper formerRow = new RowWrapper(dataSetViewerTableModel, modelIndexByViewIndex.get(viewIx - 1));
            RowWrapper thisRow = new RowWrapper(dataSetViewerTableModel, modelIndexByViewIndex.get(viewIx));

            if (Utilities.equalsRespectNull(formerRow, thisRow))
            {
               _colorByViewRow.put(viewIx - 1, curColor);
               _colorByViewRow.put(viewIx, curColor);
               lastColoredViewRow = viewIx;
            }
            else if (lastColoredViewRow == viewIx - 1)
            {
               curColor = (SquirrelConstants.DUPLICATE_COLOR == curColor) ? SquirrelConstants.DUPLICATE_COLOR_DARKER : SquirrelConstants.DUPLICATE_COLOR;
            }
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
      return MarkDuplicatesMode.DUPLICATE_CONSECUTIVE_ROWS;
   }

   public Color getBackgroundForCell(int row, int column, Object value)
   {
      if (null == _colorByViewRow)
      {
         return null;
      }

      return _colorByViewRow.get(row);
   }
}
