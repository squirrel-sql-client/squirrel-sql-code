package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;

import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;

public class TablePopupMenuHandler
{
   private final boolean _allowUpdate;
   private final IDataSetUpdateableModel _updateableObject;
   private final DataSetViewerTablePanel _dataSetViewerTablePanel;
   private final ISession _session;
   private TablePopupMenu _tablePopupMenu;
   private boolean _currentRowNumberMenuItemState;

   public TablePopupMenuHandler(boolean allowUpdate, IDataSetUpdateableModel updateableObject, DataSetViewerTablePanel dataSetViewerTablePanel, ISession session)
   {
      _allowUpdate = allowUpdate;
      _updateableObject = updateableObject;
      _dataSetViewerTablePanel = dataSetViewerTablePanel;
      _session = session;

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
         _tablePopupMenu = new TablePopupMenu(_allowUpdate, _updateableObject, _dataSetViewerTablePanel, _session);
      }
   }

   public void reset()
   {
      _currentRowNumberMenuItemState = false;
   }

   public void displayPopupMenu(MouseEvent evt)
   {
      _tablePopupMenu.ensureRowNumbersMenuItemIsUpToDate(_currentRowNumberMenuItemState);
      _tablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
   }

   public void ensureRowNumersMenuItemIsUpToDate(boolean currentRowNumberMenutItemState)
   {
      _currentRowNumberMenuItemState = currentRowNumberMenutItemState;
   }
}
