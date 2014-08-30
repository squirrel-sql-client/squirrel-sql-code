package org.squirrelsql.table;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.squirrelsql.workaround.TableCellByCoordinatesWA;

import java.util.Set;

public class ExtendedTableSelection
{

   private final Canvas _canvas = new Canvas();
   private final StackPane _stackPane = new StackPane();
   private TableView _tableView;

   private Point2D _pBegin = new Point2D(0,0);
   public static final int LINE_WIDTH = 2;

   public ExtendedTableSelection(TableView tableView)
   {
      _tableView = tableView;

      _stackPane.getChildren().add(_tableView);


      /////////////////////////////////////////////////////////////////////////////////////
      // We use this instead of setOnMousePressed() ... to leave these setters to others
      tableView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> onMousePressed(event));

      tableView.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> onReleased());

      tableView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> onDragged(event));
      //
      /////////////////////////////////////////////////////////////////////////////////////



//      tableView.setOnMousePressed(new EventHandler<MouseEvent>()
//      {
//         @Override
//         public void handle(MouseEvent event)
//         {
//            System.out.println("Second listener works!!!");
//         }
//      });

//      tableView.setOnMousePressed(new EventHandler<MouseEvent>()
//      {
//         @Override
//         public void handle(MouseEvent event)
//         {
//            onMousePressed(event);
//         }
//      });

//      tableView.setOnMouseReleased(new EventHandler<MouseEvent>()
//      {
//         @Override
//         public void handle(MouseEvent event)
//         {
//            _stackPane.getChildren().remove(_canvas);
//         }
//      });

//      _tableView.setOnMouseDragged(new EventHandler<MouseEvent>()
//      {
//         @Override
//         public void handle(MouseEvent event)
//         {
//            onDragged(event);
//         }
//      });
   }

   private boolean onReleased()
   {
      return _stackPane.getChildren().remove(_canvas);
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

      _pBegin = new Point2D(event.getX(), event.getY());
   }


   private void onDragged(MouseEvent event)
   {

      clearCanvas();

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

      double xBeg = ensureXInGrid(scrollBarV, _pBegin.getX());
      double yBeg = ensureYInGrid(scrollBarH, tableColumnHeader, _pBegin.getY());

      double xEnd = ensureXInGrid(scrollBarV, event.getX());
      double yEnd = ensureYInGrid(scrollBarH, tableColumnHeader, event.getY());


//      System.out.println("xEnd = " + xEnd);
//      System.out.println("yEnd = " + yEnd);

//      System.out.println("VV=" + scrollBarV.getValue());
//      System.out.println("VVA=" + scrollBarV.getVisibleAmount());
//      System.out.println("HV=" + scrollBarH.getValue());
//      System.out.println("HVA=" + scrollBarH.getVisibleAmount());
//      System.out.println("Eq=" + (scrollBarH == scrollBarV));


      double delta = scrollIfBottomOrTopReached(event, yEnd, tableColumnHeader);

      _pBegin = new Point2D(_pBegin.getX(), _pBegin.getY() - delta);


      GraphicsContext gc = _canvas.getGraphicsContext2D();
      gc.setStroke(Color.BLACK);
      gc.setLineWidth(LINE_WIDTH);

      gc.strokeLine(xBeg, yBeg, xEnd, yBeg);
      gc.strokeLine(xEnd, yBeg, xEnd, yEnd);
      gc.strokeLine(xEnd, yEnd, xBeg, yEnd);
      gc.strokeLine(xBeg, yEnd, xBeg, yBeg);




      //System.out.println("BEGIN Mouse: X=" + event.getX() + "; Y=" + event.getY());
      TableCell tableCellForPoint = TableCellByCoordinatesWA.findTableCellForPoint(_tableView, event.getX(), event.getY());

      System.out.println("tableCellForPoint = " + tableCellForPoint);

   }

   private double ensureYInGrid(ScrollBar scrollBarH, TableColumnHeader tableColumnHeader, double y)
   {
      double yRet = y;
      yRet = Math.max(tableColumnHeader.getHeight(), yRet);
      if(scrollBarH.isVisible())
      {
         yRet = Math.min(yRet, _tableView.getHeight() - scrollBarH.getHeight() - LINE_WIDTH);
      }
      else
      {
         yRet = Math.min(yRet, _tableView.getHeight() - LINE_WIDTH);
      }
      return yRet;
   }

   private double ensureXInGrid(ScrollBar scrollBarV, double x)
   {
      double xRet = x;
      xRet = Math.max(LINE_WIDTH, xRet);
      if(scrollBarV.isVisible())
      {
         xRet = Math.min(xRet, _tableView.getWidth() - scrollBarV.getWidth() - LINE_WIDTH);
      }
      else
      {
         xRet = Math.min(xRet, _tableView.getWidth() - LINE_WIDTH);
      }
      return xRet;
   }

   private double scrollIfBottomOrTopReached(MouseEvent event, double yEnd, TableColumnHeader tableColumnHeader)
   {
      if(event.getY() > yEnd)
      {
         VirtualFlow virtualFlow = (VirtualFlow) _tableView.lookup(".virtual-flow");

         IndexedCell lastVisibleCell = virtualFlow.getLastVisibleCell();
         int rowIndex = lastVisibleCell.getIndex();

         if (rowIndex + 1 < _tableView.getItems().size())
         {
            _tableView.scrollTo(rowIndex + 1);
            return virtualFlow.getFirstVisibleCell().getHeight();
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

            return -virtualFlow.getFirstVisibleCell().getHeight();
         }
      }

      return 0;
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
