package org.squirrelsql.table;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Iterator;
import java.util.Set;

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


      Set<Node> nodes = _tableView.lookupAll(".scroll-bar");

      ScrollBar scrollBarH = null;
      ScrollBar scrollBarV = null;

      for (Node node : nodes)
      {
         ScrollBar buf = (ScrollBar) node;

         if(buf.getOrientation() == Orientation.HORIZONTAL)
         {
            scrollBarH = buf;
         }

         if(buf.getOrientation() == Orientation.VERTICAL)
         {
            scrollBarV = buf;
         }
      }

      TableColumnHeader tableColumnHeader = (TableColumnHeader) _tableView.lookup(".column-header");

      yEnd = Math.max(tableColumnHeader.getHeight(), yEnd);
      xEnd = Math.max(lineWidth, xEnd);


      if(scrollBarV.isVisible())
      {
         xEnd = Math.min(xEnd, _tableView.getWidth() - scrollBarV.getWidth() - lineWidth);
      }
      else
      {
         xEnd = Math.min(xEnd, _tableView.getWidth() - lineWidth);
      }

      if(scrollBarH.isVisible())
      {
         yEnd = Math.min(yEnd, _tableView.getHeight() - scrollBarH.getHeight() - lineWidth);
      }
      else
      {
         yEnd = Math.min(yEnd, _tableView.getHeight() - lineWidth);
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


      scrollIfBottomOrTopReached(event, yEnd, tableColumnHeader);
   }

   private void scrollIfBottomOrTopReached(MouseEvent event, double yEnd, TableColumnHeader tableColumnHeader)
   {
      if(event.getY() > yEnd)
      {
         VirtualFlow virtualFlow = (VirtualFlow) _tableView.lookup(".virtual-flow");

         IndexedCell lastVisibleCell = virtualFlow.getLastVisibleCell();
         int rowIndex = lastVisibleCell.getIndex();

         if (rowIndex + 1 < _tableView.getItems().size())
         {
            _tableView.scrollTo(rowIndex + 1);
         }
      }

      if(event.getY() < tableColumnHeader.getHeight())
      {
         VirtualFlow virtualFlow = (VirtualFlow) _tableView.lookup(".virtual-flow");

         IndexedCell firstVisibleCell = virtualFlow.getFirstVisibleCell();
         int rowIndex = firstVisibleCell.getIndex();

         if (rowIndex - 1 >= 0)
         {
            _tableView.scrollTo(rowIndex - 1);
         }
      }
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
