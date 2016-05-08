package org.squirrelsql.session.graph;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.objecttree.ObjectTreeFilterCtrl;

import java.util.List;

public class GraphPaneCtrl
{

   private final ScrollPane _scrollPane;
   private GraphTableDndChannel _graphTableDndChannel;
   private Session _session;
   private final Pane _desktopPane = new Pane();
   private final DrawLinesCtrl _drawLinesCtrl;

   public GraphPaneCtrl(GraphTableDndChannel graphTableDndChannel, Session session)
   {
      _graphTableDndChannel = graphTableDndChannel;
      _session = session;

      initDrop(_desktopPane);

      // init and show the stage
      // create a scene that displays the desktopPane (resolution 600,600)
      StackPane stackPane = new StackPane();

      _scrollPane = new ScrollPane();
      _drawLinesCtrl = new DrawLinesCtrl(_desktopPane, _scrollPane);


      Canvas sizingDummyPane = new Canvas();
      stackPane.getChildren().add(sizingDummyPane);
      SizeBindingHelper.bindSizingDummyCanvasToScrollPane(_scrollPane, sizingDummyPane);

      stackPane.getChildren().add(_drawLinesCtrl.getCanvas());
      stackPane.getChildren().add(_desktopPane);

      _scrollPane.setContent(stackPane);
   }

   private void initDrop(Pane desktopPane)
   {
      desktopPane.setOnDragDropped(this::onDragDroppedOfTableFromObjectTreeDialogToDesktop);
      desktopPane.setOnDragOver(e -> onDragOverOfTableFromObjectTreeDialogToDesktop(e));
   }

   private void onDragOverOfTableFromObjectTreeDialogToDesktop(DragEvent dragEvent)
   {
      if (ObjectTreeFilterCtrl.DRAGGING_TO_QUERY_BUILDER.equals(dragEvent.getDragboard().getString()))
      {
         dragEvent.acceptTransferModes(TransferMode.MOVE);
      }
      dragEvent.consume();
   }

   private void onDragDroppedOfTableFromObjectTreeDialogToDesktop(DragEvent dragEvent)
   {
      if( ObjectTreeFilterCtrl.DRAGGING_TO_QUERY_BUILDER.equals(dragEvent.getDragboard().getString()) )
      {
         List<TableInfo> tableInfos = _graphTableDndChannel.getLastDroppedTableInfos();

         double offset = 0;
         for (TableInfo tableInfo : tableInfos)
         {
            double x = dragEvent.getX() + offset;
            double y = dragEvent.getY() + offset;

            TableWindowCtrl tableWindowCtrl = new TableWindowCtrl(_session, tableInfo, x, y, ctrl -> _drawLinesCtrl.doDraw());

            _desktopPane.getChildren().add(tableWindowCtrl.getWindow());

            offset += 10;
         }

         _drawLinesCtrl.doDraw();
      }
      dragEvent.consume();
   }


   public Node getPane()
   {
      return _scrollPane;
   }
}
