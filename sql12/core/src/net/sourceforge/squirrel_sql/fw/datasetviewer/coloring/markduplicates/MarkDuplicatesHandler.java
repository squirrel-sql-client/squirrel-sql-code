package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowNumberTableColumn;

import javax.swing.JToggleButton;
import java.awt.Color;
import java.util.ArrayList;

public class MarkDuplicatesHandler
{
   private ArrayList<DuplicateHandler> _duplicateHandlers = new ArrayList<>();

   private DataSetViewerTable _dataSetViewerTable;


   private DuplicateHandler _currentDuplicateHandler;

   public MarkDuplicatesHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;

      _duplicateHandlers.add(new DuplicateValuesInColumnsHandler(_dataSetViewerTable));
      _duplicateHandlers.add(new DuplicateConsecutiveValuesInColumnsHandler(_dataSetViewerTable));
      _duplicateHandlers.add(new DuplicateRowsHandler(_dataSetViewerTable));
      _duplicateHandlers.add(new DuplicateConsecutiveRowsHandler(_dataSetViewerTable));
   }

   public void markDuplicates(MarkDuplicatesMode mode)
   {
      try
      {
         if(null == mode)
         {
            _duplicateHandlers.forEach(dh -> dh.markDuplicates(false));
            _currentDuplicateHandler = null;
            return;
         }

         for (DuplicateHandler duplicateHandler : _duplicateHandlers)
         {
            if(duplicateHandler.getMode() == mode)
            {
               _currentDuplicateHandler = duplicateHandler;
               _currentDuplicateHandler.markDuplicates(true);
            }
            else
            {
               duplicateHandler.markDuplicates(false);
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
      if(null == _currentDuplicateHandler)
      {
         return null;
      }


      int columnModelIndex = _dataSetViewerTable.getColumnModel().getColumn(column).getModelIndex();

      if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == columnModelIndex)
      {
         return null;
      }

      return _currentDuplicateHandler.getBackgroundForCell(row, column, value);
   }


   public MarkDuplicatesMode getMarkDuplicatesMode()
   {
      if(null == _currentDuplicateHandler)
      {
         return null;
      }

      return _currentDuplicateHandler.getMode();
   }
}
