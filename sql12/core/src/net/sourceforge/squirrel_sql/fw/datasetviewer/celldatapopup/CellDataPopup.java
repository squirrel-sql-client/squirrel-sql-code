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
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Generate a popup window to display and manipulate the
 * complete contents of a cell.
 */
public class CellDataPopup
{
   public static final String PREF_KEY_POPUPEDITABLEIOPANEL_WIDTH = "Squirrel.popupEditableIOPanelWidth";
   public static final String PREF_KEY_POPUPEDITABLEIOPANEL_HEIGHT = "Squirrel.popupEditableIOPanelHeight";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDataPopup.class);

   /**
    * function to create the popup display when called from JTable
    */
   public static void showDialog(JTable table,
                                 ColumnDisplayDefinition colDef,
                                 MouseEvent evt,
                                 boolean isModelEditable)
   {
      CellDataPopup popup = new CellDataPopup();
      popup.createAndShowDialog(table, evt, colDef, isModelEditable);
   }

   private void createAndShowDialog(JTable table, MouseEvent evt,
                                    ColumnDisplayDefinition colDef, boolean isModelEditable)
   {
      Point pt = evt.getPoint();
      int row = table.rowAtPoint(pt);
      int col = table.columnAtPoint(pt);

      Object obj = table.getValueAt(row, col);

      // since user is now using popup, stop editing
      // using the in-cell editor, if any
      CellEditor editor = table.getCellEditor(row, col);
      if (editor != null)
		{
			editor.cancelCellEditing();
		}

      Component parent = SwingUtilities.windowForComponent(table);

      final CellDataTextAreaDialog dialog = new CellDataTextAreaDialog(table, table.getColumnName(col), colDef, obj,row, col, isModelEditable, table);

      dialog.pack();

      Dimension dim;
      if (Main.getApplication().getSquirrelPreferences().isRememberValueOfPopup())
      {
         int width = Props.getInt(PREF_KEY_POPUPEDITABLEIOPANEL_WIDTH, 600);
         int height = Props.getInt(PREF_KEY_POPUPEDITABLEIOPANEL_HEIGHT, 300);
         dim = new Dimension(width, height);
      }
      else
      {
         dim = dialog.getSize();
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

      Point dialogPos = parent.getLocation();

      dialogPos.x += SwingUtilities.convertPoint((Component) evt.getSource(), pt, parent).x;
      dialogPos.y += SwingUtilities.convertPoint((Component) evt.getSource(), pt, parent).y;

//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//
//		if (dialogPos.x + dim.width > screenSize.getWidth())
//		{
//			dialogPos.x = (int)screenSize.getWidth() - dim.width;
//		}
//
//		if (dialogPos.y + dim.height > screenSize.getHeight())
//		{
//			dialogPos.y = (int)screenSize.getHeight() - dim.height;
//		}


      Rectangle dialogRect = GUIUtils.ensureBoundsOnOneScreen(new Rectangle(dialogPos.x, dialogPos.y, dim.width, dim.height));


      dialog.setBounds(dialogRect);
      //dialog.setSize(dim);

      dialog.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            Props.putInt(PREF_KEY_POPUPEDITABLEIOPANEL_WIDTH, dialog.getSize().width);
            Props.putInt(PREF_KEY_POPUPEDITABLEIOPANEL_HEIGHT, dialog.getSize().height);
         }
      });

      dialog.setVisible(true);
   }


}
