package org.squirrelsql.session.graph.whereconfig;

import javafx.scene.input.*;
import org.squirrelsql.services.dndpositionmarker.DndDragPositionMarker;

public class WhereConfigColCell extends javafx.scene.control.TreeCell<WhereConfigColTreeNode>
{
   private WhereConfigColDragDroppedListener _whereConfigColDragDroppedListener;

   public WhereConfigColCell(WhereConfigColDragDroppedListener whereConfigColDragDroppedListener)
   {
      _whereConfigColDragDroppedListener = whereConfigColDragDroppedListener;
      DndDragPositionMarker<WhereConfigColTreeNode> dragPositionMarker = new DndDragPositionMarker<>(this, this::getChildren);

      setOnDragOver(e -> {dragPositionMarker.onDragOver(e); onDragOver(e);});


      setOnDragDetected(this::onDragDetected);

      setOnDragExited(dragPositionMarker::onDragExit);

      setOnDragDropped(e -> onDragDropped(e, dragPositionMarker));
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

   private void onDragDropped(DragEvent dragEvent, DndDragPositionMarker<WhereConfigColTreeNode> dragPositionMarker)
   {
      String idToMove = dragEvent.getDragboard().getString();
      _whereConfigColDragDroppedListener.dropped(idToMove, getTreeItem(), dragPositionMarker.getMovePosition());

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




   @Override
   protected void updateItem(WhereConfigColTreeNode whereConfigColTreeNode, boolean empty)
   {
      super.updateItem(whereConfigColTreeNode, empty);

      if(empty)
      {
         setText(null);
         return;
      }

      setText(whereConfigColTreeNode.toString());
   }
}
