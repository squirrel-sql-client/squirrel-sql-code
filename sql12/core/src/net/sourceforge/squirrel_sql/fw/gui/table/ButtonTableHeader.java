package net.sourceforge.squirrel_sql.fw.gui.table;
/*
 * Copyright (C) 2002 Johan Compagner
 * jcompagner@j-com.nl
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowNumberTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.SquirrelTableCellRenderer;
import net.sourceforge.squirrel_sql.fw.gui.ColumnOrder;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

public class ButtonTableHeader extends JTableHeader
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ButtonTableHeader.class);

   private static final String PREF_KEY_ALWAYS_ADJUST_ALL_COLUMN_HEADERS = "Squirrel.alwaysAdoptAllColumnHeaders";


   /** Listens for changes in the underlying data. */
   private TableModelListener _dataListener;

   private SortingListener _sortingListener;

   private TableHeaderMouseState _mouseState = new TableHeaderMouseState();

   private TableAccessForHeader _tableAccess;

   private ButtonTableHeaderDraggedColumnListener _buttonTableHeaderDraggedColumnListener;

   /**
    * Constructor for ButtonTableHeader.
    */
   public ButtonTableHeader()
   {
      _tableAccess = () -> getTable();
      setDefaultRenderer(new TableHeaderButtonRenderer(getFont(), _mouseState, _tableAccess));

      HeaderListener hl = new HeaderListener();
      addMouseListener(hl);
      addMouseMotionListener(hl);

      _sortingListener = tableSortingAdmin -> onSortingDone(tableSortingAdmin);

      _dataListener = e -> _tableAccess.getTableSortingAdmin().clear();

   }

   private void onSortingDone(TableSortingAdmin tableSortingAdmin)
   {
      if( null != tableSortingAdmin.getLastChangedSortingItem() ) // null == Happens when sorting is by applying a TableState
      {
         _mouseState.setPressedViewColumnIdx(getViewColumnIndex(tableSortingAdmin.getLastChangedSortingItem().getSortedModelColumn()));
      }
      repaint();
   }

   /**
    * Consider replacing by getTable().convertColumnIndexToView(modelColumnIx).
    * See {@link JTable#convertColumnIndexToView(int)}.
    */
   public int getViewColumnIndex(int modelColumnIx)
   {
      for (int i = 0; i < getTable().getColumnModel().getColumnCount(); i++)
      {
         if (modelColumnIx == getTable().getColumnModel().getColumn(i).getModelIndex())
         {
            return i;
         }
      }
      return -1;
   }

   public void setTable(JTable table)
   {
      JTable oldTable = getTable();
      if (oldTable != null)
      {
         Object obj = oldTable.getModel();
         if (obj instanceof SortableTableModel)
         {
            SortableTableModel sortableTableModel = (SortableTableModel)obj;
            sortableTableModel.getActualModel().removeTableModelListener(_dataListener);
            sortableTableModel.removeSortingListener(_sortingListener);
         }
      }

      super.setTable(table);

      if (table != null)
      {
         Object obj = table.getModel();
         if (obj instanceof SortableTableModel)
         {
            SortableTableModel sortableTableModel = (SortableTableModel)obj;
            sortableTableModel.getActualModel().addTableModelListener(_dataListener);
            sortableTableModel.addSortingListener(_sortingListener);
         }
      }

      _tableAccess.getTableSortingAdmin().clear();
   }

   // SS: Display complete column header as tooltip if the column isn't wide enough to display it
   public String getToolTipText(MouseEvent e)
   {
      int col = columnAtPoint(e.getPoint());
      //int modelCol = getTable().convertColumnIndexToModel(col);
      String retStr = null;

      if (col >= 0)
      {
         TableColumn tcol = getColumnModel().getColumn(col);
         int colWidth = tcol.getWidth();
         TableCellRenderer h = tcol.getHeaderRenderer();

         if (h == null)
         {
            h = getDefaultRenderer();
         }

         Component c = h.getTableCellRendererComponent(table, tcol.getHeaderValue(), false, false, -1, col);

         int prefWidth = c.getPreferredSize().width;
         if (prefWidth > colWidth)
         {
            retStr = tcol.getHeaderValue().toString();
         }
      }
      return retStr;
   }

   public void adjustAllColWidths(boolean includeColHeaders)
   {
      for(int i=0; i < getTable().getColumnModel().getColumnCount(); ++i)
      {
         adjustColWidth(i, includeColHeaders);
      }
   }

   public static boolean isAlwaysAdjustAllColWidths()
   {
      return Props.getBoolean(PREF_KEY_ALWAYS_ADJUST_ALL_COLUMN_HEADERS, false);
   }

   public static void setAlwaysAdjustAllColWidths(boolean b)
   {
      Props.putBoolean(PREF_KEY_ALWAYS_ADJUST_ALL_COLUMN_HEADERS, b);
   }

   public void initColWidths()
   {
      if (isAlwaysAdjustAllColWidths())
      {
         SwingUtilities.invokeLater(() -> adjustAllColWidths(true));
      }
   }

   private void adjustColWidth(int colIx, boolean includeColHeaders)
   {
      int modelIx = getTable().convertColumnIndexToModel(colIx);

      int rowCount = getTable().getModel().getRowCount();


      int newWidth = 20;

      if(includeColHeaders)
      {
         TableColumn column = getTable().getColumnModel().getColumn(colIx);
         TableCellRenderer headerRenderer = column.getHeaderRenderer();
         if(null == headerRenderer)
         {
            headerRenderer = getTable().getTableHeader().getDefaultRenderer();
         }

         Component headerComp = headerRenderer.getTableCellRendererComponent(getTable(), null, false, false, 0, colIx);
         FontMetrics headerFontMetrics = headerComp.getFontMetrics(headerComp.getFont());

         Rectangle2D bounds = headerFontMetrics.getStringBounds("" + column.getHeaderValue(), headerComp.getGraphics());

         int width = bounds.getBounds().width;

         //  if(-1 != getTableSortingAdmin().getSortedColumn())
         // {
         //    if(colIx == getViewColumnIndex(getTableSortingAdmin().getSortedColumn())
         //       && null != getTableSortingAdmin().getSortedColumnIcon())
         //    {
         //       width += getTableSortingAdmin().getSortedColumnIcon().getIconWidth();
         //    }
         // }

         TableSortingItem tableSortingItem = _tableAccess.getTableSortingAdmin().getTableSortingItem(getTable().convertColumnIndexToModel(colIx));
         if(null != tableSortingItem && null != tableSortingItem.getSortedColumnIcon())
         {
            width += tableSortingItem.getSortedColumnIcon().getIconWidth();
         }

         newWidth = Math.max(newWidth, width);
      }



      if(0 == rowCount)
      {
         if(includeColHeaders)
         {
            getColumnModel().getColumn(colIx).setPreferredWidth(newWidth + 10);
            //getTable().doLayout();
         }

         return;
      }


      TableCellRenderer cellRenderer = getTable().getCellRenderer(0, colIx);
      Component cellComp = cellRenderer.getTableCellRendererComponent(getTable(), null, false, false, rowCount - 1, colIx);
      FontMetrics cellFontMetrics = cellComp.getFontMetrics(cellComp.getFont());


      int maxColumnAdjustLength = Integer.MAX_VALUE;

      if(Main.getApplication().getSquirrelPreferences().getMaxColumnAdjustLengthDefined())
      {
         maxColumnAdjustLength = Main.getApplication().getSquirrelPreferences().getMaxColumnAdjustLength();
      }

      for (int i = 0; i < rowCount; i++)
      {
         Object value = getTable().getModel().getValueAt(i, modelIx);

         String stringVal = "";
         if( getTable().getCellRenderer(i,colIx) instanceof SquirrelTableCellRenderer)
         {
            stringVal += ((SquirrelTableCellRenderer)getTable().getCellRenderer(i,colIx)).renderValue(value);
         }
         else
         {
            stringVal += value;
         }


         if( maxColumnAdjustLength < stringVal.length())
         {
            stringVal = stringVal.substring(0, maxColumnAdjustLength);
         }


         Rectangle2D bounds = cellFontMetrics.getStringBounds(stringVal, cellComp.getGraphics());

         newWidth = Math.max(newWidth, bounds.getBounds().width);
      }

      getColumnModel().getColumn(colIx).setPreferredWidth(newWidth + 10);
      //getTable().doLayout();
   }


   public void setDraggedColumnListener(ButtonTableHeaderDraggedColumnListener buttonTableHeaderDraggedColumnListener)
   {
      _buttonTableHeaderDraggedColumnListener = buttonTableHeaderDraggedColumnListener;
   }

   class HeaderListener extends MouseAdapter implements MouseMotionListener
   {
      public void mousePressed(MouseEvent e)
      {
         if(getCursor().getType() == Cursor.E_RESIZE_CURSOR || MouseEvent.BUTTON1 != e.getButton())
         {
            return;
         }

         _mouseState.setPressed(true);
         if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == table.convertColumnIndexToModel(columnAtPoint(e.getPoint())))
         {
            return;
         }

         _mouseState.setPressedViewColumnIdx(columnAtPoint(e.getPoint()));
         repaint();
      }

      public void mouseClicked(MouseEvent e)
      {
         if(2 == e.getClickCount() && getCursor().getType() == Cursor.E_RESIZE_CURSOR)
         {
            int colIx = columnAtPoint(e.getPoint());
            Rectangle headerRect = getHeaderRect(colIx);

            int distToColBegin = e.getPoint().x - headerRect.x;
            int distToColEnd = headerRect.x + headerRect.width - e.getPoint().x;
            if(distToColBegin < distToColEnd && 0 < colIx && colIx < getColumnModel().getColumnCount())
            {
               --colIx;
            }


            adjustColWidth(colIx, true);
         }
      }


      public void mouseReleased(MouseEvent e)
      {
         if(getCursor().getType() == Cursor.E_RESIZE_CURSOR || MouseEvent.BUTTON1 != e.getButton())
         {
            return;
         }


         if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == table.convertColumnIndexToModel(columnAtPoint(e.getPoint())))
         {
            _mouseState.setPressed(false);
            _mouseState.setDragged(false);
            return;
         }

         _mouseState.setPressed(false);
         if (!_mouseState.isDragged())
         {
            int newSortColumn = getTable().convertColumnIndexToModel(_mouseState.getPressedViewColumnIdx());
            TableModel tm = table.getModel();
            if (newSortColumn > -1  && newSortColumn < tm.getColumnCount() && tm instanceof SortableTableModel)
            {
               //ColumnOrder newOrder = ColumnOrder.ASC;
               //if (newSortColumn == getTableSortingAdmin().getSortedColumn())
               //{
               //   newOrder = getTableSortingAdmin().getColumnOrder().next();
               //}
               //getTableSortingAdmin().updateSortedColumn(newSortColumn, newOrder);
               //((SortableTableModel) tm).sortTableBySortingAdmin();

               TableSortingItem tableSortingItem = _tableAccess.getTableSortingAdmin().getTableSortingItem(newSortColumn);
               ColumnOrder newOrder = ColumnOrder.ASC;
               if(null != tableSortingItem)
               {
                  newOrder = tableSortingItem.getColumnOrder().next();
               }
               _tableAccess.getTableSortingAdmin().updateSortedColumn(newSortColumn, newOrder, 0 == (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK));
               writeMultipleSortingToMessagePanel();
               ((SortableTableModel) tm).sortTableBySortingAdmin();
            }
            else
            {
               _tableAccess.getTableSortingAdmin().clear();
            }
            repaint();
         }
         else
         {
            if(null != _buttonTableHeaderDraggedColumnListener)
            {
               _buttonTableHeaderDraggedColumnListener.columnDragged();
            }
         }
         _mouseState.setDragged(false);
      }

      public void writeMultipleSortingToMessagePanel()
      {
         if(1 < _tableAccess.getTableSortingAdmin().getTableSortingItems().size())
         {
            String msg = s_stringMgr.getString("TableSortingAdmin.multipleSortingMessagePrefix") + " ";


            for (int i = 0; i < _tableAccess.getTableSortingAdmin().getTableSortingItems().size(); i++)
            {
               TableSortingItem tableSortingItem = _tableAccess.getTableSortingAdmin().getTableSortingItems().get(i);

               msg += getColumnModel().getColumn(tableSortingItem.getSortedModelColumn()).getHeaderValue() + " " + tableSortingItem.getColumnOrder().name();

               if(i + 1 < _tableAccess.getTableSortingAdmin().getTableSortingItems().size())
               {
                  msg += ", ";
               }
            }

            Main.getApplication().getMessageHandler().showMessage(msg);
         }
      }

      public void mouseDragged(MouseEvent e)
      {
         _mouseState.setDragged(true);
         if (_mouseState.isPressed())
         {
            _mouseState.setPressed(false);
            repaint();
         }
      }

      public void mouseMoved(MouseEvent e)
      {
         _mouseState.setDragged(false);
      }

   }

}
