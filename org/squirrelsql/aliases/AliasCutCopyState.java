package org.squirrelsql.aliases;

import javafx.scene.control.TreeItem;

import java.util.ArrayList;

public class AliasCutCopyState
{
   private TreeItem<AliasTreeNode> _treeItemBeingCopied;
   private TreeItem<AliasTreeNode> _treeItemBeingCut;
   private AliasTreeNodeChannel _aliasTreeNodeChannel;


   public AliasCutCopyState(AliasTreeNodeChannel aliasTreeNodeChannel)
   {
      _aliasTreeNodeChannel = aliasTreeNodeChannel;
   }


   public void setTreeItemBeingCut(TreeItem<AliasTreeNode> treeItemBeingCut)
   {
      TreeItem<AliasTreeNode> old = _treeItemBeingCut;
      _treeItemBeingCut = treeItemBeingCut;
      _aliasTreeNodeChannel.fireChanged(old);
      _aliasTreeNodeChannel.fireChanged(_treeItemBeingCut);
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
   }
}
