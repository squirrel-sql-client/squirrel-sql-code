package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Generate a popup window to display and manipulate the
 * complete contents of a cell.
 */
public class CellDataDialogHandler
{
   public static final String PREF_KEY_POPUPEDITABLEIOPANEL_WIDTH = "Squirrel.popupEditableIOPanelWidth";
   public static final String PREF_KEY_POPUPEDITABLEIOPANEL_HEIGHT = "Squirrel.popupEditableIOPanelHeight";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDataDialogHandler.class);

   /**
    * Method to open a cell data popup dialog.
    * This method is called when a cell is double-clicked.
    * <p>
    * This method does nothing when a pinned cell data dialog exists
    * because refreshes of a pinned cell data dialog are handled by
    * {@link #showSelectedValueInPinnedCellDataDialog(JTable, boolean)}
    */
   public static void showDialog(JTable table,
                                 ColumnDisplayDefinition colDef,
                                 MouseEvent evt,
                                 boolean isModelEditable)
   {
      CellDataWindow pinnedCellDataWindow = Main.getApplication().getGlobalCellDataDialogManager().getPinnedCellDataDialog();
      if(null != pinnedCellDataWindow)
      {
         return;
      }

      Point pt = evt.getPoint();
      int rowIx = table.rowAtPoint(pt);
      int colIx = table.columnAtPoint(pt);

      Object obj = table.getValueAt(rowIx, colIx);

      // since user is now using popup, stop editing
      // using the in-cell editor, if any
      CellEditor editor = table.getCellEditor(rowIx, colIx);
      if (editor != null)
      {
         editor.cancelCellEditing();
      }

      createAndShowCellDataDialog(table, table.getColumnName(colIx), rowIx, colIx, colDef, obj, isModelEditable, evt);
   }

   /**
    *  This method refreshes data in a pinned cell data dialog.
    *  It is called when the cell selection changes.
    */
   public static void showSelectedValueInPinnedCellDataDialog(JTable table, boolean isModelEditable)
   {
      CellDataWindow pinnedCellDataWindow = Main.getApplication().getGlobalCellDataDialogManager().getPinnedCellDataDialog();
      if(null == pinnedCellDataWindow)
      {
         return;
      }

      int selectedRow = table.getSelectedRow();
      int selectedColumn = table.getSelectedColumn();

      if(-1 == selectedRow || -1 == selectedColumn)
      {
         return;
      }

      TableColumn column = table.getColumnModel().getColumn(selectedColumn);

      if(false == column instanceof ExtTableColumn)
      {
         return;
      }

      Object obj = table.getValueAt(selectedRow, selectedColumn);
      ColumnDisplayDefinition colDef = ((ExtTableColumn) column).getColumnDisplayDefinition();

      CellDataDialogState cellDataDialogState =
            new CellDataDialogState(table.getColumnName(selectedColumn), colDef, obj, true, isModelEditable, table, selectedRow, selectedColumn);

      pinnedCellDataWindow.initCellDisplayPanel(cellDataDialogState);
   }



   private static void createAndShowCellDataDialog(JTable parentTable,
                                                   String columnName,
                                                   int rowIx,
                                                   int colIx,
                                                   ColumnDisplayDefinition colDef,
                                                   Object objectToDisplay,
                                                   boolean isModelEditable,
                                                   MouseEvent evt)
   {
      CellDataDialogState cellDataDialogState =
            new CellDataDialogState(columnName, colDef, objectToDisplay, false, isModelEditable, parentTable, rowIx, colIx);

      createAndShowCellDataDialog(cellDataDialogState, SwingUtilities.windowForComponent(parentTable), evt);
   }

   public static void createAndShowCellDataDialog(CellDataDialogState cellDataDialogState, Window parentWindow)
   {
      createAndShowCellDataDialog(cellDataDialogState, parentWindow, null);
   }


   public static void createAndShowCellDataDialog(CellDataDialogState cellDataDialogState, Window parentWindow, MouseEvent mouseEvent)
   {
      CellDataWindow cellDataWindow = new CellDataWindow(CellWindowType.getSelected(), cellDataDialogState, parentWindow);

      cellDataWindow.getCellDataWindowAdapter().pack();

      Dimension dim;
      if (Main.getApplication().getSquirrelPreferences().isRememberValueOfPopup())
      {
         int width = Props.getInt(PREF_KEY_POPUPEDITABLEIOPANEL_WIDTH, 600);
         int height = Props.getInt(PREF_KEY_POPUPEDITABLEIOPANEL_HEIGHT, 300);
         dim = new Dimension(width, height);
      }
      else
      {
         dim = cellDataWindow.getCellDataWindowAdapter().getSize();
         if (dim.width < 300)
         {
            dim.width = 300;
         }
         if (dim.height < 300)
         {
            dim.height = 300;
         }
         if (dim.width > 600)
         {
            dim.width = 600;
         }
         if (dim.height > 500)
         {
            dim.height = 500;
         }
      }

      if(null != mouseEvent)
      {
         Point dialogPos = parentWindow.getLocation();
         dialogPos.x += SwingUtilities.convertPoint((Component) mouseEvent.getSource(), mouseEvent.getPoint(), parentWindow).x;
         dialogPos.y += SwingUtilities.convertPoint((Component) mouseEvent.getSource(), mouseEvent.getPoint(), parentWindow).y;

         Rectangle dialogRect = GUIUtils.ensureBoundsOnOneScreen(new Rectangle(dialogPos.x, dialogPos.y, dim.width, dim.height));

         cellDataWindow.getCellDataWindowAdapter().setBounds(dialogRect);
      }
      else
      {
         cellDataWindow.getCellDataWindowAdapter().setSize(dim);
         cellDataWindow.getCellDataWindowAdapter().centerWithinParent();
      }

      cellDataWindow.getCellDataWindowAdapter().addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            Props.putInt(PREF_KEY_POPUPEDITABLEIOPANEL_WIDTH, cellDataWindow.getCellDataWindowAdapter().getSize().width);
            Props.putInt(PREF_KEY_POPUPEDITABLEIOPANEL_HEIGHT, cellDataWindow.getCellDataWindowAdapter().getSize().height);
         }
      });

      cellDataWindow.getCellDataWindowAdapter().setVisible(true);
      Main.getApplication().getGlobalCellDataDialogManager().registerOpenCellDataDialog(cellDataWindow);
   }

   public static void reopenAsType(CellDataWindow oldCellDataWindow, CellWindowType newType)
   {
      CellDataWindow newCellDataWindow = new CellDataWindow(newType,
                                                            oldCellDataWindow.getCellDataDialogState(),
                                                            oldCellDataWindow.getCellDataWindowAdapter().getParent());

      newCellDataWindow.getCellDataWindowAdapter().setBounds(oldCellDataWindow.getCellDataWindowAdapter().getBounds());

      oldCellDataWindow.getCellDataWindowAdapter().setVisible(false);
      oldCellDataWindow.getCellDataWindowAdapter().dispose();

      newCellDataWindow.getCellDataWindowAdapter().setVisible(true);
   }
}
