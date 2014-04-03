package org.squirrelsql.aliases.channel;

import javafx.scene.control.TreeItem;
import org.squirrelsql.aliases.AliasTreeNode;
import org.squirrelsql.aliases.RelativeNodePosition;

public interface AliasTreeNodeMoveListener
{
   void moveNodeRequest(TreeItem<AliasTreeNode> itemToMoveTo, TreeItem<AliasTreeNode> itemToMove, RelativeNodePosition relativeNodePosition);

   void doubleClicked(TreeItem<AliasTreeNode> selectedItem);
}
