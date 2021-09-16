package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.gui.action.colorrows.ColorSelectionType;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserColorHandler
{
   private DataSetViewerTable _dataSetViewerTable;

   private HashMap<Integer, Color> _colorByRow = new HashMap<>();
   private HashMap<Point, Color> _colorByCell = new HashMap<>();

   /**
    * We don't want to create new Points in {@link #getBackground(int, int, boolean)}
    * as it gets called by UI rendering.
    */
   private Point _readBufferPoint = new Point();

   public UserColorHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
   }

   public Color getFirstColorInSelection()
   {
      for (int row : _dataSetViewerTable.getSelectedRows())
      {
         final int modelRow = _dataSetViewerTable.getSortableTableModel().transformToModelRow(row);

         if(null != _colorByRow.get(modelRow))
         {
            return _colorByRow.get(modelRow);
         }

         for (int col : _dataSetViewerTable.getSelectedColumns())
         {
            _readBufferPoint.x = col;
            _readBufferPoint.y = modelRow;

            if(null != _colorByCell.get(_readBufferPoint))
            {
               return _colorByCell.get(_readBufferPoint);
            }
         }
      }

      return null;
   }

   public void setColorForSelection(Color newColor, ColorSelectionType selectionType)
   {
      switch (selectionType)
      {
         case ROWS:
            setColorForSelectedRows(newColor);
            break;
         case CELLS:
            setColorForSelectedCells(newColor);
            break;
      }
   }


   public void setColorForSelectedRows(Color newColor)
   {
      for (int viewRow : _dataSetViewerTable.getSelectedRows())
      {
         int modelRow = _dataSetViewerTable.getSortableTableModel().transformToModelRow(viewRow);

         for (int col = 0; col < _dataSetViewerTable.getColumnCount(); col++)
         {
            // We remove the colors of all cells in the selected rows.
            _colorByCell.remove(new Point(col, modelRow));
         }

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

   public void setColorForSelectedCells(Color newColor)
   {
      for (int viewRow : _dataSetViewerTable.getSelectedRows())
      {
         int modelRow = _dataSetViewerTable.getSortableTableModel().transformToModelRow(viewRow);

         if (null == newColor)
         {
            // On null == newColor we remove colors for rows and cells
            _colorByRow.remove(modelRow);
         }

         for (Integer col : _dataSetViewerTable.getSelectedColumns())
         {
            if (null == newColor)
            {
               _colorByCell.remove(new Point(col, modelRow));
            }
            else
            {
               _colorByCell.put(new Point(col, modelRow), newColor);
            }
         }
      }

      _dataSetViewerTable.repaint();
   }

   public Color getBackground(int column, int viewRow, boolean isSelected)
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


      if(0 == _colorByRow.size() && 0 == _colorByCell.size())
      {
         // A little performance
         return backGround;
      }

      int modelRow = _dataSetViewerTable.getSortableTableModel().transformToModelRow(viewRow);

      _readBufferPoint.x = column;
      _readBufferPoint.y = modelRow;
      final Color cellColor = _colorByCell.get(_readBufferPoint);

      if(null != cellColor)
      {
         if (isSelected)
         {
            backGround = cellColor.darker();
         }
         else
         {
            backGround =  cellColor;
         }
      }
      else
      {
         final Color rowColor = _colorByRow.get(modelRow);
         if(null != rowColor)
         {
            if (isSelected)
            {
               backGround = rowColor.darker();
            }
            else
            {
               backGround = rowColor;
            }
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


   public HashMap<Integer, Color> getColorByRow()
   {
      return _colorByRow;
   }

   public HashMap<Point, Color> getColorByCell()
   {
      return _colorByCell;
   }

   public void setColorForRow(Integer row, Color color)
   {
      _colorByRow.put(row, color);
   }

   public Set<Integer> getAllColoredRows()
   {
      HashSet<Integer> ret = new HashSet<>();

      ret.addAll(_colorByRow.keySet());

      ret.addAll(_colorByCell.keySet().stream().map(p -> p.y).collect(Collectors.toSet()));

      return ret;
   }
}
