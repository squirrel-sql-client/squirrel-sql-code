package org.squirrelsql.aliases.channel;

import javafx.scene.control.TreeItem;
import org.squirrelsql.aliases.AliasTreeNode;
import org.squirrelsql.aliases.MovePosition;

public interface AliasTreeNodeMoveListener
{
   void moveNodeRequest(TreeItem<AliasTreeNode> itemToMove, TreeItem<AliasTreeNode> itemToMoveTo, MovePosition movePosition);
}
