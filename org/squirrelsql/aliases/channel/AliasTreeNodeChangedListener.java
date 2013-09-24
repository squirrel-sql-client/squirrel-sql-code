package org.squirrelsql.aliases.channel;

import javafx.scene.control.TreeItem;
import org.squirrelsql.aliases.AliasTreeNode;

public interface AliasTreeNodeChangedListener
{
   void treeNodeChanged(TreeItem<AliasTreeNode> ti);
}
