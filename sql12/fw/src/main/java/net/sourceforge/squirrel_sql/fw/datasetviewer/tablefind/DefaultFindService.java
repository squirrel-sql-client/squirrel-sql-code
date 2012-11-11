package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import javax.swing.*;
import java.awt.*;

public class DefaultFindService implements FindService
{
   private JTable _table;

   public DefaultFindService(JTable table)
   {
      _table = table;
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
      Rectangle cellRect = _table.getCellRect(viewRow, viewCol, true);
      _table.scrollRectToVisible(cellRect);
      _table.repaint(cellRect);
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
   public void setFindServiceRenderCallBack(FindServiceRenderCallBack findServiceRenderCallBack)
   {

      for (int i = 0; i < _table.getColumnModel().getColumnCount(); i++)
      {
         _table.getColumnModel().getColumn(i).setCellRenderer(new FindServiceCellRendererDecorator(_table.getColumnModel().getColumn(i).getCellRenderer(), findServiceRenderCallBack));
      }
   }
}
