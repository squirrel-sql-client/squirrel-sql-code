package net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableColumnModel;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CopySelectedRowsToOwnWindowCommand
{
   private static ILogger s_log = LoggerController.createLogger(CopySelectedRowsToOwnWindowCommand.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CopySelectedRowsToOwnWindowCommand.class);


   private final JTable _table;
   private final ISession _session;

   public CopySelectedRowsToOwnWindowCommand(JTable table, ISession session)
   {
      _table = table;
      _session = session;
   }

   public void execute()
   {
      ArrayList<ColumnDisplayDefinition> columnDisplayDefinitionsSortedByModelIndex = getColumnDisplayDefinitionsSortedByModelIndex();

      if (columnDisplayDefinitionsSortedByModelIndex == null)
      {
         return;
      }


      ArrayList<Object[]> rows = RowSelectionTableUtil.getSelectedRows(_table);

      ArrayList<RowsWindowFrame> rowsWindowFrames = RowsWindowFrameRegistryProvider.getRegistry(_session).getMatchingWindows(columnDisplayDefinitionsSortedByModelIndex);

      if(0 == rowsWindowFrames.size())
      {
         RowsWindowFrame rowsWindowFrame = openWindowForTable(rows, columnDisplayDefinitionsSortedByModelIndex);
         RowsWindowFrameRegistryProvider.getRegistry(_session).add(rowsWindowFrame);
      }
      else
      {
         JPopupMenu popupRowsWindows = new JPopupMenu();


         for (RowsWindowFrame rowsWindowFrame : rowsWindowFrames)
         {
            JMenuItem mi = new JMenuItem(rowsWindowFrame.getTitle());
            mi.addActionListener(e -> onMenuItemSelected(rowsWindowFrame, rows, columnDisplayDefinitionsSortedByModelIndex));

            mi.addMouseListener(new MouseAdapter() {
               @Override
               public void mouseEntered(MouseEvent e)
               {
                  rowsWindowFrame.markWindow(true);
               }

               @Override
               public void mouseExited(MouseEvent e)
               {
                  rowsWindowFrame.markWindow(false);
               }
            });

            popupRowsWindows.add(mi);

         }

         JMenuItem mi = new JMenuItem(s_stringMgr.getString("CopySelectedRowsToOwnWindowCommand.new.window"));
         mi.addActionListener(e -> onMenuItemSelected(null, rows, columnDisplayDefinitionsSortedByModelIndex));
         popupRowsWindows.add(mi);


         popupRowsWindows.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {}

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {
               rowsWindowFrames.forEach( w -> w.markWindow(false));
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {
               rowsWindowFrames.forEach( w -> w.markWindow(false));
            }
         });

         Point mouseLocationOnScreen = MouseInfo.getPointerInfo().getLocation();
         popupRowsWindows.show(_table, mouseLocationOnScreen.x - _table.getLocationOnScreen().x - 5, mouseLocationOnScreen.y - _table.getLocationOnScreen().y - 5);
      }
   }

   private ArrayList<ColumnDisplayDefinition> getColumnDisplayDefinitionsSortedByModelIndex()
   {
      TreeMap<Integer, ColumnDisplayDefinition> sortedMap = new TreeMap<>();


      TableColumnModel columnModel = _table.getColumnModel();
      for (int i = 0; i < columnModel.getColumnCount(); i++)
      {
         if(columnModel.getColumn(i) instanceof ExtTableColumn)
         {
            ExtTableColumn col = (ExtTableColumn) columnModel.getColumn(i);
            sortedMap.put(col.getModelIndex(), col.getColumnDisplayDefinition());
         }
         else
         {
            s_log.error(
                  "Failed to copy selected rows to own table. Expected column " + columnModel.getColumn(i).getHeaderValue() +
                  " to be of type ExtTableColumn but was of type " + columnModel.getColumn(i).getClass().getName());

            return null;
         }
      }

      ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions = new ArrayList<>(sortedMap.values());
      return columnDisplayDefinitions;
   }

   private void onMenuItemSelected(RowsWindowFrame rowsWindowFrame, List<Object[]> rows, ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions)
   {
      if(null == rowsWindowFrame)
      {
         RowsWindowFrame newRowsWindowFrame = openWindowForTable(rows, columnDisplayDefinitions);
         RowsWindowFrameRegistryProvider.getRegistry(_session).add(newRowsWindowFrame);
      }
      else
      {
         rowsWindowFrame.appendSelectedRows(rows, columnDisplayDefinitions);
      }

   }

   private RowsWindowFrame openWindowForTable(List<Object[]> rows, ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions)
   {
      Window parent = SwingUtilities.windowForComponent(_table);

      RowsWindowFrame rowsWindowFrame = new RowsWindowFrame(rows, parent, columnDisplayDefinitions, _session);
      GUIUtils.centerWithinParent(rowsWindowFrame);

      return rowsWindowFrame;
   }
}
