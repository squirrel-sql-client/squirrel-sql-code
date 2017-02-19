package org.squirrelsql.session.graph;

import javafx.scene.layout.Pane;
import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.objecttree.ObjectTreeFilterCtrl;
import org.squirrelsql.session.objecttree.ObjectTreeNode;

import java.util.HashSet;
import java.util.List;

public class GraphChannel
{
   private ObjectTreeFilterCtrl _lastDraggingObjectTreeFilter;

   private ShowToolbarListener _showToolbarListener;
   private TabTitleListener _tabTitleListener;
   private GraphTabListener _graphTabListener;
   private ColumnListCtrl _lastDraggingColumnListCtrl;
   private Pane _desktopPane;
   private HashSet<AllTablesAddedListener> _allTablesAddedListeners = new HashSet<>();
   private QueryChannel _queryChannel = new QueryChannel();

   public GraphChannel(GraphTabListener graphTabListener)
   {
      _graphTabListener = graphTabListener;
   }

   public void setLastDraggingObjectTreeFilter(ObjectTreeFilterCtrl draggingObjectTreeFilter)
   {
      _lastDraggingObjectTreeFilter = draggingObjectTreeFilter;
   }

   public List<TableInfo> getLastDroppedTableInfos()
   {
      List< ObjectTreeNode> buf = _lastDraggingObjectTreeFilter.getSelectedObjectTreeNodes();
      return CollectionUtil.transform(buf, ObjectTreeNode::getTableInfo);
   }

   public void showToolBar(boolean b)
   {
      _showToolbarListener.showToolbar(b);
   }

   public void setShowtoolbarListener(ShowToolbarListener showToolbarListener)
   {
      _showToolbarListener = showToolbarListener;
   }

   public void setTabTitleListener(TabTitleListener tabTitleListener)
   {
      _tabTitleListener = tabTitleListener;
   }

   public void setTabTitle(String tabTitle)
   {
      _tabTitleListener.setTabTitle(tabTitle);
   }

   public void selectGraphTab()
   {
      _graphTabListener.selectTab();
   }

   public void removeGraphTab()
   {
      _graphTabListener.removeTab();
   }

   public void setLastDraggingColumnListCtrl(ColumnListCtrl lastDraggingColumnListCtrl)
   {
      _lastDraggingColumnListCtrl = lastDraggingColumnListCtrl;
   }

   public ColumnListCtrl getLastDraggingColumnListCtrl()
   {
      return _lastDraggingColumnListCtrl;
   }

   public GraphFinder getGraphFinder()
   {
      return new GraphFinder(_desktopPane);
   }

   public void setDesktopPane(Pane desktopPane)
   {
      _desktopPane = desktopPane;
   }

   public void addAllTablesAddedListener(AllTablesAddedListener allTablesAddedListener)
   {
      _allTablesAddedListeners.add(allTablesAddedListener);
   }

   public void fireAllTablesAdded()
   {
      for (AllTablesAddedListener l : _allTablesAddedListeners.toArray(new AllTablesAddedListener[_allTablesAddedListeners.size()]))
      {
         l.allTablesAdded();
      }
   }

   public QueryChannel getQueryChannel()
   {
      return _queryChannel;
   }
}
