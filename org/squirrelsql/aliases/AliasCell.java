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
         return;
      }

      setText(aliasTreeNode.getName());
//      setTextFill(getTextFillColor(aliasTreeNode));
//      setGraphic(getTreeItem().getGraphic());
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

   private ImageView getIcon(AliasTreeNode aliasTreeNode)
   {
      return null;  //To change body of created methods use File | Settings | File Templates.
   }

   public void setTreeItemBeingCut(TreeItem<AliasTreeNode> treeItemBeingCut)
   {
      _treeItemBeingCut = treeItemBeingCut;
   }

}
