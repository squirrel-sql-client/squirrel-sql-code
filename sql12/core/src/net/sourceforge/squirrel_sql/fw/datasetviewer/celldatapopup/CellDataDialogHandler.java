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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    * function to create the popup display when called from JTable
    */
   public static void showDialog(JTable table,
                                 ColumnDisplayDefinition colDef,
                                 MouseEvent evt,
                                 boolean isModelEditable)
   {
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

      if(null == Main.getApplication().getStickyCellDataDialog().getStickyPopup())
      {
         createAndShowCellDataDialog(table, table.getColumnName(colIx), rowIx, colIx, colDef, obj, isModelEditable, evt);
      }
      else
      {
         CellDataDialog cellDataDialog = Main.getApplication().getStickyCellDataDialog().getStickyPopup();
         cellDataDialog.initCellDisplayPanel(table, table.getColumnName(colIx), rowIx, colIx, colDef, obj, isModelEditable, true);
      }
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
      CellDataDialog cellDataDialog = new CellDataDialog(parentTable, columnName, rowIx, colIx, colDef, objectToDisplay, isModelEditable);

      cellDataDialog.pack();

      Dimension dim;
      if (Main.getApplication().getSquirrelPreferences().isRememberValueOfPopup())
      {
         int width = Props.getInt(PREF_KEY_POPUPEDITABLEIOPANEL_WIDTH, 600);
         int height = Props.getInt(PREF_KEY_POPUPEDITABLEIOPANEL_HEIGHT, 300);
         dim = new Dimension(width, height);
      }
      else
      {
         dim = cellDataDialog.getSize();
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

      Window parentWindow = SwingUtilities.windowForComponent(parentTable);
      Point dialogPos = parentWindow.getLocation();

      dialogPos.x += SwingUtilities.convertPoint((Component) evt.getSource(), evt.getPoint(), parentWindow).x;
      dialogPos.y += SwingUtilities.convertPoint((Component) evt.getSource(), evt.getPoint(), parentWindow).y;

      Rectangle dialogRect = GUIUtils.ensureBoundsOnOneScreen(new Rectangle(dialogPos.x, dialogPos.y, dim.width, dim.height));


      cellDataDialog.setBounds(dialogRect);

      cellDataDialog.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            Props.putInt(PREF_KEY_POPUPEDITABLEIOPANEL_WIDTH, cellDataDialog.getSize().width);
            Props.putInt(PREF_KEY_POPUPEDITABLEIOPANEL_HEIGHT, cellDataDialog.getSize().height);
         }
      });

      cellDataDialog.setVisible(true);
   }
}
