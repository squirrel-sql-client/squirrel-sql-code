package org.squirrelsql.aliases;

import javafx.scene.control.TreeItem;

public interface AliasCutStateListener
{
   public void treeItemCutChanged(TreeItem<AliasTreeNode> treeItemBeingCut);
}
