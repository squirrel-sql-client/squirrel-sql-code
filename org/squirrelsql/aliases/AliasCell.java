package org.squirrelsql.aliases;

import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.squirrelsql.aliases.channel.AliasTreeNodeChannel;
import org.squirrelsql.aliases.dnd.DndDragPositionMarker;

public class AliasCell extends TreeCell<AliasTreeNode>
{
   private Label _label = new Label();

   private Paint _stdTextFill;
   private AliasCutCopyState _aliasCutCopyState;
   private AliasTreeNodeChannel _aliasTreeNodeChannel;

   public AliasCell(AliasTreeNodeChannel aliasTreeNodeChannel, AliasCutCopyState aliasCutCopyState)
   {
      _aliasTreeNodeChannel = aliasTreeNodeChannel;
      _aliasCutCopyState = aliasCutCopyState;
      _aliasTreeNodeChannel.addListener(this::onTreeNodeChanged);
      _stdTextFill = _label.getTextFill();


      setOnDragDetected(this::onDragDetected);

      DndDragPositionMarker<AliasTreeNode> dragPositionMarker = new DndDragPositionMarker<>(this, this::getChildren);

      setOnDragOver(e -> {dragPositionMarker.onDragOver(e); onDragOver(e);});

      setOnDragExited(dragPositionMarker::onDragExit);

      setOnDragOver(e -> {
         dragPositionMarker.onDragOver(e); onDragOver(e);});

      setOnDragDropped(e -> onDragDropped(e, dragPositionMarker));

      setOnMouseClicked(this::onMouseClicked);

   }

   private void onMouseClicked(MouseEvent mouseEvent)
   {
      if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2)
      {
         TreeItem<AliasTreeNode> selectedItem = getTreeItem();

         if(null == selectedItem)
         {
            return;
         }

         _aliasTreeNodeChannel.doubleClicked(selectedItem);
      }
   }

   private void onDragDropped(DragEvent dragEvent, DndDragPositionMarker<AliasTreeNode> dragPositionMarker)
   {
      String idToMove = dragEvent.getDragboard().getString();
      TreeItem<AliasTreeNode> itemToMove = AliasTreeUtil.search(getTreeView().getRoot(), idToMove);
      TreeItem<AliasTreeNode> itemToMoveTo = getTreeItem();

      _aliasTreeNodeChannel.moveNodeRequest(itemToMove, itemToMoveTo, dragPositionMarker.getMovePosition());

      dragEvent.consume();
   }



   private void onDragOver(DragEvent dragEvent)
   {
      if (dragEvent.getDragboard().hasString())
      {
         String id = dragEvent.getDragboard().getString();
         if (false == isEmpty() && false == getItem().getId().equals(id))
         {
            dragEvent.acceptTransferModes(TransferMode.MOVE);
         }
      }
      dragEvent.consume();
   }




   private void onDragDetected(MouseEvent event)
   {
      if (isEmpty())
      {
         return;
      }
      Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
      ClipboardContent content = new ClipboardContent();
      content.put(DataFormat.PLAIN_TEXT, "" + getItem().getId());
      dragBoard.setContent(content);
      event.consume();
   }

   private void onTreeNodeChanged(TreeItem<AliasTreeNode> ti)
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
