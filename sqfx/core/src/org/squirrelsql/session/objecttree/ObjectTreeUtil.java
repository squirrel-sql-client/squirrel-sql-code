package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.schemainfo.CatalogSchema;

import java.util.ArrayList;
import java.util.List;

public class ObjectTreeUtil
{
   public static List<TreeItem<ObjectTreeNode>> findTreeItems(TreeView<ObjectTreeNode> objectsTree, ObjectTreeNodeTypeKey objectTreeNodeTypeKey)
   {
      return findByType(objectsTree, objectTreeNodeTypeKey);
   }

   private static List<TreeItem<ObjectTreeNode>> findByType(TreeView<ObjectTreeNode> objectsTree, ObjectTreeNodeTypeKey objectTreeNodeTypeKey)
   {
      List<TreeItem<ObjectTreeNode>> matches = new ArrayList<>();
      TreeItem<ObjectTreeNode> root = objectsTree.getRoot();

      recurse(root, matches, objectTreeNodeTreeItem -> objectTreeNodeTreeItem.getValue().getTypeKey().equals(objectTreeNodeTypeKey));

      return matches;
   }

   private static void recurse(TreeItem<ObjectTreeNode> parent, List<TreeItem<ObjectTreeNode>> matches, ObjectTreeNodeItemMatcher objectTreeNodeItemMatcher)
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
      List<TreeItem<ObjectTreeNode>> arr = findByType(objectsTree, objectTreeNodeTypeKey);

      if(1 == arr.size())
      {
         return arr.get(0);
      }

      throw new IllegalStateException("Found " + arr.size() + " instead of one");
   }

   public static TreeItem<ObjectTreeNode> findTreeItem(TreeView<ObjectTreeNode> objectsTree, TreeItem<ObjectTreeNode> itemToMatch)
   {
      if(null == itemToMatch)
      {
         return null;
      }


      List<TreeItem<ObjectTreeNode>> matches = new ArrayList<>();
      recurse(objectsTree.getRoot(), matches, (item) -> matches(item, itemToMatch));

      if(0 == matches.size())
      {
         return null;
      }

      return matches.get(0);
   }

   private static boolean matches(TreeItem<ObjectTreeNode> item, TreeItem<ObjectTreeNode> itemToMatch)
   {
      return item.getValue().matches(itemToMatch.getValue());
   }

   public static List<TreeItem<ObjectTreeNode>> findTreeItemsByName(TreeView<ObjectTreeNode> objectsTree, QualifiedObjectName objName)
   {
      List<TreeItem<ObjectTreeNode>> matches = new ArrayList<>();
      TreeItem<ObjectTreeNode> root = objectsTree.getRoot();

      recurse(root, matches, objectTreeNodeTreeItem -> objName.matches(objectTreeNodeTreeItem.getValue()));

      return matches;
   }

   public static List<TreeItem<ObjectTreeNode>> findObjectsMatchingName(TreeView<ObjectTreeNode> objectsTree, String name)
   {
      if(Utils.isEmptyString(name))
      {
         return new ArrayList<>();
      }

      ArrayList<TreeItem<ObjectTreeNode>> matches = new ArrayList<>();

      recurse(objectsTree.getRoot(), matches, objectTreeNodeTreeItem -> matchesName(objectTreeNodeTreeItem, name));

      return matches;
   }

   private static boolean matchesName(TreeItem<ObjectTreeNode> objectTreeNodeTreeItem, String name)
   {
      String nodeName = objectTreeNodeTreeItem.getValue().getNodeName();
      return nodeName.trim().toLowerCase().equals(name.trim().toLowerCase());
   }

   public static void selectItem(TreeItem<ObjectTreeNode> itemToSelect, TreeView<ObjectTreeNode> objectsTree)
   {
      itemToSelect.setExpanded(true);
      objectsTree.getSelectionModel().clearSelection();
      objectsTree.getSelectionModel().select(itemToSelect);
      int row = objectsTree.getRow(itemToSelect);
      objectsTree.scrollTo(row);
   }
}
