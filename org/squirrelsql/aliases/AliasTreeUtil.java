package org.squirrelsql.aliases;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.squirrelsql.Props;

public class AliasTreeUtil
{
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
         throw new UnsupportedOperationException("NYI");
      }
   }

   public static TreeItem<AliasTreeNode> createFolderNode(String newFolderName)
   {
      return new TreeItem<AliasTreeNode>(new AliasFolder(newFolderName), new Props(AliasTreeUtil.class).getImageView("folder.png"));
   }

   public static TreeItem<AliasTreeNode> createAliasNode(Alias alias)
   {
      return new TreeItem<AliasTreeNode>(alias, new Props(AliasTreeUtil.class).getImageView("alias.png"));
   }
}
