package org.squirrelsql.aliases;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

public class AliasTreeStructureNode
{
   List<AliasTreeStructureNode> _kids = new ArrayList<>();
   private String _aliasId;
   private boolean _expanded;
   private String _folderName;

   public AliasTreeStructureNode()
   {
   }

   public AliasTreeStructureNode(String aliasId)
   {
      _aliasId = aliasId;
   }

   public AliasTreeStructureNode(TreeItem<AliasTreeNode> item)
   {
      _folderName = item.getValue().getName();
      _expanded = item.isExpanded();
   }

   public List<AliasTreeStructureNode> getKids()
   {
      return _kids;
   }

   public void setKids(List<AliasTreeStructureNode> kids)
   {
      _kids = kids;
   }

   public String getAliasId()
   {
      return _aliasId;
   }

   public void setAliasId(String aliasId)
   {
      _aliasId = aliasId;
   }

   public boolean isExpanded()
   {
      return _expanded;
   }

   public void setExpanded(boolean expanded)
   {
      _expanded = expanded;
   }

   public String getFolderName()
   {
      return _folderName;
   }

   public void setFolderName(String folderName)
   {
      _folderName = folderName;
   }

   public void addAll(ObservableList<TreeItem<AliasTreeNode>> items, List<AliasDecorator> toFill)
   {
      for (TreeItem<AliasTreeNode> item : items)
      {
         if(item.getValue() instanceof AliasDecorator)
         {
            AliasDecorator alias = (AliasDecorator) item.getValue();
            _kids.add(new AliasTreeStructureNode(alias.getId()));
            toFill.add(alias);
         }
         else
         {
            AliasTreeStructureNode node = new AliasTreeStructureNode(item);
            node.addAll(item.getChildren(), toFill);
            _kids.add(node);
         }
      }
   }


   public List<AliasDecorator> apply(TreeItem<AliasTreeNode> parent, List<AliasDecorator> aliases)
   {
      for (AliasTreeStructureNode kid : _kids)
      {
         if(null!= kid.getAliasId())
         {
            AliasDecorator alias = findAndRemoveAlias(aliases, kid.getAliasId());

            if (null != alias) // another precaution to definitely ensure Aliases are read.
            {
               parent.getChildren().add(AliasTreeUtil.createAliasNode(alias));
            }
         }
         else
         {
            TreeItem<AliasTreeNode> item = AliasTreeUtil.createFolderNode(kid.getFolderName());
            item.setExpanded(kid.isExpanded());
            parent.getChildren().add(item);
            kid.apply(item, aliases);
         }
      }

      return aliases;
   }

   private AliasDecorator findAndRemoveAlias(List<AliasDecorator> aliases, String aliasId)
   {
      for (AliasDecorator alias : aliases)
      {
         if(alias.getId().equals(aliasId))
         {
            aliases.remove(alias);
            return alias;
         }
      }

      return null;
   }
}
