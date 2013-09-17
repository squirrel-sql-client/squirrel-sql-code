package org.squirrelsql.aliases;

import javafx.scene.control.TreeItem;

import java.util.ArrayList;

public class AliasTreeNodeChannel
{
   private ArrayList<AliasTreeNodeChannelListener> _listeners = new ArrayList<>();

   public void addListener(AliasTreeNodeChannelListener l)
   {
      _listeners.add(l);
   }

   public void fireChanged(TreeItem<AliasTreeNode> changedItem)
   {
      if(null == changedItem)
      {
         return;
      }


      AliasTreeNodeChannelListener[] clone = _listeners.toArray(new AliasTreeNodeChannelListener[_listeners.size()]);

      for (AliasTreeNodeChannelListener listener : clone)
      {
         listener.treeNodeChanged(changedItem);
      }
   }

   public void moveNode(TreeItem<AliasTreeNode> itemToMove, TreeItem<AliasTreeNode> itemToMoveTo, MovePosition movePosition)
   {
      System.out.println("Moving " + itemToMove.getValue().getName()  + " to " + itemToMoveTo.getValue().getName() + " as " + movePosition);
   }
}
