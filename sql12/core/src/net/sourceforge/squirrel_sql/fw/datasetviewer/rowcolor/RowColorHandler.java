package net.sourceforge.squirrel_sql.fw.datasetviewer.rowcolor;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.awt.Color;
import java.util.HashMap;

public class RowColorHandler
{
   private DataSetViewerTable _dataSetViewerTable;

   private HashMap<Integer, Color> _colorByRow = new HashMap<>();

   public RowColorHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
   }

   public Color getFirstColorInSelection()
   {
      for (int row : _dataSetViewerTable.getSelectedRows())
      {
         if(null != _colorByRow.get(row))
         {
            return _colorByRow.get(row);
         }
      }

      return null;
   }

   public void setColorForSelectedRows(Color newColor)
   {
      for (int viewRow : _dataSetViewerTable.getSelectedRows())
      {
         int modelRow = _dataSetViewerTable.getSortableTableModel().transformToModelRow(viewRow);

         if (null == newColor)
         {
            _colorByRow.remove(modelRow);
         }
         else
         {
            _colorByRow.put(modelRow, newColor);
         }
      }

      _dataSetViewerTable.repaint();
   }

   public Color getBackgroundForRow(int viewRow, boolean isSelected)
   {
      Color backGround;

      if (isSelected)
      {
         backGround = _dataSetViewerTable.getSelectionBackground();
      }
      else
      {
         backGround =  _dataSetViewerTable.getBackground();
      }


      if(0 == _colorByRow.size())
      {
         // A little performance
         return backGround;
      }

      int modelRow = _dataSetViewerTable.getSortableTableModel().transformToModelRow(viewRow);

      if(null != _colorByRow.get(modelRow))
      {
         if (isSelected)
         {
            backGround = _colorByRow.get(modelRow).darker();
         }
         else
         {
            backGround =  _colorByRow.get(modelRow);
         }
      }

      return backGround;
   }

   public RowColorHandlerState getState()
   {
      return new RowColorHandlerState(Utilities.cloneObject(_colorByRow, _colorByRow.getClass().getClassLoader()));
   }

   public void applyState(RowColorHandlerState rowColorHandlerState)
   {
      _colorByRow = rowColorHandlerState.getColorByRow();
   }
}
