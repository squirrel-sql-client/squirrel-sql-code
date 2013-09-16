package org.squirrelsql.aliases;

import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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

      DndDragPositionMarker<AliasTreeNode> marker = new DndDragPositionMarker<>(this, this::getChildren);

      setOnDragOver((e) -> {marker.onDragOver(e); onDragOver(e);});

      setOnDragExited(marker::onDragExit);

      setOnDragOver((e) -> {marker.onDragOver(e); onDragOver(e);});

      setOnDragDropped(dragEvent -> onDragDropped(dragEvent));
   }


   private void onDragDropped(DragEvent dragEvent)
   {
// TODO
//      System.out.println("Drag dropped on " + getItem());
//      int valueToMove = Integer.parseInt(dragEvent.getDragboard().getString());
//      TreeItem<Integer> itemToMove = search(getTreeView().getRoot(), valueToMove);
//      TreeItem<Integer> newParent = search(parentTree.getRoot(), item);
//      // Remove from former parent.
//      itemToMove.getParent().getChildren().remove(itemToMove);
//      // Add to new parent.
//      newParent.getChildren().add(itemToMove);
//      newParent.setExpanded(true);
//      dragEvent.consume();
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
