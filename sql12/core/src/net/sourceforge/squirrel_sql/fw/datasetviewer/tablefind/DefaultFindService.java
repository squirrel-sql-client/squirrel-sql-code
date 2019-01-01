package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.gui.ColumnOrder;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.gui.SortingListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class DefaultFindService implements FindService
{
   private DataSetViewerTable _table;
   private ColumnDisplayDefinition[] _columnDefinitions;

   public DefaultFindService(DataSetViewerTable table, ColumnDisplayDefinition[] columnDefinitions)
   {
      _table = table;
      _columnDefinitions = columnDefinitions;
   }

   @Override
   public int getRowCount()
   {
      return _table.getRowCount();
   }

   @Override
   public int getColCount()
   {
      return _table.getColumnCount();
   }

   @Override
   public String getViewDataAsString(int row, int col)
   {
      Object value = _table.getValueAt(row, col);
      if (value instanceof String)
      {
         return (String) value;
      }
      else
      {
         return "" + value;
      }
   }

   @Override
   public void scrollToVisible(int viewRow, int viewCol)
   {
      _table.scrollToVisible(viewRow, viewCol);
   }

   @Override
   public void repaintCell(int viewRow, int viewCol)
   {
      Rectangle cellRect = _table.getCellRect(viewRow, viewCol, true);
      _table.repaint(cellRect);
   }

   @Override
   public void repaintAll()
   {
      _table.repaint();
   }

   @Override
   public ColumnDisplayDefinition[] getColumnDisplayDefinitions()
   {
      return _columnDefinitions;
   }

   @Override
   public List<Object[]> getRowsForIndexes(ArrayList<Integer> rowIndexes)
   {
      ArrayList<Object[]> ret = new ArrayList<Object[]>();

      for (Integer row : rowIndexes)
      {
         ret.add(_table.getDataSetViewerTableModel().getRowAt(row));
      }
      return ret;
   }

   @Override
   public Dimension getVisibleSize()
   {
      return _table.getVisibleRect().getSize();
   }

   @Override
   public void setFindServiceCallBack(final FindServiceCallBack findServiceCallBack)
   {
      _table.getColoringService().getFindColorHandler().setFindServiceCallBack(findServiceCallBack);

      if (_table.getModel() instanceof SortableTableModel)
      {
         ((SortableTableModel)_table.getModel()).addSortingListener(new SortingListener()
         {
            @Override
            public void sortingDone(int modelColumnIx, ColumnOrder columnOrder)
            {
               findServiceCallBack.tableCellStructureChanged();
            }
         });
      }

      _table.getColumnModel().addColumnModelListener(new TableColumnModelListener()
      {
         @Override
         public void columnAdded(TableColumnModelEvent e){}

         @Override
         public void columnRemoved(TableColumnModelEvent e) {}

         @Override
         public void columnMoved(TableColumnModelEvent e)
         {
            findServiceCallBack.tableCellStructureChanged();
         }

         @Override
         public void columnMarginChanged(ChangeEvent e) {}

         @Override
         public void columnSelectionChanged(ListSelectionEvent e) {}
      });
   }
}
