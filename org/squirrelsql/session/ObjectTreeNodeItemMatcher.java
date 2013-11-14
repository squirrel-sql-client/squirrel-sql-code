package org.squirrelsql.session;

import javafx.scene.control.TreeItem;
import org.squirrelsql.session.objecttree.ObjectTreeNode;

public interface ObjectTreeNodeItemMatcher
{
   boolean matches(TreeItem<ObjectTreeNode> objectTreeNodeTreeItem);

}
