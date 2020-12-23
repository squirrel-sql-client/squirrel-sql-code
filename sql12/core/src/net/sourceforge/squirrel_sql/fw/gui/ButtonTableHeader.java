package net.sourceforge.squirrel_sql.fw.gui;
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
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

/**
 * @version 	$Id: ButtonTableHeader.java,v 1.10 2009-09-17 05:40:53 gerdwagner Exp $
 * @author		Johan Compagner
 */
public class ButtonTableHeader extends JTableHeader
{
   /** Logger for this class. */
   private static ILogger s_log = LoggerController.createLogger(ButtonTableHeader.class);

   private static final String PREF_KEY_ALWAYS_ADJUST_ALL_COLUMN_HEADERS = "Squirrel.alwaysAdoptAllColumnHeaders";

   /** Icon for "Sorted ascending". */
   private static Icon s_ascIcon;

   /** Icon for "Sorted descending". */
   private static Icon s_descIcon;

   /** Listens for changes in the underlying data. */
   private TableDataListener _dataListener = new TableDataListener();

   private SortingListener _sortingListener;

   /** If <TT>true</TT> then the mouse button is currently pressed. */
   private boolean _pressed;

   /**
    * If <TT>true</TT> then the mouse is being dragged. This is only relevant
    * while the mouse is pressed.
    */
   private boolean _dragged;

   /**
    * if <tt>_pressed</tt> is <tt>true</tt> then this is the physical column
    * that the mouse was pressed in.
    */
   private int _pressedViewColumnIdx;

   /** Icon for the currently sorted column. */
   private Icon _currentSortedColumnIcon;

   /** Physical (as opposed to model) index of the currently sorted column. */
   private int _currentlySortedModelIdx = -1;

   static
   {
      try
      {
         LibraryResources rsrc = new LibraryResources();
         s_descIcon =
            rsrc.getIcon(LibraryResources.IImageNames.TABLE_DESCENDING);
         s_ascIcon =
            rsrc.getIcon(LibraryResources.IImageNames.TABLE_ASCENDING);
      }
      catch (Exception ex)
      {
         s_log.error("Error retrieving icons", ex);
      }
   }

   /**
    * Constructor for ButtonTableHeader.
    */
   public ButtonTableHeader()
   {
      _pressed = false;
      _dragged = false;
      _pressedViewColumnIdx = -1;

      setDefaultRenderer(new ButtonTableRenderer(getFont()));

      HeaderListener hl = new HeaderListener();
      addMouseListener(hl);
      addMouseMotionListener(hl);

      _sortingListener = (modelColumnIx, columnOrder) -> onSortingDone(modelColumnIx, columnOrder);

   }

   private void onSortingDone(int modelColumnIx, ColumnOrder columnOrder)
   {
      int viewColumnIndex = getViewColumnIndex(modelColumnIx);


      if (ColumnOrder.ASC == columnOrder)
      {
         _currentSortedColumnIcon = s_ascIcon;
      }
      else if (ColumnOrder.DESC == columnOrder)
      {
         _currentSortedColumnIcon = s_descIcon;
      }
      else
      {
         _currentSortedColumnIcon = null;
      }
      _currentlySortedModelIdx = modelColumnIx;
      _pressedViewColumnIdx = viewColumnIndex;

      repaint();
   }

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
      _currentSortedColumnIcon = null;
      _currentlySortedModelIdx = -1;
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

   /**
    * @return The currently sorted column index. If no column is sorted -1.
    */
   public int getCurrentlySortedModelIdx()
   {
      return _currentlySortedModelIdx;
   }

   /**
    *
    * @return The direction of the currently sorted column. If no column is sorted false.
    */
   public boolean isAscending()
   {
      return _currentSortedColumnIcon == s_ascIcon;
   }

   public void columnIndexWillBeRemoved(int colIx)
   {
//      if( colIx < _currentlySortedModelIdx)
//      {
//         --_currentlySortedModelIdx;
//      }
//      else if (colIx == _currentlySortedModelIdx)
//      {
//         _currentlySortedModelIdx = -1;
//      }
   }

