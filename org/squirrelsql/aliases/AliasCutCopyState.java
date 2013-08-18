package org.squirrelsql.aliases;

import javafx.scene.control.TreeItem;

import java.util.ArrayList;

public class AliasCutCopyState
{
   private TreeItem<AliasTreeNode> _treeItemBeingCopied;
   private TreeItem<AliasTreeNode> _treeItemBeingCut;

   private ArrayList<AliasCutStateListener> _listeners = new ArrayList<>();


   public void setTreeItemBeingCut(TreeItem<AliasTreeNode> treeItemBeingCut)
   {

      TreeItem<AliasTreeNode> old = _treeItemBeingCut;
      _treeItemBeingCut = treeItemBeingCut;
      fireListeners(treeItemBeingCut, old);
   }

   public TreeItem<AliasTreeNode> getTreeItemBeingCut()
   {
      return _treeItemBeingCut;
   }


   public TreeItem<AliasTreeNode> getTreeItemBeingCopied()
   {
      return _treeItemBeingCopied;
   }

   public void setTreeItemBeingCopied(TreeItem<AliasTreeNode> treeItemBeingCopied)
   {
      TreeItem<AliasTreeNode> old = _treeItemBeingCopied;
      _treeItemBeingCopied = treeItemBeingCopied;
      fireListeners(treeItemBeingCopied, old);
   }

   public void addListener(AliasCutStateListener l)
   {
      _listeners.add(l);
   }

   private void fireListeners(TreeItem<AliasTreeNode> treeItem, TreeItem<AliasTreeNode> oldItem)
   {
      AliasCutStateListener[] buf = _listeners.toArray(new AliasCutStateListener[_listeners.size()]);

      for (AliasCutStateListener listener : buf)
      {
         if(null != oldItem && oldItem != treeItem)
         {
            listener.treeItemCutChanged(oldItem);
         }

         if (null != treeItem)
         {
            listener.treeItemCutChanged(treeItem);
         }
      }
   }

}
