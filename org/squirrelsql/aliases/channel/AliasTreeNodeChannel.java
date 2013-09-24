package org.squirrelsql.aliases.channel;

import javafx.scene.control.TreeItem;
import org.squirrelsql.aliases.AliasTreeNode;
import org.squirrelsql.aliases.RelativeNodePosition;

import java.util.ArrayList;

public class AliasTreeNodeChannel
{
   private AliasTreeNodeMoveListener _aliasTreeNodeMoveListener;

   public AliasTreeNodeChannel(AliasTreeNodeMoveListener aliasTreeNodeMoveListener)
   {
      _aliasTreeNodeMoveListener = aliasTreeNodeMoveListener;
   }

   private ArrayList<AliasTreeNodeChangedListener> _listeners = new ArrayList<>();

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
}
