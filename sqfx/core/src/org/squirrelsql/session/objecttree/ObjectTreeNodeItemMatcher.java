package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;

public interface ObjectTreeNodeItemMatcher
{
   boolean matches(TreeItem<ObjectTreeNode> objectTreeNodeTreeItem);

}
