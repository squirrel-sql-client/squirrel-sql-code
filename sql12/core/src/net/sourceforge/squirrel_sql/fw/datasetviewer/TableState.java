package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.datasetviewer.rowcolor.RowColorHandlerState;
import net.sourceforge.squirrel_sql.fw.gui.ColumnOrder;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.HashMap;

public class TableState
{
   private boolean _applySorting;

   private int _sortedColumn;
   private ColumnOrder _columnOrder;
   private Rectangle _visibleRect;
   private HashMap<Integer, Integer> _columnWidthsByModelIndex = new HashMap<Integer, Integer>();
   private HashMap<Integer, Integer> _columnIndexByModelIndex = new HashMap<Integer, Integer>();
   private int _selectedRow;
   private RowColorHandlerState _rowColorHandlerState;

   private boolean _isShowingRowNumbers;

   public TableState(JTable table)
   {

      if(table.getModel() instanceof SortableTableModel)
      {
         _applySorting = true;
         _sortedColumn = ((SortableTableModel)table.getModel()).getSortedColumn();
         _columnOrder = ((SortableTableModel)table.getModel()).getColumnOrder();
      }


      _selectedRow = table.getSelectedRow();

      for (int i = 0; i < table.getColumnModel().getColumnCount(); i++)
      {
         TableColumn column = table.getColumnModel().getColumn(i);
         _columnWidthsByModelIndex.put(column.getModelIndex(), column.getWidth());
         _columnIndexByModelIndex.put(column.getModelIndex(), i);
      }
      _visibleRect = table.getVisibleRect();


      if(table instanceof DataSetViewerTable)
      {
         _rowColorHandlerState = ((DataSetViewerTable) table).getColoringService().getRowColorHandler().getState();
         _isShowingRowNumbers = ((DataSetViewerTable) table).isShowingRowNumbers();
      }
   }

   public void apply(final JTable table)
   {
      Runnable runnable = new Runnable()
      {
         public void run()
         {
            doApply(table);
         }
      };

      SwingUtilities.invokeLater(runnable);
   }

   private void doApply(JTable table)
   {
      for (int i = 0; i < table.getColumnModel().getColumnCount(); i++)
      {
         Integer width = _columnWidthsByModelIndex.get(table.getColumnModel().getColumn(i).getModelIndex());
         if (null != width)
         {
            table.getColumnModel().getColumn(i).setPreferredWidth(width);
         }
      }

      for (Integer modelIndex : _columnIndexByModelIndex.keySet())
      {
         moveTo(table.getColumnModel(), modelIndex, _columnIndexByModelIndex.get(modelIndex));
      }

      if (_applySorting && table.getModel() instanceof SortableTableModel)
      {
         if(-1 != _sortedColumn)
         {
            ((SortableTableModel)table.getModel()).sortByColumn(_sortedColumn, _columnOrder);
         }
      }
      
      if(-1 != _selectedRow)
      {
         table.getSelectionModel().setSelectionInterval(_selectedRow, _selectedRow);
      }

      if(table instanceof DataSetViewerTable)
      {
         ((DataSetViewerTable) table).getColoringService().getRowColorHandler().applyState(_rowColorHandlerState);

         if(_isShowingRowNumbers)
         {
            ((DataSetViewerTable) table).setShowRowNumbers(true);
         }

      }


      table.scrollRectToVisible(_visibleRect);
   }

   private void moveTo(TableColumnModel columnModel, int modelIndex, int columnIndex)
   {
      if(columnIndex >= columnModel.getColumnCount() || columnIndex < 0)
      {
         return;
      }

      for (int i = 0; i < columnModel.getColumnCount(); i++)
      {
         if (modelIndex == columnModel.getColumn(i).getModelIndex())
         {
            columnModel.moveColumn(i, columnIndex);
            break;
         }
      }
   }
}
