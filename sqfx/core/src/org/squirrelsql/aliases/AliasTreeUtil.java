package org.squirrelsql.aliases;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;

public class AliasTreeUtil
{
   private static Props _props = new Props(AliasTreeUtil.class);
   private final static Image _aliasImage = _props.getImage(GlobalIconNames.ALIAS);
   private final static Image _folderImage = _props.getImage(GlobalIconNames.FOLDER);
	
   public static TreeItem<AliasTreeNode> deepCopy(TreeItem<AliasTreeNode> toCopy)
   {
      TreeItem<AliasTreeNode> copyParent = createCopy(toCopy);

      _traverse(copyParent, toCopy.getChildren());

      return  copyParent;
   }

   private static void _traverse(TreeItem<AliasTreeNode> parentCopy, ObservableList<TreeItem<AliasTreeNode>> childrenToCopy)
   {
      for (TreeItem<AliasTreeNode> childToCopy : childrenToCopy)
      {
         TreeItem<AliasTreeNode> copiedChild = createCopy(childToCopy);
         parentCopy.getChildren().add(copiedChild);
         _traverse(copiedChild, childToCopy.getChildren());
      }
   }

   private static TreeItem<AliasTreeNode> createCopy(TreeItem<AliasTreeNode> toCopy)
   {
      if(toCopy.getValue() instanceof AliasFolder)
      {
         return createFolderNode(toCopy.getValue().getName());
      }
      else
      {
         return createAliasNode(cloneAlias((AliasDecorator) toCopy.getValue()));
      }
   }

   public static TreeItem<AliasTreeNode> createFolderNode(String newFolderName)
   {
      return new TreeItem<AliasTreeNode>(new AliasFolder(newFolderName), _props.getImageView(_folderImage));
   }

   public static TreeItem<AliasTreeNode> createAliasNode(AliasDecorator alias)
   {
      return new TreeItem<>(alias, _props.getImageView(_aliasImage));
   }

   public static AliasDecorator cloneAlias(AliasDecorator aliasDecorator)
   {
      AliasDecorator ret = aliasDecorator.copy();

      return ret;
   }

   public static void sortChildren(TreeItem<AliasTreeNode> parent)
   {
      parent.getChildren().sort((n1, n2) -> n1.getValue().getName().compareTo(n2.getValue().getName()));

      for (TreeItem<AliasTreeNode> child : parent.getChildren())
      {
         sortChildren(child);
      }
   }

   public static TreeItem<AliasTreeNode> search(TreeItem<AliasTreeNode> currentNode, String idToFind)
   {
      if (currentNode.getValue().getId().equals(idToFind))
      {
         return currentNode;
      }

      for (TreeItem<AliasTreeNode> child : currentNode.getChildren())
      {
         TreeItem<AliasTreeNode> result = search(child, idToFind);
         if (result != null)
         {
            return result;
         }
      }

      return null;
   }
}
