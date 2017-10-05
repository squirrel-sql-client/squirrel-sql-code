package org.squirrelsql.aliases.channel;

import javafx.scene.control.TreeItem;
import org.squirrelsql.aliases.AliasTreeNode;
import org.squirrelsql.services.dndpositionmarker.RelativeNodePosition;

import java.util.ArrayList;
import java.util.List;

public class AliasTreeNodeChannel
{
   private AliasTreeNodeMoveListener _aliasTreeNodeMoveListener;

   public AliasTreeNodeChannel(AliasTreeNodeMoveListener aliasTreeNodeMoveListener)
   {
      _aliasTreeNodeMoveListener = aliasTreeNodeMoveListener;
   }

   private List<AliasTreeNodeChangedListener> _listeners = new ArrayList<>();

   public void addListener(AliasTreeNodeChangedListener l)
   {
      _listeners.add(l);
   }

   public void fireChanged(TreeItem<AliasTreeNode> changedItem)
   {
      if(null == changedItem)
      {
         return;
      }


      AliasTreeNodeChangedListener[] clone = _listeners.toArray(new AliasTreeNodeChangedListener[_listeners.size()]);

      for (AliasTreeNodeChangedListener listener : clone)
      {
         listener.treeNodeChanged(changedItem);
      }
   }

   public void moveNodeRequest(TreeItem<AliasTreeNode> itemToMove, TreeItem<AliasTreeNode> itemToMoveTo, RelativeNodePosition relativeNodePosition)
   {
      _aliasTreeNodeMoveListener.moveNodeRequest(itemToMoveTo, itemToMove, relativeNodePosition);
   }

   public void doubleClicked(TreeItem<AliasTreeNode> selectedItem)
   {
      _aliasTreeNodeMoveListener.doubleClicked(selectedItem);
   }
}
