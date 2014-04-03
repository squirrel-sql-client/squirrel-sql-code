package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import org.squirrelsql.session.objecttree.ObjectTreeNode;

public interface ObjectTreeNodeItemMatcher
{
   boolean matches(TreeItem<ObjectTreeNode> objectTreeNodeTreeItem);

}
