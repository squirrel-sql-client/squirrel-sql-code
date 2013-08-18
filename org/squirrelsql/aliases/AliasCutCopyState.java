package org.squirrelsql.aliases;

import javafx.scene.control.TreeItem;

public class AliasCutCopyState
{
   private TreeItem<AliasTreeNode> _treeItemBeingCopied;
   private TreeItem<AliasTreeNode> _treeItemBeingCut;


   public void setTreeItemBeingCut(TreeItem<AliasTreeNode> treeItemBeingCut)
   {
      _treeItemBeingCut = treeItemBeingCut;
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
      _treeItemBeingCopied = treeItemBeingCopied;
   }

}
