package org.squirrelsql.aliases;

import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class AliasCell extends TreeCell<AliasTreeNode>
{
   private Label _label = new Label();

   private Paint _stdTextFill;
   private AliasCutCopyState _aliasCutCopyState;

   public AliasCell(AliasCutCopyState aliasCutCopyState)
   {
      _aliasCutCopyState = aliasCutCopyState;
      _aliasCutCopyState.addListener(this::onTreeItemCutChanged);
      _stdTextFill = _label.getTextFill();
   }

   private void onTreeItemCutChanged(TreeItem<AliasTreeNode> ti)
   {
      if(super.getTreeItem() == ti)
      {
         updateItem(ti.getValue(), false);
      }
   }


   @Override
   protected void updateItem(AliasTreeNode aliasTreeNode, boolean empty)
   {
      super.updateItem(aliasTreeNode, empty);

      if(empty)
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
      if(null != _aliasCutCopyState.getTreeItemBeingCut() && aliasTreeNode == _aliasCutCopyState.getTreeItemBeingCut().getValue())
      {
         return Color.GRAY;
      }
      else
      {
         return _stdTextFill;
      }
   }
}
