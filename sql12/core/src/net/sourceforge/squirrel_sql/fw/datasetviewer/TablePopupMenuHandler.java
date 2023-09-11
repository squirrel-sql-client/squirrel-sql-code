package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.menuattic.AtticHandler;
import net.sourceforge.squirrel_sql.client.session.menuattic.MenuOrigin;
import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class TablePopupMenuHandler
{
   private final boolean _allowUpdate;
   private final IDataSetUpdateableModel _dataSetUpdateableModel;
   private final DataSetViewerTablePanel _dataSetViewerTablePanel;
   private final ISession _session;
   private TablePopupMenu _tablePopupMenu;
   private boolean _currentRowNumberMenuItemState;

   public TablePopupMenuHandler(boolean allowUpdate, IDataSetUpdateableModel dataSetUpdateableModel, DataSetViewerTablePanel dataSetViewerTablePanel, ISession session)
   {
      _allowUpdate = allowUpdate;
      _dataSetUpdateableModel = dataSetUpdateableModel;
      _dataSetViewerTablePanel = dataSetViewerTablePanel;
      _session = session;

      // The TablePopupMenu MUST be created that early
      // to make the shortcuts, that are defined in TablePopupMenu,
      // work right from beginning of the time the table exists.
      createTableMenuPopupWhenTableIsPresent();

   }

   private void createTableMenuPopupWhenTableIsPresent()
   {
      _createTableMenuPopupWhenTableIsPresent(new int[1]);
   }

   private void _createTableMenuPopupWhenTableIsPresent(int[] counter)
   {
      if(null == _dataSetViewerTablePanel.getTable())
      {
         if (5 == counter[0])
         {
            throw new IllegalStateException("Failed to create TablePopupMenu");
         }
         else
         {
            ++counter[0];
            SwingUtilities.invokeLater(() -> _createTableMenuPopupWhenTableIsPresent(counter));
         }
      }
      else
      {
         _tablePopupMenu = new TablePopupMenu(_allowUpdate, _dataSetUpdateableModel, _dataSetViewerTablePanel, _session);
      }
   }

   public void reset()
   {
      _currentRowNumberMenuItemState = false;
   }

   public void displayPopupMenu(MouseEvent evt, TableClickPosition tableClickPosition)
   {
      _tablePopupMenu.ensureRowNumbersMenuItemIsUpToDate(_currentRowNumberMenuItemState);

      AtticHandler.initAtticForMenu(_tablePopupMenu, MenuOrigin.SQL_RESULT);
      _tablePopupMenu.showPopupMenu(evt.getComponent(), evt.getX(), evt.getY(), tableClickPosition);
   }

   public void ensureRowNumersMenuItemIsUpToDate(boolean currentRowNumberMenutItemState)
   {
      _currentRowNumberMenuItemState = currentRowNumberMenutItemState;
   }
}
