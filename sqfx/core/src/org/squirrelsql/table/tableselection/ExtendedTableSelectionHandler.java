package org.squirrelsql.table.tableselection;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.squirrelsql.table.ColumnHandle;
import org.squirrelsql.workaround.TableCellByCoordinatesWA;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExtendedTableSelectionHandler
{
   public static final int LINE_WIDTH = 2;

   private final Canvas _canvas = new Canvas();
   private final StackPane _stackPane = new StackPane();
   private TableView _tableView;

   private Point2D _pBegin = new Point2D(0,0);
   private ExtendedTableSelection _extendedTableSelection;

   public ExtendedTableSelectionHandler(TableView tableView)
   {
      _tableView = tableView;

      _stackPane.getChildren().add(_tableView);


      /////////////////////////////////////////////////////////////////////////////////////
      // We use this instead of setOnMousePressed() ... to leave these setters to others
      tableView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> onPressed(event));

      tableView.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> onReleased(event));

      tableView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> onDragged(event));
      //
      /////////////////////////////////////////////////////////////////////////////////////
   }


   private void onPressed(MouseEvent event)
   {
      if (event.getButton() != MouseButton.PRIMARY)
      {
         return;
      }


      clearCanvas();
      _stackPane.getChildren().remove(_canvas);
      _stackPane.getChildren().add(_canvas);

      _canvas.setWidth(_tableView.getWidth());
      _canvas.setHeight(_tableView.getHeight());

      _pBegin = new Point2D(event.getX(), event.getY());

      TableCell beginCell = TableCellByCoordinatesWA.findTableCellForPoint(_tableView, event.getX(), event.getY());

      if(null != beginCell) {
    	  _extendedTableSelection = new ExtendedTableSelection(beginCell);
      }
   }


   private void onDragged(MouseEvent event)
   {
      if (event.getButton() != MouseButton.PRIMARY)
      {
         return;
      }


      clearCanvas();

      ScrollBar scrollBarH = getScrollbar(Orientation.HORIZONTAL);
      ScrollBar scrollBarV = getScrollbar(Orientation.VERTICAL);


      TableColumnHeader tableColumnHeader = getTableColumnHeader();

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

      //System.out.println("tableCellForPoint = " + tableCellForPoint);

   }

   private void onReleased(MouseEvent event)
   {
      if (event.getButton() != MouseButton.PRIMARY)
      {
         return;
      }

      double xEnd = ensureXInGrid(getScrollbar(Orientation.VERTICAL), event.getX());
      double yEnd = ensureYInGrid(getScrollbar(Orientation.HORIZONTAL), getTableColumnHeader(), event.getY());

      TableCell endCell = TableCellByCoordinatesWA.findTableCellForPoint(_tableView, xEnd, yEnd);
      
      if(null == endCell) {
    	  return;
      }

      _extendedTableSelection.setEndCell(endCell);

      _stackPane.getChildren().remove(_canvas);

      _extendedTableSelection.initSelectedColumns(_tableView);

      if(_extendedTableSelection.isSingleCell())
      {
         return;
      }

      int minRowIx = _extendedTableSelection.getMinRowIx();
      int maxRowIx = _extendedTableSelection.getMaxRowIx();

      _tableView.getSelectionModel().clearSelection();



      for (int i = minRowIx; i <= maxRowIx; i++)
      {
         _tableView.getSelectionModel().select(i);
      }
   }

   private TableColumnHeader getTableColumnHeader()
   {
      return (TableColumnHeader) _tableView.lookup(".column-header");
   }

   private ScrollBar getScrollbar(Orientation orientation)
   {
      Set<Node> nodes = _tableView.lookupAll(".scroll-bar");

      for (Node node : nodes)
      {
         ScrollBar ret = (ScrollBar) node;

         if(ret.getOrientation() == orientation)
         {
            return ret;
         }
      }

      throw new IllegalStateException("Could not find scroll bar for orientation: " + orientation);
   }

   private double ensureYInGrid(ScrollBar horizontalScrollBar, TableColumnHeader tableColumnHeader, double y)
   {
      double yRet = y;
      yRet = Math.max(tableColumnHeader.getHeight(), yRet);
      if(horizontalScrollBar.isVisible())
      {
         yRet = Math.min(yRet, _tableView.getHeight() - horizontalScrollBar.getHeight() - LINE_WIDTH);
      }
      else
      {
         yRet = Math.min(yRet, _tableView.getHeight() - LINE_WIDTH);
      }
      return yRet;
   }

   private double ensureXInGrid(ScrollBar verticalScrollBar, double x)
   {
      double xRet = x;
      xRet = Math.max(LINE_WIDTH, xRet);
      if(verticalScrollBar.isVisible())
      {
         xRet = Math.min(xRet, _tableView.getWidth() - verticalScrollBar.getWidth() - LINE_WIDTH);
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

   public List<CellItemsWithColumn> getSelectedCellItemsWithColumn()
   {
      List<CellItemsWithColumn> ret = new ArrayList<>();

      if(null == _extendedTableSelection)
      {
         return ret;
      }

      ArrayList<TableColumn> selectedColumns = _extendedTableSelection.getSelectedColumns();

      List<List<ObjectProperty>> rows = (List) _tableView.getSelectionModel().getSelectedItems();

      for (TableColumn selectedColumn : selectedColumns)
      {
         CellItemsWithColumn buf = new CellItemsWithColumn(selectedColumn);
         ret.add(buf);

         for (List<ObjectProperty> row : rows)
         {
            ObjectProperty prop = row.get(ColumnHandle.extractColumnIndex(selectedColumn));
            buf.addItem(prop.get());
         }

      }

      return ret;
   }
}
