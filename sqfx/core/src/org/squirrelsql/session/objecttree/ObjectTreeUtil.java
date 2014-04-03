package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;

public class ObjectTreeUtil
{
   public static ArrayList<TreeItem<ObjectTreeNode>> findTreeItems(TreeView<ObjectTreeNode> objectsTree, ObjectTreeNodeTypeKey objectTreeNodeTypeKey)
   {
      return findByType(objectsTree, objectTreeNodeTypeKey);
   }

   private static ArrayList<TreeItem<ObjectTreeNode>> findByType(TreeView<ObjectTreeNode> objectsTree, ObjectTreeNodeTypeKey objectTreeNodeTypeKey)
   {
      ArrayList<TreeItem<ObjectTreeNode>> matches = new ArrayList<>();
      TreeItem<ObjectTreeNode> root = objectsTree.getRoot();

      recurse(root, matches, objectTreeNodeTreeItem -> objectTreeNodeTreeItem.getValue().getTypeKey().equals(objectTreeNodeTypeKey));

      return matches;
   }

   private static void recurse(TreeItem<ObjectTreeNode> parent, ArrayList<TreeItem<ObjectTreeNode>> matches, ObjectTreeNodeItemMatcher objectTreeNodeItemMatcher)
   {
      if(objectTreeNodeItemMatcher.matches(parent))
      {
         matches.add(parent);
      }

      for (TreeItem<ObjectTreeNode> objectTreeNodeTreeItem : parent.getChildren())
      {
         recurse(objectTreeNodeTreeItem, matches, objectTreeNodeItemMatcher);
      }
   }

   public static TreeItem<ObjectTreeNode> findSingleTreeItem(TreeView<ObjectTreeNode> objectsTree, ObjectTreeNodeTypeKey objectTreeNodeTypeKey)
   {
      ArrayList<TreeItem<ObjectTreeNode>> arr = findByType(objectsTree, objectTreeNodeTypeKey);

      if(1 == arr.size())
      {
         return arr.get(0);
      }

      throw new IllegalStateException("Found " + arr.size() + " instead of one");
   }
}
