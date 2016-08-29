package org.squirrelsql.session.graph;

import javafx.geometry.Bounds;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import org.squirrelsql.session.graph.graphdesktop.Window;

import java.util.WeakHashMap;

public class ColumnPositionHelper
{
   private ListView<GraphColumn> _listView;
   private Window _window;

   private WeakHashMap _cells = new WeakHashMap();


   public ColumnPositionHelper(ListView<GraphColumn> listView, Window window)
   {
      _listView = listView;
      _window = window;
   }

   public ColumnListCell registerCell(ColumnListCell columnListCell)
   {
      _cells.put(columnListCell, null);
      return columnListCell;
   }

   public double getMiddleYOfColumn(GraphColumn graphColumn)
   {
      ColumnListCell[] cells  = (ColumnListCell[]) _cells.keySet().toArray(new ColumnListCell[0]);

      double midYOfCol = 0;


      int maxVisibleItemIndex = -1;
      for (ColumnListCell cell : cells)
      {
         if(graphColumn == cell.getItem())
         {
            Bounds boundsInParent = cell.getBoundsInParent();

            midYOfCol =  boundsInParent.getMinY() + boundsInParent.getHeight() / 2;
         }

         if(null != cell.getItem())
         {
            maxVisibleItemIndex = Math.max(maxVisibleItemIndex, _listView.getItems().indexOf(cell.getItem()));
         }
      }

      if(maxVisibleItemIndex < _listView.getItems().indexOf(graphColumn))
      {
         midYOfCol = _listView.getHeight();
      }

      midYOfCol = Math.max(0, midYOfCol);
      midYOfCol = Math.min(_listView.getHeight(), midYOfCol);


      double ret = midYOfCol + _listView.getBoundsInParent().getMinY() + _window.getBoundsInParent().getMinY() + GraphConstants.TITLEBAR_HEIGHT;
      return ret;
   }

   public GraphColumn getColumnAt(DragEvent e)
   {
      ColumnListCell[] cells  = (ColumnListCell[]) _cells.keySet().toArray(new ColumnListCell[0]);

      for (ColumnListCell cell : cells)
      {
         if(    cell.getBoundsInParent().getMinX() < e.getX() && e.getX()  < cell.getBoundsInParent().getMaxX()
             && cell.getBoundsInParent().getMinY() < e.getY() && e.getY()  < cell.getBoundsInParent().getMaxY()
           )
         {
            return cell.getItem();
         }
      }

      return null;
   }
}
