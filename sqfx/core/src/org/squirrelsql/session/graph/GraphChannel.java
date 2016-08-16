package org.squirrelsql.session.graph;

import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.objecttree.ObjectTreeFilterCtrl;
import org.squirrelsql.session.objecttree.ObjectTreeNode;

import java.util.List;
import java.util.function.Predicate;

public class GraphChannel
{
   private ObjectTreeFilterCtrl _lastDraggingObjectTreeFilter;

   private ShowToolbarListener _showToolbarListener;
   private TabTitleListener _tabTitleListener;

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
}
