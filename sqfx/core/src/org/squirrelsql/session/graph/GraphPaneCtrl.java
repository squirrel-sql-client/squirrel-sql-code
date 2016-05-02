package org.squirrelsql.session.graph;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.graph.graphdesktop.Window;
import org.squirrelsql.session.objecttree.ObjectTreeFilterCtrl;

import java.util.List;

public class GraphPaneCtrl
{

   public static final int PREVENT_INITIAL_SCROLL_DIST = 2;
   private final ScrollPane _scrollPane;
   private GraphTableDndChannel _graphTableDndChannel;
   private Session _session;
   private final Pane _desktopPane = new Pane();

   public GraphPaneCtrl(GraphTableDndChannel graphTableDndChannel, Session session)
   {
      _graphTableDndChannel = graphTableDndChannel;
      _session = session;

      initDrop(_desktopPane);

      // init and show the stage
      // create a scene that displays the desktopPane (resolution 600,600)
      StackPane stackPane = new StackPane();
      Canvas canvas = new Canvas();
      stackPane.getChildren().add(canvas);
      stackPane.getChildren().add(_desktopPane);

      _scrollPane = new ScrollPane(stackPane);

      DoubleBinding dbWidth = new DoubleBinding()
      {
         {
            super.bind(_scrollPane.widthProperty());
         }


         @Override
         protected double computeValue()
         {
            return _scrollPane.widthProperty().get() - PREVENT_INITIAL_SCROLL_DIST;
         }
      };

      DoubleBinding dbHeight = new DoubleBinding()
      {
         {
            super.bind(_scrollPane.heightProperty());
         }


         @Override
         protected double computeValue()
         {
            return _scrollPane.heightProperty().get() - PREVENT_INITIAL_SCROLL_DIST;
         }
      };


      canvas.widthProperty().bind(dbWidth);
      canvas.heightProperty().bind(dbHeight);

      canvas.widthProperty().addListener((observable, oldValue, newValue) -> onDraw(canvas));
      canvas.heightProperty().addListener((observable, oldValue, newValue) -> onDraw(canvas));
      onDraw(canvas);
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

            TableWindowCtrl tableWindowCtrl = new TableWindowCtrl(_session, tableInfo, x, y);
            _desktopPane.getChildren().add(tableWindowCtrl.getWindow());

            offset += 10;
         }
      }
      dragEvent.consume();
   }

   private void onDraw(Canvas canvas)
   {
      GraphicsContext gc = canvas.getGraphicsContext2D();

      gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

      gc.setStroke(Color.RED);
      gc.setLineWidth(3);
      gc.strokeLine(0, 0, canvas.getWidth(), canvas.getHeight());

      for (Node pkNode : _desktopPane.getChildren())
      {
         TableWindowCtrl pkCtrl = ((Window) pkNode).getCtrl();

         for (Node fkNode : _desktopPane.getChildren())
         {
            TableWindowCtrl fkCtrl = ((Window) fkNode).getCtrl();

            List<Point2D> pkPoints = pkCtrl.getPkPointsTo(fkCtrl);
            List<Point2D> fkPoints = fkCtrl.getFkPointsTo(pkCtrl);


         }
      }


   }

   public Node getPane()
   {
      return _scrollPane;
   }
}
