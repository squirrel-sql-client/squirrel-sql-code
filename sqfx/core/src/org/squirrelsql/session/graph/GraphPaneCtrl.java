package org.squirrelsql.session.graph;

import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import jfxtras.scene.control.window.Window;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.objecttree.ObjectTreeFilterCtrl;

import java.util.List;

public class GraphPaneCtrl
{

   public static final int PREVENT_INITIAL_SCROLL_DIST = 2;
   private final ScrollPane _scrollPane;
   private GraphTableDndChannel _graphTableDndChannel;

   public GraphPaneCtrl(GraphTableDndChannel graphTableDndChannel)
   {
      _graphTableDndChannel = graphTableDndChannel;
      Pane desktopPane = createGraphDesktopPane();

      initDrop(desktopPane);

      Window w;
      w = createInternalFrame(1, 0);
      desktopPane.getChildren().add(w);
      w = createInternalFrame(2, 0);
      desktopPane.getChildren().add(w);

//      w = createInternalFrame(3, 800);
//      desktopPane.getChildren().add(w);

      // init and show the stage
      // create a scene that displays the desktopPane (resolution 600,600)
      StackPane stackPane = new StackPane();
      Canvas canvas = new Canvas();
      stackPane.getChildren().add(canvas);
      stackPane.getChildren().add(desktopPane);

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

      canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
         onDraw(canvas);
      });
      canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
         onDraw(canvas);
      });

      onDraw(canvas);


   }

   private void initDrop(Pane desktopPane)
   {
      desktopPane.setOnDragDropped(this::onDragDropped);
      desktopPane.setOnDragOver(e -> onDragOver(e));

   }

   private void onDragOver(DragEvent dragEvent)
   {
      if (ObjectTreeFilterCtrl.DRAGGING_TO_QUERY_BUILDER.equals(dragEvent.getDragboard().getString()))
      {
         dragEvent.acceptTransferModes(TransferMode.MOVE);
      }
      dragEvent.consume();
   }

   private void onDragDropped(DragEvent dragEvent)
   {
      if( ObjectTreeFilterCtrl.DRAGGING_TO_QUERY_BUILDER.equals(dragEvent.getDragboard().getString()) )
      {
         List<TableInfo> tableInfos = _graphTableDndChannel.getLastDroppedTableInfos();

         tableInfos.forEach(ti -> System.out.println("Adding to query builder: "  +  ti.getName()));
      }
      dragEvent.consume();
   }

   private void onDraw(Canvas canvas)
   {
      GraphicsContext gc = canvas.getGraphicsContext2D();

      gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

      gc.setStroke(Color.BLACK);
      gc.setLineWidth(3);
      gc.strokeLine(0, 0, canvas.getWidth(), canvas.getHeight());
   }

   private Pane createGraphDesktopPane()
   {
      Pane desktopPane = new Pane();
      return desktopPane;
   }

   private Window createInternalFrame(int ix, int layoutOffset)
   {
      // create a window with title "My Window"
      Window w = new Window("My Window " + ix);

      Pane contentPane = createContentPane();

      w.setContentPane(contentPane);

      // set the window position to 10,10 (coordinates inside canvas)
      w.setLayoutX(10 + layoutOffset);
      w.setLayoutY(10 + layoutOffset);

      // define the initial window size
      w.setPrefSize(300, 200);
      return w;
   }

   private Pane createContentPane()
   {
      BorderPane contentPane = new BorderPane();
      contentPane.setCenter(new ListView<String>(FXCollections.observableArrayList("1", "2", "3")));
      return contentPane;
   }


   public Node getPane()
   {
      return _scrollPane;
   }
}