   public void columnIndexWillBeAdded(int colIx)
   {
//      if( colIx <= _currentlySortedModelIdx)
//      {
//         ++_currentlySortedModelIdx;
//      }
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
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               adjustAllColWidths(true);
            }
         });
      }
   }

   private final class TableDataListener implements TableModelListener
   {
      public void tableChanged(TableModelEvent evt)
      {
         _currentSortedColumnIcon = null;
         _currentlySortedModelIdx = -1;
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
         if(-1 != _currentlySortedModelIdx && colIx == getViewColumnIndex(_currentlySortedModelIdx) && null != _currentSortedColumnIcon)
         {
            width += _currentSortedColumnIcon.getIconWidth();
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


   class HeaderListener extends MouseAdapter implements MouseMotionListener
   {
      /*
         * @see MouseListener#mousePressed(MouseEvent)
         */
      public void mousePressed(MouseEvent e)
      {
         if(getCursor().getType() == Cursor.E_RESIZE_CURSOR || MouseEvent.BUTTON1 != e.getButton())
         {
            return;
         }

         _pressed = true;
         if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == table.convertColumnIndexToModel(columnAtPoint(e.getPoint())))
         {
            return;
         }

         _pressedViewColumnIdx = columnAtPoint(e.getPoint());
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


      /*
		* @see MouseListener#mouseReleased(MouseEvent)
		*/
      public void mouseReleased(MouseEvent e)
      {
         if(getCursor().getType() == Cursor.E_RESIZE_CURSOR || MouseEvent.BUTTON1 != e.getButton())
         {
            return;
         }


         if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == table.convertColumnIndexToModel(columnAtPoint(e.getPoint())))
         {
            _pressed = false;
            _dragged = false;
            return;
         }

         _pressed = false;
         if (!_dragged)
         {
            _currentSortedColumnIcon = null;
            int column = getTable().convertColumnIndexToModel(_pressedViewColumnIdx);
            TableModel tm = table.getModel();

            if (column > -1
               && column < tm.getColumnCount()
               && tm instanceof SortableTableModel)
            {
               ((SortableTableModel) tm).sortByColumn(column);
               if (ColumnOrder.ASC == ((SortableTableModel)tm).getColumnOrder())
               {
                  _currentSortedColumnIcon = s_ascIcon;
               }
               else if (ColumnOrder.DESC == ((SortableTableModel)tm).getColumnOrder())
               {
                  _currentSortedColumnIcon = s_descIcon;
               }
               else
               {
                  _currentSortedColumnIcon = null;
               }
               _currentlySortedModelIdx = column;
            }
            repaint();
         }
         _dragged = false;
      }

      /*
         * @see MouseMotionListener#mouseDragged(MouseEvent)
         */
      public void mouseDragged(MouseEvent e)
      {
         _dragged = true;
         if (_pressed)
         {
            //_currentSortedColumnIcon = null;
//            _currentlySortedModelIdx = -1;
//            _pressedViewColumnIdx = -1;
            _pressed = false;
            repaint();
         }
      }

      /*
         * @see MouseMotionListener#mouseMoved(MouseEvent)
         */
      public void mouseMoved(MouseEvent e)
      {
         _dragged = false;
      }

   }


   protected class ButtonTableRenderer implements TableCellRenderer
   {
      JButton _buttonRaised;
      JButton _buttonLowered;

      ButtonTableRenderer(Font font)
      {
         _buttonRaised = new JButton();
         _buttonRaised.putClientProperty("JButton.buttonType", "gradient"); // Added by Patch 2856103 for Apple/Mac
         _buttonRaised.setMargin(new Insets(0, 0, 0, 0));
         _buttonRaised.setFont(font);
         _buttonLowered = new JButton();
         _buttonLowered.putClientProperty("JButton.buttonType", "gradient"); // Added by Patch 2856103 for Apple/Mac
         _buttonLowered.setMargin(new Insets(0, 0, 0, 0));
         _buttonLowered.setFont(font);
         _buttonLowered.getModel().setArmed(true);
         _buttonLowered.getModel().setPressed(true);

         _buttonLowered.setMinimumSize(new Dimension(50, 25));
         _buttonRaised.setMinimumSize(new Dimension(50, 25));
      }
      /*
         * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
         */
      public Component getTableCellRendererComponent(
         JTable table,
         Object value,
         boolean isSelected,
         boolean hasFocus,
         int row,
         int column)
      {

         if (value == null)
         {
            value = "";
         }

         // Rendering the column that the mouse has been pressed in.
         if (_pressedViewColumnIdx == column && _pressed)
         {
            _buttonLowered.setText(value.toString());

            // If this is the column that the table is currently is
            // currently sorted by then display the sort icon.
            if (column == getViewColumnIndex(_currentlySortedModelIdx)
               && _currentSortedColumnIcon != null)
            {
               _buttonLowered.setIcon(_currentSortedColumnIcon);
            }
            else
            {
               _buttonLowered.setIcon(null);
            }
            return _buttonLowered;
         }

         // This is not the column that the mouse has been pressed in.
         _buttonRaised.setText(value.toString());
         if (_currentSortedColumnIcon != null
            && column == getViewColumnIndex(_currentlySortedModelIdx))
         {
            _buttonRaised.setIcon(_currentSortedColumnIcon);
         }
         else
         {
            _buttonRaised.setIcon(null);
         }
         return _buttonRaised;
      }
   }
}
