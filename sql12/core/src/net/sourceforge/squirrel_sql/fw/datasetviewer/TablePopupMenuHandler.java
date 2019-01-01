package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;

import java.awt.event.MouseEvent;

public class TablePopupMenuHandler
{
   private final boolean _allowUpdate;
   private final IDataSetUpdateableModel _updateableObject;
   private final DataSetViewerTablePanel _dataSetViewerTablePanel;
   private final ISession _session;
   private boolean _currentRowNumberMenuItemState;

   public TablePopupMenuHandler(boolean allowUpdate, IDataSetUpdateableModel updateableObject, DataSetViewerTablePanel dataSetViewerTablePanel, ISession session)
   {
      _allowUpdate = allowUpdate;
      _updateableObject = updateableObject;
      _dataSetViewerTablePanel = dataSetViewerTablePanel;
      _session = session;
   }

   public void reset()
   {
      _currentRowNumberMenuItemState = false;
   }

   public void displayPopupMenu(MouseEvent evt)
   {
      TablePopupMenu _tablePopupMenu = new TablePopupMenu(_allowUpdate, _updateableObject, _dataSetViewerTablePanel, _session);
      _tablePopupMenu.ensureRowNumersMenuItemIsUpToDate(_currentRowNumberMenuItemState);
      _tablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
   }

   public void ensureRowNumersMenuItemIsUpToDate(boolean currentRowNumberMenutItemState)
   {
      _currentRowNumberMenuItemState = currentRowNumberMenutItemState;
   }
}
