package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.squirrelsql.services.CollectionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

   public static List<TreeItem<ObjectTreeNode>> findObjectsMatchingName(TreeView<ObjectTreeNode> objectsTree, String name, NameMatchMode nameMatchMode)
   {
      ArrayList<TreeItem<ObjectTreeNode>> matches = new ArrayList<>();

      recurse(objectsTree.getRoot(), matches, objectTreeNodeTreeItem -> matchesName(objectTreeNodeTreeItem, name, nameMatchMode));

      return matches;
   }


   public static List<TreeItem<ObjectTreeNode>> findObjectsMatchingNameAndType(TreeView<ObjectTreeNode> objectsTree, String name, NameMatchMode nameMatchMode, ObjectTreeNodeTypeKey objectTreeNodeTypeKey)
   {
      List<TreeItem<ObjectTreeNode>> listByType = findByType(objectsTree, objectTreeNodeTypeKey);
      return CollectionUtil.filter(listByType, objectTreeNodeTreeItem -> matchesName(objectTreeNodeTreeItem, name, nameMatchMode));
   }


   private static boolean matchesName(TreeItem<ObjectTreeNode> objectTreeNodeTreeItem, String name, NameMatchMode nameMatchMode)
   {
      String nodeName = objectTreeNodeTreeItem.getValue().getNodeName();

      switch (nameMatchMode)
      {
         case EQUALS:
            return nodeName.trim().toLowerCase().equals(name.trim().toLowerCase());
         case STARTS_WITH:
            return nodeName.trim().toLowerCase().startsWith(name.trim().toLowerCase());
         default:
            throw new UnsupportedOperationException("Unsupported NameMatchMode: " + nameMatchMode);
      }

   }

   public static void selectItem(TreeView<ObjectTreeNode> objectsTree, TreeItem<ObjectTreeNode> itemToSelect)
   {
      selectItems(objectsTree, Collections.singletonList(itemToSelect));
   }

   public static void selectItems(TreeView<ObjectTreeNode> objectsTree, List<TreeItem<ObjectTreeNode>> toSelectList)
   {
      objectsTree.getSelectionModel().clearSelection();
      for (TreeItem<ObjectTreeNode> toSelect : toSelectList)
      {
         toSelect.setExpanded(true);
         objectsTree.getSelectionModel().select(toSelect);
         int row = objectsTree.getRow(toSelect);
         objectsTree.scrollTo(row);
      }

   }


   public static void setExpandedAll(TreeView<ObjectTreeNode> objectsTree, boolean expanded)
   {
      _setExpandedAll(objectsTree.getRoot(), expanded);
   }

   private static void _setExpandedAll(TreeItem<ObjectTreeNode> nodeTreeItem, boolean expanded)
   {
      nodeTreeItem.setExpanded(expanded);

      for (TreeItem<ObjectTreeNode> child : nodeTreeItem.getChildren())
      {
         _setExpandedAll(child, expanded);
      }
   }

   public static List<TreeItem<ObjectTreeNode>> findTreeItemsByObjectTreeNodes(TreeView<ObjectTreeNode> objectsTree, List<ObjectTreeNode> toFind)
   {
      List<TreeItem<ObjectTreeNode>> matches = new ArrayList<>();
      TreeItem<ObjectTreeNode> root = objectsTree.getRoot();

      recurse(root, matches, objectTreeNodeTreeItem -> CollectionUtil.contains(toFind, oti -> oti == objectTreeNodeTreeItem.getValue()));

      return matches;

   }
}
