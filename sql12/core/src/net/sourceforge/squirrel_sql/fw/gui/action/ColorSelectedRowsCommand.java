package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.ColorPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;

public class ColorSelectedRowsCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ColorSelectedRowsCommand.class);


   private DataSetViewerTable _table;

   public ColorSelectedRowsCommand(DataSetViewerTable table)
   {
      _table = table;
   }

   public void execute()
   {
      Color startColor = _table.getRowColorHandler().getFirstColorInSelection();


      if (null != startColor)
      {
         JPopupMenu popupMenu = new JPopupMenu();

         popupMenu.add(createChooseColorItem(startColor));
         popupMenu.add(createRemoveColorItem());

         Point p = MouseInfo.getPointerInfo().getLocation();

         SwingUtilities.convertPointFromScreen(p, _table);

         popupMenu.show(_table, p.x, p.y);
      }
      else
      {
         colorSelection(null, false);
      }
   }


   private JMenuItem createRemoveColorItem()
   {
      JMenuItem ret = new JMenuItem(s_stringMgr.getString("ColorSelectedRowsCommand.remove.color"));
      ret.addActionListener(e -> colorSelection(null, true));
      return ret;
   }

   private JMenuItem createChooseColorItem(Color startColor)
   {
      JMenuItem ret = new JMenuItem(s_stringMgr.getString("ColorSelectedRowsCommand.choose.color"));
      ret.addActionListener(e -> colorSelection(startColor, false));
      return ret;
   }

   private void colorSelection(Color startColor, boolean remove)
   {

      Color newColor = null;

      if (false == remove)
      {
         newColor = JColorChooser.showDialog(_table, ColorPropertiesPanel.i18n.ALIAS_BACKGROUND_COLOR_CHOOSER_DIALOG_TITLE, startColor);

         if (null == newColor)
         {
            return;
         }
      }

      _table.getRowColorHandler().setColorForSelectedRows(newColor);
   }

}
