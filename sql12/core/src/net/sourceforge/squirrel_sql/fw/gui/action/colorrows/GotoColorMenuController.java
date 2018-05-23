package net.sourceforge.squirrel_sql.fw.gui.action.colorrows;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class GotoColorMenuController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GotoColorMenuController.class);
   private final JMenu _parentMenu;


   public GotoColorMenuController()
   {
      _parentMenu = new JMenu(s_stringMgr.getString("GotoColorMenuController.goto.color"));
   }

   public void createSubMenus(DataSetViewerTable table)
   {
      _parentMenu.removeAll();

      if(0 == table.getRowColorHandler().getColorByRow().size())
      {
         JMenuItem menuItem = new JMenuItem(s_stringMgr.getString("GotoColorMenuController.no.colored.rows"));
         menuItem.setEnabled(false);

         _parentMenu.add(menuItem);
         return;
      }


      TreeMap<Integer, Color> colorByViewRowSorted = createColorByViewRowMapSortedByRow(table);

      for (Map.Entry<Integer, Color> viewRowColorEntry : colorByViewRowSorted.entrySet())
      {
         JMenuItem menuItem = new JMenuItem(s_stringMgr.getString("GotoColorMenuController.row.number", (viewRowColorEntry.getKey() + 1)));
         menuItem.setBackground(viewRowColorEntry.getValue());

         menuItem.addActionListener( a-> onGotoLine(table, viewRowColorEntry.getKey()));

         _parentMenu.add(menuItem);
      }

   }

   private void onGotoLine(DataSetViewerTable table, Integer viewRow)
   {
      Rectangle visibleRect = table.getVisibleRect();

      Rectangle gotoRowRect = table.getCellRect(viewRow, 0, true);
      table.scrollRectToVisible(new Rectangle(visibleRect.x, gotoRowRect.y, visibleRect.width,3 * visibleRect.height/4));
   }

   private TreeMap<Integer, Color> createColorByViewRowMapSortedByRow(DataSetViewerTable table)
   {
      HashMap<Color, Integer> minViewIndexByColor = new HashMap<>();

      for (Map.Entry<Integer, Color> modelRowColorEntry : table.getRowColorHandler().getColorByRow().entrySet())
      {
         Integer viewRow = table.getSortableTableModel().transformToViewRow(modelRowColorEntry.getKey());

         Integer minViewRow = minViewIndexByColor.get(modelRowColorEntry.getValue());

         if(null == minViewRow || viewRow.intValue() < minViewRow.intValue())
         {
            minViewIndexByColor.put(modelRowColorEntry.getValue(), viewRow);
         }
      }


      TreeMap<Integer, Color> colorByViewRowSorted = new TreeMap<>();
      for (Map.Entry<Color, Integer> colorViewRowEntry : minViewIndexByColor.entrySet())
      {
         colorByViewRowSorted.put(colorViewRowEntry.getValue(), colorViewRowEntry.getKey());
      }
      return colorByViewRowSorted;
   }

   public JMenu getParentMenu()
   {
      return _parentMenu;
   }
}
