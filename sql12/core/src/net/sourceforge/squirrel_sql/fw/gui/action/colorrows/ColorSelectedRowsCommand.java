package net.sourceforge.squirrel_sql.fw.gui.action.colorrows;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;

public class ColorSelectedRowsCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ColorSelectedRowsCommand.class);

   private static final String PREF_KEY_PREVIOUS_COLOR_RGB = "ColorSelectedRowsCommand.previous.color.rgb";


   private DataSetViewerTable _table;

   public ColorSelectedRowsCommand(DataSetViewerTable table)
   {
      _table = table;
   }

   public void execute()
   {
      Color startColor = _table.getColoringService().getRowColorHandler().getFirstColorInSelection();


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
         if(null == startColor)
         {
            int rgb = getPreviousRowColorRgb();

            if(rgb != -1)
            {
               startColor = new Color(rgb);
            }
         }

         newColor = JColorChooser.showDialog(GUIUtils.getOwningFrame(_table), s_stringMgr.getString("ColorSelectedRowsCommand.color.selected.rows"), startColor);

         if (null == newColor)
         {
            return;
         }

         setPreviousRowColorRgb(newColor);
      }

      _table.getColoringService().getRowColorHandler().setColorForSelectedRows(newColor);
   }

   public static void setPreviousRowColorRgb(Color newColor)
   {
      Props.putInt(PREF_KEY_PREVIOUS_COLOR_RGB, newColor.getRGB());
   }

   public static int getPreviousRowColorRgb()
   {
      return Props.getInt(PREF_KEY_PREVIOUS_COLOR_RGB, -1);
   }
}
