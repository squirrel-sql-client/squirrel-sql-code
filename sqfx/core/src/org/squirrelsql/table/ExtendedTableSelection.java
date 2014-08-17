package org.squirrelsql.table;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ExtendedTableSelection
{

   private final Canvas _canvas = new Canvas();
   private final StackPane _stackPane = new StackPane();
   private TableView _tableView;

   private Point2D pBegin = new Point2D(0,0);

   public ExtendedTableSelection(TableView tableView)
   {
      _tableView = tableView;

      _stackPane.getChildren().add(_tableView);


      tableView.setOnMousePressed(new EventHandler<MouseEvent>()
      {
         @Override
         public void handle(MouseEvent event)
         {
            onMousePressed(event);
         }
      });

      tableView.setOnMouseReleased(new EventHandler<MouseEvent>()
      {
         @Override
         public void handle(MouseEvent event)
         {
            _stackPane.getChildren().remove(_canvas);
         }
      });



      _tableView.setOnMouseDragged(new EventHandler<MouseEvent>()
      {
         @Override
         public void handle(MouseEvent event)
         {
            onDragged(event);
         }
      });
   }

   private void onMousePressed(MouseEvent event)
   {
      onPressed(event);
   }

   private void onPressed(MouseEvent event)
   {
      clearCanvas();
      _stackPane.getChildren().remove(_canvas);
      _stackPane.getChildren().add(_canvas);

      _canvas.setWidth(_tableView.getWidth());
      _canvas.setHeight(_tableView.getHeight());

      pBegin = new Point2D(event.getX(), event.getY());
   }


   private void onDragged(MouseEvent event)
   {

      clearCanvas();

      GraphicsContext gc = _canvas.getGraphicsContext2D();


      gc.setStroke(Color.BLACK);

      int lineWidth = 2;
      gc.setLineWidth(lineWidth);


      double xEnd = event.getX();
      double yEnd = event.getY();


      ScrollBar scrollBarH = (ScrollBar) _tableView.lookup(".scroll-bar.horizontal");
      ScrollBar scrollBarV = (ScrollBar) _tableView.lookup(".scroll-bar.vertical");

      TableColumnHeader tableColumnHeader = (TableColumnHeader) _tableView.lookup(".column-header");

      yEnd = Math.max(tableColumnHeader.getHeight(), yEnd);
      xEnd = Math.max(lineWidth, xEnd);

      if(scrollBarV.isVisible())
      {
         xEnd = Math.min(xEnd, _tableView.getWidth() - scrollBarV.getWidth());
      }

      if(scrollBarH.isVisible())
      {
         yEnd = Math.min(yEnd, _tableView.getHeight() - scrollBarH.getWidth());
      }


//      System.out.println("xEnd = " + xEnd);
//      System.out.println("yEnd = " + yEnd);

//      System.out.println("VV=" + scrollBarV.getValue());
//      System.out.println("VVA=" + scrollBarV.getVisibleAmount());
//      System.out.println("HV=" + scrollBarH.getValue());
//      System.out.println("HVA=" + scrollBarH.getVisibleAmount());
//      System.out.println("Eq=" + (scrollBarH == scrollBarV));



      gc.strokeLine(pBegin.getX(), pBegin.getY(), xEnd, pBegin.getY());
      gc.strokeLine(xEnd, pBegin.getY(), xEnd, yEnd);
      gc.strokeLine(xEnd, yEnd, pBegin.getX(), yEnd);
      gc.strokeLine(pBegin.getX(), yEnd, pBegin.getX() ,pBegin.getY());
   }

   private void clearCanvas()
   {
      _canvas.getGraphicsContext2D().clearRect(0, 0, _canvas.getWidth(), _canvas.getHeight());
   }


   public StackPane getStackPane()
   {
      return _stackPane;
   }
}
