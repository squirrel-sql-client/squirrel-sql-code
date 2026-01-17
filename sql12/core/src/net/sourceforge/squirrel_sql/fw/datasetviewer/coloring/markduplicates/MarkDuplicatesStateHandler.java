package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.MarkDuplicatesToggleAction;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.table.ButtonTableHeaderDraggedColumnListener;
import net.sourceforge.squirrel_sql.fw.gui.table.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.gui.table.SortingListener;

public class MarkDuplicatesStateHandler
{
   private final ActionListener _noResultTabFallBackActionListener;
   private final SortingListener _tableSortingListener;
   private final ButtonTableHeaderDraggedColumnListener _buttonTableHeaderDraggedColumnListener;
   private Action _actionProxy;

   private IDataSetViewer _sqlResultDataSetViewer;
   private IResultTab _resultTab;
   private MarkDuplicatesToggleAction _proxyDelegate;

   public MarkDuplicatesStateHandler(ActionListener noResultTabFallBackActionListener,
                                     SortingListener tableSortingListener,
                                     ButtonTableHeaderDraggedColumnListener buttonTableHeaderDraggedColumnListener)
   {
      _noResultTabFallBackActionListener = noResultTabFallBackActionListener;
      _tableSortingListener = tableSortingListener;
      _buttonTableHeaderDraggedColumnListener = buttonTableHeaderDraggedColumnListener;

      _actionProxy = new AbstractAction()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            if(null == _proxyDelegate)
            {
               _noResultTabFallBackActionListener.actionPerformed(e);
            }
            else
            {
               _proxyDelegate.actionPerformed(e);
            }
         }
      };

      GUIUtils.copyAllActionProperties(Main.getApplication().getActionCollection().get(MarkDuplicatesToggleAction.class), _actionProxy);
   }

   public Action getAction()
   {
      return _actionProxy;
   }

   public void init(IDataSetViewer dataSetViewer, IResultTab resultTab)
   {
      _sqlResultDataSetViewer = dataSetViewer;
      _resultTab = resultTab;

      _proxyDelegate = null;
      if(null != _resultTab)
      {
         _proxyDelegate = new MarkDuplicatesToggleAction(_resultTab);
      }

      if(null != _resultTab)
      {
         _sqlResultDataSetViewer = _resultTab.getSQLResultDataSetViewer();
      }

      if(_sqlResultDataSetViewer instanceof DataSetViewerTablePanel)
      {
         SortableTableModel sortableTableModel = ((DataSetViewerTablePanel) _sqlResultDataSetViewer).getTable().getSortableTableModel();
         sortableTableModel.addSortingListener(_tableSortingListener);
      }

      if(_sqlResultDataSetViewer instanceof DataSetViewerTablePanel)
      {
         ((DataSetViewerTablePanel) _sqlResultDataSetViewer).getTable().getButtonTableHeader().setDraggedColumnListener(_buttonTableHeaderDraggedColumnListener);
      }
   }

   public boolean hasDatasetViewerTablePanel()
   {
      return _sqlResultDataSetViewer instanceof DataSetViewerTablePanel;
   }

   /**
    * Maybe should be moved to {@link MarkDuplicatesChooserController}
    */
   public void markDuplicates(MarkDuplicatesMode mode)
   {
      if(hasDatasetViewerTablePanel())
      {
         ((DataSetViewerTablePanel) _sqlResultDataSetViewer).getTable().getColoringService().getMarkDuplicatesHandler().markDuplicates(mode);
      }
   }
}
