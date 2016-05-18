package org.squirrelsql.session.graph;

import javafx.geometry.Bounds;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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

   public ListCell<GraphColumn> registerCell(ColumnListCell columnListCell)
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


      double ret = midYOfCol + _listView.getBoundsInParent().getMinY() + _window.getBoundsInParent().getMinY() + GraphConstants.TITLEBAR_HEIGHT;

      System.out.println("ret = " + ret);
      return ret;
   }
}
