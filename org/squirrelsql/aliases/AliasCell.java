package org.squirrelsql.aliases;

import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class AliasCell extends TreeCell<AliasTreeNode>
{
   private Label _label = new Label();
   private TreeItem<AliasTreeNode> _treeItemBeingCut;

   private Paint _stdTextFill;
   private TreeItem<AliasTreeNode> _treeItemBeingCopied;

   public AliasCell()
   {
      _stdTextFill = _label.getTextFill();
   }




   @Override
   protected void updateItem(AliasTreeNode aliasTreeNode, boolean b)
   {
      super.updateItem(aliasTreeNode, b);

      if(null == aliasTreeNode)
      {
         setText(null);
         setGraphic(null);
         setTextFill(_stdTextFill);
         return;
      }

      setText(aliasTreeNode.getName());
      setTextFill(getTextFillColor(aliasTreeNode));
      setGraphic(getTreeItem().getGraphic());
   }


   private Paint getTextFillColor(AliasTreeNode aliasTreeNode)
   {
      if(null != _treeItemBeingCut && aliasTreeNode == _treeItemBeingCut.getValue())
      {
         return Color.LIGHTGRAY;
      }
      else
      {
         return _stdTextFill;
      }
   }

   public void setTreeItemBeingCut(TreeItem<AliasTreeNode> treeItemBeingCut)
   {
      _treeItemBeingCut = treeItemBeingCut;
   }

   public TreeItem<AliasTreeNode> getTreeItemBeingCut()
   {
      return _treeItemBeingCut;
   }


   public TreeItem<AliasTreeNode> getTreeItemBeingCopied()
   {
      return _treeItemBeingCopied;
   }

   public void setTreeItemBeingCopied(TreeItem<AliasTreeNode> treeItemBeingCopied)
   {
      _treeItemBeingCopied = treeItemBeingCopied;
   }
}
