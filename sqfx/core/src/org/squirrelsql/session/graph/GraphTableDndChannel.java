package org.squirrelsql.session.graph;

import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.objecttree.ObjectTreeFilterCtrl;
import org.squirrelsql.session.objecttree.ObjectTreeNode;

import java.util.List;
import java.util.function.Predicate;

public class GraphTableDndChannel
{
   private ObjectTreeFilterCtrl _lastDraggingObjectTreeFilter;

   private ShowToolbarListener _showToolbarListener;

   public void setLastDraggingObjectTreeFilter(ObjectTreeFilterCtrl dragingObjectTreeFilter)
   {
      _lastDraggingObjectTreeFilter = dragingObjectTreeFilter;
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
}
